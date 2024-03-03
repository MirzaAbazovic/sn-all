/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.2010
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.Leitungsnummer;
import de.augustakom.hurrican.model.cc.Leitungsnummer.Typ;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Panel für die Darstellung der Leitungsnummer-Daten einer Endstelle
 */
public class LeitungsnummerPanel extends AbstractDataPanel implements IAuftragStatusValidator,
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(LeitungsnummerPanel.class);

    private AKJButton btnNew = null;
    private AKJButton btnDelete = null;
    private AKJTable tbLeitungsnummer = null;
    private AKReflectionTableModel<Leitungsnummer> tableModel = null;

    private CCAuftragModel auftrag = null;
    private List<Leitungsnummer> leitungsnummern = null;

    public LeitungsnummerPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/LeitungsnummerPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnNew = getSwingFactory().createButton("panel.button.new", getActionListener(), null);
        btnDelete = getSwingFactory()
                .createButton("panel.button.delete", getActionListener(), null);

        AKJPanel pnlBtn = new AKJPanel(new GridBagLayout());
        pnlBtn.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 10, 2)));
        pnlBtn.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        pnlBtn.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        tableModel = new AKReflectionTableModel<Leitungsnummer>(new String[] { "Typ",
                "Leitungsnummer" }, new String[] { "typ", "leitungsnummer" }, new Class[] {
                Typ.class, String.class });
        tbLeitungsnummer = new AKJTable(tableModel, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION);
        tbLeitungsnummer.attachSorter();
        tbLeitungsnummer.fitTable(new int[] { 200, 500 });
        tbLeitungsnummer.addMouseListener(new AKTableDoubleClickMouseListener(this));

        AKJScrollPane spTable = new AKJScrollPane(tbLeitungsnummer);
        spTable.setPreferredSize(new Dimension(710, 70));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(spTable, BorderLayout.CENTER);

        this.setBorder(BorderFactory.createTitledBorder("zugeordnete Leitungsnummern"));
        this.setLayout(new GridBagLayout());
        this.add(pnlBtn, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(tablePanel, GBCFactory.createGBC(100, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnNew, btnDelete);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.auftrag = null;
        if (model instanceof CCAuftragModel) {
            this.auftrag = (CCAuftragModel) model;
        }
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        try {
            setWaitCursor();
            tableModel.removeAll();
            if (auftrag != null) {
                leitungsnummern = getCCService(CCAuftragService.class).findLeitungsnummerByAuftrag(auftrag);
                tableModel.setData(leitungsnummern);
                btnNew.setEnabled(true);
                btnDelete.setEnabled(true);
            }
            else {
                btnNew.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof Leitungsnummer) {
            LeitungsnummerDialog dlg = new LeitungsnummerDialog((Leitungsnummer) selection);
            Object value = DialogHelper.showDialog(getMainFrame(), dlg, true, true);

            // nur für 'Speichern'-Button und wenn neuer Eintrag hinzugefügt wurde nötig
            if ((value.equals(JOptionPane.OK_OPTION)) && (!leitungsnummern.contains(selection))) {
                leitungsnummern.add((Leitungsnummer) selection);
                tableModel.setData(leitungsnummern);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("panel.button.new".equals(command)) {
            Leitungsnummer leitungsnummer = new Leitungsnummer();
            leitungsnummer.setAuftragId(auftrag.getAuftragId());
            leitungsnummer.setTyp(Typ.NUE_LBZ);
            leitungsnummer.setLeitungsnummer("");
            objectSelected(leitungsnummer);
        }
        else if ("panel.button.delete".equals(command)) {
            Leitungsnummer selected = ((AKMutableTableModel<Leitungsnummer>)
                    tbLeitungsnummer.getModel()).getDataAtRow(tbLeitungsnummer.getSelectedRow());
            try {
                if (selected == null) {
                    throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
                }
                getCCService(CCAuftragService.class).deleteLeitungsnummer(selected);
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            leitungsnummern.remove(selected);
            tableModel.setData(leitungsnummern);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(java.lang.Integer)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * Dialog zur Anzeige (und Änderung) einer Leitungsnummer
     */
    static class LeitungsnummerDialog extends AbstractServiceOptionDialog {

        private Leitungsnummer leitungsnummer = null;

        private AKJComboBox cbTyp = null;
        private AKJTextField tfLeitungsnummer = null;

        /**
         * @param leitungsnummer anzuzeigende Leitungsnummer (not null)
         */
        public LeitungsnummerDialog(Leitungsnummer leitungsnummer) {
            super("de/augustakom/hurrican/gui/auftrag/resources/LeitungsnummerPanel.xml");
            if (leitungsnummer == null) {
                this.leitungsnummer = new Leitungsnummer();
            }
            else {
                this.leitungsnummer = leitungsnummer;
            }
            createGUI();
            loadFieldValues();
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
         */
        @Override
        protected final void createGUI() {
            String title;
            if (leitungsnummer.getId() == null) {
                title = getSwingFactory().getText("dialog.title.create");
            }
            else {
                title = getSwingFactory().getText("dialog.title.change");
            }
            setTitle(title);

            AKJLabel lblTyp = getSwingFactory().createLabel("dialog.label.typ");
            cbTyp = getSwingFactory().createComboBox("dialog.combobox.typ");
            for (Leitungsnummer.Typ typ : Leitungsnummer.Typ.values()) {
                cbTyp.addItem(typ);
            }
            cbTyp.setPreferredSize(new Dimension(260, 20));

            AKJLabel lblLeitungsnummer = getSwingFactory().createLabel("dialog.label.leitungsnummer");
            tfLeitungsnummer = getSwingFactory().createTextField("dialog.textfield.leitungsnummer");

            AKJPanel child = new AKJPanel(new GridBagLayout());
            child.add(lblTyp, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(cbTyp, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(lblLeitungsnummer, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(tfLeitungsnummer, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

            getChildPanel().setLayout(new BorderLayout());
            getChildPanel().add(child, BorderLayout.CENTER);
        }

        /**
         * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
         */
        @Override
        protected void doSave() {
            try {
                retrieveFieldValues();
                if (StringUtils.isBlank(leitungsnummer.getLeitungsnummer())) {
                    throw new HurricanGUIException("Bitte geben Sie eine nicht leere Leitungsnummer an.");
                }
                getCCService(CCAuftragService.class).saveLeitungsnummer(leitungsnummer);

                prepare4Close();
                setValue(JOptionPane.OK_OPTION);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
         */
        @Override
        protected void execute(String command) {
            LOGGER.debug("execute");
        }

        /**
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        @Override
        public void update(Observable o, Object arg) {
            LOGGER.debug("update");
        }

        /**
         * Lädt die Daten des Objekts <code>leitungsnummer</code> für den Dialog
         */
        private void loadFieldValues() {
            cbTyp.setSelectedItem(leitungsnummer.getTyp());
            tfLeitungsnummer.setText(leitungsnummer.getLeitungsnummer());
        }

        /**
         * Überträgt die im Dialog eingegebenen Daten in das Objekt <code>leitungsnummer<code>
         */
        private void retrieveFieldValues() {
            leitungsnummer.setTyp((Typ) cbTyp.getSelectedItem());
            leitungsnummer.setLeitungsnummer(tfLeitungsnummer.getText());
        }
    }
}
