/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 18:17:20
 */

package de.augustakom.hurrican.model.internet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.base.AbstractHurricanModel;


/**
 * Endgerat aus der Monline.
 */
public class IntEndgeraet extends AbstractHurricanModel implements DebugModel {

    private String contract;
    private String serialNo;
    private String vendor;
    private String type;
    private String managementIp;
    private String loginUser;
    private String loginPass;

    public String getTypeAndSerialAndManagementIp() {
        StringBuilder sb = new StringBuilder();
        appendFieldToStringBuilder(sb, type);
        appendFieldToStringBuilder(sb, serialNo);
        appendFieldToStringBuilder(sb, managementIp);
        return sb.toString();
    }

    private void appendFieldToStringBuilder(StringBuilder sb, String field) {
        if (StringUtils.isBlank(field)) {
            return;
        }
        if (sb.length() != 0) {
            sb.append(" | ");
        }
        sb.append(field);
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getManagementIp() {
        return managementIp;
    }

    public void setManagementIp(String managementIp) {
        this.managementIp = managementIp;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginPass() {
        return loginPass;
    }

    public void setLoginPass(String loginPass) {
        this.loginPass = loginPass;
    }

    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + IntEndgeraet.class.getName());
            logger.debug("  serialNo    : " + this.getSerialNo());
            logger.debug("  contract    : " + this.getContract());
        }
    }

}
