/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.reporting.ReportDAO;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.Report2Prod;
import de.augustakom.hurrican.model.reporting.Report2ProdStati;
import de.augustakom.hurrican.model.reporting.ReportTemplate;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportDAO</code>
 *
 *
 */
public class ReportDAOImpl extends Hibernate4DAOImpl implements ReportDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Report> findReportsByProdStati(final Long type, final Long prodId, final Long statusId) {
        if (type == null) {
            return null;
        }
        final StringBuilder hql = new StringBuilder("select rep.id, rep.name, rep.userw, ");
        hql.append(" rep.description, rep.type, rep.reportGruppeId from ");
        hql.append(Report.class.getName()).append(" rep, ");
        if (prodId != null) {
            hql.append(Report2Prod.class.getName()).append(" r2p, ");
            if (statusId != null) {
                hql.append(Report2ProdStati.class.getName()).append(" r2pst, ");
            }
        }
        hql.append(ReportTemplate.class.getName()).append(" reptemp ");
        hql.append(" where reptemp.reportId = rep.id");
        if (prodId != null) {
            hql.append(" and r2p.produktId = :ProdId");
            hql.append(" and r2p.reportId = rep.id");
            if (statusId != null) {
                hql.append(" and r2pst.statusId = :StatusId");
                hql.append(" and r2pst.report2ProdId = r2p.id");
            }
        }
        hql.append(" and rep.type = :Type ");
        hql.append(" and reptemp.gueltigVon <= :Date ");
        hql.append(" and ((reptemp.gueltigBis is null) or (reptemp.gueltigBis >= :Date))");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        if (prodId != null) {
            q.setLong("ProdId", prodId);
            if (statusId != null) {
                q.setLong("StatusId", statusId);
            }
        }
        q.setLong("Type", type);
        q.setTimestamp("Date", DateTools.getActualSQLDate());

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (CollectionTools.isNotEmpty(result)) {
            List<Report> retVal = new ArrayList<Report>();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != null) {
                    Object[] values = result.get(i);
                    Report rep = new Report();
                    rep.setId(ObjectTools.getLongSilent(values, 0));
                    rep.setName(ObjectTools.getStringSilent(values, 1));
                    rep.setUserw(ObjectTools.getStringSilent(values, 2));
                    rep.setDescription(ObjectTools.getStringSilent(values, 3));
                    rep.setType(ObjectTools.getLongSilent(values, 4));
                    rep.setReportGruppeId(ObjectTools.getLongSilent(values, 5));
                    retVal.add(rep);
                }
            }
            return retVal;
        }
        return null;
    }

    @Override
    public List<Report> findActiveReports() {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Report.class);
        crit.add(Restrictions.or(Restrictions.eq("disabled", Boolean.FALSE), (Restrictions.isNull("disabled"))));
        @SuppressWarnings("unchecked")
        List<Report> results = crit.list();
        return results;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
