/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:37:19
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.hurrican.dao.cc.KundeNbzDAO;
import de.augustakom.hurrican.model.cc.KundeNbz;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Hibernate DAO-Implementierung von KundeNbzDAO.
 *
 *
 */
public class KundeNbzDAOImpl extends Hibernate4DAOImpl implements KundeNbzDAO, DeleteDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.cc.KundeNbzDAO#findKundeNbzByNbz(java.lang.String)
     */
    @Override
    public List<KundeNbz> findKundeNbzByNbz(String nbz) throws FindException {
        @SuppressWarnings("unchecked")
        List<KundeNbz> result = find("from " + KundeNbz.class.getName() + " knbz where knbz.nbz=?",
                nbz);
        return (result == null) ? new ArrayList<KundeNbz>() : result;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.KundeNbzDAO#findKundeNbzByNo(java.lang.Long)
     */
    @Override
    public KundeNbz findKundeNbzByNo(Long kundeNo) throws FindException {
        @SuppressWarnings("unchecked")
        List<KundeNbz> result = find("from " + KundeNbz.class.getName() + " knbz where knbz.kundeNo=?",
                kundeNo);
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.KundeNbzDAO#saveKundeNbz(de.augustakom.hurrican.model.cc.KundeNbz)
     */
    @Override
    public void saveKundeNbz(KundeNbz kundeNbz) throws StoreException {
        store(kundeNbz);
    }

    /**
     * @see de.augustakom.common.tools.dao.iface.DeleteDAO#deleteById(java.io.Serializable)
     */
    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(KundeNbz.class.getName());
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
