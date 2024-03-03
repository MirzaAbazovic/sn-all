package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.WbciCdmVersion;

public class RufnummerOnkzTestBuilder extends RufnummerOnkzBuilder implements WbciTestBuilder<RufnummerOnkz> {
    @Override
    public RufnummerOnkz buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        return buildValidList(wbciCdmVersion, gfTyp, 1).get(0);
    }

    public List<RufnummerOnkz> buildValidList(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, int listSize) {
        if (onkz == null) {
            onkz = "089";
        }
        if (rufnummer == null) {
            rufnummer = "123456789";
        }
        List<RufnummerOnkz> rufnummerOnkzs = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            rufnummerOnkzs.add(build());
        }
        return rufnummerOnkzs;
    }

    public RufnummerOnkzTestBuilder withRufnummer(String rufnummer) {
        return (RufnummerOnkzTestBuilder) super.withRufnummer(rufnummer);
    }

    @Override
    public RufnummerOnkzTestBuilder withOnkz(String onkz) {
        return (RufnummerOnkzTestBuilder) super.withOnkz(onkz);
    }
}
