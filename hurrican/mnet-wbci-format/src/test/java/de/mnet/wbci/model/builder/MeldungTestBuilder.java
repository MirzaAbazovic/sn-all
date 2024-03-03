/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
public class MeldungTestBuilder<POS extends MeldungPosition> {

    public static void enrich(MeldungBuilder meldungBuilder, WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {

        if (meldungBuilder.wbciGeschaeftsfall == null) {
            final WbciGeschaeftsfall wbciGeschaeftsfall;
            switch (gfTyp) {
                case VA_KUE_MRN:
                    wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(wbciCdmVersion, gfTyp);
                    break;
                case VA_KUE_ORN:
                    wbciGeschaeftsfall = new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(wbciCdmVersion, gfTyp);
                    break;
                case VA_RRNP:
                    wbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder().buildValid(wbciCdmVersion, gfTyp);
                    break;
                default:
                    wbciGeschaeftsfall = null;
                    break;
            }
            meldungBuilder.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        }

        if (meldungBuilder.absender == null) {
            meldungBuilder.absender = CarrierCode.MNET;
        }

        if (meldungBuilder.wbciVersion == null) {
            meldungBuilder.wbciVersion = WbciVersion.getDefault();
        }

        if (meldungBuilder.processedAt == null) {
            meldungBuilder.processedAt = LocalDateTime.now();
        }
        if (meldungBuilder.ioType == null) {
            meldungBuilder.ioType = IOType.IN;
        }
        if (meldungBuilder.meldungsPositionen.isEmpty()) {
            meldungBuilder.meldungsPositionen.add(new MeldungPositionRueckmeldungVaTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }
    }

}
