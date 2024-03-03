/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 10:40:24
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;


/**
 *
 */
@SuppressWarnings("unused")
public class Leistungsbuendel2ProduktBuilder extends EntityBuilder<Leistungsbuendel2ProduktBuilder, Leistungsbuendel2Produkt> {

    private Long lbId = null;
    @ReferencedEntityId("lbId")
    private LeistungsbuendelBuilder leistungsbuendelBuilder;
    private Long leistungNoOrig = null;
    private Long protokollLeistungNoOrig = null;
    private Long productOeNo = Long.valueOf(randomInt(10000, 100000000));

    public Leistungsbuendel2ProduktBuilder withLeistungsbuendelBuilder(LeistungsbuendelBuilder leistungsbuendelBuilder) {
        this.leistungsbuendelBuilder = leistungsbuendelBuilder;
        return this;
    }

    public Leistungsbuendel2ProduktBuilder withRandomLeistungNoOrig() {
        this.leistungNoOrig = Long.valueOf(randomInt(900000, 9999999));
        return this;
    }

    public Leistungsbuendel2ProduktBuilder withLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
        return this;
    }

    public Leistungsbuendel2ProduktBuilder withRandomProtokollLeistungNoOrig() {
        this.protokollLeistungNoOrig = Long.valueOf(randomInt(900000, 9999999));
        return this;
    }

}


