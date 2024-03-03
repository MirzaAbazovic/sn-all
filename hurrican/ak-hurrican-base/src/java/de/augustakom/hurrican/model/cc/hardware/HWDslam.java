/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 12:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.HVTTechnik;

/**
 * Modell, um einen Hardware-DSLAM (Digital Subscriber Line Access Multiplexer) abzubilden.
 * ------------------------------------------------------------ Beschreibung: <br> In der xDSL-Technologie (Digital
 * Subscriber Line) eine in den Anschlussknoten (Vermittlungsstellen) eingesetzte Netzkomponente zur Konzentration
 * mehrerer xDSL-Verbindungen. <br> Funktionen des DSLAM: <br> <ul> <li>Zugang zum ATM-Breitbandnetz über ein
 * 155-Mbit/s-Interface (STM-1, Synchronous Transport Module - Layer 1). <li>Multiplexen der virtuellen Verbindungen auf
 * VPI/VCI-Basis (Virtual Path Identifier/Virtual Channel Identifier). <li>Bitratenanpassung an die
 * Übertragungsgeschwindigkeit der xDSL-Strecke (Anschlussleitung) <li>Bereitstellung von Netzmanagementinformationen
 * <li>Einrichtung permanenter virtueller Verbindungen (PVC, Permanent Virtual Circuit). <li>Einrichtung und Auslösung
 * gewählter virtueller Verbindungen (SVC, Switched Virtual Circuit). <li>Verkehrssteuerung (Policing) zur
 * Gewährleistung der Dienstgüte. </ul> -------------------------------------------------------------
 *
 *
 */
@DslamType
public class HWDslam extends HWRack {

    public static final String ETHERNET = "VLAN";
    public static final String ATM = "VPI";
    public static final String ALT_GSLAM_BEZ = "altGslamBez";

    private String dhcp82Name = null;
    private Integer vpiUbrADSL = null;
    private Integer vpiSDSL = null;
    private Integer vpiCpeMgmt = null;
    private Integer outerTagADSL = null;
    private Integer outerTagSDSL = null;
    private Integer outerTagVoip = null;
    private Integer outerTagCpeMgmt = null;
    private Integer outerTagIadMgmt = null;
    private Integer brasVpiADSL = null;
    private Integer brasOuterTagADSL = null;
    private Integer brasOuterTagSDSL = null;
    private Integer brasOuterTagVoip = null;
    private String ipAdress = null;
    public static final String IP_ADDRESS = "ipAdress";
    private String einbauplatz = null;
    private String anschluss = null;
    private String ansSlotPort = null;
    private String ansArt = null;
    private Boolean schmidtschesKonzept = null;
    private String softwareVersion = null;
    private String erxIfaceDaten = null;
    private String erxStandort = null;
    private String physikArt = null;
    private String physikWert = null;
    private String atmPattern = null;
    private Integer ccOffset = null;
    private Integer svlan;
    private String altGslamBez;
    private String dslamType;

    /**
     * Ueberprueft, ob es sich um einen Siemens-DSLAM handelt.
     */
    public boolean isSiemensDSLAM() {
        return NumberTools.equal(getHwProducer(), HVTTechnik.SIEMENS);
    }

    /**
     * Ueberprueft, ob es sich um einen Huawei-DSLAM handelt.
     */
    public boolean isHuaweiDSLAM() {
        return NumberTools.equal(getHwProducer(), HVTTechnik.HUAWEI);
    }

    public String getAnsArt() {
        return ansArt;
    }

    public void setAnsArt(String ansArt) {
        this.ansArt = ansArt;
    }

    public String getAnschluss() {
        return anschluss;
    }

    public void setAnschluss(String anschluss) {
        this.anschluss = anschluss;
    }

    public String getAnsSlotPort() {
        return ansSlotPort;
    }

    public void setAnsSlotPort(String ansSlotPort) {
        this.ansSlotPort = ansSlotPort;
    }

    public String getDhcp82Name() {
        return dhcp82Name;
    }

    public void setDhcp82Name(String dhcp82Name) {
        this.dhcp82Name = dhcp82Name;
    }

    public String getEinbauplatz() {
        return einbauplatz;
    }

    public void setEinbauplatz(String einbauplatz) {
        this.einbauplatz = einbauplatz;
    }

    public String getErxIfaceDaten() {
        return erxIfaceDaten;
    }

    public void setErxIfaceDaten(String erxIfaceDaten) {
        this.erxIfaceDaten = erxIfaceDaten;
    }

    public String getErxStandort() {
        return erxStandort;
    }

    public void setErxStandort(String erxStandort) {
        this.erxStandort = erxStandort;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public Integer getOuterTagADSL() {
        return outerTagADSL;
    }

    public void setOuterTagADSL(Integer outerTagADSL) {
        this.outerTagADSL = outerTagADSL;
    }

    public Integer getOuterTagSDSL() {
        return outerTagSDSL;
    }

    public void setOuterTagSDSL(Integer outerTagSDSL) {
        this.outerTagSDSL = outerTagSDSL;
    }

    public String getPhysikArt() {
        return physikArt;
    }

    public void setPhysikArt(String physikArt) {
        this.physikArt = physikArt;
    }

    public String getPhysikWert() {
        return physikWert;
    }

    public void setPhysikWert(String physikWert) {
        this.physikWert = physikWert;
    }

    public Boolean getSchmidtschesKonzept() {
        return schmidtschesKonzept;
    }

    public void setSchmidtschesKonzept(Boolean schmidtschesKonzept) {
        this.schmidtschesKonzept = schmidtschesKonzept;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public Integer getVpiSDSL() {
        return vpiSDSL;
    }

    public void setVpiSDSL(Integer vpiSDSL) {
        this.vpiSDSL = vpiSDSL;
    }

    public Integer getVpiUbrADSL() {
        return vpiUbrADSL;
    }

    public void setVpiUbrADSL(Integer vpiUbrADSL) {
        this.vpiUbrADSL = vpiUbrADSL;
    }

    public String getAtmPattern() {
        return atmPattern;
    }

    public void setAtmPattern(String atmPattern) {
        this.atmPattern = atmPattern;
    }

    public Integer getVpiCpeMgmt() {
        return vpiCpeMgmt;
    }

    public void setVpiCpeMgmt(Integer vpiCpeMgmt) {
        this.vpiCpeMgmt = vpiCpeMgmt;
    }

    public Integer getOuterTagCpeMgmt() {
        return outerTagCpeMgmt;
    }

    public void setOuterTagCpeMgmt(Integer outerTagCpeMgmt) {
        this.outerTagCpeMgmt = outerTagCpeMgmt;
    }

    public Integer getOuterTagIadMgmt() {
        return outerTagIadMgmt;
    }

    public void setOuterTagIadMgmt(Integer outerTagIadMgmt) {
        this.outerTagIadMgmt = outerTagIadMgmt;
    }

    public Integer getOuterTagVoip() {
        return outerTagVoip;
    }

    public void setOuterTagVoip(Integer outerTagVoip) {
        this.outerTagVoip = outerTagVoip;
    }

    public Integer getBrasVpiADSL() {
        return brasVpiADSL;
    }

    public void setBrasVpiADSL(Integer brasVpiADSL) {
        this.brasVpiADSL = brasVpiADSL;
    }

    public Integer getBrasOuterTagADSL() {
        return brasOuterTagADSL;
    }

    public void setBrasOuterTagADSL(Integer brasOuterTagADSL) {
        this.brasOuterTagADSL = brasOuterTagADSL;
    }

    public Integer getBrasOuterTagSDSL() {
        return brasOuterTagSDSL;
    }

    public void setBrasOuterTagSDSL(Integer brasOuterTagSDSL) {
        this.brasOuterTagSDSL = brasOuterTagSDSL;
    }

    public Integer getBrasOuterTagVoip() {
        return brasOuterTagVoip;
    }

    public void setBrasOuterTagVoip(Integer brasOuterTagVoip) {
        this.brasOuterTagVoip = brasOuterTagVoip;
    }

    public void setCcOffset(Integer ccOffset) {
        this.ccOffset = ccOffset;
    }

    public Integer getCcOffset() {
        return ccOffset;
    }

    public Integer getSvlan() {
        return svlan;
    }

    public void setSvlan(final Integer svlan) {
        this.svlan = svlan;
    }

    public String getAltGslamBez() {
        return altGslamBez;
    }

    public void setAltGslamBez(String altGslamBez) {
        this.altGslamBez = altGslamBez;
    }

    public String getDslamType() {
        return dslamType;
    }

    public void setDslamType(String dslamType) {
        this.dslamType = dslamType;
    }
}
