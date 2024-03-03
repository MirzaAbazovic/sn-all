/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci;


import java.awt.*;
import org.apache.commons.lang.SystemUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.tools.lang.StringTools;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;

/**
 * Detail-Panel fuer die Darstellung von Vorabstimmungs-Details.
 */
public class RequestDetailVaPanel extends AbstractRequestDetailPanel<VorabstimmungsAnfrage> {

    private static final long serialVersionUID = -6743236203619331754L;
    public static final String VA_TYP = "va.typ";
    public static final String KWT = "kwt";
    public static final String PROJEKTKENNER = "projektkenner";
    public static final String ENDKUNDE = "endkunde";
    public static final String WEITERE_AI = "weitere.ai";
    public static final String ANSCHLUSS_IDENTIFIKATION = "anschluss.identifikation";

    private AKJTextField tfVaTyp;
    private AKJDateComponent dcKwt;
    private AKJTextField tfProjektkenner;
    private AKJTextField tfEndkunde;
    private AKJTextArea taWeitereAI;
    private AKJTextField tfAnschlussIdent;

    public RequestDetailVaPanel() {
        super("de/augustakom/hurrican/gui/tools/wbci/resources/RequestDetailVaPanel.xml");
    }

    @Override
    protected AKJPanel buildDetailPanel() {
        AKJLabel lblVaTyp = getSwingFactory().createLabel(VA_TYP);
        AKJLabel lblKwt = getSwingFactory().createLabel(KWT);
        AKJLabel lblProjektkenner = getSwingFactory().createLabel(PROJEKTKENNER);
        AKJLabel lblEndkunde = getSwingFactory().createLabel(ENDKUNDE);
        AKJLabel lblWeitereAI = getSwingFactory().createLabel(WEITERE_AI);
        AKJLabel lblAnschlussIdent = getSwingFactory().createLabel(ANSCHLUSS_IDENTIFIKATION);

        tfVaTyp = getSwingFactory().createTextField(VA_TYP, false);
        dcKwt = getSwingFactory().createDateComponent(KWT, false);
        tfProjektkenner = getSwingFactory().createTextField(PROJEKTKENNER, false);
        tfEndkunde = getSwingFactory().createTextField(ENDKUNDE, false);
        taWeitereAI = getSwingFactory().createTextArea(WEITERE_AI, false);
        AKJScrollPane spWeitereAI = new AKJScrollPane(taWeitereAI);
        tfAnschlussIdent = getSwingFactory().createTextField(ANSCHLUSS_IDENTIFIKATION, false);

        // @formatter:off
        AKJPanel detailPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        detailPnl.add(lblVaTyp          , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(tfVaTyp           , GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblKwt            , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(dcKwt             , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblProjektkenner  , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfProjektkenner   , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblEndkunde       , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfEndkunde        , GBCFactory.createGBC(100,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblWeitereAI      , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(spWeitereAI       , GBCFactory.createGBC(100,100, 2, 4, 1, 2, GridBagConstraints.BOTH));
        detailPnl.add(lblAnschlussIdent , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfAnschlussIdent  , GBCFactory.createGBC(100,  0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        return detailPnl;
    }

    @Override
    protected void showVaDetails() {
        super.showVaDetails();

        tfVaTyp.setText(wbciRequest.getTyp().getShortName());
        dcKwt.setDateTime(wbciRequest.getVaKundenwunschtermin().atStartOfDay());
        if (wbciRequest.getWbciGeschaeftsfall().getProjekt() != null) {
            tfProjektkenner.setText(wbciRequest.getWbciGeschaeftsfall().getProjekt().getProjektKenner());
        }

        tfEndkunde.setText(StringTools.join(new String[] {
                wbciRequest.getWbciGeschaeftsfall().getEndkunde().getNameOrFirma(),
                wbciRequest.getWbciGeschaeftsfall().getEndkunde().getVornameOrZusatz() }, " ", true));

        if (wbciRequest.getWbciGeschaeftsfall().getWeitereAnschlussinhaber() != null) {
            StringBuilder weitereAi = new StringBuilder();
            for (PersonOderFirma weitererAi : wbciRequest.getWbciGeschaeftsfall().getWeitereAnschlussinhaber()) {
                if (weitereAi.length() > 0) {
                    weitereAi.append(SystemUtils.LINE_SEPARATOR);
                }

                weitereAi.append(StringTools.join(new String[] {
                        weitererAi.getNameOrFirma(),
                        weitererAi.getVornameOrZusatz() }, " ", true));
            }

            taWeitereAI.setText(weitereAi.toString());
        }

        if (GeschaeftsfallTyp.VA_KUE_ORN.equals(wbciRequest.getWbciGeschaeftsfall().getTyp())) {
            WbciGeschaeftsfallKueOrn kueOrn = (WbciGeschaeftsfallKueOrn) wbciRequest.getWbciGeschaeftsfall();
            if (kueOrn.getAnschlussIdentifikation() != null) {
                RufnummerOnkz ident = kueOrn.getAnschlussIdentifikation();
                tfAnschlussIdent.setText(String.format("0%s/%s", ident.getOnkz(), ident.getRufnummer()));
            }
        }
    }

}
