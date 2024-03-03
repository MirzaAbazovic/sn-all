/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 13:22:11
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;


/**
 * View-Klasse, um die Anschrift eines Kunden abzubilden.
 *
 *
 */
public class AnschriftView extends AbstractBillingModel implements DebugModel {

    private Long kundeNo = null;
    private String anschrift1 = null;
    private String anschrift2 = null;
    private String anschrift3 = null;
    private String anschrift4 = null;
    private String strasse = null;
    private String plz = null;
    private String ort = null;
    private Long adresseNo = null;


    /**
     * Erzeugt ein schoeneres Format fuer die Anschrift. <br> Die Felder Anschrift1-4 werden dabei so weit nach unten
     * geschoben wie moeglich ist.
     */
    public void prettyFormat() {
        List<String> values = new ArrayList<String>();
        CollectionTools.addIfNotBlank(values, getAnschrift4());
        CollectionTools.addIfNotBlank(values, getAnschrift3());
        CollectionTools.addIfNotBlank(values, getAnschrift2());
        CollectionTools.addIfNotBlank(values, getAnschrift1());

        setAnschrift4(CollectionTools.getFromCollection(values, 0));
        setAnschrift3(CollectionTools.getFromCollection(values, 1));
        setAnschrift2(CollectionTools.getFromCollection(values, 2));
        setAnschrift1(CollectionTools.getFromCollection(values, 3));
    }

    /**
     * @return Returns the anschrift1.
     */
    public String getAnschrift1() {
        return anschrift1;
    }

    /**
     * @param anschrift1 The anschrift1 to set.
     */
    public void setAnschrift1(String anschrift1) {
        this.anschrift1 = anschrift1;
    }

    /**
     * @return Returns the anschrift2.
     */
    public String getAnschrift2() {
        return anschrift2;
    }

    /**
     * @param anschrift2 The anschrift2 to set.
     */
    public void setAnschrift2(String anschrift2) {
        this.anschrift2 = anschrift2;
    }

    /**
     * @return Returns the anschrift3.
     */
    public String getAnschrift3() {
        return anschrift3;
    }

    /**
     * @param anschrift3 The anschrift3 to set.
     */
    public void setAnschrift3(String anschrift3) {
        this.anschrift3 = anschrift3;
    }

    /**
     * @return Returns the anschrift4.
     */
    public String getAnschrift4() {
        return anschrift4;
    }

    /**
     * @param anschrift4 The anschrift4 to set.
     */
    public void setAnschrift4(String anschrift4) {
        this.anschrift4 = anschrift4;
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
     * @return Returns the adresseNo.
     */
    public Long getAdresseNo() {
        return adresseNo;
    }

    /**
     * @param adresseNo The adresseNo to set.
     */
    public void setAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("  Anschrift1: " + getAnschrift1());
            logger.debug("  Anschrift2: " + getAnschrift2());
            logger.debug("  Anschrift3: " + getAnschrift3());
            logger.debug("  Anschrift4: " + getAnschrift4());
            logger.debug("  Strasse   : " + getStrasse());
            logger.debug("  PLZ/Ort   : " + getPlz() + " " + getOrt());
        }
    }

}

