/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;


import java.awt.*;

import de.augustakom.common.gui.swing.AKJPanel;
import de.mnet.wbci.model.StornoAnfrage;

/**
 * Detail-Panel fuer die Darstellung von Storno-Details.
 */
public class RequestDetailStornoPanel extends AbstractRequestDetailPanel<StornoAnfrage> {

    private static final long serialVersionUID = 6356713034813077655L;

    public RequestDetailStornoPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/RequestDetailStornoPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        return new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
    }

}
