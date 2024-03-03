/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2005 08:04:53
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessResourceFailureException;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.dao.base.impl.DataSourceCreateDAO;
import de.augustakom.hurrican.dao.cc.ScvViewDAO;
import de.augustakom.hurrican.dao.cc.utils.IncompleteAVWithNameRowMapper;
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;


/**
 * DAO-Implementierung von <code>ScvViewDAO</code>. <br> <br> Die Connection fuer die Embedded-DB wird in dieser Klasse
 * selbst aufgebaut und nicht ueber IoC (Spring) uebergeben! Der Grund dafuer ist, dass die Connection nur dann
 * aufgebaut werden soll, wenn sie auch wirklich benoetigt wird. Wuerde die Connection (bzw. DataSource) in den
 * Konfig-Dateien von Spring definiert werden, wuerde sie bereits beim Start-Up geoeffnet werden.
 *
 *
 */
public class ScvViewDAOImplJdbc extends DataSourceCreateDAO implements ScvViewDAO {

    private static final Logger LOGGER = Logger.getLogger(ScvViewDAOImplJdbc.class);
    private ResourceReader resourceReader = null;

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#initializeDB()
     */
    @Override
    public void initializeDB() throws DataAccessResourceFailureException {
        resourceReader = new ResourceReader("de.augustakom.hurrican.dao.cc.resources.ScvViewDB");
        createDataSource();
        dropDataStructure();
        createDataStructure();
    }

    /* Entfernt alle Tabellen aus der DB-Struktur. */
    private void dropDataStructure() {
        String drop = resourceReader.getValue("drop.tables");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Drop SCV-View Tables: " + drop);
        }
        getJdbcTemplate().execute(drop);
    }

    /* Erzeugt die Struktur der Embedded-DB. */
    private void createDataStructure() {
        String create = resourceReader.getValue("create.tables");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Create SCV-View Tables: " + create);
        }
        getJdbcTemplate().execute(create);
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#shutdownDB()
     */
    @Override
    public void shutdownDB() {
        getJdbcTemplate().execute(resourceReader.getValue("shutdown"));
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#insert(null)
     */
    @Override
    public void insert(List<IncompleteAuftragView> toInsert) {
        if (toInsert != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO SCV_VIEW (AUFTRAG_ID, PRODAK_ORDER__NO, TDN, KUNDE__NO, PRODUKTNAME, ");
            sql.append("PRODUKTGRUPPE, AUFTRAGSART, VORGABE_SCV, BA_ANLASS, BA_REALISIERUNGSTERMIN, ");
            sql.append("BA_AN_DISPO, CB_BESTELLT_AM, CB_ZURUECK_AM, CB_LBZ, CB_KUENDIGUNG_AN, ");
            sql.append("CB_KUENDIGUNG_ZURUECK, NAME, VORNAME, BEARBEITER, NIEDERLASSUNG) ");
            sql.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            for (IncompleteAuftragView view : toInsert) {
                List<Object> values = new ArrayList<Object>();
                values.add(view.getAuftragId());
                values.add(view.getAuftragNoOrig());
                values.add(view.getVbz());
                values.add(view.getKundeNo());
                values.add(view.getProduktName());
                values.add(view.getProduktGruppe());
                values.add(view.getAuftragsart());
                values.add(view.getVorgabeSCV());
                values.add(view.getBaAnlass());
                values.add(view.getBaRealTermin());
                values.add(view.getBaAnDispo());
                values.add(view.getCbBestelltAm());
                values.add(view.getCbZurueckAm());
                values.add(view.getCbLbz());
                values.add(view.getCbKuendigungAm());
                values.add(view.getCbKuendigungZurueck());
                values.add(view.getName());
                values.add(view.getVorname());
                values.add(view.getBearbeiter());
                values.add(view.getNiederlassung());

                getJdbcTemplate().update(sql.toString(), values.toArray());
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findAll()
     */
    @Override
    public List<IncompleteAuftragView> findAll() {
        return getJdbcTemplate().query("select * from SCV_VIEW order by VORGABE_SCV", new IncompleteAVWithNameRowMapper());
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findWithoutBA()
     */
    @Override
    public List<IncompleteAuftragView> findWithoutBA() {
        return getJdbcTemplate().query(
                "select * from SCV_VIEW where BA_AN_DISPO is null order by VORGABE_SCV",
                new IncompleteAVWithNameRowMapper());
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findWithoutBAUeberfaellig()
     */
    @Override
    public List<IncompleteAuftragView> findWithoutBAUeberfaellig() {
        StringBuilder sql = new StringBuilder("select * from SCV_VIEW where ");
        sql.append(" VORGABE_SCV>=? and BA_AN_DISPO is null order by VORGABE_SCV");

        return getJdbcTemplate().query(sql.toString(),
                new Object[] { DateTools.getActualSQLDate() }, new IncompleteAVWithNameRowMapper());
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findWithoutLbz()
     */
    @Override
    public List<IncompleteAuftragView> findWithoutLbz() {
        StringBuilder sql = new StringBuilder("select * from SCV_VIEW where ");
        sql.append(" CB_BESTELLT_AM is not null and CB_ZURUECK_AM is null ");
        sql.append(" and (CB_LBZ is null or CB_LBZ=?) order by CB_BESTELLT_AM ");

        return getJdbcTemplate().query(sql.toString(), new Object[] { "" }, new IncompleteAVWithNameRowMapper());
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findCuDaBestellungen()
     */
    @Override
    public List<IncompleteAuftragView> findCuDaBestellungen() {
        StringBuilder sql = new StringBuilder("select * from SCV_VIEW where ");
        sql.append(" CB_BESTELLT_AM is not null and CB_BESTELLT_AM<=? and ");
        sql.append(" ((CB_LBZ is null or CB_LBZ=?) or CB_ZURUECK_AM is null) order by CB_BESTELLT_AM ");

        Date changed = DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -7);
        java.sql.Date past = new java.sql.Date(changed.getTime());

        return getJdbcTemplate().query(sql.toString(), new Object[] { past, "" }, new IncompleteAVWithNameRowMapper());
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findCuDaKuendigungen()
     */
    @Override
    public List<IncompleteAuftragView> findCuDaKuendigungen() {
        StringBuilder sql = new StringBuilder("select * from SCV_VIEW where ");
        sql.append(" CB_KUENDIGUNG_AN is not null and CB_KUENDIGUNG_AN<=? ");
        sql.append(" and CB_KUENDIGUNG_ZURUECK is null order by CB_KUENDIGUNG_AN ");

        Date changed = DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -7);
        java.sql.Date past = new java.sql.Date(changed.getTime());

        return getJdbcTemplate().query(sql.toString(), new Object[] { past }, new IncompleteAVWithNameRowMapper());
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ScvViewDAO#findByDates(java.util.Date, java.util.Date)
     */
    @Override
    public List<IncompleteAuftragView> findByDates(Date vorgabeScv, Date realDate) {
        List<Object> params = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("select * from SCV_VIEW ");
        if ((vorgabeScv != null) || (realDate != null)) {
            sql.append(" where ");
            if (vorgabeScv != null) {
                sql.append(" VORGABE_SCV=? ");
                params.add(vorgabeScv);
            }
            if (realDate != null) {
                if (!params.isEmpty()) { sql.append(" and "); }
                sql.append(" BA_REALISIERUNGSTERMIN=? ");
                params.add(realDate);
            }
        }

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new IncompleteAVWithNameRowMapper());
    }

}


