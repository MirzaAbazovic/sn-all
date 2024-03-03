/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2004 11:43:00
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragCarrierTableModel;
import de.augustakom.hurrican.gui.auftrag.shared.WbciRequestCarrierViewTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierQuery;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.FeatureService;

/**
 * Filter-Panel fuer die Kundensuche ueber Carrier-Daten.
 */
class AuftragCarrierFilterPanel extends AbstractServicePanel implements
        IFilterOwner<AuftragCarrierQuery, AKTableModel<?>> {

    private static final Logger LOGGER = Logger.getLogger(AuftragCarrierFilterPanel.class);

    private static final long serialVersionUID = 2309215975302667227L;

    private KeyListener searchKL = null;

    private AKJTextField tfLbz = null;
    private AKJTextField tfVtrNr = null;
    private AKJTextField tfCarrierRefNr = null;
    private AKJTextField tfWbciVaId = null;

    private FeatureService featureService;

    /**
     * Konstruktor
     */
    public AuftragCarrierFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragCarrierFilterPanel.xml");
        this.searchKL = searchKL;
        init();
        createGUI();
    }

    private void init() {
        try {
            featureService = getCCService(FeatureService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblWita = getSwingFactory().createLabel("wita", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblWbci = getSwingFactory().createLabel("wbci", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblLbz = getSwingFactory().createLabel("lbz");
        AKJLabel lblVtrNr = getSwingFactory().createLabel("vtr.nr");
        AKJLabel lblCarrierRefNr = getSwingFactory().createLabel("carrier.ref.nr");
        AKJLabel lblWbciVaId = getSwingFactory().createLabel("wbci.va.id");

        tfLbz = getSwingFactory().createTextField("lbz", true, true, searchKL);
        tfVtrNr = getSwingFactory().createTextField("vtr.nr", true, true, searchKL);
        tfCarrierRefNr = getSwingFactory().createTextField("carrier.ref.nr", true, true, searchKL);
        tfWbciVaId = getSwingFactory().createTextField("wbci.va.id", true, true, searchKL);

        // @formatter:off
        AKJPanel wita = new AKJPanel(new GridBagLayout());
        wita.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        wita.add(lblWita         , GBCFactory.createGBC(  0,  0, 1, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        wita.add(lblLbz          , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        wita.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        wita.add(tfLbz           , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        wita.add(lblVtrNr        , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        wita.add(tfVtrNr         , GBCFactory.createGBC(  0,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        wita.add(lblCarrierRefNr , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        wita.add(tfCarrierRefNr  , GBCFactory.createGBC(  0,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        wita.add(new AKJPanel()  , GBCFactory.createGBC(100,100, 4, 4, 1, 1, GridBagConstraints.BOTH));

        AKJPanel wbci = new AKJPanel(new GridBagLayout());
        wbci.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        wbci.add(lblWbci         , GBCFactory.createGBC(  0,  0, 1, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        wbci.add(lblWbciVaId     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        wbci.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        wbci.add(tfWbciVaId      , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        wbci.add(new AKJPanel()  , GBCFactory.createGBC(100,100, 4, 2, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(wita           , GBCFactory.createGBC(  0,100,  0, 0, 1, 1, GridBagConstraints.VERTICAL));
        if (featureService.isFeatureOnline(Feature.FeatureName.WBCI_ENABLED)) {
            this.add(wbci       , GBCFactory.createGBC(  0,100, 1, 0, 1, 1, GridBagConstraints.VERTICAL, 15));
        }
        this.add(new AKJPanel() , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    @Override
    public AuftragCarrierQuery getFilter() {
        AuftragCarrierQuery query = new AuftragCarrierQuery();
        query.setLbz(tfLbz.getText());
        query.setVtrNr(tfVtrNr.getText());
        query.setCarrierRefNr(tfCarrierRefNr.getText());

        return query;
    }

    @Override
    public AKTableModel doSearch(AuftragCarrierQuery query) throws HurricanGUIException {
        try {
            String wbciVaId = tfWbciVaId.getText(null);
            if (wbciVaId != null && !query.isEmpty()) {
                throw new HurricanGUIException("Gleichzeitige Suche nach TAL/WITA und WBCI Daten ist nicht möglich!");
            }

            CCAuftragService service = getCCService(CCAuftragService.class);
            if (wbciVaId != null) {
                List<WbciRequestCarrierView> result = service.findWbciRequestCarrierViews(wbciVaId);

                WbciRequestCarrierViewTableModel tbModel = new WbciRequestCarrierViewTableModel();
                tbModel.setData(result);
                return tbModel;
            }
            else {
                List<AuftragCarrierView> result = service.findAuftragCarrierViews(query);

                AuftragCarrierTableModel tbModel = new AuftragCarrierTableModel();
                tbModel.setData(result);
                return tbModel;
            }
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AKTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        if (tableModel instanceof AuftragCarrierTableModel) {
            resultTable.fitTable(new int[] { 70, 70, 120, 80, 60, 70, 100, 100, 80, 20, 120, 80, 70, 35, 25, 70 });
        }
        else if (tableModel instanceof WbciRequestCarrierViewTableModel) {
            resultTable.fitTable(new int[] { 150, 150, 100, 110, 100, 70, 70, 70, 100 });
        }
    }

    @Override
    public void clearFilter() {
        GuiTools.cleanFields(this);
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }
}
