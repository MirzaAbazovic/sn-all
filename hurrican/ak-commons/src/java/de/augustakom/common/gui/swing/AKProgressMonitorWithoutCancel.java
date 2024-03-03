/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2016
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.lang.reflect.*;
import javax.swing.*;

public class AKProgressMonitorWithoutCancel extends ProgressMonitor {
    public AKProgressMonitorWithoutCancel(Component component, Object o, String s, int i, int i1) {
        super(component, o, s, i, i1);
        hideCancelButton();
    }

    private void hideCancelButton() {
        try {
            final Field field = ProgressMonitor.class.getDeclaredField("cancelOption");
            field.setAccessible(true);
            field.set(this, new Object[0]);
        }
        catch (Exception ex) {
            // ignore
        }
    }
}
