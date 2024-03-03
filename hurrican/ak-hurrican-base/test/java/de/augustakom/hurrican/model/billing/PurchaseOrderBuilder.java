/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2009 12:30:01
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unused")
public class PurchaseOrderBuilder extends BillingEntityBuilder<PurchaseOrderBuilder, PurchaseOrder> {

    private Long deliveryAddressNo;
    private Date deliveryDatePlanned;
    private Long purchaseOrderNo;
    // Verbindung mit der AUFTRAG Tabelle durch AUFTRAG__NO i.e. auftragNoOrig
    private Long serviceNoOrig;
    private String status;
    private Long supplierNo = PurchaseOrder.SUPPLIER_KOMSA;
    private String userW;
    private Date dateW;

    public PurchaseOrderBuilder withStatusPlanned() {
        this.status = PurchaseOrder.STATUS_PLANNED;
        return this;
    }

    public PurchaseOrderBuilder withServiceNoOrig(Long serviceNoOrig) {
        this.serviceNoOrig = serviceNoOrig;
        return this;
    }

    public PurchaseOrderBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public PurchaseOrderBuilder withSupplier(Long supplierNo) {
        this.supplierNo = supplierNo;
        return this;
    }
}
