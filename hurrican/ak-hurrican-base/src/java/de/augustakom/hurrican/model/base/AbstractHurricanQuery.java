/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2004 15:58:53
 */
package de.augustakom.hurrican.model.base;

import de.augustakom.common.model.IQuery;


/**
 * Abstrakte Klasse fuer alle Query-Objekte innerhalb des Hurrican-Systems.
 *
 *
 */
public abstract class AbstractHurricanQuery extends AbstractHurricanModel implements IQuery {

    /**
     * @see de.augustakom.common.model.IQuery#isEmpty()
     */
    public abstract boolean isEmpty();

}


