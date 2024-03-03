/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2012 08:49:58
 */
package de.augustakom.hurrican.model.billing;

@SuppressWarnings("unused")
public class BAuftragKombiBuilder extends BillingEntityBuilder<BAuftragKombiBuilder, BAuftragKombi> {

    private Long auftragNo = null;
    private String switchId = null;
    private String zielnummerKuerzung = null;
    private Long kwk = null;
    private Long timeSlotNo = null;


    public BAuftragKombiBuilder withTimeSlotNo(Long timeSlotNo) {
        this.timeSlotNo = timeSlotNo;
        return this;
    }

}


