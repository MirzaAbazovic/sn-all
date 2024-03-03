/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2007 11:17:12
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.math.*;
import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.model.cc.view.CBVorgangView;


/**
 * Hibernate DAO-Implementierung von <code>CBVorgangDAO</code>.
 * <p/>
 * <b>ACHTUNG:</b> werden CBVorgang bzw. deren Ableitungen geladen, muss nachträglich noch der AKUser in die geladenen
 * Entities "gemerged" werden. Siehe dazu die mergeUser... Methoden.
 *
 *
 */
@Component
public class CBVorgangDAOImpl extends Hibernate4DAOImpl implements CBVorgangDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final String SEQ_NEXT_VAL_UNIQUE_CODE = "SELECT S_T_CB_VORGANG_CARRIERREFNR_0.NEXTVAL FROM DUAL";
    private static final String SEQ_NEXT_VAL_AUFTRAG_KLAMMER = "SELECT S_T_CB_VORGANG_AUF_KLAMMER_0.NEXTVAL FROM DUAL";

    @Value("${carrier.ref.nr.prefix}")
    String carrierRefNrPrefix;

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    protected AKUserService userService;

    @Override
    public List<CBVorgang> findCBVorgaenge4CB(Long... cbIDs) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CBVorgang.class);
        criteria.add(Restrictions.in("cbId", cbIDs));
        criteria.addOrder(Order.desc("id"));
        List<CBVorgang> cbVorgangs = criteria.list();
        return mergeUser(cbVorgangs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CBVorgangNiederlassung> findOpenCBVorgaengeNiederlassungWithWiedervorlage() {
        final String hql = new StringBuilder("select cbv, nl.name ")
                .append(" from CBVorgang as cbv, AuftragTechnik as at, Niederlassung as nl ")
                .append(" left join fetch cbv.subOrders ")
                .append(" where ")
                .append(" cbv.auftragId = at.auftragId ")
                .append(" and at.niederlassungId = nl.id ")
                .append(" and cbv.status < :statusClosed and at.gueltigBis > :now ")
                .append(" and (cbv.wiedervorlageAm <= :dateTimeNow or cbv.wiedervorlageAm is null)")
                .toString();

        Session session = sessionFactory.getCurrentSession();
        // "left join fetch" auf subOrders und tamUserTask zur Performance-Verbesserung!

        Query query = session.createQuery(hql);
        query.setParameter("statusClosed", CBVorgang.STATUS_CLOSED);
        query.setParameter("now", new Date());
        query.setParameter("dateTimeNow", new Date());
        List<Object[]> list = query.list();
        List<CBVorgangNiederlassung> result = new ArrayList<>(list.size());
        for (Object[] entry : list) {
            result.add(new CBVorgangNiederlassung(mergeUser((CBVorgang) entry[0]), (String) entry[1]));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CBVorgangView> findOpenCBVorgaenge(final List<Long> carrierIds, final Long minStatus,
            final Boolean minEqual, final Long maxStatus, final Boolean maxEqual) {
        final StringBuilder hql = new StringBuilder("select cbv.id, cbv.typ, cbv.status, cbv.returnOk, t.vbz, ");
        hql.append("p.anschlussart, ad.prodId, cbv.submittedAt, cbv.vorgabeMnet, ad.auftragId, ");
        hql.append("e.hvtIdStandort, hg.ortsteil from ");
        hql.append(CBVorgang.class.getName()).append(" cbv, ");
        hql.append(Carrierbestellung.class.getName()).append(" cb, ");
        hql.append(Endstelle.class.getName()).append(" e, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Produkt.class.getName()).append(" p, ");
        hql.append(HVTStandort.class.getName()).append(" hs, ");
        hql.append(HVTGruppe.class.getName()).append(" hg ");
        hql.append("where cbv.cbId=cb.id and cbv.auftragId=at.auftragId ");
        hql.append("and at.auftragTechnik2EndstelleId=e.endstelleGruppeId ");
        hql.append("and e.hvtIdStandort=hs.id and hs.hvtGruppeId=hg.id ");
        hql.append("and at.vbzId=t.id and at.auftragId=ad.auftragId and ad.prodId=p.id ");
        hql.append("and at.gueltigBis> :now and ad.gueltigBis> :now ");
        hql.append("and cbv.carrierId in (:carrierIds) ");

        if (minStatus != null) {
            if (BooleanTools.nullToFalse(minEqual)) {
                hql.append("and cbv.status>= :minStatus ");
            }
            else {
                hql.append("and cbv.status> :minStatus ");
            }
        }
        if (maxStatus != null) {
            if (BooleanTools.nullToFalse(maxEqual)) {
                hql.append("and cbv.status<= :maxStatus ");
            }
            else {
                hql.append("and cbv.status< :maxStatus ");
            }
        }
        hql.append("order by cbv.vorgabeMnet");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameterList("carrierIds", carrierIds);
        query.setDate("now", new Date());
        if (minStatus != null) {
            query.setLong("minStatus", minStatus);
        }
        if (maxStatus != null) {
            query.setLong("maxStatus", maxStatus);
        }

        List<Object[]> result = query.list();
        if (result != null) {
            List<CBVorgangView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                CBVorgangView view = new CBVorgangView();
                view.setCbVorgangId(ObjectTools.getLongSilent(values, 0));
                view.setTyp(ObjectTools.getLongSilent(values, 1));
                view.setStatus(ObjectTools.getLongSilent(values, 2));
                view.setReturnOk(ObjectTools.getBooleanSilent(values, 3));
                view.setVbz(ObjectTools.getStringSilent(values, 4));
                view.setProdukt(ObjectTools.getStringSilent(values, 5));
                view.setProdId(ObjectTools.getLongSilent(values, 6));
                view.setSubmittedAt(ObjectTools.getDateSilent(values, 7));
                view.setVorgabeMnet(ObjectTools.getDateSilent(values, 8));
                view.setAuftragId(ObjectTools.getLongSilent(values, 9));
                view.setHvtStandortId(ObjectTools.getLongSilent(values, 10));
                view.setHvt(ObjectTools.getStringSilent(values, 11));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public String getNextCarrierRefNr() {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(SEQ_NEXT_VAL_UNIQUE_CODE);
        Long nextCode = ((BigDecimal) sqlQuery.uniqueResult()).longValue();
        String formattedNextCode = StringTools.fillToSize(String.format("%s", nextCode), 10, '0', true);
        if (StringUtils.isBlank(carrierRefNrPrefix)) {
            return formattedNextCode;
        }
        return carrierRefNrPrefix + formattedNextCode.substring(carrierRefNrPrefix.length());
    }

    @Override
    public Long getNextAuftragsKlammer() {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(SEQ_NEXT_VAL_AUFTRAG_KLAMMER);
        return ((BigDecimal) sqlQuery.uniqueResult()).longValue();
    }

    @Override
    public void deleteById(final Serializable id) {
        Session session = sessionFactory.getCurrentSession();
        CBVorgang toDelete = (CBVorgang) session.get(CBVorgang.class, id);
        session.delete(toDelete);
        session.flush();
    }

    @Override
    public CBVorgang findCBVorgangByCarrierRefNr(String carrierRefNr) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CBVorgang.class);
        criteria.add(Restrictions.eq("carrierRefNr", carrierRefNr));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        List<CBVorgang> result = criteria.list();
        if (!CollectionUtils.isEmpty(result)) {
            return mergeUser(result.get(0));
        }

        return null;
    }

    private List<CBVorgang> mergeUser(List<CBVorgang> cbVorgangs) {
        for (CBVorgang cbVorgang : cbVorgangs) {
            mergeUser(cbVorgang);
        }
        return cbVorgangs;
    }

    private CBVorgang mergeUser(CBVorgang cbVorgang) {
        if (cbVorgang != null && cbVorgang.getUserId() != null) {
            try {
                AKUser user = userService.findById(cbVorgang.getUserId());
                cbVorgang.setBearbeiter(user);
            }
            catch (AKAuthenticationException e) {
                throw new RuntimeException(e);
            }
        }
        return cbVorgang;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
