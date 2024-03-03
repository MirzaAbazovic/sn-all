/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;


import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;

/**
 * Detail-Panel fuer die Darstellung von TV-Details.
 */
public class RequestDetailTvPanel extends AbstractRequestDetailPanel<TerminverschiebungsAnfrage> {

    private static final long serialVersionUID = -4610065736445500490L;

    public static final String NEUER_KWT = "neuer.kwt";

    private AKJDateComponent dcNeuerKwt;

    public RequestDetailTvPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/RequestDetailTvPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblNeuerKwt = getSwingFactory().createLabel(NEUER_KWT);
        dcNeuerKwt = getSwingFactory().createDateComponent(NEUER_KWT, false);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblNeuerKwt   , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(dcNeuerKwt    , GBCFactory.createGBC( 10,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(100,100, 3, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        dcNeuerKwt.setDateTime(wbciRequest.getTvTermin().atStartOfDay());
    }

}
