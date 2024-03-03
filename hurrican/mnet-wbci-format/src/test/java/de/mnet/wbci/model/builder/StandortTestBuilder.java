package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciCdmVersion;

public class StandortTestBuilder extends StandortBuilder implements WbciTestBuilder<Standort> {
    private MeldungsCode code;

    @Override
    public Standort buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (strasse == null && (code == null || MeldungsCode.ADAHSNR.equals(code) || MeldungsCode.ADASTR.equals(code))) {
            strasse = new StrasseTestBuilder().withConsiderationOfMeldungsCode(code).buildValid(wbciCdmVersion, gfTyp);
        }
        if (postleitzahl == null && (code == null || MeldungsCode.ADAPLZ.equals(code))) {
            postleitzahl = "80992";
        }
        if (ort == null && (code == null || MeldungsCode.ADAORT.equals(code))) {
            ort = "München";
        }
        return build();
    }

    public Standort buildEmpty(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        strasse = new StrasseTestBuilder().buildEmpty(wbciCdmVersion, gfTyp);
        return build();
    }

    public StandortTestBuilder withConsiderationOfMeldungsCode(MeldungsCode code) {
        this.code = code;
        return this;
    }
}
