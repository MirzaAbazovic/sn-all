/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 14:12:04
 */
package de.augustakom.common.gui.swing;

import javax.swing.tree.*;


/**
 * Implementierung eines TreeNodes.
 *
 *
 */
public class AKJDefaultMutableTreeNode extends DefaultMutableTreeNode {

    private boolean leaf = false;
    private boolean loaded = false;
    private String text = null;
    private String tooltip = null;
    private String iconName = null;

    /**
     * @see javax.swing.tree.DefaultMutableTreeNode
     */
    public AKJDefaultMutableTreeNode() {
        super();
    }

    /**
     * @see javax.swing.tree.DefaultMutableTreeNode(Object)
     */
    public AKJDefaultMutableTreeNode(Object userObject) {
        super(userObject);
    }

    /**
     * @see javax.swing.tree.DefaultMutableTreeNode(Object, boolean)
     */
    public AKJDefaultMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /**
     * Markiert den Node als 'Leaf' oder 'Parent'
     *
     * @param isLeaf
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * @see javax.swing.tree.DefaultMutableTreeNod#isLeaf
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @return Returns the loaded.
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @param loaded The loaded to set.
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Setzt den Namen/URL des Icons, das fuer den Node verwendet werden soll.
     *
     * @return Returns the iconName.
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * Gibt den Namen/URL des zu verwendenden Icons zurueck.
     *
     * @param iconName The iconName to set.
     */
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    /**
     * Setzt den Text, der fuer den Node dargestellt werden soll.
     *
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * Gibt den darzustellenden Text zurueck.
     *
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Setzt den Tooltip-Text fuer den Node.
     *
     * @return Returns the tooltip.
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Gibt den Tooltip-Text fuer den Node zurueck.
     *
     * @param tooltip The tooltip to set.
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

}
