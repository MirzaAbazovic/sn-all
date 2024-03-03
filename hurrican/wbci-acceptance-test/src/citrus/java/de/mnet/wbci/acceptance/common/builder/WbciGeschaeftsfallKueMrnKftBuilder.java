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
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;

/**
 *
 */
public class WbciGeschaeftsfallKueMrnKftBuilder extends WbciGeschaeftsfallKueMrnBuilder {

    private WbciCdmVersion wbciCdmVersion;

    public WbciGeschaeftsfallKueMrnKftBuilder(WbciCdmVersion wbciCdmVersion) {
        this(wbciCdmVersion, true);
    }

    public WbciGeschaeftsfallKueMrnKftBuilder(WbciCdmVersion wbciCdmVersion, boolean anlagenAnschluss) {
        this.wbciCdmVersion = wbciCdmVersion;
        withAbsender(CarrierCode.MNET);

        withAbgebenderEKP(CarrierCode.DTAG);
        withAufnehmenderEKP(CarrierCode.MNET);

        withKundenwunschtermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14));

        withEndkunde(new PersonKftBuilder(wbciCdmVersion).build());

        addAnschlussinhaber(new PersonKftBuilder(wbciCdmVersion)
                .withVorname("Kate")
                .withAnrede(Anrede.FRAU).build());

        if (anlagenAnschluss) {
            withRufnummernportierung(new RufnummernportierungAnlageKftBuilder(wbciCdmVersion)
                    .withPortierungskennungPKIauf("D123")
                    .build());
        }
        else {
            withRufnummernportierung(new RufnummernportierungEinzelKftBuilder(wbciCdmVersion)
                    .withPortierungskennungPKIauf("D123")
                    .build());
        }

        withStandort(new StandortKftBuilder(wbciCdmVersion).build());
        withStatus(WbciGeschaeftsfallStatus.ACTIVE);

        withAuftragId(1L);
    }

    public WbciGeschaeftsfallKueMrnKftBuilder withOrt(String ort) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) withStandort(new StandortKftBuilder(wbciCdmVersion).withOrt(ort).build());
    }

    public WbciGeschaeftsfallKueMrnKftBuilder withPostleitzahl(String postleitzahl) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) withStandort(
                new StandortKftBuilder(wbciCdmVersion).withPostleitzahl(postleitzahl).build());
    }

    public WbciGeschaeftsfallKueMrnKftBuilder withAnschlussinhaber(String nachname, String vorname, Anrede anrede) {
        weitereAnschlussinhaber = new ArrayList<>();
        return (WbciGeschaeftsfallKueMrnKftBuilder) addAnschlussinhaber(
                new PersonKftBuilder(wbciCdmVersion)
                        .withNachname(nachname)
                        .withVorname(vorname)
                        .withAnrede(anrede)
                        .build()
        );
    }

    public WbciGeschaeftsfallKueMrnKftBuilder withEndkunde(String nachname, String vorname, Anrede anrede) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) withEndkunde(
                new PersonKftBuilder(wbciCdmVersion)
                        .withNachname(nachname)
                        .withVorname(vorname)
                        .withAnrede(anrede)
                        .build()
        );
    }

    @Override
    public WbciGeschaeftsfallKueMrnKftBuilder withVorabstimmungsId(String vorabstimmungsId) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) super.withVorabstimmungsId(vorabstimmungsId);
    }

    @Override
    public WbciGeschaeftsfallKueMrnKftBuilder withKundenwunschtermin(LocalDate kundenwunschtermin) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) super.withKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin : null);
    }

    @Override
    public WbciGeschaeftsfallKueMrnKftBuilder withAufnehmenderEKP(CarrierCode aufnehmenderEKP) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) super.withAufnehmenderEKP(aufnehmenderEKP);
    }

    @Override
    public WbciGeschaeftsfallKueMrnKftBuilder withAbgebenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallKueMrnKftBuilder) super.withAbgebenderEKP(abgebenderEKP);
    }

}
