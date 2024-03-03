/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragspositionBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.BereitstellungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.BereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TALBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.UFAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.VormieterAnschlussType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallNeu;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;

@SuppressWarnings("Duplicates")
class GeschaeftsfallNeuMarshallerV2 extends GeschaeftsfallMarshallerV2<BereitstellungType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallNeu);

        BereitstellungType bereitstellungType = new BereitstellungType();
        bereitstellungType.setAnsprechpartner(ansprechPartner(input));
        bereitstellungType.setTermine(terminNeu(input));
        bereitstellungType.getAuftragsposition().addAll(auftragsPositionenNeu(input));
        bereitstellungType.setAnlagen(anlagen(input));
        bereitstellungType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setNEU(bereitstellungType);

        return gf;
    }

    private Collection<? extends AuftragspositionBereitstellungType> auftragsPositionenNeu(
            Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionBereitstellungType auftragsPosition = OBJECT_FACTORY.createAuftragspositionBereitstellungType();

        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionBereitstellungType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionBereitstellungTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProduktNeu(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALBereitstellungType geschaeftsfallProduktNeu(Auftragsposition auftragsposition) {
        TALBereitstellungType result;
        switch (auftragsposition.getProdukt()) {
            case TAL:
                result = OBJECT_FACTORY.createTALBereitstellungType();
                break;
            default:
                throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }

        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortA(standortKunde(input));
        result.setStandortB(standortKollokation(input));
        result.setSchaltangaben(schaltAngaben(input));
        result.setAnsprechpartnerMontage(ansprechPartnerMontage(input));
        result.setMontagehinweis(montageHinweis(input));
        result.setTerminReservierungsID(montageReservierungsID(input));
        result.setUebertragungsverfahren(getUebertragungsverfahren(input));
        result.setVormieter(vormieter(input));
        result.setVorabstimmungsID(input.getVorabstimmungsId());

        return result;
    }

    private VormieterAnschlussType vormieter(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        Vormieter vormieter = geschaeftsfallProdukt.getVormieter();
        if (vormieter == null) {
            return null;
        }

        VormieterAnschlussType vormieterAnschlussType = new VormieterAnschlussType();

        if (StringUtils.isNotBlank(vormieter.getOnkz())) {
            AnschlussType anschluss = new AnschlussType();
            anschluss.setONKZ(vormieter.getOnkz());
            anschluss.setRufnummer(vormieter.getRufnummer());
            vormieterAnschlussType.setAnschluss(anschluss);
        }

        if (StringUtils.isNotBlank(vormieter.getNachname())) {
            VormieterAnschlussType.Person person = new VormieterAnschlussType.Person();
            person.setNachname(vormieter.getNachname());
            person.setVorname(vormieter.getVorname());
            vormieterAnschlussType.setPerson(person);
        }

        if (StringUtils.isNotBlank(vormieter.getUfaNummer())) {
            UFAType ufa = new UFAType();
            ufa.setUFAnummer(vormieter.getUfaNummer());
            vormieterAnschlussType.setUFA(ufa);
        }

        return vormieterAnschlussType;
    }

    private BereitstellungAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        BereitstellungAnlagenType bereitstellungAnlagenType = new BereitstellungAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            if (Anlagentyp.LAGEPLAN.equals(inputAnlage.getAnlagentyp())) {
                bereitstellungAnlagenType.setLageplan(kuendigungsOrLageplanAnlage(inputAnlage));
                set = true;
            }
            else {
                bereitstellungAnlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
                set = true;
            }
        }
        return (set) ? bereitstellungAnlagenType : null;
    }

}
