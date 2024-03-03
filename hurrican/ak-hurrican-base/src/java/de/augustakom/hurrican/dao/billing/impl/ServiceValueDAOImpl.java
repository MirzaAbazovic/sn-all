/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2006 08:17:38
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.hurrican.dao.billing.ServiceValueDAO;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Hibernate DAO-Implementierung von <code>ServiceValueDAO</code>.
 *
 *
 */
public class ServiceValueDAOImpl implements ServiceValueDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.billing.ServiceValueDAO#findServiceValue(java.lang.Long, java.lang.String)
     */

    public ServiceValue findServiceValue(final Long leistungNo, final String value) throws FindException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ServiceValue.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "leistungNo", leistungNo);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "value", value);
        List result = criteria.list();
        if (result != null) {
            if (result.size() == 1) {
                return (ServiceValue) result.get(0);
            }
            else if (result.size() > 1) {
                throw new FindException("Leistungs-Wert konnte nicht eindeutig ermittelt werden! " +
                        "Anzahl gefundener Leistungs-Werte: " + result.size());
            }
        }
        return null;
    }

}


