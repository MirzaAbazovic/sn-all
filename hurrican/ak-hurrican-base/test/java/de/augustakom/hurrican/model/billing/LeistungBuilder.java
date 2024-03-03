/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 09:41:53
 */
package de.augustakom.hurrican.model.billing;


/**
 * Entity-Builder for Leistung objects.
 *
 *
 */
@SuppressWarnings("unused")
public class LeistungBuilder extends BillingEntityBuilder<LeistungBuilder, Leistung> {

    private Long leistungNo = null;
    private Long leistungNoOrig = null;
    private String name = null;
    private Long externProduktNo = null;
    private Long externLeistungNo = null;
    private Long externMiscNo = null;
    private Long oeNoOrig = null;
    private String fibuGebuehrenArt = null;
    private Boolean techExport = null;
    private Float preis = null;
    private String preisQuelle = null;
    private String vatCode = null;
    private String leistungKat = null;
    private Boolean generateBillPos = null;
    private String billingCode = null;

    public LeistungBuilder withLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
        return this;
    }

    public LeistungBuilder withExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
        return this;
    }

    public LeistungBuilder withExternProduktNo(Long externProduktNo) {
        this.externProduktNo = externProduktNo;
        return this;
    }

}


