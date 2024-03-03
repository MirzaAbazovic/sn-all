/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2011 11:55:24
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Testklasse fuer {@link MigrationHwSwitchTypes}.
 *
 *
 * @since Release 10
 */
@Test(groups = { BaseTest.UNIT })
public class MigrationHwSwitchTypesTest extends BaseTest {

    private MigrationHwSwitchTypes cut;

    @BeforeMethod
    void reset() {
        cut = new MigrationHwSwitchTypes();
    }

    @Test(groups = BaseTest.UNIT)
    public void create_NullSet() {
        final HWSwitchType source = null;
        final HWSwitchType destination = null;
        cut = MigrationHwSwitchTypes.create(source, destination);
        assertNull(cut.getSource());
        assertNull(cut.getDestination());
    }

    @Test(groups = BaseTest.UNIT)
    public void create_ProperlySet() {
        final HWSwitchType source = HWSwitchType.IMS;
        final HWSwitchType destination = HWSwitchType.IMS;
        cut = MigrationHwSwitchTypes.create(source, destination);
        assertEquals(cut.getSource(), source);
        assertEquals(cut.getDestination(), destination);
    }

    @DataProvider(name = "dataProviderIsSourceHwSwitchImsAndDestinationHwSwitchNot")
    protected Object[][] dataProviderIsSourceHwSwitchImsAndDestinationHwSwitchNot() {
        // @formatter:off
        return new Object[][] {
                {             null,                      null,          false },
                { HWSwitchType.IMS,         HWSwitchType.EWSD,          true },
                { HWSwitchType.IMS,         HWSwitchType.SOFTSWITCH,    true },
                { HWSwitchType.IMS,         HWSwitchType.IMS,           false },
                { HWSwitchType.EWSD,        HWSwitchType.SOFTSWITCH,    false },
                { HWSwitchType.EWSD,        HWSwitchType.IMS,           false },
                { HWSwitchType.EWSD,        HWSwitchType.IMS,           false },
                { HWSwitchType.EWSD,        HWSwitchType.EWSD,          false },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.NSP,           false },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.IMS,           false },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.EWSD,          false },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.SOFTSWITCH,    false }
        };
        // @formatter:on
    }

    @Test(groups = BaseTest.UNIT, dataProvider = "dataProviderIsSourceHwSwitchImsAndDestinationHwSwitchNot")
    public void isSourceHwSwitchImsAndDestinationHwSwitchNot(HWSwitchType source,
            HWSwitchType dest, boolean expectedResult) {
        MigrationHwSwitchTypes spy = Mockito.spy(cut);
        when(spy.getSource()).thenReturn(source);
        when(spy.getDestination()).thenReturn(dest);
        assertEquals(spy.isSourceHwSwitchImsAndDestinationHwSwitchNot(), expectedResult);
    }

    @DataProvider(name = "dataProviderIsDestinationHwSwitchImsAndSourceHwSwitchNot")
    protected Object[][] dataProviderIsDestinationHwSwitchImsAndSourceHwSwitchNot() {
        // @formatter:off
        return new Object[][] {
                {             null,                      null,          false },
                { HWSwitchType.IMS,         HWSwitchType.EWSD,          false },
                { HWSwitchType.IMS,         HWSwitchType.SOFTSWITCH,    false},
                { HWSwitchType.IMS,         HWSwitchType.IMS,           false },
                { HWSwitchType.EWSD,        HWSwitchType.SOFTSWITCH,    false },
                { HWSwitchType.EWSD,        HWSwitchType.IMS,           true },
                { HWSwitchType.EWSD,        HWSwitchType.EWSD,          false },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.IMS,           true },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.EWSD,          false },
                { HWSwitchType.SOFTSWITCH,  HWSwitchType.SOFTSWITCH,    false }
        };
        // @formatter:on
    }

    @Test(groups = BaseTest.UNIT, dataProvider = "dataProviderIsDestinationHwSwitchImsAndSourceHwSwitchNot")
    public void isDestinationHwSwitchImsAndSourceHwSwitchNot(HWSwitchType source,
            HWSwitchType dest, boolean expectedResult) {
        MigrationHwSwitchTypes spy = Mockito.spy(cut);
        when(spy.getSource()).thenReturn(source);
        when(spy.getDestination()).thenReturn(dest);
        assertEquals(spy.isDestinationHwSwitchImsAndSourceHwSwitchNot(), expectedResult);
    }

    @DataProvider(name = "isMigrationNeeded")
    protected Object[][] dataProviderIsMigrationNeeded() {
        // @formatter:off
        return new Object[][] {
                { false, false, false },
                { false, true,  true },
                { true,  false, true },
                { true,  true,  false }
        };
        // @formatter:on
    }

    @Test(groups = BaseTest.UNIT, dataProvider = "isMigrationNeeded")
    public void isMigrationNeeded(boolean sourceImsAndDestinationNot, boolean destinationImsAndSourceNot, boolean expectedResult) {
        MigrationHwSwitchTypes spy = Mockito.spy(cut);
        when(spy.isDestinationHwSwitchImsAndSourceHwSwitchNot()).thenReturn(destinationImsAndSourceNot);
        when(spy.isSourceHwSwitchImsAndDestinationHwSwitchNot()).thenReturn(sourceImsAndDestinationNot);
        assertEquals(spy.isMigrationNeeded(), expectedResult);
    }

} // end
