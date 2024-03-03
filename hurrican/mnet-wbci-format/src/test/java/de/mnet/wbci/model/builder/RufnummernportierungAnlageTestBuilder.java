package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciCdmVersion;

public class RufnummernportierungAnlageTestBuilder extends RufnummernportierungAnlageBuilder implements
        WbciTestBuilder<RufnummernportierungAnlage> {
    @Override
    public RufnummernportierungAnlage buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (onkz == null) {
            onkz = "089";
        }
        if (durchwahlnummer == null) {
            durchwahlnummer = "456";
        }
        if (abfragestelle == null) {
            abfragestelle = "100";
        }
        if (rufnummernbloecke == null) {
            rufnummernbloecke = Arrays.asList(
                    new RufnummernblockTestBuilder().buildValid(wbciCdmVersion, gfTyp),
                    new RufnummernblockTestBuilder()
                            .withRnrBlockVon("900")
                            .withRnrBlockBis("950")
                            .buildValid(wbciCdmVersion, gfTyp)
            );
        }
        if (portierungszeitfenster == null) {
            portierungszeitfenster = Portierungszeitfenster.ZF2;
        }

        if ((GeschaeftsfallTyp.VA_KUE_MRN.equals(gfTyp) || GeschaeftsfallTyp.VA_RRNP.equals(gfTyp))
                && portierungskennungPKIauf == null) {
            portierungskennungPKIauf = "D123";
        }
        return build();
    }

    @Override
    public RufnummernportierungAnlageTestBuilder withRufnummernbloecke(List<Rufnummernblock> rufnummernbloecke) {
        return (RufnummernportierungAnlageTestBuilder) super.withRufnummernbloecke(rufnummernbloecke);
    }

    @Override
    public RufnummernportierungAnlageTestBuilder withOnkz(String onkz) {
        return (RufnummernportierungAnlageTestBuilder) super.withOnkz(onkz);
    }

    @Override
    public RufnummernportierungAnlageTestBuilder withDurchwahlnummer(String durchwahlnummer) {
        return (RufnummernportierungAnlageTestBuilder) super.withDurchwahlnummer(durchwahlnummer);
    }

    @Override
    public RufnummernportierungAnlageTestBuilder withAbfragestelle(String abfragestelle) {
        return (RufnummernportierungAnlageTestBuilder) super.withAbfragestelle(abfragestelle);
    }

    @Override
    public RufnummernportierungAnlageTestBuilder addRufnummernblock(Rufnummernblock rufnummernblock) {
        return (RufnummernportierungAnlageTestBuilder) super.addRufnummernblock(rufnummernblock);
    }

    @Override
    public RufnummernportierungAnlageTestBuilder withPortierungskennungPKIauf(String portierungskennungPKIauf) {
        return (RufnummernportierungAnlageTestBuilder) super.withPortierungskennungPKIauf(portierungskennungPKIauf);
    }
}
