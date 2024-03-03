/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2009 09:27:12
 */
package de.augustakom.common.tools.ws;


/**
 * Interface, um einem WebService-Template die Konfigurationsdaten zu uebergeben.
 */
public interface IWebServiceConfiguration {

    /**
     * Gibt die URL des WebServices zurueck.
     */
    String getWsURL();

    /**
     * Gibt die zu verwendende WebService Securement-Action (z.B. UsernameToken) an.
     */
    String getWsSecurementAction();

    /**
     * Gibt den Username fuer die Securement-Action <UsernameToken> an.
     */
    String getWsSecurementUser();

    /**
     * Gibt das Passwort fuer die Securement-Action <UsernameToken> an.
     */
    String getWsSecurementPassword();

}


