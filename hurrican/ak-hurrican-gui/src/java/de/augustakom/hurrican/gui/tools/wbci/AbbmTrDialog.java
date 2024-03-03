/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceBuilder;

/**
 * Dialog, um eine WBCI 'ABBM' Meldung zu einer Terminverschiebung zu generieren bzw. Details dafuer festzulegen.
 *
 *
 */
public class AbbmTrDialog extends AbstractAbbmDialog<VorabstimmungsAnfrage, AbbruchmeldungTechnRessource> {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/AbbmTrDialog.xml";
    private static final long serialVersionUID = -838491701940462636L;

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}s, auf den sich die Abbm beziehen soll.
     *
     * @param va zu verwendende VA
     */
    public AbbmTrDialog(VorabstimmungsAnfrage va) {
        super(RESOURCE, va, DecisionVOHelper.createAbbmTrDecisionVo(), true, false);
    }

    @Override
    protected MeldungPositionTyp getPositionType() {
        return MeldungPositionTyp.ABBM_TR;
    }

    @Override
    protected AbbruchmeldungTechnRessource createMeldung() {
        return new AbbruchmeldungTechnRessourceBuilder()
                .withAbsender(CarrierCode.MNET)
                .withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall())
                .withIoType(IOType.OUT)
                .withMeldungsPositionen(DecisionVOHelper.extractMeldungPositionAbbmTr(decisionVOs))
                .build();
    }

}
