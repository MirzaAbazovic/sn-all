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
import javax.swing.*;
import javax.swing.border.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.TextInputDialog;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.swing.table.FilterRelation;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaData;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByCbDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.BaseAenderungsKennzeichenDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.StornoDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Feature.FeatureName;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangAutomationError;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.model.exmodules.tal.TALVorfall;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.ESAATalOrderService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.augustakom.hurrican.service.exmodules.tal.TALService;
import de.augustakom.hurrican.service.exmodules.tal.utils.TALServiceFinder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.validators.RufnummerPortierungCheck;
import de.mnet.wita.service.TalQueryService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Panel fuer die Darstellung der noch nicht abgeschlossenen el. TAL-Bestellungen.
 */
public class UnfinishedCarrierBestellungPanel extends AbstractServicePanel implements AKTableOwner,
        AKDataLoaderComponent, AKFilterTableModelListener, AKObjectSelectionListener {

    private static final long serialVersionUID = -4878747180732406525L;

    private static final Logger LOGGER = Logger.getLogger(UnfinishedCarrierBestellungPanel.class);

    private static final String HISTORY = "history";
    private static final String CLOSE = "close";
    private static final String STORNO = "storno";
    private static final String ALLE_TASKS = "alle.tasks";
    private static final String EIGENE_TASKS = "eigene.tasks";
    private static final String WIEDERVORLAGE = "wiedervorlage";

    static final String TYP_COLUMN_NAME = "Typ";
    static final String CARRIER_COLUMN_NAME = "Carrier";
    static final String STATUS_COLUMN_NAME = "Status";
    static final String RUECKMELDUNG_COLUMN_NAME = "Rückmeldung";
    static final String AENDERUNGSKENNZEICHEN_COLUMN_NAME = "Änderungskennzeichen";
    private static final String BEARBEITER_COLUMN_NAME = "Bearbeiter";
    private static final String KLAERFALL_COLUMN_NAME = "Klärfall";

    private static final String EIGENE_TASKS_FILTER_NAME = "eigene.tasks.filter";

    // GUI-Komponenten
    private CBVorgangTable tbCBVorgang = null;
    private AKReferenceAwareTableModel<CBVorgangNiederlassung> tbMdlCBVorgang = null;
    private AKJButton btnWiedervorlage = null;
    private AKJButton btnStorno = null;
    private AKJButton btnClose = null;
    private AKJButton btnHistory = null;
    private AKJTextField tfKName = null;
    private AKJTextField tfKVorname = null;
    private AKJTextArea taCBMontagehinweis = null;
    private AKReferenceField rfDTAGUsecase = null;
    private AKJTextField tfEName = null;
    private AKJTextField tfEEndstelle = null;
    private AKJTextField tfEPLZ = null;
    private AKJTextField tfEOrt = null;
    private AKJTextField tfEHvt = null;
    private AKJTextField tfReturnLBZ = null;
    private AKJTextField tfReturnVtrnr = null;
    private AKJTextField tfReturnAQS = null;
    private AKJTextField tfReturnLL = null;
    private AKJTextField tfReturnCBearb = null;
    private AKReferenceField rfReturnTyp = null;
    private AKJTextArea taReturnBemerkung = null;
    private AKJScrollPane spReturnBemerkung = null;
    private Border spReturnBemerkungDefaultBorder;
    private AKJLabel lblHvtKvz = null;
    private AKJLabel lblRefOrderId = null;
    private AKJFormattedTextField tfRefOrderId = null;

    private AKJTextArea taStatusBemerkung = null;
    // Sonstiges
    private boolean defaultsLoaded = false;
    private boolean showOnlyEigeneTasks = true;

    private CBVorgangNiederlassung actCBVorgang = null;
    private BAService baService;
    private CarrierService carrierService;
    private CarrierElTALService carrierElTalService;
    private CCAuftragService auftragService;
    private ESAATalOrderService esaaTalOrderService;
    private HVTService hvtService;
    private ReferenceService referenceService;
    private TalQueryService talQueryService;
    private WitaTalOrderService witaTalOrderService;
    private FeatureService featureService;
    private ElektraFacadeService elektraFacadeService;
    private BillingAuftragService billingAuftragService;

    public UnfinishedCarrierBestellungPanel() {
        this(true);
    }

    public UnfinishedCarrierBestellungPanel(boolean createGui) {
        super("de/augustakom/hurrican/gui/tools/tal/resources/UnfinishedCarrierBestellungPanel.xml");
        if (createGui) {
            initServices();
            createGUI();
            loadDefaults();
        }
    }

    private void initServices() {
        try {
            baService = getCCService(BAService.class);
            auftragService = getCCService(CCAuftragService.class);
            carrierService = getCCService(CarrierService.class);
            carrierElTalService = getCCService(CarrierElTALService.class);
            esaaTalOrderService = getCCService(ESAATalOrderService.class);
            hvtService = getCCService(HVTService.class);
            referenceService = getCCService(ReferenceService.class);
            talQueryService = getCCService(TalQueryService.class);
            witaTalOrderService = getCCService(WitaTalOrderService.class);
            featureService = getCCService(FeatureService.class);
            elektraFacadeService = getElektraService(ElektraFacadeService.class);
            billingAuftragService = getBillingService(BillingAuftragService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        tbMdlCBVorgang = createCBVorgangTableModel();
        tbMdlCBVorgang.addFilterTableModelListener(this);
        tbCBVorgang  = new CBVorgangTable(tbMdlCBVorgang, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.SINGLE_SELECTION, STATUS_COLUMN_NAME);
        tbCBVorgang.attachSorter();
        tbCBVorgang.addTableListener(this);
        tbCBVorgang.addPopupAction(new TableOpenOrderAction());
        tbCBVorgang.addPopupAction(new ShowAutomationErrorsAction());
        tbCBVorgang.addPopupAction(new ShowHistoryAction());
        tbCBVorgang.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbCBVorgang.fitTable(new int[] { 30, 30, 60, 95, 115, 40, 70, 60, 70, 90, 70, 60, 70, 75, 65, 45, 80, 75,
                100, 100, 50 });
        AKJScrollPane spCBVorgang = new AKJScrollPane(tbCBVorgang, new Dimension(700, 300));

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

        btnWiedervorlage = getSwingFactory().createButton(WIEDERVORLAGE, getActionListener());
        btnStorno = getSwingFactory().createButton(STORNO, getActionListener());
        btnClose = getSwingFactory().createButton(CLOSE, getActionListener());
        btnHistory = getSwingFactory().createButton(HISTORY, getActionListener());

        AKJLabel lblKunde = getSwingFactory().createLabel("kunde", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblKName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblKVorname = getSwingFactory().createLabel("kunde.vorname");
        AKJLabel lblCBDetails = getSwingFactory().createLabel("cb.details", SwingConstants.LEFT, Font.BOLD);

        AKJLabel lblCBMontagehinweis;
        lblCBMontagehinweis = getSwingFactory().createLabel("montagehinweis");

        AKJLabel lblStatusBem = getSwingFactory().createLabel("status.bemerkung");
        AKJLabel lblDTAGUsecase = getSwingFactory().createLabel("dtag.usecase");
        AKJLabel lblEndstelle = getSwingFactory().createLabel("endstelle", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblEName = getSwingFactory().createLabel("es.name");
        AKJLabel lblEEndstelle = getSwingFactory().createLabel("es.strasse");
        AKJLabel lblEPLZ = getSwingFactory().createLabel("es.plz.ort");
        AKJLabel lblEHvt = getSwingFactory().createLabel("es.hvt");
        AKJLabel lblReturn = getSwingFactory().createLabel("rueckmeldung", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblReturnLBZ = getSwingFactory().createLabel("lbz");
        AKJLabel lblReturnVtrnr = getSwingFactory().createLabel("vtrnr");
        AKJLabel lblReturnAQS = getSwingFactory().createLabel("aqs");
        AKJLabel lblReturnLL = getSwingFactory().createLabel("laenge");
        AKJLabel lblReturnCBearb = getSwingFactory().createLabel("carrier.bearbeiter");
        AKJLabel lblReturnTyp = getSwingFactory().createLabel("exm.fehlertyp");
        AKJLabel lblRetBemerkung = getSwingFactory().createLabel("description");
        lblHvtKvz = getSwingFactory().createLabel("hvt.kvz", SwingConstants.LEFT, Font.BOLD);
        lblRefOrderId = getSwingFactory().createLabel("hvt.kvz.ref.auftrag.id");

        tfKName = getSwingFactory().createTextField("kunde.name", false);
        tfKVorname = getSwingFactory().createTextField("kunde.vorname", false);
        taCBMontagehinweis = getSwingFactory().createTextArea("montagehinweis", false);

        AKJScrollPane spCBMontagehinweis = new AKJScrollPane(taCBMontagehinweis);
        taStatusBemerkung = getSwingFactory().createTextArea("status.bemerkung", false);
        taStatusBemerkung.addMouseListener(new AddStatusBemMouseListener());
        AKJScrollPane spStatusBem = new AKJScrollPane(taStatusBemerkung);
        rfDTAGUsecase = getSwingFactory().createReferenceField("dtag.usecase");
        rfDTAGUsecase.setEnabled(false);
        tfEName = getSwingFactory().createTextField("es.name", false);
        tfEEndstelle = getSwingFactory().createTextField("es.strasse", false);
        tfEPLZ = getSwingFactory().createTextField("es.plz", false);
        tfEOrt = getSwingFactory().createTextField("es.ort", false);
        tfEHvt = getSwingFactory().createTextField("es.hvt", false);
        tfReturnLBZ = getSwingFactory().createTextField("lbz", false);
        tfReturnVtrnr = getSwingFactory().createTextField("vtrnr", false);
        tfReturnAQS = getSwingFactory().createTextField("aqs", false);
        tfReturnLL = getSwingFactory().createTextField("laenge", false);
        tfReturnCBearb = getSwingFactory().createTextField("carrier.bearbeiter", false);
        rfReturnTyp = getSwingFactory().createReferenceField("exm.fehlertyp");
        rfReturnTyp.setEnabled(false);
        taReturnBemerkung = getSwingFactory().createTextArea("description", false);
        spReturnBemerkung = new AKJScrollPane(taReturnBemerkung);
        spReturnBemerkungDefaultBorder = spReturnBemerkung.getBorder();
        tfRefOrderId = getSwingFactory().createFormattedTextField("hvt.kvz.ref.auftrag.id", false);

        AKJPanel kdPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        kdPnl.add(lblKunde          , GBCFactory.createGBC(  0,  0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblKName          , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        kdPnl.add(tfKName           , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblKVorname       , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(tfKVorname        , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.NONE));
        kdPnl.add(lblCBDetails      , GBCFactory.createGBC(  0,  0, 0, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblDTAGUsecase    , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(rfDTAGUsecase     , GBCFactory.createGBC(  0,  0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblCBMontagehinweis,GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(spCBMontagehinweis, GBCFactory.createGBC(  0,  0, 2, 6, 1, 2, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblStatusBem      , GBCFactory.createGBC(  0,  0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(spStatusBem       , GBCFactory.createGBC(  0,  0, 2, 8, 1, 2, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblHvtKvz         , GBCFactory.createGBC(  0,  0, 0,10, 3, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(lblRefOrderId     , GBCFactory.createGBC(  0,  0, 0,11, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1,11, 1, 1, GridBagConstraints.NONE));
        kdPnl.add(tfRefOrderId      , GBCFactory.createGBC(  0,  0, 2,11, 1, 1, GridBagConstraints.HORIZONTAL));
        kdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0,12, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on
        setVisibleHvtKvzFields(false);

        AKJPanel esPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        esPnl.add(lblEndstelle      , GBCFactory.createGBC(  0,  0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEName          , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        esPnl.add(tfEName           , GBCFactory.createGBC(  0,  0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEEndstelle     , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEEndstelle      , GBCFactory.createGBC(  0,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEPLZ           , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEPLZ            , GBCFactory.createGBC(  0,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEOrt            , GBCFactory.createGBC(  0,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEHvt           , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEHvt            , GBCFactory.createGBC(  0,  0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        AKJPanel rmPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        rmPnl.add(lblReturn         , GBCFactory.createGBC(  0,  0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblReturnTyp      , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        rmPnl.add(rfReturnTyp       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblReturnLBZ      , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(tfReturnLBZ       , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblReturnVtrnr    , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(tfReturnVtrnr     , GBCFactory.createGBC(  0,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblReturnLL       , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(tfReturnLL        , GBCFactory.createGBC(  0,  0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblReturnAQS      , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(tfReturnAQS       , GBCFactory.createGBC(  0,  0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblReturnCBearb   , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(tfReturnCBearb    , GBCFactory.createGBC(  0,  0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(lblRetBemerkung   , GBCFactory.createGBC(  0,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        rmPnl.add(spReturnBemerkung, GBCFactory.createGBC(  0,  0, 2, 7, 1, 2, GridBagConstraints.HORIZONTAL, 10, 6));
        rmPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0, 9, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        int yButtonsPnl = 0;

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("functions"));
        btnPnl.add(filtersPnl, GBCFactory.createGBC(  0,  0, 0, yButtonsPnl  , 1, 1, GridBagConstraints.HORIZONTAL));

        // @formatter:off
        btnPnl.add(btnWiedervorlage , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnClose         , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnStorno        , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnHistory	    , GBCFactory.createGBC(  0,100, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,100, 0, ++yButtonsPnl, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("details"));
        // @formatter:off
        detPnl.add(kdPnl            , GBCFactory.createGBC( 10,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        detPnl.add(esPnl            , GBCFactory.createGBC( 10,100, 1, 0, 1, 2, GridBagConstraints.BOTH, 20));
        detPnl.add(rmPnl            , GBCFactory.createGBC( 10,100, 2, 0, 1, 2, GridBagConstraints.BOTH, 20));
        detPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        AKJPanel downPnl = new AKJPanel(new BorderLayout());
        downPnl.add(btnPnl, BorderLayout.WEST);
        downPnl.add(detPnl, BorderLayout.CENTER);

        AKJSplitPane split = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        split.setTopComponent(spCBVorgang);
        split.setBottomComponent(new AKJScrollPane(downPnl));
        split.setDividerSize(2);
        split.setResizeWeight(1d); // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        manageGUI(btnClose, btnStorno);
        validateButtons();
    }

    private Optional<AKJComboBox> createAdditionalFilterComponent() {
        return Optional.empty();
    }

    private void setVisibleHvtKvzFields(boolean visible) {
        lblHvtKvz.setVisible(visible);
        lblRefOrderId.setVisible(visible);
        tfRefOrderId.setVisible(visible);
    }

    public AKReferenceAwareTableModel<CBVorgangNiederlassung> createCBVorgangTableModel() {
        return new AKReferenceAwareTableModel<>(
                new ReflectionTableMetaData("Prio", "prio", Boolean.class),
                new ReflectionTableMetaData("", "auftragsKlammerSymbol", String.class),
                new ReflectionTableMetaData("Klammer", "auftragsKlammer", Long.class),
                new ReflectionTableMetaData("Tech. Auftragsnr.", "auftragId", Long.class),
                new ReflectionTableMetaData("Bez.M-net", "bezeichnungMnet", String.class),
                new ReflectionTableMetaData(CARRIER_COLUMN_NAME, "carrierId", String.class),
                new ReflectionTableMetaData("Ref.Nr.", "carrierRefNr", String.class),
                new ReflectionTableMetaData("Vorgabe", "vorgabeMnet", Date.class),
                new ReflectionTableMetaData(TYP_COLUMN_NAME, "typ", String.class),
                new ReflectionTableMetaData(AENDERUNGSKENNZEICHEN_COLUMN_NAME, "aenderungsKennzeichen", Enum.class),
                new ReflectionTableMetaData(STATUS_COLUMN_NAME, "status", String.class),
                new ReflectionTableMetaData("übermittelt", "submittedAt", Date.class),
                new ReflectionTableMetaData("Antwort am", "answeredAt", Date.class),
                new ReflectionTableMetaData(RUECKMELDUNG_COLUMN_NAME, "returnOk", String.class),
                new ReflectionTableMetaData("Real.Datum", "returnRealDate", Date.class),
                new ReflectionTableMetaData(KLAERFALL_COLUMN_NAME, "klaerfallSet", Boolean.class),
                new ReflectionTableMetaData(BEARBEITER_COLUMN_NAME, "bearbeiter.loginName", String.class),
                new ReflectionTableMetaData("Bearb. Team", "bearbeiter.team.name", String.class),
                new ReflectionTableMetaData("Niederlassung", "niederlassung", String.class),
                new ReflectionTableMetaData("Bearbeitungsstatus", "statusBemerkung", String.class),
                new ReflectionTableMetaData("autom. Abschluss", "automation", Boolean.class));
    }

    /**
     * Laedt Default-Daten fuer das Panel.
     */
    private void loadDefaults() {
        try {
            ISimpleFindService exmSFS = (ISimpleFindService) TALServiceFinder.instance()
                    .getTALService(TALService.class);
            rfDTAGUsecase.setFindService(exmSFS);
            rfReturnTyp.setFindService(exmSFS);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        tbMdlCBVorgang.removeAll();
        showDetails(null);
        try {
            if (!defaultsLoaded) {
                loadDefaultTableData();
            }

            final SwingWorker<List<CBVorgangNiederlassung>, Void> worker = new SwingWorker<List<CBVorgangNiederlassung>, Void>() {
                @Override
                public List<CBVorgangNiederlassung> doInBackground() throws Exception {
                    return carrierElTalService.findOpenCBVorgaengeNiederlassungWithWiedervorlage();
                }

                @Override
                protected void done() {
                    try {
                        tbMdlCBVorgang.setData(get());
                        if (showOnlyEigeneTasks) {
                            UnfinishedCarrierBestellungPanel.this.execute(EIGENE_TASKS);
                        }
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

    private void loadDefaultTableData() throws FindException {
        try {
            setWaitCursor();
            List<Reference> typen = referenceService.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_TYP,
                    Boolean.FALSE);
            Map<Long, Reference> typMap = new HashMap<>();
            CollectionMapConverter.convert2Map(typen, typMap, "getId", null);

            List<Reference> stati = referenceService.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_STATUS,
                    Boolean.FALSE);
            Map<Long, Reference> statiMap = new HashMap<>();
            CollectionMapConverter.convert2Map(stati, statiMap, "getId", null);

            List<Carrier> carrier = carrierService.findCarrier();
            Map<Long, Carrier> carrierMap = new HashMap<>();
            CollectionMapConverter.convert2Map(carrier, carrierMap, "getId", null);

            Map<Boolean, String> rueckTypMap = new HashMap<>();
            rueckTypMap.put(Boolean.TRUE, "positiv");
            rueckTypMap.put(Boolean.FALSE, "negativ");

            addReferences(tbMdlCBVorgang, typMap, statiMap, carrierMap, rueckTypMap);

            defaultsLoaded = true;
        }
        finally {
            setDefaultCursor();
        }
    }

    void addReferences(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableModel, Map<Long, Reference> typMap,
            Map<Long, Reference> statiMap, Map<Long, Carrier> carrierMap, Map<Boolean, String> rueckTypMap) {
        addCarrierReference(tableModel, carrierMap);
        addTypReference(tableModel, typMap);
        addStatusReference(tableModel, statiMap);
        addRueckmeldungTypReference(tableModel, rueckTypMap);
    }

    void addRueckmeldungTypReference(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableModel,
            Map<Boolean, String> rueckTypMap) {
        tableModel.addReference(tableModel.findColumn(RUECKMELDUNG_COLUMN_NAME), rueckTypMap, "toString");
    }

    void addStatusReference(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableModel,
            Map<Long, Reference> statiMap) {
        tableModel.addReference(tableModel.findColumn(STATUS_COLUMN_NAME), statiMap, "strValue");
    }

    void addTypReference(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableModel, Map<Long, Reference> typMap) {
        tableModel.addReference(tableModel.findColumn(TYP_COLUMN_NAME), typMap, "strValue");
    }

    void addCarrierReference(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableModel,
            Map<Long, Carrier> carrierMap) {
        tableModel.addReference(tableModel.findColumn(CARRIER_COLUMN_NAME), carrierMap, "name");
    }

    @Override
    public void showDetails(Object details) {
        this.actCBVorgang = null;
        GuiTools.cleanFields(this);
        setVisibleHvtKvzFields(false);
        spReturnBemerkung.setBorder(spReturnBemerkungDefaultBorder);
        if (details instanceof CBVorgangNiederlassung) {
            this.actCBVorgang = (CBVorgangNiederlassung) details;
            try {
                CBUsecase usecase = carrierElTalService.findCBUsecase(actCBVorgang.getUsecaseId());

                if (usecase != null && usecase.getExmTbvId() != null) {
                    TALService exmTals = (TALService) TALServiceFinder.instance().getTALService(TALService.class);
                    TALVorfall vorfall = exmTals.findById(usecase.getExmTbvId(), TALVorfall.class);
                    rfDTAGUsecase.setReferenceId(vorfall != null ? vorfall.getId() : null);
                }

                taCBMontagehinweis.setText(actCBVorgang.getMontagehinweis());
                taStatusBemerkung.setText(actCBVorgang.getStatusBemerkung());
                rfReturnTyp.setReferenceId(actCBVorgang.getExmRetFehlertyp());
                tfReturnLBZ.setText(actCBVorgang.getReturnLBZ());
                tfReturnVtrnr.setText(actCBVorgang.getReturnVTRNR());
                tfReturnLL.setText(actCBVorgang.getReturnLL());
                tfReturnAQS.setText(actCBVorgang.getReturnAQS());
                tfReturnCBearb.setText(actCBVorgang.getCarrierBearbeiter());
                setTaReturnBemerkung(actCBVorgang.getReturnBemerkung());

                Auftrag auftrag = auftragService.findAuftragById(actCBVorgang.getAuftragId());

                KundenService ks = getBillingService(KundenService.class);
                Kunde kunde = ks.findKunde(auftrag.getKundeNo());
                if (kunde != null) {
                    tfKName.setText(kunde.getName());
                    tfKVorname.setText(kunde.getVorname());
                }

                Endstelle es = getEndstelleForCBVorgang();
                tfEName.setText(es.getName());
                tfEEndstelle.setText(es.getEndstelle());
                tfEPLZ.setText(es.getPlz());
                tfEOrt.setText(es.getOrt());

                HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(es.getHvtIdStandort());
                if (hvtGruppe != null) {
                    tfEHvt.setText(hvtGruppe.getOrtsteil());
                }

                final CBVorgang cbVorgang = actCBVorgang.getCbVorgang();
                if (cbVorgang instanceof WitaCBVorgang) {
                    final WitaCBVorgang witaCBVorgang = (WitaCBVorgang) cbVorgang;
                    if (witaCBVorgang.isHvtToKvz()) {
                        final CBVorgang refCbVorgang = carrierElTalService.findCBVorgang(witaCBVorgang
                                .getCbVorgangRefId());
                        tfRefOrderId.setValue(refCbVorgang.getAuftragId());
                        setVisibleHvtKvzFields(true);
                    }
                    else if (CBVorgang.TYP_KUENDIGUNG.equals(witaCBVorgang.getTyp())) {
                        final WitaCBVorgang witaCBVorgangByRefId =
                                witaTalOrderService.findWitaCBVorgangByRefId(witaCBVorgang.getId());
                        if (witaCBVorgangByRefId != null) {
                            tfRefOrderId.setValue(witaCBVorgangByRefId.getAuftragId());
                            setVisibleHvtKvzFields(true);
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        validateButtons();
    }

    private void setTaReturnBemerkung(String returnBemerkungText) {
        String bemerkung = "";
        if (actCBVorgang.isKlaerfallSet()) {
            spReturnBemerkung.setBorder(BorderFactory.createLineBorder(Color.red, 2));

            if (isNotEmpty(actCBVorgang.getCbVorgang().getKlaerfallBemerkung())) {
                bemerkung = "Klärfall: "
                        + actCBVorgang.getCbVorgang().getKlaerfallBemerkung()
                        + "\n" + CBVorgang.KLAERFALL_BEMERKUNGS_SEPARATOR + "\n";
            }
        }

        if (isNotEmpty(returnBemerkungText)) {
            bemerkung += returnBemerkungText;
        }
        taReturnBemerkung.setText(bemerkung);
    }

    private Endstelle getEndstelleForCBVorgang() throws HurricanGUIException, FindException {
        Endstelle endstelle = talQueryService.findEndstelleForCBVorgang(actCBVorgang.getCbVorgang());
        if (endstelle == null) {
            throw new HurricanGUIException("Endstelle nicht gefunden!");
        }
        return endstelle;
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case EIGENE_TASKS:
                eigeneTasks();
                break;
            case ALLE_TASKS:
                alleTasks();
                break;
            case WIEDERVORLAGE:
                wiedervorlage();
                break;
            case CLOSE:
                closeCBVorgang();
                break;
            case STORNO:
                storno();
                break;
            case HISTORY:
                showCBVorgangHistoryDialog();
                break;
            default:
                break;
        }
        validateButtons();
    }

    private void wiedervorlage() {
        WiedervorlageDialog dialog = new WiedervorlageDialog(actCBVorgang.getCbVorgang().getWiedervorlageAmAsLocalDateTime(),
                actCBVorgang.getCbVorgang().getStatusBemerkung(), true);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dialog, true, true);

        if (result instanceof Integer && result.equals(JOptionPane.OK_OPTION)
                && (actCBVorgang.getCbVorgang().getWiedervorlageAm() == null
                || !actCBVorgang.getCbVorgang().getWiedervorlageAmAsLocalDateTime().equals(dialog.getWiedervorlageDatum()))) {
            actCBVorgang.getCbVorgang().setWiedervorlageAm(dialog.getWiedervorlageDatum());
            actCBVorgang.getCbVorgang().setStatusBemerkung(dialog.getStatusBemerkung());
            try {
                carrierElTalService.saveCBVorgang(actCBVorgang.getCbVorgang());
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }

            tbMdlCBVorgang.removeObject(actCBVorgang);
            showDetails(null);
        }
    }

    /**
     * Zeigt die Details aller el. Vorgaenge zur TAL-Bestellung an
     */
    private void showCBVorgangHistoryDialog() {
        if (actCBVorgang != null) {
            AbstractServiceOptionDialog dlg;
            if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
                dlg = new HistoryByCbDialog(actCBVorgang.getCbId(), actCBVorgang.getAuftragId());
            }
            else {
                dlg = new CBVorgangHistoryDialog(actCBVorgang.getCbId());
            }
            DialogHelper.showDialog(getMainFrame(), dlg, false, true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Es wurde kein Vorgang ausgewählt!", null, true);
        }
    }

    private void showAutomationErrorsDialog() {
        if (actCBVorgang != null) {
            if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
                Set<CBVorgangAutomationError> errors = actCBVorgang.getCbVorgang().getAutomationErrors();
                if (CollectionTools.isNotEmpty(errors)) {
                    CBVorgangAutomationErrorDialog dlg = new CBVorgangAutomationErrorDialog(errors);
                    DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Es sind keine Automatisierungsfehler zu dem Vorgang hinterlegt.",
                            "Keine Fehler", null, null);
                }
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Automatisierungs-Fehler können nur bei WITA-Vorgängen angezeigt werden.",
                        "Kein WITA-Vorgang", null, null);
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Es wurde kein Vorgang ausgewählt!", null, true);
        }
    }

    /**
     * Schliesst den CBVorgang ab. Die Daten werden auf die Carrierbestellung uebertragen.
     */
    private void closeCBVorgang() {
        try {
            setWaitCursor();
            if (actCBVorgang == null) {
                throw new HurricanGUIException("Es ist keine Carrierbestellung ausgewählt.");
            }

            if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
                RufnummerPortierungCheck checkResult = witaTalOrderService
                        .checkRufnummerPortierungAufnehmend(actCBVorgang.getId());
                if (checkResult.hasWarnings()) {
                    int result = MessageHelper.showWarningDialog(this, checkResult.generateWarningsText()
                                    + "\n\nWollen sie den Vorgang trotzdem abschliessen?", "Geänderte Portierungsdaten",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                    );
                    if (result == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            }

            CBVorgang cbVorgang;
            if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
                cbVorgang = witaTalOrderService.closeCBVorgang(actCBVorgang.getId(), HurricanSystemRegistry.instance()
                        .getSessionId());
            }
            else {
                cbVorgang = esaaTalOrderService.closeCBVorgang(actCBVorgang.getId(), HurricanSystemRegistry.instance()
                        .getSessionId());
            }
            PropertyUtils.copyProperties(actCBVorgang, cbVorgang);
            actCBVorgang.setCbVorgang(cbVorgang);
            actCBVorgang.notifyObservers(true);
            tbCBVorgang.repaint();

            if (Boolean.TRUE.equals(cbVorgang.getAnbieterwechselTkg46())) {
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(cbVorgang.getAuftragId());
                if (BAVerlaufAnlass.NEUSCHALTUNG.equals(auftragTechnik.getAuftragsart())) {
                    auftragTechnik.setAuftragsart(BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG);
                    auftragService.saveAuftragTechnik(auftragTechnik, false);
                }
            }

            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId());
            if (isAutomationAllowed()) {
                processWitaAbm(auftragDaten, cbVorgang);
            }
            else {
                // Wenn wir versuchen bei der Automatisierung einen Auftrag, der zu einer Klammer gehört, zu schliessen
                // und die andere Aufträge aus diese Klammer noch nicht geschlossen sind, dieses PopUp soll nicht
                // vorkommen
                if (!this.isAutomationAllowedAndKlammerNotClosed(auftragDaten) && cbVorgang.returnRealDateDiffers()) {
                    // Realisierungstermin abweichend von Vorgabe --> Benachrichtigung
                    MessageHelper
                            .showInfoDialog(
                                    getMainFrame(),
                                    getSwingFactory().getText("date.difference"),
                                    DateTools.formatDate(actCBVorgang.getVorgabeMnet(),
                                            DateTools.PATTERN_DAY_MONTH_YEAR),
                                    DateTools.formatDate(actCBVorgang.getReturnRealDate(),
                                            DateTools.PATTERN_DAY_MONTH_YEAR)
                            );
                }
            }

            if (cbVorgang instanceof WitaCBVorgang) {
                WitaCBVorgang witaCBVorgang = (WitaCBVorgang) cbVorgang;
                if (witaCBVorgang.aenderungsKennzeichenIsDifferent()) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "{0} wurde abgelehnt und die Rückmeldung bezieht sich auf die ursprüngliche Bestellung.",
                            witaCBVorgang.getLetztesGesendetesAenderungsKennzeichen().getValue());
                }
                if (witaCBVorgang.getAbbmOnAbm()) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Eine Abbruchmeldung wurde auf eine Auftragsbestätigungsmeldung empfangen. "
                                    + "Bitte den Kunden informieren!"
                    );
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


    private void processWitaAbm(AuftragDaten auftragDaten, CBVorgang cbVorgang) {
        try {
            AuftragDaten referencedOrder = carrierService.findReferencingOrder(cbVorgang);
            Long referencedOrderNoOrig = referencedOrder != null ? referencedOrder.getAuftragNoOrig() : null;

            // beachten: positives Ergebnis muss dem User nicht angezeigt werden.
            elektraFacadeService.changeOrderRealDate(
                    auftragDaten, referencedOrderNoOrig, cbVorgang, getSubAuftragsIds());
            elektraFacadeService.generateAndPrintReportWithEvaluation(auftragDaten, cbVorgang);

            // Bauauftrag erstellen
            createBauauftrag(auftragDaten, cbVorgang);
            if (referencedOrder != null) {
                createBauauftragKuendigung(referencedOrder, cbVorgang.getReturnRealDate());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void createBauauftrag(AuftragDaten auftragDaten, CBVorgang cbVorgang) throws StoreException, FindException {
        CreateVerlaufParameter parameter = new CreateVerlaufParameter();
        parameter.setAnlass(Boolean.TRUE.equals(cbVorgang.getAnbieterwechselTkg46()) ?
                BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG : BAVerlaufAnlass.NEUSCHALTUNG);
        parameter.setAuftragId(auftragDaten.getAuftragId());
        parameter.setSubAuftragsIds(this.getSubAuftragsIds());
        parameter.setRealisierungsTermin(cbVorgang.getReturnRealDate());
        parameter.setInstallType(getBaInstallType(auftragDaten));
        parameter.setAnZentraleDispo(false);
        parameter.setSessionId(HurricanSystemRegistry.instance().getSessionId());

        Pair<Verlauf, AKWarnings> result = baService.createVerlauf(parameter);
        if (result.getFirst() == null) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Bauauftrag wurde nicht erstellt.\nBitte erstellen Sie den Bauauftrag manuell.",
                    "Bauauftrag nicht erstellt!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createBauauftragKuendigung(AuftragDaten auftragDaten, Date realDate) throws StoreException, FindException {
        CreateVerlaufParameter parameter = new CreateVerlaufParameter();
        parameter.setAnlass(BAVerlaufAnlass.KUENDIGUNG);
        parameter.setAuftragId(auftragDaten.getAuftragId());
        parameter.setSubAuftragsIds(null);
        parameter.setRealisierungsTermin(realDate);
        parameter.setAnZentraleDispo(false);
        parameter.setSessionId(HurricanSystemRegistry.instance().getSessionId());

        Pair<Verlauf, AKWarnings> result = baService.createVerlauf(parameter);
        if (result.getFirst() == null) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Kündigungs-Bauauftrag wurde nicht erstellt.\nBitte erstellen Sie den Bauauftrag manuell.",
                    "Kündigungs-Bauauftrag nicht erstellt!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Ermittelt die Auftrag Ids von den Aufträgen die sich in der gleiche Klammer wie der aktuell gewaehlte CB-Vorgang
     * befinden.
     */
    private Set<Long> getSubAuftragsIds() {
        Set<Long> subAuftragsIds = new HashSet<>();
        List<WitaCBVorgang> klammerAuftragList = this.getOtherKlammerAuftraege();
        for (WitaCBVorgang witaCbVorgang : klammerAuftragList) {
            subAuftragsIds.add(witaCbVorgang.getAuftragId());
        }
        return subAuftragsIds;
    }

    /*
     * Ermittelt den Installations-Typ (Selbstmontage / Montage M-net) zu dem Auftrag.
     * Eine Montage M-net wird dann verwendet, wenn der Auftrag eine noch nicht abgerechnete Montage-Leistung besitzt!
     */
    private Long getBaInstallType(AuftragDaten auftragDaten) {
        try {
            if (billingAuftragService.hasUnchargedServiceElementsWithExtMiscNo(auftragDaten.getAuftragNoOrig(), Leistung.EXT_MISC_NO_MONTAGE_MNET)) {
                return Reference.REF_ID_MONTAGE_MNET;
            }

            return Reference.REF_ID_MONTAGE_CUSTOMER;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }


    /*
     * Prueft, ob der Abschluss des aktuell gewaehlten CB-Vorgangs (bzw. des Auftrags)
     * ueber einen Automatismus erfolgen darf oder nicht. <br>
     * Falls alle Bedingungen zutreffen wird zum Schluss noch der User gefragt, ob die Automatisierung durchgefuehrt
     * werden soll.
     */
    private boolean isAutomationAllowed() throws FindException {
        if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
            WitaCBVorgang witaCbVorgang = (WitaCBVorgang) actCBVorgang.getCbVorgang();
            List<WitaCBVorgang> klammerAuftragList = this.getOtherKlammerAuftraege();

            boolean isPossible =
                    witaTalOrderService.isAutomationAllowed(witaCbVorgang)
                            && (witaCbVorgang.isNeuschaltung() || witaCbVorgang.isAnbieterwechsel() || witaCbVorgang.isAenderung())
                            && (witaCbVorgang.getAuftragsKlammer() == null ||
                            this.isKlammerClosed(klammerAuftragList) && this.isKlammerStandardAndPositive(klammerAuftragList));

            if (isPossible) {
                // User fragen, ob Automatisierung durchgefuehrt werden soll
                int option = MessageHelper.showYesNoQuestion(this,
                        getSwingFactory().getText("process.wita.abm.message"),
                        getSwingFactory().getText("process.wita.abm.title"));

                if (option == JOptionPane.YES_OPTION) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Prueft, ob der Abschluss des aktuell gewaehlten CB-Vorgangs (bzw. des Auftrags)
     * ueber einen Automatismus erfolgen darf und zusätlzlich ob es um eine Klammerung geht und noch nicht
     * alle Vorgänge in die Klammer geschlossen sind. <br>
     */
    private boolean isAutomationAllowedAndKlammerNotClosed(AuftragDaten auftragDaten) {
        Boolean result = Boolean.FALSE;

        if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
            WitaCBVorgang witaCbVorgang = (WitaCBVorgang) actCBVorgang.getCbVorgang();
            List<WitaCBVorgang> klammerAuftragList = this.getOtherKlammerAuftraege();

            result =
                    DateTools.isDateAfter(witaCbVorgang.getReturnRealDate(), new Date())
                            && witaCbVorgang.isStandardPositiv()
                            && (witaCbVorgang.isNeuschaltung() || witaCbVorgang.isAnbieterwechsel())
                            && this.isKlammerStandardAndPositive(klammerAuftragList)
                            && !this.isKlammerClosed(klammerAuftragList)
                            && featureService.isFeatureOnline(FeatureName.ORDER_AUTOMATION)
                            && auftragService.isAutomationPossible(auftragDaten, witaCbVorgang.getTyp());

        }
        return result;
    }

    /*
     * Ermittelt ob alle gegebene Aufträge standard und positive sind.
     */
    private Boolean isKlammerStandardAndPositive(List<WitaCBVorgang> klammerAuftragList) {
        Boolean standardAndPositive = Boolean.TRUE;
        if (klammerAuftragList.isEmpty()) {
            standardAndPositive = Boolean.FALSE;
        }
        for (WitaCBVorgang witaCBVorgang : klammerAuftragList) {
            if (!witaCBVorgang.isStandardPositiv()) {
                standardAndPositive = Boolean.FALSE;
                break;
            }
        }
        return standardAndPositive;
    }

    /*
     * Ermittelt ob alle gegebene Aufträge closed sind.
     */
    private Boolean isKlammerClosed(List<WitaCBVorgang> klammerAuftragList) {
        Boolean closed = Boolean.TRUE;
        if (klammerAuftragList.isEmpty()) {
            closed = Boolean.FALSE;
        }
        for (WitaCBVorgang witaCBVorgang : klammerAuftragList) {
            if (!witaCBVorgang.isClosed()) {
                closed = Boolean.FALSE;
                break;
            }
        }
        return closed;
    }

    /**
     * Ermittelt alle Wita CB Vorgaenge die mit dem aktuell gewaehlten CB-Vorgang in einer Klammer zusammen gehören und
     * schliesst den gegebenen Vorgang aus. Die Methode gibt WitaCBVorgänge zurück weil nur Wita Vorgänge geklammert
     * sein können.
     *
     * @return Eine List mit den Wita CB Vorgängen (aktuelle Vorgang ausgeschlossen) oder eine leere Liste falls Klammer
     * null ist oder falls es keine andere Vorgänge im Klammer gibt
     */
    private List<WitaCBVorgang> getOtherKlammerAuftraege() {
        List<WitaCBVorgang> witaCbVorgaenge = new LinkedList<>();
        if (actCBVorgang.getCbVorgang() instanceof WitaCBVorgang) {
            WitaCBVorgang witaCbVorgang = (WitaCBVorgang) actCBVorgang.getCbVorgang();
            witaCbVorgaenge = witaTalOrderService.findCBVorgaenge4Klammer(witaCbVorgang.getAuftragsKlammer(), witaCbVorgang.getAuftragId());
        }
        return witaCbVorgaenge;
    }

    /**
     * Storniert den aktuellen CBVorgang via dem Storno-Dialog.
     */
    private void storno() {
        try {
            if (actCBVorgang == null) {
                throw new HurricanGUIException("Es ist kein elektronischer Vorgang ausgewaehlt!");
            }
            BaseAenderungsKennzeichenDialog dlg = StornoDialog.forCbVorgangNiederlassung(actCBVorgang);
            Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
            if (result instanceof Collection) {
                Collection<?> cbVorgaenge = (Collection<?>) result;
                if (cbVorgaenge.size() == 1) {
                    PropertyUtils.copyProperties(actCBVorgang, Iterables.getOnlyElement(cbVorgaenge));
                    actCBVorgang.notifyObservers(true);
                    tbCBVorgang.repaint();
                }
                else {
                    loadData();
                }
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void eigeneTasks() {
        FilterRelation relation = new FilterRelation(EIGENE_TASKS_FILTER_NAME, FilterRelations.AND);
        int idx = tbMdlCBVorgang.findColumn(BEARBEITER_COLUMN_NAME);
        relation.addChild(new FilterOperator(FilterOperators.EQ, HurricanSystemRegistry.instance().getCurrentUser()
                .getLoginName(), idx));
        tbMdlCBVorgang.addFilter(relation);
        showOnlyEigeneTasks = true;
    }

    private void alleTasks() {
        tbMdlCBVorgang.removeFilter(EIGENE_TASKS_FILTER_NAME);
        showOnlyEigeneTasks = false;
    }

    /**
     * Validiert die Buttons je nach Status des CBVorgangs.
     */
    private void validateButtons() {
        if (actCBVorgang == null) {
            btnClose.setEnabled(false);
            btnStorno.setEnabled(false);
            btnWiedervorlage.setEnabled(false);
            btnHistory.setEnabled(false);
        }
        else {
            boolean isWita = actCBVorgang.getCbVorgang() instanceof WitaCBVorgang;

            btnClose.setEnabled(actCBVorgang.hasAnswer());
            btnStorno.setEnabled(isWita);
            btnHistory.setEnabled(actCBVorgang.getExmId() != null);
            btnWiedervorlage.setEnabled(true);
            btnHistory.setEnabled(true);
        }
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
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    @Override
    public void setWaitCursor() {
        tbCBVorgang.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        super.setWaitCursor();
    }

    @Override
    public void setDefaultCursor() {
        tbCBVorgang.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        super.setDefaultCursor();
    }

    /**
     * MouseListener, um einen Dialog zu oeffnen, uber den eine Status-Bemerkung zur TAL-Bestellung eingegeben werden
     * kann.
     */
    private class AddStatusBemMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (actCBVorgang != null && e.getClickCount() >= 2) {
                try {
                    TextInputDialog dlg = new TextInputDialog("Status-Bemerkung",
                            "Status-Bemerkung zur aktuellen TAL-Bestellung", "Bemerkung:");
                    dlg.showText(actCBVorgang.getStatusBemerkung());
                    Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    if (result instanceof String) {
                        taStatusBemerkung.setText((String) result);
                        actCBVorgang.getCbVorgang().setStatusBemerkung(taStatusBemerkung.getText());
                        actCBVorgang.initCBVorgangFields(actCBVorgang.getCbVorgang());

                        carrierElTalService.saveCBVorgang(actCBVorgang.getCbVorgang());
                    }
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(getMainFrame(), ex);
                }
            }
        }
    }

    /**
     * Action, um die Details einer Carrier-Bestellung ueber das Kontext-Menu der Tabelle zu oeffnen.
     */
    class ShowHistoryAction extends AKAbstractAction {

        private static final long serialVersionUID = 808167729709120648L;

        public ShowHistoryAction() {
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
     * Action, um einen Dialog mit den Automatisierungs-Fehlern zu dem aktuellen Vorgang anzuzeigen.
     */
    class ShowAutomationErrorsAction extends AKAbstractAction {

        private static final long serialVersionUID = 424163129579126642L;

        public ShowAutomationErrorsAction() {
            super();
            setName("Automatisierungs-Fehler...");
            setActionCommand("show.automation.errors");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showAutomationErrorsDialog();
        }

    }

}
