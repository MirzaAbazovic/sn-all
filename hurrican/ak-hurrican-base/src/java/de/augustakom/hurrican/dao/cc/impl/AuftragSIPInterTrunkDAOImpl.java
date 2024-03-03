/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 11:56:29
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragSIPInterTrunkDAO;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;


/**
 * Hibernate DAO-Implementierung von {@link AuftragSIPInterTrunkDAO}.
 */
public class AuftragSIPInterTrunkDAOImpl extends Hibernate4DAOImpl implements AuftragSIPInterTrunkDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.common.tools.dao.iface.DeleteDAO#deleteById(java.io.Serializable)
     */
    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(AuftragSIPInterTrunk.class.getName());
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


