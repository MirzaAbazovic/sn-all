/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 19.09.13 
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class StornoAufhebungAufAnfrageBuilder<GF extends WbciGeschaeftsfall> extends
        StornoAnfrageBuilder<StornoAufhebungAufAnfrage<GF>, GF> {

    public StornoAufhebungAufAnfrageBuilder() {
        wbciRequest = new StornoAufhebungAufAnfrage<>();
    }

    @Override
    public StornoAufhebungAufAnfrage<GF> build() {
        return wbciRequest;
    }

    @Override
    public StornoAufhebungAufAnfrageBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        return (StornoAufhebungAufAnfrageBuilder<GF>) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public StornoAufhebungAufAnfrageBuilder<GF> withIoType(IOType ioType) {
        return (StornoAufhebungAufAnfrageBuilder<GF>) super.withIoType(ioType);
    }

    @Override
    public StornoAufhebungAufAnfrageBuilder<GF> withCreationDate(LocalDateTime creationDate) {
        super.withCreationDate(creationDate);
        return this;
    }
}
