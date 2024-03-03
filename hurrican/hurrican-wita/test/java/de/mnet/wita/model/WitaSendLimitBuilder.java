/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 14:15:35
 */
package de.mnet.wita.model;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public class WitaSendLimitBuilder extends EntityBuilder<WitaSendLimitBuilder, WitaSendLimit> {

    private String geschaeftsfallTyp;
    private Long sendLimit;
    private KollokationsTyp kollokationsTyp = KollokationsTyp.HVT;
    private Boolean allowed = Boolean.TRUE;
    private String ituCarrierCode;
    private String montageHinweise;

    public WitaSendLimitBuilder withGeschaeftsfallTyp(String geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        return this;
    }

    public WitaSendLimitBuilder withSendLimit(Long sendLimit) {
        this.sendLimit = sendLimit;
        return this;
    }

    public WitaSendLimitBuilder withAllowed(Boolean allowed) {
        this.allowed = allowed;
        return this;
    }

    public WitaSendLimitBuilder withKollokationsTyp(KollokationsTyp kollokationsTyp) {
        this.kollokationsTyp = kollokationsTyp;
        return this;
    }

    public WitaSendLimitBuilder withItuCarrierCode(String ituCarrierCode) {
        this.ituCarrierCode = ituCarrierCode;
        return this;
    }

    public WitaSendLimitBuilder withMontageHinweis(String montageHinweis) {
        this.montageHinweise = montageHinweis;
        return this;
    }

}
