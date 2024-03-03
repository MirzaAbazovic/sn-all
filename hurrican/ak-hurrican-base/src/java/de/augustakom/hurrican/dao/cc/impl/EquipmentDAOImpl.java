/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 14:25:01
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.sql.*;
import java.util.*;
import javax.annotation.*;
import javax.sql.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.utils.HVTBelegungRowMapper;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.query.EquipmentQuery;
import de.augustakom.hurrican.model.cc.view.EqCuDAView;
import de.augustakom.hurrican.model.cc.view.EquipmentInOutView;
import de.augustakom.hurrican.model.cc.view.HVTBelegungView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;


/**
 * Hibernate DAO-Implementierung von <code>EquipmentDAO</code>.
 *
 *
 */
public class EquipmentDAOImpl extends Hibernate4DAOImpl implements EquipmentDAO, InitializingBean {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final Logger LOGGER = Logger.getLogger(EquipmentDAOImpl.class);

    private EquipmentDAOImplJdbc daoJdbc = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        daoJdbc = new EquipmentDAOImplJdbc();
        daoJdbc.setDataSource(SessionFactoryUtils.getDataSource(getSessionFactory()));
    }

    @Override
    public List<HVTBelegungView> find2DrahtBelegt(boolean hochbitratig) {
        return daoJdbc.find2DrahtBelegt(hochbitratig);
    }

    @Override
    public List<HVTBelegungView> find4DrahtBelegt() {
        return daoJdbc.find4DrahtBelegt();
    }

    @Override
    public Integer getEquipmentCount(EquipmentQuery query) {
        if (query == null || query.isEmpty()) { return null; }

        List<Object> params = new ArrayList<>();

        StringBuilder hql = new StringBuilder("select count(*) from ");
        hql.append(Equipment.class.getName()).append(" eq where ");
        if (query.getHvtIdStandort() != null) {
            hql.append(" eq.hvtIdStandort=? ");
            params.add(query.getHvtIdStandort());
        }
        if (StringUtils.isNotBlank(query.getRangLeiste1())) {
            if (!params.isEmpty()) {
                hql.append(" and ");
            }
            hql.append(" eq.rangLeiste1=? ");
            params.add(query.getRangLeiste1());
        }
        if (query.getRangSchnittstelle() != null && StringUtils.isNotBlank(query.getRangSchnittstelle().name())) {
            if (!params.isEmpty()) {
                hql.append(" and ");
            }
            hql.append(" eq.rangSchnittstelle=? ");
            params.add(query.getRangSchnittstelle());
        }
        if (StringUtils.isNotBlank(query.getRangSSType())) {
            if (!params.isEmpty()) {
                hql.append(" and ");
            }
            hql.append(" eq.rangSSType=? ");
            params.add(query.getRangSSType());
        }
        if (StringUtils.isNotBlank(query.getRangVerteiler())) {
            if (!params.isEmpty()) {
                hql.append(" and ");
            }
            hql.append(" eq.rangVerteiler=? ");
            params.add(query.getRangVerteiler());
        }
        if (query.getStatus() != null && StringUtils.isNotBlank(query.getStatus().name())) {
            if (!params.isEmpty()) {
                hql.append(" and ");
            }
            hql.append(" eq.status=? ");
            params.add(query.getStatus());
        }

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), params.toArray());
        return result != null && result.size() == 1 ? result.get(0).intValue() : null;
    }

    @Override
    public List<Equipment> findEquipment(Long rackId, String hwEQN, String rangSSType) {
        StringBuilder hql = new StringBuilder("select eq.id from ");
        hql.append(Equipment.class.getName()).append(" eq, ");
        hql.append(HWBaugruppe.class.getName()).append(" hb where ");
        hql.append("hb.rackId=? and hb.id=eq.hwBaugruppenId ");
        hql.append("and eq.hwEQN=? and lower(eq.rangSSType) like ?");

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(),
                new Object[] { rackId, hwEQN, WildcardTools.replaceWildcards(rangSSType).toLowerCase() });
        if (CollectionTools.isNotEmpty(result)) {
            List<Equipment> retVal = new ArrayList<>();
            for (Long eqId : result) {
                retVal.add(findById(eqId, Equipment.class));
            }
            return retVal;
        }

        return null;
    }

    @Override
    public Equipment findEquipmentByBaugruppe(Long hwBaugruppenId, String hwEQN, String rangSSType) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        StringBuilder hql = new StringBuilder("select eq.id from ");
        hql.append(Equipment.class.getName()).append(" eq where ");

        //HWEqn
        hql.append("eq." + Equipment.HW_EQN + "=? ");
        params.add(hwEQN);
        types.add(new StringType());

        //RangSSType
        if (rangSSType != null) {
            hql.append(" and lower(eq." + Equipment.RANG_SS_TYPE + ") like ? ");
            params.add(WildcardTools.replaceWildcards(rangSSType).toLowerCase());
            types.add(new StringType());
        }

        //HWBaugruppenId
        if (hwBaugruppenId != null) {
            hql.append(" and eq." + Equipment.HW_BAUGRUPPEN_ID + " = ? ");
            params.add(hwBaugruppenId);
            types.add(new LongType());
        }

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), params.toArray(new Object[params.size()]));
        if (CollectionTools.hasExpectedSize(result, 1)) {
            Long eqId = result.get(0);
            return findById(eqId, Equipment.class);
        }
        return null;
    }

    @Override
    public List<Equipment> findEquipments(Equipment example, String[] orderParams) {
        List<Equipment> result = getByExampleDAO().queryByExample(example, Equipment.class, orderParams, null);

        if (StringTools.isIn("rangLeiste1", orderParams)) {
            // bei Sortierung auf 'rangLeiste1' muss selbst sortiert werden, da Stift 100 nach 99 erscheinen soll.
            Collections.sort(result, new Comparator<Equipment>() {
                @Override
                public int compare(Equipment eq1, Equipment eq2) {
                    if (NumberUtils.isNumber(eq1.getRangBucht()) && NumberUtils.isNumber(eq2.getRangBucht())) {
                        // manuelle Sortierung - Integer-Werte
                        Integer bucht1 = Integer.valueOf(eq1.getRangBucht());
                        Integer bucht2 = Integer.valueOf(eq2.getRangBucht());
                        if (NumberTools.equal(bucht1, bucht2)) {
                            if (NumberUtils.isNumber(eq1.getRangLeiste1()) && NumberUtils.isNumber(eq2.getRangLeiste1())) {
                                Integer leiste1 = Integer.valueOf(eq1.getRangLeiste1());
                                Integer leiste2 = Integer.valueOf(eq2.getRangLeiste1());
                                if (NumberTools.equal(leiste1, leiste2)) {
                                    Integer stift1 = Integer.valueOf(eq1.getRangStift1());
                                    Integer stift2 = Integer.valueOf(eq2.getRangStift1());
                                    if (NumberTools.equal(stift1, stift2)) {
                                        return 0;
                                    }
                                    else if (NumberTools.isLess(stift1, stift2)) {
                                        return -1;
                                    }

                                    return 1;
                                }
                                else if (NumberTools.isLess(leiste1, leiste2)) {
                                    return -1;
                                }

                                return 1;
                            }
                            else {
                                // Leiste keine Zahl - Fallback auf Stringvergleich
                                if (eq1.getRangLeiste1() != null && eq1.getRangLeiste2() != null) {
                                    return eq1.getRangLeiste1().compareToIgnoreCase(eq2.getRangLeiste1());
                                }
                                else if (eq1.getRangLeiste1() != null && eq1.getRangLeiste2() == null) {
                                    // eq1 != null und eq2 == null -> eq1 > eq2
                                    return 1;
                                }
                                else if (eq1.getRangLeiste1() == null && eq1.getRangLeiste2() != null) {
                                    // eq1 == null und eq2 != null -> eq1 < eq2
                                    return -1;
                                }
                                else {
                                    // eq1 == null und eq2 == null -> eq1 == eq2
                                    return 0;
                                }
                            }
                        }
                        else if (NumberTools.isLess(bucht1, bucht2)) {
                            return -1;
                        }

                        return 1;
                    }
                    else {
                        // Bucht ist null oder kein Integer - Sortierung ueber String-Vergleich
                        return StringTools.compare(eq1.getRangBucht(), eq2.getRangBucht(), true);
                    }
                }
            });
        }

        return result;
    }

    @Override
    public List<EquipmentInOutView> findEqInOutViews(Equipment example) {
        return daoJdbc.findEqInOutViews(example);
    }

    @Override
    public List<UevtCuDAView> createUevtCuDAViews() {
        EqCuDACountMappingQuery query = new EqCuDACountMappingQuery(
                SessionFactoryUtils.getDataSource(getSessionFactory()));

        List<EqCuDAView> eqViews = query.execute(EqStatus.frei.name(), EqStatus.rang.name(), EqStatus.vorb.name());

        UevtCuDAMappingQuery ucQuery = new UevtCuDAMappingQuery(
                SessionFactoryUtils.getDataSource(getSessionFactory()));

        List<UevtCuDAView> ucViews = ucQuery.execute(EqStatus.frei.name(), EqStatus.rang.name(), EqStatus.vorb.name(),
                HVTStandort.HVT_STANDORT_TYP_HVT, HVTStandort.HVT_STANDORT_TYP_KVZ, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);

        if (ucViews != null && eqViews != null) {
            Predicate4CuDACount predicate = new Predicate4CuDACount();
            for (UevtCuDAView ucView : ucViews) {
                // zug. EqCuDAView ermitteln
                predicate.setPredicateValues(ucView.getUevt(), ucView.getHvtIdStandort(),
                        ucView.getCudaPhysik(), ucView.getCarrier(), ucView.getRangSSType());

                predicate.setStatus(EqStatus.frei);
                EqCuDAView frei = (EqCuDAView) CollectionUtils.find(eqViews, predicate);

                predicate.setStatus(EqStatus.rang);
                EqCuDAView rangiert = (EqCuDAView) CollectionUtils.find(eqViews, predicate);

                predicate.setStatus(EqStatus.vorb);
                EqCuDAView vorbereitet = (EqCuDAView) CollectionUtils.find(eqViews, predicate);

                ucView.setCudaFrei(frei != null ? frei.getAnzahl() : null);
                ucView.setCudaRangiert(rangiert != null ? rangiert.getAnzahl() : null);
                ucView.setCudaVorbereitet(vorbereitet != null ? vorbereitet.getAnzahl() : null);
            }
        }

        return ucViews;
    }

    @Override
    public List<Equipment> findEquipments(final Long hvtIdStd, final String rangVerteiler, final String leiste1) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Equipment.class);
        criteria.add(Restrictions.eq(Equipment.HVT_ID_STANDORT, hvtIdStd));
        criteria.add(Restrictions.eq(Equipment.RANG_VERTEILER, rangVerteiler));
        criteria.add(Restrictions.eq(Equipment.RANG_LEISTE1, leiste1));
        criteria.addOrder(Order.asc(Equipment.RANG_STIFT1));
        return criteria.list();
    }


    @Override
    public Equipment findCorrespondingEquipment(Equipment eq) {
        if (eq == null) {
            return null;
        }
        Equipment ex = new Equipment();
        ex.setHwBaugruppenId(eq.getHwBaugruppenId());
        ex.setHwEQN(eq.getHwEQN());
        ex.setHvtIdStandort(eq.getHvtIdStandort());
        ex.setHwSwitch(eq.getHwSwitch());

        Example example = Example.create(ex);
        List<Equipment> resultList = queryByCreatedExample(example, Equipment.class);

        Equipment result = null;
        for (Equipment equipment : resultList) {
            if (!equipment.getId().equals(eq.getId())) {
                if (result != null) {
                    throw new RuntimeException("Found more than one corresponding equipment!");
                }
                result = equipment;
            }
        }
        return result;
    }


    @Override
    public List<Equipment> findEquipmentsForPhysiktyp(final Long hvtStandortId, final Long physiktypId) {
        final StringBuilder builder = new StringBuilder("select {eq.*} from T_EQUIPMENT {eq} ");
        builder.append(" join T_HW_BAUGRUPPE bg on {eq}.HW_BAUGRUPPEN_ID = bg.ID ");
        builder.append(" join T_HW_BAUGRUPPEN_TYP bgt on bg.HW_BG_TYP_ID = bgt.ID ");
        builder.append(" join T_HW_BG_TYP_2_PHYSIK_TYP bg2pt on bgt.ID = bg2pt.BAUGRUPPEN_TYP_ID ");
        builder.append(" join T_HW_RACK r on bg.RACK_ID = r.ID ");
        builder.append(" where bg2pt.PHYSIKTYP_ID = :physiktypId ");
        builder.append("   and r.HVT_ID_STANDORT = :hvtStandortId ");
        builder.append("   and {eq}.STATUS = :status ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(builder.toString()).addEntity("eq", Equipment.class)
                .setLong("physiktypId", physiktypId)
                .setLong("hvtStandortId", hvtStandortId)
                .setString("status", EqStatus.frei.name());

        return query.list();
    }

    @Override
    public void deleteProduktEQConfig(final Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(ProduktEQConfig.class.getName());
        hql.append(" p where p.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, id);
        query.executeUpdate();
    }

    @Override
    public Map<String, Integer> getEquipmentCount(Long hvtIdStd, String rangVerteiler, String leiste1) {
        return daoJdbc.getEquipmentCount(hvtIdStd, rangVerteiler, leiste1);
    }

    @Override
    public List<RangSchnittstelle> findAvailableSchnittstellen4HVT(Long hvtStandortId) {
        if (hvtStandortId == null) {
            return null;
        }
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct e.rangSchnittstelle from ");
        hql.append(Equipment.class.getName());
        hql.append(" e where e.hvtIdStandort=?");
        return find(hql.toString(), hvtStandortId);
    }

    /**
     * JDBC DAO-Implementierung
     */
    static class EquipmentDAOImplJdbc extends JdbcDaoSupport {

        public List<HVTBelegungView> find2DrahtBelegt(boolean hochbitratig) {
            StringBuilder sql = new StringBuilder("select eq.RANG_VERTEILER as UEVT, Count(r.ES_ID) AS BELEGT, ");
            sql.append(" eq.RANG_SCHNITTSTELLE as CUDA_PHYSIK, eq.HVT_ID_STANDORT, eq.RANG_LEISTE1, ");
            sql.append(" eq.RANG_SS_TYPE FROM T_EQUIPMENT eq LEFT JOIN T_RANGIERUNG r ON eq.EQ_ID=r.EQ_OUT_ID ");
            sql.append(" WHERE r.ES_ID Is Not Null AND r.ES_ID<>-1 AND r.GUELTIG_BIS>? ");
            sql.append(" AND eq.RANG_LEISTE2 Is Null ");
            sql.append(" GROUP BY eq.RANG_VERTEILER, eq.RANG_SCHNITTSTELLE, eq.HVT_ID_STANDORT, eq.RANG_LEISTE1, ");
            sql.append(" eq.RANG_SS_TYPE ");
            sql.append(" HAVING eq.RANG_SCHNITTSTELLE Is Not Null and eq.RANG_SCHNITTSTELLE=? ");

            LOGGER.info(sql.toString());
            return getJdbcTemplate().query(sql.toString(),
                    new Object[] { new java.util.Date(), hochbitratig ? "H" : "N" },
                    new HVTBelegungRowMapper());
        }

        public List<HVTBelegungView> find4DrahtBelegt() {
            StringBuilder sql = new StringBuilder("select eq.RANG_VERTEILER as UEVT, Count(r.ES_ID)*2 as BELEGT, ");
            sql.append(" eq.RANG_SCHNITTSTELLE as CUDA_PHYSIK, eq.HVT_ID_STANDORT, eq.RANG_LEISTE1, eq.RANG_SS_TYPE ");
            sql.append(" FROM T_EQUIPMENT eq LEFT JOIN T_RANGIERUNG r ON eq.EQ_ID = r.EQ_OUT_ID ");
            sql.append(" WHERE r.ES_ID Is Not Null And r.ES_ID<>-1 AND r.GUELTIG_BIS>? ");
            sql.append(" AND eq.RANG_LEISTE2 Is Not Null ");
            sql.append(" GROUP BY eq.RANG_VERTEILER, eq.RANG_SCHNITTSTELLE, eq.HVT_ID_STANDORT, eq.RANG_LEISTE1, ");
            sql.append(" eq.RANG_SS_TYPE HAVING eq.RANG_SCHNITTSTELLE Is Not Null");

            LOGGER.info(sql.toString());
            return getJdbcTemplate().query(sql.toString(),
                    new Object[] { new java.util.Date() }, new HVTBelegungRowMapper());
        }

        public Map<String, Integer> getEquipmentCount(Long hvtIdStd, String rangVerteiler, String leiste1) {
            StringBuilder sql = new StringBuilder("select count(eq.EQ_ID) as ANZAHL, eq.RANG_SCHNITTSTELLE ");
            sql.append(" from t_equipment eq  ");
            sql.append(" where hvt_id_standort=? and rang_verteiler=? and rang_leiste1=? ");
            sql.append(" group by RANG_SCHNITTSTELLE ");

            List<Map<String, Object>> result =
                    getJdbcTemplate().queryForList(sql.toString(), hvtIdStd, rangVerteiler, leiste1);
            if (result != null && !result.isEmpty()) {
                Map<String, Integer> retVal = new HashMap<>();
                for (Map<String, Object> values : result) {
                    retVal.put(MapTools.getString(values, "RANG_SCHNITTSTELLE"), MapTools.getInteger(values, "ANZAHL"));
                }
                return retVal;
            }

            return null;
        }

        public List<EquipmentInOutView> findEqInOutViews(Equipment example) {
            List<Object> params = new ArrayList<>();
            params.add(example.getHvtIdStandort());
            params.add(EqStatus.frei.name());
            params.add(example.getHvtIdStandort());
            params.add(EqStatus.frei.name());

            StringBuilder sql = new StringBuilder("select eqIn.EQ_ID as inID, eqIn.HW_EQN as inEQN, ");
            sql.append("eqOut.EQ_ID as outID, eqOut.HW_EQN as outEQN ");
            sql.append("from t_equipment eqIn ");
            sql.append("inner join t_equipment eqOut on eqIn.HW_EQN=eqOut.HW_EQN and eqIn.HW_BAUGRUPPEN_ID=eqOut.HW_BAUGRUPPEN_ID ");
            sql.append("where eqIn.HVT_ID_STANDORT=? and eqIn.STATUS=? ");
            sql.append("and eqOut.HVT_ID_STANDORT=? and eqOut.STATUS=? ");
            if (StringUtils.isNotBlank(example.getHwSchnittstelle())) {
                sql.append("and eqIn.HW_SCHNITTSTELLE=? ");
                sql.append("and eqOut.HW_SCHNITTSTELLE=? ");
                params.add(example.getHwSchnittstelle() + "-IN");
                params.add(example.getHwSchnittstelle() + "-OUT");
            }
            sql.append("order by eqIn.HW_BAUGRUPPEN_ID, eqIn.HW_EQN");

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("SQL: " + sql.toString());
            }

            List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql.toString(), params.toArray());
            if (result != null && !result.isEmpty()) {
                List<EquipmentInOutView> retVal = new ArrayList<>();
                for (Map<String, Object> values : result) {
                    EquipmentInOutView view = new EquipmentInOutView();
                    view.setEqIdIn(MapTools.getLong(values, "inID", null));
                    view.setHwEqnIn(MapTools.getString(values, "inEQN"));
                    view.setEqIdOut(MapTools.getLong(values, "outID", null));
                    view.setHwEqnOut(MapTools.getString(values, "outEQN"));
                    retVal.add(view);
                }
                return retVal;
            }
            return null;
        }
    }


    /**
     * MappingSqlQuery, um nach der CuDA-Anzahl fuer div. Parameter zu suchen.
     */
    static class EqCuDACountMappingQuery extends MappingSqlQuery<EqCuDAView> {

        /* SQL-Statement, um die Anzahl von Stiften zu ermitteln. */
        private static final String CUDA_COUNT_SQL = "select eq.HVT_ID_STANDORT, eq.RANG_VERTEILER as UEVT, eq.RANG_SS_TYPE, " +
                "eq.RANG_SCHNITTSTELLE as CUDA_PHYSIK, eq.CARRIER, eq.STATUS, count(eq.RANG_LEISTE1) as ANZAHL " +
                "FROM T_EQUIPMENT eq " +
                "GROUP BY eq.HVT_ID_STANDORT, eq.RANG_VERTEILER, eq.RANG_SCHNITTSTELLE, eq.CARRIER, eq.STATUS, eq.RANG_SS_TYPE " +
                "having eq.RANG_SCHNITTSTELLE is not null and (eq.STATUS=? or eq.STATUS=? or eq.STATUS=?)";

        /* Konstruktor mit Angabe der DataSource. */
        public EqCuDACountMappingQuery(DataSource ds) {
            super();
            super.setDataSource(ds);
            super.setSql(CUDA_COUNT_SQL);
            super.declareParameter(new SqlParameter("STATUS", Types.CHAR));
            super.declareParameter(new SqlParameter("STATUS", Types.CHAR));
            super.declareParameter(new SqlParameter("STATUS", Types.CHAR));
            compile();
        }

        @Override
        protected EqCuDAView mapRow(ResultSet rs, int row) throws SQLException {
            EqCuDAView view = new EqCuDAView();
            view.setHvtIdStandort(rs.getLong("HVT_ID_STANDORT"));
            view.setUevt(rs.getString("UEVT"));
            view.setCudaPhysik(rs.getString("CUDA_PHYSIK"));
            view.setCarrier(rs.getString("CARRIER"));
            view.setAnzahl(rs.getInt("ANZAHL"));
            view.setStatus(rs.getString("STATUS"));
            view.setRangSSType(rs.getString("RANG_SS_TYPE"));
            return view;
        }
    }


    /**
     * MappingSqlQuery, um eine Uebersicht der UEVTs, HVTs und Equipments zu erhalten.
     */
    static class UevtCuDAMappingQuery extends MappingSqlQuery<UevtCuDAView> {
        public UevtCuDAMappingQuery(DataSource ds) {
            super();
            setDataSource(ds);

            StringBuilder sql = new StringBuilder("select eq.HVT_ID_STANDORT, eq.RANG_VERTEILER as UEVT, ");
            sql.append(" eq.RANG_SCHNITTSTELLE as CUDA_PHYSIK, eq.RANG_SS_TYPE, ");
            sql.append(" eq.CARRIER, hs.ASB, hg.ONKZ, hg.ORTSTEIL, u.UEVT_ID ");
            sql.append(" FROM T_EQUIPMENT eq left join T_HVT_STANDORT hs on hs.HVT_ID_STANDORT=eq.HVT_ID_STANDORT ");
            sql.append(" left join T_HVT_GRUPPE hg on hg.HVT_GRUPPE_ID=hs.HVT_GRUPPE_ID ");
            sql.append(" left join T_UEVT u on (u.UEVT=eq.RANG_VERTEILER and u.HVT_ID_STANDORT=eq.HVT_ID_STANDORT) ");
            sql.append(" WHERE eq.RANG_SCHNITTSTELLE is not null ");
            sql.append(" and eq.STATUS in (?, ?, ?) and hs.STANDORT_TYP_REF_ID in (?,?,?) ");
            sql.append(" GROUP BY eq.HVT_ID_STANDORT, eq.RANG_VERTEILER, ");
            sql.append(" eq.RANG_SCHNITTSTELLE, eq.RANG_SS_TYPE, ");
            sql.append(" eq.CARRIER, hs.ASB, hg.ONKZ, hg.ORTSTEIL, u.UEVT_ID, u.SCHWELLWERT");
            setSql(sql.toString());

            super.declareParameter(new SqlParameter("STATUS", Types.CHAR));
            super.declareParameter(new SqlParameter("STATUS", Types.CHAR));
            super.declareParameter(new SqlParameter("STATUS", Types.CHAR));
            super.declareParameter(new SqlParameter("STANDORT_TYP_REF_ID", Types.INTEGER));
            super.declareParameter(new SqlParameter("STANDORT_TYP_REF_ID", Types.INTEGER));
            super.declareParameter(new SqlParameter("STANDORT_TYP_REF_ID", Types.INTEGER));
            super.compile();
        }

        @Override
        protected UevtCuDAView mapRow(ResultSet rs, int row) throws SQLException {
            UevtCuDAView view = new UevtCuDAView();
            view.setHvtIdStandort(rs.getLong("HVT_ID_STANDORT"));
            view.setUevt(rs.getString("UEVT"));
            view.setCudaPhysik(rs.getString("CUDA_PHYSIK"));
            view.setCarrier(rs.getString("CARRIER"));
            view.setOnkz(rs.getString("ONKZ"));
            view.setAsb(rs.getInt("ASB"));
            view.setHvtName(rs.getString("ORTSTEIL"));
            view.setUevtId(rs.getLong("UEVT_ID"));
            view.setRangSSType(rs.getString("RANG_SS_TYPE"));
            return view;
        }
    }


    /**
     * Predicate, um nach der Anzahl Stifte in einem best. Status zu suchen.
     */
    static class Predicate4CuDACount implements Predicate {
        private String uevt = null;
        private Long hvtIdStandort = null;
        private String cudaPhysik = null;
        private String carrier = null;
        private EqStatus status = null;
        private String rangSSType = null;

        /**
         * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
         *
         * @param uevt
         * @param hvtId
         * @param cudaPhysik
         * @param carrier
         */
        public void setPredicateValues(String uevt, Long hvtId, String cudaPhysik, String carrier, String rangSSType) {
            this.uevt = uevt;
            this.hvtIdStandort = hvtId;
            this.cudaPhysik = cudaPhysik;
            this.carrier = carrier;
            this.rangSSType = rangSSType;
        }

        public void setStatus(EqStatus status) {
            this.status = status;
        }

        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof EqCuDAView) {
                EqCuDAView view = (EqCuDAView) obj;
                if (!StringUtils.equals(view.getUevt(), uevt)) {
                    return false;
                }
                if (!NumberTools.equal(view.getHvtIdStandort(), hvtIdStandort)) {
                    return false;
                }
                if (!StringUtils.equals(view.getCudaPhysik(), cudaPhysik)) {
                    return false;
                }
                if (!StringUtils.equals(view.getStatus(), status.name())) {
                    return false;
                }
                if (!StringUtils.equals(view.getCarrier(), carrier)) {
                    return false;
                }
                if (!StringUtils.equals(view.getRangSSType(), rangSSType)) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }

    @Override
    @Nonnull
    public List<Equipment> findEquipmentsByBaugruppe(@Nonnull Long hwBaugruppenId) {
        DetachedCriteria crit = DetachedCriteria.forClass(Equipment.class);
        crit.add(Property.forName("hwBaugruppenId").eq(hwBaugruppenId));
        return (List<Equipment>) crit.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    public List<Equipment> findEquipments4Kvz(Long hvtStandortId, String kvzNummer) {
        String queryStr = "select e from Equipment e where e.hvtIdStandort = ? and e.kvzNummer = ?";
        return find(queryStr, hvtStandortId, kvzNummer);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
