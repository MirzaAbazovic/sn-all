/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 08:45:51
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.HibernateInClauseHelper;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.billing.OeDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.OE;


/**
 * Hibernate DAO-Implemntierung zur Verwaltung von Objekten des Typs <code>OE</code>
 *
 *
 */
public class OeDAOImpl implements OeDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public OE findByOeNoOrig(final Long oeNoOrig) {
        StringBuilder hql = new StringBuilder("select oe.oeNo, oe.oeNoOrig, oe.produktCode, oe.name, ");
        hql.append("oe.auftragZusatz, langs.name ");
        hql.append("from ").append(OE.class.getName()).append(" oe left join oe.oeLanguages langs ");
        hql.append("where oe.oeNoOrig= :oeNoOrig and oe.histStatus= :histStatus ");
        hql.append("and (trim(langs.language)= :langSign or langs.language is null)");

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setLong("oeNoOrig", oeNoOrig);
        query.setString("histStatus", BillingConstants.HIST_STATUS_AKT);
        query.setString("langSign", BillingConstants.LANG_CODE_GERMAN);

        List<?> result = query.list();
        if ((result != null) && (result.size() == 1)) {
            Object[] values = (Object[]) result.get(0);
            OE retVal = new OE();
            retVal.setOeNo(ObjectTools.getLongSilent(values, 0));
            retVal.setOeNoOrig(ObjectTools.getLongSilent(values, 1));
            retVal.setProduktCode(ObjectTools.getStringSilent(values, 2));
            retVal.setName(ObjectTools.getStringSilent(values, 3));
            retVal.setAuftragZusatz(ObjectTools.getStringSilent(values, 4));
            retVal.setRechnungstext(ObjectTools.getStringSilent(values, 5));

            return retVal;
        }
        return null;
    }

    @Override
    public Map<Long, String> findProduktNamen4Auftraege(List<Long> auftragNoOrigs) {
        StringBuilder hql = new StringBuilder("select o.name, a.auftragNoOrig from ");
        hql.append(OE.class.getName()).append(" o, ");
        hql.append(BAuftrag.class.getName());
        hql.append(" a where a.histLast= :hLast ");
        hql.append(" and a.oeNoOrig=o.oeNoOrig and o.histLast= :hLast and %s");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("hLast", Boolean.TRUE);

        List<Object[]> result = HibernateInClauseHelper.list(sessionFactory, hql.toString(),
                params, "a.auftragNoOrig", auftragNoOrigs);

        if (result != null) {
            Map<Long, String> retVal = new HashMap<Long, String>();
            for (int i = 0; i < result.size(); i++) {
                Object[] values = result.get(i);
                String pname = ObjectTools.getStringSilent(values, 0);
                Long aId = ObjectTools.getLongSilent(values, 1);
                retVal.put(aId, pname);
            }
            return retVal;
        }
        return new HashMap<Long, String>();
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.OeDAO#findProduktName4Auftrag(java.lang.Long)
     */
    @Override
    public String findProduktName4Auftrag(final Long auftragNoOrig) {
        final StringBuilder hql = new StringBuilder("select o.name, a.auftragNoOrig from ");
        hql.append(OE.class.getName()).append(" o, ");
        hql.append(BAuftrag.class.getName());
        hql.append(" a where a.auftragNoOrig= :aNoOrig  and a.histLast= :hLast ");
        hql.append(" and a.oeNoOrig=o.oeNoOrig and o.histLast= :hLast");

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setLong("aNoOrig", auftragNoOrig);
        query.setBoolean("hLast", Boolean.TRUE);
        List<?> result = query.list();
        if ((result != null) && (result.size() == 1)) {
            Object[] values = (Object[]) result.get(0);
            String retVal = ObjectTools.getStringSilent(values, 0);
            return retVal;
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.OeDAO#findVaterOeNoOrig4Auftrag(java.lang.Long)
     */
    @Override
    public Long findVaterOeNoOrig4Auftrag(final Long auftragNoOrig) {
        final StringBuilder hql = new StringBuilder("select o.vaterOeNoOrig, a.auftragNoOrig from ");
        hql.append(OE.class.getName()).append(" o, ");
        hql.append(BAuftrag.class.getName());
        hql.append(" a where a.auftragNoOrig= :aNoOrig  and a.histLast= :hLast ");
        hql.append(" and a.oeNoOrig=o.oeNoOrig and o.histLast= :hLast");

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setLong("aNoOrig", auftragNoOrig);
        query.setBoolean("hLast", Boolean.TRUE);
        List<?> result = query.list();
        if ((result != null) && (result.size() == 1)) {
            Object[] values = (Object[]) result.get(0);
            return ObjectTools.getLongSilent(values, 0);
        }
        return 0L;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.OeDAO#findAllByOetyp(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<OE> findAllByOetyp(final String oeTyp) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OE.class);
        criteria.add(Restrictions.sqlRestriction("RTRIM(OETYP)=?", oeTyp, new StringType()));
        criteria.add(Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_AKT));

        return criteria.list();
    }


}


