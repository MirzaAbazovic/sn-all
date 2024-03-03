/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 15:46:52
 */
package de.augustakom.hurrican.model.cc.tal;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell bildet das Mapping des Dtag-Produktes auf den Eintrag in der T_EQUIPMENT.RANG_SS_TYPE ab
 *
 *
 */
public class ProduktDtag extends AbstractCCIDModel {

    private String rangSsType = null;
    private Integer produktDtag = null;
    private Integer b0102 = null;
    private String b0103 = null;
    private String b0104 = null;

    /**
     * @return Returns the produktDtag.
     */
    public Integer getProduktDtag() {
        return produktDtag;
    }

    /**
     * @param produktDtag The produktDtag to set.
     */
    public void setProduktDtag(Integer produktDtag) {
        this.produktDtag = produktDtag;
    }

    /**
     * @return Returns the rangSsType.
     */
    public String getRangSsType() {
        return rangSsType;
    }

    /**
     * @param rangSsType The rangSsType to set.
     */
    public void setRangSsType(String rangSsType) {
        this.rangSsType = rangSsType;
    }


    /**
     * @return Returns the b0102.
     */
    public Integer getB0102() {
        return b0102;
    }


    /**
     * @param b0102 The b0102 to set.
     */
    public void setB0102(Integer b0102) {
        this.b0102 = b0102;
    }


    /**
     * @return Returns the b0103.
     */
    public String getB0103() {
        return b0103;
    }


    /**
     * @param b0103 The b0103 to set.
     */
    public void setB0103(String b0103) {
        this.b0103 = b0103;
    }


    /**
     * @return Returns the b0104.
     */
    public String getB0104() {
        return b0104;
    }


    /**
     * @param b0104 The b0104 to set.
     */
    public void setB0104(String b0104) {
        this.b0104 = b0104;
    }

}


