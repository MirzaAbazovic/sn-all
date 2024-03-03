/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 16:40:51
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;

/**
 * Interface zur Definition der DAO-Methoden, um HWSubrack-Objekte zu verwalten.
 *
 *
 */
public interface HWSubrackDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach allen Subracks, die in einem best. Rack eingebaut sind.
     *
     * @param rackId ID des Racks
     * @return Subrack, oder {@code null}, falls das Rack/Subrack nicht existiert
     */
    public List<HWSubrack> findSubracksForRack(Long rackId);

    /**
     * Sucht nach allen Subracks, die an einem best. HVT Standort eingebaut sind.
     *
     * @param hvtIdStandort ID des Standorts
     * @return Subrack, oder {@code null}, falls das Rack/Subrack nicht existiert
     */
    public List<HWSubrack> findSubracksForStandort(Long hvtIdStandort);

    /**
     * Sucht nach allen Subracks, die in einem bestimmten Rack-Typ eingebaut werden koennen.
     *
     * @param rackType Typ des Racks (siehe HWRack). Falls {@code null}, werden alle Subrack-Typen zurueckgegeben.
     * @return Subrack-Typen, oder leere Liste, falls fuer den rackTypen keine Subracks definiert sind.
     */
    public List<HWSubrackTyp> findSubrackTypes(String rackType);

    /**
     * Sucht nach allen Subracks mit der angegebenen rackId und modNumber
     *
     * @param rackId
     * @param modNumber
     * @return Liste mit allen gefunden Ergebnissen oder leere Liste
     */
    public List<HWSubrack> findHwSubracksByRackIdAndModNumber(Long rackId, String modNumber);
}


