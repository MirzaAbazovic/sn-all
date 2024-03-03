/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;
import javax.validation.constraints.*;

import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class AbbruchmeldungTechnRessourceBuilder extends MeldungBuilder<AbbruchmeldungTechnRessource, MeldungPositionAbbruchmeldungTechnRessource> {

    @Override
    public AbbruchmeldungTechnRessource build() {
        AbbruchmeldungTechnRessource meldung = new AbbruchmeldungTechnRessource();
        super.enrich(meldung);
        return meldung;
    }

    /**
     * Build an outgoing ABBM-TR meldung especially for VA and provided geschaeftsfall and meldungs codes.
     */
    public AbbruchmeldungTechnRessource buildOutgoingForVa(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall,
            @NotNull MeldungsCode... meldungsCodes) {

        withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withIoType(IOType.OUT)
                .withAbsender(CarrierCode.MNET)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .withProcessedAt(LocalDateTime.now());

        for (MeldungsCode code : meldungsCodes) {
            addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                    .withMeldungsCode(code)
                    .withMeldungsText(code.getStandardText())
                    .build());
        }

        return build();
    }
}
