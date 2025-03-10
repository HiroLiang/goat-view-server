package com.hiro.core.model.assemblies.platform;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import com.hiro.core.model.assemblies.postal.PostalSignal;
import com.hiro.core.model.assemblies.postal.Postbox;
import com.hiro.core.model.enumeration.ErrorCode;
import com.hiro.core.model.enumeration.RunningState;
import com.hiro.core.model.parts.automation.Continuous;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class Dock implements Continuous {

    protected final Postbox postbox;

    protected RunningState state = RunningState.STOPPED;

    protected Thread foreman;

    protected ExecutorService workers;

    @Setter
    private PostalNetwork network;

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
        log.info("Dock [{}] Starting...", className);
        if (isState(RunningState.STARTED)) {
            log.warn("Dock: [{}] is already started...!", className);
            return;
        }

        init();
        if (this.foreman == null || !this.foreman.isAlive())
            this.foreman = new Thread(this::process, this.getClass().getSimpleName() + "-foreman");
        if (this.workers == null || this.workers.isTerminated())
            this.workers = Executors.newCachedThreadPool();

        this.foreman.start();
        this.state = RunningState.STARTED;
        log.info("Dock [{}] started successfully!", className);
    }

    @Override
    public synchronized void pause() {
        if (!isState(RunningState.PAUSED)) {
            this.state = RunningState.PAUSED;
            this.postbox.deliver(PostalSignal.STOP);
        }
        log.info("Dock [{}] paused.", this.getClass().getSimpleName());
    }

    @Override
    public synchronized void stop() {
        log.info("Dock [{}] Stopping...", this.getClass().getSimpleName());
        if (isState(RunningState.STOPPED)) {
            log.warn("Dock [{}] is already stopped...!", this.getClass().getSimpleName());
            return;
        }

        this.state = RunningState.STOPPED;
        this.postbox.deliver(PostalSignal.STOP);
        waitForStopped();
        log.info("Dock [{}] stopped successfully!", this.getClass().getSimpleName());

    }

    @Override
    public synchronized void restart() {
        log.info("Dock [{}] Restarting...", this.getClass().getSimpleName());
        stop();
        start();
    }

    @Override
    public synchronized void destroy() {
        log.info("Dock [{}] Destroying...", this.getClass().getSimpleName());

        stop();
        releaseResources();

        log.info("Dock [{}] Destroyed", this.getClass().getSimpleName());
    }

    @Override
    public synchronized RunningState getState() {
        return this.state;
    }

    protected abstract void init();

    protected abstract void process();

    public <T> void ship(String receiver, T contain, Class<T> containClass) {
        if (this.network == null) throw new GenericException(ErrorCode.DOCK_NETWORK_ERROR);

        this.network.ship(this.postbox, Parcel.pack(this.postbox, receiver, contain, containClass));
    }

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

    private boolean isState(RunningState state) {
        if (this.state == null) throw new GenericException(ErrorCode.POSTAL_DESTROYED);
        return this.state.equals(state);
    }

}
