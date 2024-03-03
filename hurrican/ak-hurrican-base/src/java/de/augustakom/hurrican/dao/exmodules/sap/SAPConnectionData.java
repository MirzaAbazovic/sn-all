/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2007 08:58:47
 */
package de.augustakom.hurrican.dao.exmodules.sap;


/**
 * Interface fuer SAP-Connection-Daten.
 *
 *
 */
public interface SAPConnectionData {


    /**
     * Setzt Connection-Daten
     *
     * @param user SAP-Username
     * @param pw   SAP-Passwort
     * @param bk   Buchungskreis
     *
     */
    public void setConnectionData(String user, String pw, String bk);


}



