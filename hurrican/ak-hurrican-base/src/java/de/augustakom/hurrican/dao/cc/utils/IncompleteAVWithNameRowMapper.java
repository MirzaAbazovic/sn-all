/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2010 16:28:03
 */
package de.augustakom.hurrican.dao.cc.utils;

import java.sql.*;

import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;


/**
 * Ableitung von {@link IncompleteAVRowMapper}, die auch die Parameter {@link IncompleteAuftragView#setName(String)} und
 * {@link IncompleteAuftragView#setVorname(String)} beruecksichtigt!
 */
public class IncompleteAVWithNameRowMapper extends IncompleteAVRowMapper {

    @Override
    public IncompleteAuftragView mapRow(ResultSet rs, int rowNum) throws SQLException {
        IncompleteAuftragView result = super.mapRow(rs, rowNum);
        result.setName(rs.getString("NAME"));
        result.setVorname(rs.getString("VORNAME"));
        return result;
    }

}


