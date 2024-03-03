/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2005 15:30:40
 */
package de.augustakom.hurrican.gui.auftrag.search;


import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragEquipmentTableModel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IFilterOwner;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Filter-Panel fuer die Suche ueber Equipment-Daten.
 *
 *
 */
public class EquipmentFilterPanel extends AbstractServicePanel implements IFilterOwner<AuftragEquipmentQuery, AuftragEquipmentTableModel>, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(EquipmentFilterPanel.class);
    private static final long serialVersionUID = -5026194829742828262L;

    private KeyListener searchKL = null;

    private AKReferenceField rfHVT = null;
    private AKJTextField tfEqBucht = null;
    private AKJTextField tfEqLeiste1 = null;
    private AKJTextField tfEqStift1 = null;
    private AKJComboBox cbSwitch = null;
    private AKJTextField tfHwEqn = null;
    private AKJTextField tfGeraetebezeichnung = null;
    private AKJTextField tfMgmtbezeichnung = null;
    private AKJCheckBox chbOnlyActive = null;

    private boolean dataLoaded = false;
    private List<HVTGruppeStdView> hvtViews = null;

    /**
     * Default-Konstruktor
     */
    public EquipmentFilterPanel(KeyListener searchKL) {
        super("de/augustakom/hurrican/gui/auftrag/resources/EquipmentFilterPanel.xml");
        this.searchKL = searchKL;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblEqBucht = getSwingFactory().createLabel("eq.bucht");
        AKJLabel lblEqLeiste1 = getSwingFactory().createLabel("eq.leiste1");
        AKJLabel lblEqStift1 = getSwingFactory().createLabel("eq.stift1");
        AKJLabel lblSwitch = getSwingFactory().createLabel("switch");
        AKJLabel lblHwEqn = getSwingFactory().createLabel("hw.eqn");
        AKJLabel lblGeraetebezeichnung = getSwingFactory().createLabel("eq.bezeichnung.geraet");
        AKJLabel lblMgmtbezeichnung = getSwingFactory().createLabel("eq.bezeichnung.mgmt");
        AKJLabel lblOnlyActive = getSwingFactory().createLabel("only.active");

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        rfHVT.setSize(new Dimension(200, 20));
        tfEqBucht = getSwingFactory().createTextField("eq.bucht", true, true, searchKL);
        tfEqLeiste1 = getSwingFactory().createTextField("eq.leiste1", true, true, searchKL);
        tfEqStift1 = getSwingFactory().createTextField("eq.stift1", true, true, searchKL);
        cbSwitch = getSwingFactory().createComboBox("switch");
        cbSwitch.setPreferredSize(new Dimension(150, 20));
        tfHwEqn = getSwingFactory().createTextField("hw.eqn", true, true, searchKL);
        tfGeraetebezeichnung = getSwingFactory().createTextField("eq.bezeichnung.geraet", true, true, searchKL);
        tfMgmtbezeichnung = getSwingFactory().createTextField("eq.bezeichnung.mgmt", true, true, searchKL);
        chbOnlyActive = getSwingFactory().createCheckBox("only.active");

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblHVT, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(rfHVT, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblEqBucht, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfEqBucht, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblEqLeiste1, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfEqLeiste1, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblEqStift1, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfEqStift1, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 4, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(lblSwitch, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        right.add(cbSwitch, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblHwEqn, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfHwEqn, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblGeraetebezeichnung, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfGeraetebezeichnung, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblMgmtbezeichnung, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfMgmtbezeichnung, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblOnlyActive, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbOnlyActive, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 5, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(right, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH, 5));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        if (!dataLoaded) {
            dataLoaded = true;

            try {
                setWaitCursor();

                if (CollectionTools.isEmpty(hvtViews)) {
                    HVTService hvtService = getCCService(HVTService.class);
                    hvtViews = hvtService.findHVTViews();
                }
                rfHVT.setReferenceList(hvtViews);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#getFilter()
     */
    @Override
    public AuftragEquipmentQuery getFilter() throws HurricanGUIException {
        AuftragEquipmentQuery query = new AuftragEquipmentQuery();
        query.setHvtIdStandort(rfHVT.getReferenceIdAs(Long.class));
        query.setEqBucht(tfEqBucht.getText(null));
        query.setEqLeiste1(tfEqLeiste1.getText(null));
        query.setEqStift1(tfEqStift1.getText(null));
        query.setEqSwitch((cbSwitch.getSelectedItem() instanceof String)
                ? (String) cbSwitch.getSelectedItem() : null);
        query.setEqHwEqn(tfHwEqn.getText(null));
        query.setEqGeraetebezeichnung(tfGeraetebezeichnung.getText(null));
        query.setEqMgmtbezeichnung(tfMgmtbezeichnung.getText(null));
        query.setOnlyActive(chbOnlyActive.isSelected());

        return query;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#doSearch(Object)
     */
    @Override
    public AuftragEquipmentTableModel doSearch(AuftragEquipmentQuery query) throws HurricanGUIException {
        try {
            CCAuftragService service = getCCService(CCAuftragService.class);
            List<AuftragEquipmentView> result = service.findAuftragEquipmentViews(query);

            AuftragEquipmentTableModel tbModel = new AuftragEquipmentTableModel();
            tbModel.setData(result);
            return tbModel;
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    @Override
    public void updateGui(AuftragEquipmentTableModel tableModel, AKJTable resultTable) {
        resultTable.setModel(tableModel);
        resultTable.attachSorter();
        resultTable.fitTable(new int[] { 130, 40, 80, 50, 50, 45, 40, 110, 100, 80, 70, 90, 120, 120, 120, 120 });
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


