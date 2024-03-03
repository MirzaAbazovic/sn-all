/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.time.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.mnet.wbci.model.AutomationTask;

/**
 * Table model for todolist objects. User can mark todos as done in table via checkbox.
 *
 *
 */
public class AutomationTaskTableModel extends AKTableModel<AutomationTask> {

    private static final long serialVersionUID = 3721093439364629780L;

    public static final int COL_DONE = 0;
    public static final int COL_NAME = 1;
    public static final int COL_STATUS = 2;
    public static final int COL_COMPLETED_AT = 3;
    public static final int COL_USERNAME = 4;
    public static final int COL_CREATED_AT = 5;
    public static final int COL_EXECUTION_LOG = 6;

    private static final int COLUMN_COUNT = 7;

    private static final Logger LOGGER = Logger.getLogger(AutomationTaskTableModel.class);

    private boolean editable = true;

    public AutomationTaskTableModel(boolean editable) {
        this.editable = editable;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_DONE:
                return "Erledigt";
            case COL_NAME:
                return "Automatisierungsschritt";
            case COL_STATUS:
                return "Status";
            case COL_COMPLETED_AT:
                return "Erledigt am";
            case COL_USERNAME:
                return "Ausgeführt von";
            case COL_CREATED_AT:
                return "Erzeugt am";
            case COL_EXECUTION_LOG:
                return "Protokoll";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        AutomationTask automationTask = getDataAtRow(row);
        if (automationTask != null) {
            switch (column) {
                case COL_DONE:
                    return automationTask.isDone();
                case COL_NAME:
                    return automationTask.getName();
                case COL_STATUS:
                    return automationTask.getStatus();
                case COL_COMPLETED_AT:
                    return automationTask.getCompletedAt();
                case COL_USERNAME:
                    return automationTask.getUserName();
                case COL_CREATED_AT:
                    return automationTask.getCreatedAt();
                case COL_EXECUTION_LOG:
                    return automationTask.getExecutionLog();
                default:
                    break;
            }
        }
        return super.getValueAt(row, column);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof AutomationTask && Boolean.TRUE.equals(aValue)) {
            AutomationTask automationTask = (AutomationTask) o;
            automationTask.complete();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (editable && column == COL_DONE) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_DONE:
                return Boolean.class;
            case COL_NAME:
                return AutomationTask.TaskName.class;
            case COL_STATUS:
                return AutomationTask.AutomationStatus.class;
            case COL_COMPLETED_AT:
                return LocalDateTime.class;
            case COL_CREATED_AT:
                return LocalDateTime.class;
            default:
                return String.class;
        }
    }

}
