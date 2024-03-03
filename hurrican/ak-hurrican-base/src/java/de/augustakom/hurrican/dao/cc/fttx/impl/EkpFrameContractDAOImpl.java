/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 16:33:01
 */
package de.augustakom.hurrican.dao.cc.fttx.impl;

import java.time.*;
import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.fttx.EkpFrameContractDAO;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;

/**
 * Hibernate DAO Implementierung von {@link EkpFrameContractDAO}
 */
public class EkpFrameContractDAOImpl extends Hibernate4DAOImpl implements EkpFrameContractDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public EkpFrameContract findEkpFrameContract(String ekpId, String frameContractId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(EkpFrameContract.class);
        criteria.add(Property.forName(EkpFrameContract.EKP_ID).eq(ekpId));
        criteria.add(Property.forName(EkpFrameContract.FRAME_CONTRACT_ID).eq(frameContractId));
        @SuppressWarnings("unchecked")
        List<EkpFrameContract> ekpFrameContracts = (List<EkpFrameContract>) criteria.getExecutableCriteria(
                sessionFactory.getCurrentSession()).list();
        return Iterables.getFirst(ekpFrameContracts, null);
    }

    @Override
    public Auftrag2EkpFrameContract findAuftrag2EkpFrameContract(Long auftragId, LocalDate validAt) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Auftrag2EkpFrameContract.class);
        criteria.add(Property.forName(Auftrag2EkpFrameContract.AUFTRAG_ID).eq(auftragId));
        criteria.add(Property.forName(Auftrag2EkpFrameContract.ASSIGNED_FROM).le(validAt));
        criteria.add(Property.forName(Auftrag2EkpFrameContract.ASSIGNED_TO).gt(validAt));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        @SuppressWarnings("unchecked")
        List<Auftrag2EkpFrameContract> auftrag2Ekp = (List<Auftrag2EkpFrameContract>) criteria.getExecutableCriteria(
                sessionFactory.getCurrentSession()).list();
        if (auftrag2Ekp != null) {
            if (auftrag2Ekp.size() > 1) {
                throw new IncorrectResultSizeDataAccessException("More than one EKP assignment to order found!", 1,
                        auftrag2Ekp.size());
            }
            return Iterables.getFirst(auftrag2Ekp, null);
        }
        return null;
    }

    @Override
    public void deleteAuftrag2EkpFrameContract(Long id) {
        sessionFactory.getCurrentSession().delete(findById(id, Auftrag2EkpFrameContract.class));
    }

    @Override
    public void delete(EkpFrameContract ekpFrameContract) {
        sessionFactory.getCurrentSession().delete(ekpFrameContract);
    }

    @Override
    public boolean hasAuftrag2EkpFrameContract(List<EkpFrameContract> ekpFrameContracts) {
        if (ekpFrameContracts.isEmpty()) {
            return false;
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(Auftrag2EkpFrameContract.class);
        criteria.add(Property.forName(Auftrag2EkpFrameContract.EKP_FRAME_CONTRACT).in(ekpFrameContracts));
        criteria.add(Property.forName(Auftrag2EkpFrameContract.ASSIGNED_TO).gt(LocalDate.now()));
        criteria.setProjection(Projections.rowCount());

        @SuppressWarnings("unchecked")
        List<Long> result = (List<Long>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return result.get(0) > 0L;
    }

    @Override
    public List<EkpFrameContract> findAll() {
        return this.findAll(EkpFrameContract.class);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
