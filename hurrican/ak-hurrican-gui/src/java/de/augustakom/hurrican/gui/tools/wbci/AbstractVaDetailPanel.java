/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.mnet.wbci.model.WbciEntity;

/**
 * Abstrakte Basis-Klasse fuer Panels, die Details zu einer WBCI Nachricht darstellen koennen.
 */
public abstract class AbstractVaDetailPanel extends AbstractServicePanel {

    private static final long serialVersionUID = -8922648299700183348L;

    public AbstractVaDetailPanel(String resource) {
        super(resource);
    }


    /**
     * Muss von den Sub-Klassen ueberschrieben werden, um weitere Details auf der GUI darzustellen. <br/> Das Panel
     * sollte moeglichst einen 'Titled-Border' besitzen, um eine durchgaengige Optik zu gewaehrleisten.
     *
     * @return das Detail-Panel
     */
    protected abstract AKJPanel buildDetailPanel();

    protected abstract void setVaModel(WbciEntity wbciEntity);

    protected abstract void showVaDetails();
}
