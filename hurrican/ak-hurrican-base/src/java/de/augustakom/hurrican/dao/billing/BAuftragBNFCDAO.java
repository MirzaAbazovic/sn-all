/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2006 14:35:08
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.query.BAuftragBNFCQuery;
import de.augustakom.hurrican.model.shared.view.AuftragINRufnummerView;


/**
 * DAO-Interface fuer die Ermittlung von Objekten des Typs BAuftragBNFC.
 *
 *
 */
public interface BAuftragBNFCDAO extends FindDAO {

    /**
     * Sucht nach Auftragsdaten ueber das angegebene Query.
     *
     * @param query
     * @return
     *
     */
    public List<AuftragINRufnummerView> findINViews(BAuftragBNFCQuery query);

}


