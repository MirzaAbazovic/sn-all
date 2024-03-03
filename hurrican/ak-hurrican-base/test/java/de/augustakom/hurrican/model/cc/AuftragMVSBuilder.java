/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2012 16:52:59
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public abstract class AuftragMVSBuilder<BUILDER extends AuftragMVSBuilder<BUILDER, ENTITY>, ENTITY extends AuftragMVS>
        extends EntityBuilder<BUILDER, ENTITY> {

    private String userName = "admin";
    private String password = "123";
    private AuftragBuilder auftragBuilder;
    private Long auftragId;

    public BUILDER withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return getActualClass();
    }

    public BUILDER withUserName(String userName) {
        this.userName = userName;
        return getActualClass();
    }

    public BUILDER withPassword(String password) {
        this.password = password;
        return getActualClass();
    }

    public BUILDER withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return getActualClass();
    }

    private BUILDER getActualClass() {
        @SuppressWarnings("unchecked")
        BUILDER actualClass = (BUILDER) this;
        return actualClass;
    }

}


