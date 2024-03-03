/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2006 08:16:51
 */
package de.augustakom.hurrican.dao.billing;

import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * DAO-Interface fuer die Ermittlung von Objekten des Typs <code>ServiceValue</code>.
 *
 *
 */
public interface ServiceValueDAO {

    /**
     * Sucht nach einem bestimmten Leistungswert.
     *
     * @param leistungNo
     * @param value      (optional)
     * @return
     *
     */
    ServiceValue findServiceValue(Long leistungNo, String value) throws FindException;

}


