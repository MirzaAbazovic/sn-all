/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2007 11:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.reporting.TxtBausteinDAO;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBaustein2Gruppe;

/**
 * Hibernate DAO-Implementierung fuer <code>TxtBausteinDAO</code>
 *
 *
 */
public class TxtBausteinDAOImpl extends Hibernate4DAOImpl implements TxtBausteinDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public <T extends Serializable> T store(T toStore) {
        if (toStore instanceof TxtBaustein) {
            // Falls id == null handelt es sich um einen neuen Datensatz,
            // => Hibernate.save
            if (((TxtBaustein) toStore).getId() == null) {
                Long id = getNewId();
                ((TxtBaustein) toStore).setId(id);
                if (((TxtBaustein) toStore).getIdOrig() == null) {
                    ((TxtBaustein) toStore).setIdOrig(((TxtBaustein) toStore).getId());
                }
                sessionFactory.getCurrentSession().save(toStore);
            }
            // Update eines bestehenden Datensatzes
            else {
                if (((TxtBaustein) toStore).getIdOrig() == null) {
                    ((TxtBaustein) toStore).setIdOrig(((TxtBaustein) toStore).getId());
                }
                sessionFactory.getCurrentSession().update(toStore);
            }
        }
        return toStore;
    }

    /* Funktion liefert eine neue Id */
    public Long getNewId() {
        final StringBuilder hql = new StringBuilder("select max(txt.id) from ");
        hql.append(TxtBaustein.class.getName()).append(" txt");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if ((result != null) && (result.size() == 1)) {
            return result.get(0) + 1;
        }

        return null;
    }

    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(TxtBaustein.class.getName());
        hql.append(" r where r.id = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter(0, id);
        query.executeUpdate();
    }

    @Override
    public List<TxtBaustein> findAllValidTxtBausteine() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TxtBaustein.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.GREATER_EQUAL, "gueltigBis", DateTools.getActualSQLDate());
        CriteriaHelper.addExpression(criteria, CriteriaHelper.LESS_EQUAL, "gueltigVon", DateTools.getActualSQLDate());
        return criteria.list();
    }

    @Override
    public List<TxtBaustein> findAllValid4TxtBausteinGruppe(final Long gruppeId) {
        if (gruppeId == null) {
            return null;
        }

        final StringBuilder hql = new StringBuilder("select txt.id, txt.idOrig, txt.name, txt.text, txt.editable, ");
        hql.append(" txt.gueltigVon, txt.gueltigBis , txt.mandatory from ");
        hql.append(TxtBaustein.class.getName()).append(" txt, ");
        hql.append(TxtBaustein2Gruppe.class.getName()).append(" grp ");
        hql.append(" where grp.txtBausteinGruppeId = :Id");
        hql.append(" and grp.txtBausteinIdOrig = txt.idOrig");
        hql.append(" and txt.gueltigVon <= :Date");
        hql.append(" and txt.gueltigBis >= :Date");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("Id", gruppeId);
        q.setDate("Date", DateTools.getActualSQLDate());

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();

        if (CollectionTools.isNotEmpty(result)) {
            List<TxtBaustein> li = new ArrayList<TxtBaustein>();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != null) {
                    Object[] values = result.get(i);
                    TxtBaustein view = new TxtBaustein();
                    view.setId(ObjectTools.getLongSilent(values, 0));
                    view.setIdOrig(ObjectTools.getLongSilent(values, 1));
                    view.setName(ObjectTools.getStringSilent(values, 2));
                    view.setText(ObjectTools.getStringSilent(values, 3));
                    view.setEditable(ObjectTools.getBooleanSilent(values, 4));
                    view.setGueltigVon(ObjectTools.getDateSilent(values, 5));
                    view.setGueltigBis(ObjectTools.getDateSilent(values, 6));
                    view.setMandatory(ObjectTools.getBooleanSilent(values, 7));
                    li.add(view);
                }
            }
            return li;
        }
        return null;
    }

    @Override
    public List<TxtBaustein> findAllNewTxtBausteine() {
        final StringBuilder hql = new StringBuilder("from " + TxtBaustein.class.getName() + " txt ");
        hql.append(" where txt.idOrig in ");
        hql.append(" (select distinct txt2.idOrig from ").append(TxtBaustein.class.getName()).append(" txt2) ");
        hql.append(" and txt.gueltigBis = ");
        hql.append(" (select max(txt3.gueltigBis) from ").append(TxtBaustein.class.getName()).append(" txt3 where txt3.idOrig = txt.idOrig)");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        @SuppressWarnings("unchecked")
        List<TxtBaustein> result = q.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
