/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.common.servicetest;

import static com.google.common.collect.Iterables.*;
import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;
import static de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.MeldungsType.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.common.service.HistoryService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.activiti.BusinessKeyUtils;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.TestAnlage;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.AnlagenDto;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.IoArchiveBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangAnlageBuilder;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.SERVICE)
public class HistoryServiceTest extends AbstractServiceTest {

    @Autowired
    private HistoryService underTest;

    @Autowired
    private Provider<IoArchiveBuilder> ioArchiveBuilderProvider;
    @Autowired
    private Provider<WitaCBVorgangBuilder> witaCBVorgangBuilderProvider;
    @Autowired
    private CarrierElTALService carrierElTalService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private Provider<CarrierbestellungBuilder> carrierbestellungBuilder;
    @Autowired
    private Provider<Carrierbestellung2EndstelleBuilder> carrierbestellung2EndstelleBuilder;
    @Autowired
    private Provider<AuftragTechnikBuilder> auftragTechnikBuilder;
    @Autowired
    private Provider<AuftragDatenBuilder> auftragDatenBuilder;
    @Autowired
    private Provider<AuftragBuilder> auftragBuilder;
    @Autowired
    private Provider<EndstelleBuilder> endstelleBuilder;
    @Autowired
    private Provider<NiederlassungBuilder> niederlassungBuilder;
    @Autowired
    private Provider<WitaCBVorgangAnlageBuilder> witaCBVorgangAnlageBuilderProvider;

    public void testFindIoArchivesAsTreeForExtOrderNo() throws FindException {
        String carrierRefNr = "123456";

        IoArchive archive1 = createIoArchiveOut(carrierRefNr, 1);
        IoArchive archive2 = createIoArchiveIn(carrierRefNr, 2, QEB);
        flushAndClear();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(
                carrierRefNr).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 1);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
    }

    public void testFindIoArchivesAsTreeForExtOrderNoWithStronoVorgehalten() throws FindException {
        String carrierRefNr = "123456";

        IoArchive archive1 = createIoArchiveOut(carrierRefNr, 1);
        IoArchive archive2 = createIoArchiveIn(carrierRefNr, 2, QEB);
        Storno storno = new StornoBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(carrierRefNr).withSentAt(null)
                .buildValid();
        mwfEntityDao.store(storno);
        flushAndClear();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(
                carrierRefNr).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1).getRequestMeldungstyp(), MeldungsType.STORNO.getLongName());
        assertNull(ioArchiveTree.get(0).getSecond().get(1).getTimestampSent());
    }

    public void testFindIoArchivesAsTreeForExtOrderNoWithTvVorgehalten() throws FindException {
        String carrierRefNr = "123456";

        IoArchive archive1 = createIoArchiveOut(carrierRefNr, 1);
        IoArchive archive2 = createIoArchiveIn(carrierRefNr, 2, QEB);
        TerminVerschiebung tv = new TerminVerschiebungBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(carrierRefNr)
                .withSentAt(null).buildValid();
        mwfEntityDao.store(tv);
        flushAndClear();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(
                carrierRefNr).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1).getRequestMeldungstyp(), MeldungsType.TV.getLongName());
        assertNull(ioArchiveTree.get(0).getSecond().get(1).getTimestampSent());
    }

    public void testFindIoArchivesAsTreeForExtOrderNoWithStronoVorgehaltenAndThenSent() throws FindException {
        String carrierRefNr = "123456";

        IoArchive archive1 = createIoArchiveOut(carrierRefNr, 1);
        IoArchive archive2 = createIoArchiveIn(carrierRefNr, 2, QEB);
        Storno storno = new StornoBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(carrierRefNr).withSentAt(null)
                .buildValid();
        mwfEntityDao.store(storno);

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(
                carrierRefNr).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1).getRequestMeldungstyp(), MeldungsType.STORNO.getLongName());
        assertNull(ioArchiveTree.get(0).getSecond().get(1).getTimestampSent());

        LocalDateTime sentTimestamp = LocalDateTime.now();

        IoArchive stornoArchive = ioArchiveBuilderProvider.get().withWitaExtOrderNo(carrierRefNr)
                .withWitaVertragsnummer(null).withRequestTimestamp(LocalDateTime.of(2011, 1, 3, 0, 0, 0, 0))
                .withIoType(IOType.OUT).withTimestampSent(sentTimestamp)
                .withRequestMeldungstyp(MeldungsType.STORNO.getLongName()).build();
        storno.setSentAt(sentTimestamp != null ? Date.from(sentTimestamp.atZone(ZoneId.systemDefault()).toInstant()) : null);
        mwfEntityDao.store(storno);
        flushAndClear();

        ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(carrierRefNr).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1), stornoArchive);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1).getRequestMeldungstyp(), MeldungsType.STORNO.getLongName());
        assertNotNull(ioArchiveTree.get(0).getSecond().get(1).getTimestampSent());
    }

    public void testFindIoArchivesAsTreeForVertragsnummer() throws Exception {
        String vtrNr = "23456368745";

        createIoArchiveOut("123", 1);
        IoArchive akmPv = createIoArchive(null, vtrNr, 2, IOType.IN, AKM_PV);
        IoArchive ruemPv = createIoArchive(null, vtrNr, 4, IOType.OUT, RUEM_PV);
        IoArchive abmPv = createIoArchive(null, vtrNr, 5, IOType.OUT, ABM_PV);
        flushAndClear();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest
                .loadIoArchiveTreeAndAnlagenListForVertragsnummer(vtrNr).getFirst();
        assertThat(ioArchiveTree, hasSize(1));
        assertEquals(ioArchiveTree.get(0).getFirst(), akmPv);
        assertThat(ioArchiveTree.get(0).getSecond(), hasSize(2));
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), ruemPv);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1), abmPv);
    }

    public void testLoadIoArchiveTreeAndAnlagenListForArchiveTreeByExtOrderNo() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(null);

        WitaCBVorgang cbVorgang1 = createCbVorgang(cbAndAuftrag, "123");
        WitaCBVorgang cbVorgang2 = createCbVorgang(cbAndAuftrag, "234");

        IoArchive archive1 = createIoArchiveOut(cbVorgang1.getCarrierRefNr(), 1);
        IoArchive archive2 = createIoArchiveIn(cbVorgang1.getCarrierRefNr(), 2, QEB);
        IoArchive archive3 = createIoArchiveIn(cbVorgang2.getCarrierRefNr(), 3, ABM);
        flushAndClear();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListFor(
                getCbId(cbAndAuftrag), getAuftragId(cbAndAuftrag)).getFirst();

        assertThat(ioArchiveTree, hasSize(2));
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertThat(ioArchiveTree.get(0).getSecond(), hasSize(1));
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(1).getFirst(), archive3);
        assertTrue(ioArchiveTree.get(1).getSecond().isEmpty());
    }

    public void testLoadIoArchiveTreeAndAnlagenListForArchiveTreeWithoutCb() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = Pair.create(null, (new AuftragDatenBuilder())
                .withAuftragId(666L).build());

        WitaCBVorgang cbVorg1 = createCbVorgang(cbAndAuftrag, "123", RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, TYP_REX_MK);
        WitaCBVorgang cbVorg2 = createCbVorgang(cbAndAuftrag, "234", RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, TYP_REX_MK);

        createIoArchiveOut(cbVorg1.getCarrierRefNr(), 1);
        createIoArchiveOut(cbVorg2.getCarrierRefNr(), 3);
        flushAndClear();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListFor(null,
                getAuftragId(cbAndAuftrag)).getFirst();
        assertEquals(ioArchiveTree.size(), 2);
    }

    public void testLoadIoArchiveTreeAndAnlagenListForArchiveTreeWithCbAndRexMk() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(null);

        WitaCBVorgang cbVorgang1 = createCbVorgang(cbAndAuftrag, "123");
        WitaCBVorgang cbVorgang2 = createCbVorgang(cbAndAuftrag, "234",
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, CBVorgang.TYP_REX_MK);
        cbVorgang2.setCbId(null);
        carrierElTalService.saveCBVorgang(cbVorgang2);

        IoArchive archive1 = createIoArchiveOut(cbVorgang1.getCarrierRefNr(), 1);
        IoArchive archive2 = createIoArchiveIn(cbVorgang1.getCarrierRefNr(), 2, QEB);
        IoArchive archive3 = ioArchiveBuilderProvider.get().withWitaExtOrderNo(cbVorgang2.getCarrierRefNr())
                .withRequestTimestamp(LocalDateTime.of(2011, 1, 3, 0, 0, 0, 0)).build();

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListFor(
                getCbId(cbAndAuftrag), getAuftragId(cbAndAuftrag)).getFirst();

        assertEquals(ioArchiveTree.size(), 2);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 1);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(1).getFirst(), archive3);
        assertTrue(ioArchiveTree.get(1).getSecond().isEmpty());
    }

    public void testLoadIoArchiveTreeAndAnlagenListForArchiveTreeByVertragsnummer() throws Exception {
        String vertragsnummer = "123456";
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(null);
        cbAndAuftrag.getFirst().setVtrNr(vertragsnummer);
        carrierService.saveCB(cbAndAuftrag.getFirst());

        IoArchive archive1 = createIoArchive(null, vertragsnummer, 1, IOType.IN, AKM_PV);
        IoArchive archive2 = createIoArchive(null, vertragsnummer, 2, IOType.IN, ABM_PV);
        IoArchive archive3 = createIoArchive(null, vertragsnummer, 3, IOType.IN, ERLM_PV);
        IoArchive archive4 = createIoArchive(null, vertragsnummer, 6, IOType.IN, AKM_PV);

        createIoArchive(null, "123123", 3, IOType.IN, AKM_PV);
        createIoArchive(null, "123123", 4, IOType.IN, ABM_PV);
        createIoArchive(null, "123123", 5, IOType.IN, ENTM_PV);

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListFor(
                getCbId(cbAndAuftrag), getAuftragId(cbAndAuftrag)).getFirst();

        assertEquals(ioArchiveTree.size(), 2);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
        assertEquals(ioArchiveTree.get(0).getSecond().get(1), archive3);
        assertEquals(ioArchiveTree.get(1).getFirst(), archive4);
        assertTrue(ioArchiveTree.get(1).getSecond().isEmpty());
    }

    public void testLoadIoArchiveTreeAndAnlagenListForArchiveTreeByKueDtSuffix() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(null);

        WitaCBVorgang cbVorgang1 = createCbVorgang(cbAndAuftrag, "123");

        IoArchive archive1 = createIoArchiveIn(cbVorgang1.getCarrierRefNr() + BusinessKeyUtils.KUEDT_SUFFIX, 4, ERLM);
        IoArchive archive2 = createIoArchiveIn(cbVorgang1.getCarrierRefNr() + BusinessKeyUtils.KUEDT_SUFFIX, 5, ENTM);

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListFor(
                getCbId(cbAndAuftrag), getAuftragId(cbAndAuftrag)).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive1);
        assertEquals(ioArchiveTree.get(0).getSecond().size(), 1);
        assertEquals(ioArchiveTree.get(0).getSecond().get(0), archive2);
    }

    public void testLoadIoArchiveTreeAndAnlagenListForArchiveTreeByExtOrderNoWithBothEs() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftragB = createCbAndAuftrag(null);
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftragA = createCbAndAuftragForEsA(cbAndAuftragB);

        WitaCBVorgang cbVorgang2 = createCbVorgang(cbAndAuftragA, "234");
        WitaCBVorgang cbVorgang1 = createCbVorgang(cbAndAuftragB, "123");

        createIoArchiveOut(cbVorgang1.getCarrierRefNr(), 1);
        IoArchive archive2 = createIoArchiveOut(cbVorgang2.getCarrierRefNr(), 3);

        List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree = underTest.loadIoArchiveTreeAndAnlagenListFor(
                getCbId(cbAndAuftragA), getAuftragId(cbAndAuftragA)).getFirst();

        assertEquals(ioArchiveTree.size(), 1);
        assertEquals(ioArchiveTree.get(0).getFirst(), archive2);
        assertTrue(ioArchiveTree.get(0).getSecond().isEmpty());
    }

    public void testLoadIoArchiveTreeAndAnlagenListForAnlagen() throws Exception {
        String extOrderNo1 = "1233";
        String extOrderNo2 = "2343";
        String vtrNr = "2345636876";

        // make sure to use ext order numbers here that don't already exist!!!
        assertCbVorgangDoesNotExist(extOrderNo1, extOrderNo2);

        Auftrag auftrag = createMwfAuftrag(extOrderNo1, BEREITSTELLUNG,
                GeschaeftsfallBuilder.createAnlage(TestAnlage.SIMPLE, Anlagentyp.LAGEPLAN),
                GeschaeftsfallBuilder.createAnlage(TestAnlage.SIMPLE, Anlagentyp.LETZTE_TELEKOM_RECHNUNG));
        mwfEntityDao.store(auftrag);

        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(vtrNr);

        createCbVorgangWithAnlage(cbAndAuftrag, extOrderNo1, ArchiveDocumentType.LAGEPLAN);

        WitaCBVorgang cbVorgang2 = createCbVorgang(cbAndAuftrag, extOrderNo2);

        createIoArchiveOut(extOrderNo1, 1);
        createIoArchiveOut(extOrderNo1, 3);
        createIoArchiveOut(extOrderNo1, 4);
        createIoArchiveOut(cbVorgang2.getCarrierRefNr(), 6);

        createIoArchive(null, extOrderNo1, 2, IOType.IN, AKM_PV);
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUNDENAUFTRAG).withExterneAuftragsnummer(extOrderNo1)
                .build();
        mwfEntityDao.store(akmPv);

        createIoArchiveIn(cbVorgang2.getCarrierRefNr(), 7, TAM);
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().withExterneAuftragsnummer(
                cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(tam);

        createIoArchiveIn(cbVorgang2.getCarrierRefNr(), 8, ABBM);
        AbbruchMeldung abbm = new AbbruchMeldungBuilder()
                .withAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG)
                .withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(abbm);

        createIoArchive(null, vtrNr, 9, IOType.IN, AKM_PV);
        AnkuendigungsMeldungPv akmPv2 = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE)
                .withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr()).withVertragsnummer(vtrNr).build();
        mwfEntityDao.store(akmPv2);
        flushAndClear();

        List<AnlagenDto> result = underTest.loadIoArchiveTreeAndAnlagenListFor(getCbId(cbAndAuftrag),
                getAuftragId(cbAndAuftrag)).getSecond();
        // One outgoing, two incoming
        assertEquals(result.size(), 3);
    }

    public void testLoadIoArchiveTreeAndAnlagenListForSingleOutgoingAnlage() throws Exception {
        String extOrderNo1 = "1233";
        String extOrderNo2 = "2343";

        // make sure to use ext order numbers here that don't already exist!!!
        assertCbVorgangDoesNotExist(extOrderNo1, extOrderNo2);

        String vtrNr = "23456368745";
        Auftrag auftrag = createMwfAuftrag(extOrderNo1, BEREITSTELLUNG,
                GeschaeftsfallBuilder.createAnlage(TestAnlage.SIMPLE, Anlagentyp.LAGEPLAN));
        mwfEntityDao.store(auftrag);

        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(vtrNr);

        WitaCBVorgangAnlage anlage = getOnlyElement(createCbVorgangWithAnlage(cbAndAuftrag, extOrderNo1, LAGEPLAN));
        createCbVorgang(cbAndAuftrag, extOrderNo2);

        AnlagenDto result = getOnlyElement(underTest.loadIoArchiveTreeAndAnlagenListFor(getCbId(cbAndAuftrag),
                getAuftragId(cbAndAuftrag)).getSecond());
        assertOutgoingAnlageOk(result, auftrag, anlage, extOrderNo1);
    }

    public void testLoadIoArchiveTreeAndAnlagenListForSingleIncomingAnlage() throws Exception {
        String extOrderNo1 = "1233";
        String extOrderNo2 = "2343";

        // make sure to use ext order numbers here that don't already exist!!!
        assertCbVorgangDoesNotExist(extOrderNo1, extOrderNo2);

        String vtrNr = "23456368746";
        Auftrag auftrag = createMwfAuftrag(extOrderNo1, BEREITSTELLUNG);
        mwfEntityDao.store(auftrag);

        // Carrierbestellung
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(vtrNr);
        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag2 = createCbAndAuftrag(null);

        WitaCBVorgang cbVorgang1 = createCbVorgang(cbAndAuftrag, extOrderNo1);
        WitaCBVorgang cbVorgang2 = createCbVorgang(cbAndAuftrag2, extOrderNo2);

        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUNDENAUFTRAG)
                .withExterneAuftragsnummer(cbVorgang1.getCarrierRefNr()).build();
        mwfEntityDao.store(akmPv);
        AnkuendigungsMeldungPv akmPv2 = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE)
                .withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(akmPv2);
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().withExterneAuftragsnummer(
                cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(tam);
        AbbruchMeldung abbm = new AbbruchMeldungBuilder()
                .withAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG)
                .withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(abbm);
        flushAndClear();

        AnlagenDto result = getOnlyElement(underTest.loadIoArchiveTreeAndAnlagenListFor(getCbId(cbAndAuftrag),
                getAuftragId(cbAndAuftrag)).getSecond());
        assertIncomingAnlageOk(result, akmPv);
    }

    public void testLoadIoArchiveTreeAndAnlagenListForMultipleAnlagen() throws Exception {
        String extOrderNo = "1234";
        String vtrNr = "23456368745";

        Auftrag auftrag = createMwfAuftrag(extOrderNo, BEREITSTELLUNG,
                GeschaeftsfallBuilder.createAnlage(TestAnlage.SIMPLE, Anlagentyp.LAGEPLAN),
                GeschaeftsfallBuilder.createAnlage(TestAnlage.SIMPLE, Anlagentyp.LETZTE_TELEKOM_RECHNUNG));
        mwfEntityDao.store(auftrag);

        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(vtrNr);

        List<WitaCBVorgangAnlage> anlagen = createCbVorgangWithAnlage(cbAndAuftrag, extOrderNo, LAGEPLAN, AUFTRAG);

        WitaCBVorgang cbVorgang2 = createCbVorgang(cbAndAuftrag, "2345");

        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withVersandZeitstempel((LocalDateTime.now()).plusHours(1))
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUNDENAUFTRAG).withExterneAuftragsnummer(extOrderNo)
                .build();
        mwfEntityDao.store(akmPv);
        AnkuendigungsMeldungPv akmPv2 = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE)
                .withVersandZeitstempel((LocalDateTime.now()).plusHours(0))
                .withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(akmPv2);
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().withExterneAuftragsnummer(
                cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(tam);
        AbbruchMeldung abbm = new AbbruchMeldungBuilder()
                .withAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG)
                .withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr()).build();
        mwfEntityDao.store(abbm);
        EntgeltMeldung entm = new EntgeltMeldungBuilder().withExterneAuftragsnummer(cbVorgang2.getCarrierRefNr())
                .withVersandZeitstempel((LocalDateTime.now()).plusHours(2))
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.LETZTE_TELEKOM_RECHNUNG).build();
        mwfEntityDao.store(entm);
        flushAndClear();

        List<AnlagenDto> results = underTest.loadIoArchiveTreeAndAnlagenListFor(getCbId(cbAndAuftrag),
                getAuftragId(cbAndAuftrag)).getSecond();
        assertEquals(results.size(), 5);
        // order cannot be determined uniquely
        try {
            assertOutgoingAnlageOk(results.get(0), auftrag, anlagen.get(0), extOrderNo);
            assertOutgoingAnlageOk(results.get(1), auftrag, anlagen.get(1), extOrderNo);
        }
        catch (Throwable e) {
            assertOutgoingAnlageOk(results.get(0), auftrag, anlagen.get(1), extOrderNo);
            assertOutgoingAnlageOk(results.get(1), auftrag, anlagen.get(0), extOrderNo);
        }
        assertIncomingAnlageOk(results.get(2), akmPv);
        assertIncomingAnlageOk(results.get(3), akmPv2);
        assertIncomingAnlageOk(results.get(4), entm);
        assertOrderOfAnlagenDto(results);
    }

    public void testFindOutgoingAnlageByExtAuftragsnummer() throws Exception {
        String extOrderNo = "1248";
        Auftrag auftrag = createMwfAuftrag(extOrderNo, BEREITSTELLUNG);

        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(null);

        WitaCBVorgangAnlage anlage = getOnlyElement(createCbVorgangWithAnlage(cbAndAuftrag, extOrderNo, LAGEPLAN));
        flushAndClear();

        AnlagenDto res = getOnlyElement(underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(extOrderNo).getSecond());
        assertOutgoingAnlageOk(res, auftrag, anlage, extOrderNo);
    }

    public void testFindIncomingAnlageByExtAuftragsnummer() throws Exception {
        String extOrderNo = "1249";
        String vtrNr = "1234567800";

        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUENDIGUNGSSCHREIBEN).withVertragsnummer(vtrNr)
                .withExterneAuftragsnummer(extOrderNo).build();
        mwfEntityDao.store(akmPv);

        AnlagenDto res = getOnlyElement(underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(extOrderNo).getSecond());
        assertIncomingAnlageOk(res, akmPv);
    }

    public void testFindIncomingAndOutgoingAnlageByExtAuftragsnummer() throws Exception {
        String extOrderNo = "12345";
        Auftrag auftrag = createMwfAuftrag(extOrderNo, BEREITSTELLUNG);

        Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag = createCbAndAuftrag(null);

        WitaCBVorgangAnlage anlage = getOnlyElement(createCbVorgangWithAnlage(cbAndAuftrag, extOrderNo, LAGEPLAN));

        EntgeltMeldung entm = new EntgeltMeldungBuilder().withExterneAuftragsnummer(extOrderNo)
                .withVersandZeitstempel((LocalDateTime.now()).plusHours(2))
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.LETZTE_TELEKOM_RECHNUNG).build();
        mwfEntityDao.store(entm);
        flushAndClear();

        List<AnlagenDto> result = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(extOrderNo).getSecond();

        assertEquals(result.size(), 2);
        assertOutgoingAnlageOk(result.get(0), auftrag, anlage, extOrderNo);
        assertIncomingAnlageOk(result.get(1), entm);
    }

    public void testFindNoAnlageByExtAuftragsnummer() throws Exception {
        String extOrderNo = "1357";
        createMwfAuftrag(extOrderNo, BEREITSTELLUNG);

        mwfEntityDao.store(new EntgeltMeldungBuilder().withExterneAuftragsnummer(extOrderNo).build());
        flushAndClear();

        List<AnlagenDto> result = underTest.loadIoArchiveTreeAndAnlagenListForExtOrderNo(extOrderNo).getSecond();
        assertEquals(result.size(), 0);
    }

    public void testFindIncomingAnlageByVertragsnummer() throws Exception {
        String vtrNr = "0034567800";
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUENDIGUNGSSCHREIBEN).withVertragsnummer(vtrNr)
                .withExterneAuftragsnummer("123").build();
        mwfEntityDao.store(akmPv);
        flushAndClear();

        AnlagenDto res = getOnlyElement(underTest.loadIoArchiveTreeAndAnlagenListForVertragsnummer(vtrNr).getSecond());
        assertIncomingAnlageOk(res, akmPv);
    }

    public void testFindMoreIncomingAnlagenByVertragsnummer() throws Exception {
        String vtrNr = "000123000";
        createMwfAuftrag("124", BEREITSTELLUNG);

        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.PORTIERUNGSANZEIGE).withVertragsnummer(vtrNr).build();
        mwfEntityDao.store(akmPv);

        AnkuendigungsMeldungPv akmPv2 = new AnkuendigungsMeldungPvBuilder()
                .addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.KUENDIGUNG_ABGEBENDER_PROVIDER).withVertragsnummer(vtrNr)
                .withVersandZeitstempel((LocalDateTime.now()).plusHours(2)).build();
        mwfEntityDao.store(akmPv2);
        flushAndClear();

        List<AnlagenDto> result = underTest.loadIoArchiveTreeAndAnlagenListForVertragsnummer(vtrNr).getSecond();
        assertEquals(result.size(), 2);
        assertOrderOfAnlagenDto(result);
        assertIncomingAnlageOk(result.get(0), akmPv);
        assertIncomingAnlageOk(result.get(1), akmPv2);
    }

    private Pair<Carrierbestellung, AuftragDaten> createCbAndAuftrag(String vtrNr) {
        Carrierbestellung2EndstelleBuilder cb2EsBuilder = carrierbestellung2EndstelleBuilder.get();
        AuftragDatenBuilder adBuilder = auftragDatenBuilder.get();

        Endstelle endstelle = endstelleBuilder
                .get()
                .withAuftragTechnikBuilder(
                        auftragTechnikBuilder
                                .get()
                                .withNiederlassungBuilder(niederlassungBuilder.get())
                                .withAuftragBuilder(
                                        auftragBuilder.get().withAuftragDatenBuilder(
                                                adBuilder.withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN))
                                )
                )
                .withCb2EsBuilder(cb2EsBuilder).build();

        Carrierbestellung carrierbestellung = carrierbestellungBuilder.get().withCb2EsBuilder(cb2EsBuilder)
                .withVtrNr(vtrNr).build();
        AuftragDaten auftragDaten = adBuilder.get();

        assertNotNull(carrierbestellung.getId());
        assertEquals(carrierbestellung.getCb2EsId(), endstelle.getCb2EsId());
        assertNotNull(auftragDaten.getId());

        return Pair.create(carrierbestellung, auftragDaten);
    }

    private WitaCBVorgang createCbVorgang(Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag, String extOrderNo)
            throws Exception {
        return createCbVorgang(cbAndAuftrag, extOrderNo, GeschaeftsfallTyp.BEREITSTELLUNG, CBVorgang.TYP_NEU);
    }

    private WitaCBVorgang createCbVorgang(Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag, String extOrderNo,
            GeschaeftsfallTyp geschaeftsfallTyp, Long cbVorgangTyp) throws Exception {
        Long cbId = getCbId(cbAndAuftrag);

        WitaCBVorgang cbVorgang = witaCBVorgangBuilderProvider.get().withCarrierRefNr(extOrderNo).withCbId(cbId)
                .withAuftragId(getAuftragId(cbAndAuftrag)).withWitaGeschaeftsfallTyp(geschaeftsfallTyp)
                .withTyp(cbVorgangTyp).build();
        if (cbId == null) {
            cbVorgang.setCbId(null);
            carrierElTalService.saveCBVorgang(cbVorgang);
        }
        return cbVorgang;
    }

    private List<WitaCBVorgangAnlage> createCbVorgangWithAnlage(Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag,
            String extOrderNo, ArchiveDocumentType... archiveDocumentTypes) throws Exception {
        List<WitaCBVorgangAnlage> anlagen = Lists.newArrayList();
        for (ArchiveDocumentType archiveDocumentType : archiveDocumentTypes) {
            anlagen.add(witaCBVorgangAnlageBuilderProvider.get().withArchiveDocumentType(archiveDocumentType)
                    .setPersist(false).build());
        }

        WitaCBVorgang cbVorgang = createCbVorgang(cbAndAuftrag, extOrderNo);
        cbVorgang.setAnlagen(anlagen);
        carrierElTalService.saveCBVorgang(cbVorgang);

        return anlagen;
    }

    private Long getCbId(Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag) {
        if ((cbAndAuftrag == null) || (cbAndAuftrag.getFirst() == null)) {
            return null;
        }
        return cbAndAuftrag.getFirst().getId();
    }

    private Long getAuftragId(Pair<Carrierbestellung, AuftragDaten> cbAndAuftrag) {
        if ((cbAndAuftrag == null) || (cbAndAuftrag.getSecond() == null)) {
            return null;
        }
        return cbAndAuftrag.getSecond().getAuftragId();
    }

    private IoArchive createIoArchiveOut(String extOrderNo, int day) {
        return createIoArchive(extOrderNo, null, day, IOType.OUT, null);
    }

    private IoArchive createIoArchiveIn(String extOrderNo, int day, MeldungsType meldungsTyp) {
        return createIoArchive(extOrderNo, null, day, IOType.IN, meldungsTyp);
    }

    private IoArchive createIoArchive(String extOrderNo, String vtrNr, int day, IOType ioType, MeldungsType meldungsTyp) {
        return ioArchiveBuilderProvider.get().withWitaExtOrderNo(extOrderNo).withWitaVertragsnummer(vtrNr)
                .withRequestTimestamp(LocalDateTime.of(2011, 1, day, 0, 0, 0, 0)).withIoType(ioType)
                .withRequestMeldungstyp((meldungsTyp == null) ? null : meldungsTyp.getValue()).build();
    }

    private Pair<Carrierbestellung, AuftragDaten> createCbAndAuftragForEsA(
            Pair<Carrierbestellung, AuftragDaten> cbAndAuftragEsB) throws Exception {
        Carrierbestellung2EndstelleBuilder cb2EsBuilder = carrierbestellung2EndstelleBuilder.get();
        Carrierbestellung cbA = carrierbestellungBuilder.get().withCb2EsBuilder(cb2EsBuilder).build();

        Endstelle endstelleA = endstelleBuilder.get().withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).build();
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(getAuftragId(cbAndAuftragEsB),
                Endstelle.ENDSTELLEN_TYP_B);
        endstelleA.setEndstelleGruppeId(endstelleB.getEndstelleGruppeId());
        endstellenService.saveEndstelle(endstelleA);

        return Pair.create(cbA, cbAndAuftragEsB.getSecond());
    }

    private Auftrag createMwfAuftrag(String extOrderNo, GeschaeftsfallTyp geschaeftsfallTyp, Anlage... anlagen) {
        GeschaeftsfallBuilder geschaeftsfallBuilder = new GeschaeftsfallBuilder(geschaeftsfallTyp);
        for (Anlage anlage : anlagen) {
            geschaeftsfallBuilder.addTestAnlage(anlage);
        }
        Auftrag auftrag = new de.mnet.wita.message.builder.AuftragBuilder(geschaeftsfallBuilder).withExterneAuftragsnummer(
                extOrderNo).buildValid();
        mwfEntityDao.store(auftrag);
        return auftrag;
    }

    private void assertIncomingAnlageOk(AnlagenDto result, Meldung<?> meldung) {
        Anlage anlage = getOnlyElement(meldung.getAnlagen());
        assertEquals(result.getArchiveDocumentType(), anlage.getAnlagentyp().value);
        assertEquals(result.getIoType(), IOType.IN);
        assertEquals(result.getGeschaeftsfallOderMeldungsTyp(), meldung.getMeldungsTyp().getValue());
        assertEquals(result.getWitaExtOrderNo(), meldung.getExterneAuftragsnummer());
        assertEquals(result.getSentTimestamp().withNano(0), DateConverterUtils.asLocalDateTime(meldung.getVersandZeitstempel()).withNano(0));
    }

    private void assertOutgoingAnlageOk(AnlagenDto result, Auftrag auftrag, WitaCBVorgangAnlage anlage,
            String extAuftragsnummer) {
        assertEquals(result.getArchiveDocumentType(), anlage.getArchiveDocumentType().getDocumentTypeName());
        assertEquals(result.getIoType(), IOType.OUT);
        assertEquals(result.getGeschaeftsfallOderMeldungsTyp(), GeschaeftsfallTyp.BEREITSTELLUNG.getDisplayName());
        assertEquals(result.getWitaExtOrderNo(), extAuftragsnummer);
        assertEquals(result.getSentTimestamp().withNano(0), DateConverterUtils.asLocalDateTime(auftrag.getMwfCreationDate()).withNano(0));
    }

    private void assertOrderOfAnlagenDto(List<AnlagenDto> toBeChecked) {
        String currentExtAuftragNo = null;
        LocalDateTime currentCreationDate = LocalDateTime.of(1700, 1, 1, 0, 0, 0, 0);
        for (AnlagenDto anlage : toBeChecked) {
            if (anlage.getWitaExtOrderNo().equals(currentExtAuftragNo)) {
                if (anlage.getSentTimestamp().isBefore(currentCreationDate)) {
                    fail("sorted order incurrent due to timestamp problem");
                }
                else {
                    currentCreationDate = anlage.getSentTimestamp();
                }
            }
            else {
                currentExtAuftragNo = anlage.getWitaExtOrderNo();
                currentCreationDate = anlage.getSentTimestamp();
            }
        }
    }

    private void assertCbVorgangDoesNotExist(String ... carrierRefNumbers) {
        for(String carrierRefNumber: carrierRefNumbers) {
            Assert.assertNull(
                    carrierElTalService.findCBVorgangByCarrierRefNr(carrierRefNumber),
                    String.format("Problem with test data: a CBVorgang already exists with the CarrierRefNr %s", carrierRefNumber)
            );
        }

    }
}
