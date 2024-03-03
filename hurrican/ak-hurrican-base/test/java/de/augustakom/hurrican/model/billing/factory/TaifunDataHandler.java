/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2014
 */
package de.augustakom.hurrican.model.billing.factory;

import static de.augustakom.hurrican.model.billing.factory.TaifunCheckSqlMapper.*;

import java.lang.reflect.*;
import java.util.*;
import javax.sql.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testng.Assert;

import de.augustakom.common.tools.lang.DateTools;

/**
 * Klasse soll nur von TaifunDataFactory verwendet werden, um die per Builder erzeugten Taifun Entitaeten
 * in der DB zu speichern.
 */
public class TaifunDataHandler {

    private static final Logger LOGGER = Logger.getLogger(TaifunDataHandler.class);

    private JdbcTemplate taifunJdbcTemplate;
    private NamedParameterJdbcOperations hurricanJdbcTemplate;

    TaifunDataHandler configure(DataSource taifunDataSource, DataSource hurricanDataSource) {
        this.taifunJdbcTemplate = new JdbcTemplate(taifunDataSource);
        this.hurricanJdbcTemplate = new NamedParameterJdbcTemplate(hurricanDataSource);
        return this;
    }
    void insert(Object toInsert) {
        taifunJdbcTemplate.execute(getSql(true, toInsert));
    }


    void update(Object toUpdate) {
        taifunJdbcTemplate.execute(getSql(false, toUpdate));
    }


    /**
     * Ermittelt zu der anzulegenden (bzw. zu aktualisierenden) Test-Entity das notwendige SQL-Statement
     * und ersetzt die darin vorhandenen Platzhaler ( ${my_placeholder} ) mit dem jeweiligen Property der Test-Entitaet.
     *
     * Die Platzhalter werden ueber {@link StrSubstitutor} ersetzt. Somit koennen im SQL auch ganz einfach Default-Values
     * definiert werden. <br/>
     * Beispiel: ${undefinedPlaceholder:-NULL} - resultiert in der Angabe von NULL, falls fuer 'undefinedPlaceholder'
     * kein Wert gefunden wird.
     *
     * @param useInsert
     * @param object
     * @return
     */
    private String getSql(boolean useInsert, Object object) {
        try {
            String fileName = (useInsert)
                    ? String.format("/de/augustakom/hurrican/model/billing/factory/sqls/%s_insert.sql", object.getClass().getSimpleName())
                    : String.format("/de/augustakom/hurrican/model/billing/factory/sqls/%s_update.sql", object.getClass().getSimpleName());

            String sql = IOUtils.toString(getClass().getResourceAsStream(fileName));

            // Platzhalter im SQL durch die Werte aus 'object' ersetzen
            Map values = BeanUtils.describe(object);
            formatDatePatterns(values, object);
            StrSubstitutor substitutor = new StrSubstitutor(values);
            String sqlWithValues = substitutor.replace(sql);

            // falls durch die einfache Ersetzung der Placeholder im SQL ein 'NULL' (inkl. der Anfuehrungszeichen)
            // entsteht, soll daraus einfach ein NULL (ohne Anfuehrungszeichen) gemacht werden!
            sqlWithValues = StringUtils.replace(sqlWithValues, "'NULL'", "NULL");

            return sqlWithValues;
        }
        catch (Exception e) {
            LOGGER.error("Error loading SQL!", e);
            return null;
        }
    }


    private void formatDatePatterns(Map<String, String> values, Object object) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (String propertyName : values.keySet()) {
            Object result = PropertyUtils.getProperty(object, propertyName);
            if (result instanceof Date) {
                values.put(propertyName, DateTools.formatDate((Date) result, "MM/dd/yyyy HH:mm:ss"));
            }
        }
    }

    public Long getNextId(Class taifunClazz) {
        String sql = String.format("SELECT %s.nextVal FROM DUAL", getSequenceName(taifunClazz));
        String checkHurricanSql = TaifunCheckSqlMapper.getHurricanCheckSQL(taifunClazz);
        int tryCount = 0;
        Long id;

        // if Hurrican and Taifun dumps are not dumped at same time, sequences will become inconsistent
        // here we try to catch up until a valid ID is found via seq.nextval, or give up after 5000 tries
        while (true) {
            id = taifunJdbcTemplate.queryForObject(sql, Long.class);
            if (checkHurricanSql != null) {
                Long checkCount = hurricanJdbcTemplate.queryForObject(checkHurricanSql, new MapSqlParameterSource(ID_PARAM, id), Long.class);
                if (checkCount == 0L) {
                    break;
                }
                else if (tryCount > 5000) {
                    Assert.assertEquals(checkCount.longValue(), 0L,
                            String.format("Attentation! Hurrican database is inconsistent, the ID '%s' of %s is already "
                                    + "created in the Hurrican DB. Enter '%s' to check your database!", id, taifunClazz.getSimpleName(), sql));
                    break;
                }
                ++tryCount;
            }
            else {
                break;
            }
        }

        return id;
    }

    /**
     * checks if generated ID of the assigned Taifun {@link Class} is not present in some Hurrican tables.
     *
     * @param id          the generated id as {@link Long}
     * @param taifunClazz the referencing class for the id
     */
    public void checkHurricanConsistence(Long id, Class taifunClazz) {
        String sql = TaifunCheckSqlMapper.getHurricanCheckSQL(taifunClazz);
        if (sql != null) {
            final Long count = hurricanJdbcTemplate.queryForObject(sql, new MapSqlParameterSource(ID_PARAM, id), Long.class);
            Assert.assertEquals(count.longValue(), 0L,
                    String.format("Attentation! Hurrican database is inconsistent, the ID '%s' of %s is already "
                            + "created in the Hurrican DB. Enter '%s' to check your database!", id, taifunClazz.getSimpleName(), sql));
        }
    }

    /**
     * Ermittelt den Sequence-Namen, der zur ID-Generierung fuer die aktuelle Entitaet verwendet werden soll.
     * @return
     */
    private String getSequenceName(Class taifunClazz) {
        return (taifunClazz != null)
                ? TaifunSequenceMapper.getSequenceName(taifunClazz)
                : null;
    }

}
