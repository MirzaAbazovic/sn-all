/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2004 14:21:30
 */
package de.augustakom.hurrican.gui.system;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.cc.DummySocketService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Info-Dialog ueber die Hurrican-Applikation.
 *
 *
 */
public class HurricanAboutDialog extends AKJAbstractOptionDialog {

    private static final String BTN_SOCKET_TEST = "btn.socket.test";
    private static final Logger LOGGER = Logger.getLogger(HurricanAboutDialog.class);

    private AKJLabel lblAppName = null;
    private AKJLabel lblVersion = null;
    private AKJLabel lblSysMode = null;

    private DummySocketService dummySocketService;
    private AKJTextField tfSocketTestDuration;
    private AKJLabel lbSocketTestInfo;

    /**
     * Konstruktor.
     */
    public HurricanAboutDialog() {
        super("de/augustakom/hurrican/gui/system/resources/HurricanAboutDialog.xml");
        createGUI();
        load();
        initServices();
    }

    private void initServices() {
        try {
            dummySocketService = CCServiceFinder.instance().getCCService(DummySocketService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Infos zur Applikation");
        setIconURL("de/augustakom/hurrican/gui/images/info.gif");

        AKJLabel lblIcon = getSwingFactory().createLabel("info.icon");
        AKJLabel lblCompany = getSwingFactory().createLabel("company");
        lblCompany.setFontStyle(Font.BOLD);
        AKJLabel lblApplication = getSwingFactory().createLabel("application");
        AKJLabel lblVersionDef = getSwingFactory().createLabel("version");
        AKJLabel lblSysModeDef = getSwingFactory().createLabel("system.mode");

        lblAppName = getSwingFactory().createLabel("application.name");
        lblAppName.setFontStyle(Font.BOLD);
        lblVersion = getSwingFactory().createLabel("version.number");
        lblSysMode = getSwingFactory().createLabel("system.mode.value");

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblIcon, GBCFactory.createGBC(0, 0, 1, 1, 1, 3, GridBagConstraints.BOTH));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        top.add(lblCompany, GBCFactory.createGBC(100, 0, 3, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.NONE));
        top.add(lblApplication, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 3, 1, 1, GridBagConstraints.NONE));
        top.add(lblAppName, GBCFactory.createGBC(0, 0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblVersionDef, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblVersion, GBCFactory.createGBC(0, 0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblSysModeDef, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblSysMode, GBCFactory.createGBC(0, 0, 5, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 6, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        Dimension tableDim = new Dimension(365, 180);

        InfoTableModel tbMdlInfos = new InfoTableModel();
        AKJTable tbInfos = new AKJTable(tbMdlInfos, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbInfos.fitTable(new int[] { 170, 170 });
        tbInfos.getPopupMouseListener().setChangeSelectionOnPopup(false);
        AKJScrollPane spTable = new AKJScrollPane(tbInfos);
        spTable.setPreferredSize(tableDim);

        DBInfoTableModel tbMdlDbInfo = new DBInfoTableModel();
        AKJTable tbDbInfos = new AKJTable(tbMdlDbInfo, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbDbInfos.fitTable(new int[] { 130, 210 });
        tbDbInfos.getPopupMouseListener().setChangeSelectionOnPopup(false);
        AKJScrollPane spDbTable = new AKJScrollPane(tbDbInfos);
        spDbTable.setPreferredSize(tableDim);

        AKJButton btnOk = getSwingFactory().createButton("ok", getActionListener());
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnOk, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel socketTestPanel = new AKJPanel(new GridBagLayout());
        AKJButton btnSocketTest = getSwingFactory().createButton(BTN_SOCKET_TEST, getActionListener());
        tfSocketTestDuration = getSwingFactory().createTextField("socket.test.duration");
        tfSocketTestDuration.setText("5000");
        lbSocketTestInfo = getSwingFactory().createLabel("socket.test.info");
        socketTestPanel.add(btnSocketTest, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        socketTestPanel.add(tfSocketTestDuration, GBCFactory.createGBC(1, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        socketTestPanel.add(lbSocketTestInfo, GBCFactory.createGBC(1, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab("System-Infos", spTable);
        tabbedPane.addTab("DB-Infos", spDbTable);
        tabbedPane.addTab("Socket-Test", socketTestPanel);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(top, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tabbedPane, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(btnPanel, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    private void load() {
        lblAppName.setText("HURRICAN II");
        AuthenticationSystem authSys = (AuthenticationSystem) HurricanSystemRegistry.instance().getValue(
                HurricanSystemRegistry.AUTHENTICATION_SYSTEM);
        lblSysMode.setText((authSys != null) ? authSys.getName() : "unbekannt");

        ResourceReader rr = new ResourceReader("de.augustakom.hurrican.gui.system.resources.version");
        if (StringUtils.isNotBlank(rr.getValue("hurrican.version"))) {
            lblVersion.setText(rr.getValue("hurrican.version"));
        }
        else {
            lblVersion.setText("?");
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("ok".equals(command)) {
            prepare4Close();
            setValue(OK_OPTION);
        }
        else if (BTN_SOCKET_TEST.equals(command)) {
            if (dummySocketService != null) {
                LOGGER.info("calling dummy service");
                lbSocketTestInfo.setText("Socket test gestartet @ " + new Date());
                invalidate();
                repaint();

                String duration = tfSocketTestDuration.getText();
                long sleep = 5000L;
                try {
                    sleep = Long.valueOf(duration);
                }
                catch (NumberFormatException e) {
                    LOGGER.error(e.getMessage());
                }

                lbSocketTestInfo.setText("Socket test finished @ " + new Date());
                invalidate();
                repaint();

                String msg = dummySocketService.doSomething(sleep);
                LOGGER.info(msg);
                JOptionPane.showMessageDialog(this, msg);
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /* TableModel fuer System-Informationen. */
    static class InfoTableModel extends AKTableModel<String> {

        /**
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount() {
            return 10;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return 2;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Name";
                case 1:
                    return "Wert";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            if (column == 0) {
                switch (row) {
                    case 0:
                        return "Applikation:";
                    case 1:
                        return "Angemeldet als";
                    case 2:
                        return "Session-ID";
                    case 3:
                        return "Betriebssystem:";
                    case 4:
                        return "Name";
                    case 5:
                        return "Version";
                    case 6:
                        return "User";
                    case 7:
                        return "Java:";
                    case 8:
                        return "Version";
                    case 9:
                        return "Java-Home";
                    default:
                        break;
                }
            }
            else if (column == 1) {
                switch (row) {
                    case 1:
                        return HurricanSystemRegistry.instance().getCurrentUserName();
                    case 2:
                        return HurricanSystemRegistry.instance().getSessionId();
                    case 4:
                        return SystemUtils.OS_NAME;
                    case 5:
                        return SystemUtils.OS_VERSION;
                    case 6:
                        return SystemUtils.USER_NAME;
                    case 8:
                        return SystemUtils.JAVA_VERSION;
                    case 9:
                        return SystemUtils.JAVA_HOME;
                    default:
                        break;
                }
            }

            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /* TableModel fuer die Anzeige der DB-Eigenschaften. */
    static class DBInfoTableModel extends AKTableModel<String> {
        private SortedMap<String, String> dbProperties = null;

        /**
         * Default-Konstruktor
         */
        public DBInfoTableModel() {
            super();
            initModel();
        }

        /* Initialisiert das TableModel. */
        private void initModel() {
            dbProperties = new TreeMap<>();
            Iterator<Object> sysKeys = System.getProperties().keySet().iterator();
            while (sysKeys.hasNext()) {
                String key = (String) sysKeys.next();
                if ((key != null)
                        && key.startsWith(HurricanConstants.HURRICAN_NAME)
                        && !StringUtils.contains(key, HurricanConstants.JDBC_PASSWORD_SUFFIX)) {
                    dbProperties.put(key, System.getProperty(key));
                }
            }
            setData(dbProperties.values());
        }

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return 2;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            String keyObject = null;
            Iterator<String> keyIt = dbProperties.keySet().iterator();
            int rowX = 0;
            while (keyIt.hasNext()) {
                String next = keyIt.next();
                if (rowX == row) {
                    keyObject = next;
                    break;
                }
                rowX++;
            }

            if (column == 0) {
                return keyObject;
            }
            else if (column == 1) {
                return dbProperties.get(keyObject);
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "DB-Eigenschaft";
                case 1:
                    return "Wert";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}


