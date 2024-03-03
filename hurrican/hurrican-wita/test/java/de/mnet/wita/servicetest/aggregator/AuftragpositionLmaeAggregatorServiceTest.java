/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2011 18:16:33
 */
package de.mnet.wita.servicetest.aggregator;

import static org.testng.Assert.*;

import javax.inject.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.aggregator.AuftragspositionLmaeAggregator;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.SERVICE)
public class AuftragpositionLmaeAggregatorServiceTest extends AbstractServiceTest {

    @Autowired
    private AuftragspositionLmaeAggregator auftragspositionLmaeAggregator;

    @Autowired
    private Provider<AcceptanceTestDataBuilder> acceptanceTestDataBuilderProvider;

    @Autowired
    private EndstellenService endstellenService;

    @Test(enabled = false)
    //test deaktiviert da er aufgrund des timings der flushes auf manchen umgebungen oft fehl schlaegt
    public void testAggregate() throws Exception {
        CreatedData createdData = acceptanceTestDataBuilderProvider.get()
                .withHurricanProduktId(de.augustakom.hurrican.model.cc.Produkt.AK_CONNECT)
                .withReferencingCbBuilder((new CarrierbestellungBuilder()).withLbz("96W/89/89/000011"))
                .withUetv(Uebertragungsverfahren.H13)
                .build(getSessionId());

        Endstelle enndstelleB = createdData.referencingEndstellen.get(1);

        Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withEqOutBuilder(getBuilder(EquipmentBuilder.class).withCarrier(Carrier.DTAG.name()).withRangSchnittstelle(RangSchnittstelle.H).withUETV(Uebertragungsverfahren.H04))
                .withEsId(enndstelleB.getId()).build();
        enndstelleB.setRangierId(rangierung.getId());

        endstellenService.saveEndstelle(enndstelleB);

        flushAndClear();

        WitaCBVorgang witaCBVorgang = getBuilder(WitaCBVorgangBuilder.class)
                .withAuftragId(createdData.auftrag.getAuftragId())
                .withCbId(createdData.carrierbestellung.getId())
                .withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG).build();

        Auftragsposition mainAuftragsposition = auftragspositionLmaeAggregator.aggregate(witaCBVorgang);
        assertNotNull(mainAuftragsposition);
        assertEquals(mainAuftragsposition.getAktionsCode(), AktionsCode.AENDERUNG);
        assertEquals(mainAuftragsposition.getProdukt(), Produkt.TAL);

        assertNotNull(mainAuftragsposition.getGeschaeftsfallProdukt().getStandortKunde());
        assertNotNull(mainAuftragsposition.getGeschaeftsfallProdukt().getStandortKollokation());
        assertNotNull(mainAuftragsposition.getGeschaeftsfallProdukt().getLeitungsBezeichnung());
        assertEquals(mainAuftragsposition.getGeschaeftsfallProdukt().getSchaltangaben().getSchaltungKupfer().get(0)
                .getUebertragungsverfahren(), de.mnet.wita.message.common.Uebertragungsverfahren.H04);

        Auftragsposition subAuftragsposition = mainAuftragsposition.getPosition();
        assertEquals(subAuftragsposition.getAktionsCode(), AktionsCode.AENDERUNG);
        assertEquals(subAuftragsposition.getGeschaeftsfallProdukt().getSchaltangaben().getSchaltungKupfer().get(0)
                .getUebertragungsverfahren(), de.mnet.wita.message.common.Uebertragungsverfahren.H13);
        assertNull(subAuftragsposition.getGeschaeftsfallProdukt().getStandortKunde());
        assertNull(subAuftragsposition.getGeschaeftsfallProdukt().getLeitungsBezeichnung());

        assertSame(mainAuftragsposition.getGeschaeftsfallProdukt().getStandortKollokation(), subAuftragsposition
                .getGeschaeftsfallProdukt().getStandortKollokation());
        assertSame(mainAuftragsposition.getProdukt(), subAuftragsposition.getProdukt());

        // ProduktBezeichner wird nachtraeglich aggregiert
        assertNull(mainAuftragsposition.getProduktBezeichner());
        assertNull(subAuftragsposition.getProduktBezeichner());
    }
}
