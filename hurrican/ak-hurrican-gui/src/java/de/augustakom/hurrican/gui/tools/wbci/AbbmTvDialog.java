/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;

/**
 * Dialog, um eine WBCI 'ABBM' Meldung zu einer Terminverschiebung zu generieren bzw. Details dafuer festzulegen.
 *
 *
 */
public class AbbmTvDialog extends AbstractAbbmDialog<TerminverschiebungsAnfrage, Abbruchmeldung> {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/AbbmTVDialog.xml";
    private static final long serialVersionUID = -838491701940462636L;

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}s, auf den sich die Abbm beziehen soll.
     *
     * @param tv zu verwendende Terminverscheibung
     */
    public AbbmTvDialog(TerminverschiebungsAnfrage tv) {
        super(RESOURCE, tv, DecisionVOHelper.createAbbmTvDecisionVo(tv), true, true);
    }

    @Override
    protected MeldungPositionTyp getPositionType() {
        return MeldungPositionTyp.ABBM;
    }

    @Override
    protected Abbruchmeldung createMeldung() {
        return createAbbmBuilder()
                .withAenderungsIdRef(wbciRequest.getAenderungsId())
                .withWechseltermin(wbciRequest.getWbciGeschaeftsfall().getWechseltermin())
                .buildForTv();
    }

}
