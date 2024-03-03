/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import java.time.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpBuilder;

/**
 *
 */
public class WbciGeschaeftsfallRrnpKftBuilder extends WbciGeschaeftsfallRrnpBuilder {

    public WbciGeschaeftsfallRrnpKftBuilder(WbciCdmVersion wbciCdmVersion) {
        this(wbciCdmVersion, true);
    }

    public WbciGeschaeftsfallRrnpKftBuilder(WbciCdmVersion wbciCdmVersion, boolean anlagenAnschluss) {
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
                    .withPortierungskennungPKIauf("D123").build());
        }
        else {
            withRufnummernportierung(new RufnummernportierungEinzelKftBuilder(wbciCdmVersion)
                    .withPortierungskennungPKIauf("D123").build());
        }
        withAuftragId(1L);
    }

}
