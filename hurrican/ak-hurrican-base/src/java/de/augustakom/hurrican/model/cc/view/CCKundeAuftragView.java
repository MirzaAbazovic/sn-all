/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 13:21:18
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragStatusModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View-Modell, um alle Auftraege eines Kunden mit allen wichtigen Auftrags-Daten aus dem CC-System darzustellen.
 *
 *
 */
public class CCKundeAuftragView extends AbstractCCModel implements CCAuftragModel, KundenModel, CCAuftragStatusModel {

    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private Long kundeNo = null;
    private Date inbetriebnahme = null;
    private Date kuendigung = null;
    private Integer buendelNr = null;
    private Boolean brauchtBuendel = null;
    private String anschlussart = null;
    private String produktNamePattern = null;
    private Long auftragStatusId = null;
    private String statusText = null;
    private Long vpnId = null;
    private Long vpnNr = null;
    private String vbz = null;
    private Long endstelleIdA = null;
    private String endstelleA = null;
    private Long endstelleIdB = null;
    private String endstelleB = null;
    private Long produktId = null;
    private Long produktGruppeId = null;
    private Boolean exportKdpM = null;
    private String auftragBemerkung = null;

    private String produktName = null;

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
     * @return Returns the anschlussart.
     */
    public String getAnschlussart() {
        return anschlussart;
    }

    /**
     * @param anschlussart The anschlussart to set.
     */
    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
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

    /**
     * @return Returns the brauchtBuendel.
     */
    public Boolean getBrauchtBuendel() {
        return brauchtBuendel;
    }

    /**
     * @param brauchtBuendel The brauchtBuendel to set.
     */
    public void setBrauchtBuendel(Boolean brauchtBuendel) {
        this.brauchtBuendel = brauchtBuendel;
    }

    /**
     * @return Returns the buendelNr.
     */
    public Integer getBuendelNr() {
        return buendelNr;
    }

    /**
     * @param buendelNr The buendelNr to set.
     */
    public void setBuendelNr(Integer buendelNr) {
        this.buendelNr = buendelNr;
    }

    /**
     * @return Returns the endstelleIdA.
     */
    public Long getEndstelleIdA() {
        return endstelleIdA;
    }

    /**
     * @param endstelleIdA The endstelleIdA to set.
     */
    public void setEndstelleIdA(Long endstelleIdA) {
        this.endstelleIdA = endstelleIdA;
    }

    /**
     * @return Returns the endstelleA.
     */
    public String getEndstelleA() {
        return endstelleA;
    }

    /**
     * @param endstelleA The endstelleA to set.
     */
    public void setEndstelleA(String endstelleA) {
        this.endstelleA = endstelleA;
    }

    /**
     * @return Returns the endstelleIdB.
     */
    public Long getEndstelleIdB() {
        return endstelleIdB;
    }

    /**
     * @param endstelleIdB The endstelleIdB to set.
     */
    public void setEndstelleIdB(Long endstelleIdB) {
        this.endstelleIdB = endstelleIdB;
    }

    /**
     * @return Returns the endstelleB.
     */
    public String getEndstelleB() {
        return endstelleB;
    }

    /**
     * @param endstelleB The endstelleB to set.
     */
    public void setEndstelleB(String endstelleB) {
        this.endstelleB = endstelleB;
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
     * @return Returns the produktGruppeId.
     */
    public Long getProduktGruppeId() {
        return produktGruppeId;
    }

    /**
     * @param produktGruppeId The produktGruppeId to set.
     */
    public void setProduktGruppeId(Long produktGruppeId) {
        this.produktGruppeId = produktGruppeId;
    }

    /**
     * @return Returns the statusText.
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * @param statusText The statusText to set.
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param verbindungsBezeichnung The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    /**
     * @return Returns the vpnId.
     */
    public Long getVpnId() {
        return vpnId;
    }

    /**
     * @param vpnId The vpnId to set.
     */
    public void setVpnId(Long vpnId) {
        this.vpnId = vpnId;
    }

    /**
     * @return Returns the vpnNr.
     */
    public Long getVpnNr() {
        return vpnNr;
    }

    /**
     * @param vpnNr The vpnNr to set.
     */
    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    /**
     * @return Returns the exportKdpM.
     */
    public Boolean getExportKdpM() {
        return exportKdpM;
    }

    /**
     * @param exportKdpM The exportKdpM to set.
     */
    public void setExportKdpM(Boolean exportKdpM) {
        this.exportKdpM = exportKdpM;
    }

    /**
     * @return Returns the produktId.
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId The produktId to set.
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * @return Returns the produktNamePattern.
     */
    public String getProduktNamePattern() {
        return this.produktNamePattern;
    }

    /**
     * @param produktNamePattern The produktNamePattern to set.
     */
    public void setProduktNamePattern(String produktNamePattern) {
        this.produktNamePattern = produktNamePattern;
    }

    /**
     * @return the auftragBemerkung
     */
    public String getAuftragBemerkung() {
        return auftragBemerkung;
    }

    /**
     * @param auftragBemerkung the auftragBemerkung to set
     */
    public void setAuftragBemerkung(String auftragBemerkung) {
        this.auftragBemerkung = auftragBemerkung;
    }

}


