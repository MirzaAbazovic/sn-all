/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.hurrican.acceptance.behavior;

/**
 * Performs CPS transaction service response and validates according service response acknowledgement. Optionally
 * also expects update resource operation on Command resource inventory interface as result of the Freigabe date processing in Hurrican.
 *
 */
public class SendCpsServiceResponse_Behavior extends AbstractTestBehavior {

    /** Should receive update resource with Freigabe date on Command resource inventory */
    private boolean updateResourceFreigabe = true;

    private String cpsServiceResponseTemplate = "cpsServiceResponse";
    private String cpsServiceResponseAckTemplate = "cpsServiceResponseAck";

    @Override
    public void apply() {
        cps().sendCpsAsyncServiceResponse(cpsServiceResponseTemplate);

        if (updateResourceFreigabe) {
            resourceInventory().receiveUpdateResourceRequest("updateResourceRequest_Freigabe");
            resourceInventory().sendUpdateResourceResponse("updateResourceResponse");
        }

        cps().receiveCpsAsyncServiceResponseAck(cpsServiceResponseAckTemplate);
    }

    /**
     * Enables/disables check on Command inventory resource interface for update resource on Freigabe date.
     * @param doUpdateResource
     * @return
     */
    public SendCpsServiceResponse_Behavior withUpdateResourceFreigabe(boolean doUpdateResource) {
        this.updateResourceFreigabe = doUpdateResource;
        return this;
    }

    public SendCpsServiceResponse_Behavior withCpsServiceResponseTemplate(String templateName) {
        this.cpsServiceResponseTemplate = templateName;
        return this;
    }

    public SendCpsServiceResponse_Behavior withCpsServiceResponseAckTemplate(String templateName) {
        this.cpsServiceResponseAckTemplate = templateName;
        return this;
    }
}
