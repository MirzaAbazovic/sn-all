/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2013 07:39:57
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.billing.view.BAuftragView;
import de.augustakom.hurrican.service.billing.BillingAuftragService;

/**
 * Wizard-Panel zur Anzeige aller Taifun-Auftraege des Kunden. Durch Selektion eines Auftrags wird speziell dieser
 * Auftrag fuer den Kunden abgeglichen.
 */
public class TaifunOrderSelectionWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(TaifunOrderSelectionWizardPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/wizards/abgleich/TaifunOrderSelectionWizardPanel.xml";

    private AKJTable tbTaifunOrders = null;
    private AKReflectionTableModel<BAuftragView> tbMdlTaifunOrders = null;

    public TaifunOrderSelectionWizardPanel(AKJWizardComponents wizardComponents) {
        super(RESOURCE, wizardComponents);
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        // @formatter:off
        tbMdlTaifunOrders = new AKReflectionTableModel<BAuftragView>(
            new String[]{"Taifun A.-Nr", "Produkt", "Typ", "Status", "Bearbeiter", "letzte Änd."},
            new String[]{BAuftragView.AUFTRAG_NO_ORIG, BAuftragView.PRODUCT_NAME, BAuftragView.ATYP, BAuftragView.HIST_STATUS, BAuftragView.LAST_MODIFIED_BY, BAuftragView.LAST_MODIFIED_AT},
            new Class[]{Long.class, String.class, String.class, String.class, String.class, Date.class});
        // @formatter:on

        tbTaifunOrders = new AKJTable(tbMdlTaifunOrders, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbTaifunOrders.attachSorter();
        tbTaifunOrders.fitTable(new int[] { 80, 130, 75, 80, 120, 100 });
        tbTaifunOrders.addMouseListener(new TableMouseListener());

        AKJScrollPane spTable = new AKJScrollPane(tbTaifunOrders);
        spTable.setPreferredSize(new Dimension(735, 230));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    @Override
    public final void loadData() {
        try {
            Long kundeNo = (Long) getWizardObject(WIZARD_OBJECT_KUNDEN_NO);

            BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);
            List<BAuftragView> auftragViews = billingAuftragService.findAuftragViews(kundeNo);

            tbMdlTaifunOrders.setData(auftragViews);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected boolean goNext() {
        @SuppressWarnings("unchecked")
        AKMutableTableModel<BAuftragView> tbMdl = (AKMutableTableModel<BAuftragView>) tbTaifunOrders.getModel();
        int selection = tbTaifunOrders.getSelectedRow();
        Object selObj = tbMdl.getDataAtRow(selection);
        if (selObj instanceof BAuftragView) {
            // zum Abgleich gewaehlten Taifun-Auftrag als Wizard-Objekt "merken"
            addWizardObject(WIZARD_OBJECT_SELECTED_TAIFUN_ORDER, ((BAuftragView) selObj).getAuftragNoOrig());

            getWizardComponents().addWizardPanel(new AuftragsMonitorWizardPanel(getWizardComponents()));
            return super.goNext();
        }
        else {
            String msg = getSwingFactory().getText("keine.selektion.msg");
            MessageHelper.showInfoDialog(this, msg, null, true);
            return false;
        }
    }

    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_BACKWARD) {
            removePanelsAfter(this);
        }
    }

    class TableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (tbTaifunOrders.getSelectedRow() >= 0) {
                setNextButtonEnabled(true);
            }
            else {
                setNextButtonEnabled(false);
            }
        }
    }
}


