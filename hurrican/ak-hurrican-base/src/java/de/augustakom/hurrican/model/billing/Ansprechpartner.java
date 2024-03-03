/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 09:30:58
 */
package de.augustakom.hurrican.model.billing;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Modell-Klasse zur Abbildung eines Ansprechpartners. <br>
 *
 *
 */
public class Ansprechpartner extends AbstractBillingModel implements KundenModel {

    private Long ansprechpartnerNo = null;
    private Long kundeNo = null;
    private String typ = null;
    private String titel = null;
    private String name = null;
    private String vorname = null;
    private String strasse = null;
    private String nummer = null;
    private String postfach = null;
    private String plz = null;
    private String ort = null;
    private String rufnummer = null;
    private String fax = null;
    private String rnMobile = null;
    private String email = null;
    private String info = null;

    /**
     * Erstellt eine kurze Info ueber den Ansprechpartner bestehend aus Name und Phone/Mobil.
     */
    public String getShortInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        if (StringUtils.isNotBlank(getRufnummer())) {
            sb.append(" - ");
            sb.append(getRufnummer());
        }
        else if (StringUtils.isNotBlank(getRnMobile())) {
            sb.append(" - ");
            sb.append(getRnMobile());
        }
        return sb.toString();
    }

    /**
     * @return the kundeNo
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo the kundeNo to set
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the ansprechpartnerNo.
     */
    public Long getAnsprechpartnerNo() {
        return ansprechpartnerNo;
    }

    /**
     * @param ansprechpartnerNo The ansprechpartnerNo to set.
     */
    public void setAnsprechpartnerNo(Long ansprechpartnerNo) {
        this.ansprechpartnerNo = ansprechpartnerNo;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the fax.
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax The fax to set.
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return Returns the info.
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info The info to set.
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
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
     * @return Returns the ort.
     */
    public String getOrt() {
        return ort;
    }

    /**
     * @param ort The ort to set.
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    /**
     * @return Returns the plz.
     */
    public String getPlz() {
        return plz;
    }

    /**
     * @param plz The plz to set.
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

    /**
     * @return Returns the postfach.
     */
    public String getPostfach() {
        return postfach;
    }

    /**
     * @param postfach The postfach to set.
     */
    public void setPostfach(String postfach) {
        this.postfach = postfach;
    }

    /**
     * @return Returns the rnMobile.
     */
    public String getRnMobile() {
        return rnMobile;
    }

    /**
     * @param rnMobile The rnMobile to set.
     */
    public void setRnMobile(String rnMobile) {
        this.rnMobile = rnMobile;
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
     * @return Returns the strasse.
     */
    public String getStrasse() {
        return strasse;
    }

    /**
     * @param strasse The strasse to set.
     */
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    /**
     * Gibt die Kombination von Strasse und Nummer zurueck.
     *
     * @return
     *
     */
    public String getStrasseAndNummer() {
        if (StringUtils.isNotBlank(getStrasse())) {
            StringBuilder sb = new StringBuilder();
            sb.append(getStrasse());
            if (StringUtils.isNotBlank(getNummer())) {
                sb.append(" ");
                sb.append(getNummer());
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * @return Returns the titel.
     */
    public String getTitel() {
        return titel;
    }

    /**
     * @param titel The titel to set.
     */
    public void setTitel(String titel) {
        this.titel = titel;
    }

    /**
     * @return Returns the typ.
     */
    public String getTyp() {
        return typ;
    }

    /**
     * @param typ The typ to set.
     */
    public void setTyp(String typ) {
        this.typ = typ;
    }

    /**
     * @return Returns the vorname.
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * @param vorname The vorname to set.
     */
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

}


