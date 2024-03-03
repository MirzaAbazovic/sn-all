/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.10.2009 09:42:53
 */

package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GewofagWohnung;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.GewofagService;

/**
 * Dialog zur Auswahl einer Gewofag-Wohnung
 *
 *
 */
public class ChooseGewofagWohnungDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, AKObjectSelectionListener {
    private static final Logger LOGGER = Logger.getLogger(ChooseGewofagWohnungDialog.class);

    // Dialog Data
    private final Endstelle endstelle;

    // GUI Elements
    private AKReflectionTableModel<GewofagWohnung> tbModelWohnungen;
    private AKJTable tbWohnungen;

    // Used Services
    private GewofagService gewofagService;
    private AvailabilityService availabilityService;


    /**
     * Konstruktor mit Angabe der Endstelle.
     *
     * @param endstelle
     */
    public ChooseGewofagWohnungDialog(Endstelle endstelle) {
        super("de/augustakom/hurrican/gui/auftrag/resources/ChooseGewofagWohnungDialog.xml");
        this.endstelle = endstelle;

        initServices();
        createGUI();
        loadData();
    }


    private void initServices() {
        try {
            availabilityService = getCCService(AvailabilityService.class);
            gewofagService = getCCService(GewofagService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, getSwingFactory().getText("ok"), getSwingFactory().getText("ok"), true, true);

        tbModelWohnungen = new AKReflectionTableModel<GewofagWohnung>(
                new String[] {
                        getSwingFactory().getText("name"), getSwingFactory().getText("strasse"), getSwingFactory().getText("hausnr"),
                        getSwingFactory().getText("etage"), getSwingFactory().getText("lage"), getSwingFactory().getText("tae") },
                new String[] {
                        GewofagWohnung.NAME, GewofagWohnung.GEO_ID + "." + GeoId.STREET, GewofagWohnung.GEO_ID + "." + GeoId.COMPLETE_HOUSENUM,
                        GewofagWohnung.ETAGE, GewofagWohnung.LAGE, GewofagWohnung.TAE },
                new Class[] {
                        String.class, String.class, String.class,
                        String.class, String.class, String.class }
        );
        tbWohnungen = new AKJTable(tbModelWohnungen, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbWohnungen.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbWohnungen.attachSorter();
        AKJScrollPane spWohnungen = new AKJScrollPane(tbWohnungen, new Dimension(800, 150));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(spWohnungen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }


    @Override
    public final void loadData() {
        try {
            if (endstelle.getGeoId() == null) {
                throw new HurricanGUIException("Endstelle ist keiner GeoID zugeordnet!");
            }
            GeoId geoId = availabilityService.findGeoId(endstelle.getGeoId());
            List<GewofagWohnung> wohnungen = gewofagService.findGewofagWohnungenByGeoId(geoId);
            tbModelWohnungen.setData(wohnungen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }


    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        doSave();
    }


    @Override
    protected void doSave() {
        Object selection = ((AKMutableTableModel) tbWohnungen.getModel()).getDataAtRow(tbWohnungen.getSelectedRow());
        if (selection instanceof GewofagWohnung) {
            prepare4Close();
            setValue(selection);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Es ist kein gültiger Datensatz selektiert.", null, true);
        }
    }


    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
        // do nothing
    }
}
