/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2009 08:53:41
 */
package de.augustakom.hurrican.dao.exmodules.massenbenachrichtigung.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.exmodules.massenbenachrichtigung.MassenbenachrichtigungDAO;
import de.augustakom.hurrican.model.exmodules.massenbenachrichtigung.TServiceExp;

/**
 *
 */
public class MassenbenachrichtigungDAOImpl extends Hibernate4DAOImpl implements MassenbenachrichtigungDAO {

    @Autowired
    @Qualifier("tal.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.exmodules.massenbenachrichtigung.MassenbenachrichtigungDAO#saveTServiceExp(de.augustakom.hurrican.model.exmodules.massenbenachrichtigung.TServiceExp)
     */
    @Override
    public void saveTServiceExp(final TServiceExp toSave) throws DataAccessResourceFailureException, HibernateException {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(toSave);
        session.flush();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
