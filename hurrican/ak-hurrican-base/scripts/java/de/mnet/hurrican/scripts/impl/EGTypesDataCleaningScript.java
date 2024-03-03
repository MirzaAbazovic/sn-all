/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2011 09:38:53
 */
package de.mnet.hurrican.scripts.impl;

import java.util.*;
import jxl.*;
import oracle.jdbc.OracleDriver;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.utils.ExcelFileHelper;
import de.mnet.hurrican.scripts.AbstractHurricanDataSourceScript;


/**
 * Säubert Endgerätetypen anhand der einer Excelliste von der Technik
 */
public class EGTypesDataCleaningScript extends AbstractHurricanDataSourceScript {

    private static final Logger LOGGER = Logger.getLogger(EGTypesDataCleaningScript.class);

    private static final String DS_HURRICAN = "ds.hurrican";

    private static final String IMPORT_FILE = "/de/mnet/hurrican/scripts/resources/EG-Typen.xls";

    private static final String PATTERN_NULL_THIS = "<null>";


    private static final Integer INDEX_HERSTELLER_ALT = 0;
    private static final Integer INDEX_MODELL_ALT = 1;
    private static final Integer INDEX_HERSTELLER_NEU = 2;
    private static final Integer INDEX_MODELL_NEU = 3;
    private static final Integer INDEX_MODELL_ZUSATZ_NEU = 4;

    private static final String UNKONWN_CONTENT = "unbekannt";


    @Override
    public void executeScript() {

        // sollte nur manuell ausgeführt werden

        //        LOGGER.info("Script zur EG-Typen Datenbereinigung in Hurrican wurde gestartet.");
        //        cleanEGTypenWithExcelFile();
        //        LOGGER.info("Script zur EG-Typen Datenbereinigung in Hurrican wurde beendet.");
    }

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

    private void cleanEGTypenWithExcelFile() {
        ExcelFileHelper excelHelper = new ExcelFileHelper();
        try {
            int lastRowNo = 0;
            int success = 0;
            int ignored = 0;
            int errors = 0;

            Sheet sheet = excelHelper.loadExcelFile(IMPORT_FILE);

            lastRowNo = sheet.getRows();

            // UPDATE aller leeren Felder auf "unbekannt"
            success = getJdbcTemplate(DS_HURRICAN).update("UPDATE T_EG_CONFIG SET HERSTELLER = '" + UNKONWN_CONTENT + "', MODELL ='" + UNKONWN_CONTENT + "' WHERE HERSTELLER IS NULL AND MODELL IS NULL");

            LOGGER.info("\n" + success + " leere Datensätze befüllt! \n");

            //beginnend bei der zweiten Zeile
            for (Integer rowCount = 1; rowCount < lastRowNo; rowCount++) {

                Cell[] row = sheet.getRow(rowCount);

                if (row != null) {
                    LOGGER.info("HERSTELLER ALT  : " + getCellContent(row, INDEX_HERSTELLER_ALT));
                    LOGGER.info("MODELL ALT      : " + getCellContent(row, INDEX_MODELL_ALT));
                    LOGGER.info("HERSTELLER NEU  : " + getCellContent(row, INDEX_HERSTELLER_NEU));
                    LOGGER.info("MODELL NEU      : " + getCellContent(row, INDEX_MODELL_NEU));
                    LOGGER.info("MODELLZUSATZ NEU: " + getCellContent(row, INDEX_MODELL_ZUSATZ_NEU) + "\n");

                    Pair<String, Object[]> updateStatement = buildUpdateStatement(row);
                    if (updateStatement != null) {
                        success = getJdbcTemplate(DS_HURRICAN).update(updateStatement.getFirst(), updateStatement.getSecond());
                        if (success > 0) {
                            LOGGER.info("UPDATE für Zeile " + rowCount.toString() + " mit " + success + "Ersetzungen durchgefuehrt! \n");
                        }
                        else {
                            LOGGER.error(String.format("Zeile %s hat keine Daten fuer einen Update!", rowCount + 1));
                        }
                    }
                    else {
                        LOGGER.error(String.format("Zeile %s hat keine Daten fuer einen Update!", rowCount + 1));
                        errors++;
                    }
                }
                else {
                    LOGGER.error(String.format("Zeile %s ist leer!", rowCount + 1));
                    errors++;
                }
            }
        }
        catch (StoreException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Pair<String, Object[]> buildUpdateStatement(Cell[] row) {
        String content;

        if (row == null) {return null;}

        List<Object> whereParams = new LinkedList<>();
        List<Object> setParams = new LinkedList<>();

        StringBuilder setFields = new StringBuilder();
        StringBuilder whereFields = new StringBuilder();

        content = getCellContent(row, INDEX_HERSTELLER_ALT);
        addParam(whereFields, whereParams, "HERSTELLER", content, " and ");
        content = getCellContent(row, INDEX_MODELL_ALT);
        addParam(whereFields, whereParams, "MODELL", content, " and ");

        content = StringUtils.clean(getCellContent(row, INDEX_HERSTELLER_NEU));
        if (StringUtils.isNotBlank(content)) {
            addParam(setFields, setParams, "HERSTELLER", content, ", ");
        }

        content = StringUtils.clean(getCellContent(row, INDEX_MODELL_NEU));
        if (StringUtils.isNotBlank(content)) {
            addParam(setFields, setParams, "MODELL", content, ", ");
        }

        // optional
        content = StringUtils.clean(getCellContent(row, INDEX_MODELL_ZUSATZ_NEU));
        if (StringUtils.isNotBlank(content)) {
            addParam(setFields, setParams, "MODELLZUSATZ", content, ", ");
        }

        Pair<String, Object[]> result = null;

        if ((whereFields.length() > 0) && (!whereParams.isEmpty())
                && (setFields.length() > 0) && (!setParams.isEmpty())) {
            StringBuilder statement = new StringBuilder();

            statement.append("UPDATE T_EG_CONFIG SET ");
            statement.append(setFields);
            statement.append(" WHERE ");
            statement.append(whereFields);

            setParams.addAll(whereParams);
            LOGGER.info("SQL: " + statement.toString() + " PARAMS: " + setParams.toString());
            result = Pair.create(statement.toString(), setParams.toArray());
        }
        return result;
    }

    private String getCellContent(Cell[] row, int cellIndex) {
        if ((row == null) || (row.length <= 0) || (cellIndex >= row.length)) {return null;}
        return row[cellIndex].getContents();
    }

    private void addParam(StringBuilder fields, List<Object> params, String name, String content, String contatinator) {
        if (!StringUtils.isBlank(content)) {
            if (PATTERN_NULL_THIS.equalsIgnoreCase(content)) {
                content = "null";
            }
            if (fields.length() > 0) {
                fields.append(contatinator);
            }
            fields.append(name);
            fields.append("=?");
            params.add(content);
        }
    }
}


