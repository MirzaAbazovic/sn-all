package de.augustakom.hurrican.gui.tools.tal.wizard.wbci;

import static org.apache.commons.collections.MapUtils.*;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.validation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.tools.tal.RufnummerPortierungTableModel;
import de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.converter.HurricanToWbciConverter;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.exception.WbciBaseException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.PersonOderFirmaTyp;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Strasse;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.FirmaBuilder;
import de.mnet.wbci.model.builder.PersonBuilder;
import de.mnet.wbci.model.builder.ProjektBuilder;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;
import de.mnet.wita.config.WitaConstants;

/**
 * Basis-Dialog zum Erfassen aller notwendigen Daten für alle drei (ausgehenden) WBCI-Geschaeftsfaelle
 *
 *
 */
public abstract class AbstractWbciGeschaeftsfallPanel extends AbstractServiceWizardPanel implements
        AKDataLoaderComponent, AKTableOwner {

    private static final long serialVersionUID = 4782869131859828440L;
    private static final Logger LOGGER = Logger.getLogger(AbstractWbciGeschaeftsfallPanel.class);
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/wbci/AbstractWbciGeschaeftsfallPanel.xml";

    private static final String MSG_WBCI_PROCESS_CREATED = "Ein neuer Vorabstimmungsprozess wurde erfolgreich erzeugt.";
    private static final String PKI_AUF = "pkiAuf";
    private static final String TITLE = "title";
    private static final String LABEL_PERSON_OR_COMPANY = "person.or.company";
    private static final String PERSON = "person";
    private static final String COMPANY = "company";
    private static final String SURNAME = "surname";
    private static final String FORENAME = "forename";
    private static final String TITLE_SURNAME_FORENAME = "title.surname.forename";
    private static final String COMPANYNAME = "companyname";
    private static final String COMPANYNAME_ADDITIONAL = "companyname.additional";
    private static final String LABEL_PERSON_OR_COMPANY_WAI = "person.or.company.wai";
    private static final String TITLE_SURNAME_FORENAME_WAI = "title.surname.forename.wai";
    private static final String COMPANYNAME_WAI = "companyname.wai";
    private static final String ZIP = "zip";
    private static final String CITY = "city";
    private static final String STREET = "street";
    private static final String STREET_NUMBER = "street.number";
    private static final String STREET_NUMBER_ADDITIONAL = "street.number.additional";
    private static final String CUSTOMER_DESIRED_DATE = "customer.desired.date";
    private static final String TIME_SLOT = "time.slot";
    private static final String ADD_WEITERER_ANSCHLUSSINHABER = "weiterer.anschlussinhaber";
    private static final String PROJECT_DETAILS = "project.details";
    private static final String SELECT_ALL_NUMBERS = "select.all.numbers";
    private static final String AUTOMATABLE_GESCHAEFTSFALL = "automatable";
    private static final String PORT_ALL_NUMBERS = "port.all.numbers";
    private static final String IDENTIFIKATION_LABEL = "phone.identification.label";
    private static final String IDENTIFIKATION_ONKZ = "phone.identification.onkz";
    private static final String IDENTIFIKATION_NUMBER = "phone.identification.number";
    private static final String LABEL_ZIP_CITY = "label.zip.city";
    private static final String LABEL_STREET = "label.street";
    private static final String PHONE_NUMBERS = "phone.numbers";
    private static final String VORABSTIMMUNG_CARRIER = "vorabstimmung.carrier";
    private static final String HINWEIS_VORABSTIMMUNG_CARRIER = "hinweis.vorabstimmung.carrier";
    private static final String STR_AEN_TITLE = "str.aen.title";
    private static final String STR_AEN_VORABSTIMMUNGEN = "str.aen.vorabstimmungen";

    private final GeschaeftsfallTyp wbciGeschaeftsfallTyp;
    protected AKJTextField tfPKIAuf;
    protected AKJFormattedTextField ftfIdentificationOnkz;
    protected AKJFormattedTextField ftfIdentificationNumber;
    protected WbciCommonService wbciCommonService;
    protected WbciValidationService wbciValidationService;
    protected FeatureService featureService;
    private AKJComboBox cbStrAenVas;
    private AKJComboBox cbCarrier;
    private AKJComboBox cbTitle;
    private AKJRadioButton rbPerson;
    private AKJRadioButton rbCompany;
    private AKJPanel pnlTitleSurnameForename;
    private AKJPanel pnlCompanyName;
    private AKJComboBox cbTitleWAI;
    private AKJRadioButton rbPersonWAI;
    private AKJRadioButton rbCompanyWAI;
    private AKJLabel lblPersonOrCompanyWAI;
    private AKJPanel pnlPersonOrCompanyWAI;
    private AKJPanel pnlTitleSurnameForenameWAI;
    private AKJPanel pnlCompanyNameWAI;
    private AKJLabel lblCompanyName;
    private AKJLabel lblTitleSurnameForename;
    private AKJTextField tfSurname;
    private AKJTextField tfForename;
    private AKJTextField tfCompanyName;
    private AKJTextField tfCompanyNameAdditional;
    private AKJLabel lblCompanyNameWAI;
    private AKJLabel lblTitleSurnameForenameWAI;
    private AKJTextField tfSurnameWAI;
    private AKJTextField tfForenameWAI;
    private AKJTextField tfCompanyNameWAI;
    private AKJTextField tfCompanyNameAdditionalWAI;
    private AKJFormattedTextField ftfZip;
    private AKJTextField tfCity;
    private AKJTextField tfStreet;
    private AKJFormattedTextField ftfStreetNumber;
    private AKJTextField tfStreetNumberAdditional;
    private AKJDateComponent dcCustomerDesiredDate;
    private AKJComboBox cbTimeSlot;
    private AKJTextField tfProjectDetails;
    private AKJCheckBox chkWAI;
    private AKJCheckBox chkPortAllNumbers;
    private AKJCheckBox chkSelectAllNumbers;
    private AKJCheckBox chkAutomatable;
    private RufnummerPortierungTableModel tbMdlRufnummer;
    private Set<String> strAenVas = null;
    private CarrierService carrierService;
    private AvailabilityService availabilityService;
    private Map<CarrierCode, Carrier> carrierMapEkp;

    public AbstractWbciGeschaeftsfallPanel(AKJWizardComponents wizardComponents, GeschaeftsfallTyp wbciGeschaeftsfallTyp) {
        super(RESOURCE, wizardComponents);
        this.wbciGeschaeftsfallTyp = wbciGeschaeftsfallTyp;
        setNextButtonEnabled(false);
        setFinishButtonEnabled(true);
        try {
            initServices();

            // careful: the StrAenVas have to be retrieved before the gui is created
            strAenVas = getCancelledVorabstimmungIdsWithMissingVaRequest();

            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws ServiceNotFoundException if a service could not be looked up
     */
    protected void initServices() throws ServiceNotFoundException {
        wbciCommonService = getCCService(WbciCommonService.class);
        wbciValidationService = getCCService(WbciValidationService.class);
        carrierService = getCCService(CarrierService.class);
        availabilityService = getCCService(AvailabilityService.class);
        featureService = getCCService(FeatureService.class);
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblStrAenTitle = getSwingFactory().createLabel(STR_AEN_TITLE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblStrAenVas = getSwingFactory().createLabel(STR_AEN_VORABSTIMMUNGEN);
        cbStrAenVas = getSwingFactory().createComboBox(STR_AEN_VORABSTIMMUNGEN);
        cbStrAenVas.addActionListener(getActionListener());

        cbCarrier = getSwingFactory().createComboBox(VORABSTIMMUNG_CARRIER,
                new AKCustomListCellRenderer<>(Carrier.class, Carrier::getPortierungskennungAndNameAndVaModus));
        AKJLabel lblCarrierHinweis = getSwingFactory().createLabel(HINWEIS_VORABSTIMMUNG_CARRIER);
        lblCarrierHinweis.setIcon(getSwingFactory().createIcon("hinweis.icon"));
        lblCarrierHinweis.setToolTipText("Falls der abgebende Carrier nicht in der Liste erscheint, nutzen Sie bitte das Faxverfahren.");
        lblCarrierHinweis.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        cbCarrier.addActionListener(getActionListener());
        tfPKIAuf = getSwingFactory().createTextField(PKI_AUF, true, true);
        tfPKIAuf.setEditable(false);

        cbTitle = getSwingFactory().createComboBox(TITLE,
                new AKCustomListCellRenderer<>(Anrede.class, Anrede::getDescription));
        cbTitle.addItems(Anrede.PERSON_ANREDEN);
        cbTitle.setSelectedItem(Anrede.UNBEKANNT);
        ButtonGroup bgPersonOrCompany = new ButtonGroup();
        RBActionListener rbActionListener = new RBActionListener();
        rbPerson = getSwingFactory().createRadioButton(PERSON, rbActionListener, false, bgPersonOrCompany);
        rbCompany = getSwingFactory().createRadioButton(COMPANY, rbActionListener, false, bgPersonOrCompany);
        pnlTitleSurnameForename = new AKJPanel(new GridBagLayout());
        pnlCompanyName = new AKJPanel(new GridBagLayout());
        tfSurname = getSwingFactory().createTextField(SURNAME, true, true);
        tfForename = getSwingFactory().createTextField(FORENAME, true, true);
        tfCompanyName = getSwingFactory().createTextField(COMPANYNAME, true, true);
        tfCompanyNameAdditional = getSwingFactory().createTextField(COMPANYNAME_ADDITIONAL, true, true);

        cbTitleWAI = getSwingFactory().createComboBox(TITLE,
                new AKCustomListCellRenderer<>(Anrede.class, Anrede::getDescription));
        cbTitleWAI.addItems(Anrede.PERSON_ANREDEN);
        cbTitleWAI.setSelectedItem(Anrede.UNBEKANNT);
        ButtonGroup bgPersonOrCompanyWAI = new ButtonGroup();
        rbPersonWAI = getSwingFactory().createRadioButton(PERSON, rbActionListener, true, bgPersonOrCompanyWAI);
        rbCompanyWAI = getSwingFactory().createRadioButton(COMPANY, rbActionListener, false, bgPersonOrCompanyWAI);
        pnlTitleSurnameForenameWAI = new AKJPanel(new GridBagLayout());
        pnlCompanyNameWAI = new AKJPanel(new GridBagLayout());
        tfSurnameWAI = getSwingFactory().createTextField(SURNAME, true, true);
        tfForenameWAI = getSwingFactory().createTextField(FORENAME, true, true);
        tfCompanyNameWAI = getSwingFactory().createTextField(COMPANYNAME, true, true);
        tfCompanyNameAdditionalWAI = getSwingFactory().createTextField(COMPANYNAME_ADDITIONAL, true, true);

        ftfZip = getSwingFactory().createFormattedTextField(ZIP, true);
        ftfZip.setHorizontalAlignment(SwingConstants.LEFT);
        tfCity = getSwingFactory().createTextField(CITY, true, true);
        tfStreet = getSwingFactory().createTextField(STREET, true, true);
        ftfStreetNumber = getSwingFactory().createFormattedTextField(STREET_NUMBER, true);
        ftfStreetNumber.setHorizontalAlignment(SwingConstants.LEFT);
        tfStreetNumberAdditional = getSwingFactory().createTextField(STREET_NUMBER_ADDITIONAL, true, true);
        dcCustomerDesiredDate = getSwingFactory().createDateComponent(CUSTOMER_DESIRED_DATE);
        cbTimeSlot = getSwingFactory().createComboBox(TIME_SLOT,
                new AKCustomListCellRenderer<>(Portierungszeitfenster.class, Portierungszeitfenster::getDescription));
        cbTimeSlot.addItems(Arrays.asList(Portierungszeitfenster.supported()));
        initialiseCbTimeSlot();
        tfProjectDetails = getSwingFactory().createTextField(PROJECT_DETAILS, true, true);
        ChkBActionListener chkActionListener = new ChkBActionListener();
        chkWAI = getSwingFactory().createCheckBox(ADD_WEITERER_ANSCHLUSSINHABER, chkActionListener, false);
        chkPortAllNumbers = getSwingFactory().createCheckBox(PORT_ALL_NUMBERS);
        chkSelectAllNumbers = getSwingFactory().createCheckBox(SELECT_ALL_NUMBERS);
        chkSelectAllNumbers.addActionListener(getActionListener());
        chkAutomatable = getSwingFactory().createCheckBox(AUTOMATABLE_GESCHAEFTSFALL);
        ftfIdentificationOnkz = getSwingFactory().createFormattedTextField(IDENTIFIKATION_ONKZ, true);
        ftfIdentificationOnkz.setHorizontalAlignment(SwingConstants.LEFT);
        ftfIdentificationNumber = getSwingFactory().createFormattedTextField(IDENTIFIKATION_NUMBER, true);
        ftfIdentificationNumber.setHorizontalAlignment(SwingConstants.LEFT);

        AKJLabel lblCarrier = getSwingFactory().createLabel(VORABSTIMMUNG_CARRIER);
        AKJLabel lblPKIAuf = getSwingFactory().createLabel(PKI_AUF);
        AKJLabel lblPersonOrCompany = getSwingFactory().createLabel(LABEL_PERSON_OR_COMPANY);
        lblTitleSurnameForename = getSwingFactory().createLabel(TITLE_SURNAME_FORENAME);
        lblCompanyName = getSwingFactory().createLabel(COMPANYNAME);
        lblPersonOrCompanyWAI = getSwingFactory().createLabel(LABEL_PERSON_OR_COMPANY_WAI);
        lblTitleSurnameForenameWAI = getSwingFactory().createLabel(TITLE_SURNAME_FORENAME_WAI);
        lblCompanyNameWAI = getSwingFactory().createLabel(COMPANYNAME_WAI);
        AKJLabel lblZipCity = getSwingFactory().createLabel(LABEL_ZIP_CITY);
        AKJLabel lblStreet = getSwingFactory().createLabel(LABEL_STREET);
        AKJLabel lblCustomerDesiredDate = getSwingFactory().createLabel(CUSTOMER_DESIRED_DATE);
        AKJLabel lblTimeSlot = getSwingFactory().createLabel(TIME_SLOT);
        AKJLabel lblProjectDetails = getSwingFactory().createLabel(PROJECT_DETAILS);
        AKJLabel lblWAI = getSwingFactory().createLabel(ADD_WEITERER_ANSCHLUSSINHABER);
        AKJLabel lblPortAllNumbers = getSwingFactory().createLabel(PORT_ALL_NUMBERS);
        AKJLabel lblSelectAllNumbers = getSwingFactory().createLabel(SELECT_ALL_NUMBERS);
        AKJLabel lblAutomatable = getSwingFactory().createLabel(AUTOMATABLE_GESCHAEFTSFALL);
        AKJLabel lblIdentification = getSwingFactory().createLabel(IDENTIFIKATION_LABEL);
        AKJLabel lblRufnummern = getSwingFactory().createLabel(PHONE_NUMBERS);

        AKJPanel childWita = getChildPanel();
        childWita.setLayout(new GridBagLayout());
        int yCoordinate = -1;

        // @formatter:off
        if(!CollectionUtils.isEmpty(strAenVas)) {
            AKJPanel pnlStrAenVas = new AKJPanel(new GridBagLayout());
            pnlStrAenVas.add(cbStrAenVas, GBCFactory.createGBC(100,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));

            childWita.add(lblStrAenTitle, GBCFactory.createGBC(  0,  0, 0, ++yCoordinate, 2, 1, GridBagConstraints.HORIZONTAL));
            childWita.add(lblStrAenVas  , GBCFactory.createGBC(  0,  0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlStrAenVas  , GBCFactory.createGBC(100,  0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

            childWita.add(new JSeparator(SwingConstants.HORIZONTAL),
                                          GBCFactory.createGBC(  0,  0, 0, ++yCoordinate, 2, 1, GridBagConstraints.HORIZONTAL));
        }

        final boolean isGfKueMrn = GeschaeftsfallTyp.VA_KUE_MRN.equals(wbciGeschaeftsfallTyp);
        final boolean isGfKueOrn = GeschaeftsfallTyp.VA_KUE_ORN.equals(wbciGeschaeftsfallTyp);
        final boolean isGfRrnp = GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfallTyp);

        //WBCI-Carrier Panel
        AKJPanel pnlCarrier = new AKJPanel(new GridBagLayout());
        pnlCarrier.add(cbCarrier,           GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlCarrier.add(lblCarrierHinweis,   GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));

        childWita.add(lblCarrier,           GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlCarrier,           GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        // Anschlussinhaber
        AKJPanel pnlPersonOrCompany = new AKJPanel(new GridBagLayout());
        pnlPersonOrCompany.add(rbPerson,        GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlPersonOrCompany.add(rbCompany,       GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(lblPersonOrCompany,       GBCFactory.createGBC(  0,  0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlPersonOrCompany,       GBCFactory.createGBC(100,  0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        pnlTitleSurnameForename.add(cbTitle,    GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlTitleSurnameForename.add(tfSurname,  GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlTitleSurnameForename.add(tfForename, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(lblTitleSurnameForename,  GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlTitleSurnameForename,  GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        pnlCompanyName.add(tfCompanyName,           GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlCompanyName.add(tfCompanyNameAdditional, GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(lblCompanyName,           GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlCompanyName,           GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        // Weiterer Anschlussinhaber
        AKJPanel pnlAddWAI = new AKJPanel(new GridBagLayout());
        pnlAddWAI.add(chkWAI,               GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblWAI,               GBCFactory.createGBC(   0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL,5));
        childWita.add(pnlAddWAI,            GBCFactory.createGBC( 100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL,5));

        pnlPersonOrCompanyWAI = new AKJPanel(new GridBagLayout());
        pnlPersonOrCompanyWAI.add(rbPersonWAI,        GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlPersonOrCompanyWAI.add(rbCompanyWAI,       GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(lblPersonOrCompanyWAI,       GBCFactory.createGBC(  0,  0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlPersonOrCompanyWAI,       GBCFactory.createGBC(100,  0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        pnlTitleSurnameForenameWAI.add(cbTitleWAI,    GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlTitleSurnameForenameWAI.add(tfSurnameWAI,  GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlTitleSurnameForenameWAI.add(tfForenameWAI, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(lblTitleSurnameForenameWAI,  GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlTitleSurnameForenameWAI,  GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        pnlCompanyNameWAI.add(tfCompanyNameWAI,           GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlCompanyNameWAI.add(tfCompanyNameAdditionalWAI, GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(lblCompanyNameWAI,           GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlCompanyNameWAI,           GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        hideWaiPanel();

        // Standort is only for Geschäftsfall KUE_MRN und KUE_ORN nessecarry
        if (isGfKueMrn || isGfKueOrn) {
            AKJPanel pnlStreet = new AKJPanel(new GridBagLayout());
            pnlStreet.add(tfStreet,                 GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlStreet.add(ftfStreetNumber,          GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            pnlStreet.add(tfStreetNumberAdditional, GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(lblStreet,            GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlStreet,            GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

            AKJPanel pnlZipCity = new AKJPanel(new GridBagLayout());
            pnlZipCity.add(ftfZip, GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlZipCity.add(tfCity, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(lblZipCity,           GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlZipCity,           GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        }

        AKJPanel pnlCustomerDesiredDate = new AKJPanel(new GridBagLayout());
        pnlCustomerDesiredDate.add(dcCustomerDesiredDate, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblCustomerDesiredDate,   GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlCustomerDesiredDate,   GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        //Alle Anschluss-Rufnummern portieren und Zeitfenster für GF VA-KUE-MRN und VA-RRNP
        if (isGfKueMrn || isGfRrnp) {
            AKJPanel pnlPortAllNumbers = new AKJPanel(new GridBagLayout());
            pnlPortAllNumbers.add(chkPortAllNumbers, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            childWita.add(lblPortAllNumbers,        GBCFactory.createGBC(   0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL,5));
            childWita.add(pnlPortAllNumbers,        GBCFactory.createGBC( 100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL,5));

            AKJPanel pnlTimeSlot = new AKJPanel(new GridBagLayout());
            pnlTimeSlot.add(cbTimeSlot, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            childWita.add(lblTimeSlot,          GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlTimeSlot,          GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));
        }

        AKJPanel pnlProjectDetails = new AKJPanel(new GridBagLayout());
        pnlProjectDetails.add(tfProjectDetails, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblProjectDetails,        GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(pnlProjectDetails,        GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        //Portierungskennung PKIauf für GF RRNP
        if (isGfRrnp) {
            AKJPanel pnlPKIAuf = new AKJPanel(new GridBagLayout());
            pnlPKIAuf.add(tfPKIAuf, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));

            childWita.add(lblPKIAuf,            GBCFactory.createGBC(0,   0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlPKIAuf,            GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));
        }

        if (isGfKueMrn || isGfRrnp) {
            AKJPanel pnlSelectAllNumbers = new AKJPanel(new GridBagLayout());
            pnlSelectAllNumbers.add(chkSelectAllNumbers,   GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            pnlSelectAllNumbers.add(lblSelectAllNumbers,   GBCFactory.createGBC(  100, 0, 1, 0, 100, 1, GridBagConstraints.HORIZONTAL, 2));

            tbMdlRufnummer = new RufnummerPortierungTableModel();
            AKJTable tbRufnummer = new AKJTable(tbMdlRufnummer, JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tbRufnummer.fitTable(new int[]{65, 50, 70, 70, 70, 70, 80});
            TableColumn col = tbRufnummer.getColumnModel().getColumn(RufnummerPortierungTableModel.COL_PORTIERUNG_AM);
            col.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
            tbRufnummer.addTableListener(this);
            AKJScrollPane spRufnummer = new AKJScrollPane(tbRufnummer, new Dimension(570, 150));

            childWita.add(lblRufnummern ,       GBCFactory.createGBC( 0,  0,  0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlSelectAllNumbers , GBCFactory.createGBC(100, 0,  1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));
            childWita.add(spRufnummer ,         GBCFactory.createGBC(100,100, 1, ++yCoordinate, 1, 1, GridBagConstraints.BOTH));
        } else {
            AKJPanel pnlIdentification = new AKJPanel(new GridBagLayout());
            pnlIdentification.add(ftfIdentificationOnkz,    GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            pnlIdentification.add(ftfIdentificationNumber,  GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(lblIdentification,    GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
            childWita.add(pnlIdentification,    GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));

        }
        childWita.add(lblAutomatable,    GBCFactory.createGBC(  0, 0, 0, ++yCoordinate, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        childWita.add(chkAutomatable,    GBCFactory.createGBC(100, 0, 1, yCoordinate,   1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    /**
     * Initialises the CbTimeSlot (i.e. the Portierungszeitfenster), depending on the user role and WBCI
     * geschaeftsfall.
     */
    private void initialiseCbTimeSlot() {
        // select the default zeitfenster based on the geschaeftsfall type
        switch (this.wbciGeschaeftsfallTyp) {
            case VA_RRNP:
                cbTimeSlot.setSelectedItem(Portierungszeitfenster.ZF1);
                break;
            case VA_KUE_MRN:
                cbTimeSlot.setSelectedItem(Portierungszeitfenster.ZF2);
                break;
            default:
                break;
        }
    }

    private Set<String> getCancelledVorabstimmungIdsWithMissingVaRequest() {
        Long billingOrderNumber = getAuftragNoOrig();
        VorabstimmungIdsByBillingOrderNoCriteria criteria = new VorabstimmungIdsByBillingOrderNoCriteria(billingOrderNumber, VorabstimmungsAnfrage.class);
        criteria.addMatchingGeschaeftsfallStatus(WbciGeschaeftsfallStatus.NEW_VA);
        return wbciCommonService.findVorabstimmungIdsByBillingOrderNoOrig(criteria);
    }

    protected List<RufnummerPortierungSelection> getRufnummerPortierungSelections() {
        List<RufnummerPortierungSelection> tableSelectionAsList = new ArrayList<>();

        for (RufnummerPortierungSelection row : tbMdlRufnummer.getData()) {
            if (BooleanTools.nullToFalse(row.getSelected())) {
                tableSelectionAsList.add(row);
            }
        }

        return tableSelectionAsList;
    }

    @Override
    public final void loadData() {
        Long auftragNoOrig = getAuftragNoOrig();
        Long auftragId = getAuftragId();
        if (!GeschaeftsfallTyp.VA_KUE_ORN.equals(wbciGeschaeftsfallTyp)) {
            // Bei GF KUE_ORN macht es keinen Sinn die Rufnummern zu laden, weil die nicht zu portierenden
            // Rufnummern auch nicht in Taifun hinterlegt sind.
            List<RufnummerPortierungSelection> rufnummerList =
                    wbciCommonService.getRufnummerPortierungList(auftragNoOrig);
            tbMdlRufnummer.setData(rufnummerList);
        }

        /**
         * Die Auswahl des WBCI-Carriers ist Aufgabe des Users, da aus den in Taifun hinterlegten PKIs der EKP
         * nicht ermittelt werden kann. <br/>
         * Beispiel:
         *   - PKI auf der Rufnummer ist mit D001 eingetragen (=DTAG)
         *   - WBCI Vorgang kann aber z.B. mit 1&1 durchgefuehrt werden muessen, da der bisherige Provider des Kunden
         *     eben die 1&1 ist (kommt daher, weil die 1&1 kein eigenes Netz betreibt sondern nur als Resellter agiert)
         */
        try {
            if (isEmpty(carrierMapEkp)) {
                carrierMapEkp = new HashMap<>();
                for (Carrier carrier : carrierService.findWbciAwareCarrier()) {
                    carrierMapEkp.put(getCarrierCode(carrier), carrier);
                }
                cbCarrier.addItems(carrierMapEkp.values(), true, Carrier.class);
            }
            if (carrierMapEkp.size() == 1) {
                cbCarrier.setSelectedItem(carrierMapEkp.values().iterator().next());
            }
        }
        catch (FindException e) {
            throw new WbciBaseException("Ein unerwarteter Fehler beim Ermitteln der Carrier-Liste ist aufgetreten!", e);
        }

        if (GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfallTyp)) {
            String portierungskennungPKIauf = wbciCommonService.getTnbKennung(auftragId);
            tfPKIAuf.setText(portierungskennungPKIauf);
        }
        // Fuer den GF RRPN wird keine Adresse, aber es werden Personendaten benötigt.
        Adresse anschlussAdresse = wbciCommonService.getAnschlussAdresse(auftragNoOrig);
        if (anschlussAdresse != null) {
            fillAddressFields(anschlussAdresse);
        }

        //Übernahme des Vorgabe-AM-Datums
        Date vorgabeAM = getAuftragDaten().getVorgabeSCV();
        if (vorgabeAM != null && DateTools.isDateAfter(vorgabeAM, new Date())) {
            dcCustomerDesiredDate.setDate(vorgabeAM);
        }

        if (!strAenVas.isEmpty()) {
            cbStrAenVas.addItem("", null);
            cbStrAenVas.addItems(strAenVas);
            checkAndPreselectStrAenVorabstimmungsId();
        }
        initChkAutomatable(auftragNoOrig);
    }

    protected void initChkAutomatable(Long auftragNoOrig) {
        chkAutomatable.setSelected(false);
        chkAutomatable.setEnabled(false);

        if (featureService.isFeatureOnline(Feature.FeatureName.WBCI_WITA_AUTO_PROCESSING)) {
            final KundenTyp kundenTyp;
            try {
                kundenTyp = wbciCommonService.getKundenTyp(auftragNoOrig);
            }
            catch (FindException e) {
                throw new WbciBaseException("Ein unerwarteter Fehler beim Ermitteln des Kundentyps für den Auftrag ist aufgetreten!", e);
            }
            chkAutomatable.setEnabled(true);
            chkAutomatable.setSelected(KundenTyp.PK.equals(kundenTyp));
        }
    }

    /**
     * Checks to see if a STR-AEN VorabstimmungsId should be preselected in the drop-down list and if required
     * automatically selects it. A preselection is typically required when a new Vorabstimmung is created to replace a
     * previously cancelled vorabstimmung.
     */
    private void checkAndPreselectStrAenVorabstimmungsId() {
        String preselectedVorabstimmungsId = (String) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_WBCI_VORABSTIMMUNGSID);
        if (preselectedVorabstimmungsId != null) {
            cbStrAenVas.setSelectedItem(preselectedVorabstimmungsId);
        }
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case STR_AEN_VORABSTIMMUNGEN:
                fillFormDataUsingStrAenData(getSelectedStrAenVorabstimmungsId());
                break;
            case SELECT_ALL_NUMBERS:
                selectAllNumbers();
                break;
            default:
                super.execute(command);
                break;
        }
    }

    /**
     * Returns the selected Str-Aen VA Id from the StrAen combo. If no VA Id is selected, then an empty string is
     * returned.
     *
     * @return
     */
    protected String getSelectedStrAenVorabstimmungsId() {
        return (String) cbStrAenVas.getSelectedItem();
    }

    private void fillFormDataUsingStrAenData(String strAenVa) {
        LocalDateTime toSet = null;
        if (StringUtils.isNotEmpty(strAenVa)) {
            showWarningIfStrAenVaHasFaultyAutomationTasks(strAenVa);
            WbciGeschaeftsfall strAenGf = wbciCommonService.findWbciGeschaeftsfall(strAenVa);
            if (strAenGf.getWechseltermin() != null
                    && ChronoUnit.DAYS.between(LocalDate.now(), strAenGf.getWechseltermin()) >= WitaConstants.MINDESTVORLAUFZEIT) {
                toSet = strAenGf.getWechseltermin().atStartOfDay();
            }
            cbCarrier.setSelectedItem(carrierMapEkp.get(strAenGf.getAbgebenderEKP()));
            cbCarrier.setEnabled(false);
        }
        else {
            cbCarrier.setEnabled(true);
        }

        dcCustomerDesiredDate.setDateTime(toSet);
    }

    private void showWarningIfStrAenVaHasFaultyAutomationTasks(String strAenVa) {
        try {
            wbciValidationService.assertLinkedVaHasNoFaultyAutomationTasks(strAenVa);
        }
        catch (WbciValidationException e) {
            String title = "Die Vorabstimmung hat noch fehlerhafte Automatisierungsschritte!";
            Object[] options = { "OK" };
            MessageHelper.showOptionDialog(this, e.getMessage(), title, JOptionPane.WARNING_MESSAGE, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        }
    }

    private void fillAddressFields(Adresse anschlussAdresse) {
        final PersonOderFirma personOderFirma = HurricanToWbciConverter.extractPersonOderFirma(anschlussAdresse);
        if (PersonOderFirmaTyp.FIRMA == personOderFirma.getTyp()) {
            final Firma firma = (Firma) personOderFirma;
            tfCompanyName.setText(firma.getFirmenname());
            tfCompanyNameAdditional.setText(firma.getFirmennamenZusatz());
            rbCompany.doClick();
        }
        else if (PersonOderFirmaTyp.PERSON == personOderFirma.getTyp()) {
            final Person person = (Person) personOderFirma;
            tfSurname.setText(person.getNachname());
            tfForename.setText(person.getVorname());
            cbTitle.setSelectedItem(person.getAnrede());
            rbPerson.doClick();
        }

        AddressModel location;
        try {
            location = availabilityService.getDtagAddressForCb(anschlussAdresse.getGeoId(), anschlussAdresse);
        }
        catch (FindException e) {
            // just log the exception and use the original address for further processing.
            LOGGER.warn(String.format("Error loading the DTAG address for GeoId %s:", anschlussAdresse.getGeoId()), e);
            location = anschlussAdresse;
        }

        final Standort standort = HurricanToWbciConverter.extractStandort(location);
        ftfZip.setText(standort.getPostleitzahl());
        tfCity.setText(standort.getOrt());
        tfStreet.setText(standort.getStrasse().getStrassenname());
        ftfStreetNumber.setText(standort.getStrasse().getHausnummer());
        tfStreetNumberAdditional.setText(standort.getStrasse().getHausnummernZusatz());
    }

    protected CarrierCode getAbgebenderCarrier() {
        return getCarrierCode((Carrier) cbCarrier.getSelectedItem());
    }

    protected CarrierCode getCarrierCode(Carrier carrier) {
        final String ituCarrierCode = carrier != null ? carrier.getItuCarrierCode() : null;
        return ituCarrierCode != null ? CarrierCode.fromITUCarrierCode(ituCarrierCode) : null;
    }

    protected LocalDate getCustomerDesiredDate() {
        Date customerDesiredDate = dcCustomerDesiredDate.getDate(null);
        if (customerDesiredDate != null) {
            return DateConverterUtils.asLocalDate(customerDesiredDate);

        }
        return null;
    }

    protected Portierungszeitfenster getSelectedPortierungszeitfenster() {
        Object selectedItem = cbTimeSlot.getSelectedItem();
        if (selectedItem != null) {
            return (Portierungszeitfenster) selectedItem;
        }
        return null;
    }

    protected Projekt getProjekt() {
        if (StringUtils.isEmpty(tfProjectDetails.getText())) {
            return null;
        }
        return new ProjektBuilder()
                .withProjektKenner(tfProjectDetails.getText())
                .build();
    }

    /**
     * Returns null if the specified parameter is an empty string
     */
    protected String getNullableString(String text) {
        return StringUtils.isEmpty(text) ? null : text;
    }

    protected PersonOderFirma getPersonOderFirma() {
        return getPersonOderFirma(rbCompany, tfCompanyNameAdditional, tfCompanyName, tfForename, tfSurname, cbTitle);
    }

    protected PersonOderFirma getWeitererAnschlussinhaber() {
        return getPersonOderFirma(rbCompanyWAI, tfCompanyNameAdditionalWAI, tfCompanyNameWAI, tfForenameWAI, tfSurnameWAI, cbTitleWAI);
    }

    private PersonOderFirma getPersonOderFirma(AKJRadioButton rbCompany, AKJTextField tfCompanyNameAdditional,
            AKJTextField tfCompanyName, AKJTextField tfForename,
            AKJTextField tfSurname, AKJComboBox cbTitle) {
        PersonOderFirma personOderFirma;
        if (rbCompany.isSelected()) {
            String firmenZusatz = getNullableString(tfCompanyNameAdditional.getText());
            String firmenName = getNullableString(tfCompanyName.getText());
            personOderFirma = new FirmaBuilder()
                    .withFirmename(firmenName)
                    .withFirmennamenZusatz(firmenZusatz)
                    .withAnrede(Anrede.FIRMA)
                    .build();
        }
        else {
            String vorname = getNullableString(tfForename.getText());
            String nachname = getNullableString(tfSurname.getText());
            personOderFirma = new PersonBuilder()
                    .withVorname(vorname)
                    .withNachname(nachname)
                    .withAnrede((Anrede) cbTitle.getSelectedItem())
                    .build();
        }
        return personOderFirma;
    }

    protected Standort getStandort() {
        String hausnummerZusatz = getNullableString(tfStreetNumberAdditional.getText());
        String hausnummer = getNullableString(ftfStreetNumber.getText());
        String strassenName = getNullableString(tfStreet.getText());

        Strasse strasse = null;
        if (!(hausnummer == null && hausnummerZusatz == null && strassenName == null)) {
            strasse = new StrasseBuilder()
                    .withHausnummer(hausnummer)
                    .withHausnummernZusatz(hausnummerZusatz)
                    .withStrassenname(strassenName)
                    .build();
        }

        String ort = getNullableString(tfCity.getText());
        String plz = getNullableString(ftfZip.getText());

        if (ort == null && plz == null && strasse == null) {
            return null;
        }
        return new StandortBuilder()
                .withOrt(ort)
                .withPostleitzahl(plz)
                .withStrasse(strasse)
                .build();
    }

    /**
     * Ermittelt den technischen Auftrag
     */
    private AuftragDaten getAuftragDaten() {
        return (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
    }

    private Long getAuftragNoOrig() {
        AuftragDaten ad = getAuftragDaten();
        final Long auftragNoOrig = ad.getAuftragNoOrig();
        if (auftragNoOrig == null) {
            throw new IllegalStateException("Keine AuftragNoOrig innerhalb vom AuftragDaten-Objekt gesetzt.");
        }
        return auftragNoOrig;
    }

    /**
     * Ermittelt die technische Auftragsnummer
     */
    protected Long getAuftragId() {
        AuftragDaten ad = getAuftragDaten();
        final Long auftragId = ad.getAuftragId();
        if (auftragId == null) {
            throw new IllegalStateException("Keine AuftragId innerhalb vom AuftragDaten-Objekt gesetzt.");
        }
        return auftragId;
    }

    /**
     * Ermittelt die Taifun-Auftragsnummer
     */
    protected Long getTaifunAuftragId() {
        AuftragDaten ad = getAuftragDaten();
        final Long taifunAuftragId = ad.getAuftragNoOrig();
        if (taifunAuftragId == null) {
            throw new IllegalStateException("Keine Taifun-AuftragId (AuftragNoOrig) innerhalb vom AuftragDaten-Objekt gesetzt.");
        }
        return taifunAuftragId;
    }

    @Override
    public void finish() throws AKJWizardFinishVetoException {
        try {
            createAndSendVorabstimmungsAnfrage();
            MessageHelper.showInfoDialog(this, MSG_WBCI_PROCESS_CREATED);
        }
        catch (WbciValidationException e) {
            displayErrorMessage(e.getMessage());
        }
        catch (RuntimeException e) {
            MessageHelper.showErrorDialog(this, e);
            throw new AKJWizardFinishVetoException(e);
        }
    }

    /**
     * Generates the VA Request, using the details entered within the GUI and invokes the remote hurrican servier for
     * creating the VA.
     */
    public abstract void createAndSendVorabstimmungsAnfrage() throws AKJWizardFinishVetoException;

    protected void validateWbciRequest(WbciRequest wbciRequest) throws AKJWizardFinishVetoException {
        if (wbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP() == null) {
            displayErrorMessage("Es muss ein abgegebender Carrier ausgewählt werden!");
        }
        Set<ConstraintViolation<WbciRequest>> errors = wbciValidationService.checkWbciMessageForErrors(
                wbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP(), wbciRequest);
        if (errors != null && !errors.isEmpty()) {
            String errorMsg = new ConstraintViolationHelper().generateErrorMsg(errors);
            displayErrorMessage(errorMsg);
        }
        else {
            Set<ConstraintViolation<WbciRequest>> warnings = wbciValidationService.checkWbciMessageForWarnings(
                    wbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP(), wbciRequest);
            if (warnings != null && !warnings.isEmpty()) {
                String warningMsg = new ConstraintViolationHelper().generateWarningMsg(warnings);
                displayWarningMessage(warningMsg);
            }
        }
    }

    private void displayWarningMessage(String warningMsg) throws AKJWizardFinishVetoException {
        String title = "Vorabstimmungsanfrage abschicken?";
        int result = MessageHelper.showYesNoQuestion(this, warningMsg, title);
        if (result == JOptionPane.NO_OPTION) {
            throw new AKJWizardFinishVetoException(warningMsg);
        }
    }

    private void displayErrorMessage(String errorMsg) throws AKJWizardFinishVetoException {
        String title = "Fehler in der Vorabstimmung!";
        Object[] options = { "OK" };
        MessageHelper.showOptionDialog(this, errorMsg, title, JOptionPane.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        throw new AKJWizardFinishVetoException(errorMsg);
    }

    /**
     * selektiert / deselektiert alle Rufnummern in der Rufnummern-Tabelle
     */
    private void selectAllNumbers() {
        for (RufnummerPortierungSelection rnr : tbMdlRufnummer.getData()) {
            rnr.setSelected(chkSelectAllNumbers.isSelected());
        }
        tbMdlRufnummer.fireTableDataChanged();
    }

    protected Boolean isWeitererAnschlussinhaberSelected() {
        return chkWAI.isSelectedBoolean(false);
    }

    private void hideWaiPanel() {
        makePnlPersonOrCompanyWAIVisible(false);
        makePnlCompanyWaiVisible(false);
        makePnlSurnameVornameWaiVisible(false);
    }

    private void showWaiPanel() {
        makePnlPersonOrCompanyWAIVisible(true);
        if (rbCompanyWAI.isSelected()) {
            makePnlCompanyWaiVisible(true);
        }
        else if (rbPersonWAI.isSelected()) {
            makePnlSurnameVornameWaiVisible(true);
        }
    }

    private void makePnlCompanyVisible(boolean visible) {
        pnlCompanyName.setVisible(visible);
        lblCompanyName.setVisible(visible);
    }

    private void makePnlSurnameVornameVisible(boolean visible) {
        pnlTitleSurnameForename.setVisible(visible);
        lblTitleSurnameForename.setVisible(visible);
    }

    private void makePnlCompanyWaiVisible(boolean visible) {
        pnlCompanyNameWAI.setVisible(visible);
        lblCompanyNameWAI.setVisible(visible);
    }

    private void makePnlSurnameVornameWaiVisible(boolean visible) {
        pnlTitleSurnameForenameWAI.setVisible(visible);
        lblTitleSurnameForenameWAI.setVisible(visible);
    }

    private void makePnlPersonOrCompanyWAIVisible(boolean visible) {
        pnlPersonOrCompanyWAI.setVisible(visible);
        lblPersonOrCompanyWAI.setVisible(visible);
    }

    @Override
    public void showDetails(Object details) {
        final RufnummerPortierungSelection portierungSelection = (RufnummerPortierungSelection) details;
        portierungSelection.setSelected(!portierungSelection.getSelected());
        tbMdlRufnummer.fireTableDataChanged();
    }

    protected boolean isAlleRufnummernPortieren() {
        return chkPortAllNumbers.isSelected();
    }

    protected boolean isAutomatable() {
        return chkAutomatable.isSelected();
    }

    private class RBActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == rbCompany) {
                makePnlCompanyVisible(true);
                makePnlSurnameVornameVisible(false);
            }
            else if (e.getSource() == rbPerson) {
                makePnlCompanyVisible(false);
                makePnlSurnameVornameVisible(true);
            }
            else if (e.getSource() == rbCompanyWAI) {
                makePnlCompanyWaiVisible(true);
                makePnlSurnameVornameWaiVisible(false);
            }
            else if (e.getSource() == rbPersonWAI) {
                makePnlCompanyWaiVisible(false);
                makePnlSurnameVornameWaiVisible(true);
            }
        }
    }

    private class ChkBActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == chkWAI) {
                if (!isWeitererAnschlussinhaberSelected()) {
                    hideWaiPanel();
                }
                else {
                    showWaiPanel();
                }
            }
        }
    }

}
