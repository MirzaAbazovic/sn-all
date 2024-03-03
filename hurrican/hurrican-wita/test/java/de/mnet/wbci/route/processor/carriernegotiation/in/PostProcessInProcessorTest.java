/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciVaService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class PostProcessInProcessorTest extends BaseTest {

    @Mock
    private WbciTvService wbciTvService;
    @Mock
    private FeatureService featureService;
    @Mock
    private WbciVaService wbciVaKueMrnService;
    @Mock
    private WbciVaService wbciVaKueOrnService;
    @Mock
    private WbciVaService wbciVaRrnpService;
    @Mock
    private WbciStornoService wbciStornoService;
    @Mock
    private WbciMeldungService wbciMeldungService;

    @Mock
    private Exchange exchange;
    @InjectMocks
    @Spy
    private PostProcessInProcessor testling = new PostProcessInProcessor();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPostProcessTv() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef("DEU.DTAG.V123456789")
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Mockito.doReturn(tv).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        Mockito.verify(featureService).isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE);
        Mockito.verify(wbciTvService).postProcessIncomingTv(Matchers.eq(tv));
    }

    @Test
    public void testPostProcessNoTv() throws Exception {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Mockito.doReturn(vorabstimmungsAnfrage).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        Mockito.verify(featureService).isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE);
        Mockito.verify(wbciTvService, never()).postProcessIncomingTv(any(TerminverschiebungsAnfrage.class));
    }

    @Test
    public void testPostProcessVaKueMrn() throws Exception {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Mockito.doReturn(vorabstimmungsAnfrage).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        verify(wbciVaKueMrnService).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
        verify(wbciVaKueOrnService, never()).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
        verify(wbciVaRrnpService, never()).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
    }

    @Test
    public void testPostProcessVaKueOrn() throws Exception {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);

        Mockito.doReturn(vorabstimmungsAnfrage).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        verify(wbciVaKueOrnService).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
        verify(wbciVaKueMrnService, never()).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
        verify(wbciVaRrnpService, never()).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
    }

    @Test
    public void testPostProcessVaRrnp() throws Exception {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);

        Mockito.doReturn(vorabstimmungsAnfrage).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        verify(wbciVaRrnpService).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
        verify(wbciVaKueMrnService, never()).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
        verify(wbciVaKueOrnService, never()).autoAssignTaifunOrderToVA(vorabstimmungsAnfrage);
    }

    @Test
    public void testAutoProcessingDisabled() throws Exception {
        final VorabstimmungsAnfrage vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Mockito.doReturn(vorabstimmungsAnfrage).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.FALSE);

        testling.process(exchange);
        Mockito.verify(featureService).isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE);
        Mockito.verify(wbciTvService, never()).postProcessIncomingTv(any(TerminverschiebungsAnfrage.class));
    }

    @Test
    public void testPostProcessingMeldung() throws Exception {
        Meldung erlm = new ErledigtmeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Mockito.doReturn(erlm).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        Mockito.verify(featureService).isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE);
        Mockito.verify(wbciTvService, never()).postProcessIncomingTv(any(TerminverschiebungsAnfrage.class));
    }

    @Test
    public void testPostProcessingAkmTr() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Mockito.doReturn(akmTr).when(testling).getOriginalMessage(exchange);
        when(featureService.isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE)).thenReturn(Boolean.TRUE);

        testling.process(exchange);
        Mockito.verify(featureService).isFeatureOffline(Feature.FeatureName.WBCI_KFT_TEST_MODE);
        Mockito.verify(wbciMeldungService).postProcessIncomingAkmTr(akmTr);
        Mockito.verify(wbciTvService, never()).postProcessIncomingTv(any(TerminverschiebungsAnfrage.class));
    }

}
