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
import de.augustakom.common.gui.iface.AKTextSizeAware;
import de.augustakom.common.gui.swing.text.DocumentSizeFilter;


/**
 * AK-Implementierung einer JTextPane.
 *
 *
 * @see javax.swing.JTextPane
 */
public class AKJTextPane extends JTextPane implements AKColorChangeableComponent,
        AKManageableComponent, AKSwingConstants, AKTextSizeAware, AKPreventKeyStrokeAwareComponent {

    private static final AKJTextField referenceField = new AKJTextField();
    private static final long serialVersionUID = -8725573698856889890L;

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean textFlag = false;
    private String settedText = null;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    private int maxChars = -1;

    /**
     * Erzeugt eine neue Text-Pane.
     */
    public AKJTextPane() {
        super();
        init();
    }

    /**
     * Erzeugt eine neue Text-Pane mit Angabe des <code>StyledDocument</code>-Objekts.
     *
     * @param doc
     */
    public AKJTextPane(StyledDocument doc) {
        super(doc);
        init();
    }

    /* Initialisiert die TextPane */
    private void init() {
        // JTextField und JTextPane besitzen unterschiedliche Schriftgroessen
        // --> werden hier identisch gesetzt.
        setFont(referenceField.getFont());
    }

    /**
     * Gibt den Text des TextFields zurueck. <br> Wurde dem TextField ueber die setText-Methode kein Wert uebergeben und
     * ist das TextField 'leer', wird <code>defaultText</code> zurueck gegeben.
     *
     * @param defaultText Standard-Text, der verwendet werden soll.
     * @return Der Text des TextFields oder <code>defaultText</code>
     */
    public String getText(String defaultText) {
        String displayedText = getText();
        if ("".equals(displayedText) && textFlag) {
            if ("".equals(settedText)) {
                return settedText;
            }
            return defaultText;
        }

        return ("".equals(displayedText)) ? defaultText : displayedText;
    }

    /**
     * @see javax.swing.text.JTextComponent#setText(java.lang.String)
     */
    @Override
    public void setText(String t) {
        textFlag = true;
        settedText = t;
        super.setText(t);
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
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
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
    @Override
    public void setEnabled(boolean enabled) {
        boolean x = (!executable) ? false : enabled;
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
    @Override
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
    @Override
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
        this.setFont(getFont().deriveFont(fontStyle));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKTextSizeAware#setMaxChars(int)
     */
    public void setMaxChars(int maxChars) {
        this.maxChars = maxChars;
        Document doc = getDocument();
        if (doc instanceof AbstractDocument) {
            if (maxChars > 0) {
                ((AbstractDocument) doc).setDocumentFilter(new DocumentSizeFilter(this, maxChars));
            }
            else {
                ((AbstractDocument) doc).setDocumentFilter(null);
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

    /**
     * @see de.augustakom.common.gui.iface.AKTextSizeAware#getMaxChars()
     */
    public int getMaxChars() {
        return maxChars;
    }
}
