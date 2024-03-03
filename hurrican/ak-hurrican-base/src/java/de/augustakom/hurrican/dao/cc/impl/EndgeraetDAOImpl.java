/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 16:54:54
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ArrayTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.EndgeraetDAO;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Produkt2EG;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;

/**
 * Hibernate DAO-Implementierung von <code>EndgeraetDAO</code>.
 *
 *
 */
public class EndgeraetDAOImpl extends Hibernate4DAOImpl implements EndgeraetDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public EG2Auftrag findEG2Auftrag(Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from ");
        hql.append(EG2Auftrag.class.getName()).append(" eg2a ");
        hql.append(" where eg2a.id = ?");

        @SuppressWarnings("unchecked")
        List<EG2Auftrag> tmp = find(hql.toString(), new Object[] { id });
        if ((tmp != null) && (tmp.size() == 1)) {
            return tmp.get(0);
        }
        return null;
    }

    @Override
    public List<EG> findValidEG(Long extLeistungNoOrig, Long prodId) {
        StringBuilder hql = new StringBuilder();
        hql.append("select eg.id from ");
        hql.append(EG.class.getName()).append(" eg, ");
        hql.append(Produkt2EG.class.getName()).append(" p2e ");
        hql.append(" where eg.id=p2e.endgeraetId and ");
        hql.append(" eg.egVerfuegbarVon<=? and eg.egVerfuegbarBis>? ");
        hql.append(" and eg.extLeistungNo=? and p2e.prodId=?");
        hql.append(" and p2e.isActive=?");

        Date now = DateTools.getActualSQLDate();
        @SuppressWarnings("unchecked")
        List<Long> tmp = find(hql.toString(),
                new Object[] { now, now, extLeistungNoOrig, prodId, Boolean.TRUE });

        List<EG> retVal = new ArrayList<EG>();
        if (CollectionTools.isNotEmpty(tmp)) {
            for (Long egId : tmp) {
                retVal.add(findById(egId, EG.class));
            }
        }

        return retVal;
    }

    @Override
    public List<EG> findEGs4Produkt(Long prodId, boolean onlyDefault) {
        Date now = DateTools.getActualSQLDate();
        Object[] params = new Object[] { prodId, now, now, Boolean.TRUE };

        StringBuilder hql = new StringBuilder("select eg.id from ");
        hql.append(EG.class.getName()).append(" eg, ");
        hql.append(Produkt2EG.class.getName()).append(" p2eg ");
        hql.append(" where p2eg.prodId=? and p2eg.endgeraetId=eg.id and ");
        hql.append(" eg.egVerfuegbarVon<=? and eg.egVerfuegbarBis>? ");
        hql.append(" and p2eg.isActive=? ");
        if (onlyDefault) {
            hql.append(" and p2eg.isDefault=? ");
            params = ArrayTools.add(params, Boolean.TRUE);
        }

        @SuppressWarnings("unchecked")
        List<Long> tmp = find(hql.toString(), params);

        List<EG> result = new ArrayList<EG>();
        for (Long egId : tmp) {
            result.add(findById(egId, EG.class));
        }
        return result;
    }

    @Override
    public void deleteProdukt2EG(final Long prodId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Produkt2EG.class.getName());
        hql.append(" p where p.prodId=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, prodId);
        query.executeUpdate();
    }

    @Override
    public void deleteEG2Auftrag(final Long eg2AuftragId) {
        sessionFactory.getCurrentSession().delete(findById(eg2AuftragId, EG2Auftrag.class));
    }

    @Override
    public List<EG2AuftragView> findEG2AuftragViews(Long ccAuftragId) {
        StringBuilder sql = new StringBuilder(
                "select eg.ID as EG_ID, eg.NAME as EG_NAME, eg.CONFIGURABLE, eg.CPS_PROVISIONING, ");
        sql.append("e2a.AUFTRAG_ID, e2a.DEACTIVATED, e2a.ID as E2A_ID, m.ID as M_ID, m.NAME as MONTAGE, ");
        sql.append("e2a.BEMERKUNG, e2a.RAUM as RAUM, e2a.ETAGE as ETAGE, cnf.ID as CONFIG_ID, cnf.SERIAL_NUMBER, ");
        sql.append("endstelle.ES_TYP ");
        sql.append("from T_EG_2_AUFTRAG e2a ");
        sql.append("inner join T_EG eg on e2a.EG_ID=eg.ID ");
        sql.append("left join T_MONTAGEART m on e2a.MONTAGEART=m.ID ");
        sql.append("left join T_EG_CONFIG cnf on e2a.ID=cnf.EG2A_ID ");
        sql.append("left join T_ENDSTELLE endstelle on e2a.ENDSTELLE_ID=endstelle.ID ");
        sql.append("where e2a.AUFTRAG_ID=? ");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong(0, ccAuftragId);
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<EG2AuftragView> retVal = new ArrayList<EG2AuftragView>();
            for (Object[] values : result) {
                int columnIndex = 0;
                EG2AuftragView view = new EG2AuftragView();
                view.setEgId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEgName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setIsConfigurable(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setCpsProvisioning(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setDeactivated(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setEg2AuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setMontageartId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setMontageart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBemerkung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRaum(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEtage(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHasConfiguration(
                        (ObjectTools.getLongSilent(values, columnIndex++)) != null ? Boolean.TRUE : Boolean.FALSE);
                view.setSeriennummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsTyp(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<EndgeraetAcl> findAllEndgeraetAcls() {
        return findAll(EndgeraetAcl.class);
    }

    @Override
    public EndgeraetAcl findEndgeraetAclByName(String name) {
        Criterion nameEq = Restrictions.eq("name", name);
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(EndgeraetAcl.class);
        crit.add(nameEq);
        return (EndgeraetAcl) crit.uniqueResult();
    }

    @Override
    public void deleteEndgeraetAcl(EndgeraetAcl endgeraetAcl) {
        sessionFactory.getCurrentSession().delete(endgeraetAcl);
    }

    @Override
    public List<EGType> findAllEGTypes() {
        return findAll(EGType.class);
    }

    @Override
    public List<EndgeraetPort> findDefaultEndgeraetPorts4Count(final Integer count) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(EndgeraetPort.class.getName()).append(" egp ");
        hql.append("where egp.endgeraetTyp is null ");
        if (count != null) {
            hql.append("and egp.number <= :count ");
        }
        hql.append("order by egp.number ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        if (count != null) {
            q.setInteger("count", count);
        }
        @SuppressWarnings("unchecked")
        List<EndgeraetPort> result = q.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
