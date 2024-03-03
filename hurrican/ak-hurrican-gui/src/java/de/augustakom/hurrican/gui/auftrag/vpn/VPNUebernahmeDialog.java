/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 10:50:01
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.view.BVPNAuftragView;


/**
 * Dialog, um einen VPN-Auftrag aus dem Billing-System auszuwaehlen. Der ausgewaehlte Auftrag wird ueber die Methode
 * <code>setValue</code> gespeichert und kann vom Aufrufer ueber die Methode <code>getValue</code> abgefragt werden.
 *
 *
 */
public class VPNUebernahmeDialog extends AbstractServiceOptionDialog {

    private List<BVPNAuftragView> vpns = null;

    private AKJTable tbVPNs = null;
    private VPNTableModel tbMdlVPNs = null;

    /**
     * Konstruktor mit Angabe der VPN-Auftraege, die zur Auswahl stehen.
     *
     * @param vpns
     */
    public VPNUebernahmeDialog(List<BVPNAuftragView> vpns) {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/VPNUebernahmeDialog.xml");
        this.vpns = vpns;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("VPN-Auftrag übernehmen");
        setIconURL("de/augustakom/hurrican/gui/images/vpn.gif");
        configureButton(CMD_SAVE, "Übernehmen", "Übernimmt den selektierten VPN-Auftrag", true, true);

        tbMdlVPNs = new VPNTableModel();
        tbMdlVPNs.setData(vpns);
        tbVPNs = new AKJTable(tbMdlVPNs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVPNs.fitTable(new int[] { 80, 80, 120, 120 });
        AKJScrollPane spVPNs = new AKJScrollPane(tbVPNs);
        spVPNs.setPreferredSize(new Dimension(425, 200));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(spVPNs, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        manageGUI(new AKManageableComponent[] { getButton(CMD_SAVE) });
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        int selection = tbVPNs.getSelectedRow();
        Object o = tbMdlVPNs.getDataAtRow(selection);
        if (o instanceof BVPNAuftragView) {
            prepare4Close();
            setValue(o);
        }
        else {
            MessageHelper.showInfoDialog(this,
                    "Bitte selektieren Sie den VPN-Auftrag, der nach Hurrican übernommen werden soll.", null, true);
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

    /* TableModel fuer die VPN-Auftraege aus dem Billing-System. */
    static class VPNTableModel extends AKTableModel<BVPNAuftragView> {
        private static final int COL_VPN_NO = 0;
        private static final int COL_KUNDE__NO = 1;
        private static final int COL_NAME = 2;
        private static final int COL_VORNAME = 3;

        private static final int COL_COUNT = 4;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_VPN_NO:
                    return "VPN-Nr";
                case COL_KUNDE__NO:
                    return "Kunde__No";
                case COL_NAME:
                    return "Name";
                case COL_VORNAME:
                    return "Vorname";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof BVPNAuftragView) {
                BVPNAuftragView vpn = (BVPNAuftragView) o;
                switch (column) {
                    case COL_VPN_NO:
                        return vpn.getAuftragNoOrig();
                    case COL_KUNDE__NO:
                        return vpn.getKundeNo();
                    case COL_NAME:
                        return vpn.getKundeName();
                    case COL_VORNAME:
                        return vpn.getKundeVorname();
                    default:
                        return "";
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_VPN_NO:
                    return Long.class;
                case COL_KUNDE__NO:
                    return Long.class;
                default:
                    return String.class;
            }
        }
    }
}


