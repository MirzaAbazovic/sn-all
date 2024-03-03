/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2015
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.Auftrag2PeeringPartnerDAO;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;

/**
 *
 */
public class Auftrag2PeeringPartnerDAOImpl extends Hibernate4DAOImpl implements Auftrag2PeeringPartnerDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Auftrag2PeeringPartner> findByAuftragId(Long auftragId) {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(Auftrag2PeeringPartner.class)
                        .add(Property.forName(Auftrag2PeeringPartner.AUFTRAG_ID).eq(auftragId));

        @SuppressWarnings("unchecked")
        List<Auftrag2PeeringPartner> auftrag2PeeringPartner =
                (List<Auftrag2PeeringPartner>) criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
        return auftrag2PeeringPartner;
    }

    @Override
    public List<Auftrag2PeeringPartner> findValidAuftrag2PeeringPartner(Long ccAuftragId, Date validAt) {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(Auftrag2PeeringPartner.class)
                        .add(Property.forName(Auftrag2PeeringPartner.AUFTRAG_ID).eq(ccAuftragId))
                        .add(Property.forName(Auftrag2PeeringPartner.GUELTIG_VON).le(validAt))
                        .add(Property.forName(Auftrag2PeeringPartner.GUELTIG_BIS).gt(validAt));

        @SuppressWarnings("unchecked")
        List<Auftrag2PeeringPartner> auftrag2PeeringPartner =
                (List<Auftrag2PeeringPartner>) criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
        return auftrag2PeeringPartner;
    }

    @Override
    public List<Auftrag2PeeringPartner> findAuftrag2PeeringPartner(Long peeringPartnerId, Date validAt) {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(Auftrag2PeeringPartner.class)
                        .add(Property.forName(Auftrag2PeeringPartner.PEERING_PARTNER_ID).eq(peeringPartnerId))
                        .add(Property.forName(Auftrag2PeeringPartner.GUELTIG_VON).le(validAt))
                        .add(Property.forName(Auftrag2PeeringPartner.GUELTIG_BIS).gt(validAt));

        @SuppressWarnings("unchecked")
        List<Auftrag2PeeringPartner> auftrag2PeeringPartner =
                (List<Auftrag2PeeringPartner>) criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
        return auftrag2PeeringPartner;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
