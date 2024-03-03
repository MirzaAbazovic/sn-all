/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 12:00:01
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.EndgeraetPortDAO;
import de.augustakom.hurrican.model.cc.EndgeraetPort;

/**
 * Hibernate DAO-Implementierung von <code>EndgeraetPortDAO</code>.
 */
public class EndgeraetPortDAOImpl extends Hibernate4DAOImpl implements EndgeraetPortDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Override
    public List<EndgeraetPort> findByNumbers(final Collection<Integer> numbers) {
        Session session = sessionFactory.getCurrentSession();
        List<EndgeraetPort> portsFound = Collections.EMPTY_LIST;
        boolean atLeastOnePortNumber = !CollectionUtils.isEmpty(numbers);
        if (atLeastOnePortNumber) {
            portsFound = session.createCriteria(EndgeraetPort.class).add(Restrictions.in("number", numbers)).list();
        }
        return portsFound;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
