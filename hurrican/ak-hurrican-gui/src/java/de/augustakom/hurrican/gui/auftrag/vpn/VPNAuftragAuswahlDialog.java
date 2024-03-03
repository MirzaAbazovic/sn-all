/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2004 08:38:58
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

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
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Dialog fuer die Auswahl eines Auftrags, der in ein VPN aufgenommen werden soll. <br> Die (CC-)ID des ausgewaehlten
 * Auftrags wird ueber die Methode <code>setValue</code> gespeichert und kann vom Aufrufer abgefragt werden.
 *
 *
 */
public class VPNAuftragAuswahlDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VPNAuftragAuswahlDialog.class);

    private Long hauptKundeNo = null;
    private AKJTable tbAuftraege = null;
    private AuftragAuswahlTableModel tbMdlAuftraege = null;

    /**
     * Konstruktor mit Angabe der Hauptkunden-No. Es werden alle Auftraege der Kunden angezeigt, die dem Hauptkunden mit
     * der No. <code>hauptKundeNo</code> zugeordnet sind. Ausserdem werden auch die Auftraege des Hauptkunden
     * angezeigt.
     *
     * @param hauptKundeNo
     */
    public VPNAuftragAuswahlDialog(Long hauptKundeNo) {
        super(null);
        this.hauptKundeNo = hauptKundeNo;
        createGUI();
        load();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Auftragsauswahl für VPN");
        configureButton(CMD_SAVE, "Übernehmen", "Übernimmt den selektierten Auftrag in das VPN", true, true);

        tbMdlAuftraege = new AuftragAuswahlTableModel();
        tbAuftraege = new AKJTable(tbMdlAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.attachSorter();
        tbAuftraege.fitTable(new int[] { 80, 80, 80, 80, 120, 100, 100, 100, 100, 100, 80 });
        tbAuftraege.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbAuftraege);
        spTable.setPreferredSize(new Dimension(800, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /* Laedt alle benoetigten Daten. */
    private void load() {
        if (this.hauptKundeNo != null) {
            setWaitCursor();
            final SwingWorker<List<AuftragEndstelleView>, Void> worker = new SwingWorker<List<AuftragEndstelleView>, Void>() {

                @Override
                protected List<AuftragEndstelleView> doInBackground() throws Exception {
                    // Kunden-No's ermitteln
                    KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
                    List<Long> kNos = ks.findKundeNos4HauptKunde(hauptKundeNo);
                    if (kNos == null) {
                        kNos = new ArrayList<Long>();
                    }
                    kNos.add(hauptKundeNo);

                    CCAuftragService ccAS = getCCService(CCAuftragService.class);
                    List<AuftragEndstelleView> auftraege =
                            ccAS.findAuftragEndstelleViews4VPN(null, kNos);
                    return auftraege;
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
        else {
            MessageHelper.showInfoDialog(this,
                    "Es wurde keine Kunden-No angegeben. Deshalb konnten keine Aufträge ermittelt werden, die in das VPN aufgenommen werden können.",
                    null, true);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#cancel()
     */
    @Override
    protected void cancel() {
        prepare4Close();
        setValue(null);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        AKMutableTableModel mdl = (AKMutableTableModel) tbAuftraege.getModel();
        Object selection = mdl.getDataAtRow(tbAuftraege.getSelectedRow());
        objectSelected(selection);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof AuftragEndstelleView) {
            if (getButton(CMD_SAVE).isEnabled()) {
                prepare4Close();
                setValue(((AuftragEndstelleView) selection).getAuftragId());
            }
        }
        else {
            MessageHelper.showInfoDialog(this,
                    "Wählen Sie bitte einen Auftrag aus, der dem VPN zugeordnet werden soll oder brechen Sie den Dialog ab.",
                    null, true);
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

    /* TableModel fuer die Darstellung der zur Auswahl stehenden Auftraege */
    static class AuftragAuswahlTableModel extends AKTableModel<AuftragEndstelleView> {
        private static final int COL_AUFTRAG_ID = 0;
        private static final int COL_VBZ = 1;
        private static final int COL_HAUPT_KUNDE_NO = 2;
        private static final int COL_KUNDE__NO = 3;
        private static final int COL_NAME = 4;
        private static final int COL_VORNAME = 5;
        private static final int COL_ANSCHLUSSART = 6;
        private static final int COL_ENDSTELLE = 7;
        private static final int COL_ES_NAME = 8;
        private static final int COL_ES_ORT = 9;
        private static final int COL_ORDER__NO = 10;

        private static final int COL_COUNT = 11;

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
                case COL_VBZ:
                    return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
                case COL_HAUPT_KUNDE_NO:
                    return "Hauptkunde-No";
                case COL_KUNDE__NO:
                    return "Kunde__No";
                case COL_NAME:
                    return "Name";
                case COL_VORNAME:
                    return "Vorname";
                case COL_ANSCHLUSSART:
                    return "Anschlussart";
                case COL_ENDSTELLE:
                    return "Endstelle";
                case COL_ES_NAME:
                    return "ES-Name";
                case COL_ES_ORT:
                    return "ES-Ort";
                case COL_ORDER__NO:
                    return "Order__No";
                default:
                    return "";
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
                    case COL_VBZ:
                        return view.getVbz();
                    case COL_HAUPT_KUNDE_NO:
                        return view.getHauptKundenNo();
                    case COL_KUNDE__NO:
                        return view.getKundeNo();
                    case COL_NAME:
                        return view.getName();
                    case COL_VORNAME:
                        return view.getVorname();
                    case COL_ANSCHLUSSART:
                        return view.getAnschlussart();
                    case COL_ENDSTELLE:
                        return view.getEndstelle();
                    case COL_ES_NAME:
                        return view.getEndstelleName();
                    case COL_ES_ORT:
                        return view.getEndstelleOrt();
                    case COL_ORDER__NO:
                        return view.getAuftragNoOrig();
                    default:
                        break;
                }
            }
            return super.getValueAt(row, column);
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
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_AUFTRAG_ID:
                    return Long.class;
                case COL_HAUPT_KUNDE_NO:
                    return Long.class;
                case COL_KUNDE__NO:
                    return Long.class;
                case COL_ORDER__NO:
                    return Long.class;
                default:
                    return String.class;
            }
        }
    }
}


