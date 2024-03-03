/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2011 10:22:50
 */

package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Admin-Panel fuer die Verwaltung von Technologietypen pro Standort
 */
public class HVTTechTypeAdminPanel extends AbstractAdminPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(HVTTechTypeAdminPanel.class);

    private static final String ACTION_DELETE_TECHTYPE = "action.delete.tech.type";
    private static final String ACTION_EDIT_TECHTYPE = "action.edit.tech.type";
    private static final String BTN_DELETE_TECH_TYPE = "btn.delete.tech.type";

    private DeleteTechTypeAction deleteAction;
    private EditTechTypeAction editAction;

    private HVTStandort hvtStandort = null;

    private AKReflectionTableModel<HVTStandortTechType> tbMdlTechType = null;
    private AKJTable tbTechType = null;

    public HVTTechTypeAdminPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTTechTypeAdminPanel.xml");
        createGUI();
    }

    @Override
    protected final void createGUI() {
        tbMdlTechType = new AKReflectionTableModel<HVTStandortTechType>(
                new String[] { "Technologietyp", "Verfügbar von", "Verfügbar bis" },
                new String[] { HVTStandortTechType.TECH_TYPE_NAME, HVTStandortTechType.AVAILABLE_FROM, HVTStandortTechType.AVAILABLE_TO },
                new Class[] { String.class, Date.class, Date.class });
        tbTechType = new AKJTable(tbMdlTechType, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbTechType.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbTechType.attachSorter();
        tbTechType.fitTable(new int[] { 120, 120, 120 });
        AKJScrollPane spTechType = new AKJScrollPane(tbTechType, new Dimension(400, 300));

        AKJButton btnDeleteTechType = getSwingFactory().createButton(BTN_DELETE_TECH_TYPE, getActionListener());
        btnDeleteTechType.setBorder(null);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        left.add(spTechType, GBCFactory.createGBC(100, 0, 1, 1, 1, 2, GridBagConstraints.HORIZONTAL));
        left.add(btnDeleteTechType, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 2, 3, 1, 1, GridBagConstraints.VERTICAL));

        editAction = new EditTechTypeAction();
        editAction.setParentClass(this.getClass());
        tbTechType.addPopupAction(editAction);

        deleteAction = new DeleteTechTypeAction();
        deleteAction.setParentClass(this.getClass());
        tbTechType.addPopupAction(deleteAction);

        manageGUI(editAction, deleteAction, btnDeleteTechType);

        this.setLayout(new BorderLayout());
        this.add(left, BorderLayout.WEST);
    }

    @Override
    protected void execute(String command) {
        if ((command != null) && StringUtils.equals(BTN_DELETE_TECH_TYPE, command)) {
            deleteAction.actionPerformed(null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void showDetails(Object details) {
        if (details instanceof HVTStandort) {
            try {
                hvtStandort = (HVTStandort) details;
                HVTService hvtService = getCCService(HVTService.class);

                List<HVTStandortTechType> hvtStandortTechTypes = hvtService.findTechTypes4HVTStandort(hvtStandort.getHvtIdStandort());
                tbMdlTechType.setData(hvtStandortTechTypes);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
        else {
            tbMdlTechType.setData(null);
        }
    }

    @Override
    public final void loadData() {
    }

    @Override
    public void createNew() {
        if (hvtStandort != null) {
            HVTTechTypeEditDialog dialog = new HVTTechTypeEditDialog(null, hvtStandort.getHvtIdStandort());
            Object result = DialogHelper.showDialog(getParent(), dialog, true, true);
            if ((result != null) && (result instanceof HVTStandortTechType)) {
                HVTStandortTechType hvtStandortTechType = (HVTStandortTechType) result;
                tbMdlTechType.addObject(hvtStandortTechType);
                tbMdlTechType.fireTableDataChanged();
            }
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
        }
    }

    @Override
    public void saveData() {
    }

    @Override
    public void objectSelected(Object selection) {
        if (editAction.isEnabled()) {
            editAction.actionPerformed(null);
        }
    }

    /**
     * Action, die Daten einer Baugruppe zu editieren.
     */
    class EditTechTypeAction extends AKAbstractAction {
        public EditTechTypeAction() {
            setName("Technologietyp editieren...");
            setTooltip("Oeffnet einen Dialog, um den aktuellen Technologietypen zu editieren");
            setActionCommand(ACTION_EDIT_TECHTYPE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                @SuppressWarnings("unchecked")
                Object tmp = ((AKMutableTableModel<HVTStandortTechType>) tbTechType.getModel()).getDataAtRow(tbTechType.getSelectedRow());
                if (tmp instanceof HVTStandortTechType) {
                    HVTTechTypeEditDialog dialog = new HVTTechTypeEditDialog((HVTStandortTechType) tmp, hvtStandort.getHvtIdStandort());
                    Object value = DialogHelper.showDialog(getParent(), dialog, true, true);

                    if (value instanceof HVTStandortTechType) {
                        HVTService hvtService = getCCService(HVTService.class);
                        List<HVTStandortTechType> hvtStandortTechTypes = hvtService.findTechTypes4HVTStandort(hvtStandort.getHvtIdStandort());
                        tbMdlTechType.setData(hvtStandortTechTypes);
                        tbMdlTechType.fireTableDataChanged();
                    }
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /**
     * Action, die Daten einer Baugruppe zu editieren.
     */
    class DeleteTechTypeAction extends AKAbstractAction {
        public DeleteTechTypeAction() {
            setName("Technologietyp löschen...");
            setTooltip("Löscht den selektierten Technologietypen");
            setActionCommand(ACTION_DELETE_TECHTYPE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int index = tbTechType.getSelectedRow();
                if (index != -1) {
                    @SuppressWarnings("unchecked")
                    Object tmp = ((AKMutableTableModel<HVTStandortTechType>) tbTechType.getModel()).getDataAtRow(index);
                    if (tmp instanceof HVTStandortTechType) {
                        HVTStandortTechType hvtStandortTechType = (HVTStandortTechType) tmp;
                        HVTService hvtService = getCCService(HVTService.class);
                        hvtService.deleteTechType(hvtStandortTechType);
                        List<HVTStandortTechType> hvtStandortTechTypes = hvtService.findTechTypes4HVTStandort(hvtStandort.getHvtIdStandort());
                        tbMdlTechType.setData(hvtStandortTechTypes);
                        tbMdlTechType.fireTableDataChanged();
                    }
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Technologietypen auswählen!");
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }
}
