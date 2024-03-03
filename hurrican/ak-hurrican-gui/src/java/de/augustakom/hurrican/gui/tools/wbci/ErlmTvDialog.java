/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.time.*;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.gui.tools.wbci.helper.MeldungServiceHelper;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;

/**
 * Dialog sending new ERLM meldung for a Terminverschiebung.
 *
 *
 */
public class ErlmTvDialog extends AbstractErlmDialog<TerminverschiebungsAnfrage> {
    private static final long serialVersionUID = -8574007410402337401L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/ErlmTvDialog.xml";

    private static final String OLD_TERMIN = "old.termin";
    private static final String NEW_TERMIN = "new.termin";

    private AKJDateComponent dcOldTermin;
    private AKJDateComponent dcNewTermin;

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}.
     *
     * @param tv
     */
    public ErlmTvDialog(TerminverschiebungsAnfrage tv) {
        super(RESOURCE, tv);
    }

    @Override
    protected AKJPanel createDetailPanel() {
        AKJLabel lblOldTermin = getSwingFactory().createLabel(OLD_TERMIN);
        dcOldTermin = getSwingFactory().createDateComponent(OLD_TERMIN, false);

        AKJLabel lblNewTermin = getSwingFactory().createLabel(NEW_TERMIN);
        dcNewTermin = getSwingFactory().createDateComponent(NEW_TERMIN, false);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        dtlPnl.add(lblOldTermin, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(dcOldTermin, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(lblNewTermin, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(dcNewTermin, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        return dtlPnl;
    }

    @Override
    public final void loadData() {
        super.loadData();
        dcOldTermin.setDate(Date.from(wbciRequest.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dcNewTermin.setDate(Date.from(wbciRequest.getTvTermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    protected void onSave() {
        String vaId = wbciRequest.getVorabstimmungsId();
        String aenderungsId = wbciRequest.getAenderungsId();
        MeldungServiceHelper.createTvErledigtmeldung(getWbciMeldungService(), vaId, wbciRequest.getTvTermin(), aenderungsId);
    }
}
