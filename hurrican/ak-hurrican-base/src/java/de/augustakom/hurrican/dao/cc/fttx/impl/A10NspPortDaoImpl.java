/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 13:32:51
 */
package de.augustakom.hurrican.dao.cc.fttx.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.fttx.A10NspPortDao;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;

public class A10NspPortDaoImpl extends Hibernate4DAOImpl implements A10NspPortDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void delete(A10NspPort port) {
        sessionFactory.getCurrentSession().delete(port);
    }

    @Override
    public void delete(A10Nsp a10Nsp) {
        sessionFactory.getCurrentSession().delete(a10Nsp);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


