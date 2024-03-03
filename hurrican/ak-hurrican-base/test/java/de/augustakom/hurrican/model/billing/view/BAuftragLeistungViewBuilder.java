/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2012 08:03:45
 */
package de.augustakom.hurrican.model.billing.view;

import java.util.*;

import de.augustakom.hurrican.model.billing.BillingEntityBuilder;

/**
 * EntityBuilder fuer {@link BAuftragLeistungView} Objekte.
 */
@SuppressWarnings("unused")
public class BAuftragLeistungViewBuilder extends BillingEntityBuilder<BAuftragLeistungViewBuilder, BAuftragLeistungView> {

    private final Long itemNo = null;
    private final Long itemNoOrig = null;
    private final Long kundeNo = null;
    private final String atyp = null;
    private final Integer astatus = null;
    private final Long auftragNo = null;
    private Long auftragNoOrig = null;
    private final String oldAuftragNoOrig = null;
    private Long menge = Long.valueOf(1);
    private Integer bundleOrderNo = null;
    private final Long leistungNo = null;
    private Long leistungNoOrig = null;
    private final String serviceValueParam = null;
    private Long externMiscNo = null;
    private Long externProduktNo = null;
    private final Long externLeistungNo = null;
    private final Long oeNo = null;
    private final Long oeNoOrig = null;
    private String oeName = null;
    private String leistungName = null;
    private final String auftragHistStatus = null;
    private final Date auftragGueltigVon = null;
    private final Date auftragGueltigBis = null;
    private Date auftragPosGueltigVon = null;
    private Date auftragPosGueltigBis = null;
    private Date auftragPosChargedUntil = null;
    private final String auftragPosParameter = null;
    private final Long rechInfoNoOrig = null;
    private String freeText;

    public BAuftragLeistungViewBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public BAuftragLeistungViewBuilder withOeName(String oeName) {
        this.oeName = oeName;
        return this;
    }

    public BAuftragLeistungViewBuilder withBundleOrderNo(Integer bundleOrderNo) {
        this.bundleOrderNo = bundleOrderNo;
        return this;
    }

    public BAuftragLeistungViewBuilder withExternProduktNo(Long externProduktNo) {
        this.externProduktNo = externProduktNo;
        return this;
    }

    public BAuftragLeistungViewBuilder withExternMiscNo(Long extMiscNo) {
        this.externMiscNo = extMiscNo;
        return this;
    }

    public BAuftragLeistungViewBuilder withAuftragPosGueltigVon(Date auftragPosGueltigVon) {
        this.auftragPosGueltigVon = auftragPosGueltigVon;
        return this;
    }

    public BAuftragLeistungViewBuilder withAuftragPosGueltigBis(Date auftragPosGueltigBis) {
        this.auftragPosGueltigBis = auftragPosGueltigBis;
        return this;
    }

    public BAuftragLeistungViewBuilder withMenge(Long menge) {
        this.menge = menge;
        return this;
    }

    public BAuftragLeistungViewBuilder withAuftragPosChargedUntil(Date chargedUntil) {
        this.auftragPosChargedUntil = chargedUntil;
        return this;
    }

    public BAuftragLeistungViewBuilder withLeistungName(String leistungName) {
        this.leistungName = leistungName;
        return this;
    }

    public BAuftragLeistungViewBuilder withLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
        return this;
    }

}
