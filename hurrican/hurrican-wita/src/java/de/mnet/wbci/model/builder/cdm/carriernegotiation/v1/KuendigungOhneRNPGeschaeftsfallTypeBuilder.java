/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;

public class KuendigungOhneRNPGeschaeftsfallTypeBuilder extends AbstractKuendigungGeschaeftsfallTypeBuilder<KuendigungOhneRNPGeschaeftsfallType> {

    public KuendigungOhneRNPGeschaeftsfallTypeBuilder() {
        objectType = OBJECT_FACTORY.createKuendigungOhneRNPGeschaeftsfallType();
    }

    public KuendigungOhneRNPGeschaeftsfallTypeBuilder withAnschlussidentifikation(OnkzRufNrType onkzRufNrType) {
        objectType.setAnschlussidentifikation(onkzRufNrType);
        return this;
    }

}
