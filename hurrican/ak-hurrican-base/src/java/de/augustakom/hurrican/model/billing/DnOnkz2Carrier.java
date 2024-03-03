/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.04.2014
 */
package de.augustakom.hurrican.model.billing;


import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.TNB;

/**
 * Abbildung von ONKZ zum Carrier.
 */
public class DnOnkz2Carrier extends AbstractBillingModel {
    private String onkz;
    private String carrier;

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    @Transient
    public TNB getTNB() {
        for (TNB dnOnkzCarrier : TNB.values()) {
            if (StringUtils.equalsIgnoreCase(dnOnkzCarrier.carrierName, getCarrier())) {
                return dnOnkzCarrier;
            }
        }
        return null;
    }

}
