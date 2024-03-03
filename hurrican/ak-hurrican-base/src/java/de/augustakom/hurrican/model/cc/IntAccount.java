/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 13:03:33
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell enthaelt Account-Daten. <br> Folgende Account-Arten werden von diesem Modell abgedeckt: <ul>
 * <li>Abrechnungsaccount <li>Einwahlaccount (fuer DSL) <li>Verwaltungsaccount (fuer Mail/Hosting) <li>Zusatz
 * Postfach-Account </ul>
 *
 *
 */
public class IntAccount extends AbstractCCHistoryModel implements DebugModel {

    public static final String DEFAULT_REALM = "dsl.mnet-online.de";

    /**
     * Default Seperator fuer Account / Realm
     */
    public static final String REALM_SEP_DEFAULT = "@";
    /**
     * Seperator fuer L2TPs Account / Realm
     */
    public static final String REALM_SEP_L2TP = "%";
    /**
     * Start-Zeichen fuer L2TP Default-Realm
     */
    public static final String REALM_START_L2TP = "#";

    public static final String ACCOUNT_PREFIX_DEFAULT = "XX";

    /**
     * Wert fuer <code>liNr</code>, der einen Abrechnungsaccount kennzeichnet.
     */
    public static final Integer LINR_ABRECHNUNGSACCOUNT = Integer.valueOf(0);
    /**
     * Wert fuer <code>liNr</code>, der einen Einwahlaccount kennzeichnet. (ADSL)
     */
    public static final Integer LINR_EINWAHLACCOUNT = Integer.valueOf(1);
    /**
     * Wert fuer <code>liNr</code>, der einen Verwaltungsaccount kennzeichnet.
     */
    public static final Integer LINR_VERWALTUNGSACCOUNT = Integer.valueOf(2);
    /**
     * Wert fuer <code>liNr</code>, der einen zusaetzlichen Postfachaccount kennzeichnet.
     */
    public static final Integer LINR_ZUSATZ_POSTFACHACCOUNT = Integer.valueOf(28);
    /**
     * Wert fuer <code>liNr</code>, der technisch einen Einwahlaccount kennzeichnet der allerdings nicht auf den
     * Kundenanschreiben erscheinen darf (SDSL)
     */
    public static final Integer LINR_EINWAHLACCOUNT_KONFIG = Integer.valueOf(4);

    /**
     * Wert fuer <code>status</code> der anzeigt, dass der Auftrag abgearbeitet wurde - der Account aktiv ist.
     */
    public static final Short STATUS_ABGEARBEITET = Short.valueOf((short) 0);
    /**
     * Wert fuer <code>status</code> der anzeigt, dass der Account umgehaengt werden soll.
     */
    public static final Short STATUS_UMHAENGEN = Short.valueOf((short) 1);
    /**
     * Wert fuer <code>status</code> der anzeigt, dass der Account einen anderen Tarif erhaelt.
     */
    public static final Short STATUS_TARIFWECHSEL = Short.valueOf((short) 2);
    /**
     * Wert fuer <code>status</code> der anzeigt, dass der Account gekuendigt wird.
     */
    public static final Short STATUS_KUENDIGUNG = Short.valueOf((short) 3);

    /**
     * Wert fuer <code>freigabe</code> der anzeigt, dass der Account nicht freigegeben ist.
     */
    public static final Short FREIGABE_NICHT_FREIGEGEBEN = Short.valueOf((short) 0);
    /**
     * Wert fuer <code>freigabe</code> der anzeigt, dass der Account freigegeben ist.
     */
    public static final Short FREIGABE_FREIGEGEBEN = Short.valueOf((short) 1);
    /**
     * Wert fuer <code>freigabe</code> der anzeigt, dass der Account zum Druck freigegeben ist.
     */
    public static final Short FREIGABE_FUER_DRUCK = Short.valueOf((short) 2);
    /**
     * Wert fuer <code>freigabe</code> der anzeigt, dass die Account-Daten gedruckt und an Kunden verschickt wurden.
     */
    public static final Short FREIGABE_GEDRUCKT = Short.valueOf((short) 3);

    /**
     * Account-Name - soll eindeutig einen Account identifizieren
     */
    private String account = null;
    private String passwort = null;
    private String url = null;
    private Boolean gesperrt = null;
    private String rufnummer = null;
    private Date loeschdatum = null;
    private Date kuendigungsdatum = null;
    private Date aenderungsdatum = null;
    private Date erstelldatum = null;
    private String nummer = null;
    private Short status = null;
    private Short freigabe = null;
    private String bemerkung = null;
    private Integer liNr = null;
    private Short abrechnungsmonat = null;
    private Integer hostingNeu = null;
    private Integer mailNeu = null;
    private Boolean evnStatus = null;
    private Boolean evnStatusPending = null;
    /**
     * Wird gesetzt, falls der Account manuell bearbeitet wurde.
     */
    private String bearbeiter;

    public static boolean isEinwahlaccount(Integer liNr) {
        return (NumberTools.isIn(liNr, new Number[] { LINR_EINWAHLACCOUNT, LINR_EINWAHLACCOUNT_KONFIG }));
    }

    /**
     * Ueberprueft, ob es sich bei dem Account um einen Einwahlaccount handelt.
     *
     * @return
     */
    public boolean isEinwahlaccount() {
        return IntAccount.isEinwahlaccount(getLiNr());
    }

    /**
     * Prueft, ob das Einwahlaccount gesperrt ist
     */
    public boolean isEinwahlaccountGesperrt() {
        return (getGesperrt() != null) && getGesperrt().booleanValue();
    }

    /**
     * @return Returns the abrechnungsmonat.
     */
    public Short getAbrechnungsmonat() {
        return abrechnungsmonat;
    }

    /**
     * @param abrechnungsmonat The abrechnungsmonat to set.
     */
    public void setAbrechnungsmonat(Short abrechnungsmonat) {
        this.abrechnungsmonat = abrechnungsmonat;
    }

    /**
     * @return Returns the account.
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account The account to set.
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return Returns the aenderungsdatum.
     */
    public Date getAenderungsdatum() {
        return aenderungsdatum;
    }

    /**
     * @param aenderungsdatum The aenderungsdatum to set.
     */
    public void setAenderungsdatum(Date aenderungsdatum) {
        this.aenderungsdatum = aenderungsdatum;
    }

    /**
     * @return Returns the bemerkung.
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * @param bemerkung The bemerkung to set.
     */
    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * @return Returns the erstelldatum.
     */
    public Date getErstelldatum() {
        return erstelldatum;
    }

    /**
     * @param erstelldatum The erstelldatum to set.
     */
    public void setErstelldatum(Date erstelldatum) {
        this.erstelldatum = erstelldatum;
    }

    /**
     * @return Returns the freigabe.
     */
    public Short getFreigabe() {
        return freigabe;
    }

    /**
     * @param freigabe The freigabe to set.
     */
    public void setFreigabe(Short freigabe) {
        this.freigabe = freigabe;
    }

    /**
     * @return Returns the gesperrt.
     */
    public Boolean getGesperrt() {
        return gesperrt;
    }

    /**
     * @param gesperrt The gesperrt to set.
     */
    public void setGesperrt(Boolean gesperrt) {
        this.gesperrt = gesperrt;
    }

    /**
     * @return Returns the kuendigungsdatum.
     */
    public Date getKuendigungsdatum() {
        return kuendigungsdatum;
    }

    /**
     * @param kuendigungsdatum The kuendigungsdatum to set.
     */
    public void setKuendigungsdatum(Date kuendigungsdatum) {
        this.kuendigungsdatum = kuendigungsdatum;
    }

    /**
     * @return Returns the liNr.
     */
    public Integer getLiNr() {
        return liNr;
    }

    /**
     * @param liNr The liNr to set.
     */
    public void setLiNr(Integer liNr) {
        this.liNr = liNr;
    }

    /**
     * @return Returns the loeschdatum.
     */
    public Date getLoeschdatum() {
        return loeschdatum;
    }

    /**
     * @param loeschdatum The loeschdatum to set.
     */
    public void setLoeschdatum(Date loeschdatum) {
        this.loeschdatum = loeschdatum;
    }

    /**
     * @return Returns the mailNeu.
     */
    public Integer getMailNeu() {
        return mailNeu;
    }

    /**
     * @param mailNeu The mailNeu to set.
     */
    public void setMailNeu(Integer mailNeu) {
        this.mailNeu = mailNeu;
    }

    /**
     * @return Returns the nummer.
     */
    public String getNummer() {
        return nummer;
    }

    /**
     * @param nummer The nummer to set.
     */
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    /**
     * @return Returns the passwort.
     */
    public String getPasswort() {
        return passwort;
    }

    /**
     * @param passwort The passwort to set.
     */
    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    /**
     * @return Returns the rufnummer.
     */
    public String getRufnummer() {
        return rufnummer;
    }

    /**
     * @param rufnummer The rufnummer to set.
     */
    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    /**
     * @return Returns the status.
     */
    public Short getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(Short status) {
        this.status = status;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return Returns the hostingNeu.
     */
    public Integer getHostingNeu() {
        return hostingNeu;
    }

    /**
     * @param hostingNeu The hostingNeu to set.
     */
    public void setHostingNeu(Integer hostingNeu) {
        this.hostingNeu = hostingNeu;
    }

    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public Boolean getEvnStatus() {
        return evnStatus;
    }

    public void setEvnStatus(Boolean evnStatus) {
        this.evnStatus = evnStatus;
    }

    public Boolean getEvnStatusPending() {
        return evnStatusPending;
    }

    public void setEvnStatusPending(Boolean evnStatusPending) {
        this.evnStatusPending = evnStatusPending;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + IntAccount.class.getName());
            logger.debug("  ID      : " + getId());
            logger.debug("  Account : " + getAccount());
            logger.debug("  Passwort: " + getPasswort());
            logger.debug("  URL     : " + getUrl());
        }
    }

    @Override
    public String toString() {
        return "IntAccount{" +
                "account='" + account + '\'' +
                ", passwort='" + passwort + '\'' +
                ", url='" + url + '\'' +
                ", gesperrt=" + gesperrt +
                ", rufnummer='" + rufnummer + '\'' +
                ", loeschdatum=" + loeschdatum +
                ", kuendigungsdatum=" + kuendigungsdatum +
                ", aenderungsdatum=" + aenderungsdatum +
                ", erstelldatum=" + erstelldatum +
                ", nummer='" + nummer + '\'' +
                ", status=" + status +
                ", freigabe=" + freigabe +
                ", bemerkung='" + bemerkung + '\'' +
                ", liNr=" + liNr +
                ", abrechnungsmonat=" + abrechnungsmonat +
                ", hostingNeu=" + hostingNeu +
                ", mailNeu=" + mailNeu +
                ", evnStatus=" + evnStatus +
                ", evnStatusPending=" + evnStatusPending +
                ", bearbeiter='" + bearbeiter + '\'' +
                '}';
    }
}


