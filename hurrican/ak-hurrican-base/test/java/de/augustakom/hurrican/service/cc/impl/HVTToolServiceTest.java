/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 16:27:03
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import java.util.stream.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.HVTBestellungBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.UEVTBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTToolService;

/**
 * JUnit-TestCase fuer <code>HVTToolService</code>.
 *
 *
 *
 */
@Test(groups = { "service" })
public class HVTToolServiceTest extends AbstractHurricanBaseServiceTest {

    private class ConfirmCallback implements IServiceCallback {
        @Override
        public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
            if (callbackAction == HVTToolService.CALLBACK_CONFIRM) {
                return Boolean.TRUE;
            }
            return null;
        }
    }

    private HVTToolService hvtToolService;
    private final ConfirmCallback confirmCallback = new ConfirmCallback();

    @BeforeMethod
    public void initHVTToolService() {
        hvtToolService = getCCService(HVTToolService.class);
    }

    @DataProvider(name = "dataProviderActivateMdu")
    protected Object[][] dataProviderActivateMdu() {
        return new Object[][] {
                { new java.sql.Date(System.currentTimeMillis()) }
        };
    }

    @DataProvider(name = "dataProviderFindCudaDiffViews")
    protected Object[][] dataProviderFindCudaDiffViews() {
        return new Object[][] {
                { 1, 3 }
        };
    }

    @DataProvider(name = "dataProviderGetZVRangierung")
    protected Object[][] dataProviderGetZVRangierung() {
        return new Object[][] {
                { null, "LZV-400491", "04" }
        };
    }

    @DataProvider(name = "dataProviderCreateZVRangierung")
    protected Object[][] dataProviderCreateZVRangierung() {
        return new Object[][] {
                { null, "LZV-400104", "19" }
        };
    }

    @DataProvider(name = "dataProviderFindEquipmentBelegung")
    protected Object[][] dataProviderFindEquipmentBelegung() {
        return new Object[][] {
                { 13L }
        };
    }

    /**
     * Test fuer die Methode HVTToolService#findEquipmentBelegung(Long)
     */
    @Test(dataProvider = "dataProviderFindEquipmentBelegung")
    public void testFindEquipmentBelegung(Long uevtId) throws Exception {
        List<EquipmentBelegungView> result = hvtToolService.findEquipmentBelegung(uevtId);
        assertNotEmpty(result, "Belegung fuer UEVT konnte nicht ermittelt werden!");
        Set<String> switchSet =
                result.stream()
                        .map(EquipmentBelegungView::getSwitchAK)
                        .collect(Collectors.toSet());
        Assert.assertTrue(switchSet.size() > 1);
        Assert.assertTrue(switchSet.contains(null));
    }

    /**
     * Test fuer die Methode HVTToolService#activateMdu
     */
    @Test(enabled = false, dataProvider = "dataProviderActivateMdu", expectedExceptions = IllegalArgumentException.class)
    public void testMissingGeraeteBezActivateMdu(java.sql.Date timestamp) throws Exception {
        HWMduBuilder hwMduBuilder = getHWMduBuilder();
        HWMdu hwMdu = hwMduBuilder.get();
        hvtToolService.activateMDU(hwMdu.getGeraeteBez(), timestamp);
    }

    private HWMduBuilder getHWMduBuilder() {
        return getBuilder(HWMduBuilder.class);
    }

    @DataProvider(name = "dataProviderFillUevtPreconditionsFail")
    protected Object[][] dataProviderFillUevtPreconditionsFail() {
        // @formatter:off
        return new Object[][] {
                { null, null, null, null },
                { 1L, null, null, null },
                { 1L, "", null, null },
                { 1L, "1", null, null },
                { 1L, "123", null, null },
                { 1L, "01", null, null },
                { 1L, "01", 1, null },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFillUevtPreconditionsFail", expectedExceptions = StoreException.class)
    public void testFillUevtPreconditionsFail(Long hvtBestellungId, String leiste1, Integer uevtClusterNo,
            IServiceCallback serviceCallback) throws StoreException {
        hvtToolService.fillUevt(hvtBestellungId, leiste1, null, uevtClusterNo, serviceCallback, true, -1, null);
    }

    @Test()
    public void testFillUevtSuccess() throws StoreException {
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);
        UEVTBuilder uevtBuilder = getBuilder(UEVTBuilder.class);
        HVTBestellungBuilder hvtBestellungBuilder = getBuilder(HVTBestellungBuilder.class);
        HVTBestellung hvtBestellung = hvtBestellungBuilder.withUevtBuilder(
                uevtBuilder.withHvtStandortBuilder(hvtStandortBuilder)).build();
        hvtToolService.fillUevt(hvtBestellung.getId(), "01", "A001", -1, confirmCallback, true, -1, null);
    }

}
