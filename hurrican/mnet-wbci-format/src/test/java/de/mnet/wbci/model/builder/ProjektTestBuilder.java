package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.WbciCdmVersion;

public class ProjektTestBuilder extends ProjektBuilder implements WbciTestBuilder<Projekt> {
    @Override
    public Projekt buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (projektKenner == null) {
            projektKenner = "Projektkenner";
        }
        if (kopplungsKenner == null) {
            kopplungsKenner = "Kopplungskenner";
        }
        return build();
    }
}
