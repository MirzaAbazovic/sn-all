/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.06.2011 18:00:18
 */
package de.mnet.wita.servicetest.validation;

import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.AuftragConnectDAO;
import de.augustakom.hurrican.model.cc.AuftragConnect;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.auftrag.Kunde;

@Test(groups = BaseTest.SERVICE)
public class AnnotationBeanValidationTest extends AbstractServiceTest {

    @Autowired
    MwfEntityDao mwfEntityDao;
    @Autowired
    AuftragConnectDAO auftragConnectDao;

    @Test(expectedExceptions = PropertyValueException.class)
    public void testNotNullValidationMwfEntity() {
        // Das Feld Kunde.kundennummer muss not-null sein
        mwfEntityDao.store(new Kunde());
        commitTransactions();
    }

    @Test(expectedExceptions = PropertyValueException.class)
    public void testNotNullValidationHurricanEntity() {
        // Das Feld AuftragConnect.auftragId muss not-null sein
        auftragConnectDao.store(new AuftragConnect());
        commitTransactions();
    }

    /**
     * Die automatische Validierung wird leider erst beim Commit durchgeführt
     */
    private void commitTransactions() {
        if (txManagers != null) {
            for (int i = transactions.size() - 1; i >= 0; --i) {
                PlatformTransactionManager txManager = transactions.get(i).getFirst();
                TransactionStatus status = transactions.get(i).getSecond();
                if ((status != null) && !status.isCompleted()) {
                    txManager.commit(status);
                }
            }
        }
    }

}
