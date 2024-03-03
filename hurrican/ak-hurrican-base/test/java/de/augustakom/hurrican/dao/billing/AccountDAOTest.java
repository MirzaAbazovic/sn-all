package de.augustakom.hurrican.dao.billing;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Account;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class AccountDAOTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private AccountDAO accountDAO;

    @Test
    public void testSaveAndUpdateAccount() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory().surfAndFonWithDns(1).persist();
        final Long auftragNo = generatedTaifunData.getBillingAuftrag().getAuftragNo();
        final String accountName = "test123456";
        final String password = "testpassword";
        accountDAO.saveAccount(auftragNo,
                accountName,
                password
        );
        List<Account> accounts4Auftrag = accountDAO.findAccounts4Auftrag(auftragNo, null);
        Assert.assertTrue(accounts4Auftrag.stream().anyMatch(
                        a -> accountName.equals(a.getAccountName()) && password.equals(a.getPassword())),
                "no valid account found"
        );

        final String newPassword = "newPassword";
        accountDAO.updatePassword(auftragNo, accountName, newPassword);
        accounts4Auftrag = accountDAO.findAccounts4Auftrag(auftragNo, null);
        Assert.assertTrue(accounts4Auftrag.stream().anyMatch(
                        a -> accountName.equals(a.getAccountName()) && newPassword.equals(a.getPassword())),
                "no valid updated account found"
        );
    }
}