/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 13:19:40
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.math.*;
import java.sql.Date;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.ProduktViewDAO;
import de.augustakom.hurrican.model.cc.ProduktGruppe;


/**
 * JDBC DAO-Implementierung von ProduktViewDAO.
 *
 *
 */
public class ProduktViewDAOImpl extends Hibernate4FindDAOImpl implements ProduktViewDAO {

    private static final Logger LOGGER = Logger.getLogger(ProduktViewDAOImpl.class);
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public ProduktGruppe findPG4Auftrag(Long ccAuftragId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select pg.ID from T_AUFTRAG_DATEN ad ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID ");
        sql.append(" where ad.AUFTRAG_ID=? and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>?");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Date now = DateTools.getActualSQLDate();

        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] { ccAuftragId, now, now }, new Type[] { new LongType(), new DateType(), new DateType() });
        List<BigDecimal> result = sqlQuery.list();
        if ((result != null) && (!result.isEmpty())) {
            BigDecimal value = result.get(result.size() - 1);
            return findById(value.longValue(), ProduktGruppe.class);
        }

        return null;
    }

    @Override
    public List<ProduktGruppe> findProduktGruppen4Hurrican() {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct pg.* ");
        sql.append(" from T_PRODUKTGRUPPE pg ");
        sql.append(" inner join T_PRODUKT p on (pg.ID=p.PRODUKTGRUPPE_ID AND p.AUFTRAGSERSTELLUNG='1')");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.addEntity(ProduktGruppe.class);
        List<ProduktGruppe> results = (ArrayList<ProduktGruppe>) sqlQuery.list();
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }

        return results;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


