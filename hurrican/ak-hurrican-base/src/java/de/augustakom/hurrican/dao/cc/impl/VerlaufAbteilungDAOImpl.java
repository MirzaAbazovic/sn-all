/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2004 15:21:56
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Hibernate DAO-Implementierung von <code>VerlaufAbteilungDAO</code>.
 *
 *
 */
public class VerlaufAbteilungDAOImpl extends Hibernate4DAOImpl implements VerlaufAbteilungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public VerlaufAbteilung findByVerlaufAndAbtId(Long verlaufId, Long abtId, Long niederlassungId) {
        VerlaufAbteilung example = new VerlaufAbteilung();
        example.setVerlaufId(verlaufId);
        example.setAbteilungId(abtId);
        if (niederlassungId != null) {
            example.setNiederlassungId(niederlassungId);
        }

        List<VerlaufAbteilung> result = queryByExample(example, VerlaufAbteilung.class);
        return ((result != null) && (result.size() == 1)) ? (VerlaufAbteilung) result.get(0) : null;
    }

    @Override
    public List<VerlaufAbteilung> findByVerlaufAndVerlaufAbteilungId(final Long verlaufId, final Long verlaufAbteilungId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(VerlaufAbteilung.class);
        criteria.add(Restrictions.eq(VerlaufAbteilung.VERLAUF_ID, verlaufId));
        if (verlaufAbteilungId == null) {
            criteria.add(Restrictions.isNull(VerlaufAbteilung.PARENT_VERLAUF_ABTEILUNG_ID));
        }
        else {
            criteria.add(Restrictions.eq(VerlaufAbteilung.PARENT_VERLAUF_ABTEILUNG_ID, verlaufAbteilungId));
        }

        @SuppressWarnings("unchecked")
        List<VerlaufAbteilung> result = criteria.list();
        return result;
    }

    @Override
    public List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId, Long... abteilungIds) throws FindException {
        if (verlaufId == null || abteilungIds == null || abteilungIds.length == 0) { return Collections.emptyList(); }

        final DetachedCriteria criteria = DetachedCriteria.forClass(VerlaufAbteilung.class)
                .add(Restrictions.eq(VerlaufAbteilung.VERLAUF_ID, verlaufId))
                .add(Restrictions.in(VerlaufAbteilung.ABTEILUNG_ID, abteilungIds));

        return (List<VerlaufAbteilung>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    public int updateStatus(Long verlaufId, Long statusId) {
        StringBuilder sql = new StringBuilder();
        sql.append("update T_VERLAUF_ABTEILUNG set VERLAUF_STATUS_ID=:statusId where VERLAUF_ID=:verlaufId");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong("statusId", statusId);
        sqlQuery.setLong("verlaufId", verlaufId);

        return sqlQuery.executeUpdate();
    }

    @Override
    public int deleteVerlaufAbteilung(Long verlaufId, boolean notAMAndDispo) {
        StringBuilder sql = new StringBuilder("delete from T_VERLAUF_ABTEILUNG where VERLAUF_ID=:verlaufId ");
        if (notAMAndDispo) {
            sql.append(" and ABTEILUNG_ID not in (:am, :dispo, :np) ");
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameter("verlaufId", verlaufId);

        if (notAMAndDispo) {
            sqlQuery.setLong("am", Abteilung.AM);
            sqlQuery.setLong("dispo", Abteilung.DISPO);
            sqlQuery.setLong("np", Abteilung.NP);
        }
        return sqlQuery.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


