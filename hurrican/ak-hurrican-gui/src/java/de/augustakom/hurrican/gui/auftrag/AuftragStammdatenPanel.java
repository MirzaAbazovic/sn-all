/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 09:11:29
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.actions.PrintAuftragAction;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragIDsTableModel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.SimpleVerlaufView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Panel zur Darstellung der Stammdaten eines CC-Auftrags.
 */
public class AuftragStammdatenPanel extends AbstractAuftragPanel implements AKModelOwner,
        AKObjectSelectionListener, IAuftragStatusValidator {

    private static final long serialVersionUID = -3518832177940293313L;

    public static final String TF_AUFTRAG_ID = "auftrag.id";

    private static final Logger LOGGER = Logger.getLogger(AuftragStammdatenPanel.class);

    /**
     * String, um das AuftragDaten-Objekt zu ueberwachen.
     */
    private static final String WATCH_AUFTRAG_DATEN = "watch.auftrag.daten";

    /**
     * String, um das AuftragTechnik-Objekt zu ueberwachen.
     */
    private static final String WATCH_AUFTRAG_TECHNIK = "watch.auftrag.technik";
    private static final String AUTO_SMS_VERSAND = "auto.sms.versand";

    private AKJFormattedTextField tfAuftragId = null;
    private AKJTextField tfVbz = null;
    private AKJTextField tfProdukt = null;
    private AKJTextField tfBProdukt = null;
    private AKJTextField tfStatus = null;
    private AKJTextField tfBAStatus = null;
    private AKJDateComponent dcAngebotAm = null;
    private AKJDateComponent dcAuftragAm = null;
    private AKJDateComponent dcVorgabeKunde = null;
    private AKJDateComponent dcVorgabeAM = null;
    private AKJDateComponent dcInbetrieb = null;
    private AKJDateComponent dcKuendigung = null;
    private AKJFormattedTextField tfBAuftragNo = null;
    private AKJTextField tfSapId = null;
    private AKJTextField tfBestellNr = null;
    private AKJTextField tfLbzKunde = null;
    private AKJTextArea taBemerkung = null;
    private AKJComboBox cbAuftragsart = null;
    private AKJComboBox cbNiederlassung = null;
    private AKJFormattedTextField tfKNoOrig = null;
    private AKJTextField tfKName = null;
    private AKJTextField tfKVorname = null;
    private AKJTextField tfBearbeiter = null;
    private AKJCheckBox chbPreventCPS = null;
    private AKJCheckBox chbStatusmeldungen = null;
    private AKJComboBox cbProjectResponsible;
    private AKJComboBox cbProjectLead;

    private AuftragIDsTableModel tbMdlAuftraege = null;
    private AKReflectionTableModel<SimpleVerlaufView> tbMdlVerlauf = null;

    private CCAuftragModel model = null;
    private AuftragDaten auftragDaten = null;
    private AuftragTechnik auftragTechnik = null;
    private AuftragStatus auftragStatus = null;
    private VerbindungsBezeichnung verbindungsBezeichnung = null;
    private Produkt produkt = null;
    private Kunde kunde = null;
    private String produktNameBilling = null;
    private BAuftrag billingAuftrag = null;
    private List<SimpleVerlaufView> verlaeufe = null;

    private boolean inLoad = false;
    private AKJCheckBox chbAutoSms;

    public AuftragStammdatenPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragStammdatenPanel.xml");
        createGUI();
        loadDefaultData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblAuftragId = getSwingFactory().createLabel(TF_AUFTRAG_ID);
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblBProdukt = getSwingFactory().createLabel("produkt.billing");
        AKJLabel lblStatus = getSwingFactory().createLabel("status");
        AKJLabel lblBAStatus = getSwingFactory().createLabel("verlauf.status");
        AKJLabel lblAngebotAm = getSwingFactory().createLabel("angebot.am");
        AKJLabel lblAuftragAm = getSwingFactory().createLabel("auftrag.am");
        AKJLabel lblVorgabeKunde = getSwingFactory().createLabel("vorgabe.kunde");
        AKJLabel lblVorgabeAM = getSwingFactory().createLabel("vorgabe.am");
        AKJLabel lblInbetrieb = getSwingFactory().createLabel("inbetriebnahme");
        AKJLabel lblKuendigung = getSwingFactory().createLabel("kuendigung");
        AKJLabel lblBAuftragNo = getSwingFactory().createLabel("billing.auftrag.no");
        AKJLabel lblBestellNr = getSwingFactory().createLabel("bestell.nr");
        AKJLabel lblLbzKunde = getSwingFactory().createLabel("lbz.kunde");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblAuftragsart = getSwingFactory().createLabel("auftragsart");
        AKJLabel lblKNoOrig = getSwingFactory().createLabel("kunde.no.orig");
        AKJLabel lblKName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblKVorname = getSwingFactory().createLabel("kunde.vorname");
        AKJLabel lblBearbeiter = getSwingFactory().createLabel("bearbeiter");
        AKJLabel lblPreventCPS = getSwingFactory().createLabel("prevent.cps.provisioning");
        AKJLabel lblStatusmeldungen = getSwingFactory().createLabel("taifun.status.feed.back");
        AKJLabel lblNiederlassung = getSwingFactory().createLabel("niederlassung");
        AKJLabel lblProjectResponsible = getSwingFactory().createLabel("projektverantwortlicher");
        AKJLabel lblProjectLead = getSwingFactory().createLabel("projektleiter");

        Dimension cbDim = new Dimension(80, 22);
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        tfAuftragId = getSwingFactory().createFormattedTextField(TF_AUFTRAG_ID, false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        tfBProdukt = getSwingFactory().createTextField("produkt.billing", false);
        tfStatus = getSwingFactory().createTextField("status", false);
        tfStatus.addMouseListener(new ChangeAuftragStatusMouseListener());
        tfBAStatus = getSwingFactory().createTextField("verlauf.status", false);
        tfBearbeiter = getSwingFactory().createTextField("bearbeiter", false);
        dcAngebotAm = getSwingFactory().createDateComponent("angebot.am");
        dcAuftragAm = getSwingFactory().createDateComponent("auftrag.am");
        dcVorgabeKunde = getSwingFactory().createDateComponent("vorgabe.kunde");
        dcVorgabeAM = getSwingFactory().createDateComponent("vorgabe.am");
        dcInbetrieb = getSwingFactory().createDateComponent("inbetriebnahme");
        dcKuendigung = getSwingFactory().createDateComponent("kuendigung");
        cbNiederlassung = getSwingFactory().createComboBox("niederlassung",
                new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));
        tfBAuftragNo = getSwingFactory().createFormattedTextField("billing.auftrag.no", false);
        tfSapId = getSwingFactory().createTextField("sap.id", false);
        cbAuftragsart = getSwingFactory().createComboBox("auftragsart",
                new AKCustomListCellRenderer<>(BAVerlaufAnlass.class, BAVerlaufAnlass::getName));
        cbAuftragsart.setPreferredSize(cbDim);
        tfKNoOrig = getSwingFactory().createFormattedTextField("kunde.no.orig", false);
        tfKName = getSwingFactory().createTextField("kunde.name", false);
        tfKVorname = getSwingFactory().createTextField("kunde.vorname", false);
        tfBestellNr = getSwingFactory().createTextField("bestell.nr");
        tfLbzKunde = getSwingFactory().createTextField("lbz.kunde");
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        taBemerkung.setLineWrap(true);
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        chbPreventCPS = getSwingFactory().createCheckBox("prevent.cps.provisioning");
        chbStatusmeldungen = getSwingFactory().createCheckBox("taifun.status.feed.back");
        AKJButton btnLockCps = getSwingFactory().createButton("lock.cps", getActionListener(), null);
        btnLockCps.setPreferredSize(new Dimension(25, 25));
        AKJButton btnDefineVbz = getSwingFactory().createButton("define.vbz", getActionListener());
        btnDefineVbz.setPreferredSize(new Dimension(20, 20));
        cbProjectResponsible = getSwingFactory().createComboBox("projektverantwortlicher",
                new AKCustomListCellRenderer<>(AKUser.class, AKUser::getNameAndFirstName));
        cbProjectLead = getSwingFactory().createComboBox("projektverantwortlicher",
                new AKCustomListCellRenderer<>(AKUser.class, AKUser::getNameAndFirstName));

        // Tabelle mit den Buendel-Auftraegen
        tbMdlAuftraege = new AuftragIDsTableModel();
        AKJTable tbAuftraege = new AKJTable(tbMdlAuftraege, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftraege.setFilterEnabled(false);
        tbAuftraege.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbAuftraege.attachSorter();
        tbAuftraege.fitTable(new int[] { 120, 70, 70, 150 });
        AKJScrollPane spBuendel = new AKJScrollPane(tbAuftraege);
        spBuendel.setPreferredSize(new Dimension(410, 160));
        AKJPanel buendelPnl = new AKJPanel(new BorderLayout());
        buendelPnl.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("buendel")));
        buendelPnl.add(spBuendel, BorderLayout.CENTER);

        // Tabelle mit den Verlaufs-Eintraegen zu dem Auftrag
        tbMdlVerlauf = new AKReflectionTableModel<>(
                new String[] { "Real.-Termin", "Anlass", "Status", "Projektierung" },
                new String[] { "realisierungstermin", "anlass", "status", "projektierung" },
                new Class[] { Date.class, String.class, String.class, Boolean.class });
        AKJTable tbVerlauf = new AKJTable(tbMdlVerlauf, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVerlauf.setFilterEnabled(false);
        tbVerlauf.fitTable(new int[] { 80, 100, 125, 90 });
        AKJScrollPane spVerlauf = new AKJScrollPane(tbVerlauf);
        spVerlauf.setPreferredSize(new Dimension(400, 160));
        AKJPanel verlaufPnl = new AKJPanel(new BorderLayout());
        verlaufPnl.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("verlauf")));
        verlaufPnl.add(spVerlauf, BorderLayout.CENTER);

        final AKJLabel lblAutoSms = getSwingFactory().createLabel(AUTO_SMS_VERSAND);
        chbAutoSms = getSwingFactory().createCheckBox(AUTO_SMS_VERSAND);

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblAuftragId       , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel()     , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfAuftragId        , GBCFactory.createGBC(100, 0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVbz             , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfVbz              , GBCFactory.createGBC(100, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnDefineVbz       , GBCFactory.createGBC(  0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProdukt         , GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfProdukt          , GBCFactory.createGBC(100, 0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBProdukt        , GBCFactory.createGBC(  0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBProdukt         , GBCFactory.createGBC(100, 0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStatus          , GBCFactory.createGBC(  0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfStatus           , GBCFactory.createGBC(100, 0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBAStatus        , GBCFactory.createGBC(  0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBAStatus         , GBCFactory.createGBC(100, 0, 2, 5, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBearbeiter      , GBCFactory.createGBC(  0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBearbeiter       , GBCFactory.createGBC(100, 0, 2, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAngebotAm       , GBCFactory.createGBC(  0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcAngebotAm        , GBCFactory.createGBC(100, 0, 2, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAuftragAm       , GBCFactory.createGBC(  0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcAuftragAm        , GBCFactory.createGBC(100, 0, 2, 8, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVorgabeKunde    , GBCFactory.createGBC(  0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcVorgabeKunde     , GBCFactory.createGBC(100, 0, 2, 9, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVorgabeAM       , GBCFactory.createGBC(  0, 0, 0,10, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcVorgabeAM        , GBCFactory.createGBC(100, 0, 2,10, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblInbetrieb       , GBCFactory.createGBC(  0, 0, 0,11, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcInbetrieb        , GBCFactory.createGBC(100, 0, 2,11, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblKuendigung      , GBCFactory.createGBC(  0, 0, 0,12, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcKuendigung       , GBCFactory.createGBC(100, 0, 2,12, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPreventCPS      , GBCFactory.createGBC(  0, 0, 0,13, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbPreventCPS      , GBCFactory.createGBC(  0, 0, 2,13, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnLockCps         , GBCFactory.createGBC(100, 0, 3,13, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStatusmeldungen , GBCFactory.createGBC(  0, 0, 0,14, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbStatusmeldungen , GBCFactory.createGBC(  0, 0, 2,14, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAutoSms         , GBCFactory.createGBC(  0, 0, 0,15, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbAutoSms         , GBCFactory.createGBC(  0, 0, 2,15, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel()     , GBCFactory.createGBC(  0, 0, 0,16, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblBAuftragNo         , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfBAuftragNo          , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfSapId               , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKNoOrig            , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfKNoOrig             , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKName              , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfKName               , GBCFactory.createGBC(  0,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblKVorname           , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfKVorname            , GBCFactory.createGBC(  0,  0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblAuftragsart        , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbAuftragsart         , GBCFactory.createGBC(  0,  0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblBestellNr          , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfBestellNr           , GBCFactory.createGBC(  0,  0, 2, 5, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblLbzKunde           , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfLbzKunde            , GBCFactory.createGBC(  0,  0, 2, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblNiederlassung      , GBCFactory.createGBC(  0,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbNiederlassung       , GBCFactory.createGBC(  0,  0, 2, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblProjectResponsible , GBCFactory.createGBC(  0,  0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbProjectResponsible  , GBCFactory.createGBC(  0,  0, 2, 8, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblProjectLead        , GBCFactory.createGBC(  0,  0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbProjectLead         , GBCFactory.createGBC(  0,  0, 2, 9, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblBemerkung          , GBCFactory.createGBC(  0,  0, 0,10, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(spBemerkung           , GBCFactory.createGBC(100,100, 2,10, 2, 3, GridBagConstraints.BOTH));
        right.add(new AKJPanel()        , GBCFactory.createGBC(  0,100, 0,11, 1, 1, GridBagConstraints.VERTICAL));
        right.add(btnPrint              , GBCFactory.createGBC(  0,  0, 0,12, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(40, 10));
        AKJPanel stammPnl = new AKJPanel(new GridBagLayout());
        stammPnl.setBorder(BorderFactory.createTitledBorder("Auftrags- und Kundendaten"));
        stammPnl.add(left          , GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        stammPnl.add(fill          , GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        stammPnl.add(right         , GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.BOTH));
        stammPnl.add(new AKJPanel(), GBCFactory.createGBC(1, 0, 3, 0, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(stammPnl       , GBCFactory.createGBC(100,   0, 0, 0, 2, 1, GridBagConstraints.BOTH));
        this.add(buendelPnl     , GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        this.add(verlaufPnl     , GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        manageGUI(dcAngebotAm, dcAuftragAm, dcVorgabeAM, btnLockCps,
                tfBestellNr, taBemerkung, chbPreventCPS, chbStatusmeldungen, cbProjectResponsible, cbProjectLead, btnPrint );

        // Folgende Fields immer disabled
        dcVorgabeKunde.setEnabled(false);
        dcInbetrieb.setEnabled(false);
        dcKuendigung.setEnabled(false);
    }

    @Override
    public void setModel(Observable model) {
        if (model instanceof CCAuftragModel) {
            this.model = (CCAuftragModel) model;
        }
        else {
            this.model = null;
        }

        readModel();
    }

    /* Laedt die Standard-Daten. */
    private void loadDefaultData() {
        try {
            setWaitCursor();

            // Werte fuer 'Auftragsart' ermitteln
            BAConfigService bas = getCCService(BAConfigService.class);
            List<BAVerlaufAnlass> vas = bas.findBAVerlaufAnlaesse(Boolean.TRUE, Boolean.TRUE);
            cbAuftragsart.addItems(vas, true, BAVerlaufAnlass.class);

            // Werte fuer Niederlassung ermitteln
            NiederlassungService nlService = getCCService(NiederlassungService.class);
            List<Niederlassung> nlRefs = nlService.findNiederlassungen();
            cbNiederlassung.addItems(nlRefs, true, Niederlassung.class);

            // Werte fuer Hauptprojektverantwortlichen ermitteln
            AKUserService userService = getAuthenticationService(AKUserService.class);

            List<AKUser> users = userService.findAllProjektleiter();
            cbProjectResponsible.addItems(users, false, AKUser.class);
            cbProjectLead.addItems(users, false, AKUser.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    public void readModel() {
        auftragDaten = null;
        auftragStatus = null;
        verlaeufe = null;
        produkt = null;
        billingAuftrag = null;
        tbMdlAuftraege.removeAll();
        tbMdlVerlauf.removeAll();

        if (this.model != null) {
            try {
                inLoad = true;
                setWaitCursor();
                CCAuftragService as = getCCService(CCAuftragService.class);

                // Auftragsdaten laden
                auftragDaten = as.findAuftragDatenByAuftragId(model.getAuftragId());
                addObjectToWatch(WATCH_AUFTRAG_DATEN, auftragDaten);

                auftragTechnik = as.findAuftragTechnikByAuftragId(model.getAuftragId());
                addObjectToWatch(WATCH_AUFTRAG_TECHNIK, auftragTechnik);

                // Verlaufs-Daten laden
                BAService bas = getCCService(BAService.class);
                verlaeufe = bas.findSimpleVerlaufViews4Auftrag(model.getAuftragId());
                tbMdlVerlauf.setData(verlaeufe);

                if (auftragDaten != null) {
                    auftragStatus = as.findAuftragStatus(auftragDaten.getStatusId());

                    ProduktService ps = getCCService(ProduktService.class);
                    produkt = ps.findProdukt(auftragDaten.getProdId());

                    // VerbindungsBezeichnung laden
                    PhysikService phys = getCCService(PhysikService.class);
                    verbindungsBezeichnung = phys.findVerbindungsBezeichnungByAuftragId(model.getAuftragId());

                    loadBuendelAuftraege(as);

                    // Kunde laden
                    Auftrag ccAuftrag = as.findAuftragById(auftragDaten.getAuftragId());
                    KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
                    kunde = ks.findKunde(ccAuftrag.getKundeNo());

                    if (auftragDaten.getAuftragNoOrig() != null) {
                        // Produktname aus dem Billing-System laden
                        OEService oes = getBillingService(OEService.class);
                        produktNameBilling = oes.findProduktName4Auftrag(auftragDaten.getAuftragNoOrig());

                        // Billing-Auftrag laden
                        BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);
                        billingAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
                    }
                }

                if ((auftragTechnik != null) && !cbAuftragsart.containsItem("getId",
                        BAVerlaufAnlass.class, auftragTechnik.getAuftragsart())) {
                    // aus historischen Gruenden muessen auch andere Auftragsarten
                    // beruecksichtigt werden.
                    BAConfigService configService = getCCService(BAConfigService.class);
                    BAVerlaufAnlass anlass = configService.findBAVerlaufAnlass(auftragTechnik.getAuftragsart());
                    if ((anlass != null) && BooleanTools.nullToFalse(anlass.getAuftragsart())) {
                        cbAuftragsart.addItem(anlass);
                    }
                }

                showValues();
            }
            catch (ServiceNotFoundException | FindException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                Long tmpStatusId = (auftragDaten != null) ? auftragDaten.getStatusId() : null;
                validate4Status(tmpStatusId);
                setDefaultCursor();
                inLoad = false;
            }
        }
    }

    private void loadBuendelAuftraege(CCAuftragService as) throws FindException {
        Collection<CCAuftragIDsView> buendel = as.findAufragIdAndVbz4AuftragNoOrigAndBuendel(auftragDaten.getAuftragNoOrig(),
                auftragDaten.getBuendelNr(), auftragDaten.getBuendelNrHerkunft());
        if (buendel.size() > 1) {
            // falls nur ein Auftrag (=aktueller Auftrag) -> keine Anzeige
            tbMdlAuftraege.setData(buendel);
        }
    }

    /**
     * Zeigt die Daten des AuftragDaten-Objekts an.
     */
    private void showValues() {
        clear();
        if (auftragDaten != null) {
            chbAutoSms.setSelected(auftragDaten.isAutoSmsAndMailVersand());
            tfAuftragId.setValue(auftragDaten.getAuftragId());
            tfBProdukt.setText(produktNameBilling);
            tfBearbeiter.setText(auftragDaten.getBearbeiter());
            dcAngebotAm.setDate(auftragDaten.getAngebotDatum());
            dcAuftragAm.setDate(auftragDaten.getAuftragDatum());
            dcVorgabeKunde.setDate(auftragDaten.getVorgabeKunde());
            dcVorgabeAM.setDate(auftragDaten.getVorgabeSCV());
            dcInbetrieb.setDate(auftragDaten.getInbetriebnahme());
            dcKuendigung.setDate(auftragDaten.getKuendigung());
            tfBAuftragNo.setValue(auftragDaten.getAuftragNoOrig());
            tfBestellNr.setText(auftragDaten.getBestellNr());
            tfLbzKunde.setText(auftragDaten.getLbzKunde());
            taBemerkung.setText(auftragDaten.getBemerkungen());
            chbStatusmeldungen.setSelected(auftragDaten.getStatusmeldungen());
            if ((verlaeufe != null) && !verlaeufe.isEmpty()) {
                SimpleVerlaufView verlView = verlaeufe.get(0);
                tfBAStatus.setText(verlView.getStatus());
            }

            if (billingAuftrag != null) {
                tfSapId.setText(billingAuftrag.getSapId());
            }

            if (auftragTechnik != null) {
                chbPreventCPS.setSelected(auftragTechnik.getPreventCPSProvisioning());
                cbAuftragsart.selectItem("getId", BAVerlaufAnlass.class, auftragTechnik.getAuftragsart());
                cbNiederlassung.selectItem("getId", Niederlassung.class, auftragTechnik.getNiederlassungId());
                cbProjectResponsible.selectItem("getId", AKUser.class, auftragTechnik.getProjectResponsibleUserId());
                cbProjectLead.selectItem("getId", AKUser.class, auftragTechnik.getProjectLeadUserId());
            }

            if (verbindungsBezeichnung != null) {
                tfVbz.setText(verbindungsBezeichnung.getVbz());
            }

            if (auftragStatus != null) {
                tfStatus.setText(auftragStatus.getStatusText());
            }

            if (produkt != null) {
                tfProdukt.setText(produkt.getAnschlussart());
            }

            if (kunde != null) {
                tfKNoOrig.setValue(kunde.getKundeNo());
                tfKName.setText(kunde.getName());
                tfKVorname.setText(kunde.getVorname());
            }

        }
        else {
            String aId = (this.model != null) ? this.model.getAuftragId().toString() : null;
            String msg = "Es wurden keine Daten zu dem Auftrag {0} gefunden.";
            MessageHelper.showMessageDialog(this,
                    msg, new Object[] { aId }, "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /* 'Loescht' alle Felder. */
    private void clear() {
        GuiTools.cleanFields(this);
    }

    /**
     * Liefert folende Objekte in einem Object-Array zurueck: <ul> <li>0: Objekt vom Typ CCAuftragModel, das dem Panel
     * urspruenglich uebergeben wurde <li>1: Objekt vom Typ AuftragDaten <li>2: Objekt vom Typ AuftragStatus <li>3:
     * Objekt vom Typ Produkt <li>4: Objekt vom Typ Kunde </ul>
     *
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if (auftragDaten != null) {
            auftragDaten.setAngebotDatum(dcAngebotAm.getDate(null));
            auftragDaten.setAuftragDatum(dcAuftragAm.getDate(null));
            auftragDaten.setVorgabeSCV(dcVorgabeAM.getDate(null));
            auftragDaten.setVorgabeKunde(dcVorgabeKunde.getDate(null));
            auftragDaten.setKuendigung(dcKuendigung.getDate(null));
            auftragDaten.setBestellNr(tfBestellNr.getText(null));
            auftragDaten.setLbzKunde(tfLbzKunde.getText(null));
            String bemerkungNeu = appendUserAndDateIfChanged(auftragDaten.getBemerkungen(), taBemerkung.getText(null));
            auftragDaten.setBemerkungen(bemerkungNeu);
            auftragDaten.setStatusmeldungen(chbStatusmeldungen.isSelectedBoolean());
            auftragDaten.setKuendigung(dcKuendigung.getDate(null));
            auftragDaten.setVorgabeSCV(dcVorgabeAM.getDate(null));
            auftragDaten.setAutoSmsAndMailVersand(chbAutoSms.isSelectedBoolean());
        }

        if (auftragTechnik != null) {
            auftragTechnik.setAuftragsart((cbAuftragsart.getSelectedItem() instanceof BAVerlaufAnlass)
                    ? ((BAVerlaufAnlass) cbAuftragsart.getSelectedItem()).getId() : null);
            auftragTechnik.setNiederlassungId((cbNiederlassung.getSelectedItem() instanceof Niederlassung)
                    ? ((Niederlassung) cbNiederlassung.getSelectedItem()).getId() : null);
            auftragTechnik.setProjectResponsibleUserId((cbProjectResponsible.getSelectedItem() instanceof AKUser)
                    ? ((AKUser) cbProjectResponsible.getSelectedItem()).getId() : null);
            auftragTechnik.setProjectLeadUserId((cbProjectLead.getSelectedItem() instanceof AKUser)
                    ? ((AKUser) cbProjectLead.getSelectedItem()).getId() : null);
            auftragTechnik.setPreventCPSProvisioning(chbPreventCPS.isSelectedBoolean());
        }

        return new Object[] { this.model, auftragDaten, auftragTechnik, auftragStatus, produkt, kunde };
    }


    /**
     * Ueberprueft, ob Auftragsdaten geaendert wurden.
     *
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        if (inLoad) {
            return false;
        }

        Object[] values = (Object[]) getModel();
        boolean changed = hasChanged(WATCH_AUFTRAG_DATEN, values[1]);
        if (!changed) {
            changed = hasChanged(WATCH_AUFTRAG_TECHNIK, values[2]);
        }
        return changed;
    }

    @Override
    public void saveModel() {
        try {
            setWaitCursor();
            if (hasModelChanged()) {
                Object[] values = (Object[]) getModel();
                AuftragDaten adToSave = (AuftragDaten) values[1];
                if (StringUtils.isBlank(adToSave.getBearbeiter())) {
                    adToSave.setBearbeiter(HurricanSystemRegistry.instance().getCurrentUserName());
                }

                AuftragTechnik atToSave = (AuftragTechnik) values[2];

                CCAuftragService as = getCCService(CCAuftragService.class);
                this.auftragDaten = as.saveAuftragDaten(adToSave, makeHistory4Status(adToSave.getStatusId()));
                addObjectToWatch(WATCH_AUFTRAG_DATEN, this.auftragDaten);

                this.auftragTechnik = as.saveAuftragTechnik(atToSave, false);
                addObjectToWatch(WATCH_AUFTRAG_TECHNIK, this.auftragTechnik);

                showValues();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if ("print".equals(command)) {
            PrintAuftragAction action = new PrintAuftragAction();
            action.putValue(PrintAuftragAction.MODEL_OWNER, this);
            action.actionPerformed(new ActionEvent(this, 0, "report.auftrag"));
        }
        else if ("define.vbz".equals(command)) {
            showVbzDialog();
        }
        else if ("lock.cps".equals(command)) {
            lockCps();
        }
    }

    /**
     * Setzt das Flag 'PREVENT_CPS' auf true. Wird umstaendlich ueber einen eigenen Button aufgerufen, da AM in der Lage
     * sein soll, einen Auftrag gegen CPS-Provisionierung zu sperren, aber nicht, einen gg. CPS-Provisionierung
     * gesperrten Auftrag wieder "frei zu schalten".
     */
    private void lockCps() {
        if (chbPreventCPS.isSelected()) {
            return;
        }

        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                "Soll der Auftrag wirklich gegen eine CPS-Provisionierung gesperrt werden?",
                "CPS sperren?");
        if (option == JOptionPane.YES_OPTION) {
            chbPreventCPS.setSelected(true);
        }
    }

    private void showVbzDialog() {
        try {
            if (verbindungsBezeichnung == null) {
                throw new HurricanGUIException("Auftrag besitzt keine Verbindungsbezeichnung!");
            }

            DefineVbzDialog vbzDialog = new DefineVbzDialog(verbindungsBezeichnung, produkt, auftragTechnik);
            Object result = DialogHelper.showDialog(getMainFrame(), vbzDialog, true, true);
            if (result instanceof VerbindungsBezeichnung) {
                verbindungsBezeichnung = (VerbindungsBezeichnung) result;
                tfVbz.setText(verbindungsBezeichnung.getVbz());
            }
        }
        catch (HurricanGUIException e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }


    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.UNDEFINIERT, AuftragStatus.ERFASSUNG,
                AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, AuftragStatus.ERFASSUNG_SCV })) {
            GuiTools.unlockComponents(new Component[] { dcAngebotAm, dcAuftragAm, dcVorgabeKunde, dcVorgabeAM,
                    cbAuftragsart, tfBestellNr, taBemerkung, cbNiederlassung });
            GuiTools.lockComponents(new Component[] { dcInbetrieb, dcKuendigung });
        }
        else if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.PROJEKTIERUNG, AuftragStatus.PROJEKTIERUNG_ERLEDIGT })) {
            GuiTools.unlockComponents(new Component[] { dcVorgabeAM, cbAuftragsart,
                    tfBestellNr, taBemerkung, cbNiederlassung, dcVorgabeKunde });
            GuiTools.lockComponents(new Component[] { dcAngebotAm, dcAuftragAm,
                    dcInbetrieb, dcKuendigung });
        }
        else if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.ABSAGE })) {
            GuiTools.unlockComponents(new Component[] { dcAngebotAm });
            GuiTools.lockComponents(new Component[] { dcAuftragAm, dcVorgabeKunde, dcVorgabeAM,
                    dcInbetrieb, dcKuendigung, cbAuftragsart, tfBestellNr, taBemerkung, cbNiederlassung });
        }
        else if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.INTERNET_AUFTRAG, AuftragStatus.ANSCHREIBEN_KUNDEN_ERFASSUNG })) {
            GuiTools.unlockComponents(new Component[] { tfBestellNr, taBemerkung });
            GuiTools.lockComponents(new Component[] { dcAngebotAm, dcAuftragAm, dcVorgabeKunde, dcVorgabeAM,
                    dcInbetrieb, dcKuendigung, cbAuftragsart, cbNiederlassung });
        }
        else if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.TECHNISCHE_REALISIERUNG,
                AuftragStatus.IN_BETRIEB, AuftragStatus.AENDERUNG_IM_UMLAUF, AuftragStatus.AENDERUNG,
                AuftragStatus.KUENDIGUNG_CUDA, AuftragStatus.ANSCHREIBEN_KUNDE_KUEND, AuftragStatus.AUFTRAG_GEKUENDIGT,
                AuftragStatus.KUENDIGUNG_TECHN_REAL })) {
            GuiTools.unlockComponents(new Component[] { tfBestellNr, taBemerkung });
            GuiTools.lockComponents(new Component[] { dcAngebotAm, dcAuftragAm, dcVorgabeKunde, dcVorgabeAM,
                    dcInbetrieb, dcKuendigung, cbAuftragsart, cbNiederlassung });
        }
        else if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.KUENDIGUNG, AuftragStatus.KUENDIGUNG_ERFASSEN })) {
            GuiTools.unlockComponents(new Component[] { tfBestellNr, taBemerkung, dcKuendigung });
            GuiTools.lockComponents(new Component[] { dcAngebotAm, dcAuftragAm, dcVorgabeKunde, dcVorgabeAM,
                    dcInbetrieb, cbAuftragsart, cbNiederlassung });
        }
        else {
            // Storno bzw. unbekannter Status
            GuiTools.lockComponents(new Component[] { dcAngebotAm, dcAuftragAm, dcVorgabeKunde, dcVorgabeAM,
                    dcInbetrieb, dcKuendigung, cbAuftragsart, tfBestellNr, taBemerkung, cbNiederlassung });
        }

        if ((auftragDaten.getStatusId() < AuftragStatus.IN_BETRIEB)
                || ((auftragDaten.getStatusId() > AuftragStatus.IN_BETRIEB) && (auftragDaten.getStatusId() <= AuftragStatus.AENDERUNG_IM_UMLAUF))) {
            dcVorgabeAM.setEnabled(true);
        }
        else {
            dcVorgabeAM.setEnabled(false);
        }
        if ((auftragDaten.getStatusId() < AuftragStatus.AUFTRAG_GEKUENDIGT)
                && (auftragDaten.getStatusId() >= AuftragStatus.KUENDIGUNG)) {
            dcKuendigung.setEnabled(true);
        }
        else {
            dcKuendigung.setEnabled(false);
        }

        if (!cbAuftragsart.isEnabled() && (cbAuftragsart.getSelectedIndex() <= 0)) {
            cbAuftragsart.setEnabled(true);
        }
    }

    /*
     * MouseListener, um einen Dialog zu oeffnen, ueber den der
     * Auftragsstatus von Abrechnungsauftraegen geaendert werden kann.
     */
    private class ChangeAuftragStatusMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getClickCount() >= 2) && (e.getSource() == tfStatus)) {
                ChangeAuftragStatusDialog dlg = new ChangeAuftragStatusDialog(auftragDaten);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof Integer && result.equals(ChangeAuftragStatusDialog.OK_OPTION)) {
                    readModel();
                }
            }
        }
    }

}
