package de.mnet.migration.hurrican.evn;

import static org.testng.Assert.*;

import org.apache.commons.lang.reflect.FieldUtils;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.env.Environment;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.EvnService;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.InitializeLog4J;

/**
 * UT for {@link AccountEvnTransformer}
 */
public class AccountEvnTransformerTest {
    private AccountEvnTransformer transformer;

    @Mock
    private EvnService evnService;
    @Mock
    private AccountService accountService;
    @Spy
    private AccountEvnMigIdHolder migIdHolder = new AccountEvnMigIdHolder();
    @Mock
    private Environment springEnvironment;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        InitializeLog4J.initializeLog4J("log4j-migration");
        MockitoAnnotations.initMocks(this);
        transformer = new AccountEvnTransformer();
        FieldUtils.writeField(transformer, "evnService", evnService, true);
        FieldUtils.writeField(transformer, "accountService", accountService, true);
        FieldUtils.writeField(transformer, "migIdHolder", migIdHolder, true);
        FieldUtils.writeField(transformer, "springEnvironment", springEnvironment, true);
    }

    @Test
    public void testTransform_Nulls() throws Exception {
        final AccountEvn row = new AccountEvn(null, null);
        final TransformationResult result = transformer.transform(row);
        assertNotNull(result);
        assertEquals(result.getTranformationStatus(), TransformationStatus.BAD_DATA);
    }

    @Test
    public void testTransform_NotFound() throws Exception {
        final AccountEvn row = new AccountEvn("account_nr", true);
        final TransformationResult result = transformer.transform(row);
        assertNotNull(result);
        assertEquals(result.getTranformationStatus(), TransformationStatus.BAD_DATA);
    }

    @Test
    public void testTransform_Found() throws Exception {
        final AccountEvn row = new AccountEvn("X12345", false);

        final IntAccount intAccount = new IntAccount();
        intAccount.setAccount(row.accountNumber);
        when(accountService.findIntAccount(row.accountNumber)).thenReturn(intAccount);

        when(evnService.getAuftragByRadiusAccount(row.accountNumber)).thenReturn(mock(AuftragDaten.class, RETURNS_DEEP_STUBS));

        final TransformationResult result = transformer.transform(row);
        assertNotNull(result);
        assertNotNull(result.getTargetValues());
        assertTrue(result.getTargetValues().getList().size() >= 1);
        assertEquals(result.getTranformationStatus(), TransformationStatus.OK);
    }

}