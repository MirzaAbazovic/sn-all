/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2006 14:06:19
 */
package de.augustakom.hurrican.model.billing.query;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query fuer die Suche nach Servicerufnummern.
 *
 *
 */
public class BAuftragBNFCQuery extends AbstractHurricanQuery {

    private String prefix = null;
    private String businessNr = null;

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

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getPrefix())) {
            return false;
        }
        if (StringUtils.isNotBlank(getBusinessNr())) {
            return false;
        }
        return true;
    }

}


