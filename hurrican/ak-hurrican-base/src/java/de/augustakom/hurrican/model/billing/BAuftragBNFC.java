/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2006 11:54:19
 */
package de.augustakom.hurrican.model.billing;


/**
 * Billing-Modell fuer die Abbildung des Auftragszusatz fuer Business-Nummern (BNFC = Business number fee call).
 *
 *
 */
public class BAuftragBNFC extends AbstractBillingModel {

    private Long auftragNo = null;
    private String prefix = null;
    private String businessNr = null;
    private String destination = null;

    /**
     * @return Returns the auftragNo.
     */
    public Long getAuftragNo() {
        return this.auftragNo;
    }

    /**
     * @param auftragNo The auftragNo to set.
     */
    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    /**
     * @return Returns the businessNr.
     */
    public String getBusinessNr() {
        return this.businessNr;
    }

    /**
     * @param businessNr The businessNr to set.
     */
    public void setBusinessNr(String businessNr) {
        this.businessNr = businessNr;
    }

    /**
     * @return Returns the destination.
     */
    public String getDestination() {
        return this.destination;
    }

    /**
     * @param destination The destination to set.
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return Returns the prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * @param prefix The prefix to set.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}


