/**
 * Copyright (c) 20ew10 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2010 11:01:16
 */

package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.temp.BASelectSubOrder;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel zur Ermittlung und Auswahl von Hurrican Auftraegen zu einem Taifun Auftrag. Das Panel wird dazu verwendet, die
 * Auftraege zu selektieren, die zu einem Verlauf gebuendelt werden sollen.
 *
 *
 */
public class BASelectSubOrdersPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(BADefinitionDialog.class);

    // String Konstanten
    private static final String RESOURCE = "de/augustakom/hurrican/gui/shared/resources/BASelectSubOrdersPanel.xml";

    // String Konstanten GUI
    private static final String TITLE = "title";
    private static final String ORDER_ID = "order.id";
    private static final String BILLING_ORDER_ID = "billing.order.id";

    // Daten
    private Long hauptAuftragId = null;
    private Long billingAuftragId = null;

    private Dimension scrollPaneDimension = null;
    private AKJFormattedTextField tfHauptAuftragId = null;
    private AKJFormattedTextField tfBillingAuftragId = null;
    private AKTableModelXML<BASelectSubOrder> tbMdlSelectOrders = null;

    public BASelectSubOrdersPanel(Long hauptAuftragId, Long billingAuftragId, Dimension scrollPaneDimension) {
        super(RESOURCE);

        // init primitive Datentypen
        this.hauptAuftragId = hauptAuftragId;
        this.billingAuftragId = billingAuftragId;
        this.scrollPaneDimension = scrollPaneDimension;

        //init GUI, Data
        createGUI();
        loadData();
        showData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblHauptAuftrag = getSwingFactory().createLabel(ORDER_ID);
        AKJLabel lblBillingAuftrag = getSwingFactory().createLabel(BILLING_ORDER_ID);

        tfHauptAuftragId = getSwingFactory().createFormattedTextField(ORDER_ID, false);
        tfBillingAuftragId = getSwingFactory().createFormattedTextField(BILLING_ORDER_ID, false);

        tbMdlSelectOrders = new AKTableModelXML<BASelectSubOrder>(
                "de/augustakom/hurrican/gui/shared/resources/BASelectSubOrdersTableModel.xml");
        AKJTable tbSelectOrders = new AKJTable(tbMdlSelectOrders, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSelectOrders.attachSorter();
        tbSelectOrders.fitTable(new int[] { 100, 120, 120 });

        AKJScrollPane scrollPane = new AKJScrollPane(tbSelectOrders);
        scrollPane.setPreferredSize(scrollPaneDimension);

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(TITLE)));
        this.add(lblHauptAuftrag, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfHauptAuftragId, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblBillingAuftrag, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfBillingAuftragId, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(scrollPane, GBCFactory.createGBC(100, 100, 0, 2, 4, 1, GridBagConstraints.BOTH));
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public final void loadData() {
        try {
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            ProduktService produktService = getCCService(ProduktService.class);
            BAService baService = getCCService(BAService.class);

            AuftragDaten hauptauftrag = auftragService.findAuftragDatenByAuftragId(hauptAuftragId);
            List<AuftragDaten> results = auftragService.findAuftragDaten4OrderNoOrig(billingAuftragId);

            if (CollectionTools.isNotEmpty(results) && (hauptauftrag != null)) {
                for (AuftragDaten order : results) {
                    if (!order.getAuftragId().equals(hauptAuftragId)
                            && !order.isInBetrieb()
                            && !order.isCancelled()
                            && hauptauftrag.matchSubOrderStatus(order)
                            && CollectionTools.isEmpty(baService.findAllActVerlauf4Auftrag(order.getAuftragId()))) {
                        Produkt produkt = produktService.findProdukt(order.getProdId());
                        BASelectSubOrder subOrder = new BASelectSubOrder(order, produkt);
                        tbMdlSelectOrders.addObject(subOrder);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

    private void showData() {
        tfHauptAuftragId.setValue(hauptAuftragId);
        tfBillingAuftragId.setValue(billingAuftragId);
    }

    /**
     * Filter die Unterauftraege, die selektiert sind und stellt diese in ein Set, das an den Aufrufer uebergeben wird.
     *
     * @return Set aus Auftrag IDs, null, wenn kein Unterauftrag existiert
     */
    public Set<Long> getSelectedSubOrders() {
        List<BASelectSubOrder> subOrders = (List<BASelectSubOrder>) tbMdlSelectOrders.getData();

        if (CollectionTools.isNotEmpty(subOrders)) {
            Set<Long> selectedSubOrders = new HashSet<Long>();
            for (BASelectSubOrder order : subOrders) {
                if (order.getSelected()) {
                    selectedSubOrders.add(order.getAuftragId());
                }
            }
            return selectedSubOrders;
        }
        return null;
    }

}
