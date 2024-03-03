/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.13
 */
package de.mnet.wbci.model.helper;

import static de.mnet.wbci.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.*;
import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class TechnischeRessourceHelperTest {
    @Test
    public void testConvertToMap() throws Exception {
        assertThat(TechnischeRessourceHelper.convertToMap(new HashSet<TechnischeRessource>()).values(),
                Matchers.<TechnischeRessource>empty());

        Map<String, TechnischeRessource> result = TechnischeRessourceHelper.convertToMap(Sets.newHashSet(
                new TechnischeRessourceBuilder().build(),
                new TechnischeRessourceBuilder().withLineId("LINE-ID").build(),
                new TechnischeRessourceBuilder().withVertragsnummer("VERTRAGS-NR").build(),
                new TechnischeRessourceBuilder().withIdentifizierer("IDENT-NR").build()));

        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.containsKey("LINE-ID"));
        Assert.assertTrue(result.containsKey("VERTRAGS-NR"));
        Assert.assertTrue(result.containsKey("IDENT-NR"));
    }

    @Test
    public void testfilterOutKeys() throws Exception {
        assertThat(
                TechnischeRessourceHelper.filterKeys(new HashMap<String, TechnischeRessource>(),
                        Arrays.asList("BLA", "BLA")).values(), Matchers.<TechnischeRessource>empty()
        );

        Map<String, TechnischeRessource> testdata = TechnischeRessourceHelper.convertToMap(Sets.newHashSet(
                new TechnischeRessourceBuilder().withLineId("LINE-ID").build(),
                new TechnischeRessourceBuilder().withLineId("LINE-ID-1").build(),
                new TechnischeRessourceBuilder().withVertragsnummer("VERTRAGS-NR").build(),
                new TechnischeRessourceBuilder().withVertragsnummer("VERTRAGS-NR-2").build()));

        Map<String, TechnischeRessource> result = TechnischeRessourceHelper.filterKeys(testdata,
                Arrays.asList("LINE-ID", "VERTRAGS-NR"));

        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.containsKey("LINE-ID"));
        Assert.assertTrue(result.containsKey("VERTRAGS-NR"));
    }
}
