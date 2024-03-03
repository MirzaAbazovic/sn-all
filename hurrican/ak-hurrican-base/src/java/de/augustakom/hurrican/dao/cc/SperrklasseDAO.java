/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.10.2011 11:28:05
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;

/**
 * Datenzugriff fuer {@link Sperrklasse}.
 *
 *
 * @since Release 10
 */
public interface SperrklasseDAO extends ByExampleDAO {

    /**
     * liefert die {@link Sperrklasse} fuer alle Switchtypen ausser IMS.
     *
     * @param sperrklasseNumber
     * @return
     */
    Sperrklasse findDefaultSperrklasse(Integer sperrklasseNumber);

    /**
     * liefert die {@link Sperrklasse} fuer den Switchtyp IMS.
     *
     * @param sperrklasseNumber
     * @return
     */
    Sperrklasse findImsSperrklasse(Integer sperrklasseNumber);

}

