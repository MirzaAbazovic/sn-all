/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.StornoAnfrage;

/**
 *
 */
public class AbbmStornoDialog extends AbstractAbbmDialog<StornoAnfrage, Abbruchmeldung> {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/AbbmStornoDialog.xml";
    private static final long serialVersionUID = 3644591174650224581L;

    /**
     * Konstruktor mit Angabe des {@link de.mnet.wbci.model.WbciRequest}s, auf den sich die Abbm beziehen soll.
     *
     * @param stornoAnfrage zu verwendende StornoAnfrage
     */
    protected AbbmStornoDialog(StornoAnfrage stornoAnfrage) {
        super(RESOURCE, stornoAnfrage, DecisionVOHelper.createAbbmStornoDecisionVo(), true, true);
    }

    @Override
    protected MeldungPositionTyp getPositionType() {
        return MeldungPositionTyp.ABBM;
    }

    @Override
    protected Abbruchmeldung createMeldung() {
        return createAbbmBuilder()
                .withStornoIdRef(wbciRequest.getAenderungsId())
                .buildForStorno(wbciRequest.getTyp());
    }

}
