/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.validation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciAction;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageBuilder;
import de.mnet.wbci.model.helper.WbciRequestHelper;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

/**
 *
 */
public class StornoDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {
    private static final long serialVersionUID = 268443495999706398L;

    private static final Logger LOGGER = Logger.getLogger(StornoDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/StornoDialog.xml";

    public static final String STORNO_TITLE = "storno.title";
    public static final String STORNO_ART = "storno.art";
    public static final String STORNO_AUF = "storno.aufhebung";
    public static final String STORNO_HINWEIS = "storno.hinweis";
    public static final String STORNO_AUF_TEXT = "storno.aufhebung.text";
    public static final String STORNO_AEN = "storno.aenderung";
    public static final String STORNO_AEN_TEXT = "storno.aenderung.text";
    public static final String STORNO_AEN_EKPAUF_ZUSATZ = "storno.aenderung.ekpauf.zusatz";
    public static final String STORNO_AEN_EKPAGB_ZUSATZ = "storno.aenderung.ekpabg.zusatz";
    public static final String STORNO_AUF_EKPAGB_ZUSATZ = "storno.aufhebung.ekpabg.zusatz";
    public static final String STORNO_GRUND = "storno.grund";
    private static final String AUTOMATABLE_GESCHAEFTSFALL = "automatable";

    private AKJRadioButton rbStornoAuf;
    private AKJRadioButton rbStornoAen;

    private AKJTextArea taStornoHinweis;

    private AKJComboBox cbStornoGrund;
    private List<Reference> stornoAenGruende;
    private List<Reference> stornoAufGruende;
    private AKJCheckBox chkAutomatable;

    private final WbciRequest wbciRequest;
    private final List<WbciRequest> requestsForSelectedItem;
    private WbciStornoService wbciStornoService;
    private WbciValidationService wbciValidationService;
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private FeatureService featureService;
    private ReferenceService referenceService;

    /**
     * Marks if Mnet is carrier role abgebend
     */
    private final boolean mnetCarrierRoleAbgebend;

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}s, auf den sich der Dialog beziehen soll.
     *
     * @param wbciRequest
     * @param requestsForSelectedItem
     */
    public StornoDialog(WbciRequest wbciRequest, List<WbciRequest> requestsForSelectedItem) {
        super(RESOURCE, true, true);
        this.wbciRequest = wbciRequest;
        this.requestsForSelectedItem = requestsForSelectedItem;

        this.mnetCarrierRoleAbgebend = getMNetCarrierRole().equals(CarrierRole.ABGEBEND);

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
        wbciStornoService = getCCService("WbciStornoService", WbciStornoService.class);
        wbciValidationService = getCCService(WbciValidationService.class);
        wbciGeschaeftsfallService = getCCService(WbciGeschaeftsfallService.class);
        referenceService = getCCService(ReferenceService.class);
        featureService = getCCService(FeatureService.class);
    }

    @Override
    protected final void createGUI() {
        setSize(new Dimension(200, 100));
        setTitle("Storno Daten erfassen");

        AKJLabel lblStornoArt = getSwingFactory().createLabel(STORNO_ART);

        taStornoHinweis = getSwingFactory().createTextArea(STORNO_HINWEIS);
        taStornoHinweis.setEditable(false);
        taStornoHinweis.setWrapStyleWord(true);
        taStornoHinweis.setLineWrap(true);
        taStornoHinweis.setFontStyle(Font.BOLD);
        taStornoHinweis.setColumns(30);
        taStornoHinweis.setRows(3);
        taStornoHinweis.setText(getSwingFactory().getText(STORNO_AUF_TEXT) + getStornoAufhebungZusatzText());

        ButtonGroup bgStornoArt = new ButtonGroup();
        StornoArtActionListener rbActionListener = new StornoArtActionListener();
        rbStornoAuf = getSwingFactory().createRadioButton(STORNO_AUF, rbActionListener, false, bgStornoArt);
        rbStornoAuf.setSelected(true);
        rbStornoAen = getSwingFactory().createRadioButton(STORNO_AEN, rbActionListener, false, bgStornoArt);

        AKJLabel lblIcon = new AKJLabel();
        lblIcon.setIcon(getSwingFactory().getText("info.icon"));
        lblIcon.setAlignmentX(RIGHT_ALIGNMENT);
        lblIcon.setAlignmentY(TOP_ALIGNMENT);

        AKCustomListCellRenderer renderer = new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue);
        cbStornoGrund = getSwingFactory().createComboBox(STORNO_GRUND);
        cbStornoGrund.setRenderer(renderer);

        AKJLabel lblAutomatable = getSwingFactory().createLabel(AUTOMATABLE_GESCHAEFTSFALL);
        chkAutomatable = getSwingFactory().createCheckBox(AUTOMATABLE_GESCHAEFTSFALL);
        chkAutomatable.setEnabled(false);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblStornoArt         , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(rbStornoAuf          , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(rbStornoAen          , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        if (mnetCarrierRoleAbgebend) {
            AKJLabel lblStornoGrund = getSwingFactory().createLabel(STORNO_GRUND);
            dtlPnl.add(lblStornoGrund   , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            dtlPnl.add(cbStornoGrund    , GBCFactory.createGBC(  0,  0, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL));

            dtlPnl.add(lblAutomatable   , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            dtlPnl.add(chkAutomatable   , GBCFactory.createGBC(  0,  0, 1, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        }

        dtlPnl.add(lblIcon              , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(taStornoHinweis      , GBCFactory.createGBC(100,  0, 1, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(100,100, 4, 5, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, getSwingFactory().getText("send.txt"), getSwingFactory().getText("send.tooltip"), true, true);
        configureButton(CMD_CANCEL, getSwingFactory().getText("cancel.txt"), getSwingFactory().getText("cancel.tooltip"), true, true);

        validateButtons();
    }

    private void validateButtons() {
        rbStornoAen.setEnabled(isStornoAenderungEnabled());
    }

    private boolean isStornoAenderungEnabled() {
        return WbciAction.CREATE_STORNO_AENDERUNG.isActionPermitted(
                getMNetCarrierRole(),
                WbciRequestHelper.getAllGeschaeftsfallRequestStatuses(requestsForSelectedItem),
                wbciRequest.getWbciGeschaeftsfall().getStatus(),
                null, null
        );
    }

    @Override
    public final void loadData() {
        if (wbciRequest != null && wbciRequest.getWbciGeschaeftsfall() != null) {
            setTitle(String.format(getSwingFactory().getText(STORNO_TITLE), wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId()));
        }

        if (mnetCarrierRoleAbgebend) {
            try {
                stornoAenGruende = referenceService.findReferencesByType(Reference.REF_TYPE_STR_AEN_REASON, true);
                stornoAufGruende = referenceService.findReferencesByType(Reference.REF_TYPE_STR_AUF_REASON, true);

                cbStornoGrund.addItems(stornoAufGruende);

                if (featureService.isFeatureOnline(Feature.FeatureName.WBCI_ABGEBEND_STR_AUF_AUTO_PROCESSING)) {
                    chkAutomatable.setEnabled(true);
                    chkAutomatable.setSelected(wbciRequest.getWbciGeschaeftsfall().getAutomatable());
                }
            }
            catch (FindException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        try {
            StornoAnfrage storno;

            if (rbStornoAuf.isSelected()) {
                storno = buildStornoAufhebungAnfrage();
            }
            else if (rbStornoAen.isSelected()) {
                storno = buildStornoAenderungAnfrage();
            }
            else {
                throw new HurricanGUIException("Storno Art (Aenderung oder Aufhebung) muss ausgewählt werden!");
            }

            if (isStornoRequestValid(storno)) {
                boolean wbciGfAutomatable = BooleanTools.nullToFalse(wbciRequest.getWbciGeschaeftsfall().getAutomatable());
                if (chkAutomatable.isEnabled() && chkAutomatable.isSelected() != wbciGfAutomatable) {
                    wbciRequest.getWbciGeschaeftsfall().setAutomatable(chkAutomatable.isSelectedBoolean());
                    wbciGeschaeftsfallService.markGfAsAutomatable(
                            wbciRequest.getWbciGeschaeftsfall().getId(), chkAutomatable.isSelectedBoolean());
                }

                wbciStornoService.createWbciStorno(storno, wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId());
                prepare4Close();
                setValue(AKJOptionDialog.OK_OPTION);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private StornoAnfrage buildStornoAenderungAnfrage() {
        if (mnetCarrierRoleAbgebend) {
            return new StornoAenderungAbgAnfrageBuilder()
                    .withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall())
                    .withIoType(IOType.OUT)
                    .withVorabstimmungsIdRef(wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId())
                    .withStornoGrund(((Reference) cbStornoGrund.getSelectedItem()).getStrValue())
                    .build();
        }
        else {
            return new StornoAenderungAufAnfrageBuilder()
                    .withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall())
                    .withIoType(IOType.OUT)
                    .withVorabstimmungsIdRef(wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId())
                    .build();
        }
    }

    private StornoAnfrage buildStornoAufhebungAnfrage() {
        if (mnetCarrierRoleAbgebend) {
            return new StornoAufhebungAbgAnfrageBuilder()
                    .withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall())
                    .withIoType(IOType.OUT)
                    .withVorabstimmungsIdRef(wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId())
                    .withStornoGrund(((Reference) cbStornoGrund.getSelectedItem()).getStrValue())
                    .build();
        }
        else {
            return new StornoAufhebungAufAnfrageBuilder()
                    .withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall())
                    .withIoType(IOType.OUT)
                    .withVorabstimmungsIdRef(wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId())
                    .build();
        }
    }

    private <T extends StornoAnfrage> boolean isStornoRequestValid(T storno) {
        Set<ConstraintViolation<T>> errors = wbciValidationService.checkWbciMessageForErrors(storno.getEKPPartner(), storno);
        if (errors != null && !errors.isEmpty()) {
            String title = "Die Stornoanfrage ist nicht vollstaendig";
            String errorMsg = new ConstraintViolationHelper().generateErrorMsg(errors);
            Object[] options = { "OK" };
            MessageHelper.showOptionDialog(this, errorMsg, title, JOptionPane.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            return false;
        }
        else {
            Set<ConstraintViolation<T>> warnings = wbciValidationService.checkWbciMessageForWarnings(storno.getEKPPartner(), storno);
            if (warnings != null && !warnings.isEmpty()) {
                String title = "Stornoanfrage abschicken?";
                String warningMsg = new ConstraintViolationHelper().generateWarningMsg(warnings);
                int result = MessageHelper.showYesNoQuestion(this, warningMsg, title);
                if (result == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets MNet carrier role.
     *
     * @return
     */
    private CarrierRole getMNetCarrierRole() {
        CarrierCode abgebenderEKP = wbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP();
        CarrierCode aufnehmenderEKP = wbciRequest.getWbciGeschaeftsfall().getAufnehmenderEKP();
        return CarrierRole.lookupMNetCarrierRoleByCarrierCode(aufnehmenderEKP, abgebenderEKP);
    }

    /**
     * Gets explanation text according to MNET being ekpAuf or ekpAbg role.
     *
     * @return
     */
    private String getStornoAenderungZusatzText() {
        if (mnetCarrierRoleAbgebend) {
            return " " + getSwingFactory().getText(STORNO_AEN_EKPAGB_ZUSATZ);
        }
        else {
            return " " + getSwingFactory().getText(STORNO_AEN_EKPAUF_ZUSATZ);
        }
    }

    /**
     * Gets explanation text according to MNET being ekpAuf or ekpAbg role.
     *
     * @return
     */
    private String getStornoAufhebungZusatzText() {
        if (mnetCarrierRoleAbgebend) {
            return " " + getSwingFactory().getText(STORNO_AUF_EKPAGB_ZUSATZ);
        }
        else {
            return "";
        }
    }

    private class StornoArtActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<Reference> stornoGruende = null;
            if (e.getSource() == rbStornoAuf) {
                stornoGruende = stornoAufGruende;
                taStornoHinweis.setText(getSwingFactory().getText(STORNO_AUF_TEXT) + getStornoAufhebungZusatzText());
            }
            else if (e.getSource() == rbStornoAen) {
                stornoGruende = stornoAenGruende;
                taStornoHinweis.setText(getSwingFactory().getText(STORNO_AEN_TEXT) + getStornoAenderungZusatzText());
            }

            if (mnetCarrierRoleAbgebend) {
                cbStornoGrund.removeAllItems();
                cbStornoGrund.addItems(stornoGruende);
            }
        }
    }
}
