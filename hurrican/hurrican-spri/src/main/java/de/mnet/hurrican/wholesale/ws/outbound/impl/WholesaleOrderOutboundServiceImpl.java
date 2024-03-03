package de.mnet.hurrican.wholesale.ws.outbound.impl;

import static de.mnet.hurrican.wholesale.interceptor.WholesaleRequestInterceptor.*;

import java.math.*;
import java.time.*;
import java.util.*;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.WholesaleOrderService;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MessageType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AuftragType;
import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.RequestXml;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.service.WholesaleAuditService;
import de.mnet.hurrican.wholesale.ws.MessageTypeSpri;
import de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService;
import de.mnet.hurrican.wholesale.ws.outbound.mapper.WholesaleAuftragMapper;
import de.mnet.hurrican.wholesale.ws.outbound.mapper.WholesaleRuemPvMapper;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.service.WbciCommonService;


/**
 * Service for outgoing messages using the WholesaleOrderService interface
 */
@CcTxRequired
@Component("de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService")
public class WholesaleOrderOutboundServiceImpl implements WholesaleOrderOutboundService {

    private static final Logger LOG = LoggerFactory.getLogger(WholesaleOrderOutboundServiceImpl.class);

    public static final String SENDING_CARRIER = "MNETH"; //MNET-H
    private static final String RECEIVING_CARRIER = EkpFrameContract.EKP_ID_MNET; //MNET
    private static final BigInteger SPRI_VERSION = BigInteger.valueOf(4);
    public static final String SYSTEM = "SYSTEM";

    @Autowired
    private WholesaleOrderService wholesaleOrderService;

    @Autowired
    private WholesaleAuditService wholesaleAuditService;

    @Autowired
    private WholesaleAuftragMapper wholesaleAuftragMapper;

    @Autowired
    private WholesaleRuemPvMapper wholesaleRuemPvMapper;

    @Autowired
    private WbciCommonService wbciCommonService;

    @Override
    public void sendWholesaleCreateOrderPV(PreAgreementVO preAgreementVO, String loginName) {
        LOG.debug(">> sendWholesaleCreateOrderPV: {}", preAgreementVO);
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(preAgreementVO.getVaid());
        WbciGeschaeftsfallKue wbciGeschaeftsfallKue = (WbciGeschaeftsfallKue) wbciGeschaeftsfall;
        Standort standort = wbciGeschaeftsfallKue.getStandort();
        PersonOderFirma endkundePersonOderFirma = wbciGeschaeftsfallKue.getEndkunde();

        //FIXME Abhaengigkeit zu WHOX-1363
        String lineId = "DEU.MNET.123456789";

        String externeAuftragsnummer = preAgreementVO.getVaid();
        AuftragType auftrag = wholesaleAuftragMapper.createPVAuftrag(wbciGeschaeftsfall, externeAuftragsnummer, lineId, standort, endkundePersonOderFirma);

        Client client = configureClient();

        wholesaleOrderService.createOrder(SENDING_CARRIER, RECEIVING_CARRIER, SPRI_VERSION, auftrag);
        writeWholesaleAudit("PV", preAgreementVO.getVaid(), (RequestXml) client.getRequestContext().get(REQUEST_XML_OUT), loginName);
    }


    @Override
    public void sendWholesaleUpdateOrderRUEMPV(AuftragstypType auftragsType, MeldungstypAKMPVType akmpv) {
        LOG.debug(">> sendWholesaleUpdateOrderRUEMPV: {}", auftragsType);

        Client client = configureClient();

        MessageType message = new MessageType();
        message.setRUEMPV(wholesaleRuemPvMapper.createRUEMPV(akmpv));

        wholesaleOrderService.updateOrder(SENDING_CARRIER, RECEIVING_CARRIER, SPRI_VERSION, auftragsType, message);
        writeWholesaleAudit(MessageTypeSpri.RUEMPV.getLabel(), akmpv.getMeldungsattribute().getVorabstimmungId(), (RequestXml) client.getRequestContext().get(REQUEST_XML_OUT), SYSTEM);
    }

    private Client configureClient() {
        Client client = getClientProxy();
        final RequestXml requestXml = new RequestXml();
        client.getRequestContext().put(REQUEST_XML_OUT, requestXml);
        return client;
    }


    @Override
    public List<WholesaleAudit> loadWholesaleAuditsByVorabstimmungsId(String vorabstimmungsId) {
        List<WholesaleAudit> wholesaleAudits;

        try {
            wholesaleAudits = wholesaleAuditService.findByVorabstimmungsId(vorabstimmungsId);
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            wholesaleAudits = Collections.emptyList();
        }

        return wholesaleAudits;
    }


    private void writeWholesaleAudit(String beschreibung, String vorabstimmungsId, RequestXml xmlMessage, String loginName) {
        LOG.debug(">> writeWholesauidit: {}", xmlMessage);
        WholesaleAudit wholesaleAudit = createWholesaleAudit(beschreibung, vorabstimmungsId);
        wholesaleAudit.setBearbeiter(loginName);
        wholesaleAudit.setRequestXml(xmlMessage.getXml());
        try {
            wholesaleAuditService.saveWholesaleAudit(wholesaleAudit);
        }
        catch (StoreException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private WholesaleAudit createWholesaleAudit(String beschreibung, String vorabstimmungsId) {
        WholesaleAudit wholesaleAudit = new WholesaleAudit();
        wholesaleAudit.setVorabstimmungsId(vorabstimmungsId);
        wholesaleAudit.setBeschreibung(beschreibung);
        wholesaleAudit.setDatum(LocalDateTime.now());
        wholesaleAudit.setStatus(PvStatus.GESENDET);
        return wholesaleAudit;
    }

    // Package protected for testing
    Client getClientProxy() {
        return ClientProxy.getClient(wholesaleOrderService);
    }
}
