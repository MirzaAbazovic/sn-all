/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2010 16:06:13
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;


/**
 *
 */
public class CBVorgangSubOrderBuilder extends EntityBuilder<CBVorgangSubOrderBuilder, CBVorgangSubOrder> {

    private AuftragBuilder auftragBuilder = null;
    private Long auftragId;
    private String dtagPort = "0001 01 01";
    private String returnLBZ;
    private String returnVTRNR;
    private String returnAQS;
    private String returnLL;

    @Override
    public boolean getPersist() {
        return false;
    }

    public CBVorgangSubOrderBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public CBVorgangSubOrderBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public CBVorgangSubOrderBuilder withDtagPort(String dtagPort) {
        this.dtagPort = dtagPort;
        return this;
    }

    public CBVorgangSubOrderBuilder withReturnLBZ(String returnLBZ) {
        this.returnLBZ = returnLBZ;
        return this;
    }

    public CBVorgangSubOrderBuilder withReturnVTRNR(String returnVTRNR) {
        this.returnVTRNR = returnVTRNR;
        return this;
    }

    public CBVorgangSubOrderBuilder withReturnAQS(String returnAQS) {
        this.returnAQS = returnAQS;
        return this;
    }

    public CBVorgangSubOrderBuilder withReturnLL(String returnLL) {
        this.returnLL = returnLL;
        return this;
    }

}


