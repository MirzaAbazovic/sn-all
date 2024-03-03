/*

 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 13:01:52
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import static com.google.common.collect.Lists.*;
import static de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard.*;
import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;
import static org.apache.commons.lang.StringUtils.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import javax.swing.*;
import javax.swing.table.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.UnexpectedRollbackException;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.exceptions.WitaUnexpectedRollbackException;
import de.augustakom.hurrican.gui.tools.tal.RufnummerPortierungTableModel;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.billing.helper.RufnummerPortierungSelectionHelper;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.TALOrderService;
import de.augustakom.hurrican.service.cc.utils.WitaSelectionUtils;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.exception.InvalidRufnummerPortierungException;
import de.mnet.wbci.helper.WbciRequestStatusHelper;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;

/**
 * WizardPanel, um einige Details zu einer elektronischen TAL-Bestellung abzufragen.
 */
public class CBVorgangDetailWizardPanel extends AbstractServiceWizardPanel implements AKTableOwner,
        AKDataLoaderComponent, PropertyChangeListener, ItemListener {

    public static final String MSG_BESTELLUNG_UEBERMITTELT = "Die TAL-Bestellung wurde erfolgreich erzeugt und wird " +
            "demnächst an die Schnittstelle übermittelt.";
    public static final String MSG_HVT_KVZ_UEBERMITTELT = "Die Neubestellung des KVz-Auftrags sowie die Kündigung des " +
            "HVt-Auftrags wurden erfolgreich erzeugt und werden demnächst an die Schnittstelle übermittelt.";
    public static final String MNET_MIN_WORKING_DAY_MSG = String.format("Die Mindestanzahl an Arbeitstagen fuer die interne " +
            "TAL-Bestellung ist unterschritten!%n" +
            "Bitte evtl. die zuständigen Abteilungen informieren.");
    public static final String DTAG_MIN_WORKING_DAY_MSG = String.format("Das angegebene Vorgabedatum ist nicht gültig. Bitte beachten Sie folgende Kriterien:%n" +
            "  + Vorlaufzeit für die Bestellung%n" +
            "    |-- 7 Tage für herkömmliche Bestellungen%n" +
            "    |-- 16 Tage für Wechsel HVt -> KVz%n" +
            "  + Vorgabedatum nicht am Wochenende%n" +
            "  + darauffolgender Tag ein Arbeitstag (bei Anbieterwechsel bzw. Kündigung/Bereitstellung mit WBCI)%n" +
            "  + bei Kündigungen:%n" +
            "    |-- Kündigung erfolgt generell drei Tage nach dem abgestimmten Wechseltermin%n");


    protected static final String VORGABEDATUM_ZEITFENSTER_MSG = "Vorgabedatum und Zeitfenster stimmen nicht überein! Bitte um Korrektur.";
    protected static final Color BG_COLOR_PORTIERUNG_ERROR = new Color(255, 100, 100);
    protected static final Color BG_COLOR_PORTIERUNG_NOT_ACTIVE = new Color(240, 240, 240);
    protected static final Color BG_COLOR_PORTIERUNG_ACTIVE = null;

    private static final long serialVersionUID = 89636199546842219L;

    private static final Logger LOGGER = Logger.getLogger(CBVorgangDetailWizardPanel.class);
    private static final String VORABSTIMMUNG_ID = "vorabstimmung.id";
    protected AKJComboBox cbZeitfenster;
    protected AKJTextArea taMontagehinweis;
    protected AKJTextField tfProjektkenner;
    protected AKJTextField tfKopplungkenner;
    protected TALOrderService talOrderService;
    protected WitaTalOrderService witaTalOrderService;
    protected WitaConfigService witaConfigService;
    private AKJDateComponent dcVorgabeMnet;
    private AKJCheckBox chbAbw46Tkg;
    private AKJCheckBox chbAutoVerarbeitung;
    private SelectedRufnummernTable tbRufnummer;
    private RufnummerPortierungTableModel tbMdlRufnummer;
    private AKJLabel lblRufnummern;
    private AKJScrollPane spRufnummer;
    private AKJComboBox cbVorabstimmungsId;
    private WitaWbciServiceFacade witaWbciServiceFacade;
    private FeatureService featureService;
    private WitaVorabstimmungService witaVorabstimmungService;

    private AKJTextField vorabstimmungsField = new AKJTextField();
    private String vorabstimmungsID = null;

    private String selectedWbciVorabstimmungsId;
    private Date suggestDate;

    public CBVorgangDetailWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/tools/tal/resources/CBVorgangDetailWizardPanel.xml", wizardComponents);
        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            featureService = getCCService(FeatureService.class);
            talOrderService = getCCService(TALOrderService.class);
            witaWbciServiceFacade = getCCService(WitaWbciServiceFacade.class);
            witaTalOrderService = getCCService(WitaTalOrderService.class);
            witaConfigService = getCCService(WitaConfigService.class);
            this.witaVorabstimmungService = getCCService(WitaVorabstimmungService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSubTitle = getSwingFactory().createLabel("sub.title", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblVorgabeMnet = getSwingFactory().createLabel("vorgabe.mnet");
        AKJLabel lblZeitfenster = getSwingFactory().createLabel("zeitfenster");

        AKJLabel lblMontagehinweis;
        AKJLabel lblProjektkenner = getSwingFactory().createLabel("projektkenner");
        AKJLabel lblKopplungkenner = getSwingFactory().createLabel("kopplungkenner");

        AKJLabel lbAbw46Tkg = getSwingFactory().createLabel("anbieterwechsel46tkg");
        AKJLabel lbAutoVerarbeiten = getSwingFactory().createLabel("rueckmeldung.automatisch.verarbeiten");

        AKJLabel lblVorabstimmungsId = getSwingFactory().createLabel(VORABSTIMMUNG_ID);

        cbVorabstimmungsId = getSwingFactory().createComboBox(VORABSTIMMUNG_ID);
        cbVorabstimmungsId.addItemListener(this);

        lblMontagehinweis = getSwingFactory().createLabel("montagehinweis");
        taMontagehinweis = getSwingFactory().createTextArea("montagehinweis");
        tfProjektkenner = getSwingFactory().createTextField("projektkenner");
        tfKopplungkenner = getSwingFactory().createTextField("kopplungkenner");

        cbZeitfenster = getSwingFactory().createComboBox("zeitfenster");

        lblRufnummern = getSwingFactory().createLabel("rufnummern");
        lblRufnummern.setVisible(false);

        tbMdlRufnummer = new RufnummerPortierungTableModel();
        tbRufnummer = new SelectedRufnummernTable(tbMdlRufnummer, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbRufnummer.fitTable(new int[] { 70, 70, 70, 70, 70, 70, 100 });
        tbRufnummer.addTableListener(this);

        TableColumn col = tbRufnummer.getColumnModel().getColumn(RufnummerPortierungTableModel.COL_PORTIERUNG_AM);
        col.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));

        spRufnummer = new AKJScrollPane(tbRufnummer, new Dimension(250, 110));
        spRufnummer.setVisible(false);

        chbAbw46Tkg = getSwingFactory().createCheckBox("anbieterwechsel46tkg");
        chbAbw46Tkg.addItemListener(this);

        chbAutoVerarbeitung = getSwingFactory().createCheckBox("rueckmeldung.automatisch.verarbeiten");
        chbAutoVerarbeitung.addItemListener(this);

        dcVorgabeMnet = getSwingFactory().createDateComponent("vorgabe.mnet");
        dcVorgabeMnet.addPropertyChangeListener(this);
        AKJScrollPane spBemMnet = new AKJScrollPane(taMontagehinweis);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        int yCoordinate = 0;
        // @formatter:off
        child.add(new AKJPanel()            , GBCFactory.createGBC(  0,  0, 0, yCoordinate  , 1, 1, GridBagConstraints.NONE));
        child.add(lblSubTitle               , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 3, 1, GridBagConstraints.HORIZONTAL));
        if (featureService.isFeatureOnline(Feature.FeatureName.WBCI_ENABLED)
                && !CBVorgang.TYP_HVT_KVZ.equals(getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP))) {
            child.add(lblVorabstimmungsId   , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
            child.add(cbVorabstimmungsId    , GBCFactory.createGBC(100,100, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        }
        child.add(lblVorgabeMnet        , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 2, yCoordinate  , 1, 1, GridBagConstraints.NONE));
        child.add(dcVorgabeMnet         , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));

        child.add(lblZeitfenster        , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 2, yCoordinate  , 1, 1, GridBagConstraints.NONE));
        child.add(cbZeitfenster         , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));

        child.add(lblProjektkenner      , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(tfProjektkenner       , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblKopplungkenner     , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(tfKopplungkenner      , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));

        child.add(lbAbw46Tkg            , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(chbAbw46Tkg           , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));

        child.add(lbAutoVerarbeiten     , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(chbAutoVerarbeitung   , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblMontagehinweis     , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(spBemMnet             , GBCFactory.createGBC(  0,  0, 3, yCoordinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel()        , GBCFactory.createGBC(100,100, 4, ++yCoordinate, 1, 1, GridBagConstraints.BOTH));

        child.add(lblRufnummern         , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(spRufnummer           , GBCFactory.createGBC(  0,  0, 1, ++yCoordinate, 3, 1, GridBagConstraints.HORIZONTAL, 15));
        child.add(new AKJPanel()        , GBCFactory.createGBC(100,100, 4, ++yCoordinate, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        manageGUI(new AKManageableComponent[] { cbZeitfenster, tfProjektkenner, tfKopplungkenner });
    }

    /**
     * Ermittelt die gueltigen Zeitfenster.
     */
    private List<String> getPossibleZeitfenster() {
        List<String> zeitfensterList = newArrayList();

        Long carrierId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CARRIER_ID);
        Long cbTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);

        List<Zeitfenster> possibleZeitfenster = witaConfigService.getPossibleZeitfenster(cbTyp, carrierId);
        for (Zeitfenster zf : possibleZeitfenster) {
            zeitfensterList.add(zf.getDescription());
        }
        return zeitfensterList;
    }

    /**
     * Gibt das zu verwendende Default-Zeitfenster zurueck.
     * <pre>
     *   - KUE-KD = SLOT_2
     *   - REX-MK = SLOT_1
     *   - alle anderen = SLOT_9
     * </pre>
     */
    private String getDefaultZeitfenster() {
        // abhaengig von Geschaeftsfall ermitteln!
        Zeitfenster defaultZf = Zeitfenster.getDefaultZeitfenster(retrieveGfTyp());

        return defaultZf.getDescription();
    }

    @Override
    public void update() {
        GuiTools.cleanFields(this);

        Long carrierId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CARRIER_ID);
        Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
        AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);

        cbZeitfenster.addItems(getPossibleZeitfenster());
        cbZeitfenster.setSelectedItem(getDefaultZeitfenster());
        dcVorgabeMnet.setDate(suggestDate);

        String preselectedVaIdFromVorabstimunngWizard = (String) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_WBCI_VORABSTIMMUNGSID);
        cbVorabstimmungsId.setSelectedItem(preselectedVaIdFromVorabstimunngWizard);
        if (isNotEmpty(preselectedVaIdFromVorabstimunngWizard)) {
            cbVorabstimmungsId.setEnabled(false);
        }
        else {
            cbVorabstimmungsId.setEnabled(true);
        }

        if (vorabstimmungsID != null) {
            vorabstimmungsField.setText(vorabstimmungsID);
        }

        if (TYP_PORTWECHSEL.equals(cbVorgangTyp) || Carrier.isMNetCarrier(carrierId)) {
            cbZeitfenster.setEnabled(false);
        }
        if (!Carrier.isMNetCarrier(carrierId) && (TYP_KUENDIGUNG.equals(cbVorgangTyp)
                || TYP_REX_MK.equals(cbVorgangTyp))) {
            taMontagehinweis.setEnabled(false);
        }
        else {
            taMontagehinweis.setEnabled(true);
        }

        boolean setAbw46 = !Carrier.isMNetCarrier(carrierId)
                && TYP_ANBIETERWECHSEL.equals(cbVorgangTyp);
        chbAbw46Tkg.setSelected(setAbw46);
        chbAbw46Tkg.setEnabled(!setAbw46);

        // bei Klammerung ist ein automatischer Abschluss der Rueckmeldung nie erlaubt!
        boolean autoClosingAllowed = !isKlammerung() && witaTalOrderService.checkAutoClosingAllowed(auftragDaten,
                carrierId, cbVorgangTyp);
        chbAutoVerarbeitung.setEnabled(autoClosingAllowed);
        if (autoClosingAllowed && TYP_KUENDIGUNG.equals(cbVorgangTyp)) {
            // Kuendigungen automatisch selektieren; bei allen anderen Vorgaengen muss der User die automatische
            // Verarbeitung aktivieren
            chbAutoVerarbeitung.setSelected(true);
        }

        if (hasRufnummerTable()) {
            spRufnummer.setVisible(true);
            lblRufnummern.setVisible(true);
        }
        else {
            spRufnummer.setVisible(false);
            lblRufnummern.setVisible(false);
        }
        if (!Carrier.ID_DTAG.equals(carrierId)) {
            cbZeitfenster.setEnabled(false);
            tfProjektkenner.setEnabled(false);
            tfKopplungkenner.setEnabled(false);
        }

        addMontageHinweis(retrieveGfTyp());
    }

    @Override
    public final void loadData() {
        AuftragDaten ad = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
        if (ad != null) {
            // Vorgabedatum ermitteln:
            if (DateTools.isDateAfter(ad.getVorgabeSCV(), new Date())) {
                suggestDate = ad.getVorgabeSCV();
            }
            else if (ad.getKuendigung() != null) {
                suggestDate = ad.getKuendigung();
            }

            dcVorgabeMnet.setDate(suggestDate);
            List<RufnummerPortierungSelection> rufnummerList = witaTalOrderService.getRufnummerPortierungList(
                    ad.getAuftragNoOrig(), suggestDate);
            tbMdlRufnummer.setData(rufnummerList);

            final Endstelle endstelle = new Endstelle();
            endstelle.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
            final Vorabstimmung vorabstimmung = witaVorabstimmungService.findVorabstimmung(endstelle, ad);

            if (vorabstimmung != null && vorabstimmung.getWbciVorabstimmungFax() != null) {
                final WBCIVorabstimmungFax wbciVorabstimmungFax = vorabstimmung.getWbciVorabstimmungFax();
                vorabstimmungsID = wbciVorabstimmungFax.getVorabstimmungsId();
                final Container parent = cbVorabstimmungsId.getParent();
                parent.remove(cbVorabstimmungsId);
                parent.add(vorabstimmungsField, GBCFactory.createGBC(100, 100, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
                vorabstimmungsField.setEnabled(false);
            }
            else {
                Set<String> vorabstimmungIds = witaWbciServiceFacade.findNonCompletedVorabstimmungen(
                        WbciRequestStatusHelper.getActiveWbciRequestStatus(retrieveGfTyp()),
                        ad.getAuftragNoOrig(), false);
                // add an empty entry
                cbVorabstimmungsId.addItem("", null);
                cbVorabstimmungsId.addItems(vorabstimmungIds, true);
            }
            addMontageHinweis(retrieveGfTyp());
        }
    }

    @Override
    public void finish() throws AKJWizardFinishVetoException {
        // HUR-8980: Account Uebernahme in TAL Wizard
        Long auftragIdOld = (Long) getWizardObject(WIZARD_OBJECT_TRANSFER_ACCOUNT_FROM);
        if (auftragIdOld != null) {
            try {
                AccountService accountService = getCCService(AccountService.class);
                AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
                Long auftragIdNew = auftragDaten.getAuftragId();
                accountService.moveAccount(auftragIdNew, auftragIdOld);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
        }

        try {

            if (WitaSelectionUtils.isWitaSimulatorTestMode()) {
                createCBVorgangWithTestUser();
            }
            else {
                createCBVorgang();
            }
            Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
            String infoMessage = CBVorgang.TYP_HVT_KVZ.equals(cbVorgangTyp) ? MSG_HVT_KVZ_UEBERMITTELT : MSG_BESTELLUNG_UEBERMITTELT;
            MessageHelper.showInfoDialog(this, infoMessage);

        }
        catch (AKJWizardFinishVetoException e) {
            throw e;
        }
        catch (UnexpectedRollbackException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    new WitaUnexpectedRollbackException(e));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    protected Date getCheckedVorgabeMnet() throws AKJWizardFinishVetoException {
        Date vorgabeMnet = dcVorgabeMnet.getDate(null);
        if (vorgabeMnet == null || DateTools.isDateBefore(vorgabeMnet, new Date())) {
            MessageHelper.showInfoDialog(this, "Es muss ein gültiges Vorgabedatum erfasst werden!", null, true);
            throw new AKJWizardFinishVetoException(getSwingFactory().getText("no.date"));
        }
        return vorgabeMnet;
    }

    protected void createCBVorgang() throws Exception {
        String montagehinweis = taMontagehinweis.getText(null);
        if ("".equals(montagehinweis)) {
            montagehinweis = null;
        }

        Date vorgabeMnet = getCheckedVorgabeMnet();
        Long carrierId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CARRIER_ID);

        String minWorkingDayMsg = NumberTools.isIn(carrierId, new Long[] { Carrier.ID_AKOM, Carrier.ID_MNET, Carrier.ID_MNET_NGN })
                ? MNET_MIN_WORKING_DAY_MSG
                : DTAG_MIN_WORKING_DAY_MSG;

        GeschaeftsfallTyp witaGeschaeftsfallTyp = retrieveGfTyp();
        selectedWbciVorabstimmungsId = (String) cbVorabstimmungsId.getSelectedItem();

        if (Arrays.asList(Carrier.ID_AKOM, Carrier.ID_MNET, Carrier.ID_MNET_NGN, Carrier.ID_DTAG).contains(carrierId)) {
            Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
            if (!witaTalOrderService.isMinWorkingDaysInFuture(
                    vorgabeMnet,
                    witaGeschaeftsfallTyp,
                    getSelectedWbciVorabstimmungsIdOrNull(),
                    CBVorgang.TYP_HVT_KVZ.equals(cbVorgangTyp))) {

                MessageHelper.showInfoDialog(this, new Dimension(500, 300), minWorkingDayMsg, "Information", null, true);
                if (NumberTools.equal(carrierId, Carrier.ID_DTAG)) {
                    // kein Versand an DTAG bei unterschrittener Mindestvorlaufzeit!
                    throw new AKJWizardFinishVetoException("incorrect date");
                }
            }
        }

        Long cbId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);
        AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
        @SuppressWarnings("unchecked")
        Set<CBVorgangSubOrder> subOrders = (Set<CBVorgangSubOrder>) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_SUB_ORDERS_4_KLAMMERUNG);
        Long cbTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
        Long gfTypIntern = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_TAL_GF_TYP_INTERN);
        Boolean vierDraht = (Boolean) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_4_DRAHT);

        if (!witaTalOrderService.isDayInZeitfenster(vorgabeMnet, getZeitfenster())) {
            MessageHelper.showInfoDialog(this, VORGABEDATUM_ZEITFENSTER_MSG, null, true);
            throw new AKJWizardFinishVetoException("incorrect date or time interval");
        }

        Long auftragId4PortChange = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_ID_4_PORT_CHANGE);
        Long auftragIdHvt4HvtToKvz = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_ID_4_HVT_TO_KVZ);
        Map<Long, Set<Pair<ArchiveDocumentDto, String>>> attachements = createAttachmentsForCudaKuendigung();
        Boolean processAutomation = chbAutoVerarbeitung.isEnabled() && chbAutoVerarbeitung.isSelected() ? Boolean.TRUE
                : Boolean.FALSE;

        // @formatter:off
        CbVorgangData cbvData = new CbVorgangData().withCbId(cbId)
                .addAuftragId(auftragDaten.getAuftragId(), auftragId4PortChange)
                .withAuftragId4HvtToKvz(auftragIdHvt4HvtToKvz)
                .withCarrierId(carrierId)
                .withVorgabe(vorgabeMnet)
                .withCbVorgangTyp(cbTyp)
                .withGfTypIntern(gfTypIntern)
                .withSubOrders(subOrders, vierDraht)
                .withMontagehinweis(montagehinweis)
                .withAnbieterwechselTkg46(chbAbw46Tkg.isSelected())
                .withAutomation(processAutomation)
                .withUser(HurricanSystemRegistry.instance().getCurrentUser())
                .withRealisierungsZeitfenster(getZeitfenster())
                .withArchiveDocuments(attachements)
                .withProjektKenner(tfProjektkenner.getText(null))
                .withKopplungsKenner(tfKopplungkenner.getText(null))
                .withVorabstimmungsId(getSelectedWbciVorabstimmungsIdOrNull())
                .withRufnummerIds(getRufnummerIds());
        // @formatter:on

        if (CBVorgang.TYP_HVT_KVZ.equals(cbTyp)) {
            witaTalOrderService.createHvtKvzCBVorgaenge(cbvData);
        }
        else {
            witaTalOrderService.createCBVorgang(cbvData);
        }
    }

    private GeschaeftsfallTyp retrieveGfTyp() {
        Long cbTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
        Long carrierId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CARRIER_ID);
        GeschaeftsfallTyp[] gfTypen = WitaCBVorgang.transformCbVorgangToGeschaeftsfallTyp(cbTyp, carrierId);
        return (gfTypen != null && gfTypen.length > 0) ? gfTypen[0] : GeschaeftsfallTyp.BEREITSTELLUNG;
    }

    private Map<Long, Set<Pair<ArchiveDocumentDto, String>>> createAttachmentsForCudaKuendigung() {
        Map<Long, Set<Pair<ArchiveDocumentDto, String>>> auftraege2attachments = Maps.newHashMap();

        @SuppressWarnings("unchecked")
        Map<Long, Set<ArchiveDocumentDto>> archiveDocuments = (HashMap<Long, Set<ArchiveDocumentDto>>) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_WITH_ATTACHMENTS);
        if (archiveDocuments != null && !archiveDocuments.isEmpty()) {
            for (Entry<Long, Set<ArchiveDocumentDto>> archiveDocs : archiveDocuments.entrySet()) {
                Set<Pair<ArchiveDocumentDto, String>> anlagen = Sets.newHashSet();
                for (ArchiveDocumentDto archiveDoc : archiveDocs.getValue()) {
                    anlagen.add(Pair.create(archiveDoc, Anlagentyp.KUENDIGUNGSSCHREIBEN.value));
                }
                auftraege2attachments.put(archiveDocs.getKey(), anlagen);
            }
        }
        return auftraege2attachments;
    }

    private void createCBVorgangWithTestUser() throws Exception {
        String testAmUser = (String) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_TEST_AM_USER);

        // Wichtig: Hier vom Backend laden, sonst kriegt man eine alte Version, die dann beim Speichern im Server zu
        // Problemen fuehrt
        AKUser user = witaTalOrderService.findUserBySessionId(getSessionId());
        user.setName(testAmUser);
        // Wichtig: Hier im Backend speichern, sonst kriegt der Service das nicht mit und man muss 2 Minuten warten
        witaTalOrderService.saveUser(user);

        createCBVorgang();
    }

    protected Zeitfenster getZeitfenster() {
        String zeitAsString = cbZeitfenster.getSelectedItem().toString();
        for (Zeitfenster zf : Zeitfenster.values()) {
            if (zeitAsString.equals(zf.getDescription())) {
                return zf;
            }
        }
        return Zeitfenster.SLOT_2;
    }

    protected Set<Long> getRufnummerIds() {
        return RufnummerPortierungSelectionHelper.getRufnummerIds(tbMdlRufnummer.getData());
                }

    protected boolean hasRufnummerTable() {
        final boolean isWitaV1 = WitaCdmVersion.V1.equals(witaConfigService.getDefaultWitaVersion());
        Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
        return isWitaV1 && (TYP_ANBIETERWECHSEL.equals(cbVorgangTyp) || TYP_REX_MK.equals(cbVorgangTyp));
    }

    @Override
    public void showDetails(Object details) {
        // nothing to do
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!isWbciVorabstimmungSelected()) {
            Date date = dcVorgabeMnet.getDate(null);
            if (date != null) {
                reloadRufnummernTable(date);
            }
        }
    }

    private void reloadRufnummernTable(Date date) {
        AuftragDaten ad = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
        if (ad != null) {
            List<RufnummerPortierungSelection> rufnummerList = witaTalOrderService.getRufnummerPortierungList(
                    ad.getAuftragNoOrig(), date);
            tbRufnummer.removeAll();
            if (!rufnummerList.isEmpty()) {
                tbMdlRufnummer.setData(rufnummerList);
                tbRufnummer.repaint();
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == chbAbw46Tkg) {
            if (chbAbw46Tkg.isSelected()) {
                StringBuilder montagehinweis = new StringBuilder().append(WitaCBVorgang.ANBIETERWECHSEL_46TKG);
                if (StringUtils.isNotBlank(taMontagehinweis.getText())) {
                    montagehinweis.append(" ");
                    montagehinweis.append(taMontagehinweis.getText());
                }
                taMontagehinweis.setText(montagehinweis.toString());
            }
            else {
                if (taMontagehinweis.getText("").startsWith(WitaCBVorgang.ANBIETERWECHSEL_46TKG)) {
                    String montagehinweisWithoutAbw = StringUtils.removeStart(taMontagehinweis.getText(),
                            WitaCBVorgang.ANBIETERWECHSEL_46TKG);
                    taMontagehinweis.setText(montagehinweisWithoutAbw);
                }
                else {
                    taMontagehinweis.setText("");
                }
            }
        }
        else if (e.getSource() == cbVorabstimmungsId && e.getStateChange() == ItemEvent.SELECTED) {
            selectedWbciVorabstimmungsId = (String) cbVorabstimmungsId.getSelectedItem();

            if (isWbciVorabstimmungSelected()) {

                LocalDateTime vorgabeMnet = witaWbciServiceFacade.getWechselterminForVaId(selectedWbciVorabstimmungsId);
                vorgabeMnet = modifyDateForMinWorkingDaysInFuture(vorgabeMnet);

                // set the current acknowledged Wechseltermin of the WBCI-Geschaeftsfall
                dcVorgabeMnet.setDateTime(vorgabeMnet);
                dcVorgabeMnet.setEnabled(false);
                tbRufnummer.setBackgroundColor(BG_COLOR_PORTIERUNG_NOT_ACTIVE);
                tbRufnummer.setEnabled(false);
                setSelectionForVaId(selectedWbciVorabstimmungsId);
            }
            else {
                removeWbciVorabstimmungDependentChanges();
            }
        }
    }

    /**
     * Falls es sich bei dem WITA GF um eine KUE-KD handelt, dann wird das Vorgabedatum noch zusaetzlich auf
     * die "minWorkingDaysInFuture" geprueft; falls unterschritten, wird das Datum so lange angepasst, bis die
     * Vorlaufzeit i.O. ist.
     * @param vorgabeMnetIn
     * @return das (ggf.) angepasste Vorgabedatum fuer den WITA-Vorgang
     */
    private LocalDateTime modifyDateForMinWorkingDaysInFuture(LocalDateTime vorgabeMnetIn) {
        LocalDateTime vorgabeMnet = vorgabeMnetIn;
        try {
            GeschaeftsfallTyp witaGeschaeftsfallTyp = retrieveGfTyp();
            if (GeschaeftsfallTyp.KUENDIGUNG_KUNDE.equals(witaGeschaeftsfallTyp)) {
                selectedWbciVorabstimmungsId = (String) cbVorabstimmungsId.getSelectedItem();

                Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
                while (!witaTalOrderService.isMinWorkingDaysInFuture(
                        DateConverterUtils.asDate(vorgabeMnet),
                        witaGeschaeftsfallTyp,
                        getSelectedWbciVorabstimmungsIdOrNull(),
                        CBVorgang.TYP_HVT_KVZ.equals(cbVorgangTyp))) {

                    vorgabeMnet = DateCalculationHelper.addWorkingDays(vorgabeMnet.toLocalDate(), 1)
                            .atStartOfDay();
                }
            }
        }
        catch (FindException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return vorgabeMnet;
    }

    private boolean isKlammerung() {
        @SuppressWarnings("unchecked")
        Set<CBVorgangSubOrder> subOrders
                = (Set<CBVorgangSubOrder>) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_SUB_ORDERS_4_KLAMMERUNG);
        return CollectionTools.isNotEmpty(subOrders);
    }

    private boolean isWbciVorabstimmungSelected() {
        return StringUtils.isNotEmpty(selectedWbciVorabstimmungsId);
    }

    private String getSelectedWbciVorabstimmungsIdOrNull() {
        if (vorabstimmungsID != null)
            return vorabstimmungsID;
        return isWbciVorabstimmungSelected() ? selectedWbciVorabstimmungsId : null;
    }

    /**
     * Selects all Rufnummern, which are confirmend in the RUEM-VA of the WBCI Vorabstimmung.
     *
     * @param selectedWbciVorabstimmungsId
     */
    private void setSelectionForVaId(String selectedWbciVorabstimmungsId) {
        if (hasRufnummerTable()) {
            reloadRufnummernTable(dcVorgabeMnet.getDate(null));
            Collection<RufnummerPortierungSelection> rufnummernSelection = tbMdlRufnummer.getData();
            unselectAllRufnummern(rufnummernSelection);

            try {
                WbciGeschaeftsfall wbciGf = witaWbciServiceFacade.getWbciGeschaeftsfall(selectedWbciVorabstimmungsId);
                if (!de.mnet.wbci.model.GeschaeftsfallTyp.VA_KUE_ORN.equals(wbciGf.getTyp())) {
                    rufnummernSelection = witaWbciServiceFacade.updateRufnummernSelectionForVaId(rufnummernSelection,
                            selectedWbciVorabstimmungsId);
                    tbMdlRufnummer.setData(rufnummernSelection);
                }
            }
            catch (InvalidRufnummerPortierungException excp) {
                LOGGER.error(excp.getMessage(), excp);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), excp);
                unselectAllRufnummern(tbMdlRufnummer.getData());
                tbRufnummer.setBackgroundColor(BG_COLOR_PORTIERUNG_ERROR);

                setFinishButtonEnabled(false);
            }
        }
    }

    /**
     * deselect all items of a collection of {@link RufnummerPortierungSelection}s.
     *
     * @param portierungSelections
     */
    private void unselectAllRufnummern(Collection<RufnummerPortierungSelection> portierungSelections) {
        for (RufnummerPortierungSelection rnr : portierungSelections) {
            rnr.setSelected(false);
        }
    }

    @Override
    public void back() {
        selectedWbciVorabstimmungsId = null;
        removeWbciVorabstimmungDependentChanges();
        super.back();
    }

    private void removeWbciVorabstimmungDependentChanges() {
        dcVorgabeMnet.setDate(suggestDate);
        dcVorgabeMnet.setEnabled(true);
        tbRufnummer.setBackgroundColor(BG_COLOR_PORTIERUNG_ACTIVE);
        tbRufnummer.setEnabled(true);
    }

    private void addMontageHinweis(final GeschaeftsfallTyp witaGeschaeftsfallTyp) {
        if (taMontagehinweis == null) {
            return;
        }

        try {
            final Long hvtStandortId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_HVT_STANDORT_ID);
            final String montageHinweise = witaConfigService.findWitaSendLimit(witaGeschaeftsfallTyp, hvtStandortId).getMontageHinweis();
            taMontagehinweis.append(montageHinweise);
        }
        catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private static class SelectedRufnummernTable extends AKJTable {
        private static final long serialVersionUID = 7554078125305011463L;
        private Color background;

        public SelectedRufnummernTable(TableModel dm, int autoResizeMode, int selectionMode) {
            super(dm, autoResizeMode, selectionMode);
            background = null;
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component comp = super.prepareRenderer(renderer, row, column);
            if (getModel() instanceof AKMutableTableModel) {
                Object data = ((AKMutableTableModel<?>) getModel()).getDataAtRow(row);
                if (data instanceof RufnummerPortierungSelection) {
                    RufnummerPortierungSelection rps = (RufnummerPortierungSelection) data;
                    if (rps.getRufnummer().isRealDateInVergangenheit()) {
                        comp.setBackground(BG_COLOR_PORTIERUNG_ERROR);
                    }
                    else if (background != null) {
                        comp.setBackground(background);
                    }
                }
            }
            return comp;
        }

        public void setBackgroundColor(Color color) {
            background = color;
            this.repaint();
        }

    }

}
