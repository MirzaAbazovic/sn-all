package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Strasse;
import de.mnet.wbci.model.WbciCdmVersion;

public class StrasseTestBuilder extends StrasseBuilder implements WbciTestBuilder<Strasse> {
    private MeldungsCode code;

    @Override
    public Strasse buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (strassenname == null && (code == null || MeldungsCode.ADASTR.equals(code))) {
            strassenname = "Emmy-Noether-Straße";
        }
        if (hausnummer == null && (code == null || MeldungsCode.ADAHSNR.equals(code))) {
            hausnummer = "2";
        }
        if (hausnummernZusatz == null && (code == null || !code.isADACode())) {
            hausnummernZusatz = "a";
        }
        return build();
    }

    public Strasse buildEmpty(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        return build();
    }

    public StrasseTestBuilder withConsiderationOfMeldungsCode(MeldungsCode code) {
        this.code = code;
        return this;
    }
}
