/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2007 16:53:58
 */
package de.augustakom.hurrican.model.cc.command;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zur Abbildung von Mappings zwischen einem Service-Command und einer anderen Referenzklasse. <br> Als
 * Referenzklasse kann z.B. die Service-Chain, Produkt, techn. Leistung etc. verwendet werden. <br> <br> Durch die
 * Verwendung dieses Mapping-Modells erspart man sich die Verwaltung von mehreren Mappings eines Service-Commands auf
 * unterschiedliche Referenzmodelle.
 *
 *
 */
public class ServiceCommandMapping extends AbstractCCIDModel {

    private Long commandId;
    private Long refId;
    private String refClass;
    private Integer orderNo;


    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getRefClass() {
        return refClass;
    }

    public void setRefClass(String refClass) {
        this.refClass = refClass;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

}


