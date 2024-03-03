/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOHelper;

/**
 * TableModel zur Darstellung der MeldungsCodes aus einem DecisionVO Objekt
 */
public class MeldungsCodesTableModel extends AKTableModel<DecisionVO> {

    private static final long serialVersionUID = -813862405777761565L;
    private static final int COL_CODE = 0;
    private static final int COL_TEXT = 1;
    private static final int COL_DETAIL = 2;
    private static final int COL_COUNT = 3;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_CODE:
                return "MeldungsCode";
            case COL_TEXT:
                return "Beschreibung";
            case COL_DETAIL:
                return "Details";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        DecisionVO decisionVO = getDataAtRow(row);
        if (decisionVO != null) {
            switch (column) {
                case COL_CODE:
                    return (decisionVO.getFinalMeldungsCode() != null) ? decisionVO.getFinalMeldungsCode().name() : null;
                case COL_TEXT:
                    return (decisionVO.getFinalMeldungsCode() != null) ? decisionVO.getFinalMeldungsCode().getStandardText() : null;
                case COL_DETAIL:
                    return decisionVO.getFinalValue();
                default:
                    break;
            }
        }
        return super.getValueAt(row, column);
    }

    @Override
    public void setData(Collection<DecisionVO> data) {
        super.setData(DecisionVOHelper.consolidateZwaAndNatMeldungsCodes(data));
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

}
