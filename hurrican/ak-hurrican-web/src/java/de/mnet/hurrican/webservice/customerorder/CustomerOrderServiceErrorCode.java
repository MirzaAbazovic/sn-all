package de.mnet.hurrican.webservice.customerorder;

public enum CustomerOrderServiceErrorCode {

    // login data
    ORDER_NOT_FOUND("HUR-001"),
    ACTIVE_ORDER_NOT_FOUND("HUR-002"),
    NOT_UNIQUE_ORDER_BY_ID("HUR-003"),
    IMS_HW_SWITCH("HUR-004"),
    ORDER_ID_NOT_NUMBER("HUR-005"),
    UNKNOWN("HUR-006"),
    PRODUCT_NOT_PROVIDED("HUR-007");

    // for evn statuses delivered via AtlasErrorHandlingService
    // see de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceFault

    final String externalErrorCode;

    CustomerOrderServiceErrorCode(String externalErrorCode) {
        this.externalErrorCode = externalErrorCode;
    }

    public String getExternalErrorCode() {
        return externalErrorCode;
    }
}
