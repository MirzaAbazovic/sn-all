/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2008 13:19:29
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.view.BillRunView;


/**
 * DAO-Interface
 *
 *
 */
public interface BillDAO {

    /**
     * Funktion liefert eine Liste mit Views, die alle Informationen zu den Rechnungslaeufen entahlten
     *
     * @return Liste mit BillRunView-Objekten
     *
     */
    public List<BillRunView> findBillRunViews();
}


