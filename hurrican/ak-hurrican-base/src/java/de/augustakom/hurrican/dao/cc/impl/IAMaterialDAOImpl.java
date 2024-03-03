/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 15:59:07
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.IAMaterialDAO;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;


/**
 * Hibernate DAO-Implementierung von <code>IAMaterialDAO</code>.
 *
 *
 */
public class IAMaterialDAOImpl extends Hibernate4DAOImpl implements IAMaterialDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<IAMaterialEntnahmeArtikel> findArtikel4MatEntnahme(final Long matEntId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(IAMaterialEntnahmeArtikel.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "materialEntnahmeId", matEntId);
        criteria.add(Restrictions.isNull("removedAt"));

        return criteria.list();
    }

    @Override
    public IAMaterial findMaterial(final String materialNr) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(IAMaterial.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "materialNr", materialNr);

        List<IAMaterial> result = criteria.list();
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public void deleteMaterials() {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("delete " + IAMaterial.class.getName()).executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


