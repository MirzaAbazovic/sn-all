/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2010 15:50:41
 */

package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.HVTStandortTechTypeDAO;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;


/**
 * Hibernate DAO-Implementierung von {@link HVTStandortTechTypeDAO}.
 *
 *
 */
public class HVTStandortTechTypeDAOImpl extends Hibernate4DAOImpl implements HVTStandortTechTypeDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteById(Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(HVTStandortTechType.class.getName());
        hql.append(" c where c.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter(0, id);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
