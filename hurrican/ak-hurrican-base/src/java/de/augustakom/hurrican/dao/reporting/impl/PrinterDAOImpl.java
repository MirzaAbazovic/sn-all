/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2008 09:04:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.PrinterDAO;
import de.augustakom.hurrican.model.reporting.Printer;

/**
 * Hibernate DAO-Implementierung fuer <code>PrinterDAO</code>
 *
 *
 */
public class PrinterDAOImpl extends Hibernate4DAOImpl implements PrinterDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.reporting.PrinterDAO#findPrintersByName(java.lang.String)
     */
    public List<Printer> findPrintersByName(final String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Printer.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "name", name.toLowerCase());

        @SuppressWarnings("unchecked")
        List<Printer> results = crit.list();
        return (CollectionTools.isNotEmpty(results)) ? results : null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
