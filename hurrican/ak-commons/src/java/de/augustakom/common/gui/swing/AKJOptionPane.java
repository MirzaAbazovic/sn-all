/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import javax.swing.*;


/**
 * AK-Implementierung einer JOptionPane.
 *
 *
 * @see javax.swing.JOptionPane
 */
public class AKJOptionPane extends JOptionPane {

    /**
     * @see javax.swing.JOptionPane()
     */
    public AKJOptionPane() {
        super();
    }

    /**
     * @param message
     * @see javax.swing.JOptionPane(Object)
     */
    public AKJOptionPane(Object message) {
        super(message);
    }

    /**
     * @param message
     * @param messageType
     * @see javax.swing.JOptionPane(Object, int)
     */
    public AKJOptionPane(Object message, int messageType) {
        super(message, messageType);
    }

    /**
     * @param message
     * @param messageType
     * @param optionType
     * @see javax.swing.JOptionPane(Object, int, int)
     */
    public AKJOptionPane(Object message, int messageType, int optionType) {
        super(message, messageType, optionType);
    }

    /**
     * @param message
     * @param messageType
     * @param optionType
     * @param icon
     * @see javax.swing.JOptionPane(Object, int, int, Icon)
     */
    public AKJOptionPane(Object message, int messageType, int optionType, Icon icon) {
        super(message, messageType, optionType, icon);
    }

    /**
     * @param message
     * @param messageType
     * @param optionType
     * @param icon
     * @param options
     * @see javax.swing.JOptionPane(Object, int, int, Icon, Object[])
     */
    public AKJOptionPane(Object message, int messageType, int optionType,
            Icon icon, Object[] options) {
        super(message, messageType, optionType, icon, options);
    }

    /**
     * @param message
     * @param messageType
     * @param optionType
     * @param icon
     * @param options
     * @param initialValue
     * @see javax.swing.JOptionPane(Object, int, int, Icon, Object[], Object)
     */
    public AKJOptionPane(Object message, int messageType, int optionType,
            Icon icon, Object[] options, Object initialValue) {
        super(message, messageType, optionType, icon, options, initialValue);
    }

}
