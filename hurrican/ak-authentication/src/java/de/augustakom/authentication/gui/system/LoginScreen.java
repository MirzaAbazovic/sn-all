/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 09:55:36
 */
package de.augustakom.authentication.gui.system;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.AuthenticationSystemReader;
import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.authentication.service.impl.AKAdminApplicationContextInitializer;
import de.augustakom.common.AKDefaultConstants;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractWindow;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJPasswordField;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.service.locator.ServiceLocator;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.ResourceReader;

/**
 * Splash-Screen zur Anzeige des Initialisierungsfortschritts und fuer den Login.
 */
public class LoginScreen extends AKJAbstractWindow {

    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class);

    private static final String[] CONFIGS = new String[] {
            "de/augustakom/authentication/gui/tree/resources/AdminTreeService.xml"
    };

    private static final String RESOURCE = "de/augustakom/authentication/gui/system/resources/LoginScreen.xml";
    public static final String RESOURCES_VERSION = "de.augustakom.authentication.gui.system.resources.version";

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_CANCEL = "cancel";

    private ImagePanel imagePanel = null;
    private AKJPanel initPanel = null;

    private AKJPanel loginPanel = null;
    private AKJComboBox cbDB = null;
    private AKJTextField tfName = null;
    private AKJPasswordField tfPassword = null;

    private boolean autoLogin = false;
    private String autoLoginUser = null;
    private String autoLoginPassword = null;

    private List<AuthenticationSystem> authSystems = Collections.emptyList();

    /**
     * Konstruktor fuer den Login-Screen.
     */
    public LoginScreen(Frame frame) {
        super(RESOURCE, frame);
        initScreen();
        createGUI();
    }

    /*
     * Initialisiert den Login-Screen. <br> Es werden best. System-Properties ausgelesen, um einen AutoLogin
     * durchfuehren zu koennen. <br>
     */
    private void initScreen() {
        authSystems = AuthenticationSystemReader.instance().getAuthenticationSystems();
        Boolean autoLoginTmp = StringUtils.equalsIgnoreCase(System.getProperty(SystemConstants.SYSTEM_PROPERTY_AUTOLOGIN),
                BooleanTools.DEFAULT_TRUE_STRING);
        autoLogin = BooleanTools.nullToFalse(autoLoginTmp);
        autoLoginUser = System.getProperty(SystemConstants.SYSTEM_PROPERTY_AUTOLOGIN_USER);
        autoLoginPassword = System.getProperty(SystemConstants.SYSTEM_PROPERTY_AUTOLOGIN_PASSWORD);
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        ImageIcon background = getSwingFactory().createIcon("mnet.logo");
        setSize(new Dimension(background.getIconWidth(), background.getIconHeight()));

        imagePanel = new ImagePanel(background.getImage());
        imagePanel.setLayout(new GridBagLayout());

        this.getContentPane().add(imagePanel, BorderLayout.CENTER);

        // @formatter:off
        imagePanel.setBorder(BorderFactory.createEtchedBorder());
        imagePanel.add(getFillPanel()   , GBCFactory.createGBC(  50, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        imagePanel.add(getFillPanel()   , GBCFactory.createGBC(  50,   0, 2, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        createGUI4Login();
    }

    /* Erstellt die GUI, die den Initialisierungsfortschritt anzeigt */
    private void createGUI4Init() {
        if ((loginPanel != null) && loginPanel.isVisible()) {
            imagePanel.remove(loginPanel);
        }

        if (initPanel == null) {
            initPanel = new AKJPanel(new GridBagLayout());
            initPanel.setOpaque(false);

            AKJLabel lblInitTitle = getSwingFactory().createLabel("init.title");
            AKJLabel lblInit = getSwingFactory().createLabel("init.text");

            initPanel.add(lblInitTitle, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            initPanel.add(lblInit, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        imagePanel.add(initPanel, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    /* Erstellt die GUI fuer den Login */
    private void createGUI4Login() {
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
        tfName = getSwingFactory().createTextField("user.name");
        tfName.addKeyListener(tfKeyListener);
        tfPassword = getSwingFactory().createPasswordField("user.password");
        tfPassword.addKeyListener(tfKeyListener);
        cbDB = getSwingFactory().createComboBox("datenbank");
        cbDB.setRenderer(new AKCustomListCellRenderer<>(AuthenticationSystem.class, AuthenticationSystem::getName));
        cbDB.setPreferredSize(new Dimension(70, 20));
        cbDB.addKeyListener(tfKeyListener);
        cbDB.addItems(authSystems, true, AuthenticationSystem.class);

        AKJButton btnLogin = getSwingFactory().createButton(ACTION_LOGIN, getActionListener());
        btnLogin.setOpaque(false);
        AKJButton btnCancel = getSwingFactory().createButton(ACTION_CANCEL, getActionListener());
        btnCancel.setOpaque(false);

        AKJPanel applicationPanel = new AKJPanel(new GridBagLayout());
        applicationPanel.setOpaque(false);

        AKJLabel lblAppName = getSwingFactory().createLabel("application.name");
        AKJLabel lblVersion = getSwingFactory().createLabel("version");
        ResourceReader rr = new ResourceReader(RESOURCES_VERSION);
        String version = rr.getValue("hurrican.version");
        lblVersion.setText(lblVersion.getText() + (StringUtils.isNotBlank(version) ? version : "?"));

        // @formatter:off
        applicationPanel.add(lblAppName    , GBCFactory.createGBC(0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        applicationPanel.add(lblVersion    , GBCFactory.createGBC(0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        applicationPanel.add(getFillPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        loginPanel.add(lblDB           , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(applicationPanel, GBCFactory.createGBC(  0,  0, 5, 0, 1, 3, GridBagConstraints.VERTICAL));
        loginPanel.add(getFillPanel()  , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        loginPanel.add(cbDB            , GBCFactory.createGBC(100,  0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(lblName         , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(tfName          , GBCFactory.createGBC(100,  0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(lblPassword     , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(tfPassword      , GBCFactory.createGBC(100,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(vLine           , GBCFactory.createGBC(  0,100, 4, 0, 1, 5, GridBagConstraints.VERTICAL));
        loginPanel.add(hLine           , GBCFactory.createGBC(100,  0, 0, 3, 6, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(btnLogin        , GBCFactory.createGBC(100,  0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        loginPanel.add(btnCancel       , GBCFactory.createGBC(  0,  0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        imagePanel.add(loginPanel, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        imagePanel.revalidate();
        imagePanel.repaint();
        // @formatter:on

        tfPassword.requestFocus();
        initLoginData();
    }

    /*
     * Fuellt die Login-Fields mit Default-Werten. Im Modus 'AutoLogin' wird der User-Name und das User-Passwort mit den
     * Werten aus den System-Properties belegt und der Login sofort angestossen. Ansonsten wird nur der User-Name mit
     * dem aktuellen System-User gefuellt.
     */
    private void initLoginData() {
        if (autoLogin) {
            tfName.setText(autoLoginUser);
            tfPassword.setText(autoLoginPassword);
            doLogin();
        }
        else {
            tfName.setText(System.getProperty("user.name"));
        }
    }

    @Override
    protected void execute(String command) {
        if (ACTION_LOGIN.equals(command)) {
            doLogin();
        }
        else if (ACTION_CANCEL.equals(command)) {
            cancel();
        }
    }

    /* Fuehrt den Login durch. */
    private void doLogin() {
        String password = new String(tfPassword.getPassword());

        AuthenticationSystem authSys = (AuthenticationSystem) cbDB.getSelectedItem();
        authSys.setUser(tfName.getText());
        authSys.setPassword(password);

        System.setProperty(IServiceMode.SYSTEM_PROPERTY_MODE, authSys.getBeanName());

        setSystemProperties4AuthenticationDb(tfName.getText(), password);

        GUISystemRegistry.instance().setValue(GUISystemRegistry.AUTHENTICATION_SYSTEM, authSys);

        createGUI4Init();

        try {
            AKAdminApplicationContextInitializer authInit = new AKAdminApplicationContextInitializer();
            ApplicationContext authContext = authInit.initializeApplicationContext(authSys);
            ClassPathXmlApplicationContext context =
                    new ClassPathXmlApplicationContext(CONFIGS, authContext);
            // register this application context under various names used for lookups
            ServiceLocator.instance().setApplicationContext(context);

            // MainFrame anzeigen
            showMainFrame();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(null,
                    new Exception("Fehler bei der Initialisierung des Systems! Programm wird beendet.", e));
            cancel();
        }
    }

    private void setSystemProperties4AuthenticationDb(String username, String password) {
        System.setProperty(SystemConstants.DB_NAME + AKDefaultConstants.JDBC_USER_SUFFIX, username);
        System.setProperty(SystemConstants.DB_NAME + AKDefaultConstants.JDBC_PASSWORD_SUFFIX, password);
    }

    /* Zeigt das Hauptfenster der Applikation an. */
    private void showMainFrame() {
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

        MDIMainFrame mainFrame = new MDIMainFrame();
        GUISystemRegistry.instance().setValue(GUISystemRegistry.REGKEY_MAINFRAME, mainFrame);
        mainFrame.setVisible(true);

        this.dispose();
    }

    /**
     * Bricht den Login ab.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DM_EXIT", justification = "ExitAction soll VM tatsaechlich beenden")
    private void cancel() {
        systemExit();
    }

    private void systemExit() {
        System.exit(0);
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

    @Override
    public void update(Observable o, Object arg) {
        // NOSONAR squid:S1186 ; nothing to do here...
    }

    /**
     * Ableitung von AKJPanel, um ein Panel mit Hintergrundbild darzustellen.
     */
    static class ImagePanel extends AKJPanel {
        private Image image = null;

        public ImagePanel(Image image) {
            super();
            this.image = image;
        }

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
     */
    class TFKeyListener extends KeyAdapter {
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
