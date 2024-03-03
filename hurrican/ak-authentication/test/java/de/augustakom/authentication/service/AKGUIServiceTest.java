/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 08:54:01
 */
package de.augustakom.authentication.service;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.dao.AKRoleDAO;
import de.augustakom.authentication.model.AKApplicationBuilder;
import de.augustakom.authentication.model.AKCompBehaviourBuilder;
import de.augustakom.authentication.model.AKGUIComponentBuilder;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKRoleBuilder;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.temp.AKCompBehaviorSummary;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.authentication.utils.AKCompBehaviorTools;
import de.augustakom.common.BaseTest;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;

/**
 * TestCase fuer AKGUIServiceImpl
 *
 *
 */
public class AKGUIServiceTest extends AbstractAuthenticationTest {

    private Long sessionId = null;
    private String parentClass;
    private String component1Name;
    private String component2Name;

    /**
     * Class under test for void evaluateRights(Long, String, AKManageableComponent)
     */
    @Test(groups = BaseTest.SERVICE)
    public void testEvaluateRights() throws AKAuthenticationException, AKPasswordException {
        login();

        AKJButton comp1 = new AKJButton();
        comp1.getAccessibleContext().setAccessibleName(component1Name);

        AKJButton comp2 = new AKJButton();
        comp2.getAccessibleContext().setAccessibleName(component2Name);

        AKManageableComponent[] comps = new AKManageableComponent[] { comp1, comp2 };

        AKCompBehaviorSummary sum1 = new AKCompBehaviorSummary();
        sum1.setComponentName(comp1.getComponentName());
        sum1.setParentClass(parentClass);

        AKCompBehaviorSummary sum2 = new AKCompBehaviorSummary();
        sum2.setComponentName(comp2.getComponentName());
        sum2.setParentClass(parentClass);

        AKCompBehaviorSummary[] sums = new AKCompBehaviorSummary[] { sum1, sum2 };

        AKGUIService service = getGUIService();
        AKCompBehaviorSummary[] summary = service.evaluateRights(sessionId, sums);
        AKCompBehaviorTools.assignUserRights(comps, parentClass, summary);

        // Ergebnis auswerten...
        Assert.assertTrue(comp1.isVisible(), "GUI-Komp 'UnitTestComponent1' should be visible but is not!");
        Assert.assertTrue(comp1.isEnabled(), "GUI-Komp 'UnitTestComponent1' should be enabled but is not!");

        Assert.assertTrue(comp2.isVisible(), "GUI-Komp 'UnitTestComponent2' should be visible but is not!");
        Assert.assertTrue(!comp2.isEnabled(), "GUI-Komp 'UnitTestComponent2' should NOT be enabled but is!");

        logout();
    }

    protected void login() throws AKAuthenticationException, AKPasswordException {
        final String PW = "1#Unit@Test";

        AKUser user = getUserService().findByLoginName("UnitTest");
        AKRoleBuilder roleBuilder = getBuilder(AKRoleBuilder.class).withApplicationBuilder(
                getBuilder(AKApplicationBuilder.class));
        getApplicationContext().getBean(AKRoleDAO.class).addUserRole(user.getId(), roleBuilder.get().getId());
        AKApplicationBuilder applicationBuilder = roleBuilder.getApplicationBuilder();

        // Create GUI components and user rights for components
        AKGUIComponentBuilder compBuilder1 = getBuilder(AKGUIComponentBuilder.class)
                .withApplicationBuilder(applicationBuilder);
        AKGUIComponentBuilder compBuilder2 = getBuilder(AKGUIComponentBuilder.class)
                .withApplicationBuilder(applicationBuilder);
        getBuilder(AKCompBehaviourBuilder.class)
                .withRoleBuilder(roleBuilder)
                .withComponentBuilder(compBuilder1)
                .build();
        getBuilder(AKCompBehaviourBuilder.class)
                .withRoleBuilder(roleBuilder)
                .withComponentBuilder(compBuilder2)
                .isExecutable(false)
                .build();
        parentClass = compBuilder1.get().getParent();
        component1Name = compBuilder1.get().getName();
        component2Name = compBuilder2.get().getName();

        AKLoginService loginService = getLoginService();
        AKLoginContext loginData = loginService.ldapLogin(user.getLoginName(), PW,
                applicationBuilder.get().getName(), "1.0.0-test");
        sessionId = loginData.getUserSession().getSessionId();
    }

    protected void logout() throws AKAuthenticationException {
        AKLoginService service = getLoginService();
        service.logout(sessionId);
    }

}
