/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2007 08:58:47
 */
package de.augustakom.hurrican.dao.exmodules.sap.impl;

import org.springframework.dao.support.DaoSupport;

import de.augustakom.hurrican.dao.exmodules.sap.SAPConnectionData;


/**
 * Klasse von der alle SAP-Dao-Klassen ableiten können
 *
 *
 */
public class SAPDAOSupport extends DaoSupport implements SAPConnectionData {

    protected String buchungskreis = null;
    protected String sapUser = null;
    protected String sapPasswort = null;

    /**
     * @see de.augustakom.hurrican.dao.exmodules.sap.SAPConnectionData#setConnectionData(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void setConnectionData(String user, String pw, String bk) {
        buchungskreis = bk;
        sapPasswort = pw;
        sapUser = user;
    }

    /**
     * @see org.springframework.dao.support.DaoSupport#checkDaoConfig()
     */
    protected void checkDaoConfig(){
    }


}



