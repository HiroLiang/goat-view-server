package com.hiro.core.model.assemblies.postman;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.assemblies.store.GenericStore;
import com.hiro.core.model.components.platform.Continuous;
import com.hiro.core.model.components.postman.Subscribable;
import com.hiro.core.model.components.store.Store;
import com.hiro.core.model.enumeration.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public abstract class PostNetwork implements Subscribable<Postbox>, Continuous {

    protected final Store postBoxStore = new GenericStore();

    protected final BlockingQueue<Cargo<?>> cargoQueue = new LinkedBlockingQueue<>(20);

    private Thread dispatcherThread;

    private ExecutorService threadPool;

    private volatile boolean running = false;

    @Override
    public void subscribe(Postbox postbox) {
        this.postBoxStore.store(postbox.getIdentity(), postbox);
    }

    @Override
    public void cancel(Postbox postbox) {
        this.postBoxStore.discontinued(postbox.getIdentity());
    }

    @Override
    public synchronized void start() {
        String className = this.getClass().getSimpleName();
        if (running) {
            log.warn("Post network: {} is already running...!", className);
            return;
        }

        running = true;
        if (dispatcherThread == null)
            this.dispatcherThread = new Thread(this::startDelivery, className + "-dispatcher");
        if (threadPool == null)
            this.threadPool = new ThreadPoolExecutor(
                    2, 10,
                    60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(20),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        this.dispatcherThread.start();
        log.info("Post network: {} started!", className);

    }

    @Override
    public synchronized void pause() {
        if (running) running = false;
        log.warn("Post network: {} paused", this.getClass().getSimpleName());
    }

    @Override
    public synchronized void stop() {
        try {
            this.running = true;
        } finally {

        }
    }

    @Override
    public synchronized void restart() {
        pause();
        stop();
        start();
    }

    public <T> void submitCargo(String postCode, Cargo<T> cargo) {
        if (this.postBoxStore.retrieve(postCode) == null)
            throw new GenericException(ErrorCode.DELIVERY_WITHOUT_PERMISSION);

        this.cargoQueue.add(cargo);
    }

    protected void startDelivery() {
        while (running) {
            try {
              Cargo<?> cargo = cargoQueue.take();
              threadPool.execute(() -> deliver(cargo, cargo.receiverCodes));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    protected void deliver(Cargo<?> cargo, Set<String> identities) {
        for (String postCode : identities) {
            Postbox postbox = this.postBoxStore.retrieve(postCode);
            if (postbox != null) {
                postbox.put(cargo.getCopy());
            }
        }
    }
}
