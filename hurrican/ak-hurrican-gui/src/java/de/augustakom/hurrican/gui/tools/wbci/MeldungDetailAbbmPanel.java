/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.util.*;
import org.apache.commons.lang.SystemUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungPositionAbbmRufnummer;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;

/**
 * Panel zur Anzeige von ABBM Details.
 */
public class MeldungDetailAbbmPanel extends AbstractMeldungDetailPanel<Abbruchmeldung> {
    private static final long serialVersionUID = 6602282646503205751L;
    public static final String BEGRUENDUNG = "begruendung";
    public static final String RUFNUMMERN = "rufnummern";

    private AKJTextArea taBegruendung;
    private AKJTextArea taRufnummern;

    public MeldungDetailAbbmPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/MeldungDetailAbbmPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblBegruendung = getSwingFactory().createLabel(BEGRUENDUNG);
        AKJLabel lblRufnummern = getSwingFactory().createLabel(RUFNUMMERN);
        taBegruendung = getSwingFactory().createTextArea(BEGRUENDUNG, false, true, true);
        AKJScrollPane spBegruendung = new AKJScrollPane(taBegruendung);
        taRufnummern = getSwingFactory().createTextArea(RUFNUMMERN, false, true, true);
        AKJScrollPane spRufnummern = new AKJScrollPane(taRufnummern);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblBegruendung, GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(spBegruendung , GBCFactory.createGBC(100,100, 2, 0, 1, 2, GridBagConstraints.BOTH));
        detailPnl.add(lblRufnummern , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(spRufnummern  , GBCFactory.createGBC(100,100, 2, 2, 1, 2, GridBagConstraints.BOTH));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        taBegruendung.setText(meldung.getBegruendung());

        if (meldung.getMeldungsPositionen() != null) {
            StringBuilder rufnummernText = new StringBuilder();
            Iterator<MeldungPositionAbbruchmeldung> iterator = meldung.getMeldungsPositionen().iterator();
            while (iterator.hasNext()) {
                MeldungPositionAbbruchmeldung next = iterator.next();
                Set<MeldungPositionAbbmRufnummer> rufnummern = next.getRufnummern();
                if (rufnummern != null) {
                    for (MeldungPositionAbbmRufnummer rufnummer : rufnummern) {
                        if (rufnummernText.length() > 0) {
                            rufnummernText.append(SystemUtils.LINE_SEPARATOR);
                        }
                        rufnummernText.append(rufnummer.getRufnummer());
                    }
                }
            }
            taRufnummern.setText(rufnummernText.toString());
        }
    }
}
