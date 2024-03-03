/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 09:13:12
 */
package de.augustakom.hurrican.views;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = { BaseTest.VIEW }, enabled = true)
public class HurricanEdiTmpViewsTest extends AbstractViewsTest {

    @DataProvider(name = "hurricanInternalViewNamesDP")
    public Object[][] hurricanInternalViewNamesDP() {
        return new Object[][] { { "V1EDI_T_HVT_2_NL" }, { "V1EDI_T_ONKZ_2_NL" }, { "V1T_CARRIERBESTELLUNG" },
                { "V1EDI_T_CARRIERBESTELLUNG" }, { "V2EDI_T_CARRIERBESTELLUNG" }, { "V3EDI_T_CARRIERBESTELLUNG" },
                { "V4EDI_T_CARRIERBESTELLUNG_A" }, { "V4EDI_T_CARRIERBESTELLUNG_N" }, { "V4KUP_T_CARRIERBESTELL_AKT_A" } };
    }

    @Test(dataProvider = "hurricanInternalViewNamesDP")
    public void hurricanInternalViewsValid(String viewName) {
        getJdbcTemplate().queryForInt(createSelectRowCountSQL(viewName));
    }

    @DataProvider(name = "hurricanInternalViewsColumnCheckDP")
    public Object[][] hurricanInternalViewsColumnCheckDP() {
        return new Object[][] {
                { "V1EDI_T_HVT_2_NL",
                        new String[] { "HVT_GRUPPE_ID", "ONKZ", "ASB", "KOSTENSTELLE", "NIEDERLASSUNG", "COMP" } },
                { "V1EDI_T_ONKZ_2_NL", new String[] { "ONKZLEN5", "NIEDERLASSUNG" } },
                {
                        "V1T_CARRIERBESTELLUNG",
                        new String[] { "LBZ_COMP", "CB_ID", "CB_2_ES_ID", "CARRIER_ID", "BESTELLT_AM", "VORGABEDATUM",
                                "ZURUECK_AM", "BEREITSTELLUNG_AM", "KUNDE_VOR_ORT", "LBZ", "VTRNR", "AQS", "LL",
                                "NEGATIVERM", "WIEDERVORLAGE", "TIMESTAMP", "KUENDIGUNG_AN_CARRIER",
                                "KUENDBESTAETIGUNG_CARRIER", "AUFTRAG_ID_4_TAL_NA", "TAL_NA_TYP", "AI_ADDRESS_ID",
                                "MAX_BRUTTO_BITRATE", "EQ_OUT_ID", "VERSION" } },
                {
                        "V1EDI_T_CARRIERBESTELLUNG",
                        new String[] { "LBZ_COMP", "LBZ", "VTRNR", "PRODAK_ORDER__NO", "AUFTRAG_ID",
                                "BEREITSTELLUNG_AM", "KUENDBESTAETIGUNG_CARRIER", "ONKZ", "ASB", "BEMERKUNGEN" } },
                {
                        "V2EDI_T_CARRIERBESTELLUNG",
                        new String[] { "LBZ_COMP", "LBZ", "VTRNR", "PRODAK_ORDER__NO", "AUFTRAG_ID",
                                "BEREITSTELLUNG_AM", "KUENDBESTAETIGUNG_CARRIER", "ONKZ", "ASB", "BEMERKUNGEN" } },
                {
                        "V3EDI_T_CARRIERBESTELLUNG",
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
                        "V4EDI_T_CARRIERBESTELLUNG_A",
                        new String[] { "NIEDERLASSUNG", "EDT_ID", "EDF_RECHNUNGSDATUM", "EAL_NR_ELFE", "EDT_VON",
                                "EDT_BIS", "EDT_ANZ", "EDT_BETRAG", "EDT_LEITUNGSNR", "EDT_REFERENZ", "EDT_NAME",
                                "EDT_TEXT1", "EDT_TEXT2", "EDT_LEITUNGSNR_ORI", "EDT_ENDSTELLE_A", "EDT_ENDSTELLE_B",
                                "EDT_EXTERNE_AUFTRAGSNUMMER", "EDT_STOERUNGSNUMMER", "EDT_STOERUNGSDATUM",
                                "EDT_GUTSCHRIFT_VERTRAGSNUMMER", "EDT_MATERIALNUMMER", "EDT_KONDITIONSID",
                                "EDT_VERTRAGSNUMMER", "T_DTAG_TAL_RECH_ID", "T_DTAG_TAL_ID", "EAL_KONDITIONS_ID",
                                "EAL_NR", "EAL_RECHNUNGSTEXT", "EAL_TEXT", "BEMERKUNGEN" } },
                {
                        "V4EDI_T_CARRIERBESTELLUNG_N",
                        new String[] { "NIEDERLASSUNG", "EDT_ID", "EDF_RECHNUNGSDATUM", "EAL_NR_ELFE", "EDT_VON",
                                "EDT_BIS", "EDT_ANZ", "EDT_BETRAG", "EDT_LEITUNGSNR", "EDT_REFERENZ", "EDT_NAME",
                                "EDT_TEXT1", "EDT_TEXT2", "EDT_LEITUNGSNR_ORI", "EDT_ENDSTELLE_A", "EDT_ENDSTELLE_B",
                                "EDT_EXTERNE_AUFTRAGSNUMMER", "EDT_STOERUNGSNUMMER", "EDT_STOERUNGSDATUM",
                                "EDT_GUTSCHRIFT_VERTRAGSNUMMER", "EDT_MATERIALNUMMER", "EDT_KONDITIONSID",
                                "EDT_VERTRAGSNUMMER", "T_DTAG_TAL_RECH_ID", "T_DTAG_TAL_ID", "EAL_KONDITIONS_ID",
                                "EAL_NR", "EAL_RECHNUNGSTEXT", "EAL_TEXT" } },
                {
                        "V4KUP_T_CARRIERBESTELL_AKT_A",
                        new String[] { "NIEDERLASSUNG", "LBZ_COMP", "LBZ", "VTRNR", "PRODAK_ORDER__NO", "AUFTRAG_ID",
                                "BEREITSTELLUNG_AM", "ONKZ", "ASB", "BEMERKUNGEN" } } };
    }

    @Test(dataProvider = "hurricanInternalViewsColumnCheckDP")
    public void hurricanInternalViewsColumnCheck(String viewName, String[] columns) {
        checkColumnsForView(viewName, columns);
    }
}
