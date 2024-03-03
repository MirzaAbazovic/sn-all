/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 18:16:10
 */

package de.augustakom.hurrican.dao.internet;

import java.util.*;

import de.augustakom.hurrican.model.internet.IntEndgeraet;


/**
 * Dao zum Lesen von Endgeraeten aus der Monline.
 */
public interface EndgeraeteDao {

    List<IntEndgeraet> findEndgeraeteForVerbindungsbezeichnung(String verbindungsbezeichnung);

}
