package de.mnet.wbci.acceptance.common.builder;

import java.time.*;
import java.util.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;

public class AbbruchmeldungKftBuilder extends AbbruchmeldungBuilder {

    public AbbruchmeldungKftBuilder(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, IOType ioType) {
        AbstractMeldungKftBuilder.withMeldungMetaData(this, ioType);
        withWbciVersion(WbciVersion.V2);
        withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14));
    }

    public AbbruchmeldungKftBuilder withMeldungsCodes(MeldungsCode... meldungsCodes) {
        meldungsPositionen = new HashSet<>();
        for (MeldungsCode meldungsCode : meldungsCodes) {
            addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                    .withMeldungsCode(meldungsCode)
                    .withMeldungsText(meldungsCode.getStandardText())
                    .build());
        }
        return this;
    }

    public AbbruchmeldungKftBuilder withoutWechseltermin() {
        withWechseltermin(null);
        return this;
    }

}
