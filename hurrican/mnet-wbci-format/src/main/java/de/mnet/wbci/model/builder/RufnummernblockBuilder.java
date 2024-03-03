package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Rufnummernblock;

public class RufnummernblockBuilder implements WbciBuilder<Rufnummernblock> {

    protected String rnrBlockVon;
    protected String rnrBlockBis;
    protected String pkiAbg;

    @Override
    public Rufnummernblock build() {
        Rufnummernblock rufnummernblock = new Rufnummernblock();
        rufnummernblock.setRnrBlockVon(rnrBlockVon);
        rufnummernblock.setRnrBlockBis(rnrBlockBis);
        rufnummernblock.setPortierungskennungPKIabg(pkiAbg);
        return rufnummernblock;
    }

    public RufnummernblockBuilder withRnrBlockVon(String rnrBlockVon) {
        this.rnrBlockVon = rnrBlockVon;
        return this;
    }

    public RufnummernblockBuilder withRnrBlockBis(String rnrBlockBis) {
        this.rnrBlockBis = rnrBlockBis;
        return this;
    }

    public RufnummernblockBuilder withPkiAbg(String pkiAbg) {
        this.pkiAbg = pkiAbg;
        return this;
    }

}
