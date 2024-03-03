/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2010 14:52:45
 */

package de.mnet.common.scheduler;

/**
 * Spring/RMI Schnittstelle zur Anmeldung/Abmeldung der Hurrican Clients am Server.
 */
public interface IRMISchedulerConnect {

    void onConnect(String hostName);

    void onDisconnect(String hostName);
}
