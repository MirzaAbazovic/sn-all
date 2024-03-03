/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 12:01:16
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;


/**
 * EntityBuilder for EQCrossConnection objects
 *
 *
 */
@SuppressWarnings("unused")
public class AnsprechpartnerBuilder extends AbstractCCIDModelBuilder<AnsprechpartnerBuilder, Ansprechpartner> {

    private CCAddressBuilder addressBuilder;
    private AuftragBuilder auftragBuilder;
    @ReferencedEntityId("typeRefId")
    private ReferenceBuilder referenceBuilder;
    private Long typeRefId;
    private Ansprechpartner.Typ type = Typ.KUNDE;
    private Boolean preferred = Boolean.TRUE;
    private String text = "Test Ansprechpartner";


    public CCAddressBuilder getAddressBuilder() {
        return addressBuilder;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public AnsprechpartnerBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AnsprechpartnerBuilder withType(Ansprechpartner.Typ type) {
        this.type = type;
        this.typeRefId = type.refId();
        return this;
    }

    public AnsprechpartnerBuilder withReferenceBuilder(ReferenceBuilder referenceBuilder) {
        this.referenceBuilder = referenceBuilder;
        return this;
    }

    public AnsprechpartnerBuilder withAddressBuilder(CCAddressBuilder addressBuilder) {
        this.addressBuilder = addressBuilder;
        return this;
    }

    public AnsprechpartnerBuilder withPreferred(Boolean preferred) {
        this.preferred = preferred;
        return this;
    }
}
