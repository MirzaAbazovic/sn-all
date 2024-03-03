/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 14:02:53
 */
package de.mnet.wita.bpm.gateways;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;

@Test(groups = UNIT)
public class ProcessTamGatewayTest extends BaseTest {

    @InjectMocks
    private ProcessTamGateway underTest;

    @Mock
    private MwfEntityDao mwfEntityDao;

    @BeforeMethod
    public void setupMocks() {
        underTest = new ProcessTamGateway();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] dataProviderIsAbmWithFutureVlt() {
        // @formatter:off
        return new Object[][] {
                { -1, false },
                { +1, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsAbmWithFutureVlt")
    public void testIsAbmWithFutureVlt(int plusDays, boolean expectedResult) throws Exception {
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).withVerbindlicherLiefertermin(
                LocalDate.now().plusDays(plusDays)).build();

        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable(WitaTaskVariables.WITA_IN_MWF_ID.id)).thenReturn(123L);
        when(execution.getVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id)).thenReturn(MeldungsType.ABM.name());

        when(mwfEntityDao.findById(any(Long.class), eq(AuftragsBestaetigungsMeldung.class))).thenReturn(abm);

        assertEquals(underTest.isAbmWithFutureVlt(execution), expectedResult);
    }
}
