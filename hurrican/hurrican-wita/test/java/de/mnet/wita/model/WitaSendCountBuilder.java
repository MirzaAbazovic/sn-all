/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 12:01:00
 */
package de.mnet.wita.model;

import java.time.*;
import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.SessionFactoryAware;
import de.mnet.wita.message.GeschaeftsfallTyp;

@SessionFactoryAware("cc.sessionFactory")
@SuppressWarnings("unused")
public class WitaSendCountBuilder extends EntityBuilder<WitaSendCountBuilder, WitaSendCount> {

    private String geschaeftsfallTyp = GeschaeftsfallTyp.BEREITSTELLUNG.name();
    private KollokationsTyp kollokationsTyp = KollokationsTyp.HVT;
    private LocalDate sentAt = LocalDate.now();
    private String ituCarrierCode;

    public WitaSendCountBuilder withKollokationsTyp(KollokationsTyp kollokationsTyp) {
        this.kollokationsTyp = kollokationsTyp;
        return this;
    }

    public WitaSendCountBuilder withGeschaeftsfallTyp(String geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        return this;
    }

    public WitaSendCountBuilder withSentAt(LocalDate sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public WitaSendCountBuilder withItuCarrierCode(String ituCarrierCode) {
        this.ituCarrierCode = ituCarrierCode;
        return this;
    }

}
