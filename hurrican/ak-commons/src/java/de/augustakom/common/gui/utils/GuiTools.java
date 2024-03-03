/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2004 09:53:20
 */
package de.augustakom.common.gui.utils;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.iface.AKCleanableComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.SwingFactory;


/**
 * Hilfsklasse fuer GUI-Operationen.
 *
 *
 */
public class GuiTools {

    /**
     * Setzt alle GUI-Komponenten des Arrays <code>components</code> auf enabled = <code>enable</code>.
     *
     * @param components     GUI-Komponenten, die auf enabled/disabled gesetzt werden sollen
     * @param enable         Flag, ob die Komponenten auf enabled (true) oder disabled (false) gesetzt werden sollen.
     * @param noticeTextComp Ueber dieses Flag wird gesteuert, ob bei Objekten des Typs <code>(J)TextComponent</code>
     *                       die Methode setEnabled oder setEditable aufgerufen werden soll. <br> Bei <code>true</code>
     *                       wird setEditable, sonst setEnabled aufgerufen.
     */
    public static void enableComponents(Component[] components, boolean enable, boolean noticeTextComp) {
        if ((components != null) && (components.length > 0)) {
            for (Component comp : components) {
                if (noticeTextComp && (comp instanceof JTextComponent)) {
                    ((JTextComponent) comp).setEditable(enable);
                }
                else if (noticeTextComp && (comp instanceof TextComponent)) {
                    ((TextComponent) comp).setEditable(enable);
                }
                else {
                    comp.setEnabled(enable);
                }
            }
        }
    }

    /**
     * Setzt alle Felder von Container (und Sub-Containers) auf 'enable'.
     *
     * @param container
     * @param enable
     *
     */
    public static void enableFields(Container container, boolean enable) {
        if (container != null) {
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component comp = container.getComponent(i);
                comp.setEnabled(enable);
            }
        }
    }

    /**
     * Setzt alle Felder eines Container oder Sub-Containern auf 'enable'. Derzeit anwendbar bei AKJTextField,
     * AKJDateComponent, AKJCheckBox, AKJComboBox, AKJTable.
     *
     * @param container uebergebener Container, der die zu aktivierenden Components enthaelt
     * @param enable    boolscher Wert, der Status der Components festlegt
     *
     */
    public static void enableContainerComponents(Container container, boolean enable) {
        if (container != null) {
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component comp = container.getComponent(i);

                if (comp instanceof AKJTextField) {
                    ((AKJTextField) comp).setEditable(enable);
                }
                else if (comp instanceof AKJFormattedTextField) {
                    ((AKJFormattedTextField) comp).setEditable(enable);
                }
                else if (comp instanceof AKJDateComponent) {
                    ((AKJDateComponent) comp).setUsable(enable);
                }
                else if (comp instanceof AKJCheckBox) {
                    ((AKJCheckBox) comp).setEnabled(enable);
                }
                else if (comp instanceof AKJComboBox) {
                    ((AKJComboBox) comp).setEnabled(enable);
                }
                else if (comp instanceof AKJTable) {
                    ((AKJTable) comp).setEnabled(enable);
                }
                else if (comp instanceof AKReferenceField) {
                    ((AKReferenceField) comp).setEnabled(enable);
                }
                else if (comp instanceof JTextComponent) {
                    ((JTextComponent) comp).setEnabled(enable);
                }
                else if (comp instanceof Container) {
                    enableContainerComponents((Container) comp, enable);
                }
            }
        }
    }

    /**
     * Entsperrt die angegebenen Komponenten. <br> Objekte, die von (J)TextComponent ableiten, werden auf editable=true
     * gesetzt. Alle anderen Objekte werden auf enabled=true gesetzt.
     *
     * @param components
     */
    public static void unlockComponents(Component[] components) {
        enableComponents(components, true, true);
    }

    /**
     * Sperrt die angegebenen Komponenten. <br> Objekte, die von (J)TextComponent ableiten, werden auf editable=false
     * gesetzt. Alle anderen Objekte werden auf enabled=false gesetzt.
     *
     * @param components
     */
    public static void lockComponents(Component[] components) {
        enableComponents(components, false, true);
    }

    /**
     * Setzt alle Komponenten des Arrays auf enabled=true.
     *
     * @param components
     */
    public static void enableComponents(Component[] components) {
        enableComponents(components, true, false);
    }

    /**
     * Setzt alle Komponenten des Arrays auf enabled=false.
     *
     * @param components
     */
    public static void disableComponents(Component[] components) {
        enableComponents(components, false, false);
    }

    /**
     * 'Loescht' den Inhalt aller Felder, die in dem Container <code>container</code> oder dessen Sub-Containern
     * vorhanden sind.
     *
     * @param container Container, dessen Childs 'geloescht' werden sollen
     * @param except    optionale Angabe der {@link java.awt.Component}s, die nicht 'geloescht' werden sollen
     */
    public static void cleanFields(Container container, Component... except) {
        if (container != null) {
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component comp = container.getComponent(i);

                boolean clean = (except == null || !ArrayUtils.contains(except, comp));
                if (clean) {
                    if (comp instanceof AKCleanableComponent) {
                        ((AKCleanableComponent) comp).clean();
                    }
                    else if (comp instanceof AKJDateComponent) {
                        ((AKJDateComponent) comp).setDate(null);
                    }
                    else if (comp instanceof AKJFormattedTextField) {
                        ((AKJFormattedTextField) comp).setValue(null);
                        ((AKJFormattedTextField) comp).setText(null);
                    }
                    else if (comp instanceof JTextComponent) {
                        ((JTextComponent) comp).setText(null);
                    }
                    else if (comp instanceof AKJCheckBox) {
                        ((AKJCheckBox) comp).setSelected(false);
                    }
                    else if (comp instanceof AKJComboBox) {
                        ((AKJComboBox) comp).setSelectedIndex(-1);
                    }
                    else if (comp instanceof AKReferenceField) {
                        ((AKReferenceField) comp).clearReference();
                    }
                    else if (comp instanceof Container) {
                        cleanFields((Container) comp, except);
                    }
                }
            }
        }
    }

    /**
     * Aendert die Textfarbe der Text-Komponente <code>comp</code>. In der Map <code>keyColorMap</code> wird nach dem
     * Key <code>keyValue</code> gesucht. Ist der zugehoerige Wert vom Typ <code>Color</code> wird diese Color als
     * Hintergrundfarbe verwendet - sonst wird <code>defaultColor</code> verwendet.
     *
     * @param comp         zu aendernde Text-Komponente
     * @param keyValue     Wert nach dem in der Map gesucht werden soll
     * @param keyColorMap  Map mit den Key-Color Zuweisungen
     * @param defaultColor Standard-Farbe, falls der Key in der Map nicht vorhanden ist.
     */
    public static void switchForegroundColor(JTextComponent comp, Object keyValue, Map keyColorMap, Color defaultColor) {
        if (comp != null) {
            Color color2Use = null;
            if ((keyValue != null) && (keyColorMap != null)) {
                if (defaultColor == null) {
                    defaultColor = new AKJTextField().getBackground();
                }

                Object color = keyColorMap.get(keyValue);
                if (color instanceof Color) {
                    color2Use = (Color) color;
                }
                else {
                    color2Use = defaultColor;
                }
            }

            if (color2Use == null) {
                color2Use = Color.black;
            }
            comp.setForeground(color2Use);
        }
    }

    /**
     * Fuegt dem PopupMenu des TextFields eine weitere Action hinzu.
     *
     * @param textComp     TextComponent, deren PopupMenu erweitert werden soll
     * @param toAdd        Action, die dem PopupMenu hinzugefuegt werden soll
     * @param addSeperator Flag, ob VOR der neuen Action ein Seperator aufgenommen werden soll.
     */
    public static void addAction2ComponentPopupMenu(JTextComponent textComp, Action toAdd, boolean addSeperator) {
        if (toAdd != null) {
            JPopupMenu popup = new JPopupMenu();
            SwingFactory.addCCPActions2PopupMenu(textComp, popup);

            if (addSeperator) {
                popup.addSeparator();
            }

            popup.insert(toAdd, popup.getComponentCount());
            textComp.setComponentPopupMenu(popup);
        }
    }

    /**
     * @param textComp
     * @param toAdd
     * @param mouseListenerForItem
     * @param addSeperator
     * @see {@link GuiTools#addAction2ComponentPopupMenu(JTextComponent, Action, boolean)} Zusaetzlich kann noch ein
     * MouseListener fuer das zu erzeugende MenuItem angegeben werden.
     */
    public static void addAction2ComponentPopupMenu(JTextComponent textComp, AKAbstractAction toAdd,
            MouseListener mouseListenerForItem, boolean addSeperator) {
        if (toAdd != null) {
            JPopupMenu popup = new JPopupMenu();
            SwingFactory.addCCPActions2PopupMenu(textComp, popup);

            if (addSeperator) {
                popup.addSeparator();
            }

            AKJMenuItem menuItem = new AKJMenuItem(toAdd);
            menuItem.addMouseListener(mouseListenerForItem);
            menuItem.setParentClassName(toAdd.getParentClassName());

            popup.insert(menuItem, popup.getComponentCount());
            textComp.setComponentPopupMenu(popup);
        }
    }

    /**
     * Ermittelt aus einer ButtonGroup den aktuell selektierten (Radio!)Button.
     *
     * @param bg ButtonGroup
     * @return selektierter AKJRadioButton oder <code>null</code>.
     */
    public static AKJRadioButton getSelectedRadioButton(ButtonGroup bg) {
        if (bg != null) {
            ButtonModel model = bg.getSelection();
            if ((model != null) && StringUtils.isNotBlank(model.getActionCommand())) {
                String actionCmd = model.getActionCommand();
                Enumeration<AbstractButton> buttons = bg.getElements();
                if (buttons != null) {
                    while (buttons.hasMoreElements()) {
                        AbstractButton button = buttons.nextElement();
                        if ((button instanceof AKJRadioButton) &&
                                StringUtils.equals(actionCmd, button.getActionCommand())) {
                            return (AKJRadioButton) button;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Erzeugt ein Panel fuer die Darstellung einer Linie. <br> Die Groesse kann ueber den Parameter
     * <code>prefSize</code> angegeben werden.
     *
     * @param prefSize
     * @return
     */
    public static AKJPanel createLinePanel(Dimension prefSize) {
        AKJPanel line = new AKJPanel();
        line.setOpaque(false);
        line.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        line.setPreferredSize(prefSize);
        return line;
    }
}
