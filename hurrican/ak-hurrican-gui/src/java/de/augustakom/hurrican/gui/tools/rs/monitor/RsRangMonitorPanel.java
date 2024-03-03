/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2008 08:15:12
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Panel fuer die Ueberwachung der Rangierungen.
 *
 *
 */
public class RsRangMonitorPanel extends AbstractMonitorPanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RsRangMonitorPanel.class);

    private AKJTable tbRang = null;
    private RangDetailTableModel tbMdlRang = null;
    private List<RSMonitorConfig> monitorConfigurations = null;
    private List<RsmRangCountView> rangCount = null;
    private Map<Long, String> physiktypenMap = null;
    private Map<Long, HVTGruppe> hvtGruppenMap = null;
    private Map<Long, HVTStandort> hvtStandortMap = null;
    private Map<Long, String> niederlassungenMap = null;

    private boolean running;

    /**
     * Default-Konstruktor.
     */
    public RsRangMonitorPanel() {
        super(null);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        tbMdlRang = new RangDetailTableModel();
        tbRang = new RangTable(tbMdlRang, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRang.attachSorter();
        tbRang.fitTable(new int[] { 200, 60, 120, 120, 60, 60, 60, 60, 60, 70, 50, 70, 70, 80 });
        EditSchwellwertAction eswAction = new EditSchwellwertAction();
        eswAction.setParentClass(this.getClass());
        tbRang.addPopupAction(eswAction);

        ShowPortUsageAction showPortUsageAction = new ShowPortUsageAction();
        showPortUsageAction.setParentClass(this.getClass());
        tbRang.addPopupAction(showPortUsageAction);

        AKJScrollPane spTable = new AKJScrollPane(tbRang);
        spTable.setPreferredSize(new Dimension(950, 400));

        manageGUI(eswAction);

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.CENTER);
    }

    @Override
    public final void loadData() {
        try {
            MonitorService ms = getCCService(MonitorService.class);
            HVTService hvts = getCCService(HVTService.class);
            PhysikService ps = getCCService(PhysikService.class);
            NiederlassungService ns = getCCService(NiederlassungService.class);

            monitorConfigurations = ms.findMonitorConfig4HvtType(null, RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);

            hvtStandortMap = new HashMap<Long, HVTStandort>();
            CollectionMapConverter.convert2Map(hvts.findHVTStandorte(), hvtStandortMap, "getId", null);

            hvtGruppenMap = new HashMap<Long, HVTGruppe>();
            CollectionMapConverter.convert2Map(hvts.findHVTGruppen(), hvtGruppenMap, "getId", null);

            physiktypenMap = new HashMap<Long, String>();
            CollectionMapConverter.convert2Map(ps.findPhysikTypen(), physiktypenMap, "getId", "getName");

            niederlassungenMap = new HashMap<Long, String>();
            CollectionMapConverter.convert2Map(ns.findNiederlassungen(), niederlassungenMap, "getId", "getName");
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected void refresh() {
        tbMdlRang.setData(null);
    }

    @Override
    protected void execute(String command) {
    }

    private static class SwingWorkerResult {
        private List<RSMonitorConfig> configResult;
        private List<HVTStandort> hvtStandortList;
        private List<HVTGruppe> hvtGruppenList;
        private List<PhysikTyp> physiktypenList;
        private List<RsmRangCountView> rangCountResult;
        private List<Niederlassung> niederlassungenList;
    }


    @Override
    protected void showRM() {
        final SwingWorker<SwingWorkerResult, Void> worker = new SwingWorker<SwingWorkerResult, Void>() {

            @Override
            public SwingWorkerResult doInBackground() throws Exception {
                // Services
                MonitorService ms = getCCService(MonitorService.class);
                HVTService hvts = getCCService(HVTService.class);
                PhysikService ps = getCCService(PhysikService.class);
                NiederlassungService ns = getCCService(NiederlassungService.class);

                // Lade Daten fuer TableModel
                SwingWorkerResult result = new SwingWorkerResult();
                result.configResult = ms.findMonitorConfig4HvtType(null, RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);
                result.hvtStandortList = hvts.findHVTStandorte();
                result.hvtGruppenList = hvts.findHVTGruppen();
                result.physiktypenList = ps.findPhysikTypen();
                result.niederlassungenList = ns.findNiederlassungen();

                // Lade View
                result.rangCountResult = ms.findRsmRangCount(getQuery());

                return result;
            }

            @Override
            protected void done() {
                try {
                    SwingWorkerResult result = get();

                    // Lade Daten fuer TableModel
                    monitorConfigurations = result.configResult;

                    hvtStandortMap = new HashMap<Long, HVTStandort>();
                    CollectionMapConverter.convert2Map(result.hvtStandortList, hvtStandortMap, "getId", null);

                    hvtGruppenMap = new HashMap<Long, HVTGruppe>();
                    CollectionMapConverter.convert2Map(result.hvtGruppenList, hvtGruppenMap, "getId", null);

                    physiktypenMap = new HashMap<Long, String>();
                    CollectionMapConverter.convert2Map(result.physiktypenList, physiktypenMap, "getId", "getName");

                    niederlassungenMap = new HashMap<Long, String>();
                    CollectionMapConverter.convert2Map(result.niederlassungenList, niederlassungenMap, "getId", "getName");

                    // Lade View
                    rangCount = result.rangCountResult;
                    tbMdlRang.setData(rangCount);

                    running = false;
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        if (!running) {
            running = true;

            setWaitCursor();
            showProgressBar("lade Übersicht...");
            tbMdlRang.setData(null);
            worker.execute();
        }
    }

    @Override
    protected Long getRMType() {
        return RSMonitorRun.RS_REF_TYPE_RANG_MONITOR;
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void readModel() throws AKGUIException {
    }

    @Override
    public void saveModel() throws AKGUIException {
    }

    /* Ableitung von AKJTable, um die BG-Color zu aendern. */
    class RangTable extends AKJTable {
        /**
         * @param dm
         * @param autoResizeMode
         * @param selectionMode
         */
        public RangTable(TableModel dm, int autoResizeMode, int selectionMode) {
            super(dm, autoResizeMode, selectionMode);
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            boolean colorChanged = false;
            Component c = super.prepareRenderer(renderer, row, column);

            RsmRangCount rc = (RsmRangCount) ((AKMutableTableModel<?>) tbRang.getModel()).getDataAtRow(row);

            if (rc != null) {
                // Schwellwert ermitteln
                Predicate4RMConfig predicate = new Predicate4RMConfig();
                predicate.setPredicateValues(rc.getHvtStandortId(), rc.getKvzNummer(), rc.getPhysiktyp(), rc.getPhysiktypAdd());
                RSMonitorConfig rmConfig = (RSMonitorConfig) CollectionUtils.find(monitorConfigurations, predicate);

                Integer schwellwert = ((rmConfig != null) && (rmConfig.getMinCount() != null))
                        ? rmConfig.getMinCount() : null;

                // Pruefe Schwellwert
                int check = rc.checkFreePortThreshold(schwellwert);

                // Zelle blau, falls kein Schwellwert definiert ist aber Stifte zugeordnet
                if (check == RSMonitorConfig.SCHWELLWERT_NICHT_DEFINIERT) {
                    c.setBackground(Color.blue);
                    colorChanged = true;
                }
                // Zelle rot: frei Stifte < Schwellwert
                else if (check == RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN) {
                    c.setBackground(Color.red);
                    colorChanged = true;
                }
                // Zeile orange: freie Stifte ist knapp am Schwellwert
                else if (check == RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN) {
                    c.setBackground(Color.orange);
                    colorChanged = true;
                }
            }

            if (colorChanged) {
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    Font f = lbl.getFont();
                    lbl.setFont(f.deriveFont(Font.BOLD));
                    lbl.setForeground(Color.white);
                }
                return c;
            }
            else {
                if (!isCellSelected(row, column) && (c instanceof JLabel)) {
                    JLabel lbl = (JLabel) c;
                    lbl.setForeground(Color.black);
                    return c;
                }
            }
            return super.prepareRenderer(renderer, row, column);
        }
    }

    /* Action, um einen Schwellwert anzulegen/zu editieren. */
    class EditSchwellwertAction extends AKAbstractAction {
        public EditSchwellwertAction() {
            setName("Schwellwert editieren");
            setTooltip("Schwellwert editieren/anlegen");
            setActionCommand("edit.schwellwert");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                RsmRangCount view = (RsmRangCount) ((AKMutableTableModel<?>) tbRang.getModel()).getDataAtRow(tbRang.getSelectedRow());
                //Ermittle Schwellwert, falls vorhanden
                Predicate4RMConfig predicate = new Predicate4RMConfig();
                predicate.setPredicateValues(view.getHvtStandortId(), view.getKvzNummer(), view.getPhysiktyp(), view.getPhysiktypAdd());
                RSMonitorConfig rmConfig = (RSMonitorConfig) CollectionUtils.find(monitorConfigurations, predicate);

                // Falls kein Schwellwert vorhanden, belege Standortdaten und Physiktypen vor
                if (rmConfig == null) {
                    rmConfig = new RSMonitorConfig();
                    rmConfig.setHvtIdStandort(view.getHvtStandortId());
                    rmConfig.setKvzNummer(view.getKvzNummer());
                    rmConfig.setPhysiktyp(view.getPhysiktyp());
                    rmConfig.setPhysiktypAdd(view.getPhysiktypAdd());
                }
                DialogHelper.showDialog(getMainFrame(), new RsMonitorConfigRangAnlegenDialog(rmConfig), true, true);

                //Lade Schwellwerte neu und zeichne Tabelle neu
                MonitorService ms = getCCService(MonitorService.class);
                monitorConfigurations = ms.findMonitorConfig4HvtType(null, RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);
                tbRang.repaint();
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /* Action, um  Anzeige Port-Verbrauch letzter x Monate */
    class ShowPortUsageAction extends AKAbstractAction {
        public ShowPortUsageAction() {
            setName("Details");
            setTooltip("Anzeige Port-Verbrauch letzter x Monate");
            setActionCommand("show.portusage");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                RsmRangCount view = (RsmRangCount) ((AKMutableTableModel<?>) tbRang.getModel()).getDataAtRow(tbRang.getSelectedRow());
                RsMonitorPortUsageDialog rsMonitorPortUsageDialog = new RsMonitorPortUsageDialog(view);
                DialogHelper.showDialog(getMainFrame(), rsMonitorPortUsageDialog, Boolean.TRUE, Boolean.TRUE);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /* TableModel fuer die Darstellung der freien und freigabebereiten Stifte pro HVT/Physiktyp */
    class RangDetailTableModel extends AKTableModel<RsmRangCountView> {
        static final int COL_HVT = 0;
        static final int COL_KVZ = 1;
        static final int COL_PHYSIKTYP = 2;
        static final int COL_PHYSIKTYP_ADD = 3;
        static final int COL_BELEGT = 4;
        static final int COL_DEFEKT = 5;
        static final int COL_FREI = 6;
        static final int COL_FREIGABEBEREIT = 7;
        static final int COL_IM_AUFBAU = 8;
        static final int COL_GESAMT = 9;
        static final int COL_ALARM = 10;
        static final int COL_RESTLAUFZEIT = 11;
        static final int COL_AVERAGE_USAGE = 12;
        static final int COL_NIEDERLASSUNG = 13;
        static final int COL_CLUSTER = 14;

        static final int COL_COUNT = 15;

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
                case COL_HVT:
                    return "HVT-Standort";
                case COL_KVZ:
                    return "KVZ-Nummer";
                case COL_PHYSIKTYP:
                    return "Physiktyp";
                case COL_PHYSIKTYP_ADD:
                    return "Zugeordneter Physiktyp";
                case COL_BELEGT:
                    return "Belegt";
                case COL_DEFEKT:
                    return "Defekt";
                case COL_FREI:
                    return "Frei";
                case COL_FREIGABEBEREIT:
                    return "Freigabebereit";
                case COL_IM_AUFBAU:
                    return "Im Aufbau";
                case COL_GESAMT:
                    return "Gesamt";
                case COL_ALARM:
                    return "Alarm";
                case COL_RESTLAUFZEIT:
                    return "Restlaufzeit";
                case COL_AVERAGE_USAGE:
                    return "Ø Portverbrauch";
                case COL_NIEDERLASSUNG:
                    return "Niederlassung";
                case COL_CLUSTER:
                    return "Cluster";
                default:
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            RsmRangCountView rc = getDataAtRow(row);
            if (rc != null) {
                switch (column) {
                    case COL_HVT:
                        HVTStandort hvt = hvtStandortMap.get(rc.getHvtStandortId());
                        if (hvt != null) {
                            HVTGruppe hg = hvtGruppenMap.get(hvt.getHvtGruppeId());
                            return (hg != null) ? hg.getOrtsteil() : null;
                        }
                        return "";
                    case COL_KVZ:
                        return rc.getKvzNummer();
                    case COL_PHYSIKTYP:
                        return physiktypenMap.get(rc.getPhysiktyp());
                    case COL_PHYSIKTYP_ADD:
                        return physiktypenMap.get(rc.getPhysiktypAdd());
                    case COL_BELEGT:
                        return rc.getBelegt();
                    case COL_FREI:
                        return rc.getFrei();
                    case COL_FREIGABEBEREIT:
                        return rc.getFreigabebereit();
                    case COL_GESAMT:
                        return rc.getVorhanden();
                    case COL_DEFEKT:
                        return rc.getDefekt();
                    case COL_IM_AUFBAU:
                        return rc.getImAufbau();
                    case COL_ALARM:
                        Predicate4RMConfig predicate = new Predicate4RMConfig();
                        predicate.setPredicateValues(rc.getHvtStandortId(), rc.getKvzNummer(),
                                rc.getPhysiktyp(), rc.getPhysiktypAdd());
                        RSMonitorConfig rmConfig = (RSMonitorConfig) CollectionUtils.find(monitorConfigurations,
                                predicate);
                        return (rmConfig != null) ? rmConfig.getAlarmierung() : null;
                    case COL_RESTLAUFZEIT:
                        return rc.getPortReach();
                    case COL_AVERAGE_USAGE:
                        return rc.getAverageUsage();
                    case COL_NIEDERLASSUNG:
                        return rc.getNiederlassung();
                    case COL_CLUSTER:
                        return rc.getCluster();
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
            if ((columnIndex == COL_HVT)
                    || (columnIndex == COL_PHYSIKTYP)
                    || (columnIndex == COL_PHYSIKTYP_ADD)
                    || (columnIndex == COL_CLUSTER)
                    || (columnIndex == COL_NIEDERLASSUNG)) {
                return String.class;
            }
            else if (columnIndex == COL_ALARM) {
                return Boolean.class;
            }
            else if (columnIndex == COL_AVERAGE_USAGE) {
                return Float.class;
            }
            else {
                return Integer.class;
            }
        }
    }

    /* Predicate, um nach einer bestimmten Konfiguration zu suchen. */
    static class Predicate4RMConfig implements Predicate {

        private Long hvtIdStandort = null;
        private String kvzNummer = null;
        private Long physiktyp = null;
        private Long physiktypAdd = null;

        /**
         * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
         */
        public void setPredicateValues(Long hvtId, String kvzNummer, Long physiktyp, Long physiktypAdd) {
            this.hvtIdStandort = hvtId;
            this.kvzNummer = kvzNummer;
            this.physiktyp = physiktyp;
            this.physiktypAdd = physiktypAdd;
        }

        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof RSMonitorConfig) {
                RSMonitorConfig view = (RSMonitorConfig) obj;
                if (!NumberTools.equal(view.getHvtIdStandort(), hvtIdStandort)) {
                    return false;
                }
                if (!StringUtils.equals(view.getKvzNummer(), kvzNummer)) {
                    return false;
                }
                if (!NumberTools.equal(view.getPhysiktyp(), physiktyp)) {
                    return false;
                }
                if (!NumberTools.equal(view.getPhysiktypAdd(), physiktypAdd)) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }

}


