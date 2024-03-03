/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2012 14:02:51
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;

/**
 *
 */
public class EndstelleLtgDatenBuilder extends AbstractCCIDModelBuilder<EndstelleLtgDatenBuilder, EndstelleLtgDaten> {

    @SuppressWarnings("unused")
    @ReferencedEntityId(value = "endstelleId")
    private EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
    @SuppressWarnings("unused")
    @ReferencedEntityId(value = "leitungsartId")
    private LeitungsartBuilder leitungsartBuilder = getBuilder(LeitungsartBuilder.class);
    @SuppressWarnings("unused")
    @ReferencedEntityId(value = "schnittstelleId")
    private SchnittstelleBuilder schnittstelleBuilder = getBuilder(SchnittstelleBuilder.class);
    private Long endstelleId = null;
    private Date gueltigBis = null;

    public EndstelleLtgDatenBuilder withEndstelleBuilder(EndstelleBuilder endstelleBuilder) {
        this.endstelleBuilder = endstelleBuilder;
        return this;
    }

    public EndstelleLtgDatenBuilder withLeitungsartBuilder(LeitungsartBuilder leitungsartBuilder) {
        this.leitungsartBuilder = leitungsartBuilder;
        return this;
    }

    public EndstelleLtgDatenBuilder withSchnittstelleBuilder(SchnittstelleBuilder schnittstelleBuilder) {
        this.schnittstelleBuilder = schnittstelleBuilder;
        return this;
    }

    public EndstelleLtgDatenBuilder withEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
        return this;
    }

    public EndstelleLtgDatenBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

}
