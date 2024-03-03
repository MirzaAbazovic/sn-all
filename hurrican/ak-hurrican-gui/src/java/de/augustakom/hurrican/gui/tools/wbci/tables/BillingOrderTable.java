/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.gui.tools.wbci.TaifunOrderIdSelectDialog;
import de.mnet.wbci.model.OrderMatchVO;

/**
 * Table for the {@link TaifunOrderIdSelectDialog}, to
 */
public class BillingOrderTable extends AbstractWbciTable {

    private static final long serialVersionUID = -7792777382970999487L;

    public BillingOrderTable(BillingOrderTableModel tableModel) {
        super(tableModel, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION, false);
        fitTable(tableModel.getColumnDimensions());
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);

        OrderMatchVO orderMatchVO = getModel().getDataAtRow(row).getSecond();
        if (orderMatchVO != null) {
            if (column == BillingOrderTableModel.COL_DN) {
                component.setBackground(getColorForOrderMatch(orderMatchVO, OrderMatchVO.MatchViolation.DN,
                        OrderMatchVO.BasicSearch.DN));
            }
            else if (column == BillingOrderTableModel.COL_LOCATION) {
                component.setBackground(getColorForOrderMatch(orderMatchVO, OrderMatchVO.MatchViolation.LOCATION,
                        OrderMatchVO.BasicSearch.LOCATION));
            }
            else if (column == BillingOrderTableModel.COL_NAME) {
                component.setBackground(getColorForOrderMatch(orderMatchVO, OrderMatchVO.MatchViolation.NAME,
                        OrderMatchVO.BasicSearch.DN, OrderMatchVO.BasicSearch.LOCATION));
            }
        }
        return component;
    }

    /**
     * returns the color for the machting violation and responsible basic searches *
     */
    protected Color getColorForOrderMatch(OrderMatchVO orderMatchVO, OrderMatchVO.MatchViolation matchingViolation,
            OrderMatchVO.BasicSearch... matchingSearches) {
        Color result = BG_COLOR_GREY;
        if (Arrays.asList(matchingSearches).contains(orderMatchVO.getBasicSearch())) {
            result = BG_COLOR_GREEN;
        }
        if (CollectionUtils.isNotEmpty(orderMatchVO.getMatchViolations())
                && orderMatchVO.getMatchViolations().contains(matchingViolation)) {
            result = BG_COLOR_RED;
        }
        return result;
    }

    @Override
    public BillingOrderTableModel getModel() {
        return (BillingOrderTableModel) super.getModel();
    }

}
