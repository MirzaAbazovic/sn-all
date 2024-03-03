/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004 11:45:36
 */
package de.augustakom.hurrican.gui.system;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import javax.annotation.*;
import javax.swing.*;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.qoppa.pdfViewer.PDFViewerBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.AuthenticationSystemReader;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.authentication.service.impl.AuthenticationApplicationContextInitializer;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKCommonGUIConstants;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJAbstractWindow;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJPasswordField;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.service.locator.ServiceLocator;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.system.ApplicationUpdater;
import de.augustakom.common.tools.system.SystemInformation;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.common.tools.system.VersionResult;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.HurricanSystemPropertyWriter;
import de.augustakom.hurrican.gui.HurricanGUIConstants;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.system.actions.ExitAction;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Splash-Screen zur Anzeige des Initialisierungsfortschritts und fuer den Login.
 */
public class LoginScreen extends AKJAbstractWindow implements FocusListener {

    private static final long serialVersionUID = 4466062572776091214L;
    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class);

    private static final ImmutableList<String> BASE_SERVICES = ImmutableList.of(
            "de/augustakom/hurrican/gui/system/resources/JmsClientApplicationContext.xml",
            "de/augustakom/hurrican/service/exmodules/sap/resources/SAPServices.xml",
            "de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml",
            "de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "de/augustakom/hurrican/service/cc/resources/CCServices.xml",
            "de/augustakom/hurrican/service/cc/resources/CCClientServices.xml",
            "de/augustakom/hurrican/service/cc/resources/CCMailDefClient.xml",
            "de/augustakom/hurrican/service/elektra/resources/elektra-client-context.xml",
            "de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml",
            "de/augustakom/common/service/resources/HttpClient.xml",
            "de/augustakom/hurrican/service/cc/resources/ESAATalServices.xml",
            "de/augustakom/hurrican/service/exmodules/archive/resources/ArchiveClientServices.xml");

    private static final ImmutableList<String> COMMON_SERVICES = ImmutableList
            .of("de/mnet/common/common-client-context.xml");
    private static final ImmutableList<String> WITA_SERVICES = ImmutableList.of("de/mnet/wita/v1/wita-client-context.xml");
    private static final ImmutableList<String> WBCI_SERVICES = ImmutableList.of("de/mnet/wbci/wbci-client-context.xml");
    private static final ImmutableList<String> FFM_SERVICES = ImmutableList.of("de/augustakom/hurrican/service/cc/ffm/resources/ffm-client-context.xml");
    private static final ImmutableList<String> WHOLESALE_SPRI_SERVICES = ImmutableList.of("de/mnet/hurrican/wholesale/ws/outbound/resources/wholesale-spri-client-context.xml");

    private static final String RESOURCE = "de/augustakom/hurrican/gui/system/resources/LoginScreen.xml";
    public static final String RESOURCES_VERSION = "de.augustakom.hurrican.gui.system.resources.version";
    private static final String CONFIGURATION_FILE = "configuration.properties";

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_CANCEL = "cancel";

    private static final short UPDATE_TIMEOUT = 3000;

    private static final String MSG_MANDATORY_UPDATE = "Es ist eine neue Hurrican-Version verfügbar.\n"
            + "Hurrican wird jetzt beendet und aktualisiert.";
    private static final String MSG_MANDATORY_UPDATE_BUT_OTHER_USERS = "Es ist eine neue Hurrican-Version verfügbar.\n"
            + "Diese kann jedoch erst aktualisiert werden, wenn folgende Anwender Hurrican beendet haben:\n";
    private static final String MSG_MANDATORY_UPDATE_BUT_SELF = "Es ist eine neue Hurrican-Version verfügbar.\n"
            + "Bitte beenden Sie alle Hurrican-Instanzen und starten erneut um die Aktuallisierung durchzuführen.\n";
    public static final String TXT_CAPSLOCK_ACTIVE = "Die Feststelltaste ist aktiv!";

    private ImagePanel imagePanel = null;
    private AKJPanel initPanel = null;
    private AKJLabel lblInit = null;

    private AKJPanel loginPanel = null;
    private AKJButton btnLogin = null;
    private AKJButton btnCancel = null;
    private AKJComboBox cbDB = null;
    private AKJTextField tfName = null;
    private AKJPasswordField tfPassword = null;

    private boolean autoLogin = false;
    private String autoLoginUser = null;
    private String autoLoginPassword = null;
    private String version = null;

    private List<AuthenticationSystem> authSystems = null;
    private Properties userConfigs = null;

    private static boolean loginDone = false;
    private static boolean mainFrameCreated = false;

    private boolean capsLockActive;

    private final AuthenticationApplicationContextInitializer authenticationInitializer = new AuthenticationApplicationContextInitializer();

    static {
        LocaleContextHolder.setLocale(Locale.GERMANY, true);
    }

    private final AKJLabel lblCapsLockActive = new AKJLabel(" ");

    /**
     * Konstruktor fuer den Login-Screen.
     */
    public LoginScreen(Frame frame) {
        super(RESOURCE, frame);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                new KeyEventDispatcher() {
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        lblCapsLockActive.setText(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)
                                ? TXT_CAPSLOCK_ACTIVE
                                : " ");
                        return false;
                    }
                }
        );
        readSystemConfigurations();
        initScreen();
        createGUI();
    }

    /*
     * Initialisiert den Login-Screen. <br> Es werden best. System-Properties ausgelesen, um einen AutoLogin
     * durchfuehren zu koennen. <br>
     */
    private void initScreen() {
        try {
            authSystems = AuthenticationSystemReader.instance().getAuthenticationSystems();
            if (authSystems == null) {
                throw new NullPointerException("no AuthenticationSystems found");
            }
            ResourceReader rr = new ResourceReader(RESOURCES_VERSION);
            version = rr.getValue("hurrican.version");
        }
        catch (Exception e) {
            LOGGER.error("initScreen() - Die moeglichen Datenbanken konnten nicht ermittelt werden!", e);
            MessageHelper.showMessageDialog(null, "Die moeglichen Datenbanken konnten nicht ermittelt werden!",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            systemExit(-1);
        }
        String defaultAuthenticationMode = System.getProperty(IServiceMode.SYSTEM_PROPERTY_DEFAULT_MODE);
        if (defaultAuthenticationMode != null) {
            List<AuthenticationSystem> allAuthSystems = authSystems;
            authSystems = new ArrayList<>();
            for (AuthenticationSystem currAuthSystem : allAuthSystems) {
                if (defaultAuthenticationMode.equalsIgnoreCase(currAuthSystem.getName())) {
                    authSystems.add(currAuthSystem);
                    break;
                }
            }
            if (authSystems.isEmpty()) {
                LOGGER.warn("initScreen() - desired default authentication mode " + defaultAuthenticationMode
                        + " not found, reverting to full list");
                authSystems = allAuthSystems;
            }
            else {
                LOGGER.info("initScreen() - using default authentication mode " + defaultAuthenticationMode);
            }
        }

        autoLogin = Boolean.valueOf(System.getProperty(IServiceMode.SYSTEM_PROPERTY_AUTOLOGIN));
        autoLoginUser = System.getProperty(IServiceMode.SYSTEM_PROPERTY_AUTOLOGIN_USER);
        autoLoginPassword = System.getProperty(IServiceMode.SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD);

    }

    private void systemExit(int value) {
        System.exit(value);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractWindow#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        ImageIcon background = getSwingFactory().createIcon("mnet.logo");
        setSize(new Dimension(background.getIconWidth(), background.getIconHeight()));

        imagePanel = new ImagePanel(background.getImage());
        imagePanel.setLayout(new GridBagLayout());
        imagePanel.setBorder(BorderFactory.createEtchedBorder());
        imagePanel.add(getFillPanel(), GBCFactory.createGBC(50, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        imagePanel.add(getFillPanel(), GBCFactory.createGBC(50, 0, 2, 2, 1, 1, GridBagConstraints.BOTH));

        this.getContentPane().add(imagePanel, BorderLayout.CENTER);
    }

    /* Liest ein Properties-File ein und speichert die Daten in der HurricanSystemRegistry. */
    private void readSystemConfigurations() {
        Properties configs = SystemPropertyTools.instance().getProperties(CONFIGURATION_FILE);
        if (configs != null) {
            for (Entry<Object, Object> entry : configs.entrySet()) {
                HurricanSystemRegistry.instance().setValue(entry.getKey(), entry.getValue());
            }
        }

        try {
            userConfigs = SystemPropertyTools.instance().getPropertiesFromFile(HurricanGUIConstants.USER_CONFIG_FILE);
            if (userConfigs == null) {
                userConfigs = new Properties();
            }
            HurricanSystemRegistry.instance().setValue(HurricanSystemRegistry.HURRICAN_USER_PROPERTIES, userConfigs);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Veranlasst den Screen dazu, alle 'Basis'-Services zu initialisieren. <br> Zu den Basis-Services gehoeren z.B. der
     * Loggin-Service und der Authentication-Service. <br> <br> Alle weiteren Services sollten erst <b>nach</b> dem
     * Login initialisiert werden.
     */
    protected void init() {
        // Hurrican-Update
        if (StringUtils.equals(System.getProperty(HurricanConstants.AUTO_UPDATE), "true")) {
            hurricanUpdate();
        }

        createGUI4Init();
        createGUI4Login();
        initLoginData();
    }

    /* Erstellt die GUI, die den Initialisierungsfortschritt anzeigt */
    private void createGUI4Init() {
        if ((loginPanel != null) && loginPanel.isVisible()) {
            imagePanel.remove(loginPanel);

            AKJPanel fillPanel = new AKJPanel();
            fillPanel.setBackground(Color.white);
            // @formatter:off
            imagePanel.add(fillPanel, GBCFactory.createGBC(100, 37, 0, 2, 2, 1, GridBagConstraints.BOTH));
            // @formatter:on
        }

        if (initPanel == null) {
            initPanel = new AKJPanel(new GridBagLayout());
            initPanel.setOpaque(false);

            AKJLabel lblInitTitle = getSwingFactory().createLabel("init.title");
            lblInit = getSwingFactory().createLabel("init.text");

            // @formatter:off
            initPanel.add(lblInitTitle  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            initPanel.add(lblInit       , GBCFactory.createGBC(100,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            // @formatter:on
        }

        // @formatter:off
        imagePanel.add(initPanel, GBCFactory.createGBC(100,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    /* Erstellt die GUI fuer den Login */
    private void createGUI4Login() {
        imagePanel.remove(initPanel);
        imagePanel.repaint();

        AKJPanel hLine = new AKJPanel();
        hLine.setOpaque(false);
        hLine.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        hLine.setPreferredSize(new Dimension(200, 1));

        AKJPanel vLine = new AKJPanel();
        vLine.setOpaque(false);
        vLine.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        vLine.setPreferredSize(new Dimension(1, 50));

        loginPanel = new AKJPanel(new GridBagLayout());
        loginPanel.setOpaque(false);

        AKJLabel lblDB = getSwingFactory().createLabel("datenbank");
        AKJLabel lblName = getSwingFactory().createLabel("user.name");
        AKJLabel lblPassword = getSwingFactory().createLabel("user.password");

        TFKeyListener tfKeyListener = new TFKeyListener();
        tfName = getSwingFactory().createTextField("user.name", true, true);
        tfName.addKeyListener(tfKeyListener);
        tfName.addFocusListener(this);
        tfPassword = getSwingFactory().createPasswordField("user.password", true);
        tfPassword.addKeyListener(tfKeyListener);
        tfPassword.addFocusListener(this);
        cbDB = getSwingFactory().createComboBox("datenbank");
        cbDB.setRenderer(new AKCustomListCellRenderer<>(AuthenticationSystem.class, AuthenticationSystem::getName));
        cbDB.setPreferredSize(new Dimension(70, 20));
        cbDB.addKeyListener(tfKeyListener);
        cbDB.addItems(authSystems, true);

        btnLogin = getSwingFactory().createButton(ACTION_LOGIN, getActionListener());
        btnLogin.setOpaque(false);
        btnCancel = getSwingFactory().createButton(ACTION_CANCEL, getActionListener());
        btnCancel.setOpaque(false);

        AKJPanel applicationPanel = new AKJPanel(new GridBagLayout());
        applicationPanel.setOpaque(false);

        AKJLabel lblAppName = getSwingFactory().createLabel("application.name");
        AKJLabel lblVersion = getSwingFactory().createLabel("version");
        lblCapsLockActive.setForeground(Color.RED);

        lblVersion.setText(lblVersion.getText() + (StringUtils.isNotBlank(version) ? version : "?"));

        // @formatter:off
        applicationPanel.add(lblAppName           , GBCFactory.createGBC(0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        applicationPanel.add(lblVersion           , GBCFactory.createGBC(0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        applicationPanel.add(lblCapsLockActive    , GBCFactory.createGBC(0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        loginPanel.add(lblDB           , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(applicationPanel, GBCFactory.createGBC(  0,  0, 5, 0, 1, 3, GridBagConstraints.VERTICAL));
        loginPanel.add(cbDB            , GBCFactory.createGBC(100,  0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(lblName         , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(getFillPanel()  , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        loginPanel.add(tfName          , GBCFactory.createGBC(100,  0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(lblPassword     , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(tfPassword      , GBCFactory.createGBC(100,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(vLine           , GBCFactory.createGBC(  0,100, 4, 0, 1, 5, GridBagConstraints.VERTICAL));
        loginPanel.add(hLine           , GBCFactory.createGBC(100,  0, 0, 3, 6, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(btnLogin        , GBCFactory.createGBC(100,  0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(btnCancel       , GBCFactory.createGBC(  0,  0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        imagePanel.add(loginPanel, GBCFactory.createGBC(100,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        imagePanel.revalidate();
        imagePanel.repaint();
        // @formatter:on

        tfPassword.requestFocus();
    }

    /*
     * Fuellt die Login-Fields mit Default-Werten. Im Modus 'AutoLogin' wird der User-Name und das User-Passwort mit den
     * Werten aus den System-Properties belegt und der Login sofort angestossen. Ansonsten wird nur der User-Name mit
     * dem aktuellen System-User gefuellt. Falls kein auto-Login per Properties konfiguriert ist, wird versucht ein auto-Login
     * ueber den angemeldeten Windows-User durchzufuehren
     */
    private void initLoginData() {
        String authSystem = getAuthenticationSystemId();
        cbDB.selectItem("getBeanName", AuthenticationSystem.class, authSystem);

        capsLockActive = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        if (capsLockActive) {
            lblCapsLockActive.setText(TXT_CAPSLOCK_ACTIVE);
        }
        if (autoLogin) {
            doPropertiesAutoLogin();
        }
        else if (windowsAutoLoginAllowed()) {
            doWindowsAutoLogin();
        }
        else {
            tfName.setText(System.getProperty("user.name"));

        }
    }

    private boolean windowsAutoLoginAllowed() {
        return authSystems.size() == 1 && !autoLogin && !capsLockActive;
    }

    /*
        Auto-Login ueber den Windows-User
     */
    private void doWindowsAutoLogin() {
        try {
            doLogin(new WindowsLoginMethod(version), authSystems.get(0));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /*
        Auto-Login der ueber die Properties gesteuert wird: user/password wird aus properties ermittelt und gegen ldap-Authentifiziert
     */
    private void doPropertiesAutoLogin() {
        tfName.setText(autoLoginUser);
        tfPassword.setText(autoLoginPassword);
        doLoginFromGui();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractWindow#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (ACTION_LOGIN.equals(command)) {
            doLoginFromGui();
        }
        else if (ACTION_CANCEL.equals(command)) {
            cancel();
        }
    }

    /* Fuehrt den Login durch. */
    private void doLoginFromGui() {
        // Passwort nicht ueber getText() auslesen!
        String password = new String(tfPassword.getPassword());

        final AuthenticationSystem authSys;
        try {
            // Applikationsmodus auslesen
            authSys = (cbDB.getSelectedItem() instanceof AuthenticationSystem) ? (AuthenticationSystem) cbDB
                    .getSelectedItem()
                    : null;
            String authenticationSystem = (authSys != null) ? authSys.getBeanName() : null;
            if (!isAuthenticationSystemValid(authenticationSystem)) {
                throw new HurricanGUIException(
                        "Bitte wählen Sie die Datenbank aus, die Sie verwenden möchten.");
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            GuiTools.disableComponents(new Component[] { btnLogin, btnCancel });
        }
        catch (Exception e) {
            MessageHelper.showInfoDialog(this, e.getMessage(), null, true);
            return;
        }

        AKLoginService loginService = null;
        try {
            // Login durchfuehren
            if (!loginDone) {
                doLogin(new LdapLoginMethode(tfName.getText(), password, version), authSys);
            }
        }
        catch (AKPasswordException e) {
            if (!SystemUtils.IS_OS_WINDOWS) {
                this.toBack(); // Workaround fuer Linux...
            }
            MessageHelper.showInfoDialog(this, e.getMessage(), null, true);

            this.requestFocus();
            tfPassword.setText("");
        }
        catch (AKAuthenticationException e) {
            MessageHelper.showErrorDialog(this, e);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e, true);
            ExitAction exit = new ExitAction(loginService);
            exit.actionPerformed(null);
        }
        finally {
            GuiTools.enableComponents(new Component[] { btnLogin, btnCancel });
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private static interface LoginMethod {
        public AKLoginContext execute(final AKLoginService loginService) throws Exception;
    }

    private static final class LdapLoginMethode implements LoginMethod {
        private LdapLoginMethode(String username, String password, String version) {
            this.username = username;
            this.password = password;
            this.version = version;
        }

        final String username;
        final String password;
        final String version;

        @Override
        public AKLoginContext execute(final AKLoginService loginService) throws Exception {
            return loginService.ldapLogin(username, password, HurricanGUIConstants.APPLICATION_NAME, version);
        }
    }

    private static final class WindowsLoginMethod implements LoginMethod {
        private WindowsLoginMethod(String version) {
            this.version = version;
        }

        final String version;

        @Override
        public AKLoginContext execute(final AKLoginService loginService) throws AKAuthenticationException, AKPasswordException {
            return loginService.windowsLogin(HurricanGUIConstants.APPLICATION_NAME, version);
        }
    }

    private void doLogin(final LoginMethod loginMethod, final AuthenticationSystem authenticationSystem) throws Exception {
        final ConfigurableApplicationContext authContext = authenticationInitializer.initializeApplicationContext(authenticationSystem);
        final AKLoginContext loginContext = loginMethod.execute(authContext.getBean(AKLoginService.class));

        HurricanSystemRegistry.instance().setSessionId(loginContext.getUserSession().getSessionId());
        HurricanSystemRegistry.instance().setValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT, loginContext);

        // Account-Daten auslesen, um weitere Services zu initialisieren
        final List<AKAccount> accounts = loginContext.getAccounts();
        final Map<Long, AKDb> databases = loginContext.getDatabases();
        HurricanSystemPropertyWriter.readDBInfosIntoSystem(accounts, databases);

        System.setProperty(AKCommonGUIConstants.ADMIN_APPLICATION_ID, loginContext.getApplication().getId()
                .toString());
        System.setProperty(IServiceMode.SYSTEM_PROPERTY_MODE, authenticationSystem.getName());
        saveLastAppMode(authenticationSystem.getName());
        HurricanSystemRegistry.instance().setValue(HurricanSystemRegistry.AUTHENTICATION_SYSTEM, authenticationSystem);

        // Admin-Flag fuer GUI-Administration setzen
        final List<AKRole> roles = loginContext.getRoles();
        for (AKRole role : roles) {
            if (role.isAdmin()) {
                System.setProperty(AKCommonGUIConstants.ADMIN_FLAG, "true");
                break;
            }
        }

        loginDone = true;
        initExtendedServices(authContext);
    }

    /**
     * Initialisiert weitere Services
     */
    private void initExtendedServices(final ApplicationContext authContext) {
        createGUI4Init();
        lblInit.setText("Hurrican Services");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                Builder<String> builder = ImmutableList.<String>builder().addAll(BASE_SERVICES);

                builder.addAll(COMMON_SERVICES);
                builder.addAll(WITA_SERVICES);
                builder.addAll(WBCI_SERVICES);
                builder.addAll(FFM_SERVICES);
                builder.addAll(WHOLESALE_SPRI_SERVICES);
                ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(builder.build()
                        .toArray(new String[0]), authContext);
                PDFViewerBean.setKey("81792639a8ad0ea6");
                ServiceLocatorRegistry.instance().setApplicationContext(context);
                ServiceLocator.instance().setApplicationContext(context);
                initBillingAndCCService();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    showMainFrame();
                }
                catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        };
        worker.execute();
    }

    /* Zeigt das Hauptfenster der Applikation an. */
    private void showMainFrame() {
        if (!mainFrameCreated) {
            mainFrameCreated = true;
            try {
                if (SystemUtils.IS_OS_LINUX) {
                    UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
                }
                else {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            HurricanMainFrame mainFrame = new HurricanMainFrame();
            HurricanSystemRegistry.instance().setValue(HurricanSystemRegistry.REGKEY_MAINFRAME, mainFrame);
            mainFrame.setVisible(true);

            this.dispose();
        }
    }

    /* Bricht den Login ab. */
    private void cancel() {
        Collection<IServiceLocator> locators = ServiceLocatorRegistry.instance().getServiceLocators();
        for (IServiceLocator locator : locators) {
            locator.closeServiceLocator();
        }

        systemExit(0);
    }

    /**
     * Initialisiert im Hintergrund den Billing- und den CC-Service, damit er nicht erst dann initialisiert werden muss,
     * wenn der Anwender den Service benoetigt.
     */
    private void initBillingAndCCService() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    BillingServiceFinder.instance().getBillingService(KundenService.class);
                    CCServiceFinder.instance().getCCService(CCAuftragService.class);
                }
                catch (ServiceNotFoundException e) {
                    LOGGER.warn(e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }

    /*
     * Gibt ein Kuerzel fuer den aktuellen Applikations-Modus zurueck. Ist das System-Property 'SYSTEM_PROPERTY_MODE'
     * auf einen gueltigen Wert gesetzt, wird dieser zurueck gegeben. Ansonsten wird nach den zuletzt gespeicherten
     * System-Daten gesucht und dieser Wert zurueck gegeben. Ist auch dieser nicht gesetzt, wird <code>null</code>
     * zurueck gegeben.
     */
    private String getAuthenticationSystemId() {
        String beanName = null;
        String sysModus = System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE);
        if (isAuthenticationSystemValid(sysModus)) {
            beanName = sysModus.toLowerCase();
        }
        else {
            String value = userConfigs.getProperty(HurricanGUIConstants.USER_CONFIG_APP_MODUS);
            if (isAuthenticationSystemValid(value)) {
                beanName = value.toLowerCase();
            }
        }
        return beanName;
    }

    /* Ueberprueft, ob der Applikations-Modus gueltig ist. */
    private boolean isAuthenticationSystemValid(String systemName) {
        if (authSystems != null) {
            for (AuthenticationSystem as : authSystems) {
                if (StringUtils.isNotBlank(systemName) && StringUtils.equalsIgnoreCase(systemName, as.getBeanName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Speichert den zuletzt verwendeten Applikations-Modus in einer Konfig-Datei.
     */
    private void saveLastAppMode(String mode) {
        try {
            userConfigs.put(HurricanGUIConstants.USER_CONFIG_APP_MODUS, mode);
            SystemPropertyTools.instance().storeProperties(userConfigs, HurricanGUIConstants.USER_CONFIG_FILE);
        }
        catch (Exception e) {
            LOGGER.info("saveLastAppMode() - exception saving mode", e);
        }
    }

    /**
     * Gibt ein Panel zurueck, das zum auffuellen von Leerraeumen verwendet werden kann. <br> Das Panel ist auf opaque =
     * false gesetzt.
     */
    private JPanel getFillPanel() {
        JPanel p = new JPanel();
        p.setBackground(Color.white);
        p.setOpaque(false);
        return p;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    /*
     * Funktion prueft Hurrican-Version und fuehrt bei Bedarf Update durch
     */
    private void hurricanUpdate() {
        try {
            // Aktuell funktioniert das Update-Funktion nur fuer Windows-Betriebssystem
            if (!SystemUtils.IS_OS_WINDOWS) {
                return;
            }
            VersionResult result = ApplicationUpdater.getVersionResult(UPDATE_TIMEOUT);
            if ((result == null) || result.isAct()) {
                return;
            }
            ImmutableList<String> users = getLocalUsers();
            if (!result.isMandatory()) {
                if (!users.isEmpty()) {
                    // Nicht verbindliches Updates ueberspringen, da noch ein Hurrican laeuft
                    return;
                }
                int input = MessageHelper.showConfirmDialog(this, "Neues Update vorhanden. Aktualisieren?",
                        "Aktualisieren", JOptionPane.YES_NO_OPTION);
                if (JOptionPane.YES_OPTION == input) {
                    ApplicationUpdater.update(result);
                }
            }
            else {
                switch (users.size()) {
                    case 0:
                        MessageHelper.showMessageDialog(this, MSG_MANDATORY_UPDATE);
                        ApplicationUpdater.update(result);
                        break;
                    case 1:
                        if (users.get(0).equals(SystemUtils.USER_NAME)) {
                            MessageHelper.showMessageDialog(this, MSG_MANDATORY_UPDATE_BUT_SELF);
                            return;
                        }
                        // fall through
                    default:
                        MessageHelper.showMessageDialog(this, MSG_MANDATORY_UPDATE_BUT_OTHER_USERS
                                + Joiner.on('\n').join(users));
                }
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Liefert alle Benutzer des aktuellen Hosts */
    private ImmutableList<String> getLocalUsers() {
        ConfigurableApplicationContext authContext = null;
        try {
            /*
             Initialisiere Authentication-Services fuer Produktiv-System
             authSystems enthaelt an get(0) immer "produktiv" oder das per "authenticationsystem.default" in
             ak-hurrican-gui.properties definierte Authentication-Schema (ohne "devel_"-Prefix)
            */
            AuthenticationSystem authSys = authSystems.get(0);
            authContext = authenticationInitializer.initializeApplicationContext(authSys.getBeanName());

            // Ermittle, ob bereits eine UserSession auf diesem Host aktiv ist.
            AKUserService us = authContext.getBean(AKUserService.class);
            List<AKUserSession> sessions = us.findAktUserSessionByHostName(SystemInformation.getLocalHostName());

            return ImmutableList.copyOf(Iterables.transform(sessions, new Function<AKUserSession, String>() {
                @Override
                @Nullable
                public String apply(@Nullable AKUserSession input) {
                    return input == null ? null : input.getHostUser();
                }
            }));
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
            return ImmutableList.of();
        }
        finally {
            // Setze Datenbank-Auswahl zurueck
            System.clearProperty(IServiceMode.SYSTEM_PROPERTY_MODE);
            if (authContext != null) {
                authContext.close();
            }
        }
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
        this.toFront();
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
        // intentionally left blank
    }

    /**
     * Ableitung von AKJPanel, um ein Panel mit Hintergrundbild darzustellen.
     *
     *
     */
    static class ImagePanel extends AKJPanel {
        private static final long serialVersionUID = 8262736443086983396L;
        private Image image = null;

        public ImagePanel(Image image) {
            super();
            this.image = image;
        }

        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        public void paintComponent(Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, null);
            }
            else {
                super.paintComponent(g);
            }
        }
    }

    /**
     * KeyListener fuer das Text- bzw. PasswordField. <br> Durch die Betaetigung von 'ENTER' wird der Login
     * durchgefuehrt.
     *
     *
     */
    private class TFKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                execute(ACTION_LOGIN);
            }
        }
    }

}
