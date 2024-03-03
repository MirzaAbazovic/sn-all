/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 12:07:02
 */
package de.augustakom.authentication.service;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKApplicationBuilder;
import de.augustakom.authentication.model.AKDepartmentBuilder;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKRoleBuilder;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.model.AKUserSessionBuilder;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;

/**
 * TestNG-Test fuer AKUserService. <br>
 *
 *
 */
public class AKUserServiceTest extends AbstractAuthenticationTest {

    private static final Logger LOGGER = Logger.getLogger(AKUserServiceTest.class);

    /**
     * Test fuer die Methode AKUserService#findUserBySessionId(Long)
     */
    @Test(groups = { "service" })
    public void testFindUserBySessionId() throws AKAuthenticationException {
        AKUserSession userSession = getBuilder(AKUserSessionBuilder.class).build();

        AKUserService service = getUserService();
        AKUser user = service.findUserBySessionId(userSession.getSessionId());

        Assert.assertNotNull(user, "User konnte ueber Session-ID nicht ermittelt werden!");
    }

    /**
     * Test fuer die Methoden AKUserService#save(AKUser) und AKUserService#delete(Long)
     */
    @Test(groups = { "service" })
    public void testSaveAndDelete() throws AKAuthenticationException {
        AKUserService service = getUserService();

        AKDepartmentBuilder departmentBuilder = getBuilder(AKDepartmentBuilder.class);
        departmentBuilder.build();
        AKUser user = getBuilder(AKUserBuilder.class).setPersist(false).withDepartmentBuilder(departmentBuilder)
                .build();

        service.save(user);

        service.delete(user.getId());
    }

    @Test(groups = { "service" })
    public void testFindProjektleiter() throws AKAuthenticationException {
        AKUser testUser = getBuilder(AKUserBuilder.class).withProjektleiter(false).withActive(true).build();
        AKUser projektleiter = getBuilder(AKUserBuilder.class).withProjektleiter(true).withActive(true).build();

        List<AKUser> usersThatAreProjektleiter = getUserService().findAllProjektleiter();

        Assert.assertFalse(usersThatAreProjektleiter.contains(testUser));
        Assert.assertTrue(usersThatAreProjektleiter.contains(projektleiter));
    }

    @Test(groups = { "service" })
    public void testFindManager() throws AKAuthenticationException {
        AKUser testUser = getBuilder(AKUserBuilder.class).withManager(false).build();
        AKUser manager = getBuilder(AKUserBuilder.class).withManager(true).build();

        List<AKUser> usersThatAreManager = getUserService().findManagers();

        Assert.assertFalse(usersThatAreManager.contains(testUser));
        Assert.assertTrue(usersThatAreManager.contains(manager));
    }

    /**
     * Test fuer die Methode AKUserService#getRoles(Long)
     */
    @Test(groups = { "service" })
    public void testGetRoles4User() throws AKAuthenticationException {
        AKApplicationBuilder applicationBuilder = getBuilder(AKApplicationBuilder.class);
        AKUserBuilder userBuilder = getBuilder(AKUserBuilder.class)
                .addRoleBuilder(
                        getBuilder(AKRoleBuilder.class).withRandomName().withApplicationBuilder(applicationBuilder))
                .addRoleBuilder(
                        getBuilder(AKRoleBuilder.class).withRandomName().withApplicationBuilder(applicationBuilder));

        AKUser user = userBuilder.build();

        AKUserService service = getUserService();
        List<AKRole> roles = service.getRoles(user.getId());

        Assert.assertNotNull(roles, "List of roles for user with ID " + user.getId() + " is null!");
        Assert.assertEquals(roles.size(), 2, "Wrong number of roles loaded for user with ID " + user.getId());
        LOGGER.debug("Count of roles: " + roles.size());
    }

    /**
     * Test fuer die Methode AKUserService#getRoles(Long, Long)
     */
    @Test(groups = { "service" })
    public void testGetRoles4UserAndApp() throws AKAuthenticationException {
        AKApplicationBuilder applicationBuilder = getBuilder(AKApplicationBuilder.class);
        AKUserBuilder userBuilder = getBuilder(AKUserBuilder.class)
                .addRoleBuilder(
                        getBuilder(AKRoleBuilder.class).withRandomName().withApplicationBuilder(applicationBuilder))
                .addRoleBuilder(
                        getBuilder(AKRoleBuilder.class).withRandomName().withApplicationBuilder(applicationBuilder));
        AKUser user = userBuilder.build();

        AKUserService service = getUserService();
        List<AKRole> roles = service.getRoles(user.getId(), applicationBuilder.get().getId());

        Assert.assertNotNull(roles, "List of roles for user with ID " + user.getId() + " and App-ID " +
                applicationBuilder.get().getId() + " is null!");
        Assert.assertEquals(roles.size(), 2, "Wrong number of roles loaded for user with ID " + user.getId() +
                " and App-ID " + applicationBuilder.get().getId());
        LOGGER.debug("Count of roles: " + roles.size());
    }

    /**
     * Legt sehr viele Benutzer an.
     */
    @Test(groups = { "service" })
    public void testCreateManyUsers() throws AKAuthenticationException {
        AKUserService service = getUserService();

        AKDepartmentBuilder departmentBuilder = getBuilder(AKDepartmentBuilder.class);
        departmentBuilder.build();
        AKUserBuilder builder = getBuilder(AKUserBuilder.class)
                .withDepartmentBuilder(departmentBuilder)
                .setPersist(false);

        List<AKRole> roles = new ArrayList<AKRole>();
        roles.add(getBuilder(AKRoleBuilder.class).build());

        for (int i = 0; i < 100; i++) {
            AKUser user = builder.withRandomLoginName().build();
            service.save(user);
            service.setRoles(user.getId(), roles);
        }
    }

}
