/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2005 13:31:03
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung eines Rufnummern-Blocks.
 *
 *
 */
public class DNBlock extends AbstractHistoryModel {

    private Long blockNo = null;
    private Long blockNoOrig = null;
    private String onkz = null;
    private String blockId = null;
    private String carrier = null;

    /**
     * @return Returns the blockId.
     */
    public String getBlockId() {
        return blockId;
    }

    /**
     * @param blockId The blockId to set.
     */
    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    /**
     * @return Returns the blockNo.
     */
    public Long getBlockNo() {
        return blockNo;
    }

    /**
     * @param blockNo The blockNo to set.
     */
    public void setBlockNo(Long blockNo) {
        this.blockNo = blockNo;
    }

    /**
     * @return Returns the blockNoOrig.
     */
    public Long getBlockNoOrig() {
        return blockNoOrig;
    }

    /**
     * @param blockNoOrig The blockNoOrig to set.
     */
    public void setBlockNoOrig(Long blockNoOrig) {
        this.blockNoOrig = blockNoOrig;
    }

    /**
     * @return Returns the carrier.
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * @param carrier The carrier to set.
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * @return Returns the onkz.
     */
    public String getOnkz() {
        return onkz;
    }

    /**
     * @param onkz The onkz to set.
     */
    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

}


