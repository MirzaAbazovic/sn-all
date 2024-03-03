/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.dao.cc.ffm.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.ffm.FfmDAO;
import de.augustakom.hurrican.dao.cc.impl.HurricanHibernateDaoImpl;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping;

public class FfmDAOImpl extends HurricanHibernateDaoImpl implements FfmDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<FfmQualificationMapping> findQualificationsByLeistung(Long techLeistungId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping qm "
                + "where qm.techLeistungId=:leistungId");

        query.setLong("leistungId", techLeistungId);

        return query.list();
    }

    @Override
    public List<FfmQualificationMapping> findQualificationsByProduct(Long productId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping qm "
                + "where qm.productId=:productId");

        query.setLong("productId", productId);

        return query.list();
    }

    @Override
    public List<FfmQualificationMapping> findQualifications4Vpn() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping qm "
                + "where qm.vpn=:vpn");

        query.setBoolean("vpn", true);

        return query.list();
    }

    @Override
    public List<FfmQualificationMapping> findQualificationsByStandortRef(Long standortRefId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping qm "
                + "where qm.standortRefId=:standortRefId");

        query.setLong("standortRefId", standortRefId);

        return query.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
