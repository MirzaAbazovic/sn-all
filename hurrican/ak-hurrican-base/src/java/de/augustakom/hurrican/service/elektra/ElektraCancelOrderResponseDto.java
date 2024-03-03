/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.14
 */
package de.augustakom.hurrican.service.elektra;


public class ElektraCancelOrderResponseDto extends ElektraResponseDto {

    private boolean reclaimPositions;

    public boolean isReclaimPositions() {
        return reclaimPositions;
    }

    public void setReclaimPositions(boolean reclaimPositions) {
        this.reclaimPositions = reclaimPositions;
    }
}
