package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.WbciCdmVersion;

public class RufnummernblockTestBuilder extends RufnummernblockBuilder implements WbciTestBuilder<Rufnummernblock> {
    @Override
    public Rufnummernblock buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        return buildValidList(wbciCdmVersion, gfTyp, 1).get(0);
    }

    public List<Rufnummernblock> buildValidList(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, int listSize) {
        if (rnrBlockVon == null) {
            rnrBlockVon = "1000";
        }
        if (rnrBlockBis == null) {
            rnrBlockBis = "1100";
        }
        if (pkiAbg == null) {
            pkiAbg = "D001";
        }
        List<Rufnummernblock> rufnummernblocks = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            rufnummernblocks.add(build());
        }
        return rufnummernblocks;
    }

    @Override
    public RufnummernblockTestBuilder withRnrBlockVon(String rnrBlockVon) {
        return (RufnummernblockTestBuilder) super.withRnrBlockVon(rnrBlockVon);
    }

    @Override
    public RufnummernblockTestBuilder withRnrBlockBis(String rnrBlockBis) {
        return (RufnummernblockTestBuilder) super.withRnrBlockBis(rnrBlockBis);
    }

    @Override
    public RufnummernblockTestBuilder withPkiAbg(String pkiAbg) {
        return (RufnummernblockTestBuilder) super.withPkiAbg(pkiAbg);
    }
}
