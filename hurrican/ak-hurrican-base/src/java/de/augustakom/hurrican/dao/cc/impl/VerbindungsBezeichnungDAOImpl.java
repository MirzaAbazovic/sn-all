/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 10:00:53
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.math.*;
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
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.VerbindungsBezeichnungDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.VerbindungsBezeichnungHistoryView;

/**
 * Hibernate DAO-Implementierung von <code>VerbindungsBezeichnungDAO</code>.
 *
 *
 */
public class VerbindungsBezeichnungDAOImpl extends Hibernate4DAOImpl implements VerbindungsBezeichnungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final String SEQ_NEXT_VAL_UNIQUE_CODE = "SELECT S_T_TDN_UNIQUE_CODE_0.NEXTVAL FROM DUAL";
    private static final String SEQ_NEXT_VAL_LINE_ID = "SELECT S_T_TDN_LINEID_0.NEXTVAL FROM DUAL";
    private static final String SEQ_NEXT_VAL_WBCI_LINE_ID = "SELECT S_T_TDN_WBCI_LINEID_0.NEXTVAL FROM DUAL";

    @Override
    public VerbindungsBezeichnung findByAuftragId(final Long ccAuftragId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select t.id from ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(AuftragTechnik.class.getName()).append(" at ");
        hql.append(" where at.auftragId= :aId and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and at.vbzId=t.id");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", ccAuftragId);
        q.setDate("now", now);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if ((result != null) && (result.size() == 1)) {
            Long vbzId = result.get(0);
            return findById(vbzId, VerbindungsBezeichnung.class);
        }

        return null;
    }

    @Override
    public List<VerbindungsBezeichnung> findVerbindungsBezeichnungenByKundeNo(final Long kundeNo) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select distinct t.id from ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(AuftragDaten.class.getName()).append(" ad ");
        hql.append(" where at.auftragId = a.id and ad.auftragId = a.id ");
        hql.append(" and at.gueltigVon <= :now and at.gueltigBis > :now ");
        hql.append(" and ad.gueltigVon <= :now and ad.gueltigBis > :now ");
        hql.append(" and at.vbzId=t.id and a.kundeNo = :kundeNo");
        hql.append(" and ad.statusId != :aufKons ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("kundeNo", kundeNo);
        q.setDate("now", new Date());
        q.setLong("aufKons", AuftragStatus.KONSOLIDIERT);

        @SuppressWarnings("unchecked")
        List<Long> list = q.list();
        if (CollectionTools.isNotEmpty(list)) {
            List<VerbindungsBezeichnung> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Long vbzId = list.get(i);
                result.add(findById(vbzId, VerbindungsBezeichnung.class));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<VerbindungsBezeichnung> findVerbindungsBezeichnungLike(final String vbz) {
        final String bezeichnung;
        if (!vbz.endsWith(WildcardTools.SYSTEM_WILDCARD) && !vbz.endsWith(WildcardTools.DB_WILDCARD)) {
            if (!vbz.endsWith("-")) {
                bezeichnung = vbz + "-" + WildcardTools.DB_WILDCARD;
            } else {
                bezeichnung = vbz + WildcardTools.DB_WILDCARD;
            }
        } else {
            bezeichnung = vbz;
        }

        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(VerbindungsBezeichnung.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.ILIKE, VerbindungsBezeichnung.VBZ, bezeichnung);
        @SuppressWarnings("unchecked")
        List<VerbindungsBezeichnung> result = crit.list();
        return result;
    }

    @Override
    public Integer getNextUniqueCode() {
        return ((BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(SEQ_NEXT_VAL_UNIQUE_CODE).uniqueResult()).intValue();
    }

    @Override
    public Integer getNextLineId() {
        return ((BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(SEQ_NEXT_VAL_LINE_ID).uniqueResult()).intValue();
    }

    @Override
    public Integer getNextWbciLineIdValue() {
        return ((BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(SEQ_NEXT_VAL_WBCI_LINE_ID).uniqueResult()).intValue();
    }

    @Override
    public List<VerbindungsBezeichnungHistoryView> findVerbindungsBezeichnungHistory(String vbz) {
        StringBuilder hql = new StringBuilder("select t.id, t.vbz, ad.auftragId, ad.inbetriebnahme, ");
        hql.append("ad.kuendigung, ad.vorgabeSCV, ast.statusText, p.anschlussart, e.endstelle, e.name from ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragStatus.class.getName()).append(" ast, ");
        hql.append(Produkt.class.getName()).append(" p, ");
        hql.append(Endstelle.class.getName()).append(" e ");
        hql.append("where t.vbz=? and at.vbzId=t.id and at.gueltigVon<=? and at.gueltigBis>? ");
        hql.append("and (at.auftragTechnik2EndstelleId is null or at.auftragTechnik2EndstelleId=e.endstelleGruppeId) ");
        hql.append("and (e.endstelleTyp is null or e.endstelleTyp=?) ");
        hql.append("and at.auftragId=ad.auftragId and ad.gueltigVon<=? and ad.gueltigBis>? ");
        hql.append("and ad.prodId=p.id and ad.statusId=ast.id");

        Date now = new Date();
        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString(),
                new Object[] { vbz, now, now, Endstelle.ENDSTELLEN_TYP_B, now, now });
        if (result != null) {
            List<VerbindungsBezeichnungHistoryView> retVal = new ArrayList<VerbindungsBezeichnungHistoryView>();
            for (Object[] values : result) {
                VerbindungsBezeichnungHistoryView view = new VerbindungsBezeichnungHistoryView();
                view.setVbzId(ObjectTools.getLongSilent(values, 0));
                view.setVbz(ObjectTools.getStringSilent(values, 1));
                view.setAuftragId(ObjectTools.getLongSilent(values, 2));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, 3));
                view.setKuendigung(ObjectTools.getDateSilent(values, 4));
                view.setVorgabeSCV(ObjectTools.getDateSilent(values, 5));
                view.setStatusText(ObjectTools.getStringSilent(values, 6));
                view.setProdukt(ObjectTools.getStringSilent(values, 7));
                view.setEndstelleB(ObjectTools.getStringSilent(values, 8));
                view.setEndstelleBName(ObjectTools.getStringSilent(values, 9));
                retVal.add(view);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public void delete(VerbindungsBezeichnung vbz) {
        sessionFactory.getCurrentSession().delete(vbz);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
