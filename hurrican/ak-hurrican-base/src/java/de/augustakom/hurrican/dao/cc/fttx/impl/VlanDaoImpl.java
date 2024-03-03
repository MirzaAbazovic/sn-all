/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 11:40:49
 */
package de.augustakom.hurrican.dao.cc.fttx.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.fttx.VlanDao;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;

/**
 * Hibernate DAO-Implementierung von {@link VlanDao}.
 */
public class VlanDaoImpl extends Hibernate4DAOImpl implements VlanDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<EqVlan> findEqVlans(Long equipmentId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(EqVlan.class);
        criteria.add(Property.forName(EqVlan.EQ_ID).eq(equipmentId));
        criteria.addOrder(Order.asc(EqVlan.GUELTIG_VON));
        @SuppressWarnings("unchecked")
        List<EqVlan> eqVlans = (List<EqVlan>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return eqVlans;
    }

    @Override
    public List<EqVlan> findEqVlans(Long equipmentId, Date when) {
        DetachedCriteria criteria = DetachedCriteria.forClass(EqVlan.class);
        criteria.add(Property.forName(EqVlan.EQ_ID).eq(equipmentId));
        criteria.add(Restrictions.le(HistoryModel.GUELTIG_VON, when));
        criteria.add(Restrictions.gt(HistoryModel.GUELTIG_BIS, when));
        @SuppressWarnings("unchecked")
        List<EqVlan> eqVlans = (List<EqVlan>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return eqVlans;
    }

    @Override
    public List<EqVlan> findEqVlansForFuture(Long equipmentId, Date when) {
        DetachedCriteria criteria = DetachedCriteria.forClass(EqVlan.class);
        criteria.add(Property.forName(EqVlan.EQ_ID).eq(equipmentId));
        criteria.add(Restrictions.gt(HistoryModel.GUELTIG_BIS, when));
        @SuppressWarnings("unchecked")
        List<EqVlan> eqVlans = (List<EqVlan>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return eqVlans;
    }

    @Override
    public void delete(EqVlan entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public List<EqVlan> findEqVlansByBaugruppe(Long baugruppeId) {
        DetachedCriteria equipmentCriteria = DetachedCriteria.forClass(Equipment.class)
                .add(Property.forName(Equipment.HW_BAUGRUPPEN_ID).eq(baugruppeId))
                .setProjection(Projections.property(Equipment.ID));
        DetachedCriteria criteria = DetachedCriteria.forClass(EqVlan.class);
        criteria.add(Property.forName(EqVlan.EQ_ID).in(equipmentCriteria));
        @SuppressWarnings("unchecked")
        List<EqVlan> eqVlans = (List<EqVlan>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return eqVlans;
    }

    @Override
    public List<EqVlan> findEqVlans4Auftrag(final Long auftragId) {
        final StringBuilder hql = new StringBuilder(
                "select vlan from EqVlan vlan, Rangierung rang, Endstelle es, AuftragTechnik at ")
                .append("where (vlan.equipmentId = rang.eqInId) ")
                .append("and (at.auftragId = ").append(auftragId).append(") ")
                .append("and (es.endstelleGruppeId = at.auftragTechnik2EndstelleId) ")
                .append("and (es.endstelleTyp = 'B') ")
                .append("and (es.rangierId = rang.id ) ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        return (List<EqVlan>) query.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
