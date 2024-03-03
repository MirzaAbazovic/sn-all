/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2010 08:56:37
 */
package de.mnet.migration.common.dao;

import de.mnet.migration.common.result.SourceIdList;


/**
 *
 */
public interface KeyAbleSource<T> {
    T getKey();

    /**
     * Fuer BAD_DATA wenn doppelte keys, fuer MapListLoader nicht benoetigt
     */
    SourceIdList getValueSources();

    /**
     * Fuer BAD_DATA wenn doppelte keys, fuer MapListLoader nicht benoetigt
     */
    SourceIdList getKeySources();
}
