/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.10.2011 11:25:41
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.SperrklasseDAO;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;

/**
 * Implementiert die Methoden aus {@link SperrklasseDAO}.
 *
 *
 * @since Release 10
 */
public class SperrklasseDAOImpl extends Hibernate4DAOImpl implements SperrklasseDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    Sperrklasse findSperrklasse(final Integer sperrklasseNumber, final String propertyToCheck) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Sperrklasse.class);
        criteria.add(Restrictions.eq(propertyToCheck, sperrklasseNumber));
        List<Sperrklasse> result = criteria.list();
        if (result.isEmpty()) {
            return null;
        }
        else if (result.size() > 1) {
            final String errorMsg = String.format("Fuer die Sperrklassennummer '%s' wurden mehrere Sperrklassen!",
                    sperrklasseNumber);
            throw new java.lang.IllegalStateException(errorMsg);
        }
        return result.get(0);
    }

    @Override
    public Sperrklasse findDefaultSperrklasse(Integer sperrklasseNumber) {
        return findSperrklasse(sperrklasseNumber, "sperrklasse");
    }

    @Override
    public Sperrklasse findImsSperrklasse(Integer sperrklasseNumber) {
        return findSperrklasse(sperrklasseNumber, "sperrklasseIms");
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
