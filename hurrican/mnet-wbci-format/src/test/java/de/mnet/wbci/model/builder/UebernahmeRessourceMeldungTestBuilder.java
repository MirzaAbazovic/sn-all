/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
public class UebernahmeRessourceMeldungTestBuilder extends UebernahmeRessourceMeldungBuilder implements
        WbciTestBuilder<UebernahmeRessourceMeldung> {

    @Override
    public UebernahmeRessourceMeldung buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (uebernahme == null) {
            uebernahme = true;
        }

        if (sichererHafen == null) {
            sichererHafen = false;
        }

        if (portierungskennungPKIauf == null && (gfTyp.equals(GeschaeftsfallTyp.VA_KUE_MRN) || gfTyp.equals(GeschaeftsfallTyp.VA_RRNP))) {
            withPortierungskennungPKIauf("D000");
        }

        if (leitungen.isEmpty()) {
            leitungen.add(new LeitungTestBuilder().buildValidLineId(wbciCdmVersion, gfTyp));
        }
        if (meldungsPositionen == null || meldungsPositionen.isEmpty()) {
            addMeldungPosition(new MeldungPositionUebernahmeRessourceMeldungTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }
        MeldungTestBuilder.enrich(this, wbciCdmVersion, gfTyp);

        return build();
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withSendAfter(LocalDateTime sendAfter) {
        super.withSendAfter(sendAfter);
        return this;
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder addLeitung(Leitung leitung) {
        super.addLeitung(leitung);
        return this;
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        return this;
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withMeldungsPositionen(Set<MeldungPositionUebernahmeRessourceMeldung> positionen) {
        return (UebernahmeRessourceMeldungTestBuilder) super.withMeldungsPositionen(positionen);
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder addMeldungPosition(MeldungPositionUebernahmeRessourceMeldung position) {
        return (UebernahmeRessourceMeldungTestBuilder) super.addMeldungPosition(position);
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withUebernahme(Boolean uebernahme) {
        return (UebernahmeRessourceMeldungTestBuilder) super.withUebernahme(uebernahme);
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withPortierungskennungPKIauf(String portierungskennungPKIauf) {
        return (UebernahmeRessourceMeldungTestBuilder) super.withPortierungskennungPKIauf(portierungskennungPKIauf);
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withLeitungen(Set<Leitung> leitungen) {
        return (UebernahmeRessourceMeldungTestBuilder) super.withLeitungen(leitungen);
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withSichererhafen(Boolean sichererHafen) {
        return (UebernahmeRessourceMeldungTestBuilder) super.withSichererhafen(sichererHafen);
    }

    @Override
    public UebernahmeRessourceMeldungTestBuilder withProcessedAt(LocalDateTime processedAt) {
        return (UebernahmeRessourceMeldungTestBuilder) super.withProcessedAt(processedAt);
    }
}
