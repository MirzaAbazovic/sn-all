/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 11:27:09
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.UevtBuchtView;


/**
 * DAO-Interface zur Verwaltung von UEVT-Objekten.
 *
 *
 */
public interface UevtDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach allen UEVTs mit einer best. HVTStandort-ID.
     *
     * @param hvtStandort
     * @return List mit Objekten des Typs <code>UEVT</code>
     */
    public List<UEVT> findByHVTStandortId(Long hvtStandort);

    /**
     * Sucht nach allen UEVT-Buchen für den übergebenen UEVT und HVT-Standort
     *
     * @return {@link List} von {@link UevtBuchtView}s
     */
    public List<UevtBuchtView> findUevtBuchtenForUevt(Long hvtIdStandort, String uevt);

}


