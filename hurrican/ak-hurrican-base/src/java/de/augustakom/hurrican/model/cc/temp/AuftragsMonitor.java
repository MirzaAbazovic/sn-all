/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 09:01:12
 */
package de.augustakom.hurrican.model.cc.temp;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Modell stellt eine Uebersicht ueber die CC- und Billing-Auftraege dar. <br> Wichtig: Das Modell ist nur temporaer -
 * es wird nicht physikalisch gespeichert!
 *
 *
 */
public class AuftragsMonitor extends AbstractCCModel implements DebugModel, KundenModel, CCAuftragModel {

    /**
     * Wert fuer <code>amAktion</code>, der eine Auftragskuendigung kennzeichnet.
     */
    public static final int AM_AKTION_KUENDIGEN = 1;
    /**
     * Wert fuer <code>amAktion</code>, der eine Auftragsanlage kennzeichnet.
     */
    public static final int AM_AKTION_ANLEGEN = 2;
    /**
     * Wert fuer <code>amAktion</code>, der einen Leistungsabgleich kennzeichnet.
     */
    public static final int AM_AKTION_LEISTUNGS_DIFF = 3;

    private Long kundeNo = null;
    private String ccProdukt = null;
    private Long ccProduktId = null;
    private String oeName = null;
    private Long auftragId = null;
    private Long auftragStatusId = null;
    private Long auftragNoOrig = null;
    private String oldAuftragNoOrig = null;
    private Integer bundleOrderNo = null;
    private String bundleNoHerkunft = null;
    private int amAktion = -1;
    private String amText = null;
    private int differenz = 0;
    private int anzahlCC = 0;
    private List<LeistungsDiffView> leistungsDiffs = null;

    /**
     * Default-Konstruktor
     */
    public AuftragsMonitor() {
        super();
    }

    /**
     * Konstruktor mit Angabe der zu Kundennummer des Kunden, fuer den der Auftragsmonitor erstellt wurde.
     *
     * @param kundeNo
     */
    public AuftragsMonitor(Long kundeNo) {
        super();
        this.kundeNo = kundeNo;
    }

    /**
     * Ueberprueft, ob es sich bei der Aktion um eine Kuendigung handelt.
     */
    public boolean isKuendigung() {
        return amAktion == AM_AKTION_KUENDIGEN;
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the amAktion.
     */
    public int getAmAktion() {
        return amAktion;
    }

    /**
     * @param amAktion The amAktion to set.
     */
    public void setAmAktion(int amAktion) {
        this.amAktion = amAktion;
    }

    /**
     * @return Returns the amText.
     */
    public String getAmText() {
        return amText;
    }

    /**
     * @param amText The amText to set.
     */
    public void setAmText(String amText) {
        this.amText = amText;
    }

    /**
     * @return Returns the anzahlCC.
     */
    public int getAnzahlCC() {
        return anzahlCC;
    }

    /**
     * @param anzahlCC The anzahlCC to set.
     */
    public void setAnzahlCC(int anzahlCC) {
        this.anzahlCC = anzahlCC;
    }

    /**
     * @return Returns the differenz.
     */
    public int getDifferenz() {
        return differenz;
    }

    /**
     * @param differenz The differenz to set.
     */
    public void setDifferenz(int auftragDiff) {
        this.differenz = auftragDiff;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return this.auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the auftragStatusId.
     */
    public Long getAuftragStatusId() {
        return this.auftragStatusId;
    }

    /**
     * @param auftragStatusId The auftragStatusId to set.
     */
    public void setAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the auftragNoOrigOld.
     */
    public String getOldAuftragNoOrig() {
        return this.oldAuftragNoOrig;
    }

    /**
     * @param auftragNoOrigOld The auftragNoOrigOld to set.
     */
    public void setOldAuftragNoOrig(String auftragNoOrigOld) {
        this.oldAuftragNoOrig = auftragNoOrigOld;
    }

    /**
     * @return Returns the leistung.
     */
    public String getCcProdukt() {
        return ccProdukt;
    }

    /**
     * @param leistung The leistung to set.
     */
    public void setCcProdukt(String leistung) {
        this.ccProdukt = leistung;
    }

    /**
     * @return Returns the ccProduktId.
     */
    public Long getCcProduktId() {
        return this.ccProduktId;
    }

    /**
     * @param ccProduktId The ccProduktId to set.
     */
    public void setCcProduktId(Long ccProduktId) {
        this.ccProduktId = ccProduktId;
    }

    /**
     * @return Returns the oeName.
     */
    public String getOeName() {
        return oeName;
    }

    /**
     * @param oeName The oeName to set.
     */
    public void setOeName(String oeName) {
        this.oeName = oeName;
    }

    /**
     * @return Returns the bundleOrderNo.
     */
    public Integer getBundleOrderNo() {
        return bundleOrderNo;
    }

    /**
     * @param bundleOrderNo The bundleOrderNo to set.
     */
    public void setBundleOrderNo(Integer bundleOrderNo) {
        this.bundleOrderNo = bundleOrderNo;
    }

    /**
     * @return Returns the bundleNoHerkunft.
     */
    public String getBundleNoHerkunft() {
        return bundleNoHerkunft;
    }

    /**
     * @param bundleNoHerkunft The bundleNoHerkunft to set.
     */
    public void setBundleNoHerkunft(String bundleNoHerkunft) {
        this.bundleNoHerkunft = bundleNoHerkunft;
    }

    /**
     * @return Returns the leistungsDiffs.
     */
    public List<LeistungsDiffView> getLeistungsDiffs() {
        return this.leistungsDiffs;
    }

    /**
     * @param leistungsDiffs The leistungsDiffs to set.
     */
    public void setLeistungsDiffs(List<LeistungsDiffView> leistungsDiffs) {
        this.leistungsDiffs = leistungsDiffs;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + AuftragsMonitor.class.getName());
            logger.debug("  auftragNoOrig  : " + getAuftragNoOrig());
            logger.debug("  Leistung/A-Art : " + getCcProdukt());
            logger.debug("  Text           : " + getAmText());
            logger.debug("  Aktion(1=k;2=a): " + getAmAktion());
        }
    }

}


