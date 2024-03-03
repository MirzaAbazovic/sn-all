/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2011 15:04:39
 */
package de.mnet.hurrican.scripts.impl;

import java.util.*;
import oracle.jdbc.OracleDriver;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.MapTools;
import de.mnet.hurrican.scripts.AbstractHurricanDataSourceScript;


/**
 * Migiert die EG-Typen von der T_EG_CONFIG Tabelle in die T_EG_TYPES Tabelle
 */
public class EGTypesMigratorScript extends AbstractHurricanDataSourceScript {

    private static final Logger LOGGER = Logger.getLogger(EGTypesMigratorScript.class);

    private static final String DS_HURRICAN = "ds.hurrican";

    private static final Integer INDEX_HERSTELLER_ALT = 0;
    private static final Integer INDEX_MODELL_ALT = 1;
    private static final Integer INDEX_HERSTELLER_NEU = 2;
    private static final Integer INDEX_MODELL_NEU = 3;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //Achtung! Produktiv Einstellungen nie committen!
        //        createDataSource(DS_HURRICAN, OracleDriver.class,
        //                "jdbc:oracle:thin:@hcprod01-vip.m-net.de:1524:HCPROD01", "HURRICAN", "1nacirruh");
        createDataSource(DS_HURRICAN, OracleDriver.class,
                "jdbc:oracle:thin:@mnetdbsvr15.m-net.de:1521:HCDEV01", "HURRICAN_RUSTEBERGKA", "1nacirruh");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        closeDataSource(DS_HURRICAN);
    }

    @Override
    public void executeScript() {

        // sollte nur manuell ausgeführt werden

        //        LOGGER.info("Script zur EG-Typen Migration wurde gestartet.");
        //        migrateEGTypenToEGType();
        //        LOGGER.info("Script zur EG-Typen Migration wurde beendet.");
    }

    private void migrateEGTypenToEGType() {
        int errors = 0;
        Integer success = 0;
        String herstellerNeu;
        String modellNeu;

        String deleteEgConfigReferencesSQL = "UPDATE T_EG_CONFIG SET EG_TYPE_ID = NULL";
        String deleteAllEgTypesSQL = "DELETE FROM T_EG_TYPE";
        String distinctEgConfigSQL = "SELECT DISTINCT HERSTELLER, MODELL FROM T_EG_CONFIG ORDER BY MODELL";
        String insertEgTypeSQL = "INSERT INTO T_EG_TYPE (ID, HERSTELLER, MODELL, USERW, VERSION) VALUES (S_T_EG_TYPE_0.nextval, ?, ?, 'MIGRATION', 1)";
        String selectIDFromEgTypeSQL = "SELECT ID FROM T_EG_TYPE WHERE HERSTELLER=? AND MODELL=?";
        String updateEgConfigSQL = "UPDATE T_EG_CONFIG SET EG_TYPE_ID = ? ";

        getJdbcTemplate(DS_HURRICAN).update(deleteEgConfigReferencesSQL);
        getJdbcTemplate(DS_HURRICAN).update(deleteAllEgTypesSQL);

        List<Map<String, Object>> result = getJdbcTemplate(DS_HURRICAN).queryForList(distinctEgConfigSQL);
        if (result != null) {
            for (Map<String, Object> record : result) {
                String hersteller = MapTools.getString(record, "HERSTELLER");
                String modell = MapTools.getString(record, "MODELL");
                if (StringUtils.isBlank(hersteller)) {
                    herstellerNeu = "unbekannt";
                }
                else {
                    herstellerNeu = hersteller;
                }

                if (StringUtils.isBlank(modell)) {
                    modellNeu = "unbekannt";
                }
                else {
                    modellNeu = modell;
                }

                success = getJdbcTemplate(DS_HURRICAN).update(insertEgTypeSQL, new Object[] { herstellerNeu, modellNeu });
                if (success == 1) {
                    Long typeID = getJdbcTemplate(DS_HURRICAN).queryForLong(selectIDFromEgTypeSQL, new Object[] { herstellerNeu, modellNeu });
                    if (typeID != null) {
                        String whereClause = "WHERE ";
                        List<Object> parameters = new ArrayList<Object>(1);
                        parameters.add(typeID);
                        if (hersteller == null) {
                            whereClause = whereClause + "HERSTELLER IS NULL";
                        }
                        else {
                            whereClause = whereClause + "HERSTELLER=?";
                            parameters.add(hersteller);
                        }
                        whereClause = whereClause + " AND ";
                        if (modell == null) {
                            whereClause = whereClause + "MODELL IS NULL";
                        }
                        else {
                            whereClause = whereClause + "MODELL=?";
                            parameters.add(modell);
                        }

                        success = getJdbcTemplate(DS_HURRICAN).update(updateEgConfigSQL + whereClause, parameters.toArray());

                        if (success < 1) {
                            LOGGER.error("Die Referenz vom Engerätetyp (ID=" + typeID.toString() + ") konnte in der EG_CONFIG nicht eingetragen werden");
                            LOGGER.error("STATEMENT: " + updateEgConfigSQL + " (" + typeID + hersteller + modell + ")");
                            errors++;
                        }
                    }
                    else {
                        LOGGER.error("Konnte zuvor eingetragenen Datensatz nicht finden (Hersteller=" + hersteller + ", modell=" + modell + ")");
                        errors++;
                    }
                }
                else {
                    LOGGER.error("Konnte Datensatz nicht hinzufügen (" + insertEgTypeSQL + ")");
                    errors++;
                }
            }
        }
        LOGGER.info("Die Migration der EG-Typen wurde mit " + errors + " Fehlern durchgeführt");
    }
}


