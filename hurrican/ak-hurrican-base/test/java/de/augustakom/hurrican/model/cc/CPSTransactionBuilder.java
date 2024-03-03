/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2009 16:39:53
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;


/**
 * Entity-Builder fuer CPSTransaction Objekte
 *
 *
 */
@SuppressWarnings("unused")
public class CPSTransactionBuilder extends AbstractCCIDModelBuilder<CPSTransactionBuilder, CPSTransaction>
        implements IServiceObject {

    private Long orderNoOrig = null;
    @DontCreateBuilder
    private AuftragBuilder auftragBuilder = null;
    @DontCreateBuilder
    private HWRackBuilder<?, ?> hwRackBuilder = null;
    private Long verlaufId = null;
    private Long txState = CPSTransaction.TX_STATE_IN_PREPARING;
    private Long txSource = CPSTransaction.TX_SOURCE_HURRICAN_ORDER;
    private Long serviceOrderType = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB;
    private Long serviceOrderPrio = CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT;
    private Long serviceOrderStackId = null;
    private Long serviceOrderStackSeq = null;
    private Long region = null;
    private Date estimatedExecTime = new Date();
    private Date requestAt = null;
    private Date responseAt = null;
    private byte[] requestData = null;
    private byte[] responseData = null;
    private byte[] serviceOrderData = null;
    private String txUser = randomString(8);
    private String userW = randomString(8);


    public CPSTransactionBuilder withServiceOrderData(final byte[] soData) {
        this.serviceOrderData = soData;
        return this;
    }

    public CPSTransactionBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public CPSTransactionBuilder withHwRackBuilder(HWRackBuilder hwRackBuilder) {
        this.hwRackBuilder = hwRackBuilder;
        return this;
    }

    public CPSTransactionBuilder withOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
        return this;
    }

    public CPSTransactionBuilder withEstimatedExecTime(Date estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
        return this;
    }

    public CPSTransactionBuilder withServiceOrderType(Long serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
        return this;
    }

    public CPSTransactionBuilder withTxState(Long txState) {
        this.txState = txState;
        return this;
    }

    public CPSTransactionBuilder withRegion(Long region) {
        this.region = region;
        return this;
    }

    public CPSTransactionBuilder withVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
        return this;
    }

    public CPSTransactionBuilder withTxSource(Long txSourceId) {
        this.txSource = txSourceId;
        return this;
    }
}
