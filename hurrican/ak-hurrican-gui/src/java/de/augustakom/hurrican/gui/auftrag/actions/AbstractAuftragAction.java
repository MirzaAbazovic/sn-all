/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2004 16:36:07
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;


/**
 * Basis-Klasse fuer alle Actions, die das Auftrags-Frame benoetigen.
 *
 *
 */
public abstract class AbstractAuftragAction extends AbstractServiceAction {

    /**
     * Aktualisiert das Auftrags-Frame.
     */
    protected void refreshFrame() {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AuftragDataFrame dataFrame = getAuftragDataFrame();
        if (dataFrame != null) {
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
            dataFrame.refresh();
        }
    }

    /**
     * Gibt das Auftrags-Frame zurueck
     *
     * @return das aktuelle AuftragDataFrame oder <code>null</code>.
     */
    protected AuftragDataFrame getAuftragDataFrame() {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(AuftragDataFrame.class);
        AuftragDataFrame dataFrame = null;
        if (frames != null && frames.length == 1) {
            dataFrame = (AuftragDataFrame) frames[0];
        }

        return dataFrame;
    }

    /**
     * Ueberprueft das AuftragsDaten-Frame auf Aenderungen.
     *
     * @return true/false abhaengig davon, ob Aenderungen vorhanden sind.
     */
    protected boolean hasChanges() {
        AuftragDataFrame f = getAuftragDataFrame();
        return (f != null) ? f.hasModelChanged() : false;
    }

}


