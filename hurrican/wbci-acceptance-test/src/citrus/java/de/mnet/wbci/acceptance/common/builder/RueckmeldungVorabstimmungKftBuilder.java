/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import java.time.*;
import java.util.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;

/**
 *
 */
public class RueckmeldungVorabstimmungKftBuilder extends RueckmeldungVorabstimmungBuilder {

    public RueckmeldungVorabstimmungKftBuilder(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, IOType ioType) {
        AbstractMeldungKftBuilder.withMeldungMetaData(this, ioType);
        if (!GeschaeftsfallTyp.VA_RRNP.equals(gfTyp)) {
            withTechnologie(Technologie.TAL_ISDN);
        }
        withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14));

        if (!GeschaeftsfallTyp.VA_KUE_ORN.equals(gfTyp)) {
            withRufnummernportierung(
                    new RufnummernportierungAnlageKftBuilder(wbciCdmVersion)
                            .withRufnummernbloecke(
                                    Arrays.asList(
                                            new RufnummernBlockKftBuilder(wbciCdmVersion)
                                                    .withPkiAbg("D123")
                                                    .build()
                                    )
                            )
                            .build()
            );
        }

        if (GeschaeftsfallTyp.VA_RRNP.equals(gfTyp)) {
            withTechnischeRessourcen(new HashSet<TechnischeRessource>());
        }
        else {
            addTechnischeRessource(new TechnischeRessourceKftBuilder(wbciCdmVersion, ioType).build());
        }

        addMeldungPosition(new MeldungPositionRueckmeldungVaBuilder()
                .withMeldungsCode(MeldungsCode.ZWA)
                .withMeldungsText(MeldungsCode.ZWA.getStandardText())
                .build());
    }

    public RueckmeldungVorabstimmungKftBuilder withExplicitMeldungsCode(Standort standortAbweichend,
            MeldungsCode... codes) {

        for (MeldungsCode code : codes) {
            addMeldungPosition(new MeldungPositionRueckmeldungVaBuilder()
                    .withStandortAbweichend(standortAbweichend)
                    .withMeldungsCode(code)
                    .withMeldungsText(code.getStandardText())
                    .build());
        }

        return this;
    }

}
