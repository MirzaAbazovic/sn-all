/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2011 13:25:33
 */
package de.mnet.migration.common.main;

import java.util.*;
import javax.annotation.*;
import javax.sql.*;
import org.apache.log4j.Logger;

import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Additional Data, das eine StoredProcedure aufruft. Diese hat als Parameter iMIG_RESULT_ID. Dafür wird zur Zeit -42
 * übergeben und die Procedure sollte diesen Parameter einfach ignorieren.
 *
 *
 */
public class StoredProcedureAdditionalData implements MigrationAdditionalData {

    private static final Logger LOGGER = Logger.getLogger(StoredProcedureAdditionalData.class);

    private DataSource dataSource;
    private String procedureName;
    MigrationStoredProcedure migrationStoredProcedure;

    @Override
    public List<TransformationResult> call() throws Exception {
        List<TransformationResult> results = new ArrayList<TransformationResult>();
        try {
            migrationStoredProcedure.execute(-42L);
        }
        catch (Exception e) {
            String errorMessage = "Error calling stored procedure " + procedureName;
            LOGGER.error(errorMessage, e);
            new TransformationResult(
                    TransformationStatus.ERROR,
                    null,
                    null,
                    errorMessage,
                    0L, "PROCEDURE_CALL_FAILED",
                    e);
        }

        return results;
    }

    @PostConstruct
    public void setup() {
        migrationStoredProcedure = new MigrationStoredProcedure(dataSource, procedureName);
    }

    /**
     * Injected
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;

    }

    /**
     * Injected
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
