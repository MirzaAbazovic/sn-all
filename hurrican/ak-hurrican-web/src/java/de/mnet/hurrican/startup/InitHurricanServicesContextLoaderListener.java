/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.mnet.hurrican.startup;

import static de.mnet.hurrican.startup.HurricanWebConstants.*;

import java.util.*;
import javax.servlet.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.gui.iface.AKCommonGUIConstants;
import de.augustakom.common.service.exceptions.InitializationException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.HurricanSystemPropertyWriter;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;

/**
 * ContextLoader-Listener zur Initialisierung der Hurrican-Services. <br> Ueber diesen Listener erfolgt der Login auf
 * die Authentication-DB sowie die Initialisierung der einzelnen Service-Bereiche von Hurrican (z.B. billing, cc etc.).
 * <br> Ausserdem ist der Listener fuer den Logout (Destroy) verantwortlich.
 */
public class InitHurricanServicesContextLoaderListener implements ServletContextListener {

    /**
     * Key fuer ein ServletContext-Attribut, um den Hurrican Login-Context zu speichern / laden.
     */
    public static final String SERVLET_CONTEXT_HURRICAN_LOGIN_CONTEXT = "hurrican.login.context";

    private static final Logger LOGGER = Logger.getLogger(InitHurricanServicesContextLoaderListener.class);

    private static final ImmutableList<String> BASE_SERVICES = ImmutableList.of(
            "classpath:/de/augustakom/hurrican/service/cc/resources/CCServices.xml",
            "classpath:/de/augustakom/hurrican/service/cc/resources/CCServerServices.xml",
            "classpath:/de/augustakom/hurrican/service/cc/resources/CCServerServices.xml",
            "classpath:/de/augustakom/hurrican/service/cc/resources/CCMailDefServer.xml",
            "classpath:/de/augustakom/hurrican/service/elektra/resources/elektra-server-context.xml",
            "classpath:/de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "classpath:/de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "classpath:/de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml",
            "classpath:/de/augustakom/hurrican/service/cc/resources/ESAATalServices.xml",
            "classpath:/de/augustakom/hurrican/service/wholesale/WholesaleServices.xml",
            "classpath:/de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml",
            "classpath:/de/augustakom/common/service/resources/HttpClient.xml",
            "classpath:/de/mnet/hurrican/tools/mail/resources/mail-context.xml",
            "classpath:/spring/availability-context.xml");

    private static final ImmutableList<String> REPORTING_SERVICES = ImmutableList.of(
            "classpath:/de/augustakom/hurrican/service/reporting/resources/ReportServicesServer.xml",
            "classpath:/de/mnet/hurrican/reporting/resources/JmsServerApplicationContext.xml");

    private static final ImmutableList<String> COMMON_SERVER_CONTEXT = ImmutableList.of(
            "classpath:/de/mnet/common/common-server-context.xml",
            "classpath:/de/mnet/common/common-camel.xml");

    private static final ImmutableList<String> ATLAS_CONNECTION = ImmutableList.of(
            "classpath:/spring/atlas-connection.xml",
            "classpath:/spring/messagedelivery.xml");

    private static final ImmutableList<String> WITA_SERVICES = ImmutableList.of(
            "classpath:/de/mnet/wita/v1/wita-server-context.xml",
            "classpath:/de/mnet/wita/v1/wita-activiti-context.xml");

    private static final ImmutableList<String> WBCI_SERVICES = ImmutableList.of(
            "classpath:/de/mnet/wbci/wbci-server-context.xml");

    private static final ImmutableList<String> SCHEDULER_SERVICES = ImmutableList
            .of("classpath:/de/mnet/hurrican/scheduler/service/resources/SchedulerServices.xml");

    private static final List<String> LOCATION_NOTIFICATION_CONSUMER =
            ImmutableList.of("classpath:/spring/location.xml");

    private static final List<String> CUSTOMER_ORDER_SERVICE =
            ImmutableList.of("classpath:/spring/customerorder.xml");

    private static final List<String> RESOURCE_REPORTING_SERVICE =
            ImmutableList.of("classpath:/spring/resourcereporting.xml");

    private static final List<String> RESOURCE_INVENTORY_SERVICE = ImmutableList.of(
            "classpath:/spring/resourceinventory.xml",
            "classpath:/spring/resourceinventory-client.xml");

    private static final List<String> FFM_SERVICE =
            ImmutableList.of("classpath:/de/augustakom/hurrican/service/cc/ffm/resources/ffm-server-context.xml",
                    "classpath:/spring/resourcecharacteristics.xml");

    private static final List<String> PORTIERING_SERVICE =
            ImmutableList.of("classpath:/spring/ngn-context.xml");

    private static final List<String> DOCUMENT_ARCHIVE_SERVICE =
            ImmutableList.of("classpath:/de/augustakom/hurrican/service/exmodules/archive/resources/ArchiveServerServices.xml");

    private static final List<String> RESOURCE_ORDER_SERVICES =
            ImmutableList.of("classpath:/spring/resourceorder.xml");

    private static final List<String> ROM_NOTIFICATION_SERVICE =
            ImmutableList.of("classpath:/de/augustakom/hurrican/service/wholesale/resources/wholesale-server-context.xml");

    private static final List<String> WHOLESALE_ORDER_SERVICE =
            ImmutableList.of("classpath:/de/mnet/hurrican/wholesale/ws/outbound/resources/wholesale-spri-server-context.xml",
                    "classpath:WholesaleDefaultServices.xml");

    private ServletContext servletContext;
    private HurricanScheduler scheduler;
    private Boolean startScheduler;
    private Boolean startReporting;
    private Boolean startWita;
    private Boolean startLocationNotificationConsumer;
    private Boolean startCustomerOrderService;
    private boolean startResourceReportingService;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        InitializeLog4J.initializeLog4J("log4j-hurrican-web");

        // Application-Properties auswerten und als System-Property setzen
        Properties defaultProps = PropertyUtil.loadPropertyHierarchy(
                Arrays.asList("common", "hurrican-base", "hurrican-web"), "properties", true);
        SystemPropertyTools.instance().setSystemProperties(defaultProps);
        startScheduler = SystemPropertyTools.getSystemPropertyAsBoolean(START_SCHEDULER, Boolean.TRUE);
        startReporting = SystemPropertyTools.getSystemPropertyAsBoolean(START_REPORTING, Boolean.TRUE);
        startWita = SystemPropertyTools.getSystemPropertyAsBoolean(START_WITA, Boolean.TRUE);
        startLocationNotificationConsumer = SystemPropertyTools.getSystemPropertyAsBoolean(
                START_LOCATION_NOTIFICATION_CONSUMER, Boolean.FALSE);
        startCustomerOrderService = SystemPropertyTools.getSystemPropertyAsBoolean(
                START_CUSTOMER_ORDER_SERVICE, Boolean.FALSE);
        startResourceReportingService = SystemPropertyTools.getSystemPropertyAsBoolean(
                START_RESOURCE_REPORTING_SERVICE, Boolean.TRUE);

        System.setProperty("java.security.policy", "ak.java.policy");

        Object existingSL = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);

        // ApplicationContexts nur einmal initialisieren!
        if (existingSL == null) {
            LOGGER.debug("initialize hurrican application contexts");
            try {
                this.servletContext = event.getServletContext();
                initializeAuthenticationServicesAndLogin();

                // Services "hoch fahren"
                CCServiceFinder.instance().getCCService(HVTService.class);
                BillingServiceFinder.instance().getBillingService(BillingAuftragService.class);
            }
            catch (ServiceNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException("ServiceNotFoundException when starting up hurrican-web", e);
            }
        }
        else {
            LOGGER.info("hurrican contexts already initialized and registered");
        }
    }

    protected void initializeAuthenticationServicesAndLogin() throws InitializationException {
        try {
            login();
        }
        catch (InitializationException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InitializationException(e.getMessage(), e);
        }
    }

    private void login() {
        try {
            AuthenticationApplicationContextInitializer authInit = new AuthenticationApplicationContextInitializer();
            ApplicationContext authContext = authInit.initializeApplicationContext(System
                    .getProperty(IServiceMode.SYSTEM_PROPERTY_MODE));

            AKLoginService loginService = authContext.getBean(AKAuthenticationServiceNames.LOGIN_SERVICE,
                    AKLoginService.class);

            AKLoginContext loginContext = loginService.ldapLogin(
                    System.getProperty(IServiceMode.SYSTEM_PROPERTY_AUTOLOGIN_USER),
                    System.getProperty(IServiceMode.SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD), HURRICAN_WEB_APP_NAME, "1.0", true);

            // Session-ID und Login-Context in Servlet-Context speichern
            servletContext.setAttribute(HurricanConstants.HURRICAN_SESSION_ID, loginContext.getUserSession()
                    .getSessionId());
            servletContext.setAttribute(SERVLET_CONTEXT_HURRICAN_LOGIN_CONTEXT, loginContext);

            // Session-ID global zur Verfuegung stellen
            HurricanScheduler.setSessionId(loginContext.getUserSession().getSessionId());
            HurricanWebUserSession.setSessionId(loginContext.getUserSession().getSessionId());

            // Account-Daten auslesen, um weitere Services zu initialisieren
            List<AKAccount> accounts = loginContext.getAccounts();
            Map<Long, AKDb> databases = loginContext.getDatabases();
            HurricanSystemPropertyWriter.readDBInfosIntoSystem(accounts, databases);

            System.setProperty(AKCommonGUIConstants.ADMIN_APPLICATION_ID, loginContext.getApplication().getId()
                    .toString());

            // Admin-Flag setzen
            List<AKRole> roles = loginContext.getRoles();
            if (roles != null) {
                for (AKRole role : roles) {
                    if (role.isAdmin()) {
                        System.setProperty(AKCommonGUIConstants.ADMIN_FLAG, "true");
                        break;
                    }
                }
            }

            ApplicationContext rootContext = initExtendedServices(authContext);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootContext);
            startScheduler(rootContext);
            addRoutesToCamel(rootContext);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt alle Camel RouteBuilder und fuegt sie dem CamelContext hinzu.
     *
     * @param applicationContext
     * @throws Exception
     */
    private void addRoutesToCamel(ApplicationContext applicationContext) throws Exception {
        if (BooleanTools.nullToFalse(startLocationNotificationConsumer) || BooleanTools.nullToFalse(startWita)) {
            CamelContext camelContext = applicationContext.getBean(CamelContext.class);
            if (camelContext != null) {
                Map<String, RouteBuilder> routeBuilders = applicationContext.getBeansOfType(RouteBuilder.class);
                if (routeBuilders != null) {
                    for (RouteBuilder toAdd : routeBuilders.values()) {
                        camelContext.addRoutes(toAdd);
                    }
                }
            }
        }
    }

    private ApplicationContext initExtendedServices(ApplicationContext authContext) throws InitializationException {
        try {
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            context.setServletContext(servletContext);

            context.setConfigLocations(getSpringConfigs());
            context.setParent(authContext);
            context.refresh();
            ServiceLocatorRegistry.instance().setApplicationContext(context);
            return context;
        }
        catch (InitializationException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InitializationException(e.getMessage(), e);
        }
    }

    private String[] getSpringConfigs() {
        Builder<String> configs = ImmutableList.<String>builder().addAll(BASE_SERVICES);
        LOGGER.info("Atlas connection wird hergestellt.");
        configs.addAll(ATLAS_CONNECTION);
        if (BooleanTools.nullToFalse(startWita)) {
            LOGGER.info("WITA-Komponenten werden gestartet.");
            configs.addAll(COMMON_SERVER_CONTEXT);
            configs.addAll(WITA_SERVICES);
            LOGGER.info("WBCI-Komponenten werden gestartet.");
            configs.addAll(WBCI_SERVICES);
        }
        if (BooleanTools.nullToFalse(startReporting)) {
            LOGGER.info("Reporting-Komponenten werden gestartet.");
            configs.addAll(REPORTING_SERVICES);
        }
        if (BooleanTools.nullToFalse(startScheduler)) {
            LOGGER.info("Scheduler-Komponenten werden gestartet.");
            configs.addAll(SCHEDULER_SERVICES);
        }
        if (BooleanTools.nullToFalse(startLocationNotificationConsumer)) {
            LOGGER.info("LocationNotificationConsumer - Endpoint wird gestartet.");
            configs.addAll(LOCATION_NOTIFICATION_CONSUMER);
        }
        if (BooleanTools.nullToFalse(startCustomerOrderService)) {
            LOGGER.info("CustomerOrderService - Endpoint wird gestartet.");
            configs.addAll(CUSTOMER_ORDER_SERVICE);
        }
        if (BooleanTools.nullToFalse(startResourceReportingService)) {
            LOGGER.info("ResourceReportingService - Endpoint wird gestartet.");
            configs.addAll(RESOURCE_REPORTING_SERVICE);
        }

        configs.addAll(FFM_SERVICE);
        configs.addAll(PORTIERING_SERVICE);
        configs.addAll(RESOURCE_INVENTORY_SERVICE);
        configs.addAll(DOCUMENT_ARCHIVE_SERVICE);
        configs.addAll(RESOURCE_ORDER_SERVICES);
        configs.addAll(ROM_NOTIFICATION_SERVICE);
        configs.addAll(WHOLESALE_ORDER_SERVICE);
        return configs.build().toArray(new String[0]);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("contextDestroyed called");
        try {
            shutdownScheduler();
            logout();
            closeAllServiceLocators();

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void logout() throws AKAuthenticationException, ServiceNotFoundException {
        IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        AKLoginService loginService = authSL.getService(AKAuthenticationServiceNames.LOGIN_SERVICE,
                AKLoginService.class, null);
        loginService.logout((Long) servletContext.getAttribute(HurricanConstants.HURRICAN_SESSION_ID));
    }

    private void closeAllServiceLocators() {
        Collection<IServiceLocator> locators = ServiceLocatorRegistry.instance().getServiceLocators();
        if (locators != null) {
            List<String> locatorNames = new ArrayList<>();
            for (IServiceLocator locator : locators) {
                locator.closeServiceLocator();
                locatorNames.add(locator.getServiceLocatorName());
            }
            for (String name : locatorNames) {
                ServiceLocatorRegistry.instance().removeServiceLocator(name);
            }
        }
    }

    private void startScheduler(ApplicationContext rootContext) throws AKSchedulerException {
        if (BooleanTools.nullToFalse(startScheduler)) {
            scheduler = new HurricanScheduler();
            scheduler.start(rootContext);
        }
    }

    private void shutdownScheduler() throws SchedulerException {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

}
