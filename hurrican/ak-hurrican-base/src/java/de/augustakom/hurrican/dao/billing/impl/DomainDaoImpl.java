/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.2004 14:13:30
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.billing.DomainDao;
import de.augustakom.hurrican.model.billing.view.IntDomain;


/**
 * Implementierung von <code>DomainDao</code>.
 *
 *
 */
public class DomainDaoImpl extends Hibernate4DAOImpl implements DomainDao {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<IntDomain> findByAuftragNoOrigs(List<Long> auftragNoOrigs) {
        DetachedCriteria criteria = DetachedCriteria.forClass(IntDomain.class);
        criteria.add(Property.forName(IntDomain.TAIFUN_AUFTRAG_ID).in(auftragNoOrigs));

        @SuppressWarnings("unchecked")
        List<IntDomain> domains = (List<IntDomain>) criteria.getExecutableCriteria(
                sessionFactory.getCurrentSession()).list();
        return domains;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


