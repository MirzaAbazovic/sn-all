package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;

public abstract class WbciGeschaeftsfallKueBuilder<T extends WbciGeschaeftsfallKue> extends WbciGeschaeftsfallBuilder<T> {

    protected Standort standort;

    protected void enrich(T wbciGeschaeftsfall) {
        super.enrich(wbciGeschaeftsfall);
        wbciGeschaeftsfall.setStandort(standort);
    }

    public WbciGeschaeftsfallKueBuilder<T> withStandort(Standort standort) {
        this.standort = standort;
        return this;
    }

}
