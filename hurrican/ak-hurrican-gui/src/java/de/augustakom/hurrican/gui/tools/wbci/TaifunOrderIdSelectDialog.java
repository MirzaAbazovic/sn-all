/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.wbci.tables.BillingOrderTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.BillingOrderTableModel;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.OrderMatchVO;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciVaService;

/**
 * Dialog zur Auswahl eines TaifunAutragsId für die Zuordnung zu einer eingehenden Vorabstimmungsanfrage.
 *
 *
 */
public class TaifunOrderIdSelectDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = -91745390026410660L;
    private static final Logger LOGGER = Logger.getLogger(TaifunOrderIdSelectDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/TaifunOrderIdSelectDialog.xml";

    public static final String DIALOG_TITLE = "dialog.title";
    public static final String HINWEIS_MSG = "hinweis.msg";
    public static final String HINWEIS_STORNO_MSG = "hinweis.storno.msg";
    public static final String HINWEIS_ALREADY_ASSIGNED = "hinweis.already.assigned";
    public static final String HINWEIS_ALREADY_ASSIGNED_TITLE = "hinweis.already.assigned.title";
    public static final String HINWEIS_NON_BILLING_RELEVANT_ASSIGNED_TITLE = "hinweis.non.billing.relevant.assigned.title";
    public static final String HINWEIS_NON_BILLING_RELEVANT_ASSIGNED = "hinweis.non.billing.relevant.assigned";
    public static final String HINWEIS_GFWECHSEL_RRNP_MRNORN = "hinweis.gfwechsel.rrnp.mrnorn";
    public static final String HINWEIS_GFWECHSEL_RRNP_MRNORN_TITLE = "hinweis.gfwechsel.rrnp.mrnorn.title";
    public static final String HINWEIS_NEW_VA_EXPIRED_EXISTS = "hinweis.new-va-expired.exists";
    public static final String ORDER_ID = "taifun.orderid";
    public static final String SUGGESTED_ORDERS = "suggested.orders";

    public static final String ABBM_BTN = "abbm.btn";
    public static final String SAVE_BTN = "save.btn";
    public static final String CANCEL_BTN = "cancel.btn";

    // GUI Elemente
    private BillingOrderTable tbSuggestedOrders;
    private BillingOrderTableModel tbMdlSuggestedOrders;
    private AKJFormattedTextField tfOrderId;

    // sonstiges
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private WbciCommonService wbciCommonService;
    protected WbciMeldungService wbciMeldungService;
    private WbciVaService wbciVaService;
    private final String vorabstimmungsId;
    private final WbciGeschaeftsfall wbciGeschaeftsfall;

    public TaifunOrderIdSelectDialog(WbciGeschaeftsfall wbciGeschaeftsfall) {
        super(RESOURCE, false, false);
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;
        this.vorabstimmungsId = wbciGeschaeftsfall.getVorabstimmungsId();

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

    private void initServices() throws ServiceNotFoundException {
        wbciGeschaeftsfallService = getCCService(WbciGeschaeftsfallService.class);
        wbciCommonService = getCCService(WbciCommonService.class);
        wbciMeldungService = getCCService(WbciMeldungService.class);
        wbciVaService = getCCService("WbciVaKueMrnService", WbciVaService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(DIALOG_TITLE));

        AKJLabel lblSuggestion = getSwingFactory().createLabel(SUGGESTED_ORDERS, AKJLabel.LEFT, Font.BOLD);
        tbMdlSuggestedOrders = new BillingOrderTableModel();
        tbSuggestedOrders = new BillingOrderTable(tbMdlSuggestedOrders);
        AKJScrollPane spSuggestedOrders = new AKJScrollPane(tbSuggestedOrders, new Dimension(720, 250));

        AKJLabel lblOrderId = getSwingFactory().createLabel(ORDER_ID);
        tfOrderId = getSwingFactory().createFormattedTextField(ORDER_ID);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblSuggestion    , GBCFactory.createGBC(100,  0, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(spSuggestedOrders, GBCFactory.createGBC(100,100, 1, 2, 3, 1, GridBagConstraints.BOTH));
        dtlPnl.add(lblOrderId       , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 2, 3, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(tfOrderId        , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 4, 4, 1, 1, GridBagConstraints.NONE));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        AKJButton btnAbbm = getSwingFactory().createButton(ABBM_BTN, getActionListener());
        AKJButton btnSave = getSwingFactory().createButton(SAVE_BTN, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(CANCEL_BTN, getActionListener());

        // @formatter:off
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnSave      , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnAbbm        , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel() , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel      , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
        getChildPanel().add(btnPanel, BorderLayout.SOUTH);
    }

    @Override
    public final void loadData() {
        try {
            tbMdlSuggestedOrders.setData(wbciVaService.getTaifunOrderAssignmentCandidates(vorabstimmungsId, false));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            Long taifunOrderId = getTaifunOrderNo();
            if (taifunOrderId == null) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Bitte wählen Sie einen Auftrag aus der Liste aus oder geben Sie manuell eine Taifun Auftragsnummer ein.",
                        "Auftrag wählen", null, true);
                return;
            }

            final List<WbciGeschaeftsfall> activeGFs = wbciCommonService.findActiveGfByTaifunId(taifunOrderId, true);
            if (CollectionUtils.isEmpty(activeGFs)) {
                wbciCommonService.assignTaifunOrder(vorabstimmungsId, taifunOrderId, true);

                Set<Long> nonBillingRelevantTaifunOrders = wbciCommonService
                        .findNonBillingRelevantTaifunAuftragIds(vorabstimmungsId);
                if (!CollectionUtils.isEmpty(nonBillingRelevantTaifunOrders)) {
                    MessageHelper.showInfoDialog(getMainFrame(), String.format(
                                    getSwingFactory().getText(HINWEIS_NON_BILLING_RELEVANT_ASSIGNED),
                                    StringUtils.arrayToCommaDelimitedString(nonBillingRelevantTaifunOrders.toArray())),
                            getSwingFactory().getText(HINWEIS_NON_BILLING_RELEVANT_ASSIGNED_TITLE), null, true
                    );
                }
            }
            else if (activeGFs.size() == 1 && wbciGeschaeftsfallService.isLinkedToStrAenGeschaeftsfall(vorabstimmungsId, activeGFs.get(0).getId())) {
                WbciGeschaeftsfall actualGf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
                WbciGeschaeftsfall activeGf = activeGFs.get(0);

                // Sonderfall: falls 'activeGf' den Status NEW_VA_EXPIRED hat, dann muss der Bearbeiter diesen Vorgang
                //             zuerst vollstaendig abarbeiten, bevor eine Zuordnung erfolgen kann/darf.
                if (WbciGeschaeftsfallStatus.NEW_VA_EXPIRED.equals(activeGf.getStatus())) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            String.format(getSwingFactory().getText(HINWEIS_NEW_VA_EXPIRED_EXISTS), activeGf.getVorabstimmungsId()),
                            "Geschäftsfall existiert noch",
                            null, true);
                    return;
                }

                // GF-Wechsel RRNP auf MRN/ORN pruefen und ggf. gleich mit ABBM ablehnen
                if (wbciGeschaeftsfallService.isGeschaeftsfallWechselRrnpToMrnOrn(activeGf, actualGf)) {
                    askIfAbbmShouldBeCreatedAndSendAbbm(actualGf, activeGf);
                    return;
                }

                int option = MessageHelper.showOptionDialog(
                        getMainFrame(),
                        String.format(getSwingFactory().getText(HINWEIS_STORNO_MSG), taifunOrderId, activeGFs.get(0).getVorabstimmungsId()),
                        "Taifunauftrag zuweisen?",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Zuordnen", "Abbrechen" },
                        null);

                if (option == JOptionPane.OK_OPTION) {
                    wbciGeschaeftsfallService.assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                            vorabstimmungsId, taifunOrderId, activeGFs.get(0).getId(), true);
                }
                else {
                    return;
                }
            }
            else {
                if (wbciGeschaeftsfallService.isGfAssignedToTaifunOrder(vorabstimmungsId, taifunOrderId)) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            String.format(getSwingFactory().getText(HINWEIS_ALREADY_ASSIGNED), taifunOrderId),
                            getSwingFactory().getText(HINWEIS_ALREADY_ASSIGNED_TITLE),
                            null, true);
                    return;
                }

                int option = MessageHelper.showOptionDialog(
                        getMainFrame(),
                        String.format(getSwingFactory().getText(HINWEIS_MSG), taifunOrderId, convertAktiveGfsToString(activeGFs)),
                        "Taifunauftrag zuweisen?",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Zuordnen und ablehnen", "Abbrechen" },
                        null);

                if (option == JOptionPane.OK_OPTION) {
                    wbciGeschaeftsfallService.assignTaifunOrderAndRejectVA(vorabstimmungsId, taifunOrderId);
                }
                else {
                    return;
                }
            }

            prepare4Close();
            setValue(AKJOptionDialog.OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Fragt den User, ob eine ABBM (wg. Wechsel von RRNP zu MRN/ORN ueber eine STR-AEN) erstellt
     * werden soll. Falls 'ja', dann wird die ABBM direkt gesendet. <br/>
     * @param actualGf der 'neue' WBCI Vorgang, der einen ungueltigen Geschaeftsfalltyp besitzt
     * @param rrnpGf der RRNP Vorgang mit STR-AEN und Status NEW_VA
     */
    private void askIfAbbmShouldBeCreatedAndSendAbbm(WbciGeschaeftsfall actualGf, WbciGeschaeftsfall rrnpGf) {
        int option = MessageHelper.showOptionDialog(getMainFrame(),
                String.format(getSwingFactory().getText(HINWEIS_GFWECHSEL_RRNP_MRNORN)),
                getSwingFactory().getText(HINWEIS_GFWECHSEL_RRNP_MRNORN_TITLE),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (option == JOptionPane.YES_OPTION) {
            wbciMeldungService.createAndSendWbciMeldung(
                    new AbbruchmeldungBuilder().withWbciGeschaeftsfall(actualGf)
                            .withBegruendung(String.format(
                                    Abbruchmeldung.BEGRUENDUNG_WECHSEL_RRNP_MRNORN,
                                    rrnpGf.getVorabstimmungsId()), true)
                            .build(), 
                    actualGf.getVorabstimmungsId());

            prepare4Close();
            setValue(AKJOptionDialog.OK_OPTION);
        }
    }

    protected void doAbbm() {
        try {
            wbciMeldungService.createAndSendWbciMeldung(createABBM(), vorabstimmungsId);
            prepare4Close();
            setValue(AKJOptionDialog.OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private Abbruchmeldung createABBM() {
        AbbruchmeldungBuilder abbruchmeldungBuilder = new AbbruchmeldungBuilder()
                .withAbsender(CarrierCode.MNET)
                .withIoType(IOType.OUT)
                .withWechseltermin(wbciGeschaeftsfall.getKundenwunschtermin())
                .addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                        .withMeldungsCode(MeldungsCode.KNI)
                        .withMeldungsText(MeldungsCode.KNI.getStandardText())
                        .build());

        return abbruchmeldungBuilder.build();
    }

    /*
     * Gibt die ausgewaehlte Taifun Auftrags-Id zurueck.
     * Vorrang hat das Text-Feld vor einer Auswahl in der Tabelle!
     */
    private Long getTaifunOrderNo() {
        Long manualTaifunOrderNo = tfOrderId.getValueAsLong(null);
        if (manualTaifunOrderNo == null) {
            int row = tbSuggestedOrders.getSelectedRow();
            if (row >= 0) {
                Pair<BAuftragVO, OrderMatchVO> orderMatch = tbMdlSuggestedOrders.getDataAtRow(row);
                if (orderMatch != null) {
                    return orderMatch.getSecond().getOrderNoOrig();
                }
            }
        }
        return manualTaifunOrderNo;
    }

    private String convertAktiveGfsToString(final List<WbciGeschaeftsfall> activeGFs) {
        StringBuilder builder = new StringBuilder();
        for (WbciGeschaeftsfall activeGF : activeGFs) {
            builder.append("        VorabstimmungsId: ").append(activeGF.getVorabstimmungsId()).append(", ")
                    .append("Bearbeiter: ")
                    .append(activeGF.getUserName() != null ? activeGF.getUserName() : "kein Bearbeiter")
                    .append(";%n");
        }
        return String.format(builder.toString());
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case ABBM_BTN:
                doAbbm();
                break;
            case SAVE_BTN:
                doSave();
                break;
            case CANCEL_BTN:
                cancel();
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
