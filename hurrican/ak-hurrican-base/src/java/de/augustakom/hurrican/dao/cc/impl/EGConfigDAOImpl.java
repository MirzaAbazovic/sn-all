/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 14:10:58
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.EGConfigDAO;
import de.augustakom.hurrican.model.cc.EGConfig;


/**
 * Hibernate DAO-Implementierung von <code>IPEndgeraeteConfigDAO</code>.
 *
 *
 */
public class EGConfigDAOImpl extends Hibernate4DAOImpl implements EGConfigDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public EGConfig findEGConfig(final Long eg2AuftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(EGConfig.class);
        criteria.add(Restrictions.eq("eg2AuftragId", eg2AuftragId));

        @SuppressWarnings("unchecked")
        List<EGConfig> result = criteria.list();
        if (result != null) {
            if (result.size() == 1) {
                return result.get(0);
            }
            else if (result.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(
                        "Zu viele EG-Konfigurationen gefunden!", 1, result.size());
            }
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

