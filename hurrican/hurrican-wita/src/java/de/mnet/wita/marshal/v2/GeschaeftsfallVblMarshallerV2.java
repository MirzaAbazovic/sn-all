/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragspositionVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.OnkzRnrRngNrType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TALVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.VerbundleistungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.VerbundleistungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.VerbundleistungType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallVbl;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;

@SuppressWarnings("Duplicates")
public class GeschaeftsfallVblMarshallerV2 extends GeschaeftsfallMarshallerV2<VerbundleistungType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallVbl);

        VerbundleistungType verbundleistungType = new VerbundleistungType();
        verbundleistungType.setVertragsnummer(vertragsNummer(input));
        verbundleistungType.setAnsprechpartner(ansprechPartner(input));
        verbundleistungType.setTermine(terminVbl(input));
        verbundleistungType.getAuftragsposition().addAll(auftragsPositionenVbl(input));
        verbundleistungType.setAnlagen(anlagen(input));
        verbundleistungType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setVBL(verbundleistungType);

        return gf;
    }

    private VerbundleistungTermineType terminVbl(Geschaeftsfall geschaeftsfall) {
        VerbundleistungTermineType termin = new VerbundleistungTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionVerbundleistungType> auftragsPositionenVbl(
            Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionVerbundleistungType auftragsPosition = new AuftragspositionVerbundleistungType();

        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionVerbundleistungType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionVerbundleistungTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProduktVbl(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALVerbundleistungType geschaeftsfallProduktVbl(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALVerbundleistungType result = new TALVerbundleistungType();
        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortA(standortKundeErweitert(input));
        result.setStandortB(standortKollokation(input));
        result.setSchaltangaben(schaltAngaben(input));
        result.setUebertragungsverfahren(getUebertragungsverfahren(input));
        result.setAnsprechpartnerMontage(ansprechPartnerMontage(input));
        result.setMontagehinweis(montageHinweis(input));
        result.setTerminReservierungsID(montageReservierungsID(input));
        BestandsSuche inputBestandSuche = input.getBestandsSuche();
        if (inputBestandSuche != null) {
            OnkzRnrRngNrType bestandSuche = new OnkzRnrRngNrType();
            bestandSuche.setONKZ(inputBestandSuche.getOnkz());
            bestandSuche.setRufnummer(inputBestandSuche.getRufnummer());
        }
        result.setVorabstimmungsID(input.getVorabstimmungsId());
        return result;
    }

    private VerbundleistungAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        VerbundleistungAnlagenType vblAnlagenType = new VerbundleistungAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            if (Anlagentyp.KUENDIGUNGSSCHREIBEN.equals(inputAnlage.getAnlagentyp())) {
                vblAnlagenType.setKuendigungsschreiben(kuendigungsOrLageplanAnlage(inputAnlage));
                set = true;
            }
            else {
                vblAnlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
                set = true;
            }
        }
        return (set) ? vblAnlagenType : null;
    }
}
