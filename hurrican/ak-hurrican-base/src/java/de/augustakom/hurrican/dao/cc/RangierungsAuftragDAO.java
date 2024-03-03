/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 16:30:22
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.query.RangierungsAuftragBudgetQuery;
import de.augustakom.hurrican.model.cc.view.RangierungsAuftragBudgetView;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>RangierungsAuftrag</code>.
 *
 *
 */
public interface RangierungsAuftragDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Ermittelt eine Liste mit allen noch nicht erledigten Rangierungs-Auftraegen. Ein Rangierungsauftrag ist so lange
     * nicht erledigt, bis von der Technik ein Ausfuehrungstermin eingetragen ist.
     *
     * @return Liste mit den offenen Rangierungsauftraegen (Objekte vom Typ <code>RangierungsAuftrag</code>).
     *
     */
    public List<RangierungsAuftrag> findUnfinishedRAs();

    /**
     * Ermittelt div. Daten von Rangierungs-Auftraegen und den zugehoerigen Budgets an Hand der Filter-Parameter aus dem
     * Query-Objekt.
     *
     * @param query Query-Objekt mit den Filter-Parametern
     * @return Liste mit Objekten des Typs <code>RangierungsAuftragBudgetView</code>
     *
     */
    public List<RangierungsAuftragBudgetView> findRABudgetViews(RangierungsAuftragBudgetQuery query);

}


