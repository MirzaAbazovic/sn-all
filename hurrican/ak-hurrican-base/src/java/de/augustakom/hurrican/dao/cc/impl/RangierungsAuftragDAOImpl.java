/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 16:31:06
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.RangierungsAuftragDAO;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.query.RangierungsAuftragBudgetQuery;
import de.augustakom.hurrican.model.cc.view.RangierungsAuftragBudgetView;


/**
 * Hibernate DAO-Implementierung von <code>RangierungsAuftragDAO</code>.
 *
 *
 */
public class RangierungsAuftragDAOImpl extends Hibernate4DAOImpl implements RangierungsAuftragDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.cc.RangierungsAuftragDAO#findUnfinishedRAs()
     */
    public List<RangierungsAuftrag> findUnfinishedRAs() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(RangierungsAuftrag.class);
        criteria.add(Restrictions.isNull("ausgefuehrtAm"));
        criteria.add(Restrictions.or(
                Restrictions.isNull("cancelled"),
                Restrictions.not(Restrictions.eq("cancelled", Boolean.TRUE))));
        @SuppressWarnings("unchecked")
        List<RangierungsAuftrag> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.RangierungsAuftragDAO#findRABudgetViews(de.augustakom.hurrican.model.cc.query.RangierungsAuftragBudgetQuery)
     */
    public List<RangierungsAuftragBudgetView> findRABudgetViews(final RangierungsAuftragBudgetQuery query) {
        final StringBuilder hql = new StringBuilder("select ra.id, ra.hvtStandortId, ra.faelligAm, ");
        hql.append("ra.ausgefuehrtAm, ia.id, ia.iaNummer, ib.budget, ib.createdAt, ib.closedAt ");
        hql.append("from ");
        hql.append(RangierungsAuftrag.class.getName()).append(" ra, ");
        hql.append(IA.class.getName()).append(" ia, ");
        hql.append(IABudget.class.getName()).append(" ib ");
        hql.append("where ra.id=ia.rangierungsAuftragId and ia.id=ib.iaId ");
        hql.append("and (ib.cancelled<> :cancelled or ib.cancelled is null) ");
        if (query.getHvtStandortId() != null) {
            hql.append("and ra.hvtStandortId= :hvtStdId ");
        }
        if (query.getFaelligVon() != null) {
            hql.append("and ra.faelligAm>= :faelligVon ");
        }
        if (query.getFaelligBis() != null) {
            hql.append("and ra.faelligAm<= :faelligBis ");
        }

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setBoolean("cancelled", true);
        if (query.getHvtStandortId() != null) {
            q.setLong("hvtStdId", query.getHvtStandortId());
        }
        if (query.getFaelligVon() != null) {
            q.setDate("faelligVon", query.getFaelligVon());
        }
        if (query.getFaelligBis() != null) {
            q.setDate("faelligBis", query.getFaelligBis());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            List<RangierungsAuftragBudgetView> retVal = new ArrayList<RangierungsAuftragBudgetView>();
            for (Object[] values : result) {
                RangierungsAuftragBudgetView view = new RangierungsAuftragBudgetView();
                view.setRangierungsAuftragId(ObjectTools.getLongSilent(values, 0));
                view.setHvtStandortId(ObjectTools.getLongSilent(values, 1));
                view.setFaelligAm(ObjectTools.getDateSilent(values, 2));
                view.setAusgefuehrtAm(ObjectTools.getDateSilent(values, 3));
                view.setInnenauftragsId(ObjectTools.getLongSilent(values, 4));
                view.setIaNummer(ObjectTools.getStringSilent(values, 5));
                view.setBudget(ObjectTools.getFloatSilent(values, 6));
                view.setBudgetCreatedAt(ObjectTools.getDateSilent(values, 7));
                view.setBudgetClosedAt(ObjectTools.getDateSilent(values, 8));
                retVal.add(view);
            }

            return retVal;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


