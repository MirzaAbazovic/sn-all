/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class ISDNBackupTestBuilder extends ISDNBackupBuilder {

    public ISDNBackupTestBuilder() {
        withAttempts("Attempts");
        withCalledStationId("called station id");
        withCallingStationId("calling station id");
        withChannelBundling("channel bundling");
        withFrequency("frequnecy");
        withIdleTimer("idle timer");
        withIntervall("interval");
        withReenable("reenable");
        withTriggerPoint("trigger point");
    }
}