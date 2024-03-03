/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public abstract class StornoAnfrageBuilder<T extends StornoAnfrage<GF>, GF extends WbciGeschaeftsfall> extends WbciRequestBuilder<T, GF> {
    public StornoAnfrageBuilder<T, GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        wbciRequest.setVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }

    public StornoAnfrageBuilder<T, GF> withAenderungsId(String aenderungsId) {
        wbciRequest.setAenderungsId(aenderungsId);
        return this;
    }

    @Override
    public T build() {
        return wbciRequest;
    }

}
