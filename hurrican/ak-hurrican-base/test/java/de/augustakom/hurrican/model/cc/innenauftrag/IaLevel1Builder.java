/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

/**
 * Created by glinkjo on 23.02.2015.
 */
public class IaLevel1Builder extends AbstractCCIDModelBuilder<IaLevel1Builder, IaLevel1> {

    private String name = randomString(50);
    private String sapId = randomString(50);
    private boolean lockMode;
    private String bereichName = randomString(20);

    private List<IaLevel3> level3s = new ArrayList<>();

    public IaLevel1Builder addIaLevel3(IaLevel3 iaLevel3) {
        this.level3s.add(iaLevel3);
        return this;
    }

    public IaLevel1Builder withLockMode(final boolean lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    public IaLevel1Builder withBereichName(final String bereichName)   {
        this.bereichName = bereichName;
        return this;
    }
}
