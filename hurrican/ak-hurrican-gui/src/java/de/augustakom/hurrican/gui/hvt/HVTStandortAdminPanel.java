/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2004 08:13:32
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Admin-Panel fuer die HVT-Standorte.
 *
 *
 */
public class HVTStandortAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HVTStandortAdminPanel.class);

    private static final int TAB_INDEX_KVZ_ADRESSE = 7;

    private AKJTabbedPane tabbedPane = null;

    private AKJTable tbHVTST = null;
    private HVTStandortTM tbModelHVTST = null;
    protected Map<Long, HVTGruppe> hvtGruppen = null;
    private Map<Long, String> standortTypen;
    private HVTUevtAdminPanel hvtUevtAdminPanel;
    protected HVTGruppenAdminPanel hvtGruppenAdminPanel;
    protected HVTStandortDetailAdminPanel hvtStandortDetailAdminPanel;

    /**
     * Standardkonstruktor.
     */
    public HVTStandortAdminPanel(HVTGruppenAdminPanel hvtGruppenAdminPanel) {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTStandortAdminPanel.xml");
        this.hvtGruppen = new HashMap<Long, HVTGruppe>();
        this.hvtGruppenAdminPanel = hvtGruppenAdminPanel;
        createGUI();
        initializeGUIData();
    }

    @Override
    protected final void createGUI() {
        tbModelHVTST = new HVTStandortTM();
        tbHVTST = new AKJTable(tbModelHVTST, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbHVTST.attachSorter();
        tbHVTST.addMouseListener(getTableListener());
        tbHVTST.addKeyListener(getTableListener());
        tbHVTST.setDefaultRenderer(Date.class, new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        AKJScrollPane tableSP = new AKJScrollPane(tbHVTST, new Dimension(750, 250));
        tbHVTST.fitTable(new int[] { 60, 150, 60, 100, 100 });

        tabbedPane = new AKJTabbedPane();
        // beim Veraendern der Reihenfolge der Tabs bitte die Konstante 'TAB_INDEX_KVZ_ADRESSE' entsprechend anpassen!
        hvtStandortDetailAdminPanel = new HVTStandortDetailAdminPanel(this);
        tabbedPane.addTab(getSwingFactory().getText("tab.standort.details"), hvtStandortDetailAdminPanel);
        hvtUevtAdminPanel = new HVTUevtAdminPanel();
        tabbedPane.addTab(getSwingFactory().getText("tab.uevt"), hvtUevtAdminPanel);
        tabbedPane.addTab(getSwingFactory().getText("tab.raum"), new HVTRaumAdminPanel());
        tabbedPane.addTab(getSwingFactory().getText("tab.rack"), new HWRackAdminPanel());
        tabbedPane.addTab(getSwingFactory().getText("tab.subrack"), new HWSubrackAdminPanel());
        tabbedPane.addTab(getSwingFactory().getText("tab.bg"), new HWBaugruppenAdminPanel());
        tabbedPane.addTab(getSwingFactory().getText("tab.tech.type"), new HVTTechTypeAdminPanel());
        tabbedPane.addTab(getSwingFactory().getText("tab.kvz.adresse"), new HvtKvzAdresseAdminPanel(new KvzAdresse()));
        tabbedPane.setEnabledAt(TAB_INDEX_KVZ_ADRESSE, false);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableSP);
        split.setBottomComponent(tabbedPane);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    private void initializeGUIData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            final ReferenceService rs = getCCService(ReferenceService.class);
            final List<Reference> typen = rs.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, true);
            standortTypen = new HashMap<>();
            CollectionMapConverter.convert2Map(typen, standortTypen, "getId", "getStrValue");

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }

    public void updateData(Map<Long, HVTGruppe> hvtGruppenMap, List<HVTGruppe> hvtGruppenList, List<HVTStandort> hvtStandorts) {
        this.hvtGruppen = hvtGruppenMap;
        tbModelHVTST.setData(hvtStandorts);
        // update data in subsidiary components
        hvtUevtAdminPanel.updateData(hvtGruppenMap, hvtGruppenList, hvtStandorts);

    }

    @Override
    public final void loadData() {
        // done in HVTAdminSearchPanel by button click
    }

    @Override
    public void showDetails(Object details) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setEnabledAt(i, true);
            ((AbstractAdminPanel) tabbedPane.getComponentAt(i)).showDetails(details);
        }
        if (details instanceof HVTStandort) {
            HVTStandort hvtStandort = (HVTStandort) details;
            if (!hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                tabbedPane.setEnabledAt(TAB_INDEX_KVZ_ADRESSE, false);
                if (tabbedPane.getSelectedIndex() == TAB_INDEX_KVZ_ADRESSE) {
                    tabbedPane.setSelectedIndex(0);
                }
            }
        }
    }

    @Override
    public void createNew() {
        ((AbstractAdminPanel) tabbedPane.getSelectedComponent()).createNew();
    }

    @Override
    public void saveData() {
        ((AbstractAdminPanel) tabbedPane.getSelectedComponent()).saveData();
    }

    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    /**
     * Aktualisiert die Darstellung der HVT-Standorte
     *
     * @param detail Falls ein neuer HVT-Standort angelegt wurde, wird dieser dm TableModel hinzugefuegt
     *
     */
    public void updateTable(HVTStandort detail) {
        if (detail != null) {
            tbModelHVTST.addObject(detail);
            tbHVTST.selectAndScrollToLastRow();
        }
    }

    public void lockForNewStandort() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            AbstractAdminPanel panel = (AbstractAdminPanel) tabbedPane.getComponentAt(i);
            if (!(panel instanceof HVTStandortDetailAdminPanel)) {
                tabbedPane.setEnabledAt(i, false);
            }
        }
    }

    /**
     * TableModel fuer die HVT-Standorte.
     */
    class HVTStandortTM extends AKTableModel<HVTStandort> {
        static final int COL_ID = 0;
        static final int COL_GRUPPE = 1;
        static final int COL_VT = 2;
        static final int COL_STANDORT_TYP = 3;
        static final int COL_GUELTIG_VON = 4;
        static final int COL_GUELTIG_BIS = 5;

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
                case COL_ID:
                    return "ID";
                case COL_GRUPPE:
                    return "HVT-Gruppe";
                case COL_VT:
                    return "ASB";
                case COL_STANDORT_TYP:
                    return "Standorttyp";
                case COL_GUELTIG_VON:
                    return "Gültig von";
                case COL_GUELTIG_BIS:
                    return "Gültig bis";
                default:
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof HVTStandort) {
                HVTStandort hvt = (HVTStandort) tmp;
                switch (column) {
                    case COL_ID:
                        return hvt.getId();
                    case COL_GRUPPE:
                        Object hg = hvtGruppen.get(hvt.getHvtGruppeId());
                        return (hg instanceof HVTGruppe) ? ((HVTGruppe) hg).getOrtsteil() : "";
                    case COL_VT:
                        return hvt.getAsb();
                    case COL_STANDORT_TYP:
                        return standortTypen.get(hvt.getStandortTypRefId());
                    case COL_GUELTIG_VON:
                        return hvt.getGueltigVon();
                    case COL_GUELTIG_BIS:
                        return hvt.getGueltigBis();
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
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_GUELTIG_VON:
                    return Date.class;
                case COL_GUELTIG_BIS:
                    return Date.class;
                case COL_ID:
                    return Long.class;
                case COL_VT:
                    return Integer.class;
                case COL_STANDORT_TYP:
                    return String.class;
                default:
                    return super.getColumnClass(columnIndex);
            }
        }
    }


}


