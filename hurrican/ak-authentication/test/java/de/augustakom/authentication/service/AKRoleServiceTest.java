/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 12:07:02
 */
package de.augustakom.authentication.service;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKRoleBuilder;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;


/**
 * TestNG-Test fuer AKRoleService. <br>
 *
 *
 */
public class AKRoleServiceTest extends AbstractAuthenticationTest {

    /**
     * Test fuer die Methode AKRoleService#save() und AKRoleService#delete()
     */
    @Test(groups = BaseTest.SERVICE)
    public void testSaveAndDelete() throws AKAuthenticationException {
        AKRoleService service = getRoleService();

        AKRole role = getBuilder(AKRoleBuilder.class).setPersist(false).build();

        service.save(role);
        Assert.assertNotNull(role.getId(), "Role is not created!");

        service.delete(role.getId());
    }
}
