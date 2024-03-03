/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2012 10:41:13
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.JFormattedTextField.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.shared.FindUserDialog;
import de.augustakom.hurrican.gui.tools.tal.WiedervorlageDialog;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgang.AbmState;

/**
 * Panel mit dem AM-Clearing Tool für Änderung der CBVorgang bzw. Tam-UserTask
 */
public class WitaClearingToolsPanel extends AbstractServicePanel implements AKDataLoaderComponent, AKSearchComponent {

    private static final long serialVersionUID = 8317266929800892257L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/WitaClearingToolsPanel.xml";

    private static final Logger LOGGER = Logger.getLogger(WitaClearingToolsPanel.class);

    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private static final Map<Long, Reference> statusMap = new HashMap<>();

    private static final String BORDER_TITLE_FILTER_PANEL = "border.title.filter.panel";
    private static final String BORDER_TITLE_CB_VORGANG_PANEL = "border.title.cbVorgang.panel";
    private static final String BORDER_TITLE_TAM_USERTASK_PANEL = "border.title.TAMUsertask.panel";
    private static final String SEARCH_EXTERNE_AUFTRAGSNUMMER = "search.externe.auftragsnummer";
    private static final String SEARCH_BUTTON = "search.button";
    private static final String SAVE_BUTTON = "save.button";
    private static final String ID_CB_VORGANG = "id.cbVorgang";
    private static final String AUFTRAG_ID_CB_VORGANG = "auftrag.id.cbVorgang";
    private static final String CARRIER_REF_NUMMER_CB_VORGANG = "carrier.ref.nummer.cbVorgang";
    private static final String TYP_CB_VORGANG = "typ.cbVorgang";
    private static final String WITA_GESCHAEFTSFALL_TYP_CB_VORGANG = "wita.geschaeftsfall.typ.cbVorgang";
    private static final String WITA_AENDERUNGSLENNZEICHEN_CB_VORGANG = "wita.aenderungskennzeichen.cbVorgang";
    private static final String STATUS_CB_VORGANG = "status.cbVorgang";
    private static final String ABM_STATUS_CB_VORGANG = "abm.status.cbVorgang";
    private static final String BEARBEITER_CB_VORGANG = "bearbeiter.cbVorgang";
    private static final String EDIT_BEARBEITER_CB_VORGANG = "edit.bearbeiter.cbVorgang";
    private static final String ANTWORT_DATUM_CB_VORGANG = "antwort.datum.cbVorgang";
    private static final String RUECKMELDUNG_OK_CB_VORGANG = "rueckmeldung.ok.cbVorgang";
    private static final String RUECKMELDUNG_LBZ_CB_VORGANG = "rueckmeldung.lbz.cbVorgang";
    private static final String RUECKMELDUNG_VERTRAGSNUMMER_CB_VORGANG = "rueckmeldung.vetragsnummer.cbVorgang";
    private static final String RUECKMELDUNG_AQS_CB_VORGANG = "rueckmeldung.aqs.cbVorgang";
    private static final String RUECKMELDUNG_LEISTUNGSLAENGE_CB_VORGANG = "rueckmeldung.leistungslaenge.cbVorgang";
    private static final String RUECKMELDUNG_KUNDE_VOR_ORT_CB_VORGANG = "rueckmeldung.kunde.vor.ort.cbVorgang";
    private static final String RUECKMELDUNG_REAL_DATUM_CB_VORGANG = "rueckmeldung.real.datum.cbVorgang";
    private static final String RUECKMELDUNG_BEMERKUNG_CB_VORGANG = "rueckmeldung.bemerkung.cbVorgang";
    private static final String MAX_BRUTTO_BITRATE_CB_VORGANG = "max.brutto.bitrate.cbVorgang";
    private static final String CARRIER_BEARBEITER_CB_VORGANG = "carrier.bearbeiter.cbVorgang";
    private static final String CARRIER_KENNUNG_ABS_CB_VORGANG = "carrier.kennung.abs.cbVorgang";
    private static final String FRUEHRES_UETV_CB_VORGANG = "frueheres.uetv.cbVorgang";
    private static final String BEMERKUNG_STATUS_CB_VORGANG = "bemerkung.status.cbVorgang";
    private static final String WIEDERVORLAGE_AM_CB_VORGANG = "wiedervorlage.am.cbVorgang";
    private static final String ID_TAM_USERTASK = "id.tam.usertask";
    private static final String BEARBEITER_TAM_USERTASK = "bearbeiter.tam.usertask";
    private static final String LETZTE_AENDERUNG_DATUM_TAM_USERTASK = "letzte.aenderung.datum.tam.usertask";
    private static final String TV_60_SENT_TAM_USERTASK = "tv.60.sent.tam.usertask";
    private static final String STATUS_TAM_USERTASK = "status.tam.usertask";
    private static final String BEARBEITUNG_STATUS_TAM_USERTASK = "bearbeitung.status.tam.usertask";
    private static final String BEMERKUNG_TAM_USERTASK = "bemerkung.tam.usertask";
    private static final String MAHN_TAM = "mahn.tam";
    private static final String WIEDERVORLAGE_AM_TAM_USERTASK = "wiedervorlage.am.tam.usertask";
    private static final String NO_EXT_AUFTRAGSNUMMER = "no.ext.auftragsnummer";
    private static final String NO_CB_VORGANG_FOR_EXT_AUFTRAGSNUMMER = "no.cbVorgang.for.ext.auftragsnummer";
    private static final String MULTIPLE_CB_VORGANG_FOR_EXT_AUFTRAGSNUMMER = "multiple.cbVorgang.for.ext.auftragsnummer";
    private static final String EDIT_WIEDERVORLAGE_AM_CB_VORGANG = "edit.wiedervorlage.am.cbVorgang";
    private static final String EDIT_WIEDERVORLAGE_AM_TAM_USERTASK = "edit.wiedervorlage.am.tam.usertask";

    private AKJTextField tfExterneAuftragsnummer;
    private AKJTextField tfIdCbVorgang;
    private AKJTextField tfAuftragIdCbVorgang;
    private AKJTextField tfCarrierRefNr;
    private AKJTextField tfTypCbVorgang;
    private AKJTextField tfWitaGeschaeftsfallTyp;
    private AKJTextField tfWitaAenderungskennzeichen;
    private AKJComboBox cbStatusCbVorgang;
    private AKJComboBox cbABMStatus;
    private AKJFormattedTextField tfBearbeiterCbVorgang;
    private AKJDateComponent dcAntwortDatumCbVorgang;
    private AKJCheckBox chbRueckmeldungOk;
    private AKJTextField tfRueckmeldungLBZ;
    private AKJTextField tfRueckmeldungVertragsnummer;
    private AKJTextField tfRueckmeldungAQS;
    private AKJTextField tfRueckmeldungLL;
    private AKJCheckBox chbRueckmeldungKundeVorOrt;
    private AKJDateComponent dcRueckmeldungRealDatum;
    private AKJTextArea taRueckmeldungBemerkung;
    private AKJTextField tfMaxBruttoBitrate;
    private AKJTextField tfCarrierBearbeiter;
    private AKJTextField tfCarrierKennungABS;
    private AKJComboBox cbPreUetv;
    private AKJTextArea taBemerkungStatus;
    private AKJTextField tfWiedervorlageAmCbVorgang;
    private AKJTextField tfIdTAMUsertask;
    private AKJTextField tfBearbeiterTAMUsertask;
    private AKJTextField tfLetzteAenderungTAM;
    private AKJCheckBox chbTV60Sent;
    private AKJComboBox cbStatusTAMUsertask;
    private AKJComboBox cbBearbeitungStatusTAMUsertask;
    private AKJTextArea taBemerkungTAMUsertask;
    private AKJCheckBox chbMahnTAM;
    private AKJTextField tfWiedervorlageAmTAMUsertask;

    private CarrierElTALService carrierService;
    private ReferenceService refService;

    private CBVorgang cbVorgang = null;

    public WitaClearingToolsPanel() {
        super(RESOURCE);
        try {
            createGUI();
            initServices();
            loadDefaultData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    private void initServices() throws ServiceNotFoundException {
        carrierService = getCCService(CarrierElTALService.class);
        refService = getCCService(ReferenceService.class);
    }

    @Override
    protected final void createGUI() {
        AKSearchKeyListener searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        AKJLabel lblExterneAuftragsnummer = getSwingFactory().createLabel(SEARCH_EXTERNE_AUFTRAGSNUMMER);
        tfExterneAuftragsnummer = getSwingFactory().createTextField(SEARCH_EXTERNE_AUFTRAGSNUMMER, true, true,
                searchKeyListener);
        AKJButton btnSearch = getSwingFactory().createButton(SEARCH_BUTTON, getActionListener());

        // @formatter:off
        AKJPanel filterPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(BORDER_TITLE_FILTER_PANEL));
        filterPnl.add(new AKJPanel()           , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(lblExterneAuftragsnummer , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel()           , GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(tfExterneAuftragsnummer  , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(btnSearch                , GBCFactory.createGBC(  0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL, 20));
        filterPnl.add(new AKJPanel()           , GBCFactory.createGBC(100, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        AKJLabel lblIdCbVorgang = getSwingFactory().createLabel(ID_CB_VORGANG);
        tfIdCbVorgang = getSwingFactory().createTextField(ID_CB_VORGANG, false);
        tfIdCbVorgang.setSize(new Dimension(20, 1));
        AKJLabel lblAuftragIdCbVorgang = getSwingFactory().createLabel(AUFTRAG_ID_CB_VORGANG);
        tfAuftragIdCbVorgang = getSwingFactory().createTextField(AUFTRAG_ID_CB_VORGANG, false);
        AKJLabel lblCarrierRefNr = getSwingFactory().createLabel(CARRIER_REF_NUMMER_CB_VORGANG);
        tfCarrierRefNr = getSwingFactory().createTextField(CARRIER_REF_NUMMER_CB_VORGANG, false);
        AKJLabel lblTypCbVorgang = getSwingFactory().createLabel(TYP_CB_VORGANG);
        tfTypCbVorgang = getSwingFactory().createTextField(TYP_CB_VORGANG, false);
        AKJLabel lblWitaGeschaeftsfallTyp = getSwingFactory().createLabel(WITA_GESCHAEFTSFALL_TYP_CB_VORGANG);
        tfWitaGeschaeftsfallTyp = getSwingFactory().createTextField(WITA_GESCHAEFTSFALL_TYP_CB_VORGANG, false);
        AKJLabel lblWitaAenderungskennzeichen = getSwingFactory().createLabel(WITA_AENDERUNGSLENNZEICHEN_CB_VORGANG);
        tfWitaAenderungskennzeichen = getSwingFactory().createTextField(WITA_AENDERUNGSLENNZEICHEN_CB_VORGANG, false);
        AKJLabel lblStatusCbVorgang = getSwingFactory().createLabel(STATUS_CB_VORGANG);
        cbStatusCbVorgang = getSwingFactory().createComboBox(STATUS_CB_VORGANG);
        AKJLabel lblABMStatus = getSwingFactory().createLabel(ABM_STATUS_CB_VORGANG);
        cbABMStatus = getSwingFactory().createComboBox(ABM_STATUS_CB_VORGANG);
        AKJLabel lblBearbeiterCbVorgang = getSwingFactory().createLabel(BEARBEITER_CB_VORGANG);
        tfBearbeiterCbVorgang = getSwingFactory().createFormattedTextField(BEARBEITER_CB_VORGANG, false);
        tfBearbeiterCbVorgang.setFormatterFactory(new AbstractFormatterFactory() {
            @Override
            public AbstractFormatter getFormatter(JFormattedTextField tf) {
                return new AbstractFormatter() {
                    private static final long serialVersionUID = -4085447561631042390L;

                    @Override
                    public String valueToString(Object value) throws ParseException {
                        if (value instanceof AKUser) {
                            return ((AKUser) value).getLoginName();
                        }
                        return null;
                    }

                    @Override
                    public Object stringToValue(String text) throws ParseException {
                        return null;
                    }
                };
            }
        });
        AKJButton bBearbeiterCbVorgangEdit = getSwingFactory().createButton(EDIT_BEARBEITER_CB_VORGANG, getActionListener());
        AKJPanel pBearbeiter = new AKJPanel(new BorderLayout());
        int h = tfBearbeiterCbVorgang.getPreferredSize().height;
        bBearbeiterCbVorgangEdit.setPreferredSize(new Dimension(h, h));
        pBearbeiter.add(tfBearbeiterCbVorgang);
        pBearbeiter.add(bBearbeiterCbVorgangEdit, BorderLayout.EAST);
        AKJLabel lblWiedervorlageAmCbVorgang = getSwingFactory().createLabel(WIEDERVORLAGE_AM_CB_VORGANG);
        tfWiedervorlageAmCbVorgang = getSwingFactory().createTextField(WIEDERVORLAGE_AM_CB_VORGANG);
        AKJLabel lblAntwortDatumCbVorgang = getSwingFactory().createLabel(ANTWORT_DATUM_CB_VORGANG);
        dcAntwortDatumCbVorgang = getSwingFactory().createDateComponent(ANTWORT_DATUM_CB_VORGANG, true);
        AKJLabel lblRueckmeldungOk = getSwingFactory().createLabel(RUECKMELDUNG_OK_CB_VORGANG);
        chbRueckmeldungOk = getSwingFactory().createCheckBox(RUECKMELDUNG_OK_CB_VORGANG);
        AKJLabel lblRueckmeldungLBZ = getSwingFactory().createLabel(RUECKMELDUNG_LBZ_CB_VORGANG);
        tfRueckmeldungLBZ = getSwingFactory().createTextField(RUECKMELDUNG_LBZ_CB_VORGANG);
        AKJLabel lblRueckmeldungVertragsnummer = getSwingFactory().createLabel(RUECKMELDUNG_VERTRAGSNUMMER_CB_VORGANG);
        tfRueckmeldungVertragsnummer = getSwingFactory().createTextField(RUECKMELDUNG_VERTRAGSNUMMER_CB_VORGANG);
        AKJLabel lblRueckmeldungAQS = getSwingFactory().createLabel(RUECKMELDUNG_AQS_CB_VORGANG);
        tfRueckmeldungAQS = getSwingFactory().createTextField(RUECKMELDUNG_AQS_CB_VORGANG);
        AKJLabel lblRueckmeldungLL = getSwingFactory().createLabel(RUECKMELDUNG_LEISTUNGSLAENGE_CB_VORGANG);
        tfRueckmeldungLL = getSwingFactory().createTextField(RUECKMELDUNG_LEISTUNGSLAENGE_CB_VORGANG);
        AKJLabel lblRueckmeldungKundeVorOrt = getSwingFactory().createLabel(RUECKMELDUNG_KUNDE_VOR_ORT_CB_VORGANG);
        chbRueckmeldungKundeVorOrt = getSwingFactory().createCheckBox(RUECKMELDUNG_KUNDE_VOR_ORT_CB_VORGANG);
        AKJLabel lblRueckmeldungRealDatum = getSwingFactory().createLabel(RUECKMELDUNG_REAL_DATUM_CB_VORGANG);
        dcRueckmeldungRealDatum = getSwingFactory().createDateComponent(RUECKMELDUNG_REAL_DATUM_CB_VORGANG, true);
        AKJLabel lblRueckmeldungBemerkung = getSwingFactory().createLabel(RUECKMELDUNG_BEMERKUNG_CB_VORGANG);
        taRueckmeldungBemerkung = getSwingFactory().createTextArea(RUECKMELDUNG_BEMERKUNG_CB_VORGANG);
        AKJScrollPane spRueckmeldungBemerkung = new AKJScrollPane(taRueckmeldungBemerkung);
        AKJLabel lblMaxBruttoBitrate = getSwingFactory().createLabel(MAX_BRUTTO_BITRATE_CB_VORGANG);
        tfMaxBruttoBitrate = getSwingFactory().createTextField(MAX_BRUTTO_BITRATE_CB_VORGANG);
        AKJLabel lblCarrierBearbeiter = getSwingFactory().createLabel(CARRIER_BEARBEITER_CB_VORGANG);
        tfCarrierBearbeiter = getSwingFactory().createTextField(CARRIER_BEARBEITER_CB_VORGANG);
        AKJLabel lblCarrierKennungABS = getSwingFactory().createLabel(CARRIER_KENNUNG_ABS_CB_VORGANG);
        tfCarrierKennungABS = getSwingFactory().createTextField(CARRIER_KENNUNG_ABS_CB_VORGANG);
        AKJLabel lblPreUetv = getSwingFactory().createLabel(FRUEHRES_UETV_CB_VORGANG);
        cbPreUetv = getSwingFactory().createComboBox(FRUEHRES_UETV_CB_VORGANG);
        AKJLabel lblBemerkungStatus = getSwingFactory().createLabel(BEMERKUNG_STATUS_CB_VORGANG);
        taBemerkungStatus = getSwingFactory().createTextArea(BEMERKUNG_STATUS_CB_VORGANG);
        AKJScrollPane spBemerkungStatus = new AKJScrollPane(taBemerkungStatus);
        AKJLabel lblIdTAMUsertask = getSwingFactory().createLabel(ID_TAM_USERTASK);
        tfIdTAMUsertask = getSwingFactory().createTextField(ID_TAM_USERTASK, false);
        AKJLabel lblBearbeiterTAMUsertask = getSwingFactory().createLabel(BEARBEITER_TAM_USERTASK);
        tfBearbeiterTAMUsertask = getSwingFactory().createTextField(BEARBEITER_TAM_USERTASK, false);
        AKJLabel lblLetzteAenderungTAM = getSwingFactory().createLabel(LETZTE_AENDERUNG_DATUM_TAM_USERTASK);
        tfLetzteAenderungTAM = getSwingFactory().createTextField(LETZTE_AENDERUNG_DATUM_TAM_USERTASK, false);
        AKJLabel lblTV60Sent = getSwingFactory().createLabel(TV_60_SENT_TAM_USERTASK);
        chbTV60Sent = getSwingFactory().createCheckBox(TV_60_SENT_TAM_USERTASK, false);
        AKJLabel lblWiedervorlageAmTAMUsertask = getSwingFactory().createLabel(WIEDERVORLAGE_AM_TAM_USERTASK);
        tfWiedervorlageAmTAMUsertask = getSwingFactory().createTextField(WIEDERVORLAGE_AM_TAM_USERTASK);
        AKJLabel lblStatusTAMUsertask = getSwingFactory().createLabel(STATUS_TAM_USERTASK);
        cbStatusTAMUsertask = getSwingFactory().createComboBox(STATUS_TAM_USERTASK);
        AKJLabel lblBearbeitungStatusTAMUsertask = getSwingFactory().createLabel(BEARBEITUNG_STATUS_TAM_USERTASK);
        cbBearbeitungStatusTAMUsertask = getSwingFactory().createComboBox(BEARBEITUNG_STATUS_TAM_USERTASK);
        AKJLabel lblMahnTAM = getSwingFactory().createLabel(MAHN_TAM);
        chbMahnTAM = getSwingFactory().createCheckBox(MAHN_TAM);
        AKJLabel lblBemerkungTAMUsertask = getSwingFactory().createLabel(BEMERKUNG_TAM_USERTASK);
        taBemerkungTAMUsertask = getSwingFactory().createTextArea(BEMERKUNG_TAM_USERTASK);
        AKJScrollPane spBemerkungTAMUsertask = new AKJScrollPane(taBemerkungTAMUsertask);
        spBemerkungTAMUsertask.setPreferredSize(new Dimension(100, 200));
        AKJButton btnEditWiedervorlageAmCbVorgang = getSwingFactory().createButton(EDIT_WIEDERVORLAGE_AM_CB_VORGANG,
                getActionListener());
        btnEditWiedervorlageAmCbVorgang.setEnabled(true);
        btnEditWiedervorlageAmCbVorgang.setVisible(true);
        btnEditWiedervorlageAmCbVorgang.setPreferredSize(new Dimension(20, 20));
        AKJButton btnEditWiedervorlageAmTamUsertask = getSwingFactory().createButton(EDIT_WIEDERVORLAGE_AM_TAM_USERTASK,
                getActionListener());
        btnEditWiedervorlageAmTamUsertask.setEnabled(true);
        btnEditWiedervorlageAmTamUsertask.setVisible(true);
        btnEditWiedervorlageAmTamUsertask.setPreferredSize(new Dimension(20, 20));

        // @formatter:off
        AKJPanel cbVorgangPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(BORDER_TITLE_CB_VORGANG_PANEL));
        int yCbVorgangPnl = 0;
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(  0,  0, 0,   yCbVorgangPnl, 1, 1, GridBagConstraints.NONE));
        cbVorgangPnl.add(lblIdCbVorgang                     , GBCFactory.createGBC(  0,  0, 1,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(  0,  0, 2,   yCbVorgangPnl, 1, 1, GridBagConstraints.NONE));
        cbVorgangPnl.add(tfIdCbVorgang                      , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(  0,  0, 4,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblAuftragIdCbVorgang              , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(  0,  0, 6,   yCbVorgangPnl, 1, 1, GridBagConstraints.NONE));
        cbVorgangPnl.add(tfAuftragIdCbVorgang               , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(100,  0, 9,   yCbVorgangPnl, 1, 1, GridBagConstraints.NONE));
        cbVorgangPnl.add(lblCarrierRefNr                    , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfCarrierRefNr                     , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblTypCbVorgang                    , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfTypCbVorgang                     , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblWitaGeschaeftsfallTyp           , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfWitaGeschaeftsfallTyp            , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblWitaAenderungskennzeichen       , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfWitaAenderungskennzeichen        , GBCFactory.createGBC(  0,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(100, 30, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.VERTICAL));
        cbVorgangPnl.add(lblStatusCbVorgang                 , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(cbStatusCbVorgang                  , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblABMStatus                       , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(cbABMStatus                        , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblBearbeiterCbVorgang             , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(pBearbeiter                        , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblWiedervorlageAmCbVorgang        , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfWiedervorlageAmCbVorgang         , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(btnEditWiedervorlageAmCbVorgang    , GBCFactory.createGBC(  0,  0, 8,   yCbVorgangPnl, 1, 1, GridBagConstraints.NONE));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(100, 30, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.VERTICAL));
        cbVorgangPnl.add(lblAntwortDatumCbVorgang           , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(dcAntwortDatumCbVorgang            , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungRealDatum           , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(dcRueckmeldungRealDatum            , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungVertragsnummer      , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfRueckmeldungVertragsnummer       , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungLBZ                 , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfRueckmeldungLBZ                  , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungLL                  , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfRueckmeldungLL                   , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungAQS                 , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfRueckmeldungAQS                  , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungKundeVorOrt         , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(chbRueckmeldungKundeVorOrt         , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungOk                  , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(chbRueckmeldungOk                  , GBCFactory.createGBC(  0,  0, 7,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblRueckmeldungBemerkung           , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(spRueckmeldungBemerkung            , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 6, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(100, 10, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.VERTICAL));
        cbVorgangPnl.add(lblMaxBruttoBitrate                , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfMaxBruttoBitrate                 , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblCarrierBearbeiter               , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfCarrierBearbeiter                , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblCarrierKennungABS               , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(tfCarrierKennungABS                , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblPreUetv                         , GBCFactory.createGBC(  0,  0, 5,   yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(cbPreUetv                          , GBCFactory.createGBC(100,  0, 7,   yCbVorgangPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(lblBemerkungStatus                 , GBCFactory.createGBC(  0,  0, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(spBemerkungStatus                  , GBCFactory.createGBC(100,  0, 3,   yCbVorgangPnl, 6, 1, GridBagConstraints.HORIZONTAL));
        cbVorgangPnl.add(new AKJPanel()                     , GBCFactory.createGBC(  0,100, 1, ++yCbVorgangPnl, 1, 1, GridBagConstraints.BOTH));

        AKJPanel tamUsertaskPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(BORDER_TITLE_TAM_USERTASK_PANEL));
        int yTamUsertaskPnl = 0;
        tamUsertaskPnl.add(new AKJPanel()                   , GBCFactory.createGBC(  0,  0, 0,   yTamUsertaskPnl, 1, 1, GridBagConstraints.NONE));
        tamUsertaskPnl.add(lblIdTAMUsertask                 , GBCFactory.createGBC(  0,  0, 1,   yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(new AKJPanel()                   , GBCFactory.createGBC(  0,  0, 2,   yTamUsertaskPnl, 1, 1, GridBagConstraints.NONE));
        tamUsertaskPnl.add(tfIdTAMUsertask                  , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(new AKJPanel()                   , GBCFactory.createGBC(100,  0, 5,   yTamUsertaskPnl, 1, 1, GridBagConstraints.NONE));
        tamUsertaskPnl.add(lblBearbeiterTAMUsertask         , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(tfBearbeiterTAMUsertask          , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(lblLetzteAenderungTAM            , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(tfLetzteAenderungTAM             , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(lblTV60Sent                      , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(chbTV60Sent                      , GBCFactory.createGBC(  0,  0, 3,   yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(new AKJPanel()                   , GBCFactory.createGBC(100, 30, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.VERTICAL));
        tamUsertaskPnl.add(lblWiedervorlageAmTAMUsertask    , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(tfWiedervorlageAmTAMUsertask     , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(btnEditWiedervorlageAmTamUsertask, GBCFactory.createGBC(  0,  0, 4,   yTamUsertaskPnl, 1, 1, GridBagConstraints.NONE));
        tamUsertaskPnl.add(lblStatusTAMUsertask             , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(cbStatusTAMUsertask              , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(lblBearbeitungStatusTAMUsertask  , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(cbBearbeitungStatusTAMUsertask   , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(lblMahnTAM                       , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(chbMahnTAM                       , GBCFactory.createGBC(  0,  0, 3,   yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(lblBemerkungTAMUsertask          , GBCFactory.createGBC(  0,  0, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(spBemerkungTAMUsertask           , GBCFactory.createGBC(100,  0, 3,   yTamUsertaskPnl, 2, 1, GridBagConstraints.HORIZONTAL));
        tamUsertaskPnl.add(new AKJPanel()                   , GBCFactory.createGBC(  0,100, 1, ++yTamUsertaskPnl, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        AKJButton btnSave = getSwingFactory().createButton(SAVE_BUTTON, getActionListener());

        // @formatter:off
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnSave                                  , GBCFactory.createGBC( 0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel()                           , GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(filterPnl                                  , GBCFactory.createGBC(100,  0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()                             , GBCFactory.createGBC(100,  0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(cbVorgangPnl                               , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.BOTH));
        this.add(tamUsertaskPnl                             , GBCFactory.createGBC(100,100, 1, 2, 1, 1, GridBagConstraints.BOTH));
        this.add(btnPnl                                     , GBCFactory.createGBC(  0,  0, 0, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()                             , GBCFactory.createGBC(100,  0, 0, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void loadDefaultData() {
        try {
            List<Reference> statiRef = refService.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_STATUS,
                    Boolean.FALSE);
            CollectionMapConverter.convert2Map(statiRef, statusMap, "getId", null);
            List<String> stati = new ArrayList<>();
            for (Map.Entry<Long, Reference> status : statusMap.entrySet()) {
                stati.add(status.getValue().getStrValue());
            }
            cbStatusCbVorgang.addItems(stati);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e, false);
        }

        cbABMStatus.addItems(EnumSet.allOf(AbmState.class), true);
        cbStatusTAMUsertask.addItems(EnumSet.allOf(UserTaskStatus.class), true);
        cbBearbeitungStatusTAMUsertask.addItems(EnumSet.allOf(TamBearbeitungsStatus.class), true);

        cbPreUetv.addItem("");
        cbPreUetv.addItems(EnumSet.allOf(Uebertragungsverfahren.class), true);
    }

    @Override
    public void doSearch() {
        String externeAuftragsnummer = tfExterneAuftragsnummer.getText();
        if (cbVorgang != null) {
            clearData();
            tfExterneAuftragsnummer.setText(externeAuftragsnummer);
        }
        if (StringUtils.isBlank(externeAuftragsnummer)) {
            MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText(NO_EXT_AUFTRAGSNUMMER), null, null,
                    true);
            return;
        }
        try {
            cbVorgang = carrierService.findCBVorgangByCarrierRefNr(externeAuftragsnummer);
            loadData();
        }
        catch (NoSuchElementException e) {
            MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory()
                    .getText(NO_CB_VORGANG_FOR_EXT_AUFTRAGSNUMMER), null, null, true);
        }
        catch (IllegalArgumentException e) {
            MessageHelper.showInfoDialog(getMainFrame(),
                    getSwingFactory().getText(MULTIPLE_CB_VORGANG_FOR_EXT_AUFTRAGSNUMMER), null, null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        if (cbVorgang != null) {
            tfIdCbVorgang.setText(cbVorgang.getId());
            tfAuftragIdCbVorgang.setText(cbVorgang.getAuftragId());
            tfCarrierRefNr.setText(cbVorgang.getCarrierRefNr());
            cbStatusCbVorgang.setSelectedItem(statusMap.get(cbVorgang.getStatus()).getStrValue());
            tfBearbeiterCbVorgang.setValue(cbVorgang.getBearbeiter());
            dcAntwortDatumCbVorgang.setDate(cbVorgang.getAnsweredAt());
            dcRueckmeldungRealDatum.setDate(cbVorgang.getReturnRealDate());
            tfRueckmeldungVertragsnummer.setText(cbVorgang.getReturnVTRNR());
            tfRueckmeldungLBZ.setText(cbVorgang.getReturnLBZ());
            tfRueckmeldungLL.setText(cbVorgang.getReturnLL());
            tfRueckmeldungAQS.setText(cbVorgang.getReturnAQS());
            chbRueckmeldungKundeVorOrt.setSelected(cbVorgang.getReturnKundeVorOrt());
            chbRueckmeldungOk.setSelected(cbVorgang.getReturnOk());
            taRueckmeldungBemerkung.setText(cbVorgang.getReturnBemerkung());
            tfMaxBruttoBitrate.setText(cbVorgang.getReturnMaxBruttoBitrate());
            tfCarrierBearbeiter.setText(cbVorgang.getCarrierBearbeiter());
            tfCarrierKennungABS.setText(cbVorgang.getCarrierKennungAbs());
            taBemerkungStatus.setText(cbVorgang.getStatusBemerkung());
            if (cbVorgang.getWiedervorlageAm() != null) {
                tfWiedervorlageAmCbVorgang.setText(cbVorgang.getWiedervorlageAmAsLocalDateTime().format(DateTimeFormatter.ofPattern(("dd.MM.YYYY HH:mm"))));
            }
            try {
                List<Reference> typen = refService.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_TYP,
                        Boolean.FALSE);
                Map<Long, Reference> typMap = new HashMap<>();
                CollectionMapConverter.convert2Map(typen, typMap, "getId", null);
                tfTypCbVorgang.setText(typMap.get(cbVorgang.getTyp()).getStrValue());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e, false);
            }

            if (cbVorgang instanceof WitaCBVorgang) {
                WitaCBVorgang witaCbVorgang = (WitaCBVorgang) cbVorgang;
                tfWitaGeschaeftsfallTyp.setText(witaCbVorgang.getWitaGeschaeftsfallTyp().getDisplayName());
                tfWitaAenderungskennzeichen.setText(witaCbVorgang.getAenderungsKennzeichen().getValue());
                cbABMStatus.setSelectedItem(witaCbVorgang.getAbmState());
                cbPreUetv.setSelectedItem(witaCbVorgang.getPreviousUebertragungsVerfahren());
                TamUserTask tamUserTask = witaCbVorgang.getTamUserTask();
                if (tamUserTask != null) {
                    tfIdTAMUsertask.setText(tamUserTask.getId());
                    tfBearbeiterTAMUsertask.setText(tamUserTask.getBearbeiter() != null ? tamUserTask.getBearbeiter()
                            .getLoginName() : null);
                    tfLetzteAenderungTAM.setText(Instant.ofEpochMilli(tamUserTask.getLetzteAenderung().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                    chbTV60Sent.setSelected(tamUserTask.isTv60Sent());
                    cbStatusTAMUsertask.setSelectedItem(tamUserTask.getStatus());
                    cbBearbeitungStatusTAMUsertask.setSelectedItem(tamUserTask.getTamBearbeitungsStatus());
                    chbMahnTAM.setSelected(tamUserTask.isMahnTam());
                    taBemerkungTAMUsertask.setText(tamUserTask.getBemerkungen());
                    if (tamUserTask.getWiedervorlageAm() != null) {
                        tfWiedervorlageAmTAMUsertask.setText(tamUserTask.getWiedervorlageAmAsLocalDateTime().format(DateTimeFormatter.ofPattern((
                                "dd.MM.YYYY HH:mm"))));
                    }
                }
            }
        }
    }

    @Override
    protected void execute(String command) {
        if (SEARCH_BUTTON.equals(command)) {
            doSearch();
        }
        else if (SAVE_BUTTON.equals(command)) {
            doSave();
        }
        else if (EDIT_WIEDERVORLAGE_AM_CB_VORGANG.equals(command)) {
            handleWiedervorlageAM(tfWiedervorlageAmCbVorgang);
        }
        else if (EDIT_WIEDERVORLAGE_AM_TAM_USERTASK.equals(command)) {
            handleWiedervorlageAM(tfWiedervorlageAmTAMUsertask);
        }
        else if (EDIT_BEARBEITER_CB_VORGANG.equals(command)) {
            searchBearbeiter();
        }
    }

    private void searchBearbeiter() {
        FindUserDialog findUserDialog = new FindUserDialog();
        Object selection = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), findUserDialog,
                true, true);
        if (selection instanceof AKUser) {
            tfBearbeiterCbVorgang.setValue(selection);
        }
    }

    private void handleWiedervorlageAM(AKJTextField tfWiedervorlage) {
        LocalDateTime wiedervorlage = convertTf2DateTime(tfWiedervorlage);
        WiedervorlageDialog dialog = new WiedervorlageDialog(wiedervorlage, null, false);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dialog, true, true);

        if (result instanceof Integer
                && result.equals(JOptionPane.OK_OPTION)
                && !wiedervorlage.equals(dialog.getWiedervorlageDatum())) {
            tfWiedervorlage.setText(dialog.getWiedervorlageDatum().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        }
    }

    private void doSave() {
        if (cbVorgang != null) {
            String selectedStatus = cbStatusCbVorgang.getSelectedItem().toString();
            for (Map.Entry<Long, Reference> status : statusMap.entrySet()) {
                if (status.getValue().getStrValue().equals(selectedStatus)) {
                    cbVorgang.setStatus(status.getKey());
                    break;
                }
            }
            cbVorgang.setBearbeiter((AKUser) tfBearbeiterCbVorgang.getValue());
            cbVorgang.setAnsweredAt(dcAntwortDatumCbVorgang.getDate(null));
            cbVorgang.setReturnRealDate(dcRueckmeldungRealDatum.getDate(null));
            cbVorgang.setReturnVTRNR(tfRueckmeldungVertragsnummer.getText());
            cbVorgang.setReturnLBZ(tfRueckmeldungLBZ.getText());
            cbVorgang.setReturnLL(tfRueckmeldungLL.getText());
            cbVorgang.setReturnAQS(tfRueckmeldungAQS.getText());
            cbVorgang.setReturnKundeVorOrt(chbRueckmeldungKundeVorOrt.isSelectedBoolean());
            cbVorgang.setReturnOk(chbRueckmeldungOk.isSelectedBoolean());
            cbVorgang.setReturnBemerkung(taRueckmeldungBemerkung.getText());
            cbVorgang.setReturnMaxBruttoBitrate(tfMaxBruttoBitrate.getText());
            cbVorgang.setCarrierBearbeiter(tfCarrierBearbeiter.getText());
            cbVorgang.setCarrierKennungAbs(tfCarrierKennungABS.getText());
            cbVorgang.setStatusBemerkung(taBemerkungStatus.getText());
            if (tfWiedervorlageAmCbVorgang != null) {
                cbVorgang.setWiedervorlageAm(convertTf2DateTime(tfWiedervorlageAmCbVorgang));
            }

            if ( !(cbVorgang instanceof WitaCBVorgang) ) {
                MessageHelper.showWarningDialog(getMainFrame(),
                        "Die Änderungen auf dem Vorgang für die externe Auftragsnummer {0} "
                                + "werden nicht gespreichert, weil es kein WITA bzw. Tam-Vorgang ist.", true, cbVorgang.getCarrierRefNr());
                return;  // do not save
            }

            final WitaCBVorgang witaCbVorgang = (WitaCBVorgang) cbVorgang;
            witaCbVorgang.setAbmState((AbmState) cbABMStatus.getSelectedItem());
            if (cbPreUetv.getSelectedItem() instanceof String && StringUtils.isBlank((String) cbPreUetv.getSelectedItem())) {
                witaCbVorgang.setPreviousUebertragungsVerfahren(null);
            }
            else {
                witaCbVorgang.setPreviousUebertragungsVerfahren((Uebertragungsverfahren) cbPreUetv.getSelectedItem());
            }

            TamUserTask tamUserTask = witaCbVorgang.getTamUserTask();
            if (tamUserTask != null) {
                tamUserTask.setStatus((UserTaskStatus) cbStatusTAMUsertask.getSelectedItem());
                tamUserTask.setTamBearbeitungsStatus((TamBearbeitungsStatus) cbBearbeitungStatusTAMUsertask
                        .getSelectedItem());
                tamUserTask.setMahnTam(chbMahnTAM.isSelected());
                tamUserTask.setBemerkungen(taBemerkungTAMUsertask.getText());
                if (tfWiedervorlageAmTAMUsertask != null) {
                    tamUserTask.setWiedervorlageAm(convertTf2DateTime(tfWiedervorlageAmTAMUsertask));
                }
            }
            try {
                carrierService.saveCBVorgang(witaCbVorgang);
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Die Änderungen auf dem CBVorgang bzw. Tam-Usertask für die externe Auftragsnummer {0} "
                                + "wurden gespeichert.", witaCbVorgang.getCarrierRefNr()
                );
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e, true);
            }
            doSearch();
        }
    }

    private void clearData() {
        this.cbVorgang = null;
        GuiTools.cleanFields(this);
    }

    private LocalDateTime convertTf2DateTime(AKJTextField tf) {
        if (StringUtils.isEmpty(tf.getText())) {
            return null;
        }
        return LocalDateTime.parse(tf.getText(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}
