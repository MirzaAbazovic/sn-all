/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2005 08:59:34
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.temp.AbstractRevokeModel;
import de.augustakom.hurrican.model.cc.temp.RevokeCreationModel;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.QueryCCService;

/**
 * Dialog, um eine Aktion (Inbetriebnahme bzw. Kuendigung) auf einem Auftrag rueckgaengig zu machen.
 *
 *
 */
public class RevokeOrderActionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RevokeOrderActionDialog.class);

    private static final String XML_SOURCE = "de/augustakom/hurrican/gui/tools/physik/resources/RevokeOrderActionDialog.xml";

    // GUI-Komponenten
    private AKJFormattedTextField tfAuftragsNummer = null;
    private AKJCheckBox chbStatus = null;
    private AKJCheckBox chbTechLeistung = null;
    private AKJCheckBox chbRangierung = null;
    private AKReferenceField rfAuftragsart = null;
    private AKReferenceField rfCPSSoType = null;

    // sonstiges
    private static final String DIALOG_TITLE_TERMINATION = "dialog.title.cancel";
    private static final String DIALOG_TITLE_CREATION = "dialog.title.creation";
    private final static String AUFTRAGSART = "auftragsart";
    private final static String AUFTRAGSNUMMER = "auftragsnummer";
    private final static String AUFTRAGSSTATUS = "auftragsstatus";
    private final static String TECHLEISTUNG = "techleistung";
    private final static String RANGIERUNG = "rangierung";
    private final static String CPS_SO_TYPE = "cps.so.type";
    public final static String OK = "ok";
    public final static String CANCEL = "cancel";

    private final AbstractRevokeModel revokeModel;
    private boolean isRevokeTermination = false;

    /**
     * Konstruktor, um eine Kuendigung rueckgaengig zu machen.
     *
     * @param terminationModel
     */
    public RevokeOrderActionDialog(RevokeTerminationModel terminationModel) {
        super(XML_SOURCE);
        this.revokeModel = terminationModel;
        isRevokeTermination = true;
        createGUI();
        loadData();
        preselect();
    }

    /**
     * Konstruktor, um eine Inbetriebnahme rueckgaengig zu machen.
     *
     * @param creationModel
     */
    public RevokeOrderActionDialog(RevokeCreationModel creationModel) {
        super(XML_SOURCE);
        this.revokeModel = creationModel;
        createGUI();
        loadData();
        preselect();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        if (isRevokeTermination) {
            setTitle(getSwingFactory().getText(DIALOG_TITLE_TERMINATION));
        }
        else {
            setTitle(getSwingFactory().getText(DIALOG_TITLE_CREATION));
        }

        AKJLabel lblAuftragsummer = getSwingFactory().createLabel(AUFTRAGSNUMMER);
        AKJLabel lblStatus = getSwingFactory().createLabel(AUFTRAGSSTATUS);
        AKJLabel lblTechLeistung = getSwingFactory().createLabel(TECHLEISTUNG);
        AKJLabel lblRangierung = getSwingFactory().createLabel(RANGIERUNG);
        AKJLabel lblAuftragsart = getSwingFactory().createLabel(AUFTRAGSART);
        AKJLabel lblCPSSoType = getSwingFactory().createLabel(CPS_SO_TYPE);

        tfAuftragsNummer = getSwingFactory().createFormattedTextField(AUFTRAGSNUMMER);
        chbStatus = getSwingFactory().createCheckBox(AUFTRAGSSTATUS, Boolean.TRUE);
        chbTechLeistung = getSwingFactory().createCheckBox(TECHLEISTUNG, Boolean.TRUE);
        chbRangierung = getSwingFactory().createCheckBox(RANGIERUNG, Boolean.TRUE);
        rfAuftragsart = getSwingFactory().createReferenceField(AUFTRAGSART);
        rfCPSSoType = getSwingFactory().createReferenceField(CPS_SO_TYPE);

        chbStatus.setSelected(Boolean.TRUE);
        chbTechLeistung.setSelected(Boolean.TRUE);
        chbRangierung.setSelected(Boolean.TRUE);

        AKJPanel top = new AKJPanel(new GridBagLayout());

        Insets lblInset = new Insets(10, 10, 10, 2);
        Insets fieldInset = new Insets(2, 15, 2, 2);
        top.add(lblAuftragsummer, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, lblInset));
        top.add(tfAuftragsNummer, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, fieldInset));
        top.add(lblStatus, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, lblInset));
        top.add(chbStatus, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, fieldInset));
        top.add(lblTechLeistung, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL, lblInset));
        top.add(chbTechLeistung, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL, fieldInset));
        top.add(lblRangierung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL, lblInset));
        top.add(chbRangierung, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL, fieldInset));
        top.add(lblAuftragsart, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL, lblInset));
        top.add(rfAuftragsart, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL, fieldInset));
        top.add(lblCPSSoType, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL, lblInset));
        top.add(rfCPSSoType, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL, fieldInset));

        manageGUI(new AKManageableComponent[] { rfCPSSoType });
        validateFields();

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            QueryCCService queryCCService = getCCService(QueryCCService.class);
            BAVerlaufAnlass baVerlaufAnlassEx = new BAVerlaufAnlass();
            baVerlaufAnlassEx.setAkt(Boolean.TRUE);
            baVerlaufAnlassEx.setAuftragsart(Boolean.TRUE);

            rfAuftragsart.setReferenceFindExample(baVerlaufAnlassEx);
            rfAuftragsart.setFindService(queryCCService);

            Reference cpsSoTypeEx = new Reference();
            cpsSoTypeEx.setType(Reference.REF_TYPE_CPS_SERVICE_ORDER_TYPE);
            rfCPSSoType.setReferenceFindExample(cpsSoTypeEx);
            rfCPSSoType.setFindService(queryCCService);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Vorbelegung der Auftragsart
     */
    private void preselect() {
        if ((revokeModel != null) && (revokeModel.getAuftragId() != null)) {
            tfAuftragsNummer.setText(String.format("%d", revokeModel.getAuftragId()));
        }

        rfAuftragsart.setReferenceId(BAVerlaufAnlass.NEUSCHALTUNG);
        if (rfCPSSoType.isComponentExecutable()) {
            if (isRevokeTermination) {
                // Kuendigung rueckgaengig --> createSub
                rfCPSSoType.setReferenceId(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
            }
            else {
                // Inbetriebnahme rueckgaengig --> cancelSub
                rfCPSSoType.setReferenceId(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
            }
        }
    }

    /* Definiert die Revoke-Parameter und uebergibt sie dem entsprechenden Modell. */
    private void createRevokeModel() {
        revokeModel.setAuftragId(tfAuftragsNummer.getValueAsLong(null));
        revokeModel.setCpsTxServiceOrderType(rfCPSSoType.getReferenceIdAs(Long.class));
        revokeModel.setSessionId(HurricanSystemRegistry.instance().getSessionId());

        if (isRevokeTermination) {
            RevokeTerminationModel revokeTerminationModel = (RevokeTerminationModel) revokeModel;
            revokeTerminationModel.setIsAuftragStatus(chbStatus.isSelectedBoolean());
            revokeTerminationModel.setIsTechLeistungen(chbTechLeistung.isSelectedBoolean());
            revokeTerminationModel.setIsRangierung(chbRangierung.isSelectedBoolean());
            revokeTerminationModel.setAuftragsArtId(((BAVerlaufAnlass) rfAuftragsart.getReferenceObject()).getId());
        }
    }

    @Override
    protected void doSave() {
        try {
            setWaitCursor();
            createRevokeModel();
            if (revokeModel.getAuftragId() == null) {
                throw new HurricanGUIException("Es ist kein Auftrag angegeben!");
            }

            AKWarnings warnings = null;
            CCAuftragService service = getCCService(CCAuftragService.class);
            if (isRevokeTermination) {
                warnings = service.revokeTermination((RevokeTerminationModel) revokeModel);
            }
            else {
                warnings = service.revokeCreation((RevokeCreationModel) revokeModel);
            }

            showWarnings(warnings);
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Auftrag ist zurueck gesetzt. Bitte die Daten kontrollieren.");

            prepare4Close();
            setValue(OK_OPTION);
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
    protected void execute(String command) {
    }

    /*
     * Zeigt Dialog mit den Warnungen an
     *
     * @param warnings
     */
    private void showWarnings(AKWarnings warnings) {
        if ((warnings != null) && (warnings.getAKMessages() != null) && (warnings.getAKMessages().size() > 0)) {
            MessageHelper.showInfoDialog(getMainFrame(), warnings.getWarningsAsText(), null, true);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    private void validateFields() {
        if (!isRevokeTermination) {
            chbStatus.setEnabled(false);
            chbTechLeistung.setEnabled(false);
            chbRangierung.setEnabled(false);
        }
    }

}
