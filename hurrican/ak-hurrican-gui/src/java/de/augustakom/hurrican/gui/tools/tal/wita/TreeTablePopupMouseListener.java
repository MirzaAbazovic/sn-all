/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2011 09:55:52
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.netbeans.swing.outline.Outline;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJPopupMenu;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.common.tools.lang.BooleanTools;

/**
 * Klasse um Popups fuer eine TreeTable anzuzeigen
 */
public class TreeTablePopupMouseListener extends MouseAdapter {

    public static class PopupSeparator {
    }

    public TreeTablePopupMouseListener(AKAbstractAction... actions) {
        for (AKAbstractAction action : actions) {
            addAction(action);
        }
    }

    private final List<Object> actions = new ArrayList<Object>();

    public void addAction(AKAbstractAction action) {
        actions.add(action);
    }

    public void addSeparator() {
        actions.add(new PopupSeparator());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    protected void showPopup(MouseEvent e) {
        Outline outline = null;
        if (e.getSource() instanceof Outline) {
            outline = (Outline) e.getSource();
        }
        else {
            return;
        }

        AKJPopupMenu popup = new AKJPopupMenu();

        for (Object o : actions) {
            if (o instanceof AKAbstractAction) {
                AKAbstractAction toAdd = (AKAbstractAction) o;
                boolean addSep = BooleanTools.nullToFalse((Boolean) toAdd.getValue(AKAbstractAction.ADD_SEPARATOR));
                if (addSep) {
                    popup.addSeparator();
                }

                toAdd.putValue(AKAbstractAction.OBJECT_4_ACTION, outline);
                toAdd.putValue(AKAbstractAction.ACTION_SOURCE, e);
                JMenuItem mi = popup.add(toAdd);
                if (mi != null) {
                    AdministrationMouseListener adminML = new AdministrationMouseListener();
                    mi.addMouseListener(adminML);
                    mi.addMenuKeyListener(adminML);
                }
            }
            else if (o instanceof PopupSeparator) {
                popup.addSeparator();
            }
        }

        if (popup.getComponentCount() > 0) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @SuppressWarnings("unused")
    private Object getSelectedObject(Outline table) {
        return table.getSelectionModel();
    }

}
