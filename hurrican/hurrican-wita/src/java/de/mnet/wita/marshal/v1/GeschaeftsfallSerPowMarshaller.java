/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionPortwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALPortwechselType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallSerPow;
import de.mnet.wita.message.common.Anlage;

public class GeschaeftsfallSerPowMarshaller extends GeschaeftsfallMarshaller<PortwechselType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallSerPow);

        PortwechselType portwechselType = new PortwechselType();
        portwechselType.setAnsprechpartner(ansprechPartner(input));
        portwechselType.setTermine(termin(input));
        portwechselType.getAuftragsposition().addAll(auftragsPositionen(input));
        portwechselType.setVertragsnummer(vertragsNummer(input));
        portwechselType.setAnlagen(anlagen(input));
        portwechselType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setSERPOW(portwechselType);

        return gf;
    }

    private PortwechselTermineType termin(Geschaeftsfall geschaeftsfall) {
        PortwechselTermineType termin = new PortwechselTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionPortwechselType> auftragsPositionen(Geschaeftsfall
            geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionPortwechselType auftragsPosition = new AuftragspositionPortwechselType();

        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionPortwechselType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionPortwechselTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProdukt(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALPortwechselType geschaeftsfallProdukt(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException(
                    "Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALPortwechselType result = new TALPortwechselType();

        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortB(standortKollokation(input));
        result.setSchaltangaben(schaltAngaben(input));
        result.setBestandsvalidierung2(leitungsbezeichnung2(auftragsposition));
        result.setMontageleistung(montageleistung(input));
        return result;
    }

    private PortwechselAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        PortwechselAnlagenType anlagenType = new PortwechselAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            anlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
            set = true;
        }
        return (set) ? anlagenType : null;
    }
}

