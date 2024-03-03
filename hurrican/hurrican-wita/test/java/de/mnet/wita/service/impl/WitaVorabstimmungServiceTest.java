/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:09:42
 */
package de.mnet.wita.service.impl;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.VorabstimmungAbgebendBuilder;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaVorabstimmungService;

@Test(groups = BaseTest.SERVICE)
public class WitaVorabstimmungServiceTest extends AbstractServiceTest {

    @Autowired
    private WitaVorabstimmungService underTest;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private CCAuftragService auftragService;

    @Autowired
    private Provider<AuftragTechnikBuilder> auftragTechnikBuilder;
    @Autowired
    private Provider<AuftragDatenBuilder> auftragDatenBuilder;
    @Autowired
    private Provider<AuftragBuilder> auftragBuilder;
    @Autowired
    private Provider<EndstelleBuilder> endstelleBuilder;

    public void testSaveAndFindVorabstimmung() throws FindException {
        Endstelle endstelle = createEndstelleWithAuftragDaten();

        Vorabstimmung cbPv = new Vorabstimmung();
        cbPv.setCarrier(carrierService.findCarrier(Carrier.ID_QSC));
        cbPv.setProduktGruppe(ProduktGruppe.TAL);
        cbPv.setProviderLbz("96W/821/821/123456");
        cbPv.setProviderVtrNr("123456A789");
        cbPv.setBestandssucheOnkz("0821");
        cbPv.setBestandssucheDn("123456");
        cbPv.setBestandssucheDirectDial("1");

        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
        cbPv.setAuftragId(auftragDaten.getAuftragId());
        cbPv.setEndstelleTyp(endstelle.getEndstelleTyp());

        underTest.saveVorabstimmung(cbPv);

        flushAndClear();

        Vorabstimmung result = underTest.findVorabstimmung(endstelle, auftragDaten.getAuftragId());

        assertNotNull(result);
        assertEquals(result.getProduktGruppe(), cbPv.getProduktGruppe());
        assertEquals(result.getProviderLbz(), cbPv.getProviderLbz());
        assertEquals(result.getProviderVtrNr(), cbPv.getProviderVtrNr());
        assertEquals(result.getBestandssucheOnkz(), cbPv.getBestandssucheOnkz());
        assertEquals(result.getBestandssucheDn(), cbPv.getBestandssucheDn());
        assertEquals(result.getBestandssucheDirectDial(), cbPv.getBestandssucheDirectDial());
    }

    public void testSaveAndFindVorabstimmungAbgebend() throws FindException {
        Endstelle endstelle = createEndstelleWithAuftragDaten();

        VorabstimmungAbgebend vorabstimmungAbgebend = new VorabstimmungAbgebend();
        vorabstimmungAbgebend.setAbgestimmterProdiverwechsel(LocalDate.now().plusDays(14));
        vorabstimmungAbgebend.setCarrier(carrierService.findCarrier(Carrier.ID_DTAG));
        vorabstimmungAbgebend.setBemerkung("Der Vertrag mit M-net laeuft noch 1 Jahr.");
        vorabstimmungAbgebend.setRueckmeldung(VorabstimmungAbgebend.RUECKMELDUNG_NEGATIVE);

        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
        vorabstimmungAbgebend.setAuftragId(auftragDaten.getAuftragId());
        vorabstimmungAbgebend.setEndstelleTyp(endstelle.getEndstelleTyp());

        underTest.saveVorabstimmungAbgebend(vorabstimmungAbgebend);

        flushAndClear();

        VorabstimmungAbgebend result = underTest.findVorabstimmungAbgebend(endstelle.getEndstelleTyp(), auftragDaten.getAuftragId());

        assertNotNull(result);
        assertEquals(result.getAbgestimmterProdiverwechsel(), vorabstimmungAbgebend.getAbgestimmterProdiverwechsel());
        assertEquals(result.getCarrier(), vorabstimmungAbgebend.getCarrier());
        assertEquals(result.getBemerkung(), vorabstimmungAbgebend.getBemerkung());
        assertEquals(result.getRueckmeldung(), vorabstimmungAbgebend.getRueckmeldung());
    }

    private Endstelle createEndstelleWithAuftragDaten() {
        return endstelleBuilder
                .get()
                .withAuftragTechnikBuilder(
                        auftragTechnikBuilder.get().withAuftragBuilder(
                                auftragBuilder.get().withAuftragDatenBuilder(auftragDatenBuilder.get()))
                ).build();
    }

    public void findVorabstimmungForAuftragsKlammer() throws FindException {
        Endstelle endstelle1 = createEndstelleWithAuftragDaten();
        Endstelle endstelle2 = createEndstelleWithAuftragDaten();

        AuftragDaten auftragDaten1 = auftragService.findAuftragDatenByEndstelleTx(endstelle1.getId());
        AuftragDaten auftragDaten2 = auftragService.findAuftragDatenByEndstelleTx(endstelle2.getId());

        Vorabstimmung cbPv1 = getBuilder(VorabstimmungBuilder.class)
                .withCarrier(carrierService.findCarrier(Carrier.ID_DTAG)).withProduktGruppe(ProduktGruppe.TAL)
                .withBestandssucheEinzelanschluss().withAuftragId(auftragDaten1.getAuftragId())
                .withEndstelleTyp(endstelle1.getEndstelleTyp()).build();
        underTest.saveVorabstimmung(cbPv1);

        WitaCBVorgang witaCbv1 = getBuilder(WitaCBVorgangBuilder.class).withAuftragId(auftragDaten1.getAuftragId())
                .withAuftragsKlammer(99L).build();
        getBuilder(WitaCBVorgangBuilder.class).withAuftragId(auftragDaten2.getAuftragId())
                .withAuftragsKlammer(99L).build();

        List<Vorabstimmung> result = underTest.findVorabstimmungForAuftragsKlammer(witaCbv1.getAuftragsKlammer(),
                endstelle1);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), cbPv1.getId());
    }

    public void testDeleteVorabstimmung() throws Exception {
        Endstelle endstelle = createEndstelleWithAuftragDaten();
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());

        Carrier carrier = carrierService.findCarrier(Carrier.ID_DTAG);
        Vorabstimmung v = getBuilder(VorabstimmungBuilder.class).withAuftragId(auftragDaten.getAuftragId())
                .withEndstelleTyp(endstelle.getEndstelleTyp()).withCarrier(carrier).build();
        flushAndClear();

        Vorabstimmung vorabstimmung2 = underTest.findVorabstimmung(endstelle, auftragDaten);
        assertNotNull(vorabstimmung2);
        assertEquals(vorabstimmung2, v);
        underTest.deleteVorabstimmung(vorabstimmung2);
        Vorabstimmung result = underTest.findVorabstimmung(endstelle, auftragDaten);
        assertNull(result);
    }

    public void testDeleteVorabstimmungAbgebend() throws Exception {
        Endstelle endstelle = createEndstelleWithAuftragDaten();
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());

        Carrier carrier = carrierService.findCarrier(Carrier.ID_DTAG);
        VorabstimmungAbgebend v = getBuilder(VorabstimmungAbgebendBuilder.class)
                .withAuftragId(auftragDaten.getAuftragId()).withEndstelleTyp(endstelle.getEndstelleTyp())
                .withCarrier(carrier).withRueckmeldung(Boolean.TRUE)
                .withAbgestimmterProdiverwechselTermin(LocalDate.now()).build();
        underTest.saveVorabstimmungAbgebend(v);
        flushAndClear();

        VorabstimmungAbgebend vorabstimmungAbgebend = underTest.findVorabstimmungAbgebend(endstelle.getEndstelleTyp(), auftragDaten.getAuftragId());
        assertNotNull(vorabstimmungAbgebend);
        assertEquals(v, vorabstimmungAbgebend);
        underTest.deleteVorabstimmungAbgebend(vorabstimmungAbgebend);
        VorabstimmungAbgebend result = underTest.findVorabstimmungAbgebend(endstelle.getEndstelleTyp(), auftragDaten.getAuftragId());
        assertNull(result);
    }
}
