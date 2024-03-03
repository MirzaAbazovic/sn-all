package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.exceptions.ModelCalculationException;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;

/**
 * Modultests fuer {@link HWServiceImpl}.
 */
@Test(groups = BaseTest.UNIT)
public class HWServiceImplUnitTest {

    @Mock
    HardwareDAO hwDAO;
    @Mock(name="hwRackValidator")
    AbstractValidator hwRackValidator;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    RangierungFreigabeService rangierungFreigabeService;


    @Spy
    @InjectMocks
    HWServiceImpl cut;

    @BeforeMethod
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] dataProviderGenerateOltChildIp() {
        // @formatter:off
       return new Object[][] {
                { "", 1, 0, 0, null },
                { "192.168.1", 1, 0, 0, null },
                { "192.168.1.1", 2, 1, 1, "192.168.2.34" },
                { "192.168.1.1", 2, 1, 0, "192.168.2.33" },
                { "192.168.1.1", 2, 0, 1, "192.168.2.2" },
                { "192.168.1.1", 2, 3, 4, "192.168.2.101" },
                { "192.168.1.1", 8, 0, 0, "192.168.8.1" },
                { "192.168.1.1", 11, 0, 0, "192.168.9.1" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGenerateOltChildIp")
    public void testGenerateOltChildIp(String ipVon, int oltSlot, int gponPort, int gponId, String expected) throws ModelCalculationException {
        HWOltBuilder oltBuilder = new HWOltBuilder().withRandomId().withIpNetzVon(ipVon)
                .setPersist(false);
        HWOntBuilder ontBuilder = new HWOntBuilder()
                .withOltSlot(Integer.valueOf(oltSlot).toString())
                .withOltGPONPort(Integer.valueOf(gponPort).toString())
                .withOltGPONId(Integer.valueOf(gponId).toString())
                .setPersist(false);
        String result = cut.generateOltChildIp(oltBuilder.get(), ontBuilder.get());
        assertEquals(result, expected);
    }

    @Test
    public void testFreigabeDPU() throws Exception {
        final Date date = new Date();
        final Long rackId = 12335321L;
        final HWDpu hwDpu = new HWDpu();
        hwDpu.setId(rackId);
        when(hwDAO.findById(rackId, HWRack.class)).thenReturn(hwDpu);
        cut.setHwRackValidator(hwRackValidator);
        when(applicationContext.getBean(RangierungFreigabeService.class.getName(), RangierungFreigabeService.class))
                .thenReturn(rangierungFreigabeService);

        cut.freigabeDPU(rackId, date);

        verify(hwDAO).findById(rackId, HWRack.class);
        verify(hwDAO).store(hwDpu);
        verify(rangierungFreigabeService).freigabeMduDpuRangierungen(rackId, date);
    }

}
