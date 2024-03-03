/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2009 11:59:25
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.auftrag.shared.VPNVertragViewTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Dialog zur Suche / Auswahl eines VPNs. <br> Der Dialog liefert als Result ein Objekt vom Typ {@link VPNVertragView}
 * zurueck, sofern ein VPN ausgewaehlt wurde.
 *
 *
 */
public class VPNSearchDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(VPNSearchDialog.class);

    private VPNVertragTable tbVPNs = null;
    private VPNVertragViewTableModel tbMdlVPNs = null;

    private VPNService vpnService;

    /**
     * Default-Konstruktor.
     */
    public VPNSearchDialog() {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/VPNSearchDialog.xml");
        createGUI();
        loadData();
    }

    private void init() throws ServiceNotFoundException {
        vpnService = getCCService(VPNService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        tbMdlVPNs = new VPNVertragViewTableModel(false);
        tbVPNs = new VPNVertragTable(tbMdlVPNs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVPNs.setDefaultRenderer(Niederlassung.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : ((Niederlassung) value).getName());
            }
        });
        tbVPNs.attachSorter();
        tbVPNs.fitTable(new int[] { 80, 80, 120, 80, 120, 100, 100, 80, 80, 120 });
        AKJScrollPane spVPNs = new AKJScrollPane(tbVPNs);
        spVPNs.setPreferredSize(new Dimension(800, 300));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(spVPNs, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    protected void validateSaveButton() {
    }

    @Override
    public final void loadData() {
        try {
            init();
            List<VPNVertragView> vpns = vpnService.findVPNVertraege();
            tbMdlVPNs.setData(vpns);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        int selectedRow = tbVPNs.getSelectedRow();
        if (selectedRow >= 0) {
            prepare4Close();

            @SuppressWarnings("unchecked")
            AKMutableTableModel<VPNVertragView> tm = (AKMutableTableModel<VPNVertragView>) tbVPNs.getModel();
            Object selection = tm.getDataAtRow(selectedRow);
            if (selection instanceof VPNVertragView) {
                setValue(selection);
            }
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable arg0, Object arg1) {
    }

}


