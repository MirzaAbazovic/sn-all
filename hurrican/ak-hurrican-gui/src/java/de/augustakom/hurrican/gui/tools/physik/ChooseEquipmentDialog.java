/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 28.07.2010 12:19:20
  */

package de.augustakom.hurrican.gui.tools.physik;

import static de.augustakom.hurrican.model.cc.Equipment.*;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
public class ChooseEquipmentDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ChooseEquipmentDialog.class);

    private AKReflectionTableModel<Equipment> tbMdlEq = null;
    private AKJTable tbEq = null;

    private final Long hvtIdStandort;
    private final String hwSchnittstelle;

    public ChooseEquipmentDialog(Long hvtIdStandort, String hwSchnittstelle) {
        super("de/augustakom/hurrican/gui/tools/physik/resources/ChooseEquipmentDialog.xml");
        this.hvtIdStandort = hvtIdStandort;
        this.hwSchnittstelle = hwSchnittstelle;
        createGUI();
        loadData();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    protected void doSave() {
        prepare4Close();
        setValue(tbMdlEq.getDataAtRow(tbEq.getSelectedRow()));
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("dialog.title"));

        tbMdlEq = new AKReflectionTableModel<Equipment>(
                new String[] { "HW-EQN", "Status", "Bucht", "Leiste", "Stift",
                        "Rang-SS-Typ", "R.-Schnittst.", "UETV", "KVZ Nr", "Verwendung" },
                new String[] { HW_EQN, STATUS, RANG_BUCHT, RANG_LEISTE1, RANG_STIFT1,
                        RANG_SS_TYPE, RANG_SCHNITTSTELLE, UETV, KVZ_NUMMER, VERWENDUNG },
                new Class[] { String.class, String.class, String.class, String.class, String.class,
                        String.class, String.class, String.class, String.class, Enum.class }
        );
        tbEq = new AKJTable(tbMdlEq, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbEq.attachSorter();
        tbEq.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbEq.fitTable(new int[] { 100, 100, 100, 80, 60, 60, 60, 60, 90, 90, 60, 60, 90 });
        AKJScrollPane spEqOut = new AKJScrollPane(tbEq, new Dimension(890, 250));

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(spEqOut, GBCFactory.createGBC(100, 100, 0, 1, 4, 1, GridBagConstraints.BOTH));

        getChildPanel().add(panel);
    }

    @Override
    protected void execute(String command) {

    }

    @Override
    public void objectSelected(Object selection) {
        prepare4Close();
        setValue(selection);
    }

    @Override
    public final void loadData() {
        try {
            RangierungsService rangierungsService = getCCService(RangierungsService.class);
            Equipment example = new Equipment();
            example.setHvtIdStandort(hvtIdStandort);
            example.setStatus(EqStatus.frei);
            example.setHwSchnittstelle(hwSchnittstelle);
            List<Equipment> equipments = rangierungsService.findEquipments(example, "rangBucht", "rangLeiste1", "rangStift1");
            tbMdlEq.setData(equipments);
        }
        catch (Exception ex) {
            LOGGER.error(ex, ex);
            MessageHelper.showErrorDialog(this, ex);
        }
    }

}
