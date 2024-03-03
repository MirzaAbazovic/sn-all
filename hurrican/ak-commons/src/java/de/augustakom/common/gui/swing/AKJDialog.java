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
 * AK-Implementierung eines JDialogs.
 *
 *
 * @see javax.swing.JDialog
 */
public class AKJDialog extends JDialog {

    /**
     * @see javax.swing.JDialog()
     */
    public AKJDialog() {
        super();
    }

    /**
     * @param owner
     * @see javax.swing.JDialog(Dialog)
     */
    public AKJDialog(Dialog owner) {
        super(owner);
    }

    /**
     * @param owner
     * @param modal
     * @see javax.swing.JDialog(Dialog, boolean)
     */
    public AKJDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    /**
     * @param owner
     * @throws java.awt.HeadlessException
     * @see javax.swing.JDialog(Frame)
     */
    public AKJDialog(Frame owner) {
        super(owner);
    }

    /**
     * @param owner
     * @param modal
     * @throws java.awt.HeadlessException
     * @see javax.swing.JDialog(Frame, boolean)
     */
    public AKJDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    /**
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     * @see javax.swing.JDialog(Dialog, String)
     */
    public AKJDialog(Dialog owner, String title) {
        super(owner, title);
    }

    /**
     * @param owner
     * @param title
     * @param modal
     * @throws java.awt.HeadlessException
     * @see javax.swing.JDialog(Dialog, String, boolean)
     */
    public AKJDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * @param owner
     * @param title
     * @see javax.swing.JDialog(Frame, String)
     */
    public AKJDialog(Frame owner, String title) {
        super(owner, title);
    }

    /**
     * @param owner
     * @param title
     * @param modal
     * @see javax.swing.JDialog(Frame, String, boolean)
     */
    public AKJDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * @param owner
     * @param title
     * @param modal
     * @param gc
     * @see javax.swing.JDialog(Dialog, String, boolean, GraphicsConfiguration)
     */
    public AKJDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    /**
     * @param owner
     * @param title
     * @param modal
     * @param gc
     * @see javax.swing.JDialog(Frame, String, boolean, GraphicsConfiguration)
     */
    public AKJDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

}
