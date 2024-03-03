/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;

/**
 * TreeNode fuer die Darstellung einer WBCI Meldung.
 */
public class VaMeldungTreeNode extends AbstractVaTreeNode {

    private static final long serialVersionUID = 1372239335529656320L;

    private final Meldung wbciMeldung;

    public VaMeldungTreeNode(DynamicTree tree, Meldung wbciMeldung) {
        super(tree, false);
        this.wbciMeldung = wbciMeldung;
        setUserObject(wbciMeldung);
    }

    @Override
    public String getDisplayName() {
        return wbciMeldung.getTyp().getShortName();
    }

    @Override
    protected IOType getIoType() {
        return wbciMeldung.getIoType();
    }

}
