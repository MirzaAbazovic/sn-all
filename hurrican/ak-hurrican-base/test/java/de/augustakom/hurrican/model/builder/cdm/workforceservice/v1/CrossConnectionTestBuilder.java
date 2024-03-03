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
public class CrossConnectionTestBuilder extends CrossConnectionBuilder {

    public CrossConnectionTestBuilder() {
        withPort("port");
        withATMPort("atm port");
        withATMSlot("atm slot");
        withBRAS("bras");
        withBRASOuterInner("bras outer inner");
        withLTOuterInner(BigInteger.ONE);
        withNTOuterInner(BigInteger.ONE);
        withType(BigInteger.ONE);
    }
}