/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2004 07:48:34
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKCompBehaviorDAO;
import de.augustakom.authentication.model.AKCompBehavior;
import de.augustakom.authentication.model.AKGUIComponent;
import de.augustakom.authentication.model.view.AKCompBehaviorView;
import de.augustakom.common.tools.lang.ObjectTools;


/**
 * Hibernate DAO-Implementierung von AKCompBehaviorDAO
 *
 *
 */
public class AKCompBehaviorDAOImpl implements AKCompBehaviorDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKCompBehaviorDAO#save(de.augustakom.authentication.model.AKCompBehavior)
     */
    public void save(AKCompBehavior toSave) {
        sessionFactory.getCurrentSession().saveOrUpdate(toSave);
    }

    /**
     * @see de.augustakom.authentication.dao.AKCompBehaviorDAO#delete(de.augustakom.authentication.model.AKCompBehavior)
     */
    public void delete(AKCompBehavior toDelete) {
        sessionFactory.getCurrentSession().delete(toDelete);
    }

    /**
     * @see de.augustakom.authentication.dao.AKCompBehaviorDAO#find(java.lang.Long, java.lang.Long)
     */
    public AKCompBehavior find(final Long compId, final Long roleId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AKCompBehavior.class);
        criteria.add(Restrictions.eq("componentId", compId));
        criteria.add(Restrictions.eq("roleId", roleId));
        @SuppressWarnings("unchecked")
        List<AKCompBehavior> result = criteria.list();

        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    /**
     * @see de.augustakom.authentication.dao.AKGUIComponentDAO#findBehaviors(java.lang.Long, java.lang.Long[])
     */
    public List<AKCompBehavior> findBehaviors(final Long compId, final Long[] roleIds) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AKCompBehavior.class);
        criteria.add(Restrictions.eq("componentId", compId));
        criteria.add(Restrictions.in("roleId", roleIds));

        criteria.setCacheable(true);

        @SuppressWarnings("unchecked")
        List<AKCompBehavior> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKCompBehaviorDAO#findBehaviorViews(java.util.List, java.util.List,
     * java.lang.Long, java.lang.Long[])
     */
    public List<AKCompBehaviorView> findBehaviorViews(final List<String> compNames, final List<String> parentNames,
            final Long applicationId, final Long[] roleIds) {
        final boolean filter4Parents = ((parentNames != null) && (!parentNames.isEmpty()));

        final StringBuilder hql = new StringBuilder("select gc.id, gc.name, gc.parent, ");
        hql.append(" cb.id, cb.roleId, cb.visible, cb.executable from ");
        hql.append(AKGUIComponent.class.getName()).append("  gc, ");
        hql.append(AKCompBehavior.class.getName()).append(" cb ");
        hql.append(" where gc.id=cb.componentId and gc.name in (:compNames) ");
        if (filter4Parents) {
            hql.append(" and gc.parent in (:parentNames) ");
        }
        hql.append(" and gc.applicationId= :appId and cb.roleId in (:roleIds) ");

        Query q = sessionFactory.getCurrentSession().createQuery(hql.toString());
        q.setParameterList("compNames", compNames);
        if (filter4Parents) {
            q.setParameterList("parentNames", parentNames);
        }
        q.setLong("appId", applicationId);
        q.setParameterList("roleIds", roleIds);

        q.setCacheable(true);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            List<AKCompBehaviorView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                AKCompBehaviorView view = new AKCompBehaviorView();
                view.setComponentId(ObjectTools.getLongSilent(values, 0));
                view.setCompName(ObjectTools.getStringSilent(values, 1));
                view.setCompParent(ObjectTools.getStringSilent(values, 2));
                view.setId(ObjectTools.getLongSilent(values, 3));
                view.setRoleId(ObjectTools.getLongSilent(values, 4));
                view.setVisible(ObjectTools.getBooleanTypeSilent(values, 5));
                view.setExecutable(ObjectTools.getBooleanTypeSilent(values, 6));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

}


