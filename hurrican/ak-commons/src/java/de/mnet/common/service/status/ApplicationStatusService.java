/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 16:52:20
 */
package de.mnet.common.service.status;

/**
 * Service zur Abfrage, des Status eines Teils der Anwendung
 */
public interface ApplicationStatusService {

    /**
     * Methode zur Status-Abfrage
     */
    ApplicationStatusResult getStatus();

    /**
     * Gibt an was ueberprueft wird
     */
    String getStatusName();

}
