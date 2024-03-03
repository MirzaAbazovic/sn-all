/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2012 10:19:20
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public class ProduktMappingBuilder extends EntityBuilder<ProduktMappingBuilder, ProduktMapping> {

    private Long mappingGroup = null;
    private Long extProdNo = randomLong(100000);
    private Long prodId = null;
    private String mappingPartType = ".";
    private Long priority = null;

    public ProduktMappingBuilder withMappingGroup(Long mappingGroup) {
        this.mappingGroup = mappingGroup;
        return this;
    }

    public ProduktMappingBuilder withExtProdNo(Long extProdNo) {
        this.extProdNo = extProdNo;
        return this;
    }

    public ProduktMappingBuilder withProdId(Long prodId) {
        this.prodId = prodId;
        return this;
    }

    public ProduktMappingBuilder withPriority(Long priority) {
        this.priority = priority;
        return this;
    }

    public ProduktMappingBuilder withMappingPartType(String mappingPartType) {
        this.mappingPartType = mappingPartType;
        return this;
    }

}


