/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class ISDNBackupBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.ISDNBackup> {

    private String triggerPoint;
    private String calledStationId;
    private String callingStationId;
    private String intervall;
    private String reenable;
    private String idleTimer;
    private String attempts;
    private String frequency;
    private String channelBundling;

    @Override
    public OrderTechnicalParams.ISDNBackup build() {
        OrderTechnicalParams.ISDNBackup isdnBackup = new OrderTechnicalParams.ISDNBackup();
        isdnBackup.setTriggerPoint(this.triggerPoint);
        isdnBackup.setCalledStationId(this.calledStationId);
        isdnBackup.setCallingStationId(this.callingStationId);
        isdnBackup.setIntervall(this.intervall);
        isdnBackup.setReenable(this.reenable);
        isdnBackup.setIdleTimer(this.idleTimer);
        isdnBackup.setAttempts(this.attempts);
        isdnBackup.setFrequency(this.frequency);
        isdnBackup.setChannelBundling(this.channelBundling);
        return isdnBackup;
    }

    public ISDNBackupBuilder withTriggerPoint(String triggerPoint) {
        this.triggerPoint = triggerPoint;
        return this;
    }


    public ISDNBackupBuilder withCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
        return this;
    }

    public ISDNBackupBuilder withCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
        return this;
    }

    public ISDNBackupBuilder withIntervall(String intervall) {
        this.intervall = intervall;
        return this;
    }

    public ISDNBackupBuilder withReenable(String reenable) {
        this.reenable = reenable;
        return this;
    }

    public ISDNBackupBuilder withIdleTimer(String idleTimer) {
        this.idleTimer = idleTimer;
        return this;
    }

    public ISDNBackupBuilder withAttempts(String attempts) {
        this.attempts = attempts;
        return this;
    }

    public ISDNBackupBuilder withFrequency(String frequency) {
        this.frequency = frequency;
        return this;
    }

    public ISDNBackupBuilder withChannelBundling(String channelBundling) {
        this.channelBundling = channelBundling;
        return this;
    }
}