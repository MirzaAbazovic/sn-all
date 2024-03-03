/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2007 13:17:51
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Lager;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zur Anlage einer Material-Entnahme. <br> Die angelegte Materialentnahme wird ueber die Methode
 * <code>setValue</code> gesetzt.
 *
 *
 */
public class IAMaterialEntnahmeDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(IAMaterialEntnahmeDialog.class);

    private IABudget budget = null;
    private List<Lager> lager = null;

    private AKJComboBox cbLager = null;
    private AKJComboBox cbEntnahmetyp = null;
    private AKReferenceField rfHVT = null;

    /**
     * Konstruktor fuer den Dialog
     *
     * @param budget Budget, zu dem eine Materialentnahme angelegt werden soll
     * @param lager  Liste mit den verfuegbaren Lagern
     */
    public IAMaterialEntnahmeDialog(IABudget budget, List<Lager> lager) {
        super("de/augustakom/hurrican/gui/auftrag/innenauftrag/resources/IAMaterialEntnahmeDialog.xml");
        this.budget = budget;
        this.lager = lager;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblTyp = getSwingFactory().createLabel("entnahmetyp");
        AKJLabel lblLager = getSwingFactory().createLabel("lager");
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");

        Dimension cbDim = new Dimension(150, 22);
        cbEntnahmetyp = getSwingFactory().createComboBox("entnahmetyp");
        cbEntnahmetyp.setPreferredSize(cbDim);
        cbLager = getSwingFactory().createComboBox("lager",
                new AKCustomListCellRenderer<>(Lager.class, Lager::getName));
        cbLager.setPreferredSize(cbDim);
        cbLager.addItems(lager, true, Lager.class, true);
        rfHVT = getSwingFactory().createReferenceField("hvt.standort");

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblTyp, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbEntnahmetyp, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblLager, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(cbLager, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblHVT, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(rfHVT, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHVT.setFindService(sfs);
            rfHVT.setReferenceList(hvtViews);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // nothing to do
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            IAMaterialEntnahme me = new IAMaterialEntnahme();
            me.setIaBudgetId(budget.getId());
            me.setEntnahmetyp((Short) cbEntnahmetyp.getSelectedItemValue());
            me.setLagerId((Long) cbLager.getSelectedItemValue("getId", Long.class));
            me.setHvtIdStandort(rfHVT.getReferenceIdAs(Long.class));

            InnenauftragService ias = getCCService(InnenauftragService.class);
            ias.saveMaterialEntnahme(me, HurricanSystemRegistry.instance().getSessionId());

            prepare4Close();
            setValue(me);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
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


