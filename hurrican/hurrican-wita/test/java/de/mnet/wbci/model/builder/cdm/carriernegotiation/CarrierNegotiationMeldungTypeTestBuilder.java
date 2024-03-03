package de.mnet.wbci.model.builder.cdm.carriernegotiation;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;

public interface CarrierNegotiationMeldungTypeTestBuilder<T> extends CarrierNegotiationTypeBuilder<T> {

    /**
     * This interface represents all TestBuilders for the CarrierNegotationTypes.
     *
     * @return a valid {@link T} for a specific Geschäftsfall
     */
    T buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType);
}
