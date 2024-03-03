/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 07.10.2010 10:00:00
  */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.WildcardTools;


/**
 * Such-Dialog fuer einen Tree.
 *
 *
 */
public class AKTreeSearchDialog extends AKJAbstractOptionDialog {

    private AKJTextField tfSearch = null;
    private DefaultMutableTreeNode node2Search = null;
    private AKJCheckBox chbWildcNec = null;
    private AKJCheckBox chbExpandAll = null;
    private AKJButton btnClose = null;
    private AKJButton btnCancel = null;
    private AKJLabel lblInfo = null;

    private static final int STATUS_OK = 0;
    private static final int STATUS_WORKING = 1;
    private static final int STATUS_CANCELED = 2;
    private int status = STATUS_OK;

    /**
     * Default-Konstruktor.
     */
    public AKTreeSearchDialog(DefaultMutableTreeNode node) {
        super("de/augustakom/common/gui/resources/AKTreeSearchDialog.xml", true);
        this.node2Search = node;
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
        btnClose = getSwingFactory().createButton("close", getActionListener());
        btnCancel = getSwingFactory().createButton("cancel", getActionListener());
        btnCancel.setVisible(false);
        chbWildcNec = getSwingFactory().createCheckBox("chbWC");
        chbWildcNec.addKeyListener(searchKL);
        chbExpandAll = getSwingFactory().createCheckBox("chbEX");
        chbExpandAll.addKeyListener(searchKL);
        lblInfo = getSwingFactory().createLabel("info");

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnSearch, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnClose, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(lblInfo, GBCFactory.createGBC(0, 0, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(2, 2));

        AKJPanel panel = getChildPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(lblSearch, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfSearch, GBCFactory.createGBC(0, 0, 3, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbWildcNec, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        panel.add(chbExpandAll, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        panel.add(btnPnl, GBCFactory.createGBC(0, 0, 1, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        panel.add(fill, GBCFactory.createGBC(100, 100, 4, 5, 1, 1, GridBagConstraints.BOTH));

        this.setPreferredSize(new Dimension(300, 150));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("search".equals(command) && (status == STATUS_OK)) {
            status = STATUS_WORKING;
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    doSearch();
                }
            };
            new Thread(runner).start();
        }
        else if ("close".equals(command)) {
            prepare4Close();
            status = STATUS_CANCELED;
            setValue(Integer.valueOf(CANCEL_OPTION));
        }
        else if ("cancel".equals(command)) {
            status = STATUS_CANCELED;
        }
    }

    /* Fuehrt die Suche durch. */
    private void doSearch() {

        String searchPattern = tfSearch.getText();

        if (StringUtils.isNotBlank(searchPattern)) {
            // Wenn chbWildcNec = True den Suchtext (tfSearch) auf vorhandene Wildcards untersuchen und evtl. erweitern
            searchPattern = wildcardVal(searchPattern);
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnCancel.setCursor(Cursor.getDefaultCursor());
        btnClose.setVisible(false);
        btnCancel.setVisible(true);
        lblInfo.setText("Suchen...");

        DefaultMutableTreeNode nextNode = node2Search;
        DefaultMutableTreeNode found = null;
        while ((found == null) && (nextNode != null) && !isCanceled()) {
            found = searchNodeChildren(nextNode, searchPattern, nextNode == node2Search);
            if (found != null) {
                node2Search = found;
            }
            else {
                if ((nextNode.getParent() == nextNode.getRoot()) && (nextNode.getNextSibling() == null)) {
                    nextNode = null; // nichts mehr zum Suchen übrig
                }
                while ((nextNode != null) && (nextNode.getNextSibling() == null) && (nextNode.getParent() != nextNode.getRoot())) {
                    nextNode = (DefaultMutableTreeNode) nextNode.getParent();
                }
            }
            if (nextNode != null) {
                nextNode = nextNode.getNextSibling();
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
        btnCancel.setVisible(false);
        btnClose.setVisible(true);
        lblInfo.setText("");

        if (found == null) {
            MessageHelper.showMessageDialog(this,
                    "Keine Übereinstimmung gefunden!", "Suche", JOptionPane.INFORMATION_MESSAGE);
        }
        status = STATUS_OK;
    }

    /**
     * Sucht nach dem angegebenen Pattern in allen Child-Knoten des übergebenen Knotens.
     *
     * @param node            Knoten, von dem aus rekursiv nach unten gesucht wird
     * @param pattern         Suchpattern
     * @param ignoreFirstNode Gibt an, ob der übergebene Knoten auch gematcht werden soll
     * @return Gefundener Knoten oder null
     */
    protected DefaultMutableTreeNode searchNodeChildren(DefaultMutableTreeNode node, String pattern, boolean ignoreFirstNode) {
        if (node.getParent() != null) {
            showInfo("Durchsuche ", node.getParent());
        }
        if (!ignoreFirstNode && matches(node, pattern)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (isCanceled()) {
                break;
            }
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            if (matches(child, pattern)) {
                return child;
            }
            DefaultMutableTreeNode subNode = searchNodeChildren(child, pattern, child.isLeaf());
            if (subNode != null) {
                return subNode;
            }
        }
        return null;
    }

    /**
     * Prüft, ob der übergebene Knoten mit dem angegebenen Pattern übereinstimmt.
     *
     * @param node    Knoten, von dem aus rekursiv nach unten gesucht wird
     * @param pattern Suchpattern
     * @return Übereinstimmung mit dem Suchpattern ja/nein
     */
    protected boolean matches(TreeNode node, String pattern) {
        return WildcardTools.matchIgnoreCase(getNodeText(node), pattern);
    }

    private void showInfo(String infoText, TreeNode node) {
        String text = infoText + getNodeText(node);
        String text2 = text;
        int width = SwingUtilities.computeStringWidth(lblInfo.getFontMetrics(lblInfo.getFont()), text);
        while (width > getWidth() - 50) {
            text2 = text2.substring(0, text2.length() - 1);
            width = SwingUtilities.computeStringWidth(lblInfo.getFontMetrics(lblInfo.getFont()), text2.concat("..."));
        }
        if (!StringUtils.equals(text, text2)) {
            text2 = text2.concat("...");
        }
        lblInfo.setText(text2);
    }

    protected String getNodeText(TreeNode node) {
        return node.toString();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Haengt an den Suchtext Wildcards, falls die CheckBox markiert ist. */
    public String wildcardVal(String searchPattern) {
        if (isWildcardSearch()) {
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

    protected boolean isWildcardSearch() {
        return chbWildcNec.isSelected();
    }

    protected boolean isExpandAll() {
        return chbExpandAll.isSelected();
    }

    protected boolean isCanceled() {
        return status == STATUS_CANCELED;
    }

    /* KeyListener fuer die Suche. */
    class SearchKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (((e.getSource() == tfSearch) || (e.getSource() == chbWildcNec) || (e.getSource() == chbExpandAll))
                    && (e.getKeyCode() == KeyEvent.VK_ENTER)) {
                execute("search");
            }
            else if (e.getKeyCode() == KeyEvent.VK_F3) {
                execute("search");
            }
        }
    }
}


