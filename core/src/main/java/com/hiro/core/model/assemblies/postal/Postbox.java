package com.hiro.core.model.assemblies.postal;

import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.parts.postal.PostalCode;
import com.hiro.core.model.parts.registration.Applicant;
import lombok.ToString;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Postbox to store received parcels:
 * 1. Define deliver and take methods
 * 2. Applicant of postal system
 */
@ToString
public class Postbox implements Applicant<PostalCode> {

    private final PostalCode postalCode;

    private final BlockingQueue<Parcel<?>> receivedParcels = new PriorityBlockingQueue<>(10,
            Comparator.comparingLong(parcel -> parcel.getMark().timestamp()));

    private final BlockingQueue<Parcel<?>> returnedParcels = new PriorityBlockingQueue<>(5,
            Comparator.comparingLong(parcel -> parcel.getMark().timestamp()));

    public Postbox(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public PostalCode getIdentity() {
        return this.postalCode;
    }

    /**
     * Take received parcel. Blocked while Queue is empty
     * @return Parcel
     * @throws InterruptedException if waiting been interrupted
     */
    public Parcel<?> getReceivedParcel() throws InterruptedException {
        return this.receivedParcels.take();
    }

    /**
     * Take returned parcel. Blocked while Queue is empty
     * @return Parcel
     * @throws InterruptedException if waiting been interrupted
     */
    public Parcel<?> getReturnedParcel() throws InterruptedException {
        return this.returnedParcels.take();
    }

    /**
     * Let postmen deliver parcel
     * @param parcel Parcel
     */
    public void deliver(Parcel<?> parcel) {
        this.receivedParcels.add(parcel);
    }

    /**
     * Let postmen return parcel which ship failed
     * @param parcel Parcel
     */
    public void returnParcel(Parcel<?> parcel) {
        this.returnedParcels.add(parcel);
    }

}
