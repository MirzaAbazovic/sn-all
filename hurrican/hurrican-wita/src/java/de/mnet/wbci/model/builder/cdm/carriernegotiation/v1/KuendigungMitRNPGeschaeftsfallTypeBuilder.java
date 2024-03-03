/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;

public class KuendigungMitRNPGeschaeftsfallTypeBuilder extends AbstractKuendigungGeschaeftsfallTypeBuilder<KuendigungMitRNPGeschaeftsfallType> {

    public KuendigungMitRNPGeschaeftsfallTypeBuilder() {
        objectType = OBJECT_FACTORY.createKuendigungMitRNPGeschaeftsfallType();
    }

    public KuendigungMitRNPGeschaeftsfallTypeBuilder withRufnummernportierungType(RufnummernportierungType rufnummernportierungType) {
        objectType.setRufnummernPortierung(rufnummernportierungType);
        return this;
    }

}
