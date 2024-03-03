/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.xml.XmlTextPane;

/**
 *
 */
public class CPSTxResponsePanel extends AKJPanel implements CPSTxObserver {
    private XmlTextPane responseTextPane = null;
    private static final String RESPONSE_DATA = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.responseData";

    /*
     * Default-Konstruktor
     */
    public CPSTxResponsePanel() {
        super();
        setLayout(new GridLayout(1, 1));
        responseTextPane = new XmlTextPane();
        JScrollPane responseDataScrollPane = new CPSTxScrollPane(responseTextPane, SwingFactory.getInstance(CPSTxPanel.RESOURCE).getText(RESPONSE_DATA));
        this.add(responseDataScrollPane);
    }

    /**
     * @return the responseTextPane
     */
    public XmlTextPane getResponseTextPane() {
        return responseTextPane;
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
     */
    public void update(CPSTxObservable observable) {
        CPSTxObserverHelper.cleanFields(observable, this);
    }
}
