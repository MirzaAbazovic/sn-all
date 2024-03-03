/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragspositionProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.ProviderwechselAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.ProviderwechselTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.ProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TALProviderwechselType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallPv;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;

@SuppressWarnings("Duplicates")
public class GeschaeftsfallPvMarshallerV2 extends GeschaeftsfallMarshallerV2<ProviderwechselType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallPv);

        ProviderwechselType providerwechselType = new ProviderwechselType();
        providerwechselType.setAnsprechpartner(ansprechPartner(input));
        providerwechselType.setTermine(terminPv(input));
        providerwechselType.getAuftragsposition().addAll(auftragsPositionenPv(input));
        providerwechselType.setVertragsnummer(vertragsNummer(input));
        providerwechselType.setAnlagen(anlagen(input));
        providerwechselType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setPV(providerwechselType);

        return gf;
    }

    private ProviderwechselTermineType terminPv(Geschaeftsfall geschaeftsfall) {
        ProviderwechselTermineType termin = new ProviderwechselTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionProviderwechselType> auftragsPositionenPv(Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionProviderwechselType auftragsPosition = new AuftragspositionProviderwechselType();

        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionProviderwechselType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionProviderwechselTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProduktPv(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALProviderwechselType geschaeftsfallProduktPv(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALProviderwechselType result = new TALProviderwechselType();

        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortA(standortKundeErweitert(input));
        result.setStandortB(standortKollokation(input));
        result.setSchaltangaben(schaltAngaben(input));
        result.setUebertragungsverfahren(getUebertragungsverfahren(input));

        result.setAnsprechpartnerMontage(ansprechPartnerMontage(input));
        result.setMontagehinweis(montageHinweis(input));
        result.setTerminReservierungsID(montageReservierungsID(input));


        result.setVorabstimmungsID(input.getVorabstimmungsId());
        return result;
    }

    private ProviderwechselAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        ProviderwechselAnlagenType pvAnlagenType = new ProviderwechselAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            if (Anlagentyp.KUENDIGUNGSSCHREIBEN.equals(inputAnlage.getAnlagentyp())) {
                pvAnlagenType.setKuendigungsschreiben(kuendigungsOrLageplanAnlage(inputAnlage));
                set = true;
            }
            else {
                pvAnlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
                set = true;
            }
        }
        return (set) ? pvAnlagenType : null;
    }
}

