/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 19.09.13 
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class StornoAenderungAufAnfrageBuilder<GF extends WbciGeschaeftsfall> extends
        StornoMitEndkundeStandortAnfrageBuilder<StornoAenderungAufAnfrage<GF>, GF> {

    public StornoAenderungAufAnfrageBuilder() {
        wbciRequest = new StornoAenderungAufAnfrage<>();
    }

    @Override
    public StornoAenderungAufAnfrage<GF> build() {
        return wbciRequest;
    }


    @Override
    public StornoAenderungAufAnfrageBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        return (StornoAenderungAufAnfrageBuilder<GF>) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public StornoAenderungAufAnfrageBuilder<GF> withIoType(IOType ioType) {
        return (StornoAenderungAufAnfrageBuilder<GF>) super.withIoType(ioType);
    }
}
