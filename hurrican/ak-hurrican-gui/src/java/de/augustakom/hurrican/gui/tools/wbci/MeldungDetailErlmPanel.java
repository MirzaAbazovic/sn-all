/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.mnet.wbci.model.Erledigtmeldung;

/**
 * Panel zur Anzeige von ERLM Details.
 */
public class MeldungDetailErlmPanel extends AbstractMeldungDetailPanel<Erledigtmeldung> {

    private static final long serialVersionUID = 7188709991758240896L;

    public static final String WECHSELTERMIN = "wechseltermin";

    private AKJDateComponent dcWechseltermin;

    public MeldungDetailErlmPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/MeldungDetailErlmPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblWechseltermin = getSwingFactory().createLabel(WECHSELTERMIN);
        dcWechseltermin = getSwingFactory().createDateComponent(WECHSELTERMIN, false);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblWechseltermin  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(dcWechseltermin   , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel()    , GBCFactory.createGBC(100,100, 3, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        if (meldung.getWechseltermin() != null) {
            dcWechseltermin.setDateTime(meldung.getWechseltermin().atStartOfDay());
        }
        else {
            dcWechseltermin.setDateTime(null);
        }
    }
}
