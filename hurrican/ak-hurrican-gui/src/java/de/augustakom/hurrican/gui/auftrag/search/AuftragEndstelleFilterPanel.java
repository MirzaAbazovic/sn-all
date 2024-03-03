/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2004 09:15:00
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
import de.augustakom.hurrican.gui.auftrag.shared.AuftragEndstelleTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Filter-Panel fuer die Kundensuche ueber Endstellen- und Leitungsdaten.
 */
class AuftragEndstelleFilterPanel extends AbstractServicePanel implements IFilterOwner<AuftragEndstelleQuery, AuftragEndstelleTableModel> {
    public static final String TF_VBZ = "vbz";

    private KeyListener searchKL = null;

    private AKJTextField tfEndstelle = null;
    private AKJTextField tfESOrt = null;
    private AKJTextField tfVbz = null;
    private AKJTextField tfLtgArt = null;


    /**
     * Konstruktor
     */
    public AuftragEndstelleFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragEndstelleFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblEndstelle = getSwingFactory().createLabel("endstelle");
        AKJLabel lblESOrt = getSwingFactory().createLabel("endstelle.ort");
        AKJLabel lblVbz = getSwingFactory().createLabel(TF_VBZ);
        AKJLabel lblLtgArt = getSwingFactory().createLabel("ltgArt");

        tfEndstelle = getSwingFactory().createTextField("endstelle", true, true, searchKL);
        tfESOrt = getSwingFactory().createTextField("endstelle.ort", true, true, searchKL);
        tfVbz = getSwingFactory().createTextField(TF_VBZ, true, true, searchKL);
        tfLtgArt = getSwingFactory().createTextField("ltgArt", true, true, searchKL);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblEndstelle, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfEndstelle, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblESOrt, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfESOrt, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVbz, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfVbz, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLtgArt, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfLtgArt, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    public AuftragEndstelleQuery getFilter() throws HurricanGUIException {
        AuftragEndstelleQuery query = new AuftragEndstelleQuery();
        query.setEndstelle(tfEndstelle.getText());
        query.setEndstelleOrt(tfESOrt.getText());
        query.setVbz(tfVbz.getText());
        query.setLtgArt((tfLtgArt.getText()));

        return query;
    }

    @Override
    public AuftragEndstelleTableModel doSearch(AuftragEndstelleQuery query) throws HurricanGUIException {
        try {
            CCAuftragService service = getCCService(CCAuftragService.class);
            List<AuftragEndstelleView> result = service.findAuftragEndstelleViews(query);

            AuftragEndstelleTableModel tbModel = new AuftragEndstelleTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragEndstelleTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 120, 70, 70, 70, 120, 80, 15, 70, 70, 120, 100, 100, 120, 70 });
    }

    @Override
    public void clearFilter() {
        GuiTools.cleanFields(this);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}

