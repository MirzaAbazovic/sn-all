/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.06.2007 07:49:04
 */
package de.augustakom.hurrican.gui.tools.tal;

import static org.apache.commons.lang.StringUtils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import javax.swing.*;
import javax.swing.border.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.swing.table.FilterRelation;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaData;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByExtOrderNoDialog;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByVertragsnummerDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.AnlagenAnzeigePanel;
import de.augustakom.hurrican.gui.tools.tal.wita.AuftragZuordnungDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.common.service.HistoryService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AnlagenDto;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.UserTaskDetails;
import de.mnet.wita.model.validators.RufnummerPortierungCheck;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;

/**
 * Panel fuer die Darstellung und Bearbeitung von User-Tasks fuer abgebende Leitungen.
 */
public class AbgebendeLeitungenPanel extends AbstractServicePanel implements AKTableOwner, AKDataLoaderComponent,
        AKFilterTableModelListener, AKObjectSelectionListener {

    private static final long serialVersionUID = -5560115845797572819L;
    private static final Logger LOGGER = Logger.getLogger(AbgebendeLeitungenPanel.class);

    private static final String HISTORY = "history";
    private static final String ABSCHLIESSEN = "abschliessen";
    private static final String TASK_FREIGEBEN = "task.freigeben";
    private static final String TASK_BEARBEITEN = "task.bearbeiten";
    private static final String WIEDERVORLAGE = "wiedervorlage";
    private static final String NEGATIVE_RUEMPV = "negative.ruempv";
    private static final String POSITIVE_RUEMPV = "positive.ruempv";
    private static final String ASSIGN_ORDER = "assign.order";
    private static final String ALLE_TASKS = "alle.tasks";
    private static final String EIGENE_TASKS = "eigene.tasks";
    private static final String SAVE_ABGEBENDE_LEITUNGEN_USER_TASK = "save.abgebende.leitungen.user.task";
    private static final String BEMERKUNGEN = "bemerkungen";
    private static final String VTRNR = "vtrnr";
    private static final String LBZ = "lbz";
    private static final String LEITUNG = "leitung";
    private static final String ES_HVT = "es.hvt";
    private static final String ES_STRASSE = "es.strasse";
    private static final String ES_NAME = "es.name";
    private static final String ES_PLZ_ORT = "es.plz.ort";
    private static final String ES_ORT = "es.ort";
    private static final String ES_PLZ = "es.plz";
    private static final String ENDSTELLE = "endstelle";
    private static final String KUNDE_VORNAME = "kunde.vorname";
    private static final String KUNDE_NAME = "kunde.name";
    private static final String KUNDE = "kunde";
    private static final String ANLAGEN = "anlagen";

    private static final String EIGENE_TASKS_FILTER_NAME = "eigene.tasks.filter";
    private static final String COLUMN_NAME_STATUS = "Status";
    private static final String COLUMN_NAME_LAST_CHANGE = "Geändert am";
    private static final String COLUMN_NAME_TASK_BEARBEITER = "Task-Bearbeiter";
    private static final String COLUMN_NAME_BEARBEITER = "Bearbeiter";
    private static final int COLUMN_INDEX_TASK_BEARBEITER;
    private static final int COLUMN_INDEX_BEARBEITER;
    private static final ReflectionTableMetaData[] COLUMN_DEFINITIONS;

    static {
        COLUMN_DEFINITIONS = new ReflectionTableMetaData[] {
                new ReflectionTableMetaData("Prio", "prio", Boolean.class),
                new ReflectionTableMetaData("Auftragsnummer Taifun", "auftragNoOrig", Long.class),
                new ReflectionTableMetaData("Verbindungsbezeichnung (VBZ)", "vbz", String.class),
                new ReflectionTableMetaData("Carrier", "abgebendeLeitungenUserTask.aufnehmenderProvider", String.class),
                new ReflectionTableMetaData("Ref.Nr.", "externeAuftragsnummer", String.class),
                new ReflectionTableMetaData("Kue. Datum", "kueDatumAsString", String.class),
                new ReflectionTableMetaData("Empfangen", "empfangenAsString", String.class),
                new ReflectionTableMetaData("AKM-PV TV", "abgebendeLeitungenUserTask.terminverschiebung", Boolean.class),
                new ReflectionTableMetaData(COLUMN_NAME_STATUS, "lastMeldungStatusAsString", String.class),
                new ReflectionTableMetaData("Rückmeldung", "lastRuemPvStatusAsString", String.class),
                new ReflectionTableMetaData("Antwortfrist", "antwortFristAsString", String.class),
                new ReflectionTableMetaData(COLUMN_NAME_LAST_CHANGE, "letzteAenderung", Date.class),
                new ReflectionTableMetaData("Klärfall", "klaerfallSet", Boolean.class),
                new ReflectionTableMetaData(COLUMN_NAME_TASK_BEARBEITER, "taskBearbeiter", String.class),
                new ReflectionTableMetaData(COLUMN_NAME_BEARBEITER, "auftragBearbeiter", String.class),
                new ReflectionTableMetaData("Kdgbearbeiter M-net", "originalBearbeiter", String.class),
                new ReflectionTableMetaData("Niederlassung", "niederlassung", String.class)
        };

        final Function<String, Integer> findColumnIndexByName = name -> {
            for (int i = 0; i < COLUMN_DEFINITIONS.length; i++) {
                if (name.equals(COLUMN_DEFINITIONS[i].getColumnName())) {
                    return i;
                }
            }
            return -1;
        };

        COLUMN_INDEX_TASK_BEARBEITER = findColumnIndexByName.apply(COLUMN_NAME_TASK_BEARBEITER);
        COLUMN_INDEX_BEARBEITER = findColumnIndexByName.apply(COLUMN_NAME_BEARBEITER);
    }

    // GUI-Komponenten

    // AbgebendeLeitungen Tabelle
    private VorgangTable<AbgebendeLeitungenVorgang> tbAbgebendeLeitungenVorgang;
    private AKReferenceAwareTableModel<AbgebendeLeitungenVorgang> tbMdlAbgebendeLeitungenVorgaenge;

    // Funktionen
    private AKJButton btnWiedervorlage;
    private AKJButton btnTaskBearbeiten;
    private AKJButton btnTaskFreigeben;
    private AKJButton btnAbschliessen;
    private AKJButton btnHistory;
    private AKJButton btnRuemPvPos;
    private AKJButton btnRuemPvNeg;
    private AKJButton btnAssignOrder;

    // Kunde und Kontaktdaten
    private AKJTextField tfKName;
    private AKJTextField tfKVorname;

    // Endstelle
    private AKJTextField tfEName;
    private AKJTextField tfEEndstelle;
    private AKJTextField tfEPLZ;
    private AKJTextField tfEOrt;
    private AKJTextField tfEHvt;

    // Leitung
    private AKJTextField tfLBZ;
    private AKJTextField tfVtrnr;
    private AKJTextArea taBemerkungen;
    private AKJScrollPane spBemerkungen;
    private Border spBemerkungenBoarder;
    private AKJButton btnSaveAbgebendeLeitungenUserTask;

    // Services
    private WitaUsertaskService witaUsertaskService;
    private WitaTalOrderService witaTalOrderService;
    private HistoryService historyService;

    // Status des Panels
    private AbgebendeLeitungenVorgang selectedVorgang;
    private UserTaskDetails selectedVorgangDetails;
    private AnlagenAnzeigePanel anlagenAnzeigePanel;

    AbgebendeLeitungenPanel() {
        super("de/augustakom/hurrican/gui/tools/tal/resources/AbgebendeLeitungenPanel.xml");
        createGUI();
        initServices();
    }

    @Override
    protected final void createGUI() {
        tbMdlAbgebendeLeitungenVorgaenge = createTable();

        tbMdlAbgebendeLeitungenVorgaenge.addFilterTableModelListener(this);
        tbAbgebendeLeitungenVorgang = new VorgangTable<>(tbMdlAbgebendeLeitungenVorgaenge,
                JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, COLUMN_NAME_STATUS);
        tbAbgebendeLeitungenVorgang.attachSorter();
        tbAbgebendeLeitungenVorgang.addTableListener(this);
        tbAbgebendeLeitungenVorgang.getColumn(COLUMN_NAME_LAST_CHANGE).setCellRenderer(
                new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME_LONG));
        tbAbgebendeLeitungenVorgang.addPopupAction(new TableOpenOrderActionMultipleAware());
        tbAbgebendeLeitungenVorgang.addPopupAction(new ShowHistoryAction());
        tbAbgebendeLeitungenVorgang.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbAbgebendeLeitungenVorgang.fitTable(
                new int[] { 40, 130, 120, 70, 100, 70, 100, 75, 120, 80, 80, 100, 45, 100, 100, 120, 100 });
        AKJScrollPane spAbgebendeLeitungenVorgang =
                new AKJScrollPane(tbAbgebendeLeitungenVorgang, new Dimension(700, 200));

        btnWiedervorlage = getSwingFactory().createButton(WIEDERVORLAGE, getActionListener());
        btnTaskBearbeiten = getSwingFactory().createButton(TASK_BEARBEITEN, getActionListener());
        btnTaskFreigeben = getSwingFactory().createButton(TASK_FREIGEBEN, getActionListener());
        btnAbschliessen = getSwingFactory().createButton(ABSCHLIESSEN, getActionListener());
        btnHistory = getSwingFactory().createButton(HISTORY, getActionListener());
        btnRuemPvPos = getSwingFactory().createButton(POSITIVE_RUEMPV, getActionListener());
        btnRuemPvNeg = getSwingFactory().createButton(NEGATIVE_RUEMPV, getActionListener());
        btnAssignOrder = getSwingFactory().createButton(ASSIGN_ORDER, getActionListener());

        AKJLabel lblKunde = getSwingFactory().createLabel(KUNDE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblKName = getSwingFactory().createLabel(KUNDE_NAME);
        AKJLabel lblKVorname = getSwingFactory().createLabel(KUNDE_VORNAME);

        AKJLabel lblEndstelle = getSwingFactory().createLabel(ENDSTELLE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblEName = getSwingFactory().createLabel(ES_NAME);
        AKJLabel lblEEndstelle = getSwingFactory().createLabel(ES_STRASSE);
        AKJLabel lblEPLZ = getSwingFactory().createLabel(ES_PLZ_ORT);
        AKJLabel lblEHvt = getSwingFactory().createLabel(ES_HVT);

        AKJLabel lblLeitung = getSwingFactory().createLabel(LEITUNG, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblLbz = getSwingFactory().createLabel(LBZ);
        AKJLabel lblVtrnr = getSwingFactory().createLabel(VTRNR);
        AKJLabel lblBemerkungen = getSwingFactory().createLabel(BEMERKUNGEN, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblAnlagen = getSwingFactory().createLabel(ANLAGEN, SwingConstants.LEFT, Font.BOLD);

        tfKName = getSwingFactory().createTextField(KUNDE_NAME, false);
        tfKVorname = getSwingFactory().createTextField(KUNDE_VORNAME, false);

        tfEName = getSwingFactory().createTextField(ES_NAME, false);
        tfEEndstelle = getSwingFactory().createTextField(ES_STRASSE, false);
        tfEPLZ = getSwingFactory().createTextField(ES_PLZ, false);
        tfEOrt = getSwingFactory().createTextField(ES_ORT, false);
        tfEHvt = getSwingFactory().createTextField(ES_HVT, false);

        tfLBZ = getSwingFactory().createTextField(LBZ, false);
        tfVtrnr = getSwingFactory().createTextField(VTRNR, false);

        taBemerkungen = getSwingFactory().createTextArea(BEMERKUNGEN, true);
        btnSaveAbgebendeLeitungenUserTask = getSwingFactory().createButton(SAVE_ABGEBENDE_LEITUNGEN_USER_TASK,
                getActionListener());

        // @formatter:off
        AKJPanel buttonsPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("functions"));
        int yButtonsPnl = 0;

        List<String> toggleButtonCommands = Lists.newArrayList();
        toggleButtonCommands.add(EIGENE_TASKS);
        toggleButtonCommands.add(ALLE_TASKS);
        EmbeddableTamFilter tamFilter = EmbeddableTamFilterBuilder.builder()
                .withActionListener(getActionListener())
                .withSwingFactory(getSwingFactory())
                .withToggleButtonCommands(toggleButtonCommands)
                .withAdditionalFilterComponent(createAdditionalFilterComponent().orElse(null))
                .build();
        AKJPanel filtersPnl = tamFilter.createFilterPanel();

        buttonsPnl.add(filtersPnl, GBCFactory.createGBC(  0,  0, 0, yButtonsPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(btnWiedervorlage  , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPnl.add(btnTaskBearbeiten , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(btnTaskFreigeben  , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPnl.add(btnRuemPvPos      , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(btnRuemPvNeg      , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPnl.add(btnAbschliessen   , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPnl.add(btnAssignOrder    , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));
        buttonsPnl.add(btnHistory        , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        buttonsPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel kundePnl = new AKJPanel(new GridBagLayout());
        int yKundePnl = 0;
        kundePnl.add(lblKunde       , GBCFactory.createGBC(  0,  0, 0, yKundePnl    , 4, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblKName       , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 1, yKundePnl    , 1, 1, GridBagConstraints.NONE));
        kundePnl.add(tfKName        , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblKVorname    , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 1, yKundePnl    , 1, 1, GridBagConstraints.NONE));
        kundePnl.add(tfKVorname     , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));

        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.VERTICAL));

        kundePnl.add(lblEndstelle   , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 4, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblEName       , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 1, yKundePnl    , 1, 1, GridBagConstraints.NONE));
        kundePnl.add(tfEName        , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblEEndstelle  , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(tfEEndstelle   , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblEPLZ        , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(tfEPLZ         , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(tfEOrt         , GBCFactory.createGBC(  0,  0, 3, yKundePnl    , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblEHvt        , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(tfEHvt         , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));

        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.VERTICAL));

        kundePnl.add(lblLeitung     , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 4, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblLbz         , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 1, yKundePnl    , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(tfLBZ          , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(lblVtrnr       , GBCFactory.createGBC(  0,  0, 0, ++yKundePnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kundePnl.add(tfVtrnr        , GBCFactory.createGBC(  0,  0, 2, yKundePnl    , 2, 1, GridBagConstraints.HORIZONTAL));

        kundePnl.add(new AKJPanel() , GBCFactory.createGBC(  0,100, 0, ++yKundePnl  , 1, 1, GridBagConstraints.VERTICAL));

        anlagenAnzeigePanel = new AnlagenAnzeigePanel();
        taBemerkungen.setPreferredSize(anlagenAnzeigePanel.getPreferredSize());
        spBemerkungen = new AKJScrollPane(taBemerkungen);
        spBemerkungenBoarder = spBemerkungen.getBorder();

        int yAnlagenPnl = 0;
        AKJPanel anlagenPnl = new AKJPanel(new GridBagLayout());
        anlagenPnl.add(lblAnlagen                        , GBCFactory.createGBC(  0,  0, 0, yAnlagenPnl,   2, 1, GridBagConstraints.HORIZONTAL));
        anlagenPnl.add(anlagenAnzeigePanel               , GBCFactory.createGBC(  0,  0, 0, ++yAnlagenPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        anlagenPnl.add(new AKJPanel()                    , GBCFactory.createGBC(  0,  0, 0, ++yAnlagenPnl, 2, 1, GridBagConstraints.VERTICAL));
        anlagenPnl.add(lblBemerkungen                    , GBCFactory.createGBC(  0,  0, 0, ++yAnlagenPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        anlagenPnl.add(spBemerkungen                     , GBCFactory.createGBC(  0,  0, 0, ++yAnlagenPnl, 2, 1, GridBagConstraints.HORIZONTAL,10,6));
        anlagenPnl.add(btnSaveAbgebendeLeitungenUserTask , GBCFactory.createGBC(  0,  0, 0, ++yAnlagenPnl, 1, 1, GridBagConstraints.NONE));
        anlagenPnl.add(new AKJPanel()                    , GBCFactory.createGBC(  0,  0, 1,   yAnlagenPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        anlagenPnl.add(new AKJPanel()                    , GBCFactory.createGBC(  0,100, 1, ++yAnlagenPnl, 2, 1, GridBagConstraints.VERTICAL));

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("kontaktdaten"));
        detPnl.add(kundePnl         , GBCFactory.createGBC(10 ,100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        detPnl.add(anlagenPnl       , GBCFactory.createGBC(10 ,100, 1, 0, 1, 1, GridBagConstraints.BOTH, 20));
        detPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        AKJPanel downPnl = new AKJPanel(new BorderLayout());
        downPnl.add(buttonsPnl, BorderLayout.WEST);
        downPnl.add(detPnl, BorderLayout.CENTER);

        AKJSplitPane split = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        split.setTopComponent(spAbgebendeLeitungenVorgang);
        split.setBottomComponent(new AKJScrollPane(downPnl));
        split.setDividerSize(2);
        split.setResizeWeight(1d); // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        manageGUI(btnTaskBearbeiten, btnTaskFreigeben, btnSaveAbgebendeLeitungenUserTask,
                btnAbschliessen, btnRuemPvPos, btnRuemPvNeg, btnAssignOrder);
        updateControls();
    }

    private Optional<AKJComboBox> createAdditionalFilterComponent() {
        return Optional.empty();
    }

    private AKReferenceAwareTableModel<AbgebendeLeitungenVorgang> createTable() {
        return new AKReferenceAwareTableModel<>(COLUMN_DEFINITIONS);
    }

    private void initServices() {
        try {
            historyService = getCCService(HistoryService.class);
            witaUsertaskService = getCCService(WitaUsertaskService.class);
            witaTalOrderService = getCCService(WitaTalOrderService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        setWaitCursor();
        tbMdlAbgebendeLeitungenVorgaenge.removeAll();
        showDetails(null);
        try {
            final SwingWorker<List<AbgebendeLeitungenVorgang>, Void> worker = new SwingWorker<List<AbgebendeLeitungenVorgang>, Void>() {

                @Override
                public List<AbgebendeLeitungenVorgang> doInBackground() throws Exception {
                    return witaUsertaskService.findOpenAbgebendeLeitungenTasksWithWiedervorlage();
                }

                @Override
                protected void done() {
                    try {
                        tbMdlAbgebendeLeitungenVorgaenge.setData(get());
                    }
                    catch (Exception e) {
                        LOGGER.error(e, e);
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
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

    @Override
    public void showDetails(Object abgebendeLeitungenVorgang) {
        clearDetails();
        if (abgebendeLeitungenVorgang instanceof AbgebendeLeitungenVorgang) {
            try {
                this.selectedVorgang = ((AbgebendeLeitungenVorgang) abgebendeLeitungenVorgang);

                AbgebendeLeitungenUserTask userTask = selectedVorgang.getUserTask();
                if (userTask != null) {
                    tfVtrnr.setText(userTask.getVertragsNummer());
                    List<AnlagenDto> anlagen;
                    if (userTask instanceof AkmPvUserTask) {
                        tfLBZ.setText(((AkmPvUserTask) userTask).getLeitungsBezeichnung());
                        anlagen = historyService.loadAnlagenListForVertragsnummer(userTask.getVertragsNummer());
                    }
                    else {
                        anlagen = historyService.loadAnlagenListForExtOrderno(selectedVorgang.getUserTask()
                                .getExterneAuftragsnummer());
                    }

                    anlagenAnzeigePanel.setAnlagen(anlagen);
                    setTaBemerkungsText(userTask.getBemerkungen());
                }

                showKundeEndstelleHVTGruppeDetails();

            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        updateControls();
    }

    private void showKundeEndstelleHVTGruppeDetails() {
        try {
            this.selectedVorgangDetails = witaUsertaskService.loadUserTaskDetails(selectedVorgang);
            Kunde kunde = selectedVorgangDetails.getKunde();
            if (kunde != null) {
                tfKName.setText(kunde.getName());
                tfKVorname.setText(kunde.getVorname());
            }

            Endstelle endstelle = selectedVorgangDetails.getEndstelle();
            if (endstelle != null) {
                tfEName.setText(endstelle.getName());
                tfEEndstelle.setText(endstelle.getEndstelle());
                tfEPLZ.setText(endstelle.getPlz());
                tfEOrt.setText(endstelle.getOrt());
            }

            HVTGruppe hvtGruppe = selectedVorgangDetails.getHvtGruppe();
            if (hvtGruppe != null) {
                tfEHvt.setText(hvtGruppe.getOrtsteil());
            }
        }
        catch (FindException e) {
            LOGGER.info(e.getMessage());
            // do nothing due to possibility that certain special cases
            // where akm-pv or erlm of kue-dt are not assigned to Auftrag,
            // therefore no user task details can be loaded
        }
    }

    private void clearDetails() {
        this.selectedVorgang = null;
        this.selectedVorgangDetails = null;
        this.spBemerkungen.setBorder(spBemerkungenBoarder);
        GuiTools.cleanFields(this);
    }

    private void setTaBemerkungsText(String userBemerkung) {
        String bemerkung = "";
        if (selectedVorgang.isKlaerfall() && isNotEmpty(selectedVorgang.getKlaerfallBemerkung())) {
            spBemerkungen.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            bemerkung = "Klärfall: "
                    + selectedVorgang.getKlaerfallBemerkung()
                    + "\n" + CBVorgang.KLAERFALL_BEMERKUNGS_SEPARATOR + "\n";
        }

        if (isNotEmpty(userBemerkung)) {
            bemerkung += userBemerkung;
        }
        taBemerkungen.setText(bemerkung);
    }

    @Override
    protected void execute(String command) {
        try {
            switch (command) {
                case TASK_BEARBEITEN:
                    taskBearbeiten();
                    break;
                case TASK_FREIGEBEN:
                    taskFreigeben();
                    break;
                case WIEDERVORLAGE:
                    wiedervorlage();
                    break;
                case EIGENE_TASKS:
                    eigeneTasks();
                    break;
                case ALLE_TASKS:
                    alleTasks();
                    break;
                case SAVE_ABGEBENDE_LEITUNGEN_USER_TASK:
                    saveUserTask();
                    break;
                case ABSCHLIESSEN:
                    closeUserTask();
                    break;
                case HISTORY:
                    showCBVorgangHistoryDialog();
                    break;
                case POSITIVE_RUEMPV:
                    openRuemPvDialog(true);
                    break;
                case NEGATIVE_RUEMPV:
                    openRuemPvDialog(false);
                    break;
                case ASSIGN_ORDER:
                    assignAuftragDaten();
                    loadData();
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            loadData();
        }
        tbAbgebendeLeitungenVorgang.repaint();
        updateControls();
    }

    private void wiedervorlage() {
        WiedervorlageDialog dialog = new WiedervorlageDialog(selectedVorgang.getAbgebendeLeitungenUserTask()
                .getWiedervorlageAmAsLocalDateTime(), selectedVorgang.getAbgebendeLeitungenUserTask().getBemerkungen(), true);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dialog, true, true);

        if (result instanceof Integer && result.equals(JOptionPane.OK_OPTION)) {
            UserTask userTask = selectedVorgang.getAbgebendeLeitungenUserTask();
            if ((userTask.getWiedervorlageAm() == null)
                    || !userTask.getWiedervorlageAm().equals(DateConverterUtils.asDate(dialog.getWiedervorlageDatum()))) {
                userTask.setWiedervorlageAm(dialog.getWiedervorlageDatum());
                userTask.setBemerkungen(dialog.getStatusBemerkung());
                witaUsertaskService.storeUserTask(userTask);

                tbMdlAbgebendeLeitungenVorgaenge.removeObject(selectedVorgang);
                selectedVorgang = null;
                showDetails(null);
            }
        }
    }

    private void closeUserTask() {
        try {
            setWaitCursor();
            if (selectedVorgang == null) {
                throw new HurricanGUIException("Es ist kein Vorgang ausgewaehlt.");
            }

            String vertragsNummer = selectedVorgang.getUserTask().getVertragsNummer();
            AbgebendeLeitungenUserTask userTask = selectedVorgang.getUserTask();
            Set<Long> cbIds = userTask.getCbIds();

            AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();
            boolean shouldClose = true;

            RufnummerPortierungCheck checkResult = witaTalOrderService.checkRufnummerPortierungAbgebend(userTask);
            if (checkResult.hasWarnings()) {
                int result = MessageHelper.showWarningDialog(this, checkResult.generateWarningsText()
                        + "\n\nWollen sie den Task trotzdem abschliessen?", "Geänderte Portierungsdaten",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.NO_OPTION) {
                    shouldClose = false;
                }
            }

            if (cbIds.isEmpty() || (cbIds.size() > 1)) {
                String baseMsg = (cbIds.isEmpty()) ? String.format(
                        "Für die Vertragsnummer %s wurde keine Carrierbestellung gefunden.\n",
                        String.format("%s", vertragsNummer)) : String.format(
                        "Für die Vertragsnummer %s wurden mehrere Carrierbestellungen gefunden.\n",
                        String.format("%s", vertragsNummer));

                // @formatter:off
                int opt = MessageHelper.showYesNoQuestion(this, baseMsg
                            + "Daten konnten nicht übernommen werden. Bitte stellen Sie sicher, dass die entsprechenden "
                            + "Daten der abzuschließenden Meldung manuell auf die richtige(n) Carrierbestellung(en) "
                            + "übernommen wurden (zu finden über Menü Suche => Auftrags-/Kundensuche => Carrier und Vertragsnummer). "
                            + "\nDie Meldung wird auch nicht Richtung BSI protokolliert und es werden keine Anlagen hochgeladen."
                            + "\n\nSoll der Task trotzdem abgeschlossen werden?",
                        "Task abschliessen?");
                // @formatter:on
                if (opt != JOptionPane.YES_OPTION) {
                    shouldClose = false;
                }
            }
            if (shouldClose) {
                AbgebendeLeitungenUserTask completedUserTask = witaUsertaskService.completeUserTask(userTask,
                        currentUser);
                selectedVorgang.setUserTask(completedUserTask);
                if (completedUserTask.getBenachrichtigung() != null) {
                    MessageHelper.showWarningDialog(this, completedUserTask.getBenachrichtigung(), true);
                }
            }
            tbAbgebendeLeitungenVorgang.repaint();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void taskBearbeiten() throws Exception {
        AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();
        for (AbgebendeLeitungenVorgang vorgang : getSelectedAbgebendeLtgVorgaenge()) {
            AbgebendeLeitungenUserTask userTask = witaUsertaskService.claimUserTask(vorgang.getUserTask(), currentUser);
            vorgang.setUserTask(userTask);
        }
    }

    private void taskFreigeben() throws Exception {
        for (AbgebendeLeitungenVorgang vorgang : getSelectedAbgebendeLtgVorgaenge()) {
            AbgebendeLeitungenUserTask userTask = witaUsertaskService.claimUserTask(vorgang.getUserTask(), null);
            vorgang.setUserTask(userTask);
        }
    }

    private void eigeneTasks() {
        FilterRelation relation = new FilterRelation(EIGENE_TASKS_FILTER_NAME, FilterRelations.OR);
        relation.addChild(new FilterOperator(FilterOperators.EQ, getLoginCurrentUser(), COLUMN_INDEX_BEARBEITER));
        relation.addChild(new FilterOperator(FilterOperators.EQ, getLoginCurrentUser(), COLUMN_INDEX_TASK_BEARBEITER));
        tbMdlAbgebendeLeitungenVorgaenge.addFilter(relation);
    }

    private void alleTasks() {
        tbMdlAbgebendeLeitungenVorgaenge.removeFilter(EIGENE_TASKS_FILTER_NAME);
    }

    private void saveUserTask() throws HurricanGUIException {
        if ((selectedVorgang == null) || (selectedVorgang.getUserTask() == null)) {
            throw new HurricanGUIException("Es ist kein Task ausgewählt.");
        }

        AbgebendeLeitungenUserTask userTask = selectedVorgang.getUserTask();
        String bemerkungenNeu = appendUserAndDateIfChanged(
                userTask.getBemerkungen(),
                removeCbVorgangKlaerfallText(taBemerkungen.getText()),
                "dd.MM.yyyy HH:mm");
        userTask.setBemerkungen(bemerkungenNeu);
        userTask.setLetzteAenderung(new Date());

        selectedVorgang.setUserTask(userTask);
        AbgebendeLeitungenUserTask storedUserTask = witaUsertaskService.storeUserTask(userTask);
        selectedVorgang.setUserTask(storedUserTask);

        setTaBemerkungsText(bemerkungenNeu);
    }

    private String removeCbVorgangKlaerfallText(String taBemerkungenText) {
        if (isNotEmpty(taBemerkungenText)) {
            String[] splitedStrings = StringUtils.splitByWholeSeparator(taBemerkungenText,
                    CBVorgang.KLAERFALL_BEMERKUNGS_SEPARATOR);
            return StringUtils.removeStart(splitedStrings[splitedStrings.length - 1], "\n");
        }
        return taBemerkungenText;
    }

    private List<AbgebendeLeitungenVorgang> getSelectedAbgebendeLtgVorgaenge() {
        List<AbgebendeLeitungenVorgang> abgebendeLtgVorgaenge = Lists.newArrayList();
        @SuppressWarnings("unchecked")
        AKTableSorter<AbgebendeLeitungenVorgang> model = (AKTableSorter<AbgebendeLeitungenVorgang>) tbAbgebendeLeitungenVorgang
                .getModel();
        for (int row : tbAbgebendeLeitungenVorgang.getSelectedRows()) {
            abgebendeLtgVorgaenge.add(model.getDataAtRow(row));
        }
        return abgebendeLtgVorgaenge;
    }

    private String getLoginCurrentUser() {
        return HurricanSystemRegistry.instance().getCurrentUser().getLoginName();
    }

    /**
     * Action, um die History zu einer Carrier-Bestellung ueber das Kontext-Menu der Tabelle zu oeffnen.
     */
    private final class ShowHistoryAction extends AKAbstractAction {

        private static final long serialVersionUID = -3536713165709865942L;

        private ShowHistoryAction() {
            super();
            setName("History...");
            setActionCommand("show.details");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showCBVorgangHistoryDialog();
        }
    }

    /**
     * Zeigt die History zum ausgewaehlten UserTask an
     */
    private void showCBVorgangHistoryDialog() {
        if (selectedVorgang != null) {
            AbstractServiceOptionDialog dlg;
            if (selectedVorgang.getUserTask() instanceof AkmPvUserTask) {
                dlg = new HistoryByVertragsnummerDialog(selectedVorgang.getUserTask().getVertragsNummer());
            }
            else {
                dlg = new HistoryByExtOrderNoDialog(selectedVorgang.getUserTask()
                        .getExterneAuftragsnummer());
            }
            DialogHelper.showDialog(getMainFrame(), dlg, false, true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Es ist kein Vorgang ausgewählt!", null, true);
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den eine positive bzw. negative RUEM-PV gesendet werden kann.
     */
    private void openRuemPvDialog(boolean positiveRuemPv) {
        try {
            if ((selectedVorgang == null) || (selectedVorgang.getAuftragDaten() == null)) {
                throw new HurricanGUIException(
                        "Es ist kein Vorgang bzw. kein Vorgang mit hinterlegtem Auftrag ausgewaehlt.");
            }
            if (selectedVorgang.getUserTask() instanceof AkmPvUserTask) {
                if (selectedVorgang.getAuftragDaten().size() > 1) {
                    String text = "\n\nDie Meldung wird auch nicht Richtung BSI protokolliert und es werden keine Anlagen hochgeladen.";
                    if (!proceedWithMultipleAuftraege(text)) {
                        return;
                    }
                }
                if (positiveRuemPv) {
                    RufnummerPortierungCheck checkResult = witaTalOrderService
                            .checkRufnummerPortierungAbgebend(selectedVorgang.getUserTask());
                    if (checkResult.hasWarnings()) {
                        int result = MessageHelper.showWarningDialog(this, checkResult.generateWarningsText()
                                + "\n\nWollen sie trotzdem eine positive Rückmeldung schicken?",
                                "Geänderte Portierungsdaten", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                        );
                        if (result == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                }

                SendRuemPvDialog ruemPvDialog = new SendRuemPvDialog((AkmPvUserTask) selectedVorgang.getUserTask(),
                        positiveRuemPv);
                DialogHelper.showDialog(getMainFrame(), ruemPvDialog, true, true);

                AkmPvUserTask closedUserTask = witaUsertaskService.findAkmPvUserTask(selectedVorgang
                        .getExterneAuftragsnummer());
                selectedVorgang.setUserTask(closedUserTask);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(AbgebendeLeitungenPanel.this.getMainFrame(), e);
        }

    }

    /**
     * Oeffnet einen Dialog, um die zugehoerige Auftrags-ID abzufragen.
     */
    private void assignAuftragDaten() throws HurricanGUIException {
        try {
            // Auftrags-ID eingeben und setzen - anschliessend Panel neu laden
            AuftragZuordnungDialog dlg = new AuftragZuordnungDialog(selectedVorgang.getAuftragDaten());
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result == null) {
                return;
            }
            if (result instanceof Integer && result.equals(JOptionPane.CANCEL_OPTION)) {
                return;
            }
            witaUsertaskService.createUserTask2AuftragDatenFor(selectedVorgang.getAbgebendeLeitungenUserTask(),
                    dlg.getSelectedAuftragIdAndCbId());
            loadData();
        }
        catch (Exception e) {
            throw new HurricanGUIException("Fehler bei der Zuordnung des Auftrags: " + e.getMessage(), e);
        }
    }

    private void updateControls() {
        List<AbgebendeLeitungenVorgang> selectedVorgaenge = getSelectedAbgebendeLtgVorgaenge();

        updateBtnTaskBearbeiten(selectedVorgaenge);
        updateBtnTaksFreigeben(selectedVorgaenge);

        if (CollectionTools.isEmpty(selectedVorgaenge) || (selectedVorgaenge.size() != 1)) {
            setUserTaskControlsEnabled(false);
            btnHistory.setEnabled(false);
            btnAssignOrder.setEnabled(false);
            clearDetails();
        }
        else { // genau ein Task selektiert
            btnHistory.setEnabled(true);
            btnAssignOrder.setEnabled(true);

            if (getLoginCurrentUser().equalsIgnoreCase(selectedVorgaenge.get(0).getTaskBearbeiter())) {
                setUserTaskControlsEnabled(true);

                AbgebendeLeitungenUserTask userTask = Iterables.getOnlyElement(selectedVorgaenge).getUserTask();
                if (userTask instanceof AkmPvUserTask) {
                    btnAbschliessen.setEnabled(((AkmPvUserTask) userTask).isAbschliessbar());
                    btnRuemPvNeg.setEnabled(((AkmPvUserTask) userTask).isRuemPvSendbar());
                    btnRuemPvPos.setEnabled(((AkmPvUserTask) userTask).isRuemPvSendbar());
                }
                else {
                    btnRuemPvNeg.setEnabled(false);
                    btnRuemPvPos.setEnabled(false);
                }
            }
            else {
                setUserTaskControlsEnabled(false);
            }
        }
    }

    /**
     * Wenn mindestens ein Task bereits in Arbeit ist, Button deaktivieren.
     */
    private void updateBtnTaskBearbeiten(List<AbgebendeLeitungenVorgang> selectedAbgebendeLtgVorgaenge) {
        boolean enabled = true;
        if (CollectionTools.isEmpty(selectedAbgebendeLtgVorgaenge)) {
            enabled = false;
        }
        for (AbgebendeLeitungenVorgang abgebendeLtgVorgang : selectedAbgebendeLtgVorgaenge) {
            if (StringUtils.isNotBlank(abgebendeLtgVorgang.getTaskBearbeiter())) {
                enabled = false;
            }
        }
        btnTaskBearbeiten.setEnabled(enabled);
    }

    /**
     * Mindestens ein selektierter Task muss in Arbeit sein, damit der Button aktiviert wird.
     */
    private void updateBtnTaksFreigeben(List<AbgebendeLeitungenVorgang> selectedAbgebendeLtgVorgaenge) {
        boolean enabled = false;
        for (AbgebendeLeitungenVorgang abgebendeLtgVorgang : selectedAbgebendeLtgVorgaenge) {
            if (StringUtils.isNotBlank(abgebendeLtgVorgang.getTaskBearbeiter())) {
                enabled = true;
            }
        }
        btnTaskFreigeben.setEnabled(enabled);
    }

    @SuppressWarnings("Duplicates")
    private void setUserTaskControlsEnabled(boolean enabled) {
        btnAbschliessen.setEnabled(enabled);
        btnWiedervorlage.setEnabled(enabled);
        taBemerkungen.setEnabled(enabled);
        btnRuemPvPos.setEnabled(enabled);
        btnRuemPvNeg.setEnabled(enabled);
        btnSaveAbgebendeLeitungenUserTask.setEnabled(enabled);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    public void tableFiltered() {
        // not used
    }

    @Override
    public void objectSelected(Object selection) {
        openAuftragMultipleAware();
    }

    private class TableOpenOrderActionMultipleAware extends AKAbstractAction {

        private static final long serialVersionUID = 7911998383343489586L;

        private TableOpenOrderActionMultipleAware() {
            super();
            setName("Auftrag öffnen");
            setActionCommand("open.auftrag");
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            openAuftragMultipleAware();
        }
    }

    private void openAuftragMultipleAware() {
        try {
            if ((selectedVorgang == null) || (selectedVorgang.getAuftragDaten() == null)) {
                throw new HurricanGUIException(
                        "Es ist kein Vorgang bzw. kein Vorgang mit hinterlegtem Auftrag ausgewaehlt.");
            }
            if (selectedVorgang.getAuftragDaten().size() > 1 && !proceedWithMultipleAuftraege("")) {
                return;
            }
            AuftragDataFrame.openFrame(selectedVorgang);
        }
        catch (Exception e) {
            LOGGER.error(e);
            MessageHelper.showErrorDialog(AbgebendeLeitungenPanel.this.getMainFrame(), e);
        }
    }

    @Override
    public void setWaitCursor() {
        tbAbgebendeLeitungenVorgang.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        super.setWaitCursor();
    }

    @Override
    public void setDefaultCursor() {
        tbAbgebendeLeitungenVorgang.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        super.setDefaultCursor();
    }

    private boolean proceedWithMultipleAuftraege(String protokollierenText) {
        Set<Long> auftragIds = Sets.newHashSet();
        for (AuftragDaten auftragDaten : selectedVorgang.getAuftragDaten()) {
            auftragIds.add(auftragDaten.getAuftragId());
        }
        int result = MessageHelper.showWarningDialog(AbgebendeLeitungenPanel.this.getMainFrame(), String.format(
                "Achtung! Für die von der WITA zurückgemeldete Vertragsnummer existieren die "
                        + "folgenden technischen Aufträge:\n%s"
                        + "\n\nBitte bereinigen Sie durch 'Auftrag zuordnen' zuerst die/den "
                        + "technischen Auftrag, der dem Vorgang zugeordnet bleiben soll." + protokollierenText
                        + "\n\nWollen sie jetzt den Auftrag mit der Id %s bearbeiten?.", Joiner.on(",")
                .join(auftragIds), selectedVorgang.getAuftragId()
        ), "Mehrere zugeordnete Aufträge",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        return (result == JOptionPane.YES_OPTION);
    }
}
