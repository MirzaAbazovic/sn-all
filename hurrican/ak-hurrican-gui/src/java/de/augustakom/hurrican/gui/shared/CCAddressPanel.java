/**
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH All rights reserved. -------------------------------------------------------
 * File created: 13.09.2007 11:28:10
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.TelefonnummerUtils;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.QueryBillingService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Panel zur Anzeige einer {@link CCAddress} <br>
 */
public class CCAddressPanel extends AbstractDataPanel implements AKTableOwner, AKDataLoaderComponent {

    private static final long serialVersionUID = 7683557719586223402L;

    private static final Logger LOGGER = Logger.getLogger(CCAddressPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/shared/resources/CCAddressPanel.xml";
    private static final String CMD_KUNDEN_AUFTRAEGE_4_ADDRESS = "show.kunden.auftraege.for.address";

    private AKReferenceField rfAnspType = null;
    private AKJCheckBox chbPreferred = null;
    private AKJTextArea tfContactText = null;
    private AKJFormattedTextField tfAnspPrio = null;

    private AKJPanel pnlAddress;
    private AKJLabel lblNewAddressWarning;
    private AKReferenceField rfAddressType = null;
    private AKReferenceField rfFormat = null;
    private AKJTextField tfTitel = null;
    private AKJTextField tfName = null;
    private AKJTextField tfVorname = null;
    private AKJTextField tfTitel2 = null;
    private AKJTextField tfName2 = null;
    private AKJTextField tfVorname2 = null;
    private AKJTextField tfStrasse = null;
    private AKJTextField tfStrasseAdd = null;
    private AKJTextField tfHNr = null;
    private AKJTextField tfHNrAdd = null;
    private AKJTextField tfPostfach = null;
    private AKJTextField tfLand = null;
    private AKJTextField tfPLZ = null;
    private AKJTextField tfOrt = null;
    private AKJTextField tfOrtsteil = null;
    private AKJTextField tfPhone = null;
    private AKJTextField tfFax = null;
    private AKJTextField tfMobil = null;
    private AKJTextField tfMail = null;
    private AKJTextArea tfBemerkung = null;
    private AKJButton btnKundenAuftraege4Address = null;
    private AKJPanel pnlKundenAuftraege4Address = null;
    private AKJFormattedTextField tfPrioBrief = null;
    private AKJFormattedTextField tfPrioFax = null;
    private AKJFormattedTextField tfPrioEmail = null;
    private AKJFormattedTextField tfPrioSMS = null;
    private AKJFormattedTextField tfPrioTel = null;

    private boolean initialized = false;
    private boolean addressForAnsprechpartner = false;

    private Long addressId = null;
    private CCAddress address = null;
    private Ansprechpartner ansprechpartner = null;
    private Long kundeNo = null;
    private GeoId geoId = null;
    private Long addressType = null;

    // Services
    private KundenService kundenService;
    private BillingAuftragService billingAuftragService;
    private ISimpleFindService billingSimpleFindService;
    private CCKundenService ccKundenService;
    private CCAuftragService auftragService;
    private ISimpleFindService ccSimpleFindService;
    private ReferenceService referenceService;

    public CCAddressPanel(Long addressId) {
        super(RESOURCE);
        initServices();
        this.addressId = addressId;
        createGUI();
        initFirst();
        loadData();
        showDetails(address);
    }

    public CCAddressPanel(Long kundeNo, GeoId geoId, Long addressType) {
        super(RESOURCE);
        initServices();
        this.kundeNo = kundeNo;
        this.geoId = geoId;
        this.addressType = addressType;
        createGUI();
        initFirst();
        loadData();
        showDetails(address);
    }

    /**
     * Konstruktor mit Angabe des Ansprechpartner, fuer bzw. ueber den die Adresse definiert wird.
     */
    public CCAddressPanel(Ansprechpartner ansprechpartner, Long kundeNo) {
        super(RESOURCE);
        initServices();
        addressForAnsprechpartner = true;
        this.ansprechpartner = ansprechpartner;
        this.kundeNo = kundeNo;
        this.addressId = (ansprechpartner.getAddress() != null) ? ansprechpartner.getAddress().getId() : null;
        createGUI();
        initFirst();
        loadData();
        showDetails(ansprechpartner);
    }

    private void initServices() {
        try {
            kundenService = getBillingService(KundenService.class);
            billingAuftragService = getBillingService(BillingAuftragService.class);
            billingSimpleFindService = getBillingService(QueryBillingService.class);
            ccKundenService = getCCService(CCKundenService.class);
            auftragService = getCCService(CCAuftragService.class);
            ccSimpleFindService = getCCService(QueryCCService.class);
            referenceService = getCCService(ReferenceService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblAnspType = getSwingFactory().createLabel("ansprechpartner.type");
        AKJLabel lblPreferred = getSwingFactory().createLabel("ansprechpartner.preferred");
        AKJLabel lblContactText = getSwingFactory().createLabel("ansprechpartner.text");
        AKJLabel lblAddressType = getSwingFactory().createLabel("address.type");
        AKJLabel lblFormat = getSwingFactory().createLabel("format.name");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblName2 = getSwingFactory().createLabel("name2");
        AKJLabel lblStrasse = getSwingFactory().createLabel("strasse");
        AKJLabel lblStrasseAdd = getSwingFactory().createLabel("strasse.zusatz");
        AKJLabel lblPostfach = getSwingFactory().createLabel("postfach");
        AKJLabel lblPlzOrt = getSwingFactory().createLabel("plz.ort");
        AKJLabel lblOrtsteil = getSwingFactory().createLabel("ortsteil");
        AKJLabel lblPhone = getSwingFactory().createLabel("phone");
        AKJLabel lblFax = getSwingFactory().createLabel("fax");
        AKJLabel lblMobil = getSwingFactory().createLabel("mobil");
        AKJLabel lblMail = getSwingFactory().createLabel("mail");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblAnspPrio = getSwingFactory().createLabel("prio");
        AKJLabel lblPrioBrief = getSwingFactory().createLabel("prio.brief");
        AKJLabel lblPrioFax = getSwingFactory().createLabel("prio.fax");
        AKJLabel lblPrioEmail = getSwingFactory().createLabel("prio.email");
        AKJLabel lblPrioSMS = getSwingFactory().createLabel("prio.sms");
        AKJLabel lblPrioTel = getSwingFactory().createLabel("prio.tel");

        rfAnspType = getSwingFactory().createReferenceField("ansprechpartner.type");
        observeAnspType();
        chbPreferred = getSwingFactory().createCheckBox("ansprechpartner.preferred", true);
        tfContactText = getSwingFactory().createTextArea("ansprechpartner.text", true);
        lblNewAddressWarning = getSwingFactory().createLabel("address.new.warning");
        AKJScrollPane spContactText = new AKJScrollPane(tfContactText, new Dimension(100, 40));
        rfAddressType = getSwingFactory().createReferenceField("address.type");
        rfFormat = getSwingFactory().createReferenceField("format.name");
        tfTitel = getSwingFactory().createTextField("titel", true, true);
        tfName = getSwingFactory().createTextField("name", true, true);
        tfVorname = getSwingFactory().createTextField("vorname", true, true);
        tfTitel2 = getSwingFactory().createTextField("titel2", true, true);
        tfName2 = getSwingFactory().createTextField("name2", true, true);
        tfVorname2 = getSwingFactory().createTextField("vorname2", true, true);
        tfStrasse = getSwingFactory().createTextField("strasse", true, true);
        tfHNr = getSwingFactory().createTextField("hnr", true, true);
        tfHNrAdd = getSwingFactory().createTextField("hnr.zusatz", true, true);
        tfStrasseAdd = getSwingFactory().createTextField("strasse.zusatz", true, true);
        tfPostfach = getSwingFactory().createTextField("postfach", true, true);
        tfLand = getSwingFactory().createTextField("land", true, true);
        tfLand.setText(AddressModel.LAND_ID_GERMANY);
        tfPLZ = getSwingFactory().createTextField("plz", true, true);
        tfOrt = getSwingFactory().createTextField("ort", true, true);
        tfOrtsteil = getSwingFactory().createTextField("ortsteil", true, true);
        tfPhone = getSwingFactory().createTextField("phone", true, true);
        tfFax = getSwingFactory().createTextField("fax", true, true);
        tfMobil = getSwingFactory().createTextField("mobil", true, true);
        tfMail = getSwingFactory().createTextField("mail", true, true);
        tfBemerkung = getSwingFactory().createTextArea("bemerkung", true);
        AKJScrollPane spBemerkung = new AKJScrollPane(tfBemerkung, new Dimension(100, 40));
        btnKundenAuftraege4Address = getSwingFactory().createButton(CMD_KUNDEN_AUFTRAEGE_4_ADDRESS,
                this.getActionListener());
        pnlKundenAuftraege4Address = addressForAnsprechpartner ? new KundenAuftraege4AddressPanel(addressId) : null;
        tfAnspPrio = getSwingFactory().createFormattedTextField("prio", true);
        tfPrioBrief = getSwingFactory().createFormattedTextField("prio.brief", true);
        tfPrioEmail = getSwingFactory().createFormattedTextField("prio.email", true);
        tfPrioFax = getSwingFactory().createFormattedTextField("prio.fax", true);
        tfPrioSMS = getSwingFactory().createFormattedTextField("prio.sms", true);
        tfPrioTel = getSwingFactory().createFormattedTextField("prio.tel", true);

        AKJButton btnNewAddress = getSwingFactory().createButton("address.new", new NewAddressAction());
        AKJButton btnSearchAddress = getSwingFactory().createButton("address.search", new SearchAddressAction());
        AKJButton btnSearchAndCopyAddress = getSwingFactory().createButton("address.copy", new SearchAndCopyAddressAction());

        this.setLayout(new GridBagLayout());

        AKJPanel pnlName = new AKJPanel(new GridBagLayout());
        pnlName.add(tfTitel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0));
        pnlName.add(tfName, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        pnlName.add(tfVorname, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));

        AKJPanel pnlName2 = new AKJPanel(new GridBagLayout());
        pnlName2.add(tfTitel2, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0));
        pnlName2.add(tfName2, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        pnlName2.add(tfVorname2, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));

        AKJPanel pnlStr = new AKJPanel(new GridBagLayout());
        pnlStr.add(tfStrasse, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0));
        pnlStr.add(tfHNr, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        pnlStr.add(tfHNrAdd, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));

        AKJPanel pnlOrt = new AKJPanel(new GridBagLayout());
        pnlOrt.add(tfLand, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0));
        pnlOrt.add(tfPLZ, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        pnlOrt.add(tfOrt, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));

        if (addressForAnsprechpartner) {
            AKJPanel pnlButtons = new AKJPanel(new GridBagLayout());
            pnlButtons.add(btnNewAddress, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0));
            pnlButtons.add(btnSearchAddress, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            pnlButtons.add(btnSearchAndCopyAddress,
                    GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));

            AKJPanel pnlAnsprechpartner = new AKJPanel(new GridBagLayout());
            pnlAnsprechpartner.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(
                    "border.ansprechpartner")));
            pnlAnsprechpartner.add(lblAnspType, GBCFactory.createGBC(10, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner.add(rfAnspType, GBCFactory.createGBC(100, 0, 3, 0, 6, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner
                    .add(lblPreferred, GBCFactory.createGBC(10, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner.add(chbPreferred, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner.add(lblAnspPrio, GBCFactory.createGBC(10, 0, 5, 1, 1, 1, GridBagConstraints.NONE));
            pnlAnsprechpartner.add(tfAnspPrio, GBCFactory.createGBC(0, 0, 7, 1, 1, 1, GridBagConstraints.NONE));
            pnlAnsprechpartner.add(new AKJPanel(),
                    GBCFactory.createGBC(100, 0, 8, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner.add(lblContactText,
                    GBCFactory.createGBC(10, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner.add(spContactText,
                    GBCFactory.createGBC(100, 0, 3, 2, 6, 1, GridBagConstraints.HORIZONTAL));
            pnlAnsprechpartner.add(pnlButtons, GBCFactory.createGBC(100, 0, 3, 3, 6, 1, GridBagConstraints.HORIZONTAL));
            if (ansprechpartner.getAddress() == null) {
                showNewAddressWarning();
            }

            this.add(pnlAnsprechpartner, GBCFactory.createGBC(100, 0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
            this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(lblNewAddressWarning, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        pnlAddress = new AKJPanel(new GridBagLayout());

        String borderText = getSwingFactory().getText("border.address.new");
        if (addressId != null) {
            borderText = getSwingFactory().getText("border.address", addressId);
        }
        pnlAddress.setBorder(BorderFactory.createTitledBorder(borderText));
        pnlAddress.add(lblAddressType, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        pnlAddress.add(rfAddressType, GBCFactory.createGBC(0, 0, 2, 1, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblFormat, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(rfFormat, GBCFactory.createGBC(0, 0, 2, 2, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblName, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(pnlName, GBCFactory.createGBC(100, 0, 2, 3, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblName2, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(pnlName2, GBCFactory.createGBC(100, 0, 2, 4, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblStrasse, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(pnlStr, GBCFactory.createGBC(0, 0, 2, 5, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblStrasseAdd, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfStrasseAdd, GBCFactory.createGBC(0, 0, 2, 6, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPostfach, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPostfach, GBCFactory.createGBC(0, 0, 2, 7, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPlzOrt, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(pnlOrt, GBCFactory.createGBC(0, 0, 2, 8, 5, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblOrtsteil, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfOrtsteil, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPrioBrief, GBCFactory.createGBC(0, 0, 4, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPrioBrief, GBCFactory.createGBC(0, 0, 6, 9, 1, 1, GridBagConstraints.NONE));
        pnlAddress.add(lblPhone, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPhone, GBCFactory.createGBC(100, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPrioTel, GBCFactory.createGBC(0, 0, 4, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPrioTel, GBCFactory.createGBC(0, 0, 6, 10, 1, 1, GridBagConstraints.NONE));
        pnlAddress.add(lblFax, GBCFactory.createGBC(0, 0, 0, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfFax, GBCFactory.createGBC(100, 0, 2, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPrioFax, GBCFactory.createGBC(0, 0, 4, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPrioFax, GBCFactory.createGBC(0, 0, 6, 11, 1, 1, GridBagConstraints.NONE));
        pnlAddress.add(lblMobil, GBCFactory.createGBC(0, 0, 0, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfMobil, GBCFactory.createGBC(100, 0, 2, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPrioSMS, GBCFactory.createGBC(0, 0, 4, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPrioSMS, GBCFactory.createGBC(0, 0, 6, 12, 1, 1, GridBagConstraints.NONE));
        pnlAddress.add(lblMail, GBCFactory.createGBC(0, 0, 0, 13, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfMail, GBCFactory.createGBC(100, 0, 2, 13, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(lblPrioEmail, GBCFactory.createGBC(0, 0, 4, 13, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(tfPrioEmail, GBCFactory.createGBC(0, 0, 6, 13, 1, 1, GridBagConstraints.NONE));
        pnlAddress.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 14, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlAddress.add(spBemerkung, GBCFactory.createGBC(0, 0, 2, 14, 5, 1, GridBagConstraints.HORIZONTAL));
        if (!addressForAnsprechpartner) {
            // HUR-24025: The dialog opened by this button was pulled inside this panel for customer contact addresses only
            pnlAddress.add(btnKundenAuftraege4Address, GBCFactory.createGBC(0, 0, 4, 15, 3, 1, GridBagConstraints.HORIZONTAL));
        }

        this.add(pnlAddress, GBCFactory.createGBC(100, 0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));

        if (addressForAnsprechpartner) {
            pnlKundenAuftraege4Address.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.kunden.auftraege.for.address")));
            this.add(pnlKundenAuftraege4Address, GBCFactory.createGBC(100, 0, 0, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        }

        enableFields(pnlAddress, false);
    }

    private void observeAnspType() {
        rfAnspType.addObserver(ev -> {
            if (ev != rfAnspType) {
                return;
            }

            final Long anspType = rfAnspType.getReferenceIdAs(Long.class);
            if (anspType.equals(Ansprechpartner.Typ.PARTNER_SICHERHEITSABFRAGEN.refId())) {
                rfAddressType.setReferenceId(CCAddress.ADDRESS_TYPE_EMAIL);
            }
        });
    }

    private void showNewAddressWarning() {
        lblNewAddressWarning.setText("Neue Adresse");
        lblNewAddressWarning.revalidate();
    }

    private void hideNewAddressWarning() {
        lblNewAddressWarning.setText(" ");
        lblNewAddressWarning.revalidate();
    }

    /**
     * Aktiviert oder deaktiviert alle GUI-Komponenten eines Containers
     */
    private void enableFields(Container container, boolean enable) {
        if (container != null) {
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component comp = container.getComponent(i);
                if ((comp != null) && !(comp instanceof AKJLabel)) {
                    if (comp instanceof AKJPanel) {
                        enableFields((AKJPanel) comp, enable);
                    }
                    comp.setEnabled(enable);
                }
            }
        }
        tfBemerkung.setEnabled(enable);
        btnKundenAuftraege4Address.setEnabled((address != null) && (address.getId() != null));
    }

    /**
     * einmaliger Init vom Panel.
     */
    private void initFirst() {
        if (!initialized) {
            initialized = true;

            try {
                Reference adrTypeEx = new Reference();
                adrTypeEx.setType(Reference.REF_TYPE_ADDRESS_TYPE);
                adrTypeEx.setGuiVisible(Boolean.TRUE);
                rfAddressType.setFindService(ccSimpleFindService);
                rfAddressType.setReferenceFindExample(adrTypeEx);

                Reference anspTypeEx = new Reference();
                anspTypeEx.setType(Reference.REF_TYPE_ANSPRECHPARTNER);
                anspTypeEx.setGuiVisible(Boolean.TRUE);
                rfAnspType.setFindService(ccSimpleFindService);
                rfAnspType.setReferenceFindExample(anspTypeEx);

                rfFormat.setFindService(billingSimpleFindService);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @return Liefert die Adresse zurück
     */
    protected CCAddress getAddress() {
        CCAddress adr;
        // Bei Ansprechpartnern soll kein neues Objekt angelegt werden
        if (!addressForAnsprechpartner || (address == null)) {
            adr = new CCAddress();
        }
        else {
            adr = address;
        }

        adr.setKundeNo(kundeNo);
        if (address != null) {
            adr.setKundeNo(address.getKundeNo());
            adr.setId(address.getId());
        }

        String telefon = StringUtils.trimToNull(tfPhone.getText(null));
        try {
            TelefonnummerUtils.convertTelefonnummer(telefon);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException(
                    "Bitte korrigieren Sie die Telefonnummer:\n\n" + e.getMessage(), e));
            return null;
        }
        String mobil = StringUtils.trimToNull(tfMobil.getText(null));
        try {
            TelefonnummerUtils.convertTelefonnummer(mobil);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException(
                    "Bitte korrigieren Sie die Mobilnummer:\n\n" + e.getMessage(), e));
            return null;
        }
        String mail = StringUtils.trimToNull(tfMail.getText(null));
        adr.setAddressType(rfAddressType.getReferenceIdAs(Long.class));
        adr.setFormatName(rfFormat.getReferenceIdAs(String.class));
        adr.setTitel(StringUtils.trimToNull(tfTitel.getText(null)));
        adr.setName(StringUtils.trimToNull(tfName.getText(null)));
        adr.setVorname(StringUtils.trimToNull(tfVorname.getText(null)));
        adr.setTitel2(StringUtils.trimToNull(tfTitel2.getText(null)));
        adr.setName2(StringUtils.trimToNull(tfName2.getText(null)));
        adr.setVorname2(StringUtils.trimToNull(tfVorname2.getText(null)));
        adr.setStrasse(StringUtils.trimToNull(tfStrasse.getText(null)));
        adr.setNummer(StringUtils.trimToNull(tfHNr.getText(null)));
        adr.setHausnummerZusatz(StringUtils.trimToNull(tfHNrAdd.getText()));
        adr.setStrasseAdd(StringUtils.trimToNull(tfStrasseAdd.getText(null)));
        adr.setPostfach(StringUtils.trimToNull(tfPostfach.getText(null)));
        adr.setLandId(StringUtils.trimToNull((StringUtils.isNotBlank(tfLand.getText(null))) ? tfLand.getText(null)
                : AddressModel.LAND_ID_GERMANY));
        adr.setPlz(StringUtils.trimToNull(tfPLZ.getText(null)));
        adr.setOrt(StringUtils.trimToNull(tfOrt.getText(null)));
        adr.setOrtsteil(StringUtils.trimToNull(tfOrtsteil.getText(null)));
        adr.setTelefon(telefon);
        adr.setFax(StringUtils.trimToNull(tfFax.getText(null)));
        adr.setHandy(mobil);
        adr.setEmail(mail);
        adr.setBemerkung(StringUtils.trimToNull(tfBemerkung.getText(null)));
        adr.setPrioBrief(tfPrioBrief.getValueAsInt(null));
        adr.setPrioFax(tfPrioFax.getValueAsInt(null));
        adr.setPrioEmail(tfPrioEmail.getValueAsInt(null));
        adr.setPrioSMS(tfPrioSMS.getValueAsInt(null));
        adr.setPrioTel(tfPrioTel.getValueAsInt(null));

        // Falls Adresse geändert wurde, setze ID auf null damit das Objekt als neuer
        // Datensatz gespeichert wird. Es darf kein Update ausgeführt werden, da eventl.
        // andere Referenzen auf diese Adresse verweisen.
        // Ausser: Die Ansprechpartner nutzen eine Adresse mehrfach, hier soll kein neues
        // Objekt angelegt werden!
        if (adr.compareCCAddress(address)) {
            return address;
        }
        else if (addressForAnsprechpartner) {
            return adr;
        }
        else {
            adr.setId(null);
            return adr;
        }
    }

    /**
     * @return Gibt das Ansprechpartner-Objekt zurueck.
     */
    protected Ansprechpartner getAnsprechpartner() {
        if (ansprechpartner == null) {
            ansprechpartner = new Ansprechpartner();
        }

        ansprechpartner.setTypeRefId(rfAnspType.getReferenceIdAs(Long.class));
        ansprechpartner.setText(tfContactText.getText(null));
        ansprechpartner.setPreferred((chbPreferred.isSelected()) ? Boolean.TRUE : Boolean.FALSE);
        ansprechpartner.setPrio(tfAnspPrio.getValueAsInt(1));
        return ansprechpartner;
    }

    @Override
    protected void execute(String command) {
        if (command.equals(CMD_KUNDEN_AUFTRAEGE_4_ADDRESS)) {
            KundenAuftraege4AddressDialog dlg = new KundenAuftraege4AddressDialog(address.getId());
            DialogHelper.showDialog(this, dlg, true, true);
        }
    }

    @Override
    public final void showDetails(Object details) {
        try {
            setWaitCursor();

            if (details instanceof CCAddress) {
                address = (CCAddress) details;
                addressId = address.getId();
            }
            else if (details instanceof Ansprechpartner) {
                this.ansprechpartner = (Ansprechpartner) details;
                rfAnspType.setReferenceId(ansprechpartner.getTypeRefId());
                chbPreferred.setSelected(ansprechpartner.getPreferred());
                tfContactText.setText(ansprechpartner.getText());
                tfAnspPrio.setValue(ansprechpartner.getPrio());

                if (ansprechpartner.getAddress() != null) {
                    addressId = ansprechpartner.getAddress().getId();
                    address = ansprechpartner.getAddress();
                }
            }

            // Falls nur AdressId übergeben wurde, lade Adresse
            if ((address == null) && (addressId != null)) {
                address = ccKundenService.findCCAddress(addressId);
            }

            // Setze Daten in GUI-Elemente
            if (address != null) {
                rfAddressType.setReferenceId(address.getAddressType());
                rfFormat.setReferenceId(address.getFormatName());
                tfTitel.setText(address.getTitel());
                tfName.setText(address.getName());
                tfVorname.setText(address.getVorname());
                tfTitel2.setText(address.getTitel2());
                tfName2.setText(address.getName2());
                tfVorname2.setText(address.getVorname2());
                tfStrasse.setText(address.getStrasse());
                tfHNr.setText(address.getNummer());
                tfHNrAdd.setText(address.getHausnummerZusatz());
                tfStrasseAdd.setText(address.getStrasseAdd());
                tfPostfach.setText(address.getPostfach());
                tfLand.setText((StringUtils.isNotBlank(address.getLandId())) ? address.getLandId()
                        : AddressModel.LAND_ID_GERMANY);
                tfPLZ.setText(address.getPlz());
                tfOrt.setText(address.getOrt());
                tfOrtsteil.setText(address.getOrtsteil());
                tfPhone.setText(address.getTelefon());
                tfFax.setText(address.getFax());
                tfMobil.setText(address.getHandy());
                tfMail.setText(address.getEmail());
                tfBemerkung.setText(address.getBemerkung());
                tfPrioBrief.setValue(address.getPrioBrief());
                tfPrioFax.setValue(address.getPrioFax());
                tfPrioEmail.setValue(address.getPrioEmail());
                tfPrioSMS.setValue(address.getPrioSMS());
                tfPrioTel.setValue(address.getPrioTel());

                String borderText = getSwingFactory().getText("border.address.new");
                if (address.getId() != null) {
                    hideNewAddressWarning();
                    borderText = getSwingFactory().getText("border.address", address.getId());
                }
                else {
                    showNewAddressWarning();
                }
                pnlAddress.setBorder(BorderFactory.createTitledBorder(borderText));
            }
            else {
                GuiTools.cleanFields(this);
                showNewAddressWarning();
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

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        // not used
    }

    @Override
    public boolean hasModelChanged() {
        return false;
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
    public void update(Observable arg0, Object arg1) {
        // not used
    }

    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            if (addressId != null) {
                address = ccKundenService.findCCAddress(addressId);

                if (address == null) {
                    throw new HurricanGUIException("Die angegebene Adresse wurde nicht gefunden!");
                }
            }
            else {
                address = new CCAddress();
                address.setAddressType(addressType);
                address.setPrioBrief(1);
                address.setPrioFax(1);
                address.setPrioEmail(1);
                address.setPrioSMS(1);
                address.setPrioTel(1);

                if (kundeNo != null) {
                    address.setKundeNo(kundeNo);
                    // Kundenname laden
                    Kunde kunde = kundenService.findKunde(kundeNo);
                    if (kunde != null) {
                        address.setName(kunde.getName());
                        address.setVorname(kunde.getVorname());
                        address.setFormatName((kunde.isBusinessCustomer()) ? AddressFormat.ADDRESS_FORMAT_NAME_BUSINESS
                                : AddressFormat.ADDRESS_FORMAT_NAME_RESIDENTIAL);
                    }
                }

                if (geoId != null) {
                    // Strassen-Details setzen
                    address.setStrasse(geoId.getStreet());
                    address.setPlz(geoId.getZipCode());
                    address.setOrt(geoId.getCity());
                    // falls Adresse noch nicht besteht, werden an Strassen, die auf 'str' enden
                    // ein '.' gehaengt.
                    if ((address.getId() == null) && geoId.getStreet().toLowerCase().endsWith("str")) {
                        tfStrasse.setText(tfStrasse.getText() + ".");
                    }
                }

                if ((ansprechpartner != null) && (ansprechpartner.getAddress() == null)) {
                    // Standard-Adresse des Kunden verwenden, falls vorhanden
                    AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(ansprechpartner
                            .getAuftragId());
                    if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
                        Adresse accesspointAddress = billingAuftragService.findAnschlussAdresse4Auftrag(auftragDaten
                                .getAuftragNoOrig());
                        CCAddress billingAddress = ccKundenService.getCCAddress4BillingAddress(accesspointAddress);
                        if (billingAddress != null) {
                            address = billingAddress;
                            address.setAddressType(CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
                        }
                    }
                }
            }

            // Setze AddressType
            addressType = address.getAddressType();

            // Focus in das naechste auszufuellende Feld setzen
            if (StringUtils.isEmpty(address.getName())) {
                tfName.requestFocus(true);
            }
            else if (StringUtils.isEmpty(address.getStrasse())) {
                tfStrasse.requestFocus(true);
            }
            else if (StringUtils.isEmpty(address.getNummer())) {
                tfHNr.requestFocus(true);
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

    /**
     * Funktion gibt Felder für Bearbeitung frei
     */
    public void changeAddress() {
        enableFields(this, true);
    }

    class NewAddressAction extends AKAbstractAction {
        private static final long serialVersionUID = -2645942314164508905L;

        @Override
        public void actionPerformed(ActionEvent e) {
            ansprechpartner.setAddress(null);
            addressId = null;
            loadData();
            // Show address details, ansprechpartner details have not changed
            showDetails(address);
        }
    }

    private class SearchAddressAction extends AKAbstractAction {
        private static final long serialVersionUID = -5821704238902859371L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object result = showDialogToSelectAddress();
                if (result instanceof CCAddress) {
                    CCAddress addr = (CCAddress) result;

                    // Endstellenadressen sollen immer kopiert werden, damit die Adress-Objekte
                    // der Endstellen nicht versehentlich geaendert werden
                    if (CCAddress.ADDRESS_TYPE_ACCESSPOINT.equals(addr.getAddressType())) {
                        addr = copyAddress(addr);
                    }
                    ansprechpartner.setAddress(addr);
                    addressId = addr.getId();
                    loadData();
                    showDetails(addr);
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }

    }

    private class SearchAndCopyAddressAction extends AKAbstractAction {
        private static final long serialVersionUID = 839166024421529716L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object result = showDialogToSelectAddress();
                if (result instanceof CCAddress) {
                    CCAddress addr = (CCAddress) result;
                    addr = copyAddress(addr);
                    ansprechpartner.setAddress(addr);
                    addressId = addr.getId();
                    loadData();
                    showDetails(addr);
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    private CCAddress copyAddress(CCAddress addr) throws Exception {
        CCAddress addr1 = (CCAddress) BeanUtils.cloneBean(addr);
        addr1.setId(null);
        addr1.setVersion(null);
        addressId = null;
        return addr1;
    }

    private Object showDialogToSelectAddress() throws FindException {
        Reference reference = referenceService.findReference((Long) rfAnspType.getReferenceId());
        Long refId = (reference != null && reference.getIntValue() != null)
                ? Long.valueOf(reference.getIntValue().longValue())
                : null;
        FindAddressDialog dlg = new FindAddressDialog(refId);
        return DialogHelper.showDialog(getMainFrame(), dlg, true, true);
    }

}
