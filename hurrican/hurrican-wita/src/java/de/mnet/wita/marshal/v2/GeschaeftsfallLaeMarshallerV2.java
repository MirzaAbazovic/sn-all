/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragspositionLeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsaenderungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsaenderungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TALLeistungsaenderungType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallLae;
import de.mnet.wita.message.common.Anlage;

@SuppressWarnings("Duplicates")
public class GeschaeftsfallLaeMarshallerV2 extends GeschaeftsfallMarshallerV2<LeistungsaenderungType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallLae);

        LeistungsaenderungType leistungsaenderungType = new LeistungsaenderungType();

        leistungsaenderungType.setAnsprechpartner(ansprechPartner(input));
        leistungsaenderungType.setTermine(terminLae(input));
        leistungsaenderungType.getAuftragsposition().addAll(auftragsPositionenLae(input));
        leistungsaenderungType.setVertragsnummer(vertragsNummer(input));
        leistungsaenderungType.setAnlagen(anlagen(input));
        leistungsaenderungType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setLAE(leistungsaenderungType);

        return gf;
    }

    private LeistungsaenderungTermineType terminLae(Geschaeftsfall geschaeftsfall) {
        LeistungsaenderungTermineType termin = new LeistungsaenderungTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionLeistungsaenderungType> auftragsPositionenLae(
            Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionLeistungsaenderungType auftragsPosition = new AuftragspositionLeistungsaenderungType();

        auftragsPosition.setAktionscode(MwfToWitaConverterV2.aktionsCode(input.getAktionsCode()));
        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionLeistungsaenderungType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionLeistungsaenderungTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProduktLae(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALLeistungsaenderungType geschaeftsfallProduktLae(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALLeistungsaenderungType result = new TALLeistungsaenderungType();

        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortA(standortKunde(input));
        result.setStandortB(standortKollokation(input));
        result.setSchaltangaben(schaltAngaben(input));
        result.setAnsprechpartnerMontage(ansprechPartnerMontage(input));
        result.setMontagehinweis(montageHinweis(input));
        result.setUebertragungsverfahren(getUebertragungsverfahren(input));
        result.setBestandsvalidierung2(leitungsbezeichnung2(auftragsposition));

        return result;
    }

    private LeistungsaenderungAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        LeistungsaenderungAnlagenType leistungsaenderungAnlagenType = new LeistungsaenderungAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            leistungsaenderungAnlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
            set = true;
        }
        return (set) ? leistungsaenderungAnlagenType : null;
    }
}
