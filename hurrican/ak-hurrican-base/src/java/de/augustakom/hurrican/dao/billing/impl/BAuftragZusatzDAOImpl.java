/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2007 08:27:02
 */
package de.augustakom.hurrican.dao.billing.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.hurrican.dao.billing.BAuftragZusatzDAO;


/**
 * Hibernate DAO-Implementierung von <code>BAUftragKombiDAO</code>
 *
 *
 */
public class BAuftragZusatzDAOImpl extends Hibernate4FindDAOImpl implements BAuftragZusatzDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
