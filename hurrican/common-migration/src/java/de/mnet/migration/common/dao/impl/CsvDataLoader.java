/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 16:48:44
 */
package de.mnet.migration.common.dao.impl;

import java.nio.charset.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.supercsv.prefs.CsvPreference;

import de.mnet.migration.common.dao.DataLoader;
import de.mnet.migration.common.util.csv.CsvMapper;

/**
 * @param <TYPE> Datentyp des Source-Objekt, auf das der CsvMappper abbildet.
 */
public class CsvDataLoader<TYPE> implements DataLoader<TYPE> {

    private static final Logger LOGGER = Logger.getLogger(CsvDataLoader.class);

    private Class<TYPE> entityClass;
    private boolean hasHeader;
    private Resource csvFile;
    private String charset;

    @Override
    public List<TYPE> getSourceData() {
        Charset cs = Charset.defaultCharset();
        if (charset != null) {
            Charset.forName(charset);
        }
        List<TYPE> result = CsvMapper.map(csvFile, entityClass, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE, hasHeader, cs);
        LOGGER.info(result.size() + " rows from " + csvFile + " loaded and mapped to entity " + getSourceObjectName());
        return result;
    }

    @Override
    public String getSourceObjectName() {
        return entityClass.getCanonicalName();
    }

    @Required
    public void setCsvFile(Resource csvFile) {
        this.csvFile = csvFile;
    }

    @Required
    public void setEntityClass(Class<TYPE> entityClass) {
        this.entityClass = entityClass;
    }

    @Required
    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
