/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2007 13:17:00
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.auftrag.shared.RInfoAdresseTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Filter-Panel fuer die Kundensuche ueber Rechnungsdaten.
 */
class RechnungFilterPanel extends AbstractServicePanel implements IFilterOwner<RInfoQuery, RInfoAdresseTableModel> {

    private KeyListener searchKL = null;

    private AKJTextField tfDebNo = null;

    /**
     * Konstruktor
     */
    public RechnungFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/RechnungFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblDebNo = getSwingFactory().createLabel("sap.debitor");

        tfDebNo = getSwingFactory().createTextField("sap.debitor", true, true, searchKL);

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblDebNo, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfDebNo, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#getFilter()
     */
    public RInfoQuery getFilter() throws HurricanGUIException {
        RInfoQuery query = new RInfoQuery();
        query.setSapDebitorNo(tfDebNo.getText());

        return query;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#doSearch(de.augustakom.common.gui.swing.AKJTable)
     */
    public RInfoAdresseTableModel doSearch(RInfoQuery query) throws HurricanGUIException {
        try {
            List<RInfoAdresseView> result = null;

            if (!query.isEmpty()) {
                RechnungsService service = getBillingService(RechnungsService.class);
                result = service.findKundeByRInfoQuery(query);
            }

            RInfoAdresseTableModel tbModel = new RInfoAdresseTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(RInfoAdresseTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 80, 80, 120, 120, 100, 50, 50, 100, 80, 140 });
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#clearFilter()
     */
    public void clearFilter() {
        GuiTools.cleanFields(this);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }
}

