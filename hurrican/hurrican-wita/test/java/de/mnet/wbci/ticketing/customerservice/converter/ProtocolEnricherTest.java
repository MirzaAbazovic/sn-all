/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.time.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceTestBuilder;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

@Test(groups = BaseTest.UNIT)
public class ProtocolEnricherTest {

    protected static final String INBOUND_COMMENT = "INBOUND COMMENT";

    protected static final String OUTBOUND_COMMENT = "OUTBOUND COMMENT";

    protected static final String VA_EXPIRED_DONATING = "DONATING COMMENT";

    protected static final String VA_EXPIRED_RECEIVING = "RECEIVING COMMENT";

    @Mock
    protected InboundMessageCommentGenerator inboundMessageCommentGenerator;

    @Mock
    protected OutboundMessageCommentGenerator outboundMessageCommentGenerator;

    @Mock
    protected NewVaExpiredCommentGenerator newVaExpiredCommentGenerator;

    @InjectMocks
    private AbbmMeldungProtocolEnricher abbmMeldungProtocolEnricher = new AbbmMeldungProtocolEnricher();

    @InjectMocks
    private AbbmTrMeldungProtocolEnricher abbmTrMeldungProtocolEnricher = new AbbmTrMeldungProtocolEnricher();

    @InjectMocks
    private AkmTrMeldungProtocolEnricher akmTrMeldungProtocolEnricher = new AkmTrMeldungProtocolEnricher();

    @InjectMocks
    private RuemVaMeldungProtocolEnricher ruemVaMeldungProtocolEnricher = new RuemVaMeldungProtocolEnricher();

    @InjectMocks
    private StrAbbmMeldungProtocolEnricher strAbbmMeldungProtocolEnricher = new StrAbbmMeldungProtocolEnricher();

    @InjectMocks
    private StrAenAbgRequestProtocolEnricher strAenAbgRequestProtocolEnricher = new StrAenAbgRequestProtocolEnricher();

    @InjectMocks
    private StrAenAufRequestProtocolEnricher strAenAufRequestProtocolEnricher = new StrAenAufRequestProtocolEnricher();

    @InjectMocks
    private StrAufhAbgRequestProtocolEnricher strAufhAbgRequestProtocolEnricher = new StrAufhAbgRequestProtocolEnricher();

    @InjectMocks
    private StrAufhAufRequestProtocolEnricher strAufhAufRequestProtocolEnricher = new StrAufhAufRequestProtocolEnricher();

    @InjectMocks
    private StrErlmMeldungProtocolEnricher strErlmMeldungProtocolEnricher = new StrErlmMeldungProtocolEnricher();

    @InjectMocks
    private TvAbbmMeldungProtocolEnricher tvAbbmMeldungProtocolEnricher = new TvAbbmMeldungProtocolEnricher();

    @InjectMocks
    private TvErlmMeldungProtocolEnricher tvErlmMeldungProtocolEnricher = new TvErlmMeldungProtocolEnricher();

    @InjectMocks
    private TvRequestProtocolEnricher tvRequestProtocolEnricher = new TvRequestProtocolEnricher();

    @InjectMocks
    private VaRequestProtocolEnricher vaRequestProtocolEnricher = new VaRequestProtocolEnricher();

    @InjectMocks
    private NewVaExpiredProtocolEnricher newVaExpiredProtocolEnricher = new NewVaExpiredProtocolEnricher();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] wbciMessages() {
        return new Object[][] {
                { abbmMeldungProtocolEnricher, new AbbruchmeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { abbmTrMeldungProtocolEnricher, new AbbruchmeldungTechnRessourceTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { akmTrMeldungProtocolEnricher, new UebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { ruemVaMeldungProtocolEnricher, new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { strAbbmMeldungProtocolEnricher, getTestAbbruchmeldungStornoAen() },
                { strAbbmMeldungProtocolEnricher, getTestAbbruchmeldungStornoAuf() },
                { strAenAbgRequestProtocolEnricher, new StornoAenderungAbgAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { strAenAufRequestProtocolEnricher, new StornoAenderungAufAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { strAufhAbgRequestProtocolEnricher, new StornoAufhebungAbgAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { strAufhAufRequestProtocolEnricher, new StornoAufhebungAufAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { strErlmMeldungProtocolEnricher, getTestErledigtmeldungStornoAen() },
                { strErlmMeldungProtocolEnricher, getTestErledigtmeldungStornoAuf() },
                { tvAbbmMeldungProtocolEnricher, getTestAbbruchmeldungTerminverschiebung() },
                { tvErlmMeldungProtocolEnricher, getTestErledigtmeldungTerminverschiebung() },
                { tvRequestProtocolEnricher, new TerminverschiebungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) },
                { vaRequestProtocolEnricher, new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN) }
        };
    }

    @DataProvider
    public Object[][] newVaExpired() {
        return new Object[][] {
                { WbciGeschaeftsfallStatus.NEW_VA_EXPIRED, CarrierRole.AUFNEHMEND, true, VA_EXPIRED_RECEIVING},
                { WbciGeschaeftsfallStatus.NEW_VA_EXPIRED, CarrierRole.ABGEBEND, true, VA_EXPIRED_DONATING},
                { WbciGeschaeftsfallStatus.ACTIVE, CarrierRole.ABGEBEND, false, null},
        };
    }

    private Object getTestErledigtmeldungTerminverschiebung() {
        return new ErledigtmeldungTestBuilder().withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)).buildForTv();
    }

    private Object getTestAbbruchmeldungTerminverschiebung() {
        return new AbbruchmeldungTestBuilder().withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)).buildForTv();
    }

    private Object getTestErledigtmeldungStornoAuf() {
        return new ErledigtmeldungTestBuilder().withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)).buildForStorno(RequestTyp.STR_AUFH_ABG);
    }

    private Object getTestErledigtmeldungStornoAen() {
        return getTestErledigtmeldungStornoAen(WbciGeschaeftsfallStatus.ACTIVE, CarrierRole.AUFNEHMEND);
    }

    private Object getTestErledigtmeldungStornoAen(WbciGeschaeftsfallStatus status, CarrierRole carrierRole) {
        CarrierCode auf;
        CarrierCode abg;

        if(carrierRole.equals(CarrierRole.ABGEBEND)) {
            auf = CarrierCode.DTAG;
            abg = CarrierCode.MNET;
        } else {
            auf = CarrierCode.MNET;
            abg = CarrierCode.DTAG;
        }

        return new ErledigtmeldungTestBuilder()
                .withWbciGeschaeftsfall(
                        new WbciGeschaeftsfallKueMrnTestBuilder()
                                .withAufnehmenderEKP(auf)
                                .withAbgebenderEKP(abg)
                                .withStatus(status)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
                ).buildForStorno(RequestTyp.STR_AEN_AUF);
    }

    private Object getTestAbbruchmeldungStornoAuf() {
        return new AbbruchmeldungTestBuilder().withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)).buildForStorno(RequestTyp.STR_AUFH_ABG);
    }

    private Object getTestAbbruchmeldungStornoAen() {
        return new AbbruchmeldungTestBuilder().withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)).buildForStorno(RequestTyp.STR_AEN_AUF);
    }

    @Test(dataProvider = "wbciMessages")
    public void testEnrichInbound(CustomerServiceProtocolEnricher enricher, WbciMessage wbciMessage) {
        Assert.assertTrue(enricher.supports(wbciMessage));
        wbciMessage.setIoType(IOType.IN);
        setupMessageCommentGenerators();
        AddCommunication csProtocol = new AddCommunication();
        enricher.enrich(wbciMessage, csProtocol);
        Assert.assertEquals(csProtocol.getNotes(), INBOUND_COMMENT);
    }

    @Test(dataProvider = "wbciMessages")
    public void testEnrichOutbound(CustomerServiceProtocolEnricher enricher, WbciMessage wbciMessage) {
        Assert.assertTrue(enricher.supports(wbciMessage));
        wbciMessage.setIoType(IOType.OUT);
        setupMessageCommentGenerators();
        AddCommunication csProtocol = new AddCommunication();
        enricher.enrich(wbciMessage, csProtocol);
        Assert.assertEquals(csProtocol.getNotes(), OUTBOUND_COMMENT);
    }

    @Test(dataProvider = "newVaExpired")
    public void testEnrichNewVaExpired(WbciGeschaeftsfallStatus status, CarrierRole carrierRole, boolean shouldSupport, String expectedNotes) {
        CustomerServiceProtocolEnricher enricher = newVaExpiredProtocolEnricher;
        WbciMessage wbciMessage = (WbciMessage) getTestErledigtmeldungStornoAen(status, carrierRole);

        if(shouldSupport) {
            Assert.assertTrue(enricher.supports(wbciMessage));
            setupMessageCommentGenerators();
            AddCommunication csProtocol = new AddCommunication();
            enricher.enrich(wbciMessage, csProtocol);
            Assert.assertEquals(csProtocol.getNotes(), expectedNotes);
        } else {
            Assert.assertFalse(enricher.supports(wbciMessage));
        }
    }

    private void setupMessageCommentGenerators() {
        when(inboundMessageCommentGenerator.getVaBemerkung(any(GeschaeftsfallTyp.class), any(CarrierCode.class), any(LocalDateTime.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getTvBemerkung(any(CarrierCode.class), any(LocalDateTime.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getStrAufhAbgBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getStrAufhAufBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getStrAenAbgBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getStrAenAufBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getAbbmBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getRuemVaBemerkung(any(CarrierCode.class), any(LocalDateTime.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getAkmTrBemerkung(any(CarrierCode.class), any(Boolean.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getAbbmTrBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getTvErlmBemerkung(any(CarrierCode.class), any(LocalDateTime.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getTvAbbmBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getStornoErlmBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);
        when(inboundMessageCommentGenerator.getStornoAbbmBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(INBOUND_COMMENT);

        when(outboundMessageCommentGenerator.getVaBemerkung(any(GeschaeftsfallTyp.class), any(CarrierCode.class), any(LocalDateTime.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getTvBemerkung(any(CarrierCode.class), any(LocalDateTime.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getStrAufhAbgBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getStrAufhAufBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getStrAenAbgBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getStrAenAufBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getAbbmBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getRuemVaBemerkung(any(CarrierCode.class), any(LocalDateTime.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getAkmTrBemerkung(any(CarrierCode.class), any(Boolean.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getAbbmTrBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getTvErlmBemerkung(any(CarrierCode.class), any(LocalDateTime.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getTvAbbmBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getStornoErlmBemerkung(any(CarrierCode.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);
        when(outboundMessageCommentGenerator.getStornoAbbmBemerkung(any(CarrierCode.class), any(String.class), any(String.class), any(String.class))).thenReturn(OUTBOUND_COMMENT);

        when(newVaExpiredCommentGenerator.getNewVaExpiredDonatingBemerkung(any(CarrierCode.class), any(String.class))).thenReturn(VA_EXPIRED_DONATING);
        when(newVaExpiredCommentGenerator.getNewVaExpiredReceivingBemerkung(any(CarrierCode.class), any(String.class))).thenReturn(VA_EXPIRED_RECEIVING);
    }

}