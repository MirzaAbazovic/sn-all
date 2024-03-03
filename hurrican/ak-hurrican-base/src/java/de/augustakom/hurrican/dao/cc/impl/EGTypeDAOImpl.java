/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2011 15:13:54
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.EGTypeDAO;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Hibernate DAO-Implementierung von <code>EGType</code>.
 *
 *
 */
public class EGTypeDAOImpl extends Hibernate4DAOImpl implements EGTypeDAO, FindDAO, StoreDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<String> getDistinctListOfManufacturer() throws FindException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EGType.class);
        criteria.setProjection(Projections.distinct(Projections.property("hersteller")));

        return criteria.list();
    }

    @Override
    public List<String> getDistinctListOfModels() throws FindException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EGType.class);
        criteria.setProjection(Projections.distinct(Projections.property("modell")));

        return criteria.list();
    }


    @Override
    public List<String> getDistinctListOfModelsByManufacturer(String manufacturer) throws FindException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EGType.class);
        criteria.setProjection(Projections.distinct(Projections.property("modell")));
        criteria.add(Restrictions.eq("hersteller", manufacturer));

        return criteria.list();
    }

    @Override
    public List<EG> findPossibleEGs4EGType(Long egTypeId) {
        StringBuilder sql = new StringBuilder("select * from t_eg where id in ");
        sql.append("(select ID as EG_ID from t_eg minus ");
        sql.append("select eg_id from t_eg_2_eg_type where eg_type_id = ?)");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong(0, egTypeId);
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<EG> retVal = new ArrayList<EG>();
            for (Object[] values : result) {
                int columnIndex = 0;
                EG eg = new EG();
                eg.setId(ObjectTools.getLongSilent(values, columnIndex++));
                eg.setEgInterneId(ObjectTools.getLongSilent(values, columnIndex++));
                eg.setEgName(ObjectTools.getStringSilent(values, columnIndex++));
                eg.setEgBeschreibung(ObjectTools.getStringSilent(values, columnIndex++));
                eg.setLsText(ObjectTools.getStringSilent(values, columnIndex++));
                eg.setEgTyp(ObjectTools.getLongSilent(values, columnIndex++));
                eg.setExtLeistungNo(ObjectTools.getLongSilent(values, columnIndex++));
                eg.setEgVerfuegbarVon(ObjectTools.getDateSilent(values, columnIndex++));
                eg.setEgVerfuegbarBis(ObjectTools.getDateSilent(values, columnIndex++));
                eg.setGarantiezeit(ObjectTools.getStringSilent(values, columnIndex++));
                eg.setProduktcode(ObjectTools.getStringSilent(values, columnIndex++));
                eg.setIsConfigurable(ObjectTools.getBooleanSilent(values, columnIndex++));
                eg.setConfPortforwarding(ObjectTools.getBooleanSilent(values, columnIndex++));
                eg.setConfS0backup(ObjectTools.getBooleanSilent(values, columnIndex++));
                eg.setCpsProvisioning(ObjectTools.getBooleanSilent(values, columnIndex++));
                eg.setUserW(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(eg);
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


