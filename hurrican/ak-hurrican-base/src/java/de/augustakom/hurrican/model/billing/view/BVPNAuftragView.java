/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 09:32:30
 */
package de.augustakom.hurrican.model.billing.view;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View-Klasse, um die wichtigsten Daten eines VPN-Auftrags aus dem Billing-System darzustellen.
 *
 *
 */
public class BVPNAuftragView extends AbstractBillingModel implements KundenModel, DebugModel {

    private Long auftragNoOrig = null;
    private Long kundeNo = null;
    private String kundeName = null;
    private String kundeVorname = null;

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
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the kundeName.
     */
    public String getKundeName() {
        return kundeName;
    }

    /**
     * @param kundeName The kundeName to set.
     */
    public void setKundeName(String kundeName) {
        this.kundeName = kundeName;
    }

    /**
     * @return Returns the kundeVorname.
     */
    public String getKundeVorname() {
        return kundeVorname;
    }

    /**
     * @param kundeVorname The kundeVorname to set.
     */
    public void setKundeVorname(String kundeVorname) {
        this.kundeVorname = kundeVorname;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + BVPNAuftragView.class.getName());
            logger.debug("  Auftrag__No: " + getAuftragNoOrig());
            logger.debug("  Kunde__No  : " + getKundeNo());
            logger.debug("  Name       : " + getKundeName());
            logger.debug("  Vorname    : " + getKundeVorname());
        }
    }
}


