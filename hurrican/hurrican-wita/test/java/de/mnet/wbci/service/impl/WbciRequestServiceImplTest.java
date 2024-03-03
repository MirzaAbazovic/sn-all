/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciVaService;

@Test(groups = UNIT)
public class WbciRequestServiceImplTest {

    @InjectMocks
    @Spy
    private WbciRequestServiceImpl testling = new WbciRequestServiceImpl();

    @Mock
    private WbciStornoService wbciStornoService;
    @Mock
    private WbciTvService wbciTvService;
    @Mock
    @Qualifier("WbciVaKueMrnService")
    private WbciVaService wbciVaKueMrnService;
    @Mock
    @Qualifier("WbciVaKueOrnService")
    private WbciVaService wbciVaKueOrnService;
    @Mock
    @Qualifier("WbciVaRrnpService")
    private WbciVaService wbciVaRrnpService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testProcessIncomingRequestVaKueMrn() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        testling.processIncomingRequest(metadata, va);
        Mockito.verify(wbciVaKueMrnService).processIncomingVA(metadata, va);
        Mockito.verify(wbciVaKueOrnService, times(0)).processIncomingVA(metadata, va);
        Mockito.verify(wbciVaRrnpService, times(0)).processIncomingVA(metadata, va);
    }

    public void testProcessIncomingRequestVaKueOrn() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        testling.processIncomingRequest(metadata, va);
        Mockito.verify(wbciVaKueOrnService).processIncomingVA(metadata, va);
        Mockito.verify(wbciVaKueMrnService, times(0)).processIncomingVA(metadata, va);
        Mockito.verify(wbciVaRrnpService, times(0)).processIncomingVA(metadata, va);
    }

    public void testProcessIncomingRequestVarrnp() {
        VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        testling.processIncomingRequest(metadata, va);
        Mockito.verify(wbciVaRrnpService).processIncomingVA(metadata, va);
        Mockito.verify(wbciVaKueMrnService, times(0)).processIncomingVA(metadata, va);
        Mockito.verify(wbciVaKueOrnService, times(0)).processIncomingVA(metadata, va);
    }

    public void testProcessTv() {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        testling.processIncomingRequest(metadata, tv);
        Mockito.verify(wbciTvService).processIncomingTv(metadata, tv);
    }

    public void testProcessStorno() {
        StornoAenderungAbgAnfrage<WbciGeschaeftsfall> stornoAenderungAbgAnfrage = new StornoAenderungAbgAnfrageTestBuilder<>()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        testling.processIncomingRequest(metadata, stornoAenderungAbgAnfrage);
        Mockito.verify(wbciStornoService).processIncomingStorno(metadata, stornoAenderungAbgAnfrage);
    }

}
