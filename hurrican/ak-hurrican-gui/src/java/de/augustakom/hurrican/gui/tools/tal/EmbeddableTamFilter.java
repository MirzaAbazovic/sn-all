/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2016
 */
package de.augustakom.hurrican.gui.tools.tal;

import static java.awt.GridBagConstraints.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import com.google.common.collect.Maps;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJToggleButton;
import de.augustakom.common.gui.swing.SwingFactory;

class EmbeddableTamFilter  {

    private final List<String> toggleButtonCommands;
    private final AKJComboBox additionalFilterComponent;
    private final ActionListener actionListener;
    private final SwingFactory swingFactory;

    EmbeddableTamFilter(List<String> toggleButtonCommands,
            AKJComboBox additionalFilterComponent,
            ActionListener actionListener,
            SwingFactory swingFactory) {
        this.toggleButtonCommands = toggleButtonCommands;
        this.additionalFilterComponent = additionalFilterComponent;
        this.actionListener = actionListener;
        this.swingFactory = swingFactory;
    }

    AKJPanel createFilterPanel()
    {
        AKJPanel filtersPnl = new AKJPanel(new GridBagLayout(), swingFactory.getText("filters"));
        Map<String, AKJToggleButton> toggleButtons = createToggleButtons();
        int x = 0;
        for (String toggleButtonCommand : toggleButtonCommands) {
            AKJToggleButton button = toggleButtons.get(toggleButtonCommand);
            filtersPnl.add(button, GBCFactory.createGBC(  0,  0, RELATIVE,  0, 1, 1, GridBagConstraints.NONE));
        }
        filtersPnl.add(new AKJPanel(),       GBCFactory.createGBC(  1,  0, RELATIVE,  0, 1, 1, GridBagConstraints.NONE));

        if (additionalFilterComponent != null)
        {
            filtersPnl.add(additionalFilterComponent, GBCFactory.createGBC(1,  0, 0,  1, 3, 1, GridBagConstraints.HORIZONTAL));
        }

        return filtersPnl;
    }

    private Map<String, AKJToggleButton> createToggleButtons()
    {
        Map<String, AKJToggleButton> toggleButtons = Maps.newHashMap();
        ButtonGroup btnGroup = new ButtonGroup();

        for (String command: toggleButtonCommands) {
            AKJToggleButton button = swingFactory.createToggleButton(command, actionListener, false, btnGroup);
            toggleButtons.put(command, button);
        }

        return toggleButtons;
    }
}

