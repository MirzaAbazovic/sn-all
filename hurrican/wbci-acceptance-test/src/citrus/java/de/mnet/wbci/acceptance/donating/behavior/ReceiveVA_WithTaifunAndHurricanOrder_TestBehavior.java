/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2015
 */
package de.mnet.wbci.acceptance.donating.behavior;

import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.mnet.wbci.citrus.builder.HurricanAuftragBuilder;

/**
 *
 */
public class ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior extends ReceiveVA_TestBehavior {

    private Long produktID = Produkt.PROD_ID_MAXI_DSL_ANALOG;
    private Long standortTypRefId = HVTStandort.HVT_STANDORT_TYP_HVT;
    private Long carrierID = Carrier.ID_DTAG;

    public ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior(GeneratedTaifunData generatedTaifunData) {
        super();
        withGeneratedTaifunData(generatedTaifunData);
        withSearchBuildings(false, false);
    }


    @Override
    public void apply() {
        Long billingOrderNoOrig = generatedTaifunData.getBillingAuftrag().getAuftragNoOrig();
        Auftrag auftrag = new HurricanAuftragBuilder(hurrican().getCcAuftragDAO())
                .withCarrierId(carrierID)
                .buildHurricanAuftrag(
                        produktID,
                        generatedTaifunData.getKunde().getKundeNo(),
                        billingOrderNoOrig,
                        standortTypRefId,
                        false, false, false)
                .getFirst();
        super.apply();
        hurrican().assertVaRequestAssignedToOrder(billingOrderNoOrig);
    }

    /**
     * Override the default produkt ID, to build up the hurrican order
     *
     * @param produktID reference e.g. to {@link Produkt#PROD_ID_MAXI_DSL_ANALOG}.
     */
    public ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior withProduktID(Long produktID) {
        this.produktID = produktID;
        return this;
    }

    /**
     * Override the default carrier ID, to build up the hurrican order
     *
     * @param carrierID reference e.g. to {@link Carrier#CARRIER_DTAG}.
     */
    public ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior withCarrierID(Long carrierID) {
        this.carrierID = carrierID;
        return this;
    }

    /**
     * Override the default standort type reference ID, to build up the hurrican order
     *
     * @param standortTypRefId reference e.g. to {@link HVTStandort#HVT_STANDORT_TYP_HVT}.
     */
    public ReceiveVA_WithTaifunAndHurricanOrder_TestBehavior withStandortType(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }
}
