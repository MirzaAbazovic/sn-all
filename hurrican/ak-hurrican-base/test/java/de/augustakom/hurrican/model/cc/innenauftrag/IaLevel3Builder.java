/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;

/**
 * Created by glinkjo on 23.02.2015.
 */
public class IaLevel3Builder extends EntityBuilder<IaLevel3Builder, IaLevel3> {

    private String name = randomString(50);
    private String sapId = randomString(50);
    private List<IaLevel5> level5s = new ArrayList<>();

    public IaLevel3Builder addIaLevel5(IaLevel5 iaLevel5) {
        this.level5s.add(iaLevel5);
        return this;
    }

}
