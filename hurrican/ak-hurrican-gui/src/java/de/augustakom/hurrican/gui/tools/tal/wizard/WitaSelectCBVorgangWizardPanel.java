/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2005 09:37:04
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import static de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.wizard.wbci.WbciGeschaeftsfallKueMrnPanel;
import de.augustakom.hurrican.gui.tools.tal.wizard.wbci.WbciGeschaeftsfallKueOrnPanel;
import de.augustakom.hurrican.gui.tools.tal.wizard.wbci.WbciGeschaeftsfallRRPnPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.WitaSelectionUtils;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * WizardPanel, um den Typ des TAL-Bestellungsvorgangs auszuwaehlen. <br> Ist der Ziel-Carrier DTAG wird auch noch der
 * DTAG-Geschaeftsfall zur Auswahl angeboten und geprueft.
 * <p/>
 * Wird nur im WITA-Modus verwendet.
 */
public class WitaSelectCBVorgangWizardPanel extends AbstractServiceWizardPanel implements AKDataLoaderComponent,
        PropertyChangeListener, ActionListener {

    private static final long serialVersionUID = 9189370588010991185L;

    private static final Logger LOGGER = Logger.getLogger(WitaSelectCBVorgangWizardPanel.class);

    private static final int MAX_SELECTION_COUNT_4_KLAMMER = 19;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/WitaSelectCBVorgangWizardPanel.xml";
    private static final String KLAMMERUNG = "klammerung";
    private static final String VIER_DRAHT = "vierDraht";
    private static final String VORMIETER = "vormieter";
    private static final String NO_SELECTION = "no.selection";
    private static final String LBZ_96Q_NOT_ALLOWED = "lbz.96q.not.allowed";
    private static final String CB_VORGANG_TYP = "cb.vorgang.typ";
    private static final String GF_TYP_INTERN = "gf.typ.intern";
    private static final String SUB_TITLE = "sub.title";
    private static final String SUB_TITLE_WBCI = "sub.title.wbci";
    private static final String LBL_VA_KUE_MRN = "create.va.kue.mrn";
    private static final String LBL_VA_KUE_ORN = "create.va.kue.orn";
    private static final String LBL_VA_KUE_RRNP = "create.va.kue.rrnp";

    // wbci buttons
    private AKJButton btnVaKueMRn;
    private AKJButton btnVaKueORn;
    private AKJButton btnVaKueRRnP;

    protected AKReferenceField rfCBVorgangTyp;
    private AKReferenceField rfGfTypIntern;
    private AKJCheckBox chbKlammerung;
    private AKJCheckBox chbVierDraht;
    private AKJCheckBox chbVormieter;

    static final String TEST_AM_USER = "test.am.user";
    private static final String NO_TEST_AM_USER = "no.test.am.user";
    private static final String INCORRECT_TEST_AM_USER = "incorrect.test.am.user";
    private AKJTextField tfTestAmUser;

    private Long carrierId;
    private Carrierbestellung carrierbestellung;

    private WitaTalOrderService witaTalOrderService;
    private CarrierService carrierService;
    private HVTService hvtService;
    private QueryCCService queryCCService;
    private FeatureService featureService;
    private WbciCommonService wbciCommonService;
    private RangierungsService rangierungsService;

    @SuppressWarnings("squid:ConstructorCallsOverridableMethod")
    public WitaSelectCBVorgangWizardPanel(AKJWizardComponents wizardComponents) {
        super(RESOURCE, wizardComponents);
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

    protected void initServices() throws ServiceNotFoundException {
        witaTalOrderService = getCCService(WitaTalOrderService.class);
        carrierService = getCCService(CarrierService.class);
        hvtService = getCCService(HVTService.class);
        queryCCService = getCCService(QueryCCService.class);
        featureService = getCCService(FeatureService.class);
        wbciCommonService = getCCService(WbciCommonService.class);
        rangierungsService = getCCService(RangierungsService.class);
    }

    @Override
    protected void createGUI() {
        List<AKJPanel> childPanels = new ArrayList<>();

        AKJPanel wbciGui = createWbciGui();
        AKJPanel witaGui = createWitaGui();

        if (!isHideWbciGui()) {
            childPanels.add(wbciGui);
        }

        if (!isHideWitaGui()) {
            childPanels.add(witaGui);
        }
        else {
            setNextButtonEnabled(false);
        }

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());

        int y = 0;
        for (AKJPanel childPanel : childPanels) {
            if (y > 0) {
                child.add(new JSeparator(SwingConstants.HORIZONTAL), GBCFactory.createGBC(0, 0, 0, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
            }
            child.add(childPanel, GBCFactory.createGBC(100, 100, 0, ++y, 1, 1, GridBagConstraints.BOTH));
        }
    }

    private AKJPanel createWitaGui() {
        AKJLabel lblSubTitle = getSwingFactory().createLabel(SUB_TITLE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblCBVorgangTyp = getSwingFactory().createLabel(CB_VORGANG_TYP);
        AKJLabel lblGfTypIntern = getSwingFactory().createLabel(GF_TYP_INTERN);
        AKJLabel lblKlammerung = getSwingFactory().createLabel(KLAMMERUNG);
        AKJLabel lblVierDraht = getSwingFactory().createLabel(VIER_DRAHT);
        AKJLabel lblVormieter = getSwingFactory().createLabel(VORMIETER);

        rfCBVorgangTyp = getSwingFactory().createReferenceField(CB_VORGANG_TYP);
        rfCBVorgangTyp.addPropertyChangeListener(AKReferenceField.PROPERTY_CHANGE_REFERENCE_ID, this);
        rfGfTypIntern = getSwingFactory().createReferenceField(GF_TYP_INTERN);
        rfGfTypIntern.addPropertyChangeListener(AKReferenceField.PROPERTY_CHANGE_REFERENCE_ID, this);
        rfGfTypIntern.setEnabled(false);
        chbKlammerung = getSwingFactory().createCheckBox(KLAMMERUNG);
        chbKlammerung.setEnabled(false);
        chbKlammerung.addActionListener(this);
        chbVierDraht = getSwingFactory().createCheckBox(VIER_DRAHT);
        chbVierDraht.setEnabled(false);
        chbVierDraht.addActionListener(this);
        chbVormieter = getSwingFactory().createCheckBox(VORMIETER);
        chbVormieter.setEnabled(false);
        chbVormieter.addActionListener(this);

        AKJPanel childWita = new AKJPanel(new GridBagLayout());
        // @formatter:off
        childWita.add(new AKJPanel()    , GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        childWita.add(lblSubTitle       , GBCFactory.createGBC(0, 0, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblCBVorgangTyp   , GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        childWita.add(new AKJPanel()    , GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        childWita.add(rfCBVorgangTyp    , GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblGfTypIntern    , GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        childWita.add(rfGfTypIntern     , GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblKlammerung     , GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        childWita.add(chbKlammerung     , GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblVierDraht      , GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        childWita.add(chbVierDraht      , GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        childWita.add(lblVormieter      , GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        childWita.add(chbVormieter      , GBCFactory.createGBC(0, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        int y = 7;
        if (WitaSelectionUtils.isWitaSimulatorTestMode()) {
            AKJLabel lblTestAmUser = getSwingFactory().createLabel(TEST_AM_USER);
            tfTestAmUser = getSwingFactory().createTextField(TEST_AM_USER);
            childWita.add(lblTestAmUser , GBCFactory.createGBC(0, 0, 1, y, 1, 1, GridBagConstraints.HORIZONTAL, 15));
            childWita.add(tfTestAmUser  , GBCFactory.createGBC(0, 0, 3, y, 1, 1, GridBagConstraints.HORIZONTAL));
            y++;
        }
        childWita.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, y, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return childWita;
    }

    private static class WbciPanel extends AKJPanel {

        final AKJButton btnVaKueMRn;
        final AKJButton btnVaKueORn;
        final AKJButton btnVaKueRRnP;

        WbciPanel(final AKJButton btnVaKueMRn, final AKJButton btnVaKueORn, final AKJButton btnVaKueRRnP, final WbciCommonService wbciCommonService, final LongSupplier getAuftragId)    {
            super(new GridBagLayout());
            this.btnVaKueMRn = btnVaKueMRn;
            this.btnVaKueORn = btnVaKueORn;
            this.btnVaKueRRnP = btnVaKueRRnP;

            this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            this.add(generateWBCIPanel(), GBCFactory.createGBC(0, 0, 0, 2, 5, 1, GridBagConstraints.HORIZONTAL));
            this.add(new VorabstimmungFaxPanel(getAuftragId.getAsLong(), wbciCommonService),
                    GBCFactory.createGBC(0, 0, 0, 3, 5, 1, GridBagConstraints.HORIZONTAL));

            this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        private JPanel generateWBCIPanel() {
            JPanel wbciPanel = new JPanel(new FlowLayout());
            wbciPanel.setBorder(new TitledBorder("Vorabstimmung"));
            wbciPanel.add(btnVaKueMRn);
            wbciPanel.add(btnVaKueORn);
            wbciPanel.add(btnVaKueRRnP);
            return wbciPanel;
        }

    }

    private AKJPanel createWbciGui() {
        btnVaKueMRn = getSwingFactory().createButton(LBL_VA_KUE_MRN, this);
        btnVaKueORn = getSwingFactory().createButton(LBL_VA_KUE_ORN, this);
        btnVaKueRRnP = getSwingFactory().createButton(LBL_VA_KUE_RRNP, this);

        final WbciPanel childWbci = new WbciPanel(btnVaKueMRn, btnVaKueORn, btnVaKueRRnP, wbciCommonService, this::getAuftragId);

        manageGUI(new AKManageableComponent[] { btnVaKueMRn, btnVaKueORn, btnVaKueRRnP });

        return childWbci;
    }


    @Override
    public void loadData() {
        try {
            carrierId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CARRIER_ID);
            Long cbId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);
            carrierbestellung = carrierService.findCB(cbId);

            Long hvtIdStandort = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_HVT_STANDORT_ID);
            HVTStandort hvtStandort = hvtService.findHVTStandort(hvtIdStandort);
            boolean isKvz = (hvtStandort != null) && hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);

            List<Reference> refTypes = witaTalOrderService.findPossibleGeschaeftsfaelle(carrierId, isKvz);
            rfCBVorgangTyp.setReferenceList(refTypes);
            rfCBVorgangTyp.setFindService(queryCCService);

            Reference gfTypInternFindExample = new Reference();
            gfTypInternFindExample.setType(Reference.REF_TYPE_TAL_INTERNER_GF_TYP);
            rfGfTypIntern.setReferenceFindExample(gfTypInternFindExample);
            rfGfTypIntern.setFindService(queryCCService);

            // optional angegebenen Vorgang setzen
            Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
            if (cbVorgangTyp != null) {
                rfCBVorgangTyp.setReferenceId(cbVorgangTyp);
            }

            loadSimulatorUser();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void loadSimulatorUser() throws AKAuthenticationException {
        if (WitaSelectionUtils.isWitaSimulatorTestMode()) {
            AKUser user = witaTalOrderService.findUserBySessionId(getSessionId());
            tfTestAmUser.setText(user.getName());
        }
    }

    @Override
    public boolean next() {
        addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP, rfCBVorgangTyp.getReferenceId());
        if (rfGfTypIntern.isEnabled()) {
            addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_TAL_GF_TYP_INTERN, rfGfTypIntern.getReferenceId());
        }

        boolean isKlammerung = chbKlammerung.isSelected();
        boolean isVierDraht = chbVierDraht.isSelected();
        if (isKlammerung) {
            getWizardComponents().addWizardPanelAfter(
                    this,
                    new SelectAuftrag4KlammerWizardPanel(getWizardComponents(), MAX_SELECTION_COUNT_4_KLAMMER,
                            isKlammerung,
                            isVierDraht)
            );
        }
        else if (isVierDraht) {
            getWizardComponents().addWizardPanelAfter(this,
                    new SelectAuftrag4KlammerWizardPanel(getWizardComponents(), 1, isKlammerung, isVierDraht));
        }

        if (CBVorgang.TYP_NEU.equals(rfCBVorgangTyp.getReferenceId()) && chbVormieter.isSelected()) {
            getWizardComponents().addWizardPanelAfter(this, new VormieterWizardPanel(getWizardComponents()));
        }
        else if (CBVorgang.TYP_PORTWECHSEL.equals(rfCBVorgangTyp.getReferenceId())
                || CBVorgang.TYP_HVT_KVZ.equals(rfCBVorgangTyp.getReferenceId())) {
            getWizardComponents().addWizardPanelAfter(this,
                    new SelectAuftrag4ChangeWizardPanel(getWizardComponents()));
        }
        else if (CBVorgang.TYP_ANBIETERWECHSEL.equals(rfCBVorgangTyp.getReferenceId())) {
            getWizardComponents().addWizardPanelAfter(this,
                    new VorabstimmungWizardPanel(getWizardComponents(), isKlammerung));
        }

        getWizardComponents().addWizardPanel(new CBVorgangDetailWizardPanel(getWizardComponents()));

        final boolean nextPanelCanBeOpened = super.next();
        if (!nextPanelCanBeOpened) {
            removePanelsAfter(this);
        }
        return nextPanelCanBeOpened;
    }

    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_BACKWARD) {
            removePanelsAfter(this);
            if (isHideWitaGui()) {
                setNextButtonEnabled(false);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            if (evt.getSource() == rfCBVorgangTyp) {
                cbVorgangTypSelected();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    private void cbVorgangTypSelected() {
        if(rfCBVorgangTyp == null)  {
            return;
        }
        
        final Long cbVorgangTyp = getCbVorgangTyp();
        chbKlammerung.removeActionListener(this);
        chbVierDraht.removeActionListener(this);

        chbKlammerung.setEnabled(false);
        chbVierDraht.setEnabled(false);
        chbVormieter.setEnabled(false);
        chbVormieter.setSelected(false);
        if (witaTalOrderService.isPossibleKlammer4GF(cbVorgangTyp, carrierbestellung)) {
            chbKlammerung.setEnabled(true);
        }
        if (witaTalOrderService.isPossible4Draht4GF(cbVorgangTyp, carrierbestellung)) {
            if (NumberTools
                    .isIn(cbVorgangTyp, new Number[] { CBVorgang.TYP_NEU })) {
                chbVierDraht.setEnabled(true);
            }
            else if (carrierbestellung.is96X() &&
                    NumberTools.notEqual(cbVorgangTyp, CBVorgang.TYP_ANBIETERWECHSEL)) {
                // bei Kuendigungen von 96X Leitungen muessen IMMER beide Auftraege beruecksichtigt werden!
                chbVierDraht.setSelected(Boolean.TRUE);
                chbVierDraht.setEnabled(false);
            }
        }
        if (CBVorgang.TYP_NEU.equals(cbVorgangTyp)) {
            rfGfTypIntern.setEnabled(true);
            if (isDtagBestellung()) {
                chbVormieter.setEnabled(true);
            }
        }
        else {
            rfGfTypIntern.clearReference();
            rfGfTypIntern.setEnabled(false);
        }

        chbKlammerung.addActionListener(this);
        chbVierDraht.addActionListener(this);
    }


    private boolean isDtagBestellung() {
        return Carrier.ID_DTAG.equals(carrierId);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == chbKlammerung) {
            if (chbKlammerung.isSelected()) {
                chbVierDraht.setSelected(false);
                chbVierDraht.setEnabled(false);
            }
            // just set enabled if it is possible otherwise would be enabled after first change of other property
            else if (witaTalOrderService.isPossible4Draht4GF(getCbVorgangTyp(), carrierbestellung)) {
                chbVierDraht.setEnabled(true);
            }
        }
        else if (event.getSource() == chbVierDraht) {
            if (chbVierDraht.isSelected()) {
                chbKlammerung.setSelected(false);
                chbKlammerung.setEnabled(false);
            }
            // just set enabled if it is possible otherwise would be enabled after first change of other property
            else if (witaTalOrderService.isPossibleKlammer4GF(getCbVorgangTyp(), carrierbestellung)) {
                chbKlammerung.setEnabled(true);
            }
        }
        else if (event.getSource() == btnVaKueMRn) {
            showWarningNoRangierung();
            if (askIfDoCreateVaDespiteExistingActiveVa()) {
                getWizardComponents().addWizardPanelAfter(this, new WbciGeschaeftsfallKueMrnPanel(getWizardComponents()));
                nextWbciPanel();
            }
        }
        else if (event.getSource() == btnVaKueORn) {
            showWarningNoRangierung();
            if (askIfDoCreateVaDespiteExistingActiveVa()) {
                getWizardComponents().addWizardPanelAfter(this, new WbciGeschaeftsfallKueOrnPanel(getWizardComponents()));
                nextWbciPanel();
            }
        }
        else if (event.getSource() == btnVaKueRRnP) {
            showWarningNoRangierung();
            if (askIfDoCreateVaDespiteExistingActiveVa()) {
                getWizardComponents().addWizardPanelAfter(this, new WbciGeschaeftsfallRRPnPanel(getWizardComponents()));
                nextWbciPanel();
            }
        }
    }

    /* Zeigt eine Warnmeldung an, falls auf der Endstelle B noch keine Rangierung zugeordnet ist. */
    private void showWarningNoRangierung() {
        try {
            if (!rangierungsService.hasRangierung(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)) {
                MessageHelper.showWarningDialog(this, "Für die Endstelle ist keine Rangierung hinterlegt. " +
                        "Bitte beachten Sie, dass eine Vorabstimmungsanfrage ohne Rangierung unter Umständen nicht möglich " +
                        "ist!", true);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Nachfrage, ob eine Vorabstimmung angelegt werden soll, obwohl zu dem TAI-Auftrag bereits eine VA existiert. */
    private boolean askIfDoCreateVaDespiteExistingActiveVa() {
        final Long taifunAuftragId = getTaifunAuftragId();
        final List<WbciGeschaeftsfall> activeGFs = wbciCommonService.findActiveGfByTaifunId(taifunAuftragId, false);
        if (!CollectionUtils.isEmpty(activeGFs)) {
            int option = MessageHelper.showConfirmDialog(this, String.format("Es existieren ein oder mehrere " +
                            "aktive Vorabstimmungsanfragen zum diesem Taifunauftrag '%s'.%n" +
                            "Soll eine neue Vorabstimmungsanfrage trotzdem erzeugt werden?", taifunAuftragId),
                    "Vorabstimmungsanfrage erzeugen?", JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.NO_OPTION) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ermittelt die technische Auftragsnummer
     */
    Long getAuftragId() {
        AuftragDaten ad = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
        final Long auftragId = ad.getAuftragId();
        if (auftragId == null) {
            throw new IllegalStateException("Keine AuftragId innerhalb vom AuftragDaten-Objekt gesetzt.");
        }
        return auftragId;
    }

    /**
     * Ermittelt die Taifun-Auftragsnummer
     */
    private Long getTaifunAuftragId() {
        AuftragDaten ad = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
        final Long taifunAuftragId = ad.getAuftragNoOrig();
        if (taifunAuftragId == null) {
            throw new IllegalStateException("Keine Taifun-AuftragId (AuftragNoOrig) innerhalb vom AuftragDaten-Objekt gesetzt.");
        }
        return taifunAuftragId;
    }


    private void nextWbciPanel() {
        super.goNext();
    }

    private Long getCbVorgangTyp() {
        try {
            return rfCBVorgangTyp.getReferenceIdAs(Long.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, new HurricanGUIException(
                    "WitaTalOrderService ist nicht verfügbar. Bitte wenden Sie sich an Ihren Administrator.", e));
        }
        return null;
    }


    /**
     * Legt eine neue Carrierbestellung fuer die Endstelle an, die dem Wizard uebergeben wurde. <br/>
     *
     * @throws StoreException
     * @throws ValidationException
     */
    private boolean createNewCarrierbestellungIfNotExist() throws FindException, StoreException, ValidationException {
        Object cbId = getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);
        if (cbId == null) {
            Endstelle endstelle = (Endstelle) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_ENDSTELLE);
            if (endstelle == null) {
                throw new IllegalArgumentException("TAL-Wizard benoetigt die Endstelle!");
            }
            HVTStandort hvtStd = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
            if (hvtStd != null && hvtStd.isFtthOrFttb()) {
                // bei FTTB, FTTH, FTTB_H, FTTX_BR, FTTX_FC Standort: Nachfrage, ob CB wirklich angelegt werden soll,
                // weil das eigentlich nur fuer REX-MK sinnvoll ist...
                int choice = MessageHelper.showYesNoQuestion(this,
                        String.format("Es handelt sich hier um einen FTTX Anschluss.%nSoll dennoch eine (interne) " +
                                "Bestellung ausgelöst werden?"), "Carrierbestellung für FTTB?");
                if (choice == JOptionPane.NO_OPTION) {
                    return false;
                }
            }

            AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);

            Carrierbestellung newCb = new Carrierbestellung();
            newCb.setCarrier(carrierId);
            newCb.setBestelltAm(new Date());
            if (DateTools.isDateAfter(auftragDaten.getVorgabeSCV(), new Date())) {
                newCb.setVorgabedatum(auftragDaten.getVorgabeSCV());
            }

            carrierService.saveCB(newCb, endstelle);

            addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID, newCb.getId());
        }
        return true;
    }

    /**
     * Gibt an, ob fuer den WITA Vorgang eine Carrierbestellung notwendig ist.
     * @return
     */
    protected boolean needsCarrierbestellung() {
        return true;
    }


    @Override
    protected boolean goNext() {
        try {
            if (needsCarrierbestellung() && !createNewCarrierbestellungIfNotExist()) {
                return false;
            }

            Long cbVorgangTyp = getCbVorgangTyp();
            if (cbVorgangTyp == null) {
                String msg = getSwingFactory().getText(NO_SELECTION);
                MessageHelper.showInfoDialog(this, msg, null, true);
                return false;
            }
            if (check96QLeistung(cbVorgangTyp)) {
                return false;
            }

            addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_4_DRAHT, chbVierDraht.isSelected()
                    ? Boolean.TRUE
                    : Boolean.FALSE);

            if (WitaSelectionUtils.isWitaSimulatorTestMode()) {
                String text = tfTestAmUser.getText();
                if (StringUtils.isBlank(text)) {
                    MessageHelper.showInfoDialog(this, getSwingFactory().getText(NO_TEST_AM_USER), null, true);
                    return false;
                }
                if (!WitaSimulatorTestUser.existsUsername(text)) {
                    int result = MessageHelper.showWarningDialog(this,
                            getSwingFactory().getText(INCORRECT_TEST_AM_USER, text), "keine Simulator-Rückmeldungen",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.NO_OPTION) {
                        return false;
                    }
                }
                addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_TEST_AM_USER, text);
            }
            return super.goNext();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            return false;
        }
    }

    /* Prueft (nur bei Nutzungsaenderungen), ob es sich um eine 96Q Leitung handelt. */
    private Boolean check96QLeistung(Long cbVorgangTyp) {
        Boolean is96QLeistung = false;
        if (NumberTools.equal(cbVorgangTyp, CBVorgang.TYP_NUTZUNGSAENDERUNG)) {
            try {
                Long cbId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);

                Carrierbestellung cb = carrierService.findCB(cbId);
                if (StringUtils.isNotBlank(cb.getLbz()) && cb.getLbz().toLowerCase().startsWith("96q")) {
                    String msg = getSwingFactory().getText(LBZ_96Q_NOT_ALLOWED);
                    MessageHelper.showInfoDialog(this, msg, null, true);
                    is96QLeistung = true;
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
        }
        return is96QLeistung;
    }

    private boolean isHideWitaGui() {
        return Boolean.TRUE.equals(getWizardObject(WIZARD_OBJECT_HIDE_WITA_WIZARD));
    }

    private boolean isHideWbciGui() {
        return Boolean.TRUE.equals(getWizardObject(WIZARD_OBJECT_HIDE_WBCI_WIZARD))
                || !featureService.isFeatureOnline(Feature.FeatureName.WBCI_ENABLED);
    }

}
