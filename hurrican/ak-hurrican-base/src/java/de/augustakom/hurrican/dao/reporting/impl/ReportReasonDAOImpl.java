/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2007 09:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.ReportReasonDAO;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportReasonDAO</code>
 *
 *
 */
public class ReportReasonDAOImpl extends Hibernate4DAOImpl implements ReportReasonDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}

