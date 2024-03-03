/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:10:53
 */
package de.mnet.wita;

/**
 * Marker-Interface fuer alle (Haupt-)Objekte, die als WITA Message verschickt werden koennen.
 */
public interface OutgoingWitaMessage extends WitaMessage {

    String getExterneAuftragsnummer();
}
