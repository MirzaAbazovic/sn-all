/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2005 19:11:08
 */
package de.augustakom.hurrican.gui.auftrag;


import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.view.CuDAVorschau;
import de.augustakom.hurrican.service.cc.CarrierService;


/**
 * Dialog zur Ermittlung/Anzeige der CuDA-Vorschau.
 *
 *
 */
public class CuDAVorschauDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(CuDAVorschauDialog.class);
    private static final long serialVersionUID = 1899852553320057889L;

    private AKReflectionTableModel tbMdlCuDA = null;

    /**
     * Default-Konstruktor.
     */
    public CuDAVorschauDialog() {
        super(null, false);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("CuDA-Vorschau");
        configureButton(CMD_CANCEL, null, null, false, false);
        configureButton(CMD_SAVE, "Ok", null, true, true);

        tbMdlCuDA = new AKReflectionTableModel<CuDAVorschau>(
                new String[] { "Vorgabe AM", "Anzahl CuDAs" },
                new String[] { "vorgabeSCV", "anzahlCuDA" },
                new Class[] { Date.class, Integer.class });
        AKJTable tbCuDA = new AKJTable(tbMdlCuDA, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbCuDA.attachSorter();
        tbCuDA.fitTable(new int[] { 100, 100 });
        AKJScrollPane spTable = new AKJScrollPane(tbCuDA, new Dimension(225, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        setWaitCursor();
        final SwingWorker<List<CuDAVorschau>, Void> worker = new SwingWorker<List<CuDAVorschau>, Void>() {

            @Override
            protected List<CuDAVorschau> doInBackground() throws Exception {
                CarrierService cs = getCCService(CarrierService.class);
                return cs.createCuDAVorschau(new Date());
            }

            @Override
            protected void done() {
                try {
                    List<CuDAVorschau> views = get();

                    if ((views == null) || (views.isEmpty())) {
                        throw new HurricanGUIException(
                                "Es konnten keine CuDAs mit Vorgabe AM > heute ermittelt werden!");
                    }

                    tbMdlCuDA.setData(views);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                }
            }
        };
        worker.execute();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        prepare4Close();
        setValue(OK_OPTION);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


