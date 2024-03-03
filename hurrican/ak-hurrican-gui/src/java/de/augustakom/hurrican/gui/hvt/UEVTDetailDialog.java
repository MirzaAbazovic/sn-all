/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.04.2005 08:42:25
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
 * Zeigt die CuDA-Zahlen fuer einen best. UEVT und einer best. CuDA-Physik an. Die Anzeige ist gruppiert nach
 * RANG_SS_TYPE.
 *
 *
 */
public class UEVTDetailDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(UEVTDetailDialog.class);

    private String uevt = null;
    private String cudaPhysik = null;
    private Long hvtIdStandort = null;

    private AKReflectionTableModel<UevtCuDAView> tbMdlDetail = null;

    /**
     * Konstruktor mit Angabe von UEVT-Daten des UEVTs, dessen Details angezeigt werden sollen.
     *
     * @param uevt
     * @param cudaPhysik
     * @param hvtIdStd
     */
    public UEVTDetailDialog(String uevt, String cudaPhysik, Long hvtIdStd) {
        super(null);
        this.uevt = uevt;
        this.cudaPhysik = cudaPhysik;
        this.hvtIdStandort = hvtIdStd;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Detail zu UEVT mit Schnittstelle");
        configureButton(CMD_SAVE, "Ok", "Schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        tbMdlDetail = new AKReflectionTableModel<UevtCuDAView>(
                new String[] { "UEVT", "Schnittstelle", "Rang-SS-Type", "definiert", "rangiert", "belegt" },
                new String[] { "uevt", "cudaPhysik", "rangSSType", "cudaFrei", "cudaRangiert", "cudaBelegt" },
                new Class[] { String.class, String.class, String.class, Integer.class, Integer.class, Integer.class });
        AKJTable tbDetail = new AKJTable(tbMdlDetail, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbDetail.attachSorter();
        tbDetail.fitTable(new int[] { 50, 60, 60, 60, 60, 60 });

        AKJScrollPane spTable = new AKJScrollPane(tbDetail);
        spTable.setPreferredSize(new Dimension(370, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        final javax.swing.SwingWorker<List<UevtCuDAView>, Void> worker = new javax.swing.SwingWorker<List<UevtCuDAView>, Void>() {
            @Override
            protected List<UevtCuDAView> doInBackground() throws Exception {
                MonitorService ms = getCCService(MonitorService.class);
                return ms.findViewsGroupedByRangSSType(uevt, cudaPhysik, hvtIdStandort);
            }

            @Override
            protected void done() {
                try {
                    List<UevtCuDAView> views = get();
                    if (views == null) {
                        throw new HurricanGUIException("Es konnten keine Details zum UEVT ermittelt werden!");
                    }
                    tbMdlDetail.setData(views);
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
        setWaitCursor();
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


