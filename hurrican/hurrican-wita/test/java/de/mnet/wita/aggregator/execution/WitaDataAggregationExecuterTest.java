/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 08:54:10
 */
package de.mnet.wita.aggregator.execution;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.aggregator.StandortKundeAggregator;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallNeu;

@Test(groups = UNIT)
public class WitaDataAggregationExecuterTest extends BaseTest {

    public void findCorrespondingSetterMethod() {
        Method method = WitaDataAggregationExecuter.findCorrespondingSetterMethod(new StandortKundeAggregator(), new GeschaeftsfallProdukt());
        assertNotNull(method);
        assertEquals(method.getName(), "setStandortKunde", "found method not as expected!");
        assertNotNull(method.getParameterTypes());
        assertEquals(method.getParameterTypes().length, 1);
        assertEquals(method.getParameterTypes()[0], StandortKunde.class);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void findCorrespondingSetterMethodOnWrongObject() {
        WitaDataAggregationExecuter.findCorrespondingSetterMethod(new StandortKundeAggregator(), new GeschaeftsfallNeu());
    }

}
