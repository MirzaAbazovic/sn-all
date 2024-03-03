/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoMitEndkundeStandortAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public abstract class StornoMitEndkundeStandortAnfrageBuilder<T extends StornoMitEndkundeStandortAnfrage<GF>, GF extends WbciGeschaeftsfall>
        extends StornoAnfrageBuilder<T, GF> {

    public StornoMitEndkundeStandortAnfrageBuilder<T, GF> withEndkunde(PersonOderFirma personOderFirma) {
        wbciRequest.setEndkunde(personOderFirma);
        return this;
    }

    public StornoMitEndkundeStandortAnfrageBuilder<T, GF> withStandort(Standort standort) {
        wbciRequest.setStandort(standort);
        return this;
    }


}
