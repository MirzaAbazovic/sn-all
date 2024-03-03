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
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.helper.RueckmeldungVorabstimmungHelper;

/**
 * Panel zur Anzeige von RUEM-VA Details.
 */
public class MeldungDetailRuemVaPanel extends AbstractMeldungDetailPanel<RueckmeldungVorabstimmung> {

    private static final long serialVersionUID = 8602282646503205753L;

    private static final String WITA_VERTRAGSNUMMERN = "wita.vertragsnummern";
    private static final String IST_TECHNOLOGIE = "ist.technologie";
    public static final String WECHSELTERMIN = "wechseltermin";
    public static final String ADA_INFOS = "ada.infos";

    private AKJDateComponent dcWechseltermin;
    private AKJTextField tfIstTechnologie;
    private AKJTextArea taWitaVtrNr;
    private AKJTextArea taAdaInfos;

    public MeldungDetailRuemVaPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/MeldungDetailRuemVaPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblWechseltermin = getSwingFactory().createLabel(WECHSELTERMIN);
        AKJLabel lblIstTechnologie = getSwingFactory().createLabel(IST_TECHNOLOGIE);
        AKJLabel lblWitaVtrNrs = getSwingFactory().createLabel(WITA_VERTRAGSNUMMERN);
        AKJLabel lblAdaInfos = getSwingFactory().createLabel(ADA_INFOS);

        dcWechseltermin = getSwingFactory().createDateComponent(WECHSELTERMIN, false);
        tfIstTechnologie = getSwingFactory().createTextField(IST_TECHNOLOGIE, false);
        taWitaVtrNr = getSwingFactory().createTextArea(WITA_VERTRAGSNUMMERN, false, true, true);
        AKJScrollPane spWitaVtrNr = new AKJScrollPane(taWitaVtrNr);
        taAdaInfos = getSwingFactory().createTextArea(ADA_INFOS, false, true, true);
        AKJScrollPane spAdaInfos = new AKJScrollPane(taAdaInfos);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblWechseltermin  , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(dcWechseltermin   , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblIstTechnologie , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfIstTechnologie  , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblWitaVtrNrs     , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(spWitaVtrNr       , GBCFactory.createGBC(100,100, 2, 2, 1, 2, GridBagConstraints.BOTH));
        detailPnl.add(lblAdaInfos       , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(spAdaInfos        , GBCFactory.createGBC(100,100, 2, 4, 1, 2, GridBagConstraints.BOTH));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        dcWechseltermin.setDateTime(meldung.getWechseltermin().atStartOfDay());
        tfIstTechnologie.setText((meldung.getTechnologie() != null) ? meldung.getTechnologie().getWbciTechnologieCode() : null);
        taWitaVtrNr.setText(RueckmeldungVorabstimmungHelper.extractWitaVtrNrsAndLineIds(meldung));
        taAdaInfos.setText(RueckmeldungVorabstimmungHelper.extractAdaInfos(meldung));
    }
}
