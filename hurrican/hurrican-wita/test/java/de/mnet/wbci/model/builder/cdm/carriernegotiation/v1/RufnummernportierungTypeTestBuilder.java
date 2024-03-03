/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungszeitfensterEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernListeType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;

public class RufnummernportierungTypeTestBuilder {

    public RufnummernportierungType buildValidEinzel() {
        RufnummernportierungType rtype = new RufnummernportierungType();
        rtype.setPortierungszeitfenster(PortierungszeitfensterEnumType.ZF_1);
        PortierungEinzelanschlussType einzel = new PortierungEinzelanschlussType();
        OnkzRufNrType onkz = new OnkzRufNrType();
        onkz.setONKZ("89");
        onkz.setRufnummer("123456");
        RufnummernListeType dnList = new RufnummernListeType();
        dnList.getZuPortierendeOnkzRnr().add(onkz);
        einzel.setRufnummernliste(dnList);
        einzel.setAlleRufnummern(Boolean.TRUE);
        rtype.setEinzelanschluss(einzel);
        return rtype;
    }

}
