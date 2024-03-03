package de.mnet.hurrican.wholesale.ws.outbound.impl;

import static com.google.common.collect.Lists.*;
import static de.mnet.hurrican.wholesale.ws.outbound.testdata.MeldungstypAKMPVTypeTestdata.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import java.math.*;
import java.util.*;
import org.apache.cxf.endpoint.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.WholesaleOrderService;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.ProviderwechselRueckmeldungType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.AenderungskennzeichenType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.GeschaeftsfallArtType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.GeschaeftsfallMeldungType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.JaNeinType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungsattributeRUEMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MessageType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AuftragType;
import de.mnet.hurrican.wholesale.interceptor.WholesaleRequestInterceptor;
import de.mnet.hurrican.wholesale.model.RequestXml;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.service.WholesaleAuditService;
import de.mnet.hurrican.wholesale.ws.outbound.mapper.WholesaleAuftragMapper;
import de.mnet.hurrican.wholesale.ws.outbound.mapper.WholesaleRuemPvMapper;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.MeldungstypAKMPVTypeTestdata;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.PersonTestdata;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.StandortTestdata;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.PreAgreementVOBuilder;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Unittest for WholesaleOrderOutboundServiceImpl.
 * Created by wieran on 06.02.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WholesaleOrderOutboundServiceImplTest {

    private final static String SENDING_CARRIER = "MNETH";
    private final static String RECEIVING_CARRIER = "MNET";

    private final static BigInteger SPRI_VERSION = BigInteger.valueOf(4);
    private final static String THE_XML = "THE_XML";

    @Mock
    private WholesaleOrderService wholesaleOrderService;

    @Mock
    private WholesaleAuftragMapper wholesaleAuftragMapper;

    @Mock
    private WbciCommonService wbciCommonService;

    @Mock
    private WholesaleAuditService wholesaleAuditService;

    @Mock
    private WholesaleRuemPvMapper wholesaleRuemPvMapper;

    @Mock
    private Client client;

    @InjectMocks
    @Spy
    private WholesaleOrderOutboundServiceImpl wsOrderOutboundService;
    private HashMap<String, Object> requestContext;

    @Before
    public void setup() {
        requestContext = new HashMap<>();
        doAnswer(invocation -> mockSetXml(requestContext)).when(wholesaleOrderService).createOrder(any(), any(), any(), any());
        doAnswer(invocation -> mockSetXml(requestContext)).when(wholesaleOrderService).updateOrder(any(), any(), any(), any(), any());

        doReturn(client).when(wsOrderOutboundService).getClientProxy();
        when(client.getRequestContext()).thenReturn(requestContext);
    }

    @Test
    public void loadWholesaleAudits() throws FindException {
        //given
        String vorabstimmungsId = "testVoId";
        List<WholesaleAudit> expectedWholesaleAudits = newArrayList(new WholesaleAudit());
        when(wholesaleAuditService.findByVorabstimmungsId(vorabstimmungsId)).thenReturn(expectedWholesaleAudits);

        //when
        List<WholesaleAudit> wholesaleAudits = wsOrderOutboundService.loadWholesaleAuditsByVorabstimmungsId(vorabstimmungsId);

        //then
        assertThat(wholesaleAudits, is(expectedWholesaleAudits));
    }


    @Test
    public void loadWholesaleAudits_should_return_empty_List_if_error_occurs() throws FindException {
        //given
        String vorabstimmungsId = "testVoId";
        when(wholesaleAuditService.findByVorabstimmungsId(vorabstimmungsId)).thenThrow(new FindException());

        //when
        List<WholesaleAudit> wholesaleAudits = wsOrderOutboundService.loadWholesaleAuditsByVorabstimmungsId(vorabstimmungsId);

        //then
        assertThat(wholesaleAudits, hasSize(0));
    }

    @Test
    public void sendWholesaleCreateOrderPV() throws Exception {
        //given
        //FIXME Abhaengigkeit zu WHOX-1363
        String expectedLineId = "DEU.MNET.123456789";
        String expectedExterneAuftragsnummer = "vaid";
        String loginName = "testuser";

        Person person = PersonTestdata.createPerson();

        PreAgreementVO expectedPreAgreementVO = createPreAgreemetVO();
        WbciGeschaeftsfallKueMrn expectedWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrn();
        when(wbciCommonService.findWbciGeschaeftsfall(eq(expectedPreAgreementVO.getVaid()))).thenReturn(expectedWbciGeschaeftsfall);
        Standort standort = StandortTestdata.createStandort();
        expectedWbciGeschaeftsfall.setStandort(standort);
        expectedWbciGeschaeftsfall.setEndkunde(person);
        AuftragType expectedAuftrag = new AuftragType();
        when(wholesaleAuftragMapper.createPVAuftrag(eq(expectedWbciGeschaeftsfall), eq(expectedExterneAuftragsnummer), eq(expectedLineId), eq(standort), eq(person))).thenReturn(expectedAuftrag);

        //when
        wsOrderOutboundService.sendWholesaleCreateOrderPV(expectedPreAgreementVO, loginName);

        //then
        verify(wbciCommonService).findWbciGeschaeftsfall(eq(expectedPreAgreementVO.getVaid()));
        verify(wholesaleAuftragMapper).createPVAuftrag(eq(expectedWbciGeschaeftsfall), eq(expectedExterneAuftragsnummer), eq(expectedLineId), eq(standort), eq(person));
        verify(wholesaleOrderService).createOrder(eq(SENDING_CARRIER), eq(RECEIVING_CARRIER), eq(SPRI_VERSION), eq(expectedAuftrag));

        assertAudit();
    }

    private void assertAudit() throws StoreException {
        final Object requestXml = requestContext.get(WholesaleRequestInterceptor.REQUEST_XML_OUT);
        assertThat(requestXml, instanceOf(RequestXml.class));

        final WholesaleAudit expectedAudit = new WholesaleAudit();
        expectedAudit.setRequestXml(THE_XML);
        final ArgumentCaptor<WholesaleAudit> wholesaleAuditCaptor = ArgumentCaptor.forClass(WholesaleAudit.class);
        verify(wholesaleAuditService).saveWholesaleAudit(wholesaleAuditCaptor.capture());
        assertThat(wholesaleAuditCaptor.getValue().getRequestXml(), is(THE_XML));
    }

    private Object mockSetXml(HashMap<String, Object> requestContext) {
        final RequestXml requestXml = (RequestXml) requestContext.get(WholesaleRequestInterceptor.REQUEST_XML_OUT);
        if (requestXml != null) {
            requestXml.setXml(THE_XML);
        }
        return null;
    }

    private PreAgreementVO createPreAgreemetVO() {
        return ((PreAgreementVOBuilder) new PreAgreementVOBuilder()
                .withVaid("vaid"))
                .withAuftragNoOrig(42L)
                .withMnetTechnologie(Technologie.FTTH)
                .build();
    }

    private void assertRuemPvAttribute(MeldungsattributeRUEMPVType meldungsattribute) {
        assertThat(meldungsattribute, notNullValue());

        ProviderwechselRueckmeldungType abgebenderProvider = meldungsattribute.getAbgebenderProvider();
        assertAbgebenderProvider(abgebenderProvider);
        assertThat(meldungsattribute.getAnschluss().getLineId(), is(MeldungstypAKMPVTypeTestdata.LINE_ID));
        assertThat(meldungsattribute.getAuftraggebernummer(), is(MeldungstypAKMPVTypeTestdata.AUFTRAGGEBERNUMMER));
        assertThat(meldungsattribute.getExterneAuftragsnummer(), is(MeldungstypAKMPVTypeTestdata.EXTERNE_AUFTRAGSNUMMER));
        assertThat(meldungsattribute.getVertragsnummer(), is(MeldungstypAKMPVTypeTestdata.VERTRAGSNUMMER));
        assertThat(meldungsattribute.getVorabstimmungId(), is(MeldungstypAKMPVTypeTestdata.VORABSTIMMUNG_ID));
    }

    @Test
    public void testSendUpdateOrderRUEMPV() throws StoreException {
        //given
        AuftragstypType auftragstypType = createAuftragstypTyp();
        MeldungstypAKMPVType meldungstypAKMPVType = createMeldungstypAKMPVType();

        //Mapper should be tested in WholesaleRuemPvMapperTest
        when(wholesaleRuemPvMapper.createRUEMPV(meldungstypAKMPVType)).thenReturn(new WholesaleRuemPvMapper().createRUEMPV(meldungstypAKMPVType));
        ArgumentCaptor<MessageType> messageTypeCaptor = ArgumentCaptor.forClass(MessageType.class);

        //when
        wsOrderOutboundService.sendWholesaleUpdateOrderRUEMPV(auftragstypType, meldungstypAKMPVType);

        //then
        verify(wholesaleOrderService).updateOrder(eq(SENDING_CARRIER), eq(RECEIVING_CARRIER), eq(SPRI_VERSION), eq(auftragstypType), messageTypeCaptor.capture());

        MessageType messageType = messageTypeCaptor.getValue();

        assertThat(messageType.getRUEMPV(), notNullValue());
        MeldungsattributeRUEMPVType meldungsattribute = messageType.getRUEMPV().getMeldungsattribute();
        assertThat(meldungsattribute, notNullValue());
        assertRuemPvAttribute(meldungsattribute);

        assertAudit();
    }

    private void assertAbgebenderProvider(ProviderwechselRueckmeldungType abgebenderProvider) {
        assertThat(abgebenderProvider, notNullValue());
        assertThat(abgebenderProvider.getZustimmungProviderwechsel(), is(JaNeinType.J));
        assertThat(abgebenderProvider.getProvidername(), is(SENDING_CARRIER));
    }

    private AuftragstypType createAuftragstypTyp() {
        AuftragstypType auftragstypType = new AuftragstypType();
        auftragstypType.setGeschaeftsfall(GeschaeftsfallMeldungType.PV);
        auftragstypType.setAenderungsKennzeichen(AenderungskennzeichenType.STANDARD);
        auftragstypType.setGeschaeftsfallArt(GeschaeftsfallArtType.ENDKUNDENANBIETERWECHSEL);
        return auftragstypType;
    }
}