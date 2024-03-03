/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.13
 */
package de.augustakom.common.gui.swing;

import java.util.*;

/**
 * AKActionGroup ermoeglicht die Gruppierung von <code>javax.swing.AbstractAction</code>, so dass diese z.B. in einem
 * Unter-Popup-Menu angezeigt werden.
 *
 *
 */
public class AKActionGroup {

    private List actions;
    private String label;

    public AKActionGroup(String label) {
        this.label = label;
    }

    public List getActions() {
        return actions;
    }

    public void addAction(AKAbstractAction action) {
        if (actions == null) {
            actions = new ArrayList();
        }
        actions.add(action);
    }

    public void addActionSeparator() {
        if (actions == null) {
            actions = new ArrayList();
        }
        actions.add(new ActionSeparator());
    }

    public String getLabel() {
        return label;
    }

    /**
     * Hilfsklasse, um einen Separator innerhalb einer ActionGroup zu erkennen.
     */
    public static class ActionSeparator {
    }

}
