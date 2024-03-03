/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2011 09:12:11
 */
package de.mnet.wita.message.meldung;

import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.MeldungsType;

/**
 * Marker interface for all meldung types that should be passed to the tal order workflow.
 *
 *
 */
public interface TalOrderMeldung extends WitaMessage {

    MeldungsType getMeldungsTyp();

    String getExterneAuftragsnummer();
}
