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
import de.augustakom.common.gui.iface.AKModelAwareComponent;
import de.augustakom.common.gui.iface.AKPreventKeyStrokeAwareComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;
import de.augustakom.common.gui.iface.AKTextSizeAware;
import de.augustakom.common.gui.swing.text.DocumentSizeFilter;


/**
 * AK-Implementierung eines JTextFields.
 *
 *
 * @see javax.swing.JTextField
 */
public class AKJTextField extends JTextField implements AKColorChangeableComponent, AKManageableComponent,
        AKSwingConstants, AKTextSizeAware, AKModelAwareComponent, AKPreventKeyStrokeAwareComponent {

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean textFlag = false;
    private String settedText = null;
    private int maxChars = -1;

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private AKSelectAllFocusListener selectAllFL = null;

    private Object model = null;

    /**
     * Erzeugt ein 'leeres' TextField.
     */
    public AKJTextField() {
        super();
    }

    /**
     * Erzeugt ein TextField mit Angabe der Anzahl der darzustellenden Spalten.
     *
     * @param columns
     */
    public AKJTextField(int columns) {
        super(columns);
    }

    /**
     * Erzeugt ein TextField mit initialem Text.
     *
     * @param text
     */
    public AKJTextField(String text) {
        super(text);
    }

    /**
     * Erzeugt ein TextField mit initialem Text und Angabe der darzustellenden Spaltenanzahl.
     *
     * @param text
     * @param columns
     */
    public AKJTextField(String text, int columns) {
        super(text, columns);
    }

    /**
     * Erzeugt ein TextField mit initialem Text und Spaltenanzahl sowie Angabe eines Dokuments.
     *
     * @param doc
     * @param text
     * @param columns
     */
    public AKJTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelAwareComponent#getModel()
     */
    @Override
    public Object getModel() {
        return model;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelAwareComponent#setModel(java.lang.Object)
     */
    @Override
    public void setModel(Object model) {
        this.model = model;
    }

    /**
     * Gibt den getrimmten Text des TextFields zurueck und schreibt gegebenfalls den getrimmten
     * Text ins TextField zurueck.
     * @return Der Text des TextFields ohne fuehrende und endende Whitespaces oder <code>leerstring</code>
     */
    @Override
    public String getText() {
        String tainted = super.getText();
        String cleaned = StringUtils.trimToEmpty(tainted);
        if (!cleaned.equals(tainted)) {
            this.setText(cleaned);
        }
        return cleaned;
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
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
        boundColorChangeFocusListener();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public Color getInactiveColor() {
        return inactiveColor;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    @Override
    public void setInactiveColor(Color inactiveColor) {
        this.inactiveColor = inactiveColor;
        boundColorChangeFocusListener();
        if (this.inactiveColor != null) {
            setBackground(this.inactiveColor);
        }
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
    @Override
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    @Override
    public boolean isComponentExecutable() {
        return executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    @Override
    public boolean isComponentVisible() {
        return visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    @Override
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    @Override
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
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
     * @see javax.swing.text.JTextComponent#setText(java.lang.String)
     */
    @Override
    public void setText(String t) {
        textFlag = true;
        settedText = t;
        super.setText(t);
    }

    /**
     * Uebergibt dem TextField ein Integer-Objekt. Dargestellt wird die entsprechende String-Abbildung.
     *
     * @param value
     */
    public void setText(Integer value) {
        if (value != null) {
            setText(value.toString());
        }
        else {
            setText("");
        }
    }

    /**
     * Uebergibt dem TextField ein Long-Objekt. Dargestellt wird die entsprechende String-Abbildung.
     *
     * @param value
     */
    public void setText(Long value) {
        if (value != null) {
            setText(value.toString());
        }
        else {
            setText("");
        }
    }

    /**
     * Uebergibt dem TextField ein Double-Objekt. Dargestellt wird die entsprechende String-Abbildung.
     *
     * @param value
     */
    public void setText(Double value) {
        if (value != null) {
            setText(value.toString());
        }
        else {
            setText("");
        }
    }

    /**
     * Uebergibt dem TextField ein Float-Objekt. Dargestellt wird die entsprechende String-Abbildung.
     *
     * @param value
     */
    public void setText(Float value) {
        if (value != null) {
            setText(value.toString());
        }
        else {
            setText("");
        }
    }

    /**
     * Gibt den Text des TextFields als Integer zurueck. <br> Kann der Text nicht in einen Integer gewandelt werden,
     * wird <code>defaultValue</code> zurueck gegeben.
     *
     * @param defaultValue
     * @return
     */
    public Integer getTextAsInt(Integer defaultValue) {
        try {
            return Integer.valueOf(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text des TextFields als Long zurueck. <br> Kann der Text nicht in einen Long gewandelt werden, wird
     * <code>defaultValue</code> zurueck gegeben.
     *
     * @param defaultValue
     * @return
     */
    public Long getTextAsLong(Long defaultValue) {
        try {
            return Long.valueOf(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text des TextFields als Double zurueck. <br> Kann der Text nicht in einen Double gewandelt werden, wird
     * <code>defaultValue</code> zurueck gegeben.
     *
     * @param defaultValue
     * @return
     */
    public Double getTextAsDouble(Double defaultValue) {
        try {
            return new Double(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gibt den Text des TextFields als Float zurueck. <br> Kann der Text nicht in einen Float gewandelt werden, wird
     * <code>defaultValue</code> zurueck gegeben.
     *
     * @param defaultValue
     * @return
     */
    public Float getTextAsFloat(Float defaultValue) {
        try {
            return new Float(getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
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
    @Override
    public void preventKeyStroke(String keyStrokeToPrevent) {
        if (StringUtils.isNotBlank(keyStrokeToPrevent)) {
            this.getInputMap(JComponent.WHEN_FOCUSED).put(
                    KeyStroke.getKeyStroke(keyStrokeToPrevent), "none");
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKTextSizeAware#getMaxChars()
     */
    @Override
    public int getMaxChars() {
        return maxChars;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKTextSizeAware#setMaxChars(int)
     */
    @Override
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

}
