/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.03.2007 09:28:57
 */
package de.augustakom.hurrican.gui.tools.stats;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.ArchPrintSet;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Dialog, um einen Bill-Cycle (bzw. ein Objekt vom Typ ArchPrintSet) auszuwaehlen.
 *
 *
 */
public class SelectArchPrintSetDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(SelectArchPrintSetDialog.class);

    private AKJComboBox cbPrintSet = null;

    /**
     * Default-Const.
     */
    public SelectArchPrintSetDialog() {
        super("de/augustakom/hurrican/gui/tools/stats/resources/SelectArchPrintSetDialog.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblPrintSet = getSwingFactory().createLabel("print.set");
        cbPrintSet = getSwingFactory().createComboBox("print.set",
                new AKCustomListCellRenderer<>(ArchPrintSet.class, ArchPrintSet::getName));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(lblPrintSet, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 5));
        getChildPanel().add(cbPrintSet, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            RechnungsService rs = getBillingService(RechnungsService.class);
            List<ArchPrintSet> printSets = rs.findArchPrintSets();

            // Reverse result-set
            Collection reversedPrintSets = CollectionTools.reverse(printSets);
            cbPrintSet.addItems(reversedPrintSets, true, ArchPrintSet.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        prepare4Close();
        setValue(cbPrintSet.getSelectedItem());
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


