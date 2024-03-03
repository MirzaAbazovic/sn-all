/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.01.2015
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKTeamDAO;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;

/**
 * Hibernate-Implementierung von AKTeamDAO. <br>
 *
 * Created by maherma on 29.01.2015.
 */
public class AKTeamDAOImpl extends Hibernate4FindDAOImpl implements AKTeamDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public List<AKTeam> findAll() {
        return findAll(AKTeam.class);
    }
}
