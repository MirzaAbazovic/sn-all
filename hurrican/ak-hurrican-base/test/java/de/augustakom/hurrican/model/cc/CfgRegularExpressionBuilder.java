/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2009 13:45:53
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
@SuppressWarnings("unused")
public class CfgRegularExpressionBuilder extends AbstractCCIDModelBuilder<CfgRegularExpressionBuilder, CfgRegularExpression>
        implements IServiceObject {
    {id = getLongId();}

    private Long refId = (long) 3;
    private String refName = "THREE";
    private String refClass = CfgRegularExpressionBuilder.class.getName();
    private String requestedInfo = CfgRegularExpression.Info.CPS_DSLAM_PORT.name();
    private String regExp = "12(\\d)45";
    private Integer matchGroup = 1;
    private String description = "Test RegExp";


    public CfgRegularExpressionBuilder withRefId(Long refId) {
        this.refId = refId;
        return this;
    }

    public CfgRegularExpressionBuilder withRefName(String refName) {
        this.refName = refName;
        return this;
    }

    public CfgRegularExpressionBuilder withRefClass(String refClass) {
        this.refClass = refClass;
        return this;
    }

    public CfgRegularExpressionBuilder withRequestedInfo(String requestedInfo) {
        this.requestedInfo = requestedInfo;
        return this;
    }

    public CfgRegularExpressionBuilder withRegExp(String regExp) {
        this.regExp = regExp;
        return this;
    }

    public CfgRegularExpressionBuilder withMatchGroup(Integer matchGroup) {
        this.matchGroup = matchGroup;
        return this;
    }
}
