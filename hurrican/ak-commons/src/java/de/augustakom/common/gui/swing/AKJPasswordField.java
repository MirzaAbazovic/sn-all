/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKPreventKeyStrokeAwareComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;


/**
 * AK-Implementierung eines JPasswordFields.
 *
 *
 * @see javax.swing.JPasswordField
 */
public class AKJPasswordField extends JPasswordField implements AKColorChangeableComponent,
        AKManageableComponent, AKSwingConstants, AKPreventKeyStrokeAwareComponent {

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private AKSelectAllFocusListener selectAllFL = null;

    /**
     * @see javax.swing.JPasswordField()
     */
    public AKJPasswordField() {
        super();
    }

    /**
     * @param columns
     * @see javax.swing.JPasswordField(int)
     */
    public AKJPasswordField(int columns) {
        super(columns);
    }

    /**
     * @param text
     * @see javax.swing.JPasswordField(String)
     */
    public AKJPasswordField(String text) {
        super(text);
    }

    /**
     * @param text
     * @param columns
     * @see javax.swing.JPasswordField(String, int)
     */
    public AKJPasswordField(String text, int columns) {
        super(text, columns);
    }

    /**
     * @param doc
     * @param txt
     * @param columns
     * @see javax.swing.JPasswordField(Document, String, int)
     */
    public AKJPasswordField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
    }


    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
        boundColorChangeFocusListener();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public void setInactiveColor(Color inactiveColor) {
        this.inactiveColor = inactiveColor;
        boundColorChangeFocusListener();
        if (this.inactiveColor != null) {
            setBackground(this.inactiveColor);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public Color getInactiveColor() {
        return inactiveColor;
    }

    /**
     * Gibt das Passwort als String zurueck.
     *
     * @return
     */
    public String getPasswordAsString() {
        char[] pw = getPassword();
        if (pw != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pw.length; i++) {
                sb.append(pw[i]);
            }

            return sb.toString();
        }

        return "";
    }

    /**
     * Fuegt dem TextField einmalig einen FocusListener vom Typ ChangeColorFocusListener hinzu.
     */
    private void boundColorChangeFocusListener() {
        if (!focusListenerBound) {
            addFocusListener(new AKChangeColorFocusListener());
            focusListenerBound = true;
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    public String getComponentName() {
        return (getAccessibleContext() != null && getAccessibleContext().getAccessibleName() != null)
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        boolean x = (!isComponentExecutable()) ? false : enabled;
        super.setEnabled(x);

        if (!x) {
            setBackground(PANEL_BACKGROUND_COLOR);
        }
        else {
            if (isEditable()) {
                if (hasFocus()) {
                    setBackground(activeColor);
                }
                else {
                    setBackground(inactiveColor);
                }
            }
        }
    }

    /**
     * @see javax.swing.text.JTextComponent#setEditable(boolean)
     */
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        if (!editable) {
            setBackground(PANEL_BACKGROUND_COLOR);
        }
        else {
            if (hasFocus()) {
                setBackground(activeColor);
            }
            else {
                setBackground(inactiveColor);
            }
        }
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        super.setVisible(x);
    }

    /**
     * Aendert den Schriftstil des Labels. Die Schriftart und die Schriftgroesse bleiben erhalten.
     *
     * @param fontStyle Stil, der gesetzt werden soll. Moegliche Werte fuer fontStyle: <br> <ul> <li>Font.PLAIN
     *                  <li>Font.BOLD <li>Font.ITALIC </ul>
     */
    public void setFontStyle(int fontStyle) {
        setFont(getFont().deriveFont(fontStyle));
    }

    /**
     * Bestimmt, ob der gesamte Inhalt des TextFields markiert werden soll, wenn das TextField den Focus erhaelt.
     *
     * @param selectAll
     */
    public void selectAllOnFocus(boolean selectAll) {
        if (selectAll) {
            selectAllFL = new AKSelectAllFocusListener();
            this.addFocusListener(selectAllFL);
        }
        else {
            if (selectAllFL != null) {
                this.removeFocusListener(selectAllFL);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKPreventKeyStrokeAwareComponent#preventKeyStroke(java.lang.String)
     */
    public void preventKeyStroke(String keyStrokeToPrevent) {
        if (StringUtils.isNotBlank(keyStrokeToPrevent)) {
            this.getInputMap(JComponent.WHEN_FOCUSED).put(
                    KeyStroke.getKeyStroke(keyStrokeToPrevent), "none");
        }
    }

}
