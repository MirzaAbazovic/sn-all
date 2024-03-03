/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2009 08:33:07
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.DontCreateBuilder;


/**
 * EntityBuilder fuer {@link Produkt2PhysikTyp } Objekte
 *
 *
 */
@SuppressWarnings("unused")
public class Produkt2PhysikTypBuilder extends AbstractCCIDModelBuilder<Produkt2PhysikTypBuilder, Produkt2PhysikTyp> {

    private ProduktBuilder produktBuilder;
    private PhysikTypBuilder physikTypBuilder;
    @DontCreateBuilder
    private PhysikTypBuilder physikTypAdditionalBuilder;
    @DontCreateBuilder
    private PhysikTypBuilder parentPhysikTypBuilder;
    private Boolean virtuell = Boolean.FALSE;
    private Long priority;


    public Produkt2PhysikTypBuilder withProduktBuilder(ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }

    public Produkt2PhysikTypBuilder withPhysikTypBuilder(PhysikTypBuilder physikTypBuilder) {
        this.physikTypBuilder = physikTypBuilder;
        return this;
    }

    public Produkt2PhysikTypBuilder withPhysikTypAdditionalBuilder(PhysikTypBuilder physikTypAdditionalBuilder) {
        this.physikTypAdditionalBuilder = physikTypAdditionalBuilder;
        return this;
    }

    public Produkt2PhysikTypBuilder withPriority(Long priority) {
        this.priority = priority;
        return this;
    }

}


