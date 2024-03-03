/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2012 11:16:55
 */
package de.augustakom.hurrican.gui.wholesale;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicOptionPaneUI.*;
import javax.swing.table.*;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;

/**
 * AdminPanel für {@link EkpFrameContract}, dessen {@link CVlan} und {@link A10NspPort} Zuordnung.
 */
public class EkpAdminPanel extends AbstractServicePanel implements AKTableOwner {

    private static final String EKP_ADMIN_PANEL_XML = "de/augustakom/hurrican/gui/wholesale/resources/EkpAdminPanel.xml";

    private static final Logger LOGGER = Logger.getLogger(EkpAdminPanel.class);

    private static final String CMD_NEW = "btn.new";
    private static final String CMD_EDIT = "btn.edit";
    private static final String CMD_DELETE = "btn.delete";
    private static final long serialVersionUID = -5344057885221442227L;

    private AKJTable ekpTable;
    private AKJTable cvlanTable;
    private AKJTable a10NspPortTable;

    private EkpFrameContractService frameContractService;

    public EkpAdminPanel() {
        super(EKP_ADMIN_PANEL_XML);
        initServices();
        createGUI();
        loadData();
    }

    private void loadData() {
        List<EkpFrameContract> ekpFrameContracts = frameContractService.findAllEkpFrameContract();
        getEkpTableModel().setData(ekpFrameContracts);
        showDetails(null);
    }

    private void initServices() {
        try {
            frameContractService = getCCService(EkpFrameContractService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        AKReflectionTableModel<EkpFrameContract> ekpTableModel = new AKReflectionTableModel<>(
                new String[] { getSwingFactory().getLabelText("EkpFrameContract.ekpId"),
                        getSwingFactory().getLabelText("EkpFrameContract.frameContractId"),
                        getSwingFactory().getLabelText("EkpFrameContract.svlanFaktor") },
                new String[] { "ekpId", "frameContractId", "svlanFaktor" },
                new Class[] { String.class, String.class, Integer.class }
        );

        AKReflectionTableModel<CVlan> cvlanTableModel = new AKReflectionTableModel<>(new String[] {
                getSwingFactory().getLabelText("CVlan.typ"),
                getSwingFactory().getLabelText("CVlan.typ.type"), getSwingFactory().getLabelText("CVlan.protocoll"),
                getSwingFactory().getLabelText("CVlan.value"), getSwingFactory().getLabelText("CVlan.pbitLimit") },
                new String[] { "typ", "typ.type", "protocoll", "value", "pbitLimit" },
                new Class[] { String.class, String.class, String.class, Integer.class, Integer.class }
        );

        AKReflectionTableModel<Entry<A10NspPort, Boolean>> a10NspPortTableModel = new AKReflectionTableModel<>(
                new String[] { getSwingFactory().getLabelText("A10NspPort.a10Nsp"),
                        getSwingFactory().getLabelText("A10NspPort.a10NspPort"),
                        getSwingFactory().getLabelText("A10NspPort.defaultPort") },
                new String[] { "key.a10Nsp", "key.vbz.vbz", "value" },
                new Class[] { A10Nsp.class, String.class, Boolean.class }
        );

        ekpTable = new AKJTable(ekpTableModel);
        AKTableListener listener = new AKTableListener(this);
        ekpTable.addMouseListener(listener);
        ekpTable.addKeyListener(listener);
        ekpTable.attachSorter();
        AKJScrollPane ekpPane = new AKJScrollPane(ekpTable, new Dimension(350, 250));
        AKJPanel ekpPanel = new AKJPanel(new BorderLayout(), "EKPs");
        ekpPanel.add(ekpPane, BorderLayout.NORTH);
        Buttons ekpButtons = new Buttons(new EkpActionListener(), ekpTable);
        ekpPanel.add(ekpButtons, BorderLayout.CENTER);

        cvlanTable = new AKJTable(cvlanTableModel);
        cvlanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cvlanTable.attachSorter();
        AKJScrollPane cvlanPane = new AKJScrollPane(cvlanTable, new Dimension(350, 250));
        AKJPanel cvlanPanel = new AKJPanel(new BorderLayout(), "CVLANs");
        cvlanPanel.add(cvlanPane, BorderLayout.NORTH);
        Buttons cvlanButtons = new Buttons(new CvlanActionListener(), cvlanTable);
        ekpButtons.addSelectionDependantButton(cvlanButtons.getNewButton());
        cvlanPanel.add(cvlanButtons, BorderLayout.CENTER);

        a10NspPortTable = new AKJTable(a10NspPortTableModel);
        a10NspPortTable.attachSorter();
        a10NspPortTable.setDefaultRenderer(A10Nsp.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = -5025519483657151359L;

            @Override
            protected void setValue(Object value) {
                super.setValue(getA10NspViewName(value));
            }
        });
        AKJScrollPane a10NspPortPane = new AKJScrollPane(a10NspPortTable, new Dimension(350, 250));
        AKJPanel a10NspPortPanel = new AKJPanel(new BorderLayout(), "A10-NSP Ports");
        a10NspPortPanel.add(a10NspPortPane, BorderLayout.NORTH);
        Buttons a10NspPortButtons = new Buttons(new A10NspPortActionListener(), a10NspPortTable);
        ekpButtons.addSelectionDependantButton(a10NspPortButtons.getNewButton());
        a10NspPortPanel.add(a10NspPortButtons, BorderLayout.CENTER);

        setLayout(new GridBagLayout());
        //@formatter:off
        this.add(ekpPanel,        GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE, 10));
        this.add(cvlanPanel,      GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.NONE, 10));
        this.add(a10NspPortPanel, GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.NONE, 10));
        this.add(new AKJPanel(),  GBCFactory.createGBC(100,   0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(),  GBCFactory.createGBC(  0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        //@formatter:on
    }

    private String getA10NspViewName(Object value) {
        if (value instanceof A10Nsp) {
            return ((A10Nsp) value).getName() + " (Nr: " + ((A10Nsp) value).getNummer() + ")";
        }
        else {
            return value == null ? "" : value.toString();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void execute(String command) {
    }

    private void save(EkpFrameContract ekpFrameContract) {
        try {
            boolean defaultPortFound = false;
            for (Entry<A10NspPort, Boolean> entry : ekpFrameContract.getA10NspPortsOfEkp().entrySet()) {
                if (defaultPortFound && entry.getValue()) {
                    throw new GUIException("Es darf maximal ein Port als Default markiert sein");
                }
                defaultPortFound |= entry.getValue();
            }
            int confirmResult = JOptionPane.OK_OPTION;
            if (ekpFrameContract.getId() != null) {
                confirmResult = new Auftrag2EkpFrameContractCheck().checkWithDialog(this, ekpFrameContract);
            }
            if (JOptionPane.YES_OPTION == confirmResult) {
                frameContractService.saveEkpFrameContract(ekpFrameContract);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        int minSelectionIndex = ekpTable.getSelectionModel().getMinSelectionIndex();
        loadData();
        // select the new created entity
        ekpTable.selectAndScrollToRow(minSelectionIndex);
    }

    @SuppressWarnings("unchecked")
    private AKMutableTableModel<EkpFrameContract> getEkpTableModel() {
        return ((AKMutableTableModel<EkpFrameContract>) ekpTable.getModel());
    }

    @SuppressWarnings("unchecked")
    private AKMutableTableModel<CVlan> getCvlanTableModel() {
        return ((AKMutableTableModel<CVlan>) cvlanTable.getModel());
    }

    @SuppressWarnings("unchecked")
    private AKMutableTableModel<Entry<A10NspPort, Boolean>> getA10NspPortTableModel() {
        return ((AKMutableTableModel<Entry<A10NspPort, Boolean>>) a10NspPortTable.getModel());
    }

    private void editEkpFrameContract(EkpFrameContract ekpFrameContract) {
        EditDialog dialog = new EditDialog(EKP_ADMIN_PANEL_XML, ekpFrameContract,
                ImmutableList.of("ekpId", "frameContractId", "svlanFaktor"));
        Object object = DialogHelper.showDialog(EkpAdminPanel.this, dialog, true, true);
        if (object instanceof EkpFrameContract) {
            save((EkpFrameContract) object);
        }
    }

    private void editCvlan(EkpFrameContract ekpFc, CVlan cVlan) {
        EditDialog dialog = new EditDialog(EKP_ADMIN_PANEL_XML, cVlan,
                ImmutableList.of("typ", "protocoll", "value", "pbitLimit"));
        Object object = DialogHelper.showDialog(EkpAdminPanel.this, dialog, true, true);
        if (object instanceof CVlan) {
            CVlan editedCvlan = (CVlan) object;
            if (editedCvlan.getId() == null) {
                ekpFc.getCvlans().add(editedCvlan);
            }
            save(ekpFc);
        }
    }

    /**
     * @param ekpFc
     * @param a10NspPortIn  ein Port für den das defaultPort Flag geändert werden soll, oder <code>null</code> wenn ein
     *                    neuer Port hinzugefügt werden soll
     * @param defaultPort
     */
    private void editA10NspPort(EkpFrameContract ekpFc, A10NspPort a10NspPortIn, Boolean defaultPort) {
        A10NspPort a10NspPort = a10NspPortIn;
        A10NspPortDialog dialog = new A10NspPortDialog(a10NspPort, defaultPort);
        Object object = DialogHelper.showDialog(EkpAdminPanel.this, dialog, true, true);
        if (object instanceof A10NspPort) {
            if (a10NspPort == null) {
                // Port ist neu
                a10NspPort = (A10NspPort) object;
                AKWarnings warnings = frameContractService.checkA10NspPortAssignableToEkp(ekpFc, a10NspPort);
                if (warnings.isNotEmpty()) {
                    MessageHelper.showWarningDialog(getMainFrame(), warnings.getWarningsAsText(), true);
                    return;
                }
            }

            ekpFc.getA10NspPortsOfEkp().put(a10NspPort, dialog.getDefaultPort());
            save(ekpFc);
        }
    }

    @Override
    public void showDetails(Object master) {
        Collection<CVlan> cVlans = null;
        Collection<Entry<A10NspPort, Boolean>> a10NspPorts = null;
        if (master != null) {
            cVlans = ((EkpFrameContract) master).getCvlans();
            a10NspPorts = ((EkpFrameContract) master).getA10NspPortsOfEkp().entrySet();
        }
        getCvlanTableModel().setData(cVlans);
        getA10NspPortTableModel().setData(a10NspPorts);
    }

    /**
     * Baut ein Buttons Panel auf und registriert Listener.
     */
    private class Buttons extends AKJPanel {
        private static final long serialVersionUID = 2236718195875638170L;
        /**
         * Buttons die abhängig von der selection auf der Tabelle enabled werden.
         */
        final List<Pair<AKJButton, AKJTable>> selectionDependantButtons = new ArrayList<>();
        /**
         * der New button. Wird abhängig von der Mastertabelle (EKP) enabled.
         *
         * @see #getNewButton()
         * @see #addSelectionDependantButton(Pair)
         */
        final Pair<AKJButton, AKJTable> newButton;

        public Buttons(final ActionListener actionListener, final AKJTable table) {
            setLayout(new ButtonAreaLayout(true, 5));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            AKJButton newBtn = getSwingFactory().createButton(CMD_NEW, actionListener);
            add(newBtn);
            newButton = Pair.create(newBtn, table);
            for (String name : ImmutableList.of(CMD_EDIT, CMD_DELETE)) {
                AKJButton btn = getSwingFactory().createButton(name, actionListener);
                add(btn);
                selectionDependantButtons.add(Pair.create(btn, table));
                btn.setEnabled(false);
            }
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                        for (Pair<AKJButton, AKJTable> bPair : selectionDependantButtons) {
                            boolean enabled = (ekpTable.getSelectedRow() != -1)
                                    && checkEnabled4MNet(bPair.getSecond(), bPair.getFirst().getActionCommand());
                            bPair.getFirst().setEnabled((!lsm.isSelectionEmpty()) && enabled);
                        }
                    }
                }
            });
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if ((e.getClickCount() > 1) && checkEnabled4MNet(table, CMD_EDIT)) {
                        actionListener.actionPerformed(new ActionEvent(table, -1, CMD_EDIT));
                    }
                }
            });
        }

        /**
         * Sonderlösung für MNET EKP. Für diesen dürfen nur die CVLANS bearbeitet, neu erstellt und gelöscht werden.
         *
         * @param table
         * @param actionCommand
         * @return
         */
        private boolean checkEnabled4MNet(final AKJTable table, String actionCommand) {
            return !((ekpTable.getSelectedRow() != -1)
                    && getEkpTableModel().getDataAtRow(ekpTable.getSelectedRow()).getEkpId()
                    .equals(EkpFrameContract.EKP_ID_MNET)
                    && ((CMD_DELETE.equals(actionCommand) && (table != cvlanTable))
                    || (CMD_EDIT.equals(actionCommand) && (table != cvlanTable))
                    || (CMD_NEW.equals(actionCommand) && (table == a10NspPortTable))));
        }

        public Pair<AKJButton, AKJTable> getNewButton() {
            return newButton;
        }

        /**
         * Fügt einen Button (einer anderen Tabelle) als selectionDependant hinzu.
         *
         * @param bPair
         * @see #getNewButton()
         */
        public void addSelectionDependantButton(Pair<AKJButton, AKJTable> bPair) {
            selectionDependantButtons.add(bPair);
            bPair.getFirst().setEnabled(false);
        }
    }

    /**
     *
     */
    private class EkpActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            int ekpIndex = ekpTable.getSelectedRow();
            if ((ekpIndex == -1) && !CMD_NEW.equals(cmd)) {
                return;
            }

            if (CMD_DELETE.equals(cmd)) {
                try {
                    EkpFrameContract ekpFrameContract = getEkpTableModel().getDataAtRow(ekpIndex);
                    if (new Auftrag2EkpFrameContractCheck().check(ekpFrameContract)) {
                        throw new GUIException(String.format(
                                "EKP '%s' kann nicht gelöscht werden, da diesem aktive Aufträge zugeordnet sind.",
                                ekpFrameContract.getFrameContractId()));
                    }
                    int confirmResult = MessageHelper.showConfirmDialog(EkpAdminPanel.this,
                            String.format("EKP '%s' wirklich löschen?", ekpFrameContract.getFrameContractId()),
                            "EKP löschen?", JOptionPane.YES_NO_OPTION);
                    if (JOptionPane.YES_OPTION == confirmResult) {
                        frameContractService.deleteEkpFrameContract(ekpFrameContract);
                        loadData();
                    }
                }
                catch (Exception e1) {
                    LOGGER.error(e1.getMessage(), e1);
                    MessageHelper.showErrorDialog(getMainFrame(), e1);
                }
            }
            else if (CMD_EDIT.equals(cmd)) {
                editEkpFrameContract(getEkpTableModel().getDataAtRow(ekpIndex));
            }
            else if (CMD_NEW.equals(cmd)) {
                final EkpFrameContract ekpFrameContract = new EkpFrameContract();
                ekpFrameContract.setSvlanFaktor(50);
                editEkpFrameContract(ekpFrameContract);
            }
        }
    }

    /**
     *
     */
    private class CvlanActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int ekpIndex = ekpTable.getSelectedRow();
            if ((ekpIndex == -1)) {
                return;
            }
            String cmd = e.getActionCommand();
            EkpFrameContract ekpFc = getEkpTableModel().getDataAtRow(ekpIndex);
            int cvlanIndex = cvlanTable.getSelectedRow();
            if (CMD_DELETE.equals(cmd)) {
                if ((cvlanIndex != -1)) {
                    int confirmResult = MessageHelper.showConfirmDialog(EkpAdminPanel.this,
                            String.format("CVLAN '%s' wirklich löschen?", ekpFc.getCvlans().get(cvlanIndex).getTyp()),
                            "CVLAN löschen?", JOptionPane.YES_NO_OPTION);
                    if (JOptionPane.YES_OPTION == confirmResult) {
                        ekpFc.getCvlans().remove(cvlanIndex);
                        save(ekpFc);
                    }
                }
            }
            else if (CMD_EDIT.equals(cmd)) {
                if ((cvlanIndex != -1)) {
                    editCvlan(ekpFc, getCvlanTableModel().getDataAtRow(cvlanIndex));
                }
            }
            else if (CMD_NEW.equals(cmd)) {
                editCvlan(ekpFc, new CVlan());
            }
        }
    }

    /**
     *
     */
    private class A10NspPortActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int ekpIndex = ekpTable.getSelectedRow();
            if (ekpIndex == -1) {
                return;
            }
            String cmd = e.getActionCommand();
            EkpFrameContract ekpFc = getEkpTableModel().getDataAtRow(ekpIndex);
            int a10NspPortIndex = a10NspPortTable.getSelectedRow();
            if (CMD_DELETE.equals(cmd)) {
                if ((a10NspPortIndex != -1)) {
                    Entry<A10NspPort, Boolean> a10NspPortEntry = getA10NspPortTableModel()
                            .getDataAtRow(a10NspPortIndex);
                    int confirmResult = MessageHelper.showConfirmDialog(EkpAdminPanel.this,
                            String.format("A10-NSP Port '%s' Zuordnung wirklich löschen?",
                                    a10NspPortEntry.getKey().getVbz().getVbz()),
                            "A10-NSP Port Zuordnung löschen?", JOptionPane.YES_NO_OPTION
                    );
                    if (JOptionPane.YES_OPTION == confirmResult) {
                        ekpFc.getA10NspPortsOfEkp().remove(a10NspPortEntry.getKey());
                        save(ekpFc);
                    }
                }
            }
            else if (CMD_EDIT.equals(cmd)) {
                if ((a10NspPortIndex != -1)) {
                    Entry<A10NspPort, Boolean> dataAtRow = getA10NspPortTableModel().getDataAtRow(a10NspPortIndex);
                    editA10NspPort(ekpFc, dataAtRow.getKey(), dataAtRow.getValue());
                }
            }
            else if (CMD_NEW.equals(cmd)) {
                editA10NspPort(ekpFc, null, Boolean.TRUE);
            }
        }
    }

    /**
     *
     */
    private class A10NspPortDialog extends AbstractServiceOptionDialog {
        private static final long serialVersionUID = -1639607046624462142L;
        private A10NspPort a10NspPort;
        private Boolean defaultPort;

        private AKJComboBox a10NspBox;
        private AKJComboBox a10NspPortBox;
        private AKJCheckBox defaultPortBox;

        public A10NspPortDialog(A10NspPort a10NspPort, Boolean defaultPort) {
            super(EKP_ADMIN_PANEL_XML, false, true);
            this.a10NspPort = a10NspPort;
            this.defaultPort = defaultPort;
            createGUI();
            loadData();
        }

        @Override
        protected final void createGUI() {
            String labelPrefix = A10NspPort.class.getSimpleName() + ".";
            setTitle(getSwingFactory().getText(labelPrefix + "title"));
            AKJPanel panel = new AKJPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
            AKJLabel a10NspLbl = getSwingFactory().createLabel(labelPrefix + "a10Nsp");
            a10NspBox = getSwingFactory().createComboBox(labelPrefix + "a10Nsp", a10NspPort == null);
            a10NspBox.setRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = -3618934977915827576L;

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, getA10NspViewName(value), index, isSelected,
                            cellHasFocus);
                }
            });
            a10NspBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        a10NspPortBox.setModel(new DefaultComboBoxModel(frameContractService
                                .findA10NspPorts((A10Nsp) a10NspBox.getSelectedItem()).toArray()));
                        a10NspPortBox.setSelectedItem(a10NspPort);
                    }
                    catch (FindException e1) {
                        LOGGER.error(e1.getMessage(), e1);
                        MessageHelper.showErrorDialog(getMainFrame(), e1);
                    }
                }
            });
            AKJLabel a10NspPortLbl = getSwingFactory().createLabel(labelPrefix + "a10NspPort");
            a10NspPortBox = getSwingFactory().createComboBox(labelPrefix + "a10NspPort", a10NspPort == null);
            a10NspPortBox.setRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = 4760151534085812592L;

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
                    Object value1 = value;
                    if (value1 instanceof A10NspPort) {
                        value1 = ((A10NspPort) value1).getVbz().getVbz();
                    }
                    return super.getListCellRendererComponent(list, value1, index, isSelected, cellHasFocus);
                }
            });
            a10NspPortBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getButton(AbstractServiceOptionDialog.CMD_SAVE).setEnabled(a10NspPortBox.getSelectedItem() != null);
                }
            });
            getButton(AbstractServiceOptionDialog.CMD_SAVE).setEnabled(false);
            AKJLabel defaultPortLbl = getSwingFactory().createLabel(labelPrefix + "defaultPort");
            defaultPortBox = getSwingFactory().createCheckBox(labelPrefix + "defaultPort");
            defaultPortBox.setSelected(defaultPort);

            panel.add(a10NspLbl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(a10NspBox, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(a10NspPortLbl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(a10NspPortBox, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(defaultPortLbl, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            panel.add(defaultPortBox, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            getChildPanel().setLayout(new BorderLayout());
            getChildPanel().add(panel, BorderLayout.CENTER);
        }

        private void loadData() {
            a10NspBox.setModel(new DefaultComboBoxModel(frameContractService.findAllA10Nsp().toArray()));
            if (a10NspPort != null) {
                a10NspBox.setSelectedItem(a10NspPort.getA10Nsp());
            }
            else if (a10NspBox.getModel().getSize() > 0) {
                a10NspBox.setSelectedIndex(0);
            }
        }

        @Override
        protected void doSave() {
            prepare4Close();
            this.a10NspPort = (A10NspPort) a10NspPortBox.getSelectedItem();
            this.defaultPort = defaultPortBox.isSelectedBoolean();
            setValue(a10NspPort);
        }

        @Override
        public void update(Observable o, Object arg) {
        }

        @Override
        protected void execute(String command) {
        }

        public Boolean getDefaultPort() {
            return defaultPort;
        }
    }
}
