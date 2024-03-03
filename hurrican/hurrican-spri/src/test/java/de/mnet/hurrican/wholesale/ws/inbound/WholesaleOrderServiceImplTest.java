package de.mnet.hurrican.wholesale.ws.inbound;

import static org.mockito.Mockito.*;

import java.math.*;
import org.apache.cxf.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.AenderungskennzeichenType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.GeschaeftsfallArtType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.GeschaeftsfallMeldungType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypERLMKType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MessageType;
import de.mnet.hurrican.wholesale.ws.inbound.processor.UpdateOrderProcessor;

/**
 * Created by morozovse on 20.02.2017.
 */
public class WholesaleOrderServiceImplTest {

    private static final String SENDING_CARRIER = EkpFrameContract.EKP_ID_MNET; //MNET
    private static final String RECEIVING_CARRIER = EkpFrameContract.EKP_ID_MNET; //MNET
    private static final BigInteger SPRI_VERSION = BigInteger.valueOf(4);

    @InjectMocks
    @Spy
    private WholesaleOrderServiceImpl cut;

    @Mock
    private UpdateOrderProcessor updateOrderProcessor;

    private MessageType messageType;

    private AuftragstypType auftragstypType;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        messageType = new MessageType();
        auftragstypType = new AuftragstypType();
        auftragstypType.setGeschaeftsfall(GeschaeftsfallMeldungType.PV);
        auftragstypType.setAenderungsKennzeichen(AenderungskennzeichenType.STANDARD);
        auftragstypType.setGeschaeftsfallArt(GeschaeftsfallArtType.ENDKUNDENANBIETERWECHSEL);
    }

    @Test
    public void testUpdateOrderSuccess() throws Exception {
        MeldungstypAKMPVType meldungstypAKMPVType = new MeldungstypAKMPVType();
        messageType.setAKMPV(meldungstypAKMPVType);

        cut.updateOrder(SENDING_CARRIER,RECEIVING_CARRIER, SPRI_VERSION, auftragstypType, messageType);

        verify(updateOrderProcessor).process(eq(auftragstypType), eq(messageType), Matchers.isNull(Message.class));
    }

    @Test
    public void testUpdateOrderNoMessage() throws Exception {
        MeldungstypAKMPVType meldungstypAKMPVType = new MeldungstypAKMPVType();
        messageType.setAKMPV(meldungstypAKMPVType);

        cut.updateOrder(SENDING_CARRIER,RECEIVING_CARRIER, SPRI_VERSION, auftragstypType, null);
    }

    @Test
    public void testUpdateOrderWrongMessageType() throws Exception {
        MeldungstypERLMKType meldungstypERLMKType = new MeldungstypERLMKType();
        messageType.setERLMK(meldungstypERLMKType);

        cut.updateOrder(SENDING_CARRIER,RECEIVING_CARRIER, SPRI_VERSION, auftragstypType, messageType);
    }

    @Test
    public void testUpdateOrderWithUnexpectedException() throws Exception {
        MeldungstypERLMKType meldungstypERLMKType = new MeldungstypERLMKType();
        messageType.setERLMK(meldungstypERLMKType);
        doThrow(new IllegalArgumentException()).when(updateOrderProcessor).process(auftragstypType, messageType, null);

        cut.updateOrder(SENDING_CARRIER,RECEIVING_CARRIER, SPRI_VERSION, auftragstypType, messageType);
    }
}