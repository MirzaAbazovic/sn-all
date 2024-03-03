/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;

@Component
public class RufnummernPortierungUnmarshaller<RT extends RufnummernportierungType>
        implements Function<RT, Rufnummernportierung> {

    @Autowired
    private RufnummernportierungMitPortierungskennungUnmarshaller rufnummernportierungMitPortierungskennungUnmarshaller;
    @Autowired
    private RufnummernlisteUnmarshaller rufnummernlisteUnmarshaller;
    @Autowired
    private RufnummernbloeckeUnmarshaller rufnummernbloeckeUnmarshaller;

    @Nullable
    @Override
    public Rufnummernportierung apply(@Nullable RT input) {
        if (input == null) {
            return null;
        }

        Rufnummernportierung rufnummernportierung = null;
        if (input.getAnlagenanschluss() != null) {
            PortierungDurchwahlanlageType anlage = input.getAnlagenanschluss();
            rufnummernportierung = new RufnummernportierungAnlageBuilder()
                    .withOnkz(anlage.getOnkzDurchwahlAbfragestelle().getONKZ())
                    .withAbfragestelle(anlage.getOnkzDurchwahlAbfragestelle().getAbfragestelle())
                    .withDurchwahlnummer(anlage.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer())
                    .withRufnummernbloecke(rufnummernbloeckeUnmarshaller.apply(anlage.getZuPortierenderRufnummernblock()))
                    .withPortierungszeitfenster(Portierungszeitfenster.valueOf(input.getPortierungszeitfenster().value()))
                    .withPortierungskennungPKIauf(rufnummernportierungMitPortierungskennungUnmarshaller.apply(input))
                    .build();
        }
        else if (input.getEinzelanschluss() != null) {
            PortierungEinzelanschlussType einzel = input.getEinzelanschluss();
            rufnummernportierung = new RufnummernportierungEinzelnBuilder()
                    .withRufnummerOnkzs(rufnummernlisteUnmarshaller.apply(einzel.getRufnummernliste()))
                    .withAlleRufnummernPortieren(einzel.isAlleRufnummern())
                    .withPortierungszeitfenster(Portierungszeitfenster.valueOf(input.getPortierungszeitfenster().value()))
                    .withPortierungskennungPKIauf(rufnummernportierungMitPortierungskennungUnmarshaller.apply(input))
                    .build();
        }

        return rufnummernportierung;
    }

}
