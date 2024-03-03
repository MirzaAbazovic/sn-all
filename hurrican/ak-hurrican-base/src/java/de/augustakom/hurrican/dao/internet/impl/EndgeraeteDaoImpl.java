/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 18:19:13
 */

package de.augustakom.hurrican.dao.internet.impl;

import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import de.augustakom.hurrican.dao.internet.EndgeraeteDao;
import de.augustakom.hurrican.model.internet.IntEndgeraet;


public class EndgeraeteDaoImpl extends SimpleJdbcDaoSupport implements EndgeraeteDao, RowMapper<IntEndgeraet> {

    private static final String SELECT_EGS_BY_VBZ = "SELECT * FROM V_HURRICAN_EQUIPMENT_CURRENT WHERE DH_CONTRACT = ?";

    @Override
    public IntEndgeraet mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        IntEndgeraet intEndgeraet = new IntEndgeraet();
        intEndgeraet.setSerialNo(resultSet.getString("DH_SERIALNO"));
        intEndgeraet.setVendor(resultSet.getString("DV_VENDOR"));
        intEndgeraet.setType(resultSet.getString("DV_TYPE"));
        intEndgeraet.setContract(resultSet.getString("DH_CONTRACT"));
        intEndgeraet.setManagementIp(resultSet.getString("DH_IP"));
        intEndgeraet.setLoginUser(resultSet.getString("DH_LOGIN_USER"));
        intEndgeraet.setLoginPass(resultSet.getString("DH_LOGIN_PASS"));
        return intEndgeraet;
    }

    @Override
    public List<IntEndgeraet> findEndgeraeteForVerbindungsbezeichnung(String verbindungsbezeichnung) {
        return getSimpleJdbcTemplate().query(SELECT_EGS_BY_VBZ, this, verbindungsbezeichnung);
    }


}
