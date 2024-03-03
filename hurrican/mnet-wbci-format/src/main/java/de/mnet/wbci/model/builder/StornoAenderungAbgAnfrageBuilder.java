/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 19.09.13 
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class StornoAenderungAbgAnfrageBuilder<GF extends WbciGeschaeftsfall> extends
        StornoMitEndkundeStandortAnfrageBuilder<StornoAenderungAbgAnfrage<GF>, GF> {

    public StornoAenderungAbgAnfrageBuilder() {
        wbciRequest = new StornoAenderungAbgAnfrage<>();
    }

    @Override
    public StornoAenderungAbgAnfrage<GF> build() {
        return wbciRequest;
    }

    @Override
    public StornoAenderungAbgAnfrageBuilder<GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (StornoAenderungAbgAnfrageBuilder<GF>) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    public StornoAenderungAbgAnfrageBuilder<GF> withStornoGrund(String stornoGrund) {
        wbciRequest.setStornoGrund(stornoGrund);
        return this;
    }

    @Override
    public StornoAenderungAbgAnfrageBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        return (StornoAenderungAbgAnfrageBuilder<GF>) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public StornoAenderungAbgAnfrageBuilder<GF> withIoType(IOType ioType) {
        return (StornoAenderungAbgAnfrageBuilder<GF>) super.withIoType(ioType);
    }
}
