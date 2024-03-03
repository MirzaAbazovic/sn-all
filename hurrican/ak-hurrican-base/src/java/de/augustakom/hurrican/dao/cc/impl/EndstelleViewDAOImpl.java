/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 14:24:45
 */
package de.augustakom.hurrican.dao.cc.impl;

import static java.util.Collections.*;

import java.sql.Date;
import java.util.*;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.EndstelleViewDAO;
import de.augustakom.hurrican.exceptions.HurricanConcurrencyException;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;

/**
 * JDBC DAO-Implementierung von EndstelleViewDAO.
 *
 *
 */
public class EndstelleViewDAOImpl extends Hibernate4FindDAOImpl implements EndstelleViewDAO {

    private static final Logger LOGGER = Logger.getLogger(EndstelleViewDAOImpl.class);
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Endstelle> findEndstellen4Auftrag(final Long auftragId) {
        final StringBuilder sql = new StringBuilder();
        sql.append("select e.* ");
        sql.append(" from T_ENDSTELLE e ");
        sql.append(" inner join T_AUFTRAG_TECHNIK_2_ENDSTELLE a2e on e.ES_GRUPPE = a2e.ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on a2e.ID = at.AT_2_ES_ID ");
        sql.append(" where at.GUELTIG_VON<=:now and at.GUELTIG_BIS>:now ");
        sql.append(" and at.AUFTRAG_ID=:auftragId order by e.ES_TYP desc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        try {
            session.flush(); // Flush wg. TX-Handling notwendig!
        }
        catch (RuntimeException e) {
            throw new HurricanConcurrencyException("Error flushing session!", e);
        }

        Date now = DateTools.getActualSQLDate();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString()).addEntity(Endstelle.class);
        sqlQuery.setDate("now", now);
        sqlQuery.setLong("auftragId", auftragId);
        @SuppressWarnings("unchecked")
        List<Endstelle> endstellen = sqlQuery.list();
        return endstellen;
    }

    @Override
    public List<Endstelle> findEndstellen4AuftragWithoutFlush(Long auftragId) {
        final Date now = DateTools.getActualSQLDate();
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(AuftragTechnik.class)
                .add(Restrictions.eq(AuftragTechnik.AUFTRAG_ID, auftragId))
                .add(Restrictions.le(AuftragTechnik.GUELTIG_VON, now))
                .add(Restrictions.gt(AuftragTechnik.GUELTIG_BIS, now))
                .setProjection(Projections.property("auftragTechnik2EndstelleId"));
        final List<Long> at2EsIds = (List<Long>) detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();

        if (!at2EsIds.isEmpty()) {
            detachedCriteria = DetachedCriteria.forClass(Endstelle.class)
                    .add(Restrictions.eq(Endstelle.ENDSTELLE_GRUPPE_ID, Iterables.getOnlyElement(at2EsIds)))
                    .addOrder(Order.desc(Endstelle.ENDSTELLE_TYP));

            return (List<Endstelle>) detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        }
        else {
            return emptyList();
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
