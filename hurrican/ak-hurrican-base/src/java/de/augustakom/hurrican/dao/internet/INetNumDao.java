/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2011 08:22:42
 */
package de.augustakom.hurrican.dao.internet;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;

/**
 * DAO-Interface zur Ermittlung von Daten aus der monline-View 'V_INETNUM_HURRICAN'
 */
public interface INetNumDao {

    /**
     * @return Alle Net-Ids aus der MOnline mit zugehörigem Pool-Name
     */
    List<Pair<Long, String>> findAllNetIdsWithPoolName();
}


