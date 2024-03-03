/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2011 14:49:43
 */
package de.mnet.wita.acceptance.common;

import javax.annotation.*;
import org.apache.commons.lang.math.NumberUtils;

import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;

/**
 * <p/>
 * Should be <b>singleton</b> that there is just one instance of {@link HVTGruppe}, {@link HVTStandort}, {@link
 * de.augustakom.hurrican.model.cc.GeoId} and {@link GeoId2TechLocation}
 */
public class KftStandortDataBuilder extends StandortDataBuilder<KftStandortDataBuilder> {

    @PostConstruct
    public void init() throws Exception {
        withOnkz("4401").withAsb(1).withCarrierKennungId(CarrierKennung.ID_AUGUSTAKOM).withPlz("26919")
                .withOrt("Brake").withHvtStrasse("Georgstr.").withHvtHausNr("1 a")
                .withKundeStrasse("Mitteldeichstr.").withKundeHausNr("8").withKundeHausNrZusatz("a").withOrtsteil("Zusatz")
                .withUevt("1PH1").build();
    }

    public boolean isValidEvsAndDoppelader(String evs, String doppelader) {
        if (NumberUtils.isNumber(evs) && NumberUtils.isNumber(doppelader)) {
            int evsInt = Integer.valueOf(evs).intValue();
            int doppeladerInt = Integer.valueOf(doppelader).intValue();

            return (0 < evsInt) && (evsInt <= 50) && (0 < doppeladerInt) && (doppeladerInt <= 4);
        }
        return false;
    }

}
