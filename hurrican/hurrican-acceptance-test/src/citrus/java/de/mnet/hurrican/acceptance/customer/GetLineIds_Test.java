/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2016
 */
package de.mnet.hurrican.acceptance.customer;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2Endstelle;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Tests the {@link CustomerOrderService#getLineIds(List) } endpoint
 * * Atlas - Hurrican interface
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GetLineIds_Test extends AbstractCustomerTestBuilder{

    @CitrusTest
    @Test
    public void GetLineIds_01_Test() {
        simulatorUseCase(SimulatorUseCase.GetLineIds_01);

        final GeneratedTaifunData generatedTaifunData = getTaifunDataFactory().surfAndFonWithDns(1).persist();
        final String testVbzId = RandomStringUtils.randomAlphanumeric(11).toUpperCase();
        createAuftragWithVbz(generatedTaifunData, testVbzId);

        variable("customerOrderId", generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
        atlas().sendOrderDetailsCustomerOrder("getLineIds");

        variable("lineId", testVbzId);
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getLineIdsResponse");
    }

    private void createAuftragWithVbz(GeneratedTaifunData generatedTaifunData, String lineId) {
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withKundeNo(generatedTaifunData.getKunde().getKundeNo())
                .setPersist(false);
        final Auftrag auftrag = hurrican().save(auftragBuilder.build());

        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .withAuftragNoOrig(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig())
                .setPersist(false)
                .build();
        auftragDaten.setAuftragId(auftrag.getAuftragId());
        hurrican().save(auftragDaten);

        final AuftragTechnik2Endstelle at2Es = new AuftragTechnik2EndstelleBuilder()
                .setPersist(false).build();
        hurrican().save(at2Es);

        final VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder()
                .setPersist(false).withVbz(lineId).build();
        hurrican().save(vbz);

        final AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .setPersist(false).build();
        auftragTechnik.setAuftragId(auftrag.getAuftragId());
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        auftragTechnik.setVbzId(vbz.getId());
        hurrican().save(auftragTechnik);
    }

    @CitrusTest
    @Test
    public void GetLineIds_02_Test() {
        simulatorUseCase(SimulatorUseCase.GetLineIds_01);

        variable("customerOrderId", "not-a-number-id");
        atlas().sendOrderDetailsCustomerOrder("getLineIds");

        atlas().receiveGetOrderDetailsCustomerOrderResponse("getLineIdsFault");
    }
}
