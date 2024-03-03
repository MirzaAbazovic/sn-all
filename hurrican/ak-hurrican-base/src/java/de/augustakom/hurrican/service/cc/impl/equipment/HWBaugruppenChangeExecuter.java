/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 13:28:35
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Interface fuer alle Executer-Implementierungen fuer Baugruppen-Schwenks.
 */
public interface HWBaugruppenChangeExecuter {

    /**
     * Fuehrt den Baugruppen-Schwenk durch
     *
     * @throws StoreException
     */
    public void execute() throws StoreException;

}


