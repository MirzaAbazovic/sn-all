/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 08:52:26
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.hurrican.dao.cc.AuftragVoIPDAO;
import de.augustakom.hurrican.model.cc.AuftragVoIP;


/**
 * Hibernate DAO-Implementierung von <code>AuftragVoIPDAO</code>.
 *
 *
 */
public class AuftragVoIPDAOImpl extends HurricanHibernateDaoImpl implements AuftragVoIPDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragVoIP findAuftragVoIP(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Criteria crit = session.createCriteria(AuftragVoIP.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "auftragId", auftragId);
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);

        List<AuftragVoIP> results = crit.list();
        return (CollectionTools.isNotEmpty(results)) ? results.get(results.size() - 1) : null;
    }

    @Override
    public AuftragVoIP update4History(AuftragVoIP obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new AuftragVoIP(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


