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
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragDNTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Filter-Panel fuer die Kundensuche ueber eine Rufnummer
 */
class RufnummerFilterPanel extends AbstractServicePanel implements IFilterOwner<RufnummerQuery, AuftragDNTableModel> {

    private KeyListener searchKL = null;

    private AKJTextField tfONKZ = null;
    private AKJTextField tfRN = null;
    private AKJCheckBox cbOnlyAct = null;

    /**
     * Konstruktor
     */
    public RufnummerFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/RufnummerFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblONKZ = getSwingFactory().createLabel("onkz");
        AKJLabel lblRN = getSwingFactory().createLabel("rufnummer");
        AKJLabel lblOnlyAct = getSwingFactory().createLabel("only.active");

        tfONKZ = getSwingFactory().createTextField("onkz", true, true, searchKL);
        tfRN = getSwingFactory().createTextField("rufnummer", true, true, searchKL);
        cbOnlyAct = getSwingFactory().createCheckBox("only.active");

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblONKZ, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfONKZ, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblRN, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfRN, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblOnlyAct, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cbOnlyAct, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#getFilter()
     */
    public RufnummerQuery getFilter() throws HurricanGUIException {
        RufnummerQuery query = new RufnummerQuery();
        query.setOnKz(tfONKZ.getText());
        query.setDnBase(tfRN.getText());
        query.setOnlyActive(cbOnlyAct.isSelected());

        if (StringUtils.isBlank(query.getOnKz())) {
            throw new HurricanGUIException("Bitte eine Vorwahl (OnKz) für die Suche angeben!");
        }

        if (WildcardTools.SYSTEM_WILDCARD.equals(query.getOnKz()) || WildcardTools.DB_WILDCARD.equals(query.getOnKz())) {
            throw new HurricanGUIException("Bitte geben Sie eine Vorwahl für die Suche ein.");
        }

        if (StringUtils.isBlank(query.getDnBase())) {
            throw new HurricanGUIException("Bitte eine Rufnummer für die Suche angeben!");
        }

        if (WildcardTools.SYSTEM_WILDCARD.equals(query.getDnBase()) || WildcardTools.DB_WILDCARD.equals(query.getDnBase())) {
            throw new HurricanGUIException("Bitte geben Sie eine Rufnummer für die Suche ein.");
        }

        return query;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#doSearch(de.augustakom.common.gui.swing.AKJTable)
     */
    public AuftragDNTableModel doSearch(RufnummerQuery query) throws HurricanGUIException {
        try {
            RufnummerService service = getBillingService(RufnummerService.class);
            List<AuftragDNView> result = service.findAuftragDNViews(query);

            AuftragDNTableModel tbModel = new AuftragDNTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragDNTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 70, 70, 45, 50, 50, 50, 70, 40, 85, 70, 80, 110, 90, 70, 35, 25, 60, 25, 40, 40, 60, 60, 70, 70 });
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

