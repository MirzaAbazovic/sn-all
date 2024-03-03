/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 17.02.2017

 */

package de.mnet.hurrican.wholesale.ws.inbound;


import java.math.*;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.WholesaleOrderService;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MessageType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AuftragType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.TerminverschiebungType;
import de.mnet.hurrican.wholesale.ws.inbound.processor.UpdateOrderProcessor;

/**
 * Implementiert den Endpoint des WholesaleOrderService.
 * Created by morozovse on 17.02.2017.
 */
@CcTxRequired
@Component
public class WholesaleOrderServiceImpl implements WholesaleOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(WholesaleOrderServiceImpl.class);

    @Autowired
    private UpdateOrderProcessor updateOrderProcessor;

    @Override
    public void updateOrder(String sendingCarrier, String receivingCarrier, BigInteger version, AuftragstypType auftagsType, MessageType messageType) {
        try {
            Message message = PhaseInterceptorChain.getCurrentMessage();
            updateOrderProcessor.process(auftagsType, messageType, message);
        }
        catch (Exception e) {
            LOG.error("Unerwarteter Fehler im WholesaleOrderService: ", e);
        }
    }

    @Override
    public void rescheduleOrder(String sendingCarrier, String receivingCarrier, BigInteger version, TerminverschiebungType order) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void createOrder(String sendingCarrier, String receivingCarrier, BigInteger version, AuftragType order) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void cancelOrder(String sendingCarrier, String receivingCarrier, BigInteger version, AuftragType order) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
