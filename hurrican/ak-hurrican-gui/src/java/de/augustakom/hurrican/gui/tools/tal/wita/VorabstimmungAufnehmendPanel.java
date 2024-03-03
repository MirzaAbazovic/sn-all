/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 17:55:46
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.carrier.CarrierLbzDialog;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.CCAddressDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.model.WBCIVorabstimmungFaxComboBoxModel;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaVorabstimmungService;
import de.mnet.wita.service.WitaWbciServiceFacade;

/**
 * Panel zur Anzeige u. Bearbeitung von Vertragsdaten eines Dritt-Providers.
 */
public class VorabstimmungAufnehmendPanel extends AbstractDataPanel implements AKDataLoaderComponent {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/VorabstimmungAufnehmendPanel.xml";

    private static final String VORABSTIMMUNG_BESTANDSSUCHE_DIRECT_DIAL = "vorabstimmung.bestandssucheDirectDial";
    private static final String VORABSTIMMUNG_BESTANDSSUCHE_DN = "vorabstimmung.bestandssucheDn";
    private static final String VORABSTIMMUNG_BESTANDSSUCHE_ONKZ = "vorabstimmung.bestandssucheOnkz";
    private static final String VORABSTIMMUNG_PROVIDER_LBZ = "vorabstimmung.providerLbz";
    private static final String VORABSTIMMUNG_PROVIDER_VERTRAGSNR = "vorabstimmung.providerVertragsnr";
    private static final String VORABSTIMMUNG_PRODUKT_GRUPPE = "vorabstimmung.produktGruppe";
    private static final String VORABSTIMMUNG_CARRIER = "vorabstimmung.carrier";
    private static final String VORABSTIMMUNG_PREVIOUS_LOCATION_ADDRESS = "vorabstimmung.previous.location.address";
    private static final String VORABSTIMMUNG_ID = "vorabstimmung.id";
    private static final String EDIT_CARRIER_LBZ = "edit.carrier.lbz";
    private static final String EDIT_PREVIOUS_LOCATION_ADDRESS = "edit.previous.location.address";

    private static final long serialVersionUID = -5237120180919500024L;

    private static final Logger LOGGER = Logger.getLogger(VorabstimmungAufnehmendPanel.class);

    private CarrierService carrierService;
    private WitaVorabstimmungService witaVorabstimmungService;
    private RufnummerService rufnummerService;
    private FeatureService featureService;
    private WitaConfigService witaConfigService;
    private WitaWbciServiceFacade witaWbciServiceFacade;
    private WbciCommonService wbciCommonService;

    private AKJComboBox cbWbciVorabstimmungsIds;
    private AKJComboBox cbCarrier;
    private AKJComboBox cbProduktGruppe;
    private AKJTextField tfProviderVtrNr;
    private AKJTextField tfProviderLbz;
    private AKJTextField tfBestandssucheOnkz;
    private AKJTextField tfBestandssucheDn;
    private AKJTextField tfBestandssucheDirectDial;
    private AKJTextField tfPreviousLocationAddress;
    private JComboBox<String> vorabstimmungsID = new JComboBox<>();

    private AKJButton btnEditCarrierLbz;
    private AKJButton btnEditAddress;

    private final Endstelle endstelle;
    private final AuftragDaten auftragDaten;
    private final boolean enableAddress;
    private final boolean enableWbciVorabstimmungsId;

    private Vorabstimmung vorabstimmungAufnehmend;
    private CCAddress currentAddress;
    protected String selectedWbciVorabstimmungsId;
    private AKJLabel lblVorabstimmungsId;

    public VorabstimmungAufnehmendPanel(Endstelle endstelle, AuftragDaten auftragDaten) {
        this(endstelle, auftragDaten, false, false);
    }

    public VorabstimmungAufnehmendPanel(Endstelle endstelle, AuftragDaten auftragDaten, boolean enableAddress,
            boolean enableWbciVorabstimmungsId) {

        super(RESOURCE);
        this.endstelle = endstelle;
        this.auftragDaten = auftragDaten;
        this.enableAddress = enableAddress;
        this.enableWbciVorabstimmungsId = enableWbciVorabstimmungsId;
        try {
            initServices();
            createGUI();
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
        carrierService = getCCService(CarrierService.class);
        witaVorabstimmungService = getCCService(WitaVorabstimmungService.class);
        rufnummerService = getBillingService(RufnummerService.class);
        featureService = getCCService(FeatureService.class);
        witaConfigService = getCCService(WitaConfigService.class);
        witaWbciServiceFacade = getCCService(WitaWbciServiceFacade.class);
        wbciCommonService = getCCService(WbciCommonService.class);
    }

    private void loadDefaultData() throws FindException {
        Collection<Carrier> carrierList = carrierService.findCarrierForAnbieterwechsel();
        cbCarrier.addItems(carrierList, true, Carrier.class);

        List<de.mnet.wbci.model.ProduktGruppe> produktGruppen = new ArrayList<>();
        for (ProduktGruppe pG : ProduktGruppe.values()) {
            if (pG != ProduktGruppe.DTAG_ANY) {
                produktGruppen.add(pG);
            }
        }
        cbProduktGruppe.addItems(produktGruppen);
        cbProduktGruppe.setSelectedItem(null);

        // add an empty entry
        cbWbciVorabstimmungsIds.addItem("", null);
        if (isWbciVorabstimmungEnabled()) {
            // add valid wbci Vorabstimmungen
            Set<String> vorabstimmungIds = witaWbciServiceFacade.findNonCompletedVorabstimmungen(
                    WbciRequestStatus.AKM_TR_VERSENDET, auftragDaten.getAuftragNoOrig(), false);
            cbWbciVorabstimmungsIds.addItems(vorabstimmungIds, true);

            if (vorabstimmungIds != null && vorabstimmungIds.size() == 1) {
                // eindeutige WBCI Vorabstimmung --> vor-selektieren!
                selectedWbciVorabstimmungsId = vorabstimmungIds.iterator().next();
                cbWbciVorabstimmungsIds.setSelectedItem(selectedWbciVorabstimmungsId);
                enableVorabstimmungFieldsBasedOnSelectedWbciVa(selectedWbciVorabstimmungsId);
            }
        }

        final List<WBCIVorabstimmungFax> vorabIDs = wbciCommonService.findAll(auftragDaten.getAuftragId(), RequestTyp.VA);
        ComboBoxModel<String> model = new WBCIVorabstimmungFaxComboBoxModel(vorabIDs);
        vorabstimmungsID.setModel(model);
    }

    @Override
    protected final void createGUI() {
        lblVorabstimmungsId = getSwingFactory().createLabel(VORABSTIMMUNG_ID);
        AKJLabel lblCarrier = getSwingFactory().createLabel(VORABSTIMMUNG_CARRIER);
        AKJLabel lblProduktGruppe = getSwingFactory().createLabel(VORABSTIMMUNG_PRODUKT_GRUPPE);
        AKJLabel lblProviderVtrNr = getSwingFactory().createLabel(VORABSTIMMUNG_PROVIDER_VERTRAGSNR);
        AKJLabel lblProviderLBZ = getSwingFactory().createLabel(VORABSTIMMUNG_PROVIDER_LBZ);
        AKJLabel lblBestandssucheOnkz = getSwingFactory().createLabel(VORABSTIMMUNG_BESTANDSSUCHE_ONKZ);
        AKJLabel lblBestandssucheDn = getSwingFactory().createLabel(VORABSTIMMUNG_BESTANDSSUCHE_DN);
        AKJLabel lblBestandssucheDirectDial = getSwingFactory().createLabel(VORABSTIMMUNG_BESTANDSSUCHE_DIRECT_DIAL);
        AKJLabel lblPreviousLocationAddress = getSwingFactory().createLabel(VORABSTIMMUNG_PREVIOUS_LOCATION_ADDRESS);

        cbWbciVorabstimmungsIds = getSwingFactory().createComboBox(VORABSTIMMUNG_ID);
        cbWbciVorabstimmungsIds.addActionListener(getActionListener());
        cbCarrier = getSwingFactory().createComboBox(VORABSTIMMUNG_CARRIER,
                new AKCustomListCellRenderer<>(Carrier.class, Carrier::getPortierungskennungAndName));
        cbCarrier.addActionListener(getActionListener());
        cbProduktGruppe = getSwingFactory().createComboBox(VORABSTIMMUNG_PRODUKT_GRUPPE);
        tfProviderVtrNr = getSwingFactory().createTextField(VORABSTIMMUNG_PROVIDER_VERTRAGSNR);
        tfProviderLbz = getSwingFactory().createTextField(VORABSTIMMUNG_PROVIDER_LBZ);
        tfBestandssucheOnkz = getSwingFactory().createTextField(VORABSTIMMUNG_BESTANDSSUCHE_ONKZ);
        tfBestandssucheDn = getSwingFactory().createTextField(VORABSTIMMUNG_BESTANDSSUCHE_DN);
        tfBestandssucheDirectDial = getSwingFactory().createTextField(VORABSTIMMUNG_BESTANDSSUCHE_DIRECT_DIAL);
        tfPreviousLocationAddress = getSwingFactory().createTextField(VORABSTIMMUNG_PREVIOUS_LOCATION_ADDRESS, false);
        GuiTools.addAction2ComponentPopupMenu(tfPreviousLocationAddress, new RemoveAddressAction(this), true);

        btnEditCarrierLbz = getSwingFactory().createButton(EDIT_CARRIER_LBZ, getActionListener());
        btnEditCarrierLbz.setEnabled(true);
        btnEditCarrierLbz.setVisible(true);
        btnEditCarrierLbz.setPreferredSize(new Dimension(20, 20));

        btnEditAddress = getSwingFactory().createButton(EDIT_PREVIOUS_LOCATION_ADDRESS, getActionListener());
        btnEditAddress.setEnabled(enableAddress);
        btnEditAddress.setPreferredSize(new Dimension(20, 20));

        // @formatter:off
        this.setLayout(new GridBagLayout());
        if (isWbciVorabstimmungEnabled()) {
            final AKJPanel wbciPanel = new AKJPanel(new GridBagLayout());
            wbciPanel.add(lblVorabstimmungsId,     GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            wbciPanel.add(new AKJPanel(),          GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            wbciPanel.add(cbWbciVorabstimmungsIds, GBCFactory.createGBC(100,  0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
            wbciPanel.add(new AKJPanel(),          GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(wbciPanel                   , GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
            this.add(new JSeparator(SwingConstants.HORIZONTAL),
                                                   GBCFactory.createGBC(100,100, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        final AKJPanel witaPanel = new AKJPanel(new GridBagLayout());
        final JLabel vorabLabel = new JLabel("Vorabstimmung-ID:");
        int yOffset = 0;
        witaPanel.add(vorabLabel,                      GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(new AKJPanel(),                  GBCFactory.createGBC(  0,  0, 1, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(vorabstimmungsID,                GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(lblCarrier                     , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(new AKJPanel()                 , GBCFactory.createGBC(  0,  0, 1, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(cbCarrier                      , GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(lblProduktGruppe               , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(cbProduktGruppe                , GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(lblProviderVtrNr               , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(tfProviderVtrNr                , GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(lblProviderLBZ                 , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(tfProviderLbz                  , GBCFactory.createGBC(100,  0, 2, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
        witaPanel.add(btnEditCarrierLbz              , GBCFactory.createGBC(  0,  0, 3, yOffset++, 1, 1, GridBagConstraints.NONE));

        final boolean isWitaV1 = WitaCdmVersion.V1.equals(witaConfigService.getDefaultWitaVersion());
        if (isWitaV1) {
            witaPanel.add(lblBestandssucheOnkz       , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(tfBestandssucheOnkz        , GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(lblBestandssucheDn         , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(tfBestandssucheDn          , GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(lblBestandssucheDirectDial , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(tfBestandssucheDirectDial  , GBCFactory.createGBC(100,  0, 2, yOffset++, 2, 1, GridBagConstraints.HORIZONTAL));
        }

        if (isWitaV1 && enableAddress) {
            witaPanel.add(lblPreviousLocationAddress , GBCFactory.createGBC(  0,  0, 0, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(tfPreviousLocationAddress  , GBCFactory.createGBC(100,  0, 2, yOffset, 1, 1, GridBagConstraints.HORIZONTAL));
            witaPanel.add(btnEditAddress             , GBCFactory.createGBC(  0,  0, 3, yOffset, 1, 1, GridBagConstraints.NONE));
        }
        this.add(witaPanel                      , GBCFactory.createGBC(100,100, 0, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        enableAndClearFields(false, false, isWitaV1);
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case VORABSTIMMUNG_CARRIER:
                carrierSelected();
                break;
            case EDIT_PREVIOUS_LOCATION_ADDRESS:
                editPreviousLocationAddress();
                break;
            case EDIT_CARRIER_LBZ:
                // hier kommt immer eine DTAG-Lbz rein
                CarrierLbzDialog.showCarrierLbzDialogFor(tfProviderLbz, Carrier.ID_DTAG, endstelle.getId());
                break;
            case VORABSTIMMUNG_ID:
                selectedWbciVorabstimmungsId = (String) cbWbciVorabstimmungsIds.getSelectedItem();
                enableVorabstimmungFieldsBasedOnSelectedWbciVa(selectedWbciVorabstimmungsId);
                break;
            default:
                break;
        }
    }

    @Override
    public void readModel() throws AKGUIException {
        // not used
    }

    @Override
    public void saveModel() throws AKGUIException {
        // not used
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * wird ausgewertet in {@link de.augustakom.hurrican.gui.tools.tal.wizard.VorabstimmungWizardPanel#next()}.
     */
    @Override
    public Object getModel() {
        if (isWbciVorabstimmungSelected()) {
            return selectedWbciVorabstimmungsId;
        }
        return vorabstimmungAufnehmend;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        vorabstimmungAufnehmend = null;
        if (model instanceof Vorabstimmung) {
            vorabstimmungAufnehmend = (Vorabstimmung) model;
            if (vorabstimmungAufnehmend.getCarrier() != null) {
                cbCarrier.selectItem("getId", Carrier.class, vorabstimmungAufnehmend.getCarrier().getId());
            }
            cbProduktGruppe.setSelectedItem(vorabstimmungAufnehmend.getProduktGruppe());
            tfProviderLbz.setText(vorabstimmungAufnehmend.getProviderLbz());
            tfProviderVtrNr.setText(vorabstimmungAufnehmend.getProviderVtrNr());
            tfBestandssucheOnkz.setText(vorabstimmungAufnehmend.getBestandssucheOnkz());
            tfBestandssucheDn.setText(vorabstimmungAufnehmend.getBestandssucheDn());
            tfBestandssucheDirectDial.setText(vorabstimmungAufnehmend.getBestandssucheDirectDial());
            setCurrentAddress(vorabstimmungAufnehmend.getPreviousLocationAddress());
            if (vorabstimmungAufnehmend.getWbciVorabstimmungFax() != null) {
                final String vorabstimmungsId = vorabstimmungAufnehmend.getWbciVorabstimmungFax().getVorabstimmungsId();
                vorabstimmungsID.setSelectedItem(vorabstimmungsId);
                cbWbciVorabstimmungsIds.setVisible(false);
                lblVorabstimmungsId.setVisible(false);
            }
        }
    }

    private boolean isWbciVorabstimmungEnabled() {
        return featureService.isFeatureOnline(Feature.FeatureName.WBCI_ENABLED) && enableWbciVorabstimmungsId;
    }

    private boolean isWbciVorabstimmungSelected() {
        return StringUtils.isNotEmpty(selectedWbciVorabstimmungsId);
    }

    private void setCurrentAddress(CCAddress address) {
        currentAddress = address;
        String addressText = (currentAddress != null) ? currentAddress.getCombinedStreetData() : null;
        tfPreviousLocationAddress.setText(addressText);
    }

    @Override
    public final void loadData() {
        // not used
    }

    public boolean saveVorabstimmung() {
        try {
            vorabstimmungAufnehmend = witaVorabstimmungService.findVorabstimmung(endstelle, auftragDaten);
            if (allFieldsClean()) {
                if (vorabstimmungAufnehmend == null) {
                    return true;
                }
                witaVorabstimmungService.deleteVorabstimmung(vorabstimmungAufnehmend);
                return true;
            }

            Carrier selectedCarrier = (Carrier) cbCarrier.getSelectedItem();
            if ((selectedCarrier == null) || (selectedCarrier.getId() == null)) {
                MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("no.carrier"), null, null, true);
                return false;
            }
            vorabstimmungAufnehmend = witaVorabstimmungService.findVorabstimmung(endstelle, auftragDaten);
            if (vorabstimmungAufnehmend == null) {
                vorabstimmungAufnehmend = new Vorabstimmung();
            }
            if (NumberTools.isIn(selectedCarrier.getId(), new Number[] { Carrier.ID_DTAG,
                    Carrier.ID_TELEKOM_DEUTSCHLAND })) {
                vorabstimmungAufnehmend.setProduktGruppe(ProduktGruppe.DTAG_ANY);
            }
            else {
                vorabstimmungAufnehmend.setProduktGruppe((ProduktGruppe) cbProduktGruppe
                        .getSelectedItem());
            }
            vorabstimmungAufnehmend.setCarrier(selectedCarrier);
            vorabstimmungAufnehmend.setProviderLbz(tfProviderLbz.getText());
            vorabstimmungAufnehmend.setProviderVtrNr(tfProviderVtrNr.getText());
            vorabstimmungAufnehmend.setBestandssucheOnkz(tfBestandssucheOnkz.getText());
            vorabstimmungAufnehmend.setBestandssucheDn(tfBestandssucheDn.getText());
            vorabstimmungAufnehmend.setBestandssucheDirectDial(tfBestandssucheDirectDial.getText());
            vorabstimmungAufnehmend.setPreviousLocationAddress(currentAddress);
            vorabstimmungAufnehmend.setAuftragId(auftragDaten.getAuftragId());
            vorabstimmungAufnehmend.setEndstelleTyp(endstelle.getEndstelleTyp());

            final String selectedItem = (String) vorabstimmungsID.getSelectedItem();
            if (selectedItem != null && !selectedItem.isEmpty()) {
                final WBCIVorabstimmungFax vorabstimmungsID = wbciCommonService.findByVorabstimmungsID(selectedItem);
                vorabstimmungAufnehmend.setWbciVorabstimmungFax(vorabstimmungsID);
            }
            vorabstimmungAufnehmend = witaVorabstimmungService.saveVorabstimmung(vorabstimmungAufnehmend);
            setCurrentAddress(vorabstimmungAufnehmend.getPreviousLocationAddress());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            return false;
        }
        return true;
    }

    private void carrierSelected() {
        if (StringUtils.isBlank(selectedWbciVorabstimmungsId)) {
            Carrier carrier = (Carrier) cbCarrier.getSelectedItem();
            boolean carrierIsDtag = false;
            boolean carrierIsOther = false;
            if ((carrier != null) && (carrier.getId() != null)) {
                carrierIsDtag = NumberTools.isIn(carrier.getId(), new Number[] { Carrier.ID_DTAG,
                        Carrier.ID_TELEKOM_DEUTSCHLAND });
                carrierIsOther = !carrierIsDtag;
            }
            final boolean isWitaV1 = WitaCdmVersion.V1.equals(witaConfigService.getDefaultWitaVersion());
            enableAndClearFields(carrierIsDtag, carrierIsOther, isWitaV1);
            if (isWitaV1 && (carrierIsDtag && StringUtils.isBlank(tfBestandssucheOnkz.getText()))) {
                fillBestandssucheWithDefaultRn();
            }
            cbCarrier.requestFocus();
        }
    }

    private void editPreviousLocationAddress() {
        Long addressId = (currentAddress != null) ? currentAddress.getId() : null;
        CCAddressDialog addressDialog = new CCAddressDialog(addressId, null, true);
        Object result = DialogHelper.showDialog(this, addressDialog, true, true);
        if (result instanceof CCAddress) {
            setCurrentAddress((CCAddress) result);
        }
    }

    private void enableAndClearFields(boolean carrierIsDtag, boolean carrierIsOther, boolean isWitaV1) {
        GuiTools.enableContainerComponents(this, false);

        if (StringUtils.isBlank(selectedWbciVorabstimmungsId)) {
            cbCarrier.setEnabled(true);
        }

        cbWbciVorabstimmungsIds.setEnabled(true);
        cbProduktGruppe.setEnabled(carrierIsOther);
        tfProviderVtrNr.setEditable(carrierIsOther || (!isWitaV1 && (carrierIsDtag || carrierIsOther)));
        tfProviderLbz.setEditable(carrierIsOther);
        btnEditCarrierLbz.setEnabled(carrierIsOther);
        tfBestandssucheOnkz.setEditable(carrierIsDtag);
        tfBestandssucheDn.setEditable(carrierIsDtag);
        tfBestandssucheDirectDial.setEditable(carrierIsDtag);

        if (!carrierIsOther) {
            cbProduktGruppe.setSelectedItem(null);
            tfProviderVtrNr.setText((String) null);
            tfProviderLbz.setText((String) null);
        }
        if (!carrierIsDtag) {
            tfBestandssucheOnkz.setText((String) null);
            tfBestandssucheDn.setText((String) null);
            tfBestandssucheDirectDial.setText((String) null);
        }
    }

    /**
     * Falls eine WBCI VA ausgewaehlt ist und der abgebende Carrier 'DTAG' ist, werden die notwendigen (WITA)
     * Vorabstimmungs-Felder enabled; ansonsten werden alle auf disabled gesetzt.
     *
     * @param vorabstimmungsId
     */
    private void enableVorabstimmungFieldsBasedOnSelectedWbciVa(String vorabstimmungsId) {
        try {
            if (StringUtils.isNotBlank(vorabstimmungsId)) {
                WbciGeschaeftsfall wbciGeschaeftsfall = witaWbciServiceFacade.getWbciGeschaeftsfall(vorabstimmungsId);
                if (wbciGeschaeftsfall != null) {
                    // bei WBCI mit DTAG die (WITA)Vorabstimmung erstellen; sonst nicht!
                    // (bei PV mit angegebener VorabstimmungId ist die Bestandsvalidierung in WITA nicht mehr anzugeben)
                    if (CarrierCode.DTAG.equals(wbciGeschaeftsfall.getAbgebenderEKP())) {
                        cbCarrier.selectItem("getId", Carrier.class, Carrier.ID_DTAG);
                        cbCarrier.setEnabled(false);
                        tfBestandssucheOnkz.setEditable(true);
                        tfBestandssucheDn.setEditable(true);

                        cbProduktGruppe.setEnabled(false);
                        tfProviderVtrNr.setEditable(false);
                        tfProviderLbz.setEditable(false);
                        btnEditCarrierLbz.setEnabled(false);
                        tfBestandssucheDirectDial.setEditable(false);
                        btnEditAddress.setEnabled(false);

                        fillBestandssucheFromWbci(wbciGeschaeftsfall);
                    }
                    else {
                        enableVorabstimmungFields(false);
                    }
                }
                else {
                    throw new HurricanGUIException(
                            String.format("WBCI Geschäftsfall zu %s nicht gefunden!", vorabstimmungsId));
                }
            }
            else {
                btnEditAddress.setEnabled(true);
            }
            carrierSelected();
        }
        catch (HurricanGUIException e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void enableVorabstimmungFields(boolean enabled) {
        cbCarrier.setEnabled(enabled);

        cbProduktGruppe.setEnabled(enabled);
        tfProviderVtrNr.setEditable(enabled);
        tfProviderLbz.setEditable(enabled);
        btnEditCarrierLbz.setEnabled(enabled);
        tfBestandssucheOnkz.setEditable(enabled);
        tfBestandssucheDn.setEditable(enabled);
        tfBestandssucheDirectDial.setEditable(enabled);
        btnEditAddress.setEnabled(enabled);
    }

    private boolean allFieldsClean() {
        // @formatter:off
        Carrier selectedCarrier = (Carrier) cbCarrier.getSelectedItem();
        return ((selectedCarrier == null) || (selectedCarrier.getId() == null))
                && StringUtils.isBlank((String) cbProduktGruppe.getSelectedItemValue())
                && StringUtils.isBlank(tfProviderVtrNr.getText())
                && StringUtils.isBlank(tfProviderLbz.getText())
                && StringUtils.isBlank(tfBestandssucheOnkz.getText())
                && StringUtils.isBlank(tfBestandssucheDn.getText())
                && StringUtils.isBlank(tfBestandssucheDirectDial.getText())
                && StringUtils.isBlank(tfPreviousLocationAddress.getText());
        // @formatter:on
    }

    private void fillBestandssucheWithDefaultRn() {
        try {
            Collection<Rufnummer> rns = rufnummerService.findAllRNs4Auftrag(auftragDaten.getAuftragNoOrig());
            rns = Collections2.filter(rns, Rufnummer.PORTMODE_KOMMEND);
            if (CollectionUtils.isNotEmpty(rns)) {
                Rufnummer rufnummer = rns.iterator().next();
                tfBestandssucheOnkz.setText(rufnummer.getOnKz());
                tfBestandssucheDn.setText(rufnummer.getDnBase());
                tfBestandssucheDirectDial.setText(rufnummer.getDirectDial());
            }
        }
        catch (FindException e) {
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }


    private void fillBestandssucheFromWbci(WbciGeschaeftsfall wbciGeschaeftsfall) {
        if (wbciGeschaeftsfall instanceof RufnummernportierungAware) {
            Rufnummernportierung rnp = ((RufnummernportierungAware) wbciGeschaeftsfall).getRufnummernportierung();
            if (rnp instanceof RufnummernportierungEinzeln &&
                    CollectionUtils.isNotEmpty(((RufnummernportierungEinzeln) rnp).getRufnummernOnkz())) {
                RufnummernportierungEinzeln rnpEinzel = (RufnummernportierungEinzeln) rnp;
                tfBestandssucheOnkz.setText(String.format("0%s", rnpEinzel.getRufnummernOnkz().get(0).getOnkz()));
                tfBestandssucheDn.setText(rnpEinzel.getRufnummernOnkz().get(0).getRufnummer());
            }
            else if (rnp instanceof RufnummernportierungAnlage) {
                RufnummernportierungAnlage rnpAnlage = (RufnummernportierungAnlage) rnp;
                tfBestandssucheOnkz.setText(String.format("0%s", rnpAnlage.getOnkz()));
                tfBestandssucheDn.setText(rnpAnlage.getDurchwahlnummer());
            }
        }
    }


    /**
     * Action-Klasse, um eine definierte Adresse vom Vorabstimmungs-Objekt zu entfernen.
     */
    static class RemoveAddressAction extends AKAbstractAction {
        private static final long serialVersionUID = -8476262348151745828L;
        private final VorabstimmungAufnehmendPanel vorabstimmungPanel;

        public RemoveAddressAction(VorabstimmungAufnehmendPanel vorabstimmungPanel) {
            setName("Adresse entfernen");
            setTooltip("Entfernt die dargestellte Adresse");
            setActionCommand("remove.address");
            this.vorabstimmungPanel = vorabstimmungPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = MessageHelper.showYesNoQuestion(vorabstimmungPanel,
                    "Soll die aktuelle Adresse wirklich entfernt werden?", "Adresse entfernen?");
            if (option == JOptionPane.YES_OPTION) {
                vorabstimmungPanel.setCurrentAddress(null);
            }
        }

    }
}
