/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2014
 */
package de.mnet.wita.unmarshal.v1;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypAbstractType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.NotifyUpdateOrder;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;

@Component
public class NotifyUpdateOrderUnmarshaller implements Function<NotifyUpdateOrder, WitaMessage> {

    private Map<Class<? extends MeldungstypAbstractType>, AbstractUnmarshallerFunction<? extends MeldungstypAbstractType>> mappings;

    @Autowired
    private List<AbstractUnmarshallerFunction<? extends MeldungstypAbstractType>> unmarshallerFunctions;


    @PostConstruct
    public void init() {
        mappings = new HashMap<>();
        for (AbstractUnmarshallerFunction<? extends MeldungstypAbstractType> unmarshallerFunction : unmarshallerFunctions) {
            mappings.put(unmarshallerFunction.getClassToUnmarshall(), unmarshallerFunction);
        }
    }

    @Nullable
    @Override
    public WitaMessage apply(@Nullable NotifyUpdateOrder input) {
        MeldungstypAbstractType meldungsTyp = extractMeldungsTyp(input.getMessage());

        WitaMessage result;
        if (mappings.containsKey(meldungsTyp.getClass())) {
            result = mappings.get(meldungsTyp.getClass()).apply(meldungsTyp);
        }
        else {
            throw new UnmarshallingFailureException("Meldungstype " + meldungsTyp.getClass() + " not yet supported");
        }

        AuftragstypType auftragstypType = input.getOrder();

        Meldung<?> meldung = (Meldung<?>) result;
        meldung.setGeschaeftsfallTyp(GeschaeftsfallTyp.buildFromMeldung(auftragstypType.getGeschaeftsfall().value()));
        meldung.setAenderungsKennzeichen(AenderungsKennzeichen.build(auftragstypType.getAenderungsKennzeichen().value()));
        meldung.setVersandZeitstempel(DateConverterUtils.toDate(input.getCaptured()));
        meldung.setCdmVersion(WitaCdmVersion.V1);
        return meldung;

    }

    private MeldungstypAbstractType extractMeldungsTyp(NotifyUpdateOrder.Message message) {
        if (message.getABBM() != null) {
            return message.getABBM();
        }
        if (message.getABBMPV() != null) {
            return message.getABBMPV();
        }
        if (message.getABM() != null) {
            return message.getABM();
        }
        if (message.getABMPV() != null) {
            return message.getABMPV();
        }
        if (message.getAKMPV() != null) {
            return message.getAKMPV();
        }
        if (message.getENTM() != null) {
            return message.getENTM();
        }
        if (message.getENTMPV() != null) {
            return message.getENTMPV();
        }
        if (message.getERGM() != null) {
            return message.getERGM();
        }
        if (message.getERLM() != null) {
            return message.getERLM();
        }
        if (message.getERLMPV() != null) {
            return message.getERLMPV();
        }
        if (message.getMTAM() != null) {
            return message.getMTAM();
        }
        if (message.getQEB() != null) {
            return message.getQEB();
        }
        if (message.getTAM() != null) {
            return message.getTAM();
        }
        if (message.getVZM() != null) {
            return message.getVZM();
        }
        if (message.getVZMPV() != null) {
            return message.getVZMPV();
        }
        throw new UnmarshallingFailureException("Meldungstyp cannot be NULL");
    }
}
