/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 09:34:54
 */
package de.mnet.wita.model;

import java.io.*;
import java.time.*;

import de.mnet.wita.message.MeldungsType;

/**
 * DTO-Objekt fuer die Messages, die nach einem aufgetretenen Workflow-Fehler erneut in diesen Workflow geschickt werden
 * können
 */
public class ResendableMessage implements Serializable {

    private static final long serialVersionUID = -419803413002542440L;

    private String businessKey;
    private MeldungsType messageType;
    private LocalDateTime versandZeitstempel;
    private Long mwfEntityId;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public MeldungsType getMessageType() {
        return messageType;
    }

    public void setMessageType(MeldungsType messageType) {
        this.messageType = messageType;
    }

    public LocalDateTime getVersandZeitstempel() {
        return versandZeitstempel;
    }

    public void setVersandZeitstempel(LocalDateTime versandZeitstempel) {
        this.versandZeitstempel = versandZeitstempel;
    }

    public Long getMwfEntityId() {
        return mwfEntityId;
    }

    public void setMwfEntityId(Long mwfEntityId) {
        this.mwfEntityId = mwfEntityId;
    }

    @Override
    public String toString() {
        return "ResendableMessage [businessKey=" + businessKey + ", messageType=" + messageType
                + ", versandZeitstempel=" + versandZeitstempel + ", mwfEntityId=" + mwfEntityId + "]";
    }
}
