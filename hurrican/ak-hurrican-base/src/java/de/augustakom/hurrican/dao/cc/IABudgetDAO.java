/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 15:54:11
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.shared.view.InnenauftragQuery;
import de.augustakom.hurrican.model.shared.view.InnenauftragView;


/**
 * DAO-Interface fuer die Verwaltung von Innenauftrags-Budgets.
 *
 *
 */
public interface IABudgetDAO extends StoreDAO, ByExampleDAO, FindDAO, FindAllDAO {

    /**
     * Ermittelt Innenauftraege (und interne Auftraege) ueber das Query-Objekt und gibt relevante Informationen zu den
     * zugehoerigen Auftraegen zurueck.
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit Objekten des Typs <code>InnenauftragView</code>
     *
     */
    List<InnenauftragView> findIAViews(InnenauftragQuery query);

    void delete(IaLevel1 toDelete);

    String fetchInnenAuftragKostenstelle(Long auftragId);
}


