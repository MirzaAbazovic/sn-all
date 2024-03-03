/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.04.2010 14:51:53
 */
package de.augustakom.hurrican.gui;

import static org.fest.swing.finder.WindowFinder.*;
import static org.testng.Assert.*;

import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemInformation;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.HurricanSystemPropertyWriter;
import de.augustakom.hurrican.gui.system.HurricanMainFrame;

/**
 *
 */
public abstract class AbstractHurricanGuiTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(AbstractHurricanGuiTest.class);

    private static final String[] CONFIGS = new String[] {
            "de/augustakom/hurrican/service/cc/resources/CCTestService.xml",
            "de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml",
            "de/augustakom/hurrican/service/exmodules/sap/resources/SAPServices.xml",
            "de/augustakom/hurrican/gui/system/resources/JmsClientApplicationContext.xml",
            "de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml",
            "de/augustakom/common/service/resources/HttpClient.xml"
    };

    private AKLoginContext loginCtx = null;
    private static Long sessionId = null;

    private Robot robot;

    public AbstractHurricanGuiTest() {
        robot = null;
    }

    protected final void setUpRobot() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }

    /**
     * Cleans up resources used by this test's <code>{@link Robot}</code>.
     */
    protected final void cleanUp() {
        robot.cleanUp();
    }

    protected final Robot robot() {
        return robot;
    }

    @BeforeClass(groups = "gui")
    public final void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeMethod(groups = "gui")
    public final void setUp() throws Exception {
        Properties loadedProps = PropertyUtil.loadPropertyHierarchy(Arrays.asList("common", "authentication-test"),
                "properties", true);
        SystemPropertyTools.instance().setSystemProperties(loadedProps);

        AuthenticationApplicationContextInitializer authInit = new AuthenticationApplicationContextInitializer();
        ConfigurableApplicationContext authContext = authInit.initializeApplicationContext(
                loadedProps.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE));

        doLogin(authContext);
        readAccounts();

        ConfigurableApplicationContext hurricanContext = new ClassPathXmlApplicationContext(
                getConfigs(), authContext);
        ServiceLocatorRegistry.instance().setApplicationContext(hurricanContext);

        setUpRobot();
        onSetUp();
    }

    /**
     * Login am Authentication-Service
     */
    private void doLogin(ConfigurableApplicationContext authContext) throws ServiceNotFoundException,
            AKAuthenticationException, AKPasswordException {
        Object tmpUser = System.getProperty("test.user");
        String user = (tmpUser instanceof String) ? (String) tmpUser : "UnitTest";
        LOGGER.debug("User used for testing: " + tmpUser);

        Object tmpPwd = System.getProperty("test.password");
        String password = (tmpPwd instanceof String) ? (String) tmpPwd : "1#Unit@Test";
        LOGGER.debug("Password for user '" + tmpUser + "': " + password);

        AKLoginService login = authContext.getBean(AKLoginService.class);

        AKLoginContext ctx = createLoginContext("Hurrican", user, password);

        this.loginCtx = login.ldapLogin(ctx.getUserName(), ctx.getPassword(), ctx.getApplicationName(), null);
        HurricanSystemRegistry.instance().setSessionId(loginCtx.getUserSession().getSessionId());
        HurricanSystemRegistry.instance().setValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT, loginCtx);

        sessionId = (loginCtx.getUserSession() != null) ? loginCtx.getUserSession().getSessionId() : null;
    }

    private AKLoginContext createLoginContext(String application, String user, String password) {
        AKLoginContext ctx = new AKLoginContext();
        ctx.setApplicationName(application);
        ctx.setUserName(user);
        ctx.setPassword(password);
        ctx.setIpAddress(SystemInformation.getLocalHostAddress());
        ctx.setHostName(SystemInformation.getLocalHostName());
        return ctx;
    }

    /**
     * Liest die DB-Accounts aus und speichert sie in den System-Properties.
     */
    private void readAccounts() {
        List<AKAccount> accounts = loginCtx.getAccounts();
        Map<Long, AKDb> databases = loginCtx.getDatabases();
        String prefix = loginCtx.getApplicationName().toLowerCase() + ".";
        for (int i = 0; i < accounts.size(); i++) {
            AKAccount account = accounts.get(i);

            AKDb db = databases.get(account.getDbId());
            if (db == null) {
                fail("DB fuer Account-Name " + account.getName() + " nicht gefunden!");
            }

            HurricanSystemPropertyWriter.setSystemPropertiesForDb(account, db, prefix);
        }
    }

    protected FrameFixture startHurricanGui() {
        HurricanMainFrame frame = GuiActionRunner.execute(new GuiQuery<HurricanMainFrame>() {
            @Override
            protected HurricanMainFrame executeInEDT() {
                try {
                    if (SystemUtils.IS_OS_LINUX) {
                        UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
                    }
                    else {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                HurricanMainFrame mainFrame = new HurricanMainFrame();
                HurricanSystemRegistry.instance().setValue(HurricanSystemRegistry.REGKEY_MAINFRAME, mainFrame);
                mainFrame.setVisible(true);
                return mainFrame;
            }
        });
        FrameFixture window = new FrameFixture(robot(), frame);
        window.show(); // shows the frame to test
        LOGGER.debug("Hurrican started");
        return window;
    }

    protected FrameFixture findHurricanMainFrame() {
        FrameFixture mainFrame = findFrame(HurricanMainFrame.class).withTimeout(15, TimeUnit.SECONDS).using(robot());
        return mainFrame;
    }

    /**
     * Logout vom Authentication-Service
     */
    private void doLogout() throws AKAuthenticationException, ServiceNotFoundException {
        IServiceLocator loc = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        AKLoginService loginService = loc.getService(AKAuthenticationServiceNames.LOGIN_SERVICE,
                AKLoginService.class, null);

        LOGGER.info("Logout from Authentication-System");
        loginService.logout(sessionId);
    }

    @AfterMethod(groups = "gui")
    public final void tearDown() {
        try {
            doLogout();
            String[] locators = ServiceLocatorRegistry.instance().getServiceLocatorNames();
            for (int i = 0; i < locators.length; i++) {
                ServiceLocatorRegistry.instance().removeServiceLocator(locators[i]);
            }
            onTearDown();
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        finally {
            cleanUp();
        }
    }

    /**
     * Implementations clean up resources in this method. This method is called before cleaning up resources used by
     * this fixture's <code>{@link Robot}</code>.
     */
    protected void onTearDown() throws Exception {
    }

    protected void onSetUp() throws Exception {
    }

    protected String[] getConfigs() {
        return CONFIGS;
    }

    protected FrameFixture startupHurricanAndGetMainFrame() {
        startHurricanGui();
        FrameFixture mainFrame = findHurricanMainFrame();
        return mainFrame;
    }

}
