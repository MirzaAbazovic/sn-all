/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 17:36:46
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import static java.lang.Boolean.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.tools.tal.ColoredRowTable;
import de.augustakom.hurrican.gui.tools.tal.XmlImmutableRowTableModel;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.WitaCBVorgang;

public class TalSubOrderPanel extends AbstractServicePanel {

    private static final long serialVersionUID = -6008111353067089667L;

    private static final String DESELECT_ALL_BUTTON = "deselect.all";
    private static final String SELECT_ALL_BUTTON = "select.all";
    private XmlImmutableRowTableModel<TalSubOrder> tbMdlSubOrders;
    private ColoredRowTable tbSubOrders;
    private AKJButton selectAll;
    private AKJButton deselectAll;
    private TalSubOrder immutableSubOrder;

    private List<TalSubOrder> subOrdersForSelection;

    public TalSubOrderPanel() {
        super("de/augustakom/hurrican/gui/tools/tal/wita/resources/TalSubOrderPanel.xml");
        createGUI();
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSuborders = getSwingFactory().createLabel("suborders");
        tbMdlSubOrders = new XmlImmutableRowTableModel<TalSubOrder>(
                "de/augustakom/hurrican/gui/tools/tal/wita/resources/TalSubOrderTable.xml");
        tbSubOrders = new ColoredRowTable(tbMdlSubOrders, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbSubOrders.fitTable(tbMdlSubOrders.getFitList());
        AKJScrollPane spSubOrders = new AKJScrollPane(tbSubOrders, new Dimension(500, 110));

        selectAll = getSwingFactory().createButton(SELECT_ALL_BUTTON, getActionListener());
        deselectAll = getSwingFactory().createButton(DESELECT_ALL_BUTTON, getActionListener());

        enableButtons(false);

        this.setLayout(new GridBagLayout());
        // @formatter:off
        int actcol = 0;
        int actline = 0;
        this.add(lblSuborders,   GBCFactory.createGBC(0,   0, actcol++, actline,   1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, actcol++, actline,   1, 1, GridBagConstraints.NONE));
        this.add(selectAll,      GBCFactory.createGBC(0,   0, actcol++, actline,   1, 1, GridBagConstraints.NONE));
        this.add(deselectAll,    GBCFactory.createGBC(0,   0, actcol++, actline++, 1, 1, GridBagConstraints.NONE));
        actcol = 0;
        this.add(spSubOrders,    GBCFactory.createGBC(0, 100, actcol++, actline++, 4, 1, GridBagConstraints.BOTH));
        // @formatter:on

    }

    private void enableButtons(boolean enabled) {
        selectAll.setEnabled(enabled);
        deselectAll.setEnabled(enabled);
    }

    @Override
    protected void execute(String command) {
        if (SELECT_ALL_BUTTON.equals(command)) {
            setSelectionForAllSubOrders(true);
        }
        else if (DESELECT_ALL_BUTTON.equals(command)) {
            setSelectionForAllSubOrders(false);
        }
    }

    private void setSelectionForAllSubOrders(boolean selected) {
        for (TalSubOrder subOrder : subOrdersForSelection) {
            if (subOrder != immutableSubOrder) {
                subOrder.setSelected(selected);
            }
        }
        tbSubOrders.repaint();
    }

    public void setData(List<TalSubOrder> subOrders, List<WitaCBVorgang> cbVorgaenge) {

        // wenn nur ein CbVorgang vorhanden, dann immutable element, sonst freie Auswahl
        if (cbVorgaenge.size() == 1) {
            CBVorgang cbVorgang = cbVorgaenge.get(0);

            for (TalSubOrder talSubOrder : subOrders) {
                if (talSubOrder.getCbVorgangId().equals(cbVorgang.getId())) {
                    immutableSubOrder = talSubOrder;
                    break;
                }
            }
            tbSubOrders.setColoredObject(immutableSubOrder);
            tbMdlSubOrders.setImmutableObject(immutableSubOrder);
        }
        subOrdersForSelection = subOrders;
        tbMdlSubOrders.setData(subOrders);

        enableButtons(true);
    }

    public Set<Long> getSelectedCbVorgaenge() {
        Set<Long> cbSelectedVorgangsIds = new HashSet<Long>();

        // Ermittlung der CBVorgaenge, die ebenfalls verschoben werden sollen
        if (CollectionTools.isNotEmpty(subOrdersForSelection)) {
            for (TalSubOrder subOrder : subOrdersForSelection) {
                if (TRUE.equals(subOrder.getSelected())) {
                    cbSelectedVorgangsIds.add(subOrder.getCbVorgangId());
                }
            }
        }
        return cbSelectedVorgangsIds;
    }

}
