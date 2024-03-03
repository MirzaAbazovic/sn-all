/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernListeType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;

public class RufnummernPortierungMitPortierungskennerTypeTestBuilder {

    public RufnummernportierungMitPortierungskennungType buildValid() {
        RufnummernportierungMitPortierungskennungType type = new RufnummernportierungMitPortierungskennungType();
        type.setPortierungskennungPKIauf("D001");

        PortierungEinzelanschlussType einzel = new PortierungEinzelanschlussType();
        OnkzRufNrType onkz = new OnkzRufNrType();
        onkz.setONKZ("89");
        onkz.setRufnummer("123456");
        RufnummernListeType dnList = new RufnummernListeType();
        dnList.getZuPortierendeOnkzRnr().add(onkz);
        einzel.setRufnummernliste(dnList);
        einzel.setAlleRufnummern(Boolean.TRUE);

        type.setEinzelanschluss(einzel);
        return type;
    }

}
