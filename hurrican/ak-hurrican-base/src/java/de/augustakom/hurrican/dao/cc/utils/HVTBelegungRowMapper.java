/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2005 13:08:19
 */
package de.augustakom.hurrican.dao.cc.utils;

import java.sql.*;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import de.augustakom.hurrican.model.cc.view.HVTBelegungView;


/**
 * RowMapper fuer Objekte des Typs <code>HVTBelegungView</code>.
 *
 *
 */
public class HVTBelegungRowMapper implements RowMapper<HVTBelegungView> {
    private static final Logger LOGGER = Logger.getLogger(HVTBelegungRowMapper.class);

    /**
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    public HVTBelegungView mapRow(ResultSet rs, int row) throws SQLException {
        HVTBelegungView view = new HVTBelegungView();
        view.setUevt(rs.getString("UEVT"));
        view.setCudaPhysik(rs.getString("CUDA_PHYSIK"));
        view.setHvtIdStandort((rs.getLong("HVT_ID_STANDORT") > 0) ? Long.valueOf(rs.getLong("HVT_ID_STANDORT")) : null);
        view.setRangLeiste1(rs.getString("RANG_LEISTE1"));
        view.setRangSSType(rs.getString("RANG_SS_TYPE"));

        try {
            view.setFrei(Integer.valueOf(rs.getInt("FREI")));
        }
        catch (Exception e) {
            LOGGER.debug("mapRow() - got (expected) exception calling setFrei");
        }

        try {
            view.setBelegt(Integer.valueOf(rs.getInt("BELEGT")));
        }
        catch (Exception e) {
            LOGGER.debug("mapRow() - got (expected) exception calling setBelegt");
        }

        return view;
    }
}


