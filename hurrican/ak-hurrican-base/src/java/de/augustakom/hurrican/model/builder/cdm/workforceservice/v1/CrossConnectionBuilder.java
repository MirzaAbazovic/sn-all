/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class CrossConnectionBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.CrossConnection> {

    protected String port;
    protected BigInteger type;
    protected BigInteger ltOuterInner;
    protected BigInteger ntOuterInner;
    protected String brasOuterInner;
    protected String bras;
    protected String atmSlot;
    protected String atmPort;

    @Override
    public OrderTechnicalParams.CrossConnection build() {
        OrderTechnicalParams.CrossConnection crossConnection = new OrderTechnicalParams.CrossConnection();
        crossConnection.setPort(this.port);
        crossConnection.setType(this.type);
        crossConnection.setLTOuterInner(this.ltOuterInner);
        crossConnection.setNTOuterInner(this.ntOuterInner);
        crossConnection.setBRASOuterInner(this.brasOuterInner);
        crossConnection.setBRAS(this.bras);
        crossConnection.setATMSlot(this.atmSlot);
        crossConnection.setATMPort(this.atmPort);
        return crossConnection;
    }

    public CrossConnectionBuilder withPort(String port) {
        this.port = port;
        return this;
    }

    public CrossConnectionBuilder withType(BigInteger type) {
        this.type = type;
        return this;
    }

    public CrossConnectionBuilder withLTOuterInner(BigInteger ltOuterInner) {
        this.ltOuterInner = ltOuterInner;
        return this;
    }

    public CrossConnectionBuilder withNTOuterInner(BigInteger ntOuterInner) {
        this.ntOuterInner = ntOuterInner;
        return this;
    }

    public CrossConnectionBuilder withBRASOuterInner(String brasOuterInner) {
        this.brasOuterInner = brasOuterInner;
        return this;
    }

    public CrossConnectionBuilder withBRAS(String bras) {
        this.bras = bras;
        return this;
    }

    public CrossConnectionBuilder withATMSlot(String atmSlot) {
        this.atmSlot = atmSlot;
        return this;
    }

    public CrossConnectionBuilder withATMPort(String atmPort) {
        this.atmPort = atmPort;
        return this;
    }
}