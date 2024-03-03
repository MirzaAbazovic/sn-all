/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 10:26:43
 */
package de.augustakom.hurrican.model.cc.gui;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell definiert die Eigenschaften einer GUI-Komponente. <br> Der Typ (z.B. Panel, MenuItem etc.) der Komponente ist
 * ueber den Wert von <code>type</code> festgelegt. <br> Die moeglichen Werte fuer <code>type</code> sind als Konstante
 * in dieser Klasse definiert.
 *
 *
 */
public class GUIDefinition extends AbstractCCIDModel {

    /**
     * Konstante, die eine GUI-Komponente als Panel markiert.
     */
    public static final String TYPE_PANEL = "PANEL";

    /**
     * Konstante, die ine GUI-Komponente als Internal-Frame markiert.
     */
    public static final String TYPE_FRAME = "INTERNALFRAME";

    /**
     * Konstante, die eine GUI-Komponente als Action markiert.
     */
    public static final String TYPE_ACTION = "ACTION";

    private String clazz = null;
    private String name = null;
    private String type = null;
    private String icon = null;
    private String tooltip = null;
    private String text = null;
    private Boolean addSeparator = null;
    private Integer orderNo = null;
    private Boolean active = null;

    /**
     * @return Returns the clazz.
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * @param clazz The clazz to set.
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * @return Returns the icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon to set.
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the orderNo.
     */
    public Integer getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo The orderNo to set.
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return Returns the tooltip.
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * @param tooltip The tooltip to set.
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the addSeparator.
     */
    public Boolean getAddSeparator() {
        return addSeparator;
    }

    /**
     * @param addSeparator The addSeparator to set.
     */
    public void setAddSeparator(Boolean addSeparator) {
        this.addSeparator = addSeparator;
    }

    /**
     * @return Returns the active.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}


