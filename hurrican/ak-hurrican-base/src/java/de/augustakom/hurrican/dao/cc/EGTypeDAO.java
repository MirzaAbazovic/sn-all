/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2011 15:15:12
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * DAO-Interface fuer die Verwaltung von Engeraete-Typen.
 *
 *
 */
public interface EGTypeDAO extends StoreDAO, FindDAO {

    /**
     * Ermittelt Liste von Endgeräten bei dem jeder Hersteller nur einmal auftaucht
     */
    public List<String> getDistinctListOfManufacturer() throws FindException;

    /**
     * Ermittelt Liste von Endgeräten bei dem jedes Modell nur einmal auftaucht
     */
    public List<String> getDistinctListOfModels() throws FindException;

    /**
     * Ermittelt Liste aller Modelle des übergebenen Herstellers
     */
    public List<String> getDistinctListOfModelsByManufacturer(String manufacturer) throws FindException;

    /**
     * Sucht alle Endgeraetetypen, die einem bestimmten Endgeraet noch nicht zugeordnet sind
     */
    public List<EG> findPossibleEGs4EGType(Long egTypeId);

}


