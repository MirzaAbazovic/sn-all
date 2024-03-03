/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 07:10:53
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.mnet.common.tools.DateConverterUtils;

/**
 * DTO zur Abbildung eines fuer Wholesale verwendbaren Ports.
 */
public class WholesalePort {

    private Long portId;
    private boolean free;
    private String techType;
    private String hwEqnIn;
    private String portTypeIn;
    private String hwRackNameIn;
    private Long maxBandwidth;
    private LocalDate availableFrom;
    private String comment;

    public static WholesalePort createWholesalePort(RangierungsEquipmentView rangierungEquipmentView) {
        WholesalePort port = new WholesalePort();
        port.setPortId(rangierungEquipmentView.getRangierId());
        port.setFree(rangierungEquipmentView.getFreigabe());
        port.setTechType(rangierungEquipmentView.getPhysikTyp());
        port.setHwEqnIn(rangierungEquipmentView.getHwEqnIn());
        port.setPortTypeIn(rangierungEquipmentView.getHwBgTypEqIn());
        port.setHwRackNameIn(rangierungEquipmentView.getRackEqIn());

        Long maxDownstream = null;
        if (rangierungEquipmentView.getHwEqInMaxBandwidth() != null){
            maxDownstream = rangierungEquipmentView.getHwEqInMaxBandwidth().getDownstream().longValue();
        }

        port.setMaxBandwidth(maxDownstream);
        port.setAvailableFrom(
                DateConverterUtils.asLocalDate(Optional.ofNullable(rangierungEquipmentView.getFreigabeAb()).orElse(new Date())));
        port.setComment(rangierungEquipmentView.getBemerkung());
        return port;
    }

    public Long getPortId() {
        return portId;
    }

    public void setPortId(Long portId) {
        this.portId = portId;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getTechType() {
        return techType;
    }

    public void setTechType(String techType) {
        this.techType = techType;
    }

    public String getHwEqnIn() {
        return hwEqnIn;
    }

    public void setHwEqnIn(String hwEqnIn) {
        this.hwEqnIn = hwEqnIn;
    }

    public String getPortTypeIn() {
        return portTypeIn;
    }

    public void setPortTypeIn(String portTypeIn) {
        this.portTypeIn = portTypeIn;
    }

    public String getHwRackNameIn() {
        return hwRackNameIn;
    }

    public void setHwRackNameIn(String hwRackNameIn) {
        this.hwRackNameIn = hwRackNameIn;
    }

    public Long getMaxBandwidth() {
        return maxBandwidth;
    }

    public void setMaxBandwidth(Long maxBandwidth) {
        this.maxBandwidth = maxBandwidth;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}


