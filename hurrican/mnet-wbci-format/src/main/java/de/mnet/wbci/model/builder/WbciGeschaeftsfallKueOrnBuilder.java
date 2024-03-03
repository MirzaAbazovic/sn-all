package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;

public class WbciGeschaeftsfallKueOrnBuilder extends WbciGeschaeftsfallKueBuilder<WbciGeschaeftsfallKueOrn> {

    protected RufnummerOnkz anschlussIdentifikation;

    @Override
    public WbciGeschaeftsfallKueOrn build() {
        WbciGeschaeftsfallKueOrn geschaeftsfall = new WbciGeschaeftsfallKueOrn();
        enrich(geschaeftsfall);
        geschaeftsfall.setAnschlussIdentifikation(anschlussIdentifikation);
        return geschaeftsfall;
    }

    public WbciGeschaeftsfallKueOrnBuilder withAnschlussIdentifikation(RufnummerOnkz anschlussIdentifikation) {
        this.anschlussIdentifikation = anschlussIdentifikation;
        return this;
    }

}
