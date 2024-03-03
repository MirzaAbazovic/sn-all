/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import static de.augustakom.hurrican.model.billing.view.BAuftragLeistungView.*;

import java.awt.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.OEService;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.service.WbciKuendigungsService;

/**
 * Dialog zur Darstellung von Hurrican berechneten naechst-moeglichen Kuendigungsdatums fuer einen aktuell gewaehlten
 * Auftrag.
 */
public class CancellationCheckDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(CancellationCheckDialog.class);
    private static final long serialVersionUID = -3603725142961038920L;
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/CancellationCheckDialog.xml";

    public static final String CANCELLATION_INCOME = "cancellation.income";
    public static final String ORDER_NO_ORIG = "order.no.orig";
    public static final String PRODUCT = "product";
    public static final String SERVICES = "services";
    public static final String MVLZ = "mvlz";
    public static final String AUTO_RENEWAL = "auto.renewal";
    public static final String DESCRIPTION = "description";
    public static final String CANCELLATION_DATE = "cancellation.date";
    public static final String CANCELLATION_DATE1 = "cancellation.date1";
    public static final String CANCELLATION_DATE2 = "cancellation.date2";
    public static final String CHECK_NOT_POSSIBLE = "check.not.possible";
    public static final String INFORMATION = "information";
    public static final String POSITIONEN = "Positionen";
    public static final String GUELTIG_VON = "gültig von";
    public static final String GUELTIG_BIS = "gültig bis";
    public static final String MVLZ_CALCULATED = "mvlz.calculated";
    private final boolean readOnlyMode;

    // GUI components
    private AKJDateComponent dcCancellationIncome;
    private AKJFormattedTextField tfOrderNoOrig;
    private AKJTextField tfProduct;
    private AKReflectionTableModel<BAuftragLeistungView> tbMdlServices;
    private AKJDateComponent dcMvlz;
    private AKJDateComponent dcMvlzCalculated;
    private AKJTextField tfMvlz;
    private AKJTextField tfAutoRenewal;
    private AKJTextField tfDescription;
    private AKJDateComponent dcCancellationDate;
    private AKJLabel lblCheckNotPossible;
    private AKJLabel lblInformation;

    // Services
    private BillingAuftragService billingAuftragService;
    private OEService oeService;
    private WbciKuendigungsService kuendigungsCheckService;

    // Modelle
    private final Long orderNoOrig;
    private String product;
    private BAuftrag billingOrder;
    private KuendigungsCheckVO kuendigungsCheckVO;
    private LocalDateTime cancellationIncome;

    /**
     * @param orderNoOrig        Angabe der Billing (Taifun) Auftragsnummer des Auftrags, fuer den das naechst-moegliche
     *                           Kuendigungsdatum ermittelt werden soll
     * @param cancellationIncome das Datum der Kündigung geschickt wurde. Ist wichtig für die Berechnung des
     *                           frühestmöglichen Kündigungstermins
     * @param readOnlyMode       wenn True, darf der Sachbearbeiter das Datum nicht anpassen und der 'Berechnen' Button
     *                           ist deaktiviert.
     */
    public CancellationCheckDialog(Long orderNoOrig, LocalDateTime cancellationIncome, boolean readOnlyMode) {
        super(RESOURCE);
        this.orderNoOrig = orderNoOrig;
        this.cancellationIncome = cancellationIncome;
        this.readOnlyMode = readOnlyMode;
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
        billingAuftragService = getBillingService(BillingAuftragService.class);
        oeService = getBillingService(OEService.class);
        kuendigungsCheckService = getCCService(WbciKuendigungsService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("dialog.title"));

        AKJLabel lblCancellationIncome = getSwingFactory().createLabel(CANCELLATION_INCOME);
        AKJLabel lblOrderNoOrig = getSwingFactory().createLabel(ORDER_NO_ORIG);
        AKJLabel lblProduct = getSwingFactory().createLabel(PRODUCT);
        AKJLabel lblServices = getSwingFactory().createLabel(SERVICES);
        AKJLabel lblMvlz = getSwingFactory().createLabel(MVLZ);
        AKJLabel lblMvlzCalculated = getSwingFactory().createLabel(MVLZ_CALCULATED);
        AKJLabel lblAutoRenewal = getSwingFactory().createLabel(AUTO_RENEWAL);
        AKJLabel lblDescription = getSwingFactory().createLabel(DESCRIPTION);
        AKJLabel lblCancellationDate1 = getSwingFactory().createLabel(CANCELLATION_DATE1, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblCancellationDate2 = getSwingFactory().createLabel(CANCELLATION_DATE2, AKJLabel.LEFT, Font.BOLD);
        lblCheckNotPossible = getSwingFactory().createLabel(CHECK_NOT_POSSIBLE);
        lblInformation = getSwingFactory().createLabel(INFORMATION, AKJLabel.CENTER, Font.BOLD);
        lblInformation.setForeground(Color.red);

        dcCancellationIncome = getSwingFactory().createDateComponent(CANCELLATION_INCOME, !readOnlyMode);
        tfOrderNoOrig = getSwingFactory().createFormattedTextField(ORDER_NO_ORIG, false);
        tfProduct = getSwingFactory().createTextField(PRODUCT, false);
        dcMvlz = getSwingFactory().createDateComponent(MVLZ, false);
        dcMvlzCalculated = getSwingFactory().createDateComponent(MVLZ_CALCULATED, false);
        tfMvlz = getSwingFactory().createTextField(MVLZ, false);
        tfAutoRenewal = getSwingFactory().createTextField(AUTO_RENEWAL, false);
        tfDescription = getSwingFactory().createTextField(DESCRIPTION, false);
        dcCancellationDate = getSwingFactory().createDateComponent(CANCELLATION_DATE, false);
        dcCancellationDate.setDateTime(cancellationIncome);

        tbMdlServices = new AKReflectionTableModel<>(
                new String[] { POSITIONEN, GUELTIG_VON, GUELTIG_BIS },
                new String[] { LEISTUNG_NAME, POS_GUELTIG_VON, POS_GUELTIG_BIS },
                new Class[] { String.class, Date.class, Date.class });
        AKJTable tbServices = new AKJTable(tbMdlServices, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        AKJScrollPane spServices = new AKJScrollPane(tbServices, new Dimension(375, 110));
        tbServices.fitTable(new int[] { 200, 70, 70 });

        // @formatter:off
        AKJPanel detail = new AKJPanel(new GridBagLayout());
        detail.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        detail.add(lblCancellationIncome, GBCFactory.createGBC(100,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        detail.add(dcCancellationIncome , GBCFactory.createGBC(100,  0, 3, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblOrderNoOrig       , GBCFactory.createGBC(100,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(tfOrderNoOrig        , GBCFactory.createGBC(100,  0, 3, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblProduct           , GBCFactory.createGBC(100,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(tfProduct            , GBCFactory.createGBC(100,  0, 3, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblServices          , GBCFactory.createGBC(100,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel()       , GBCFactory.createGBC(  0,100, 2, 5, 1, 1, GridBagConstraints.VERTICAL));
        detail.add(spServices           , GBCFactory.createGBC(100,100, 3, 4, 2, 2, GridBagConstraints.BOTH));
        detail.add(lblMvlz              , GBCFactory.createGBC(100,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(dcMvlz               , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(tfMvlz               , GBCFactory.createGBC(100,  0, 4, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblMvlzCalculated    , GBCFactory.createGBC(100,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(dcMvlzCalculated     , GBCFactory.createGBC(100,  0, 3, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblAutoRenewal       , GBCFactory.createGBC(100,  0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(tfAutoRenewal        , GBCFactory.createGBC(100,  0, 3, 8, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblDescription       , GBCFactory.createGBC(100,  0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(tfDescription        , GBCFactory.createGBC(100,  0, 3, 9, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new JSeparator(SwingConstants.HORIZONTAL),
                                          GBCFactory.createGBC(  0,  0, 1,10, 4, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblCancellationDate1 , GBCFactory.createGBC(100,  0, 1,11, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblCancellationDate2 , GBCFactory.createGBC(100,  0, 1,12, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(dcCancellationDate   , GBCFactory.createGBC(100,  0, 3,12, 2, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblCheckNotPossible  , GBCFactory.createGBC(100,  0, 1,13, 4, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblInformation       , GBCFactory.createGBC(100,  0, 1,14, 4, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel()       , GBCFactory.createGBC(100,100, 5,15, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(detail, BorderLayout.CENTER);

        configureButton(CMD_SAVE, "Berechnen", "führt eine neue Berechnung des Kündigungsdatums aus", true, !readOnlyMode);
        configureButton(CMD_CANCEL, "Schliessen", null, true, true);
    }

    @Override
    protected void validateSaveButton() {
        // nothing to do
    }

    @Override
    public final void loadData() {
        try {
            billingOrder = billingAuftragService.findAuftrag(orderNoOrig);
            if (billingOrder == null) {
                throw new HurricanGUIException("Taifun Auftrag konnte nicht ermittelt werden!");
            }

            product = oeService.findProduktName4Auftrag(orderNoOrig);

            doCalculation();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void doCalculation() {
        try {
            setWaitCursor();
            cancellationIncome = dcCancellationIncome.getDateTime(cancellationIncome);

            kuendigungsCheckVO = kuendigungsCheckService.doKuendigungsCheck(orderNoOrig, cancellationIncome);
            showValues();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void showValues() {
        GuiTools.cleanFields(this);
        tbMdlServices.removeAll();

        dcCancellationIncome.setDateTime(cancellationIncome);
        tfOrderNoOrig.setValue(orderNoOrig);
        tfProduct.setText(product);

        if (kuendigungsCheckVO != null) {

            tbMdlServices.setData(kuendigungsCheckVO.getUeberlassungsleistungen());
            dcMvlz.setDateTime(kuendigungsCheckVO.getMindestVertragslaufzeitTaifun());
            dcMvlzCalculated.setDateTime(kuendigungsCheckVO.getMindestVertragslaufzeitCalculated());
            tfAutoRenewal.setText(kuendigungsCheckVO.getVertragsverlaengerung());
            if (billingOrder.getVertragsLaufzeit() != null) {
                tfMvlz.setText(String.format("%s Monate", billingOrder.getVertragsLaufzeit()));
            }
            dcCancellationDate.setDateTime(kuendigungsCheckVO.getCalculatedEarliestCancelDate());
            if (kuendigungsCheckVO.getKuendigungsfrist() != null) {
                tfDescription.setText(kuendigungsCheckVO.getKuendigungsfrist().getDescription());
            }

            if (kuendigungsCheckVO != null && kuendigungsCheckVO.getKuendigungsfrist() == null) {
                showInfoLabels(kuendigungsCheckVO.getKuendigungsstatus());
                return;
            }
        }
        showInfoLabels(null);
    }

    private void showInfoLabels(KuendigungsCheckVO.Kuendigungsstatus kuendigungsStatus) {
        boolean show = kuendigungsStatus != null;
        if (show) {
            lblInformation.setText(kuendigungsStatus.getInfoText());
            lblInformation.convertCurrentTextToMultiline(90);
        }
        lblCheckNotPossible.setVisible(show);
        lblInformation.setVisible(show);
    }

    @Override
    protected void doSave() {
        doCalculation();
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
