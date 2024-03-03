/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import static org.apache.commons.lang.StringUtils.*;

import java.util.regex.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKManageableComponent;

/**
 * AK-Implementierung eines JLabels.
 *
 *
 * @see javax.swing.JLabel
 */
public class AKJLabel extends JLabel implements AKManageableComponent {

    private static final long serialVersionUID = 6716054430550806642L;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    /**
     * Erzeugt ein 'leeres' Label.
     */
    public AKJLabel() {
        super();
    }

    /**
     * Erzeugt ein Label mit Text.
     *
     * @param text
     */
    public AKJLabel(String text) {
        super(text);
    }

    /**
     * Erzeugt ein Label mit Text. Zusaetzlich kann die Ausrichtung des Texts angegeben werden.
     *
     * @param text
     * @param horizontalAlignment
     */
    public AKJLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    /**
     * Erzeugt ein Image-Label.
     *
     * @param image
     */
    public AKJLabel(Icon image) {
        super(image);
    }

    /**
     * Erzeugt ein Image-Label mit Angabe der Ausrichtung.
     *
     * @param image
     * @param horizontalAlignment
     */
    public AKJLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    /**
     * Erzeugt ein Label mit Text und Image. Zusaetzlich kann die Ausrichtung des Labels angegeben werden.
     *
     * @param text
     * @param icon
     * @param horizontalAlignment
     */
    public AKJLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    /**
     * Uebergibt dem Frame die URL des zu verwendenden Icons.
     *
     * @param url URL des Icons.
     */
    public void setIcon(String url) {
        IconHelper helper = new IconHelper();
        ImageIcon icon = helper.getIcon(url);
        if (icon != null) {
            setIcon(icon);
        }
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
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        super.setVisible(x);
    }

    /**
     * converts all '\n' or "\n" in the XML declaration to an HTML formatted string, which makes the {@link AKJLabel}
     * multiline enabled.
     */
    public void convertCurrentTextToMultiline() {
        String orignText = this.getText();
        String br = "<br/>";
        /**
         *  \\n and \n is needed because of the XML-Parsing of the {@link de.augustakom.common.gui.swing.SwingFactory}
         */
        String newText = "<html>" + orignText.replaceAll(Matcher.quoteReplacement("\\n"), br).replaceAll("\n", br) + "</html>";
        setText(newText);
    }

    /**
     * converts any kind of String to multined HTML-formatted text, wiht an line break after the assigned chars per
     * line. See also {@link #convertCurrentTextToMultiline()}.
     *
     * @param charsPerLine maximum chars per line (line break will take place after the last space)
     */
    public void convertCurrentTextToMultiline(int charsPerLine) {
        StringBuilder sb = new StringBuilder();
        String textToFormat = this.getText();
        do {
            textToFormat = textToFormat.trim();
            if (textToFormat.length() <= charsPerLine) {
                sb.append(textToFormat);
                break;
            }
            String substring = textToFormat.substring(0, charsPerLine);
            int cutPos = (substring.contains(" ")) ? substring.lastIndexOf(' ') : substring.length();
            sb.append(substring.substring(0, cutPos)).append("\n");
            textToFormat = textToFormat.substring(cutPos);
        }
        while (isNotEmpty(textToFormat));
        setText(sb.toString());
        convertCurrentTextToMultiline();
    }
}
