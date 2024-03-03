/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2011 12:06:35
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;


/**
 * EntityBuilder fuer Objekte des Typs {@link Produkt2TechLocationType}
 */
@SuppressWarnings("unused")
public class Produkt2TechLocationTypeBuilder extends AbstractCCIDModelBuilder<Produkt2TechLocationTypeBuilder, Produkt2TechLocationType> {

    private ProduktBuilder produktBuilder;
    private Long techLocationTypeRefId;
    private Integer priority = Integer.valueOf(1);
    private String userW = randomString(10);
    private Date dateW = new Date();

    public Produkt2TechLocationTypeBuilder withProduktBuilder(ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }

    public Produkt2TechLocationTypeBuilder withTechLocationTypeRefId(Long techLocationTypeRefId) {
        this.techLocationTypeRefId = techLocationTypeRefId;
        return this;
    }

    public Produkt2TechLocationTypeBuilder withPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

}


