/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 14:18:46
 */
package de.augustakom.hurrican.gui.customer;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;

/**
 * Frame fuer die Verwaltung von Transponder-Gruppen zu einem Kunden.
 */
public class TransponderGroupFrame extends AKJAbstractInternalFrame {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/customer/resources/TransponderGroupFrame.xml";

    private final Long kundeNo;

    public TransponderGroupFrame(Long kundeNo) {
        super(RESOURCE);
        this.kundeNo = kundeNo;
        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        TransponderGroupPanel transponderGroupPanel = new TransponderGroupPanel(kundeNo);
        this.setLayout(new BorderLayout());
        this.add(transponderGroupPanel, BorderLayout.CENTER);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void execute(String command) {
    }

}


