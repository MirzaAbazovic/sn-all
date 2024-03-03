/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 08:58:02
 */
package de.augustakom.hurrican.gui.tools.rechnung;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.validation.EMailValidator;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.view.BillRunView;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.cc.RegistryService;


/**
 * Basis-Panel fuer die Definition der Zugriffsdaten auf den Billing-Host und den Destination-Server fuer die Signatur
 * bzw. den Rechnungs-Export.
 *
 *
 */
public class RechnungBasePanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RechnungBasePanel.class);

    private AKJComboBox cbBillRun = null;
    private AKJTextField tfEMail = null;

    /**
     * Default-Const.
     */
    public RechnungBasePanel() {
        super("de/augustakom/hurrican/gui/tools/rechnung/resources/RechnungBasePanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblBillRun = getSwingFactory().createLabel("bill.run");
        AKJLabel lblBillInfo = getSwingFactory().createLabel("bill.info", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblInfo = getSwingFactory().createLabel("info", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblEMail = getSwingFactory().createLabel("info.email");

        BillRunCellRenderer cbBillRunRenderer = new BillRunCellRenderer();
        cbBillRun = getSwingFactory().createComboBox("bill.run", cbBillRunRenderer);
        tfEMail = getSwingFactory().createTextField("info.email");

        this.setLayout(new GridBagLayout());
        this.add(lblBillInfo, GBCFactory.createGBC(0, 0, 0, 0, 5, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblBillRun, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        this.add(cbBillRun, GBCFactory.createGBC(100, 0, 2, 1, 5, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblInfo, GBCFactory.createGBC(0, 0, 0, 2, 5, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblEMail, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfEMail, GBCFactory.createGBC(100, 0, 2, 3, 5, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            RegistryService rs = getCCService(RegistryService.class);
            tfEMail.setText(rs.getStringValue(RegistryService.REGID_SIGNATURE_EMAIL));

            RechnungsService res = getBillingService(RechnungsService.class);
            List<BillRunView> views = res.findBillRunViews();
            cbBillRun.addItems(views, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Schreibt die definierten Werte des Panels in eine Map und gibt diese zurueck.
     */
    protected Map getParameters() {
        Map map = new HashMap();
        BillRunView view = (BillRunView) cbBillRun.getSelectedItem();
        map.put("bill.stream", view.getBillCycle());
        String year = StringUtils.substring(view.getPeriod().toString(), 0, 4);
        map.put("bill.year", year);
        String month = StringUtils.substring(view.getPeriod().toString(), 4, 6);
        map.put("bill.month", month);
        map.put("bill.run.id", view.getRunNo());
        map.put("email", tfEMail.getText());
        return map;
    }

    /* Ueberprueft, ob alle Parameter des Panels definiert wurden. */
    protected void validateParameters(JobDataMap jobDataMap) throws HurricanGUIException {
        if (StringUtils.isBlank(jobDataMap.getString("bill.month")) ||
                StringUtils.equals(jobDataMap.getString("bill.month"), "0")) {
            throw new HurricanGUIException("Bitte waehlen Sie einen Rechnungsmonat aus");
        }

        if (StringUtils.isBlank(jobDataMap.getString("bill.year"))) {
            throw new HurricanGUIException("Bitte waehlen Sie ein Rechnungsjahr aus");
        }

        if (StringUtils.isBlank(jobDataMap.getString("bill.stream"))) {
            throw new HurricanGUIException("Bitte waehlen Sie einen Billing-Stream aus");
        }

        if (StringUtils.isBlank(jobDataMap.getString("email"))) {
            throw new HurricanGUIException("Bitte definieren Sie EMail-Adressen, an die der Job " +
                    "Infos verschicken soll.");
        }
        else {
            // eMails auf Gueltigkeit pruefen
            String[] emails = StringUtils.split(jobDataMap.getString("email"), ",");
            for (int i = 0; i < emails.length; i++) {
                if (!EMailValidator.getInstance().isValid(emails[i])) {
                    throw new HurricanGUIException("Die EMail-Adresse " + emails[i] + " ist ungueltig!");
                }
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * CellRenderer fuer die Darstellung von BillRunViews.
     *
     *
     */
    static class BillRunCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof BillRunView) {
                lbl.setText(((BillRunView) value).getBillRunDescription());
            }

            return lbl;
        }
    }
}


