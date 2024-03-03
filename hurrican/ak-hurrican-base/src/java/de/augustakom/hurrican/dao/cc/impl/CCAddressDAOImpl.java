/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:20:11
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.CCAddressDAO;
import de.augustakom.hurrican.model.cc.CCAddress;


/**
 * Hibernate DAO-Implementierung von <code>CCAddressDAO</code>.
 *
 *
 */
public class CCAddressDAOImpl extends Hibernate4DAOImpl implements CCAddressDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public <T> List<T> queryByExampleLike(CCAddress example, Class<T> type) {
        Example ex = Example.create(example)
                .enableLike()
                .ignoreCase()
                .excludeZeroes();

        return queryByCreatedExample(ex, type);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


