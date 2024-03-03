/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2009 08:47:35
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.shared.view.InnenauftragQuery;
import de.augustakom.hurrican.model.shared.view.InnenauftragView;
import de.augustakom.hurrican.service.cc.InnenauftragService;


/**
 * Filter-Panel fuer die Auftragssuche ueber Innenauftragsdaten.
 */
class InnenauftragFilterPanel extends AbstractServicePanel implements IFilterOwner<InnenauftragQuery,
        AKReflectionTableModel<InnenauftragView>> {

    private KeyListener searchKL = null;

    private AKJTextField tfIA = null;
    private AKJTextField tfBedarfsNr = null;

    /**
     * Konstruktor
     */
    public InnenauftragFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/InnenauftragFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblIA = getSwingFactory().createLabel("ia.nummer");
        AKJLabel lblBedarfsNr = getSwingFactory().createLabel("bedarfs.nr");

        tfIA = getSwingFactory().createTextField("ia.nummer", true, true, searchKL);
        tfBedarfsNr = getSwingFactory().createTextField("bedarfs.nr", true, true, searchKL);

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblIA, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        this.add(tfIA, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblBedarfsNr, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfBedarfsNr, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#getFilter()
     */
    @Override
    public InnenauftragQuery getFilter() {
        InnenauftragQuery query = new InnenauftragQuery();
        query.setIaNummer(tfIA.getText(null));
        query.setBedarfsNr(tfBedarfsNr.getText(null));
        return query;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#doSearch(de.augustakom.common.gui.swing.AKJTable)
     */
    @Override
    public AKReflectionTableModel<InnenauftragView> doSearch(InnenauftragQuery query) throws HurricanGUIException {
        try {
            InnenauftragService ias = getCCService(InnenauftragService.class);
            List<InnenauftragView> result = ias.findIAViews(query);

            AKReflectionTableModel<InnenauftragView> tbMdl = new AKReflectionTableModel<InnenauftragView>(
                    new String[] { "Auftrag-Nr", "Verb.bez.", "IA-Nr", "Bedarfs-Nr", "Auftrag-Status",
                            "Produkt", "Inbetriebnahme", "Arbeitstyp", "Kunde_No", "Name", "Vorname", "Bemerkung" },
                    new String[] { "auftragId", "vbz", "iaNummer", "bedarfsNr", "auftragStatusText",
                            "anschlussart", "inbetriebnahme", "workingType", "kundeNo", "name", "vorname", "auftragBemerkung" },
                    new Class[] { Long.class, String.class, String.class, String.class, String.class,
                            String.class, Date.class, String.class, Long.class, String.class, String.class, String.class }
            );
            tbMdl.setData(result);
            return tbMdl;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AKReflectionTableModel<InnenauftragView> tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 70, 100, 100, 80, 120, 120, 80, 120, 80, 120, 120, 100 });
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#clearFilter()
     */
    @Override
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
    @Override
    public void update(Observable o, Object arg) {
    }
}

