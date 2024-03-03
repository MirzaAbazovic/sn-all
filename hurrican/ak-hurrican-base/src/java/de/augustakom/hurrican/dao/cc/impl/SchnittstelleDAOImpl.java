/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2004 07:53:15
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.SchnittstelleDAO;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Schnittstelle;
import de.augustakom.hurrican.model.cc.Schnittstelle;


/**
 * Hibernate DAO-Implementierung von SchnittstelleDAO.
 *
 *
 */
public class SchnittstelleDAOImpl extends Hibernate4DAOImpl implements SchnittstelleDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Produkt2Schnittstelle> findSchnittstellenMappings4Produkt(Long produktId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(Produkt2Schnittstelle.class.getName());
        hql.append(" p2s where p2s.produktId=?");

        return find(hql.toString(), produktId);
    }

    @Override
    public List<Schnittstelle> findSchnittstellen4Produkt(Long produktId) {
        StringBuilder hql = new StringBuilder();
        hql.append("select s from ");
        hql.append(Schnittstelle.class.getName());
        hql.append(" s, ");
        hql.append(Produkt.class.getName());
        hql.append(" p, ");
        hql.append(Produkt2Schnittstelle.class.getName());
        hql.append(" p2s where p.id=? and p.id=p2s.produktId and p2s.schnittstelleId=s.id order by s.schnittstelle");

        return find(hql.toString(), produktId);
    }

    @Override
    public Schnittstelle findByEsId(Long esId) {
        StringBuilder hql = new StringBuilder("select s.id, s.schnittstelle from ");
        hql.append(Schnittstelle.class.getName()).append(" s, ");
        hql.append(EndstelleLtgDaten.class.getName()).append(" ltg ");
        hql.append(" where ltg.schnittstelleId=s.id and ltg.endstelleId=? ");
        hql.append(" and ltg.gueltigVon<=? and ltg.gueltigBis>?");

        Date now = new Date();
        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString(), new Object[] { esId, now, now });

        if ((result != null) && (result.size() == 1)) {
            Object[] values = result.get(0);
            Schnittstelle schnittstelle = new Schnittstelle();
            schnittstelle.setId(ObjectTools.getLongSilent(values, 0));
            schnittstelle.setSchnittstelle(ObjectTools.getStringSilent(values, 1));
            return schnittstelle;
        }

        return null;
    }

    @Override
    public void deleteSchnittstellen4Produkt(final Long produktId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Produkt2Schnittstelle.class.getName());
        hql.append(" p2s where p2s.produktId=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, produktId);
        query.executeUpdate();
    }

    @Override
    public void saveSchnittstellen4Produkt(List<Produkt2Schnittstelle> produkt2Schnittstelle) {
        if (produkt2Schnittstelle != null) {
            for (Produkt2Schnittstelle toSave : produkt2Schnittstelle) {
                sessionFactory.getCurrentSession().save(toSave);
            }
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


