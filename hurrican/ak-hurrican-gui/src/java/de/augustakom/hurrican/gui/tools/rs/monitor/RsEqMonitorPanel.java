/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2008 08:55:12
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

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.hvt.UEVTBelegungDialog;
import de.augustakom.hurrican.gui.hvt.UEVTBelegungGraphicalDialog;
import de.augustakom.hurrican.gui.hvt.UEVTDetailDialog;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.NiederlassungService;

/**
 * Panel fuer den EQ-Ressourcenmonitor.
 *
 *
 */
public class RsEqMonitorPanel extends AbstractMonitorPanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RsEqMonitorPanel.class);

    private UevtTable tbUevt = null;
    private UEVTBelegungTableModel tbMdlUevt = null;
    private List<RSMonitorConfig> config = null;
    private Map<Long, String> hvtRaeume = null;
    private Map<Long, String> niederlassungenMap = null;
    private boolean running;

    /**
     * Default-Konstruktor.
     */
    public RsEqMonitorPanel() {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsEqMonitorPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJButton btnUevt = getSwingFactory().createButton("uevt", getActionListener());
        AKJButton btnBelegung = getSwingFactory().createButton("belegung", getActionListener());
        AKJButton btnBelegungGraphic = getSwingFactory().createButton("belegung.graphic", getActionListener());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnUevt, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnBelegung, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnBelegungGraphic, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlUevt = new UEVTBelegungTableModel();
        tbUevt = new UevtTable(tbMdlUevt, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbUevt.addKeyListener(getRefreshKeyListener());
        tbUevt.attachSorter();
        tbUevt.fitTable(new int[] { 200, 40, 45, 150, 80, 60, 60, 60, 60, 60, 50, 80 });
        EditSchwellwertAction eswAction = new EditSchwellwertAction();
        eswAction.setParentClass(this.getClass());
        tbUevt.addPopupAction(eswAction);

        AKJScrollPane spTable = new AKJScrollPane(tbUevt);
        spTable.setPreferredSize(new Dimension(950, 400));

        manageGUI(eswAction);

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.CENTER);
        this.add(btnPnl, BorderLayout.SOUTH);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            MonitorService ms = getCCService(MonitorService.class);
            NiederlassungService ns = getCCService(NiederlassungService.class);

            // Lade Schwellwerte
            config = ms.findMonitorConfig4HvtType(null, RSMonitorRun.RS_REF_TYPE_EQ_MONITOR);

            // Lade Niederlassungen
            niederlassungenMap = new HashMap<Long, String>();
            CollectionMapConverter.convert2Map(ns.findNiederlassungen(), niederlassungenMap, "getId", "getName");
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractDataPanel#refresh()
     */
    @Override
    protected void refresh() {
        tbMdlUevt.setData(null);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("uevt".equals(command)) {
            showUevtDetails();
        }
        else if ("belegung".equals(command)) {
            showUevtBelegung(false);
        }
        else if ("belegung.graphic".equals(command)) {
            showUevtBelegung(true);
        }
    }

    /*
     * Funktion liefert die aktuelle Auswahl zurueck
     */
    private UevtCuDAView getSelection() throws HurricanGUIException {
        UevtCuDAView view = null;
        Object selection = ((AKMutableTableModel<?>) tbUevt.getModel()).getDataAtRow(tbUevt.getSelectedRow());
        if (selection instanceof UevtCuDAView) {
            view = (UevtCuDAView) selection;
        }

        if (view != null) {
            return view;
        }
        else {
            throw new HurricanGUIException("Bitte selektieren Sie zuerst einen UEVT, zu dem die Details "
                    + "angezeigt werden sollen.");
        }
    }

    /* Zeigt Details zu dem aktuell selektierten UEVT an. */
    private void showUevtDetails() {
        try {
            UevtCuDAView view = getSelection();
            UEVTDetailDialog dlg = new UEVTDetailDialog(view.getUevt(), view.getCudaPhysik(), view.getHvtIdStandort());
            DialogHelper.showDialog(this, dlg, true, true);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /*
     * Zeigt Belegungs-Details des aktuell selektierten UEVTs an (tabellarisch od. graphisch)
     */
    private void showUevtBelegung(boolean graphical) {
        try {
            UevtCuDAView view = getSelection();
            AKJOptionDialog dlg = null;
            if (!graphical) {
                dlg = new UEVTBelegungDialog(view.getUevt(), view.getHvtIdStandort());
            }
            else {
                dlg = new UEVTBelegungGraphicalDialog(view.getUevt(), view.getHvtIdStandort());
            }

            DialogHelper.showDialog(this, dlg, true, true);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.tools.rs.monitor.AbstractMonitorPanel#showRM()
     */
    @Override
    protected void showRM() {
        final SwingWorker<Pair<Map<Long, String>, List<UevtCuDAView>>, Void> worker = new SwingWorker<Pair<Map<Long, String>, List<UevtCuDAView>>, Void>() {

            @Override
            public Pair<Map<Long, String>, List<UevtCuDAView>> doInBackground() throws Exception {
                HWService hwService = getCCService(HWService.class);
                HVTService hvtService = getCCService(HVTService.class);
                MonitorService ms = getCCService(MonitorService.class);
                NiederlassungService ns = getCCService(NiederlassungService.class);

                // Uevt CuDA View
                List<UevtCuDAView> uevtCuDAViews = ms.findUevtCuDAViews(getQuery());

                // Lade HVT-Raeume
                Map<Long, String> hvtRaeume = new HashMap<Long, String>();
                for (UevtCuDAView view : uevtCuDAViews) {
                    Long uevtId = view.getUevtId();
                    if (hvtRaeume.containsKey(uevtId)) {
                        continue;
                    }
                    UEVT uevt = hvtService.findUEVT(uevtId);
                    if (uevt != null) {
                        HWRack rack = hwService.findRackById(uevt.getRackId());
                        if (rack != null) {
                            HVTRaum raum = hvtService.findHVTRaum(rack.getHvtRaumId());
                            if ((raum != null) && StringUtils.isNotBlank(raum.getRaum())) {
                                hvtRaeume.put(uevt.getId(), raum.getRaum());
                            }
                        }
                    }
                }

                Pair<Map<Long, String>, List<UevtCuDAView>> retVal = Pair.create(hvtRaeume, uevtCuDAViews);

                // Lade Niederlassungen (0 sec)
                niederlassungenMap = new HashMap<Long, String>();
                CollectionMapConverter.convert2Map(ns.findNiederlassungen(), niederlassungenMap, "getId", "getName");

                // Vom HVT Standort zum Niederlassungsnamen mappen (13 sec)
                for (UevtCuDAView uevtCuDAView : retVal.getSecond()) {
                    try {
                        if (uevtCuDAView.getHvtIdStandort() != null) {
                            uevtCuDAView.setNiederlassungId(hvtService.findNiederlassungId4HVTIdStandort(uevtCuDAView
                                    .getHvtIdStandort()));
                        }
                    }
                    catch (FindException e) {
                        // Ignore exception and try to resolve next item
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(e.getMessage());
                        }
                    }
                }

                return retVal;
            }

            @Override
            protected void done() {
                try {
                    Pair<Map<Long, String>, List<UevtCuDAView>> pair = get();
                    hvtRaeume = pair.getFirst();
                    tbMdlUevt.setData(pair.getSecond());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    running = false;
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        if (!running) {
            running = true;

            setWaitCursor();
            showProgressBar("lade Übersicht...");
            tbMdlUevt.setData(null);
            worker.execute();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.tools.rs.monitor.AbstractMonitorPanel#getRMType()
     */
    @Override
    protected Long getRMType() {
        return RSMonitorRun.RS_REF_TYPE_EQ_MONITOR;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /* Action, um einen Schwellwert anzulegen/zu editieren. */
    class EditSchwellwertAction extends AKAbstractAction {

        public EditSchwellwertAction() {
            setName("Schwellwert editieren");
            setTooltip("Schwellwert editieren/anlegen");
            setActionCommand("edit.schwellwert");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UevtCuDAView view = getSelection();
                // Ermittle Schwellwert, falls vorhanden
                Predicate4RMConfig predicate = new Predicate4RMConfig();
                predicate.setPredicateValues(view.getUevt(), view.getHvtIdStandort(),
                        StringUtils.trimToNull(view.getCudaPhysik()));
                RSMonitorConfig rmConfig = (RSMonitorConfig) CollectionUtils.find(config, predicate);

                // Falls kein Schwellwert vorhanden, belege Physiktypen und HVT
                // vor
                if (rmConfig == null) {
                    rmConfig = new RSMonitorConfig();
                    rmConfig.setHvtIdStandort(view.getHvtIdStandort());
                    rmConfig.setEqUEVT(view.getUevt());
                    rmConfig.setEqRangSchnittstelle(StringUtils.trimToNull(view.getCudaPhysik()));
                }
                DialogHelper.showDialog(getMainFrame(), new RsMonitorConfigEQAnlegenDialog(rmConfig), true, true);

                // Lade Schwellwerte neu und zeichne Tabelle neu
                MonitorService ms = getCCService(MonitorService.class);
                config = ms.findMonitorConfig4HvtType(null, RSMonitorRun.RS_REF_TYPE_EQ_MONITOR);
                tbUevt.repaint();
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /* Ableitung von AKJTable, um die BG-Color zu aendern. */
    class UevtTable extends AKJTable {
        /**
         * @param dm
         * @param autoResizeMode
         * @param selectionMode
         */
        public UevtTable(TableModel dm, int autoResizeMode, int selectionMode) {
            super(dm, autoResizeMode, selectionMode);
        }

        /**
         * @see de.augustakom.common.gui.swing.AKJTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
         */
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            AKMutableTableModel<?> model = (AKMutableTableModel<?>) getModel();
            if (model.getDataAtRow(row) instanceof UevtCuDAView) {
                UevtCuDAView view = (UevtCuDAView) model.getDataAtRow(row);

                // Schwellwert ermitteln
                Predicate4RMConfig predicate = new Predicate4RMConfig();
                predicate.setPredicateValues(StringUtils.trimToNull(view.getUevt()), view.getHvtIdStandort(),
                        StringUtils.trimToNull(view.getCudaPhysik()));

                RSMonitorConfig rmConfig = (RSMonitorConfig) CollectionUtils.find(config, predicate);

                Integer schwellwert = ((rmConfig != null) && (rmConfig.getMinCount() != null)) ? rmConfig.getMinCount()
                        : null;

                // Pruefe Schwellwert
                int check = view.checkSchwellwert(schwellwert);

                boolean colorChanged = false;
                Component c = super.prepareRenderer(renderer, row, column);

                // Zeile blau, falls kein Schwellwert gesetzt ist
                if (check == RSMonitorConfig.SCHWELLWERT_NICHT_DEFINIERT) {
                    c.setBackground(Color.blue);
                    colorChanged = true;
                }
                // ganze Zeile rot: freie Stifte < Schwellwert
                else if (check == RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN) {
                    c.setBackground(Color.red);
                    colorChanged = true;
                }
                // ganze Zeile orange: freie Stifte ist knapp am Schwellwert
                else if (check == RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN) {
                    c.setBackground(Color.orange);
                    colorChanged = true;
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
            }

            return super.prepareRenderer(renderer, row, column);
        }
    }

    /* Predicate, um nach einer bestimmten Konfiguration zu suchen. */
    static class Predicate4RMConfig implements Predicate {
        private String uevt = null;
        private Long hvtIdStandort = null;
        private String rangSchnittstelle = null;

        /**
         * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
         *
         * @param uevt
         * @param hvtId
         * @param cudaPhysik
         * @param carrier
         */
        public void setPredicateValues(String uevt, Long hvtId, String rangSchnittstelle) {
            this.uevt = uevt;
            this.hvtIdStandort = hvtId;
            this.rangSchnittstelle = rangSchnittstelle;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof RSMonitorConfig) {
                RSMonitorConfig view = (RSMonitorConfig) obj;
                if (!StringUtils.equals(view.getEqUEVT(), uevt)) {
                    return false;
                }
                if (!NumberTools.equal(view.getHvtIdStandort(), hvtIdStandort)) {
                    return false;
                }
                if (!StringUtils.equals(view.getEqRangSchnittstelle(), rangSchnittstelle)) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }

    /**
     * TableModel fuer die Darstellung der Belegung von UEVTs.
     *
     *
     */
    class UEVTBelegungTableModel extends AKTableModel<UevtCuDAView> {

        protected static final int COL_ORT = 0;
        protected static final int COL_ONKZ = 1;
        protected static final int COL_ASB = 2;
        protected static final int COL_RAUM = 3;
        protected static final int COL_UEVT = 4;
        protected static final int COL_CUDA_PHYSIK = 5;
        protected static final int COL_VORHANDEN = 6;
        protected static final int COL_RANGIERT = 7;
        protected static final int COL_BELEGT = 8;
        protected static final int COL_STIFTE_FREI = 9;
        protected static final int COL_ALARM = 10;
        protected static final int COL_NIEDERLASSUNG = 11;
        protected static final int COL_CLUSTER = 12;

        private static final int COL_COUNT = 13;

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
                case COL_ONKZ:
                    return "ONKZ";
                case COL_ASB:
                    return "ASB";
                case COL_ORT:
                    return "HVT-Name";
                case COL_UEVT:
                    return "UEVT";
                case COL_RAUM:
                    return "HVT-Raum";
                case COL_CUDA_PHYSIK:
                    return "CuDA Art";
                case COL_VORHANDEN:
                    return "vorhanden";
                case COL_RANGIERT:
                    return "rangiert";
                case COL_BELEGT:
                    return "belegt";
                case COL_STIFTE_FREI:
                    return "CuDA frei";
                case COL_ALARM:
                    return "Alarmierung";
                case COL_NIEDERLASSUNG:
                    return "Niederlassung";
                case COL_CLUSTER:
                    return "Cluster";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof UevtCuDAView) {
                UevtCuDAView view = (UevtCuDAView) tmp;
                switch (column) {
                    case COL_ONKZ:
                        return view.getOnkz();
                    case COL_ASB:
                        return view.getAsb();
                    case COL_ORT:
                        return view.getHvtName();
                    case COL_UEVT:
                        return view.getUevt();
                    case COL_CUDA_PHYSIK:
                        return view.getCudaPhysik();
                    case COL_VORHANDEN:
                        return view.getCudaVorhanden();
                    case COL_RANGIERT:
                        return view.getCudaRangVorb();
                    case COL_BELEGT:
                        return (view.getCudaBelegt() != null) ? view.getCudaBelegt() : 0;
                    case COL_STIFTE_FREI:
                        return view.getCudaFrei();
                    case COL_ALARM:
                        Predicate4RMConfig predicate = new Predicate4RMConfig();
                        predicate.setPredicateValues(view.getUevt(), view.getHvtIdStandort(),
                                StringUtils.trimToNull(view.getCudaPhysik()));
                        RSMonitorConfig rmConfig = (RSMonitorConfig) CollectionUtils.find(config, predicate);
                        return (rmConfig != null) ? rmConfig.getAlarmierung() : Boolean.FALSE;
                    case COL_RAUM:
                        Object hvtRaum = hvtRaeume.get(view.getUevtId());
                        if ((hvtRaum != null) && (hvtRaum instanceof String)) {
                            return hvtRaum;
                        }
                        else {
                            return "";
                        }
                    case COL_NIEDERLASSUNG:
                        if (view.getNiederlassungId() != null) {
                            Object name = niederlassungenMap.get(view.getNiederlassungId());
                            if ((name != null) && (name instanceof String)) {
                                return name;
                            }
                        }
                        return "";
                    case COL_CLUSTER:
                        return view.getCluster();
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
                case COL_ONKZ:
                    return String.class;
                case COL_ORT:
                    return String.class;
                case COL_UEVT:
                    return String.class;
                case COL_CUDA_PHYSIK:
                    return String.class;
                case COL_RAUM:
                    return String.class;
                case COL_ALARM:
                    return Boolean.class;
                case COL_NIEDERLASSUNG:
                    return String.class;
                case COL_CLUSTER:
                    return String.class;
                default:
                    return Integer.class;
            }
        }
    }
}
