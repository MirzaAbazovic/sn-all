/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.util.*;

import de.augustakom.common.gui.swing.MessageHelper;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOwithKuendigungsCheckVOHelper;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.WbciRequest;

/**
 * Dialog, um eine WBCI 'ABBM' Meldung zu einer VA zu generieren bzw. Details dafuer festzulegen.
 *
 *
 */
public class AbbmDialog extends AbstractAbbmDialog<WbciRequest, Abbruchmeldung> {

    private static final long serialVersionUID = 4754827174794573051L;
    
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/AbbmDialog.xml";
    private static final String ADF_ABBM_HINWEISTEXT = "Eine ABBM, die nur ADF MeldungsCodes beinhaltet und ohne ein "
            + "'Sonstiger Ablehnungsgrund', ist nicht erlaubt! In diesem Fall ist eine RUEM-VA mit ADA MeldungsCodes "
            + "zu verwenden";
    private static final String POSITIONEN_ABBM_HINWEISTEXT = "Eine ABBM muss mindestens eine Meldungsposition enthalten. "
            + "Sollte keine Abbruchmeldung (Tabelle) existieren, so muss manuell ein Ablehnungsgrund eingetragen oder "
            + "ausgewählt werden!";

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}s, auf den sich die Abbm beziehen soll.
     *
     * @param wbciRequest zu verwendende Vorabstimmung
     * @param decisionVOs vorab getroffene Entscheidungen aus dem {@link de.augustakom.hurrican.gui.tools.wbci.DecisionDialog}
     */
    public AbbmDialog(WbciRequest wbciRequest, List<DecisionVO> decisionVOs) {
        super(RESOURCE, wbciRequest, decisionVOs, false, true);
    }

    @Override
    protected MeldungPositionTyp getPositionType() {
        return MeldungPositionTyp.ABBM;
    }

    @Override
    public final void loadData() {
        super.loadData();
        if (decisionVOs != null) {
            KuendigungsCheckVO kuendigungsCheckVO = DecisionVOwithKuendigungsCheckVOHelper.getTaifunKuendigungsstatus(decisionVOs);
            if (kuendigungsCheckVO.getKuendigungsstatus() != null) {
                if (kuendigungsCheckVO.getKuendigungsstatus().getReferenceId() != null) {
                    rfSonstigesExclusiv.setReferenceId(kuendigungsCheckVO.getKuendigungsstatus().getReferenceId());
                }
                else if (!kuendigungsCheckVO.getKuendigungsstatus().isGeschaeftsfallAllowed(
                        wbciRequest.getWbciGeschaeftsfall().getTyp())) {
                    tfSonstiges.setText(kuendigungsCheckVO.getKuendigungsstatus().getAbbmReason());
                }
            }
        }
    }

    @Override
    protected Abbruchmeldung createMeldung() {
        return createAbbmBuilder().build();
    }

    @Override
    protected void doSave() {
        Abbruchmeldung abbm = createMeldung();
        if (abbm.isMeldungWithOnlyADFCodes()) {
            MessageHelper.showInfoDialog(getMainFrame(), ADF_ABBM_HINWEISTEXT);
        }
        else if (abbm.isMeldungLackingAnyMeldungsPosition()) {
            MessageHelper.showInfoDialog(getMainFrame(), POSITIONEN_ABBM_HINWEISTEXT);
        }
        else {
            super.doSave();
        }
    }

}
