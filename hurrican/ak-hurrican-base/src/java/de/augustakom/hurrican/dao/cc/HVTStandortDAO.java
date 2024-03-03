/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 13:09:18
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTClusterView;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;

/**
 * Interface zur Definition der DAO-Methoden, um Objekte vom Typ <code>HVTStandort</code> zu verwalten.
 *
 *
 */
public interface HVTStandortDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach <strong>allen</strong> HVTStandort-Datensaetzen, die einer bestimmten HVT-Gruppe angehoeren.
     *
     * @param hvtGruppeId ID der HVT-Gruppe deren HVT-Standorte gesucht werden.
     * @return Liste mit HVTStandort-Objekten oder <code>null</code>
     */
    public List<HVTStandort> findHVTStandorte4Gruppe(Long hvtGruppeId);

    /**
     * @see HVTStandortDAO#findHVTStandorte4Gruppe Ausnahme: es werden nur aktive/gueltige HVT-Standorte zurueck geliefert.
     */
    public List<HVTStandort> findActiveHVTStandorte4Gruppe(Long hvtGruppeId);

    /**
     * Sucht nach wichtigen Daten aller HVT-Standorte und -Gruppen.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>HVTGruppeStdView</code>.
     */
    public List<HVTGruppeStdView> findHVTViews(HVTQuery query);

    /**
     * Sucht nach wichtigen Daten aller HVT-Standorte und -Gruppen.
     *
     * @param onkz
     * @param asb
     * @param ortsteil
     * @param ort
     * @param standortTypRefId
     * @param clusterId
     * @return Pair mit zwei Liste mit <code>HVTStandort</code> und <code>HVTGruppe</code>.
     */
    Pair<List<HVTStandort>, List<HVTGruppe>> findHVTStandorteAndGruppen(String onkz, Integer asb, String ortsteil,
            String ort, Long standortTypRefId, String clusterId );

    /**
     * Ermittelt eine Liste von Cluster Views zu der Liste von HVTStandorten.
     *
     * @return Liste mit Objekten des Typs <code>HVTClusterView</code>.
     */
    public List<HVTClusterView> findHVTClusterViews(List<Long> hvtStandortIds);

    /**
     * Ermittelt einen HVT-Standort ueber die ONKZ und den ASB.
     *
     * @param onkz relevante/gesuchte ONKZ
     * @param asb  relevanter/gesuchter ASB
     * @return Objekt vom Typ <code>HVTStandort</code>
     *
     */
    public HVTStandort findHVTStandort(String onkz, Integer asb);

    /**
     * Findet alle Standorte eines Typs, mit der gegebenen ONKZ und der DTAG ASB (letzte 3 Stellen).
     */
    public List<HVTStandort> findHVTStandort4DtagAsb(String onkz, Integer dtagAsb, Long standortTypRefId);

}


