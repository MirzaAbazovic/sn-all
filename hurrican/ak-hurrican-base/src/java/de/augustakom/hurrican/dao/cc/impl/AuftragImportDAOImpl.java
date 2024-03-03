/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2005 11:08:02
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragImportDAO;
import de.augustakom.hurrican.model.cc.AuftragImport;
import de.augustakom.hurrican.model.cc.query.AuftragImportQuery;


/**
 * Hibernate DAO-Implementierung von <code>AuftragImportDAO</code>.
 *
 *
 */
public class AuftragImportDAOImpl extends Hibernate4DAOImpl implements AuftragImportDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.cc.AuftragImportDAO#findByQuery(de.augustakom.hurrican.model.cc.query.AuftragImportQuery)
     */
    public List<AuftragImport> findByQuery(final AuftragImportQuery query) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(AuftragImport.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "active", query.getActive());
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "kundeNo", query.getKundeNo());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "agName", query.getAgName());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "agVorname", query.getAgVorname());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "agStrasse", query.getAgStrasse());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "agOrt", query.getAgOrt());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "anschlussName", query.getAnschlussName());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "anschlussVorname", query.getAnschlussVorname());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "anschlussStrasse", query.getAnschlussStrasse());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "anschlussOrt", query.getAnschlussStrasse());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "altName", query.getAltName());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "altVorname", query.getAltVorname());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "altStrasse", query.getAltStrasse());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "altOrt", query.getAltOrt());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "rufnummer", query.getRufnummer());
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER_EQUAL, "importStatus", query.getMinStatus());
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "importStatus", query.getMaxStatus());
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER_EQUAL, "auftragsEingang", query.getMinIncome());
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "auftragsEingang", query.getMaxIncome());
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, "refId", query.getRefId());
        crit.addOrder(Order.asc("id"));

        return crit.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
