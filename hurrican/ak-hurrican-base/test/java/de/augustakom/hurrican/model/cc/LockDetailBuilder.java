/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2009 12:18:36
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
@SuppressWarnings("unused")
public class LockDetailBuilder extends AbstractCCIDModelBuilder<LockDetailBuilder, LockDetail> implements
        IServiceObject {

    private LockBuilder lockBuilder = null;
    private AbteilungBuilder abteilungBuilder = null;
    private Date executedAt = null;
    private String executedFrom = null;
    private Long cpsTxId = null;

    public LockDetailBuilder setLockBuilder(LockBuilder lockBuilder) {
        this.lockBuilder = lockBuilder;
        return this;
    }

    public LockDetailBuilder setAbteilungBuilder(AbteilungBuilder abteilungBuilder) {
        this.abteilungBuilder = abteilungBuilder;
        return this;
    }

    public LockDetailBuilder withCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
        return this;
    }

}
