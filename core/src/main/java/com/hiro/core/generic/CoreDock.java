package com.hiro.core.generic;

import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.assemblies.platform.Dock;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import com.hiro.core.model.assemblies.postal.PostalSignal;
import com.hiro.core.model.assemblies.postal.Postbox;
import com.hiro.core.model.enumeration.RunningState;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Core Dock:
 * 1. Define a generic dock
 * 2. Override init and process methods
 */
@Slf4j
@ToString
public class CoreDock extends Dock {

    protected CoreDock(Postbox postbox, PostalNetwork network) {
        super(postbox, network);
    }

    /**
     * Define thread pool (Workers of Dock)
     */
    @Override
    protected void init() {
        this.workers = new ThreadPoolExecutor(
                2, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * Routine take parcels which been delivered to Postbox
     */
    @Override
    protected void process() {
        while (RunningState.STARTED.equals(this.state)) {
            try {
                Parcel<?> parcel = this.postbox.getReceivedParcel();

                // If catch the signal
                if (PostalSignal.STOP == parcel) break;

                // TODO Unpack and deliver to Processor
                log.info("[{}] Get Parcel: {}", this.postbox.getIdentity().name(), parcel.unpack(this.postbox));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
