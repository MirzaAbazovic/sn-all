/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 13:57:38
 */
package de.mnet.wita.service;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Service zur Identifizierung des TAL-Aenderungstyps.
 */
public interface TalDetermineGeschaeftsfallService extends WitaService {

    /**
     * Ermittelt den Geschaeftsfall an Hand der Daten des angegebenen CBVorgangs (bzw. der dazu verlinkten
     * Carrierbestellung). <br>
     *
     * @param carrierbestellung die betroffene Carrierbestellung
     * @param cbVorgang         der aktuelle CBVorgang
     * @return Der Geschaeftsfalltyp oder null, falls kein passender Geschaeftsfall ermittelt werden konnte
     */
    GeschaeftsfallTyp determineGeschaeftsfall(Carrierbestellung carrierbestellung, WitaCBVorgang cbVorgang)
            throws WitaBaseException;

}
