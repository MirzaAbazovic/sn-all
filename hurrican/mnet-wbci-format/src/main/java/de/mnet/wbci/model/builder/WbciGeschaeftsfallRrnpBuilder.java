package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;

public class WbciGeschaeftsfallRrnpBuilder extends WbciGeschaeftsfallBuilder<WbciGeschaeftsfallRrnp> {

    protected Rufnummernportierung rufnummernportierung;

    @Override
    public WbciGeschaeftsfallRrnp build() {
        WbciGeschaeftsfallRrnp geschaeftsfall = new WbciGeschaeftsfallRrnp();
        enrich(geschaeftsfall);
        geschaeftsfall.setRufnummernportierung(rufnummernportierung);
        return geschaeftsfall;
    }

    public WbciGeschaeftsfallRrnpBuilder withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
        return this;
    }

}
