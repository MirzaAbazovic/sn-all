/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.utils.SimpleHelperModel;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.BAVerlaufAG2Produkt;
import de.augustakom.hurrican.model.cc.BAVerlaufAenderungGruppe;
import de.augustakom.hurrican.model.cc.GeoIdSource;
import de.augustakom.hurrican.model.cc.IPPoolType;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Sub-Panel, um die administrativen Daten fuer ein Produkt zu konfigurieren.
 *
 */
public class Produkt2AdminPanel extends AbstractServicePanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Produkt2AdminPanel.class);
    private static final long serialVersionUID = -8124031344975021763L;

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model;

    /*
     * Map mit allen BAVerlaufAenderungsGruppen, die dem Produkt zugeordnet sind. Es wird lediglich der Key der Map
     * verwendet (Key = ID der BAVerlaufAenderungsGruppe).
     */
    private Set<Long> aenderungsGruppen;

    // GUI-Elemente fuer das 'Admin'-Panel
    private AKJTextField tfAccountVorsatz;
    private AKJComboBox cbAktionsId;
    private AKJCheckBox chbBuendelProdukt;
    private AKJCheckBox chbBuendelHauptauftrag;
    private AKJCheckBox chbElVerlauf;
    private AKJCheckBox chbBARuecklaeufer;
    private AKJComboBox cbEndstellenTyp;
    private AKJCheckBox chbAuftrErstellen;
    private AKJComboBox cbLiNr;
    private AKJCheckBox chbVpnPhysik;
    private AKJCheckBox chbProjektierung;
    private AKJCheckBox chbVierDraht;
    private BAGruppenTableModel tbMdlGruppen;
    private AKReferenceField rfProjChainId;
    private AKReferenceField rfVerlChainId;
    private AKReferenceField rfVerlCancelChainId;
    private AKJComboBox cbVertDurch;
    private AKJComboBox cbIPPool;
    private AKJComboBox cbVerwendungszweckV4;
    private AKJCheckBox chbVerwendungszweckEditable;
    private AKJFormattedTextField tfNetzmaskeV4;
    private AKJFormattedTextField tfNetzmaskeV6;
    private AKJCheckBox chbNetzmaskeEditable;
    private AKJCheckBox chbAutoPossible;
    private AKJComboBox cbGeoIdSource;
    private AKJCheckBox chbNoAutoHvtZuordnung;
    private AKJCheckBox chbAutoSmsVersand;
    private AKJTextField tfAftrAddress = null;
    private AKJTextField tfPbitDaten = null;
    private AKJTextField tfPbitVoip = null;

    /**
     * Konstruktor.
     */
    public Produkt2AdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/Produkt2AdminPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Erzeuge GUI-Komponenten und ordne diese auf dem Panel an
        AKJLabel lblAccVorsatz = getSwingFactory().createLabel("account.vorsatz");
        AKJLabel lblAktionsId = getSwingFactory().createLabel("aktions.id");
        AKJLabel lblBuendelProdukt = getSwingFactory().createLabel("buendel.produkt");
        AKJLabel lblBuendelHauptauftrag = getSwingFactory().createLabel("buendel.hauptauftrag");
        AKJLabel lblElVerlauf = getSwingFactory().createLabel("el.verlauf");
        AKJLabel lblBARuecklaeufer = getSwingFactory().createLabel("ba.ruecklaeufer");
        AKJLabel lblEndstellenTyp = getSwingFactory().createLabel("endstellen.typ");
        AKJLabel lblAuftrErstellen = getSwingFactory().createLabel("auftragserstellung");
        AKJLabel lblVpnPhysik = getSwingFactory().createLabel("vpn.physik");
        AKJLabel lblProjektierung = getSwingFactory().createLabel("projektierung");
        AKJLabel lblLiNr = getSwingFactory().createLabel("li.nr");
        AKJLabel lblGruppen = getSwingFactory().createLabel("aenderungsgruppen");
        AKJLabel lblProjChainId = getSwingFactory().createLabel("projektierung.chain");
        AKJLabel lblVerlChainId = getSwingFactory().createLabel("verlauf.chain");
        AKJLabel lblVerlCancelChainId = getSwingFactory().createLabel("verlauf.cancel.chain");
        AKJLabel lblVertDurch = getSwingFactory().createLabel("verteilung.durch");
        AKJLabel lblVierDraht = getSwingFactory().createLabel("vier.draht");
        AKJLabel lblIPPool = getSwingFactory().createLabel("ip.pool");
        AKJLabel lblVerwendungszweckV4 = getSwingFactory().createLabel("verwendungszweck.v4");
        AKJLabel lblVerwendungszweckEditiable = getSwingFactory().createLabel("verwendungszweck.v4.editierbar");
        AKJLabel lblNetzmaskeV4 = getSwingFactory().createLabel("netzmaske.v4");
        AKJLabel lblNetzmaskeV6 = getSwingFactory().createLabel("netzmaske.v6");
        AKJLabel lblNetzmaskeEditable = getSwingFactory().createLabel("netzmaske.editierbar");
        AKJLabel lblAutoPossible = getSwingFactory().createLabel("automation.possible");
        AKJLabel lblGeoIdSrc = getSwingFactory().createLabel("geoid.src");
        AKJLabel lblNoAutoHvtZuordnung = getSwingFactory().createLabel("no.auto.hvt.zuordnung");
        AKJLabel lblAutoSmsVersand = getSwingFactory().createLabel("auto.sms.versand");
        AKJLabel lblAftrAddress = getSwingFactory().createLabel("aftr.adresse");
        AKJLabel lblPBitDaten = getSwingFactory().createLabel("p.bit.daten");
        AKJLabel lblPBitVoip= getSwingFactory().createLabel("p.bit.voip");

        tbMdlGruppen = new BAGruppenTableModel();
        AKJTable tbGruppen = new AKJTable(tbMdlGruppen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbGruppen.fitTable(new int[] { 190, 50 });
        AKJScrollPane spGruppen = new AKJScrollPane(tbGruppen);
        spGruppen.setPreferredSize(new Dimension(270, 150));

        tfAccountVorsatz = getSwingFactory().createTextField("account.vorsatz");
        cbAktionsId = getSwingFactory().createComboBox("aktions.id");
        cbAktionsId.setRenderer(new AKCustomListCellRenderer<>(SimpleHelperModel.class, SimpleHelperModel::getText));
        chbBuendelProdukt = getSwingFactory().createCheckBox("buendel.produkt");
        chbBuendelHauptauftrag = getSwingFactory().createCheckBox("buendel.hauptauftrag");
        chbElVerlauf = getSwingFactory().createCheckBox("el.verlauf");
        chbBARuecklaeufer = getSwingFactory().createCheckBox("ba.ruecklaeufer");
        cbEndstellenTyp = getSwingFactory().createComboBox("endstellen.typ");
        cbEndstellenTyp.setRenderer(new AKCustomListCellRenderer<>(SimpleHelperModel.class, SimpleHelperModel::getText));
        chbAuftrErstellen = getSwingFactory().createCheckBox("auftragserstellung");
        chbVpnPhysik = getSwingFactory().createCheckBox("vpn.physik");
        chbProjektierung = getSwingFactory().createCheckBox("projektierung.chain");
        cbLiNr = getSwingFactory().createComboBox("li.nr");
        cbLiNr.setRenderer(new AKCustomListCellRenderer<>(SimpleHelperModel.class, SimpleHelperModel::getText));
        cbVertDurch = getSwingFactory().createComboBox("verteilung.durch",
                new AKCustomListCellRenderer<>(Abteilung.class, Abteilung::getName));
        chbVierDraht = getSwingFactory().createCheckBox("vier.draht");
        cbVerwendungszweckV4 = getSwingFactory().createComboBox("verwendungszweck.v4",
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        chbVerwendungszweckEditable = getSwingFactory().createCheckBox("verwendungszweck.v4.editierbar");
        tfNetzmaskeV4 = getSwingFactory().createFormattedTextField("netzmaske.v4");
        tfNetzmaskeV6 = getSwingFactory().createFormattedTextField("netzmaske.v6");
        chbNetzmaskeEditable = getSwingFactory().createCheckBox("netzmaske.editierbar");
        chbAutoPossible = getSwingFactory().createCheckBox("automation.possible");
        chbNoAutoHvtZuordnung = getSwingFactory().createCheckBox("no.auto.hvt.zuordnung");
        chbAutoSmsVersand = getSwingFactory().createCheckBox("auto.sms.versand");
        tfAftrAddress = getSwingFactory().createTextField("aftr.adresse");
        tfPbitDaten = getSwingFactory().createTextField("p.bit.daten");
        tfPbitVoip = getSwingFactory().createTextField("p.bit.voip");

        ServiceChain findExample = new ServiceChain();
        findExample.setType(ServiceChain.CHAIN_TYPE_VERLAUF_CHECK);
        rfProjChainId = getSwingFactory().createReferenceField(
                "projektierung.chain", ServiceChain.class, "id", "name", findExample);
        rfVerlChainId = getSwingFactory().createReferenceField(
                "verlauf.chain", ServiceChain.class, "id", "name", findExample);
        rfVerlCancelChainId = getSwingFactory().createReferenceField(
                "verlauf.cancel.chain", ServiceChain.class, "id", "name", findExample);
        cbIPPool = getSwingFactory().createComboBox("ip.pool");
        cbIPPool.setRenderer(new AKCustomListCellRenderer<>(SimpleHelperModel.class, SimpleHelperModel::getText));

        cbGeoIdSource = getSwingFactory().createComboBox("geoid.src");
        cbGeoIdSource.setRenderer(new AKCustomListCellRenderer<>(GeoIdSource.class, GeoIdSource::getDisplayName));
        cbGeoIdSource.addItems(Arrays.asList(GeoIdSource.values()));
        cbGeoIdSource.setSelectedItem(GeoIdSource.HVT);

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        int leftCount = 0;
        left.add(new AKJPanel()             , GBCFactory.createGBC(  0,  0, 0, leftCount  , 1, 1, GridBagConstraints.NONE));
        left.add(lblAccVorsatz              , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel()             , GBCFactory.createGBC(  0,  0, 2, leftCount  , 1, 1, GridBagConstraints.NONE));
        left.add(tfAccountVorsatz           , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLiNr                    , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbLiNr                     , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBuendelHauptauftrag     , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbBuendelHauptauftrag     , GBCFactory.createGBC(100,  0, 3, leftCount++, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBuendelProdukt          , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbBuendelProdukt          , GBCFactory.createGBC(100,  0, 3, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVierDraht               , GBCFactory.createGBC(  0,  0, 4, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL, 15));
        left.add(chbVierDraht               , GBCFactory.createGBC(100,  0, 5, leftCount++, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        left.add(lblElVerlauf               , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbElVerlauf               , GBCFactory.createGBC(100,  0, 3, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBARuecklaeufer          , GBCFactory.createGBC(  0,  0, 4, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL, 15));
        left.add(chbBARuecklaeufer          , GBCFactory.createGBC(100,  0, 5, leftCount++, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        left.add(lblEndstellenTyp           , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbEndstellenTyp            , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAuftrErstellen          , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbAuftrErstellen          , GBCFactory.createGBC(100,  0, 3, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAutoPossible            , GBCFactory.createGBC(  0,  0, 4, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL, 15));
        left.add(chbAutoPossible            , GBCFactory.createGBC(100,  0, 5, leftCount++, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        left.add(lblVpnPhysik               , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbVpnPhysik               , GBCFactory.createGBC(100,  0, 3, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblNoAutoHvtZuordnung      , GBCFactory.createGBC(  0,  0, 4, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL, 15));
        left.add(chbNoAutoHvtZuordnung      , GBCFactory.createGBC(100,  0, 5, leftCount++, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        left.add(lblAutoSmsVersand          , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbAutoSmsVersand          , GBCFactory.createGBC(100,  0, 3, leftCount++, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfVerlChainId              , GBCFactory.createGBC(100,  0, 3, leftCount  , 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVertDurch               , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbVertDurch                , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGeoIdSrc                , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbGeoIdSource              , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAftrAddress             , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAftrAddress              , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPBitDaten               , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPbitDaten                , GBCFactory.createGBC(100,  0, 3, leftCount++, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblPBitVoip                , GBCFactory.createGBC(  0,  0, 1, leftCount  , 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfPbitVoip                 , GBCFactory.createGBC(100,  0, 3, leftCount  , 3, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(new AKJPanel()              , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(lblProjektierung            , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel()              , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(chbProjektierung            , GBCFactory.createGBC(100,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAktionsId                , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(cbAktionsId                 , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblGruppen                  , GBCFactory.createGBC(  0,  0, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        mid.add(spGruppen                   , GBCFactory.createGBC(100,100, 1, 3, 3, 5, GridBagConstraints.BOTH));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel()               , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(lblProjChainId               , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()               , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE));
        right.add(rfProjChainId                , GBCFactory.createGBC(100,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblVerlChainId               , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(rfVerlChainId                , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblVerlCancelChainId         , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(rfVerlCancelChainId          , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblIPPool                    , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbIPPool                     , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblVerwendungszweckV4        , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbVerwendungszweckV4         , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblVerwendungszweckEditiable , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbVerwendungszweckEditable  , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblNetzmaskeV4               , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfNetzmaskeV4                , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblNetzmaskeV6               , GBCFactory.createGBC(  0,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfNetzmaskeV6                , GBCFactory.createGBC(100,  0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblNetzmaskeEditable         , GBCFactory.createGBC(  0,  0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbNetzmaskeEditable         , GBCFactory.createGBC(100,  0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()               , GBCFactory.createGBC(  0,100, 1, 9, 3, 5, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left            , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(mid             , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.VERTICAL,7));
        this.add(right           , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.VERTICAL,7));
        this.add(new AKJPanel()  , GBCFactory.createGBC(100,100, 3, 1, 1, 1, GridBagConstraints.BOTH));

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(tbGruppen, tfAccountVorsatz, cbAktionsId, chbBuendelProdukt,
                chbBuendelHauptauftrag, chbElVerlauf, chbBARuecklaeufer, cbEndstellenTyp,
                chbAuftrErstellen, chbVpnPhysik, chbProjektierung,
                cbLiNr, cbVertDurch, chbVierDraht, cbVerwendungszweckV4,
                chbVerwendungszweckEditable, tfNetzmaskeV4, tfNetzmaskeV6, chbNetzmaskeEditable,
                rfProjChainId, rfVerlChainId, rfVerlCancelChainId, cbIPPool, chbAutoPossible, chbNoAutoHvtZuordnung,
                chbAutoSmsVersand,tfAftrAddress, tfPbitDaten, tfPbitVoip);
        // @formatter:on
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ISimpleFindService sf = getCCService(QueryCCService.class);
            rfProjChainId.setFindService(sf);
            rfVerlChainId.setFindService(sf);
            rfVerlCancelChainId.setFindService(sf);

            // Modelle fuer Endstellen-Typen laden
            loadEndstellenTypen();
            // Modelle fuer Aktions-IDs erzeugen
            loadAktionsIds();
            // Modelle fuer LiNr erzeugen
            loadLiNrs();
            // Aenderungsgruppen laden
            loadAenderungsGruppen();
            // Abteilungen laden
            loadAbteilungen();
            // IP-Pools laden
            loadIPPools();

            loadIpPurposeV4();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof Produkt) ? (Produkt) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        // Lösche alle Einträge
        clearAll();

        // Lade Daten aus Model in GUI-Komponenten
        if (model != null) {
            tfAccountVorsatz.setText(model.getAccountVorsatz());
            short aktionsId = (model.getAktionsId() != null) ? model.getAktionsId() : 0;
            cbAktionsId.selectItem("getId", SimpleHelperModel.class,Integer.valueOf(aktionsId));
            chbBuendelProdukt.setSelected(model.getBuendelProdukt());
            chbBuendelHauptauftrag.setSelected(model.getBuendelBillingHauptauftrag());
            chbElVerlauf.setSelected(model.getElVerlauf());
            chbBARuecklaeufer.setSelected(model.getBaRuecklaeufer());
            cbEndstellenTyp.selectItem("getId", SimpleHelperModel.class, model.getEndstellenTyp());
            chbAuftrErstellen.setSelected(model.getAuftragserstellung());
            chbVpnPhysik.setSelected(model.getVpnPhysik());
            chbProjektierung.setSelected(model.getProjektierung());
            cbLiNr.selectItem("getId", SimpleHelperModel.class, model.getLiNr());
            rfProjChainId.setReferenceId(model.getProjektierungChainId());
            rfVerlChainId.setReferenceId(model.getVerlaufChainId());
            rfVerlCancelChainId.setReferenceId(model.getVerlaufCancelChainId());
            cbVertDurch.selectItem("getId", Abteilung.class, model.getVerteilungDurch());
            chbVierDraht.setSelected(model.getIsVierDraht());
            cbIPPool.selectItem("getLongId", SimpleHelperModel.class, model.getIpPool());
            cbGeoIdSource.selectItemRaw(model.getGeoIdSource());
            tfAftrAddress.setText(model.getAftrAddress());
            tfPbitDaten.setText(model.getPbitDaten());
            tfPbitVoip.setText(model.getPbitVoip());
            Reference verwendungszweckV4 = model.getIpPurposeV4();
            if (verwendungszweckV4 != null) {
                cbVerwendungszweckV4.selectItem("getStrValue", Reference.class, verwendungszweckV4.getStrValue());
            }
            chbVerwendungszweckEditable.setSelected(model.getIpPurposeV4Editable());
            tfNetzmaskeV4.setValue(model.getIpNetmaskSizeV4());
            tfNetzmaskeV6.setValue(model.getIpNetmaskSizeV6());
            chbNetzmaskeEditable.setSelected(model.getIpNetmaskSizeEditable());
            chbAutoPossible.setSelected(model.getAutomationPossible());
            chbNoAutoHvtZuordnung.setSelected(BooleanUtils.negate(model.getAutoHvtZuordnung()));
            chbAutoSmsVersand.setSelected(model.getSmsVersand());

            // zugeordnete Aenderungsgruppen laden
            try {
                BAConfigService service = getCCService(BAConfigService.class);
                List<BAVerlaufAG2Produkt> baGruppen = service.findBAVAG4Produkt(model.getId());
                if (baGruppen != null) {
                    for (BAVerlaufAG2Produkt element : baGruppen) {
                        aenderungsGruppen.add(element.getBaVerlaufAenderungGruppeId());
                    }
                }
                tbMdlGruppen.fireTableDataChanged();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }

        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            // BAV-Aenderungsgruppen speichern
            BAConfigService baService = getCCService(BAConfigService.class);
            Collection<Long> ag2p = (aenderungsGruppen != null) ? aenderungsGruppen : new HashSet<Long>();
            baService.saveBAVAG4Produkt(model.getId(), ag2p);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        // Daten aus GUI-Komponenten in Model setzen
        model.setAccountVorsatz(tfAccountVorsatz.getText());
        model.setAktionsId((cbAktionsId.getSelectedItem() instanceof SimpleHelperModel)
                ? ((SimpleHelperModel) cbAktionsId.getSelectedItem()).getId().shortValue() : null);
        model.setBuendelProdukt(chbBuendelProdukt.isSelected());
        model.setBuendelBillingHauptauftrag(chbBuendelHauptauftrag.isSelected());
        model.setElVerlauf(chbElVerlauf.isSelected());
        model.setBaRuecklaeufer(chbBARuecklaeufer.isSelected());
        model.setEndstellenTyp((cbEndstellenTyp.getSelectedItem() instanceof SimpleHelperModel)
                ? ((SimpleHelperModel) cbEndstellenTyp.getSelectedItem()).getId() : null);
        model.setAuftragserstellung(chbAuftrErstellen.isSelected());
        model.setVpnPhysik(chbVpnPhysik.isSelected());
        model.setProjektierung(chbProjektierung.isSelected());
        model.setLiNr((cbLiNr.getSelectedItem() instanceof SimpleHelperModel)
                ? ((SimpleHelperModel) cbLiNr.getSelectedItem()).getId() : null);
        model.setProjektierungChainId((rfProjChainId.getReferenceId() instanceof Long)
                ? (Long) rfProjChainId.getReferenceId() : null);
        model.setVerlaufChainId((rfVerlChainId.getReferenceId() instanceof Long)
                ? (Long) rfVerlChainId.getReferenceId() : null);
        model.setVerlaufCancelChainId((rfVerlCancelChainId.getReferenceId() instanceof Long)
                ? (Long) rfVerlCancelChainId.getReferenceId() : null);
        model.setVerteilungDurch(((Abteilung) cbVertDurch.getSelectedItem()).getId());
        model.setIsVierDraht(chbVierDraht.isSelected());
        model.setIpPool((cbIPPool.getSelectedItem() instanceof SimpleHelperModel)
                ? ((SimpleHelperModel) cbIPPool.getSelectedItem()).getLongId() : null);
        Reference purpose = (Reference) cbVerwendungszweckV4.getSelectedItem();
        model.setIpPurposeV4(((purpose != null) && (purpose.getId() != null)) ? purpose : null);
        model.setIpPurposeV4Editable(chbVerwendungszweckEditable.isSelected());
        model.setIpNetmaskSizeEditable(chbNetzmaskeEditable.isSelected());
        model.setIpNetmaskSizeV4(tfNetzmaskeV4.getValueAsInt(null));
        model.setIpNetmaskSizeV6(tfNetzmaskeV6.getValueAsInt(null));
        model.setAutomationPossible(chbAutoPossible.isSelected());
        model.setGeoIdSource((GeoIdSource) cbGeoIdSource.getSelectedItem());
        model.setAftrAddress(tfAftrAddress.getText());
        model.setAutoHvtZuordnung(!chbNoAutoHvtZuordnung.isSelected());
        model.setSmsVersand(chbAutoSmsVersand.isSelected());
        model.setPbitDaten(tfPbitDaten.getTextAsInt(null));
        model.setPbitVoip(tfPbitVoip.getTextAsInt(null));

        return model;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Erzeugt Modelle fuer die Aktions-Ids ComboBox */
    private void loadAktionsIds() {
        SimpleHelperModel m1 = new SimpleHelperModel(0, "kein Zusammenhang (0)");
        SimpleHelperModel m2 = new SimpleHelperModel(1, "1 : 1 (1)");
        SimpleHelperModel m3 = new SimpleHelperModel(2, "Anzahl unerheblich (2)");
        DefaultComboBoxModel cbMdl = new DefaultComboBoxModel(new Object[] { m1, m2, m3 });
        cbAktionsId.setModel(cbMdl);
    }

    /* Erzeugt die Modelle fuer die Endstellen-Typen ComboBox */
    private void loadEndstellenTypen() {
        SimpleHelperModel m1 = new SimpleHelperModel(Produkt.ES_TYP_KEINE_ENDSTELLEN, "keine Endstellen (0)");
        SimpleHelperModel m2 = new SimpleHelperModel(Produkt.ES_TYP_NUR_B, "nur Endstelle B (1)");
        SimpleHelperModel m3 = new SimpleHelperModel(Produkt.ES_TYP_A_UND_B, "Endstellen A+B (2)");
        DefaultComboBoxModel cbMdl = new DefaultComboBoxModel(new Object[] { m1, m2, m3 });
        cbEndstellenTyp.setModel(cbMdl);
    }

    /* Erzeugt die Modelle fuer die LiNrs ComboBox */
    private void loadLiNrs() {
        SimpleHelperModel m0 = new SimpleHelperModel(" ");
        SimpleHelperModel m1 = new SimpleHelperModel(IntAccount.LINR_ABRECHNUNGSACCOUNT, "Abrechnungsaccount (0)");
        SimpleHelperModel m2 = new SimpleHelperModel(IntAccount.LINR_EINWAHLACCOUNT, "Einwahlaccount (1)");
        SimpleHelperModel m3 = new SimpleHelperModel(IntAccount.LINR_VERWALTUNGSACCOUNT, "Verwaltungsaccount (2)");
        SimpleHelperModel m4 = new SimpleHelperModel(IntAccount.LINR_EINWAHLACCOUNT_KONFIG, "Einwahlaccount INTERN (4)");
        DefaultComboBoxModel cbMdl = new DefaultComboBoxModel(new Object[] { m0, m1, m2, m3, m4 });
        cbLiNr.setModel(cbMdl);
    }

    /* Laedt alle verfuegbaren Bauauftrag-Verlaufs-Aenderungsgruppen. */
    private void loadAenderungsGruppen() throws ServiceNotFoundException, FindException {
        BAConfigService service = getCCService(BAConfigService.class);
        List<BAVerlaufAenderungGruppe> baGruppen = service.findBAVerlaufAenderungGruppen();
        tbMdlGruppen.setData(baGruppen);
    }

    /* Laedt alle verfuegbaren Abteilungen */
    private void loadAbteilungen() throws ServiceNotFoundException, FindException {
        NiederlassungService ns = getCCService(NiederlassungService.class);
        List<Abteilung> abteilung = ns.findAbteilungen();
        cbVertDurch.addItems(abteilung, true, Abteilung.class);
    }

    /* Erzeugt die Modelle fuer die LiNrs ComboBox */
    private void loadIPPools() {
        SimpleHelperModel[] simpleHelperModels = null;
        IPPoolType[] ipPoolValues = IPPoolType.values();
        if ((ipPoolValues != null) && (ipPoolValues.length > 0)) {
            simpleHelperModels = new SimpleHelperModel[ipPoolValues.length];
            for (int i = 1; i < ipPoolValues.length; i++) {
                simpleHelperModels[i] = new SimpleHelperModel(ipPoolValues[i].getId(), ipPoolValues[i].name());
            }
        }
        else {
            simpleHelperModels = new SimpleHelperModel[1];
        }
        simpleHelperModels[0] = new SimpleHelperModel(" ");
        DefaultComboBoxModel cbMdl = new DefaultComboBoxModel(simpleHelperModels);
        cbIPPool.setModel(cbMdl);
    }

    private void loadIpPurposeV4() throws ServiceNotFoundException, FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        List<Reference> ipPurposes = referenceService.findReferencesByType(Reference.REF_TYPE_IP_PURPOSE_TYPE_V4, true);
        cbVerwendungszweckV4.removeAllItems();
        if (CollectionTools.isNotEmpty(ipPurposes)) {
            cbVerwendungszweckV4.addItems(ipPurposes, true, Reference.class);
            cbVerwendungszweckV4.setSelectedIndex(0);
        }
    }

    /* 'Loescht' alle Felder */
    private void clearAll() {
        GuiTools.cleanFields(this);
        aenderungsGruppen = new HashSet<>();
        tbMdlGruppen.fireTableDataChanged();
    }

    /**
     * TableModel fuer die Darstellung der Bauauftragsverlauf-Aenderungsgruppen.
     */
    class BAGruppenTableModel extends AKTableModel {
        static final int COL_NAME = 0;
        static final int COL_USE = 1;

        static final int COL_COUNT = 2;
        private static final long serialVersionUID = -817040804140822978L;

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
                case COL_NAME:
                    return "Name";
                case COL_USE:
                    return "verwenden";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof BAVerlaufAenderungGruppe) {
                BAVerlaufAenderungGruppe ag = (BAVerlaufAenderungGruppe) o;
                switch (column) {
                    case COL_NAME:
                        return ag.getBeschreibung();
                    case COL_USE:
                        return (((aenderungsGruppen != null) && aenderungsGruppen.contains(ag.getId()))
                                ? Boolean.TRUE
                                : Boolean.FALSE);
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof BAVerlaufAenderungGruppe) {
                BAVerlaufAenderungGruppe ag = (BAVerlaufAenderungGruppe) o;
                if (aenderungsGruppen == null) {
                    aenderungsGruppen = new HashSet<>();
                }

                if (aValue instanceof Boolean) {
                    if (Boolean.TRUE.equals(aValue)) {
                        aenderungsGruppen.add(ag.getId());
                    }
                    else {
                        aenderungsGruppen.remove(ag.getId());
                    }
                }
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == COL_USE;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_USE) ? Boolean.class : super.getColumnClass(columnIndex);
        }
    }

}
