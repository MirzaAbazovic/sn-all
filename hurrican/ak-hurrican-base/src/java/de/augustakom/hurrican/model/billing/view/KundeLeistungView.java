/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 14:36:42
 */
package de.augustakom.hurrican.model.billing.view;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View-Modell, um eine Leistungs-Uebersicht zu einem Kunden darzustellen.
 *
 *
 */
public class KundeLeistungView extends AbstractBillingModel implements KundenModel, DebugModel {

    private Long kundeNo = null;
    private Long menge = null;
    private String leistungName = null;
    private String leistungRechnungstext = null;
    private Long oeNoOrig = null;

    /**
     * @return Returns the leistungName.
     */
    public String getLeistungName() {
        return leistungName;
    }

    /**
     * @param bezeichnungD The leistungName to set.
     */
    public void setLeistungName(String leistungName) {
        this.leistungName = leistungName;
    }

    /**
     * @return Returns the leistungRechnungstext.
     */
    public String getLeistungRechnungstext() {
        return this.leistungRechnungstext;
    }

    /**
     * @param leistungRechnungstext The leistungRechnungstext to set.
     */
    public void setLeistungRechnungstext(String leistungRechnungstext) {
        this.leistungRechnungstext = leistungRechnungstext;
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
     * @return Returns the menge.
     */
    public Long getMenge() {
        return menge;
    }

    /**
     * @param menge The menge to set.
     */
    public void setMenge(Long menge) {
        this.menge = menge;
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
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + getClass().getName());
            logger.debug(" KUNDE__NO: " + getKundeNo());
            logger.debug(" OE__NO   : " + getOeNoOrig());
            logger.debug(" Menge    : " + getMenge());
            logger.debug(" L.-Name  : " + getLeistungName());
            logger.debug(" R-Text   : " + getLeistungRechnungstext());
        }
    }

}


