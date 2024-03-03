/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 19.09.13 
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class StornoAufhebungAbgAnfrageBuilder<GF extends WbciGeschaeftsfall> extends
        StornoMitEndkundeStandortAnfrageBuilder<StornoAufhebungAbgAnfrage<GF>, GF> {

    public StornoAufhebungAbgAnfrageBuilder() {
        wbciRequest = new StornoAufhebungAbgAnfrage<>();
    }

    @Override
    public StornoAufhebungAbgAnfrage<GF> build() {
        return wbciRequest;
    }

    @Override
    public StornoAufhebungAbgAnfrageBuilder<GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (StornoAufhebungAbgAnfrageBuilder<GF>) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    public StornoAufhebungAbgAnfrageBuilder<GF> withStornoGrund(String stornoGrund) {
        wbciRequest.setStornoGrund(stornoGrund);
        return this;
    }

    @Override
    public StornoAufhebungAbgAnfrageBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        return (StornoAufhebungAbgAnfrageBuilder<GF>) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public StornoAufhebungAbgAnfrageBuilder<GF> withIoType(IOType ioType) {
        return (StornoAufhebungAbgAnfrageBuilder<GF>) super.withIoType(ioType);
    }

    @Override
    public StornoAufhebungAbgAnfrageBuilder<GF> withCreationDate(LocalDateTime creationDate) {
        super.withCreationDate(creationDate);
        return this;
    }
}
