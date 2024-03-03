/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:14:34
 */
package de.augustakom.hurrican.model.cc.dn;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * View-Klasse fuer die Abbildung von Rufnummern-Leistungen.
 *
 *
 */
public class DNLeistungsView extends AbstractCCIDModel {

    private Long dnNo = null;
    private Long leistungsId = null;
    private String leistung = null;
    private Date amRealisierung = null;
    private String amUserRealisierung = null;
    private Date amKuendigung = null;
    private String amUserKuendigung = null;
    private Date ewsdRealisierung = null;
    private String ewsdUserRealisierung = null;
    private Date ewsdKuendigung = null;
    private String ewsdUserKuendigung = null;
    private String parameter = null;
    private Long parameterId = null;
    private String provisioningName = null;

    /**
     * @return Returns the ewsdKuendigung.
     */
    public Date getEwsdKuendigung() {
        return ewsdKuendigung;
    }

    /**
     * @param ewsdKuendigung The ewsdKuendigung to set.
     */
    public void setEwsdKuendigung(Date ewsdKuendigung) {
        this.ewsdKuendigung = ewsdKuendigung;
    }

    /**
     * @return Returns the ewsdRealisierung.
     */
    public Date getEwsdRealisierung() {
        return ewsdRealisierung;
    }

    /**
     * @param ewsdRealisierung The ewsdRealisierung to set.
     */
    public void setEwsdRealisierung(Date ewsdRealisierung) {
        this.ewsdRealisierung = ewsdRealisierung;
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
     * @return Returns the amKuendigung.
     */
    public Date getAmKuendigung() {
        return amKuendigung;
    }

    /**
     * @param amKuendigung The amKuendigung to set.
     */
    public void setAmKuendigung(Date amKuendigung) {
        this.amKuendigung = amKuendigung;
    }

    /**
     * @return Returns the amRealisierung.
     */
    public Date getAmRealisierung() {
        return amRealisierung;
    }

    /**
     * @param amRealisierung The amRealisierung to set.
     */
    public void setAmRealisierung(Date amRealisierung) {
        this.amRealisierung = amRealisierung;
    }

    /**
     * @return Returns the parameter.
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter The parameter to set.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * @return Returns the dnNo.
     */
    public Long getDnNo() {
        return dnNo;
    }

    /**
     * @param dnNo The dnNo to set.
     */
    public void setDnNo(Long dnNo) {
        this.dnNo = dnNo;
    }

    /**
     * @return Returns the parameterId.
     */
    public Long getParameterId() {
        return parameterId;
    }

    /**
     * @param parameterId The parameterId to set.
     */
    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    /**
     * @return Returns the ewsdUserKuendigung.
     */
    public String getEwsdUserKuendigung() {
        return ewsdUserKuendigung;
    }

    /**
     * @param ewsdUserKuendigung The ewsdUserKuendigung to set.
     */
    public void setEwsdUserKuendigung(String ewsdUserKuendigung) {
        this.ewsdUserKuendigung = ewsdUserKuendigung;
    }

    /**
     * @return Returns the ewsdUserRealisierung.
     */
    public String getEwsdUserRealisierung() {
        return ewsdUserRealisierung;
    }

    /**
     * @param ewsdUserRealisierung The ewsdUserRealisierung to set.
     */
    public void setEwsdUserRealisierung(String ewsdUserRealisierung) {
        this.ewsdUserRealisierung = ewsdUserRealisierung;
    }

    /**
     * @return Returns the amUserKuendigung.
     */
    public String getAmUserKuendigung() {
        return amUserKuendigung;
    }

    /**
     * @param amUserKuendigung The amUserKuendigung to set.
     */
    public void setAmUserKuendigung(String amUserKuendigung) {
        this.amUserKuendigung = amUserKuendigung;
    }

    /**
     * @return Returns the amUserRealisierung.
     */
    public String getAmUserRealisierung() {
        return amUserRealisierung;
    }

    /**
     * @param amUserRealisierung The amUserRealisierung to set.
     */
    public void setAmUserRealisierung(String amUserRealisierung) {
        this.amUserRealisierung = amUserRealisierung;
    }

    /**
     * @return Returns the leistungsId.
     */
    public Long getLeistungsId() {
        return leistungsId;
    }

    /**
     * @param leistungsId The leistungsId to set.
     */
    public void setLeistungsId(Long leistungsId) {
        this.leistungsId = leistungsId;
    }

    /**
     * @return Returns the provisioningName.
     */
    public String getProvisioningName() {
        return provisioningName;
    }

    /**
     * @param provisioningName The provisioningName to set.
     */
    public void setProvisioningName(String provisioningName) {
        this.provisioningName = provisioningName;
    }
}


