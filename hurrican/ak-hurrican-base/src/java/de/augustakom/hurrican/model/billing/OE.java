/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 08:16:21
 */
package de.augustakom.hurrican.model.billing;

import java.io.*;
import java.util.*;


/**
 * Modell-Klasse zur Abbildung einer Organisationseinheit (wird z.B. verwendet fuer Abteilungen, Produktgruppen od.
 * Produkte).
 *
 *
 */
public class OE extends AbstractHistoryModel implements Serializable {

    public static final Long OE__NO_AKEMAIL = Long.valueOf(724);
    public static final Long OE__NO_AKEMAIL_K = Long.valueOf(750);
    public static final Long OE__NO_AKHOSTING = Long.valueOf(725);
    public static final Long OE__NO_AKHOSTING_K = Long.valueOf(751);
    public static final Long OE__NO_AKFLAT = Long.valueOf(726);
    public static final Long OE__NO_AKFLAT_K = Long.valueOf(752);
    public static final String OE_OETYP_RUFNUMMER = "RUFNUMMER";
    public static final String OE_OETYP_PRODUKT = "Produkt";
    public static final String OE_AUFTRAGZUSATZ_KOMBI = "AUFTRAG__KOMBI";
    public static final String OE_AUFTRAGZUSATZ_SCV = "AUFTRAG__SCV";

    private Long oeNo = null;
    private Long oeNoOrig = null;
    private Long vaterOeNoOrig = null;
    private String produktCode = null;
    private String name = null;
    private String rechnungstext = null;
    private String auftragZusatz = null;

    private Collection oeLanguages = null;

    /**
     * @return Returns the oeLanguages.
     */
    public Collection getOeLanguages() {
        return this.oeLanguages;
    }

    /**
     * @param oeLanguages The oeLanguages to set.
     */
    public void setOeLanguages(Collection oeLanguages) {
        this.oeLanguages = oeLanguages;
    }

    /**
     * @return Returns the bezeichnungD.
     */
    public String getRechnungstext() {
        return rechnungstext;
    }

    /**
     * @param bezeichnungD The bezeichnungD to set.
     */
    public void setRechnungstext(String bezeichnungD) {
        this.rechnungstext = bezeichnungD;
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
     * @return Returns the oeNo.
     */
    public Long getOeNo() {
        return oeNo;
    }

    /**
     * @param oeNo The oeNo to set.
     */
    public void setOeNo(Long oeNo) {
        this.oeNo = oeNo;
    }

    /**
     * @return Returns the oeNoOrig.
     */
    public Long getOeNoOrig() {
        return oeNoOrig;
    }

    /**
     * @param oeNoOrig The oeNoOrig to set.
     */
    public void setOeNoOrig(Long oeNoOrig) {
        this.oeNoOrig = oeNoOrig;
    }

    /**
     * @return Returns the vaterOeNoOrig.
     */
    public Long getVaterOeNoOrig() {
        return vaterOeNoOrig;
    }

    /**
     * @param vaterOeNoOrig The vaterOeNoOrig to set.
     */
    public void setVaterOeNoOrig(Long vaterOeNoOrig) {
        this.vaterOeNoOrig = vaterOeNoOrig;
    }

    /**
     * @return Returns the produktCode.
     */
    public String getProduktCode() {
        return produktCode;
    }

    /**
     * @param produktCode The produktCode to set.
     */
    public void setProduktCode(String produktCode) {
        this.produktCode = produktCode;
    }

    /**
     * @return auftragZusatz
     */
    public String getAuftragZusatz() {
        return auftragZusatz;
    }

    /**
     * @param auftragZusatz Festzulegender auftragZusatz
     */
    public void setAuftragZusatz(String auftragZusatz) {
        this.auftragZusatz = auftragZusatz;
    }

}


