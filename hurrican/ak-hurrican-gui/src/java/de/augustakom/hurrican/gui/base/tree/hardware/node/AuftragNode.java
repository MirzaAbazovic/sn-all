/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 15:39:52
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;


/**
 * Tree-Node fuer die Darstellung eines Auftrags.
 *
 *
 */
public class AuftragNode extends DynamicTreeNode {

    private final AuftragDaten auftragDaten;
    private final VerbindungsBezeichnung verbindungsBezeichnung;


    public AuftragNode(DynamicTree tree, AuftragDaten auftragDaten, VerbindungsBezeichnung verbindungsBezeichnung) {
        super(tree, false);
        setAllowsChildren(false);
        this.auftragDaten = auftragDaten;
        this.verbindungsBezeichnung = verbindungsBezeichnung;
    }


    public AuftragDaten getAuftragDaten() {
        return auftragDaten;
    }


    @Override
    public void defaultAction() {
        AuftragDataFrame.openFrame(auftragDaten);
    }


    @Override
    public String getDisplayName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Auftrag: ");
        stringBuilder.append(auftragDaten.getAuftragId());
        stringBuilder.append(" (");
        stringBuilder.append(verbindungsBezeichnung != null ? verbindungsBezeichnung.getVbz() : "?");
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String getIcon() {
        if ((auftragDaten != null) && auftragDaten.isAuftragActive()) {
            return IMAGE_BASE + "auftrag.gif";
        }
        return IMAGE_BASE + "delete.gif";
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
