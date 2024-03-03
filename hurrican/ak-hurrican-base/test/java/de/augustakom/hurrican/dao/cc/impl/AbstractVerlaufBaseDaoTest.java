/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2015
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Helper class for encapsulating common verlauf test functionality
 */
public class AbstractVerlaufBaseDaoTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    VerlaufAbteilungDAO verlaufAbteilungDAO;

    protected Verlauf createTestVerlauf() {
        Auftrag testAuftrag = createTestAuftrag();
        return createTestVerlauf(testAuftrag, Boolean.TRUE);
    }

    protected Verlauf createTestVerlauf(Auftrag testAuftrag, Boolean withProjektierung) {
        return createTestVerlauf(testAuftrag, withProjektierung, BAVerlaufAnlass.NEUSCHALTUNG);
    }

    protected Verlauf createTestVerlauf(Auftrag testAuftrag, Boolean withProjektierung, Long anlass) {
        return createTestVerlauf(testAuftrag, withProjektierung, anlass, DateConverterUtils.asDate(LocalDate.now()));
    }

    protected Verlauf createTestVerlauf(Auftrag testAuftrag, Boolean withProjektierung, Long anlass, Date realisierungstermin) {
        VerlaufBuilder bauauftragBuilder = new VerlaufBuilder()
                .withAnlass(anlass)
                .withRealisierungstermin(realisierungstermin)
                .withAkt(true)
                .withProjektierung(withProjektierung)
                .withObserveProcess(false)
                .withNotPossible(true)
                .setPersist(false);
        Verlauf verlauf = bauauftragBuilder.get();
        verlauf.setAuftragId(testAuftrag.getAuftragId());
        verlauf = verlaufAbteilungDAO.store(verlauf);
        flushAndClear();
        return verlauf;
    }

    protected VerlaufAbteilung createTestVerlaufAbteilung(Long verlaufId, Long verlaufStatus, Long abteilungId) {
        return createTestVerlaufAbteilung(verlaufId, verlaufStatus, abteilungId, Date.from(ZonedDateTime.now().toInstant()));
    }

    protected VerlaufAbteilung createTestVerlaufAbteilung(Long verlaufId, Long verlaufStatus, Long abteilungId, Date realisierungsdatum) {
        VerlaufAbteilungBuilder abteilungBuilder = new VerlaufAbteilungBuilder()
                .withAbteilungId(abteilungId)
                .withVerlaufStatusId(verlaufStatus)
                .withBearbeiter("some bearbeiter")
                .withRealisierungsdatum(realisierungsdatum)
                .setPersist(false);
        VerlaufAbteilung verlaufAbteilung = abteilungBuilder
                .get();
        verlaufAbteilung.setVerlaufId(verlaufId);
        verlaufAbteilung = verlaufAbteilungDAO.store(verlaufAbteilung);

        flushAndClear();

        return verlaufAbteilung;
    }

    protected Auftrag createTestAuftrag() {
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(
                        getBuilder(AuftragDatenBuilder.class)
                                .withStatusId(AuftragStatus.IN_BETRIEB)
                                .withBuendelNr(0)
                                .withVorgabeSCV(Date.from(ZonedDateTime.now().toInstant()))
                )
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class))
                .build();

        flushAndClear();

        return auftrag;
    }
}
