/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static de.augustakom.hurrican.gui.tools.wbci.tables.RufnummernPortierungTableModel.*;
import static de.mnet.wbci.model.RufnummernportierungVO.StatusInfo.*;

import java.awt.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.mnet.wbci.model.RufnummernportierungVO;

/**
 * Table zur Darstellung der angefragten / bestaetigten / zurueck-gemeldeten Rufnummern
 */
public class RufnummernPortierungTable extends AbstractWbciTable {

    private static final long serialVersionUID = 1520969991605201546L;

    public RufnummernPortierungTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if ((getModel() instanceof AKMutableTableModel) && !isRowSelected(row)) {
            Object data = ((AKMutableTableModel<?>) getModel()).getDataAtRow(row);
            if (data instanceof RufnummernportierungVO) {
                if (COL_STATUS == column) {
                    setBackgroundColorStatus((RufnummernportierungVO) data, comp);
                }
                else if (COL_PKI_AUF == column) {
                    setBackgroundColorPkiAuf((RufnummernportierungVO) data, comp);
                }
            }
        }
        return comp;
    }

    protected void setBackgroundColorStatus(RufnummernportierungVO rufnummernportierungVO, Component comp) {
        if (NEU_AUS_RUEM_VA.equals(rufnummernportierungVO.getStatusInfo())
                || NICHT_IN_RUEM_VA.equals(rufnummernportierungVO.getStatusInfo())) {
            comp.setBackground(BG_COLOR_ORANGE);
        }
    }

    protected void setBackgroundColorPkiAuf(RufnummernportierungVO rufnummernportierungVO, Component comp) {
        if (RufnummernportierungVO.PkiAufMatch.NO_MATCH.equals(rufnummernportierungVO.getPkiAufMatch())) {
            comp.setBackground(BG_COLOR_ORANGE);
        }
    }

}
