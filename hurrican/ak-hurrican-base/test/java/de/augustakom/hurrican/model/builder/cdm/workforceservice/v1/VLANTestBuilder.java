/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;

/**
 *
 */
public class VLANTestBuilder extends VLANBuilder {

    public VLANTestBuilder() {
        withType("type");
        withCVLAN(BigInteger.ONE);
        withSVLAN(BigInteger.TEN);
        withSVLANBackbone(BigInteger.ZERO);
        withService("service");
    }
}