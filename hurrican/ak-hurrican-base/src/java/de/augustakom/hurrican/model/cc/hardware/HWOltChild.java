/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.04.2014
 */
package de.augustakom.hurrican.model.cc.hardware;

import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;

/**
 * Gemeinsame Basisklasse fuer Hardware die an einem OLT Port (am anderen Ende) haengt.
 * <p/>
 * Created by guiber on 09.04.2014.
 */
public abstract class HWOltChild extends HWRack {
    private static final long serialVersionUID = 6834608948880238359L;
    // OLT Rack=Frame
    // OLT Subrack(Alcatel)=Shelf(Huawei)
    // OLT Slot=LT

    private Long oltRackId = null;
    private String serialNo = null;
    private String oltFrame = null;
    private String oltSubrack = null;
    private String oltSlot = null;
    private String oltGPONPort = null;
    private String oltGPONId = null;
    // das Freigabedatum wird bei erfolgreicher Initialisierung des CPS gesetzt
    private Date freigabe = null;

    public Long getOltRackId() {
        return oltRackId;
    }

    public void setOltRackId(Long oltRackId) {
        this.oltRackId = oltRackId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getOltFrame() {
        return oltFrame;
    }

    public void setOltFrame(String oltFrame) {
        this.oltFrame = oltFrame;
    }

    public String getOltSlot() {
        return oltSlot;
    }

    public void setOltSlot(String oltSlot) {
        this.oltSlot = oltSlot;
    }

    @Transient
    public Integer getOltSlotAsInteger() {
        return Integer.valueOf(oltSlot);
    }

    public String getOltGPONPort() {
        return oltGPONPort;
    }

    public void setOltGPONPort(String oltGPONPort) {
        this.oltGPONPort = oltGPONPort;
    }

    public String getOltGPONId() {
        return oltGPONId;
    }

    public void setOltGPONId(String oltGPONId) {
        this.oltGPONId = oltGPONId;
    }

    public String getOltSubrack() {
        return oltSubrack;
    }

    public void setOltSubrack(String oltSubrack) {
        this.oltSubrack = oltSubrack;
    }

    public Date getFreigabe() {
        return freigabe;
    }

    public void setFreigabe(Date freigabe) {
        this.freigabe = freigabe;
    }

    /**
     * @return the formatted GPON Port (from oltFrame-[oltSubrack-]-oltSlot-oltGponPort-oltGponId)
     */
    public String getGponPort() {
        String gponPortTemplate = (StringUtils.isNotBlank(getOltSubrack())) ? "{0}-{1}-{2}-{3}-{4}" : "{0}-{1}-{2}-{3}";
        Object[] gponParams = (StringUtils.isNotBlank(getOltSubrack()))
                ? new String[] { getOltFrame(), getOltSubrack(), getOltSlot(), getOltGPONPort(), getOltGPONId() }
                : new String[] { getOltFrame(), getOltSlot(), getOltGPONPort(), getOltGPONId() };
        return StringTools.formatString(gponPortTemplate, gponParams);
    }

}
