/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 11:15:11
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Abbildung einer Hurrican-Adresse.
 */
public class CCAddress extends AbstractCCIDModel implements KundenModel, AddressModel {

    private static final long serialVersionUID = -231287913790255916L;

    public static final int MAX_LENGTH_HAUSNUMMER_ZUSATZ = 6;

    /**
     * Wert fuer 'addressType' kennzeichnet eine Standortadresse.
     */
    public static final Long ADDRESS_TYPE_ACCESSPOINT = 200L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine Lieferadresse.
     */
    public static final Long ADDRESS_TYPE_SHIPPING = 201L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine Adresse fuer versch. Zwecke.
     */
    public static final Long ADDRESS_TYPE_VARIOUS = 202L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine Anschlussinhaberadresse.
     */
    public static final Long ADDRESS_TYPE_ACCESSPOINT_OWNER = 203L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine Adresse fuer einen Ansprechpartner.
     */
    public static final Long ADDRESS_TYPE_CUSTOMER_CONTACT = 204L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine Adresse fuer Hotline Service.
     */
    public static final Long ADDRESS_TYPE_HOTLINE_SERVICE = 205L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine Adresse fuer Hotline geplante Arbeiten.
     */
    public static final Long ADDRESS_TYPE_HOTLINE_GA = 206L;
    /**
     * Wert fuer 'addressType' kennzeichnet eine E-Mail-Adresse.
     */
    public static final Long ADDRESS_TYPE_EMAIL = 207L;
    /**
     * Wert fuer 'addressType' kennzeichnet Addressdaten für Wholesale die an FFM übermittelt wird.
     */
    public static final Long ADDRESS_TYPE_WHOLESALE_FFM_DATA = 208L;


    /**
     * Wert fuer 'formatName' kennzeichnet das Format 'PRIVATADRESSE'
     */
    public static final String ADDRESS_FORMAT_RESIDENTIAL = "RESIDENTIAL";
    /**
     * Wert fuer 'formatName' kennzeichnet das Format 'GESCHAEFTSADRESSE'
     */
    public static final String ADDRESS_FORMAT_BUSINESS = "BUSINESS";

    private Long kundeNo = null;
    private Long addressType = null;  // Referenz auf T_REFERENCE
    public static final String ADDRESS_TYPE = "addressType";
    private String formatName = null;
    public static final String FORMAT_NAME = "formatName";
    private String titel = null;
    public static final String TITEL = "titel";
    private String titel2 = null;
    public static final String TITEL2 = "titel2";
    private String name = null;
    public static final String NAME = "name";
    private String name2 = null;
    public static final String NAME2 = "name2";
    private String vorname = null;
    public static final String VORNAME = "vorname";
    private String vorname2 = null;
    public static final String VORNAME2 = "vorname2";
    private String strasse = null;
    public static final String STRASSE = "strasse";
    private String strasseAdd = null;
    public static final String STRASSE_ADD = "strasseAdd";
    private String nummer = null;
    public static final String NUMMER = "nummer";
    private String hausnummerZusatz = null;
    public static final String HAUSNUMMER_ZUSATZ = "hausnummerZusatz";
    private String postfach = null;
    public static final String POSTFACH = "postfach";
    private String plz = null;
    public static final String PLZ = "plz";
    private String ort = null;
    public static final String ORT = "ort";
    private String ortsteil = null;
    private String landId = null;
    public static final String LAND_ID = "landId";
    private String telefon = null;
    public static final String TELEFON = "telefon";
    private String fax = null;
    public static final String FAX = "fax";
    private String handy = null;
    public static final String HANDY = "handy";
    private String email = null;
    public static final String EMAIL = "email";
    private String bemerkung = null;
    public static final String BEMERKUNG = "bemerkung";
    private Integer prioBrief = null;
    public static final String PRIO_BRIEF = "prioBrief";
    private Integer prioEmail = null;
    public static final String PRIO_EMAIL = "prioEmail";
    private Integer prioFax = null;
    public static final String PRIO_FAX = "prioFax";
    private Integer prioSMS = null;
    public static final String PRIO_SMS = "prioSMS";
    private Integer prioTel = null;
    public static final String PRIO_TEL = "prioTel";
    private String gebaeudeteilName = null;
    private String gebaeudeteilZusatz = null;

    /**
     * @return Eine Adresse, deren String-Felder auf leere Strings gesetzt sind
     */
    public static CCAddress getEmptyAddress() {
        CCAddress result = new CCAddress();
        result.name = "";
        result.vorname = "";
        result.titel = "";
        result.name2 = "";
        result.vorname2 = "";
        result.titel2 = "";
        result.bemerkung = "";
        result.strasse = "";
        result.strasseAdd = "";
        result.nummer = "";
        result.plz = "";
        result.ort = "";
        result.ortsteil = "";
        result.postfach = "";
        result.telefon = "";
        result.fax = "";
        result.email = "";
        result.landId = "";
        return result;
    }

    /**
     * Erstellt eine (fast) vollstaendige Kopie von {@code toCopy} und gibt diese zurueck.
     * Es werden dabei alle Parameter ausser der ID kopiert.
     * @param toCopy
     * @return
     */
    public static CCAddress getCopy(CCAddress toCopy) {
        CCAddress result = new CCAddress();
        result.setName(toCopy.getName());
        result.setVorname(toCopy.getVorname());
        result.setTitel(toCopy.getTitel());
        result.setName2(toCopy.getName2());
        result.setVorname2(toCopy.getVorname2());
        result.setTitel2(toCopy.getTitel2());
        result.setBemerkung(toCopy.getBemerkung());
        result.setStrasse(toCopy.getStrasse());
        result.setStrasseAdd(toCopy.getStrasseAdd());
        result.setNummer(toCopy.getNummer());
        result.setPlz(toCopy.getPlz());
        result.setOrt(toCopy.getOrt());
        result.setOrtsteil(toCopy.getOrtsteil());
        result.setPostfach(toCopy.getPostfach());
        result.setTelefon(toCopy.getTelefon());
        result.setFax(toCopy.getFax());
        result.setEmail(toCopy.getEmail());
        result.setLandId(toCopy.getLandId());
        result.setGebaeudeteilName(toCopy.getGebaeudeteilName());
        result.setGebaeudeteilZusatz(toCopy.getGebaeudeteilZusatz());
        result.setPrioBrief(toCopy.getPrioBrief());
        result.setPrioEmail(toCopy.getPrioEmail());
        result.setPrioFax(toCopy.getPrioFax());
        result.setPrioSMS(toCopy.getPrioSMS());
        result.setPrioTel(toCopy.getPrioTel());
        return result;
    }


    /**
     * Gibt wichtige Daten der Adresse in Kurzform zurueck. <br> Folgende Daten werden (falls vorhanden)
     * zusammengefuegt: <br> - Name, Vorname  <br> - Strasse, Hausnummer  <br> - PLZ, Ort
     */
    public String getShortAddress() {
        StringBuilder shortAdr = new StringBuilder();
        shortAdr.append(StringTools.join(new String[] { getName(), getVorname() }, " ", true));
        shortAdr.append("; ");
        shortAdr.append(StringTools.join(new String[] { getStrasse(), getNummer(), getHausnummerZusatz() }, " ", true));
        shortAdr.append("; ");
        shortAdr.append(StringTools.join(new String[] { getPlz(), getCombinedOrtOrtsteil() }, " ", true));
        return shortAdr.toString();
    }

    /**
     * Gibt die Strassendaten des Adress-Objekts in einem String zurueck. <br> Es werden dabei folgende Daten
     * zusammengefuegt: <br> - Strasse <br> - Nummer, Hausnummernzusatz <br> - Strassenzusatz <br>
     */
    @Override
    public String getCombinedStreetData() {
        String esText = StringTools.join(
                new String[] { getStrasse(), getNummer(), getHausnummerZusatz() }, " ", true);
        return StringTools.join(new String[] { esText, getStrasseAdd() }, ", ", true);
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.AddressModel#getCombinedNameData()
     */
    @Override
    public String getCombinedNameData() {
        if (getFormatName().equals(CCAddress.ADDRESS_FORMAT_BUSINESS)) {
            return StringTools.join(new String[] { getName(), getName2(), getVorname(), getVorname2() }, " ", true);
        }
        String tmp = StringTools.join(new String[] { getName(), getVorname() }, " ", true);
        if (StringUtils.isNotBlank(getName2())) {
            return StringTools.join(new String[] { tmp, " und ", getName2(), getVorname2() }, " ", true);
        }
        return tmp;
    }

    public String getCombinedPlzOrtData() {
        return StringTools.join(new String[] { getPlzTrimmed(), getCombinedOrtOrtsteil() }, " ", true);
    }

    @Override
    public String getCombinedOrtOrtsteil() {
        return StringTools.join(new String[] { ort, ortsteil }, " - ", true);
    }

    public String getCombinedHausnummer() {
        return StringTools.join(new String[] { nummer, hausnummerZusatz }, " ", true);
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public Long getAddressType() {
        return addressType;
    }

    public void setAddressType(Long addressType) {
        this.addressType = addressType;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getHandy() {
        return handy;
    }

    public void setHandy(String handy) {
        this.handy = handy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getTitel2() {
        return titel2;
    }

    public void setTitel2(String titel2) {
        this.titel2 = titel2;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @Override
    public String getFormatName() {
        return formatName;
    }

    @Override
    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName2() {
        return name2;
    }

    @Override
    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String getVorname() {
        return vorname;
    }

    @Override
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @Override
    public String getVorname2() {
        return vorname2;
    }

    @Override
    public void setVorname2(String vorname2) {
        this.vorname2 = vorname2;
    }

    @Override
    public String getStrasse() {
        return strasse;
    }

    @Override
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    @Override
    public String getStrasseAdd() {
        return strasseAdd;
    }

    @Override
    public void setStrasseAdd(String strasseAdd) {
        this.strasseAdd = strasseAdd;
    }

    @Override
    public String getNummer() {
        return nummer;
    }

    @Override
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    @Override
    public String getHausnummerZusatz() {
        return hausnummerZusatz;
    }

    @Override
    public void setHausnummerZusatz(String hausnummerZusatz) {
        this.hausnummerZusatz = hausnummerZusatz;
    }

    @Override
    public String getPostfach() {
        return postfach;
    }

    @Override
    public void setPostfach(String postfach) {
        this.postfach = postfach;
    }

    @Override
    public String getPlz() {
        return plz;
    }

    @Override
    public String getPlzTrimmed() {
        String tmp = getPlz();
        return StringUtils.trimToEmpty(tmp);
    }

    @Override
    public void setPlz(String plz) {
        this.plz = plz;
    }

    @Override
    public String getOrt() {
        return ort;
    }

    @Override
    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Override
    public String getOrtsteil() {
        return ortsteil;
    }

    @Override
    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    @Override
    public String getLandId() {
        return landId;
    }

    @Override
    public void setLandId(String landId) {
        this.landId = landId;
    }

    public Integer getPrioBrief() {
        return prioBrief;
    }

    public void setPrioBrief(Integer prioBrief) {
        this.prioBrief = prioBrief;
    }

    public Integer getPrioEmail() {
        return prioEmail;
    }

    public void setPrioEmail(Integer prioEmail) {
        this.prioEmail = prioEmail;
    }

    public Integer getPrioFax() {
        return prioFax;
    }

    public void setPrioFax(Integer prioFax) {
        this.prioFax = prioFax;
    }

    public Integer getPrioSMS() {
        return prioSMS;
    }

    public void setPrioSMS(Integer prioSMS) {
        this.prioSMS = prioSMS;
    }

    public Integer getPrioTel() {
        return prioTel;
    }

    public void setPrioTel(Integer prioTel) {
        this.prioTel = prioTel;
    }

    /**
     * Wird nur fuer WITA KFT benoetigt - NICHT LOESCHEN
     */
    public String getGebaeudeteilName() {
        return gebaeudeteilName;
    }

    /**
     * Wird nur fuer WITA KFT benoetigt - NICHT LOESCHEN
     */
    public void setGebaeudeteilName(String gebaeudeteilName) {
        this.gebaeudeteilName = gebaeudeteilName;
    }

    /**
     * Wird nur fuer WITA KFT benoetigt - NICHT LOESCHEN
     */
    public String getGebaeudeteilZusatz() {
        return gebaeudeteilZusatz;
    }

    /**
     * Wird nur fuer WITA KFT benoetigt - NICHT LOESCHEN
     */
    public void setGebaeudeteilZusatz(String gebaeudeteilZusatz) {
        this.gebaeudeteilZusatz = gebaeudeteilZusatz;
    }

    /**
     * Funktion vergleicht zwei CCAdress-Objekte
     *
     * @return True, falls beide Objekte die gleiche Adresse enthalten. ID wird nicht berücksichtigt.
     */
    public boolean compareCCAddress(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        CCAddress a = (CCAddress) obj;
        if (!compareObject(getKundeNo(), a.getKundeNo())) {
            return false;
        }
        if (!compareObject(getAddressType(), a.getAddressType())) {
            return false;
        }
        if (!compareObject(getFormatName(), a.getFormatName())) {
            return false;
        }
        if (!compareObject(getName(), a.getName())) {
            return false;
        }
        if (!compareObject(getName2(), a.getName2())) {
            return false;
        }
        if (!compareObject(getVorname(), a.getVorname())) {
            return false;
        }
        if (!compareObject(getVorname2(), a.getVorname2())) {
            return false;
        }
        if (!compareObject(getStrasse(), a.getStrasse())) {
            return false;
        }
        if (!compareObject(getStrasseAdd(), a.getStrasseAdd())) {
            return false;
        }
        if (!compareObject(getNummer(), a.getNummer())) {
            return false;
        }
        if (!compareObject(getHausnummerZusatz(), a.getHausnummerZusatz())) {
            return false;
        }
        if (!compareObject(getPostfach(), a.getPostfach())) {
            return false;
        }
        if (!compareObject(getPlz(), a.getPlz())) {
            return false;
        }
        if (!compareObject(getOrt(), a.getOrt())) {
            return false;
        }
        if (!compareObject(getOrtsteil(), a.getOrtsteil())) {
            return false;
        }
        if (!compareObject(getLandId(), a.getLandId())) {
            return false;
        }
        if (!compareObject(getTelefon(), a.getTelefon())) {
            return false;
        }
        if (!compareObject(getFax(), a.getFax())) {
            return false;
        }
        if (!compareObject(getHandy(), a.getHandy())) {
            return false;
        }
        if (!compareObject(getEmail(), a.getEmail())) {
            return false;
        }
        if (!compareObject(getPrioBrief(), a.getPrioBrief())) {
            return false;
        }
        if (!compareObject(getPrioEmail(), a.getPrioEmail())) {
            return false;
        }
        if (!compareObject(getPrioFax(), a.getPrioFax())) {
            return false;
        }
        if (!compareObject(getPrioSMS(), a.getPrioSMS())) {
            return false;
        }
        if (!compareObject(getPrioTel(), a.getPrioTel())) {
            return false;
        }
        if (!compareObject(getGebaeudeteilName(), a.getGebaeudeteilName())) {
            return false;
        }
        if (!compareObject(getGebaeudeteilZusatz(), a.getGebaeudeteilZusatz())) {
            return false;
        }

        return true;
    }

    /**
     * Hilfsfunktion für compareCCAddress()
     */
    private boolean compareObject(Object a, Object b) {
        if (a != null) {
            if (!a.equals(b)) {
                return false;
            }
        }
        else if (b != null) {
            return false;
        }
        return true;
    }

}
