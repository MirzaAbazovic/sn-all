/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.builder.MeldungBuilder;

/**
 *
 */
public abstract class AbstractMeldungKftBuilder {

    public static <B extends MeldungBuilder> B withMeldungMetaData(B builder, IOType ioType) {
        builder.withIoType(ioType);
        if (IOType.OUT.equals(ioType)) {
            builder.withAbsender(CarrierCode.MNET);
        }
        else {
            builder.withAbsender(CarrierCode.DTAG);
        }
        return builder;
    }
}
