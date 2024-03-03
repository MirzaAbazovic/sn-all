/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.04.2005 15:19:28
 */
package de.augustakom.hurrican.gui.hvt;

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
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.cc.MonitorService;


/**
 * Dialog zur Darstellung der Belegung eines UEVTs. <br> Die Anzeige erfolgt tabellarisch!!!
 *
 *
 */
public class UEVTBelegungDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(UEVTBelegungDialog.class);

    private String uevt = null;
    private Long hvtIdStandort = null;

    private AKReflectionTableModel<UevtCuDAView> tbMdlUevt = null;

    /**
     * Konstruktor mit Angabe des UEVTs, dessen Belegung dargestellt werden soll.
     *
     * @param uevt
     * @param hvtIdStandort
     */
    public UEVTBelegungDialog(String uevt, Long hvtIdStandort) {
        super(null);
        this.uevt = uevt;
        this.hvtIdStandort = hvtIdStandort;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("UEVT-Übersicht mit Belegung");
        configureButton(CMD_SAVE, "Ok", "Schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        tbMdlUevt = new AKReflectionTableModel<UevtCuDAView>(
                new String[] { "UEVT", "Rang-Leiste1", "Rang-Schnittstelle", "Rang-SS-Type", "definiert",
                        "vorbereitet", "rangiert", "freigegeben", "belegt" },
                new String[] { "uevt", "rangLeiste1", "cudaPhysik", "rangSSType", "cudaFreigegeben",
                        "cudaVorbereitet", "cudaRangiert", "cudaFrei", "cudaBelegt" },
                new Class[] { String.class, String.class, String.class, String.class, Integer.class,
                        Integer.class, Integer.class, Integer.class, Integer.class }
        );
        AKJTable tbUevt = new AKJTable(tbMdlUevt, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbUevt.attachSorter();
        tbUevt.fitTable(new int[] { 50, 50, 50, 60, 50, 50, 50, 50, 50 });

        AKJScrollPane spTable = new AKJScrollPane(tbUevt);
        spTable.setPreferredSize(new Dimension(485, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        setWaitCursor();
        final SwingWorker<List<UevtCuDAView>, Void> worker = new SwingWorker<List<UevtCuDAView>, Void>() {

            final String uevtInput = uevt;
            final Long hvtIdStandortInput = hvtIdStandort;

            @Override
            protected List<UevtCuDAView> doInBackground() throws Exception {
                MonitorService ms = getCCService(MonitorService.class);
                List<UevtCuDAView> views = ms.findViews4UevtBelegung(uevtInput, hvtIdStandortInput);
                return views;
            }

            @Override
            protected void done() {
                try {
                    List<UevtCuDAView> views = get();
                    if (views == null) {
                        throw new HurricanGUIException("Es konnten keine Belegungs-Details zu dem UEVT ermittelt werden.");
                    }
                    tbMdlUevt.setData(views);
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
        setValue(Integer.valueOf(OK_OPTION));
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


