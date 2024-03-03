/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 11:44:35
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.time.*;
import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragAktionDAO;
import de.augustakom.hurrican.model.cc.AuftragAktion;

public class AuftragAktionDAOImpl extends Hibernate4DAOImpl implements AuftragAktionDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragAktion getActiveAktion(Long auftragId, AuftragAktion.AktionType type) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AuftragAktion.class);
        criteria.add(Property.forName("auftragId").eq(auftragId));
        criteria.add(Property.forName("desiredExecutionDate").ge(LocalDate.now()));
        criteria.add(Property.forName("cancelled").eq(false));
        criteria.add(Property.forName("action").eq(type));
        @SuppressWarnings("unchecked")
        List<AuftragAktion> list = (List<AuftragAktion>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return Iterables.getFirst(list, null);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
