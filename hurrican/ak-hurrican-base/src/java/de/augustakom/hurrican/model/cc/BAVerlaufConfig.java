/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 09:44:47
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell-Klasse fuer die Konfiguration eines Bauauftrags-Anlass. <br> Die Konfiguration verweist auf eine
 * Abteilungskonfiguration (BAVerlaufAbtConfig), ueber die die betroffenen Abteilungen definiert sind. <br><br> Das
 * Modell kann als Default fuer einen Anlass verwendet werden oder auch speziell fuer einen Anlass und ein Produkt. <br>
 * Bsp.: Anlass  = Anschlussuebernahme Produkt = <null> --> manuelle Verteilung Anlass  = Anschlussuebernahme Produkt =
 * PremiumCall --> automatische Verteilung an EWSD, SDH, IPS
 *
 *
 */
public class BAVerlaufConfig extends AbstractCCHistoryUserModel {

    public static final Long CONFIG_ID_MANUELL = Long.valueOf(1);

    private Long prodId = null;
    private Long baVerlAnlass = null;
    private Long abtConfigId = null;
    private Boolean autoVerteilen;
    private Boolean cpsNecessary;

    public Long getAbtConfigId() {
        return this.abtConfigId;
    }

    public void setAbtConfigId(Long abtConfigId) {
        this.abtConfigId = abtConfigId;
    }

    public Long getBaVerlAnlass() {
        return this.baVerlAnlass;
    }

    public void setBaVerlAnlass(Long baVerlAnlass) {
        this.baVerlAnlass = baVerlAnlass;
    }

    public Long getProdId() {
        return this.prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public Boolean getAutoVerteilen() {
        return autoVerteilen;
    }

    public void setAutoVerteilen(Boolean autoVerteilen) {
        this.autoVerteilen = autoVerteilen;
    }

    /**
     * Das Flag gibt an, ob fuer eine automatische Verteilung der Auftrag CPS-faehig sein muss. Bei Angabe von TRUE wird
     * der Auftrag also nur dann automatisch verteilt, wenn eine CPS-Transaktion theoretisch ausgefuehrt werden kann.
     *
     * @return
     */
    public Boolean getCpsNecessary() {
        return cpsNecessary;
    }

    public void setCpsNecessary(Boolean cpsNecessary) {
        this.cpsNecessary = cpsNecessary;
    }

}


