package com.hiro.core.model.assemblies.postal;

import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.parts.postal.PostalCode;
import com.hiro.core.model.parts.registration.Applicant;
import lombok.Getter;
import lombok.ToString;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Getter
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

    public Parcel<?> getReceivedParcel() {
        return this.receivedParcels.poll();
    }

    public Parcel<?> getReturnedParcel() {
        return this.returnedParcels.poll();
    }

    public void deliver(Parcel<?> parcel) {
        this.receivedParcels.add(parcel);
    }

    public void returnParcel(Parcel<?> parcel) {
        this.returnedParcels.add(parcel);
    }

}
