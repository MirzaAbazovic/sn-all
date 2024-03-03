/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 13:09:51
 */

package de.mnet.migration.common.dao;

import java.util.*;


/**
 *
 */
public interface DataLoader<TYPE> {

    /**
     * Lade alle Daten
     */
    List<TYPE> getSourceData();

    /**
     * Name der Entitaet, Tabelle oder View, die geladen werden soll
     */
    String getSourceObjectName();
}
