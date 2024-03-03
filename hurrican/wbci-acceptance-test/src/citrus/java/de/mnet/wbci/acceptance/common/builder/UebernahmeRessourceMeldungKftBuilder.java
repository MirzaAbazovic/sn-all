/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungBuilder;

/**
 *
 */
public class UebernahmeRessourceMeldungKftBuilder extends UebernahmeRessourceMeldungBuilder {

    public UebernahmeRessourceMeldungKftBuilder(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, IOType ioType) {
        AbstractMeldungKftBuilder.withMeldungMetaData(this, ioType);
        withUebernahme(true);
        withSichererhafen(true);
        if (gfTyp.equals(GeschaeftsfallTyp.VA_KUE_MRN) || gfTyp.equals(GeschaeftsfallTyp.VA_RRNP)) {
            withPortierungskennungPKIauf("D000");
        }

        addLeitung(new LeitungKftBuilder(wbciCdmVersion).build());

        addMeldungPosition(new MeldungPositionUebernahmeRessourceMeldungBuilder()
                .withMeldungsCode(MeldungsCode.AKMTR_CODE)
                .withMeldungsText(MeldungsCode.AKMTR_CODE.getStandardText())
                .build());
    }

    public UebernahmeRessourceMeldungKftBuilder withLeitungen(Leitung... leitungen) {
        this.leitungen = new HashSet();
        for (Leitung leitung : leitungen) {
            addLeitung(leitung);
        }
        return this;
    }

    public UebernahmeRessourceMeldungKftBuilder withoutUebernahme() {
        withUebernahme(false);
        withSichererhafen(true);
        this.leitungen = new HashSet();
        return this;
    }

}
