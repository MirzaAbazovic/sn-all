/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 16:46:52
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.hurrican.dao.cc.HVTBestellungDAO;
import de.augustakom.hurrican.model.cc.EqVerwendung;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;


/**
 * Hibernate DAO-Implementierung von <code>HVTBestellungDAO</code>.
 *
 *
 */
public class HVTBestellungDAOImpl extends Hibernate4DAOImpl implements HVTBestellungDAO, InitializingBean {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private HVTBestellungDAOJdbcImpl daoJdbc = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        daoJdbc = new HVTBestellungDAOJdbcImpl();
        daoJdbc.setDataSource(SessionFactoryUtils.getDataSource(getSessionFactory()));
    }

    @Override
    public List<HVTBestellungView> findHVTBestellungViews() {
        return daoJdbc.findHVTBestellungViews();
    }

    @Override
    public List<EquipmentBelegungView> findEQs4Uevt(Long uevtId, String leiste1) {
        return daoJdbc.findEQs4Uevt(uevtId, leiste1);
    }

    @Override
    public void loadStifte(EquipmentBelegungView eqView) {
        daoJdbc.loadStifte(eqView);
    }

    @Override
    public Integer getCountStifteRangiert(Long hvtIdStandort, String uevt, String rangLeiste1) {
        return daoJdbc.getCountStifteRangiert(hvtIdStandort, uevt, rangLeiste1);
    }

    @Override
    public int getCuDACount4Bestellung(Long hvtBestellungId) {
        return daoJdbc.getCuDACount4Bestellung(hvtBestellungId);
    }

    @Override
    public String getHighestKvzDA(Long hvtIdStandort, String kvzNummer) {
        return daoJdbc.getHighestKvzDA(hvtIdStandort, kvzNummer);
    }

    /*
     * JDBC DAO-Implementierung fuer Abfragen, die ueber Hibernate
     * nur schwer realisierbaren sind.
     */
    static class HVTBestellungDAOJdbcImpl extends JdbcDaoSupport {
        public List<HVTBestellungView> findHVTBestellungViews() {
            StringBuilder sql = new StringBuilder("select hg.ORTSTEIL, hs.ASB, hb.ID as BEST_ID, ");
            sql.append(" hb.ANGEBOT_DATUM, hb.PHYSIKTYP, hb.ANZAHL_CUDA, hb.REAL_DTAG_BIS, ");
            sql.append(" hb.BESTELLNR_AKOM, hb.BESTELLNR_DTAG, u.UEVT_ID, u.UEVT, u.SCHWELLWERT, hb.EQ_VERWENDUNG ");
            sql.append(" from T_HVT_GRUPPE hg ");
            sql.append(" inner join T_HVT_STANDORT hs on hg.HVT_GRUPPE_ID=hs.HVT_GRUPPE_ID ");
            sql.append(" left join T_UEVT u on u.HVT_ID_STANDORT=hs.HVT_ID_STANDORT ");
            sql.append(" left join T_HVT_BESTELLUNG hb on (hb.UEVT_ID=u.UEVT_ID and hb.BEREITGESTELLT is null)");

            return getJdbcTemplate().query(sql.toString(), new HVTBestellungViewRowMapper());
        }

        public List<EquipmentBelegungView> findEQs4Uevt(Long uevtId, String leiste1) {
            List<Object> params = new ArrayList<>();
            params.add(uevtId);

            StringBuilder sql = new StringBuilder("select eq.HVT_ID_STANDORT, s.NAME as SWITCH_NAME, eq.RANG_BUCHT, ");
            sql.append(" eq.RANG_LEISTE1, eq.RANG_SCHNITTSTELLE from T_EQUIPMENT eq ");
            sql.append(" inner join T_HVT_STANDORT h on eq.HVT_ID_STANDORT=h.HVT_ID_STANDORT ");
            sql.append(" left join T_HW_SWITCH s on eq.SWITCH=s.ID ");
            sql.append(" inner join T_UEVT u on u.HVT_ID_STANDORT=h.HVT_ID_STANDORT ");
            sql.append(" where u.UEVT_ID=? and eq.RANG_BUCHT=u.UEVT ");
            if (StringUtils.isNotBlank(leiste1)) {
                sql.append(" and eq.RANG_LEISTE1=? ");
                params.add(leiste1);
            }
            sql.append(" group by s.NAME, eq.RANG_BUCHT, eq.RANG_LEISTE1, eq.RANG_SCHNITTSTELLE, eq.HVT_ID_STANDORT ");

            List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql.toString(), params.toArray());
            if (result != null) {
                List<EquipmentBelegungView> retVal = new ArrayList<>();
                for (Map<String, Object> values : result) {
                    EquipmentBelegungView view = new EquipmentBelegungView();
                    view.setHvtIdStandort(MapTools.getLong(values, "HVT_ID_STANDORT"));
                    view.setSwitchAK(MapTools.getString(values, "SWITCH_NAME"));
                    view.setUevt(MapTools.getString(values, "RANG_BUCHT"));
                    view.setLeiste1(MapTools.getString(values, "RANG_LEISTE1"));
                    view.setPhysiktyp(MapTools.getString(values, "RANG_SCHNITTSTELLE"));
                    retVal.add(view);
                }
                return retVal;
            }
            return Collections.emptyList();
        }

        public void loadStifte(EquipmentBelegungView eqView) {
            StringBuilder sql = new StringBuilder("select count(*) as STIFTE_GES, min(e.RANG_STIFT1) as MIN_STIFT, ");
            sql.append(" max(e.RANG_STIFT1) as MAX_STIFT1, max(e.RANG_STIFT2) as MAX_STIFT2 from T_EQUIPMENT e ");
            sql.append(" where e.RANG_BUCHT=? and e.RANG_LEISTE1=? and e.HVT_ID_STANDORT=? ");

            List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql.toString(),
                    eqView.getUevt(), eqView.getLeiste1(), eqView.getHvtIdStandort());
            if ((result != null) && (result.size() == 1)) {
                Map<String, Object> values = result.get(0);
                eqView.setStifteGesamt(MapTools.getInteger(values, "STIFTE_GES"));
                eqView.setStiftMin(MapTools.getString(values, "MIN_STIFT"));

                String maxStift1 = MapTools.getString(values, "MAX_STIFT1");
                String maxStift2 = MapTools.getString(values, "MAX_STIFT2");
                String maxStift;
                if (maxStift2 != null) {
                    if (maxStift1 != null) {
                        int compare = maxStift1.compareTo(maxStift2);
                        maxStift = (compare >= 0) ? maxStift1 : maxStift2;
                    }
                    else {
                        maxStift = maxStift2;
                    }
                }
                else {
                    maxStift = maxStift1;
                }
                eqView.setStiftMax(maxStift);
            }
        }

        public Integer getCountStifteRangiert(Long hvtIdStandort, String uevt, String rangLeiste1) {
            StringBuilder sql = new StringBuilder("select count(*) as RANGIERT from T_EQUIPMENT eq ");
            sql.append(" inner join T_RANGIERUNG r on r.EQ_OUT_ID=eq.EQ_ID ");
            sql.append(" where eq.HVT_ID_STANDORT=? and eq.RANG_BUCHT=? and eq.RANG_LEISTE1=? ");
            sql.append(" and (r.GUELTIG_VON is null or r.GUELTIG_VON<=?) ");
            sql.append(" and (r.GUELTIG_BIS is null or r.GUELTIG_BIS>?)");

            Date now = DateTools.getActualSQLDate();

            return getJdbcTemplate().queryForObject(sql.toString(),
                    new Object[] { hvtIdStandort, uevt, rangLeiste1, now, now }, Integer.class);
        }

        public int getCuDACount4Bestellung(Long hvtBestellungId) {
            String sql = "select sum(h.ANZAHL) from T_HVT_BES_HIST h where h.BEST_ID=? ";
            Integer result = getJdbcTemplate().queryForObject(sql,
                    new Object[] { hvtBestellungId }, Integer.class);

            return (result != null) ? result : 0;
        }

        public String getHighestKvzDA(Long hvtIdStandort, String kvzNummer) {

            return getJdbcTemplate().queryForObject(
                    "select max(KVZ_DA) from T_EQUIPMENT where HVT_ID_STANDORT=? and KVZ_NUMMER=?",
                    new Object[] { hvtIdStandort, kvzNummer }, String.class);
        }

    }

    /* RowMapper fuer Objekte des Typs <code>HVTBestellungView</code>. */
    private static class HVTBestellungViewRowMapper implements RowMapper<HVTBestellungView> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
         */
        public HVTBestellungView mapRow(ResultSet rs, int rowNum) throws SQLException {
            HVTBestellungView view = new HVTBestellungView();
            view.setId((rs.getLong("BEST_ID") > 0) ? rs.getLong("BEST_ID") : null);
            view.setHvtOrtsteil(rs.getString("ORTSTEIL"));
            view.setHvtASB((rs.getInt("ASB") > 0) ? rs.getInt("ASB") : null);
            view.setUevtId((rs.getLong("UEVT_ID") > 0) ? rs.getLong("UEVT_ID") : null);
            view.setUevt(rs.getString("UEVT"));
            view.setUevtSchwellwert((rs.getInt("SCHWELLWERT") > 0) ? rs.getInt("SCHWELLWERT") : null);
            view.setAngebotDatum(rs.getDate("ANGEBOT_DATUM"));
            view.setPhysiktyp(rs.getString("PHYSIKTYP"));
            view.setAnzahlCuDA(rs.getString("ANZAHL_CUDA"));
            view.setRealDTAGBis(rs.getDate("REAL_DTAG_BIS"));
            view.setBestellNrAKom(rs.getString("BESTELLNR_AKOM"));
            view.setBestellNrDTAG(rs.getString("BESTELLNR_DTAG"));
            String eqVerwendungString = rs.getString("EQ_VERWENDUNG");
            view.setEqVerwendung(eqVerwendungString != null ? EqVerwendung.valueOf(eqVerwendungString) : EqVerwendung.STANDARD);
            return view;
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


