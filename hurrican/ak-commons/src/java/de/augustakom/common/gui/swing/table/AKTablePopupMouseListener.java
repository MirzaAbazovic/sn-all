/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 15:52:16
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKActionGroup;
import de.augustakom.common.gui.swing.AKJPopupMenu;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.tools.lang.BooleanTools;


/**
 * MouseListener fuer eine Tabelle. <br> Der Mouse-Listener reagiert auf einen Popup-Trigger (Windows: rechte Maustaste)
 * und zeigt ein Popup-Menu an. <br> <br> In dem Listener sind bereits Actions implementiert, um den Inhalt einer
 * kompletten Zeile und den Inhalt einer einzelnen Tabelle zu kopieren. <br> Zusaetzlich koennen dem Listener eigene
 * Actions uebergeben werden, die ebenfalls in dem Popup-Menu angezeigt werden.
 *
 *
 */
public class AKTablePopupMouseListener extends MouseAdapter {

    private boolean showCopyActions = true;
    private boolean changeSelection = true;
    private List actions = null;

    private int row = -1;
    private int column = -1;

    private MouseEvent mouseEvent = null;

    /**
     * Konstruktor
     */
    public AKTablePopupMouseListener() {
        super();
        actions = new ArrayList();
    }

    /**
     * Ueber das Flag kann definiert werden, ob die Tabellen-Selektion bei einem Popup-Event geaendert (true=Standard)
     * oder beibehalten werden soll.
     *
     * @param changeSelection
     */
    public void setChangeSelectionOnPopup(boolean changeSelection) {
        this.changeSelection = changeSelection;
    }

    /**
     * Angabe, ob die Kopier-Actions (Zelle und Zeile kopieren) im Popup-Menu angezeigt werden sollen.
     *
     * @param showCopyActions
     */
    public void setShowCopyActions(boolean showCopyActions) {
        this.showCopyActions = showCopyActions;
    }

    /**
     * Fuegt dem Listener bzw. dem PopupMenu eine Gruppe mit Aktionen, die in einem Unter-Popup-Menu dargestellt
     * werden.
     *
     * @param actionGroup Gruppe von Aktionen
     */
    public void addPopupActionGroup(AKActionGroup actionGroup) {
        actions.add(actionGroup);
    }

    /**
     * Fuegt dem Listener bzw. dem PopupMenu eine Action hinzu, die angezeigt werden soll. <br> Das selektierte Objekt
     * der Tabelle wird der Action ueber den Key <code>AKAbstractAction.OBJECT_4_ACTION</code> uebergeben.
     *
     * @param action Action fuer das Popup-Menu
     */
    public void addAction(AKAbstractAction action) {
        actions.add(action);
    }

    /**
     * Fuegt dem PopupMenu an die aktuelle Stelle einen Separator hinzu.
     */
    public void addSeparator() {
        actions.add(new PopupSeparator());
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            changeSelection(e);
            showPopup(e);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            changeSelection(e);
            showPopup(e);
        }
    }

    /**
     * Zeigt das Popup-Menu an.
     *
     * @param e MouseEvent.
     */
    protected void showPopup(MouseEvent e) {
        AKJTable table = null;
        if (e.getSource() instanceof AKJTable) {
            table = (AKJTable) e.getSource();
        }
        else if (e.getSource() instanceof JTableHeader) {
            table = (AKJTable) ((JTableHeader) e.getSource()).getTable();
        }
        else {
            return;
        }

        AKJPopupMenu popup = new AKJPopupMenu();
        if (showCopyActions) {
            popup.add(new CopyCellAction(table));
            popup.add(new CopyRowAction(table));
            popup.add(new CopyTableAction(table));

            AKPrintTableAction printAction = new AKPrintTableAction(table, null);
            printAction.setPrintMode(JTable.PrintMode.FIT_WIDTH);
            popup.add(printAction);
        }

        // Filter hinzufügen
        if (table.isTableFilterable()) {
            if (showCopyActions) {
                popup.addSeparator();
            }

            popup.add(new FilterTableAction(table));
            popup.add(new FilterTableSelectionOnlyAction(table));
            popup.add(new FilterTableExcludeSelectionAction(table));
            popup.add(new CleanFilterTableAction(table));
        }

        // weitere Actions hinzufuegen und selektiertes Objekt der Action uebergeben
        if (actions != null) {
            if ((showCopyActions || table.isTableFilterable()) && (!actions.isEmpty())) {
                popup.addSeparator();
            }
            addPopupActions(e, table, popup, actions);
        }

        if (popup.getComponentCount() > 0) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void addPopupActions(MouseEvent e, AKJTable table, JPopupMenu popup, List actions) {
        for (int i = 0; i < actions.size(); i++) {
            Object o = actions.get(i);
            if (o instanceof AKAbstractAction) {
                AKAbstractAction toAdd = (AKAbstractAction) o;
                boolean addSep = BooleanTools.nullToFalse((Boolean) toAdd.getValue(AKAbstractAction.ADD_SEPARATOR));
                if (addSep) {
                    popup.addSeparator();
                }

                toAdd.putValue(AKAbstractAction.OBJECT_4_ACTION, getSelectedObject(table));
                toAdd.putValue(AKAbstractAction.ACTION_SOURCE, e);
                JMenuItem mi = popup.add(toAdd);
                if (mi != null) {
                    AdministrationMouseListener adminML = new AdministrationMouseListener();
                    mi.addMouseListener(adminML);
                    mi.addMenuKeyListener(adminML);
                }
            }
            else if (o instanceof PopupSeparator || o instanceof AKActionGroup.ActionSeparator) {
                popup.addSeparator();
            }
            else if (o instanceof AKActionGroup) {
                final AKActionGroup actionGroup = (AKActionGroup) o;
                final JMenu menu = new JMenu(actionGroup.getLabel());
                addPopupActions(e, table, menu.getPopupMenu(), actionGroup.getActions());
                popup.add(menu);
            }
        }
    }

    /**
     * Aendert die Selektion der Tabelle.
     */
    private void changeSelection(MouseEvent e) {
        mouseEvent = e;
        if (changeSelection) {
            AKJTable table = null;

            if (e.getSource() instanceof JTableHeader) {
                table = (AKJTable) ((JTableHeader) e.getSource()).getTable();
            }
            else {
                table = (AKJTable) e.getSource();
            }

            if (table != null) {
                // ausgewaehltes Objekt markieren
                Point point = new Point(e.getX(), e.getY());
                row = table.rowAtPoint(point);
                column = table.columnAtPoint(point);
                table.changeSelection(row, column, false, false);
            }
        }
    }

    /**
     * Gibt das Objekt der selektierten Zeile zurueck.
     */
    private Object getSelectedObject(AKJTable table) {
        AKMutableTableModel tm = (AKMutableTableModel) table.getModel();

        return tm.getDataAtRow(table.getSelectedRow());
    }


    /**
     * Action, um die aktuelle Zelle in die Zwischenablage zu kopieren
     */
    class CopyCellAction extends AKAbstractAction {

        private static final long serialVersionUID = -7852284711105331899L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public CopyCellAction(AKJTable table) {
            this.table = table;
            setName("Zelle kopieren");
            setTooltip("Kopiert den Inhalt der aktuellen Zelle in die Zwischenablage");
            setActionCommand("copy.column");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (table != null) {
                int selRow = -1;
                int selCol = -1;
                if (!changeSelection && (mouseEvent != null)) {
                    Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
                    selRow = table.rowAtPoint(point);
                    selCol = table.columnAtPoint(point);
                }
                else {
                    selRow = row;
                    selCol = column;
                }

                String toCopy = null;
                if ((selRow >= 0) && (selCol >= 0)) {
                    Object value = table.getValueAt(selRow, selCol);
                    toCopy = (value != null) ? value.toString() : "";
                }

                Clipboard clip = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection content = new StringSelection(toCopy);
                clip.setContents(content, content);
            }
        }
    }


    /**
     * Action, um die aktuelle Zeile in die Zwischenablage zu kopieren
     */
    class CopyRowAction extends AKAbstractAction {
        private static final long serialVersionUID = 4210491513575159670L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public CopyRowAction(AKJTable table) {
            this.table = table;
            if ((table != null) && (table.getSelectedRowCount() > 1)) {
                setName("Zeilen kopieren");
            }
            else {
                setName("Zeile kopieren");
            }
            setTooltip("Kopiert den Inhalt der aktuellen Zeile in die Zwischenablage");
            setActionCommand("copy.row");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (table != null) {
                if (!changeSelection && (mouseEvent != null)) {
                    Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
                    row = table.rowAtPoint(point);
                    column = table.columnAtPoint(point);
                    if (table.getSelectedRowCount() <= 0) {
                        table.changeSelection(row, column, false, false);
                    }
                    else {
                        table.changeSelection(row, column, false, true);
                    }
                }

                table.copyToClipboard(true);
            }
        }
    }


    /**
     * Action, um den Inhalt der gesamten Tabelle in die Zwischenablage zu kopieren
     */
    static class CopyTableAction extends AKAbstractAction {
        private static final long serialVersionUID = 265921628856040554L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public CopyTableAction(AKJTable table) {
            this.table = table;
            setName("Tabelle kopieren");
            setTooltip("Kopiert den Inhalt der gesamten Tabelle in die Zwischenablage");
            setActionCommand("copy.table");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if (table != null) {
                table.copyToClipboard(false);
            }
        }
    }


    /**
     * Action, um den Inhalt der gesamten Tabelle zu filtern
     */
    static class FilterTableAction extends AKAbstractAction {
        private static final long serialVersionUID = -8168532642423508369L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public FilterTableAction(AKJTable table) {
            this.table = table;
            setName("Datensätze filtern");
            setTooltip("Filtert die angezeigten Datensätze");
            setActionCommand("filter.table");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if ((table != null) && (table.isTableFilterable())) {
                AKTableFilterDialog filterDialog = new AKTableFilterDialog(table);
                DialogHelper.showDialog(null, filterDialog, true, true);
            }
        }
    }


    /**
     * Action, um einen Filter rückgängig zu machen
     */
    class FilterTableSelectionOnlyAction extends AKAbstractAction {
        private static final long serialVersionUID = -7698644982650409807L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public FilterTableSelectionOnlyAction(AKJTable table) {
            this.table = table;
            setName("Auswahlbasierter Filter");
            setTooltip("Filter alle Werte heraus, die nicht dem selektierten Wert entsprechen");
            setActionCommand("filter.table.selection.only");
            setIcon("de/augustakom/hurrican/gui/images/filter_auswahlbasiert.gif");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if ((table != null) && (table.isTableFilterable())) {
                AKFilterTableModel model = (AKFilterTableModel) ((AKTableSorter) table.getModel()).getModel();
                int selRow;
                int selCol;
                if (!changeSelection && (mouseEvent != null)) {
                    Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
                    selRow = table.rowAtPoint(point);
                    selCol = table.columnAtPoint(point);
                }
                else {
                    selRow = row;
                    selCol = column;
                }
                if ((selRow >= 0) && (selCol >= 0)) {
                    Object value = table.getValueAt(selRow, selCol);
                    model.addFilter(new FilterOperator(FilterOperators.EQ, value, table.convertColumnIndexToModel(selCol)));
                }
            }
        }
    }


    /**
     * Action, um einen Filter rückgängig zu machen
     */
    class FilterTableExcludeSelectionAction extends AKAbstractAction {
        private static final long serialVersionUID = 2421022186000354817L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public FilterTableExcludeSelectionAction(AKJTable table) {
            this.table = table;
            setName("Auswahlausschließender Filter");
            setTooltip("Filter alle Werte heraus, die dem selektierten Wert entsprechen");
            setActionCommand("filter.table.exclude.selection");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if ((table != null) && (table.isTableFilterable())) {
                AKFilterTableModel model = (AKFilterTableModel) ((AKTableSorter) table.getModel()).getModel();
                int selRow;
                int selCol;
                if (!changeSelection && (mouseEvent != null)) {
                    Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
                    selRow = table.rowAtPoint(point);
                    selCol = table.columnAtPoint(point);
                }
                else {
                    selRow = row;
                    selCol = column;
                }
                if ((selRow >= 0) && (selCol >= 0)) {
                    Object value = table.getValueAt(selRow, selCol);
                    model.addFilter(new FilterOperator(FilterOperators.NOT_EQ, value, table.convertColumnIndexToModel(selCol)));
                }
            }
        }
    }


    /**
     * Action, um einen Filter rückgängig zu machen
     */
    static class CleanFilterTableAction extends AKAbstractAction {
        private static final long serialVersionUID = -1733198006122354734L;
        private AKJTable table = null;

        /**
         * Konstruktor
         */
        public CleanFilterTableAction(AKJTable table) {
            this.table = table;
            setName("Filter entfernen");
            setTooltip("Entfernt den Filter auf dieser Tabelle");
            setActionCommand("cleanfilter.table");
            setIcon("de/augustakom/hurrican/gui/images/filter_entfernen.gif");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if ((table != null) && (table.isTableFilterable())) {
                AKFilterTableModel model = (AKFilterTableModel) ((AKTableSorter) table.getModel()).getModel();
                model.removeFilter(null);
            }
        }
    }


    /**
     * Hilfsklasse, um einen Separator zu erkennen.
     */
    static class PopupSeparator {
    }
}
