/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;

/**
 * Panel zur Anzeige von AKM-TR Details.
 */
public class MeldungDetailAkmTrPanel extends AbstractMeldungDetailPanel<UebernahmeRessourceMeldung> {

    private static final long serialVersionUID = -5691007968976920903L;

    public static final String UEBERNAHME_LEITUNG = "uebernahme.leitung";
    public static final String RESSOURCEN_UEBERNAHME = "ressourcen.uebernahme";
    public static final String PKI_AUF = "pki.auf";
    public static final String SICHERER_HAFEN = "sicherer.hafen";

    private AKJCheckBox chbRessourcen;
    private AKJTextArea taUebernahmeLtg;
    private AKJCheckBox chbSichererHafen;
    private AKJTextField tfPkiAuf;

    public MeldungDetailAkmTrPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/MeldungDetailAkmTrPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblRessourcen = getSwingFactory().createLabel(RESSOURCEN_UEBERNAHME);
        AKJLabel lblUebernahmeLtg = getSwingFactory().createLabel(UEBERNAHME_LEITUNG);
        AKJLabel lblSichererHafen = getSwingFactory().createLabel(SICHERER_HAFEN);
        AKJLabel lblPkiAuf = getSwingFactory().createLabel(PKI_AUF);

        chbRessourcen = getSwingFactory().createCheckBox(RESSOURCEN_UEBERNAHME, false);
        taUebernahmeLtg = getSwingFactory().createTextArea(UEBERNAHME_LEITUNG, false, true, true);
        AKJScrollPane spUebernahmeLtg = new AKJScrollPane(taUebernahmeLtg);
        chbSichererHafen = getSwingFactory().createCheckBox(SICHERER_HAFEN, false);
        tfPkiAuf = getSwingFactory().createTextField(PKI_AUF, false);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblPkiAuf         , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(tfPkiAuf          , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblSichererHafen  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(chbSichererHafen  , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblRessourcen     , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(chbRessourcen     , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblUebernahmeLtg  , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(spUebernahmeLtg   , GBCFactory.createGBC(100,100, 2, 3, 1, 2, GridBagConstraints.BOTH));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        chbRessourcen.setSelected(meldung.isUebernahme());
        chbSichererHafen.setSelected(meldung.isSichererHafen());
        tfPkiAuf.setText(meldung.getPortierungskennungPKIauf());
        taUebernahmeLtg.setText(meldung.extractLeitungen());
    }
}
