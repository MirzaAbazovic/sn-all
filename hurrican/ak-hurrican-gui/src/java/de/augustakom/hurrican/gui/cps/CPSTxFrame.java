/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;

/**
 * Frame zur Darstellung der CPS-Transaktionen.
 *
 *
 */
public class CPSTxFrame extends AbstractInternalServiceFrame implements InternalFrameListener {

    private final static String TITLE = "title";
    private final static String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTransactionFrame.xml";
    private CPSTxPanel cpsTxPanel = null;

    public CPSTxFrame() {
        super(RESOURCE);
        addInternalFrameListener(this);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));
        cpsTxPanel = new CPSTxPanel();
        this.setLayout(new BorderLayout());
        this.add(cpsTxPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameActivated(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameClosed(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameClosing(InternalFrameEvent e) {
        cpsTxPanel.cancel();
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameOpened(InternalFrameEvent e) {
        cpsTxPanel.loadData();
    }
}
