/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 16:48:03
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails;
import de.augustakom.hurrican.model.cc.view.RangierungWithEquipmentView;


/**
 * Hibernate DAO-Implementierung von <code>RangierungDAO</code>.
 *
 *
 */
public class RangierungDAOImpl extends HurricanHibernateDaoImpl implements RangierungDAO {

    private static final Logger LOGGER = Logger.getLogger(RangierungDAOImpl.class);
    /* SQL-Statement, um den Next-Value der Sequence S_T_RANGIERUNG_LTGGESID_0 zu ermitteln. */
    private static final String SQL_NEXTVAL_LTG_GES_ID =
            "SELECT S_T_RANGIERUNG_LTGGESID_0.NEXTVAL FROM DUAL";
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Rangierung> findFreiByQuery(final RangierungQuery query, final int maxResultCount) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Criteria criteria = session.createCriteria(Rangierung.class);
        if (BooleanTools.nullToFalse(query.getIncludeDefekt())) {
            Criterion orCriterion = Restrictions.or(
                    Restrictions.eq("freigegeben", Freigegeben.freigegeben),
                    Restrictions.eq("freigegeben", Freigegeben.defekt));
            criteria.add(orCriterion);
        }
        else {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "freigegeben", Freigegeben.freigegeben);
        }

        if (BooleanTools.nullToFalse(query.getIncludeFreigabebereit())) {
            criteria.add(Restrictions.or(
                    Restrictions.isNull("esId"),
                    Restrictions.eq("esId", Rangierung.RANGIERUNG_NOT_ACTIVE)));
        }
        else {
            criteria.add(Restrictions.isNull("esId"));
        }
        criteria.add(Restrictions.le("gueltigVon", now));
        criteria.add(Restrictions.gt("gueltigBis", now));
        if (query.getHvtStandortId() != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "hvtIdStandort", query.getHvtStandortId());
        }
        if (query.getPhysikTypId() != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "physikTypId", query.getPhysikTypId());
        }
        if (query.getLeitungGesamtId() != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "leitungGesamtId", query.getLeitungGesamtId());
        }
        if (query.getLeitungLfdNr() != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "leitungLfdNr", query.getLeitungLfdNr());
        }
        criteria.addOrder(Order.asc("id"));
        if (maxResultCount > 0) {
            criteria.setMaxResults(maxResultCount);
        }

        @SuppressWarnings("unchecked")
        List<Rangierung> result = criteria.list();
        return result;
    }

    @Override
    public List<Rangierung> findFrei(final Long hvtStdId, final CarrierEquipmentDetails eqOutQuery) {
        final StringBuilder hql = new StringBuilder("select r.id from ");
        hql.append(Rangierung.class.getName()).append(" r, ");
        hql.append(Equipment.class.getName()).append(" eq ");
        hql.append(" where r.eqOutId=eq.id");
        hql.append(" and r.freigegeben= :free and r.esId is null and r.hvtIdStandort= :hvt ");
        hql.append(" and r.gueltigVon<= :now and r.gueltigBis> :now ");
        hql.append(" and eq.uetv= :uetv and eq.rangSSType= :rsst ");
        hql.append(" and r.eqInId is null");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setParameter("free", Freigegeben.freigegeben);
        q.setLong("hvt", hvtStdId);
        q.setDate("now", now);
        q.setString("uetv", eqOutQuery.getUetv());
        q.setString("rsst", eqOutQuery.getRangSSType());

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if (CollectionTools.isNotEmpty(result)) {
            List<Rangierung> retVal = new ArrayList<>(result.size());
            retVal.addAll(
                    result.stream()
                            .map(rId -> (Rangierung) sessionFactory.getCurrentSession().get(Rangierung.class, rId))
                            .collect(Collectors.toList())
            );
            return retVal;
        }

        return Collections.emptyList();
    }

    @Override
    public List<Rangierung> findByLtgGesId(final Integer ltgGesId) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Criteria criteria = session.createCriteria(Rangierung.class);
        criteria.add(Restrictions.eq(Rangierung.LEITUNG_GESAMT_ID, ltgGesId));
        criteria.add(Restrictions.le(AbstractCCHistoryModel.GUELTIG_VON, now));
        criteria.add(Restrictions.gt(AbstractCCHistoryModel.GUELTIG_BIS, now));
        criteria.addOrder(Order.asc("leitungLfdNr"));
        @SuppressWarnings("unchecked")
        List<Rangierung> result = criteria.list();
        return result;
    }

    @Override
    public Integer getNextLtgGesId() {
        return ((BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(SQL_NEXTVAL_LTG_GES_ID).uniqueResult()).intValue();
    }

    @Override
    public Integer getMaxLfdNr(Integer ltgGesId) {
        StringBuilder hql = new StringBuilder("select max(r.leitungLfdNr) from ");
        hql.append(Rangierung.class.getName());
        hql.append(" r where r.leitungGesamtId=?");

        @SuppressWarnings("unchecked")
        List<Integer> result = find(hql.toString(), ltgGesId);
        if (result != null && result.size() == 1) {
            return result.get(0);
        }
        return 0;
    }


    @Override
    public Rangierung findByAuftragAndEsTyp(final Long auftragId, final String esTyp) {
        if ((auftragId == null) || (esTyp == null)) { return null; }

        final StringBuilder hql = new StringBuilder("select r.id from ");
        hql.append(Rangierung.class.getName()).append(" r, ");
        hql.append(Endstelle.class.getName()).append(" e, ");
        hql.append(AuftragTechnik.class.getName()).append(" at ");
        hql.append(" where at.auftragId= :aId and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and at.auftragTechnik2EndstelleId=e.endstelleGruppeId and e.endstelleTyp= :esTyp ");
        hql.append(" and e.rangierId=r.id and r.gueltigVon<= :now and r.gueltigBis> :now ");
        hql.append(" and e.id=r.esId");  // letzte Bedingung nur zur Sicherheit

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", auftragId);
        q.setDate("now", now);
        q.setString("esTyp", esTyp);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if (result != null && result.size() == 1) {
            Long rId = result.get(0);
            return (Rangierung) sessionFactory.getCurrentSession().get(Rangierung.class, rId);
        }

        return null;
    }

    @Override
    public int freigabeRangierung(Long rangierID) {
        String sql = "UPDATE T_RANGIERUNG rang set FREIGABE_AB = null, ES_ID = null, BEMERKUNG = null where RANGIER_ID = :rangierID";
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sql);
        sqlQuery.setLong("rangierID", rangierID);
        return sqlQuery.executeUpdate();
    }

    @Override
    public List<RangierungWithEquipmentView> findRangierungWithEquipmentViews(Set<Long> rangierungIds) {
        if (rangierungIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        StringBuilder sql = new StringBuilder("SELECT");
        sql.append(" at.AUFTRAG_ID, r.ES_ID AS ENDSTELLE_ID, r.LEITUNG_GESAMT_ID, r.BEMERKUNG");
        sql.append(", r.RANGIER_ID, r.PHYSIK_TYP AS PHYSIK_TYP, r.FREIGEGEBEN, r.FREIGABE_AB, r.ONT_ID");
        sql.append(", eqi.HW_EQN AS HW_EQN, bgti.NAME AS BG_TYP, eqi.RANG_VERTEILER AS RANG_VERTEILER_EQ_IN");
        sql.append(", eqi.RANG_BUCHT AS RANG_BUCHT_EQ_IN, eqi_sw.NAME AS SWITCH_EQ_IN");
        sql.append(", eqi.RANG_LEISTE1 AS LEISE_EQ_ID, eqi.RANG_STIFT1 AS STIFT_EQ_IN");
        sql.append(", eqo.RANG_VERTEILER AS RANG_VERTEILER_EQ_OUT, eqo.RANG_LEISTE1 AS LEISTE_EQ_OUT");
        sql.append(", eqo.RANG_STIFT1 AS STIFT_EQ_OUT, eqo.UETV AS UETV_EQ_OUT, eqo.RANG_SS_TYPE AS RANG_SS_TYPE_EQ_OUT");
        sql.append(", r2.RANGIER_ID AS RANGIER_ID2, r2.PHYSIK_TYP AS PHYSIK_TYP2, r2.FREIGEGEBEN AS FREIGEGEBEN2");
        sql.append(", r2.FREIGABE_AB AS FREIGABE_AB2");
        sql.append(", eqi2.HW_EQN AS HW_EQN2, bgti2.NAME AS BG_TYP2, eqi2.RANG_VERTEILER AS RANG_VERTEILER_EQ_IN2");
        sql.append(", eqi2.RANG_BUCHT AS RANG_BUCHT_EQ_IN2, eqi2_sw.NAME AS SWITCH_EQ_IN_2");
        sql.append(", eqi2.RANG_LEISTE1 AS LEISE_EQ_ID2, eqi2.RANG_STIFT1 AS STIFT_EQ_IN2");
        sql.append(", eqo2.RANG_VERTEILER AS RANG_VERTEILER_EQ_OUT2, eqo2.RANG_LEISTE1 AS LEISTE_EQ_OUT2");
        sql.append(", eqo2.RANG_STIFT1 AS STIFT_EQ_OUT2, eqo2.UETV AS UETV_EQ_OUT2, eqo2.RANG_SS_TYPE AS RANG_SS_TYPE_EQ_OUT2");
        sql.append(" FROM T_RANGIERUNG r");
        sql.append(" JOIN T_EQUIPMENT eqi ON (eqi.EQ_ID = r.EQ_IN_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_SWITCH eqi_sw ON (eqi.SWITCH = eqi_sw.ID)");
        sql.append(" JOIN T_HW_BAUGRUPPE bgi ON (bgi.ID = eqi.HW_BAUGRUPPEN_ID)");
        sql.append(" JOIN T_HW_BAUGRUPPEN_TYP bgti ON (bgti.ID = bgi.HW_BG_TYP_ID)");
        sql.append(" LEFT OUTER JOIN T_EQUIPMENT eqo ON (eqo.EQ_ID = r.EQ_OUT_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_BAUGRUPPE bgo ON (bgo.ID = eqo.HW_BAUGRUPPEN_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_BAUGRUPPEN_TYP bgto ON (bgto.ID = bgo.HW_BG_TYP_ID)");
        sql.append(" LEFT OUTER JOIN T_RANGIERUNG r2 ON (r2.RANGIER_ID <> r.RANGIER_ID AND r2.LEITUNG_GESAMT_ID = r.LEITUNG_GESAMT_ID");
        sql.append(" AND (r2.GUELTIG_VON<=? OR r2.GUELTIG_VON IS NULL) AND (r2.GUELTIG_BIS>? OR r2.GUELTIG_BIS IS NULL))");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        sql.append(" LEFT OUTER JOIN T_EQUIPMENT eqi2 ON (eqi2.EQ_ID = r2.EQ_IN_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_SWITCH eqi2_sw ON (eqi2.SWITCH = eqi_sw.ID)");
        sql.append(" LEFT OUTER JOIN T_HW_BAUGRUPPE bgi2 ON (bgi2.ID = eqi2.HW_BAUGRUPPEN_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_BAUGRUPPEN_TYP bgti2 ON (bgti2.ID = bgi2.HW_BG_TYP_ID)");
        sql.append(" LEFT OUTER JOIN T_EQUIPMENT eqo2 ON (eqo2.EQ_ID = r2.EQ_OUT_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_BAUGRUPPE bgo2 ON (bgo2.ID = eqo2.HW_BAUGRUPPEN_ID)");
        sql.append(" LEFT OUTER JOIN T_HW_BAUGRUPPEN_TYP bgto2 ON (bgto2.ID = bgo2.HW_BG_TYP_ID)");
        sql.append(" LEFT OUTER JOIN T_ENDSTELLE es ON (es.ID = r.ES_ID)");
        sql.append(" LEFT OUTER JOIN T_AUFTRAG_TECHNIK_2_ENDSTELLE at2es ON (at2es.ID = es.ES_GRUPPE)");
        sql.append(" LEFT OUTER JOIN T_AUFTRAG_TECHNIK at ON (at.AT_2_ES_ID = at2es.ID and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>?))");
        sql.append(" WHERE r.RANGIER_ID IN (").append(StringUtils.join(rangierungIds, ",")).append(")");
        sql.append(" AND r.GUELTIG_VON<=? AND r.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
        }

        List<RangierungWithEquipmentView> retValue = new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0;
                RangierungWithEquipmentView view = new RangierungWithEquipmentView();

                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setLeitungGesamtId(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setBemerkung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangierId(ObjectTools.getLongSilent(values, columnIndex++));
                Long physikTypId = ObjectTools.getLongSilent(values, columnIndex++);
                view.setPhysikTyp((physikTypId == null) ? null : findById(physikTypId, PhysikTyp.class));
                String freigegeben = ObjectTools.getStringSilent(values, columnIndex++);
                view.setFreigegeben((freigegeben == null) ? null : Freigegeben.getFreigegeben(Integer.parseInt(freigegeben)));
                view.setFreigabeAb(ObjectTools.getDateSilent(values, columnIndex++));
                view.setOntId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHwEqn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBgTyp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangVerteilerEqIn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangBuchtEqIn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSwitchEqIn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeisteEqIn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStiftEqIn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangVerteilerEqOut(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeisteEqOut(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStiftEqOut(ObjectTools.getStringSilent(values, columnIndex++));
                String uetv = ObjectTools.getStringSilent(values, columnIndex++);
                view.setUetvEqOut((uetv == null) ? null : Uebertragungsverfahren.valueOf(uetv));
                view.setRangSSTypeEqOut(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangierId2(ObjectTools.getLongSilent(values, columnIndex++));
                Long physikTypId2 = ObjectTools.getLongSilent(values, columnIndex++);
                view.setPhysikTyp2((physikTypId2 == null) ? null : findById(physikTypId2, PhysikTyp.class));
                String freigegeben2 = ObjectTools.getStringSilent(values, columnIndex++);
                view.setFreigegeben2((freigegeben2 == null) ? null : Freigegeben.getFreigegeben(Integer.parseInt(freigegeben2)));
                view.setFreigabeAb2(ObjectTools.getDateSilent(values, columnIndex++));
                view.setHwEqn2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBgTyp2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangVerteilerEqIn2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangBuchtEqIn2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSwitchEqIn2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeisteEqIn2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStiftEqIn2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangVerteilerEqOut2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeisteEqOut2(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStiftEqOut2(ObjectTools.getStringSilent(values, columnIndex++));
                String uetv2 = ObjectTools.getStringSilent(values, columnIndex++);
                view.setUetvEqOut2((uetv2 == null) ? null : Uebertragungsverfahren.valueOf(uetv2));
                view.setRangSSTypeEqOut2(ObjectTools.getStringSilent(values, columnIndex));

                retValue.add(view);
            }
        }
        return retValue;
    }

    @Override
    public Map<Long, Rangierung> findForEquipments(final List<Long> eqIds) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Criteria criteria = session.createCriteria(Rangierung.class);
        criteria.add(Restrictions.or(
                Restrictions.in(Rangierung.EQ_IN_ID, eqIds),
                Restrictions.in(Rangierung.EQ_OUT_ID, eqIds)
        ));
        criteria.add(Restrictions.le(AbstractCCHistoryModel.GUELTIG_VON, now));
        criteria.add(Restrictions.gt(AbstractCCHistoryModel.GUELTIG_BIS, now));
        @SuppressWarnings("unchecked")
        List<Rangierung> list = criteria.list();

        Map<Long, Rangierung> result = new HashMap<>();
        for (Rangierung rangierung : list) {
            result.put(rangierung.getEqInId(), rangierung);
            result.put(rangierung.getEqOutId(), rangierung);
        }

        return result;
    }

    @Override
    public Rangierung update4History(Rangierung obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new Rangierung(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
