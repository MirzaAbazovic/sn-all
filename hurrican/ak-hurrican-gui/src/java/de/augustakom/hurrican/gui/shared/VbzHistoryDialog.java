/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2005 08:27:37
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.view.VerbindungsBezeichnungHistoryView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Dialog zur Anzeige einer VerbindungsBezeichnung-History (alle Auftraege inkl. Status, die einer best.
 * VerbindungsBezeichnung einmal zugeordnet waren oder immer noch sind).
 *
 *
 */
public class VbzHistoryDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VbzHistoryDialog.class);
    private static final long serialVersionUID = 9118886154419580775L;

    private String vbz = null;

    private AKReflectionTableModel<VerbindungsBezeichnungHistoryView> tbMdlHistory = null;

    /**
     * Konstruktor mit Angabe der VerbindungsBezeichnung deren History geladen werden soll.
     *
     * @param verbindungsBezeichnung
     * @throws IllegalArgumentException
     */
    public VbzHistoryDialog(String vbz) {
        super(null);
        this.vbz = vbz;
        if (StringUtils.isBlank(this.vbz)) {
            throw new IllegalArgumentException("Es muss eine Verbindungsbezeichnung angegeben werden!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("History zu Verbindungsbezeichnung " + vbz);
        configureButton(CMD_SAVE, null, null, false, false);
        configureButton(CMD_CANCEL, "Ok", "Schliesst den Dialog", true, true);

        tbMdlHistory = new AKReflectionTableModel<>(
                new String[] { "Auftrag-ID", "Produkt", "Status", "Inbetriebn.", "Kündigung",
                        "Vorgabe AM", "ES-B", "ES-B Name" },
                new String[] { "auftragId", "produkt", "statusText", "inbetriebnahme", "kuendigung",
                        "vorgabeSCV", "endstelleB", "endstelleBName" },
                new Class[] { Long.class, String.class, String.class, Date.class, Date.class,
                        Date.class, String.class, String.class }
        );
        AKJTable tbHistory = new AKJTable(tbMdlHistory, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbHistory.fitTable(new int[] { 70, 100, 90, 70, 70, 70, 100, 80 });
        tbHistory.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbHistory);
        spTable.setPreferredSize(new Dimension(675, 240));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        final SwingWorker<List<VerbindungsBezeichnungHistoryView>, Void> worker = new SwingWorker<List<VerbindungsBezeichnungHistoryView>, Void>() {
            final String localVbz = vbz;

            @Override
            protected List<VerbindungsBezeichnungHistoryView> doInBackground() throws Exception {
                PhysikService ps = getCCService(PhysikService.class);
                return ps.findVerbindungsBezeichnungHistory(localVbz);
            }

            @Override
            protected void done() {
                try {
                    List<VerbindungsBezeichnungHistoryView> views = get();
                    tbMdlHistory.setData(views);
                    if ((views == null) || (views.isEmpty())) {
                        MessageHelper.showInfoDialog(getMainFrame(), "Es wurden keine Daten zu der Verbindungsbezeichnung gefunden!", null, true);
                    }
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
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            prepare4Close();
            setValue(OK_OPTION);

            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


