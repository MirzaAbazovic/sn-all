/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;

import de.augustakom.common.gui.swing.AKJPanel;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;

/**
 * Panel zur Anzeige von ABBM-TR Details.
 */
public class MeldungDetailAbbmTrPanel extends AbstractMeldungDetailPanel<AbbruchmeldungTechnRessource> {

    private static final long serialVersionUID = -4351691356618922082L;

    public MeldungDetailAbbmTrPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/MeldungDetailAbbmTrPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        return new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
    }

}
