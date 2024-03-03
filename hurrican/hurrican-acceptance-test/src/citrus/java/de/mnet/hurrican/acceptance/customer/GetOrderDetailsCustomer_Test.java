/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.03.2015
 */
package de.mnet.hurrican.acceptance.customer;

import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.dsl.functions.Functions;
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
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2Endstelle;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Tests the getOrderDetails endpoint (atlas - hurrican interface)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GetOrderDetailsCustomer_Test extends AbstractCustomerTestBuilder{

    /**
     * Tests the getOrderDetails asynchronous endpoint in Hurrican by simulating a valid getOrderDetails
     * payload from AtlasESB.
     */
    @CitrusTest
    @Test
    public void GetOrderDetailsCustomer_01_Test() {
        simulatorUseCase(SimulatorUseCase.GetOrderDetailsCustomer_01);
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory().surfAndFonWithDns(1).persist();
        Carrierbestellung carrierbestellung = createCarrierbestellung(generatedTaifunData);

        variable("lineContractId", carrierbestellung.getVtrNr());
        variable("customerFirstname", generatedTaifunData.getKunde().getVorname());
        variable("customerLastname", generatedTaifunData.getKunde().getName());
        variable("customerId", generatedTaifunData.getKunde().getKundeNo());
        variable("customerOrderId", generatedTaifunData.getBillingAuftrag().getAuftragNo());

        atlas().sendOrderDetailsCustomerOrder("getOrderDetails");
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getOrderDetailsResponse");
    }

    /**
     * Tests the getOrderDetails asynchronous endpoint in Hurrican by simulating a schema-invalid
     * getOrderDetails payload from AtlasESB.
     */
    @CitrusTest
    @Test
    public void GetOrderDetailsCustomer_02_Test() {
        simulatorUseCase(SimulatorUseCase.GetOrderDetailsCustomer_02);
        atlas().sendOrderDetailsCustomerOrder("getOrderDetails");

        atlas().receiveErrorHandlingServiceMessage("errorNotification");
    }

    private Carrierbestellung createCarrierbestellung(GeneratedTaifunData generatedTaifunData) {
        AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withKundeNo(generatedTaifunData.getKunde().getKundeNo())
                .setPersist(false);
        Auftrag auftrag = hurrican().save(auftragBuilder.build());

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .withAuftragNoOrig(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig())
                .setPersist(false)
                .build();
        auftragDaten.setAuftragId(auftrag.getAuftragId());
        hurrican().save(auftragDaten);

        AuftragTechnik2Endstelle at2Es = new AuftragTechnik2EndstelleBuilder()
                .setPersist(false).build();
        hurrican().save(at2Es);

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .setPersist(false).build();
        auftragTechnik.setAuftragId(auftrag.getAuftragId());
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        hurrican().save(auftragTechnik);

        Carrierbestellung2Endstelle cb2Es = new Carrierbestellung2EndstelleBuilder()
                .setPersist(false).build();
        hurrican().save(cb2Es);

        Endstelle endstelle = new EndstelleBuilder()
                .setPersist(false).build();
        endstelle.setEndstelleGruppeId(at2Es.getId());
        endstelle.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
        endstelle.setCb2EsId(cb2Es.getId());
        hurrican().save(endstelle);

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder()
                .setPersist(false).build();
        carrierbestellung.setCb2EsId(cb2Es.getId());
        carrierbestellung.setVtrNr(Functions.randomNumber(6L));
        carrierbestellung.setLbz(Functions.randomNumber(8L));
        hurrican().save(carrierbestellung);

        return carrierbestellung;
    }
}
