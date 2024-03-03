/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2005 11:17:08
 */
package de.augustakom.hurrican.gui.auftrag.billing;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.auftrag.AbstractAuftragPanel;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Panel zur Darstellung der eingetragenen Billing-Leistungen zu einem Auftrag. <br> In dem Panel koennen die Leistungen
 * ueber die Gueltigkeit gefiltert werden.
 *
 *
 */
public class BillingAuftragLeistungenPanel extends AbstractAuftragPanel {

    private static final Logger LOGGER = Logger.getLogger(BillingAuftragLeistungenPanel.class);

    // GUI-Elemente
    private AKJCheckBox cbActual = null;
    private AKJCheckBox cbNew = null;
    private AKJCheckBox cbOld = null;
    private AKReflectionTableModel<BAuftragLeistungView> tbMdlBillLeistungen = null;
    private TechLeistungTableModel tbMdlTechLeistungen = null;

    // Modelle
    private CCAuftragModel auftragModel = null;
    private List<BAuftragLeistungView> leistungen = null;
    private static Map<Long, TechLeistung> techLeistungsMap = null;

    /**
     * Default-Konstruktor.
     */
    public BillingAuftragLeistungenPanel() {
        super("de/augustakom/hurrican/gui/auftrag/billing/resources/BillingAuftragLeistungenPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblFilter = getSwingFactory().createLabel("filter", AKJLabel.LEFT, Font.BOLD);
        cbActual = getSwingFactory().createCheckBox("actual", getActionListener(), true);
        cbNew = getSwingFactory().createCheckBox("new", getActionListener(), true);
        cbOld = getSwingFactory().createCheckBox("old", getActionListener(), false);

        AKJPanel north = new AKJPanel(new GridBagLayout());
        north.add(lblFilter, GBCFactory.createGBC(0, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        north.add(cbActual, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(cbNew, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(cbOld, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlBillLeistungen = new AKReflectionTableModel<BAuftragLeistungView>(
                new String[] { "Gültig von", "Gültig bis", "Menge", "Name", "Parameter", "Produkt", "Freitext" },
                new String[] { "auftragPosGueltigVon", "auftragPosGueltigBis", "menge",
                        "leistungName", "auftragPosParameter", "oeName", "freeText" },
                new Class[] { Date.class, Date.class, Long.class, String.class, String.class, String.class }
        );
        AKJTable tbBillLeistungen = new AKJTable(tbMdlBillLeistungen, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbBillLeistungen.attachSorter();
        tbBillLeistungen.fitTable(new int[] { 70, 70, 50, 250, 150, 150, 150 });
        AKJScrollPane spBillLeistungen = new AKJScrollPane(tbBillLeistungen, new Dimension(400, 150));
        AKJPanel billPnl = new AKJPanel(new BorderLayout(), "Billing-Leistungen");
        billPnl.add(north, BorderLayout.NORTH);
        billPnl.add(spBillLeistungen, BorderLayout.CENTER);

        tbMdlTechLeistungen = new TechLeistungTableModel();
        AKJTable tbTechLeistungen = new AKJTable(tbMdlTechLeistungen, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbTechLeistungen.attachSorter();
        tbTechLeistungen.fitTable(new int[] { 120, 100, 80, 80, 200 });
        AKJScrollPane spTechLeistungen = new AKJScrollPane(tbTechLeistungen, new Dimension(400, 150));
        AKJPanel techPnl = new AKJPanel(new BorderLayout(), "techn. Leistungen");
        techPnl.add(spTechLeistungen, BorderLayout.CENTER);

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(billPnl, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        center.add(techPnl, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
    }

    private static class WorkerResult {
        private List<BAuftragLeistungView> leistungen;
        private List<TechLeistung> techLeistungen;
        private List<Auftrag2TechLeistung> auftrag2TechLeistungen;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        this.leistungen = null;
        tbMdlBillLeistungen.removeAll();
        if ((this.auftragModel != null) && (this.auftragModel.getAuftragId() != null)) {
            setWaitCursor();
            final SwingWorker<WorkerResult, Void> worker = new SwingWorker<WorkerResult, Void>() {

                final Long auftragId = auftragModel.getAuftragId();

                @Override
                protected WorkerResult doInBackground() throws Exception {
                    WorkerResult wResult = new WorkerResult();
                    CCAuftragService ccAS = getCCService(CCAuftragService.class);
                    AuftragDaten ad = ccAS.findAuftragDatenByAuftragId(auftragId);

                    BillingAuftragService as = getBillingService(BillingAuftragService.class.getName(), BillingAuftragService.class);
                    wResult.leistungen = as.findAuftragLeistungViews4Auftrag(ad.getAuftragNoOrig(), false, false);

                    CCLeistungsService ccLS = getCCService(CCLeistungsService.class);
                    if (techLeistungsMap == null) {
                        wResult.techLeistungen = ccLS.findTechLeistungen(false);
                    }
                    wResult.auftrag2TechLeistungen =
                            ccLS.findAuftrag2TechLeistungen(ad.getAuftragId(), null, false);
                    return wResult;
                }

                @Override
                protected void done() {
                    try {
                        WorkerResult wResult = get();
                        leistungen = wResult.leistungen;
                        tbMdlBillLeistungen.setData(leistungen);

                        if ((techLeistungsMap == null) && (wResult.techLeistungen != null)) {
                            techLeistungsMap = new HashMap<Long, TechLeistung>();
                            CollectionMapConverter.convert2Map(wResult.techLeistungen, techLeistungsMap, "getId", null);
                        }

                        tbMdlTechLeistungen.setTechLeistungen(techLeistungsMap);
                        tbMdlTechLeistungen.setData(wResult.auftrag2TechLeistungen);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        filterTable();
                        setDefaultCursor();
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        this.auftragModel = null;
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("actual".equals(command) || "new".equals(command) || "old".equals(command)) {
            filterTable();
        }
    }

    /**
     * Filtert die Tabelle nach den Leistungen, die den Filter-Optionen entsprechen.
     */
    private void filterTable() {
        if (this.leistungen == null) {
            return;
        }

        List<BAuftragLeistungView> result = new ArrayList<BAuftragLeistungView>();
        LeistungsFilterPredicate predicate = new LeistungsFilterPredicate();
        if (cbActual.isSelected()) {
            predicate.setFilterTyp(LeistungsFilterPredicate.FILTER_ACTUAL);
            result.addAll(CollectionUtils.select(leistungen, predicate));
        }
        if (cbNew.isSelected()) {
            predicate.setFilterTyp(LeistungsFilterPredicate.FILTER_NEW);
            result.addAll(CollectionUtils.select(leistungen, predicate));
        }
        if (cbOld.isSelected()) {
            predicate.setFilterTyp(LeistungsFilterPredicate.FILTER_OLD);
            result.addAll(CollectionUtils.select(leistungen, predicate));
        }
        tbMdlBillLeistungen.setData(result);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /*
     * Predicate, um nach Leistungen ueber deren Gueltigkeit zu filtern.
     */
    private static class LeistungsFilterPredicate implements Predicate {
        public static final int FILTER_ACTUAL = 1;
        public static final int FILTER_NEW = 2;
        public static final int FILTER_OLD = 3;

        private int filterTyp = -1;

        /**
         * Setzt den FilterTyp fuer das Predicate (=Konstante aus der Klasse!).
         *
         * @param typ
         */
        public void setFilterTyp(int typ) {
            this.filterTyp = typ;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof BAuftragLeistungView) {
                BAuftragLeistungView view = (BAuftragLeistungView) obj;
                Date now = new Date();
                switch (filterTyp) {
                    case FILTER_ACTUAL:
                        if (DateTools.isDateBeforeOrEqual(view.getAuftragPosGueltigVon(), now) &&
                                ((view.getAuftragPosGueltigBis() == null) ||
                                        DateTools.isDateAfterOrEqual(view.getAuftragPosGueltigBis(), now))) {
                            return true;
                        }
                        break;
                    case FILTER_NEW:
                        if (DateTools.isDateAfter(view.getAuftragPosGueltigVon(), now)) {
                            return true;
                        }
                        break;
                    case FILTER_OLD:
                        if (DateTools.isDateBefore(view.getAuftragPosGueltigBis(), now)) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            }
            return false;
        }
    }

    /* TableModel fuer die Darstellung der techn. Leistungen. */
    static class TechLeistungTableModel extends AKTableModel<Auftrag2TechLeistung> {
        private static final int COL_NAME = 0;
        private static final int COL_TYP = 1;
        private static final int COL_AKTIV_VON = 2;
        private static final int COL_AKTIV_BIS = 3;
        private static final int COL_DESC = 4;

        private static final int COL_COUNT = 5;

        private Map<Long, TechLeistung> techLeistungen = null;

        /**
         * Uebergibt dem TableModel eine Map mit allen techn. Leistungen. <br> Als Key der Map wird die Leistungs-ID,
         * als Value das Modell selbst erwartet.
         *
         * @param techLeistungen
         *
         */
        protected void setTechLeistungen(Map<Long, TechLeistung> techLeistungen) {
            this.techLeistungen = techLeistungen;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_NAME:
                    return "Leistung";
                case COL_TYP:
                    return "Typ";
                case COL_AKTIV_VON:
                    return "geplant von";
                case COL_AKTIV_BIS:
                    return "geplant bis";
                case COL_DESC:
                    return "Beschreibung";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Auftrag2TechLeistung mdl = getDataAtRow(row);
            if (mdl != null) {
                TechLeistung tl = this.techLeistungen.get(mdl.getTechLeistungId());
                switch (column) {
                    case COL_NAME:
                        return (tl != null) ? tl.getName() : null;
                    case COL_TYP:
                        return (tl != null) ? tl.getTyp() : null;
                    case COL_AKTIV_VON:
                        return mdl.getAktivVon();
                    case COL_AKTIV_BIS:
                        return mdl.getAktivBis();
                    case COL_DESC:
                        return (tl != null) ? tl.getDescription() : null;
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_AKTIV_VON:
                    return Date.class;
                case COL_AKTIV_BIS:
                    return Date.class;
                default:
                    return String.class;
            }
        }
    }
}


