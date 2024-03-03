/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2007 08:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.Report2TechLsDAO;
import de.augustakom.hurrican.model.reporting.Report2Prod;
import de.augustakom.hurrican.model.reporting.Report2TechLs;

/**
 * Hibernate DAO-Implementierung fuer <code>Report2TechLsDAO</code>
 *
 *
 */
public class Report2TechLsDAOImpl extends Hibernate4DAOImpl implements Report2TechLsDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteReport2TechLs(final Long report2ProdId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Report2TechLs.class.getName());
        hql.append(" r where r.report2ProdId = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, report2ProdId);
        query.executeUpdate();
    }

    @Override
    public Report2TechLs findReport2TechLsByIds(Long rep2ProdId, Long techLsId) {
        Report2TechLs example = new Report2TechLs();
        example.setReport2ProdId(rep2ProdId);
        example.setTechLsId(techLsId);

        List<Report2TechLs> result = queryByExample(example, Report2TechLs.class);

        if ((result != null) && (result.size() == 1)) {
            return result.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public List<Long> findTechLsIds4ReportProdukt(final Long reportId, final Long produktId) {
        if ((reportId == null) || (produktId == null)) {
            return null;
        }

        final StringBuilder hql = new StringBuilder("select rep2TechLs.techLsId from ");
        hql.append(Report2Prod.class.getName()).append(" rep, ");
        hql.append(Report2TechLs.class.getName()).append(" rep2TechLs ");
        hql.append(" where rep.reportId = :ReportId");
        hql.append(" and rep.produktId = :ProduktId");
        hql.append(" and rep.id = rep2TechLs.report2ProdId");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("ReportId", reportId);
        q.setLong("ProduktId", produktId);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();

        if (CollectionTools.isNotEmpty(result)) {
            return result;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
