/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2008 14:01:31
 */
package de.augustakom.hurrican.gui.utils;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.model.cc.gui.GUIDefinition;


/**
 * Hilfsklasse, um aus Objekten des Typs <code>GUIDefinition</code> die eigentlichen GUI-Objekte (Swing-Objekte) zu
 * erzeugen.
 */
public class GUIDefinitionHelper {

    private static final Logger LOGGER = Logger.getLogger(GUIDefinitionHelper.class);

    /**
     * Diese Funktion erstellt aus den uebergebenen GUIDefinition-Objekten Objekte vom Typ
     * <code>AKAbstractAction</code>.
     *
     * @param guiDefinitions Liste mit den GUI-Definitionen
     * @return Liste mit den aus den Definitionen heraus erzeugten Actions
     */
    public static List<AKAbstractAction> createActions(List<GUIDefinition> guiDefinitions) {
        if (guiDefinitions != null) {
            List<AKAbstractAction> actions = new ArrayList<>();
            for (GUIDefinition guiDef : guiDefinitions) {
                try {
                    Class<?> clazz = Class.forName(guiDef.getClazz());
                    if (AKAbstractAction.class.isAssignableFrom(clazz)) {
                        AKAbstractAction action = (AKAbstractAction) clazz.newInstance();
                        action.setActionCommand(guiDef.getName());
                        action.setName(guiDef.getText());
                        action.setTooltip(guiDef.getTooltip());
                        action.setIcon(guiDef.getIcon());
                        action.putValue(AKAbstractAction.ADD_SEPARATOR, guiDef.getAddSeparator());

                        actions.add(action);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            return actions;
        }

        return Collections.emptyList();
    }

    /**
     * Diese Funktion erstellt aus den uebergebenen GUIDefinition-Objekten Objekte vom Typ <code>AKJPanel</code>.
     *
     * @param guiDefinitions Liste mit den GUI-Definitionen
     * @return Liste mit den aus den Definitionen heraus erzeugten Panels
     */
    public static List<AKJPanel> createPanels(List<GUIDefinition> guiDefinitions) {
        List<AKJPanel> panels = new ArrayList<>();
        if (guiDefinitions != null) {
            for (GUIDefinition guiDef : guiDefinitions) {
                try {
                    Class<?> clazz = Class.forName(guiDef.getClazz());
                    if (AKJPanel.class.isAssignableFrom(clazz)) {
                        AKJPanel panel = (AKJPanel) clazz.newInstance();
                        panel.getAccessibleContext().setAccessibleName(guiDef.getName());
                        panel.getAccessibleContext().setAccessibleDescription(guiDef.getText());
                        panel.setName(guiDef.getName());
                        panels.add(panel);
                    }
                    else {
                        LOGGER.warn("Class is not of expected type! Class: " + clazz + " - Expected: " + AKJPanel.class.getName());
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return panels;
    }

}


