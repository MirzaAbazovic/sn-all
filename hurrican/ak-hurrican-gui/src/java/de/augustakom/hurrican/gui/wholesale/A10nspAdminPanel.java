/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2012 11:19:50
 */
package de.augustakom.hurrican.gui.wholesale;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI.*;
import javax.swing.table.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;

/**
 *
 */
public class A10nspAdminPanel extends AbstractServicePanel {
    private static final Logger LOGGER = Logger.getLogger(A10nspAdminPanel.class);

    private static final String CMD_NEW = "btn.new";
    private static final String CMD_EDIT = "btn.edit";
    private static final String CMD_DELETE = "btn.delete";

    private static final List<String> CMDS = ImmutableList.of(CMD_NEW, CMD_EDIT, CMD_DELETE);

    private static final String BTN_ASSIGN_OLT = "btn.assign.olt";
    private static final String BTN_WITHDRAW_OLT = "btn.withdraw.olt";

    private EkpFrameContractService frameContractService;
    private HWService hwService;
    private A10Nsp selectedA10Nsp = null;
    private A10NspPort selectedA10NspPort = null;
    private final Buttons a10NspPortButtons = new Buttons(CMD_NEW, CMD_DELETE);
    boolean isMnetA10Nsp;

    private AKJTable a10NspTable;
    private AKJTable a10NspPortTable;
    private OltTable zugewieseneOltsTable;
    private OltTable verfuegbareOltsTable;

    private AKJButton btnAssignOlt;
    private AKJButton btnWithdrawOlt;

    public A10nspAdminPanel() {
        super("de/augustakom/hurrican/gui/wholesale/resources/A10nspAdminPanel.xml");
        initServices();
        createGUI();
        loadData();
    }

    private void loadData() {
        List<A10Nsp> a10Nsps = frameContractService.findAllA10Nsp();
        getA10NspTableModel().setData(a10Nsps);
    }

    @SuppressWarnings("unchecked")
    private AKMutableTableModel<A10Nsp> getA10NspTableModel() {
        return ((AKMutableTableModel<A10Nsp>) a10NspTable.getModel());
    }

    @SuppressWarnings("unchecked")
    private AKMutableTableModel<A10NspPort> getA10NspPortTableModel() {
        return ((AKMutableTableModel<A10NspPort>) a10NspPortTable.getModel());
    }

    private void initServices() {
        try {
            frameContractService = getCCService(EkpFrameContractService.class);
            hwService = getCCService(HWService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    @Override
    protected final void createGUI() {
        AKReflectionTableModel<A10Nsp> a10NspTableModel = new AKReflectionTableModel<A10Nsp>(
                new String[] {
                        getSwingFactory().getLabelText("A10Nsp.nummer"),
                        getSwingFactory().getLabelText("A10Nsp.name") },
                new String[] { "nummer", "name" },
                new Class[] { Integer.class, String.class }
        );

        AKReflectionTableModel<A10NspPort> a10NspPortTableModel = new AKReflectionTableModel<A10NspPort>(
                new String[] {
                        getSwingFactory().getLabelText("A10NspPort.vbz.vbz") },
                new String[] { "vbz.vbz" },
                new Class[] { String.class }
        );

        AKJPanel a10NspPanel = new A10NspTable(a10NspTableModel);
        AKJPanel a10NspPortPanel = createA10NspPortPanel(a10NspPortTableModel);

        AKJPanel a10nspPanelsPanel = new AKJPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        a10nspPanelsPanel.add(a10NspPanel);
        a10nspPanelsPanel.add(a10NspPortPanel);

        AKJPanel oltZuweisungPanel = createOltZuweisungPanel();

        setLayout(new GridBagLayout());
        //@formatter:off
        GridBagConstraints a10Gbc  = GBCFactory.createGBC(100,   0, 0, 0, 1, 1, GridBagConstraints.NONE);
        GridBagConstraints oltGbc  = GBCFactory.createGBC(100,   0, 0, 1, 1, 1, GridBagConstraints.NONE);
        GridBagConstraints fillGbc = GBCFactory.createGBC(  0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL);
        //@formatter:on
        a10Gbc.anchor = GridBagConstraints.WEST;
        oltGbc.anchor = GridBagConstraints.WEST;
        this.add(a10nspPanelsPanel, a10Gbc);
        this.add(oltZuweisungPanel, oltGbc);
        this.add(new AKJPanel(), fillGbc);

    }

    private AKReflectionTableModel<HWOlt> createHwOltTableModel() {
        return new AKReflectionTableModel<HWOlt>(
                new String[] {
                        getSwingFactory().getLabelText("HWOlt.oltNr"),
                        getSwingFactory().getLabelText("HWOlt.geraeteBez"),
                        getSwingFactory().getLabelText("HWOlt.vlanAktivAb") },
                new String[] { "oltNr", "geraeteBez", "vlanAktivAb" },
                new Class[] { Integer.class, String.class, Date.class }
        );
    }

    private AKJPanel createA10NspPortPanel(AKReflectionTableModel<A10NspPort> a10NspPortTableModel) {
        AKJPanel a10NspPortPanel = new AKJPanel(new BorderLayout(), "A10-NSP Ports");
        a10NspPortTable = new A10NspPortTable(a10NspPortTableModel);
        AKJScrollPane a10NspPortPane = new AKJScrollPane(a10NspPortTable, new Dimension(350, 250));
        a10NspPortPanel.add(a10NspPortPane, BorderLayout.NORTH);
        a10NspPortPanel.add(a10NspPortButtons, BorderLayout.SOUTH);
        a10NspPortButtons.getButton(CMD_NEW).addActionListener(event -> {
            try {
                A10NspPort port = frameContractService.createA10NspPort(selectedA10Nsp);
                getA10NspPortTableModel().addObject(port);
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        });
        a10NspPortButtons.getButton(CMD_DELETE).addActionListener(event -> {
            int confirmResult = MessageHelper.showConfirmDialog(this,
                    String.format("Port '%s' wirklich löschen?", selectedA10NspPort.getVbz().getVbz()),
                    "A10-NSP Port löschen?", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION != confirmResult) {
                return;
            }
            try {
                frameContractService.deleteA10NspPort(selectedA10NspPort);
                getA10NspPortTableModel().removeObject(selectedA10NspPort);
                a10NspPortButtons.getButton(CMD_DELETE).setEnabled(false);
                removeAllObjectsFromTable(verfuegbareOltsTable);
                removeAllObjectsFromTable(zugewieseneOltsTable);
            }
            catch (DataIntegrityViolationException e) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Dieser Port wird von einem EKP verwendet und kann daher nicht gelöscht werden. Löschen Sie bei Bedarf zuerst die Zuordnung zum EKP.");
            }
            catch (RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        });
        return a10NspPortPanel;
    }

    private AKJPanel createOltZuweisungPanel() {
        zugewieseneOltsTable = new OltTable(createHwOltTableModel());
        verfuegbareOltsTable = new OltTable(createHwOltTableModel());

        AKJPanel zugewieseneOltsPanel = createPanelWithOltTable(zugewieseneOltsTable, "zugewiesene OLTs");
        AKJPanel verfuegbareOltsPanel = createPanelWithOltTable(verfuegbareOltsTable, "verfügbare OLTs");

        AKJPanel oltZuweisungsButtonsPanel = new AKJPanel(new GridLayout(2, 1, 0, 5));
        btnAssignOlt = createBtnAssignOlt();
        btnWithdrawOlt = createBtnWithdrawOlt();

        oltZuweisungsButtonsPanel.add(btnAssignOlt);
        oltZuweisungsButtonsPanel.add(btnWithdrawOlt);

        AKJPanel oltsPanel = new AKJPanel(new FlowLayout(FlowLayout.LEFT, 5, 5), "A10-NSP Port zu OLTs");
        oltsPanel.add(zugewieseneOltsPanel);
        oltsPanel.add(oltZuweisungsButtonsPanel);
        oltsPanel.add(verfuegbareOltsPanel);

        return oltsPanel;
    }

    private AKJButton createBtnWithdrawOlt() {
        AKJButton btnWithdrawOlt = getSwingFactory().createButton(BTN_WITHDRAW_OLT);
        btnWithdrawOlt.addActionListener(event -> {
            try {
                if (selectedA10NspPort != null) {
                    List<HWOlt> selectedOlts = zugewieseneOltsTable.getSelectedOlts();
                    selectedA10NspPort.getOlt().removeAll(selectedOlts);
                    frameContractService.saveA10NspPort(selectedA10NspPort);
                    getTableModel(verfuegbareOltsTable).addObjects(selectedOlts);
                    getTableModel(zugewieseneOltsTable).removeObjects(selectedOlts);
                }
            }
            catch (Exception exc) {
                LOGGER.error(exc.getMessage(), exc);
                MessageHelper.showErrorDialog(getMainFrame(), exc);
            }
        });
        return btnWithdrawOlt;
    }

    private AKJButton createBtnAssignOlt() {
        AKJButton btnAssignOlt = getSwingFactory().createButton(BTN_ASSIGN_OLT);
        btnAssignOlt.addActionListener(event -> {
            try {
                if (selectedA10NspPort != null) {
                    List<HWOlt> selectedOlts = verfuegbareOltsTable.getSelectedOlts();
                    AKWarnings warnings = frameContractService.filterNotAssignableOlts(selectedA10NspPort, selectedOlts);
                    if (warnings.isNotEmpty()) {
                        MessageHelper.showWarningDialog(getMainFrame(), warnings.getWarningsAsText(), true);
                    }
                    selectedA10NspPort.getOlt().addAll(selectedOlts);
                    frameContractService.saveA10NspPort(selectedA10NspPort);
                    getTableModel(zugewieseneOltsTable).addObjects(selectedOlts);
                    getTableModel(verfuegbareOltsTable).removeObjects(selectedOlts);
                }
            }
            catch (Exception exc) {
                LOGGER.error(exc.getMessage(), exc);
                MessageHelper.showErrorDialog(getMainFrame(), exc);
            }
        });
        return btnAssignOlt;
    }

    private AKJPanel createPanelWithOltTable(AKJTable oltsTable, String headline) {
        AKJScrollPane oltsPane = new AKJScrollPane(oltsTable, new Dimension(350, 250));
        AKJPanel oltsPanel = new AKJPanel(new BorderLayout(), headline);
        oltsPanel.add(oltsPane, BorderLayout.NORTH);
        return oltsPanel;
    }

    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    private void removeAllObjectsFromTable(AKJTable table) {
        if (getTableModel(table).getData() != null) {
            getTableModel(table).removeObjects(getTableModel(table).getData());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> AKMutableTableModel<T> getTableModel(AKJTable table) {
        return (AKMutableTableModel<T>) table.getModel();
    }

    private class Buttons extends AKJPanel {
        final Map<String, JButton> buttonMap;

        public Buttons() {
            this(CMD_NEW, CMD_EDIT, CMD_DELETE);
        }

        public Buttons(String... cmds) {
            setLayout(new ButtonAreaLayout(true, 5));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            List<String> cmdsAsList = ImmutableList.copyOf(cmds);
            ImmutableMap.Builder<String, JButton> buttonListBuilder = ImmutableMap.builder();
            for (String name : CMDS) {
                if (cmdsAsList.contains(name)) {
                    AKJButton btn = getSwingFactory().createButton(name);
                    btn.setEnabled(false);
                    add(btn);
                    buttonListBuilder.put(name, btn);
                }
            }
            buttonMap = buttonListBuilder.build();
        }

        public JButton getButton(String name) {
            return buttonMap.get(name);
        }
    }

    private class A10NspTable extends AKJPanel implements AKTableOwner {
        final Buttons buttons = new Buttons();
        final AKTableModel<A10Nsp> tableModel;

        public A10NspTable(AKTableModel<A10Nsp> tableModel) {
            super(new BorderLayout(), "A10-NSPs");
            this.tableModel = tableModel;
            a10NspTable = new AKJTable(tableModel, AKJTable.AUTO_RESIZE_LAST_COLUMN,
                    ListSelectionModel.SINGLE_SELECTION);
            AKJScrollPane pane = new AKJScrollPane(a10NspTable, new Dimension(350, 250));
            a10NspTable.attachSorter();
            final AKTableListener listener = new AKTableListener(this);
            a10NspTable.addMouseListener(listener);
            a10NspTable.addKeyListener(listener);
            add(pane, BorderLayout.NORTH);
            add(buttons, BorderLayout.SOUTH);
            a10NspTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if ((e.getClickCount() > 1) && buttons.getButton(CMD_EDIT).isEnabled()) {
                        editDetail(selectedA10Nsp);
                    }
                }
            });
            buttons.getButton(CMD_NEW).setEnabled(true);
            buttons.getButton(CMD_NEW).addActionListener(e -> editDetail(new A10Nsp()));
            buttons.getButton(CMD_EDIT).addActionListener(e -> editDetail(selectedA10Nsp));
            buttons.getButton(CMD_DELETE).addActionListener(event -> {
                int confirmResult = MessageHelper.showConfirmDialog(A10nspAdminPanel.this,
                        String.format("'%s' wirklich löschen?", selectedA10Nsp.getName()),
                        "A10-NSP löschen?", JOptionPane.YES_NO_OPTION);
                if (JOptionPane.YES_OPTION != confirmResult) {
                    return;
                }
                try {
                    frameContractService.deleteA10Nsp(selectedA10Nsp);
                    getA10NspTableModel().removeObject(selectedA10Nsp);
                    removeAllObjectsFromTable(a10NspPortTable);
                    removeAllObjectsFromTable(verfuegbareOltsTable);
                    removeAllObjectsFromTable(zugewieseneOltsTable);
                }
                catch (DataIntegrityViolationException e) {
                    MessageHelper
                            .showInfoDialog(
                                    getMainFrame(),
                                    "Ein Port der A10-NSP wird von einem EKP verwendet und kann daher nicht gelöscht werden. Löschen Sie bei Bedarf zuerst die Zuordnung zum EKP.");
                }
                catch (FindException e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            });
        }

        protected void editDetail(A10Nsp a10Nsp) {
            EditDialog dialog = new EditDialog("de/augustakom/hurrican/gui/wholesale/resources/A10nspAdminPanel.xml",
                    a10Nsp,
                    ImmutableList.of("nummer", "name"));
            Object object = DialogHelper.showDialog(A10nspAdminPanel.this, dialog, true, true);
            if (object instanceof A10Nsp) {
                try {
                    A10Nsp edited = (A10Nsp) object;
                    boolean isNew = edited.getId() == null;
                    edited = frameContractService.saveA10Nsp(edited);
                    if (isNew) {
                        tableModel.addObject(edited);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                tableModel.fireTableDataChanged();
                // nach dem fireTableDataChanged() ist die Selection weg - Edit erst wieder nach Selektion
                buttons.getButton(CMD_EDIT).setEnabled(false);
                buttons.getButton(CMD_DELETE).setEnabled(false);
                a10NspPortButtons.getButton(CMD_NEW).setEnabled(false);
            }
        }

        @Override
        public void showDetails(Object details) {
            if (!(details instanceof A10Nsp)) {
                return;
            }

            selectedA10Nsp = (A10Nsp) details;
            try {
                List<A10NspPort> ports = frameContractService.findA10NspPorts(selectedA10Nsp);
                getA10NspPortTableModel().setData(ports);
            }
            catch (FindException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }

            // A10-NSP ist selektiert, edit und delete ist nun moeglich, falls nicht M-net
            isMnetA10Nsp = selectedA10Nsp.getNummer().equals(A10Nsp.A10_NSP_MNET_NUMMER);
            buttons.getButton(CMD_EDIT).setEnabled(!isMnetA10Nsp);
            buttons.getButton(CMD_DELETE).setEnabled(!isMnetA10Nsp);
            a10NspPortButtons.getButton(CMD_NEW).setEnabled(!isMnetA10Nsp);
            a10NspPortButtons.getButton(CMD_DELETE).setEnabled(false);

            selectedA10NspPort = null;

            // getTableModel(verfuegbareOltsTable).removeAll() funktioniert nicht !?
            removeAllObjectsFromTable(verfuegbareOltsTable);
            // getTableModel(zugewieseneOltsTable).removeAll(); funktioniert nicht !?
            removeAllObjectsFromTable(zugewieseneOltsTable);
        }
    }


    private class A10NspPortTable extends AKJTable implements AKTableOwner {
        public A10NspPortTable(TableModel tableModel) {
            super(tableModel, AKJTable.AUTO_RESIZE_LAST_COLUMN, ListSelectionModel.SINGLE_SELECTION);
            this.attachSorter();
            AKTableListener listener = new AKTableListener(this);
            this.addMouseListener(listener);
            this.addKeyListener(listener);
        }

        @Override
        public void showDetails(Object details) {
            if (!(details instanceof A10NspPort)) {
                return;
            }
            try {
                selectedA10NspPort = frameContractService.findA10NspPortById(((A10NspPort) details).getId());

                // ImmutableList.copyOf(a10NspPort.getOlt()) notwendig wegen hashcode()-Implementierung
                List<HWOlt> zuweisbareOlts = findZuweisbareOlts(ImmutableList.copyOf(selectedA10NspPort.getOlt()));

                removeAllObjectsFromTable(verfuegbareOltsTable);
                removeAllObjectsFromTable(zugewieseneOltsTable);

                if (isMnetA10Nsp) {
                    btnAssignOlt.setEnabled(false);
                    btnWithdrawOlt.setEnabled(false);
                }
                else {
                    btnAssignOlt.setEnabled(true);
                    btnWithdrawOlt.setEnabled(true);
                }

                getTableModel(verfuegbareOltsTable).addObjects(zuweisbareOlts);
                getTableModel(zugewieseneOltsTable).addObjects(selectedA10NspPort.getOlt());

                a10NspPortButtons.getButton(CMD_DELETE).setEnabled(!isMnetA10Nsp);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getFirstParentFrame(), e);
            }
        }

        private List<HWOlt> findZuweisbareOlts(Collection<HWOlt> zugewieseneOlts) {
            List<HWOlt> zuweisbareOlts = new LinkedList<HWOlt>();
            try {
                List<HWOlt> alleOlts = hwService.findRacksByType(HWOlt.class);
                for (HWOlt olt : alleOlts) {
                    if (!zugewieseneOlts.contains(olt)) {
                        zuweisbareOlts.add(olt);
                    }
                }
            }
            catch (FindException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            return zuweisbareOlts;
        }
    }

    private static class OltTable extends AKJTable {

        public OltTable(TableModel tableModel) {
            super(tableModel, AKJTable.AUTO_RESIZE_LAST_COLUMN, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            this.attachSorter();
        }

        public List<HWOlt> getSelectedOlts() {
            List<HWOlt> selectedOlts = Lists.newLinkedList();
            @SuppressWarnings("unchecked")
            AKMutableTableModel<HWOlt> tm = (AKMutableTableModel<HWOlt>) getModel();
            for (int i : this.getSelectedRows()) {
                selectedOlts.add(tm.getDataAtRow(i));
            }
            return selectedOlts;
        }
    }
}


