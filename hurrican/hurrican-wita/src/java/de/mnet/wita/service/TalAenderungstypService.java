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
public interface TalAenderungstypService extends TalDetermineGeschaeftsfallService {

    /**
     * Ermittelt den Aenderungsgeschaeftsfall (LAE, LMAE, SER-POW) an Hand der Daten des angegebenen CBVorgangs (bzw.
     * der dazu verlinkten Carrierbestellung). <br> <ul> <li>Port-Typ (nieder-/hochbit) unterschiedlich: LAE
     * <li>Port-Typ gleich, Uebertragungsverfahren unterschiedlich: LMAE (nur bei hochbit) <li>Port-Typ und
     * Uebertragungsverfahren gleich: SER-POW </ul>
     *
     * @param carrierbestellung die betroffene Carrierbestellung
     * @param auftragIdNew      die Auftrags-ID, auf der die Carrierbestellung ausgefuehrt wird
     * @return Der Geschaeftsfalltyp oder null, falls kein passender Geschaeftsfall ermittelt werden konnte
     */
    @Override
    GeschaeftsfallTyp determineGeschaeftsfall(Carrierbestellung carrierbestellung, WitaCBVorgang witaCBVorgang)
            throws WitaBaseException;

}
