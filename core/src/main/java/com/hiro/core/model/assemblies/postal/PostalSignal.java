package com.hiro.core.model.assemblies.postal;

import com.hiro.core.model.assemblies.carrier.Parcel;
import com.hiro.core.model.parts.postal.PostalCode;

public class PostalSignal {

    public static final Parcel<?> STOP =
            Parcel.pack(new Postbox(new PostalCode("STOP", "STOP", null)),
                    "STOP", "STOP", String.class);

}
