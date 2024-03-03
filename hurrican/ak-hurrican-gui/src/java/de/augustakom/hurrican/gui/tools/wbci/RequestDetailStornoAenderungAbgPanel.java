/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;


import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;

/**
 * Detail-Panel fuer die Darstellung von Storno-Details.
 */
public class RequestDetailStornoAenderungAbgPanel extends AbstractRequestDetailPanel<StornoAenderungAbgAnfrage> {

    private static final long serialVersionUID = 6356713034813077655L;

    public static final String STORNO_GRUND = "storno.grund";

    private AKJTextArea taStornoGrund;

    public RequestDetailStornoAenderungAbgPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/RequestDetailStornoAenderungAbgPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblStornoGrund = getSwingFactory().createLabel(STORNO_GRUND);

        taStornoGrund = getSwingFactory().createTextArea(STORNO_GRUND, false);
        AKJScrollPane spStornoGrund = new AKJScrollPane(taStornoGrund);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblStornoGrund, GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(spStornoGrund , GBCFactory.createGBC(100,100, 2, 0, 1, 2, GridBagConstraints.BOTH));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        taStornoGrund.setText(wbciRequest.getStornoGrund());
    }

}
