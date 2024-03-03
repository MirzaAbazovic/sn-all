/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2011 08:59:28
 */
package de.augustakom.hurrican.service.internet;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;

/**
 * Für (Lese-)Zugriff auf IP-Adressdaten in monline
 */
public interface INetNumService extends IInternetService {

    /**
     * @return Net-Ids mit zugehörigem Pool-Name aus monline
     */
    List<Pair<Long, String>> findAllNetIdsWithPoolName();
}


