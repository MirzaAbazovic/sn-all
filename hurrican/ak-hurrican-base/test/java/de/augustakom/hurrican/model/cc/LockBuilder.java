/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 10:25:41
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
@SuppressWarnings("unused")
public class LockBuilder extends AbstractCCIDModelBuilder<LockBuilder, Lock> implements IServiceObject {

    private Long lockModeRefId = Lock.REF_ID_LOCK_MODE_FULL;
    private Long lockStateRefId = Lock.REF_ID_LOCK_STATE_ACTIVE;
    private Date createdAt = new Date();
    private String createdFrom = "LockBuilder";
    private Long kundeNo = randomLong(Integer.MAX_VALUE / 2);
    private String debId = "A100" + randomInt(100000, 999999);
    private Long auftragNoOrig = null;
    private AuftragBuilder auftragBuilder = null;
    private Long lockReasonRefId = Lock.REF_ID_LOCK_REASON_INSOLVENZ;
    private String lockReasonText = "Nur wegen Test gesperrt";
    private Long cpsTxId = null;
    private Boolean manualProvisioning = null;
    private Lock parentLock = null;


    /**
     * @see de.augustakom.common.model.EntityBuilder#beforeBuild()
     */
    @Override
    protected void beforeBuild() {
        getAuftragBuilder();
    }


    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }


    public LockBuilder withLockModeRefId(Long lockModeRefId) {
        this.lockModeRefId = lockModeRefId;
        return this;
    }

    public LockBuilder withLockModeStateId(Long lockStateRefId) {
        this.lockStateRefId = lockStateRefId;
        return this;
    }

    public LockBuilder withCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LockBuilder withCreatedFrom(String createdFrom) {
        this.createdFrom = createdFrom;
        return this;
    }

    public LockBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public LockBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public LockBuilder withDebId(String debId) {
        this.debId = debId;
        return this;
    }

    public LockBuilder withRandomDebId() {
        this.debId = "A" + randomInt(Integer.MAX_VALUE / 2, Integer.MAX_VALUE);
        return this;
    }


    public LockBuilder withLockReasonText(String lockReasonText) {
        this.lockReasonText = lockReasonText;
        return this;
    }


    public LockBuilder withParentLock(Lock parentLock) {
        this.parentLock = parentLock;
        return this;
    }


    public LockBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

}
