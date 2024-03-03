/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 14:16:20
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.HWBaugruppenChangeDAO;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;


/**
 * Hibernate DAO-Implementierung von {@link HWBaugruppenChangeDAO}
 *
 *
 */
public class HWBaugruppenChangeDAOImpl extends Hibernate4DAOImpl implements HWBaugruppenChangeDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<HWBaugruppenChange> findOpenHWBaugruppenChanges() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(HWBaugruppenChange.class);
        criteria.add(Restrictions.isNull(HWBaugruppenChange.CANCELLED_AT));
        criteria.add(Restrictions.isNull(HWBaugruppenChange.CLOSED_AT));
        criteria.addOrder(Order.asc(HWBaugruppenChange.ID));

        @SuppressWarnings("unchecked")
        List<HWBaugruppenChange> result = criteria.list();
        return result;
    }

    @Override
    public void deleteHWBaugruppenChangeBgType(HWBaugruppenChangeBgType toDelete) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(HWBaugruppenChangeBgType.class.getName());
        hql.append(" bgt where bgt.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, toDelete.getId());
        query.executeUpdate();
    }

    @Override
    public void deletePort2Port(HWBaugruppenChangePort2Port toDelete) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(HWBaugruppenChangePort2Port.class.getName());
        hql.append(" p2p where p2p.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, toDelete.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteHWBaugruppenChangeDluV5(Long hwBgChangeDluId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(HWBaugruppenChangeDluV5.class.getName());
        hql.append(" v5 where v5." + HWBaugruppenChangeDluV5.HW_BG_CHANGE_DLU_ID + "=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, hwBgChangeDluId);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


