/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2007 09:04:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.ReportPaperFormatDAO;
import de.augustakom.hurrican.model.reporting.Printer;
import de.augustakom.hurrican.model.reporting.Printer2Paper;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportPaperFormatDAO</code>
 *
 *
 */
public class ReportPaperFormatDAOImpl extends Hibernate4DAOImpl implements ReportPaperFormatDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public String findTrayName4Report(final String printerName, final Long paperId) {
        if (StringUtils.isEmpty(printerName) || (paperId == null)) {
            return null;
        }

        final StringBuilder hql = new StringBuilder(" select p2r.trayName from ");
        hql.append(Printer.class.getName()).append(" prn, ");
        hql.append(Printer2Paper.class.getName()).append(" p2r ");
        hql.append(" where lower(prn.name) = :PrnName");
        hql.append(" and prn.id = p2r.printerId");
        hql.append(" and p2r.paperId = :PaperId");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setString("PrnName", printerName.toLowerCase());
        q.setLong("PaperId", paperId);

        @SuppressWarnings("unchecked")
        List<String> result = q.list();
        if (CollectionTools.isNotEmpty(result) && (result.size() == 1)) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
