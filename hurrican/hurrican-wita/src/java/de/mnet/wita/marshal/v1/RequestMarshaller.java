/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragType;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;

public abstract class RequestMarshaller<T extends MnetWitaRequest, U extends AuftragType>
        extends AbstractBaseMarshaller
        implements Function<T, U> {

    private KundeMarshaller kundeRequestGenerator = new KundeMarshaller();
    private KennerMarshaller kennerMarshaller = new KennerMarshaller();

    private final Map<GeschaeftsfallTyp, GeschaeftsfallMarshaller<?>> gfGeneratorMap = ImmutableMap
            .<GeschaeftsfallTyp, GeschaeftsfallMarshaller<?>>builder()
            .put(BEREITSTELLUNG, new GeschaeftsfallNeuMarshaller())
            .put(KUENDIGUNG_KUNDE, new GeschaeftsfallKueKdMarshaller())
            .put(LEISTUNGS_AENDERUNG, new GeschaeftsfallLaeMarshaller())
            .put(LEISTUNGSMERKMAL_AENDERUNG, new GeschaeftsfallLmaeMarshaller())
            .put(PORTWECHSEL, new GeschaeftsfallSerPowMarshaller())
            .put(PROVIDERWECHSEL, new GeschaeftsfallPvMarshaller())
            .put(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, new GeschaeftsfallRexMkMarshaller())
            .put(VERBUNDLEISTUNG, new GeschaeftsfallVblMarshaller()).build();

    @Nullable
    @Override
    public U apply(@Nullable T input) {
        U auftragType = createAuftragType(input);

        auftragType.setExterneAuftragsnummer(input.getExterneAuftragsnummer());
        auftragType.setKunde(kundeRequestGenerator.generate(input.getKunde()));
        if (input.getBesteller() != null) {
            auftragType.setBesteller(kundeRequestGenerator.generate(input.getBesteller()));
        }
        auftragType.setKenner(kennerMarshaller.apply(input));

        GeschaeftsfallTyp geschaeftsfalltyp = input.getGeschaeftsfall().getGeschaeftsfallTyp();
        GeschaeftsfallMarshaller<?> gfReqGenerator = gfGeneratorMap.get(geschaeftsfalltyp);
        if (gfReqGenerator == null) {
            throw new RuntimeException("Geschaeftsfall " + geschaeftsfalltyp + " not supported by marshaller");
        }

        auftragType.setGeschaeftsfall(gfReqGenerator.generate(input.getGeschaeftsfall()));

        return auftragType;
    }

    abstract U createAuftragType(T input);
}
