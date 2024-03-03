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
public class CommonBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Common> {

    private String lineId;
    private String contractId;
    private String customerNumber;
    private String additionalContractInfo;
    private String productName;
    private String contractPartner;
    private OrderTechnicalParams.Common.Porting porting;
    private String serviceNew;
    private String serviceCancelled;

    @Override
    public OrderTechnicalParams.Common build() {
        OrderTechnicalParams.Common common = new OrderTechnicalParams.Common();
        common.setLineId(this.lineId);
        common.setContractId(this.contractId);
        common.setCustomerNumber(this.customerNumber);
        common.setAdditionalContractInfo(this.additionalContractInfo);
        common.setProductName(this.productName);
        common.setContractPartner(this.contractPartner);
        common.setPorting(this.porting);
        common.setServiceNew(this.serviceNew);
        common.setServiceCancelled(this.serviceCancelled);
        return common;
    }

    public CommonBuilder withLineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public CommonBuilder withContractId(String contractId) {
        this.contractId = contractId;
        return this;
    }

    public CommonBuilder withCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
        return this;
    }

    public CommonBuilder withAdditionalContractInfo(String additionalContractInfo) {
        this.additionalContractInfo = additionalContractInfo;
        return this;
    }

    public CommonBuilder withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public CommonBuilder withContractPartner(String contractPartner) {
        this.contractPartner = contractPartner;
        return this;
    }

    public CommonBuilder withPorting(OrderTechnicalParams.Common.Porting porting) {
        this.porting = porting;
        return this;
    }

    public CommonBuilder withServiceNew(String serviceNew) {
        this.serviceNew = serviceNew;
        return this;
    }

    public CommonBuilder withServiceCancelled(String serviceCancelled) {
        this.serviceCancelled = serviceCancelled;
        return this;
    }
}