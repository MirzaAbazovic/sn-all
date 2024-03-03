/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 10:05:27
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class HurricanEdiTmpKupViewsTest extends AbstractViewsTest {

    @DataProvider(name = "hurricanEdiTmpKupViewNamesDP")
    public Object[][] hurricanEdiTmpKupViewNamesDP() {
        return new Object[][] { { "V3KUP_T_CARRIERBESTELLUNG" }, { "V4KUP_T_CARRIERBESTELLUNG_M" },
                { "V4KUP_T_CARRIERBESTELLUNG_N" }, { "V4KUP_T_CARRIERBESTELL_AKT_M" } };
    }

    @Test(dataProvider = "hurricanEdiTmpKupViewNamesDP")
    public void hurricanEdiTmpKupViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "hurricanEdiTmpKupColumnCheckDP")
    public Object[][] hurricanEdiTmpKupColumnCheckDP() {
        return new Object[][] {
                {
                        "V3KUP_T_CARRIERBESTELLUNG",
                        new String[] { "LBZ", "VTRNR", "NIEDERLASSUNG", "PRODAK_ORDER__NO", "AUFTRAG_ID",
                                "BEREITSTELLUNG_AM", "KUENDBESTAETIGUNG_CARRIER", "ONKZ", "ASB", "EDT_ID",
                                "EDF_RECHNUNGSDATUM", "EAL_NR_ELFE", "EDT_VON", "EDT_BIS", "EDT_ANZ", "EDT_BETRAG",
                                "EDT_LEITUNGSNR", "EDT_REFERENZ", "EDT_NAME", "EDT_TEXT1", "EDT_TEXT2",
                                "EDT_LEITUNGSNR_ORI", "EDT_ENDSTELLE_A", "EDT_ENDSTELLE_B",
                                "EDT_EXTERNE_AUFTRAGSNUMMER", "EDT_STOERUNGSNUMMER", "EDT_STOERUNGSDATUM",
                                "EDT_GUTSCHRIFT_VERTRAGSNUMMER", "EDT_MATERIALNUMMER", "EDT_KONDITIONSID",
                                "EDT_VERTRAGSNUMMER", "T_DTAG_TAL_RECH_ID", "T_DTAG_TAL_ID", "EAL_KONDITIONS_ID",
                                "EAL_NR", "EAL_RECHNUNGSTEXT", "EAL_TEXT", "BEMERKUNGEN" } },
                {
                        "V4KUP_T_CARRIERBESTELLUNG_M",
                        new String[] { "NIEDERLASSUNG", "EDT_ID", "EDF_RECHNUNGSDATUM", "EAL_NR_ELFE", "EDT_VON",
                                "EDT_BIS", "EDT_ANZ", "EDT_BETRAG", "EDT_LEITUNGSNR", "EDT_REFERENZ", "EDT_NAME",
                                "EDT_TEXT1", "EDT_TEXT2", "EDT_LEITUNGSNR_ORI", "EDT_ENDSTELLE_A", "EDT_ENDSTELLE_B",
                                "EDT_EXTERNE_AUFTRAGSNUMMER", "EDT_STOERUNGSNUMMER", "EDT_STOERUNGSDATUM",
                                "EDT_GUTSCHRIFT_VERTRAGSNUMMER", "EDT_MATERIALNUMMER", "EDT_KONDITIONSID",
                                "EDT_VERTRAGSNUMMER", "T_DTAG_TAL_RECH_ID", "T_DTAG_TAL_ID", "EAL_KONDITIONS_ID",
                                "EAL_NR", "EAL_RECHNUNGSTEXT", "EAL_TEXT", "BEMERKUNGEN" } },
                {
                        "V4KUP_T_CARRIERBESTELLUNG_N",
                        new String[] { "NIEDERLASSUNG", "EDT_ID", "EDF_RECHNUNGSDATUM", "EAL_NR_ELFE", "EDT_VON",
                                "EDT_BIS", "EDT_ANZ", "EDT_BETRAG", "EDT_LEITUNGSNR", "EDT_REFERENZ", "EDT_NAME",
                                "EDT_TEXT1", "EDT_TEXT2", "EDT_LEITUNGSNR_ORI", "EDT_ENDSTELLE_A", "EDT_ENDSTELLE_B",
                                "EDT_EXTERNE_AUFTRAGSNUMMER", "EDT_STOERUNGSNUMMER", "EDT_STOERUNGSDATUM",
                                "EDT_GUTSCHRIFT_VERTRAGSNUMMER", "EDT_MATERIALNUMMER", "EDT_KONDITIONSID",
                                "EDT_VERTRAGSNUMMER", "T_DTAG_TAL_RECH_ID", "T_DTAG_TAL_ID", "EAL_KONDITIONS_ID",
                                "EAL_NR", "EAL_RECHNUNGSTEXT", "EAL_TEXT" } },
                {
                        "V4KUP_T_CARRIERBESTELL_AKT_M",
                        new String[] { "NIEDERLASSUNG", "CUST_NO", "VALID_FROM", "VALID_TO", "OE__NO", "LBZ_COMP",
                                "LBZ", "VTRNR", "PRODAK_ORDER__NO", "AUFTRAG_ID", "AUF_NR", "ONKZ", "ASB",
                                "BEMERKUNGEN" } }

        };
    }

    @Test(dataProvider = "hurricanEdiTmpKupColumnCheckDP")
    public void hurricanEdiTmpKupColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
