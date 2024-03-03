/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2011 10:15:11
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Tests fuer {@link FinishVerlauf4AbteilungCommand}.
 *
 *
 * @since Release 10
 */
@Test(groups = BaseTest.UNIT)
public class FinishVerlauf4AbteilungCommandTest extends BaseTest {

    private FinishVerlauf4AbteilungCommand sut = Mockito.spy(new FinishVerlauf4AbteilungCommand());

    @Mock
    private IPAddressService ipAddressService;

    @BeforeMethod
    public void setup() {
        initMocks(this);
        sut.setIpAddressService(ipAddressService);
    }

    /**
     * Tests fuer {@link FinishVerlauf4AbteilungCommand#changeStati()}.
     *
     * @throws StoreException
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    @Test
    public void changeStati_Good() throws StoreException, FindException, ServiceNotFoundException {
        Verlauf verlauf = new Verlauf();
        verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN);
        prepareMocks(verlauf);
        sut.changeStati();
        verify(sut).doFinishFromAllTechAbt(Mockito.any(BAService.class), Mockito.any(CCAuftragService.class));
    }

    private void prepareMocks(Verlauf verlauf) throws StoreException, FindException, ServiceNotFoundException {
        doReturn(verlauf).when(sut).getVerlauf();
        doReturn(true).when(sut).isBAFinishedFromAllTechAbt();
        doReturn(false).when(sut).needsBARuecklaeufer();
        doNothing().when(sut).doFinishFromAllTechAbt(Mockito.any(BAService.class), Mockito.any(CCAuftragService.class));
    }

}
