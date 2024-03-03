/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 09:05:39
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.BAVerlaufDefinitionDAO;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.model.cc.BAVerlaufAG2Produkt;
import de.augustakom.hurrican.model.cc.BAVerlaufAenderungGruppe;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatz;


/**
 * Hibernate DAO-Implementierung von BAVerlaufDefinitionDAO.
 *
 *
 */
public class BAVerlaufDefinitionDAOImpl extends HurricanHibernateDaoImpl implements BAVerlaufDefinitionDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<BAVerlaufAG2Produkt> findBAVAG4Produkt(Long produktId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(BAVerlaufAG2Produkt.class.getName());
        hql.append(" as ag2p where ag2p.produktId=?");

        return find(hql.toString(), produktId);
    }

    @Override
    public void deleteBAVAG4Produkt(final Long produktId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from " + BAVerlaufAG2Produkt.class.getName() + " b2p where b2p.produktId=?");
        query.setLong(0, produktId);
        query.executeUpdate();
    }

    @Override
    public void saveBAVerlaufAG2Produkte(List<BAVerlaufAG2Produkt> baVerlaufAG2Produkte) {
        if (baVerlaufAG2Produkte != null) {
            for (BAVerlaufAG2Produkt toSave : baVerlaufAG2Produkte) {
                sessionFactory.getCurrentSession().save(toSave);
            }
        }
    }

    @Override
    public List<BAVerlaufAnlass> findPossibleBAAnlaesse4Produkt(Long produktId, Boolean onlyAuftragsart) {
        StringBuilder hql = new StringBuilder();
        hql.append("select bva.id from ");
        hql.append(BAVerlaufAnlass.class.getName()).append(" bva, ");
        hql.append(BAVerlaufAenderungGruppe.class.getName()).append(" g, ");
        hql.append(BAVerlaufAG2Produkt.class.getName()).append(" p2g ");
        hql.append("where p2g.produktId=? and p2g.baVerlaufAenderungGruppeId=g.id ");
        hql.append("and g.id=bva.baVerlGruppe and bva.akt=? ");
        if (onlyAuftragsart != null) {
            hql.append("and bva.auftragsart=? ");
        }
        hql.append("order by bva.name");

        List<Object> params = new ArrayList<Object>();
        params.add(produktId);
        params.add(Boolean.TRUE);

        if (onlyAuftragsart != null) {
            params.add(onlyAuftragsart);
        }

        List<Long> result = find(hql.toString(), params.toArray());

        List<BAVerlaufAnlass> retVal = new ArrayList<BAVerlaufAnlass>();
        if (result != null) {
            for (Long bvaId : result) {
                retVal.add(findById(bvaId, BAVerlaufAnlass.class));
            }
        }

        return retVal;
    }

    @Override
    public List<BAVerlaufAnlass> findBAVerlaufAnlaesse(final Boolean onlyAct, final Boolean onlyAuftragsart) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(BAVerlaufAnlass.class);
        if (onlyAct != null) {
            criteria.add(Restrictions.eq("akt", onlyAct));
        }
        if (onlyAuftragsart != null) {
            criteria.add(Restrictions.eq("auftragsart", onlyAct));
        }
        criteria.addOrder(Order.asc("positionNo"));
        return criteria.list();
    }

    @Override
    public List<BAVerlaufConfig> findBAVerlaufConfigs(final Long anlass, final Long prodId, final boolean forceProdId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(BAVerlaufConfig.class);
        criteria.add(Restrictions.eq("baVerlAnlass", anlass));
        if ((prodId != null) || forceProdId) {
            criteria.add(Restrictions.eq("prodId", prodId));
        }
        criteria.add(Restrictions.gt("gueltigBis", new Date()));

        return criteria.list();
    }

    @Override
    public AbstractCCHistoryModel update4History(AbstractCCHistoryModel obj4History, Serializable id, Date gueltigBis) {
        if (obj4History instanceof BAVerlaufConfig) {
            return update4History(obj4History, new BAVerlaufConfig(), id, gueltigBis);
        }
        else if (obj4History instanceof BAVerlaufZusatz) {
            return update4History(obj4History, new BAVerlaufZusatz(), id, gueltigBis);
        }
        else {
            throw new IllegalArgumentException("obj4History muss vom Typ BAVerlaufConfig oder BAVerlaufZusatz sein!");
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}



