/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2009 08:51:22
 */
package de.augustakom.hurrican.dao.exmodules.massenbenachrichtigung;

import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessResourceFailureException;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.exmodules.massenbenachrichtigung.TServiceExp;
import de.augustakom.hurrican.service.exmodules.tal.ITALService;


/**
 *
 */
public interface MassenbenachrichtigungDAO extends ITALService, StoreDAO {

    /**
     * Speichert das angegebene Objekt
     *
     * @param toSave zu speicherndes Objekt.
     * @throws DataAccessResourceFailureException
     * @throws HibernateException
     * @throws IllegalStateException
     *
     */
    public void saveTServiceExp(TServiceExp toSave) throws DataAccessResourceFailureException, HibernateException, IllegalStateException;

}
