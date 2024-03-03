/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:19:57
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.hibernate.HibernateInClauseHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.DNLeistungDAO;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;

/**
 * Hibernate DAO-Implementierung von <code>DNLeistungDAO</code>.
 *
 *
 */
public class DNLeistungDAOImpl extends Hibernate4DAOImpl implements DNLeistungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<DNLeistungsView> findLeistungViews(final List<Long> dnNos, final Long leistungsbuendelId) {
        final Map<String, Object> params = new HashMap<String, Object>();
        final StringBuilder hql = new StringBuilder("select ldn.id, ldn.dnNo, l.id, l.leistung, ");
        hql.append(" ldn.scvRealisierung, ldn.scvKuendigung, ldn.ewsdRealisierung, ldn.ewsdKuendigung, ");
        hql.append(" ldn.leistungParameter, ldn.parameterId, ldn.scvUserRealisierung, ldn.scvUserKuendigung, ");
        hql.append(" ldn.ewsdUserRealisierung, ldn.ewsdUserKuendigung, l.provisioningName ");
        hql.append(" from ");
        hql.append(Leistung4Dn.class.getName()).append(" l, ");
        hql.append(Leistung2DN.class.getName()).append(" ldn where ldn.leistung4DnId=l.id ");
        if (leistungsbuendelId != null) {
            hql.append(" and ldn.lbId= :lbId ");
            params.put("lbId", leistungsbuendelId);
        }
        hql.append(" and %s order by ldn.dnNo, ldn.id desc, ldn.scvKuendigung ");

        List<Object[]> tmp = HibernateInClauseHelper.list(getSessionFactory(), hql.toString(), params, "ldn.dnNo", dnNos);

        if (tmp != null) {
            List<DNLeistungsView> result = new ArrayList<DNLeistungsView>();
            for (Object[] values : tmp) {
                DNLeistungsView view = new DNLeistungsView();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setDnNo(ObjectTools.getLongSilent(values, 1));
                view.setLeistungsId(ObjectTools.getLongSilent(values, 2));
                view.setLeistung(ObjectTools.getStringSilent(values, 3));
                view.setAmRealisierung(ObjectTools.getDateSilent(values, 4));
                view.setAmKuendigung(ObjectTools.getDateSilent(values, 5));
                view.setEwsdRealisierung(ObjectTools.getDateSilent(values, 6));
                view.setEwsdKuendigung(ObjectTools.getDateSilent(values, 7));
                view.setParameter(ObjectTools.getStringSilent(values, 8));
                view.setParameterId(ObjectTools.getLongSilent(values, 9));
                view.setAmUserRealisierung(ObjectTools.getStringSilent(values, 10));
                view.setAmUserKuendigung(ObjectTools.getStringSilent(values, 11));
                view.setEwsdUserRealisierung(ObjectTools.getStringSilent(values, 12));
                view.setEwsdUserKuendigung(ObjectTools.getStringSilent(values, 13));
                view.setProvisioningName(ObjectTools.getStringSilent(values, 14));
                result.add(view);
            }

            return result;
        }
        return null;
    }

    @Override
    public List<Leistungsbuendel2Produkt> findLeistungsbuendel2Produkt(Long leistungNoOrig) {
        StringBuilder hql = new StringBuilder("select l2p.lbId, l2p.leistungNoOrig, ");
        hql.append(" l2p.protokollLeistungNoOrig, l2p.productOeNo from ");
        hql.append(Leistungsbuendel2Produkt.class.getName());
        hql.append(" l2p where l2p.leistungNoOrig=? ");
        hql.append(" order by l2p.protokollLeistungNoOrig asc"); // Sortierung unbedingt beibehalten, wg. Ermittlung des
        // passenden Leistungsbuendels!

        @SuppressWarnings("unchecked")
        List<Object[]> tmp = find(hql.toString(), new Object[] { leistungNoOrig });

        if (tmp != null) {
            List<Leistungsbuendel2Produkt> result = new ArrayList<Leistungsbuendel2Produkt>();
            for (Object[] values : tmp) {
                Leistungsbuendel2Produkt view = new Leistungsbuendel2Produkt();
                view.setLbId(ObjectTools.getLongSilent(values, 0));
                view.setLeistungNoOrig(ObjectTools.getLongSilent(values, 1));
                view.setProtokollLeistungNoOrig(ObjectTools.getLongSilent(values, 2));
                view.setProductOeNo(ObjectTools.getLongSilent(values, 3));
                result.add(view);
            }
            return result;
        }
        return null;
    }

    @Override
    public void deleteLeistungsbuendel2Produkt(final Long prodOeNoOrig, final Long leistungNoOrig) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Leistungsbuendel2Produkt.class.getName());
        hql.append(" l2p where l2p.leistungNoOrig=? and l2p.productOeNo=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameters(new Object[] { leistungNoOrig, prodOeNoOrig }, new Type[] {new LongType(), new LongType()});
        query.executeUpdate();
    }

    @Override
    public void saveLeistungsbuendel2Produkt(List<Leistungsbuendel2Produkt> prod2Oe) {
        if (prod2Oe != null) {
            for (Leistungsbuendel2Produkt lb2P : prod2Oe) {
                saveLeistungsbuendel2Produkt(lb2P);
            }
        }
    }

    @Override
    public void saveLeistungsbuendel2Produkt(Leistungsbuendel2Produkt lb2P) {
        sessionFactory.getCurrentSession().save(lb2P);
    }

    @Override
    public List<Leistung2Parameter> findLeistung2Parameter(final Long leistungId) {
        StringBuilder hql = new StringBuilder("select l2p.leistungId, l2p.parameterId from ");
        hql.append(Leistung2Parameter.class.getName()).append(" l2p where l2p.leistungId =? ");

        @SuppressWarnings("unchecked")
        List<Object[]> tmp = find(hql.toString(), leistungId);
        if (tmp != null) {
            List<Leistung2Parameter> result = new ArrayList<Leistung2Parameter>();
            for (Object[] values : tmp) {
                Leistung2Parameter view = new Leistung2Parameter();
                view.setLeistungId(ObjectTools.getLongSilent(values, 0));
                view.setParameterId(ObjectTools.getLongSilent(values, 1));
                result.add(view);
            }
            return result;
        }
        return null;
    }

    @Override
    public void deleteLeistung2Parameter(final Long leistungId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Leistung2Parameter.class.getName());
        hql.append(" l2p where l2p.leistungId=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, leistungId);
        query.executeUpdate();
    }

    @Override
    public void saveLeistung2Parameter(List<Leistung2Parameter> leistung2parameter) {
        if (leistung2parameter != null) {
            for (Leistung2Parameter l2p : leistung2parameter) {
                saveLeistung2Parameter(l2p);
            }
        }
    }

    @Override
    public void saveLeistung2Parameter(Leistung2Parameter l2p) {
        sessionFactory.getCurrentSession().save(l2p);
    }

    @Override
    public List<Leistung4Dn> findDNLeistungen4Buendel(final Long lbId) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(" select l4d.id , l4d.leistung, l4d.beschreibung from ");
        hql.append(Leistung4Dn.class.getName()).append(" l4d, ");
        hql.append(Lb2Leistung.class.getName()).append(" l2l ");
        hql.append(" where l2l.leistungId=l4d.id ");
        hql.append(" and l2l.lbId= :buendelId ");
        hql.append(" and l2l.verwendenVon<= :now and l2l.verwendenBis> :now ");

        Query q = session.createQuery(hql.toString());
        q.setLong("buendelId", lbId);
        q.setDate("now", new Date());

        @SuppressWarnings("unchecked")
        List<Object[]> tmp = q.list();
        if (tmp != null) {
            List<Leistung4Dn> result = new ArrayList<>();
            for (Object[] values : tmp) {
                Leistung4Dn view = new Leistung4Dn();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setLeistung(ObjectTools.getStringSilent(values, 1));
                view.setBeschreibung(ObjectTools.getStringSilent(values, 2));
                result.add(view);
            }
            return result;
        }
        return null;
    }

    @Override
    public List<LeistungParameter> findSignedParameter2Leistung(Long lId) {
        StringBuilder hql = new StringBuilder("select lp.id, ");
        hql.append("lp.leistungParameterBeschreibung, lp.leistungParameterMehrfach, lp.leistungParameterMehrfachIms, ");
        hql.append("lp.leistungParameterTyp from ");
        hql.append(Leistung2Parameter.class.getName()).append(" l2p, ");
        hql.append(LeistungParameter.class.getName()).append(" lp ");
        hql.append("where l2p.parameterId = lp.id and l2p.leistungId =? ");

        @SuppressWarnings("unchecked")
        List<Object[]> tmp = find(hql.toString(), lId);
        if (tmp != null) {
            List<LeistungParameter> result = new ArrayList<>();
            for (Object[] values : tmp) {
                LeistungParameter view = new LeistungParameter();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setLeistungParameterBeschreibung(ObjectTools.getStringSilent(values, 1));
                view.setLeistungParameterMehrfach(ObjectTools.getIntegerSilent(values, 2));
                view.setLeistungParameterMehrfachIms(ObjectTools.getIntegerSilent(values, 3));
                view.setLeistungParameterTyp(ObjectTools.getIntegerSilent(values, 4));
                result.add(view);
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteLeistung2DnByDnNo(final Long dnNo) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Leistung2DN.class.getName());
        hql.append(" l2dn where l2dn.dnNo =?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, dnNo);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Leistung2DN> findLeistung2DnByDnNos(final List<Long> dnNos) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(Leistung2DN.class.getName());
        hql.append(" ldn where ldn.dnNo in (:nos)");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameterList("nos", dnNos);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Leistung2DN> findUnProvisionedDNServices(final Date provDate) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(Leistung2DN.class.getName());
        hql.append(" ldn where (ldn.scvRealisierung= :date and ldn.ewsdRealisierung is null ");
        hql.append(" and ldn.cpsTxIdCreation is null) or ");
        hql.append(" (ldn.scvKuendigung= :date and ldn.ewsdKuendigung is null and ldn.cpsTxIdCancel is null)");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setDate("date", provDate);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Leistung2DN> findLeistung2DN4CPSTx(final Long cpsTxId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Leistung2DN.class);
        crit.add(Restrictions.or(Restrictions.eq("cpsTxIdCreation", cpsTxId),
                Restrictions.eq("cpsTxIdCancel", cpsTxId)));

        return crit.list();
    }

    @Override
    public List<Leistung4Dn> findDefaultLeistungen4Buendel(final Long lbId) {
        final StringBuilder hql = new StringBuilder("select l4d.id , l4d.leistung, l4d.beschreibung from ");
        hql.append(Leistung4Dn.class.getName()).append(" l4d, ");
        hql.append(Lb2Leistung.class.getName()).append(" l2l ");
        hql.append("where l2l.leistungId=l4d.id and l2l.lbId= :lbId and l2l.standard= :st ");
        hql.append("and l2l.verwendenVon<= :now and l2l.verwendenBis> :now ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("lbId", lbId);
        q.setDate("now", now);
        q.setBoolean("st", Boolean.TRUE);

        @SuppressWarnings("unchecked")
        List<Object[]> tmp = q.list();
        if (tmp != null) {
            List<Leistung4Dn> result = new ArrayList<>();
            for (Object[] values : tmp) {
                Leistung4Dn view = new Leistung4Dn();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setLeistung(ObjectTools.getStringSilent(values, 1));
                view.setBeschreibung(ObjectTools.getStringSilent(values, 2));
                result.add(view);
            }
            return result;
        }

        return null;
    }

    @Override
    public List<LeistungInLeistungsbuendelView> findAllLb2Leistung(Long lbId) {
        StringBuilder sql = new StringBuilder("select lb2l.ID, lb2l.LB_ID, lb2l.VERWENDEN_BIS, ");
        sql.append(" lb2l.VERWENDEN_VON, lb2l.STANDARD, lb2l.OE__NO,  ");
        sql.append(" l4d.LEISTUNG, l4d.BESCHREIBUNG,  l4d.ID AS Leistung_id");
        sql.append(" from  t_leistung_4_dn l4d ");
        sql.append(" LEFT JOIN t_lb_2_leistung lb2l ON l4d.ID = lb2l.LEISTUNG_ID  ");
        sql.append(" where lb2l.LB_ID =?  ");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong(0, lbId);
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<LeistungInLeistungsbuendelView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                LeistungInLeistungsbuendelView view = new LeistungInLeistungsbuendelView();
                view.setLb2LeistungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setLbId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerwendenBis(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerwendenVon(ObjectTools.getDateSilent(values, columnIndex++));
                view.setStandard(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setOeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setLeistung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBeschreibung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeistungId(ObjectTools.getLongSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public void updateLB2Leistung(Date vBis, Long lb2LId) {
        if ((vBis != null) && (lb2LId != null)) {
            Session session = sessionFactory.getCurrentSession();
            SQLQuery sqlQuery = session.createSQLQuery("UPDATE T_LB_2_LEISTUNG SET GUELTIG_BIS=?, VERWENDEN_BIS=? WHERE ID=?");
            sqlQuery.setParameters(new Object[] { new Date(), vBis, lb2LId },
                    new Type[] {new DateType(), new DateType(), new LongType()});
            sqlQuery.executeUpdate();
        }
    }

    @Override
    public Lb2Leistung findLb2Leistung(Long leistungId, Long leistungsbuendelId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(Lb2Leistung.class.getName());
        hql.append(" lb2l where lb2l.leistungId=? and lb2l.lbId=? and lb2l.gueltigBis>?");

        @SuppressWarnings("unchecked")
        List<Lb2Leistung> result = find(hql.toString(),
                new Object[] { leistungId, leistungsbuendelId, DateTools.getActualSQLDate() });
        if ((result != null) && (!result.isEmpty())) { return result.get(result.size() - 1); }

        return null;
    }

    @Override
    public List<Long> groupedDnNos() {
        StringBuilder hql = new StringBuilder("SELECT ldn.dnNo FROM ");
        hql.append(Leistung2DN.class.getName()).append(" ldn ");
        hql.append(" GROUP BY ldn.dnNo ");

        @SuppressWarnings("unchecked")
        List<Long> tmp = find(hql.toString());
        return tmp;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public List<Leistung2DN> findAktiveLeistung2DnByRufnummern(@Nonnull Long leistung4DnId,
            @Nonnull List<Long> rufnummern) {
        DetachedCriteria crit = DetachedCriteria.forClass(Leistung2DN.class);
        crit.add(Restrictions.eq("leistung4DnId", leistung4DnId));
        crit.add(Restrictions.in("dnNo", rufnummern));
        crit.add(Restrictions.isNull("scvKuendigung"));
        return (List<Leistung2DN>) crit.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
