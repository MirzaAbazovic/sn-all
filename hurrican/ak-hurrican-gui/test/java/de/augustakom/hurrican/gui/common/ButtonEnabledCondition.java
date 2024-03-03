/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.04.2010 15:22:42
 */
package de.augustakom.hurrican.gui.common;

import static org.fest.swing.edt.GuiActionRunner.*;
import static org.fest.swing.timing.Pause.*;

import javax.swing.*;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Timeout;

public class ButtonEnabledCondition extends Condition {
    private static final long DEFAULT_DELAY = 30000;

    public static void waitForButtonEnabled(JButton button) {
        waitForButtonEnabled(button, DEFAULT_DELAY);
    }

    public static void waitForButtonEnabled(JButton button, Timeout timeout) {
        if (timeout == null) {
            throw new NullPointerException("The given timeout should not be null");
        }
        waitForButtonEnabled(button, timeout.duration());
    }

    public static void waitForButtonEnabled(JButton button, long timeout) {
        pause(new ButtonEnabledCondition(button), timeout);
    }

    private final JButton button;

    public ButtonEnabledCondition(JButton button) {
        super("Search Button enabled");
        this.button = button;
    }

    @Override
    public boolean test() {
        return execute(new GuiQuery<Boolean>() {
            @Override
            public Boolean executeInEDT() {
                return button.isEnabled();
            }
        });
    }
}
