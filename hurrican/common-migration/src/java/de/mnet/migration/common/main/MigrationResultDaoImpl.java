/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2009 10:53:55
 */

package de.mnet.migration.common.main;

import java.sql.*;
import java.util.*;
import javax.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.mnet.migration.common.MigrationResultDao;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.MigrationStatus;


/**
 *
 */
public class MigrationResultDaoImpl implements MigrationResultDao {
    private static final class MigrationResultMapper implements RowMapper<MigrationResult> {
        public MigrationResult mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            final String migrationName = resultSet.getString("MIGRATION_NAME");
            MigrationResult migrationResult = new MigrationResult(migrationName);
            migrationResult.setId(resultSet.getLong("ID"));
            migrationResult.setStatus(MigrationStatus.valueOf(resultSet.getString("SUCCESS")));
            migrationResult.setCounter(resultSet.getInt("COUNTER"));
            migrationResult.setMigrated(resultSet.getInt("MIGRATED"));
            migrationResult.setWarnings(resultSet.getInt("WARNINGS"));
            migrationResult.setInfos(resultSet.getInt("INFO"));
            migrationResult.setSkipped(resultSet.getInt("SKIPPED"));
            migrationResult.setBadData(resultSet.getInt("BAD_DATA"));
            migrationResult.setErrors(resultSet.getInt("ERRORS"));
            return migrationResult;
        }
    }

    protected Boolean simulate;
    protected DataSource dataSource;
    protected String migResultTableName;
    protected String migResultSequenceName;
    protected String migLogTableName;

    /**
     * @see de.mnet.migration.common.MigrationResultDao#saveMigrationResult(MigrationResult, String)
     */
    @Override
    public void saveMigrationResult(MigrationResult migrationResult, String migrationSuite) {
        if (migrationResult == null) {
            throw new IllegalArgumentException("The migrationResult may not be null");
        }

        if (Boolean.FALSE.equals(simulate)) {
            String sqlStatement = null;
            if (migrationResult.getId() == null) {
                //Need to create a new Id
                migrationResult.setId(createJdbcTemplate()
                        .queryForObject("Select " + migResultSequenceName + ".NEXTVAL FROM Dual", Long.class));
                sqlStatement =
                        "insert into "
                                + migResultTableName + "("
                                + "MIGRATION_NAME, "
                                + "MIGRATION_SUITE, "
                                + "SUCCESS, "
                                + "COUNTER, "
                                + "MIGRATED, "
                                + "WARNINGS, "
                                + "INFO, "
                                + "SKIPPED, "
                                + "BAD_DATA, "
                                + "ERRORS, "
                                + "ID) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
            else {
                sqlStatement =
                        "update "
                                + migResultTableName + " "
                                + "SET MIGRATION_NAME = ?, "
                                + "MIGRATION_SUITE = ?, "
                                + "SUCCESS = ?, "
                                + "COUNTER = ?, "
                                + "MIGRATED = ?, "
                                + "WARNINGS = ?, "
                                + "INFO = ?, "
                                + "SKIPPED = ?, "
                                + "BAD_DATA = ?, "
                                + "ERRORS = ? "
                                + "WHERE ID = ?";
            }

            List<Object> params = new ArrayList<Object>();
            params.add(migrationResult.getMigrationName());
            params.add(migrationSuite);
            params.add(migrationResult.getStatus().name());
            params.add(migrationResult.getCounter());
            params.add(migrationResult.getMigrated());
            params.add(migrationResult.getWarnings());
            params.add(migrationResult.getInfos());
            params.add(migrationResult.getSkipped());
            params.add(migrationResult.getBadData());
            params.add(migrationResult.getErrors());
            params.add(migrationResult.getId());

            createJdbcTemplate().update(sqlStatement, params.toArray());
        }
        else {
            migrationResult.setId(1L);
        }
    }

    /**
     * @see de.mnet.migration.common.MigrationResultDao#mergeMigrationResult(MigrationResult, MigrationResult)
     */
    @Override
    public MigrationResult mergeMigrationResult(MigrationResult dest, MigrationResult src) {
        if ((dest.getId() == null) || (src.getId() == null)) {
            throw new IllegalArgumentException("Both migrationresults must have an id!");
        }
        String updateStatement = "update " + migLogTableName + " set MIGRESULT_ID = ? where migresult_id = ?";
        String deleteStatement = "delete from " + migResultTableName + " where id = ?";

        if (Boolean.FALSE.equals(simulate)) {
            createJdbcTemplate().update(updateStatement, dest.getId(), src.getId());
            createJdbcTemplate().update(deleteStatement, src.getId());
        }
        return dest.mergeWith(src);
    }

    /**
     * @see de.mnet.migration.common.MigrationResultDao#wasMigrationAlreadySuccesful(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean wasMigrationAlreadySuccesful(String migrationName, String migrationSuite) {
        if (migrationName == null) {
            throw new IllegalArgumentException("The migrationName may not be null");
        }

        String query = "select * from " + migResultTableName + " where SUCCESS='SUCCESS' and MIGRATION_NAME=?";

        List<Object> params = new ArrayList<Object>();
        params.add(migrationName);
        if (migrationSuite != null) {
            params.add(migrationSuite);
            query += " and MIGRATION_SUITE=?";
        }

        List<Map<String, Object>> result = createJdbcTemplate().queryForList(query, params.toArray());
        return (!result.isEmpty());
    }


    /**
     * @see de.mnet.migration.common.MigrationResultDao#loadLastMigrationResult(String, String)
     */
    @Override
    public MigrationResult loadLastMigrationResult(final String migrationName, String migrationSuite) {
        if (migrationName == null) {
            throw new IllegalArgumentException("The migrationName may not be null");
        }

        String query = "select * from " + migResultTableName + " where MIGRATION_NAME=?";
        List<Object> params = new ArrayList<Object>();
        params.add(migrationName);
        if (migrationSuite != null) {
            params.add(migrationSuite);
            query += " and MIGRATION_SUITE=?";
        }
        query += " order by INSERT_TIMESTAMP desc";

        List<MigrationResult> results = createJdbcTemplate().query(query, params.toArray(), new MigrationResultMapper());
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    protected JdbcTemplate createJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Verwendete-Datasource für Migrations-Ergebnisse Injected by Spring
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Injected by Spring
     */
    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }

    /**
     * Injected by Spring
     */
    public void setMigResultTableName(String migResultTableName) {
        this.migResultTableName = migResultTableName;
    }

    /**
     * Injected by Spring
     */
    public void setMigResultSequenceName(String migResultSequenceName) {
        this.migResultSequenceName = migResultSequenceName;
    }

    /**
     * Injected by Spring
     */
    public void setMigLogTableName(String migLogTableName) {
        this.migLogTableName = migLogTableName;
    }

    /* (non-Javadoc)
     * @see de.mnet.migration.common.MigrationResultDao#loadAllMigrationResult()
     */
    @Override
    public List<MigrationResult> loadAllMigrationResult() {
        final String query = "SELECT * FROM  NAVI_MIG_RESULT";
        List<MigrationResult> migrationResults = createJdbcTemplate().query(query, new MigrationResultMapper());
        if (migrationResults.isEmpty()) {
            migrationResults = new ArrayList<MigrationResult>();
        }
        return migrationResults;
    }
}
