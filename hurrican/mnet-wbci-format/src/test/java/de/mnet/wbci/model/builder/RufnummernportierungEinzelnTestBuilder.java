package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.WbciCdmVersion;

public class RufnummernportierungEinzelnTestBuilder extends RufnummernportierungEinzelnBuilder implements
        WbciTestBuilder<RufnummernportierungEinzeln> {

    @Override
    public RufnummernportierungEinzeln buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (rufnummernListe == null) {
            rufnummernListe = Arrays.asList(
                    new RufnummerOnkzTestBuilder().buildValid(wbciCdmVersion, gfTyp),
                    new RufnummerOnkzTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }
        if (portierungszeitfenster == null) {
            portierungszeitfenster = Portierungszeitfenster.ZF2;
        }
        if (portierungskennungPKIauf == null) {
            portierungskennungPKIauf = "D001";
        }
        return build();
    }

    @Override
    public RufnummernportierungEinzelnTestBuilder withAlleRufnummernPortieren(Boolean alleRufnummernPortieren) {
        return (RufnummernportierungEinzelnTestBuilder) super.withAlleRufnummernPortieren(alleRufnummernPortieren);
    }

    @Override
    public RufnummernportierungEinzelnTestBuilder withRufnummerOnkzs(List<RufnummerOnkz> rufnummernListe) {
        return (RufnummernportierungEinzelnTestBuilder) super.withRufnummerOnkzs(rufnummernListe);
    }

    @Override
    public RufnummernportierungEinzelnTestBuilder addRufnummer(RufnummerOnkz rufnummer) {
        return (RufnummernportierungEinzelnTestBuilder) super.addRufnummer(rufnummer);
    }

    @Override
    public RufnummernportierungEinzelnTestBuilder withPortierungszeitfenster(Portierungszeitfenster zf) {
        return (RufnummernportierungEinzelnTestBuilder) super.withPortierungszeitfenster(zf);
    }

    @Override
    public RufnummernportierungEinzelnTestBuilder withPortierungskennungPKIauf(String portierungskennungPKIauf) {
        return (RufnummernportierungEinzelnTestBuilder) super.withPortierungskennungPKIauf(portierungskennungPKIauf);
    }
}
