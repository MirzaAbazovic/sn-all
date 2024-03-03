/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2015
 */
package de.augustakom.common.tools.dao.hibernate;

import java.io.*;

import de.augustakom.common.tools.dao.iface.LoadDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;

/**
 * Created by maherma on 28.01.2015.
 */
public abstract class Hibernate4DAOImpl extends Hibernate4FindDAOImpl implements StoreDAO, LoadDAO {

    @Override
    public void load(Object object, Serializable id) {
        getSession().load(object, id);
    }

    @Override
    public <T extends Serializable> T store(T toStore) {
        getSession().saveOrUpdate(toStore);
        return toStore;
    }

    @Override
    public <T extends Serializable> T merge(T toMerge) {
        return (T) getSession().merge(toMerge);
    }

    @Override
    public void flushSession() {
        HibernateSessionHelper.flushSession(getSessionFactory());
    }

    @Override
    public void flushSessionLoud() {
        HibernateSessionHelper.flushSessionLoud(getSessionFactory());
    }

}
