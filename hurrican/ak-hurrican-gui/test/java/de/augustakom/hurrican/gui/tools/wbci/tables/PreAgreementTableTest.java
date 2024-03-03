/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTable.*;
import static de.mnet.wbci.converter.MeldungsCodeConverter.*;
import static de.mnet.wbci.model.MeldungsCode.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.awt.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.WbciRequestStatus;

@Test(groups = BaseTest.UNIT)
public class PreAgreementTableTest {
    private PreAgreementTable testling = new PreAgreementTable();

    @Mock
    private PreAgreementVO preAgreementVOMock;

    @Mock
    private Component componentMock;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetBackgroundColorOfRow() throws Exception {
        when(preAgreementVOMock.getRequestStatus()).thenReturn(WbciRequestStatus.RUEM_VA_VERSENDET);
        when(preAgreementVOMock.getEkpAufITU()).thenReturn(CarrierCode.DTAG.getITUCarrierCode());
        when(preAgreementVOMock.getEkpAbgITU()).thenReturn(CarrierCode.MNET.getITUCarrierCode());

        testling.setBackgroundColor(preAgreementVOMock, componentMock);

        verify(componentMock).setBackground(BG_COLOR_GREEN);
    }

    @Test
    public void testSetBackgroundColorOfRowWhenMeldungsCodeIsNull() throws Exception {
        when(preAgreementVOMock.getMeldungCodes()).thenReturn(null);
        testling.setBackgroundColor(preAgreementVOMock, componentMock);
        verify(componentMock, never()).setBackground(any(Color.class));
    }

    @DataProvider
    public Object[][] bgColorProvider() {
        // @formatter:off
        return new Object[][] {
                {VA_VORGEHALTEN,        "",                                 null},
                {RUEM_VA_EMPFANGEN,     meldungcodesToCodeString(ZWA),      BG_COLOR_GREEN},
                {RUEM_VA_EMPFANGEN,     meldungcodesToCodeString(NAT),      BG_COLOR_LIGHT_GREEN},
                {RUEM_VA_EMPFANGEN,     meldungcodesToCodeString(ADAHSNR),  BG_COLOR_LIGHT_GREEN},
                {ABBM_EMPFANGEN,        meldungcodesToCodeString(ADAHSNR),  BG_COLOR_RED},
                {ABBM_TR_EMPFANGEN,     "",                                 BG_COLOR_ORANGE},
        };
        //@formatter:on
    }

    @Test(dataProvider = "bgColorProvider")
    public void testLookupBackgroundColorGreen(WbciRequestStatus requestStatus, String meldungCodes, Color expectedColor) throws Exception {
        setupPreagreementMock(requestStatus, meldungCodes);
        Assert.assertEquals(testling.lookupBackgroundColor(preAgreementVOMock), expectedColor);
    }

    private void setupPreagreementMock(WbciRequestStatus requestStatus, String meldungCodes) {
        when(preAgreementVOMock.getRequestStatus()).thenReturn(requestStatus);
        when(preAgreementVOMock.getMeldungCodes()).thenReturn(meldungCodes);
        when(preAgreementVOMock.getEkpAbg()).thenReturn(CarrierCode.MNET);
        when(preAgreementVOMock.getEkpAuf()).thenReturn(CarrierCode.DTAG);
    }
}
