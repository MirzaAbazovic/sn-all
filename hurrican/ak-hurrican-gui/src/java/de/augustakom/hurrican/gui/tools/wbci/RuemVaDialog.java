/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import static de.mnet.wbci.model.helper.TechnischeRessourceHelper.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.wbci.tables.MeldungsCodesTableModel;
import de.augustakom.hurrican.gui.tools.wbci.tables.RufnummernPortierungTableModel;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungVOHelper;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.helper.TechnischeRessourceHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDecisionService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

/**
 * Dialog, um eine WBCI 'RUEM-VA' Meldung zu generieren.
 */
public class RuemVaDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RuemVaDialog.class);
    private static final long serialVersionUID = 8995716834738365832L;
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/RuemVaDialog.xml";
    private static final String DIALOG_TITLE = "dialog.title";
    private static final String DETAILS = "details";
    private static final String MELDE_CODES = "melde.codes";
    private static final String RUEMVA_TERMIN = "ruemva.termin";
    private static final String MNET_TECHNOLOGY = "mnet.technology";
    private static final String VTR_NRS = "vtr.nrs";
    private static final String SEND_TXT = "send.txt";
    private static final String SEND_TOOLTIP = "send.tooltip";
    private static final String ALL_NUMBERS = "all.numbers";
    private static final String PHONE_NUMBERS = "phone.numbers";
    private static final String AUTOMATABLE_GESCHAEFTSFALL = "automatable";

    private final WbciRequest wbciRequest;
    private MeldungsCodesTableModel tbMdlRuemVa;
    private AKJDateComponent dcRuemVaTermin;
    private AKJTextField tfTechnology;
    private AKJList lsVtrNrs;
    private AKJCheckBox chkAllNumbers;
    private AKJCheckBox chkAutomatable;
    private RufnummernPortierungTableModel tbMdlRufnummer;
    // Services
    private WbciDecisionService wbciDecisionService;
    private WbciMeldungService wbciMeldungService;
    private WbciCommonService wbciCommonService;
    private WbciValidationService wbciValidationService;
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private FeatureService featureService;

    private Collection<DecisionVO> decisionVOs;
    private Map<String, TechnischeRessource> technischeResourcen;

    public RuemVaDialog(@NotNull WbciRequest wbciRequest, @NotNull Collection<DecisionVO> decisionVOs) {
        super(RESOURCE, true, true);
        this.wbciRequest = wbciRequest;
        this.decisionVOs = new ArrayList<>(decisionVOs);

        // nur Decisions mit einem finalen MeldungsCode verwenden!
        CollectionUtils.filter(decisionVOs, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return (object instanceof DecisionVO && ((DecisionVO) object).getFinalMeldungsCode() != null);
            }
        });

        try {
            initServices();
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
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected void initServices() throws ServiceNotFoundException {
        wbciMeldungService = getCCService(WbciMeldungService.class);
        wbciDecisionService = getCCService(WbciDecisionService.class);
        wbciCommonService = getCCService(WbciCommonService.class);
        wbciValidationService = getCCService(WbciValidationService.class);
        wbciGeschaeftsfallService = getCCService(WbciGeschaeftsfallService.class);
        featureService = getCCService(FeatureService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(String.format(getSwingFactory().getText(DIALOG_TITLE), wbciRequest.getWbciGeschaeftsfall()
                .getVorabstimmungsId()));

        AKJLabel lblDetails = getSwingFactory().createLabel(DETAILS, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblRuemVaTermin = getSwingFactory().createLabel(RUEMVA_TERMIN);
        AKJLabel lblTechnology = getSwingFactory().createLabel(MNET_TECHNOLOGY);
        AKJLabel lblVtrNrs = getSwingFactory().createLabel(VTR_NRS);
        AKJLabel lblAutomatable = getSwingFactory().createLabel(AUTOMATABLE_GESCHAEFTSFALL);

        dcRuemVaTermin = getSwingFactory().createDateComponent(RUEMVA_TERMIN);
        dcRuemVaTermin.addFocusListener(new KwtChangeListener());
        dcRuemVaTermin.addPropertyChangeListener(AKJDateComponent.PROPERTY_CHANGE_DATE, new KwtChangeListener());
        tfTechnology = getSwingFactory().createTextField(MNET_TECHNOLOGY, false);
        lsVtrNrs = getSwingFactory().createList(VTR_NRS);
        lsVtrNrs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        AKJScrollPane spVtrNrs = new AKJScrollPane(lsVtrNrs, new Dimension(120, 80));
        chkAutomatable = getSwingFactory().createCheckBox(AUTOMATABLE_GESCHAEFTSFALL);

        AKJLabel lblAllNumbers = getSwingFactory().createLabel(ALL_NUMBERS);
        chkAllNumbers = getSwingFactory().createCheckBox(ALL_NUMBERS);
        chkAllNumbers.setEnabled(false);

        AKJLabel lblRufnummern = getSwingFactory().createLabel(PHONE_NUMBERS);
        tbMdlRufnummer = new RufnummernPortierungTableModel();
        AKJTable tbRufnummer = new AKJTable(tbMdlRufnummer, JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbRufnummer.fitTable(new int[] { 40, 100, 80, 30, 30, 50 });
        tbRufnummer.setEnabled(false);
        AKJScrollPane spRufnummer = new AKJScrollPane(tbRufnummer, new Dimension(250, 90));
        spRufnummer.setPreferredSize(new Dimension(280, 100));

        AKJLabel lblCodes = getSwingFactory().createLabel(MELDE_CODES, AKJLabel.LEFT, Font.BOLD);

        tbMdlRuemVa = new MeldungsCodesTableModel();
        AKJTable tbRuemVaCodes = new AKJTable(tbMdlRuemVa, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRuemVaCodes.fitTable(new int[] { 80, 250, 150 });

        AKJScrollPane spTable = new AKJScrollPane(tbRuemVaCodes, new Dimension(500, 200));

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblDetails       , GBCFactory.createGBC(  0,  0, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblRuemVaTermin  , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(dcRuemVaTermin   , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        if (!GeschaeftsfallTyp.VA_RRNP.equals(wbciRequest.getWbciGeschaeftsfall().getTyp())) {
            dtlPnl.add(lblTechnology, GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            dtlPnl.add(tfTechnology , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));

            dtlPnl.add(lblVtrNrs    , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
            dtlPnl.add(spVtrNrs     , GBCFactory.createGBC(100,  0, 3, 4, 1, 2, GridBagConstraints.HORIZONTAL));
            dtlPnl.add(new AKJPanel(),GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.VERTICAL));
        }

        dtlPnl.add(lblAllNumbers    , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        dtlPnl.add(chkAllNumbers    , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        dtlPnl.add(lblRufnummern    , GBCFactory.createGBC(  0,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        dtlPnl.add(spRufnummer      , GBCFactory.createGBC(100,  0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        dtlPnl.add(lblAutomatable   , GBCFactory.createGBC(  0,  0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        dtlPnl.add(chkAutomatable   , GBCFactory.createGBC(100,  0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        dtlPnl.add(lblCodes         , GBCFactory.createGBC(  0,  0, 1, 9, 3, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(spTable          , GBCFactory.createGBC(  0,  0, 1,10, 3, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 4,11, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, getSwingFactory().getText(SEND_TXT), getSwingFactory().getText(SEND_TOOLTIP), true, true);
    }

    @Override
    public final void loadData() {
        try {
            tbMdlRuemVa.removeAll();
            this.decisionVOs = wbciDecisionService.getDecisionVOsForMeldungPositionTyp(decisionVOs,
                    MeldungPositionTyp.RUEM_VA);
            tbMdlRuemVa.setData(decisionVOs);

            DecisionVO decisionKW = DecisionVOHelper.findKundenwunschterminVo(decisionVOs);
            if (decisionKW != null) {
                Date dcDate = DateTools.parse(DateTools.PATTERN_DAY_MONTH_YEAR, decisionKW.getFinalValue(), null);
                dcRuemVaTermin.setDate(dcDate);
            }

            chkAllNumbers.setSelected(DecisionVOHelper.isAlleRufnummernPortieren(decisionVOs));
            tbMdlRufnummer.setData(DecisionVOHelper.extractActiveRufnummernportierung(decisionVOs));
            if (wbciRequest.getWbciGeschaeftsfall().getMnetTechnologie() != null) {
                tfTechnology.setText(wbciRequest.getWbciGeschaeftsfall().getMnetTechnologie().getWbciTechnologieCode());
            }

            if (!GeschaeftsfallTyp.VA_RRNP.equals(wbciRequest.getWbciGeschaeftsfall().getTyp())) {
                technischeResourcen = TechnischeRessourceHelper.convertToMap(
                        wbciCommonService.getTechnischeRessourcen(
                                wbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig(),
                                wbciRequest.getWbciGeschaeftsfall().getNonBillingRelevantOrderNoOrigs())
                );
            }
            else {
                technischeResourcen = Collections.EMPTY_MAP;
            }

            lsVtrNrs.addItems(technischeResourcen.keySet());
            lsVtrNrs.selectAll();

            initChkAutomatable();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            if (e instanceof WbciServiceException) {
                this.cancel();
            }
        }
    }

    protected void initChkAutomatable() {
        chkAutomatable.setSelected(false);
        chkAutomatable.setEnabled(false);

        if (featureService.isFeatureOnline(Feature.FeatureName.WBCI_ABGEBEND_RUEMVA_AUTO_PROCESSING)) {
            chkAutomatable.setEnabled(true);
            chkAutomatable.setSelected(KundenTyp.PK.equals(wbciRequest.getWbciGeschaeftsfall().getEndkunde().getKundenTyp()));
        }
    }

    @Override
    protected void cancel() {
        prepare4Close();
        setValue(null);
    }

    @Override
    protected void doSave() {
        try {
            Date wechseltermin = dcRuemVaTermin.getDate(null);

            // RUEM-VA erstellen
            RueckmeldungVorabstimmungBuilder ruemVaBuilder = new RueckmeldungVorabstimmungBuilder()
                    .withTechnologie(Technologie.lookUpWbciTechnologieCode(tfTechnology.getText(null)))
                    .withTechnischeRessourcen(createTechnischeRessourcen())
                    .withWechseltermin(DateConverterUtils.asLocalDate(wechseltermin));

            // Rufnummernportierung hinzufügen
            if (wbciRequest.getWbciGeschaeftsfall() instanceof RufnummernportierungAware
                    && tbMdlRufnummer.getData() != null && !tbMdlRufnummer.getData().isEmpty()) {
                ruemVaBuilder.withRufnummernportierung(
                        RufnummernportierungVOHelper.convertToRuemVaRufnummerportierung(
                                tbMdlRufnummer.getData(),
                                ((RufnummernportierungAware) wbciRequest.getWbciGeschaeftsfall()))
                );
            }

            // attach the current WbciGescheftsfall for the BeanValidation
            ruemVaBuilder.withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall());

            // RUEM-VA Meldungspositionen erstellen
            ruemVaBuilder.addMeldungPositionen(DecisionVOHelper.extractMeldungPositionRueckmeldungVa(tbMdlRuemVa
                    .getData()));

            final RueckmeldungVorabstimmung rueckmeldungVorabstimmung = ruemVaBuilder.build();

            if (isRuemVaValid(rueckmeldungVorabstimmung)) {
                wbciMeldungService.createAndSendWbciMeldung(
                        rueckmeldungVorabstimmung,
                        wbciRequest.getVorabstimmungsId());

                if (chkAutomatable.isSelected() != wbciRequest.getWbciGeschaeftsfall().getAutomatable()) {
                    wbciGeschaeftsfallService.markGfAsAutomatable(wbciRequest.getWbciGeschaeftsfall().getId(),
                        chkAutomatable.isSelected());
                }

                prepare4Close();
                setValue(wbciRequest);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Generiert aus allen selektierten WITA-Vertragsnummern 'TechnischeRessource' Objekte und liefert
     * diese als Set zurueck.
     */
    private Set<TechnischeRessource> createTechnischeRessourcen() {
        List<String> selectedValues = lsVtrNrs.getSelectedValues(String.class);
        if (selectedValues == null) {
            selectedValues = Collections.EMPTY_LIST;
        }
        Map<String, TechnischeRessource> filteredMap = filterKeys(technischeResourcen, selectedValues);
        return new HashSet<>(filteredMap.values());
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }


    /**
     * Focus- and PropertyChangeListener auf das KWT-Feld, um ein evtl. ueberschriebenes (oder ausgewähltes) KWT-Datum
     * zurueck in das DecisionVO Objekt zu schreiben.
     */
    class KwtChangeListener implements FocusListener, PropertyChangeListener {
        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            checkAndUpdateKundenwunschtermin();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            checkAndUpdateKundenwunschtermin();
        }

        private void checkAndUpdateKundenwunschtermin() {
            if (dcRuemVaTermin.getDate(null) == null) {
                return;
            }

            DecisionVO dateVo = DecisionVOHelper.findKundenwunschterminVo(decisionVOs);
            Date selectedDate = dcRuemVaTermin.getDate(null) != null ? Date.from(DateTools.stripTimeFromDate(DateConverterUtils.asLocalDateTime(dcRuemVaTermin.getDate(null))).atZone(ZoneId.systemDefault()).toInstant()) : null;
            String selectedDateString = DateTools.formatDate(selectedDate, DateTools.PATTERN_DAY_MONTH_YEAR);

            if (dateVo.getPropertyValue().equals(selectedDateString)) {
                dateVo.setFinalMeldungsCode(MeldungsCode.ZWA);
            }
            else {
                dateVo.setFinalMeldungsCode(MeldungsCode.NAT);
            }

            dateVo.setFinalResult(DecisionResult.MANUELL);
            dateVo.setFinalValue(selectedDateString);

            tbMdlRuemVa.removeAll();
            tbMdlRuemVa.setData(decisionVOs);
            tbMdlRuemVa.fireTableDataChanged();
        }
    }

    protected boolean isRuemVaValid(RueckmeldungVorabstimmung ruemVa) {
        Set<ConstraintViolation<RueckmeldungVorabstimmung>> errors =
                wbciValidationService.checkWbciMessageForErrors(ruemVa.getEKPPartner(), ruemVa);
        if (errors != null && !errors.isEmpty()) {
            String title = "Die Rueckmeldung zur Vorabstimmung ist nicht vollstaendig";
            String errorMsg = new ConstraintViolationHelper().generateErrorMsg(errors);
            Object[] options = { "OK" };
            MessageHelper.showOptionDialog(this, errorMsg, title, JOptionPane.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            return false;
        }
        else {
            Set<ConstraintViolation<RueckmeldungVorabstimmung>> warnings =
                    wbciValidationService.checkWbciMessageForWarnings(ruemVa.getEKPPartner(), ruemVa);
            if (warnings != null && !warnings.isEmpty()) {
                String title = "Rueckmeldung zur Vorabstimmung abschicken?";
                String warningMsg = new ConstraintViolationHelper().generateWarningMsg(warnings);
                int result = MessageHelper.showYesNoQuestion(this, warningMsg, title);
                if (result == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
        }
        return true;
    }

}
