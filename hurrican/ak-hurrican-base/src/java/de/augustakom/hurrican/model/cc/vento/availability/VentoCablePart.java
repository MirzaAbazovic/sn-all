/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2012 10:45:27
 */
package de.augustakom.hurrican.model.cc.vento.availability;

import java.math.*;

/**
 *
 */
public class VentoCablePart {

    private BigDecimal diameterInMillimeter;
    private Integer lengthInMeter;

    public BigDecimal getDiameterInMillimeter() {
        return diameterInMillimeter;
    }

    public void setDiameterInMillimeter(BigDecimal diameterInMillimeter) {
        this.diameterInMillimeter = diameterInMillimeter;
    }

    public Integer getLengthInMeter() {
        return lengthInMeter;
    }

    public void setLengthInMeter(Integer lengthInMeter) {
        this.lengthInMeter = lengthInMeter;
    }

}


