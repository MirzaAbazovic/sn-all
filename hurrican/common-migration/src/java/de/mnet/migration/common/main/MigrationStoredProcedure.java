/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2010 11:43:10
 */

package de.mnet.migration.common.main;

import java.sql.*;
import java.util.*;
import java.util.Map.*;
import javax.sql.*;
import com.google.common.collect.ImmutableMap;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;


/**
 *
 */
public class MigrationStoredProcedure extends StoredProcedure {
    public static final String PARAM_MIGRESULT_ID = "iMIG_RESULT_ID";

    private static final ImmutableMap<Class<?>, Integer> javaClass2SqlType =
            ImmutableMap.<Class<?>, Integer>of(
                    Long.class, Types.NUMERIC,
                    Integer.class, Types.NUMERIC,
                    String.class, Types.VARCHAR
            );

    public MigrationStoredProcedure(DataSource dataSource, String procedureName) {
        super(dataSource, procedureName);
        declareParameters();
        compile();
    }

    /**
     * Führt die stored procedure mit für das angegebene Result durch.
     *
     * @param migresultId
     * @return
     * @throws DataAccessException
     */
    public Map<String, Object> execute(Long migresultId) throws DataAccessException {
        Map<String, Object> inParams = new HashMap<String, Object>();
        inParams.put(PARAM_MIGRESULT_ID, migresultId);
        return execute(inParams);
    }

    private void declareParameters() {
        Map<String, Object> inParams = new HashMap<String, Object>();
        inParams.put(PARAM_MIGRESULT_ID, 0L);
        Map<String, Integer> paramTypes = convertParams2ParamTypes(inParams);
        for (Entry<String, Integer> entry : paramTypes.entrySet()) {
            declareParameter(new SqlParameter(entry.getKey(), entry.getValue()));
        }
    }

    static public Map<String, Integer> convertParams2ParamTypes(Map<String, Object> params) {
        Map<String, Integer> paramTypes = new HashMap<String, Integer>();
        for (Entry<String, Object> entry : params.entrySet()) {
            paramTypes.put(entry.getKey(), javaClass2SqlType.get(entry
                    .getValue().getClass()));
        }
        return paramTypes;
    }
}
