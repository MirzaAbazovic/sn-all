/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALKuendigungType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallKueKd;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;

public class GeschaeftsfallKueKdMarshaller extends GeschaeftsfallMarshaller<KuendigungType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallKueKd);

        KuendigungType kuendigungType = new KuendigungType();
        kuendigungType.setVertragsnummer(vertragsNummer(input));
        kuendigungType.setAnsprechpartner(ansprechPartner(input));
        kuendigungType.setTermine(terminKueKd(input));
        kuendigungType.getAuftragsposition().addAll(auftragsPositionenKueKd(input));
        kuendigungType.setAnlagen(anlagen(input));
        kuendigungType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setKUEKD(kuendigungType);

        return gf;
    }

    private KuendigungTermineType terminKueKd(Geschaeftsfall geschaeftsfall) {
        KuendigungTermineType termin = new KuendigungTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionKuendigungType> auftragsPositionenKueKd(Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionKuendigungType auftragsPosition = new AuftragspositionKuendigungType();

        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionKuendigungType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionKuendigungTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProduktKueKd(input));

        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALKuendigungType geschaeftsfallProduktKueKd(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALKuendigungType result = new TALKuendigungType();

        result.setBestandsvalidierung2(leitungsbezeichnung2(auftragsposition));
        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setVorabstimmungsID(input.getVorabstimmungsId());

        return result;
    }

    private KuendigungAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        KuendigungAnlagenType kuendigungAnlagenType = new KuendigungAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            if (Anlagentyp.KUENDIGUNGSSCHREIBEN.equals(inputAnlage.getAnlagentyp())) {
                kuendigungAnlagenType.setKuendigungsschreiben(kuendigungsOrLageplanAnlage(inputAnlage));
                set = true;
            }
            else {
                kuendigungAnlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
                set = true;
            }
        }
        return (set) ? kuendigungAnlagenType : null;
    }

}
