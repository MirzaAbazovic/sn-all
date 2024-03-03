/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2009 08:21:46
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;


/**
 * Entity-Builder fuer {@link Rangierungsmatrix }
 *
 *
 */
@SuppressWarnings("unused")
public class RangierungsmatrixBuilder extends EntityBuilder<RangierungsmatrixBuilder, Rangierungsmatrix> {

    private Long id;
    private Long produktId;
    @ReferencedEntityId("produktId")
    private ProduktBuilder produktBuilder;
    private Long uevtId;
    @ReferencedEntityId("uevtId")
    private UEVTBuilder uevtBuilder;
    private Long produkt2PhysikTypId;
    @ReferencedEntityId("produkt2PhysikTypId")
    private Produkt2PhysikTypBuilder p2ptBuilder;
    private Integer priority;
    private Long hvtStandortIdZiel;
    private Boolean projektierung;
    private String bearbeiter = randomString(10);
    private Date gueltigVon = DateTools.createDate(2009, 1, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public RangierungsmatrixBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RangierungsmatrixBuilder withProduktBuilder(ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }

    public RangierungsmatrixBuilder withUevtBuilder(UEVTBuilder uevtBuilder) {
        this.uevtBuilder = uevtBuilder;
        return this;
    }

    public RangierungsmatrixBuilder withPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public RangierungsmatrixBuilder withProdukt2PhysikTypBuilder(Produkt2PhysikTypBuilder p2ptBuilder) {
        this.p2ptBuilder = p2ptBuilder;
        return this;
    }

}


