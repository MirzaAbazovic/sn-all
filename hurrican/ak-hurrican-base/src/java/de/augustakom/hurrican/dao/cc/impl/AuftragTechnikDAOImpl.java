/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2004 08:30:18
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.cc.AuftragTechnik;


/**
 * Hibernate DAO-Implementierung von <code>AuftragTechnikDAO</code>
 *
 *
 */
public class AuftragTechnikDAOImpl extends HurricanHibernateDaoImpl implements AuftragTechnikDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragTechnik findByAuftragId(final Long auftragId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragTechnik.class.getName());
        hql.append(" at where at.auftragId= :aId and at.gueltigVon<= :now  and at.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", auftragId.longValue());
        q.setDate("now", now);

        List result = q.list();
        return ((result != null) && (!result.isEmpty())) ? (AuftragTechnik) result.get(result.size() - 1) : null;
    }

    @Override
    public AuftragTechnik findAuftragTechnik4ESGruppe(final Long esGruppeId) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(AuftragTechnik.class.getName());
        hql.append(" at where at.auftragTechnik2EndstelleId= :egId and at.gueltigVon<= :now and at.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("egId", esGruppeId.longValue());
        q.setDate("now", now);

        List result = q.list();
        return ((result != null) && (!result.isEmpty())) ? (AuftragTechnik) result.get(result.size() - 1) : null;
    }

    @Override
    public AuftragTechnik findAuftragTechnik4VbzId(final Long vbzId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(AuftragTechnik.class);
        Date now = new Date();
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "vbzId", vbzId);

        @SuppressWarnings("unchecked")
        List<AuftragTechnik> matchingAuftragTechnik = crit.list();
        if (CollectionTools.isNotEmpty(matchingAuftragTechnik)) {
            return matchingAuftragTechnik.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AuftragTechnik> findAuftragTechniken4VbzId(final Long vbzId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(AuftragTechnik.class);
        Date now = new Date();
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "vbzId", vbzId);
        return crit.list();
    }

    @Override
    public List<AuftragTechnik> findByIntAccountId(final Long intAccountId) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(AuftragTechnik.class.getName());
        hql.append(" at where at.intAccountId= :accId and at.gueltigVon<= :now and at.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("accId", intAccountId.longValue());
        q.setDate("now", now);

        return q.list();
    }

    @Override
    public void update4IntAccount(Long intAccIdNew, Long intAccIdOld) {
        StringBuilder sql = new StringBuilder("update T_AUFTRAG_TECHNIK set INT_ACCOUNT_ID=? ");
        sql.append(" where INT_ACCOUNT_ID=? and GUELTIG_VON<=? and GUELTIG_BIS>?");

        Date now = new Date();
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] { intAccIdNew, intAccIdOld, now, now },
                new Type[] {new LongType(), new LongType(), new DateType(), new DateType()});

        sqlQuery.executeUpdate();
    }

    @Override
    public AuftragTechnik update4History(AuftragTechnik obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new AuftragTechnik(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


