/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.10.2011 10:35:16
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 *
 */
public interface SperrklasseService extends ICCService {

    /**
     * Findet alle Sperrklassen, welche fuer den Switch Typ eine SperrklassenID definieren (aktuell nur Unterscheidung
     * IMS-other).
     *
     * @param hwSwitchType
     * @return
     */
    List<Sperrklasse> findSperrklasseByHwSwitchType(final HWSwitchType hwSwitchType);

    /**
     * findet die Sperrklassen zu dem angegeben Beispiel abhaengig vom angegebenen {@link HWSwitchType}. Es wird die erste Sperrklasse
     * gefunden, die in der "example" Ergebnisliste enthalten ist, und fuer "hwSwitchType" eine nicht-null Sperrklassen-ID enthaelt.
     *
     * @param example
     * @param  hwSwitchType
     * @return
     * @throws FindException
     */
    Sperrklasse findSperrklasseByExample(Sperrklasse example, HWSwitchType hwSwitchType) throws FindException;

    /**
     * erstellt eine Collection der möglichen Varianten der zu sperrenden Checkboxen abhaengig vom gegebenen Typ des
     * Switches.
     *
     * @param example
     * @param hwSwitchType
     * @return Collection mit den IDs der Sperrklassen
     */
    List<Long> findPossibleSperrtypen(Sperrklasse example, HWSwitchType hwSwitchType);

    /**
     * findet eine Sperrklasse nach deren Sperrklassennummer fuer einen speziellen {@link HWSwitchType}.
     *
     * @param sperrklasseNo
     * @param  hwSwitchType
     * @return Object vom Typ Sperrklasse
     * @throws FindException
     */
    Sperrklasse findSperrklasseBySperrklasseNo(Integer sperrklasseNo, HWSwitchType hwSwitchType)
            throws FindException;

}
