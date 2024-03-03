/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKPreventKeyStrokeAwareComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;
import de.augustakom.common.gui.iface.AKTextSizeAware;
import de.augustakom.common.gui.swing.text.DocumentSizeFilter;


/**
 * AK-Implementierung eines JFormattedTextFields
 *
 *
 * @see javax.swing.JFormattedTextField
 */
public class AKJFormattedTextField extends JFormattedTextField implements
        AKColorChangeableComponent, AKManageableComponent, AKSwingConstants, AKTextSizeAware,
        AKPreventKeyStrokeAwareComponent {
    private static final Logger LOGGER = Logger.getLogger(AKJFormattedTextField.class);

    private static final AKJTextField referenceField = new AKJTextField();

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    private int maxChars = -1;

    /**
     * @see javax.swing.JFormattedTextField()
     */
    public AKJFormattedTextField() {
        super();
        init();
    }

    /**
     * @param value
     * @see javax.swing.JFormattedTextField(Object)
     */
    public AKJFormattedTextField(Object value) {
        super(value);
        init();
    }

    /**
     * @param format
     * @see javax.swing.JFormattedTextField(Format)
     */
    public AKJFormattedTextField(Format format) {
        super(format);
        init();
    }

    /**
     * @param formatter
     * @see javax.swing.JFormattedTextField(AbstractFormatter)
     */
    public AKJFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
        init();
    }

    /**
     * @param factory
     * @see javax.swing.JFormattedTextField(AbstractFormatterFactory)
     */
    public AKJFormattedTextField(AbstractFormatterFactory factory) {
        super(factory);
        init();
    }

    /**
     * @param factory
     * @param currentValue
     * @see javax.swing.JFormattedTextField(AbstractFormatterFactory, Object)
     */
    public AKJFormattedTextField(AbstractFormatterFactory factory, Object currentValue) {
        super(factory, currentValue);
        init();
    }

    /* Initialisiert das TextField. */
    private void init() {
        // JTextField und JFormattedTextField besitzen unterschiedliche Schriftgroessen
        // --> werden hier identisch gesetzt.
        setFont(referenceField.getFont());
    }

    /**
     * @see javax.swing.JFormattedTextField#commitEdit()
     */
    @Override
    public void commitEdit() throws ParseException {
        if (StringUtils.isBlank(getText())) {
            setValue(null);
            super.commitEdit();
            return;
        }

        super.commitEdit();
    }

    /**
     * @see javax.swing.JFormattedTextField#commitEdit() Aenderung: Exceptions werden unterdrueckt.
     */
    public void commitEditSilent() {
        try {
            this.commitEdit();
        }
        catch (Exception e) {
            LOGGER.debug("commitEditSilent() - caught exception", e);
        }
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
     * Gibt den Text als Integer-Objekt zurueck.
     *
     * @param defaultValue
     * @return
     */
    public Integer getValueAsInt(Integer defaultValue) {
        if (getValue() instanceof Number) {
            return Integer.valueOf(((Number) getValue()).intValue());
        }

        try {
            return Integer.valueOf(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text als Long-Objekt zurueck.
     *
     * @param defaultValue
     * @return
     */
    public Long getValueAsLong(Long defaultValue) {
        if (getValue() instanceof Number) {
            return Long.valueOf(((Number) getValue()).longValue());
        }

        try {
            return Long.valueOf(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text als Short-Objekt zurueck.
     *
     * @param defaultValue
     * @return
     */
    public Short getValueAsShort(Short defaultValue) {
        if (getValue() instanceof Number) {
            return Short.valueOf(((Number) getValue()).shortValue());
        }

        try {
            return Short.valueOf(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text als Double-Objekt zurueck.
     *
     * @param defaultValue
     * @return
     */
    public Double getValueAsDouble(Double defaultValue) {
        if (getValue() instanceof Number) {
            return new Double(((Number) getValue()).doubleValue());
        }

        try {
            return new Double(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text als Float-Objekt zurueck.
     *
     * @param defaultValue
     * @return
     */
    public Float getValueAsFloat(Float defaultValue) {
        if (getValue() instanceof Number) {
            return new Float(((Number) getValue()).floatValue());
        }

        try {
            return new Float(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text als Date-Objekt zurueck.
     *
     * @param defaultValue
     * @return
     */
    public Date getValueAsDate(Date defaultValue) {
        if (getValue() instanceof Date) {
            return (Date) getValue();
        }

        return defaultValue;
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
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
        Font f = getFont();
        this.setFont(new Font(f.getName(), fontStyle, f.getSize()));
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
