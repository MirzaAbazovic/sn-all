/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 07:56:09
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpeAclConverter;


/**
 * Modell-Klasse fuer die Abbildung von ACLs fuer ein Business CPE. Das Streaming erfolgt ueber die Konverter-Klasse
 * {@link CPSBusinessCpeAclConverter}.
 */
public class CPSBusinessCpeAclData extends AbstractCPSServiceOrderDataModel {

    private String acl;

    /**
     * Konstruktor mit Angabe des {@link EndgeraetAcl} Objekts, aus dem das CPS Modell aufgebaut werden soll
     *
     * @param egAcl
     */
    public CPSBusinessCpeAclData(EndgeraetAcl egAcl) {
        setAcl(egAcl.getName());
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = StringUtils.deleteWhitespace(acl);
    }

}


