/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import javax.swing.*;

import de.augustakom.common.gui.swing.SwingFactory;

/**
 * TabbedPane für CPS-Transaktionen
 *
 *
 */
public class CPSTxTabbedPane extends JTabbedPane {
    private final static String RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTransactionPanel.xml";

    private CPSTxFieldPanel fieldPanel = null;
    private CPSTxRequestPanel requestPanel = null;
    private CPSTxResponsePanel responsePanel = null;
    private CPSTxLogPanel logPanel = null;
    private CPSTxOrderPanel orderPanel = null;

    /**
     * Default-Konstruktor
     */
    public CPSTxTabbedPane() {
        fieldPanel = new CPSTxFieldPanel();
        requestPanel = new CPSTxRequestPanel();
        responsePanel = new CPSTxResponsePanel();
        logPanel = new CPSTxLogPanel();
        orderPanel = new CPSTxOrderPanel();

        addTab(SwingFactory.getInstance(RESOURCE).getText(CPSTxDetailPanel.FIELDSDETAILS), fieldPanel);
        addTab(SwingFactory.getInstance(RESOURCE).getText(CPSTxDetailPanel.REQUEST), requestPanel);
        addTab(SwingFactory.getInstance(RESOURCE).getText(CPSTxDetailPanel.RESPONSE), responsePanel);
        addTab(SwingFactory.getInstance(RESOURCE).getText(CPSTxDetailPanel.LOG), logPanel);
        addTab(SwingFactory.getInstance(RESOURCE).getText(CPSTxDetailPanel.ORDERS), orderPanel);
    }

    /**
     * @return the fieldPanel
     */
    public CPSTxFieldPanel getFieldPanel() {
        return fieldPanel;
    }

    /**
     * @return the requestPanel
     */
    public CPSTxRequestPanel getRequestPanel() {
        return requestPanel;
    }

    /**
     * @return the responsePanel
     */
    public CPSTxResponsePanel getResponsePanel() {
        return responsePanel;
    }

    /**
     * @return the logPanel
     */
    public CPSTxLogPanel getLogPanel() {
        return logPanel;
    }

    /**
     * @return the orderPanel
     */
    public CPSTxOrderPanel getOrderPanel() {
        return orderPanel;
    }
}
