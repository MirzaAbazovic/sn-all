/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 11:28:51
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.math.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.PhysikTypDAO;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;


/**
 * Hibernate DAO-Implementierung fuer Objekte des Typs <code>PhysikTyp</code>
 *
 *
 */
public class PhysikTypDAOImpl extends Hibernate4DAOImpl implements PhysikTypDAO, StoreDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteP2PTById(final Long p2ptId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from " + Produkt2PhysikTyp.class.getName() + " p2pt where p2pt.id=?");
        query.setLong(0, p2ptId);
        query.executeUpdate();
    }

    @Override
    public void saveP2PTs(List<Produkt2PhysikTyp> toSave) {
        if (toSave != null) {
            for (Produkt2PhysikTyp p2pt : toSave) {
                store(p2pt);
            }
        }
    }

    @Override
    public List<Object[]> findAllPhysiktypKombinationen() {
        StringBuilder hql = new StringBuilder("select distinct p2pt.physikTypId, ");
        hql.append("p2pt.physikTypAdditionalId from ");
        hql.append(Produkt2PhysikTyp.class.getName()).append(" p2pt");

        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString());
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.PhysikTypDAO#findP2PTsByQuery(de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery)
     */
    @Override
    public List<Produkt2PhysikTyp> findP2PTsByQuery(Produkt2PhysikTypQuery query) {
        if ((query == null) || query.isEmpty()) { return null; }

        List<Long> params = new ArrayList<>();

        StringBuilder hql = new StringBuilder("select p2pt.id, p2pt.produktId, ");
        hql.append(" p2pt.physikTypId, p2pt.virtuell, p2pt.parentPhysikTypId from ");
        hql.append(Produkt2PhysikTyp.class.getName()).append(" p2pt, ");
        hql.append(PhysikTyp.class.getName()).append(" pt where pt.id=p2pt.physikTypId ");
        if (query.getProduktId() != null) {
            hql.append(" and p2pt.produktId=? ");
            params.add(query.getProduktId());
        }
        if (query.getPhysikTypId() != null) {
            hql.append(" and p2pt.physikTypId=? ");
            params.add(query.getPhysikTypId());
        }
        if (query.getParentPhysikTypId() != null) {
            hql.append(" and p2pt.parentPhysikTypId=? ");
            params.add(query.getParentPhysikTypId());
        }
        if (query.getHvtTechnikId() != null) {
            hql.append(" and pt.hvtTechnikId=? and p2pt.physikTypId=pt.id");
            params.add(query.getHvtTechnikId());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString(), params.toArray());
        if ((result != null) && (!result.isEmpty())) {
            List<Produkt2PhysikTyp> retVal = new ArrayList<>();
            for (Object[] values : result) {
                Produkt2PhysikTyp p = new Produkt2PhysikTyp();
                p.setId(ObjectTools.getLongSilent(values, 0));
                p.setProduktId(ObjectTools.getLongSilent(values, 1));
                p.setPhysikTypId(ObjectTools.getLongSilent(values, 2));
                p.setVirtuell(ObjectTools.getBooleanSilent(values, 3));
                p.setParentPhysikTypId(ObjectTools.getLongSilent(values, 4));
                retVal.add(p);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<PhysikTyp> find4ParentPhysiktypen(List<Long> parentPhysiktypIds) {
        StringBuilder sql = new StringBuilder("select p.ID from ");
        sql.append(" T_PRODUKT_2_PHYSIKTYP p2p inner join T_PHYSIKTYP p on p.ID=p2p.PHYSIKTYP_ADDITIONAL ");
        sql.append(" where p2p.PHYSIKTYP in ( ");
        for (int i = 0; i < parentPhysiktypIds.size(); i++) {
            if (i > 0) { sql.append(","); }
            sql.append("?");
        }
        sql.append(") group by p.ID");

        Type[] types = new Type[parentPhysiktypIds.size()];
        for (int i = 0; i < types.length; i++) {
            types[i] = new LongType();
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(parentPhysiktypIds.toArray(), types);
        @SuppressWarnings("rawtypes")
        List<BigDecimal> result = sqlQuery.list();
        if (result != null) {
            List<PhysikTyp> retVal = new ArrayList<>();
            for (BigDecimal id : result) {
                retVal.add(findById(id.longValue(), PhysikTyp.class));
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<Long> findPhysiktypen4Auftrag(Long auftragId) {
        StringBuilder sql = new StringBuilder("select ra.PHYSIK_TYP as PT_A, rb.PHYSIK_TYP as PT_B ");
        sql.append("from T_AUFTRAG_TECHNIK at ");
        sql.append("left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append("left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append("left join T_RANGIERUNG ra on esa.RANGIER_ID=ra.RANGIER_ID ");
        sql.append("left join T_RANGIERUNG rb on esb.RANGIER_ID=rb.RANGIER_ID ");
        sql.append("where at.GUELTIG_VON<=:now and at.GUELTIG_BIS>:now ");
        sql.append("and (ra.GUELTIG_VON is null or (ra.GUELTIG_VON<=:now and ra.GUELTIG_BIS>:now)) ");
        sql.append("and (rb.GUELTIG_VON is null or (rb.GUELTIG_VON<=:now and rb.GUELTIG_BIS>:now)) ");
        sql.append("and esa.ES_TYP=:esA and esb.ES_TYP=:esB and at.AUFTRAG_ID=:auftragId ");

        Date now = new Date();
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setDate("now", now);
        sqlQuery.setParameter("esA", Endstelle.ENDSTELLEN_TYP_A);
        sqlQuery.setParameter("esB", Endstelle.ENDSTELLEN_TYP_B);
        sqlQuery.setLong("auftragId", auftragId);
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<Long> retVal = new ArrayList<>();
            for (Object[] values : result) {
                Long ptA = ObjectTools.getLongSilent(values, 0);
                if (ptA != null) {
                    retVal.add(ptA);
                }

                Long ptB = ObjectTools.getLongSilent(values, 1);
                if (ptB != null) {
                    retVal.add(ptB);
                }
            }
            return retVal;
        }
        return null;
    }

    @Override
    public List<Produkt2PhysikTyp> findSimpleP2PTs4Produkt(Long produktId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Produkt2PhysikTyp.class);
        criteria.add(Property.forName("produktId").eq(produktId));
        criteria.add(Property.forName("physikTypId").isNotNull());
        criteria.add(Property.forName("physikTypAdditionalId").isNull());
        criteria.add(Property.forName("parentPhysikTypId").isNull());
        @SuppressWarnings("unchecked")
        List<Produkt2PhysikTyp> list = criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return list;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


