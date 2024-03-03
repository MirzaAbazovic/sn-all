/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2015
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

/**
 *
 */
public class BAVerlaufZusatzBuilder  extends EntityBuilder<BAVerlaufZusatzBuilder, BAVerlaufZusatz> {
    private Long baVerlaufConfigId;
    private Long abtId;
    private Long hvtGruppeId;
    private Boolean auchSelbstmontage;
    private Long standortTypRefId;

    public BAVerlaufZusatzBuilder withBaVerlaufConfigId(Long baVerlaufConfigId) {
        this.baVerlaufConfigId = baVerlaufConfigId;
        return this;
    }

    public BAVerlaufZusatzBuilder withAbtId(Long abtId) {
        this.abtId = abtId;
        return this;
    }

    public BAVerlaufZusatzBuilder withHvtGruppeId(Long hvtGruppeId) {
        this.hvtGruppeId = hvtGruppeId;
        return this;
    }

    public BAVerlaufZusatzBuilder withAuchSelbstmontage(Boolean auchSelbstmontage) {
        this.auchSelbstmontage = auchSelbstmontage;
        return this;
    }

    public BAVerlaufZusatzBuilder withStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }
}