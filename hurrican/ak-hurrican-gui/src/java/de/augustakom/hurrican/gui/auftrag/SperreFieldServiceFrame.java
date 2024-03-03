/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2005 09:03:02
 */
package de.augustakom.hurrican.gui.auftrag;


import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;


/**
 * Frame fuer die Bearbeitung der SCT-Sperren.
 *
 *
 */
public class SperreFieldServiceFrame extends AbstractDataFrame {

    // FIXME (HUR-15 / HUR-31) delete me - wenn manuell auszufuehrende Sperren zukuenftig ueber Bauauftraege laufen

    /**
     * Default-Konstruktor.
     */
    public SperreFieldServiceFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("aktuelle Sperren Field-Service");
        configureButton(CMD_SAVE, "Schliessen", "Schliesst das Fenster", true, true);

        SperreFieldServicePanel sctPanel = new SperreFieldServicePanel();
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(sctPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
        this.dispose();
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
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

}


