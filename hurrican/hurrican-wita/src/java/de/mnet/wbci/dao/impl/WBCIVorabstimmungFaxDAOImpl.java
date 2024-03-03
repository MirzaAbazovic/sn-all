/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.02.2017
 */
package de.mnet.wbci.dao.impl;

import java.util.*;
import javax.inject.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.mnet.wbci.dao.WBCIVorabstimmungFaxDAO;
import de.mnet.wbci.model.RequestTyp;

public class WBCIVorabstimmungFaxDAOImpl extends Hibernate4DAOImpl implements WBCIVorabstimmungFaxDAO {

    @Inject
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void delete(Collection<WBCIVorabstimmungFax> ids) {
        final Session currentSession = getSessionFactory().getCurrentSession();
        ids.forEach(currentSession::delete);
        currentSession.flush();
    }

    @Override
    public WBCIVorabstimmungFax findByVorabstimmungsID(String id) {
        final Session currentSession = getSessionFactory().getCurrentSession();
        return (WBCIVorabstimmungFax) currentSession.createQuery(
                "FROM de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax wf WHERE wf.vorabstimmungsId=:id")
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public List<WBCIVorabstimmungFax> findAll(long auftragsId, RequestTyp requestTyp) {
        final Session currentSession = getSessionFactory().getCurrentSession();
        return (List<WBCIVorabstimmungFax>) currentSession
                .createQuery("FROM de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax wf WHERE wf.auftragId=:auftragsId AND "
                        + "wf.vorabstimmungsId LIKE :requestTyp")
                .setParameter("auftragsId", auftragsId)
                .setParameter("requestTyp", "%.%." + requestTyp.getPreAgreementIdCode() + "%")
                .list();
    }

    @Override
    public List<WBCIVorabstimmungFax> findAll(long auftragsId) {
        final Session currentSession = getSessionFactory().getCurrentSession();
        return (List<WBCIVorabstimmungFax>) currentSession.createQuery(
                "FROM de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax wf WHERE wf.auftragId=:auftragsId")
                .setParameter("auftragsId", auftragsId)
                .list();
    }


}