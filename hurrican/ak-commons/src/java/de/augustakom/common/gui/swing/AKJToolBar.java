/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.beans.*;
import javax.swing.*;

/**
 * AK-Implementierung einer JToolBar.
 *
 *
 * @see javax.swing.JToolBar
 */
public class AKJToolBar extends JToolBar {

    /**
     * @see javax.swing.JToolBar()
     */
    public AKJToolBar() {
        super();
    }

    /**
     * @param orientation
     * @see javax.swing.JToolBar(int)
     */
    public AKJToolBar(int orientation) {
        super(orientation);
    }

    /**
     * @param name
     * @see javax.swing.JToolBar(String)
     */
    public AKJToolBar(String name) {
        super(name);
    }

    /**
     * @param name
     * @param orientation
     * @see javax.swing.JToolBar(String, int)
     */
    public AKJToolBar(String name, int orientation) {
        super(name, orientation);
    }

    /**
     * Factory method which creates the <code>JButton</code> for <code>Action</code> s added to the
     * <code>JToolBar</code>. The default name is empty if a <code>null</code> action is passed.
     * <p/>
     * <p/>
     * As of 1.3, this is no longer the preferred method for adding <code>Action</code> s to a <code>Container</code>.
     * Instead it is recommended to configure a control with an action using <code>setAction</code>, and then add that
     * control directly to the <code>Container</code>.
     *
     * @param a the <code>Action</code> for the button to be added
     * @return the newly created button
     * @see Action
     */
    @Override
    protected JButton createActionComponent(Action a) {
        String text = (a != null) ? (String) a.getValue(Action.NAME) : null;
        Icon icon = (a != null) ? (Icon) a.getValue(Action.SMALL_ICON) : null;
        boolean enabled = (a != null) ? a.isEnabled() : true;
        String tooltip = (a != null) ? (String) a.getValue(Action.SHORT_DESCRIPTION) : null;
        String accessibleName = (a != null) ? (String) a.getValue(Action.ACTION_COMMAND_KEY) : null;
        AKJButton b = new AKJButton(text, icon) {

            @Override
            protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
                PropertyChangeListener pcl = createActionChangeListener(this);
                if (pcl == null) {
                    pcl = super.createActionPropertyChangeListener(a);
                }
                return pcl;
            }
        };
        if (icon != null) {
            b.putClientProperty("hideActionText", Boolean.TRUE);
        }
        b.setHorizontalTextPosition(JButton.CENTER);
        b.setVerticalTextPosition(JButton.BOTTOM);
        b.setEnabled(enabled);
        b.setToolTipText(tooltip);
        b.addMouseListener(new AdministrationMouseListener());
        b.getAccessibleContext().setAccessibleName(accessibleName);
        return b;
    }
}
