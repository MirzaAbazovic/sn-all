package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.WbciCdmVersion;

public class FirmaTestBuilder extends FirmaBuilder implements WbciTestBuilder<Firma> {

    @Override
    public Firma buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (firmenname == null) {
            firmenname = "Company X";
        }
        if (firmennamenZusatz == null) {
            firmennamenZusatz = "GmbH";
        }
        if (anrede == null) {
            anrede = Anrede.FIRMA;
        }
        if (kundenTyp == null) {
            kundenTyp = KundenTyp.GK;
        }
        return build();
    }

    @Override
    public FirmaTestBuilder withFirmename(String firmenname) {
        super.withFirmename(firmenname);
        return this;
    }

    @Override
    public FirmaTestBuilder withFirmennamenZusatz(String firmennamenZusatz) {
        super.withFirmennamenZusatz(firmennamenZusatz);
        return this;
    }
}
