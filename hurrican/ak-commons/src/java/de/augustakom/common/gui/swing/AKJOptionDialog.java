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
 * Dialog-Implementierung, die von <code>javax.swing.JOptionPane</code> ableitet. <br> Durch die Ableitung von
 * JOptionPane kann dem Dialog u.a. auch ein anderes Icon fuer die Titelleiste zugeordnet werden. <br> Diese
 * Dialog-Implementierung ist der Klasse AKJDialog vorzuziehen.
 *
 *
 */
public class AKJOptionDialog extends JOptionPane {

    private String title = null;
    private String iconURL = null;

    private boolean willClose = false;

    /**
     * Standardkonstruktor fuer den Dialog.
     */
    public AKJOptionDialog() {
        super();
        initOptionDialog();
    }

    /**
     * Initialisiert den OptionDialog.
     */
    protected void initOptionDialog() {
        removeAll();
    }

    /**
     * Setzt den Titel fuer den Dialog. <br> Der Titel wird nur dann angezeigt, wenn der Option-Dialog ueber die Klasse
     * <code>de.augustakom.common.gui.swing.DialogHelper</code> aufgerufen wird.
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Titel fuer den Dialog.
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

    /**
     * Setzt den Cursor des Panels auf 'WAIT'
     */
    public void setWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Setzt den Cursor des Panels auf 'DEFAULT'
     */
    public void setDefaultCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Bereitet den Option-Dialog auf das Schliessen vor. <br> Diese Methode muss nur dann aufgerufen werden, wenn der
     * OptionDialog ueber die Hilfsklasse <code>DialogHelper</code> angezeigt wird und das Objekt der Methode
     * <code>setValue(Object)</code> nicht vom Typ Integer ist. <br> Wird in einem solchen Fall die Methode
     * <code>prepare4Close</code> nicht vor der Methode <code>setValue(Object)</code> aufgerufen, wird zwar der Dialog
     * geschlossen, es bleibt jedoch der Eintrag in der Task-Leiste des Betriebsystems bestehen.
     */
    public void prepare4Close() {
        this.willClose = true;
    }

    /**
     * Gibt an, ob der Option-Dialog geschlossen werden soll. <br> Diese Methode ist z.Z. nur fuer die Hilfsklasse
     * <code>DialogHelper</code> von Bedeutung.
     *
     * @return true wenn zuvor die Methode <code>prepare4Close</code> aufgerufen wurde.
     */
    public boolean willClose() {
        return willClose;
    }
}
