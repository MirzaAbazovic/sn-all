/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.2006 13:23:19
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;

/**
 * View-Modell zur Abbildung von Leistungen und Leistungsbuendeln
 *
 *
 */

public class LeistungInLeistungsbuendelView extends AbstractCCModel {

    private Long lb2LeistungId = null;
    private Long lbId = null;
    private Long leistungId = null;
    private Long oeNo = null;
    private Boolean standard = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private Date verwendenBis = null;
    private Date verwendenVon = null;

    private String leistung = null;
    private String beschreibung = null;
    private Long externLeistungNo = null;
    private Long externSonstigesNo = null;

    /**
     * @return Returns the lb2LeistungId.
     */
    public Long getLb2LeistungId() {
        return lb2LeistungId;
    }


    /**
     * @param lb2LeistungId The lb2LeistungId to set.
     */
    public void setLb2LeistungId(Long lb2LeistungId) {
        this.lb2LeistungId = lb2LeistungId;
    }

    /**
     * @return Returns the beschreibung.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung The beschreibung to set.
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
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
     * @return Returns the leistung.
     */
    public String getLeistung() {
        return leistung;
    }

    /**
     * @param leistung The leistung to set.
     */
    public void setLeistung(String leistung) {
        this.leistung = leistung;
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
     * @return Returns the externLeistungNo.
     */
    public Long getExternLeistungNo() {
        return this.externLeistungNo;
    }

    /**
     * @param externLeistungNo The externLeistungNo to set.
     */
    public void setExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
    }


    /**
     * @return Returns the externSonstigesNo.
     */
    public Long getExternSonstigesNo() {
        return this.externSonstigesNo;
    }

    /**
     * @param externSonstigesNo The externSonstigesNo to set.
     */
    public void setExternSonstigesNo(Long externSonstigesNo) {
        this.externSonstigesNo = externSonstigesNo;
    }

}


