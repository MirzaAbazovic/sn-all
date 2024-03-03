/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 16:40:51
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDluView;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationSearchCriteria;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;

/**
 * Hibernate DAO-Implementierung fuer Hardware-Objekte.
 *
 *
 */
public class HardwareDAOImpl extends Hibernate4DAOImpl implements HardwareDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<HWRack> findRacks(Long hvtIdStandort) {
        return findRacks(hvtIdStandort, null, false);
    }

    @Override
    public <T extends HWRack> List<T> findRacks(final Long hvtIdStandort, final Class<T> typ, final boolean onlyActive) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        StringBuilder hql = new StringBuilder("from ");
        hql.append(typ != null ? typ.getName() : HWRack.class.getName());
        hql.append(" hwr where hwr.");
        hql.append(HWRack.HVT_STANDORT_ID);
        hql.append(" = :hvtId");
        if (onlyActive) {
            hql.append(" and hwr.");
            hql.append(AbstractCCHistoryModel.GUELTIG_VON);
            hql.append(" <= :now and hwr.");
            hql.append(AbstractCCHistoryModel.GUELTIG_BIS);
            hql.append(" > :now");
        }

        Query q = session.createQuery(hql.toString());
        q.setLong("hvtId", hvtIdStandort);
        if (onlyActive) {
            q.setDate("now", now);
        }

        @SuppressWarnings("unchecked")
        List<T> result = q.list();
        return result;
    }

    @Override
    public <T extends HWRack> List<T> findRacksWithBaugruppenTyp(final Long hvtIdStandort, final Class<T> typ,
            final Long hwBgTypId, final boolean onlyActive) {
        List<Object> values = new ArrayList<>();
        final StringBuilder hql = new StringBuilder("select distinct rack from ").append(typ.getName()).append(" rack, ")
                .append(HWBaugruppe.class.getName()).append(" bg, ")
                .append(HWBaugruppenTyp.class.getName()).append(" bgt ")
                .append(" where bg.rackId = rack.id")
                .append(" and bgt.id = bg.hwBaugruppenTyp.id")
                .append(" and bgt.tunneling = ?")
                .append(" and rack.hvtIdStandort = ").append(hvtIdStandort)
                .append(" and bg.eingebaut = ?");
        values.add(HWBaugruppenTyp.Tunneling.VLAN);
        values.add(Boolean.TRUE);
        if (onlyActive) {
            hql.append(" and rack.");
            hql.append(AbstractCCHistoryModel.GUELTIG_VON);
            hql.append(" <= ?");
            hql.append(" and rack.");
            hql.append(AbstractCCHistoryModel.GUELTIG_BIS);
            hql.append(" > ? ");
            Date now = new Date();
            values.add(now);
            values.add(now);
        }
        return find(hql.toString(), values.toArray());
    }

    @Override
    public List<HWBaugruppenTyp> findBaugruppenTypen(String prefix, boolean onlyActive) {
        HWBaugruppenTyp ex = new HWBaugruppenTyp();
        ex.setName(prefix + WildcardTools.DB_WILDCARD);
        if (onlyActive) {
            ex.setIsActive(Boolean.TRUE);
        }

        Example example = Example.create(ex)
                .enableLike()
                .ignoreCase();

        return queryByCreatedExample(example, HWBaugruppenTyp.class);
    }

    @Override
    public List<HWDluView> findEWSDBaugruppen(Long hvtIdStandort, boolean onlyFree) {
        StringBuilder sql = new StringBuilder("select bg.ID as BG_ID, bgt.NAME as BG_TYP, dlu.DLU_NUMBER, ");
        sql.append("bg.MOD_NUMBER ");
        sql.append("from T_HW_BAUGRUPPE bg ");
        sql.append("inner join T_HW_BAUGRUPPEN_TYP bgt on bg.HW_BG_TYP_ID=bgt.ID ");
        sql.append("inner join T_HW_RACK rack on bg.RACK_ID=rack.ID ");
        sql.append("inner join T_HW_RACK_DLU dlu on rack.ID=dlu.RACK_ID ");
        sql.append("where rack.HVT_ID_STANDORT=:hvtIdStandort and bgt.NAME like :name ");
        sql.append("order by dlu.DLU_NUMBER ASC, bg.MOD_NUMBER ASC ");

        String name = onlyFree ? HWBaugruppenTyp.HW_PORT_EWSD_FREE : HWBaugruppenTyp.HW_PORT_EWSD_PREFIX + WildcardTools.DB_WILDCARD;

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong("hvtIdStandort", hvtIdStandort);
        sqlQuery.setParameter("name", name);

        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null && !result.isEmpty()) {
            List<HWDluView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                HWDluView view = new HWDluView();
                view.setHvtIdStandort(hvtIdStandort);
                view.setBaugruppenId(ObjectTools.getLongSilent(values, 0));
                view.setBgTyp(ObjectTools.getStringSilent(values, 1));
                view.setDluNumber(ObjectTools.getStringSilent(values, 2));
                view.setModNumber(ObjectTools.getStringSilent(values, 3));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public HWDslam findHWDslamByIP(String ipAddress) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(HWDslam.class.getName()).append(" hd where hd.");
        hql.append(HWDslam.IP_ADDRESS);
        hql.append("=? and hd.");
        hql.append(AbstractCCHistoryModel.GUELTIG_BIS);
        hql.append("=?");

        @SuppressWarnings("unchecked")
        List<HWDslam> result = find(hql.toString(), new Object[] { ipAddress, DateTools.getHurricanEndDate() });
        if (CollectionTools.isNotEmpty(result)) {
            if (result.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(
                        "Anzahl gefundener DSLAMs ist ungueltig!", 1, result.size());
            }
            return result.get(0);
        }

        return null;
    }

    @Override
    public List<SwitchMigrationView> createSwitchMigrationViews(SwitchMigrationSearchCriteria searchCriteria) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return Collections.emptyList();
        }
        StringBuilder sql = new StringBuilder("select distinct TDN.TDN, AD.PRODAK_ORDER__NO, AD.AUFTRAG_ID, AD.STATUS_ID, ")
                .append("AST.STATUS_TEXT, AD.PROD_ID, PROD.ANSCHLUSSART, VLF.REALISIERUNGSTERMIN, AD.INBETRIEBNAHME, HVTGP.ORTSTEIL, ")
                .append("sw.name as name ")
                .append("FROM t_auftrag_daten ad ")
                .append("inner join t_auftrag_technik at on AD.AUFTRAG_ID = AT.AUFTRAG_ID ")
                .append("inner join t_hw_switch sw on sw.ID = at.HW_SWITCH AND SW.ID = :switchId ")
                .append("inner join t_auftrag_status ast on AD.STATUS_ID = AST.ID ")
                .append("inner join t_endstelle es on AT.AT_2_ES_ID = ES.ES_GRUPPE and ES.ES_TYP = 'B' ")
                .append("left join t_hvt_standort hvtst on ES.HVT_ID_STANDORT = HVTST.HVT_ID_STANDORT ")
                .append("left join t_hvt_gruppe hvtgp on HVTST.HVT_GRUPPE_ID = HVTGP.HVT_GRUPPE_ID ")
                .append("inner join t_produkt prod on AD.PROD_ID = PROD.PROD_ID ")
                .append("left join t_tdn tdn on AT.TDN_ID = TDN.ID ")
                .append("left join t_verlauf vlf ON AD.AUFTRAG_ID = vlf.AUFTRAG_ID ")
                .append("where AT.GUELTIG_VON <= SYSDATE AND AT.GUELTIG_BIS > SYSDATE ")
                .append("AND AD.GUELTIG_VON <= SYSDATE AND AD.GUELTIG_BIS > SYSDATE ")
                .append("AND ast.id < :auftragStatus AND ast.id not in (:auftragStatusStorniert, :auftragStatusAbgesagt) ")
                .append("AND (vlf.ID is null OR vlf.ID in (SELECT MAX(v.id) from t_verlauf v ")
                .append("   WHERE v.auftrag_id = ad.auftrag_id GROUP BY v.auftrag_id)) ");

        List<Integer> billingAuftragIdList = searchCriteria.getBillingAuftragIdList();
        if (billingAuftragIdList != null) {
            if (billingAuftragIdList.size()==1) {
                sql.append("AND AD.PRODAK_ORDER__NO = :billingAuftragId ");
            } else {
                sql.append("AND AD.PRODAK_ORDER__NO IN (:billingAuftragIdListe) ");
            }

        }
        if (searchCriteria.getBaRealisierungVon() != null) {
            sql.append("AND vlf.REALISIERUNGSTERMIN >= :baRealisierungsdatumVon ");
        }
        if (searchCriteria.getBaRealisierungBis() != null) {
            sql.append("AND vlf.REALISIERUNGSTERMIN <= :baRealisierungsdatumBis ");
        }
        if (searchCriteria.getProdId() != null) {
            sql.append("AND PROD.PROD_ID = :prodId ");
        }
        if (searchCriteria.getInbetriebnahmeVon() != null) {
            sql.append("AND AD.INBETRIEBNAHME >= :inbetriebnahmedatumVon ");
        }
        if (searchCriteria.getInbetriebnahmeBis() != null) {
            sql.append("AND AD.INBETRIEBNAHME <= :inbetriebnahmedatumBis ");
        }
        if (searchCriteria.getLimit() != null) {
            sql.append("AND ROWNUM <= :limit ");
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameter("switchId", searchCriteria.getHwSwitch().getId());
        sqlQuery.setParameter("auftragStatus", AuftragStatus.AUFTRAG_GEKUENDIGT.toString());
        sqlQuery.setParameter("auftragStatusStorniert", AuftragStatus.STORNO.toString());
        sqlQuery.setParameter("auftragStatusAbgesagt", AuftragStatus.ABSAGE.toString());
        if (billingAuftragIdList != null) {
            if (billingAuftragIdList.size()==1) {
                sqlQuery.setParameter("billingAuftragId", billingAuftragIdList.get(0));
            } else {
                sqlQuery.setParameterList("billingAuftragIdListe", billingAuftragIdList);
            }

        }
        if (searchCriteria.getBaRealisierungVon() != null) {
            sqlQuery.setParameter("baRealisierungsdatumVon", searchCriteria.getBaRealisierungVon());
        }
        if (searchCriteria.getBaRealisierungBis() != null) {
            sqlQuery.setParameter("baRealisierungsdatumBis", searchCriteria.getBaRealisierungBis());
        }
        if (searchCriteria.getProdId() != null) {
            sqlQuery.setParameter("prodId", searchCriteria.getProdId());
        }
        if (searchCriteria.getInbetriebnahmeVon() != null) {
            sqlQuery.setParameter("inbetriebnahmedatumVon", searchCriteria.getInbetriebnahmeVon());
        }
        if (searchCriteria.getInbetriebnahmeBis() != null) {
            sqlQuery.setParameter("inbetriebnahmedatumBis", searchCriteria.getInbetriebnahmeBis());
        }
        if (searchCriteria.getLimit() != null) {
            sqlQuery.setParameter("limit", searchCriteria.getLimit());
        }

        return executySwitchMigrationQuery(sqlQuery);
    }

    private List<SwitchMigrationView> executySwitchMigrationQuery(SQLQuery sqlQuery) {
        List<SwitchMigrationView> retVal = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null && !result.isEmpty()) {
            for (Object[] values : result) {
                int columnIndex = 0;
                SwitchMigrationView view = new SwitchMigrationView();
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBillingAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProdId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProdukt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBaRealisierung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setTechLocation(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSwitchKennung(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
        }
        return retVal;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public HWRack findActiveRackByBezeichnung(@Nonnull String bezeichnung) {
        Date now = new Date();

        StringBuilder hql = new StringBuilder("from ");
        hql.append(HWRack.class.getName());
        hql.append(" hwr where hwr.");
        hql.append(HWRack.GERAETE_BEZ);
        hql.append(" = ? and hwr.");
        hql.append(HWRack.GUELTIG_VON);
        hql.append(" <= ? and hwr.");
        hql.append(HWRack.GUELTIG_BIS);
        hql.append(" > ?");

        @SuppressWarnings("unchecked")
        List<HWRack> result = find(hql.toString(), new Object[] { bezeichnung, now, now });
        if (CollectionTools.isNotEmpty(result)) {
            if (result.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(
                        "Anzahl gefundener aktiver Rack ist ungueltig!", 1, result.size());
            }
            return result.get(0);
        }

        return null;
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends HWOltChild> List<T> findHWOltChildByOlt(@Nonnull Long oltId, Class<T> clazz) {
        DetachedCriteria crit = DetachedCriteria.forClass(clazz);
        crit.add(Property.forName("oltRackId").eq(oltId));
        return (List<T>) crit.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends HWOltChild> List<T> findHWOltChildBySerialNo(@Nonnull String serialNo, Class<T> clazz) {
        DetachedCriteria crit = DetachedCriteria.forClass(clazz);
        crit.add(Property.forName("serialNo").eq(serialNo));

        return (List<T>) crit.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    public String findRackType4EqIn(Long rangierungId) {
        if (rangierungId != null) {
            StringBuilder sql = new StringBuilder("SELECT rack.RACK_TYP from T_RANGIERUNG rang ");
            sql.append(" LEFT JOIN T_EQUIPMENT port on rang.EQ_IN_ID = port.EQ_ID ");
            sql.append(" LEFT JOIN T_HW_BAUGRUPPE bg on port.HW_BAUGRUPPEN_ID = bg.ID ");
            sql.append(" LEFT JOIN T_HW_RACK rack on bg.RACK_ID = rack.ID ");
            sql.append(" WHERE (rang.GUELTIG_VON is null OR rang.GUELTIG_VON <= SYSDATE) ");
            sql.append(" AND (rang.GUELTIG_BIS is null OR rang.GUELTIG_BIS > SYSDATE) ");
            sql.append(" AND (port.GUELTIG_VON is null OR port.GUELTIG_VON <= SYSDATE) ");
            sql.append(" AND (port.GUELTIG_BIS is null OR port.GUELTIG_BIS > SYSDATE) ");
            sql.append(" AND rack.GUELTIG_VON <= SYSDATE AND rack.GUELTIG_BIS > SYSDATE ");
            sql.append(" AND rang.RANGIER_ID = ?");

            Session session = sessionFactory.getCurrentSession();
            SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
            sqlQuery.setLong(0, rangierungId);
            @SuppressWarnings("unchecked")
            List<String> result = sqlQuery.list();
            if ((result != null) && (result.size() == 1)) {
                return result.get(0);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HWRack findRack4EqInOfRangierung(Long rangierungId) {
        final String hqlFindRack = "select rack from HWRack rack, HWBaugruppe bg, Equipment eqIn, Rangierung rang "
                + "where eqIn.id = rang.eqInId "
                + "and bg.id = eqIn.hwBaugruppenId "
                + "and bg.rackId = rack.id "
                + "and rang.id = :rangierungId";

        return (HWRack) sessionFactory
                .getCurrentSession()
                .createQuery(hqlFindRack)
                .setLong("rangierungId", rangierungId)
                .uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HWOlt findHwOltForRack(final HWRack hwOltChild)  {
        final HWOlt result;
        if(hwOltChild instanceof HWOltChild) {
            final String hqlOlt = "select olt from HWOlt olt where olt.id = :oltId";
            result = (HWOlt) sessionFactory
                    .getCurrentSession()
                    .createQuery(hqlOlt)
                    .setLong("oltId", ((HWOltChild)hwOltChild).getOltRackId())
                    .uniqueResult();
        }
        else {
            result = null;
        }
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
