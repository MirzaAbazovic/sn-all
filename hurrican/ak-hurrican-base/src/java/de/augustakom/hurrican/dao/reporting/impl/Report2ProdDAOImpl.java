/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 13:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.reporting.Report2ProdDAO;
import de.augustakom.hurrican.model.reporting.Report2Prod;
import de.augustakom.hurrican.model.reporting.view.Report2ProdView;

/**
 * Hibernate DAO-Implementierung fuer <code>Report2ProdDAO</code>
 *
 *
 */
public class Report2ProdDAOImpl extends Hibernate4DAOImpl implements Report2ProdDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteProds4Report(final Long reportId) {
        if (reportId == null) {
            return;
        }
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Report2Prod.class.getName());
        hql.append(" r where r.reportId = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, reportId);
        query.executeUpdate();
    }

    @Override
    public Report2Prod findReport2ProdByIds(Long reportId, Long produktId) {
        Report2Prod example = new Report2Prod();
        example.setReportId(reportId);
        example.setProduktId(produktId);

        List<Report2Prod> rep2Prods = queryByExample(example, Report2Prod.class);

        if ((rep2Prods != null) && (rep2Prods.size() == 1)) {
            return rep2Prods.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public List<Report2ProdView> findReport2ProdView4Report(Long reportId) {
        if (reportId == null) {
            return null;
        }

        StringBuilder sql = new StringBuilder("select rep.NAME, r2p.PROD_ID, p.ANSCHLUSSART, ");
        sql.append(" r2s.STATUS_ID, s.STATUS_TEXT from t_report_2_prod r2p ");
        sql.append(" inner join t_rep2prod_stati r2s on r2s.REP2PROD_ID=r2p.ID ");
        sql.append(" inner join t_report rep on rep.ID=r2p.REP_ID ");
        sql.append(" inner join T_auftrag_status s on s.ID=r2s.STATUS_ID ");
        sql.append(" inner join t_produkt p on p.PROD_ID=r2p.PROD_ID ");
        sql.append(" where rep.ID=:reportId ");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong("reportId", reportId);

        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<Report2ProdView> retVal = new ArrayList<Report2ProdView>();
            for (Object[] values : result) {
                Report2ProdView view = new Report2ProdView();
                view.setReportId(reportId);
                view.setReportName(ObjectTools.getStringSilent(values, 0));
                view.setProduktId(ObjectTools.getLongSilent(values, 1));
                view.setProduktName(ObjectTools.getStringSilent(values, 2));
                view.setStatusId(ObjectTools.getLongSilent(values, 3));
                view.setStatusName(ObjectTools.getStringSilent(values, 4));
                retVal.add(view);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
