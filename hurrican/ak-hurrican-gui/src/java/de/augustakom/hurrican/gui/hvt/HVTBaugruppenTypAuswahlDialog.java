/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.2010 17:13:57
 */

package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Dialog zum Auswählen eines Baugruppentyps. Dieser Dialog kann mit jedem Referenzfeld, das Baugruppentypen zur Auswahl
 * stellt, verbunden werden. Die Verknüpfung kann indirekt über die SwingFactory XML Beschreibung geschehen. Hierzu
 * einfach <code>&lt;selection.dialog.class&gt;</code> in die Referenzfeld Deklaration einfügen.
 *
 *
 */
public class HVTBaugruppenTypAuswahlDialog extends AbstractServiceOptionDialog implements
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(HVTBaugruppenTypAuswahlDialog.class);

    // GUI-Komponenten
    private AKReflectionTableModel<HWBaugruppenTyp> tbMdlBaugruppenTypen = null;
    private AKJTable tbBaugruppenTypen = null;

    // Sonstiges
    private boolean initialized = false;

    public HVTBaugruppenTypAuswahlDialog() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTBaugruppenTypAuswahlDialog.xml");
        createGUI();
        init();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        tbMdlBaugruppenTypen = new AKReflectionTableModel<HWBaugruppenTyp>(
                new String[] { "ID", "Name", "Bemerkung" },
                new String[] { "id", "name", "description" },
                new Class[] { Long.class, String.class, String.class });
        tbBaugruppenTypen = new AKJTable(tbMdlBaugruppenTypen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbBaugruppenTypen.attachSorter();
        tbBaugruppenTypen.fitTable(new int[] { 70, 120, 200 });
        tbBaugruppenTypen.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane tableSP = new AKJScrollPane(tbBaugruppenTypen, new Dimension(420, 300));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(tableSP, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        AKJButton btnCMDSave = getButton(CMD_SAVE);
        if (btnCMDSave != null) {
            btnCMDSave.setToolTipText(getSwingFactory().getText("tooltip.cmd.save"));
            btnCMDSave.setText("Auswahl übernehmen");
            btnCMDSave.setMnemonic('ü');
        }
    }

    @Override
    protected void execute(String command) {
    }

    private void init() {
        if (!initialized) {
            try {
                HWService service = getCCService(HWService.class);
                List<HWBaugruppenTyp> baugruppenTypen = service.findAllBaugruppenTypen();
                tbMdlBaugruppenTypen.setData(baugruppenTypen);

                initialized = true;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void objectSelected(Object selection) {
        try {
            if (selection == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst einen Baugruppentyp aus!");
            }
            else {
                prepare4Close();
                setValue(selection);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected void doSave() {
        @SuppressWarnings("unchecked")
        AKMutableTableModel<HWBaugruppenTyp> tableModel = (AKMutableTableModel<HWBaugruppenTyp>) tbBaugruppenTypen.getModel();
        HWBaugruppenTyp typ = tableModel.getDataAtRow(tbBaugruppenTypen.getSelectedRow());
        objectSelected(typ);
    }

}
