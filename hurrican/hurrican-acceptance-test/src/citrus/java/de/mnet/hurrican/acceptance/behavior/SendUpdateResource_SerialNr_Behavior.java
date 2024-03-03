/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.hurrican.acceptance.behavior;

import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Sends update resource request as Command. When serial number is set update on CPS interface should be called
 * with proper CREATE_DEVICE transaction data.
 *
 *
 */
public class SendUpdateResource_SerialNr_Behavior extends AbstractTestBehavior {

    private final String hwRackType;
    private final String serialNumber;

    private String cpsServiceRequestTemplate = "cpsServiceRequest";
    private String cpsServiceRequestAckTemplate = "cpsServiceRequestAck";

    /**
     * Initialize behavior with serial number.
     * @param hwRackType
     * @param serialNumber
     */
    public SendUpdateResource_SerialNr_Behavior(String hwRackType, String serialNumber) {
        this.hwRackType = hwRackType;
        this.serialNumber = serialNumber;
    }

    @Override
    public void apply() {
        variables().add(VariableNames.SERIAL_NUMBER, serialNumber);
        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_SerialNr")
                .fork(true);

        cps().receiveCpsAsyncServiceRequest(cpsServiceRequestTemplate);
        cps().sendCpsAsyncServiceRequestAck(cpsServiceRequestAckTemplate);
        hurrican().verifyCpsTx(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, CPSTransaction.TX_STATE_IN_PROVISIONING, false);

        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifySerialNumber(hwRackType, serialNumber);
    }

    public SendUpdateResource_SerialNr_Behavior withCpsServiceRequestTemplate(String templateName) {
        this.cpsServiceRequestTemplate = templateName;
        return this;
    }

    public SendUpdateResource_SerialNr_Behavior withCpsServiceRequestAckTemplate(String templateName) {
        this.cpsServiceRequestAckTemplate = templateName;
        return this;
    }
}
