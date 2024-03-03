/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import javax.swing.*;

/**
 *
 */
public class CPSTxScrollPane extends JScrollPane {

    /**
     * @param view
     * @param title
     */
    public CPSTxScrollPane(Component view, String title) {
        super(view);
        this.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder(title),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                        this.getBorder()
                )
        );

        this.setPreferredSize(new Dimension(350, 250));
    }
}
