package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.EndstelleLtgDatenBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.LeitungsartBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.SchnittstelleBuilder;
import de.augustakom.hurrican.model.cc.consistence.HistoryConsistence;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class ConsistenceCheckDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private ConsistenceCheckDAOImpl consistenceCheckDAO;

    @Autowired
    VerlaufAbteilungDAO verlaufAbteilungDAO;

    @Test
    public void testFindMultipleUsedIntAccountIds() {
        List<String> multipleUsedIntAccountIds = consistenceCheckDAO.findMultipleUsedIntAccountIds();
        assertFalse(multipleUsedIntAccountIds.isEmpty());
        for (String multipleUsedIntAccountId : multipleUsedIntAccountIds) {
            assertNotNull(multipleUsedIntAccountId);
        }
    }

    @Test
    public void testCheckHistoryConsistenceAuftragDaten() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(AuftragDaten.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);;

        getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(AuftragDaten.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        getBuilder(AuftragDatenBuilder.class)
                .withAuftragId(auftragBuilder.get().getAuftragId())
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(3).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(AuftragDaten.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences + 1);
    }

    @Test
    public void testCheckHistoryConsistenceAuftragTechnik() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(AuftragTechnik.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);;

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(AuftragTechnik.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragId(auftragBuilder.get().getAuftragId())
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(3).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(AuftragTechnik.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences+1);
    }

    @Test
    public void testCheckHistoryConsistenceRangierungEqIn() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(Rangierung.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class);;

        getBuilder(RangierungBuilder.class)
                .withFreigegeben(Rangierung.Freigegeben.freigegeben)
                .withEqInBuilder(equipmentBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(Rangierung.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        getBuilder(RangierungBuilder.class)
                .withFreigegeben(Rangierung.Freigegeben.freigegeben)
                .withEqInBuilder(equipmentBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(3).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(Rangierung.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences+1);
    }

    @Test
    public void testCheckHistoryConsistenceRangierungEqOut() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(Rangierung.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class);;

        getBuilder(RangierungBuilder.class)
                .withFreigegeben(Rangierung.Freigegeben.freigegeben)
                .withEqOutBuilder(equipmentBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(Rangierung.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        getBuilder(RangierungBuilder.class)
                .withFreigegeben(Rangierung.Freigegeben.freigegeben)
                .withEqOutBuilder(equipmentBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(3).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(Rangierung.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences+1);
    }

    @Test
    public void testCheckHistoryConsistenceIntAccount() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(IntAccount.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        String account = "asdf123";
        Integer liNr = 12;

        getBuilder(IntAccountBuilder.class)
                .withAccount(account)
                .withLiNr(liNr)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(IntAccount.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        getBuilder(IntAccountBuilder.class)
                .withAccount(account)
                .withLiNr(liNr)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(3).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(IntAccount.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences+1);
    }

    @Test
    public void testCheckHistoryConsistenceEndstelleLtgDaten() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(EndstelleLtgDaten.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        LeitungsartBuilder leitungsartBuilder =
                getBuilder(LeitungsartBuilder.class)
                        .withId(9999999999999L);
        SchnittstelleBuilder schnittstelleBuilder =
                getBuilder(SchnittstelleBuilder.class)
                        .withId(9999999999999L);

        getBuilder(EndstelleLtgDatenBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withLeitungsartBuilder(leitungsartBuilder)
                .withSchnittstelleBuilder(schnittstelleBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(1).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(EndstelleLtgDaten.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        getBuilder(EndstelleLtgDatenBuilder.class)
                .withEndstelleId(endstelleBuilder.get().getId())
                .withLeitungsartBuilder(leitungsartBuilder)
                .withSchnittstelleBuilder(schnittstelleBuilder)
                .withGueltigBis(Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(EndstelleLtgDaten.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences+1);
    }

    @Test
    public void testCheckHistoryConsistenceEndstelleAnsprechpartner() {
        List<HistoryConsistence> historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(EndstelleAnsprechpartner.class);
        int currenctNumberOfInconsistences = historyConsistenceList.size();
        Endstelle endstelle = getBuilder(EndstelleBuilder.class).build();
        assert endstelle.getId() != null;

        EndstelleAnsprechpartner endstelleAnsprechpartner =
                createEndstelleAnsprechpartner(endstelle.getId(), Date.from(LocalDateTime.now().plusMonths(1).atZone(ZoneId.systemDefault()).toInstant()));
        endstelleAnsprechpartner = verlaufAbteilungDAO.store(endstelleAnsprechpartner);
        assert endstelleAnsprechpartner.getId() != null;
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(EndstelleAnsprechpartner.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences);

        EndstelleAnsprechpartner endstelleAnsprechpartner1 =
                createEndstelleAnsprechpartner(endstelle.getId(), Date.from(LocalDateTime.now().plusMonths(2).atZone(ZoneId.systemDefault()).toInstant()));
        endstelleAnsprechpartner1 = verlaufAbteilungDAO.store(endstelleAnsprechpartner1);
        assert endstelleAnsprechpartner1.getId() != null;
        flushAndClear();
        historyConsistenceList = consistenceCheckDAO.checkHistoryConsistence(EndstelleAnsprechpartner.class);
        assertEquals(historyConsistenceList.size(), currenctNumberOfInconsistences+1);
    }

    private EndstelleAnsprechpartner createEndstelleAnsprechpartner(Long endstelleId, Date gueltigBis) {
        EndstelleAnsprechpartner endstelleAnsprechpartner = new EndstelleAnsprechpartner();
        endstelleAnsprechpartner.setEndstelleId(endstelleId);
        endstelleAnsprechpartner.setAnsprechpartner("Ansprechpartner");
        endstelleAnsprechpartner.setGueltigBis(gueltigBis);
        return endstelleAnsprechpartner;
    }

}
