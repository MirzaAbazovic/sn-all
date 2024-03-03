/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import java.time.*;
import java.util.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnBuilder;

/**
 *
 */
public class WbciGeschaeftsfallKueOrnKftBuilder extends WbciGeschaeftsfallKueOrnBuilder {

    private WbciCdmVersion wbciCdmVersion;

    public WbciGeschaeftsfallKueOrnKftBuilder(WbciCdmVersion wbciCdmVersion) {
        this.wbciCdmVersion = wbciCdmVersion;

        withAbsender(CarrierCode.MNET);

        withAbgebenderEKP(CarrierCode.DTAG);
        withAufnehmenderEKP(CarrierCode.MNET);

        withKundenwunschtermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14));

        withEndkunde(new PersonKftBuilder(wbciCdmVersion).build());

        addAnschlussinhaber(new PersonKftBuilder(wbciCdmVersion)
                .withVorname("Kate")
                .withAnrede(Anrede.FRAU).build());

        withAnschlussIdentifikation(new RufnummerOnkzKftBuilder(wbciCdmVersion).build());
        withStandort(new StandortKftBuilder(wbciCdmVersion).build());

        withAuftragId(1L);
    }

    public WbciGeschaeftsfallKueOrnKftBuilder withOrt(String ort) {
        return (WbciGeschaeftsfallKueOrnKftBuilder) withStandort(new StandortKftBuilder(wbciCdmVersion).withOrt(ort).build());
    }

    public WbciGeschaeftsfallKueOrnKftBuilder withPostleitzahl(String postleitzahl) {
        return (WbciGeschaeftsfallKueOrnKftBuilder) withStandort(
                new StandortKftBuilder(wbciCdmVersion).withPostleitzahl(postleitzahl).build());
    }

    public WbciGeschaeftsfallKueOrnKftBuilder withAnschlussinhaber(String nachname, String vorname, Anrede anrede) {
        weitereAnschlussinhaber = new ArrayList<>();
        return (WbciGeschaeftsfallKueOrnKftBuilder) addAnschlussinhaber(
                new PersonKftBuilder(wbciCdmVersion)
                        .withNachname(nachname)
                        .withVorname(vorname)
                        .withAnrede(anrede)
                        .build()
        );
    }

    public WbciGeschaeftsfallKueOrnKftBuilder withEndkunde(String nachname, String vorname, Anrede anrede) {
        return (WbciGeschaeftsfallKueOrnKftBuilder) withEndkunde(
                new PersonKftBuilder(wbciCdmVersion)
                        .withNachname(nachname)
                        .withVorname(vorname)
                        .withAnrede(anrede)
                        .build()
        );
    }

}
