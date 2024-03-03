/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci.helper;

import java.time.*;

import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.builder.ErledigtmeldungBuilder;
import de.mnet.wbci.service.WbciMeldungService;

/**
 * Helper class for working with Meldungen from the GUI
 */
public final class MeldungServiceHelper {

    private MeldungServiceHelper() {
        // private as its a helper class
    }

    /**
     * Creates and sends a Tv Erlm Meldung, using the supplied parameters to fill the Meldung.
     *
     * @param wbciMeldungService
     * @param vorabstimmungsId
     * @param wechselTermin
     * @param aenderungsId
     */
    public static void createTvErledigtmeldung(WbciMeldungService wbciMeldungService, String vorabstimmungsId, LocalDate wechselTermin, String aenderungsId) {
        Erledigtmeldung erledigtMeldung = new ErledigtmeldungBuilder()
                .withWechseltermin(wechselTermin)
                .withVorabstimmungsIdRef(vorabstimmungsId)
                .withAenderungsIdRef(aenderungsId)
                .buildForTv(MeldungsCode.TV_OK);
        wbciMeldungService.createAndSendWbciMeldung(erledigtMeldung, vorabstimmungsId);
    }

    /**
     * Creates and sends a Storno Erlm Meldung, using the supplied parameters to fill the Meldung.
     *
     * @param wbciMeldungService
     * @param vorabstimmungsId
     * @param stornoId
     */
    public static void createStornoErledigtmeldung(WbciMeldungService wbciMeldungService, String vorabstimmungsId, String stornoId, RequestTyp requestTyp) {
        Erledigtmeldung erledigtMeldung = new ErledigtmeldungBuilder()
                .withVorabstimmungsIdRef(vorabstimmungsId)
                .withStornoIdRef(stornoId)
                .buildForStorno(requestTyp, MeldungsCode.STORNO_OK);
        wbciMeldungService.createAndSendWbciMeldung(erledigtMeldung, vorabstimmungsId);
    }
}
