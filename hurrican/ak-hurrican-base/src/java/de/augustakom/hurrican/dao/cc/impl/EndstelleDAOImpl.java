/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2004 16:55:40
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.EndstelleDAO;
import de.augustakom.hurrican.model.cc.AuftragTechnik2Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;

/**
 * Hibernate DAO-Implementierung von <code>EndstelleDAO</code>.
 *
 *
 */
public class EndstelleDAOImpl extends HurricanHibernateDaoImpl implements EndstelleDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Long createNewMappingId() {
        AuftragTechnik2Endstelle a2e = new AuftragTechnik2Endstelle();
        sessionFactory.getCurrentSession().save(a2e);
        return a2e.getId();
    }

    @Override
    public EndstelleAnsprechpartner update4History(EndstelleAnsprechpartner obj4History, Serializable id,
            Date gueltigBis) {
        return update4History(obj4History, new EndstelleAnsprechpartner(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


