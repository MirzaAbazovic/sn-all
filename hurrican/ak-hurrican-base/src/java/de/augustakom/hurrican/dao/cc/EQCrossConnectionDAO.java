/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:41:37
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;


/**
 * DAO Definition fuer die Verwaltung von EQCrossConnection Objekten.
 *
 *
 */
public interface EQCrossConnectionDAO extends StoreDAO, ByExampleDAO, FindDAO {

    /**
     * Ermittelt alle Cross-Connections zu einem Equipment, die zum angegebenen Datum gueltig sind.
     *
     * @param equipmentId ID des Equipments (=Ports), dessen Cross-Connections ermittelt werden sollen
     * @param validDate   Datum, zu dem die Cross-Connections ermittelt werden sollen
     * @return Liste mit den Cross-Connections zum angegebenen Equipment u. Datum, nie {@code null}
     */
    public List<EQCrossConnection> findEQCrossConnections(Long equipmentId, Date validDate);

    /**
     * Ermittelt alle Cross-Connections zu einem Equipment
     *
     * @param equipmentId ID des Equipments (=Ports), dessen Cross-Connections ermittelt werden sollen
     * @return Liste mit den Cross-Connections zum angegebenen Equipment u. Datum, nie {@code null}
     */
    public List<EQCrossConnection> findEQCrossConnections(Long equipmentId);

    /**
     * Ermittelt eine Liste der fuer den gegebenen BRAS VP schon genutzten VCs Ermittelt eine Liste der fuer den
     * gegebenen BRAS Pool heute und in Zukunft schon genutzten VCs
     *
     * @return Liste mit VPc, kann leer sein, nie {@code null}
     */
    List<Integer> findUsedBrasVcs(BrasPool brasPool);

    /**
     * Ermittelt eine Liste der fuer den gegebenen BRAS Pool im angegebenen Zeitraum schon genutzten VCs.
     *
     * @param from Falls {@code null}, wird das heutige Datum genutzt
     * @param till Falls {@code null}, wird das Hurrican-End-Date genutzt
     * @return Liste mit VPc, kann leer sein, nie {@code null}
     */
    List<Integer> findUsedBrasVcs(BrasPool brasPool, Date from, Date till);

    /**
     * Loescht alle Cross Connections des uebergenenen Ports.
     *
     * @param equipmentId id des Ports dessen Cross Connections geloescht werden sollen
     */
    public void deleteEQCrossConnectionsOfEquipment(Long equipmentId);

}
