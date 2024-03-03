package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.auftrag.shared.VPNVertragViewTableModel;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;

public class VPNVertragTable extends AKJTable {

    private static final Color BG_COLOR_CANCELLED = new Color(127, 127, 127);

    public VPNVertragTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        TableModel tableModel = getModel();
        if (tableModel instanceof AKTableSorter) {
            tableModel = ((AKTableSorter) tableModel).getModel();
        }
        if (tableModel instanceof VPNVertragViewTableModel) {
            VPNVertragViewTableModel model = (VPNVertragViewTableModel) tableModel;
            VPNVertragView view = model.getDataAtRow(row);
            if (view.isCancelled()) {
                comp.setForeground(BG_COLOR_CANCELLED);
            }
        }
        return comp;
    }

}
