/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 10:26:44
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.hurrican.dao.billing.AdresseDAO;
import de.augustakom.hurrican.model.billing.Adresse;

/**
 * Hibernate DAO-Implementierung fuer Objekte vom Typ <code>de.augustakom.hurrican.model.billing.Adresse</code>
 *
 *
 */
public class AdresseDAOImpl extends Hibernate4FindDAOImpl implements AdresseDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Adresse> findByANos(final List<Long> aNos) {
        Session session = sessionFactory.getCurrentSession();
        List<Adresse> result = new ArrayList<Adresse>();
        int maxLength = 100;
        int length = aNos.size();
        int count = 0;
        while (count < length) {
            int forCount = (length - count > maxLength) ? count + maxLength : count + (length - count);
            int arrayLength = (length - count > maxLength) ? maxLength : length - count;
            Long[] params = new Long[arrayLength];
            int x = 0;
            for (int i = count; i < forCount; i++) {
                params[x] = aNos.get(i);
                x++;
            }

            Criteria criteria = session.createCriteria(Adresse.class);
            criteria.add(Restrictions.in("adresseNo", params));

            @SuppressWarnings("unchecked")
            List<Adresse> tmp = criteria.list();
            result.addAll(tmp);
            count += arrayLength;

            session.clear();
        }
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


