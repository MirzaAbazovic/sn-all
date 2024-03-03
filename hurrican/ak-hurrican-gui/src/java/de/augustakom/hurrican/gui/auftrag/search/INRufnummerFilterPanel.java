/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 08:43:00
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
import de.augustakom.hurrican.gui.auftrag.shared.AuftragINRufnummerTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.billing.query.BAuftragBNFCQuery;
import de.augustakom.hurrican.model.shared.view.AuftragINRufnummerView;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Filter-Panel fuer die Kundensuche ueber eine IN-Rufnummer
 */
class INRufnummerFilterPanel extends AbstractServicePanel implements IFilterOwner<BAuftragBNFCQuery, AuftragINRufnummerTableModel> {

    private KeyListener searchKL = null;

    private AKJTextField tfPrefix = null;
    private AKJTextField tfBusinessNr = null;

    /**
     * Konstruktor
     */
    public INRufnummerFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/INRufnummerFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblPrefix = getSwingFactory().createLabel("in.prefix");
        AKJLabel lblBusinessNr = getSwingFactory().createLabel("in.business.nr");

        tfPrefix = getSwingFactory().createTextField("in.rufnummer", true, true, searchKL);
        tfBusinessNr = getSwingFactory().createTextField("in.business.nr", true, true, searchKL);

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblPrefix, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfPrefix, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblBusinessNr, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfBusinessNr, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#getFilter()
     */
    public BAuftragBNFCQuery getFilter() {
        BAuftragBNFCQuery query = new BAuftragBNFCQuery();
        query.setPrefix(tfPrefix.getText());
        query.setBusinessNr(tfBusinessNr.getText());
        return query;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#doSearch(de.augustakom.common.gui.swing.AKJTable)
     */
    public AuftragINRufnummerTableModel doSearch(BAuftragBNFCQuery query) throws HurricanGUIException {
        try {
            RufnummerService service = getBillingService(RufnummerService.class);
            List<AuftragINRufnummerView> result = service.findAuftragINRufnummerViews(query);

            AuftragINRufnummerTableModel tbModel = new AuftragINRufnummerTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragINRufnummerTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 70, 70, 70, 120, 80, 100, 70, 70, 110 });
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
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }
}

