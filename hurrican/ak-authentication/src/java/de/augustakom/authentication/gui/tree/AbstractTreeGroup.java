/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 07:47:43
 */
package de.augustakom.authentication.gui.tree;

import de.augustakom.common.model.AbstractObservable;


/**
 * Klassen, die von dieser Klasse ableiten, stellen eine TreeGroup dar. <br> Eine TreeGroup wird verwendet, um die Nodes
 * innerhalb des AdminTrees besser in logische Gruppen aufteilen zu koennen.
 */
public abstract class AbstractTreeGroup extends AbstractObservable {

    private String displayName = null;
    private String tooltip = null;
    private String iconURL = null;

    /**
     * Setzt den Text, der im TreeNode angezeigt werden soll.
     *
     * @param displayName anzuzeigender Text
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gibt den Text zurueck, der im TreeNode angezeigt werden soll.
     *
     * @return Text, der angezeigt werden soll
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setzt den Text, der als Tooltip fuer den TreeNode verwendet werden soll.
     *
     * @param tooltip Tooltip fuer TreeNode
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Gibt den Text zurueck, der als Tooltip fuer den TreeNode verwendet werden soll.
     *
     * @return Tooltip fuer den TreeNode
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Setzt die URL des Icons, das fuer den TreeNode verwendet werden soll.
     *
     * @param iconURL URL des zu verwendenden Icons.
     */
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    /**
     * Gibt die URL des Icons zurueck, das fuer den TreeNode verwendet werden soll.
     *
     * @return URL des zu verwendenden Icons.
     */
    public String getIconURL() {
        return iconURL;
    }
}
