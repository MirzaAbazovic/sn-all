/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2010 15:27:48
 */

package de.mnet.migration.common;

import java.util.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Droppt einen Index vor einer Migration. Falls nicht vorhanden -> Ignorieren
 * <p/>
 * Der Index kann z.B. mit der Klasse
 *
 * @see de.mnet.migration.common.SqlPostHook nach der Migration neu erzeugt werden.
 * <p/>
 * Achtung: Diese Klasse ist nich zum Laden zusätzlicher Daten.
 */
public class DropIndexAdditionalData implements MigrationAdditionalData {

    private static final Logger LOGGER = Logger.getLogger(DropIndexAdditionalData.class);

    DataSource dataSource;
    String index;

    @Override
    public List<TransformationResult> call() throws Exception {
        List<TransformationResult> result = new ArrayList<TransformationResult>();
        try {
            new JdbcTemplate(dataSource).execute("DROP INDEX " + index);
        }
        catch (DataAccessException e) {
            LOGGER.warn("call() - Fehler beim Droppen des Index " + index + ". Wahrscheinlich wurde der Index bereits gedroppt.");
            result.add(new TransformationResult(TransformationStatus.WARNING, null, null,
                    "Index " + index + " konnte nicht gedroppt werden", 0L, "DROP_INDEX", e));
        }
        return result;
    }

    @Required
    public void setIndex(String index) {
        this.index = index;
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
