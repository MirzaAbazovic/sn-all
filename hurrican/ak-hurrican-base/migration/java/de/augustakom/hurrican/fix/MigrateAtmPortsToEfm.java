/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.14
 */
package de.augustakom.hurrican.fix;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.testng.annotations.Test;
import org.hibernate.SessionFactory;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;

// TESTGRUPPE MUSS SERVICE TEST SEIN!!!!!!

// TODO: nur lokal bei Bedarf einschalten

/**
 * Migriert das Layer2-Protokoll freier 4er-Bloecke von ATM auf EFM WICHTIG! folgende (temporaere) Aenderungen muessen
 * im Vorfeld gemacht werden: - In 'TransactionBeanPostProcessortStatus' muss der Aufruf von 'setRollbackOnly()'
 * auskommentiert werden - In CCDataAccess.xml muss fuer die bean 'cc.hibernateTxManager' die property '<property
 * name="globalRollbackOnParticipationFailure" value="false" />' konfiguriert werden - Die gewünschte Umgebung (DB) über
 * die System Property <code>-Duse.config=...</code> setzen
 */
@Test(groups = BaseTest.SERVICE)
public class MigrateAtmPortsToEfm extends AbstractHurricanBaseServiceTest {
    private static final Logger LOGGER = Logger.getLogger(MigrateAtmPortsToEfm.class);

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    // @Resource(name="de.augustakom.hurrican.service.cc.RangierungFreigabeService")
    @Autowired
    private RangierungFreigabeService freigabeService;

    // TODO: nur lokal bei Bedarf einschalten!!!
    // @Test(enabled = true)
    public void migratePorts() throws Exception {
        LOGGER.debug("=== Start migration of ATM to EFM =============================================================");
        long startTime = System.nanoTime();

        // select count(*) from T_RANGIERUNG where freigegeben = '1' and es_id is null
        // int count = 313345;

        // 107: SDSL Alc-IP; 108: SHDSL: Alc-IP; 509: SDSL-DA (H); 515: SHDSL-DA (H)
        // select count(*) from T_RANGIERUNG where freigegeben = '1' and es_id is null and physik_typ in
        // (509,515,107,108)
        // int count = 4880;

        int checked = 0;
        int migrated = 0;
        int exceptions = 0;

        String selectRangierungen = "from Rangierung r where r.esId = null and r.freigegeben = '1' and r.physikTypId in (509,515,107,108)";
        List<Rangierung> rangierungen = sessionFactory.getCurrentSession().createQuery(selectRangierungen).list();

        for (Rangierung rangierung: rangierungen) {
            // if (migrated == 1) break;
            try {
                if (freigabeService.migrateAtm2EfmIfPossible(rangierung)) {
                    logMigration(rangierung);
                    migrated++;
                }
            }
            catch (Exception e) {
                System.out.println("Exception bei der Migration von " + rangierungToString(rangierung));
                System.out.println(e.getMessage());
                exceptions++;
            }

            if (checked % 10 == 0) {
                System.out.println(
                        "geprueft: " + checked + ", " +
                                "migriert: " + migrated + ", " +
                                "Exceptions: " + exceptions
                );
            }
            checked++;
        }

        double elapsedTimeInSeconds = (System.nanoTime() - startTime) / 1e9;
        double elapsedTimeInMinutes = elapsedTimeInSeconds / 60.;
        double elapsedTimeInHours = elapsedTimeInMinutes / 60.;

        System.out.println("");
        System.out.println("Rangierungen geprueft:  " + checked);
        System.out.println("Rangierungen migriert:  " + migrated);
        System.out.println("migriert/geprueft in %: " + (migrated / (double) checked) * 100);
        System.out.println("");
        System.out.println("Laufzeit in Sekunden: " + elapsedTimeInSeconds);
        System.out.println("Laufzeit in Minuten:  " + elapsedTimeInMinutes);
        System.out.println("Laufzeit in Stunden:  " + elapsedTimeInHours);

        LOGGER.debug("=== Stop migration of ATM to EFM ==============================================================");
    }

    private String rangierungToString(Rangierung rangierung) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rangierung(id = ");
        sb.append(rangierung.getId());
        sb.append(", EqInId = ");
        sb.append(rangierung.getEqInId());
        sb.append(", EqOutId = ");
        sb.append(rangierung.getEqOutId());
        sb.append(", HvtIdStandort = ");
        sb.append(rangierung.getHvtIdStandort());
        sb.append(")");
        return sb.toString();
    }

    private void logMigration(Rangierung rangierung) {
        System.out.println(rangierungToString(rangierung) + " migriert");
    }

    // commit instead of rollback
    protected void tryRollback(PlatformTransactionManager txManager, TransactionStatus status) {
        try {
            txManager.commit(status);
        }
        catch (Exception e) {
            LOGGER.warn(
                    "Exception trying to commit transaction for transaction manager " + txManagers.get(txManager), e);
        }
    }
}
