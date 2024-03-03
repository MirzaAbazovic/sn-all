/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.14
 */
package de.mnet.wbci.helper;

import static de.mnet.wbci.TestGroups.*;
import static org.testng.Assert.*;

import java.util.*;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class RufnummerHelperTest {

    public void testConvertWbciEinzelrufnummer() {
        RufnummerOnkz onkz1 = new RufnummerOnkzBuilder().withOnkz("89").withRufnummer("1111112").build();
        RufnummerOnkz onkz2 = new RufnummerOnkzBuilder().withOnkz("89").withRufnummer("1111111").build();
        List<RufnummerOnkz> onkzs = new ArrayList<>();
        onkzs.add(onkz1);
        onkzs.add(onkz2);

        RufnummernportierungEinzeln rnp = new RufnummernportierungEinzelnTestBuilder().withRufnummerOnkzs(onkzs)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Set<String> result = RufnummerHelper.convertWbciEinzelrufnummer(rnp);
        assertFalse(CollectionUtils.isEmpty(result));
        assertEquals(result.size(), 2);
        Iterator<String> it = result.iterator();
        assertEquals(it.next(), "089/1111111");
        assertEquals(it.next(), "089/1111112");
    }


    public void testConvertWbciRufnummerAnlage() {
        Rufnummernblock block1 = new RufnummernblockBuilder().withRnrBlockVon("50").withRnrBlockBis("99").build();
        Rufnummernblock block2 = new RufnummernblockBuilder().withRnrBlockVon("00").withRnrBlockBis("49").build();
        RufnummernportierungAnlage anlage = new RufnummernportierungAnlageTestBuilder()
                .withOnkz("89").withDurchwahlnummer("4500").withAbfragestelle("0")
                .addRufnummernblock(block1)
                .addRufnummernblock(block2)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        Set<String> result = RufnummerHelper.convertWbciRufnummerAnlage(anlage);
        assertFalse(CollectionUtils.isEmpty(result));
        assertEquals(result.size(), 2);
        Iterator<String> it = result.iterator();
        assertEquals(it.next(), "089/4500 (0) - 00 bis 49");
        assertEquals(it.next(), "089/4500 (0) - 50 bis 99");
    }

}
