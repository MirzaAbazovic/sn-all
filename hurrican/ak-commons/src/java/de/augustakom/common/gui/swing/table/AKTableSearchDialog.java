/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 15:31:11
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.WildcardTools;


/**
 * Such-Dialog fuer eine Tabelle.
 *
 *
 */
public class AKTableSearchDialog extends AKJAbstractOptionDialog {

    private AKJTextField tfSearch = null;
    private AKJTable table2Search = null;
    private AKJCheckBox chbWildcNec = null;
    private int col2Search = -1;
    private int row2Search = 0;
    private String searchPattern = null;

    /**
     * Default-Konstruktor.
     */
    public AKTableSearchDialog(AKJTable table2Search) {
        super("de/augustakom/common/gui/resources/AKTableSearchDialog.xml", true);
        this.table2Search = table2Search;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIconURL(getSwingFactory().getText("title.icon"));

        KeyListener searchKL = new SearchKeyListener();

        AKJLabel lblSearch = getSwingFactory().createLabel("search");
        tfSearch = getSwingFactory().createTextField("search");
        tfSearch.addKeyListener(searchKL);
        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());
        btnSearch.addKeyListener(searchKL);
        AKJButton btnClose = getSwingFactory().createButton("close", getActionListener());
        chbWildcNec = getSwingFactory().createCheckBox("chbWC");
        chbWildcNec.addKeyListener(searchKL);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnSearch, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnClose, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(2, 2));

        AKJPanel panel = getChildPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(lblSearch, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfSearch, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbWildcNec, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        panel.add(btnPnl, GBCFactory.createGBC(0, 0, 1, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        panel.add(fill, GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
        else if ("close".equals(command)) {
            prepare4Close();
            setValue(Integer.valueOf(CANCEL_OPTION));
        }
    }

    boolean rowEndReached = false;
    boolean colEndReached = false;

    /* Fuehrt die Suche durch. */
    private void doSearch() {
        String tmpSearch = tfSearch.getText();
        tmpSearch = wildcardVal(tmpSearch);
        if (!StringUtils.equals(tmpSearch, searchPattern)) {
            clearSearch();
        }
        searchPattern = tfSearch.getText();

        if (StringUtils.isNotBlank(searchPattern)) {
            // Wenn chbWildcNec = True den Suchtext (tfSearch) auf vorhanden Wildcars untersuchen und evtl. erweitern
            searchPattern = wildcardVal(searchPattern);

            int rowCount = table2Search.getRowCount();
            int colCount = table2Search.getColumnCount();

            int row = row2Search;
            if (rowEndReached) {
                colEndReached = false;
                col2Search = 0;
            }

            if (!rowEndReached) {
                boolean found = false;
                for (int r = row; r < rowCount; r++) {
                    boolean match = false;
                    row2Search = (colEndReached && !rowEndReached) ? r + 1 : r;
                    int col = (colEndReached) ? 0 : col2Search + 1;

                    for (int c = col; c < colCount; c++) {
                        col2Search = c;
                        Object tmpValue = table2Search.getValueAt(r, c);
                        if (tmpValue != null) {
                            Object value = null;
                            TableCellRenderer renderer = table2Search.getCellRenderer(r, c);
                            Component comp = renderer.getTableCellRendererComponent(table2Search, tmpValue, false, false, r, c);
                            if (comp instanceof JLabel) {
                                value = ((JLabel) comp).getText();
                            }
                            else {
                                value = tmpValue;
                            }

                            match = WildcardTools.matchIgnoreCase(value.toString(), searchPattern);
                            if (match) {
                                found = true;
                                table2Search.setRowSelectionInterval(r, r);
                                table2Search.setColumnSelectionInterval(c, c);
                                table2Search.scrollToRowAndCol(r, c);

                                if (c == colCount - 1) {
                                    colEndReached = true;
                                }

                                if (r == rowCount - 1) {
                                    rowEndReached = true;
                                }
                                return;
                            }
                        }

                        if (c == colCount - 1) {
                            colEndReached = true;
                        }
                    }

                    if (r == rowCount - 1) {
                        rowEndReached = true;
                    }
                }

                if (!found) {
                    MessageHelper.showMessageDialog(this,
                            "Keine Übereinstimmung gefunden!", "Suche", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else {
                clearSearch();
                doSearch();
            }
        }
    }

    /* Loescht die urspruengliche Suche. */
    private void clearSearch() {
        rowEndReached = false;
        colEndReached = false;
        row2Search = 0;
        col2Search = -1;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /* Haengt an den Suchtext Wildcards, falls die CheckBox markiert ist. */
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

    /* KeyListener fuer die Suche. */
    class SearchKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (((e.getSource() == tfSearch) || (e.getSource() == chbWildcNec))
                    && (e.getKeyCode() == KeyEvent.VK_ENTER)) {
                doSearch();
            }
            else if (e.getKeyCode() == KeyEvent.VK_F3) {
                doSearch();
            }
        }
    }
}


