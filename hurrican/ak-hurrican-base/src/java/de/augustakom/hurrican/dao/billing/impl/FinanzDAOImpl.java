/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 14:47:02
 */
package de.augustakom.hurrican.dao.billing.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.hurrican.dao.billing.FinanzDAO;


/**
 * Hibernate DAO-Implementierung von <code>FinanzDAO</code>
 *
 *
 */
public class FinanzDAOImpl extends Hibernate4FindDAOImpl implements FinanzDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
