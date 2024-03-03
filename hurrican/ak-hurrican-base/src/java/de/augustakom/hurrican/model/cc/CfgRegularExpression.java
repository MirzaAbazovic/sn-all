/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2009 09:39:33
 */
package de.augustakom.hurrican.model.cc;


/**
 * Konfigurations-Modell, ueber das eine bestimmte Regular-Expression definiert werden kann.
 *
 *
 */
public class CfgRegularExpression extends AbstractCCIDModel {

    /**
     * Ermittlung der Regular-Expr., um die gewuenschten Information zu ermitteln.
     */
    public static enum Info {
        SUBRACK_MOD_NUMBER,
        CPS_DSLAM_PORT,
        PORT_NUM_FROM_HW_EQN,
        CARD_NUM_FROM_HW_EQN,
        PHONE_NUMBER,
        GPON_ID;
    }

    private Long refId = null;
    public static final String REF_ID = "refId";

    private String refName = null;
    public static final String REF_NAME = "refName";

    private String refClass = null;
    public static final String REF_CLASS = "refClass";

    private String requestedInfo = null;
    public static final String REQUESTED_INFO = "requestedInfo";

    private String regExp = null;
    public static final String REG_EXP = "regExp";

    private Integer matchGroup = null;
    public static final String MATCH = "matchGroup";

    private String description = null;
    public static final String DESCRIPTION = "description";


    /**
     * Default-Const.
     */
    public CfgRegularExpression() {
        super();
    }


    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getRefClass() {
        return refClass;
    }

    public void setRefClass(String refClass) {
        this.refClass = refClass;
    }

    public String getRequestedInfo() {
        return requestedInfo;
    }

    public void setRequestedInfo(String requestedInfo) {
        this.requestedInfo = requestedInfo;
    }

    public String getRegExp() {
        return regExp;
    }

    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    public Integer getMatchGroup() {
        return matchGroup;
    }

    public void setMatchGroup(Integer matchGroup) {
        this.matchGroup = matchGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
