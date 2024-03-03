/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.14
 */
package de.mnet.hurrican.acceptance.behavior;

import java.util.*;
import com.consol.citrus.dsl.CitrusTestBehavior;
import com.consol.citrus.dsl.TestBehavior;

import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.role.AbstractTestRole;
import de.mnet.hurrican.acceptance.role.AtlasEsbTestRole;
import de.mnet.hurrican.acceptance.role.CpsTestRole;
import de.mnet.hurrican.acceptance.role.HurricanTestRole;
import de.mnet.hurrican.acceptance.role.ResourceInventoryTestRole;
import de.mnet.hurrican.acceptance.role.TvFeedTestRole;
import de.mnet.hurrican.acceptance.role.TvProviderTestRole;

/**
 *
 */
public abstract class AbstractTestBehavior extends CitrusTestBehavior {

    /**
     * Test roles
     */
    private List<AbstractTestRole> testRoles;

    /**
     * Builder that uses this behavior - used for correct template package evaluation
     */
    private AbstractHurricanTestBuilder testBuilder;

    protected AtlasEsbTestRole atlas() {
        return testBuilder.atlas();
    }

    protected HurricanTestRole hurrican() {
        return testBuilder.hurrican();
    }

    protected TvProviderTestRole tvProvider() {
        return testBuilder.tvProvider();
    }

    protected TvFeedTestRole tvFeed() {
        return testBuilder.tvFeed();
    }

    protected ResourceInventoryTestRole resourceInventory() {
        return testBuilder.resourceInventory();
    }

    protected CpsTestRole cps() {
        return testBuilder.cps();
    }

    @Override
    public void applyBehavior(TestBehavior behavior) {
        if (behavior instanceof AbstractTestBehavior) {
            AbstractTestBehavior abstractTestBehavior = (AbstractTestBehavior) behavior;
            abstractTestBehavior.setTestBuilder(testBuilder);
            abstractTestBehavior.setTestRoles(testRoles);
        }

        super.applyBehavior(behavior);

        // reset test testBuilder for role to this testBuilder instance
        for (AbstractTestRole testRole : testRoles) {
            testRole.setTestBuilder(this);
        }
    }

    public void setTestRoles(List<AbstractTestRole> testRoles) {
        this.testRoles = testRoles;
        for (AbstractTestRole testRole : testRoles) {
            testRole.setTestBuilder(this);
        }
    }

    public void setTestBuilder(AbstractHurricanTestBuilder testBuilder) {
        this.testBuilder = testBuilder;
    }

    public AbstractHurricanTestBuilder getTestBuilder() {
        return testBuilder;
    }
}
