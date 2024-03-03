/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2004 08:18:05
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.HVTTechnik;


/**
 * DAO-Interface fuer Objekte des Typs <code>HVTTechnik</code>.
 *
 *
 */
public interface HVTTechnikDAO extends FindAllDAO, ByExampleDAO, FindDAO, StoreDAO {

    /**
     * Sucht nach allen HVT-Techniken, die einem best. HVT-Standort zugeordnet sind.
     *
     * @param hvtIdStandort
     * @return
     */
    public List<HVTTechnik> findByHVTStandort(Long hvtIdStandort);

    /**
     * Loescht die HVTTechnik-Zuordnung fuer einen best. HVT-Standort.
     *
     * @param hvtIdStandort
     */
    public void deleteHVTTechniken4Standort(Long hvtIdStandort);

}


