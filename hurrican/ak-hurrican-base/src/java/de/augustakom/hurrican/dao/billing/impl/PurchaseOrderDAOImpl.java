/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH

 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2013 11:17:32
 */
package de.augustakom.hurrican.dao.billing.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.billing.PurchaseOrderDAO;

/**
 * DAO-Implementierung fuer Objekte vom Typ <code>de.augustakom.hurrican.model.billing.PurchaseOrder</code>
 *
 *
 */
public class PurchaseOrderDAOImpl extends Hibernate4DAOImpl implements PurchaseOrderDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
