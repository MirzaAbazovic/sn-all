/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2004 13:01:41
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.UEVT2ZielDAO;
import de.augustakom.hurrican.model.cc.UEVT2Ziel;


/**
 * Hibernate DAO-Implementierung von <code>UEVT2ZielDAO</code>.
 *
 *
 */
public class UEVT2ZielDAOImpl extends Hibernate4DAOImpl implements UEVT2ZielDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteByUevtId(Long uevtId) {
        Session session = getSessionFactory().getCurrentSession();
        Query query = session.createQuery("delete from T_UEVT_2_ZIEL where UEVT_ID=:id");
        query.setParameter("id", uevtId);
        query.executeUpdate();
    }

    @Override
    public void store(List<UEVT2Ziel> toStore) {
        if (toStore != null) {
            for (UEVT2Ziel u2z : toStore) {
                store(u2z);
            }
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


