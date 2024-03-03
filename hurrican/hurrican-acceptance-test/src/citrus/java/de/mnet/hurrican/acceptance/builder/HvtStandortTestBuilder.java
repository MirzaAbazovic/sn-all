/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2014
 */
package de.mnet.hurrican.acceptance.builder;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;

/**
 *
 */
@Component
@Scope("prototype")
public class HvtStandortTestBuilder {

    @Autowired
    private CCAuftragDAO ccAuftragDAO;

    private String ortsteil = RandomStringUtils.randomAlphanumeric(20);
    private Long standortTypRefId = HVTStandort.HVT_STANDORT_TYP_FTTB;

    public HVTStandort build() {
        HVTGruppe hvtGruppe = new HVTGruppeBuilder()
                .withOrtsteil(ortsteil)
                .setPersist(false)
                .build();
        ccAuftragDAO.store(hvtGruppe);

        HVTStandort hvtStandort = new HVTStandortBuilder()
                .withStandortTypRefId(standortTypRefId)
                .setPersist(false)
                .build();
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        return ccAuftragDAO.store(hvtStandort);
    }

    public HvtStandortTestBuilder withOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
        return this;
    }

    public HvtStandortTestBuilder withStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }

}
