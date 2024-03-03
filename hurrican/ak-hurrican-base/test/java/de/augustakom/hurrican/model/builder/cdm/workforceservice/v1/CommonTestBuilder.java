/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class CommonTestBuilder extends CommonBuilder {

    public CommonTestBuilder() {
        withAdditionalContractInfo("500651956");
        withContractId("823456");
        withLineId("WV10405667");
        withPorting(new PortingTestBuilder().build());
        withContractPartner("contract partner");
        withProductName("product name");
        withServiceCancelled("service cancelled");
        withServiceNew("service new");
    }
}