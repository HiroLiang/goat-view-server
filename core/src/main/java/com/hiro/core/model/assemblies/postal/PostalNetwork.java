package com.hiro.core.model.assemblies.postal;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.enumeration.ErrorCode;
import com.hiro.core.model.enumeration.RunningState;
import com.hiro.core.model.parts.automation.Continuous;
import com.hiro.core.model.parts.postal.PostalCode;
import com.hiro.core.model.parts.registration.Applicant;
import com.hiro.core.model.parts.registration.Registrable;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public abstract class PostalNetwork implements Registrable<PostalCode>, Continuous {

    /**
     * Network control:
     * 1. Registration system: define how to subscribe network
     * 2. Deliver system: define Queue to receive parcels, and methods to deliver (can override)
     */

    protected final Set<PostalCenter> acceptPostalCenters = ConcurrentHashMap.newKeySet();

    protected final Set<Postbox> applicants = ConcurrentHashMap.newKeySet();

    protected final BlockingQueue<Parcel<?>> parcels =
            new PriorityBlockingQueue<>(20, new PostalComparator());

    protected final BlockingQueue<Parcel<?>> failedParcels =
            new PriorityBlockingQueue<>(5, new PostalComparator());

    /**
     * Override methods
     */
    @Override
    public void subscribe(Applicant<PostalCode> applicant) {
        authApplicant(applicant);
        applicants.add((Postbox) applicant);
    }

    @Override
    public void unsubscribe(Applicant<PostalCode> applicant) {
        applicants.remove((Postbox) applicant);
    }

    public void ship(Postbox sender, Parcel<?> parcel) {
        authApplicant(sender);

        parcels.add(parcel);
    }

    protected void authApplicant(Applicant<PostalCode> applicant) {
        if (applicant == null || applicant.getIdentity() == null)
            throw new GenericException(ErrorCode.POSTAL_MISSING_IDENTITY);

        PostalCode postalCode = applicant.getIdentity();
        if (!acceptPostalCenters.contains(postalCode.issuer()) || !postalCode.issuer().isRegistered(postalCode)) {
            throw new GenericException(ErrorCode.POSTAL_ERROR);
        }
    }

    protected void deliver(Parcel<?> parcel) {
        boolean delivered = false;
        try {
            for (Postbox postbox : applicants) {
                if (postbox.getIdentity().code().equals(parcel.getMark().receiver())) {
                    postbox.deliver(parcel);
                    delivered = true;
                    return;
                }
            }
        } catch (Exception e) {
            for (Postbox postbox : applicants) {
                if (postbox.getIdentity().code().equals(parcel.getMark().shipper())) {
                    postbox.returnParcel(parcel);
                    delivered = true;
                    return;
                }
            }
        } finally {
            if (!delivered) {
                log.warn("deliver failed for {}", parcel.getMark());
                failedParcels.add(parcel);
            }
        }
    }

    /**
     * Life cycle control:
     * 1. Implement Continuous
     * 2. Define protected methods to be used or override
     */
    private volatile RunningState state = RunningState.STOPPED;

    private Thread dispatcher;

    private ExecutorService threadPool;

    /**
     * Override methods
     */
    @Override
    public synchronized void start() {
        String className = this.getClass().getSimpleName();
        log.info("[{}] Starting PostalNetwork...", className);

        if (isState(RunningState.STARTED)) {
            log.warn("Post network: [{}] is already started...!", className);
            return;
        }

        if (this.dispatcher == null || !this.dispatcher.isAlive()) initDispatcher();
        if (this.threadPool == null) initThreadPool();

        this.dispatcher.start();
        state = RunningState.STARTED;
        log.info("Post network: [{}] started!", className);
    }

    @Override
    public synchronized void pause() {
        if (!isState(RunningState.PAUSED))
            state = RunningState.PAUSED;

        waitForDispatcherStop();
        log.info("Post network: [{}] paused", this.getClass().getSimpleName());
    }

    @Override
    public synchronized void stop() {
        log.info("[{}] Stopping PostalNetwork...", this.getClass().getSimpleName());

        if (isState(RunningState.STOPPED)) {
            log.warn("Post network: [{}] is already stopped...!", this.getClass().getSimpleName());
            return;
        }

        state = RunningState.STOPPED;
        waitForDispatcherStop();
        log.info("Post network: [{}] stopped!", this.getClass().getSimpleName());
    }

    @Override
    public synchronized void restart() {
        log.info("[{}] Restarting PostalNetwork...", this.getClass().getSimpleName());
        stop();
        start();
    }

    @Override
    public synchronized void destroy() {
        try {
            stop();
        } finally {
            this.dispatcher = null;
            this.threadPool.close();
            state = RunningState.DESTROYED;
            log.info("Post network: [{}] destroyed, failed parcels: {}", this.getClass().getSimpleName(), failedParcels);
        }
    }

    @Override
    public RunningState getState() {
        return this.state;
    }

    protected void initDispatcher() {
        this.dispatcher = new Thread(this::startDelivery, this.getClass().getSimpleName() + "-dispatcher");
    }

    protected void initThreadPool() {
        this.threadPool = new ThreadPoolExecutor(
                2, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    protected void startDelivery() {
        while (RunningState.STARTED.equals(state)) {
            try {
                Parcel<?> parcel = parcels.take();
                threadPool.execute(() -> deliver(parcel));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean isState(RunningState state) {
        if (this.state == null) throw new GenericException(ErrorCode.POSTAL_DESTROYED);
        return this.state.equals(state);
    }

    private void waitForDispatcherStop() {
        if (this.dispatcher == null) return;

        int counter = 0;
        while (this.dispatcher.isAlive()) {
            try {
                log.info("Waiting for dispatcher to be stopped...");
                this.dispatcher.join(3000);
                counter++;
                if (!this.dispatcher.isAlive()) break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (counter > 2) {
                this.dispatcher.interrupt();
                break;
            }
        }

        this.dispatcher = null;
    }

    /**
     * Inner classes and enumerations
     */
    private static class PostalComparator implements Comparator<Parcel<?>> {
        @Override
        public int compare(Parcel<?> p1, Parcel<?> p2) {
            if (p1.getMark().express() && !p2.getMark().express()) return -1;

            if (!p1.getMark().express() && p2.getMark().express()) return 1;

            return Long.compare(p1.getMark().timestamp(), p2.getMark().timestamp());
        }
    }

}
