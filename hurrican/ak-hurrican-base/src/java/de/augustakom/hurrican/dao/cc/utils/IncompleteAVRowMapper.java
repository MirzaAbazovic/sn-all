/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2005 09:06:51
 */
package de.augustakom.hurrican.dao.cc.utils;

import java.sql.*;
import org.springframework.jdbc.core.RowMapper;

import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;


/**
 * RowMapper, um Objekte des Typs <code>IncompleteAuftragView</code> aus einem ResultSet zu erzeugen. Der RowMapper
 * beruecksichtig die Parameter {@link IncompleteAuftragView#setName(String)} und {@link
 * IncompleteAuftragView#setVorname(String)} nicht!
 *
 *
 */
public class IncompleteAVRowMapper implements RowMapper<IncompleteAuftragView> {

    /**
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public IncompleteAuftragView mapRow(ResultSet rs, int rowNum) throws SQLException {
        IncompleteAuftragView view = new IncompleteAuftragView();
        view.setAuftragId(Long.valueOf(rs.getLong("AUFTRAG_ID")));
        view.setAuftragNoOrig(rs.getLong("PRODAK_ORDER__NO"));
        view.setVbz(rs.getString("TDN"));
        view.setKundeNo(rs.getLong("KUNDE__NO"));
        view.setProduktName(rs.getString("PRODUKTNAME"));
        view.setProduktGruppe(rs.getString("PRODUKTGRUPPE"));
        view.setAuftragsart(rs.getString("AUFTRAGSART"));
        view.setVorgabeSCV(rs.getDate("VORGABE_SCV"));
        view.setBaAnlass(rs.getString("BA_ANLASS"));
        view.setBaRealTermin(rs.getDate("BA_REALISIERUNGSTERMIN"));
        view.setBaAnDispo(rs.getDate("BA_AN_DISPO"));
        view.setCbBestelltAm(rs.getDate("CB_BESTELLT_AM"));
        view.setCbZurueckAm(rs.getDate("CB_ZURUECK_AM"));
        view.setCbLbz(rs.getString("CB_LBZ"));
        view.setCbKuendigungAm(rs.getDate("CB_KUENDIGUNG_AN"));
        view.setCbKuendigungZurueck(rs.getDate("CB_KUENDIGUNG_ZURUECK"));
        view.setBearbeiter(rs.getString("BEARBEITER"));
        view.setNiederlassung(rs.getString("NIEDERLASSUNG"));
        return view;
    }

}


