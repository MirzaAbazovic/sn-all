/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2011 09:12:11
 */
package de.mnet.wita.message.meldung;

import de.mnet.wita.OutgoingWitaMessage;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;

/**
 * Marker interface for all outgoing Meldung objects.
 */
public interface OutgoingMeldung extends OutgoingWitaMessage {

    GeschaeftsfallTyp getGeschaeftsfallTyp();

    AenderungsKennzeichen getAenderungsKennzeichen();

}
