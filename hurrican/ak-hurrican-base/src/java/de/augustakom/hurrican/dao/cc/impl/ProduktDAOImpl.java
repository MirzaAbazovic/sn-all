/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 10:58:17
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateObjectRetrievalFailureException;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.ProduktDAO;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.TechLeistung;

/**
 * Hibernate DAO-Implementierung von <code>ProduktDAO</code>
 */
public class ProduktDAOImpl extends Hibernate4DAOImpl implements ProduktDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final Logger LOGGER = Logger.getLogger(ProduktDAOImpl.class);

    /**
     * @see de.augustakom.common.tools.dao.iface.StoreDAO#store(java.io.Serializable) Ueberschrieben, da der PK ueber
     * die GUI zugeordnet werden muss!
     */
    @Override
    public <T extends Serializable> T store(T toStore) {
        if (toStore instanceof Produkt) {
            Session session = sessionFactory.getCurrentSession();
            Produkt p = (Produkt) toStore;
            if (p.getId() != null) {
                Object existing = null;
                try {
                    existing = session.get(Produkt.class, p.getId());
                }
                catch (HibernateObjectRetrievalFailureException e) {
                    LOGGER.trace("store() - no object found");
                }

                if (existing == null) {
                    session.save(toStore);
                }
                else {
                    session.evict(existing); // geladenes Objekt muss aus Session entfernt werden!
                    session.update(toStore);
                }
            }
            else {
                super.store(toStore);
            }
        }
        else {
            super.store(toStore);
        }
        return toStore;
    }

    @Override
    public List<Produkt> find(final boolean onlyActual) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();

        Criteria criteria = session.createCriteria(Produkt.class);
        if (onlyActual) {
            criteria.add(Restrictions.le("gueltigVon", now));
            criteria.add(Restrictions.ge("gueltigBis", now));
        }
        criteria.addOrder(Order.asc("id"));
        @SuppressWarnings("unchecked")
        List<Produkt> result = criteria.list();
        return result;
    }

    @Override
    public <T> List<T> findAll(Class<T> type) {
        if (Produkt.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            List<T> result = find("from " + Produkt.class.getName() + " p order by p.anschlussart");
            return result;
        }
        else {
            return super.findAll(type);
        }
    }

    @Override
    public List<Produkt> findByParentPhysikTypIds(final List<Long> parentPhysikTypIds) {
        final StringBuilder hql = new StringBuilder("select p.id from ");
        hql.append(Produkt.class.getName()).append(" p, ");
        hql.append(Produkt2PhysikTyp.class.getName()).append(" p2t ");
        hql.append("where p.id=p2t.produktId and p2t.parentPhysikTypId in (:ids) ");
        hql.append("group by p.id ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameterList("ids", parentPhysikTypIds);

        @SuppressWarnings("unchecked")
        List<Long> result = query.list();
        if ((result != null) && (!result.isEmpty())) {
            List<Produkt> retVal = new ArrayList<>();
            for (Long pId : result) {
                retVal.add((Produkt) session.get(Produkt.class, pId));
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<Produkt2Produkt> findProdukt2Produkte(final Long prodIdSrc) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Produkt2Produkt.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "prodIdSrc", prodIdSrc);
        crit.addOrder(Order.asc("prodIdDest"));

        @SuppressWarnings("unchecked")
        List<Produkt2Produkt> result = crit.list();
        return result;
    }

    @Override
    public List<Produkt2Produkt> findProdukt2ProdukteByDest(final Long prodIdDest) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Produkt2Produkt.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "prodIdDest", prodIdDest);
        crit.addOrder(Order.asc("prodIdDest"));

        @SuppressWarnings("unchecked")
        List<Produkt2Produkt> result = crit.list();
        return result;
    }


    @Override
    public void deleteProd2Prod(final Long p2pId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Produkt2Produkt.class.getName());
        hql.append(" p where p.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, p2pId);
        query.executeUpdate();
    }

    @Override
    public Produkt findActualByProdId(final boolean onlyActual, final Long prodId) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();

        Criteria criteria = session.createCriteria(Produkt.class);
        criteria.add(Restrictions.eq("id", prodId));
        if (onlyActual) {
            criteria.add(Restrictions.le("gueltigVon", now));
            criteria.add(Restrictions.ge("gueltigBis", now));
        }
        @SuppressWarnings("unchecked")
        List<Produkt> result = criteria.list();
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public List<ProduktMapping> findProduktMappings(final Long prodId, final String mappingPartType) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(ProduktMapping.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "prodId", prodId);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "mappingPartType", mappingPartType);

        @SuppressWarnings("unchecked")
        List<ProduktMapping> result = criteria.list();
        return result;
    }

    @Override
    public List<Long> findProdIdsForProdMappings(final List<Long> produktMappingGroups) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(ProduktMapping.class);
        criteria.add(Restrictions.in(ProduktMapping.MAPPING_GROUP, produktMappingGroups));
        criteria.addOrder(Order.asc(ProduktMapping.PRIORITY));

        @SuppressWarnings("unchecked")
        List<ProduktMapping> result = criteria.list();

        List<Long> retVal = new ArrayList<>();
        for (ProduktMapping produktMapping : result) {
            if (!retVal.contains(produktMapping.getProdId())) {
                retVal.add(produktMapping.getProdId());
            }
        }
        return retVal;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Produkt> findProductsByTechLeistungType(String... techLeistungTypes) {
        final StringBuilder hql = new StringBuilder("select distinct p from ")
                .append(Produkt.class.getName()).append(" p, ")
                .append(Produkt2TechLeistung.class.getName()).append(" p2tl, ")
                .append(TechLeistung.class.getName()).append(" tl ")
                .append("where p.id=p2tl.prodId and p2tl.techLeistung.id = tl.id ")
                .append("and tl.typ in (:types) ")
                .append("and tl.gueltigVon <= :now and tl.gueltigBis >= :now ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameterList("types", techLeistungTypes);
        query.setParameter("now", new Date());

        return query.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
