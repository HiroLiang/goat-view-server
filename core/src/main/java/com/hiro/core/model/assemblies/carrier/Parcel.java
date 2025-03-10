package com.hiro.core.model.assemblies.carrier;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.assemblies.postal.Postbox;
import com.hiro.core.model.enumeration.ErrorCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
public class Parcel<T> {

    @Getter
    private final ShippingMark<T> mark;

    protected final Goods<T> goods;

    protected Parcel(ShippingMark<T> mark, Goods<T> goods) {
        this.mark = mark;
        this.goods = goods;
    }

    public static <T> Parcel<T> pack(Postbox postbox, String receiver, T contain, Class<T> clazz) {
        return new Parcel<>(
                new ShippingMark<>(UUID.randomUUID().toString(), postbox.getIdentity().code(), receiver, clazz,
                        false, Instant.now().toEpochMilli()),
                new Goods<>(contain));
    }

    public Parcel<T> withExpress() {
        return new Parcel<>(this.mark.copyWith(true), this.goods);
    }

    public T unpack(Postbox postbox) {
        if (postbox.getIdentity() == null || !postbox.getIdentity().code().equals(this.mark.receiver))
            throw new GenericException(ErrorCode.POSTAL_WITHOUT_PERMISSION);

        return this.mark.containClass.cast(this.goods.get());
    }

    public record ShippingMark<T>(String code, String shipper, String receiver, Class<T> containClass,
                               boolean express, long timestamp) {
        public ShippingMark<T> copyWith(boolean express) {
            return new ShippingMark<>(this.code, this.shipper, this.receiver, this.containClass, express, this.timestamp);
        }
    }

}
