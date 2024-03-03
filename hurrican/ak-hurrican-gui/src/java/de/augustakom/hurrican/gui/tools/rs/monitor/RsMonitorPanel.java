/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.11.2008 14:19:09
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.tools.rs.monitor.actions.OpenRsMConfigAction;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Panel fuer den Ressourcenmonitor.
 *
 *
 */
public class RsMonitorPanel extends AbstractServicePanel implements ChangeListener {

    private static final Logger LOGGER = Logger.getLogger(RsMonitorPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorPanel.xml";
    private static final String FILTER_CLUSTER = "filter.cluster";
    private static final String FILTER_STANDORTTYPE = "filter.standorttype";
    private static final String FILTER_NIEDERLASSUNG = "filter.niederlassung";
    private static final String MONITOR_TYPE = "monitor.type";
    private static final String LAST_USER = "last.user";
    private static final String STATUS = "status";
    private static final String LAST_RUN = "last.run";
    private static final String START = "start";
    private static final String CONFIG = "config";
    private static final String SHOW = "show";

    private AKJDateComponent dcLastRun = null;
    private AKJTextField tfStatus = null;
    private AKJTextField tfLastUser = null;
    private AKJTextField tfMonitorType = null;
    private AKJComboBox cbNiederlassung = null;
    private AKJList lsStandorttypen = null;
    private AKJTextField tfCluster = null;
    private AKJButton btnShow = null;
    private AKJButton btnStart = null;
    private AKJTabbedPane tabbedPane = null;

    /**
     * Default-Konstruktor.
     */
    public RsMonitorPanel() {
        super(RESOURCE);
        createGUI();
        init();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblLastRun = getSwingFactory().createLabel(LAST_RUN);
        AKJLabel lblStatus = getSwingFactory().createLabel(STATUS);
        AKJLabel lblLastUser = getSwingFactory().createLabel(LAST_USER);
        AKJLabel lblMonitorType = getSwingFactory().createLabel(MONITOR_TYPE);
        AKJLabel lblNiederlassung = getSwingFactory().createLabel(FILTER_NIEDERLASSUNG);
        AKJLabel lblstandorttyp = getSwingFactory().createLabel(FILTER_STANDORTTYPE);
        AKJLabel lblCluster = getSwingFactory().createLabel(FILTER_CLUSTER);

        dcLastRun = getSwingFactory().createDateComponent(LAST_RUN, false);
        tfStatus = getSwingFactory().createTextField(STATUS, false);
        tfLastUser = getSwingFactory().createTextField(LAST_USER, false);
        tfMonitorType = getSwingFactory().createTextField(MONITOR_TYPE, false);
        cbNiederlassung = getSwingFactory().createComboBox(FILTER_NIEDERLASSUNG);
        cbNiederlassung.setRenderer(new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));
        DefaultListModel<Reference> lsMdlStandorttypen = new DefaultListModel<Reference>();
        lsStandorttypen = getSwingFactory().createList(FILTER_STANDORTTYPE, lsMdlStandorttypen);
        lsStandorttypen.setCellRenderer(new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        JScrollPane spStandorttyp = new JScrollPane(lsStandorttypen);
        tfCluster = getSwingFactory().createTextField(FILTER_CLUSTER, true);
        btnShow = getSwingFactory().createButton(SHOW, getActionListener());
        AKJButton btnConfig = getSwingFactory().createButton(CONFIG, getActionListener());
        btnStart = getSwingFactory().createButton(START, getActionListener());

        // @formatter:off
        AKJPanel btns = new AKJPanel(new GridBagLayout());
        btns.add(btnShow        , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btns.add(new AKJPanel() , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btns.add(btnStart       , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btns.add(new AKJPanel() , GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btns.add(btnConfig      , GBCFactory.createGBC(  0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel filter = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("filter"));
        filter.add(lblNiederlassung , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(cbNiederlassung  , GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(new AKJPanel()   , GBCFactory.createGBC(100, 0, 2, 0, 1, 3, GridBagConstraints.BOTH));
        filter.add(lblstandorttyp   , GBCFactory.createGBC(  0, 0, 3, 0, 1, 3, GridBagConstraints.HORIZONTAL));
        filter.add(spStandorttyp    , GBCFactory.createGBC(100, 0, 4, 0, 1, 3, GridBagConstraints.HORIZONTAL));
        filter.add(lblCluster       , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(tfCluster        , GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(new AKJPanel()   , GBCFactory.createGBC(100, 0, 0, 2, 2, 1, GridBagConstraints.BOTH));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Ressourcen-Monitor"));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblMonitorType  , GBCFactory.createGBC(  0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        top.add(tfMonitorType   , GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        top.add(lblStatus       , GBCFactory.createGBC(  0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0, 0, 6, 1, 1, 1, GridBagConstraints.NONE));
        top.add(tfStatus        , GBCFactory.createGBC(100, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblLastRun      , GBCFactory.createGBC(  0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(dcLastRun       , GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblLastUser     , GBCFactory.createGBC(  0, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfLastUser      , GBCFactory.createGBC(100, 0, 7, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(filter          , GBCFactory.createGBC(  0, 0, 0, 3, 8, 1, GridBagConstraints.HORIZONTAL));
        top.add(btns            , GBCFactory.createGBC(  0, 0, 0, 4, 8, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC( 40,40, 8, 4, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        RsEqMonitorPanel rsEqPanel = new RsEqMonitorPanel();
        RsRangMonitorPanel rsRangPanel = new RsRangMonitorPanel();

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab("ÜVT-Überwachung", rsEqPanel);
        tabbedPane.addTab("Rangierungs-Überwachung", rsRangPanel);
        tabbedPane.addChangeListener(this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(top), tabbedPane);
        this.setLayout(new BorderLayout());
        this.add(splitPane);

        stateChanged(null);
        manageGUI(btnConfig, btnShow, btnStart);
    }

    /**
     * Initialisiert die Daten des Panel.
     */
    protected void init() {
        try {
            // Lade Niederlassungen
            NiederlassungService ns = getCCService(NiederlassungService.class);
            List<Niederlassung> niederlassungen = ns.findNiederlassungen();
            cbNiederlassung.addItems(niederlassungen, Boolean.TRUE, Niederlassung.class);

            // Lade Standorttypen
            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> typen = rs.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, true);
            lsStandorttypen.addItems(typen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if (SHOW.equals(command)) {
            showRessourcenMonitor();
        }
        if (START.equals(command)) {
            startRessourcenMonitor();
        }
        if (CONFIG.equals(command)) {
            OpenRsMConfigAction action = new OpenRsMConfigAction();
            action.actionPerformed(new ActionEvent(this, 0, null));
        }
    }

    private void showRessourcenMonitor() {
        // create query out of specified criteria on gui
        ResourcenMonitorQuery query = new ResourcenMonitorQuery();
        if (cbNiederlassung.getSelectedItem() != null) {
            query.setNiederlassungId(((Niederlassung) cbNiederlassung.getSelectedItem()).getId());
        }
        for (int i : lsStandorttypen.getSelectedIndices()) {
            Reference standortTyp = (Reference) lsStandorttypen.getModel().getElementAt(i);
            query.addStandortType(standortTyp.getId());
        }
        if (!Strings.isNullOrEmpty(tfCluster.getText())) {
            query.setCluster(tfCluster.getText());
        }
        AbstractMonitorPanel rmPanel = (AbstractMonitorPanel) tabbedPane.getSelectedComponent();
        rmPanel.setQuery(query);
        rmPanel.showRM();
    }

    private void startRessourcenMonitor() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("start.ressourcen.monitor.msg"),
                getSwingFactory().getText("start.ressourcen.monitor.title"));
        if (option == JOptionPane.YES_OPTION) {
            AbstractMonitorPanel rmPanel = (AbstractMonitorPanel) tabbedPane.getSelectedComponent();
            rmPanel.startRMRun();
            stateChanged(null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        AbstractMonitorPanel rmPanel = (AbstractMonitorPanel) tabbedPane.getSelectedComponent();
        Long newMonitor = (rmPanel != null) ? rmPanel.getRMType() : null;

        if (newMonitor != null) {
            try {
                // Lade Monitor-Run-Objekt zum entsprechenden Monitor-Typ
                MonitorService ms = getCCService(MonitorService.class);
                RSMonitorRun run = ms.findByMonitorType(newMonitor);
                showDetails(run);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getParent(), ex);
            }
        }
        else {
            showDetails(null);
        }
    }

    /**
     * Zeigt Daten in den GUI-Komponenten an
     */
    public void showDetails(Object details) {
        RSMonitorRun details1 = null;
        if ((details != null) && (details instanceof RSMonitorRun)) {
            details1 = (RSMonitorRun) details;
            try {
                ReferenceService rs = getCCService(ReferenceService.class);
                Reference ref = rs.findReference(details1.getState());
                tfStatus.setText((ref != null) ? ref.getStrValue() : null);
                tfLastUser.setText(details1.getRunExecutedBy());
                ref = rs.findReference(details1.getMonitorType());
                tfMonitorType.setText((ref != null) ? ref.getStrValue() : null);
                dcLastRun.setDate(details1.getStartedAt());
                btnShow.setEnabled(true);
                btnStart.setEnabled(true);

                if (NumberTools.notEqual(details1.getState(), RSMonitorRun.RS_REF_STATE_FINISHED)) {
                    btnShow.setEnabled(false);
                }

                if (NumberTools.equal(details1.getState(), RSMonitorRun.RS_REF_STATE_RUNNING)) {
                    btnStart.setEnabled(false);
                }
            }
            catch (Exception e) {
                LOGGER.error(e);
                MessageHelper.showErrorDialog(getParent(), e);
            }
        }
        else {
            details1 = null;
            GuiTools.cleanFields(this);
            btnShow.setEnabled(false);
        }
    }
}
