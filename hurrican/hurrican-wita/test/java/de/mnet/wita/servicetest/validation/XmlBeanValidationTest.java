/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.06.2011 18:00:05
 */
package de.mnet.wita.servicetest.validation;

import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.IntAccountDAO;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.mnet.common.servicetest.AbstractServiceTest;

@Test(groups = BaseTest.SERVICE)
public class XmlBeanValidationTest extends AbstractServiceTest {

    @Autowired
    IntAccountDAO intAccountDao;

    @Test(expectedExceptions = PropertyValueException.class)
    public void testNotNullValidationError() {
        // Das Feld IntAccount.account muss not-null sein
        IntAccount account = new IntAccount();
        Assert.isNull(account.getAccount());
        intAccountDao.store(account);
    }

    public void testNotNullValidation() {
        IntAccount account = new IntAccount();
        account.setAccount("validation-account");
        intAccountDao.store(account);
    }

}
