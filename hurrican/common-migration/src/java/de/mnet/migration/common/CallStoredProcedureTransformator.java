/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2009 17:54:07
 */
package de.mnet.migration.common;

import java.util.*;
import javax.sql.*;

import de.mnet.migration.common.main.MigrationStoredProcedure;
import de.mnet.migration.common.result.TransformationResult;


/**
 * Implementation of CallStoredProcedureTransformator
 */
public class CallStoredProcedureTransformator extends MigrationTransformator<Void> {

    public static final String PARAM_MIGRESULT_ID = "iMIG_RESULT_ID";
    private DataSource dataSource;
    private String procedureName;

    public CallStoredProcedureTransformator() {
        super();
        setReadResultFromDatabase(true);
    }

    @Override
    public TransformationResult transform(Void dioV) {
        try {
            MigrationStoredProcedure myStoredProcedure = new MigrationStoredProcedure(dataSource, procedureName);
            StringBuilder result = new StringBuilder();
            Map<String, Object> resultMap = myStoredProcedure.execute(getMigResultId());
            if (resultMap != null) {
                for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                    if (result.length() > 0) {
                        result.append(", ");
                    }
                    result.append("[" + entry.getKey() != null ? entry.getKey() : "null");
                    result.append(" = ");
                    result.append(entry.getValue() != null ? entry.getValue().toString() : "null");
                }
            }
            return ok(source(procedureName), target(procedureName), "Result: " + result.toString());
        }
        catch (Exception e) {
            return error(source(procedureName), e.getMessage(), CLASS_DEFAULT, "", e);
        }
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
