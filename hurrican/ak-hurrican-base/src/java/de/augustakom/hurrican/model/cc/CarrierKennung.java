/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2006 14:54:27
 */
package de.augustakom.hurrican.model.cc;

/**
 * Modell zur Abbildung von Carrier-Kennungen. <br> Eine Carrier-Kennung sind die Daten, die die Firma gegenueber einem
 * anderen Carrier (z.B. DTAG) besitzt - wie z.B. Kundennummer, Portierungskennung etc.
 *
 *
 */
public class CarrierKennung extends AbstractCCIDModel {

    private static final long serialVersionUID = 1293851643736207201L;

    public static final String DTAG_KUNDEN_NR_MNET = "5920312290";
    public static final String DTAG_LEISTUNGS_NR_MNET = "0000004364";
    public static final String DTAG_KUNDEN_NR_AUGUSTAKOM = "5920314090";
    public static final String DTAG_LEISTUNGS_NR_AUGUSTAKOM = "0000004488";

    public static final Long ID_MNET_MUENCHEN = Long.valueOf(4);
    public static final Long ID_MNET_AUGSBURG = Long.valueOf(2);
    public static final Long ID_AUGUSTAKOM = Long.valueOf(1);

    private Long carrierId;
    private String bezeichnung;
    private String portierungsKennung;
    /**
     * Kennung des Carriers fuer el. TAL-Bestellungen zurueck
     */
    private String kundenNr;
    private String witaLeistungsNr;
    private String name;
    private String strasse;
    private String plz;
    private String ort;
    private String elTalAbsenderId;
    private String userW;
    /**
     * BKTO-Nummer zur Fakturierung der el. TAL-Bestellungen
     */
    private String bktoNummer;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public String getKundenNr() {
        return kundenNr;
    }

    public void setKundenNr(String kundenNr) {
        this.kundenNr = kundenNr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getPortierungsKennung() {
        return portierungsKennung;
    }

    public void setPortierungsKennung(String portierungsKennung) {
        this.portierungsKennung = portierungsKennung;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getElTalAbsenderId() {
        return elTalAbsenderId;
    }

    public void setElTalAbsenderId(String elTalAbsenderId) {
        this.elTalAbsenderId = elTalAbsenderId;
    }

    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    public String getWitaLeistungsNr() {
        return witaLeistungsNr;
    }

    public void setWitaLeistungsNr(String witaLeistungsNr) {
        this.witaLeistungsNr = witaLeistungsNr;
    }

    public String getBktoNummer() {
        return bktoNummer;
    }

    public void setBktoNummer(String bktoNummer) {
        this.bktoNummer = bktoNummer;
    }

}
