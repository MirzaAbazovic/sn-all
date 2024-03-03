/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2015
 */
package de.augustakom.hurrican.model.cc.tal;

import java.time.*;
import java.time.format.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;

/**
 * Enum definiert Zeitfenster fuer eine TAL-Realisierung.
 */
public enum TalRealisierungsZeitfenster {

    VORMITTAG("08:00 - 12:00", LocalTime.of(13, 0), LocalTime.of(17, 0)),

    NACHMITTAG("12:00 - 16:00", LocalTime.of(16, 0), LocalTime.of(18, 0)),

    GANZTAGS("08:00 - 16:00", LocalTime.of(16, 0), LocalTime.of(18, 0));


    private final String carrierRealisierung;
    private final LocalTime mnetTechnikerVonZF;
    private final LocalTime mnetTechnikerBisZF;


    TalRealisierungsZeitfenster(String carrierRealisierung, LocalTime mnetTechnikerVonZF, LocalTime mnetTechnikerBisZF) {
        this.carrierRealisierung = carrierRealisierung;
        this.mnetTechnikerVonZF = mnetTechnikerVonZF;
        this.mnetTechnikerBisZF = mnetTechnikerBisZF;
    }


    public String getDisplayText() {
        return String.format("%s (M-net: %s)",
                carrierRealisierung,
                getMnetTechnikerDisplayText());
    }

    public String getMnetTechnikerDisplayText() {
        return String.format("%s - %s",
                mnetTechnikerVonZF.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_TIME)),
                mnetTechnikerBisZF.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_TIME)));
    }

    public String getCarrierRealisierung() {
        return carrierRealisierung;
    }

    public LocalTime getMnetTechnikerVonZF() {
        return mnetTechnikerVonZF;
    }

    public LocalTime getMnetTechnikerBisZF() {
        return mnetTechnikerBisZF;
    }

    public static TalRealisierungsZeitfenster getNullSafe(String name) {
        if (StringUtils.isNotBlank(name)) {
            return TalRealisierungsZeitfenster.valueOf(name);
        }
        return null;
    }

}
