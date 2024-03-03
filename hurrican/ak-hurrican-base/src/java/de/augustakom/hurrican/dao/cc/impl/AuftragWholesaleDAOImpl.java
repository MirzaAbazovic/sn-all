/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 11:44:35
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragWholesaleDAO;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Hibernate DAO-Implementierung von {@link AuftragWholesaleDAO}.
 */
public class AuftragWholesaleDAOImpl extends Hibernate4DAOImpl implements AuftragWholesaleDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public AuftragWholesale findByAuftragId(Long auftragId) throws FindException {
        DetachedCriteria criteria = DetachedCriteria.forClass(AuftragWholesale.class);
        criteria.add(Property.forName("auftragId").eq(auftragId));
        @SuppressWarnings("unchecked")
        List<AuftragWholesale> list = (List<AuftragWholesale>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        if (list == null) {
            return null;
        }
        if (list.size() > 1) {
            final String msg = "'%s' Wholesale-Auftraege zur auftragId '%s' ermittelt. Anzahl muss eindeutig sein!";
            throw new FindException(String.format(msg, list.size(), auftragId));
        }
        return Iterables.getFirst(list, null);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
