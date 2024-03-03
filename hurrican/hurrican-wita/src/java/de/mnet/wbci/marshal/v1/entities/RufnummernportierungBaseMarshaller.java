/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungszeitfensterEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;

/**
 *
 */
public abstract class RufnummernportierungBaseMarshaller<RT extends RufnummernportierungType> extends
        AbstractBaseMarshaller implements
        Function<Rufnummernportierung, RT> {

    @Autowired
    protected PortierungDurchwahlanlageMarshaller portierungDurchwahlanlageMarshaller;
    @Autowired
    protected PortierungEinzelanschlussMarshaller portierungEinzelanschlussMarshaller;

    /**
     * Base apply method for basic marshalling of RufnummernportierungType types.
     *
     * @param rufnummernportierung
     * @param input
     * @return
     */
    public RT apply(RT rufnummernportierung, Rufnummernportierung input) {
        // differ between Anlagenanschluss and Einzelanschluss
        if (RufnummernportierungTyp.ANLAGE.equals(input.getTyp())) {
            rufnummernportierung.setAnlagenanschluss(portierungDurchwahlanlageMarshaller
                    .apply((RufnummernportierungAnlage) input));
        }
        else if (RufnummernportierungTyp.EINZEL.equals(input.getTyp())) {
            rufnummernportierung.setEinzelanschluss(portierungEinzelanschlussMarshaller
                    .apply((RufnummernportierungEinzeln) input));
        }
        else {
            throw new IllegalArgumentException("Rufnummernportierungstyp \"" + input.getTyp() + "\" is not supported!");
        }

        if (input.getPortierungszeitfenster() != null) {
            rufnummernportierung.setPortierungszeitfenster(PortierungszeitfensterEnumType.fromValue(input
                    .getPortierungszeitfenster().name()));
        }
        return rufnummernportierung;
    }
}
