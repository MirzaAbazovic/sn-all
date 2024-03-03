/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 28.06.2010 09:17:21
  */

package de.mnet.hurrican.webservice.taifun.order.transfer.endpoint;

import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;
import de.mnet.hurricanweb.order.transfer.types.TransferOrderType;
import de.mnet.hurricanweb.order.transfer.types.TransferOrderTypeAcknowledgement;
import de.mnet.hurricanweb.order.transfer.types.TransferOrderTypeAcknowledgementDocument;
import de.mnet.hurricanweb.order.transfer.types.TransferOrderTypeDocument;

/**
 *
 */
public class TransferOrderRequestEndpoint extends MnetAbstractMarshallingPayloadEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TransferOrderRequestEndpoint.class);

    @Override
    protected Object invokeInternal(Object object) throws Exception {
        LOGGER.debug("--------- TransferOrderRequestEndpoint called");

        TransferOrderTypeDocument document = (TransferOrderTypeDocument) object;
        TransferOrderType input = document.getTransferOrderType();

        Long orderNo = Long.valueOf(input.getSourceOrderNumber());
        Long sourceCustomerNo = Long.valueOf(input.getSourceCustomerNumber());
        Long targetCustomerNo = Long.valueOf(input.getTargetCustomerNumber());

        LOGGER.info(String.format("transfering order %d from customer %d to customer %d", orderNo, sourceCustomerNo, targetCustomerNo));

        CCAuftragService ccAuftragService = getCCService(CCAuftragService.class);
        ccAuftragService.changeCustomerIdOnAuftrag(orderNo, sourceCustomerNo, targetCustomerNo);

        TransferOrderTypeAcknowledgementDocument ackDoc = TransferOrderTypeAcknowledgementDocument.Factory.newInstance();
        TransferOrderTypeAcknowledgement acknowledgement = ackDoc.addNewTransferOrderTypeAcknowledgement();
        acknowledgement.setTransferSuccessful(true);
        return acknowledgement;
    }

}
