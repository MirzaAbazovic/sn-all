/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;

/**
 *
 */
@Component
public class PortierungDurchwahlanlageMarshaller extends AbstractBaseMarshaller implements
        Function<RufnummernportierungAnlage, PortierungDurchwahlanlageType> {

    @Nullable
    @Override
    public PortierungDurchwahlanlageType apply(@Nullable RufnummernportierungAnlage input) {
        if (input == null) {
            return null;
        }

        PortierungDurchwahlanlageType portierungDurchwahlanlageType = V1_OBJECT_FACTORY
                .createPortierungDurchwahlanlageType();

        // generate the OnkzDurchwahlAbfragestell object
        OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelleType = V1_OBJECT_FACTORY
                .createOnkzDurchwahlAbfragestelleType();
        onkzDurchwahlAbfragestelleType.setAbfragestelle(input.getAbfragestelle());
        onkzDurchwahlAbfragestelleType.setDurchwahlnummer(input.getDurchwahlnummer());
        onkzDurchwahlAbfragestelleType.setONKZ(input.getOnkz());
        portierungDurchwahlanlageType.setOnkzDurchwahlAbfragestelle(onkzDurchwahlAbfragestelleType);

        // generate the the list of Rufnummernblocks
        if (input.getRufnummernbloecke() != null) {
            for (Rufnummernblock rufnummernblock : input.getRufnummernbloecke()) {
                RufnummernblockType rufnummernblockType = V1_OBJECT_FACTORY.createRufnummernblockType();
                rufnummernblockType.setRnrBlockBis(rufnummernblock.getRnrBlockBis());
                rufnummernblockType.setRnrBlockVon(rufnummernblock.getRnrBlockVon());
                portierungDurchwahlanlageType.getZuPortierenderRufnummernblock().add(rufnummernblockType);
            }
        }

        return portierungDurchwahlanlageType;
    }
}
