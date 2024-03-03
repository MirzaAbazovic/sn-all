/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.06.2007 07:49:04
 */
package de.augustakom.hurrican.gui.tools.tal;

import static de.mnet.wita.config.WitaConstants.*;
import static de.mnet.wita.model.TamUserTask.*;
import static org.apache.commons.lang.StringUtils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.swing.table.FilterRelation;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaData;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByCbDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.BaseAenderungsKennzeichenDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.ErlmkDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.StornoDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.TerminverschiebungDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.*;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.UserTaskDetails;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Panel fuer die Darstellung und Bearbeitung der offenen TAMs.
 */
@SuppressWarnings("Duplicates")
class OffeneTKGTamsPanel extends AbstractTamsPanel<TamVorgang> {

    private static final long serialVersionUID = -5560115845797572819L;

    private static final Logger LOGGER = Logger.getLogger(OffeneTKGTamsPanel.class);

    private static final String TASK_STATUS_FILTER_NAME = "task.status.filter.name";
    private static final String TV_30TAGE = "30tage";
    // Offene TAMs Tabelle
    private AKReferenceAwareTableModel<TamVorgang> tbMdlTamVorgaenge;
    private VorgangTable<TamVorgang> tbTamVorgang;

    // Funktionen
    private AKJButton btnTv;
    private AKJButton btnStorno;
    private AKJButton btn30Tage;

    // Kunde und Kontaktdaten
    private AKJTextField tfKName;
    private AKJTextField tfKVorname;
    private AKJTextField tfHauptrufnummer;
    private AKJTextField tfGeschaeftsnummer;
    private AKJTextField tfPrivatnummer;
    private AKJTextField tfMobilnummer;
    private AKJTextField tfVoipnummer;
    private AKJTextField tfEmail;

    // Endstelle
    private AKJTextField tfEName;
    private AKJTextField tfEEndstelle;
    private AKJTextField tfEPLZ;
    private AKJTextField tfEOrt;
    private AKJTextField tfEHvt;

    // TAM-Usertask
    private AKJTextArea taBemerkungen;
    private AKJScrollPane spBemerkungen;
    private Border spBemerkungenDefaultBorder;
    private AKJComboBox cbTamStatus;
    private AKJComboBox cbTamStatusFilter;

    private AKJButton btnSaveTamUserTask;
    private AKJButton btnCloseTATamTask;
    // Status des Panels
    private boolean defaultsLoaded = false;
    private UserTaskDetails selectedTamVorgangDetails;
    private TamBearbeitungsStatus selectedTamBearbeitungsStatus;
    private TamBearbeitungsStatusFilter selectedTamstatusFilter;
    private AKAbstractAction sendErledigtMeldungAction;


    OffeneTKGTamsPanel() {
        super("de/augustakom/hurrican/gui/tools/tal/resources/OffeneTKGTamsPanel.xml");
        initServices();
        createGUI();
    }

    @Override
    protected void createCbTaskTable() {
        tbMdlTamVorgaenge = createTamVorgangTableModel();

        tbTamVorgang = new VorgangTable<>(tbMdlTamVorgaenge, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, STATUS_COLUMN_NAME);
        tbTamVorgang.addPopupAction(new TableOpenOrderAction());

//        acSendErledigtMeldung = (AKAbstractAction) getSwingFactory().createAction(ERLMK);
//        acSendErledigtMeldung.setParentClass(this.getClass());
//        acSendErledigtMeldung
//
//
//
        sendErledigtMeldungAction = new AKAbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendErledigtmeldung();
            }
        };
        sendErledigtMeldungAction.setActionCommand(ERLMK);
        /*
                <text>ERLM-K</text>
        <tooltip>Sckickt eine Erledigmeldung-Kunde für die Carrierbestellung.</tooltip>

         */
        sendErledigtMeldungAction.setName("ERLM-K");
        sendErledigtMeldungAction.setTooltip("Sckickt eine Erledigmeldung-Kunde für die Carrierbestellung");

        tbTamVorgang.addPopupAction(sendErledigtMeldungAction);

        // hier koennen keine Renderer gesetzt werden, da in der 'createGUI' ein TableSorter als Tablemodel
        // gesetzt wird. D.h. die hier gesetzten Render wirken sich nicht aus.
    }

    @Override
    protected List<Pair<String, TableCellRenderer>> createTableCellRenderer() {
        List<Pair<String, TableCellRenderer>> renderers = new LinkedList<>();
        renderers.add(new Pair<>(TAM_ART_COLUMN_NAME, new TKGTamMeldungsCodeCellRenderer()));
        renderers.add(new Pair<>(LAST_CHANGE_COLUMN_NAME, new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME_LONG)));
        return renderers;
    }

    @Override
    protected Optional<AKJComboBox> createAdditionalFilterComponent() {
        cbTamStatusFilter = getSwingFactory().createComboBox(TAM_STATUS_FILTER,
                new AKCustomListCellRenderer<>(TamBearbeitungsStatusFilter.class, TamBearbeitungsStatusFilter::getDisplay));
        cbTamStatusFilter.addItems(Arrays.asList(TamBearbeitungsStatusFilter.values()), true);
        cbTamStatusFilter.addActionListener(getActionListener());
        return Optional.of(cbTamStatusFilter);
    }


    @Override
    protected int createExtraButtons(AKJPanel btnPnl, int yBtnPnlIn) {
        int yBtnPnl = yBtnPnlIn;
        btnTv = getSwingFactory().createButton(TERMINVERSCHIEBUNG, getActionListener());
        btnStorno = getSwingFactory().createButton(STORNO, getActionListener());
        btn30Tage = getSwingFactory().createButton(TV_30TAGE, getActionListener());
        btnCloseTATamTask = getSwingFactory().createButton(CLOSE_TAM_USER_TASK, getActionListener());

        // @formatter:off
        btnPnl.add(new AKJPanel()   , GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnTv            , GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnStorno        , GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btn30Tage        , GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnCloseTATamTask, GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return yBtnPnl;
    }

    @Override
    protected AKJPanel createDetailPanel() {
        createInputComponents();
        AKJPanel kdPnl = createKdPanel();
        AKJPanel esPnl = createEsPanel();
        AKJPanel rmPnl = createRmPanel();
        return createDetPanel(kdPnl, esPnl, rmPnl);
    }

    /**
     * Erzeugt die Eingabekomponenten
     */
    private void createInputComponents() {
        tfKName = getSwingFactory().createTextField(KUNDE_NAME, false);
        tfKVorname = getSwingFactory().createTextField(KUNDE_VORNAME, false);
        tfHauptrufnummer = getSwingFactory().createTextField(KUNDE_HAUPTRUFNUMMER, false);
        tfGeschaeftsnummer = getSwingFactory().createTextField(KUNDE_GESCHAEFTSNUMMER, false);
        tfPrivatnummer = getSwingFactory().createTextField(KUNDE_PRIVATNUMMER, false);
        tfMobilnummer = getSwingFactory().createTextField(KUNDE_MOBILNUMMER, false);
        tfVoipnummer = getSwingFactory().createTextField(KUNDE_VOIPNUMMER, false);

        tfEmail = getSwingFactory().createTextField(KUNDE_EMAIL, false);
        tfEName = getSwingFactory().createTextField(ES_NAME, false);
        tfEEndstelle = getSwingFactory().createTextField(ES_STRASSE, false);
        tfEPLZ = getSwingFactory().createTextField(ES_PLZ, false);
        tfEOrt = getSwingFactory().createTextField(ES_ORT, false);
        tfEHvt = getSwingFactory().createTextField(ES_HVT, false);

        taBemerkungen = getSwingFactory().createTextArea(BEMERKUNGEN, true);
        spBemerkungen = new AKJScrollPane(taBemerkungen);
        spBemerkungenDefaultBorder = spBemerkungen.getBorder();
        cbTamStatus = getSwingFactory().createComboBox(TAM_STATUS_FILTER,
                new AKCustomListCellRenderer<>(TamBearbeitungsStatus.class, TamBearbeitungsStatus::getDisplay));
        cbTamStatus.addItems(Arrays.asList(TamBearbeitungsStatus.values()), true);
        btnSaveTamUserTask = getSwingFactory().createButton(SAVE_TAM_USER_TASK, getActionListener());
        managedComponents.add(btnSaveTamUserTask);
    }

    /**
     * Erzeugt das Detailspanel
     * @param kdPnl Kundenpanel
     * @param esPnl Endstellepanel
     * @param rmPnl RmPanel
     * @return Das Deatails-Panel
     */
    private AKJPanel createDetPanel(AKJPanel kdPnl, AKJPanel esPnl, AKJPanel rmPnl) {
        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(KONTAKTDATEN));
        detPnl.add(kdPnl            , GBCFactory.createGBC( 10,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        detPnl.add(esPnl            , GBCFactory.createGBC( 10,100, 1, 0, 1, 1, GridBagConstraints.BOTH, 20));
        detPnl.add(rmPnl            , GBCFactory.createGBC( 10,100, 2, 0, 1, 1, GridBagConstraints.BOTH, 20));
        detPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return detPnl;
    }

    private AKJPanel createRmPanel() {
        AKJLabel lblStatus = getSwingFactory().createLabel(STATUS, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblBemerkungen = getSwingFactory().createLabel(BEMERKUNGEN, SwingConstants.LEFT, Font.BOLD);

        AKJPanel rmPnl = new AKJPanel(new GridBagLayout());
        int yRmPnl = 0;
        rmPnl.add(lblBemerkungen    , GBCFactory.createGBC(  0,  0, 0, yRmPnl   , 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(spBemerkungen     , GBCFactory.createGBC(  0,  0, 0, ++yRmPnl , 0, 2, GridBagConstraints.HORIZONTAL, 10, 6));
        rmPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, ++yRmPnl , 1, 1, GridBagConstraints.NONE));
        rmPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, ++yRmPnl , 1, 1, GridBagConstraints.NONE));
        rmPnl.add(lblStatus         , GBCFactory.createGBC(  0,  0, 0, ++yRmPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(cbTamStatus       , GBCFactory.createGBC(  0,  0, 0, ++yRmPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, ++yRmPnl , 1, 1, GridBagConstraints.NONE));
        rmPnl.add(btnSaveTamUserTask, GBCFactory.createGBC(  0,  0, 0, ++yRmPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0, ++yRmPnl , 0, 2, GridBagConstraints.BOTH));
        return rmPnl;
    }

    private AKJPanel createEsPanel() {
        AKJLabel lblEndstelle = getSwingFactory().createLabel(ENDSTELLE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblEName = getSwingFactory().createLabel(ES_NAME);
        AKJLabel lblEEndstelle = getSwingFactory().createLabel(ES_STRASSE);
        AKJLabel lblEPLZ = getSwingFactory().createLabel(ES_PLZ_ORT);
        AKJLabel lblEHvt = getSwingFactory().createLabel(ES_HVT);

        AKJPanel esPnl = new AKJPanel(new GridBagLayout());

        int yEsPnl = 0;
        esPnl.add(lblEndstelle      , GBCFactory.createGBC(  0,  0, 0, yEsPnl   , 4, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEName          , GBCFactory.createGBC(  0,  0, 0, ++yEsPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, yEsPnl   , 1, 1, GridBagConstraints.NONE));
        esPnl.add(tfEName           , GBCFactory.createGBC(  0,  0, 2, yEsPnl   , 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEEndstelle     , GBCFactory.createGBC(  0,  0, 0, ++yEsPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEEndstelle      , GBCFactory.createGBC(  0,  0, 2, yEsPnl   , 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEPLZ           , GBCFactory.createGBC(  0,  0, 0, ++yEsPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEPLZ            , GBCFactory.createGBC(  0,  0, 2, yEsPnl   , 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEOrt            , GBCFactory.createGBC(  0,  0, 3, yEsPnl   , 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEHvt           , GBCFactory.createGBC(  0,  0, 0, ++yEsPnl , 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEHvt            , GBCFactory.createGBC(  0,  0, 2, yEsPnl   , 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0, ++yEsPnl , 1, 1, GridBagConstraints.VERTICAL));

        return esPnl;
    }

    private AKJPanel createKdPanel() {
        AKJLabel lblKunde = getSwingFactory().createLabel(KUNDE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblKName = getSwingFactory().createLabel(KUNDE_NAME);
        AKJLabel lblKVorname = getSwingFactory().createLabel(KUNDE_VORNAME);
        AKJLabel lblKontaktdaten = getSwingFactory().createLabel(KONTAKTDATEN, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblHauptrufnummer = getSwingFactory().createLabel(KUNDE_HAUPTRUFNUMMER);
        AKJLabel lblGeschaeftsnummer = getSwingFactory().createLabel(KUNDE_GESCHAEFTSNUMMER);
        AKJLabel lblPrivatnummer = getSwingFactory().createLabel(KUNDE_PRIVATNUMMER);
        AKJLabel lblMobilnummer = getSwingFactory().createLabel(KUNDE_MOBILNUMMER);
        AKJLabel lblVoipnummer = getSwingFactory().createLabel(KUNDE_VOIPNUMMER);
        AKJLabel lblEmailKunde = getSwingFactory().createLabel(KUNDE_EMAIL);

        AKJPanel kdPnl = new AKJPanel(new GridBagLayout());
        int yKdPnl = 0;
        kdPnl.add(lblKunde           , GBCFactory.createGBC(  0,  0, 0, yKdPnl  , 3, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblKName           , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 1, yKdPnl  , 1, 1, GridBagConstraints.NONE));
        kdPnl.add(tfKName            , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblKVorname        , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfKVorname         , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 1, ++yKdPnl, 1, 1, GridBagConstraints.NONE));
        kdPnl.add(lblKontaktdaten    , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 3, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblEmailKunde      , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfEmail            , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblHauptrufnummer  , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfHauptrufnummer   , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblGeschaeftsnummer, GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfGeschaeftsnummer , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblPrivatnummer    , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfPrivatnummer     , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblMobilnummer     , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfMobilnummer      , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblVoipnummer      , GBCFactory.createGBC(  0,  0, 0, ++yKdPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfVoipnummer       , GBCFactory.createGBC(  0,  0, 2, yKdPnl  , 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()     , GBCFactory.createGBC(  0,100, 0, ++yKdPnl, 1, 1, GridBagConstraints.VERTICAL));
        return kdPnl;
    }

    private AKReferenceAwareTableModel<TamVorgang> createTamVorgangTableModel() {
        if (witaConfigService.getDefaultWitaVersion().isGreaterOrEqualThan(WitaCdmVersion.V2))
        {
            return new AKReferenceAwareTableModel<>(
                    new ReflectionTableMetaData("Taifun Auftragsnr", "auftragNoOrig", Long.class),
                    new ReflectionTableMetaData("Bez.M-net", "bezeichnungMnet", String.class),
                    new ReflectionTableMetaData(CARRIER_COLUMN_NAME, "carrierId", String.class),
                    new ReflectionTableMetaData("Ref.Nr.", "carrierRefNr", String.class),
                    new ReflectionTableMetaData("KWT alt", "vorgabeMnet", Date.class),
                    new ReflectionTableMetaData("Meldungstext", "meldungsText", String.class),
                    new ReflectionTableMetaData(STATUS_COLUMN_NAME, "bearbeitungStatusAsString", String.class),
                    new ReflectionTableMetaData(TAM_ART_COLUMN_NAME, "meldungsCode", String.class),
                    new ReflectionTableMetaData("Geschäftsfall", "cbVorgang.witaGeschaeftsfallTyp", GeschaeftsfallTyp.class),
                    new ReflectionTableMetaData(LAST_CHANGE_COLUMN_NAME, "letzteAenderung", Date.class),
                    new ReflectionTableMetaData(TASK_BEARBEITER_COLUMN_NAME, "taskBearbeiter", String.class),
                    new ReflectionTableMetaData("Task-B. Team", "taskBearbeiterTeam", String.class),
                    new ReflectionTableMetaData("Lfd.Nr. der TAM", "tamCount", Long.class),
                    new ReflectionTableMetaData("Mahn TAM", "mahnTam", Boolean.class),
                    new ReflectionTableMetaData("Frist", "restFristInTagen", Integer.class),
                    new ReflectionTableMetaData(KLAERFALL_COLUMN_NAME, "klaerfallSet", Boolean.class),
                    new ReflectionTableMetaData(BEARBEITER_COLUMN_NAME, "auftragBearbeiter", String.class),
                    new ReflectionTableMetaData("Bearb. Team", "auftragBearbeiterTeam", String.class),
                    new ReflectionTableMetaData("Niederlassung", "niederlassung", String.class),
                    new ReflectionTableMetaData("Leitungsbezeichnung", "leitungsBezeichnung", String.class)
            );
        }
        else {
            return new AKReferenceAwareTableModel<>(
                    new ReflectionTableMetaData("Abw.", "cbVorgang.anbieterwechselTkg46", Boolean.class),
                    new ReflectionTableMetaData("Taifun Auftragsnr", "auftragNoOrig", Long.class),
                    new ReflectionTableMetaData("Bez.M-net", "bezeichnungMnet", String.class),
                    new ReflectionTableMetaData(CARRIER_COLUMN_NAME, "carrierId", String.class),
                    new ReflectionTableMetaData("Ref.Nr.", "carrierRefNr", String.class),
                    new ReflectionTableMetaData("KWT alt", "vorgabeMnet", Date.class),
                    new ReflectionTableMetaData("Meldungstext", "meldungsText", String.class),
                    new ReflectionTableMetaData(STATUS_COLUMN_NAME, "bearbeitungStatusAsString", String.class),
                    new ReflectionTableMetaData(TAM_ART_COLUMN_NAME, "meldungsCode", String.class),
                    new ReflectionTableMetaData("Geschäftsfall", "cbVorgang.witaGeschaeftsfallTyp", GeschaeftsfallTyp.class),
                    new ReflectionTableMetaData(LAST_CHANGE_COLUMN_NAME, "letzteAenderung", Date.class),
                    new ReflectionTableMetaData(TASK_BEARBEITER_COLUMN_NAME, "taskBearbeiter", String.class),
                    new ReflectionTableMetaData("Task-B. Team", "taskBearbeiterTeam", String.class),
                    new ReflectionTableMetaData("Lfd.Nr. der TAM", "tamCount", Long.class),
                    new ReflectionTableMetaData("Mahn TAM", "mahnTam", Boolean.class),
                    new ReflectionTableMetaData("Frist", "restFristInTagen", Integer.class),
                    new ReflectionTableMetaData(KLAERFALL_COLUMN_NAME, "klaerfallSet", Boolean.class),
                    new ReflectionTableMetaData(BEARBEITER_COLUMN_NAME, "auftragBearbeiter", String.class),
                    new ReflectionTableMetaData("Bearb. Team", "auftragBearbeiterTeam", String.class),
                    new ReflectionTableMetaData("Niederlassung", "niederlassung", String.class),
                    new ReflectionTableMetaData("Leitungsbezeichnung", "leitungsBezeichnung", String.class)
            );
        }
    }

    @Override
    public final void loadData() {
        tbMdlTamVorgaenge.removeAll();
        showDetails(null);
        try {
            if (!defaultsLoaded) {
                try {
                    setWaitCursor();
                    loadReferences();
                }
                finally {
                    setDefaultCursor();
                }
            }
            final SwingWorker<List<TamVorgang>, Void> worker = createDataLoader();
            setWaitCursor();
            showProgressBar("Daten laden...");
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private SwingWorker<List<TamVorgang>, Void> createDataLoader() {
        return new SwingWorker<List<TamVorgang>, Void>() {

            @Override
            public List<TamVorgang> doInBackground() throws Exception {
                return witaUsertaskService.findOpenTKGTamTasksWithWiedervorlage();
            }

            @Override
            protected void done() {
                try {
                    tbMdlTamVorgaenge.setData(get());
                }
                catch (Exception e) {
                    LOGGER.error(e, e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };
    }

    private void loadReferences() throws FindException {
        List<Carrier> carrier = carrierService.findCarrier();
        Map<Long, Carrier> carrierMap = new HashMap<>();
        CollectionMapConverter.convert2Map(carrier, carrierMap, "getId", null);
        tbMdlTamVorgaenge.addReference(tbMdlTamVorgaenge.findColumn(CARRIER_COLUMN_NAME), carrierMap, "name");
        defaultsLoaded = true;
    }

    @Override
    public void fillDetail(final TamVorgang tamVorgang) throws Exception {
        final SwingWorker<UserTaskDetails, Void> worker = new SwingWorker<UserTaskDetails, Void>() {
            @Override
            public UserTaskDetails doInBackground() throws Exception {
                return witaUsertaskService.loadUserTaskDetails(tamVorgang);
            }

            @Override
            protected void done() {
                try {
                    OffeneTKGTamsPanel.this.selectedTamVorgangDetails = get();
                    TamUserTask tamUserTask = tamVorgang.getTamUserTask();
                    String tamUserBemerkung = null;
                    if (tamUserTask != null) {
                        tamUserBemerkung = tamUserTask.getBemerkungen();
                        selectedTamBearbeitungsStatus = tamUserTask.getTamBearbeitungsStatus();
                        cbTamStatus.setSelectedItem(selectedTamBearbeitungsStatus);
                    }
                    setTaBemerkungsText(tamUserBemerkung);

                    Kunde kunde = selectedTamVorgangDetails.getKunde();
                    if (kunde != null) {
                        tfKName.setText(kunde.getName());
                        tfKVorname.setText(kunde.getVorname());
                        tfHauptrufnummer.setText(kunde.getHauptRufnummer());
                        tfGeschaeftsnummer.setText(kunde.getRnGeschaeft());
                        tfPrivatnummer.setText(kunde.getRnPrivat());
                        tfMobilnummer.setText(kunde.getRnMobile());
                        tfVoipnummer.setText(kunde.getRnVoip());
                        tfEmail.setText(kunde.getEmail());
                    }

                    Endstelle endstelle = selectedTamVorgangDetails.getEndstelle();
                    if (endstelle != null) {
                        tfEName.setText(endstelle.getName());
                        tfEEndstelle.setText(endstelle.getEndstelle());
                        tfEPLZ.setText(endstelle.getPlz());
                        tfEOrt.setText(endstelle.getOrt());
                    }

                    HVTGruppe hvtGruppe = selectedTamVorgangDetails.getHvtGruppe();
                    if (hvtGruppe != null) {
                        tfEHvt.setText(hvtGruppe.getOrtsteil());
                    }
                }
                catch (InterruptedException | ExecutionException e) {
                    LOGGER.error(e, e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };

        try {
            setWaitCursor();
            showProgressBar("Details laden...");
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void setTaBemerkungsText(String tamUserBemerkung) {
        String bemerkung = "";
        if (selectedCbTask != null) {
            if (selectedCbTask.isKlaerfallSet()) {
                spBemerkungen.setBorder(BorderFactory.createLineBorder(Color.red, 2));
                if (isNotEmpty(selectedCbTask.getKlaerfallBemerkung())) {
                    bemerkung = "Klärfall: "
                            + selectedCbTask.getKlaerfallBemerkung()
                            + "\n" + CBVorgang.KLAERFALL_BEMERKUNGS_SEPARATOR + "\n";
                }
            }

            if (isNotEmpty(tamUserBemerkung)) {
                bemerkung += tamUserBemerkung;
            }
            taBemerkungen.setText(bemerkung);
        }
    }

    @Override
    protected void clearDetails() {
        super.clearDetails();
        this.selectedTamVorgangDetails = null;
        this.selectedTamBearbeitungsStatus = null;
        this.spBemerkungen.setBorder(spBemerkungenDefaultBorder);
        cbTamStatusFilter.setSelectedItem(selectedTamstatusFilter);
    }


    @Override
    protected void executeSpecific(String command) throws Exception {
        switch (command) {
            case STORNO:
                storno();
                break;
            case TERMINVERSCHIEBUNG:
                terminverschiebung();
                break;
            case TV_30TAGE:
                terminverschiebung30Tage();
                break;
            case SAVE_TAM_USER_TASK:
                saveTamUserTask();
                break;
            case TAM_STATUS_FILTER:
                filterTamStatus();
                break;
            case CLOSE_TAM_USER_TASK:
                closeTamUserTask();
                break;
            case ERLMK:
                sendErledigtmeldung();
                break;
            default:
                break;
        }
    }

    private void closeTamUserTask() throws HurricanGUIException, StoreException {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Vorgang ausgewählt.");
        }
        TamUserTask tamUserTask = selectedCbTask.getTamUserTask();
        if (tamUserTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Usertask vorhanden.");
        }

        String bemerkungenAlt = StringUtils.isEmpty(tamUserTask.getBemerkungen()) ? "" : tamUserTask.getBemerkungen();
        String bemerkungenNeu = bemerkungenAlt + " [TA TAM geschlossen]";
        bemerkungenNeu = appendUserAndDateIfChanged(bemerkungenAlt, bemerkungenNeu);
        tamUserTask.setBemerkungen(bemerkungenNeu);
        AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();
        witaUsertaskService.closeUserTask(tamUserTask, currentUser);

        removeFromTamList(Collections.singletonList(selectedCbTask.getCbVorgang()));
    }

    /**
     * Storniert den aktuellen CBVorgang mit dem Storno-Dialog.
     */
    private void storno() throws HurricanGUIException {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Vorgang ausgewählt.");
        }
        StornoDialog dlg = StornoDialog.forTamVorgang(selectedCbTask);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
        removeFromTamList(result);
    }

    private void removeFromTamList(Object result) {
        if (result instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<CBVorgang> cbVorgaenge = (Collection<CBVorgang>) result;
            for (CBVorgang cbVorgang : cbVorgaenge) {
                this.selectedCbTask = null;

                List<TamVorgang> tamVorgaengeToRemove = Lists.newArrayList();
                for (TamVorgang tamVorgang : tbMdlTamVorgaenge.getData()) {
                    if (tamVorgang.getId().equals(cbVorgang.getId())) {
                        tamVorgaengeToRemove.add(tamVorgang);
                    }
                }
                for (TamVorgang tamVorgangToRemove : tamVorgaengeToRemove) {
                    tbMdlTamVorgaenge.removeObject(tamVorgangToRemove);
                }
            }
        }
    }

    /**
     * Aktualisiert den Zustand des Panels mit den geaenderten WitaCBVorgaengen
     */
    private void refreshTableModelAndDetailView(Collection<WitaCBVorgang> updatedCbVorgaenge) throws Exception {
        for (WitaCBVorgang cbVorgang : updatedCbVorgaenge) {
            refreshTableModelAndDetailView(cbVorgang);
        }
    }

    /**
     * Aktualisiert den Zustand des Panels mit den geaenderten WitaCBVorgang
     */
    private void refreshTableModelAndDetailView(WitaCBVorgang updatedCbVorgang) throws Exception {
        // refresh table model
        for (TamVorgang tamVorgang : tbMdlTamVorgaenge.getData()) {
            if (tamVorgang.getId().equals(updatedCbVorgang.getId())) {
                tamVorgang.refreshWithCbVorgang(updatedCbVorgang);
                if (updatedCbVorgang.getTamUserTask().isTv60Sent()) {
                    tamVorgang.setTv60Sent(true);
                    tamVorgang.setMahnTam(false);
                    tamVorgang.setRestFristInTagen(witaUsertaskService.getRestFristInTagen(tamVorgang));
                }
            }
        }

        // refresh selected Vorgang bzw Detailansicht
        if ((selectedCbTask != null) && selectedCbTask.getId().equals(updatedCbVorgang.getId())) {
            PropertyUtils.copyProperties(selectedCbTask, updatedCbVorgang);
            selectedCbTask.notifyObservers(true);
            showDetails(selectedCbTask);
        }
    }

    private void terminverschiebung() throws Exception {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Vorgang ausgewählt.");
        }
        TerminverschiebungDialog dlg = TerminverschiebungDialog.forTamVorgang(selectedCbTask, true);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
        removeFromTamList(result);
    }

    private void terminverschiebung30Tage() throws Exception {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Vorgang ausgewählt.");
        }

        Date vorgabeDatumTv30;
        try {
            showProgressBar("Vorgabedatum berechnen...");
            setWaitCursor();
            vorgabeDatumTv30 = witaTalOrderService.getVorgabeDatumTv30(selectedCbTask.getWitaGeschaeftsfallTyp(),
                    selectedCbTask.getVorabstimmungsId());
        }
        finally {
            stopProgressBar();
            setDefaultCursor();
        }
        TerminverschiebungDialog dlg = TerminverschiebungDialog.forTamVorgang(selectedCbTask, false, vorgabeDatumTv30, TamBearbeitungsStatus.TV_30_TAGE);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);

        if (result instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<WitaCBVorgang> cbVorgaenge = (Collection<WitaCBVorgang>) result;
            refreshTableModelAndDetailView(cbVorgaenge);
        }
    }

    @Override
    protected void taskBearbeiten() throws Exception {
        AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();
        for (TamVorgang tamVorgang : getSelectedCbTasks()) {
            UserTask userTask = witaUsertaskService.claimUserTask(tamVorgang.getUserTask(), currentUser);
            tamVorgang.getCbVorgang().setTamUserTask((TamUserTask) userTask);
            refreshTableModelAndDetailView(tamVorgang.getCbVorgang());
        }
    }

    @Override
    protected void taskFreigeben() throws Exception {
        for (TamVorgang tamVorgang : getSelectedCbTasks()) {
            UserTask userTask = witaUsertaskService.claimUserTask(tamVorgang.getUserTask(), null);
            tamVorgang.getCbVorgang().setTamUserTask((TamUserTask) userTask);
            refreshTableModelAndDetailView(tamVorgang.getCbVorgang());
        }
    }

    @Override
    protected void eigeneTasks() {
        tbMdlTamVorgaenge.clearFilter();
        FilterRelation relation = new FilterRelation(EIGENE_TASKS_FILTER_NAME, FilterRelations.OR);
        int bearbeiterColumnIndex = tbMdlTamVorgaenge.findColumn(BEARBEITER_COLUMN_NAME);
        int taskBearbeiterColumnIndex = tbMdlTamVorgaenge.findColumn(TASK_BEARBEITER_COLUMN_NAME);
        relation.addChild(new FilterOperator(FilterOperators.EQ, getLoginCurrentUser(), taskBearbeiterColumnIndex));
        relation.addChild(new FilterOperator(FilterOperators.EQ, getLoginCurrentUser(), bearbeiterColumnIndex));
        tbMdlTamVorgaenge.addFilter(relation);
        showOnlyEigeneTasks = true;
    }

    @Override
    protected void alleTasks() {
        removeStatusFilterFromTamVorgaenge();
        tbMdlTamVorgaenge.removeFilter(EIGENE_TASKS_FILTER_NAME);
        showOnlyEigeneTasks = false;
    }

    private void filterTamStatus() {
        TamBearbeitungsStatusFilter status = (TamBearbeitungsStatusFilter) cbTamStatusFilter.getSelectedItem();
        List<TamVorgang> selectedTasks = getSelectedCbTasks();
        try {
            if (isValidTamStatusFilter(status)) {
                LOGGER.debug("tam filter status = " + status);
                if (!status.equals(selectedTamstatusFilter)) {
                    removeStatusFilterFromTamVorgaenge();
                    selectedTamstatusFilter = status;
                    if (!TamBearbeitungsStatusFilter.UNDEFINED.equals(status)) {
                        addStatusFilterToTamVorgaenge(status);
                    }
                }
            }
        }
        finally {
            for (TamVorgang selectedTask : selectedTasks) {
                TableModel tableModel = tbTamVorgang.getModel();
                Optional<Integer> optionalIndex;
                if (tableModel instanceof AKTableSorter) {
                    @SuppressWarnings("unchecked")
                    AKTableSorter<TamVorgang> sorter = (AKTableSorter) tableModel;
                    optionalIndex = sorter.getRowIndexOfData(selectedTask);
                }
                else {
                    optionalIndex = tbMdlTamVorgaenge.getRowIndexOfData(selectedTask);
                }

                int index = optionalIndex.orElse(-1);
                if (index >= 0 && index < tbTamVorgang.getRowCount()) {
                    tbTamVorgang.addRowSelectionInterval(index, index);
                }
                else {
                    tbTamVorgang.clearSelection();
                }
            }
        }
    }

    private boolean isValidTamStatusFilter(TamBearbeitungsStatusFilter status) {
        return status != null;
    }

    private void addStatusFilterToTamVorgaenge(TamBearbeitungsStatusFilter status) {
        FilterRelation relation = new FilterRelation(TASK_STATUS_FILTER_NAME, FilterRelations.AND);
        int taskStatusColumnIndex = tbMdlTamVorgaenge.findColumn(STATUS_COLUMN_NAME);
        relation.addChild(new FilterOperator(FilterOperators.EQ, status.getDisplay(), taskStatusColumnIndex));
        tbMdlTamVorgaenge.addFilter(relation);
    }

    private void removeStatusFilterFromTamVorgaenge() {
        tbMdlTamVorgaenge.removeFilter(TASK_STATUS_FILTER_NAME);
        //        selectedTamstatusFilter = TamBearbeitungsStatusFilter.UNDEFINED;
        cbTamStatusFilter.setSelectedItem(TamBearbeitungsStatusFilter.UNDEFINED);
    }

    private void saveTamUserTask() throws Exception {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Vorgang ausgewählt.");
        }
        TamUserTask tamUserTask = selectedCbTask.getTamUserTask();
        if (tamUserTask == null) {
            throw new HurricanGUIException("Es ist kein TAM-Usertask vorhanden.");
        }

        TamBearbeitungsStatus tamBearbeitungsStatusNeu = (TamBearbeitungsStatus) cbTamStatus.getSelectedItem();
        String bemerkungenNeu = removeCbVorgangKlaerfallText(taBemerkungen.getText());

        bemerkungenNeu = appendStatusAenderungToBemerkungen(tamBearbeitungsStatusNeu,
                tamUserTask.getTamBearbeitungsStatus(), bemerkungenNeu);
        tamUserTask.setTamBearbeitungsStatus(tamBearbeitungsStatusNeu);

        bemerkungenNeu = appendUserAndDateIfChanged(tamUserTask.getBemerkungen(), bemerkungenNeu,
                DateTools.PATTERN_DATE_TIME);
        tamUserTask.setBemerkungen(bemerkungenNeu);

        tamUserTask.setLetzteAenderung(new Date());

        WitaCBVorgang currentCbVorgang = selectedCbTask.getCbVorgang();
        WitaCBVorgang savedCbVorgang = (WitaCBVorgang) carrierElTalService.saveCBVorgang(currentCbVorgang);

        refreshTableModelAndDetailView(savedCbVorgang);
        setTaBemerkungsText(bemerkungenNeu);
    }

    private String removeCbVorgangKlaerfallText(String taBemerkungenText) {
        if (selectedCbTask.isKlaerfallSet() && isNotEmpty(taBemerkungenText)) {
            String[] splitedStrings = StringUtils.splitByWholeSeparator(taBemerkungenText,
                    CBVorgang.KLAERFALL_BEMERKUNGS_SEPARATOR);
            return StringUtils.removeStart(splitedStrings[splitedStrings.length - 1], "\n");
        }
        return taBemerkungenText;
    }

    @Override
    protected AbstractServiceOptionDialog getHistoryDialog(TamVorgang selectedCbTask) {
        return new HistoryByCbDialog(selectedCbTask.getCbId(), selectedCbTask.getAuftragId());
    }

    @Override
    protected void setCbTaskControlsEnabled(boolean enabled) {
        // Buttons
        btnTv.setEnabled(enabled);
        btnStorno.setEnabled(enabled);
        btnWiedervorlage.setEnabled(enabled);
        btn30Tage.setEnabled(enabled);

        // Felder zum Bearbeiten des TamUserTasks
        taBemerkungen.setEnabled(enabled);
        cbTamStatus.setEnabled(enabled);
        btnSaveTamUserTask.setEnabled(enabled);

    }

    @Override
    protected CCAuftragModel getAuftragModelForSelection(TamVorgang selectedCbVorgangTask) {
        return selectedCbVorgangTask;
    }

    @Override
    protected AKJTable getCbTaskTable() {
        return tbTamVorgang;
    }

    @Override
    protected int[] getCbTaskTableSize() {
        return new int[] { 40, 80, 115, 45, 70, 70, 180, 90, 80, 70, 70, 40, 60, 60, 45, 70, 70, 100, 160 };
    }

    @Override
    protected int createExtraFilterButtons(AKJPanel btnPnl, int yBtnPnl) {
        return yBtnPnl;
    }

    @Override
    protected void updateSpecificControls(List<TamVorgang> selected) {
        long numOfTKGTamWith6012 = selected.stream()
                .filter(t -> MELDUNGSCODE_6012.equals(t.getMeldungsCode()))
                .count();

        boolean somethingSelected = selected.size() > 0;
        boolean singleSelection = selected.size() == 1;

        boolean TATamSelected = numOfTKGTamWith6012 > 0;
        boolean selectionWithoutTA = somethingSelected && !TATamSelected;
        boolean currentUserIsBearbeiter = isCurrentUserTaskBearbeiter(selected);

        btnSaveTamUserTask.setEnabled(shouldEnable(btnSaveTamUserTask, somethingSelected));
        btn30Tage.setEnabled(shouldEnable(btn30Tage, selectionWithoutTA));
        btnStorno.setEnabled(shouldEnable(btnStorno, selectionWithoutTA));
        btnTv.setEnabled(shouldEnable(btnTv, selectionWithoutTA));
        btnWiedervorlage.setEnabled(shouldEnable(btnWiedervorlage, selectionWithoutTA));

        boolean enableCloseTAMTaskButton = singleSelection && TATamSelected && currentUserIsBearbeiter;
        btnCloseTATamTask.setEnabled(enableCloseTAMTaskButton);

        boolean gfBereitstellungSelected = selectedCbTask != null &&
                GeschaeftsfallTyp.BEREITSTELLUNG.equals(selectedCbTask.getWitaGeschaeftsfallTyp());
        boolean enableSendErledigtMeldung = singleSelection && gfBereitstellungSelected && currentUserIsBearbeiter;
        sendErledigtMeldungAction.setEnabled(enableSendErledigtMeldung);
    }

    private boolean isCurrentUserTaskBearbeiter(List<TamVorgang> selected) {
        long count = selected.stream().filter(t->!getLoginCurrentUser().equals(t.getTaskBearbeiter())).count();
        return count == 0;
    }

    private boolean shouldEnable(AKJButton button, boolean enableButtons) {
        return button.isEnabled() && enableButtons;
    }

    /**
     * Sendet Erlmk fuer alle angehakten Vorgaenge.
     */
    private void sendErledigtmeldung() {
        BaseAenderungsKennzeichenDialog dlg = ErlmkDialog.forTamVorgang(selectedCbTask);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
        removeFromTamList(result);
    }

    @Override
    protected void wiedervorlage() {
        WiedervorlageDialog dialog = new WiedervorlageDialog(selectedCbTask.getUserTask().getWiedervorlageAmAsLocalDateTime(),
                selectedCbTask.getUserTask().getBemerkungen(), true);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dialog, true, true);

        if (result instanceof Integer && result.equals(JOptionPane.OK_OPTION)) {
            UserTask userTask = selectedCbTask.getUserTask();
            if ((userTask.getWiedervorlageAm() == null)
                    || !userTask.getWiedervorlageAm().equals(DateConverterUtils.asDate(dialog.getWiedervorlageDatum()))) {
                userTask.setWiedervorlageAm(dialog.getWiedervorlageDatum());
                userTask.setBemerkungen(dialog.getStatusBemerkung());
                witaUsertaskService.storeUserTask(userTask);
                removeFromTamList(Collections.singletonList(selectedCbTask));
                showDetails(null);
            }
        }
    }
}
