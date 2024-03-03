/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:37:19
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.AbteilungDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;


/**
 * Hibernate DAO-Implementierung von AbteilungDAO.
 *
 *
 */
public class AbteilungDAOImpl extends Hibernate4DAOImpl implements AbteilungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Abteilung> findByIds(final Collection<Long> ids) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(Abteilung.class.getName());
        hql.append(" a where a.id in (:ids)");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameterList("ids", ids);

        @SuppressWarnings("unchecked")
        List<Abteilung> result = query.list();
        return result;
    }

    @Override
    public List<Niederlassung> findNL4Abteilung(Long abtId) {
        StringBuilder sql = new StringBuilder("select n.ID, n.VERSION, n.TEXT, n.DISPO_TEAMPOSTFACH, ");
        sql.append(" n.PARENT ");
        sql.append(" from T_NIEDERLASSUNG n ");
        sql.append(" inner join T_ABTEILUNG_2_NIEDERLASSUNG a2n on a2n.NIEDERLASSUNG_ID=n.ID ");
        sql.append(" where a2n.ABTEILUNG_ID = ? ");
        sql.append(" order by n.TEXT ");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong(0, abtId);
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<Niederlassung> retVal = new ArrayList<Niederlassung>();
            for (Object[] values : result) {
                Niederlassung view = new Niederlassung();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setVersion(ObjectTools.getLongSilent(values, 1));
                view.setName(ObjectTools.getStringSilent(values, 2));
                view.setDispoTeampostfach(ObjectTools.getStringSilent(values, 3));
                view.setParentId(ObjectTools.getLongSilent(values, 4));

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


