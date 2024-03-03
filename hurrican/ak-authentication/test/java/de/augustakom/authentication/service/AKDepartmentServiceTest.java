/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.12.2006 08:17:02
 */
package de.augustakom.authentication.service;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKDepartmentBuilder;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;


/**
 * UnitTest fuer den AKDepartmentService.
 *
 *
 */
public class AKDepartmentServiceTest extends AbstractAuthenticationTest {

    /**
     * Test fuer die Methode AKDepartmentService#findById(..)
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFindById() throws AKAuthenticationException {
        AKDepartment testDepartment = getBuilder(AKDepartmentBuilder.class).build();
        flushAndClear();

        AKDepartment department = getDepartmentService().findDepartmentById(testDepartment.getId());
        assertNotNull(department, "should not be null");
    }
}

