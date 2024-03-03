/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2014
 */
package de.mnet.wita.marshal.v1;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypERLMKType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypRUEMPVType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.UpdateOrder;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.OutgoingMeldung;
import de.mnet.wita.message.meldung.RueckMeldungPv;

@Component
public class UpdateOrderMarshaller extends AbstractBaseMarshaller implements Function<OutgoingMeldung, UpdateOrder> {

    @Autowired
    private ErledigtMeldungKundeMarshaller erledigtMeldungKundeMarshaller;

    @Autowired
    private RueckMeldungPvMarshaller rueckMeldungPvMarshaller;

    @Nullable
    @Override
    public UpdateOrder apply(@Nullable OutgoingMeldung input) {
        if (input == null) {
            return null;
        }

        UpdateOrder updateOrder = OBJECT_FACTORY.createUpdateOrder();
        updateOrder.setOrder(createAuftragstypType(input));
        updateOrder.setMessage(createMessage(input));

        return updateOrder;
    }

    private AuftragstypType createAuftragstypType(OutgoingMeldung input) {
        AuftragstypType auftragstypType = new AuftragstypType();
        auftragstypType.setGeschaeftsfall(MwfToWitaConverter.map(input.getGeschaeftsfallTyp()));
        auftragstypType.setAenderungsKennzeichen(MwfToWitaConverter.map(input.getAenderungsKennzeichen()));
        auftragstypType.setGeschaeftsfallart(MwfToWitaConverter.map(input.getGeschaeftsfallTyp().getGeschaeftsfallArt()));
        return auftragstypType;
    }

    private UpdateOrder.Message createMessage(OutgoingMeldung input) {
        UpdateOrder.Message message = new UpdateOrder.Message();
        if (input instanceof ErledigtMeldungKunde) {
            MeldungstypERLMKType erlmk = erledigtMeldungKundeMarshaller.apply((ErledigtMeldungKunde) input);
            message.setERLMK(erlmk);
        }
        else if (input instanceof RueckMeldungPv) {
            MeldungstypRUEMPVType ruempv = rueckMeldungPvMarshaller.apply((RueckMeldungPv) input);
            message.setRUEMPV(ruempv);
        }
        return message;
    }

}
