/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2011 10:15:57
 */

package de.augustakom.hurrican.gui.geoid;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.hvt.HVTGruppeDialog;
import de.augustakom.hurrican.gui.shared.GeoId2TechLocationEditDialog;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Panel fuer die Administration der GeoId Konfigurationen.
 */
public class GeoIdAdminPanel extends AbstractServicePanel implements AKDataLoaderComponent, AKObjectSelectionListener, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(GeoIdAdminPanel.class);

    private static final String CMD_NEW = "btn.new";
    private static final String CMD_EDIT = "btn.edit";
    private static final String CMD_DELETE = "btn.delete";

    private AKTableModelXML<GeoId2TechLocationView> tbMdlAdminView = null;
    private AKJTable tbAdminView = null;

    private EditTechLocationAction editTechLocationAction = null;

    private Long geoId = null;

    public GeoIdAdminPanel() {
        super("de/augustakom/hurrican/gui/geoid/resources/GeoIdAdminPanel.xml");
        createGUI();
    }

    @Override
    protected final void createGUI() {
        Dimension btnDefaultDim = new Dimension(100, 25);
        AKJButton btnNew = getSwingFactory().createButton(CMD_NEW, getActionListener());
        btnNew.setPreferredSize(btnDefaultDim);
        AKJButton btnEdit = getSwingFactory().createButton(CMD_EDIT, getActionListener());
        btnEdit.setPreferredSize(btnDefaultDim);
        AKJButton btnDelete = getSwingFactory().createButton(CMD_DELETE, getActionListener());
        btnDelete.setPreferredSize(btnDefaultDim);

        tbMdlAdminView = new AKTableModelXML<GeoId2TechLocationView>(
                "de/augustakom/hurrican/gui/geoid/resources/GeoIdAdminPanelTableModel.xml");
        tbAdminView = new AKJTable(tbMdlAdminView, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAdminView.attachSorter();
        tbAdminView.addTableListener(this);
        tbAdminView.fitTable(tbMdlAdminView.getFitList());
        tbAdminView.addMouseListener(new AKTableDoubleClickMouseListener(this));
        editTechLocationAction = new EditTechLocationAction();
        editTechLocationAction.setParentClass(this.getClass());
        DeleteTechLocationAction deleteTechLocationAction = new DeleteTechLocationAction();
        deleteTechLocationAction.setParentClass(this.getClass());
        ShowHVTGruppeAction showHVTGruppeAction = new ShowHVTGruppeAction();
        showHVTGruppeAction.setParentClass(this.getClass());
        tbAdminView.addPopupAction(showHVTGruppeAction);
        tbAdminView.addPopupAction(editTechLocationAction);
        tbAdminView.addPopupAction(deleteTechLocationAction);
        AKJScrollPane spSearchResult = new AKJScrollPane(tbAdminView, new Dimension(500, 190));

        AKJPanel buttonPanel = new AKJPanel(new GridBagLayout());
        buttonPanel.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        buttonPanel.add(btnEdit, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        buttonPanel.add(btnDelete, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.tech.locations")));
        this.add(spSearchResult, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(buttonPanel, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        AKManageableComponent[] managedComponents = new AKManageableComponent[] { btnNew, btnEdit, btnDelete,
                editTechLocationAction, deleteTechLocationAction };
        manageGUI(managedComponents);

    }

    @Override
    protected void execute(String command) {
        if (CMD_NEW.equals(command)) {
            createTechLocation();
        }
        else if (CMD_EDIT.equals(command)) {
            editTechLocation();
        }
        else if (CMD_DELETE.equals(command)) {
            deleteTechLocation();
        }
    }

    private void deleteTechLocation() {
        if ((geoId != null) && (tbAdminView.getSelectedRow() != -1)) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<GeoId2TechLocationView> tbMdl = (AKMutableTableModel<GeoId2TechLocationView>) tbAdminView.getModel();
                GeoId2TechLocationView row = tbMdl.getDataAtRow(tbAdminView.getSelectedRow());
                if (row != null) {
                    int choose = MessageHelper.showConfirmDialog(this, String.format("Soll der Standort %s unwiderruflich "
                                    + "gelöscht werden?", row.getStandort()), "Standort löschen",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                    );
                    if (choose == JOptionPane.YES_OPTION) {
                        AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                        availabilityService.deleteGeoId2TechLocationView(row);
                        loadData();
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void createTechLocation() {
        if (geoId != null) {
            try {
                GeoId2TechLocationEditDialog dlg = new GeoId2TechLocationEditDialog(null);
                DialogHelper.showDialog(this, dlg, true, true);
                if (dlg.getValue() instanceof GeoId2TechLocation) {
                    GeoId2TechLocation toSave = (GeoId2TechLocation) dlg.getValue();
                    toSave.setGeoId(geoId);
                    AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                    availabilityService.saveGeoId2TechLocation(toSave, HurricanSystemRegistry.instance().getSessionId());
                    loadData();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void editTechLocation() {
        if ((geoId != null) && (tbAdminView.getSelectedRow() != -1)) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<GeoId2TechLocationView> tbMdl = (AKMutableTableModel<GeoId2TechLocationView>) tbAdminView.getModel();
                GeoId2TechLocationView row = tbMdl.getDataAtRow(tbAdminView.getSelectedRow());
                if (row != null) {
                    GeoId2TechLocationEditDialog dlg = new GeoId2TechLocationEditDialog(row);
                    DialogHelper.showDialog(this, dlg, true, true);
                    if (dlg.getValue() instanceof GeoId2TechLocation) {
                        GeoId2TechLocation toSave = (GeoId2TechLocation) dlg.getValue();
                        AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                        availabilityService.saveGeoId2TechLocation(toSave, HurricanSystemRegistry.instance().getSessionId());
                        loadData();
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void showHVTGruppe() {
        if ((geoId != null) && (tbAdminView.getSelectedRow() != -1)) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<GeoId2TechLocationView> tbMdl = (AKMutableTableModel<GeoId2TechLocationView>) tbAdminView.getModel();
                GeoId2TechLocationView row = tbMdl.getDataAtRow(tbAdminView.getSelectedRow());
                if (row != null) {
                    HVTService hvtService = getCCService(HVTService.class);
                    HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(row.getHvtIdStandort());
                    HVTGruppeDialog dlg = new HVTGruppeDialog();
                    dlg.setModel(hvtGruppe);
                    DialogHelper.showDialog(this, dlg, true, true);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if ((arg == null) || (arg instanceof Long)) {
            geoId = (arg != null) ? (Long) arg : null;
            loadData();
        }
    }

    @Override
    public void showDetails(Object details) {
    }

    @Override
    public final void loadData() {
        if (geoId == null) {
            tbMdlAdminView.setData(null);
        }
        else {
            try {
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                List<GeoId2TechLocationView> results = availabilityService.findGeoId2TechLocationViews(geoId);
                tbMdlAdminView.setData(results);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public void objectSelected(Object selection) {
        if ((selection instanceof GeoId2TechLocationView) && editTechLocationAction.isComponentExecutable()) {
            editTechLocation();
        }
    }

    /* Action, um den selektierte Standort zu bearbeiten. */
    class EditTechLocationAction extends AKAbstractAction {
        public EditTechLocationAction() {
            super();
            setName("Standort bearbeiten");
            setActionCommand("edit.location.action");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            editTechLocation();
        }
    }

    /* Action, um den selektierte Standort zu löschen. */
    class DeleteTechLocationAction extends AKAbstractAction {
        public DeleteTechLocationAction() {
            super();
            setName("Standort löschen");
            setActionCommand("delete.location.action");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTechLocation();
        }
    }

    /* Action, um den selektierte Standort zu bearbeiten. */
    class ShowHVTGruppeAction extends AKAbstractAction {
        public ShowHVTGruppeAction() {
            super();
            setName("HVT-Details anzeigen");
            setActionCommand("hvt.gruppe.action");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showHVTGruppe();
        }
    }

}
