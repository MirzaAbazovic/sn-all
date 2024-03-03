/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 07:58:48
 */
package de.augustakom.hurrican.gui.auftrag;

import static de.augustakom.common.gui.utils.SwingFactoryUtils.*;
import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.iface.AKSaveableAware;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.ProfilePanel;
import de.augustakom.hurrican.gui.base.AnsprechpartnerField;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.gui.shared.CCAddressDialog;
import de.augustakom.hurrican.gui.shared.GeoIdEndstelle2LocationDialog;
import de.augustakom.hurrican.gui.shared.GeoIdSearchDialog;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Panel fuer die Darstellung der Endstellen zu einem Auftrag.
 */
public class AuftragEndstellenPanel extends AbstractAuftragPanel implements AKModelOwner,
        AKNavigationBarListener, IAuftragStatusValidator, AKSaveableAware {

    private static final long serialVersionUID = -2388953843522138478L;

    private static final Logger LOGGER = Logger.getLogger(AuftragEndstellenPanel.class);

    private static final String SEARCH_GEO_ID = "search.geo.id";
    private static final String BTN_CHANGE_LOCATION_TYPE = "btn.change.location.type";
    private static final String BTN_EDIT_LOCATION = "btn.edit.location";

    private static final String POSITION_PARAM_NAVBAR_INDEX = "navbar.index";
    private static final String POSITION_PARAM_TAB_INDEX = "tabbed.pane.index";
    private static final String COPY_ES_DATEN = "copy.es.daten";
    /* Key, um eine Endstelle der Ueberwachung hinzuzufuegen. Als Suffix wird der ES-Typ angehaengt! */
    private static final String WATCH_ENDSTELLE_X = "watch.endstelle.";
    private static final String WATCH_ES_LTG_DATEN = "watch.es.leitungsdaten";

    private final List<AKManageableComponent> managedComponents = new ArrayList<>();

    // GUI-Komponenten
    private AKJNavigationBar navBar = null;
    private AKJButton btnCopyES = null;
    private AKJButton btnSearchGeoId = null;
    private AKJButton btnEditLocation = null;
    private AKJButton btnChangeHvtType = null;
    private AKJTextField tfESTyp = null;
    private AKJTextField tfEndstelle = null;
    private AKJTextField tfName = null;
    private AKJTextField tfGeoId = null;
    private AKJTextField tfPlz = null;
    private AKJTextField tfOrt = null;
    private AKJTextField tfLaenderkennung = null;
    private AnsprechpartnerField tfAnsprech = null;
    private AKJComboBox cbSchnittstelle = null;
    private AKJComboBox cbLeitungsart = null;
    private AKJComboBox cbAnschlussart = null;
    private AKJTextField tfBandbreite = null;
    private AKJTextField tfSwitch = null;
    private AKJTextField tfHvt = null;
    private AKJComboBox cbHvtStd = null;
    private AKJTextField tfHvtType = null;
    private AKJTextField tfKVZNummer = null;
    private AKJTextArea taBemerkung = null;
    private AKJTabbedPane tpDetails = null;
    private EG2AuftragPanel eg2AuftragPnl = null;
    private RangierungPanel rangierungPnl = null;
    private CarrierbestellungPanel carrierbestPnl = null;
    private InhousePanel inhousePnl = null;
    private DSLAMProfilePanel profilePanel = null;
    private ProfilePanel alternativeProfilePanel = null;
    private MetroEthernetPanel metroEthernetPanel = null;
    private EndstelleConnectPanel endstelleConnectPnl = null;

    private AKJLabel sperreBemerkung;

    // Modelle
    private CCAuftragModel auftragModel = null;
    private List<Endstelle> endstellen = null;
    private EndstelleLtgDaten ltgDaten = null;
    private AuftragTechnik auftragTechnik = null;
    private AuftragDaten auftragDaten = null;
    private Auftrag auftrag = null;
    private Produkt produkt = null;

    // Sonstiges
    private boolean guiCreated = false;
    private boolean defaultDataLoaded = false;
    private Endstelle actualNavObject = null;
    private boolean inLoad = false;
    private boolean saveable = true;
    private String esTypTooltip = null;
    private int navBarIndexToShow = 0;

    // Services
    private AvailabilityService availabilityService;
    private HVTService hvtService;
    private ReferenceService referenceService;
    private EndstellenService endstellenService;
    private ProduktService produktService;
    private PhysikService physikService;
    private AnsprechpartnerService ansprechpartnerService;
    private CCAuftragService auftragService;
    private CCLeistungsService leistungsService;
    private NiederlassungService niederlassungService;
    private CCKundenService kundenService;
    private ProfileService profileService;
    private HWService hwService;
    private RangierungsService rangierungsService;

    public AuftragEndstellenPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragEndstellenPanel.xml");
        init();
        createGUI();
    }

    private void init() {
        try {
            availabilityService = getCCService(AvailabilityService.class);
            hvtService = getCCService(HVTService.class);
            referenceService = getCCService(ReferenceService.class);
            endstellenService = getCCService(EndstellenService.class);
            produktService = getCCService(ProduktService.class);
            physikService = getCCService(PhysikService.class);
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            auftragService = getCCService(CCAuftragService.class);
            leistungsService = getCCService(CCLeistungsService.class);
            niederlassungService = getCCService(NiederlassungService.class);
            kundenService = getCCService(CCKundenService.class);
            profileService = getCCService(ProfileService.class);
            hwService = getCCService(HWService.class);
            rangierungsService = getCCService(RangierungsService.class);
        }
        catch (ServiceNotFoundException e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        navBar = new AKJNavigationBar(this, false, false);
        btnCopyES = getSwingFactory().createButton(COPY_ES_DATEN, getActionListener(), null);
        btnEditLocation = getSwingFactory().createButton(BTN_EDIT_LOCATION, getActionListener());
        btnChangeHvtType = getSwingFactory().createButton(BTN_CHANGE_LOCATION_TYPE, getActionListener());
        btnSearchGeoId = getSwingFactory().createButton(SEARCH_GEO_ID, getActionListener(),
                BorderFactory.createEtchedBorder());

        AKJLabel lblESTyp = getSwingFactory().createLabel("es.typ");
        AKJLabel lblEndstelle = getSwingFactory().createLabel("endstelle");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblStrasse = getSwingFactory().createLabel("geo.id");
        AKJLabel lblPlz = getSwingFactory().createLabel("plz");
        AKJLabel lblAnsprech = getSwingFactory().createLabel("ansprechpartner");
        AKJLabel lblSchnittstelle = getSwingFactory().createLabel("schnittstelle");
        AKJLabel lblLeitungsart = getSwingFactory().createLabel("leitungsart");
        AKJLabel lblAnschlussart = getSwingFactory().createLabel("anschlussart");
        AKJLabel lblBandbreite = getSwingFactory().createLabel("bandbreite");
        AKJLabel lblSwitch = getSwingFactory().createLabel("switch");
        AKJLabel lblHvt = getSwingFactory().createLabel("hvt");
        AKJLabel lblHvtStd = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblHvtType = getSwingFactory().createLabel("hvt.type");
        AKJLabel lblKVZNummer = getSwingFactory().createLabel("kvz.nummer");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        EditAddressDoubleClickListener editAdrListener = new EditAddressDoubleClickListener();

        Dimension cbDim = new Dimension(120, 22);
        tfESTyp = getSwingFactory().createTextField("es.typ", false);
        tfESTyp.setFontStyle(Font.BOLD);
        GuiTools.addAction2ComponentPopupMenu(tfESTyp, new CopyEsIdAction(), true);
        esTypTooltip = tfESTyp.getToolTipText();
        tfEndstelle = getSwingFactory().createTextField("endstelle", false);
        tfEndstelle.addMouseListener(editAdrListener);
        tfName = getSwingFactory().createTextField("name", false);
        tfName.addMouseListener(editAdrListener);
        tfGeoId = getSwingFactory().createTextField("geo.id", false);
        tfGeoId.addMouseListener(editAdrListener);
        GuiTools.addAction2ComponentPopupMenu(tfGeoId, new ShowGeoIdInfoAction(), true);
        tfPlz = getSwingFactory().createTextField("plz", false);
        tfPlz.addMouseListener(editAdrListener);
        tfOrt = getSwingFactory().createTextField("ort", false);
        tfOrt.addMouseListener(editAdrListener);
        tfLaenderkennung = getSwingFactory().createTextField("laenderkennung", false);
        tfLaenderkennung.addMouseListener(editAdrListener);
        tfAnsprech = new AnsprechpartnerField();
        tfAnsprech.setMaxContactLength(50);
        cbSchnittstelle = getSwingFactory().createComboBox("schnittstelle");
        cbSchnittstelle.setRenderer(new AKCustomListCellRenderer<>(Schnittstelle.class, Schnittstelle::getSchnittstelle));
        cbSchnittstelle.setPreferredSize(cbDim);
        cbLeitungsart = getSwingFactory().createComboBox("leitungsart");
        cbLeitungsart.setRenderer(new AKCustomListCellRenderer<>(Leitungsart.class, l -> formatStringTextWithThousandsSeparators(l.getName())));
        cbLeitungsart.setPreferredSize(cbDim);
        cbAnschlussart = getSwingFactory().createComboBox("anschlussart");
        cbAnschlussart.setRenderer(new AKCustomListCellRenderer<>(Anschlussart.class, Anschlussart::getAnschlussart));
        cbAnschlussart.setPreferredSize(cbDim);
        tfBandbreite = getSwingFactory().createTextField("bandbreite", false);
        tfSwitch = getSwingFactory().createTextField("switch", false);
        tfHvt = getSwingFactory().createTextField("hvt", false);
        cbHvtStd = getSwingFactory().createComboBox("hvt.standort",
                new AKCustomListCellRenderer<>(HVTStandort.class, standort -> Objects.toString(standort.getAsb(), "")));
        cbHvtStd.setPreferredSize(cbDim);
        tfHvtType = getSwingFactory().createTextField("hvt.type", false);
        tfKVZNummer = getSwingFactory().createTextField("kvz.nummer", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        // bemerkung und ansprechpartner muessen gleiche width haben!!!
        spBemerkung.setPreferredSize(new Dimension(300, 95));
        tfAnsprech.setPreferredSize(new Dimension(300, 22));

        ForceAddressSyncAction forceAddressSyncAction = new ForceAddressSyncAction();
        forceAddressSyncAction.setParentClass(this.getClass());
        GuiTools.addAction2ComponentPopupMenu(tfEndstelle, forceAddressSyncAction, new AdministrationMouseListener(),
                true);

        // @formatter:off
        Insets smallIns = new Insets(0, 0, 0, 0);
        AKJPanel typPnl = new AKJPanel(new GridBagLayout());
        typPnl.add(tfESTyp          , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2)));
        typPnl.add(navBar           , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, smallIns));
        typPnl.add(new AKJPanel()   , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, smallIns));
        typPnl.add(btnCopyES        , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.NONE, smallIns));

        AKJPanel strPnl = new AKJPanel(new GridBagLayout());
        strPnl.add(tfGeoId          , GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2)));
        strPnl.add(btnSearchGeoId   , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, new Insets(0, 0, 0, 2)));

        // Panels fuer die Basisdaten
        AKJPanel bLeft = new AKJPanel(new GridBagLayout());
        bLeft.add(lblESTyp          , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(new AKJPanel()    , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        bLeft.add(typPnl            , GBCFactory.createGBC(100, 0, 2, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(lblStrasse        , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(strPnl            , GBCFactory.createGBC(100, 0, 2, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(lblEndstelle      , GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfEndstelle       , GBCFactory.createGBC(100, 0, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(lblName           , GBCFactory.createGBC(  0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfName            , GBCFactory.createGBC(100, 0, 2, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(lblPlz            , GBCFactory.createGBC(  0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfLaenderkennung  , GBCFactory.createGBC( 20, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfPlz             , GBCFactory.createGBC( 20, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfOrt             , GBCFactory.createGBC(100, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel bRight = new AKJPanel(new GridBagLayout());
        bRight.add(lblBemerkung     , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(spBemerkung      , GBCFactory.createGBC(100, 0, 1, 0, 1, 2, GridBagConstraints.HORIZONTAL));
        bRight.add(lblAnsprech      , GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfAnsprech       , GBCFactory.createGBC(100, 0, 1, 2, 1, 2, GridBagConstraints.HORIZONTAL));

        AKJPanel basePnl = new AKJPanel(new GridBagLayout());
        basePnl.setBorder(BorderFactory.createTitledBorder("Basisdaten"));
        basePnl.add(bLeft           , GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        basePnl.add(new AKJPanel()  , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        basePnl.add(bRight          , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel techLocBtnPnl = new AKJPanel(new GridBagLayout());
        techLocBtnPnl.add(btnEditLocation   , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        techLocBtnPnl.add(new AKJPanel()    , GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        techLocBtnPnl.add(btnChangeHvtType  , GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

        AKJPanel ansPnl = new AKJPanel(new GridBagLayout());
        ansPnl.setBorder(BorderFactory.createTitledBorder("Angeschlossen über"));
        ansPnl.add(lblHvtStd        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        ansPnl.add(cbHvtStd         , GBCFactory.createGBC(100,  0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 4, 0, 1, 1, GridBagConstraints.NONE));
        ansPnl.add(lblHvt           , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(tfHvt            , GBCFactory.createGBC(100,  0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(lblHvtType       , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(tfHvtType        , GBCFactory.createGBC(100,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(lblKVZNummer     , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(tfKVZNummer      , GBCFactory.createGBC(100,  0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        ansPnl.add(techLocBtnPnl    , GBCFactory.createGBC(100,  0, 0, 4, 3, 1, GridBagConstraints.HORIZONTAL));

        // Panel fuer die technischen Daten der Endstelle
        AKJPanel tecPnl = new AKJPanel(new GridBagLayout());
        tecPnl.setBorder(BorderFactory.createTitledBorder("Leitungsdaten"));
        tecPnl.add(lblAnschlussart  , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        tecPnl.add(cbAnschlussart   , GBCFactory.createGBC(10, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        tecPnl.add(lblSchnittstelle , GBCFactory.createGBC(  0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        tecPnl.add(cbSchnittstelle  , GBCFactory.createGBC(10, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        tecPnl.add(lblLeitungsart   , GBCFactory.createGBC(  0, 0, 8, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        tecPnl.add(cbLeitungsart    , GBCFactory.createGBC(10, 0,10, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        tecPnl.add(lblBandbreite    , GBCFactory.createGBC(  0, 0,12, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        tecPnl.add(tfBandbreite     , GBCFactory.createGBC(20, 0,14, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        tecPnl.add(lblSwitch        , GBCFactory.createGBC(  0, 0,16, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        tecPnl.add(tfSwitch         , GBCFactory.createGBC(10, 0,18, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));

        AKJPanel sperrePanel = new AKJPanel();
        sperreBemerkung = getSwingFactory().createLabel("sperreBemerkung");
        sperreBemerkung.setFontStyle(Font.BOLD);
        sperreBemerkung.setForeground(Color.RED);
        sperrePanel.add(sperreBemerkung);

        // Panels fuer die TabbedPane
        rangierungPnl = new RangierungPanel(this);
        carrierbestPnl = new CarrierbestellungPanel();
        eg2AuftragPnl = new EG2AuftragPanel();
        profilePanel = new DSLAMProfilePanel();
        inhousePnl = new InhousePanel();
        metroEthernetPanel = new MetroEthernetPanel();
        endstelleConnectPnl = new EndstelleConnectPanel();

        tpDetails = new AKJTabbedPane();
        tpDetails.add("Rangierung", rangierungPnl);
        tpDetails.add("Carrierbestellung", carrierbestPnl);
        tpDetails.add("Endgeräte", eg2AuftragPnl);
        tpDetails.add("Inhouse", inhousePnl);
        tpDetails.add("Metro Ethernet", metroEthernetPanel);
        tpDetails.add("Kundenübergabe", endstelleConnectPnl);

        AKJPanel upperPanel = new AKJPanel(new GridBagLayout());
        upperPanel.add(basePnl          , GBCFactory.createGBC(  0,  0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        upperPanel.add(ansPnl           , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.BOTH));
        upperPanel.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 3, 0, 1, 1, GridBagConstraints.NONE));
        upperPanel.add(tecPnl           , GBCFactory.createGBC(  0,  0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        upperPanel.add(sperrePanel      , GBCFactory.createGBC(100,100, 0, 2, 1, 1, GridBagConstraints.NONE));

        this.setLayout(new BorderLayout());
        this.add(upperPanel, BorderLayout.NORTH);
        this.add(tpDetails, BorderLayout.CENTER);

        // @formatter:on

        guiCreated = true;
        managedComponents.add(btnCopyES);
        managedComponents.add(btnSearchGeoId);
        managedComponents.add(btnEditLocation);
        managedComponents.add(btnChangeHvtType);
        managedComponents.add(forceAddressSyncAction);
        manageGUI(managedComponents.toArray(new AKManageableComponent[managedComponents.size()]));
    }

    private void checkAgsn() {
        if (NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG)
                && NumberTools.isGreaterOrEqual(produkt.getMinDnCount(), 1)) {
            try {
                auftragService.checkAgsn4Auftrag(auftrag.getAuftragId());
            }
            catch (FindException e) {
                MessageHelper.showMessageDialog(this, e.getMessage(), "Prüfung AGS_N Schlüssel",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void readModel() {
        // Switch all Gui items off while there is no data loaded.
        enableGuiElements(false);
        loadDefaultData();
        cleanModels();
        inLoad = true;
        setWaitCursor();
        navBarIndexToShow = 0;
        navBar.setData(null);

        SwingWorker<WorkerReturn, Void> worker = new SwingWorker<WorkerReturn, Void>() {

            // the following attributes are final to ensure thread-safety
            final Long auftragId = auftragModel.getAuftragId();
            // Abfragen, ob Save-Button enabled ist
            final boolean canSave = getAuftragDataFrame().isSaveEnabled();

            @Override
            protected WorkerReturn doInBackground() throws Exception {
                WorkerReturn wReturn = new WorkerReturn();
                wReturn.setNiederlassung = false;
                wReturn.auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
                wReturn.auftrag = auftragService.findAuftragById(auftragId);
                wReturn.auftragTechnik = auftragService.findAuftragTechnikByAuftragId(auftragId);

                // Produkt zum Auftrag laden um Nav-Bar zu validieren.
                if (wReturn.auftragDaten != null) {
                    wReturn.produkt = produktService.findProdukt(wReturn.auftragDaten.getProdId());
                    if (wReturn.produkt != null) {
                        // Pruefe Aenderungen an der AP-Adresse in Taifun
                        if (canSave && !BooleanTools.nullToFalse(wReturn.produkt.getCreateAPAddress())
                                && (wReturn.auftragDaten.getStatusId() < AuftragStatus.TECHNISCHE_REALISIERUNG)
                                && endstellenService.hasAPAddressChanged(wReturn.auftragDaten)) {
                            endstellenService.copyAPAddress(wReturn.auftragDaten, HurricanSystemRegistry.instance()
                                    .getSessionId());
                            wReturn.setNiederlassung = true;
                        }

                        // Schnittstellen fuer das Produkt laden
                        wReturn.schnittstellen = produktService.findSchnittstellen4Produkt(wReturn.produkt.getId());
                    }
                }
                // div. Leistungen des Auftrags laden
                wReturn.bandbreite = leistungsService.getBandwidthString(auftragId);

                // Endstellen zum Auftrag laden.
                wReturn.endstellen = endstellenService.findEndstellen4Auftrag(auftragId);

                return wReturn;
            }

            @Override
            protected void done() {
                try {
                    WorkerReturn wReturn = get();
                    auftragDaten = wReturn.auftragDaten;
                    auftrag = wReturn.auftrag;
                    auftragTechnik = wReturn.auftragTechnik;
                    produkt = wReturn.produkt;

                    if ((auftragDaten != null) && (produkt != null)) {
                        if (!NumberTools.equal(Produkt.ES_TYP_A_UND_B, produkt.getEndstellenTyp())) {
                            navBar.setEnabled(false);
                        }

                        if (!BooleanTools.nullToFalse(produkt.getCreateAPAddress())) {
                            // Erfassung der Endstellen sperren
                            btnSearchGeoId.setEnabled(false);
                        }

                        // Schnittstellen fuer das Produkt in die Gui schreiben
                        cbSchnittstelle.removeAllItems();
                        cbSchnittstelle.addItems(wReturn.schnittstellen, true, Schnittstelle.class);

                        if ((wReturn.schnittstellen != null) && (wReturn.schnittstellen.size() == 1)) {
                            cbSchnittstelle.setSelectedIndex(0);
                        }
                    }

                    // Endstellen zum Auftrag laden - Achtung, Callback (showNavigationObject()) ruft clear auf!
                    endstellen = wReturn.endstellen;
                    navBar.setData(endstellen);
                    for (Endstelle endstelle : endstellen) {
                        addObjectToWatch(WATCH_ENDSTELLE_X + endstelle.getEndstelleTyp(), endstelle);
                    }

                    if (navBar.getNavCount() >= navBarIndexToShow) {
                        navBar.navigateTo(navBarIndexToShow);
                    }

                    tfBandbreite.setText(formatStringTextWithThousandsSeparators(wReturn.bandbreite));

                    //set switchkenner if some is present
                    if (auftragTechnik != null && auftragTechnik.getHwSwitch() != null) {
                        tfSwitch.setText(auftragTechnik.getHwSwitch().getName());
                    }

                    Long tmpStatusId = (auftragDaten != null) ? auftragDaten.getStatusId() : null;

                    // inLoad nicht auf true setzen wenn ne Exception fliegt => Dann wird das Model nicht gespeichert.
                    inLoad = false;
                    enableGuiElements(true);
                    validate4Status(tmpStatusId);
                    carrierbestPnl.validate4Status(tmpStatusId);
                    eg2AuftragPnl.validate4Status(tmpStatusId);
                    profilePanel.validate4Status(tmpStatusId);
                    inhousePnl.validate4Status(tmpStatusId);
                    endstelleConnectPnl.validate4Status(tmpStatusId);
                    validateFields();
                    if (wReturn.setNiederlassung) {
                        setNiederlassung4Auftrag(getEndstelle(Endstelle.ENDSTELLEN_TYP_B));
                    }
                    checkAgsn();
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

    /* Switches all components to enabled */
    private void enableGuiElements(boolean enabled) {
        tfAnsprech.setEnabled(enabled);
        tfBandbreite.setEnabled(enabled);
        tfEndstelle.setEnabled(enabled);
        tfESTyp.setEnabled(enabled);
        tfHvt.setEnabled(enabled);
        tfHvtType.setEnabled(enabled);
        tfName.setEnabled(enabled);
        tfOrt.setEnabled(enabled);
        tfPlz.setEnabled(enabled);
        tfGeoId.setEnabled(enabled);
        taBemerkung.setEnabled(enabled);

        tpDetails.setVisible(enabled);

        cbAnschlussart.setEnabled(enabled);
        cbHvtStd.setEnabled(enabled);
        cbLeitungsart.setEnabled(enabled);
        cbSchnittstelle.setEnabled(enabled);

        btnCopyES.setEnabled(enabled);
        btnSearchGeoId.setEnabled(enabled);
        btnEditLocation.setEnabled(enabled);
        btnChangeHvtType.setEnabled(enabled);

        if (enabled) {
            manageGUI(managedComponents.toArray(new AKManageableComponent[managedComponents.size()]));
        }
    }

    /* 'Loescht' die Modelle. */
    private void cleanModels() {
        endstellen = null;
        ltgDaten = null;
        auftragTechnik = null;
        auftragDaten = null;
        produkt = null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        try {
            if (inLoad || !saveable) {
                return;
            }
            setWaitCursor();
            hasModelChanged(); // nur noch, um die Daten zu setzen
            if (endstellen != null) {
                metroEthernetPanel.setAuftragTechnik(auftragTechnik);
                metroEthernetPanel.saveModel();

                for (int i = 0; i < navBar.getNavCount(); i++) {
                    Endstelle toSave = (Endstelle) navBar.getNavigationObjectAt(i);
                    saveEndstelle(toSave);
                }

                boolean makeHistory = makeHistory4Auftrag(auftragModel.getAuftragId());
                saveLtgDaten(makeHistory);

                // saveModel muss auch auf den Sub-Panels (auf der TabbedPane) aufgerufen werden.
                rangierungPnl.saveModel();
                carrierbestPnl.saveModel();
                inhousePnl.saveModel();
                endstelleConnectPnl.saveModel();
            }

            if (auftragDaten != null) {
                eg2AuftragPnl.saveModel();
                profilePanel.saveModel();
            }
            showValues(actualNavObject, true);
            getMainFrame().getStatusBar().showTimedText(
                    2, "gespeichert", "de/augustakom/hurrican/gui/images/save.gif", 750);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            validateFields();
            setDefaultCursor();
        }
    }

    /* Speichert die Endstellen-Daten. */
    private void saveEndstelle(Endstelle toSave) throws StoreException {
        endstellenService.saveEndstelle(toSave);
        addObjectToWatch(WATCH_ENDSTELLE_X + toSave.getEndstelleTyp(), toSave);
    }

    /* Speichert die Leitungsdaten der Endstelle. */
    private void saveLtgDaten(boolean makeHistory) throws StoreException, ValidationException {
        if ((ltgDaten != null) && (actualNavObject != null) && hasChanged(WATCH_ES_LTG_DATEN, ltgDaten)) {
            ltgDaten.setEndstelleId(actualNavObject.getId());
            this.ltgDaten = endstellenService.saveESLtgDaten(ltgDaten, makeHistory);
            addObjectToWatch(WATCH_ES_LTG_DATEN, ltgDaten);
        }
    }

    /* Uebergibt die angezeigten Werte dem aktuellen Endstellen-Objekt */
    private void setValues(boolean changeAnschlussart) {
        if (actualNavObject != null) {
            actualNavObject.setEndstelle(tfEndstelle.getText(null));
            actualNavObject.setName(tfName.getText(null));
            actualNavObject.setPlz(tfPlz.getText(null));
            actualNavObject.setOrt(tfOrt.getText(null));

            String bemerkungNeu = appendUserAndDateIfChanged(actualNavObject.getBemerkungStawa(),
                    taBemerkung.getText(null));
            actualNavObject.setBemerkungStawa(bemerkungNeu);

            actualNavObject.setHvtIdStandort((cbHvtStd.getSelectedItem() instanceof HVTStandort)
                    ? ((HVTStandort) cbHvtStd.getSelectedItem()).getId() : null);

            if (changeAnschlussart) {
                setEndstelleAnschlussart(actualNavObject);
            }

            // Leitungsdaten setzen
            if ((ltgDaten == null)
                    && ((cbSchnittstelle.getSelectedIndex() > 0) || (cbLeitungsart.getSelectedIndex() > 0))) {
                ltgDaten = new EndstelleLtgDaten();
            }
            if (ltgDaten != null) {
                ltgDaten.setSchnittstelleId((cbSchnittstelle.getSelectedItem() instanceof Schnittstelle)
                        ? ((Schnittstelle) cbSchnittstelle.getSelectedItem()).getId() : null);
                ltgDaten.setLeitungsartId((cbLeitungsart.getSelectedItem() instanceof Leitungsart)
                        ? ((Leitungsart) cbLeitungsart.getSelectedItem()).getId() : null);
            }
        }
    }

    private void setEndstelleAnschlussart(Endstelle endstelle) {
        endstelle.setAnschlussart((cbAnschlussart.getSelectedItem() instanceof Anschlussart)
                ? ((Anschlussart) cbAnschlussart.getSelectedItem()).getId() : null);
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    protected @NotNull List<PositionParameter> getPositionParameters() {
        return Arrays.asList(
                new PositionParameter(POSITION_PARAM_NAVBAR_INDEX, navBar.getNavPosition()),
                new PositionParameter(POSITION_PARAM_TAB_INDEX, tpDetails.getSelectedIndex()));
    }


    @Override
    protected void setPositionParameters(@NotNull List<PositionParameter> positionParameters) {
        for (PositionParameter positionParameter : positionParameters) {
            if (positionParameter.name.equals(POSITION_PARAM_NAVBAR_INDEX)) {
                navBarIndexToShow = positionParameter.position;  // nicht direkt ueber navBar.navigateTo wg. SwingWorker Thread!
            }
            else if (positionParameter.name.equals(POSITION_PARAM_TAB_INDEX)) {
                tpDetails.setSelectedIndex(positionParameter.position);
            }
        }
    }


    @Override
    public void setModel(Observable model) {
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }
        else {
            this.auftragModel = null;
        }

        readModel();
    }

    /* Ueberprueft, ob die Endstellen geaendert wurden. */
    private boolean hasEndstellenChanged() {
        setValues(true);
        for (int i = 0; i < navBar.getNavCount(); i++) {
            Endstelle toCheck = (Endstelle) navBar.getNavigationObjectAt(i);
            if (hasChanged(WATCH_ENDSTELLE_X + toCheck.getEndstelleTyp(), toCheck)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasModelChanged() {
        if (guiCreated && !inLoad) {
            if (actualNavObject != null) {
                if (hasEndstellenChanged()) {
                    return true;
                }

                if (hasChanged(WATCH_ES_LTG_DATEN, ltgDaten)) {
                    return true;
                }

                // hasModelChanged muss auch auf den Sub-Panels (auf der TabbedPane) aufgerufen werden.
                if (rangierungPnl.hasModelChanged() ||
                        carrierbestPnl.hasModelChanged() ||
                        inhousePnl.hasModelChanged() ||
                        endstelleConnectPnl.hasModelChanged()) {
                    return true;
                }
            }
            // hasModelChanged muss auch auf den Sub-Panels (auf der TabbedPane) aufgerufen werden.
            if (eg2AuftragPnl.hasModelChanged() ||
                    profilePanel.hasModelChanged() ||
                    metroEthernetPanel.hasModelChanged()) {
                return true;
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        switch (command) {
            case SEARCH_GEO_ID:
                searchGeoId();
                break;
            case COPY_ES_DATEN:
                copyEsDaten();
                break;
            case BTN_EDIT_LOCATION:
                editLocation();
                break;
            case BTN_CHANGE_LOCATION_TYPE:
                changeLocationType();
                break;
            default:
                break;
        }
    }

    private void editLocation() {
        if ((actualNavObject == null) || (produkt == null)) {
            return;
        }

        try {
            if (actualNavObject.getGeoId() == null) {
                throw new HurricanGUIException(
                        "Standortzuordnung ist nicht bearbeitbar. Bitte zuerst die Geo ID bestimmen!");
            }
            if (actualNavObject.getRangierId() != null) {
                throw new HurricanGUIException(
                        "Der Endstelle ist bereits eine Rangierung zugeordnet! Die Standortzuordnung darf daher nicht mehr geaendert werden!");
            }
            throwIfNoHvtZuordnung(produkt);

            GeoIdEndstelle2LocationDialog dlg = new GeoIdEndstelle2LocationDialog(actualNavObject, produkt);
            DialogHelper.showDialog(this, dlg, true, true);
            if (dlg.getValue() instanceof Boolean) {
                Boolean result = (Boolean) dlg.getValue();
                if (BooleanTools.nullToFalse(result)) {
                    actualNavObject.setAnschlussart(null); // showValues soll die Anschlussart neu setzen
                    showValues(actualNavObject, true);
                    setEndstelleAnschlussart(actualNavObject); // Anschlussart in Endstelle setzen
                    setValues(true);
                    setNiederlassung4Auftrag(actualNavObject);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Oeffnet einen Dialog, ueber den ein 'abstrakter' techn. Standort ausgewaehlt werden kann. Der gewaehlte Standort
     * wird der Endstelle zugeordnet
     */
    private void changeLocationType() {
        if ((actualNavObject == null) || (produkt == null)) {
            return;
        }

        try {
            if (actualNavObject.getRangierId() != null) {
                throw new HurricanGUIException(
                        "Der Endstelle ist bereits eine Rangierung zugeordnet! Der Standorttyp darf daher nicht mehr geaendert werden!");
            }
            throwIfNoHvtZuordnung(produkt);

            HVTQuery query = new HVTQuery();
            query.setStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_ABSTRACT);
            List<HVTGruppeStdView> abstractTechLocations = hvtService.findHVTViews(query);
            if (CollectionTools.isNotEmpty(abstractTechLocations)) {
                Object result = MessageHelper.showInputDialog(getMainFrame(), abstractTechLocations,
                        new AKCustomListCellRenderer<>(HVTGruppeStdView.class, HVTGruppeStdView::getOrtsteil),
                        "Standorttyp wählen",
                        "Bitte wählen Sie einen Standorttyp aus:",
                        "Standorttyp");
                if (result instanceof HVTGruppeStdView) {
                    HVTGruppeStdView selectedTechLocation = (HVTGruppeStdView) result;
                    tfHvt.setText(selectedTechLocation.getOrtsteil());
                    Reference techTypeReference = referenceService.findReference(selectedTechLocation
                            .getStandortTypRefId());
                    tfHvtType.setText((techTypeReference != null) ? techTypeReference.getStrValue() : null);
                    tfKVZNummer.setText(findKVZNummer(selectedTechLocation.getHvtIdStandort(), techTypeReference));
                    cbHvtStd.removeAllItems();
                    cbHvtStd.addItem(hvtService.findHVTStandort(selectedTechLocation.getHvtIdStandort()));
                    setValues(true);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void throwIfNoHvtZuordnung(Produkt produkt) throws HurricanGUIException {
        if (!produkt.getAutoHvtZuordnung()) {
            throw new HurricanGUIException(
                    "Die Standortzuordnung kann nicht geändert werden, da das Produkt keine Standortzuordnung vorsieht.");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

    @Override
    public void showNavigationObject(Object obj, int number) {
        saveModel(); // automatisches Speichern wurde von AM gefordert
        clear();
        if (obj instanceof Endstelle) {
            try {
                // Endstelle muss immer neu geladen werden, da die Endstllen z.T. auch aus
                // anderen Panels geaendert werden!
                actualNavObject = endstellenService.findEndstelle(((Endstelle) obj).getId());
                addObjectToWatch(WATCH_ENDSTELLE_X + actualNavObject.getEndstelleTyp(), actualNavObject);
                navBar.replaceNavigationObject(number, actualNavObject);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                actualNavObject = (Endstelle) obj;
            }
        }
        else {
            actualNavObject = null;
        }
        showValues(actualNavObject, true);
    }

    /* Zeigt die Daten der Endstelle <code>es</code> an. */
    private void showValues(Endstelle es, boolean loadHvtData) {
        try {
            setWaitCursor();
            if (es != null) {
                tfESTyp.setText(es.getEndstelleTyp());
                tfESTyp.setToolTipText(esTypTooltip + es.getId());
                tfEndstelle.setText(es.getEndstelle());
                tfName.setText(es.getName());
                tfPlz.setText(es.getPlz());
                tfOrt.setText(es.getOrt());
                taBemerkung.setText(es.getBemerkungStawa());

                // Endstellen-Felder sperren, falls Adress-ID vorhanden und Laenderkennung laden
                if (es.getAddressId() != null) {
                    GuiTools.disableComponents(new Component[] { tfEndstelle, tfName, tfPlz, tfOrt });

                    CCAddress address = kundenService.findCCAddress(es.getAddressId());
                    tfLaenderkennung.setText((address != null) ? StringUtils.trimToEmpty(address.getLandId()) : "");
                }

                // Geo ID laden
                GeoId geoId = null;
                if (es.getGeoId() != null) {
                    geoId = availabilityService.findGeoId(es.getGeoId());
                    tfGeoId.setText((geoId != null) ? geoId.getStreetAndHouseNum() : "");
                }
                tfGeoId.setModel(geoId);

                // Ansprechpartner laden
                Ansprechpartner.Typ ansprechpartnerTyp = (es.isEndstelleB()) ? ENDSTELLE_B : ENDSTELLE_A;
                Ansprechpartner ansprechpartner = ansprechpartnerService.findPreferredAnsprechpartner(
                        ansprechpartnerTyp, auftragModel.getAuftragId());
                tfAnsprech.setAnsprechpartner(ansprechpartner);

                // HVT laden
                loadHvt(es, loadHvtData, geoId);

                anschlussartForHvt(es);

                loadLeitungdatenEndstelle(es);
                rangierungPnl.setModel(es);
                carrierbestPnl.setModel(es);
                inhousePnl.setModel(es);
                endstelleConnectPnl.setModel(es);

                tryToValidateKvzSperre(es);
            }

            // Endstelle an die anderen Panels uebergeben
            if (this.auftragDaten != null) {
                eg2AuftragPnl.setModel(this.auftragDaten);
                profilePanel.setModel(this.auftragDaten);
                metroEthernetPanel.setAuftragTechnik(auftragTechnik);
                metroEthernetPanel.setModel(es);

                final Equipment eq = rangierungsService.findEquipment4Endstelle(es, false, false);
                if (eq != null && eq.getHwBaugruppenId() != null) {
                    HWBaugruppe hwBaugruppe;
                    try {
                        hwBaugruppe = hwService.findBaugruppe(eq.getHwBaugruppenId());
                    }
                    catch (FindException e) {
                        hwBaugruppe = new HWBaugruppe();
                    }

                    final boolean isAccessible = hwBaugruppe.getHwBaugruppenTyp() != null
                            && BooleanTools.nullToFalse(hwBaugruppe.getHwBaugruppenTyp().isProfileAssignable());

                    if (isAccessible && alternativeProfilePanel == null) {
                        alternativeProfilePanel =
                                new ProfilePanel(profileService, auftragDaten.getAuftragId(), eq, hwBaugruppe);
                    }
                }
                tpDetails.add("DSLAM-Profil", alternativeProfilePanel == null ? profilePanel : alternativeProfilePanel);
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            HurricanGUIException ex =
                    new HurricanGUIException("Beim Laden der Endstellen-Details sind Fehler aufgetreten. " +
                            "\nSiehe Details für weitere Informationen.", e);
            MessageHelper.showErrorDialog(this, ex);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void anschlussartForHvt(Endstelle es) throws FindException {
        cbAnschlussart.selectItem("getId", Anschlussart.class, es.getAnschlussart());
        if (cbAnschlussart.getSelectedIndex() == 0) {
            HVTStandort hvtStandort = (HVTStandort) cbHvtStd.getSelectedItem();
            if (hvtStandort != null) {
                Long anschlussart = hvtService.findAnschlussart4HVTStandort(hvtStandort.getId());
                if (anschlussart != null) {
                    cbAnschlussart.selectItem("getId", Anschlussart.class, anschlussart);
                }
            }
        }
    }

    // HVT laden
    private void loadHvt(Endstelle es, boolean loadHvtData, GeoId geoId) throws FindException, HurricanGUIException {
        if (loadHvtData && produkt.getAutoHvtZuordnung()) {
            if (es.getHvtIdStandort() != null) {
                loadHvtData(es.getHvtIdStandort());
            }
            else if (geoId != null) {
                loadHVT4GeoId(geoId);
            }
        }
    }

    private void tryToValidateKvzSperre(Endstelle es) throws FindException {
        try {
            hvtService.validateKvzSperre(es);
        }
        catch (ValidationException v) {
            sperreBemerkung.setText(v.getAllDefaultMessages());
        }
    }

    // Leitungsdaten fuer Endstelle laden
    private void loadLeitungdatenEndstelle(Endstelle es) throws FindException {
        ltgDaten = endstellenService.findESLtgDaten4ES(es.getId());
        addObjectToWatch(WATCH_ES_LTG_DATEN, ltgDaten);
        if (ltgDaten != null) {
            cbSchnittstelle.selectItem("getId", Schnittstelle.class, ltgDaten.getSchnittstelleId());
            if (cbSchnittstelle.getSelectedIndex() <= 0) {
                // Schnittstelle nachladen, falls ueber Produkt-Konfiguration nicht vorhanden
                try {
                    Schnittstelle s = produktService.findSchnittstelle(ltgDaten.getSchnittstelleId());
                    if (s != null) {
                        cbSchnittstelle.addItem(s);
                        cbSchnittstelle.selectItem("getId", Schnittstelle.class, ltgDaten.getSchnittstelleId());
                    }
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
            cbLeitungsart.selectItem("getId", Leitungsart.class, ltgDaten.getLeitungsartId());
        }
    }

    /* 'Loescht' alle Felder. */
    private void clear() {
        if (!guiCreated) {
            return;
        }

        GuiTools.cleanFields(this);
        tfGeoId.setModel(null);

        rangierungPnl.setModel(null);
        eg2AuftragPnl.setModel(null);
        profilePanel.setModel(null);
        metroEthernetPanel.setModel(null);
        endstelleConnectPnl.setModel(null);
    }

    /* Liest die Stammdaten fuer das Panel ein. */
    private void loadDefaultData() {
        if (!defaultDataLoaded) {
            defaultDataLoaded = true;
            try {
                // Leitungsarten laden
                List<Leitungsart> leitungsarten = produktService.findLeitungsarten();
                cbLeitungsart.addItems(leitungsarten, true, Leitungsart.class);

                // Anschlussarten laden
                List<Anschlussart> anschlussarten = physikService.findAnschlussarten();
                cbAnschlussart.addItems(anschlussarten, true, Anschlussart.class);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /* Oeffnet einen Dialog, um nach einer GeoId zu suchen und zu uebernehmen. */
    private void searchGeoId() {
        if (actualNavObject != null) {
            GeoIdSearchDialog dlg = new GeoIdSearchDialog((auftragDaten != null) ? auftragDaten.getAuftragNoOrig()
                    : null);

            try {
                DialogHelper.showDialog(this, dlg, true, true);
                if (dlg.getValue() instanceof GeoId) {

                    GeoId geoId = (GeoId) dlg.getValue();
                    // falls Ports zugeordnet, Check aufnehmen ob neue GeoID ueber den bereits
                    // zugeordneten Standort realisiert werden kann
                    if ((actualNavObject.getRangierId() != null) && (actualNavObject.getHvtIdStandort() != null)) {
                        List<GeoId2TechLocation> geoId2TechLocations =
                                availabilityService.findPossibleGeoId2TechLocations(geoId, auftragDaten.getProdId());
                        if (CollectionTools.isNotEmpty(geoId2TechLocations)) {
                            boolean techLocationIsValid = false;
                            for (GeoId2TechLocation geoId2TechLocation : geoId2TechLocations) {
                                if (NumberTools.equal(actualNavObject.getHvtIdStandort(),
                                        geoId2TechLocation.getHvtIdStandort())) {
                                    techLocationIsValid = true;
                                    break;
                                }
                            }

                            if (!techLocationIsValid) {
                                throw new HurricanGUIException(getSwingFactory().getText("geo.id.change.not.possible"));
                            }
                        }
                    }
                    actualNavObject.setGeoId(geoId.getId());

                    if ((produkt != null) && BooleanTools.nullToFalse(produkt.getCreateAPAddress())) {
                        editCCAddress(geoId);
                    }

                    loadHVT4GeoId(geoId);
                    setAnschlussart4Auftrag();

                    // Niederlassung aendern
                    setNiederlassung4Auftrag(actualNavObject);
                    saveModel();
                    getAuftragDataFrame().refresh(); // Refresh vom Frame notwendig!
                    // Achtung: 'readModel()' wird via 'setModel()' aufgerufen. Der darin gestartete Swing Worker lädt
                    // alle
                    // Entitäten neu. Sollte parallel 'saveModel()' die Daten Speichern, kann es sein, dass bspw.
                    // AuftragTechnik eine zu kleine Version hält!
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        else {
            String msg = "Es ist keine Endstelle ausgewählt, der eine Geo ID zugeordnet werden könnte.";
            MessageHelper.showInfoDialog(this, msg, null, true);
        }
    }

    /*
     * Funktion legt die Anschlussart des Auftrags fest
     */
    private void setAnschlussart4Auftrag() {
        try {
            if ((actualNavObject != null) && (actualNavObject.getHvtIdStandort() != null)) {
                HVTService hs = getCCService(HVTService.class);
                Long anschlussart = hs.findAnschlussart4HVTStandort(actualNavObject.getHvtIdStandort());
                if (anschlussart != null) {
                    cbAnschlussart.selectItem("getId", Anschlussart.class, anschlussart);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Funktion legt die zustaendige Niederlassung des Auftrags fest
     */
    private void setNiederlassung4Auftrag(Endstelle endstelle) {
        if (endstelle == null) {
            return;
        }

        try {
            Niederlassung niederlassung;
            // Nur bei Endstelle B wird Niederlassung gesetzt
            // Pruefe Produkt auf EndstellenTyp "nur B"
            if ((endstelle.getHvtIdStandort() != null)
                    && (StringUtils.equals(endstelle.getEndstelleTyp(), Endstelle.ENDSTELLEN_TYP_B)
                    && (produkt != null) && NumberTools.equal(produkt.getEndstellenTyp(), Produkt.ES_TYP_NUR_B))) {
                // Ermittle Niederlassung ueber den zugeordneten Standort
                HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(endstelle.getHvtIdStandort());
                niederlassung = niederlassungService.findNiederlassung(hvtGruppe.getNiederlassungId());

                if (niederlassung == null) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Zum angegebenen Standort konnte keine Niederlassung ermittelt werden.\n" +
                                    "Bitte nehmen Sie die Zuordnung der Niederlassung zum Auftrag manuell vor."
                    );
                }
                else if (!NumberTools.equal(auftragTechnik.getNiederlassungId(), niederlassung.getId())) {
                    if (auftragTechnik.getNiederlassungId() != null) {
                        // Info an Benutzer, dass Niederlassung geaendert wird
                        MessageHelper.showInfoDialog(getMainFrame(), "Niederlassung des Auftrags wurde geändert");
                    }

                    // Niederlassung aendern
                    auftragTechnik.setNiederlassungId(niederlassung.getId());
                    auftragService.saveAuftragTechnik(auftragTechnik, false);

                    auftragDaten.setBearbeiter(HurricanSystemRegistry.instance().getCurrentLoginName());
                    auftragService.saveAuftragDaten(auftragDaten, false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ermittelt fuer eine GeoId und das aktuelle Produkt des Auftrags den hoechst priorisierten technischen Standort
     * und ordnet diesen der Endstelle zu.
     */
    private void loadHVT4GeoId(GeoId geoId) {
        if (geoId == null) {
            return;
        }
        try {
            setValues(false);
            cbHvtStd.removeAllItems();
            tfHvt.setText("");
            tfHvtType.setText("");

            List<GeoId2TechLocation> geoId2TechLocations =
                    availabilityService.findPossibleGeoId2TechLocations(geoId, auftragDaten.getProdId());
            if (CollectionTools.isNotEmpty(geoId2TechLocations)) {
                // besten Standort ziehen
                Long bestHvtIdStandort = geoId2TechLocations.get(0).getHvtIdStandort();

                HVTGruppeStdView hvtGruppdeStdView = loadHvtData(bestHvtIdStandort);
                if (hvtGruppdeStdView != null) {
                    if ((produkt != null) && !NumberTools.equal(produkt.getEndstellenTyp(), Produkt.ES_TYP_A_UND_B)) {
                        trySetAndSaveEndstelle(hvtGruppdeStdView);
                    }
                    else {
                        tfHvt.setText("");
                    }
                }
            }

            setValues(false);
            showValues(actualNavObject, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            // Anschlussart nicht laden, da sonst das WatchObject vom falschen Zustand ausgeht
            // und die ermittelte Anschlussart später nicht mehr gespeichert werden kann.
            setValues(false);
        }
    }

    private void trySetAndSaveEndstelle(HVTGruppeStdView hvtGruppdeStdView) {
        Endstelle esA = getEndstelle(Endstelle.ENDSTELLEN_TYP_A);
        if (esA != null) {
            esA.setEndstelle("Switch über " + hvtGruppdeStdView.getOrtsteil());
            try {
                saveEndstelle(esA);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Laedt die HVT-Daten zu der angegebenen Standort ID und traegt die Daten in die GUI Komponenten ein.
     *
     * @param hvtIdStandort
     * @return View Objekt zu dem ermittelten Standort
     * @throws FindException
     * @throws HurricanGUIException
     */
    private HVTGruppeStdView loadHvtData(Long hvtIdStandort) throws FindException, HurricanGUIException {
        List<HVTGruppeStdView> views = hvtService.findHVTViews(new HVTQuery().withHvtIdStandort(hvtIdStandort));
        if (CollectionTools.isNotEmpty(views)) {
            tfHvt.setText(views.get(0).getOrtsteil());
            // HVT-Standorte fuer die Gruppe laden
            List<HVTStandort> stds = hvtService.findHVTStandorte4Gruppe(views.get(0).getHvtIdGruppe(), true);
            if (CollectionTools.isEmpty(stds)) {
                throw new HurricanGUIException(
                        "Es konnte kein aktiver technischer Standort zu der GeoID ermittelt werden!");
            }

            Reference hvtTypeReference = referenceService.findReference(views.get(0).getStandortTypRefId());
            tfHvtType.setText((hvtTypeReference != null) ? hvtTypeReference.getStrValue() : null);
            tfKVZNummer.setText(findKVZNummer(hvtIdStandort, hvtTypeReference));

            cbHvtStd.addItems(stds, true, HVTStandort.class);
            if ((cbHvtStd.getItemCount() > 1) && (!cbHvtStd.selectItem("getId", HVTStandort.class, hvtIdStandort))) {
                cbHvtStd.setSelectedIndex(1);
            }

            return views.get(0);
        }
        return null;
    }

    /* Validiert die einzelnen Felder. */
    private void validateFields() {
        if ((actualNavObject != null) && (actualNavObject.getHvtIdStandort() != null)) {
            cbHvtStd.setEnabled(false);
        }
    }

    /* Kopiert die Endstellen-Daten einer anderen Endstelle in diese Endstelle. */
    private void copyEsDaten() {
        if ((actualNavObject == null) || (actualNavObject.getGeoId() != null)) {
            MessageHelper
                    .showInfoDialog(
                            this,
                            "Endstellen-Daten können nicht ausgewählt werden, da "
                                    +
                                    "der Endstelle bereits eine Geo ID zugeordnet wurde.\nBitte füllen Sie die Endstellen-Daten selbst."
                    );
            return;
        }

        if ((auftrag == null) || (auftrag.getKundeNo() == null)) {
            MessageHelper.showInfoDialog(this, "Endstellen-Daten können nicht ausgewählt werden, da die " +
                    "Kundennummer nicht ermittelt werden konnte");
            return;
        }
        // Pruefe ob Endstelle editiert werden darf
        if ((produkt == null) || !BooleanTools.nullToFalse(produkt.getCreateAPAddress())) {
            MessageHelper.showInfoDialog(this,
                    "Endstellen-Adressen dürfen für diesen Auftrag nur in Taifun editiert werden.");
            return;
        }

        EndstellenSelectionDialog dlg = new EndstellenSelectionDialog(auftrag.getKundeNo());
        Object result = DialogHelper.showDialog(this, dlg, true, true);
        if ((result instanceof AuftragEndstelleView) && (((AuftragEndstelleView) result).getEndstelleId() != null)) {
            try {
                Endstelle endstelle = endstellenService.findEndstelle(((AuftragEndstelleView) result).getEndstelleId());
                if (endstelle != null) {
                    // Copy ueber Service-Funktion durchfuehren (inkl. Adresse)
                    endstellenService.copyEndstelle(endstelle, actualNavObject, Endstelle.COPY_LOCATION_DATA);

                    tfEndstelle.setText(actualNavObject.getEndstelle());
                    tfName.setText(actualNavObject.getName());
                    tfPlz.setText(actualNavObject.getPlz());
                    tfOrt.setText(actualNavObject.getOrt());

                    if (actualNavObject.getAddressId() != null) {
                        CCAddress address = kundenService.findCCAddress(actualNavObject.getAddressId());
                        tfLaenderkennung.setText((address != null) ? address.getLandId() : "");
                    }

                    GeoId geoId = availabilityService.findGeoId(actualNavObject.getGeoId());
                    tfGeoId.setText((geoId != null) ? geoId.getStreetAndHouseNum() : "");

                    if (geoId != null) {
                        loadHVT4GeoId(geoId);
                    }
                    else {
                        MessageHelper.showInfoDialog(getMainFrame(),
                                "Der ursprünglichen Endstelle war keine Geo ID zugeordnet.\n" +
                                        "Bitte wählen Sie noch eine Geo ID aus."
                        );
                    }
                }
                else {
                    throw new HurricanGUIException("Endstellen-Daten konnten nicht übernommen werden.");
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.UNDEFINIERT, AuftragStatus.ERFASSUNG,
                AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, AuftragStatus.ERFASSUNG_SCV, AuftragStatus.PROJEKTIERUNG,
                AuftragStatus.PROJEKTIERUNG_ERLEDIGT, AuftragStatus.BESTELLUNG_CUDA,
                AuftragStatus.AENDERUNG, AuftragStatus.ANSCHREIBEN_KUNDEN_ERFASSUNG })) {
            GuiTools.unlockComponents(new Component[] { btnCopyES, btnSearchGeoId, btnEditLocation, btnChangeHvtType,
                    taBemerkung, tfAnsprech, cbSchnittstelle, cbLeitungsart, cbAnschlussart });
        }
        else {
            IntRange range = new IntRange(AuftragStatus.TECHNISCHE_REALISIERUNG, AuftragStatus.AUFTRAG_GEKUENDIGT);
            if (range.containsInteger(auftragStatus)) {
                GuiTools.unlockComponents(new Component[] { taBemerkung, tfAnsprech });
                GuiTools.lockComponents(new Component[] { btnCopyES, btnSearchGeoId, btnEditLocation, btnChangeHvtType,
                        cbSchnittstelle, cbLeitungsart, cbAnschlussart });
            }
            else {
                // bei Storno/Absage und unbekannter Status
                GuiTools.lockComponents(new Component[] { btnCopyES, btnSearchGeoId, btnEditLocation, btnChangeHvtType,
                        cbSchnittstelle, cbLeitungsart, cbAnschlussart });
            }
        }
    }

    /* Sucht nach der Endstelle vom Typ <code>esTyp</code> */
    private Endstelle getEndstelle(String esTyp) {
        for (int i = 0; i < navBar.getNavCount(); i++) {
            Endstelle es = (Endstelle) navBar.getNavigationObjectAt(i);
            if (StringUtils.equals(esTyp, es.getEndstelleTyp())) {
                return es;
            }
        }
        return null;
    }

    /*
     * Oeffnet einen Adress-Dialog, um die Adresse der Endstelle zu editieren oder um eine neue Adresse anzulegen.
     *
     * @param geoId Geo ID mit den Vorgabe-Daten fuer die Adresse.
     */
    private void editCCAddress(GeoId geoId) {
        try {
            // Pruefe ob Endstelle editiert werden darf
            if ((produkt != null)) {
                boolean editMode = BooleanTools.nullToFalse(produkt.getCreateAPAddress());
                CCAddressDialog dlg;
                if (actualNavObject.getAddressId() != null) {
                    dlg = new CCAddressDialog(actualNavObject.getAddressId(), geoId, editMode);
                }
                else {
                    if (editMode) {
                        // Endstellen-Adresse darf ueber Hurrican erfasst werden
                        dlg = new CCAddressDialog(auftrag.getKundeNo(), geoId, CCAddress.ADDRESS_TYPE_ACCESSPOINT);
                    }
                    else {
                        throw new HurricanGUIException(
                                "Endstellen-Adresse kann nur ueber Taifun definiert werden!");
                    }
                }

                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof CCAddress) {
                    CCAddress adr = (CCAddress) result;

                    // Daten aus der Adresse in die Endstelle schreiben
                    tfEndstelle.setText(adr.getCombinedStreetData());
                    tfName.setText(adr.getCombinedNameData());
                    tfPlz.setText(adr.getPlz());
                    tfOrt.setText(adr.getCombinedOrtOrtsteil());
                    tfLaenderkennung.setText((adr.getLandId() != null) ? adr.getLandId() : "");

                    actualNavObject.setAddressId(adr.getId());
                    actualNavObject.setEndstelle(tfEndstelle.getText(null));
                    actualNavObject.setName(tfName.getText(null));
                    actualNavObject.setPlz(tfPlz.getText(null));
                    actualNavObject.setOrt(tfOrt.getText(null));

                    saveEndstelle(actualNavObject);
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    @Override
    public void setSaveable(boolean saveable) {
        this.saveable = saveable;
    }

    private String findKVZNummer(Long hvtStandortId, Reference techTypeReference) {
        try {
            if (NumberTools.equal(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ, techTypeReference.getId())) {
                Object model = tfGeoId.getModel();
                if (model instanceof GeoId) {
                    GeoId geoId = (GeoId) model;
                    GeoId2TechLocation geoId2TechLocation = availabilityService.findGeoId2TechLocation(geoId.getId(),
                            hvtStandortId);
                    if ((geoId2TechLocation != null) && (geoId2TechLocation.getKvzNumber() != null)) {
                        return geoId2TechLocation.getKvzNumber();
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return null;
    }

    private static class WorkerReturn {
        AuftragDaten auftragDaten;
        Auftrag auftrag;
        AuftragTechnik auftragTechnik;
        Produkt produkt;
        List<Schnittstelle> schnittstellen;
        String bandbreite;
        List<Endstelle> endstellen;
        boolean setNiederlassung;
    }

    /* Action, um die ID der aktuellen Endstelle in die Zwischenablage zu kopieren. */
    class CopyEsIdAction extends AKAbstractAction {
        private static final long serialVersionUID = 4570550388283347281L;

        /**
         * Default-Konstruktor.
         */
        public CopyEsIdAction() {
            super();
            setName("ID kopieren");
            setTooltip("Kopiert die ID der aktuell angezeigten Endstelle in die Zwischenablage");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (actualNavObject != null) {
                Clipboard clip = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection content = new StringSelection("" + actualNavObject.getId());
                clip.setContents(content, content);
            }
        }
    }

    /* Action, um Infos ueber die Geo ID in einem Dialog anzuzeigen. */
    class ShowGeoIdInfoAction extends AKAbstractAction {
        private static final long serialVersionUID = -1135350953847178952L;

        /**
         * Default-Konstruktor.
         */
        public ShowGeoIdInfoAction() {
            super();
            setName("Infos anzeigen");
            setTooltip("Zeigt div. Infos ueber die GeoId an.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tfGeoId.getModel() instanceof GeoId) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        ((GeoId) tfGeoId.getModel()).createInfo());
            }
        }
    }

    /**
     * MouseAdapter reagiert auf Doppelklick und oeffnet einen Adress-Dialog.
     *
     *
     */
    private class EditAddressDoubleClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if ((actualNavObject != null) && (e.getClickCount() >= 2)) {
                GeoId geoId = null;
                // Geo ID wird nur ermittelt, wenn Adresse noch nicht gesetzt
                if ((actualNavObject.getAddressId() == null) && (actualNavObject.getGeoId() != null)) {
                    try {
                        geoId = availabilityService.findGeoId(actualNavObject.getGeoId());
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }

                // Pruefung, ob Endstellen-Adresse editiert werden darf,
                // findet in Funktion editCCAddress statt.
                editCCAddress(geoId);
            }
        }
    }

    /**
     * Action, um die Standort-Adressen mit Taifun zu synchronisieren. Diese Funktion wird unabhaengig vom
     * Auftragsstatus durchgefuehrt!
     */
    class ForceAddressSyncAction extends AKAbstractAction {
        private static final long serialVersionUID = -1081601882633984969L;

        public ForceAddressSyncAction() {
            super();
            setName("Adress-Abgleich mit Taifun");
            setTooltip("Fuehrt einen Adress-Abgleich der Endstellen-Adresse mit Taifun durch.");
            setActionCommand("force.address.sync");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll der Abgleich der Standort-Adresse mit Taifun \n" +
                            "wirklich durchgeführt werden?",
                    "Abgleich ausführen?"
            );
            if (option == JOptionPane.YES_OPTION) {
                try {
                    endstellenService.copyAPAddress(auftragDaten, HurricanSystemRegistry.instance().getSessionId());
                    refreshAuftragFrame();
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(getMainFrame(), ex);
                }
            }
        }
    }

}
