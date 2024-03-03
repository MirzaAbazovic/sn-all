package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;

public class WbciGeschaeftsfallKueMrnBuilder extends WbciGeschaeftsfallKueBuilder<WbciGeschaeftsfallKueMrn> {

    protected Rufnummernportierung rufnummernportierung;

    @Override
    public WbciGeschaeftsfallKueMrn build() {
        WbciGeschaeftsfallKueMrn geschaeftsfall = new WbciGeschaeftsfallKueMrn();
        enrich(geschaeftsfall);
        geschaeftsfall.setRufnummernportierung(rufnummernportierung);
        return geschaeftsfall;
    }

    public WbciGeschaeftsfallKueMrnBuilder withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
        return this;
    }

}
