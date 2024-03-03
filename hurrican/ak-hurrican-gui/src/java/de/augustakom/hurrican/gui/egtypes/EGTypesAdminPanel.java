/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2011 12:39:53
 */
package de.augustakom.hurrican.gui.egtypes;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.*;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel fuer die Administration der Endgeraetetypen.
 */
public class EGTypesAdminPanel extends AbstractAdminPanel implements AKDataLoaderComponent, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(EGTypesAdminPanel.class);

    private static final String WATCH_EG_TYPE_DATA = "watch.eg.type.data";
    private static final String TF_MODELL = "tf.eg.type.modell";
    private static final String TF_HERSTELLER = "tf.eg.type.hersteller";
    private static final long serialVersionUID = 7119263106678958375L;

    private AKReflectionTableModel<EGType> tbMdlEGTypes = null;
    private AKJTable tbEGTypes = null;

    private AKJTextField tfHersteller = null;
    private AKJTextField tfModell = null;

    private EGType newEGType = null;
    private int prevSelectedRow;
    private EGType2HWSwitchPanel egType2HWSwitchPanel;

    public EGTypesAdminPanel() {
        super("de/augustakom/hurrican/gui/egtypes/resources/EGTypesAdminPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * Converts a List of {@link HWSwitch}es to a String combination with "," as separator.
     */
    private static String convertHwSwitchList(List<HWSwitch> hwSwitchList) {
        if (hwSwitchList == null || hwSwitchList.isEmpty()) {
            return null;
        }
        return hwSwitchList.stream().filter(Objects::nonNull).map(HWSwitch::getName).collect(Collectors.joining(","));

    }

    @Override
    protected final void createGUI() {
        tbMdlEGTypes = new AKReflectionTableModel<>(
                new String[]{"ID", "Hersteller", "Modell"},
                new String[]{"id", "hersteller", "modell"},
                new Class[]{Long.class, String.class, String.class});
        tbEGTypes = new AKJTable(tbMdlEGTypes, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbEGTypes.attachSorter();
        tbEGTypes.addMouseListener(getTableListener());
        tbEGTypes.fitTable(new int[]{60, 150, 150});

        AKJLabel lblHersteller = getSwingFactory().createLabel(TF_HERSTELLER);
        AKJLabel lblModell = getSwingFactory().createLabel(TF_MODELL);
        tfHersteller = getSwingFactory().createTextField(TF_HERSTELLER);
        tfModell = getSwingFactory().createTextField(TF_MODELL);

        AKJScrollPane spEGTypeTable = new AKJScrollPane(tbEGTypes, new Dimension(400, 150));
        AKJPanel egTypePanel = new AKJPanel(new BorderLayout(), getSwingFactory().getText("border.eg.type.caption"));
        egTypePanel.add(spEGTypeTable, BorderLayout.CENTER);

        egType2HWSwitchPanel = new EGType2HWSwitchPanel();
        // @formatter:off
        AKJPanel detailPanel = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("border.details.caption"));
        detailPanel.add(new AKJPanel(),       GBCFactory.createGBC(0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        detailPanel.add(lblHersteller,        GBCFactory.createGBC(0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(),       GBCFactory.createGBC(0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(tfHersteller,         GBCFactory.createGBC(70, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(lblModell,            GBCFactory.createGBC(0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(),       GBCFactory.createGBC(0,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(tfModell,             GBCFactory.createGBC(70, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(),       GBCFactory.createGBC(0,  0, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        detailPanel.add(egType2HWSwitchPanel, GBCFactory.createGBC(100,0, 0, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(),       GBCFactory.createGBC(0,  0, 0, 5, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(egTypePanel,                 GBCFactory.createGBC(100,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(detailPanel,                 GBCFactory.createGBC(100,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(),              GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on
    }

    @Override
    public final void loadData() {
        try {
            clearDetails();
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            List<EGType> egTypes = endgeraeteService.findAllEGTypes();
            tbMdlEGTypes.setData(egTypes);
            enableAllDetails(false);
            addObjectToWatch(WATCH_EG_TYPE_DATA, createEGTypeWatchObject());
            prevSelectedRow = -1;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private boolean isDataUnsaved() {
        return hasChanged(WATCH_EG_TYPE_DATA, createEGTypeWatchObject());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (newEGType == null) {
            update4ExistingData();
        } else {
            update4NewData();
        }
    }

    private void update4ExistingData() {
        if (isDataUnsaved() && saveQuestion() == JOptionPane.YES_OPTION) {
            try {
                saveExistingData(getSelectedEGTypeRow());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        loadData();
    }

    private void update4NewData() {
        if (isDataUnsaved()) {
            if (saveQuestion() == JOptionPane.YES_OPTION) {
                try {
                    saveNewData();
                } catch (Exception e) {
                    loadData();
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
        } else {
            loadData();
        }
    }

    @Override
    public void showDetails(Object details) {
        if (details instanceof EGType) {
            EGType egType = (EGType) details;
            if (newEGType == null) {
                showDetails4ExistingData(egType);
            } else {
                showDetails4NewData(egType);
            }
            prevSelectedRow = tbEGTypes.getSelectedRow();
        }
    }

    private void showDetails4ExistingData(EGType egType) {
        if (isDataUnsaved() && saveQuestion() == JOptionPane.YES_OPTION) {
            try {
                saveExistingData(getEGTypeAtRow(prevSelectedRow));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        showDetails(egType);
        enableAllDetails(true);
    }

    private void showDetails4NewData(EGType egType) {
        if (isDataUnsaved() && saveQuestion() == JOptionPane.YES_OPTION) {
            MessageHelper.showInfoDialog(getMainFrame(), "Selektion geht verloren, da zunächst der neue"
                    + " Endgerätetyp gespeichert werden muss.");
            try {
                saveNewData();
            } catch (Exception e) {
                tbEGTypes.clearSelection();
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            return;
        }
        newEGType = null;
        showDetails(egType);
        enableAllDetails(true);
    }

    private void showDetails(EGType egType) {
        clearDetails();
        tfHersteller.setText(egType.getHersteller());
        tfModell.setText(egType.getModell());
        egType2HWSwitchPanel.setModel(egType);
        addObjectToWatch(WATCH_EG_TYPE_DATA, createEGTypeWatchObject());
    }

    @Override
    public void createNew() {
        if (isDataUnsaved() && saveQuestion() == JOptionPane.YES_OPTION) {
            saveData();
            if (newEGType != null) {
                return;
            }
        }
        newEGType = new EGType();
        tbEGTypes.clearSelection();
        enable4CreateNew();
        clearDetails();
        addObjectToWatch(WATCH_EG_TYPE_DATA, createEGTypeWatchObject());
    }

    @Override
    public void saveData() {
        try {
            if (newEGType != null) {
                saveNewData();
            } else {
                EGType egType = getSelectedEGTypeRow();
                if (egType == null) {
                    throw new StoreException("Kein Endgerätetyp zum Speichern ausgewählt!");
                }
                saveExistingData(egType);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void saveExistingData(EGType egType) throws ServiceNotFoundException, StoreException {
        if (egType == null) {
            return;
        }
        EGType origin = new EGType();
        copyEGTypeValues(origin, egType);
        try {
            EndgeraeteService endgeraeteService;
            endgeraeteService = getCCService(EndgeraeteService.class);
            setEGTypeValues(egType);
            validateEGType(egType);
            endgeraeteService.saveEGType(egType, HurricanSystemRegistry.instance().getSessionId());
            addObjectToWatch(WATCH_EG_TYPE_DATA, createEGTypeWatchObject());
            tbEGTypes.repaint();
        } catch (ServiceNotFoundException | StoreException e) {
            copyEGTypeValues(egType, origin);
            throw e;
        } catch (Exception e) {
            copyEGTypeValues(egType, origin);
            throw new StoreException("Endgerätetyp konnte nicht gespeichert werden!", e);
        }
    }

    private void saveNewData() throws StoreException, ServiceNotFoundException {
        setEGTypeValues(newEGType);
        validateEGType(newEGType);
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        endgeraeteService.saveEGType(newEGType, HurricanSystemRegistry.instance().getSessionId());
        newEGType = null;
        loadData();
    }

    private void validateEGType(EGType egType) throws StoreException {
        if (egType == null) {
            throw new StoreException("Endgerätetyp ist nicht definiert!");
        }
        if ((StringUtils.isBlank(egType.getHersteller())) || (StringUtils.isBlank(egType.getModell()))) {
            throw new StoreException("Hersteller und Modell dürfen nicht leer sein!");
        }

        EGType checkEGType = null;
        try {
            EndgeraeteService endgeraeteService;
            endgeraeteService = getCCService(EndgeraeteService.class);
            checkEGType = endgeraeteService.findEGTypeByHerstellerAndModell(egType.getHersteller(), egType.getModell());
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            // Nothing to do
        }
        if (checkEGType != null && equalSwitches(egType.getOrderedCertifiedSwitches(), checkEGType.getOrderedCertifiedSwitches())) {
            throw new StoreException("Ein Endgerätetyp mit gleichem Hersteller und Modell existiert bereits!");
        }
    }

    /**
     * Compares to lists of {@link HWSwitch}, if there is any kind of change (elemnts, sorting).
     */
    private boolean equalSwitches(List<HWSwitch> o1, List<HWSwitch> o2) {
        return StringUtils.equals(convertHwSwitchList(o1), convertHwSwitchList(o2));

    }

    @Override
    protected void execute(String command) {
        //do nothing
    }

    private void setEGTypeValues(EGType egType) {
        if (egType == null) {
            return;
        }
        egType.setHersteller(tfHersteller.getText(null));
        egType.setModell(tfModell.getText(null));
        egType.setOrderedCertifiedSwitches(getCertfiedSwitches());
    }

    private void copyEGTypeValues(EGType target, EGType source) {
        if ((target == null) || (source == null)) {
            return;
        }
        target.setHersteller(source.getHersteller());
        target.setModell(source.getModell());
        target.setOrderedCertifiedSwitches(source.getOrderedCertifiedSwitches());
    }

    private EGType getSelectedEGTypeRow() {
        if (tbEGTypes.getSelectedRow() == -1) {
            return null;
        }
        return getEGTypeAtRow(tbEGTypes.getSelectedRow());
    }

    private EGType getEGTypeAtRow(int row) {
        if (row == -1) {
            return null;
        }
        @SuppressWarnings("unchecked")
        AKMutableTableModel<EGType> tableModel =
                (AKMutableTableModel<EGType>) tbEGTypes.getModel();
        return tableModel.getDataAtRow(row);
    }

    private void clearDetails() {
        tfHersteller.setText("");
        tfModell.setText("");
        egType2HWSwitchPanel.setModel(null);
    }

    private void enableAllDetails(boolean enabled) {
        enableEGType(enabled);
    }

    private void enable4CreateNew() {
        enableEGType(true);
    }

    private void enableEGType(boolean enabled) {
        tfModell.setEnabled(enabled);
        tfHersteller.setEnabled(enabled);
        egType2HWSwitchPanel.setEnabled(enabled);
    }

    private EGTypeWatchObject createEGTypeWatchObject() {
        EGTypeWatchObject egTypeWatchObject = new EGTypeWatchObject();
        egTypeWatchObject.setHersteller(tfHersteller.getText(null));
        egTypeWatchObject.setModell(tfModell.getText(null));
        egTypeWatchObject.setCertifiedSwitches(convertHwSwitchList(getCertfiedSwitches()));
        return egTypeWatchObject;
    }

    /**
     * @return the curent valueof certified switches of the model {@link #egType2HWSwitchPanel}
     */
    private List<HWSwitch> getCertfiedSwitches() {
        if (egType2HWSwitchPanel != null) {
            return egType2HWSwitchPanel.getSelectionOfCertifiedSwitches();
        }
        return new ArrayList<>();
    }
}
