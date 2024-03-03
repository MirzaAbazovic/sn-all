/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2011 08:24:27
 */
package de.augustakom.hurrican.dao.internet.impl;

import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.internet.INetNumDao;

/**
 * Implementierung von <code>INetNumDao</code>.
 */
@Repository
public class INetNumDaoImpl extends SimpleJdbcDaoSupport implements INetNumDao {

    /**
     * @see de.augustakom.hurrican.dao.internet.INetNumDao#findAllNetIdsWithPoolName()
     */
    @Override
    public List<Pair<Long, String>> findAllNetIdsWithPoolName() {
        final String sql = "SELECT ID, POOLNAME FROM V_INETNUM_HURRICAN";
        final RowMapper<Pair<Long, String>> rowMapper = new RowMapper<Pair<Long, String>>() {
            @Override
            public Pair<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Pair<Long, String>(rs.getLong("ID"), rs.getString("POOLNAME"));
            }
        };
        return getJdbcTemplate().query(sql, rowMapper);
    }

}


