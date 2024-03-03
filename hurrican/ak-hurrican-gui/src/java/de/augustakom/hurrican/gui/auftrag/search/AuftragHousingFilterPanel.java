/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2011 11:54:29
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
import de.augustakom.hurrican.gui.auftrag.shared.AuftragHousingTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.cc.view.CCAuftragHousingView;
import de.augustakom.hurrican.model.shared.view.AuftragHousingQuery;
import de.augustakom.hurrican.service.cc.HousingService;


/**
 * Panel zur Suche nach Housing-Informationen.
 *
 *
 */
public class AuftragHousingFilterPanel extends AbstractServicePanel implements IFilterOwner<AuftragHousingQuery, AuftragHousingTableModel> {

    private KeyListener searchKL = null;

    private AKJTextField tfTransponderNr = null;
    private AKJTextField tfFirstname = null;
    private AKJTextField tfLastname = null;


    /**
     * Konstruktor
     */
    public AuftragHousingFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragHousingFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblTransponderNr = getSwingFactory().createLabel("transponder.id");
        AKJLabel lblFirstname = getSwingFactory().createLabel("transponderowner.firstname");
        AKJLabel lblLastname = getSwingFactory().createLabel("transponderowner.lastname");

        tfTransponderNr = getSwingFactory().createTextField("transponder.id", true, true, searchKL);
        tfFirstname = getSwingFactory().createTextField("transponderowner.firstname", true, true, searchKL);
        tfLastname = getSwingFactory().createTextField("transponderowner.lastname", true, true, searchKL);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblTransponderNr, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfTransponderNr, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblFirstname, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfFirstname, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLastname, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfLastname, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    public AuftragHousingQuery getFilter() throws HurricanGUIException {
        AuftragHousingQuery query = new AuftragHousingQuery();
        query.setTransponderNr(tfTransponderNr.getTextAsLong(null));
        query.setFirstName(tfFirstname.getText(null));
        query.setLastName(tfLastname.getText(null));

        return query;
    }

    @Override
    public AuftragHousingTableModel doSearch(AuftragHousingQuery query) throws HurricanGUIException {
        try {
            HousingService service = getCCService(HousingService.class);
            List<CCAuftragHousingView> result = service.findHousingsByQuery(query);

            AuftragHousingTableModel housingModel = new AuftragHousingTableModel();
            housingModel.setData(result);
            return housingModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragHousingTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 100, 100, 150, 200, 200 });
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


