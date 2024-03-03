/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 * {@link EnableEinwahlaccountCommand} Unit Test
 */
@Test(groups = BaseTest.UNIT)
public class EnableEinwahlaccountCommandTest extends BaseTest {

    private CCLeistungsService ccLeistungsServiceMock;
    private AccountService accountServiceMock;

    private EnableEinwahlaccountCommand cut;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new EnableEinwahlaccountCommand();

        ccLeistungsServiceMock = mock(CCLeistungsService.class);
        cut.setCcLeistungsService(ccLeistungsServiceMock);

        accountServiceMock = mock(AccountService.class);
        cut.setAccountService(accountServiceMock);

    }

    @Test
    public void toggleAccount_WithoutAccount() throws Exception {
        when(accountServiceMock.findIntAccounts4Auftrag(any(Long.class), eq(IntAccount.LINR_EINWAHLACCOUNT)))
                .thenReturn(null);
        cut.enableAccount(true);
        verify(accountServiceMock, times(0)).saveIntAccount(any(IntAccount.class), eq(true));
    }

    @Test
    public void toggleAccount_accountAlreadyUnLocked() throws Exception {
        IntAccount account = new IntAccount();
        account.setGesperrt(Boolean.FALSE);
        when(accountServiceMock.findIntAccounts4Auftrag(any(Long.class), eq(IntAccount.LINR_EINWAHLACCOUNT)))
                .thenReturn(Arrays.asList(account));
        cut.enableAccount(false);
        verify(accountServiceMock, times(0)).saveIntAccount(any(IntAccount.class), eq(true));
    }

    @Test
    public void toggleAccount_UnlockAccount() throws Exception {
        IntAccount account = new IntAccount();
        account.setGesperrt(Boolean.TRUE);
        when(accountServiceMock.findIntAccounts4Auftrag(any(Long.class), eq(IntAccount.LINR_EINWAHLACCOUNT)))
                .thenReturn(Arrays.asList(account));
        cut.enableAccount(false);
        verify(accountServiceMock, times(1)).saveIntAccount(any(IntAccount.class), eq(true));
        assertEquals(account.getGesperrt(), Boolean.FALSE);
    }
}
