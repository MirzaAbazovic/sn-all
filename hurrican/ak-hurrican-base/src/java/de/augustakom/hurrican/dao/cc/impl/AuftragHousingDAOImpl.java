/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:56:35
 */

package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragHousingDAO;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;


public class AuftragHousingDAOImpl extends Hibernate4DAOImpl implements AuftragHousingDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragHousing findAuftragHousing(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(AuftragHousing.class);
        Date now = new Date();
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "auftragId", auftragId);

        @SuppressWarnings("unchecked")
        List<AuftragHousing> result = crit.list();
        return ((result != null) && (!result.isEmpty())) ? result.get(result.size() - 1) : null;
    }

    @Override
    public HousingBuilding findHousingBuilding4Auftrag(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria housingCriteria = session.createCriteria(AuftragHousing.class);
        CriteriaHelper.addExpression(housingCriteria, CriteriaHelper.EQUAL, "auftragId", auftragId);
        @SuppressWarnings("unchecked")
        List<AuftragHousing> housingResult = housingCriteria.list();
        if (CollectionTools.isNotEmpty(housingResult)) {
            Criteria buildingCriteria = session.createCriteria(HousingBuilding.class);
            CriteriaHelper.addExpression(buildingCriteria, CriteriaHelper.EQUAL, "id", housingResult.get(0).getBuildingId());
            @SuppressWarnings("unchecked")
            List<HousingBuilding> buildingResult = buildingCriteria.list();
            if (CollectionTools.isNotEmpty(buildingResult)) {
                return buildingResult.get(0);
            }
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
