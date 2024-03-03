/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2011 09:05:42
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragHousingKeyDAO;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;


public class AuftragHousingKeyDAOImpl extends Hibernate4DAOImpl implements AuftragHousingKeyDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<AuftragHousingKeyView> findAuftragHousingKeys(Long auftragId) {
        return findAuftragHousingKeysByID(auftragId, null);
    }

    private List<AuftragHousingKeyView> findAuftragHousingKeysByID(final Long auftragId, final Long auftragHousingId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(AuftragHousingKey.class);
        Date now = new Date();
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);
        if (auftragId != null) {
            CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "auftragId", auftragId);
        }
        if (auftragHousingId != null) {
            CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "auftragHousingId", auftragHousingId);
        }

        @SuppressWarnings("unchecked")
        List<AuftragHousingKey> result = crit.list();

        List<AuftragHousingKeyView> retVal = new ArrayList<AuftragHousingKeyView>();
        for (AuftragHousingKey key : result) {
            retVal.addAll(AuftragHousingKeyView.createAuftragHousingKeyView(key));
        }
        return retVal;
    }

    @Override
    public void deleteById(final Serializable id) {
        Session session = sessionFactory.getCurrentSession();
        AuftragHousingKey key = (AuftragHousingKey) session.get(AuftragHousingKey.class, id);
        session.delete(key);
        session.flush();
    }

    @Override
    public void deleteTransponderGroup(final Long transponderGroupId) {
        Session session = sessionFactory.getCurrentSession();
        TransponderGroup toDelete = (TransponderGroup) session.get(TransponderGroup.class, transponderGroupId);
        session.delete(toDelete);
        session.flush();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

