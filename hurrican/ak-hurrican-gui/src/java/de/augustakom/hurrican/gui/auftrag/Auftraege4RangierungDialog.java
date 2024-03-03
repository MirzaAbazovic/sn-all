/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2004 10:08:03
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Dialog fuer die Anzeige aller Auftraege, die einer best. Rangierung zugeordnet sind bzw. waren.
 *
 *
 */
public class Auftraege4RangierungDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(Auftraege4RangierungDialog.class);

    private Rangierung rangierung = null;
    private Auftrag4RangierungTableModel<AuftragEndstelleView> tbMdlAuftraege = null;

    /**
     * Konstruktor mit Angabe der Rangierung.
     *
     * @param rangierung
     */
    public Auftraege4RangierungDialog(Rangierung rangierung) {
        super(null, false);
        this.rangierung = rangierung;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        StringBuilder title = new StringBuilder("Bisherige Belegungen der Rangierung ");
        if (rangierung != null) {
            title.append(rangierung.getId());
        }
        setTitle(title.toString());

        configureButton(CMD_SAVE, "OK", "Schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        tbMdlAuftraege = new Auftrag4RangierungTableModel<AuftragEndstelleView>();
        AKJTable tbAuftraege = new AKJTable(tbMdlAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbAuftraege.attachSorter();
        tbAuftraege.fitTable(new int[] { 85, 70, 95, 80, 70, 125 });
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege);
        spTable.setPreferredSize(new Dimension(550, 150));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // Save-Button nicht validieren!
    }

    /* Laedt die Auftrags-Daten zur angegebenen Rangierung. */
    private void loadData() {
        if (this.rangierung != null) {
            setWaitCursor();

            final SwingWorker<List<AuftragEndstelleView>, Void> worker = new SwingWorker<List<AuftragEndstelleView>, Void>() {

                final Long rangierId = rangierung.getId();

                @Override
                protected List<AuftragEndstelleView> doInBackground() throws Exception {
                    AuftragEndstelleQuery query = new AuftragEndstelleQuery();
                    query.setRangierId(rangierId);

                    CCAuftragService as = getCCService(CCAuftragService.class);
                    List<AuftragEndstelleView> result = as.findAuftragEndstelleViews(query);
                    return result;
                }

                @Override
                protected void done() {
                    try {
                        tbMdlAuftraege.setData(get());
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

    /* TableModel fuer die Darstellung der Auftraege zu einer Rangierung. */
    static class Auftrag4RangierungTableModel<T> extends AKTableModel<T> {
        static final int COL_AUFTRAG_ID = 0;
        static final int COL_KUNDE__NO = 1;
        static final int COL_VBZ = 2;
        static final int COL_INBETRIEBNAHME = 3;
        static final int COL_KUENDIGUNG = 4;
        static final int COL_STATUS = 5;

        static final int COL_COUNT = 6;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_AUFTRAG_ID:
                    return "Auftrag-ID (CC)";
                case COL_KUNDE__NO:
                    return "Kunde__No";
                case COL_VBZ:
                    return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
                case COL_INBETRIEBNAHME:
                    return "Inbetriebnahme";
                case COL_KUENDIGUNG:
                    return "Kündigung";
                case COL_STATUS:
                    return "Status";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof AuftragEndstelleView) {
                AuftragEndstelleView view = (AuftragEndstelleView) o;
                switch (column) {
                    case COL_AUFTRAG_ID:
                        return view.getAuftragId();
                    case COL_KUNDE__NO:
                        return view.getKundeNo();
                    case COL_VBZ:
                        return view.getVbz();
                    case COL_INBETRIEBNAHME:
                        return view.getInbetriebnahme();
                    case COL_KUENDIGUNG:
                        return view.getKuendigung();
                    case COL_STATUS:
                        return view.getAuftragStatusText();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int ci) {
            switch (ci) {
                case COL_AUFTRAG_ID:
                    return Long.class;
                case COL_KUNDE__NO:
                    return Long.class;
                case COL_INBETRIEBNAHME:
                    return Date.class;
                case COL_KUENDIGUNG:
                    return Date.class;
                default:
                    return String.class;
            }
        }
    }
}


