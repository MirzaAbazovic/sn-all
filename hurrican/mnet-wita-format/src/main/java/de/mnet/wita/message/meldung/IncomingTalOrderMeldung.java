/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2011 09:12:11
 */
package de.mnet.wita.message.meldung;

import java.time.*;
import java.util.*;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.IncomingMessage;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;


/**
 * Marker interface for all incoming meldung types that should be passed to the tal order workflow.
 *
 *
 */
public interface IncomingTalOrderMeldung extends TalOrderMeldung, IncomingMessage {

    GeschaeftsfallTyp getGeschaeftsfallTyp();

    AenderungsKennzeichen getAenderungsKennzeichen();

    Date getVersandZeitstempel();

    void setExterneAuftragsnummer(String externeAuftragsnummer);

}
