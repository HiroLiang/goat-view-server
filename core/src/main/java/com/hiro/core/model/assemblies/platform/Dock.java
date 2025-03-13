package com.hiro.core.model.assemblies.platform;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import com.hiro.core.model.assemblies.postal.PostalSignal;
import com.hiro.core.model.assemblies.postal.Postbox;
import com.hiro.core.model.enumeration.ErrorCode;
import com.hiro.core.model.enumeration.RunningState;
import com.hiro.core.model.parts.automation.Continuous;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Dock:
 * 1. Let platform can communicate with outside
 * 2. Define a foreman thread dispatching parcels to workers
 * 3. User should inherit class and decide what workers should do.
 */
@Slf4j
public abstract class Dock implements Continuous {

    protected final Postbox postbox;

    protected volatile RunningState state = RunningState.STOPPED;

    protected Thread foreman;

    protected ExecutorService workers;

    private final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();

    private final ReentrantLock shipLock = new ReentrantLock();

    private volatile PostalNetwork network;

    protected Dock(Postbox postbox) {
        if (postbox == null) throw new GenericException(ErrorCode.DOCK_ERROR);
        this.postbox = postbox;
    }

    protected Dock(Postbox postbox, PostalNetwork network) {
        this.postbox = postbox;
        this.network = network;
    }

    @Override
    public synchronized void start() {
        String className = this.getClass().getSimpleName();
        log.info("Dock [{} - {}] Starting...", className, this.postbox.getIdentity().name());
        if (isState(RunningState.STARTED)) {
            log.warn("Dock: [{} - {}] is already started...!", className, this.postbox.getIdentity().name());
            return;
        }

        init();
        if (this.foreman == null || !this.foreman.isAlive())
            this.foreman = new Thread(this::process);
        if (this.workers == null || this.workers.isTerminated())
            this.workers = Executors.newCachedThreadPool();

        this.foreman.start();
        setState(RunningState.STARTED);
        log.info("Dock [{} - {}] started successfully!", className, this.postbox.getIdentity().name());
    }

    @Override
    public synchronized void pause() {
        if (!isState(RunningState.PAUSED)) {
            setState(RunningState.PAUSED);
            this.postbox.deliver(PostalSignal.STOP);
        }
        log.info("Dock [{} - {}] paused.", this.getClass().getSimpleName(), this.postbox.getIdentity().name());
    }

    @Override
    public synchronized void stop() {
        log.info("Dock [{} - {}] Stopping...", this.getClass().getSimpleName(), this.postbox.getIdentity().name());
        if (isState(RunningState.STOPPED)) {
            log.warn("Dock [{} - {}] is already stopped...!", this.getClass().getSimpleName(), this.postbox.getIdentity().name());
            return;
        }

        setState(RunningState.STOPPED);
        this.postbox.deliver(PostalSignal.STOP);
        waitForStopped();
        log.info("Dock [{} - {}] stopped successfully!", this.getClass().getSimpleName(), this.postbox.getIdentity().name());

    }

    @Override
    public synchronized void restart() {
        log.info("Dock [{} - {}] Restarting...", this.getClass().getSimpleName(), this.postbox.getIdentity().name());
        stop();
        start();
    }

    @Override
    public synchronized void destroy() {
        log.info("Dock [{} - {}] Destroying...", this.getClass().getSimpleName(), this.postbox.getIdentity().name());

        stop();
        releaseResources();

        log.info("Dock [{} - {}] Destroyed", this.getClass().getSimpleName(), this.postbox.getIdentity().name());
    }

    @Override
    public RunningState getState() {
        return readState();
    }

    /**
     * Let who use Dock to ship parcel through this network
     * @param receiver receiver postal code
     * @param contain parcel contain
     * @param containClass contain class
     * @param <T> contain class
     */
    public <T> void ship(String receiver, T contain, Class<T> containClass) {
        if (this.network == null) throw new GenericException(ErrorCode.DOCK_NETWORK_ERROR);

        shipLock.lock();
        try {
            this.network.ship(this.postbox, Parcel.pack(this.postbox, receiver, contain, containClass));
        } finally {
            shipLock.unlock();
        }
    }

    /**
     * Change network ( Lock ship method while changing )
     * @param network PostalNetwork
     */
    public void useNetwork(PostalNetwork network) {
        shipLock.lock();
        try {
            this.network = network;
        } finally {
            shipLock.unlock();
        }
    }

    /**
     * Override to initialize thread pool or do nothing to use default thread pool
     */
    protected abstract void init();

    /**
     * Override to define what should do after started ( Dealing with parcels )
     */
    protected abstract void process();

    /**
     * Give state reading a read lock
     * @return RunningState
     */
    protected RunningState readState() {
        stateLock.readLock().lock();
        RunningState result;
        try {
            result = this.state;
        } finally {
            stateLock.readLock().unlock();
        }
        if (RunningState.UNKNOWN.equals(result))
            throw new GenericException(ErrorCode.DOCK_GET_STATE_ERROR);

        return result;
    }

    /**
     * Give state changing a write lock
     * @param state RunningState to change
     */
    protected void setState(RunningState state) {
        stateLock.writeLock().lock();
        try {
            this.state = state;
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    /**
     * Waiting foreman to stop take parcel
     */
    protected void waitForStopped() {
        if (this.foreman == null) return;

        int counter = 0;
        while (this.foreman.isAlive()) {
            try {
                log.info("Waiting for foreman to be stopped...");
                this.foreman.join(3000);
                counter++;
                if (!this.foreman.isAlive()) break;
            } catch (InterruptedException e) {
                this.foreman.interrupt();
                break;
            }

            if (counter > 2) {
                this.foreman.interrupt();
                break;
            }
        }

        this.foreman = null;
    }

    /**
     * Release all resources if wanted to destroy Dock
     */
    protected void releaseResources() {
        this.foreman = null;

        this.workers.shutdown();
        try {
            log.info("Waiting for workers thread pool shutdown...");
            if (!workers.awaitTermination(5, TimeUnit.SECONDS)) {
                throw new InterruptedException("thread pool did not terminate");
            }
        } catch (InterruptedException e) {
            workers.shutdownNow();
            log.warn("Force shutdown thread pool.", e);
        }
        this.workers = null;

        state = RunningState.DESTROYED;
    }

    /**
     * Check is state equals input state
     * @param state RunningState
     * @return boolean
     */
    private boolean isState(RunningState state) {
        if (this.state == null) throw new GenericException(ErrorCode.POSTAL_DESTROYED);

        return readState().equals(state);
    }

}
