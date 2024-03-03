/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2004 15:40:20
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKGUIComponentDAO;
import de.augustakom.authentication.model.AKGUIComponent;


/**
 * Hibernate DAO-Implementierung fuer AKGUIComponentDAO.
 *
 *
 */
public class AKGUIComponentDAOImpl implements AKGUIComponentDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKGUIComponentDAO#save(de.augustakom.authentication.model.AKGUIComponent)
     */
    public void save(AKGUIComponent toSave) {
        sessionFactory.getCurrentSession().saveOrUpdate(toSave);
    }

    /**
     * @see de.augustakom.authentication.dao.AKGUIComponentDAO#delete(de.augustakom.authentication.model.AKGUIComponent)
     */
    public void delete(AKGUIComponent toDelete) {
        sessionFactory.getCurrentSession().delete(toDelete);
    }

    /**
     * @see de.augustakom.authentication.dao.AKGUIComponentDAO#find(java.lang.String, java.lang.String, java.lang.Long)
     */
    public AKGUIComponent find(final String name, final String parent, final Long applicationId) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(AKGUIComponent.class.getName());
        hql.append(" gui where gui.name= :name and gui.parent= :parent and gui.applicationId= :id");

        Query q = sessionFactory.getCurrentSession().createQuery(hql.toString());
        q.setString("name", name);
        q.setString("parent", parent);
        q.setLong("id", applicationId);

        q.setCacheable(true);

        @SuppressWarnings("unchecked")
        List<AKGUIComponent> result = q.list();
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

}


