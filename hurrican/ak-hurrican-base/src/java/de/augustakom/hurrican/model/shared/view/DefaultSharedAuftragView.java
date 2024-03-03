/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 13:12:15
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.shared.AbstractSharedModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Basisklasse fuer alle Views, die Auftrags- und Kunden-Daten enthalten.
 */
public class DefaultSharedAuftragView extends AbstractSharedModel implements CCAuftragModel, KundenModel, DebugModel {

    private Long auftragId;
    private Long kundeNo;
    private Long hauptKundenNo;
    private Long auftragNoOrig;
    private String name;
    private String vorname;
    private String kundenTyp;
    private String vip;
    private Boolean fernkatastrophe;
    private String vbz;
    private Long auftragStatusId;
    private String auftragStatusText;
    private Long prodId;
    private String anschlussart;
    private String prodNamePattern;
    private Date inbetriebnahme;
    private Date kuendigung;
    private String produktName;
    private String niederlassung;
    private Long projectResponsibleUserId;
    private String projectResponsibleUserName;

    /**
     * Gibt den gesetzten Produktnamen zurueck. Ist dieser 'null', wird die Anschlussart zurueck gegeben.
     *
     * @return Returns the produktName.
     */
    public String getProduktName() {
        return (StringUtils.isNotBlank(produktName)) ? produktName : getAnschlussart();
    }

    /**
     * Wird nicht von der DAO-Klasse gesetzt sondern muss von einem Service extra aufgerufen werden. Dadurch kann das
     * NamePattern durch Auftragsparameter ersetzt werden.
     *
     * @param produktName The produktName to set.
     */
    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

    /**
     * Niederlassung aus dem CC-System.
     *
     * @return Returns the niederlassung.
     */
    public String getNiederlassung() {
        return niederlassung;
    }

    /**
     * Niederlassung aus dem CC-System.
     *
     * @param niederlassung The niederlassung to set.
     */
    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    /**
     * Auftrag-ID aus dem CC-System.
     *
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * Auftrag-ID aus dem CC-System.
     *
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * Gibt den Produktnamen zurueck.
     *
     * @return Returns the anschlussart.
     */
    public String getAnschlussart() {
        return anschlussart;
    }

    /**
     * Setzt den Produktnamen.
     *
     * @param anschlussart The anschlussart to set.
     */
    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }

    /**
     * @return Returns the prodNamePattern.
     */
    public String getProdNamePattern() {
        return this.prodNamePattern;
    }

    /**
     * @param prodNamePattern The prodNamePattern to set.
     */
    public void setProdNamePattern(String prodNamePattern) {
        this.prodNamePattern = prodNamePattern;
    }

    /**
     * @return Returns the auftragStatusText.
     */
    public String getAuftragStatusText() {
        return auftragStatusText;
    }

    /**
     * @param auftragStatusText The auftragStatusText to set.
     */
    public void setAuftragStatusText(String auftragStatusText) {
        this.auftragStatusText = auftragStatusText;
    }

    /**
     * Original Auftrags-No aus dem Billing-System.
     *
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * Original Auftrags-No aus dem Billing-System.
     *
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the fernkatastrophe.
     */
    public Boolean getFernkatastrophe() {
        return fernkatastrophe;
    }

    /**
     * @return Returns the fernkatastrophe as boolean.
     */
    public boolean isFernkatastrophe() {
        return (fernkatastrophe != null) ? fernkatastrophe.booleanValue() : false;
    }

    /**
     * @param fernkatastrophe The fernkatastrophe to set.
     */
    public void setFernkatastrophe(Boolean fernkatastrophe) {
        this.fernkatastrophe = fernkatastrophe;
    }

    /**
     * @return Returns the hauptKundenNo.
     */
    public Long getHauptKundenNo() {
        return hauptKundenNo;
    }

    /**
     * @param hauptKundenNo The hauptKundenNo to set.
     */
    public void setHauptKundenNo(Long hauptKundenNo) {
        this.hauptKundenNo = hauptKundenNo;
    }

    /**
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the kundenTyp.
     */
    public String getKundenTyp() {
        return kundenTyp;
    }

    /**
     * @param kundenTyp The kundenTyp to set.
     */
    public void setKundenTyp(String kundenTyp) {
        this.kundenTyp = kundenTyp;
    }

    /**
     * Name des Kunden.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Name des Kunden.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Technische Dokumentationsnummer (Leitungsnummer) aus dem CC-System.
     *
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * Technische Dokumentationsnummer (Leitungsnummer) aus dem CC-System.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return this.prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return Returns the vip.
     */
    public String getVip() {
        return vip;
    }

    /**
     * @param vip The vip to set.
     */
    public void setVip(String vip) {
        this.vip = vip;
    }

    /**
     * Vorname des Kunden.
     *
     * @return Returns the vorname.
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * Vorname des Kunden.
     *
     * @param vorname The vorname to set.
     */
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    /**
     * @return Returns the inbetriebnahme.
     */
    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    /**
     * @param inbetriebnahme The inbetriebnahme to set.
     */
    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    /**
     * @return Returns the kuendigung.
     */
    public Date getKuendigung() {
        return kuendigung;
    }

    /**
     * @param kuendigung The kuendigung to set.
     */
    public void setKuendigung(Date kuendigung) {
        this.kuendigung = kuendigung;
    }

    /**
     * @return Returns the auftragStatusId.
     */
    public Long getAuftragStatusId() {
        return auftragStatusId;
    }

    /**
     * @param auftragStatusId The auftragStatusId to set.
     */
    public void setAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;
    }

    public Long getProjectResponsibleUserId() {
        return projectResponsibleUserId;
    }

    public void setProjectResponsibleUserId(Long projectResponsibleUserId) {
        this.projectResponsibleUserId = projectResponsibleUserId;
    }

    public String getProjectResponsibleUserName() {
        return projectResponsibleUserName;
    }

    public void setProjectResponsibleUserName(String projectResponsibleUserName) {
        this.projectResponsibleUserName = projectResponsibleUserName;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + getClass().getName());
            logger.debug("  Auftrag-ID (CC): " + getAuftragId());
            logger.debug("  Auftrag Orig   : " + getAuftragNoOrig());
            logger.debug("  Kunde-No (Orig): " + getKundeNo());
            logger.debug("  Kunde (Name)   : " + getName());
            logger.debug("  VerbindungsBezeichnung            : " + getVbz());
            logger.debug("  Anschlussart   : " + getAnschlussart());
            logger.debug("  Auftrag-Status : " + getAuftragStatusText());
            logger.debug("...");
        }
    }

}


