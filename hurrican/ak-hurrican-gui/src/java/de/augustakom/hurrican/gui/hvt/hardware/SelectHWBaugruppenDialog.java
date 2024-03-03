/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2010 08:01:56
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Dialog, um eine HW-Baugruppe von einem bestimmten HVT auszuwaehlen. Der Dialog speichert die ausgewaehlte(n)
 * Baugruppe(n) ueber die Methode setValue(..)
 */
public class SelectHWBaugruppenDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(SelectHWBaugruppenDialog.class);

    private final Long hvtIdStandort;
    private final boolean acceptMultipleSelection;

    // GUI
    private AKJTable tbBaugruppen;
    private AKReflectionTableModel<HWBaugruppeView> tbMdlBaugruppen;

    /**
     * Konstruktor mit Angabe des HVT-Standorts, von dem die Baugruppen angezeigt werden sollen.
     *
     * @param hvtIdStandort
     */
    public SelectHWBaugruppenDialog(Long hvtIdStandort, boolean acceptMultipleSelection) {
        super("de/augustakom/hurrican/gui/hvt/hardware/resources/SelectHWBaugruppenDialog.xml");
        this.hvtIdStandort = hvtIdStandort;
        this.acceptMultipleSelection = acceptMultipleSelection;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        tbMdlBaugruppen = new AKReflectionTableModel<HWBaugruppeView>(
                HWBaugruppeView.TABLE_COLUMN_NAMES, HWBaugruppeView.TABLE_PROPERTY_NAMES, HWBaugruppeView.TABLE_CLASS_TYPES);
        int selectionMode = (acceptMultipleSelection) ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION;
        tbBaugruppen = new AKJTable(tbMdlBaugruppen, AKJTable.AUTO_RESIZE_OFF, selectionMode);
        tbBaugruppen.attachSorter();
        tbBaugruppen.fitTable(HWBaugruppeView.TABLE_FIT);
        AKJScrollPane spBaugruppen = new AKJScrollPane(tbBaugruppen, new Dimension(400, 400));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spBaugruppen, BorderLayout.CENTER);

        setPreferredSize(new Dimension(800, 420));
    }

    @Override
    public final void loadData() {
        try {
            tbMdlBaugruppen.removeAll();

            HWService hwService = getCCService(HWService.class);
            List<HWBaugruppeView> hwBaugruppenViews = hwService.findHWBaugruppenViews(hvtIdStandort);
            tbMdlBaugruppen.setData(hwBaugruppenViews);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            List<HWBaugruppeView> selectedBGs = tbBaugruppen.getTableSelectionAsList(HWBaugruppeView.class);
            if (CollectionTools.isNotEmpty(selectedBGs)) {
                prepare4Close();
                setValue(selectedBGs);
            }
            else {
                throw new HurricanGUIException("Bitte wählen Sie min. eine Baugruppe aus.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


