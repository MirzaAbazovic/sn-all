/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragType;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;

@SuppressWarnings("Duplicates")
public abstract class RequestMarshallerV2<T extends MnetWitaRequest, U extends AuftragType>
        extends AbstractBaseMarshallerV2
        implements Function<T, U> {

    private KundeMarshallerV2 kundeRequestGenerator = new KundeMarshallerV2();
    private KennerMarshallerV2 kennerMarshaller = new KennerMarshallerV2();

    private final Map<GeschaeftsfallTyp, GeschaeftsfallMarshallerV2<?>> gfGeneratorMap = ImmutableMap
            .<GeschaeftsfallTyp, GeschaeftsfallMarshallerV2<?>>builder()
            .put(BEREITSTELLUNG, new GeschaeftsfallNeuMarshallerV2())
            .put(KUENDIGUNG_KUNDE, new GeschaeftsfallKueKdMarshallerV2())
            .put(LEISTUNGS_AENDERUNG, new GeschaeftsfallLaeMarshallerV2())
            .put(LEISTUNGSMERKMAL_AENDERUNG, new GeschaeftsfallLmaeMarshallerV2())
            .put(PORTWECHSEL, new GeschaeftsfallSerPowMarshallerV2())
            .put(PROVIDERWECHSEL, new GeschaeftsfallPvMarshallerV2())
            .put(VERBUNDLEISTUNG, new GeschaeftsfallVblMarshallerV2()).build();

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
        GeschaeftsfallMarshallerV2<?> gfReqGenerator = gfGeneratorMap.get(geschaeftsfalltyp);
        if (gfReqGenerator == null) {
            throw new RuntimeException("Geschaeftsfall " + geschaeftsfalltyp + " not supported by marshaller");
        }

        auftragType.setGeschaeftsfall(gfReqGenerator.generate(input.getGeschaeftsfall()));

        return auftragType;
    }

    abstract U createAuftragType(T input);
}
