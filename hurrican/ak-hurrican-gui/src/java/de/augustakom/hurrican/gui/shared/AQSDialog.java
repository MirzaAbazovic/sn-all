/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 12:32:02
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.GeoId;


/**
 * Dialog, um die AQS einer best. Strasse anzuzeigen.
 *
 *
 */
public class AQSDialog extends AbstractServiceOptionDialog {

    private GeoId geoId = null;

    /**
     * Konstruktor mit Angabe der GeoId, deren Aderquerschnitte angezeigt werden sollen.
     *
     * @param geoId
     */
    public AQSDialog(GeoId geoId) {
        super(null);
        this.geoId = geoId;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("bekannte Leitungsdaten");
        AQSPanel aqsPanel = new AQSPanel();

        configureButton(CMD_CANCEL, "Ok", "Schliesst den Dialog", true, true);
        configureButton(CMD_SAVE, null, null, false, false);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(aqsPanel, BorderLayout.CENTER);

        aqsPanel.setModel(geoId);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
    }

}


