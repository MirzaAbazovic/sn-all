/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2004 08:04:36
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragEndstelleTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Dialog zur Auswahl der Endstelle, die auf einen anderen Auftrag uebernommen werden soll. <br> Dem Aufrufer wird ueber
 * die Methode setValue(Object) - bzw. getValue() - mitgeteilt, welche Endstelle fuer die Uebernahme verwendet werden
 * soll. Das gespeicherte Objekt ist vom Typ <code>AuftragEndstelleView</code>!
 *
 *
 */
public class EndstellenSelectionDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(EndstellenSelectionDialog.class);

    // These fields are final since they are used by the SwingWorkers in the background thread!
    private final Long strategy;
    private final Long endstelleId;
    private final Long kundeNoOrig;

    private AKJTable tbSelection = null;
    private AuftragEndstelleTableModel tbMdlSelection = null;
    private String title = null;

    /**
     * Konstruktor.
     *
     * @param strategy    Ueber diesen Wert (Konstante aus RangierungsService) wird definiert, wie nach den Endstellen
     *                    gesucht werden soll.
     * @param endstelleId ID der Endstelle, der eine Physik eines anderen/alten Auftrags zugeordnet werden soll.
     */
    public EndstellenSelectionDialog(Long strategy, Long endstelleId) {
        super(null);
        this.strategy = strategy;
        this.endstelleId = endstelleId;
        this.kundeNoOrig = null;
        title = "Auswahl der zu übernehmenden Physik";
        createGUI();
        loadData();
    }

    /**
     * Konstruktor mit Angabe der (original) Kundennummer des Kunden, dessen Endstellen angezeigt werden sollen.
     *
     * @param kundeNoOrig
     */
    public EndstellenSelectionDialog(Long kundeNoOrig) {
        super(null);
        this.kundeNoOrig = kundeNoOrig;
        strategy = -1L;
        endstelleId = null;
        title = "Daten von Endstelle kopieren";
        createGUI();
        loadData4Kunde();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(title);
        configureButton(CMD_SAVE, "Übernehmen", "Übernimmt die gerade selektierte Endstelle bzw. Physik", true, true);

        tbMdlSelection = new AuftragEndstelleTableModel();
        tbSelection = new AKJTable(tbMdlSelection, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSelection.attachSorter();
        tbSelection.addMouseListener(new AKTableDoubleClickMouseListener(this));

        AKJScrollPane spTable = new AKJScrollPane(tbSelection);
        spTable.setPreferredSize(new Dimension(500, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // KEINE Ueberpruefung des Save-Buttons!
    }

    /* Laedt die Daten fuer die Auswahl. */
    private void loadData() {
        final AbstractEndstellenSelectionWorker worker = new AbstractEndstellenSelectionWorker() {

            @Override
            protected List<AuftragEndstelleView> doInBackground() throws Exception {
                List<AuftragEndstelleView> result = null;
                EndstellenService esSrv = getCCService(EndstellenService.class);
                if (PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME.equals(strategy)) {
                    result = esSrv.findEndstellen4Anschlussuebernahme(endstelleId);
                }
                else if (PhysikaenderungsTyp.STRATEGY_WANDEL_ANALOG_ISDN.equals(strategy)) {
                    result = esSrv.findEndstellen4Wandel(endstelleId, Boolean.TRUE);
                }
                else if (PhysikaenderungsTyp.STRATEGY_WANDEL_ISDN_ANALOG.equals(strategy)) {
                    result = esSrv.findEndstellen4Wandel(endstelleId, Boolean.FALSE);
                }
                else if (PhysikaenderungsTyp.STRATEGY_BANDBREITENAENDERUNG.equals(strategy)) {
                    result = esSrv.findEndstellen4Bandbreite(endstelleId);
                }
                else if (PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG.equals(strategy)) {
                    result = esSrv.findEndstellen4Wandel(endstelleId, null);
                }
                else {
                    MessageHelper.showMessageDialog(getMainFrame(), "Aenderungstyp wird nicht unterstuetzt!");
                }
                return result;
            }
        };
        worker.execute();
    }

    /* Laedt alle Endstellen eines best. Kunden. */
    private void loadData4Kunde() {
        final AbstractEndstellenSelectionWorker worker = new AbstractEndstellenSelectionWorker() {

            @Override
            protected List<AuftragEndstelleView> doInBackground() throws Exception {
                AuftragEndstelleQuery query = new AuftragEndstelleQuery();
                List<Long> kNo = new ArrayList<Long>();
                kNo.add(kundeNoOrig);
                query.setKundeNos(kNo);

                CCAuftragService as = getCCService(CCAuftragService.class);
                List<AuftragEndstelleView> result = as.findAuftragEndstelleViews(query);
                return result;
            }
        };
        worker.execute();
    }

    abstract class AbstractEndstellenSelectionWorker extends SwingWorker<List<AuftragEndstelleView>, Void> {

        public AbstractEndstellenSelectionWorker() {
            setWaitCursor();
            getButton(CMD_SAVE).setEnabled(false);
        }

        @Override
        protected void done() {
            try {
                List<AuftragEndstelleView> result = get();

                tbMdlSelection.setData(result);
                getButton(CMD_SAVE).setEnabled(true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }


    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        doSave();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        Object selection = ((AKMutableTableModel) tbSelection.getModel()).getDataAtRow(tbSelection.getSelectedRow());
        if (selection instanceof AuftragEndstelleView) {
            prepare4Close();
            setValue(selection);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Es ist kein gültiger Datensatz selektiert.", null, true);
        }
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


