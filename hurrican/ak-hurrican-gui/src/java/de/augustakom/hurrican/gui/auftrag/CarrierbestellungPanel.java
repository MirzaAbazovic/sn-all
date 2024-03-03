/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 07:49:08
 */
package de.augustakom.hurrican.gui.auftrag;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;
import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;
import org.springframework.transaction.UnexpectedRollbackException;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.changedetector.StringWrapper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.carrier.CarrierLbzDialog;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.gui.exceptions.WitaUnexpectedRollbackException;
import de.augustakom.hurrican.gui.shared.AQSDialog;
import de.augustakom.hurrican.gui.shared.CCAddressDialog;
import de.augustakom.hurrican.gui.tools.tal.CBVorgangHistoryDialog;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByCbDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.CbVorgangSelectorDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.StornoDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.TerminverschiebungDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.VorabstimmungDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.VormieterDialog;
import de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.TalQueryService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Panel fuer die Darstellung von Carrierbestellungen.
 */
public class CarrierbestellungPanel extends AbstractAuftragPanel implements AKNavigationBarListener,
        IAuftragStatusValidator {

    private static final long serialVersionUID = 5140333724646866800L;
    private static final Logger LOGGER = Logger.getLogger(CarrierbestellungPanel.class);

    /**
     * Key, um eine CB der Ueberwachung hinzuzufuegen. Als Suffix wird der Index der CB angehaengt.
     */
    private static final String WATCH_CB_X = "watch.cb.";

    /**
     * Key, um eine Leitungslaenge einer CB der Ueberwachung hinzuzufuegen. Als Suffix wird der Index der CB
     * angehaengt.
     */
    private static final String WATCH_CB_LL_X = "watch.cb.ll.";

    private AKJNavigationBar navBar;
    private AKJButton btnNew;
    private AKJButton btnDelete;
    private AKJComboBox cbCarrier;
    private AKJDateComponent dcVorgabedatum;
    private AKJDateComponent dcBestelltAm;
    private AKJDateComponent dcZurueckAm;
    private AKJDateComponent dcBereitstellung;
    private AKJTextField tfLbz;
    private AKJTextField tfVtrNr;
    private AKJComboBox cbKundeVorOrt;
    private AKJComboBox cbNegRm;
    private AKJDateComponent dcWiedervorlage;
    private AKJDateComponent dcKuendAnCarrier;
    private AKJDateComponent dcKuendBest;
    private AKJTextField tfAqs;
    private AKJTextField tfLL;
    private AKJTextField tfLLSum;
    private AKJFormattedTextField tfTalNA;
    private AKJTextField tfAIAddress;
    private AKJTextField tfMaxBitrate;
    private AKJTextField tfRealZeitfenster;
    private AKJButton btnEsaaInternHistory;

    private Endstelle endstelle;
    private AuftragDaten auftragDaten;

    private int actualNavNumber = -1;
    private Carrierbestellung actualNavObject;
    private boolean inLoad = false;

    private CarrierService carrierService;
    private CCAuftragService auftragService;
    private TalQueryService talQueryService;
    private CCKundenService kundenService;
    private AvailabilityService availabilityService;
    private WitaTalOrderService witaTalOrderService;
    private FeatureService featureService;

    public CarrierbestellungPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/CarrierbestellungPanel.xml");
        createGUI();
        initServices();
        loadDefaultData();
    }

    private void initServices() {
        try {
            carrierService = getCCService(CarrierService.class);
            auftragService = getCCService(CCAuftragService.class);
            kundenService = getCCService(CCKundenService.class);
            availabilityService = getCCService(AvailabilityService.class);
            featureService = getCCService(FeatureService.class);
            witaTalOrderService = getCCService(WitaTalOrderService.class);
            talQueryService = getCCService(TalQueryService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblCarrier = getSwingFactory().createLabel("carrier");
        AKJLabel lblVorgabeDatum = getSwingFactory().createLabel("vorgabe.datum");
        AKJLabel lblBestelltAm = getSwingFactory().createLabel("bestellt.am");
        AKJLabel lblZurueckAm = getSwingFactory().createLabel("zurueck.am");
        AKJLabel lblBereitstellung = getSwingFactory().createLabel("bereitstellung.am");
        AKJLabel lblLbz = getSwingFactory().createLabel("lbz");
        AKJLabel lblVtrNr = getSwingFactory().createLabel("vtr.nr");
        AKJLabel lblKundeVorOrt = getSwingFactory().createLabel("kunde.vor.ort");
        AKJLabel lblNegRm = getSwingFactory().createLabel("negative.rm");
        AKJLabel lblWiedervorlage = getSwingFactory().createLabel("wiedervorlage");
        AKJLabel lblKuendAnCarrier = getSwingFactory().createLabel("kuendigung");
        AKJLabel lblKuendBest = getSwingFactory().createLabel("kuendigung.best");
        AKJLabel lblAqs = getSwingFactory().createLabel("aqs");
        AKJLabel lblLL = getSwingFactory().createLabel("ll");
        AKJLabel lblLLSum = getSwingFactory().createLabel("ll.sum");
        AKJLabel lblTalNA = getSwingFactory().createLabel("tal.na");
        AKJLabel lblAIAddress = getSwingFactory().createLabel("ai.address");
        AKJLabel lblMaxBitrate = getSwingFactory().createLabel("max.bitrate");
        AKJLabel lblRealZeitfenster = getSwingFactory().createLabel("tal.realisierungs.zeitfenster");

        cbCarrier = getSwingFactory().createComboBox("carrier", false);
        cbCarrier.setRenderer(new AKCustomListCellRenderer<>(Carrier.class, Carrier::getName));
        dcVorgabedatum = getSwingFactory().createDateComponent("vorgabe.datum", false);
        dcBestelltAm = getSwingFactory().createDateComponent("bestellt.am", false);
        dcZurueckAm = getSwingFactory().createDateComponent("zurueck.am", false);
        dcBereitstellung = getSwingFactory().createDateComponent("bereitstellung.am", false);
        tfLbz = getSwingFactory().createTextField("lbz", false);
        tfVtrNr = getSwingFactory().createTextField("vtr.nr", false);
        cbKundeVorOrt = getSwingFactory().createComboBox("kunde.vor.ort");
        cbNegRm = getSwingFactory().createComboBox("negative.rm", false);
        cbNegRm.setRenderer(new AKCustomListCellRenderer<>(String.class, String::toString));
        dcWiedervorlage = getSwingFactory().createDateComponent("wiedervorlage", false);
        dcKuendAnCarrier = getSwingFactory().createDateComponent("kuendigung", false);
        dcKuendBest = getSwingFactory().createDateComponent("kuendigung.best", false);
        tfAqs = getSwingFactory().createTextField("aqs", false);
        tfLL = getSwingFactory().createTextField("ll", false);
        tfLLSum = getSwingFactory().createTextField("ll.sum", false);
        tfTalNA = getSwingFactory().createFormattedTextField("tal.na", false);
        tfAIAddress = getSwingFactory().createTextField("ai.address", false);
        tfMaxBitrate = getSwingFactory().createTextField("max.bitrate", false);
        tfRealZeitfenster = getSwingFactory().createTextField("tal.realisierungs.zeitfenster", false);

        btnNew = getSwingFactory().createButton("new", getActionListener(), null);
        btnDelete = getSwingFactory().createButton("delete", getActionListener(), null);
        btnDelete.setEnabled(false);
        AKJButton btnAqs = getSwingFactory().createButton("aqs.anzeigen", getActionListener());
        AKJButton btnKuendigung = getSwingFactory().createButton("cb.kuendigen", getActionListener());
        AKJButton btnCreateLbz = getSwingFactory().createButton("create.lbz", getActionListener());
        btnCreateLbz.setPreferredSize(new Dimension(20, 20));
        AKJButton btnTerminverschiebung = getSwingFactory().createButton("cb.vorgang.terminverschiebung", getActionListener());
        AKJButton btnStorno = getSwingFactory().createButton("cb.vorgang.storno", getActionListener());
        AKJButton btnCBVHistory = getSwingFactory().createButton("cb.vorgang.history", getActionListener());
        btnEsaaInternHistory = getSwingFactory().createButton("esaa.intern.history", getActionListener());
        AKJButton btnCreateCBV = getSwingFactory().createButton("create.cbv", getActionListener());

        AKJButton btnAIAddress = getSwingFactory().createButton("change.ai.address", getActionListener());
        AKJButton btnVorabstimmung = getSwingFactory().createButton("cb.vorabstimmung", getActionListener());
        AKJButton btnVormieter = getSwingFactory().createButton("cb.vormieter", getActionListener());
        navBar = new AKJNavigationBar(null, true, false);
        navBar.addNavigationListener(this); // Listener erst hier hinzufuegen - sonst NPE in setValues()

        AKJPanel left = new AKJPanel(new GridBagLayout());
        // @formatter:off
        left.add(btnNew             , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(btnDelete          , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        left.add(navBar             , GBCFactory.createGBC(100,  0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCarrier         , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(cbCarrier          , GBCFactory.createGBC(100,  0, 2, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVorgabeDatum    , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcVorgabedatum     , GBCFactory.createGBC(100,  0, 2, 2, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBestelltAm      , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBestelltAm       , GBCFactory.createGBC(100,  0, 2, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblZurueckAm       , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcZurueckAm        , GBCFactory.createGBC(100,  0, 2, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBereitstellung  , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBereitstellung   , GBCFactory.createGBC(100,  0, 2, 5, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLbz             , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfLbz              , GBCFactory.createGBC(100,  0, 2, 6, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnCreateLbz       , GBCFactory.createGBC(  0,  0, 5, 6, 1, 1, GridBagConstraints.NONE, new Insets(2,0,2,2)));
        left.add(lblVtrNr           , GBCFactory.createGBC(  0,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfVtrNr            , GBCFactory.createGBC(100,  0, 2, 7, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblKundeVorOrt     , GBCFactory.createGBC(  0,  0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbKundeVorOrt      , GBCFactory.createGBC(100,  0, 2, 8, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblNegRm           , GBCFactory.createGBC(  0,  0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 1, 9, 1, 1, GridBagConstraints.NONE));
        left.add(cbNegRm            , GBCFactory.createGBC(100,  0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        AKJPanel right = new AKJPanel(new GridBagLayout());
        // @formatter:off
        right.add(lblWiedervorlage  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcWiedervorlage   , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKuendAnCarrier , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcKuendAnCarrier  , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKuendBest      , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcKuendBest       , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblAqs            , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfAqs             , GBCFactory.createGBC(100,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(btnAqs            , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblLL             , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfLL              , GBCFactory.createGBC(100,  0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblLLSum          , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfLLSum           , GBCFactory.createGBC(100,  0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblTalNA          , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfTalNA           , GBCFactory.createGBC(100,  0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblAIAddress      , GBCFactory.createGBC(  0,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfAIAddress       , GBCFactory.createGBC(  0,  0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(btnAIAddress      , GBCFactory.createGBC(100,  0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblMaxBitrate     , GBCFactory.createGBC(  0,  0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfMaxBitrate      , GBCFactory.createGBC(100,  0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblRealZeitfenster, GBCFactory.createGBC(  0,  0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfRealZeitfenster , GBCFactory.createGBC(100,  0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout(), "Funktionen");
        int actLine = 0;
        // @formatter:off
        btnPnl.add(btnCreateCBV           , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnEsaaInternHistory   , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnCBVHistory          , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()         , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnTerminverschiebung  , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnStorno              , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnKuendigung          , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()         , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnVorabstimmung       , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnVormieter           , GBCFactory.createGBC(  0,  0, 0, actLine++, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()         , GBCFactory.createGBC(  0,100, 0, actLine,   1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(btnPnl         , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(left           , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 0, 1, 2, GridBagConstraints.NONE));
        this.add(right          , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel() , GBCFactory.createGBC(100,100, 4, 0, 1, 2, GridBagConstraints.NONE));
        // @formatter:on

        manageGUI(btnNew, btnDelete, btnAqs, btnKuendigung, btnTerminverschiebung,
                btnStorno, btnCreateCBV, btnCBVHistory, btnEsaaInternHistory, btnAIAddress);
        enableFields(false);

        btnKuendigung.setVisible(false);

        // wird nur im Wita-Modus sichtbar falls mindestens ein alter CB-Vorgang (ESAA oder intern) existiert
        btnEsaaInternHistory.setVisible(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        if (model instanceof Endstelle) {
            this.endstelle = (Endstelle) model;
        }
        else {
            this.endstelle = null;
        }
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        try {
            inLoad = true;
            setWaitCursor();
            clear();
            navBar.setData(null);

            if (endstelle != null) {
                List<Carrierbestellung> cbs = carrierService.findCBs4Endstelle(endstelle.getId());
                auftragDaten = auftragService.findAuftragDatenByEndstelle(endstelle.getId());

                if (CollectionTools.isNotEmpty(cbs)) {
                    enableFields(true);
                    navBar.setData(cbs);
                    for (int i = 0; i < cbs.size(); i++) {
                        addCBToWatch(i, cbs.get(i));
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            inLoad = false;
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            setWaitCursor();
            if ((endstelle != null) && hasModelChanged()) {
                for (int i = 0; i < navBar.getNavCount(); i++) {
                    Carrierbestellung cb = (Carrierbestellung) navBar.getNavigationObjectAt(i);
                    if (hasChanged(WATCH_CB_X + i, cb)) {
                        carrierService.saveCB(cb, endstelle);
                        if (hasChanged(WATCH_CB_LL_X + i, new StringWrapper(cb.getLl()))) {
                            carrierService.saveCBDistance2GeoId2TechLocations(cb, HurricanSystemRegistry.instance()
                                    .getSessionId());
                        }
                        addCBToWatch(i, cb);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AKGUIException(e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void addCBToWatch(int i, Carrierbestellung cb) {
        addObjectToWatch(WATCH_CB_X + i, cb);
        addObjectToWatch(WATCH_CB_LL_X + i, new StringWrapper(cb.getLl()));
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
        if (!inLoad) {
            setValues();
            for (int i = 0; i < navBar.getNavCount(); i++) {
                if (hasChanged(WATCH_CB_X + i, navBar.getNavigationObjectAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void execute(String command) {
        if ("new".equals(command)) {
            createNewCB();
        }
        else if ("delete".equals(command)) {
            deleteCB();
        }
        else if ("aqs.anzeigen".equals(command)) {
            showAQS();
        }
        else if ("cb.kuendigen".equals(command)) {
            cbKuendigen();
        }
        else if ("create.lbz".equals(command)) {
            createLbz();
        }
        else if ("create.cbv".equals(command)) {
            createCBV();
        }
        else if ("esaa.intern.history".equals(command)) {
            showEsaaInternHistoryDialog();
        }
        else if ("cb.vorgang.history".equals(command)) {
            showCBVorgangHistory();
        }
        else if ("change.ai.address".equals(command)) {
            changeAIAddress();
        }
        else if ("cb.vorgang.terminverschiebung".equals(command)) {
            terminverschiebung();
        }
        else if ("cb.vorgang.storno".equals(command)) {
            storno();
        }
        else if ("cb.vorabstimmung".equals(command)) {
            vorabstimmung();
        }
        else if ("cb.vormieter".equals(command)) {
            vormieter();
        }
    }

    /**
     * Legt eine neue Carrierbestellung an.
     */
    private void createNewCB() {
        try {
            if (navBar.getNavCount() > 0) {
                Carrierbestellung cb2Check = (Carrierbestellung) navBar.getNavigationObjectAt(0);
                if (!StringUtils.equals(cb2Check.getNegativeRm(), Carrierbestellung.NEGATIVE_RM_NEUBESTELLUNG)
                        && (cb2Check.getKuendigungAnCarrier() == null)) {
                    throw new HurricanGUIException(getSwingFactory().getText("create.cb.not.allowed"));
                }
            }

            Carrierbestellung cb = new Carrierbestellung();
            addCBToWatch(actualNavNumber + 1, cb);

            cb.setBestelltAm(new Date());
            if (DateTools.isDateAfter(auftragDaten.getVorgabeSCV(), new Date())) {
                // Vorgabedatum mit Vorgabe-AM fuellen, falls dieses in der Zukunft liegt
                cb.setVorgabedatum(auftragDaten.getVorgabeSCV());
            }

            saveCB(cb);

            navBar.addNavigationObject(cb);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void saveCB(Carrierbestellung cb) {
        try {
            if (endstelle != null) {
                Carrier carrier = carrierService.findCarrier4HVT(endstelle.getHvtIdStandort());
                cb.setCarrier((carrier != null) ? carrier.getId() : null);
                carrierService.saveCB(cb, endstelle);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Loescht die aktuell angezeigte Carrierbestellung.
     */
    private void deleteCB() {
        if ((actualNavObject == null) || (actualNavNumber < 0)) {
            MessageHelper.showInfoDialog(this, "Es ist keine Carriebestellung ausgewählt!", null, true);
            return;
        }

        try {
            if (actualNavObject.getId() != null) {
                carrierService.deleteCB(actualNavObject);
            }
            removeObjectFromWatch(WATCH_CB_X + actualNavNumber); // Reihenfolge einhalten!
            navBar.removeNavigationObject(actualNavNumber);

        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    @Override
    public void showNavigationObject(Object obj, int number) {
        setValues();
        if (obj instanceof Carrierbestellung) {
            actualNavNumber = number;
            actualNavObject = (Carrierbestellung) obj;
            showValues(actualNavObject);
            enableFields(true);
            manageGUI(btnDelete);
        }
        else {
            actualNavNumber = -1;
            actualNavObject = null;
            btnDelete.setEnabled(false);
            cleanFields();
            enableFields(false);
        }
    }

    /**
     * Uebergibt die angezeigten Werte dem aktuellen Carrierbestellung-Objekt
     */
    private void setValues() {
        if (actualNavObject != null) {
            actualNavObject.setCarrier((cbCarrier.getSelectedItem() instanceof Carrier) ? ((Carrier) cbCarrier
                    .getSelectedItem()).getId() : null);
            actualNavObject.setVorgabedatum(dcVorgabedatum.getDate(null));
            actualNavObject.setBestelltAm(dcBestelltAm.getDate(null));
            actualNavObject.setZurueckAm(dcZurueckAm.getDate(null));
            actualNavObject.setBereitstellungAm(dcBereitstellung.getDate(null));
            actualNavObject.setLbz(tfLbz.getText(null));
            actualNavObject.setVtrNr(tfVtrNr.getText(null));
            actualNavObject.setKundeVorOrt((Boolean) cbKundeVorOrt.getSelectedItemValue());
            String negRm = (cbNegRm.getSelectedItem() instanceof String) ? (String) cbNegRm.getSelectedItem() : null;
            actualNavObject.setNegativeRm((StringUtils.isBlank(negRm) ? null : negRm));
            actualNavObject.setWiedervorlage(dcWiedervorlage.getDate(null));
            actualNavObject.setKuendigungAnCarrier(dcKuendAnCarrier.getDate(null));
            actualNavObject.setKuendBestaetigungCarrier(dcKuendBest.getDate(null));
            actualNavObject.setAqs(tfAqs.getText(null));
            actualNavObject.setLl(tfLL.getText(null));
            actualNavObject.setMaxBruttoBitrate(tfMaxBitrate.getText(null));

            if (tfAIAddress.getModel() instanceof CCAddress) {
                actualNavObject.setAiAddressId(((CCAddress) tfAIAddress.getModel()).getId());
            }
        }
    }

    /**
     * Zeigt die Daten der Carrierbestellung an.
     */
    private void showValues(Carrierbestellung cb) {
        try {
            cbCarrier.selectItem("getId", Carrier.class, cb.getCarrier());
            dcVorgabedatum.setDate(cb.getVorgabedatum());
            dcBestelltAm.setDate(cb.getBestelltAm());
            dcZurueckAm.setDate(cb.getZurueckAm());
            dcBereitstellung.setDate(cb.getBereitstellungAm());
            tfLbz.setText(cb.getLbz());
            tfVtrNr.setText(cb.getVtrNr());
            cbKundeVorOrt.selectItemWithValue(cb.getKundeVorOrt());
            cbNegRm.selectItem("toString", String.class, (cb.getNegativeRm() != null) ? cb.getNegativeRm() : " ");
            dcWiedervorlage.setDate(cb.getWiedervorlage());
            dcKuendAnCarrier.setDate(cb.getKuendigungAnCarrier());
            dcKuendBest.setDate(cb.getKuendBestaetigungCarrier());
            tfAqs.setText(cb.getAqs());
            tfLL.setText(cb.getLl());
            tfLLSum.setText(cb.calcLlSum());
            tfTalNA.setValue(cb.getAuftragId4TalNA());
            tfMaxBitrate.setText(cb.getMaxBruttoBitrate());
            tfRealZeitfenster.setText((cb.getTalRealisierungsZeitfenster() != null)
                    ? cb.getTalRealisierungsZeitfenster().getCarrierRealisierung() : null);

            btnEsaaInternHistory.setVisible(false);
            // ESAA/Intern Button sichtbar machen, wenn im WITA-Modus und mindestens ein alter Vorgang (ESAA oder
            // intern)
            if ((cb.getId() != null)
                    && talQueryService.hasEsaaOrInterneCBVorgaenge(cb.getId())) {
                btnEsaaInternHistory.setVisible(true);
            }

            if (cb.getAiAddressId() != null) {

                CCAddress aiAddress = kundenService.findCCAddress(cb.getAiAddressId());
                if (aiAddress != null) {
                    tfAIAddress.setText(aiAddress.getShortAddress());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * 'Loescht' alle Felder
     */
    private void clear() {
        navBar.setData(null);
        auftragDaten = null;
        tfAIAddress.setModel(null);
        GuiTools.cleanFields(this);
    }

    /**
     * Liest die Stammdaten fuer das Panel ein.
     */
    private void loadDefaultData() {
        try {
            // Werte fuer 'Carrier'
            List<Carrier> carriers = carrierService.findCarrier();
            cbCarrier.addItems(carriers, true, Carrier.class);

            // Werte fuer 'negativeRm'
            List<String> negRms = new ArrayList<>();
            CollectionUtils.addAll(negRms, Carrierbestellung.NEGATIVE_RMS);
            cbNegRm.addItems(negRms, true, String.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Zeigt einen Dialog mit Leitungsquerschnitten an.
     */
    private void showAQS() {
        if ((endstelle != null) && (endstelle.getGeoId() != null)) {
            GeoId geoId = new GeoId();
            geoId.setId(endstelle.getGeoId());
            AQSDialog dlg = new AQSDialog(geoId);
            DialogHelper.showDialog(this, dlg, false, true);
        }
        else {
            MessageHelper.showInfoDialog(this, "Die Aderquerschnitte können nicht angezeigt werden, da entweder "
                    + "keine Endstelle ausgewählt oder der Endstelle keine Strasse " + "zugeordnet ist.", null, true);
        }
    }

    /**
     * Setzt alle Felder auf enabled/disabled
     */
    private void enableFields(boolean enabled) {
        cbCarrier.setEnabled(enabled);
        dcVorgabedatum.setUsable(enabled);
        dcBestelltAm.setUsable(enabled);
        dcZurueckAm.setUsable(enabled);
        dcBereitstellung.setUsable(enabled);
        cbNegRm.setEnabled(enabled);
        dcWiedervorlage.setUsable(enabled);
        dcKuendAnCarrier.setUsable(enabled);
        dcKuendBest.setUsable(enabled);
        tfLbz.setEditable(enabled);
        tfVtrNr.setEditable(enabled);
        cbKundeVorOrt.setEnabled(enabled);
        tfAqs.setEditable(enabled);
        tfLL.setEditable(enabled);
        tfMaxBitrate.setEditable(enabled);
    }

    /**
     * 'Loescht' alle Felder.
     */
    private void cleanFields() {
        GuiTools.cleanFields(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    /**
     * Kuendigt die aktuell angezeigte Carrierbestellung - sofern gueltig.
     */
    private void cbKuendigen() {
        try {
            if (hasModelChanged()) {
                MessageHelper.showInfoDialog(this, "Bitte Speichern Sie die Änderungen zuerst ab!");
                return;
            }

            if ((actualNavObject != null) && (actualNavObject.getId() != null)) {
                if (StringUtils.isBlank(actualNavObject.getLbz()) || (actualNavObject.getZurueckAm() == null)) {
                    throw new HurricanGUIException("Die Carrierbestellung ist ungültig und kann deshalb "
                            + "nicht gekündigt werden!\nLBZ oder Datum-Rückmeldung ist nicht erfasst.");
                }

                if (actualNavObject.getKuendigungAnCarrier() != null) {
                    int option = MessageHelper.showConfirmDialog(this,
                            "Es ist bereits ein Kündigungsdatum erfasst. Soll die CB\n"
                                    + "trotzdem gekündigt werden (Kündigungsdatum wird überschrieben)?",
                            "CB kündigen?", JOptionPane.YES_NO_OPTION
                    );
                    if (option == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                int option = MessageHelper.showConfirmDialog(this,
                        "Soll die Carrierbestellung wirklich gekündigt werden?", "CB kündigen?",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    // CB kuendigen und Druck
                    carrierService.cbKuendigen(actualNavObject.getId());
                    JasperPrint jp = carrierService.reportCuDAKuendigung(actualNavObject.getId(), endstelle.getId(),
                            HurricanSystemRegistry.instance().getSessionId());
                    JasperPrintManager.printReport(jp, true);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den die LBZ vereinfacht eingegeben werden kann.
     */
    private void createLbz() {
        try {
            setValues();
            CarrierLbzDialog.showCarrierLbzDialogFor(tfLbz, actualNavObject.getCarrier(), endstelle.getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Erzeugt zur aktuellen Carrierbestellung einen neuen el. Vorgang.
     */
    private void createCBV() {
        try {
            // HUR-23815: create a new carrierbestellung, if there is not exists. This will prevent a
            // NullPointerException later on, when a Vormieter will be selected.
            if (actualNavObject == null) {
                createNewCB();
            }
            Long cbId = (actualNavObject != null) ? actualNavObject.getId() : null;
            Long carrierId = (actualNavObject != null) ? actualNavObject.getCarrier() : null;
            if (carrierId == null && endstelle != null) {
                Carrier carrier = carrierService.findCarrier4HVT(endstelle.getHvtIdStandort());
                carrierId = (carrier != null) ? carrier.getId() : null;
            }

            CreateElTALVorgangWizard wizard =
                    CreateElTALVorgangWizard.create(cbId, carrierId, endstelle, auftragDaten, null);
            createCBV(wizard);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void createCBV(CreateElTALVorgangWizard wizard) {
        try {
            // Carrierbestellung auf jeden Fall vorher speichern
            saveModel();

            DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), wizard, true, true);

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Zeigt die History der el. CB-Vorgaenge an.
     */
    private void showCBVorgangHistory() {
        if (actualNavObject != null) {
            AbstractServiceOptionDialog dlg;
            dlg = new HistoryByCbDialog(
                    actualNavObject.getId(), auftragDaten.getAuftragId());
            DialogHelper.showDialog(getMainFrame(), dlg, false, true);
        }
        else {
            AbstractServiceOptionDialog dlg = new HistoryByCbDialog(null, auftragDaten.getAuftragId());
            DialogHelper.showDialog(getMainFrame(), dlg, false, true);
        }
    }

    private void showEsaaInternHistoryDialog() {
        if (actualNavObject != null) {
            DialogHelper.showDialog(getMainFrame(), new CBVorgangHistoryDialog(actualNavObject.getId()), false, true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Es ist keine Carrierbestellung vorhanden!", null, true);
        }
    }

    /**
     * Oeffnet den Adress-Dialog, um die Anschlussinhaberadresse zu editieren.
     */
    private void changeAIAddress() {
        try {
            if (actualNavObject == null) {
                throw new HurricanGUIException("Es ist keine Carrierbestellung ausgewaehlt!");
            }
            CCAddressDialog dlg = null;
            if (actualNavObject.getAiAddressId() != null) {
                dlg = new CCAddressDialog(actualNavObject.getAiAddressId(), null, true);
            }
            else {
                GeoId geoId = availabilityService.findGeoId(endstelle.getGeoId());
                Auftrag auftrag = auftragService.findAuftragById(auftragDaten.getAuftragId());
                dlg = new CCAddressDialog(auftrag.getKundeNo(), geoId, CCAddress.ADDRESS_TYPE_ACCESSPOINT_OWNER);
            }

            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof CCAddress) {
                CCAddress adr = (CCAddress) result;
                tfAIAddress.setModel(adr);
                tfAIAddress.setText(adr.getShortAddress());

                actualNavObject.setAiAddressId(adr.getId());
                saveModel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void terminverschiebung() {
        try {
            List<WitaCBVorgang> cbVorgaenge = witaTalOrderService.getWitaCBVorgaengeForTVOrStorno(
                    getCbIdOfActualNavObject(),
                    auftragDaten.getAuftragId());

            List<WitaCBVorgang> rexMkVorgaenge = Lists.newArrayList(filter(cbVorgaenge, CB_ID_NULL_PREDICATE));
            List<WitaCBVorgang> nichtRexMkVorgaenge = Lists
                    .newArrayList(filter(cbVorgaenge, not(CB_ID_NULL_PREDICATE)));

            if (rexMkVorgaenge.isEmpty() && !nichtRexMkVorgaenge.isEmpty()) {
                showTerminverschiebungDialog(nichtRexMkVorgaenge);
            }
            else if (!rexMkVorgaenge.isEmpty() && nichtRexMkVorgaenge.isEmpty()) {
                showTerminverschiebungDialog(rexMkVorgaenge);
            }
            else if (!rexMkVorgaenge.isEmpty() && !nichtRexMkVorgaenge.isEmpty()) {
                showCbVorgangSelectionDialog(cbVorgaenge, CbVorgangSelectorDialog.CB_VORGANG_SELECTION_TV);
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Es ist kein offener elektronischer WITA Bestellungsvorgang für eine Terminverschiebung vorhanden!");
            }

            readModel();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private Long getCbIdOfActualNavObject() {
        if (actualNavObject != null) {
            return actualNavObject.getId();
        }
        return null;
    }

    private void showCbVorgangSelectionDialog(List<WitaCBVorgang> cbVorgaenge, String useCase) {
        CbVorgangSelectorDialog dlg = new CbVorgangSelectorDialog(cbVorgaenge, useCase);
        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
    }

    private void showTerminverschiebungDialog(List<WitaCBVorgang> cbVorgaenge) {
        TerminverschiebungDialog dlg = TerminverschiebungDialog.forCbPanel(auftragDaten, cbVorgaenge);
        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
    }

    /**
     * Der Storno-Button ist nur im WITA-Modus sichtbar.
     */
    private void storno() {
        try {
            List<WitaCBVorgang> cbVorgaenge = witaTalOrderService.getWitaCBVorgaengeForTVOrStorno(
                    getCbIdOfActualNavObject(),
                    auftragDaten.getAuftragId());
            List<WitaCBVorgang> rexMkVorgaenge = Lists.newArrayList(filter(cbVorgaenge, CB_ID_NULL_PREDICATE));
            List<WitaCBVorgang> nichtRexMkVorgaenge = Lists
                    .newArrayList(filter(cbVorgaenge, not(CB_ID_NULL_PREDICATE)));

            if (rexMkVorgaenge.isEmpty() && !nichtRexMkVorgaenge.isEmpty()) {
                showStornoDialog(nichtRexMkVorgaenge);
            }
            else if (!rexMkVorgaenge.isEmpty() && nichtRexMkVorgaenge.isEmpty()) {
                showStornoDialog(rexMkVorgaenge);
            }
            else if (!rexMkVorgaenge.isEmpty() && !nichtRexMkVorgaenge.isEmpty()) {
                showCbVorgangSelectionDialog(cbVorgaenge,
                        CbVorgangSelectorDialog.CB_VORGANG_SELECTION_STORNO);
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Es ist kein offener elektronischer WITA Bestellungsvorgang vorhanden, der storniert werden kann!\n"
                                + "ESAA-TAL Vorgaenge koennen nur noch per Fax storniert werden.\n"
                                + "Interne Bestellungen müssen über 'el. Vorgang...' storniert werden!"
                );
            }
            readModel();
        }
        catch (UnexpectedRollbackException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(null, new WitaUnexpectedRollbackException(e));
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void showStornoDialog(List<WitaCBVorgang> cbVorgaenge) {
        StornoDialog dlg = StornoDialog.forCbPanel(auftragDaten, cbVorgaenge);
        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
    }

    private void vorabstimmung() {
        try {
            // Keine Carrierbestellung erforderlich!
            if ((endstelle == null) || (auftragDaten == null)) {
                throw new HurricanGUIException("Fehlende Endstelle oder AuftragDaten!");
            }
            saveModel();
            VorabstimmungDialog dlg = new VorabstimmungDialog(endstelle, auftragDaten);
            DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
            readModel();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void vormieter() {
        try {
            if (actualNavObject == null) {
                throw new HurricanGUIException("Es ist keine Carrierbestellung ausgewaehlt!");
            }
            saveModel();
            VormieterDialog dlg = new VormieterDialog(actualNavObject.getId());
            DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
            readModel();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void validate4Status(Long auftragStatus) {
        if (actualNavObject != null) {
            IntRange range = new IntRange(AuftragStatus.ANSCHREIBEN_KUNDEN_ERFASSUNG,
                    AuftragStatus.ANSCHREIBEN_KUNDE_KUEND);
            if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.UNDEFINIERT, AuftragStatus.ERFASSUNG,
                    AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, AuftragStatus.ERFASSUNG_SCV, AuftragStatus.PROJEKTIERUNG,
                    AuftragStatus.PROJEKTIERUNG_ERLEDIGT })
                    || range.containsInteger(auftragStatus)) {
                GuiTools.unlockComponents(new Component[] { btnNew, cbCarrier, dcVorgabedatum, dcBestelltAm,
                        dcZurueckAm, dcBereitstellung, tfMaxBitrate, tfLbz, tfVtrNr, cbKundeVorOrt, cbNegRm,
                        dcWiedervorlage, tfAqs, tfLL });
            }
            else {
                GuiTools.lockComponents(new Component[] { btnNew, cbCarrier, dcVorgabedatum, dcBestelltAm, dcZurueckAm,
                        dcBereitstellung, tfMaxBitrate, tfLbz, tfVtrNr, cbKundeVorOrt, cbNegRm, dcWiedervorlage, tfAqs,
                        tfLL });
            }
        }
        GuiTools.unlockComponents(new Component[] { dcKuendAnCarrier, dcKuendBest });
    }
}
