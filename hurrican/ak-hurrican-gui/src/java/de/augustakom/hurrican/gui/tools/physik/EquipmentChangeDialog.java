/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 28.07.2010 09:41:19
  */

package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.shared.Equipment4RangierungTableModel;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
public class EquipmentChangeDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(EquipmentChangeDialog.class);

    private static final String DIALOG_TITLE = "dialog.title";
    private static final String CHOOSE_EQ_OUT2 = "choose.eq.out2";
    private static final String CHOOSE_EQ_IN2 = "choose.eq.in2";
    private static final String CHOOSE_EQ_OUT = "choose.eq.out";
    private static final String CHOOSE_EQ_IN = "choose.eq.in";

    private Equipment4RangierungTableModel tbMdlEquipments;

    private final Rangierung rangierung;
    private final Rangierung rangierungAdd;

    private RangierungsService rangierungsService;

    /**
     * @param resource
     */
    public EquipmentChangeDialog(Rangierung rangierungToChange, Rangierung rangierungAddToChange) {
        super("de/augustakom/hurrican/gui/tools/physik/resources/EquipmentChangeDialog.xml");
        rangierung = rangierungToChange;
        rangierungAdd = rangierungAddToChange;
        createGUI();
        tbMdlEquipments.setRangierung(rangierung, rangierungAdd);
        try {
            rangierungsService = getCCService(RangierungsService.class);
        }
        catch (Exception ex) {
            LOGGER.error(ex, ex);
            MessageHelper.showErrorDialog(this, ex);
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    protected void doSave() {
        try {
            RangierungsService rangierungsService = getCCService(RangierungsService.class);
            rangierungsService.assignEquipment2Rangierung(rangierung, rangierung.getEquipmentIn(), rangierung.getEquipmentOut(), true, true, true);
            rangierungsService.assignEquipment2Rangierung(rangierungAdd, rangierungAdd.getEquipmentIn(), rangierungAdd.getEquipmentOut(), true, true, true);
            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception ex) {
            LOGGER.error(ex, ex);
            MessageHelper.showErrorDialog(this, ex);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(DIALOG_TITLE));
        tbMdlEquipments = new Equipment4RangierungTableModel();
        AKJTable tbEquipments = new AKJTable(tbMdlEquipments, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbMdlEquipments.setTable(tbEquipments);
        tbEquipments.fitTable(new int[] { 105, 125, 125, 125, 125 });
        AKJScrollPane spEquipments = new AKJScrollPane(tbEquipments);
        spEquipments.setPreferredSize(new Dimension(610, 360));

        AKJButton btnSwitchEqIn = getSwingFactory().createButton(CHOOSE_EQ_IN, getActionListener());
        AKJButton btnSwitchEqOut = getSwingFactory().createButton(CHOOSE_EQ_OUT, getActionListener());
        AKJButton btnSwitchEqIn2 = getSwingFactory().createButton(CHOOSE_EQ_IN2, getActionListener());
        AKJButton btnSwitchEqOut2 = getSwingFactory().createButton(CHOOSE_EQ_OUT2, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSwitchEqIn, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSwitchEqOut, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSwitchEqIn2, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSwitchEqOut2, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(spEquipments, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        panel.add(btnPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().add(panel);
    }

    @Override
    protected void execute(String command) {
        if (CHOOSE_EQ_IN.equals(command)) {
            ChooseEquipmentDialog dlg = new ChooseEquipmentDialog(rangierung.getHvtIdStandort(), rangierung.getEquipmentIn().getHwSchnittstelle());
            Equipment eq = (Equipment) DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (eq != null) {
                rangierung.setEquipmentIn(eq);
                if (rangierungAdd.getEquipmentOut() != null) {
                    Equipment match = rangierungsService.findCorrespondingEquipment(eq);
                    if (match != null) {
                        rangierungAdd.setEquipmentOut(match);
                    }
                }
                tbMdlEquipments.fireTableDataChanged();
            }
        }
        else if (CHOOSE_EQ_OUT.equals(command)) {
            ChooseEquipmentDialog dlg = new ChooseEquipmentDialog(rangierung.getHvtIdStandort(), rangierung.getEquipmentOut().getHwSchnittstelle());
            Equipment eq = (Equipment) DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (eq != null) {
                rangierung.setEquipmentOut(eq);
                tbMdlEquipments.fireTableDataChanged();
            }
        }
        else if (CHOOSE_EQ_IN2.equals(command)) {
            ChooseEquipmentDialog dlg = new ChooseEquipmentDialog(rangierung.getHvtIdStandort(), rangierungAdd.getEquipmentIn().getHwSchnittstelle());
            Equipment eq = (Equipment) DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (eq != null) {
                rangierungAdd.setEquipmentIn(eq);
                tbMdlEquipments.fireTableDataChanged();
            }
        }
        else if (CHOOSE_EQ_OUT2.equals(command)) {
            ChooseEquipmentDialog dlg = new ChooseEquipmentDialog(rangierung.getHvtIdStandort(), rangierungAdd.getEquipmentOut().getHwSchnittstelle());
            Equipment eq = (Equipment) DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (eq != null) {
                rangierungAdd.setEquipmentOut(eq);
                if (rangierung.getEquipmentIn() != null) {
                    Equipment match = rangierungsService.findCorrespondingEquipment(eq);
                    if (match != null) {
                        rangierung.setEquipmentIn(match);
                    }
                }
                tbMdlEquipments.fireTableDataChanged();
            }
        }
    }

}
