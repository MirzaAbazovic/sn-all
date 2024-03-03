/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 13:29:43
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.log4j.Logger;


/**
 * View-Modell, um Auftrags- und Leitungs-Daten darzustellen. (Die Informationen werden ueber Billing- und CC-Services
 * zusammengetragen.)
 *
 *
 */
public class AuftragDatenView extends DefaultSharedAuftragView {

    private String bestellNr = null;
    private String intAccount = null;
    private String lbzKunde = null;

    /**
     * @return Returns the bestellNr.
     */
    public String getBestellNr() {
        return bestellNr;
    }

    /**
     * @param bestellNr The bestellNr to set.
     */
    public void setBestellNr(String bestellNr) {
        this.bestellNr = bestellNr;
    }

    /**
     * @return Returns the intAccount.
     */
    public String getIntAccount() {
        return intAccount;
    }

    /**
     * @param intAccount The intAccount to set.
     */
    public void setIntAccount(String intAccount) {
        this.intAccount = intAccount;
    }

    /**
     * @return Returns the lbzKunde.
     */
    public String getLbzKunde() {
        return lbzKunde;
    }

    /**
     * @param lbzKunde The lbzKunde to set.
     */
    public void setLbzKunde(String lbzKunde) {
        this.lbzKunde = lbzKunde;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            super.debugModel(logger);
            logger.debug("  Bestell-Nr     : " + getBestellNr());
        }
    }

}


