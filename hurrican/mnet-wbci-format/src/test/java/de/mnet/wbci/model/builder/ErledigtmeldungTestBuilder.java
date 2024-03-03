/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model.builder;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;

import java.time.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;

public class ErledigtmeldungTestBuilder extends ErledigtmeldungBuilder implements WbciTestBuilder<Erledigtmeldung> {

    @Override
    public Erledigtmeldung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        enrichValid(wbciCdmVersion, gfTyp);
        return build();
    }

    public ErledigtmeldungTerminverschiebung buildValidForTv(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (wechseltermin == null) {
            wechseltermin = getDateInWorkingDaysFromNowAndNextDayNotHoliday(11);
        }
        enrichValid(wbciCdmVersion, gfTyp);
        return buildForTv();
    }

    public Erledigtmeldung buildValidForStorno(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, RequestTyp requestTyp) {
        enrichValid(wbciCdmVersion, gfTyp);
        return buildForStorno(requestTyp);
    }

    private void enrichValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        // explicitly set meldung positionen according to this builders meldung.
        if (meldungsPositionen.isEmpty()) {
            meldungsPositionen.add(new MeldungPositionErledigtmeldungTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }

        if (wechseltermin == null) {
            wechseltermin = LocalDate.now();
        }

        if (aenderungsIdRef == null) {
            aenderungsIdRef = IdGenerator.generateTvId(CarrierCode.DTAG);
        }

        if (stornoIdRef == null) {
            stornoIdRef = IdGenerator.generateStornoId(CarrierCode.DTAG);
        }

        MeldungTestBuilder.enrich(this, wbciCdmVersion, gfTyp);
    }

    @Override
    public ErledigtmeldungTestBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (ErledigtmeldungTestBuilder) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    @Override
    public ErledigtmeldungTestBuilder withWechseltermin(LocalDate wechseltermin) {
        return (ErledigtmeldungTestBuilder) super.withWechseltermin(wechseltermin);
    }

}
