/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;


/**
 * AK-Implementierung eines JWindows
 *
 *
 * @see javax.swing.JWindow
 */
public class AKJWindow extends JWindow {

    private String title = null;
    private String iconURL = null;

    /**
     * Erzeugt ein neues JWindow.
     */
    public AKJWindow() {
        super();
    }

    /**
     * Erzeugt ein neues Window mit Angabe eines Owner-Frames.
     *
     * @param owner
     */
    public AKJWindow(Frame owner) {
        super(owner);
    }

    /**
     * Erzeugt ein neues Window mit Angabe eines <code>GraphickConfiguration</code>-Objekts.
     *
     * @param gc
     */
    public AKJWindow(GraphicsConfiguration gc) {
        super(gc);
    }

    /**
     * Erzeugt ein Window mit Angabe eines Owner-Windows.
     *
     * @param owner
     */
    public AKJWindow(Window owner) {
        super(owner);
    }

    /**
     * Erzeugt ein neues Window mit Angabe des Owner-Windows und eines <code>GraphicsConfiguration</code>-Objekts.
     *
     * @param owner
     * @param gc
     */
    public AKJWindow(Window owner, GraphicsConfiguration gc) {
        super(owner, gc);
    }

    /**
     * Setzt den Titel fuer das Window. <br> Der Titel wird nur dann angezeigt, wenn das Window ueber die Klasse
     * <code>de.augustakom.common.gui.swing.DialogHelper</code> aufgerufen wird.
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Titel fuer das Window.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setzt die Image-URL des Icons, das in der Titelleiste des Dialogs angezeigt werden soll. <br> Das hier definierte
     * Icon wird nur verwendet, wenn der Dialog ueber die Klasse <code>de.augustakom.common.gui.swing.DialogHelper</code>
     * aufgerufen wird.
     *
     * @param iconURL
     */
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    /**
     * Gibt die URL des Icons zurueck, das in der Titelleiste angezeigt werden soll.
     *
     * @return
     */
    public String getIconURL() {
        return iconURL;
    }
}
