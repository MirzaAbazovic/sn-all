/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragspositionLeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsmerkmalAenderungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsmerkmalAenderungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeistungsmerkmalPositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TALLeistungsmerkmalAenderungType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallLmae;
import de.mnet.wita.message.common.Anlage;

@SuppressWarnings("Duplicates")
public class GeschaeftsfallLmaeMarshallerV2 extends GeschaeftsfallMarshallerV2<LeistungsmerkmalAenderungType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallLmae);

        LeistungsmerkmalAenderungType leistungsmerkmalAenderungType = new LeistungsmerkmalAenderungType();

        leistungsmerkmalAenderungType.setAnsprechpartner(ansprechPartner(input));
        leistungsmerkmalAenderungType.setTermine(terminLmae(input));
        leistungsmerkmalAenderungType.getAuftragsposition().addAll(auftragsPositionenLmae(input));
        leistungsmerkmalAenderungType.setVertragsnummer(vertragsNummer(input));
        leistungsmerkmalAenderungType.setAnlagen(anlagen(input));
        leistungsmerkmalAenderungType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setAENLMAE(leistungsmerkmalAenderungType);

        return gf;
    }

    private LeistungsmerkmalAenderungTermineType terminLmae(Geschaeftsfall geschaeftsfall) {
        LeistungsmerkmalAenderungTermineType termin = new LeistungsmerkmalAenderungTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionLeistungsmerkmalAenderungType> auftragsPositionenLmae(
            Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionLeistungsmerkmalAenderungType auftragsPosition = new AuftragspositionLeistungsmerkmalAenderungType();

        auftragsPosition.setAktionscode(MwfToWitaConverterV2.aktionsCode(input.getAktionsCode()));
        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionLeistungsmerkmalAenderungType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionLeistungsmerkmalAenderungTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProdukt(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        LeistungsmerkmalPositionType leistungsmerkmalPositionType = new LeistungsmerkmalPositionType();
        leistungsmerkmalPositionType.setAktionscode(MwfToWitaConverterV2.aktionsCode(input.getPosition().getAktionsCode()));
        leistungsmerkmalPositionType.setProdukt(produkt(input.getPosition()));

        LeistungsmerkmalPositionType.GeschaeftsfallProdukt subGfProdukt = OBJECT_FACTORY.createLeistungsmerkmalPositionTypeGeschaeftsfallProdukt();
        subGfProdukt.setTAL(geschaeftsfallProdukt(input.getPosition()));
        leistungsmerkmalPositionType.setGeschaeftsfallProdukt(subGfProdukt);

        auftragsPosition.getPosition().add(leistungsmerkmalPositionType);

        return Collections.singletonList(auftragsPosition);
    }

    private TALLeistungsmerkmalAenderungType geschaeftsfallProdukt(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALLeistungsmerkmalAenderungType result = new TALLeistungsmerkmalAenderungType();

        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortA(standortKunde(input));
        result.setStandortB(standortKollokation(input));
        result.setSchaltangaben(schaltAngaben(input));
        result.setUebertragungsverfahren(getUebertragungsverfahren(input));
        result.setBestandsvalidierung2(leitungsbezeichnung2(auftragsposition));

        return result;
    }

    private LeistungsmerkmalAenderungAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        LeistungsmerkmalAenderungAnlagenType leistungsmerkmalAenderungAnlagenType = new LeistungsmerkmalAenderungAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            leistungsmerkmalAenderungAnlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
            set = true;
        }
        return (set) ? leistungsmerkmalAenderungAnlagenType : null;
    }

}
