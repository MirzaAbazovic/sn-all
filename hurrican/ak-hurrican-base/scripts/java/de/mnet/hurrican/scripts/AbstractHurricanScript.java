package de.mnet.hurrican.scripts;


import java.net.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.service.initializer.ServiceInitializer;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.DesEncrypter;
import de.augustakom.common.tools.system.SystemInformation;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.internet.IInternetService;
import de.augustakom.hurrican.service.reporting.IReportService;


/**
 * Abstrakte Basisklasse fuer Hurrican-Scripte. <br> Ist identisch zu der abstrakten UnitTest-Klasse.
 * <p/>
 * Ueber diese Basisklasse koennen Scripte (z.B. fuer Migrationen) in Java geschrieben und dabei die bestehenden
 * Services verwendet werden.
 *
 *
 */
public abstract class AbstractHurricanScript extends TestCase implements ServiceInitializerListener {

    private static final Logger LOGGER = Logger.getLogger(AbstractHurricanScript.class);

    private static final String BASIC_INITIALIZER =
            "de/mnet/hurrican/scripts/resources/ScriptServiceInitializer.xml";

    private static final String EXTENDED_INITIALIZER =
            "de/mnet/hurrican/scripts/resources/ScriptExtendedServiceInitializer.xml";

    private static final String LOG4J =
            "/de/mnet/hurrican/scripts/resources/log4j.xml";

    private AKLoginContext loginCtx = null;
    private Long sessionId = null;
    private boolean setUpDone = false;

    public AbstractHurricanScript() {
        super();
    }

    public AbstractHurricanScript(String arg0) {
        super(arg0);
    }

    /**
     * Wird beim Start des Scripts automatisch aufgerufen - enthaelt die Script-Logik. Muss von den Ableitungen
     * ueberschrieben werden.
     *
     *
     */
    public abstract void executeScript();

    /**
     * Wird von JUnit automatisch ausgefuehrt. <br> Durch diese Methode wird die <code>executeScript</code> Methode
     * aufgerufen, in der die Ableitungen die Script-Logik implementieren koennen.
     *
     *
     */
    public void testDoScript() {
        executeScript();
    }

    /*
     * Initialisiert die benoetigten ServiceLocator und
     * fuehrt den Login am Authentication-System durch.
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        if (!setUpDone) {
            try {
                super.setUp();
                configureLogging();

                // Application-Mode konfigurieren
                if (StringUtils.isBlank(System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE))) {
                    System.setProperty(IServiceMode.SYSTEM_PROPERTY_MODE, "devel");
                }

                ServiceInitializer si = new ServiceInitializer(BASIC_INITIALIZER);
                si.initializeServices();

                doLogin();
                readAccounts();

                ServiceInitializer siExt = new ServiceInitializer(EXTENDED_INITIALIZER);
                siExt.initializeServices();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                setUpDone = true;
            }
        }
    }

    /*
     * Abmeldung vom Authentication-System und schliessen
     * der Service-Locator.
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        try {
            doLogout();
            String[] locators = ServiceLocatorRegistry.instance().getServiceLocatorNames();
            for (int i = 0; i < locators.length; i++) {
                ServiceLocatorRegistry.instance().removeServiceLocator(locators[i]);
            }
        }
        finally {
            super.tearDown();
        }
    }

    /* Login am Authentication-Service */
    private void doLogin() throws ServiceNotFoundException, AKAuthenticationException, AKPasswordException {
        Object tmpUser = System.getProperty("script.user");
        String user = (tmpUser instanceof String) ? (String) tmpUser : "UnitTest";

        String tmpPwd = System.getProperty("script.password");
        String password = (tmpPwd != null) ? tmpPwd : "1#Unit@Test";

        IServiceLocator loc = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        AKLoginService login = loc.getService(AKAuthenticationServiceNames.LOGIN_SERVICE,
                AKLoginService.class, null);

        AKLoginContext ctx = new AKLoginContext();
        ctx.setApplicationName("Hurrican");
        ctx.setUserName(user);
        ctx.setPassword(password);
        ctx.setIpAddress(SystemInformation.getLocalHostAddress());
        ctx.setHostName(SystemInformation.getLocalHostName());

        this.loginCtx = login.login(ctx);
        this.sessionId = (loginCtx.getUserSession() != null) ? loginCtx.getUserSession().getSessionId() : null;
    }

    /* Liest die DB-Accounts aus und speichert sie in den System-Properties. */
    private void readAccounts() {
        List accounts = loginCtx.getAccounts();
        Map databases = loginCtx.getDatabases();
        for (int i = 0; i < accounts.size(); i++) {
            AKAccount account = (AKAccount) accounts.get(i);

            AKDb db = (AKDb) databases.get(account.getDbId());
            if (db == null) {
                fail("DB fuer Account-Name " + account.getName() + " nicht gefunden!");
            }

            String driver = db.getDriver();
            String url = db.getUrl();
            String schema = db.getSchema();
            String user = account.getAccountUser();
            String pwd = account.getAccountPassword();
            String hibDialect = db.getHibernateDialect();
            try {
                pwd = DesEncrypter.getInstance().decrypt(pwd);
            }
            catch (Exception e) {
                fail(e.getMessage());
            }

            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_DRIVER_SUFFIX,
                    driver);
            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_URL_SUFFIX,
                    url);
            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_USER_SUFFIX,
                    user);
            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_PASSWORD_SUFFIX,
                    pwd);
            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_MAX_ACTIVE_SUFFIX,
                    "" + account.getMaxActive());
            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_MAX_IDLE_SUFFIX,
                    "" + account.getMaxIdle());
            System.setProperty(
                    loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.HIBERNATE_DIALECT_SUFFIX,
                    hibDialect);
            if (StringUtils.isNotBlank(schema)) {
                System.setProperty(
                        loginCtx.getApplicationName().toLowerCase() + "." + db.getName() + HurricanConstants.JDBC_SCHEMA_SUFFIX,
                        schema);
            }
        }
    }

    /* Logout vom Authentication-Service */
    private void doLogout() throws AKAuthenticationException, ServiceNotFoundException {
        IServiceLocator loc = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        AKLoginService loginService = loc.getService(AKAuthenticationServiceNames.LOGIN_SERVICE,
                AKLoginService.class, null);

        LOGGER.info("Logout from Authentication-System");
        loginService.logout(sessionId);
    }

    /**
     * Gibt die Session-ID des Authentication-Systems zurueck.
     *
     * @return Session-ID
     */
    protected Long getSessionId() {
        return sessionId;
    }

    /**
     * @see getBillingService(String, Class)
     */
    protected IBillingService getBillingService(Class type) {
        return getBillingService(type.getName(), type);
    }

    /**
     * Sucht nach einem Billing-Service mit dem angegebenen Namen und Typ und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected IBillingService getBillingService(String name, Class type) {
        try {
            IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.HURRICAN_BILLING_SERVICE);
            return (IBillingService) locator.getService(name, type, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * @see getCCService(String, Class)
     */
    protected ICCService getCCService(Class type) {
        return getCCService(type.getName(), type);
    }

    /**
     * Sucht nach einem CC-Service mit dem angegebenen Namen und Typ und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected ICCService getCCService(String name, Class type) {
        try {
            IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.HURRICAN_CC_SERVICE);
            return (ICCService) locator.getService(name, type, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * @see getReportService(String, Class)
     */
    protected IReportService getReportService(Class type) {
        return getReportService(type.getName(), type);
    }

    /**
     * Sucht nach einem Report-Service mit dem angegebenen Namen und Typ und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected IReportService getReportService(String name, Class type) {
        try {
            IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.HURRICAN_REPORT_SERVICE);
            return (IReportService) locator.getService(name, type, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /**
     * Sucht nach einem Internet-Service des angegebenen Typs.
     *
     * @param type Typ des gesuchten Services - gleichzeitig der Name
     * @return
     */
    protected IInternetService getInternetService(Class type) {
        try {
            IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.HURRICAN_INTERNET_SERVICE);
            return (IInternetService) locator.getService(type.getName(), type, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

    /* Initialisiert das Logging. */
    protected void configureLogging() {
        try {
            String resource = LOG4J;
            URL configFileResource = Object.class.getResource(resource);
            DOMConfigurator.configure(configFileResource.getFile());
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug("Could not configure Log4j with the DOMConfigurator. Use BasicConfigurator");
            BasicConfigurator.configure();
        }
    }

    /**
     * @see de.augustakom.common.service.iface.ServiceInitializerListener#initializationBegin()
     */
    @Override
    public void initializationBegin() {
        LOGGER.debug(">>> Begin service initialization...");
    }

    /**
     * @see de.augustakom.common.service.iface.ServiceInitializerListener#initializationDone()
     */
    @Override
    public void initializationDone() {
        LOGGER.debug("<<< Service initialization done...");
    }

    /**
     * @see de.augustakom.common.service.iface.ServiceInitializerListener#initializationFinished(de.augustakom.common.service.iface.IServiceInitializer)
     */
    @Override
    public void initializationFinished(IServiceInitializer initializer) {
        LOGGER.debug("   <<< Finished initialization of: " + initializer.getServiceDescription());
    }

    /**
     * @see de.augustakom.common.service.iface.ServiceInitializerListener#initializationStarts(de.augustakom.common.service.iface.IServiceInitializer)
     */
    @Override
    public void initializationStarts(IServiceInitializer initializer) {
        LOGGER.debug("   >>> Start initialization of: " + initializer.getServiceDescription());
    }

    /**
     * Annahme, dass die Collection nicht <code>null</code> ist und zumindest einen Eintrag enthaelt.
     *
     * @param msg
     * @param coll
     */
    public void assertNotEmpty(String msg, Collection coll) {
        if (coll == null) {
            throw new AssertionFailedError(msg);
        }

        if (coll.isEmpty()) {
            throw new AssertionFailedError(msg);
        }
    }

    /**
     * Annahme, dass die Collection <code>null</code> ist oder keinen Eintrag enthaelt.
     *
     * @param msg
     * @param coll
     */
    public void assertEmpty(String msg, Collection coll) {
        if (coll != null) {
            if (!coll.isEmpty()) {
                throw new AssertionFailedError(msg);
            }
        }
    }
}
