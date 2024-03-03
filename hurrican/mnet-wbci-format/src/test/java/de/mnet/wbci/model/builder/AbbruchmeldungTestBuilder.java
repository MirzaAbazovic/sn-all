/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class AbbruchmeldungTestBuilder extends AbbruchmeldungBuilder implements
        WbciTestBuilder<Abbruchmeldung> {

    @Override
    public Abbruchmeldung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        enrichValid(wbciCdmVersion, gfTyp);
        return build();
    }

    public AbbruchmeldungTerminverschiebung buildValidForTv(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        enrichValid(wbciCdmVersion, gfTyp);
        return buildForTv();
    }

    public Abbruchmeldung buildValidForStorno(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, RequestTyp requestTyp) {
        enrichValid(wbciCdmVersion, gfTyp);
        return buildForStorno(requestTyp);
    }

    private void enrichValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (begruendung == null) {
            withBegruendung("Begruendung");
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

        if (meldungsPositionen.isEmpty()) {
            meldungsPositionen.add(new MeldungPositionAbbruchmeldungTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }

        MeldungTestBuilder.enrich(this, wbciCdmVersion, gfTyp);
    }

    @Override
    public AbbruchmeldungTestBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (AbbruchmeldungTestBuilder) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    @Override
    public AbbruchmeldungTestBuilder withAenderungsIdRef(String aenderungsIdRef) {
        return (AbbruchmeldungTestBuilder) super.withAenderungsIdRef(aenderungsIdRef);
    }

    @Override
    public AbbruchmeldungTestBuilder withWechseltermin(LocalDate wechseltermin) {
        return (AbbruchmeldungTestBuilder) super.withWechseltermin(wechseltermin);
    }

}
