/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2005 11:30:47
 */
package de.augustakom.hurrican.dao.billing;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.WebgatePW;


/**
 * DAO-Interface zur Verwaltung von Objekten des Typs <code>WebgatePW</code>.
 *
 *
 */
public interface WebgatePWDAO extends ByExampleDAO, FindDAO {

    /**
     * Liefert das erste Objekt zu einer best. Kundennummer
     *
     * @param kundeNo
     * @return
     *
     */
    public WebgatePW findFirstByKundeNo(Long kundeNo);

}


