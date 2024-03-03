/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 10:56:55
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.IPSecSite2SiteDAO;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;


/**
 * Hibernate DAO-Implementierung von {@link IPSecSite2SiteDAO}
 *
 *
 */
public class IPSecSite2SiteDAOImpl extends Hibernate4DAOImpl implements IPSecSite2SiteDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public IPSecSite2Site findByAuftragId(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(IPSecSite2Site.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, IPSecSite2Site.AUFTRAG_ID, auftragId);
        @SuppressWarnings("unchecked")
        List<IPSecSite2Site> result = crit.list();

        if (CollectionTools.isNotEmpty(result)) {
            if (result.size() == 1) {
                return result.get(0);
            }
            throw new IncorrectResultSizeDataAccessException(1, result.size());
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


