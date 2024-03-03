/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2006 11:19:00
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;


/**
 * Dialog zur Konfiguration eines Endgeraets.
 *
 *
 */
public class EGConfigurationDialog extends AbstractServiceOptionDialog {

    private Long eg2AuftragId = null;
    private EGConfigurationPanel configPanel = null;

    /**
     * Konstruktor mit Angabe der EG-2-Auftrag Mapping-ID auf die sich die Konfiguration bezieht.
     *
     * @param eg2AuftragId
     */
    public EGConfigurationDialog(Long eg2AuftragId) {
        super(null, true, true);
        this.eg2AuftragId = eg2AuftragId;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Endgerät konfigurieren");

        configPanel = new EGConfigurationPanel(eg2AuftragId);
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(configPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            configPanel.saveModel();
            prepare4Close();
            setValue(configPanel.getModel());
        }
        catch (AKGUIException e) {
            MessageHelper.showErrorDialog(this, e);
        }
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
    @Override
    public void update(Observable o, Object arg) {
    }

}


