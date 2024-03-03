/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2012 09:29:29
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;

/**
 * EntityBuilder fuer {@link Leistung2Parameter} Objekte
 */
@SuppressWarnings("unused")
public class Leistung2ParameterBuilder extends EntityBuilder<Leistung2ParameterBuilder, Leistung2Parameter> {

    private Long leistungId = null;
    private Long parameterId = null;
    @ReferencedEntityId("parameterId")
    private LeistungParameterBuilder leistungParameterBuilder;

    public Leistung2ParameterBuilder withLeistungId(Long leistungId) {
        this.leistungId = leistungId;
        return this;
    }

    public Leistung2ParameterBuilder withLeistungParameterBuilder(LeistungParameterBuilder leistungParameterBuilder) {
        this.leistungParameterBuilder = leistungParameterBuilder;
        return this;
    }

}


