/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2005 12:12:04
 */
package de.augustakom.hurrican.model.shared.view;

import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 *
 */
public class Anschrift4ExportView implements KundenModel {

    private String anrede = null;
    private String vorname = null;
    private String familienname = null;
    private Long kundeNo = null;
    private String nummer = null;
    private String ort = null;
    private String plz = null;
    private String strasse = null;
    private String kundentyp = null;
    private String debitor = null;

    /**
     * @return Returns the anrede.
     */
    public String getAnrede() {
        return anrede;
    }

    /**
     * @param anrede The anrede to set.
     */
    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    /**
     * @return Returns the familienname.
     */
    public String getFamilienname() {
        return familienname;
    }

    /**
     * @param familienname The familienname to set.
     */
    public void setFamilienname(String familienname) {
        this.familienname = familienname;
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
     * @return Returns the kundentyp.
     */
    public String getKundentyp() {
        return kundentyp;
    }

    /**
     * @param kundentyp The kundentyp to set.
     */
    public void setKundentyp(String kundentyp) {
        this.kundentyp = kundentyp;
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

    /**
     * @return Returns the debitor.
     */
    public String getDebitor() {
        return debitor;
    }


    /**
     * @param debitor The debitor to set.
     */
    public void setDebitor(String debitor) {
        this.debitor = debitor;
    }


}
