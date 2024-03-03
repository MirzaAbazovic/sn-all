/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.MeldungsattributeERLMKType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.MeldungstypERLMKType;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

@SuppressWarnings("Duplicates")
@Component
public class ErledigtMeldungKundeMarshallerV2 extends MeldungMarshallerV2<ErledigtMeldungKunde, MeldungstypERLMKType> {

    @Override
    protected MeldungstypERLMKType createMeldungstyp(ErledigtMeldungKunde erlmk) {
        MeldungstypERLMKType erledigtMeldungKunde = new MeldungstypERLMKType();
        erledigtMeldungKunde.setMeldungsattribute(createMeldungsattributeErlmkType(erlmk));
        erledigtMeldungKunde.setMeldungspositionen(createMeldungspositionenErlmk(erlmk));
        return erledigtMeldungKunde;
    }

    private MeldungsattributeERLMKType createMeldungsattributeErlmkType(ErledigtMeldungKunde erlmk) {
        MeldungsattributeERLMKType meldungsattributeERLMKType = new MeldungsattributeERLMKType();
        meldungsattributeERLMKType.setExterneAuftragsnummer(erlmk.getExterneAuftragsnummer());
        meldungsattributeERLMKType.setKundennummer(erlmk.getKundenNummer());
        meldungsattributeERLMKType.setKundennummerBesteller(erlmk.getKundennummerBesteller());
        meldungsattributeERLMKType.setVertragsnummer(erlmk.getVertragsNummer());
        return meldungsattributeERLMKType;
    }

    private MeldungstypERLMKType.Meldungspositionen createMeldungspositionenErlmk(ErledigtMeldungKunde erlmk) {
        MeldungstypERLMKType.Meldungspositionen positionen = new MeldungstypERLMKType.Meldungspositionen();
        Set<MeldungsPosition> meldungspositionen = erlmk.getMeldungsPositionen();
        for (MeldungsPosition position : meldungspositionen) {
            positionen.getPosition().add(createMeldungsPosition(position));
        }
        return positionen;
    }

}
