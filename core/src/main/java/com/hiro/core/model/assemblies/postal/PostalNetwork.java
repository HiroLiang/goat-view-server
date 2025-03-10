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

/**
 * Postal Network:
 * 1. Define network control
 * 2. Define life cycle control
 * 3. Child need to provide PostalCenters the network should trust
 */
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

    @Override
    public boolean isSubscribed(Applicant<PostalCode> applicant) {
        return applicants.contains((Postbox) applicant);
    }

    /**
     * Ship parcel to receiver on mark
     * @param sender sender's postbox
     * @param parcel parcel to ship
     */
    public void ship(Postbox sender, Parcel<?> parcel) {
        authApplicant(sender);
        checkParcelMark(parcel);

        // sender need to be member of this network
        if (!isSubscribed(sender))
            throw new GenericException(ErrorCode.POSTAL_WITHOUT_PERMISSION);

        // Add parcel to parcels, wait dispatcher to deliver
        parcels.add(parcel);
    }

    /**
     * Auth input applicant, not null, has trusted issuer, and post code is registered
     * @param applicant applicant
     */
    protected void authApplicant(Applicant<PostalCode> applicant) {
        // Applicant might not be null
        if (applicant == null || applicant.getIdentity() == null)
            throw new GenericException(ErrorCode.POSTAL_MISSING_IDENTITY);

        // Postal code need to be registered by trust center
        PostalCode postalCode = applicant.getIdentity();
        if (!acceptPostalCenters.contains(postalCode.issuer()) || !postalCode.issuer().isRegistered(postalCode))
            throw new GenericException(ErrorCode.POSTAL_WITHOUT_PERMISSION);

    }

    /**
     * Define deliver. Try to deliver to receiver, or return to shipper, or store the failed parcels
     * @param parcel parcel to deliver
     */
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
     * Check both shipper and receiver is subscribed to this network
     * @param parcel parcel
     */
    private void checkParcelMark(Parcel<?> parcel) {
        boolean shipperCheck = false;
        boolean receiverCheck = false;

        for (Postbox postbox : applicants) {
            if (postbox.getIdentity().code().equals(parcel.getMark().shipper()))
                shipperCheck = true;

            if (postbox.getIdentity().code().equals(parcel.getMark().receiver()))
                receiverCheck = true;
        }

        if (!shipperCheck || !receiverCheck)
            throw new GenericException(ErrorCode.POSTAL_WITHOUT_PERMISSION);
    }

    /**
     * Life cycle control:
     * 1. Implement Continuous
     * 2. Define protected methods to be used or override
     */
    private volatile RunningState state = RunningState.STOPPED;

    private Thread dispatcher;

    private ExecutorService postmen;

    /**
     * Override methods
     */
    @Override
    public synchronized void start() {
        String className = this.getClass().getSimpleName();
        log.info("[{}] Starting PostalNetwork...", className);

        // If already started then break
        if (isState(RunningState.STARTED)) {
            log.warn("Post network [{}] is already started...!", className);
            return;
        }

        // Initialize main thread and thread pool
        if (this.dispatcher == null || !this.dispatcher.isAlive()) initDispatcher();
        if (this.postmen == null || this.postmen.isTerminated()) initPostmen();

        // Dispatcher listening parcels
        this.dispatcher.start();

        // Change state
        state = RunningState.STARTED;
        log.info("Post network [{}] started!", className);
    }

    @Override
    public synchronized void pause() {
        // If state is not pause, send signal to stop listening
        if (!isState(RunningState.PAUSED)) {
            state = RunningState.PAUSED;
            this.parcels.add(PostalSignal.STOP);
        }

        log.info("Post network [{}] paused", this.getClass().getSimpleName());
    }

    @Override
    public synchronized void stop() {
        log.info("[{}] Stopping PostalNetwork...", this.getClass().getSimpleName());

        // If already stopped then break
        if (isState(RunningState.STOPPED)) {
            log.warn("Post network [{}] is already stopped...!", this.getClass().getSimpleName());
            return;
        }

        // Change state and send signal
        state = RunningState.STOPPED;
        this.parcels.add(PostalSignal.STOP);

        // waiting for dispatcher stop
        waitForStopped();
        log.info("Post network [{}] stopped!", this.getClass().getSimpleName());
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
            // Stop dispatcher
            stop();
        } finally {
            // Release resources like postmen
            releaseResources();

            // Change state
            state = RunningState.DESTROYED;
            log.info("Post network [{}] destroyed, failed parcels: {}", this.getClass().getSimpleName(), failedParcels);
        }
    }

    @Override
    public RunningState getState() {
        return this.state;
    }

    /**
     * Initialize dispatcher
     */
    protected void initDispatcher() {
        this.dispatcher = new Thread(this::startDelivery, this.getClass().getSimpleName() + "-dispatcher");
    }

    /**
     * Initialize postmen
     */
    protected void initPostmen() {
        this.postmen = new ThreadPoolExecutor(
                2, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * Define dispatcher's task: call thread pool to deliver parcel
     */
    protected void startDelivery() {
        while (RunningState.STARTED.equals(state)) {
            try {
                // waiting for parcel comes
                Parcel<?> parcel = parcels.take();

                // if receive signal, then stop waiting
                if (parcel == PostalSignal.STOP) break;

                // dispatch works
                postmen.execute(() -> deliver(parcel));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (RunningState.STOPPED.equals(state)) Thread.currentThread().interrupt();
    }

    /**
     * CHeck is state equals to input, but throw if state is destroyed
     * @param state to check
     * @return boolean
     */
    private boolean isState(RunningState state) {
        if (this.state == null) throw new GenericException(ErrorCode.POSTAL_DESTROYED);
        return this.state.equals(state);
    }

    /**
     * Waiting for stopped
     */
    private void waitForStopped() {
        if (this.dispatcher == null) return;

        int counter = 0;
        while (this.dispatcher.isAlive()) {
            try {
                log.info("Waiting for dispatcher to be stopped...");
                this.dispatcher.join(3000);
                counter++;
                if (!this.dispatcher.isAlive()) break;
            } catch (InterruptedException e) {
                this.dispatcher.interrupt();
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
     * Try to save release all resources
     */
    private void releaseResources() {
        this.dispatcher = null;

        this.postmen.shutdown();
        try {
            log.info("Waiting for thread pool shutdown...");
            if (!postmen.awaitTermination(5, TimeUnit.SECONDS)) {
                throw new InterruptedException("thread pool did not terminate");
            }
        } catch (InterruptedException e) {
            postmen.shutdownNow();
            log.warn("Force shutdown thread pool.", e);
        }
        this.postmen = null;
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
