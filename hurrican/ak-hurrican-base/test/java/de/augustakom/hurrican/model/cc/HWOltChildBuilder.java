/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2014
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.hardware.HWOltChild;

/**
 * Created by guiber on 10.04.2014.
 */
public abstract class HWOltChildBuilder<BUILDER extends AbstractCCIDModelBuilder<BUILDER, ENTITY>, ENTITY extends HWOltChild>
        extends HWRackBuilder<BUILDER, ENTITY> {
    private Long rackId = null;
    private HWOltBuilder hwOltBuilder = null;
    private Long oltRackId = null;
    private String serialNo = randomString(50);
    private String ipAddress = null;
    private String oltFrame = "00";
    private String oltSubrack = "01";
    private String oltSlot = "02";
    private String oltGPONPort = "03";
    private String oltGPONId = "04";
    private Date freigabe = new Date();

    @Override
    protected void beforeBuild() {
        if (hwOltBuilder != null) {
            oltRackId = hwOltBuilder.get().getId();
        }
    }

    public BUILDER withHWRackOltBuilder(HWOltBuilder hwOltBuilder) {
        this.hwOltBuilder = hwOltBuilder;
        return (BUILDER) this;
    }

    public BUILDER withSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return (BUILDER) this;
    }

    public BUILDER withOltSlot(String oltSlot) {
        this.oltSlot = oltSlot;
        return (BUILDER) this;
    }

    public BUILDER withOltGPONPort(String oltGPONPort) {
        this.oltGPONPort = oltGPONPort;
        return (BUILDER) this;
    }

    public BUILDER withOltGPONId(String oltGPONId) {
        this.oltGPONId = oltGPONId;
        return (BUILDER) this;
    }

    public BUILDER withOltFrame(final String olftFrame) {
        this.oltFrame = olftFrame;
        return (BUILDER) this;
    }

    public BUILDER withOltSubrack(final String oltSubrack)  {
        this.oltSubrack = oltSubrack;
        return (BUILDER) this;
    }

    public BUILDER withFreigabe(final Date freigabe)  {
        this.freigabe = freigabe;
        return (BUILDER) this;
    }
}
