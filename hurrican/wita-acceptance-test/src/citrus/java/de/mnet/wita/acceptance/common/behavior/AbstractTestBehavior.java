/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2015
 */
package de.mnet.wita.acceptance.common.behavior;

import com.consol.citrus.dsl.CitrusTestBehavior;
import com.consol.citrus.dsl.TestBehavior;
import com.consol.citrus.dsl.TestBuilder;

import de.mnet.wita.acceptance.common.role.AtlasEsbTestRole;
import de.mnet.wita.acceptance.common.role.HurricanTestRole;
import de.mnet.wita.acceptance.common.role.WorkflowTestRole;

/**
 *
 */
public abstract class AbstractTestBehavior extends CitrusTestBehavior {

    /**
     * Test roles
     */
    private HurricanTestRole hurricanTestRole;
    private AtlasEsbTestRole atlasEsbTestRole;
    private WorkflowTestRole workflowTestRole;

    /**
     * Builder that uses this behavior - used for correct template package evaluation
     */
    private TestBuilder testBuilder;

    /**
     * Gets the hurrican test role which provides specific test actions for Hurrican test actor.
     * @return
     */
    protected HurricanTestRole hurrican() {
        return hurricanTestRole;
    }

    /**
     * Gets the atlas test role which provides specific test actions for Atlas ESB test actor.
     * @return
     */
    protected AtlasEsbTestRole atlas() {
        return atlasEsbTestRole;
    }

    /**
     * Gets the workflow test role which provides specific test actions for Wita Activity workflow actor.
     * @return
     */
    protected WorkflowTestRole workflow() {
        return workflowTestRole;
    }

    public void setHurricanTestRole(HurricanTestRole hurricanTestRole) {
        this.hurricanTestRole = hurricanTestRole;
        this.hurricanTestRole.setTestBuilder(this);
    }

    public void setAtlasEsbTestRole(AtlasEsbTestRole atlasEsbTestRole) {
        this.atlasEsbTestRole = atlasEsbTestRole;
        this.atlasEsbTestRole.setTestBuilder(this);
    }

    public void setWorkflowTestRole(WorkflowTestRole workflowTestRole) {
        this.workflowTestRole = workflowTestRole;
        this.workflowTestRole.setTestBuilder(this);
    }

    public void setTestBuilder(TestBuilder testBuilder) {
        this.testBuilder = testBuilder;
    }

    public TestBuilder getTestBuilder() {
        return testBuilder;
    }

    @Override
    public void applyBehavior(TestBehavior behavior) {
        if (behavior instanceof AbstractTestBehavior) {
            AbstractTestBehavior abstractTestBehavior = (AbstractTestBehavior) behavior;

            abstractTestBehavior.setTestBuilder(testBuilder);
            abstractTestBehavior.setHurricanTestRole(hurricanTestRole);
            abstractTestBehavior.setAtlasEsbTestRole(atlasEsbTestRole);
            abstractTestBehavior.setWorkflowTestRole(workflowTestRole);
        }

        super.applyBehavior(behavior);

        // reset test testBuilder for role to this testBuilder instance
        hurricanTestRole.setTestBuilder(this);
        atlasEsbTestRole.setTestBuilder(this);
        workflowTestRole.setTestBuilder(this);
    }
}
