/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2005 09:20:16
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanGUIConstants;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.gui.cps.CPSHistoryDialog;
import de.augustakom.hurrican.gui.shared.RufnummerDialog;
import de.augustakom.hurrican.gui.shared.VbzHistoryDialog;
import de.augustakom.hurrican.gui.utils.PrintVerlaufHelper;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Portierungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.esb.cdm.resource.workforceservice.v1.DeleteOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceService;

/**
 * Abstraktes Panel fuer die Darstellung von Bauauftraegen. <br/> Das Panel besitzt eine SplitPane (vertikal). In der
 * oberen Haelfte wird eine Tabelle dargestellt, in der unteren die Details zu einem selektierten Datensatz. <br/> Die
 * Ableitungen muessen das TableModel mit Daten fuellen sowie die Methode {@link #objectSelected(Object)} implementieren
 * (wird aufgerufen, wenn ein Datensatz in der Tabelle selektiert wird). <br/> <br/> Das Panel fuer die
 * Detail-Darstellung ist ueber die Methode {@link #getDetailPanel()} erreichbar.
 *
 * @param <V>     Typ des Bauauftrag-View, der die Daten des aktuellen Bauauftrags enthaelt
 * @param <T>     Typ des Value-Object mit den geladenen Details zum Bauauftrag
 * @param <MODEL> Typ des zu verwendenden TableModel
 */
public abstract class AbstractBauauftragPanel<V extends AbstractBauauftragView, T, MODEL extends AKMutableTableModel<V>>
        extends AbstractVerlaufPanel
        implements AKTableOwner, AKObjectSelectionListener, AKFilterTableModelListener,
        LoadBauauftragTable<V>, LoadBauauftragDetails<V, T> {

    protected static final String BTN_UEBERNAHME = "uebernahme";
    protected static final String BTN_PRINT = "print";
    protected static final String BTN_PRINT_COMPACT = "print.compact";
    protected static final String BTN_BEMERKUNGEN = "bemerkungen";
    protected static final String BTN_BA_ERLEDIGEN = "ba.erledigen";
    protected static final String SHOW_CPS_TX_HISTORY = "show.cps.tx.history";
    protected static final String CREATE_CPS_TX = "create.cps.tx";
    private static final Logger LOGGER = Logger.getLogger(AbstractBauauftragPanel.class);
    private static final String RB_ACTIVE_BAUAUFTRAEGE = "active.bauauftraege";
    private static final String RB_ALL_BAUAUFTRAEGE = "all.bauauftraege";
    private static final String BTN_FILTER = "filter";
    private static final String BTN_CLEAR_FILTER = "clear.filter";
    private static final String BTN_REFRESH = "refresh";

    private static final long serialVersionUID = 6934760125316531844L;

    private final Class<? extends FilterEventListener> filterEventListenerClass;
    private final boolean highlightSpecials;
    private final String colNiederlassung;
    private final Class<V> viewClass;
    protected Map<Long, Color> portierung2Color = null;
    protected MODEL tableModel = null;
    private AKJTable tbVerlauf = null;
    private AKJPanel detailPanel = null;
    private AKJPanel filterPanel = null;
    private AKJPanel optionalFilterPanel = null;
    private VerlaufActionPanel vaPanel = null;
    private VerlaufsBemerkungenPanel vbPanel = null;
    private VerlaufOrdersPanel voPanel;
    private AKJPanel buttonPanel = null;
    private AKJTabbedPane tabPane = null;
    private AKJComboBox cbAbteilung = null;
    private AKJRadioButton rbAlleBAs;
    private AKJComboBox cbNiederlassung = null;
    private AKJDateComponent dcRealisierungFrom = null;
    private AKJDateComponent dcRealisierungTo = null;
    private FilterEventListener filterEventListener = null;
    private V selectedView = null;

    private boolean showAbteilungsFilter = false;
    private boolean initDone = false;

    /**
     * Konstruktor, um das BA-Panel fuer "Standard-Abteilungen" zu erzeugen.
     *
     * @param resource          Resource-Datei
     * @param abteilungId       Id der Abteilung, die angezeigt wird
     * @param highlightSpecials Flag, ob spezielle BAs hervorgehoben werden sollen
     * @param colNiederlassung  Spalte mit der Niederlassung aus dem Tabellen-Modell zum Filtern
     */
    public AbstractBauauftragPanel(String resource, Long abteilungId, boolean highlightSpecials, String colNiederlassung) {
        this(resource, abteilungId, highlightSpecials, colNiederlassung, null);
    }

    /**
     * Konstruktor, um das "universelle" BA-Panel inkl. Abteilungs-Filter zu erzeugen.
     */
    public AbstractBauauftragPanel(String resource, boolean highlightSpecials, String colNiederlassung) {
        this(resource, null, highlightSpecials, colNiederlassung, null);
        showAbteilungsFilter = true;
    }

    /**
     * Erzeugt diesen Panel mit dem uebergebenen {@link FilterEventListener}. Siehe auch {@link
     * AbstractBauauftragPanel#AbstractBauauftragPanel(String, Long, boolean, String)}.
     *
     * @param resource                 Resource-Datei
     * @param abteilungId              Id der Abteilung, die angezeigt wird
     * @param highlightSpecials        Flag, ob spezielle BAs hervorgehoben werden sollen
     * @param colNiederlassung         Spalte mit der Niederlassung aus dem Tabellen-Modell zum Filtern
     * @param filterEventListenerClassIn Klasse fuer eigenen Event-Listner fuer die Filter-Komponenten
     * @throws ClassCastException falls uebergebene Filter-Event-Listener-Klasse nicht vom Typ {@link
     *                            FilterEventListener}
     */
    public AbstractBauauftragPanel(String resource, Long abteilungId, boolean highlightSpecials, String colNiederlassung,
            Class<?> filterEventListenerClassIn) {
        super(resource, abteilungId);
        Class<?> filterEventListenerClass = filterEventListenerClassIn;
        if (filterEventListenerClass == null) {
            filterEventListenerClass = FilterEventListener.class;
        }
        @SuppressWarnings("unchecked") // safe cast as long as first type argument is the view containing the Bauauftraege
                Class<V> v = (Class<V>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        viewClass = v;

        this.highlightSpecials = highlightSpecials;
        this.colNiederlassung = colNiederlassung;

        if (!FilterEventListener.class.isAssignableFrom(filterEventListenerClass)) {
            throw new ClassCastException("Supplied filter event listener not assignable from " + FilterEventListener.class);
        }
        @SuppressWarnings("unchecked") // safe cast because it just exists at compile-time and is checked above ;)
                Class<? extends FilterEventListener> f = (Class<? extends FilterEventListener>) filterEventListenerClass;
        this.filterEventListenerClass = f;
    }

    protected boolean showNiederlassungsFilter() {
        return true;
    }

    /**
     * Erzeugt die Standard-GUI mit Filter-Panel und Tabelle sowie Client-Bereich fuer Details.
     */
    @Override
    protected void createGUI() {
        SwingFactory swingFactory = SwingFactory.getInstance("de/augustakom/hurrican/gui/verlauf/resources/AbstractBauauftragPanel.xml");

        AKJPanel basePanel = new AKJPanel(new BorderLayout());
        detailPanel = new AKJPanel();

        AKTableListener tableListener = new AKTableListener(this, false);
        tbVerlauf = createTable();

        tbVerlauf.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbVerlauf.addMouseListener(tableListener);
        tbVerlauf.addKeyListener(tableListener);
        tbVerlauf.addPopupAction(new TableOpenOrderAction());
        tbVerlauf.addPopupAction(new OpenVbzHistoryAction());
        SplitVerlaufAction splitVerlaufAction = new SplitVerlaufAction();
        splitVerlaufAction.setParentClassName(getClassName());
        tbVerlauf.addPopupAction(splitVerlaufAction);
        tbVerlauf.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AKJScrollPane spTable = new AKJScrollPane(tbVerlauf);
        spTable.setPreferredSize(new Dimension(500, 250));

        createFilterPanel(swingFactory);

        basePanel.add(filterPanel, BorderLayout.NORTH);
        basePanel.add(spTable, BorderLayout.CENTER);

        AKJSplitPane splitPane = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane.setTopComponent(basePanel);
        vaPanel = new VerlaufActionPanel();
        vbPanel = new VerlaufsBemerkungenPanel(this, true);
        voPanel = new VerlaufOrdersPanel();
        buttonPanel = new AKJPanel(new GridBagLayout());

        tabPane = new AKJTabbedPane();
        tabPane.addTab("Details", detailPanel);
        tabPane.addTab("Bemerkungen", vbPanel);
        tabPane.addTab("Leistungs-Differenz", vaPanel);

        AKJPanel bottom = new AKJPanel(new BorderLayout());
        bottom.add(tabPane, BorderLayout.CENTER);
        bottom.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(bottom);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(1d);  // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        manageGUI(getClassName(), splitVerlaufAction);
    }

    private void createFilterPanel(SwingFactory swingFactory) {
        AKJLabel lblAbteilung = swingFactory.createLabel("abteilung");
        AKJLabel lblNiederlassung = swingFactory.createLabel("niederlassung");

        ButtonGroup rbGroup = new ButtonGroup();
        AKJRadioButton rbAktiveBAs = swingFactory.createRadioButton(RB_ACTIVE_BAUAUFTRAEGE, getFilterEventListener(this), true, rbGroup);
        rbAlleBAs = swingFactory.createRadioButton(RB_ALL_BAUAUFTRAEGE, getFilterEventListener(this), false, rbGroup);

        cbAbteilung = swingFactory.createComboBox("abteilung", new AKCustomListCellRenderer<>(Abteilung.class, Abteilung::getName));
        cbAbteilung.addItemListener(getFilterEventListener(this));
        cbNiederlassung = swingFactory.createComboBox("niederlassung", new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));
        cbNiederlassung.addItemListener(getFilterEventListener(this));

        AKJButton btnRefresh = swingFactory.createButton(BTN_REFRESH, getFilterEventListener(this), null);

        // @formatter:off
        filterPanel = new AKJPanel(new GridBagLayout());
        int nextGridBagXIdx = 0;
        if (showAbteilungsFilter) {
            filterPanel.add(lblAbteilung    , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
            filterPanel.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
            filterPanel.add(cbAbteilung     , GBCFactory.createGBC( 20,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        filterPanel.add(rbAktiveBAs         , GBCFactory.createGBC(  1,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL, 20));
        filterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
        filterPanel.add(rbAlleBAs           , GBCFactory.createGBC(  1,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
        if (showNiederlassungsFilter()) {
            filterPanel.add(lblNiederlassung, GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            filterPanel.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
            filterPanel.add(cbNiederlassung , GBCFactory.createGBC( 20,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            filterPanel.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
        }
        filterPanel.add(btnRefresh          , GBCFactory.createGBC( 20,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        nextGridBagXIdx = childComponents2FilterPanel(filterPanel, nextGridBagXIdx);
        filterPanel.add(new AKJPanel()      , GBCFactory.createGBC(100,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        // optional Filter-Panel
        AKJLabel lblRealTerminFrom = swingFactory.createLabel("realisierung.from");
        dcRealisierungFrom = swingFactory.createDateComponent("realisierung.from");
        AKJLabel lblRealTerminTo = swingFactory.createLabel("realisierung.to");
        dcRealisierungTo = swingFactory.createDateComponent("realisierung.to");
        AKJButton btnFilter = swingFactory.createButton(BTN_FILTER, getFilterEventListener(this));
        AKJButton btnClearFilter = swingFactory.createButton(BTN_CLEAR_FILTER, getFilterEventListener(this));

        // @formatter:off
        optionalFilterPanel = new AKJPanel(new GridBagLayout());
        optionalFilterPanel.setVisible(false);
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        optionalFilterPanel.add(lblRealTerminFrom   , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        optionalFilterPanel.add(dcRealisierungFrom  , GBCFactory.createGBC( 10, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        optionalFilterPanel.add(lblRealTerminTo     , GBCFactory.createGBC(  0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        optionalFilterPanel.add(dcRealisierungTo    , GBCFactory.createGBC( 10, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0, 0, 8, 0, 1, 1, GridBagConstraints.NONE));
        optionalFilterPanel.add(btnFilter           , GBCFactory.createGBC( 10, 0, 9, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(  0, 0,10, 0, 1, 1, GridBagConstraints.NONE));
        optionalFilterPanel.add(btnClearFilter      , GBCFactory.createGBC( 10, 0,11, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        optionalFilterPanel.add(new AKJPanel()      , GBCFactory.createGBC(100, 0,12, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        filterPanel.add(optionalFilterPanel, GBCFactory.createGBC(100, 0, 0, 1, nextGridBagXIdx, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * In der ueberschriebenen Version dieser Methode koennen in die erste Zeile des Filter-Panel (oben) zusaetzliche
     * eigene Komponenten hinzugefuegt werden. Dazu wird diese Methode von {@link de.augustakom.hurrican.gui.verlauf.AbstractBauauftragPanel#createGUI()}
     * aufgerufen. Dabei sollte mit einem Abstandshalter (z.B. ein {@link AKJPanel}) begonnen, jedoch mit einer
     * Komponente geendet werden. Wichtig ist, dass der korrekte X-Wert zurueckgegeben wird, damit noch Abstandshalter
     * etc. der abstrakten Klasse hinzugefuegt werden koennen.
     *
     * @param filterPanel     das Panel mit dem Grid-Bag-Layout, zum Hinzufuegen zusaetzlicher GUI-Komponenten
     * @param nextGridBagXIdx der X-Wert fuer die naechste Komponente im Grid-Bag-Layout (mit {@code nextGridBagXIdx++}
     *                        nutzen und dann einfach zurueck geben)
     * @return den naechsten X-Wert fuer das Grid-Bag-Layout nachdem die Komponenten hinzugefuegt wurden
     */
    protected int childComponents2FilterPanel(AKJPanel filterPanel, int nextGridBagXIdx) {
        return nextGridBagXIdx;
    }

    /**
     * Initialisiert die Daten des Panel.
     */
    protected void init() {
        try {
            portierung2Color = new HashMap<>();
            portierung2Color.put(Portierungsart.PORTIERUNG_STANDARD, null);
            portierung2Color.put(Portierungsart.PORTIERUNG_EXPORT, new Color(153, 0, 204));
            portierung2Color.put(Portierungsart.PORTIERUNG_SONDER, Color.red);

            clearOptionalFilter();

            NiederlassungService ns = getCCService(NiederlassungService.class);
            if (showAbteilungsFilter) {
                List<Abteilung> abteilungen = ns.findAbteilungen4UniversalGui();
                cbAbteilung.addItems(abteilungen, true, Abteilung.class);

                Long abtIdToSelect = HurricanSystemRegistry.instance().getUserConfigAsLong(
                        HurricanGUIConstants.ABTEILUNG_4_BAUAUFTRAG_GUI);
                if (abtIdToSelect != null) {
                    cbAbteilung.selectItem("getId", Abteilung.class, abtIdToSelect);
                    reloadTableData();
                }
            }

            if (showNiederlassungsFilter()) {
                // Lade Niederlassungen
                List<Niederlassung> niederlassungen = ns.findNiederlassungen();
                cbNiederlassung.addItems(niederlassungen, Boolean.TRUE, Niederlassung.class);
                Niederlassung emptyItem = (Niederlassung) cbNiederlassung.getItemAt(0);
                if (emptyItem.getId() == null) {
                    emptyItem.setName("Alle");
                }
                selectNiederlassung();
            }

            initDone = true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Fuegt die Buttons <code>buttons</code> dem Panel hinzu.
     */
    protected void addButtons(AKJButton[] buttons) {
        int x = -1;
        for (AKJButton button : buttons) {
            buttonPanel.add(button, GBCFactory.createGBC(0, 0, ++x, 0, 1, 1, GridBagConstraints.NONE));
        }
        buttonPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, ++x, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * Setzt alle Buttons auf dem Button-Panel auf enabled bzw. disabled
     */
    protected void enableButtons(boolean enable) {
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof AKJButton) {
                component.setEnabled(enable);
                if (enable) {
                    manageGUI(this.getClassName(), (AKJButton) component);
                }
            }
        }
    }

    @Override
    public final void loadData() {
        final SwingWorker<List<V>, Void> worker = new SwingWorker<List<V>, Void>() {
            final Long localAbteilungId = getAbteilungId();
            final Date realisierungFrom = (rbAlleBAs.isSelected()) ? dcRealisierungFrom.getDate(null) : null;
            final Date realisierungTo = (rbAlleBAs.isSelected()) ? dcRealisierungTo.getDate(null) : null;

            @Override
            protected List<V> doInBackground() throws Exception {
                if (rbAlleBAs.isSelected()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(realisierungTo);
                    cal.add(Calendar.DAY_OF_MONTH, -31);
                    if (cal.getTime().after(realisierungFrom)) {
                        throw new IllegalArgumentException("Die angegebene Datumsspanne ist zu gross");
                    }
                }
                return loadTableData(localAbteilungId, realisierungFrom, realisierungTo);
            }

            @Override
            protected void done() {
                try {
                    updateGuiByTableData(get());
                    filterNiederlassung(getSelectedNiederlassung());
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
        setWaitCursor();
        showProgressBar(PROGRESS_BAUAUFTRAEGE_TEXT);
        clearTable();
        worker.execute();
    }

    protected Map<Long, String> getProjektleiterById() throws ServiceNotFoundException, FindException {
        Map<Long, String> projektleiterById = new HashMap<>();
        try {
            AKUserService userService = getAuthenticationService(AKUserService.class);
            List<AKUser> allProjektleiter = userService.findAllProjektleiter();
            for (AKUser akUser : allProjektleiter) {
                String userName = akUser.getNameAndFirstName();
                if ((null != userName) && !"".equals(userName)) {
                    projektleiterById.put(akUser.getId(), userName);
                }
            }
        }
        catch (AKAuthenticationException e) {
            throw new FindException(e.getMessage(), e);
        }
        return projektleiterById;
    }

    @Override
    public void clearTable() {
        setSelectedView(null);
        tableModel.setData(null);
        clearDetails();
    }

    public V getSelectedView() {
        return selectedView;
    }

    public Optional<V> getSelectedViewOptional()    {
        return Optional.ofNullable(selectedView);
    }

    public void setSelectedView(V selectedView) {
        this.selectedView = selectedView;
    }

    /**
     * Setzt den Verlauf einer Abteilung auf 'erledigt'.
     *
     * @param verlaufView Verlauf-View mit den benoetigten Daten.
     * @return der erledigte VerlaufAbteilung-Datensatz.
     */
    protected VerlaufAbteilung verlaufErledigen(V verlaufView) throws HurricanGUIException {
        try {
            if ((verlaufView != null) && (verlaufView.getVerlaufAbtId() != null)) {
                setWaitCursor();
                BAService baService = getCCService(BAService.class);
                VerlaufAbteilung va = baService.findVerlaufAbteilung(verlaufView.getVerlaufAbtId());

                // Dialog fuer Benutzer-/Datums-Eingabe
                BauauftragErledigenDialog dlg = new BauauftragErledigenDialog(
                        va, HurricanSystemRegistry.instance().getCurrentUser(),
                        verlaufView.getRealisierungstermin());
                Object value = DialogHelper.showDialog(this, dlg, true, true);

                if (value instanceof Integer && value.equals(JOptionPane.OK_OPTION)) {
                    return verlaufErledigen(va,
                            dlg.getBearbeiter(), dlg.getBemerkung(), dlg.getRealisierungstermin(), dlg.getZusatzAufwand4Fttx(),
                            dlg.isNotPossible(), dlg.getNotPossibleReason());
                }
                return null;
            }
            throw new HurricanGUIException("Es wurde keine Verlaufs-ID angegeben. Verlauf kann nicht erledigt werden.");
        }
        catch (HurricanGUIException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Alle ausgewaehlten Verlaeufe erledigen.
     */
    protected void verlaeufeErledigen() {
        try {
            setWaitCursor();

            int[] selection = tbVerlauf.getSelectedRows();

            List<V> incompleteViews = new ArrayList<>();
            if (!CollectionTools.isEmpty(incompleteViews)) {
                // Warnmeldung, falls Daten fehlen
                StringBuilder msg = new StringBuilder();
                msg.append("Bei folgenden Leitungen fehlen die VPI/VCI Daten bzw. die VLAN-ID: ");
                msg.append(SystemUtils.LINE_SEPARATOR);
                for (V view : incompleteViews) {
                    msg.append(view.getVbz());
                    msg.append(SystemUtils.LINE_SEPARATOR);
                }
                MessageHelper.showInfoDialog(getMainFrame(), msg.toString(), null, true);
            }
            else {
                // Daten vollstaendig --> Verlaeufe erledigen
                @SuppressWarnings("unchecked")
                MODEL tableModelSorted = (MODEL) getTable().getModel();
                List<V> toFinish = new ArrayList<>();
                for (int row : selection) {
                    toFinish.add(viewClass.cast(tableModelSorted.getDataAtRow(row)));
                }

                List<VerlaufAbteilung> vas = verlaeufeErledigen(toFinish);
                if (!CollectionTools.isEmpty(vas)) {
                    for (V avv : toFinish) {
                        for (VerlaufAbteilung vAbt : vas) {
                            if (NumberTools.equal(avv.getVerlaufAbtId(), vAbt.getId())) {
                                avv.setVerlaufAbtStatusId(VerlaufStatus.STATUS_ERLEDIGT);
                                avv.setGuiFinished(true);
                                avv.notifyObservers(true);
                                break;
                            }
                        }
                    }
                }
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

    /**
     * @see #verlaeufeErledigen(java.util.List) (AbstractVerlaufView) Im Gegensatz zu oben genannter Methode koennen
     * ueber diese Methode gleichzeitig mehrere Verlauefe abgeschlossen werden. <br> Voraussetzung: Alle Verlaeufe haben
     * das gleiche vorgegebene Realisierungsdatum! <p>Es wird kein Warte-Cursor gesetzt, muss beim Aufrufer gesetzt
     * werden
     */
    protected List<VerlaufAbteilung> verlaeufeErledigen(List<V> verlaufViews) throws HurricanGUIException {
        if (CollectionTools.isEmpty(verlaufViews)) {
            throw new HurricanGUIException("Es wurde kein Verlauf angegeben. Verlauf kann nicht erledigt werden!");
        }

        Date definedRealDate = null;
        for (V avv : verlaufViews) {
            if (definedRealDate == null) {
                definedRealDate = avv.getRealisierungstermin();
            }
            else {
                if (!DateTools.isDateEqual(definedRealDate, avv.getRealisierungstermin())) {
                    throw new HurricanGUIException("Es koennen nur Verlaeufe mit gleichem vorgegebenen " +
                            "Realisierungsdatum zusammen abgeschlossen werden!");
                }
            }
        }

        try {
            V verlaufView = verlaufViews.get(0);

            BAService baService = getCCService(BAService.class);
            VerlaufAbteilung va = baService.findVerlaufAbteilung(verlaufView.getVerlaufAbtId());
            if (va == null) {
                throw new HurricanGUIException("Verlaufs-Datensatz für die Abteilung wurde nicht gefunden!");
            }

            // Dialog fuer Benutzer-/Datums-Eingabe
            BauauftragErledigenDialog dlg = new BauauftragErledigenDialog(
                    va, HurricanSystemRegistry.instance().getCurrentUser(),
                    verlaufView.getRealisierungstermin(), false);
            Object value = DialogHelper.showDialog(this, dlg, true, true);

            List<VerlaufAbteilung> retVal = null;
            if (value instanceof Integer && value.equals(JOptionPane.OK_OPTION)) {
                retVal = new ArrayList<>();
                for (V avv : verlaufViews) {
                    VerlaufAbteilung va2Finish = baService.findVerlaufAbteilung(avv.getVerlaufAbtId());

                    VerlaufAbteilung vaFinished = verlaufErledigen(va2Finish,
                            dlg.getBearbeiter(), null, dlg.getRealisierungstermin(), null,
                            dlg.isNotPossible(), dlg.getNotPossibleReason());

                    retVal.add(vaFinished);
                }
            }
            return retVal;
        }
        catch (HurricanGUIException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    // @formatter:off
    /**
     * Setzt den Verlauf einer Abteilung auf 'erledigt'. <br/>
     * Sofern es sich bei der Abteilung um 'FFM' handelt wird auch noch {@link WorkforceService#deleteOrder(DeleteOrder)}
     * an das FFM System geschickt.
     *
     * @param va                     VerlaufAbteilung-Datensatz
     * @param bearbeiter             Bearbeiter, der den BA erledigt hat
     * @param bemerkung              Bemerkung fuer den VerlaufAbteilungs-Datensatz
     * @param realDate               Realisierungsdatum, zu dem der BA wirklich erledigt wurde
     * @param zusatzAufwand          Aufwand der bei Realisierung anfiel
     */
    // @formatter:on
    protected VerlaufAbteilung verlaufErledigen(VerlaufAbteilung va, String bearbeiter,
            String bemerkung, Date realDate, Long zusatzAufwand, Boolean notPossible,
            Long notPossibleReasonRefId) throws HurricanGUIException {
        try {
            BAService baService = getCCService(BAService.class);

            VerlaufAbteilung verlAbt;
            if (Abteilung.FFM.equals(va.getAbteilungId())) {
                verlAbt = baService.finishVerlauf4FFMWithDeleteOrder(va, bearbeiter, bemerkung,
                        realDate, HurricanSystemRegistry.instance().getSessionId(), zusatzAufwand,
                        notPossible, notPossibleReasonRefId);
            }
            else {
                verlAbt = baService.finishVerlauf4Abteilung(va, bearbeiter, bemerkung,
                        realDate, HurricanSystemRegistry.instance().getSessionId(), zusatzAufwand,
                        notPossible, notPossibleReasonRefId);
            }

            if (verlAbt != null) {
                return verlAbt;
            }
            throw new HurricanGUIException("Ungueltiges Result!");
        }
        catch (HurricanGUIException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Ermittelt eine Physik-Information die evtl. zu dem Verlauf vorhanden ist.
     */
    protected String getPhysikInfo(V verlauf) {
        if (verlauf != null) {
            try {
                BAService baService = getCCService(BAService.class);
                Verlauf baVerlauf = baService.findVerlauf(verlauf.getVerlaufId());
                return baService.getPhysikInfo4Verlauf(baVerlauf);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Laedt die Referenz-Werte fuer CPS-Tx Stati und uebergibt sie dem angegebenen Table-Model.
     *
     * @param tbMdl TableModel, das die Referenz-Werte uebergeben bekommen soll
     * @param colCpsTxState Name der Spalte, in die die Referenz-Werte eingetragen werden sollen.
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    protected void loadCPSTxStateReferences(AKReferenceAwareTableModel<V> tbMdl, String colCpsTxState)
            throws ServiceNotFoundException, FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        List<Reference> cpsTxStateReference = referenceService.findReferencesByType(Reference.REF_TYPE_CPS_TX_STATE, Boolean.FALSE);
        Map<Long, Reference> cpsTxStateRefMap = new HashMap<>();
        CollectionMapConverter.convert2Map(cpsTxStateReference, cpsTxStateRefMap, "getId", null);

        Optional<Integer> index = tbMdl.getColumnIndex(colCpsTxState);
        if (index.isPresent()) {
            tbMdl.addReference(index.get(), cpsTxStateRefMap, "strValue");
        } else {
            MessageHelper.showMessageDialog(getMainFrame(), String.format("Für die CPS Stati konnten die Referenzwerte "
                    + "nicht ermittelt werden. Die Spalte %s wird daher nicht korrekt angezeigt werden.", colCpsTxState),
                    "CPS Stati Referenzwerte", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Zeigt den CPS-Tx History Dialog zu dem aktuell selektieren Auftrag an.
     */
    protected void showCPSTxHistory(V baView) {
        if (baView != null) {
            CPSHistoryDialog historyDialog = new CPSHistoryDialog(baView);
            DialogHelper.showDialog(getMainFrame(), historyDialog, true, true);
        }
        else {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Kein Bauauftrag selektiert", "keine Selektion", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Erstellt fuer den aktuellen Bauauftrag eine CPS-Transaction
     */
    protected void createCPSTx(V baView) {
        if (baView != null) {
            try {
                setWaitCursor();
                if (BooleanTools.nullToFalse(baView.getCpsProvisioning())) {
                    CPSService cpsService = getCCService(CPSService.class);
                    List<CPSTransaction> cpsTx4Ba = cpsService.findCPSTransactions4Verlauf(baView.getVerlaufId());
                    if (CollectionTools.isNotEmpty(cpsTx4Ba)) {
                        for (CPSTransaction cpsTx : cpsTx4Ba) {
                            if (cpsTx.isActive()) {
                                throw new HurricanGUIException(
                                        "Fuer den Bauauftrag existiert noch eine aktive CPS-Transaction!");
                            }
                        }
                    }
                }

                Long sessionId = HurricanSystemRegistry.instance().getSessionId();

                BAService baService = getCCService(BAService.class);
                Verlauf bauauftrag = baService.findVerlauf(baView.getVerlaufId());

                CPSService cpsService = getCCService(CPSService.class);
                CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction4BA(
                        bauauftrag, sessionId);

                if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                    StringBuilder cpsTxIds = new StringBuilder();
                    for (CPSTransaction cpsTx : cpsTxResult.getCpsTransactions()) {
                        cpsService.sendCPSTx2CPS(cpsTx, sessionId);
                        if (cpsTxIds.length() > 0) {
                            cpsTxIds.append(", ");
                        }
                        cpsTxIds.append(String.format("%s", cpsTx.getId()));
                    }

                    MessageHelper.showInfoDialog(getMainFrame(),
                            "CPS-Transaction erstellt und an den CPS gesendet.\nCPS-Transaction ID(s): {0}", cpsTxIds);
                }
                else {
                    String warnings = (cpsTxResult.getWarnings() != null)
                            ? cpsTxResult.getWarnings().getWarningsAsText() : null;
                    throw new HurricanServiceCommandException(warnings);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
        else {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Kein oder mehr als ein Bauauftrag selektiert",
                    "ungültige Selektion", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Funktion verschiebt den Realisierungstermin eines Bauauftrags
     *
     * @param view View mit Bauauftragsdaten
     */
    protected void changeRealDate(V view) throws HurricanGUIException {
        if (view == null) { throw new HurricanGUIException("Kein Datensatz ausgewählt"); }
        try {
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            ProduktService produktService = getCCService(ProduktService.class);
            BAService baService = getCCService(BAService.class);

            Produkt prod = produktService.findProdukt(view.getProduktId());
            AuftragTechnik at = auftragService.findAuftragTechnikByAuftragId(view.getAuftragId());
            Verlauf verlauf = baService.findVerlauf(view.getVerlaufId());

            // Auftraege im VPN duerfen immer verschoben werden
            if (!BooleanTools.nullToFalse(prod.getBaTerminVerschieben()) && !((at != null) && (at.getVpnId() != null))) {
                MessageHelper.showInfoDialog(this, "Terminverschiebung bei diesem Produkt nicht möglich");
                return;
            }

            // Dialog fuer Termineingabe
            AKDateSelectionDialog dlg = new AKDateSelectionDialog("Terminverschiebung", null, "Gesamtrealisierungstermin:");
            Object dlgResult = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            Date realDate = ((dlgResult instanceof Date) ? (Date) dlgResult : null);

            if (realDate != null) {
                // Lade Verlaufabteilung fuer AM un DISPO/NP und erzeuge SelectAbteilung-Objekt
                List<SelectAbteilung4BAModel> abts = new ArrayList<>();
                List<VerlaufAbteilung> vas = baService.findVerlaufAbteilungen(view.getVerlaufId());
                for (VerlaufAbteilung va : vas) {
                    if ((va != null)
                            && NumberTools.isIn(va.getAbteilungId(), new Long[] { Abteilung.AM, Abteilung.DISPO,
                            Abteilung.NP })) {
                        SelectAbteilung4BAModel model = new SelectAbteilung4BAModel();
                        model.setAbteilungId(va.getAbteilungId());
                        model.setNiederlassungId(va.getNiederlassungId());
                        model.setRealDate(realDate);
                        abts.add(model);
                    }
                }
                if ((verlauf != null) && !(NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.BEI_DISPO)
                        || NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.KUENDIGUNG_BEI_DISPO))) {
                    VerlaufAbtAuswahlDialog aad = new VerlaufAbtAuswahlDialog(true, view.getVerlaufId());
                    aad.loadData4Terminverschiebung(view.getVerlaufId(), realDate);
                    Object result = DialogHelper.showDialog(getMainFrame(), aad, true, true);
                    if ((result instanceof List<?>) && !((List<?>) result).isEmpty()) {
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        List<SelectAbteilung4BAModel> list = (List) result;
                        abts.addAll(list);
                    } else if(DialogHelper.wasDialogCancelled(result)) {
                        return;
                    }
                }
                // Terminverschiebung
                baService.changeRealDate(view.getVerlaufId(), realDate,
                        HurricanSystemRegistry.instance().getSessionId(), abts);
                MessageHelper.showMessageDialog(getMainFrame(),
                        "Termin des Bauauftrags wurde verschoben.", "Abgeschlossen", JOptionPane.INFORMATION_MESSAGE);

                view.setRealisierungstermin(realDate);
                view.notifyObservers(true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    /**
     * Funktion weist den Bauauftrag der Netzplanung zu.
     *
     * @param view View mit Bauauftragsdaten
     */
    protected void assignBA2NP(V view) throws HurricanGUIException {
        if (view == null) {
            throw new HurricanGUIException("Kein Datensatz ausgewählt");
        }
        AKLoginContext logCtx = (AKLoginContext) HurricanSystemRegistry.instance().getValue(
                HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
        if (logCtx == null) {
            throw new HurricanGUIException("Aktueller Benutzer konnte nicht ermittelt werden!");
        }
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("assign.to.np.msg"),
                getSwingFactory().getText("assign.to.np.title"));
        if (option == JOptionPane.YES_OPTION) {
            try {
                BAService baService = getCCService(BAService.class);

                VerlaufAbteilung va = baService.findVerlaufAbteilung(view.getVerlaufAbtId());
                va.setAbteilungId(Abteilung.NP);
                baService.saveVerlaufAbteilung(va);

                MessageHelper.showMessageDialog(getMainFrame(),
                        "Bauauftrag wurde der Netzplanung zugewiesen", "Abgeschlossen",
                        JOptionPane.INFORMATION_MESSAGE);

                view.setVerlaufAbtId(va.getAbteilungId());
                view.notifyObservers(true);
                view.setGuiFinished(true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
                throw new HurricanGUIException(e.getMessage(), e);
            }
        }
    }

    /**
     * Zeigt die Rufnummern zu dem Auftrag in einem Dialog an.
     *
     * @param verlaufView Verlaufs-View mit den benoetigten Daten.
     */
    protected void showRufnummern(final V verlaufView) {
        if (verlaufView != null) {
            if (verlaufView.getAuftragNoOrig() == null) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Zu diesem Auftrags-Typ können keine Rufnummern angezeigt werden!", null, true);
            }
            else {
                RufnummerDialog rufnummerDlg = new RufnummerDialog(verlaufView.getAuftragNoOrig());
                DialogHelper.showDialog(getMainFrame(), rufnummerDlg, true, true);
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
        }
    }

    /**
     * Erstellt die Verlaufs-Uebersichtstabelle. Kann bei Bedarf ueberschrieben werden.
     */
    protected AKJTable createTable() {
        return new VerlaufTable(highlightSpecials);
    }

    /**
     * Gibt die Tabelle zurueck, in der die Verlaufs-Uebersicht dargestellt wird.
     */
    @Override
    protected AKJTable getTable() {
        return tbVerlauf;
    }

    /**
     * Gibt eine TabbedPane-Instanz zurueck, auf der Detail
     *
     * @return TabbedPane-Instanz, auf der Detail-Panels dargestellt werden koennen.
     */
    protected AKJTabbedPane getTabbedPane() {
        return tabPane;
    }

    /**
     * Gibt das Panel zurueck, auf dem die Details zu einem Datensatz dargestellt werden koennen.
     */
    protected AKJPanel getDetailPanel() {
        return detailPanel;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(final Object details) {
        if (tbVerlauf.getSelectedRowCount() > 1) {
            setSelectedView(null);
            clearDetails();
        }
        else if (viewClass.isAssignableFrom(details.getClass())) {
            if ((getSelectedView() == null) ||
                    !NumberTools.equal(getSelectedView().getVerlaufId(), viewClass.cast(details).getVerlaufId())) {

                final SwingWorker<T, Void> worker = new SwingWorker<T, Void>() {
                    @SuppressWarnings("unchecked")
                    final V localSelectedView = (V) details;

                    @Override
                    protected T doInBackground() throws Exception {
                        return loadDetails(localSelectedView);
                    }

                    @Override
                    protected void done() {
                        try {
                            updateGuiByDetails(localSelectedView, get());
                            updateAuftragsPanel(localSelectedView);
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

                setWaitCursor();
                showProgressBar(PROGRESS_DETAILS_TEXT);
                clearDetails();
                worker.execute();
            }
        }
        else {
            setSelectedView(null);
            clearDetails();
        }
    }

    private void updateAuftragsPanel(V details) {
        if ((details != null) && BooleanTools.nullToFalse(details.getHasSubOrders())) {
            tabPane.add("Aufträge", voPanel);
            voPanel.setVerlaufId(details.getVerlaufId());
        }
        else {
            tabPane.remove(voPanel);
            voPanel.setVerlaufId(null);
        }
    }

    @Override
    public void clearDetails() {
        GuiTools.cleanFields(getDetailPanel());
        updateAuftragsPanel(null);
        showBemerkungenInPanel(null);
        showAuftragTechLeistungen(null, null);
    }

    /**
     * Druckt die Projektierung fuer eine best. Abteilung aus.
     */
    protected void printBA(Long verlaufId, boolean withDetails, boolean compact) {
        if (verlaufId != null) {
            try {
                setWaitCursor();
                PrintVerlaufHelper printHelper = new PrintVerlaufHelper();
                printHelper.printVerlauf(verlaufId, false, withDetails, compact);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte wählen Sie zuerst einen Bauauftrag aus.");
        }
    }

    /**
     * Ermittelt die Auftrags-Leistungen zu einem bestimmten Verlauf.
     */
    protected void showAuftragTechLeistungen(Long verlaufId, List<Auftrag2TechLeistung> auftragTechLs) {
        try {
            vaPanel.setVerlaufActions(verlaufId, auftragTechLs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ermittelt die Bemerkungen zu einem bestimmten Verlauf
     */
    protected void showBemerkungenInPanel(Long verlaufId) {
        vbPanel.setVerlaufId(verlaufId);
    }

    /**
     * Ueberprueft, ob die Installation durch M-net/extern erfolgen soll. Dies ist der Fall, wenn: <ul> <li>dem Auftrag
     * mindestens ein Endgeraet mit Montageart 'AKom' zugeordnet ist. <li>der Verlauf einen entsprechenden
     * Installationstyp besitzt </ul>
     *
     * @param view View-Modell des aktuellen Verlaufs
     * @return true, wenn eine Installation durch M-net oder durch einen externen Dienstleister erfolgen soll
     */
    protected boolean hasExternInstallation(V view) {
        try {
            BAService baService = getCCService(BAService.class);
            return baService.hasExternInstallation(view.getVerlaufId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModelListener#tableFiltered()
     */
    @Override
    public void tableFiltered() {
        refreshRowCount();
    }

    /**
     * Aktualisiert den Row-Count im Frame-Header.
     */
    private void refreshRowCount() {
        setFrameTitle(StringUtils.trimToEmpty(getFrameTitle())
                + " - Anzahl: "
                + getTable().getRowCount());
    }

    /**
     * Oeffnet das Auftrags-Frame zu dem Model.
     */
    protected void openAuftrag(CCAuftragModel auftragModel) {
        AuftragDataFrame.openFrame(auftragModel);
    }

    /**
     * Selektiert die Niederlassung des Benutzers
     */
    private void selectNiederlassung() {
        if (!showNiederlassungsFilter()) {
            return;
        }

        try {
            // Ermittle User und selektiere Niederlassung
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService us = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            AKUser user = us.findUserBySessionId(sessionId);
            cbNiederlassung.selectItem("getId", Niederlassung.class, user.getNiederlassungId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Funktion filtert nach einer Niederlassung
     *
     * @param niederlassung die gewuenschte Niederlassung, nach der gefiltert werden soll
     */
    @SuppressWarnings("unchecked")
    private void filterNiederlassung(Niederlassung niederlassung) {
        if (tableModel != null && showNiederlassungsFilter()) {
            ((AKReflectionTableModel<T>) tableModel).removeFilter(FILTER_NIEDERLASSUNG);
            setSelectedView(null);
            // element mit Id null ist "empty" => alle Daten zurueckgeben
            if ((niederlassung != null) && (niederlassung.getId() != null)) {
                // Daten nach Niederlassung filtern
                Optional<Integer> index = tableModel.getColumnIndex(colNiederlassung);
                if (index.isPresent()) {
                    ((AKReflectionTableModel<T>) tableModel).addFilter(new FilterOperator(FILTER_NIEDERLASSUNG,
                            FilterOperators.EQ, niederlassung.getName(), index.get()));

                } else {
                    MessageHelper.showMessageDialog(getMainFrame(), String.format("Für die Niederlassung %s konnte kein "
                            + "Spalten Index ermittelt werden. Daher ist kein Filter für die Niederlassung gesetzt.",
                            colNiederlassung), "Niederlassung nicht gefiltert", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Liefert selektierte Niederlassung in der Combobox oder <code>null</code>
     */
    private Niederlassung getSelectedNiederlassung() {
        Object obj = (cbNiederlassung != null) ? cbNiederlassung.getSelectedItem() : null;
        return ((obj != null) && (obj instanceof Niederlassung)) ? (Niederlassung) obj : null;
    }


    private void reloadTableData() {
        try {
            clearTable();

            if (abteilungId != null) {
                loadData();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        getTable().repaint();
        showDetails(o);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (getSelectedView() == selection) {
            openAuftrag(getSelectedView());
        }
    }

    /**
     * Gibt eine Instanz der beim Konstruktur uebergebenen Filter-Event-Listener-Klasse zurueck
     *
     * @throws IllegalArgumentException falls Instanziierung der uebergebenen {@link FilterEventListener}-Klasse
     *                                  fehlschlaegt
     */
    protected <FILTER extends AbstractBauauftragPanel<V, T, MODEL>> FilterEventListener getFilterEventListener(FILTER parent) {
        if (filterEventListener == null) {
            try {
                @SuppressWarnings("unchecked")
                Constructor<FilterEventListener>[] constructors =
                        (Constructor<FilterEventListener>[]) filterEventListenerClass.getDeclaredConstructors();
                this.filterEventListener = constructors[0].newInstance(parent);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Could not create instance of %s for filtering", filterEventListenerClass.getName()), e);
            }
        }
        return filterEventListener;
    }

    /**
     * Setzt die eingestellten Filter auf deren Standardwerte zurueck
     */
    protected void clearOptionalFilter() {
        dcRealisierungFrom.setDate(new Date());
        dcRealisierungTo.setDate(new Date());
        clearTable();
    }

    protected VerlaufAbteilung verlaufUebernehmen(String currentBearbeiter) {
        VerlaufAbteilung verlAbt;
        VerlaufAbteilung verlaufAbteilung = null;
        String username = HurricanSystemRegistry.instance().getCurrentLoginName();

        try {
            BAService bas = getCCService(BAService.class);
            verlaufAbteilung = bas.findVerlaufAbteilung(getSelectedView().getVerlaufAbtId());
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage());
            // bei auftretenden Exceptions werden diese ignoriert, da die Uebernahme des Verlaufs nicht von diesem
            // Service abruf abhaengt
        }

        if (hasBearbeiterChangedSinceLastReferesh(currentBearbeiter, verlaufAbteilung)) {
            Integer messageReturn = MessageHelper.showYesNoCancelQuestion(getMainFrame(),
                    "Der Bauauftrag wird bereits durch " +
                            (verlaufAbteilung != null ? verlaufAbteilung.getBearbeiter() : "")
                            + " bearbeitet. Möchten Sie die Bearbeitung fortsetzen?", "Bauauftrag bearbeiten"
            );

            // Wenn trotzdem die Bearbeitung uebernommen werden soll, wird der Bearbeiter ueberschrieben
            if (messageReturn == JOptionPane.YES_OPTION) {
                verlAbt = super.verlaufUebernehmen(getSelectedView());
                verlAbt.setBearbeiter(username);
            }
            else {
                verlAbt = new VerlaufAbteilung();
                verlAbt.setBearbeiter(verlaufAbteilung != null ? verlaufAbteilung.getBearbeiter() : null);
                verlAbt.setVerlaufAbteilungStatusId(getSelectedView().getVerlaufAbteilungStatusId());
            }
        }
        else {
            verlAbt = super.verlaufUebernehmen(getSelectedView());
            verlAbt.setBearbeiter(username);
        }
        return verlAbt;
    }

    private boolean hasBearbeiterChangedSinceLastReferesh(String currentBearbeiter, VerlaufAbteilung verlaufAbteilung) {
        return ((currentBearbeiter == null) && (verlaufAbteilung != null) && (verlaufAbteilung.getBearbeiter() != null))
                || (((currentBearbeiter != null) && (verlaufAbteilung != null) && (verlaufAbteilung.getBearbeiter() != null))
                && !currentBearbeiter.equals(verlaufAbteilung.getBearbeiter()));
    }

    /**
     * Action- und Item-Listener fuer die Komponenten in den Filter-Panel (oben). Kann ueberschrieben werden und im
     * Konstruktor als Argument uebergeben werden (siehe {@link AbstractBauauftragPanel#AbstractBauauftragPanel(String,
     * Long, boolean, String, Class)}).
     */
    protected class FilterEventListener implements ActionListener, ItemListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final String actionCommand = e.getActionCommand();
            if (RB_ACTIVE_BAUAUFTRAEGE.equals(actionCommand)) {
                optionalFilterPanel.setVisible(false);
                clearTable();
                loadData();
            }
            else if (RB_ALL_BAUAUFTRAEGE.equals(actionCommand)) {
                optionalFilterPanel.setVisible(true);
                clearTable();
            }
            else if (BTN_FILTER.equals(actionCommand)) {
                loadData();
            }
            else if (BTN_CLEAR_FILTER.equals(actionCommand)) {
                clearOptionalFilter();
            }
            else if(BTN_REFRESH.equals(actionCommand)){
                reloadTableData();
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getSource() == cbNiederlassung && e.getStateChange() == ItemEvent.SELECTED) {
                filterNiederlassung(getSelectedNiederlassung());
                clearDetails();
            }
            else if (e.getSource() == cbAbteilung && e.getStateChange() == ItemEvent.SELECTED) {
                abteilungId = (cbAbteilung.getSelectedItem() instanceof Abteilung)
                    ? ((Abteilung) cbAbteilung.getSelectedItem()).getId() : null;

                if (initDone) {
                    HurricanSystemRegistry.instance().setUserConfig(
                            HurricanGUIConstants.ABTEILUNG_4_BAUAUFTRAG_GUI, abteilungId);
                }

                clearDetails();
                reloadTableData();
            }
        }
    }

    /* Action-Klasse, um den Dialog mit der Verbindungsbezeichnung-History zu oeffnen. */
    class OpenVbzHistoryAction extends AKAbstractAction {
        private static final long serialVersionUID = -8299846605714981763L;

        /**
         * Default-Konstruktor.
         */
        public OpenVbzHistoryAction() {
            super();
            setName("Verbindungsbezeichnungs-History oeffnen");
            setActionCommand("open.vbz.history");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if ((actionSrc instanceof MouseEvent) && (((MouseEvent) actionSrc).getSource() instanceof AKJTable)) {
                Object value = getTableSelection((MouseEvent) getValue(ACTION_SOURCE));
                if (value.getClass().isAssignableFrom(viewClass)) {
                    String vbz = viewClass.cast(value).getVbz();
                    if (StringUtils.isNotBlank(vbz)) {
                        VbzHistoryDialog dlg = new VbzHistoryDialog(vbz);
                        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    }
                }
            }
        }
    }

    /* Action-Klasse, um den Relisierungstermin zu verschieben. */
    class OpenTerminverschiebungAction extends AKAbstractAction {
        private static final long serialVersionUID = 3486081880268536640L;

        /**
         * Default-Konstruktor
         */
        public OpenTerminverschiebungAction() {
            super();
            setName("Termin verschieben");
            setActionCommand("open.termin");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if ((actionSrc instanceof MouseEvent) && (((MouseEvent) actionSrc).getSource() instanceof AKJTable)) {
                Object value = getTableSelection((MouseEvent) getValue(ACTION_SOURCE));
                if (viewClass.isAssignableFrom(value.getClass())) {
                    try {
                        changeRealDate(viewClass.cast(value));
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

    /* Action-Klasse, um den Bauauftrag zur Netzplanung zu verschieben. */
    class AssignBAToNetzplanungAction extends AKAbstractAction {

        private static final long serialVersionUID = -4882222340903619812L;

        /**
         * Default-Konstruktor
         */
        public AssignBAToNetzplanungAction() {
            super();
            setName("zur Netzplanung verschieben");
            setActionCommand("assign.np");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if ((actionSrc instanceof MouseEvent)
                    && (((MouseEvent) actionSrc).getSource() instanceof AKJTable)) {
                Object value = getTableSelection((MouseEvent) getValue(ACTION_SOURCE));
                if (viewClass.isAssignableFrom(value.getClass())) {
                    try {
                        assignBA2NP(viewClass.cast(value));
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }
}
