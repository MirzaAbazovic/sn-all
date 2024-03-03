/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2012 15:09:13
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;


/**
 *
 */
@SuppressWarnings("unused")
public class KvzAdresseBuilder extends AbstractCCIDModelBuilder<KvzAdresseBuilder, KvzAdresse> {

    @ReferencedEntityId("hvtStandortId")
    private HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
            .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
    private String kvzNummer = randomString(5);
    private String strasse = randomString(10);
    private String hausNr = randomString(4);
    private String plz = randomString(10);
    private String ort = randomString(10);

    public KvzAdresseBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public KvzAdresseBuilder withKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
        return this;
    }
}


