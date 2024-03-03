/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2007 10:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.reporting.TxtBausteinGruppeDAO;
import de.augustakom.hurrican.model.reporting.Report2TxtBausteinGruppe;
import de.augustakom.hurrican.model.reporting.TxtBaustein2Gruppe;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;

/**
 * Hibernate DAO-Implementierung fuer <code>TxtBausteinGruppeDAO</code>
 *
 *
 */
public class TxtBausteinGruppeDAOImpl extends Hibernate4DAOImpl implements TxtBausteinGruppeDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(TxtBausteinGruppe.class.getName());
        hql.append(" g where g.id = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter(0, id);
        query.executeUpdate();
    }

    @Override
    public List<TxtBausteinGruppe> findAll4TxtBaustein(final Long bausteinOrigId) {
        if (bausteinOrigId == null) {
            return null;
        }

        final StringBuilder hql = new StringBuilder("select grp.id, grp.name, grp.mandatory, ");
        hql.append(" grp.description from ");
        hql.append(TxtBausteinGruppe.class.getName()).append(" grp, ");
        hql.append(TxtBaustein2Gruppe.class.getName()).append(" b2g ");
        hql.append(" where b2g.txtBausteinIdOrig = :Id");
        hql.append(" and grp.id = b2g.txtBausteinGruppeId");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("Id", bausteinOrigId);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();

        if (CollectionTools.isNotEmpty(result)) {
            List<TxtBausteinGruppe> li = new ArrayList<TxtBausteinGruppe>();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != null) {
                    Object[] values = result.get(i);
                    TxtBausteinGruppe view = new TxtBausteinGruppe();
                    view.setId(ObjectTools.getLongSilent(values, 0));
                    view.setName(ObjectTools.getStringSilent(values, 1));
                    view.setMandatory(ObjectTools.getBooleanSilent(values, 2));
                    view.setDescription(ObjectTools.getStringSilent(values, 3));
                    li.add(view);
                }
            }
            return li;
        }
        return null;
    }

    @Override
    public List<TxtBausteinGruppe> findAll4Report(final Long reportId) {
        if (reportId == null) {
            return null;
        }

        final StringBuilder hql = new StringBuilder("select grp.id, grp.name, grp.mandatory, ");
        hql.append(" grp.description from ");
        hql.append(TxtBausteinGruppe.class.getName()).append(" grp, ");
        hql.append(Report2TxtBausteinGruppe.class.getName()).append(" rep ");
        hql.append(" where rep.reportId = :Id");
        hql.append(" and grp.id = rep.txtBausteinGruppeId");
        hql.append(" order by rep.orderNo");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("Id", reportId);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();

        if (CollectionTools.isNotEmpty(result)) {
            List<TxtBausteinGruppe> li = new ArrayList<TxtBausteinGruppe>();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != null) {
                    Object[] values = result.get(i);
                    TxtBausteinGruppe view = new TxtBausteinGruppe();
                    view.setId(ObjectTools.getLongSilent(values, 0));
                    view.setName(ObjectTools.getStringSilent(values, 1));
                    view.setMandatory(ObjectTools.getBooleanSilent(values, 2));
                    view.setDescription(ObjectTools.getStringSilent(values, 3));
                    li.add(view);
                }
            }
            return li;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
