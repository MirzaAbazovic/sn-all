/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.14
 */
package de.mnet.wbci.model;

/**
 * Container used for recording the results of the message processing. The business services used for processing a
 * message can record the results of the processing here.
 * <p/>
 * This information can be used later by post processing services, or indeed in the camel route, to determine what
 * additional processing steps are necessary or can be skipped.
 */
public class MessageProcessingMetadata {
    private boolean postProcessMessage = true;
    private boolean isIncomingMessageDuplicateVaRequest = false;
    private boolean isResponseToDuplicateVaRequest = false;

    /**
     * Used for indicating whether the incoming message should be post processed or not. The default value is
     * {@literal false} when not overridden.
     *
     * @return
     */
    public boolean  isPostProcessMessage() {
        return postProcessMessage;
    }

    public void setPostProcessMessage(boolean postProcessMessage) {
        this.postProcessMessage = postProcessMessage;
    }

    /**
     * Used for indicating whether the incoming VA request is a duplicate request. The default value is {@literal false}
     * when not overridden.
     *
     * @return
     */
    public boolean isIncomingMessageDuplicateVaRequest() {
        return isIncomingMessageDuplicateVaRequest;
    }

    public void setIncomingMessageDuplicateVaRequest(boolean incomingMessageDuplicateVaRequest) {
        this.isIncomingMessageDuplicateVaRequest = incomingMessageDuplicateVaRequest;
    }

    /**
     * Used for indicating whether the outgoing message is a response to an duplicate VA request. The default value is
     * {@literal false} when not overridden.
     *
     * @return
     */
    public boolean isResponseToDuplicateVaRequest() {
        return isResponseToDuplicateVaRequest;
    }

    public void setResponseToDuplicateVaRequest(boolean responseToDuplicateVaRequest) {
        this.isResponseToDuplicateVaRequest = responseToDuplicateVaRequest;
    }
}
