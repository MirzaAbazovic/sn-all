/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.table.AKFilterTableModel;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTablePopupMouseListener;
import de.augustakom.common.gui.swing.table.AKTableSearchDialog;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaDataWithSize;
import de.augustakom.common.tools.lang.ClipboardTools;


/**
 * AK-Implementierung einer JTable. <br> Die Table besitzt einen Key-Listener, um ueber die Tasten 'HOME' und 'END' an
 * den Anfang bzw. das Ende der Tabelle zu gelangen. <br> Ausserdem bietet der Listener die Moeglichkeit, die
 * selektierten Zeilen/Zellen in die Zwischenablage zu kopieren (ueber Strg+C). <br><br> Die Implementierung ueberlagert
 * die Methode <code>prepareRenderer</code>, um die Hintergrundfarbe jeder zweiten Zeile hervorzuheben. Die
 * Hintergrundfarbe fuer die geraden bzw. ungeraden Zeilen koennen ueber die Methoden
 * <code>setEvenRowBackgroundColor</code> bzw. <code>setOddRowBackgroundColor</code> definiert werden.
 *
 *
 * @see javax.swing.JTable
 */
public class AKJTable extends JTable implements AKManageableComponent {

    private static final Logger LOGGER = Logger.getLogger(AKJTable.class);
    private static final long serialVersionUID = 9031118008536664637L;

    /* Hintergrundfarbe fuer gerade Zeilen. Default: (sehr) helles blau */
    private static final Color EVEN_ROW_BG_COLOR = new Color(225, 240, 255);
    /* Hintergrundfarbe fuer ungerade Zeilen. Default: weiss */
    private static final Color ODD_ROW_BG_COLOR = new Color(255, 255, 255);
    /* Vordergrundfarbe fuer auswaehlbare Zeilen. Default: schwarz */
    private static final Color ENABLED_ROW_FG_COLOR = new Color(0, 0, 0);
    /* Vordergrundfarbe fuer deaktivierte Zeilen. Default: rot */
    private static final Color DISABLED_ROW_FG_COLOR = new Color(255, 0, 0);

    private AKTablePopupMouseListener popupMouseListener = null;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    protected boolean searchEnabled = true;
    private boolean filterEnabled = true;

    /**
     * Erzeugt eine neue JTable.
     */
    public AKJTable() {
        super();
        init();
    }

    /**
     * Erzeugt eine neue JTable mit Angabe der darzustellnden Zeilen und Spalten.
     *
     * @param numRows
     * @param numColumns
     */
    public AKJTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }

    /**
     * Erzeugt eine neue JTable mit einem TableModel.
     *
     * @param dm
     */
    public AKJTable(TableModel dm) {
        super(dm);
        init();
    }

    /**
     * Konstruktor mit Angabe des TableModels, dem AutoResizeMode und dem SelectionMode
     *
     * @param dm             zu verwendendes TableModel
     * @param autoResizeMode Angabe des AutoResizeModes, z.B. AUTO_RESIZE_OFF
     * @param selectionMode  Angabe des SelectionModes, z.B. ListSelectionModel.SINGLE_SELECTION
     */
    public AKJTable(TableModel dm, int autoResizeMode, int selectionMode) {
        super(dm);
        init();
        setAutoResizeMode(autoResizeMode);
        setSelectionMode(selectionMode);
    }

    /**
     * Konstruktor mit Angabe des TableModels, dem AutoResizeMode und dem SelectionMode
     *
     * @param dm             zu verwendendes TableModel
     * @param autoResizeMode Angabe des AutoResizeModes, z.B. AUTO_RESIZE_OFF
     * @param selectionMode  Angabe des SelectionModes, z.B. ListSelectionModel.SINGLE_SELECTION
     * @param filterEnabled  Flag, ob auf der Tabelle ein Filter zugelassen ist
     */
    public AKJTable(TableModel dm, int autoResizeMode, int selectionMode, boolean filterEnabled) {
        super(dm);
        init();
        setAutoResizeMode(autoResizeMode);
        setSelectionMode(selectionMode);
        setFilterEnabled(filterEnabled);
    }

    /**
     * Erzeugt eine neue Table. Die Daten werden aus dem zweidimensionalen Array <code>rowData</code> gelesen, die
     * Spaltennamen aus <code>columnNames</code>.
     *
     * @param rowData
     * @param columnNames
     */
    public AKJTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }

    /**
     * Erzeugt eine neue Table. Die Daten werden aus dem Vector <code>rowData</code> gelesen, die Spaltennamen aus dem
     * Vector <code>columnNames</code>.
     *
     * @param rowData
     * @param columnNames
     */
    public AKJTable(Vector<?> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
        init();
    }

    /**
     * Erzeugt eine neue Table. Die anzuzeigenden Daten werden aus dem TableModel <code>dm</code> gelesen. Die
     * Spaltennamen werden ueber das TableColumnModel <code>cm</code> gelesen.
     *
     * @param dm
     * @param cm
     */
    public AKJTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }

    /**
     * Erzeugt eine neue Table. Die anzuzeigenden Daten werden aus dem TableModel <code>dm</code> gelesen. Die
     * Spaltennamen werden ueber das TableColumnModel <code>cm</code> gelesen. <br> Ueber das ListSelectionModel
     * <code>sm</code> wird die Selektions-Eigenschaft festgelegt.
     *
     * @param dm
     * @param cm
     * @param sm
     */
    public AKJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        init();
    }

    /**
     * Setzt den Cursor der Tabelle auf 'Wait'
     */
    public void setWaitCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Setzt den Cursor der Tabelle auf 'Default'
     */
    public void setDefaultCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    @Override
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    @Override
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = true;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    @Override
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    @Override
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    /* Initialisiert das Table-Objekt */
    private void init() {
        addKeyListener(new DefaultTableKeyListener());

        popupMouseListener = new AKTablePopupMouseListener();
        addMouseListener(popupMouseListener);
    }

    /**
     * Entfernt den Popup-MouseListener von der Tabelle.
     */
    public void removePopupMouseListener() {
        removeMouseListener(popupMouseListener);
    }

    /**
     * Gibt den Popup-MouseListener der Tabelle zurueck. <br> Diesem Listener koennen weitere Actions hinzugefuegt
     * werden, die dann in einem Popup-Menu dargestellt werden.
     *
     * @return Instanz von AKTablePopupMouseListener
     */
    public AKTablePopupMouseListener getPopupMouseListener() {
        return popupMouseListener;
    }

    /**
     * Uebergibt dem Popup-Menu der Tabelle eine zusaetzliche Action.
     *
     * @param action
     */
    public void addPopupAction(AKAbstractAction action) {
        getPopupMouseListener().addAction(action);
    }

    /**
     * Uebergibt dem Popup-Menu der Tabelle ein zusätzliches Popup-Menu, das weitere Aktionen enthalten kann.
     *
     * @param actionGroup
     */
    public void addPopupGroup(AKActionGroup actionGroup) {
        getPopupMouseListener().addPopupActionGroup(actionGroup);
    }

    /**
     * Uebergibt dem Popup-Menu der Tabelle einen zus. Separator.
     */
    public void addPopupSeparator() {
        getPopupMouseListener().addSeparator();
    }

    /**
     * Steuert den Popup-MouseListener. <br> Ueber das Flag kann definiert werden, ob bei der Betaetigung des
     * Popup-Triggers die Tabellen-Selektion auf die aktuelle Zeile beschraenkt (true) werden soll, oder ob die aktuelle
     * Selektion erhalten bleiben soll.
     *
     * @param changeSelection
     */
    public void setPopupChangeSelection(boolean changeSelection) {
        getPopupMouseListener().setChangeSelectionOnPopup(changeSelection);
    }

    @Override
    public void setModel(TableModel dataModel) {
        TableModel oldModel = this.getModel();
        if (oldModel instanceof AKTableSorter) {
            ((AKTableSorter<?>) oldModel).clearMouseListenerToHeaderInTable();
        }
        super.setModel(dataModel);
    }

    /**
     * Fuegt der Tabelle einen TableSorter hinzu. <br> Das vorhandene TableModel wird einem Sorter uebergeben und der
     * Sorter wird der Table als Model uebergeben. <br> Ausserdem wird dem TableHeader ein MouseListener zugeordnet.
     */
    public void attachSorter() {
        attachSorter(false);
    }

    /**
     * see attachSorter() Ueber den Parameter {@code holdSelection} wird definiert, ob die Table-Selection nach einer
     * Sortierung bestehen bleiben soll ({@code true}) oder nicht ({@code false}). <br> Dies ist allerdings nur dann
     * möglich, wenn nur eine einzelne Zeile selektiert war!
     *
     * @param holdSelection
     */
    public void attachSorter(boolean holdSelection) {
        TableModel oldModel = this.getModel();
        @SuppressWarnings("rawtypes")
        AKTableSorter<?> sorter = new AKTableSorter(oldModel, holdSelection);
        this.setModel(sorter);
        sorter.addMouseListenerToHeaderInTable(this);
    }

    /**
     * Fuegt der Tabelle einen MouseListener und einen KeyListener hinzu. <br> Die Listener benachrichtigen wiederum den
     * <code>tableOwner</code>, wenn eine Zeile markiert wird.
     *
     * @param tableOwner
     */
    public void addTableListener(AKTableOwner tableOwner) {
        AKTableListener tl = new AKTableListener(tableOwner);
        this.addMouseListener(tl);
        this.addKeyListener(tl);
    }

    /**
     * Scrollt zu der angegebenen Zeile.
     *
     * @param row
     */
    public void scrollToRow(int row) {
        if (!(getParent() instanceof JViewport)) { return; }
        JViewport viewport = (JViewport) getParent();
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = getCellRect(row, 0, true);
        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);

        notifySelectionListener();
    }

    /**
     * Scrollt zu der angegebenen Zeile und Spalte.
     *
     * @param row
     * @param col
     */
    public void scrollToRowAndCol(int row, int col) {
        if (!(getParent() instanceof JViewport)) { return; }
        JViewport viewport = (JViewport) getParent();
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = getCellRect(row, col, true);
        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);

        notifySelectionListener();
    }

    /**
     * Scrollt zur letzten Zeile der Tabelle und markiert diese.
     *
     *
     */
    public void selectAndScrollToLastRow() {
        int lastRow = getRowCount() - 1;
        selectAndScrollToRow(lastRow);
    }

    public void selectAndScrollToRow(int row) {
        setRowSelectionInterval(row, row);
        scrollToRow(row);
    }

    /**
     * Benachrichtigt die Listener vom Typ AKTableListener ueber die Selektionsaenderung.
     */
    protected void notifySelectionListener() {
        if (getModel() instanceof AKMutableTableModel<?>) {
            KeyListener[] kls = getKeyListeners();
            MouseListener[] mls = getMouseListeners();
            List<EventListener> listeners = new ArrayList<EventListener>();
            CollectionUtils.addAll(listeners, kls);
            CollectionUtils.addAll(listeners, mls);
            for (Object listener : listeners) {
                if (listener instanceof AKTableListener) {
                    ((AKTableListener) listener).selectionChanged(this);
                }
            }
        }
    }

    /**
     * Entfernt alle KeyListener vom Typ AKJTable.DefaultTableKeyListener von der Tabelle. <br>
     */
    public void removeDefaultTableKeyListener() {
        KeyListener[] listeners = getKeyListeners();
        if (listeners != null) {
            List<EventListener> toRemove = new ArrayList<EventListener>();
            for (KeyListener listener : listeners) {
                if (listener instanceof DefaultTableKeyListener) {
                    toRemove.add(listener);
                }
            }

            for (int i = 0; i < toRemove.size(); i++) {
                removeKeyListener((KeyListener) toRemove.get(i));
            }
        }
    }

    /**
     * Kopiert den Inhalt aller selektierten Zellen in die Zwischenablage.
     *
     * @param selectionOnly Flag, ob nur die selektierten Zeilen (true) oder die gesamte Tabelle (false) in die
     *                      Zwischenablage kopiert werden soll (Bug-ID 11448).
     */
    public void copyToClipboard(boolean selectionOnly) {
        String toCopy = (selectionOnly) ? copySelection() : copyTable();
        ClipboardTools.copy2Clipboard(toCopy);
    }

    /**
     * Kopiert den gesamten Tabelleninhalt in die Zwischenablage.
     */
    public void copyTableToClipboard() {
    }

    /**
     * Kopiert die Header-Namen in den StringBuilder <code>memory</code>. <br> Die einzelnen Header-Namen werden durch
     * das Zeichen <code>separator</code> voneinander getrennt.
     *
     * @param memory
     * @param separator
     */
    protected void copyColumnHeaders(StringBuilder memory, String separator) {
        int columnCount = getColumnCount();
        for (int c = 0; c < columnCount; c++) {
            if (c > 0) {
                memory.append(separator);
            }
            memory.append(getColumnName(c));
        }
        memory.append(System.getProperty("line.separator"));
    }

    /**
     * Kopiert den Inhalt der Zeile <code>row</code> in den StringBuilder <code>memory</code>. Die einzelnen Spalten
     * werden durch <code>separator</code> voneinander getrennt.
     *
     * @param row
     * @param memory
     * @param separator
     */
    protected void copyRow(int row, StringBuilder memory, String separator) {
        int columnCount = getColumnCount();
        for (int k = 0; k < columnCount; k++) {
            if (k > 0) {
                memory.append(separator);
            }

            Component comp = getCellRenderer(row, k).getTableCellRendererComponent(
                    this, getValueAt(row, k), false, false, row, k);
            try {
                // Es wird zuerst versucht, den wirklichen Text der Zelle zu ermitteln.
                // Nur bei einem Fehler wird der String-Wert des Objekts verwendet.
                Object property = PropertyUtils.getProperty(comp, "text");
                if (property != null) {
                    memory.append(property.toString());
                }
                else {
                    memory.append("");
                }
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage());
                Object value = getValueAt(row, k);
                if (value != null) {
                    memory.append(value.toString());
                }
                else {
                    memory.append("");
                }
            }
        }

        memory.append(System.getProperty("line.separator"));
    }

    /*
     * Erzeugt as dem gesamten Inhalt der Tabelle (+Header) einen String
     * und gibt diesen zurueck.
     * Die einzelnen Spalten werden durch ein Tabulator-Zeichen getrennt.
     */
    private String copyTable() {
        StringBuilder toCopy = new StringBuilder();
        for (int i = 0; i < getRowCount(); i++) {
            if (i == 0) {
                copyColumnHeaders(toCopy, "\t");
            }

            copyRow(i, toCopy, "\t");
        }
        return toCopy.toString();
    }

    /*
     * Erzeugt aus den selektierten Zeilen/Spalten einen String. <br>
     * Die einzelnen Werte in einer Zeile werden durch ein
     * Tabulator-Zeichen voneinander getrennt. <br>
     * Die Zeilen werden duch einen Zeilenumbruch voneinander getrennt.
     * @return Aus den selektierten Zeilen/Spalten erzeugter String
     */
    private String copySelection() {
        StringBuilder toCopy = new StringBuilder();
        int[] selectedRows = getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            // Spaltennamen einmalig speichern
            if (i == 0) {
                copyColumnHeaders(toCopy, "\t");
            }

            // Werte der selektierten Zeilen/Spalten speichern
            copyRow(selectedRows[i], toCopy, "\t");
        }

        return toCopy.toString();
    }

    /**
     * Setzt die PreferredWidth der einzelnen Tabellen-Spalten. <br/>
     * Der Aufruf dieser Methode wirkt sich nur dann aus, wenn der AutoResize-Mode der Tabelle auf AUTO_RESIZE_OFF
     * steht. <br/>
     * Sofern die Tabelle zusaetzlich mit {@link AKJTable#attachSorter()} konfiguriert wird, sollte diese Methode
     * NACH attachSorter aufgerufen werden.
     *
     * @param columnWidth
     */
    public void fitTable(int[] columnWidth) {
        if (getColumnCount() >= columnWidth.length) {
            for (int i = 0; i < columnWidth.length; i++) {
                int width = columnWidth[i];
                TableColumn col = getColumnModel().getColumn(i);
                col.setPreferredWidth(width);
            }
        }
    }

    public void fitTable(List<ReflectionTableMetaDataWithSize> tabelModelMetaData) {
        int[] sizes = new int[tabelModelMetaData.size()];
        for (int i = 0; i < tabelModelMetaData.size(); i++) {
            sizes[i] = tabelModelMetaData.get(i).getColumnSize();
        }
        fitTable(sizes);
    }

    /**
     * Setzt die PreferredWidth der einzelnen Tabellen-Spalten. <br> Die Angabe der gewuenschten Breiten wird in Prozent
     * angegeben.
     *
     * @param tableWidth         Breite der Tabelle
     * @param columnWidthPercent Array mit den Prozent-Angaben fuer die Tabellen-Spalten.
     */
    public void fitTable(int tableWidth, double[] columnWidthPercent) {
        if (getColumnCount() >= columnWidthPercent.length) {
            for (int i = 0; i < columnWidthPercent.length; i++) {
                double percent = columnWidthPercent[i];
                double with4Col = (tableWidth / (double) 100) * percent;

                TableColumn col = getColumnModel().getColumn(i);
                col.setPreferredWidth((int) with4Col);
            }
        }
    }

    /**
     * Diese Methode wird von JTable immer aufgerufen. <br> In dieser Implementierung wird die Hintergrundfarbe jeder
     * zweiten Zeile geaendert.
     *
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isCellSelected(row, column)) {
            if ((row % 2) == 0) {
                c.setBackground(EVEN_ROW_BG_COLOR);
            }
            else {
                c.setBackground(ODD_ROW_BG_COLOR);
            }
            if (isRowSelectable(row)) {
                c.setForeground(ENABLED_ROW_FG_COLOR);
            }
            else {
                c.setForeground(DISABLED_ROW_FG_COLOR);
            }
        }
        return c;
    }

    protected boolean isRowSelectable(int row) {
        if (getModel() instanceof AKMutableTableModel<?>) {
            return ((AKMutableTableModel<?>) getModel()).isRowEnabled(row);
        }
        return true;
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        boolean x = (!isComponentExecutable()) ? false : enabled;
        super.setEnabled(x);
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        super.setVisible(x);
    }

    /**
     * Ueber diese Methode kann bestimmt werden, ob die Suche in der Tabelle aktiv sein soll.
     *
     * @param searchEnabled
     */
    public void setSearchEnabled(boolean searchEnabled) {
        this.searchEnabled = searchEnabled;
    }

    /**
     * Startet eine Suche ueber die gesamte Tabelle. <br> Dazu wird ein Such-Dialog aufgerufen. In diesem ist die
     * eigentlich Such-Logik implementiert!
     */
    protected void searchTable() {
        if (searchEnabled) {
            AKTableSearchDialog searchDialog = new AKTableSearchDialog(this);
            DialogHelper.showDialog(getParent(), searchDialog, true, true);
        }
    }

    /**
     * Definiert fuer die Spalte <code>column</code> den CellEditor <code>editor</code>.
     *
     * @param column Index der Spalte (beginnend bei 0)
     * @param editor zu setzender CellEditor fuer die Spalte
     *
     */
    public void setCellEditor4Column(int column, TableCellEditor editor) {
        TableColumnModel tcm = getColumnModel();
        TableColumn col = tcm.getColumn(column);
        if (col != null) {
            col.setCellEditor(editor);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TableCellEditor getDefaultEditor(Class<?> columnClass) {
        TableCellEditor editor = null;
        if (Enum.class.isAssignableFrom(columnClass)) {
            editor = new AKJComboBoxCellEditor(new AKJComboBox(EnumSet.allOf((Class<Enum>) columnClass).toArray()));
        }
        if (Date.class.isAssignableFrom(columnClass)) {
            editor = new AKJDateComponentCellEditor(new AKJDateComponent());
        }
        if (String.class.isAssignableFrom(columnClass)) {
            editor = new AKStringCellEditor(new AKJTextField());
        }
        if (editor == null) {
            editor = super.getDefaultEditor(columnClass);
        }
        return editor;
    }

    /**
     * Gibt das Objekt der Tabelle an der aktuellen Mouse-Position zurueck. Es wird davon ausgegangen, dass nur eine
     * Zeile markiert ist!
     */
    @SuppressWarnings("unchecked")
    public <T> T getTableSelection(MouseEvent me, Class<T> expectedType) {
        AKJTable table = (AKJTable) me.getSource();
        Point point = new Point(me.getX(), me.getY());
        int row = table.rowAtPoint(point);
        int column = table.columnAtPoint(point);
        table.changeSelection(row, column, false, false);

        T value = ((AKMutableTableModel<T>) table.getModel()).getDataAtRow(row);
        if (expectedType.isInstance(value)) {
            return value;
        }
        return null;
    }

    /**
     * KeyListener fuer die Tabelle. <br> Ueber diesen Key-Listener kann mit 'END' und 'HOME' an das Ende bzw. den
     * Anfang der Tabelle gesprungen werden. <br> Ausserdem koennen die markierten Zellen/Zeilen in die Zwischenablage
     * uebernommen werden.
     */
    class DefaultTableKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getSource() instanceof AKJTable) {
                int keyCode = event.getKeyCode();
                AKJTable table = (AKJTable) event.getSource();

                if (keyCode == KeyEvent.VK_END) {
                    table.setRowSelectionInterval(
                            table.getRowCount() - 1, table.getRowCount() - 1);
                }
                else if (keyCode == KeyEvent.VK_HOME) {
                    table.setRowSelectionInterval(0, 0);
                }
                else if ((keyCode == KeyEvent.VK_C) && event.isControlDown()) {
                    copyToClipboard(true);
                }
                else if (event.isControlDown() && (keyCode == KeyEvent.VK_F)) {
                    searchTable();
                }
            }
        }
    }

    /**
     * @return filterEnabled
     */
    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    /**
     * @param filterEnabled Festzulegender filterEnabled
     */
    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    /**
     * Funktion überprüft, ob auf einer Tabelle ein Filter angewendet werden darf.
     *
     * @return
     *
     */
    public boolean isTableFilterable() {
        TableModel model = getModel();
        return isFilterEnabled() && ((model instanceof AKFilterTableModel)
                || ((model instanceof AKTableSorter<?>) && (((AKTableSorter<?>) model).getModel() instanceof AKFilterTableModel)));
    }

    /**
     * @return Returns a copy of the evenRowBGColor.
     */
    public static Color getEvenRowBGColor() {
        Color evenRowBGColorCopy = new Color(EVEN_ROW_BG_COLOR.getRed(), EVEN_ROW_BG_COLOR.getGreen(), EVEN_ROW_BG_COLOR.getBlue());
        return evenRowBGColorCopy;
    }

    /**
     * @return Returns a copy of the oddRowBGColor.
     */
    public static Color getOddRowBGColor() {
        Color oddRowBGColorCopy = new Color(ODD_ROW_BG_COLOR.getRed(), ODD_ROW_BG_COLOR.getGreen(), ODD_ROW_BG_COLOR.getBlue());
        return oddRowBGColorCopy;
    }

    /**
     * Ermittelt die selektierten Datensaetze der Tabelle. ACHTUNG: die Ermittlung funktioniert nur, wenn es sich bei
     * dem hinterlegten TableModel um eine Instanz von {@link AKMutableTableModel} handelt!
     *
     * @param <E>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> List<E> getTableSelectionAsList(Class<E> clazz) {
        int[] selectedRows = getSelectedRows();

        if ((selectedRows != null) && (selectedRows.length > 0)) {
            List<E> result = new ArrayList<E>();
            if (getModel() instanceof AKMutableTableModel) {
                AKMutableTableModel<E> tableModel = (AKMutableTableModel<E>) getModel();
                for (int selectedRow : selectedRows) {
                    Object selection = tableModel.getDataAtRow(selectedRow);
                    if ((selection != null) && selection.getClass().isAssignableFrom(clazz)) {
                        result.add((E) selection);
                    }
                }
            }
            else {
                throw new RuntimeException("Could not return table selection. Model is not of type AKMutableTableModel.");
            }
            return result;
        }
        return null;
    }

}
