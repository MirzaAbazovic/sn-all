/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2006 13:14:00
 */
package de.augustakom.hurrican.model.cc.dn;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell stellt die Zuordnung der Leistungen zu den Leistungbuendeln dar
 *
 *
 */
public class Lb2Leistung extends AbstractCCIDModel {

    private Long lbId = null;
    private Long leistungId = null;
    private Long oeNo = null;
    private Boolean standard = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private Date verwendenBis = null;
    private Date verwendenVon = null;
    private String defParamValue = null;

    /**
     * @return Returns the lbId.
     */
    public Long getLbId() {
        return lbId;
    }

    /**
     * @param lbId The lbId to set.
     */
    public void setLbId(Long lbId) {
        this.lbId = lbId;
    }

    /**
     * @return Returns the leistungId.
     */
    public Long getLeistungId() {
        return leistungId;
    }

    /**
     * @param leistungId The leistungId to set.
     */
    public void setLeistungId(Long leistungId) {
        this.leistungId = leistungId;
    }

    /**
     * @return Returns the oeNo.
     */
    public Long getOeNo() {
        return oeNo;
    }

    /**
     * @param oeNo The oeNo to set.
     */
    public void setOeNo(Long oeNo) {
        this.oeNo = oeNo;
    }


    /**
     * @return Returns the gueltigBis.
     */
    public Date getGueltigBis() {
        return gueltigBis;
    }


    /**
     * @param gueltigBis The gueltigBis to set.
     */
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }


    /**
     * @return Returns the gueltigVon.
     */
    public Date getGueltigVon() {
        return gueltigVon;
    }


    /**
     * @param gueltigVon The gueltigVon to set.
     */
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }


    /**
     * @return Returns the standard.
     */
    public Boolean getStandard() {
        return standard;
    }


    /**
     * @param standard The standard to set.
     */
    public void setStandard(Boolean standard) {
        this.standard = standard;
    }


    /**
     * @return Returns the verwendenBis.
     */
    public Date getVerwendenBis() {
        return verwendenBis;
    }


    /**
     * @param verwendenBis The verwendenBis to set.
     */
    public void setVerwendenBis(Date verwendenBis) {
        this.verwendenBis = verwendenBis;
    }


    /**
     * @return Returns the verwendenVon.
     */
    public Date getVerwendenVon() {
        return verwendenVon;
    }


    /**
     * @param verwendenVon The verwendenVon to set.
     */
    public void setVerwendenVon(Date verwendenVon) {
        this.verwendenVon = verwendenVon;
    }


    /**
     * @return Returns the defParamValue.
     */
    public String getDefParamValue() {
        return defParamValue;
    }


    /**
     * @param defParamValue The defParamValue to set.
     */
    public void setDefParamValue(String defParamValue) {
        this.defParamValue = defParamValue;
    }


}


