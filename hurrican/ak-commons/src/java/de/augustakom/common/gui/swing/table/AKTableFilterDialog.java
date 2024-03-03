/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2006 12:02:11
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.tools.lang.WildcardTools;


/**
 * Filter-Dialog fuer eine Tabelle.
 *
 *
 */
public class AKTableFilterDialog extends AKJAbstractOptionDialog {

    private static final long serialVersionUID = -3015941883610703283L;

    private AKJTable table2Filter = null;
    private AKJCheckBox chbWildcNec = null;
    private AKJCheckBox chbCleanFilter = null;
    private AKJComboBox cbVerknuepfung = null;
    private Object[] inputFields = null;
    private Object[] operators = null;

    public AKTableFilterDialog(AKJTable table2Filter) {
        super("de/augustakom/common/gui/resources/AKTableFilterDialog.xml", true);
        this.table2Filter = table2Filter;
        // Falls Tabelle nicht filterbar, beende Dialog
        if (!table2Filter.isTableFilterable()) {
            close(Integer.valueOf(CANCEL_OPTION));
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIconURL(getSwingFactory().getText("title.icon"));

        KeyListener searchKL = new FilterKeyListener();

        // Erzeuge Button-Panel
        AKJButton btnFilter = getSwingFactory().createButton("filter", getActionListener());
        btnFilter.addKeyListener(searchKL);
        AKJButton btnClose = getSwingFactory().createButton("close", getActionListener());
        AKJLabel lblOperator = getSwingFactory().createLabel("cbVerknuepfung");
        cbVerknuepfung = getSwingFactory().createComboBox("cbVerknuepfung");
        chbWildcNec = getSwingFactory().createCheckBox("chbWC");
        chbCleanFilter = getSwingFactory().createCheckBox("chbCleanFilter");

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(lblOperator, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        btnPnl.add(cbVerknuepfung, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        btnPnl.add(chbCleanFilter, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        btnPnl.add(chbWildcNec, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        btnPnl.add(btnFilter, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnClose, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        // Erzeuge Panel für Eingabefelder
        int colNumber = table2Filter.getColumnCount();
        AKJLabel[] colLabel = new AKJLabel[colNumber];
        inputFields = new Object[colNumber];
        operators = new Object[colNumber];

        AKJPanel fields = new AKJPanel();
        fields.setLayout(new GridBagLayout());
        fields.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        fields.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fields.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        fields.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));

        // Erzeuge dynamisch die für das jeweilige Modell nötigen Felder
        for (int i = 0; i < colNumber; i++) {
            colLabel[i] = new AKJLabel(table2Filter.getColumnName(i));
            Class<?> currentClass = table2Filter.getColumnClass(i);

            if ((currentClass == Number.class)
                    || (currentClass == Integer.class)
                    || (currentClass == Long.class)
                    || (currentClass == Float.class)
                    || (currentClass == Long.class)) {
                inputFields[i] = new AKJFormattedTextField();
            }
            else if (currentClass == Date.class) {
                inputFields[i] = new AKJDateComponent();
            }
            else if (currentClass == Timestamp.class) {
                AKJDateComponent dateComponent = new AKJDateComponent();
                dateComponent.setDateFormat(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
                inputFields[i] = dateComponent;
            }
            else if (currentClass == Boolean.class) {
                inputFields[i] = getSwingFactory().createComboBox("boolean");
            }
            else {
                inputFields[i] = new AKJTextField();
            }
            ((JComponent) inputFields[i]).addKeyListener(searchKL);

            // Operator erzeugen
            if (currentClass == Boolean.class) {
                operators[i] = getSwingFactory().createComboBox("operator-boolean");
            }
            else {
                operators[i] = getSwingFactory().createComboBox("operator");
            }
            fields.add(colLabel[i], GBCFactory.createGBC(0, 0, 1, i + 1, 1, 1, GridBagConstraints.HORIZONTAL));
            if ((currentClass == String.class)
                    || (currentClass == Number.class)
                    || (currentClass == Integer.class)
                    || (currentClass == Long.class)
                    || (currentClass == Float.class)
                    || (currentClass == Double.class)
                    || (currentClass == Boolean.class)
                    || (currentClass == Date.class)
                    || (currentClass == Timestamp.class)
                    || (currentClass == Object.class)) {
                fields.add((JComponent) operators[i], GBCFactory.createGBC(0, 0, 3, i + 1, 1, 1, GridBagConstraints.HORIZONTAL));
            }
            fields.add((JComponent) inputFields[i], GBCFactory.createGBC(100, 0, 5, i + 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        AKJScrollPane spFields = new AKJScrollPane(fields);
        spFields.setPreferredSize(new Dimension(170, 280));

        AKJPanel panel = getChildPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 400));
        panel.add(spFields, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        panel.add(btnPnl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("filter".equals(command)) {
            filter();
            close(Integer.valueOf(OK_OPTION));
        }
        else if ("close".equals(command)) {
            close(Integer.valueOf(CANCEL_OPTION));
        }
    }

    /**
     * Funktion schließt Fenster
     */
    private void close(Integer option) {
        prepare4Close();
        setValue(option);
    }

    /**
     * Funktion filtert die Datensätze im TableModel
     */
    private void filter() {
        // Falls Tabelle nicht filterbar, breche ab
        if (!table2Filter.isTableFilterable()) {
            return;
        }

        boolean filterSet = Boolean.FALSE;
        int colNumber = table2Filter.getColumnCount();
        AKFilterTableModel model = (AKFilterTableModel) ((AKTableSorter) table2Filter.getModel()).getModel();
        Object reference = null;
        FilterRelation relation = new FilterRelation(Boolean.TRUE.equals(cbVerknuepfung.getSelectedItemValue())
                ? FilterRelations.AND : FilterRelations.OR);

        // Durchlaufe alle Eingabefelder und wende die Filter an
        for (int i = 0; i < colNumber; i++) {
            Class<?> currentClass = table2Filter.getColumnClass(i);

            if (currentClass == Date.class) {
                reference = ((AKJDateComponent) inputFields[i]).getDate(null);
            }
            else if (currentClass == Timestamp.class) {
                Date date = ((AKJDateComponent) inputFields[i]).getDate(null);
                reference = null == date ? null : new Timestamp(date.getTime());
            }
            else if ((currentClass == Float.class)
                    || (currentClass == Double.class)) {
                // Falls Nutzer Gleitpunkzahl mit Komma eingibt, wird dies berichtigt.
                String eingabe = ((AKJFormattedTextField) inputFields[i]).getText();
                try {
                    eingabe = eingabe.replaceAll(",", ".");
                    reference = Float.parseFloat(eingabe);
                }
                catch (NumberFormatException e) {
                    reference = null;
                }
            }
            else if ((currentClass == Number.class)
                    || (currentClass == Integer.class)
                    || (currentClass == Long.class)) {

                if (currentClass == Integer.class) {
                    reference = ((AKJFormattedTextField) inputFields[i]).getValueAsInt(null);
                }
                else {
                    reference = ((AKJFormattedTextField) inputFields[i]).getValueAsLong(null);
                }
            }
            else if (currentClass == Boolean.class) {
                // Die Werte true und false werden richtig als Boolean-Objekt ausgelesen,
                // der Wert null kann nicht als Objekt ausgelesen werden
                // und wird deshalb als String übergeben.
                reference = ((AKJComboBox) inputFields[i]).getSelectedItemValue();
                if ((reference instanceof String) && (reference.equals("null"))) {
                    reference = null;
                }
            }
            else if ((currentClass == String.class)
                    || (currentClass == Object.class)) {
                reference = ((AKJTextField) inputFields[i]).getText();
                if (StringUtils.isEmpty(reference.toString())) {
                    reference = null;
                }
                else {
                    reference = wildcardVal(reference.toString());
                }
            }
            else {
                reference = null;
            }

            // Falls das betrachtete Eingabefeld nicht leer ist, wende Filter an
            if (reference != null) {
                Integer operatorNum = (Integer) ((AKJComboBox) operators[i]).getSelectedItemValue();
                FilterOperators operator;
                switch (operatorNum) {
                    case 0:
                        operator = FilterOperators.EQ;
                        break;
                    case 1:
                        operator = FilterOperators.LT;
                        break;
                    case 2:
                        operator = FilterOperators.GT;
                        break;
                    case 3:
                        operator = FilterOperators.LT_EQ;
                        break;
                    case 4:
                        operator = FilterOperators.GT_EQ;
                        break;
                    case 5:
                        operator = FilterOperators.NOT_EQ;
                        break;
                    default:
                        operator = FilterOperators.EQ;
                        break;
                }
                relation.addChild(new FilterOperator(operator, reference, table2Filter.convertColumnIndexToModel(i)));
                filterSet = Boolean.TRUE;
            }
        }

        // Falls kein Filter gesetzt wurde, mache Filterung rückgängig.
        if (!filterSet) {
            model.removeFilter(null);
        }
        else {
            // Löscht alle bestehenden Filter
            if (chbCleanFilter.isSelected()) {
                model.removeFilter(null);
            }
            model.addFilter(relation);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Haengt an den Suchtext Wildcards, falls die CheckBox markiert ist.
     */
    public String wildcardVal(String searchPattern) {
        if (chbWildcNec.isSelected()) {
            StringBuilder strb = new StringBuilder();
            if (searchPattern.startsWith(WildcardTools.SYSTEM_WILDCARD)) {
                strb.append(searchPattern);
            }
            else {
                strb.append(WildcardTools.SYSTEM_WILDCARD);
                strb.append(searchPattern);
            }

            if (!searchPattern.endsWith(WildcardTools.SYSTEM_WILDCARD)) {
                strb.append(WildcardTools.SYSTEM_WILDCARD);
            }

            return strb.toString();
        }

        return searchPattern;
    }

    /**
     * KeyListener fuer die Suche.
     */
    class FilterKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                filter();
                close(Integer.valueOf(OK_OPTION));
            }
        }
    }
}
