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
public class CPSTxRequestPanel extends AKJPanel implements CPSTxObserver {
    private XmlTextPane soDataTextPane = null;
    private XmlTextPane requestDataTextPane = null;

    private static final String SO_DATA = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.serviceOrderData";
    private static final String REQUEST_DATA = "de.augustakom.hurrican.model.cc.cps.CPSTransaction.requestData";

    /*
     * Default-Konstruktor
     */
    public CPSTxRequestPanel() {
        super();
        setLayout(new GridLayout(1, 1));
        soDataTextPane = new XmlTextPane();
        requestDataTextPane = new XmlTextPane();

        JScrollPane soDataScrollPane = new CPSTxScrollPane(soDataTextPane, SwingFactory.getInstance(CPSTxPanel.RESOURCE).getText(SO_DATA));
        JScrollPane requestDataScrollPane = new CPSTxScrollPane(requestDataTextPane, SwingFactory.getInstance(CPSTxPanel.RESOURCE).getText(REQUEST_DATA));

        this.add(soDataScrollPane);
        this.add(requestDataScrollPane);
    }

    /**
     * @return the soDataTextPane
     */
    public XmlTextPane getSoDataTextPane() {
        return soDataTextPane;
    }

    /**
     * @return the requestDataTextPane
     */
    public XmlTextPane getRequestDataTextPane() {
        return requestDataTextPane;
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
     */
    public void update(CPSTxObservable observable) {
        CPSTxObserverHelper.cleanFields(observable, this);
    }
}
