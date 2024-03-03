package de.mnet.hurrican.wholesale.ws.inbound.processor;

import static de.mnet.hurrican.wholesale.ws.outbound.testdata.MeldungstypAKMPVTypeTestdata.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.*;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungsattributeABMType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypABMType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypQEBType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MessageType;
import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.service.WholesaleAuditService;
import de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService;
import de.mnet.hurrican.wholesale.ws.outbound.testdata.MeldungstypAKMPVTypeTestdata;

/**
 * Created by morozovse on 21.02.2017.
 */
public class UpdateOrderProcessorTest {

    private static final String SYSTEM = "SYSTEM";
    private static final String AKM_PV = "AKM-PV";
    private static final String QEB = "QEB";
    private static final String ABM = "ABM";
    private static final String TEST_XML = "TestXML";
    private static final String VORABSTIMMUNGS_ID = "vid";

    @InjectMocks
    @Spy
    private UpdateOrderProcessor cut;

    @Mock
    private WholesaleAuditService wholesaleAuditService;

    @Mock
    private WholesaleOrderOutboundService wholesaleOrderOutboundService;

    private MessageType messageType;

    private Message message = new MessageImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        messageType = new MessageType();

        message.put("REQUEST_XML", TEST_XML);
    }

    @Test
    public void testProcessAKMPV() throws Exception {
        MeldungstypAKMPVType meldungstypAKMPVType = createMeldungstypAKMPVType();
        AuftragstypType auftragstypType = new AuftragstypType();

        messageType.setAKMPV(meldungstypAKMPVType);
        LocalDateTime auditTime = LocalDateTime.now();
        when(cut.createCurrentDateTime()).thenReturn(auditTime);

        cut.process(auftragstypType, messageType, message);

        final ArgumentCaptor<WholesaleAudit> wholesaleAuditArgumentCaptor = ArgumentCaptor.forClass(WholesaleAudit.class);
        verify(wholesaleAuditService).saveWholesaleAudit(wholesaleAuditArgumentCaptor.capture());
        WholesaleAudit wholesaleAudit = wholesaleAuditArgumentCaptor.getValue();
        assertWholesaleAudit(wholesaleAudit, auditTime, AKM_PV, MeldungstypAKMPVTypeTestdata.VORABSTIMMUNG_ID);
        verify(cut).createCurrentDateTime();

        verify(wholesaleOrderOutboundService).sendWholesaleUpdateOrderRUEMPV(auftragstypType, meldungstypAKMPVType);
    }

    /**
     * Processing AKMPV without Meldungsattribute trigggers an Exception.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProcessAKMPVIllegalArgumentException() throws Exception {
        MeldungstypAKMPVType meldungstypAKMPVType = new MeldungstypAKMPVType();
        messageType.setAKMPV(meldungstypAKMPVType);

        cut.process(new AuftragstypType(), messageType, message);
    }

    @Test
    public void testProcessQEB() throws Exception {
        createQebMessageType(VORABSTIMMUNGS_ID);
        AuftragstypType auftragstypType = new AuftragstypType();
        LocalDateTime auditTime = LocalDateTime.now();
        when(cut.createCurrentDateTime()).thenReturn(auditTime);

        cut.process(auftragstypType, messageType, message);

        final ArgumentCaptor<WholesaleAudit> wholesaleAuditArgumentCaptor = ArgumentCaptor.forClass(WholesaleAudit.class);
        verify(wholesaleAuditService).saveWholesaleAudit(wholesaleAuditArgumentCaptor.capture());
        WholesaleAudit wholesaleAudit = wholesaleAuditArgumentCaptor.getValue();
        assertWholesaleAudit(wholesaleAudit, auditTime, QEB, VORABSTIMMUNGS_ID);
        verify(cut).createCurrentDateTime();
    }

    @Test
    public void testProcessABM() throws Exception {
        createAbmMessageType(VORABSTIMMUNGS_ID);
        AuftragstypType auftragstypType = new AuftragstypType();
        LocalDateTime auditTime = LocalDateTime.now();
        when(cut.createCurrentDateTime()).thenReturn(auditTime);

        cut.process(auftragstypType, messageType, message);

        final ArgumentCaptor<WholesaleAudit> wholesaleAuditArgumentCaptor = ArgumentCaptor.forClass(WholesaleAudit.class);
        verify(wholesaleAuditService).saveWholesaleAudit(wholesaleAuditArgumentCaptor.capture());
        WholesaleAudit wholesaleAudit = wholesaleAuditArgumentCaptor.getValue();
        assertWholesaleAudit(wholesaleAudit, auditTime, ABM, VORABSTIMMUNGS_ID);
        verify(cut).createCurrentDateTime();
    }

    /**
     * Processing ABM Message with empty vorabstimmungsId triggers an Exception.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProcessABMWithEmptyVorabstimmungIdFailure() throws Exception {
        createAbmMessageType("");
        AuftragstypType auftragstypType = new AuftragstypType();

        cut.process(auftragstypType, messageType, message);
    }

    /**
     * After processing ABM Message thrown Exception while saving an AuditMessage was cought and ignored/logged only.
     *
     * @throws Exception
     */
    @Test
    public void testProcessABMWithStoreException() throws Exception {
        createAbmMessageType(VORABSTIMMUNGS_ID);
        AuftragstypType auftragstypType = new AuftragstypType();
        doThrow(new StoreException()).when(wholesaleAuditService).saveWholesaleAudit(Matchers.any(WholesaleAudit.class));

        cut.process(auftragstypType, messageType, message);
    }

    /**
     * Processing Message with empty vorabstimmungsId triggers an Exception.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProcessQEBWithEmptyVorabstimmungIdFailure() throws Exception {
        createQebMessageType("");
        AuftragstypType auftragstypType = new AuftragstypType();

        cut.process(auftragstypType, messageType, message);
    }

    /**
     * A thrown Exception while saving an AuditMessage was cought and ignored/logged only.
     *
     * @throws Exception
     */
    @Test
    public void testProcessQEBWithStoreException() throws Exception {
        createQebMessageType(VORABSTIMMUNGS_ID);
        AuftragstypType auftragstypType = new AuftragstypType();
        doThrow(new StoreException()).when(wholesaleAuditService).saveWholesaleAudit(Matchers.any(WholesaleAudit.class));

        cut.process(auftragstypType, messageType, message);
    }

    private void createQebMessageType(String vorabstimmungsId) {
        MeldungstypQEBType meldungstypQEBType = new MeldungstypQEBType();
        MeldungsattributeQEBType meldungsattributeQEBType = new MeldungsattributeQEBType();
        meldungsattributeQEBType.setVorabstimmungId(vorabstimmungsId);
        meldungstypQEBType.setMeldungsattribute(meldungsattributeQEBType);
        messageType.setQEB(meldungstypQEBType);
    }

    private void createAbmMessageType(String vorabstimmungsId) {
        MeldungstypABMType meldungstypABMType = new MeldungstypABMType();
        MeldungsattributeABMType meldungsattributeABMType = new MeldungsattributeABMType();
        meldungsattributeABMType.setVorabstimmungId(vorabstimmungsId);
        meldungstypABMType.setMeldungsattribute(meldungsattributeABMType);
        messageType.setABM(meldungstypABMType);
    }

    private void assertWholesaleAudit(WholesaleAudit wholesaleAudit, LocalDateTime auditTime, String beschreibung, String vorabstimmungsid){
        assertThat(wholesaleAudit.getBearbeiter(), is(SYSTEM));
        assertThat(wholesaleAudit.getVorabstimmungsId(), is(vorabstimmungsid));
        assertThat(wholesaleAudit.getBeschreibung(), is(beschreibung));
        assertThat(wholesaleAudit.getStatus().name(), is(PvStatus.EMPFANGEN.name()));
        assertThat(wholesaleAudit.getRequestXml(), is(TEST_XML));
        assertThat(wholesaleAudit.getDatum(), is(auditTime));
    }
}