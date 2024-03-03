/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import java.util.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionRnrExportMitKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ErweiterteBestandssucheType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungAnlagenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALRnrExportMitKuendigungType;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallRexMk;
import de.mnet.wita.message.common.Anlage;

public class GeschaeftsfallRexMkMarshaller extends GeschaeftsfallMarshaller<RnrExportMitKuendigungType> {

    @Override
    public de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall generate(Geschaeftsfall input) {
        Preconditions.checkArgument(input instanceof GeschaeftsfallRexMk);

        RnrExportMitKuendigungType rnrExportMitKuendigungType = new RnrExportMitKuendigungType();
        rnrExportMitKuendigungType.setAnsprechpartner(ansprechPartner(input));
        rnrExportMitKuendigungType.setTermine(termin(input));
        rnrExportMitKuendigungType.getAuftragsposition().addAll(auftragsPositionen(input));
        rnrExportMitKuendigungType.setAnlagen(anlagen(input));
        rnrExportMitKuendigungType.setBKTOFaktura(input.getBktoFatkura());

        de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall gf = OBJECT_FACTORY.createGeschaeftsfall();
        gf.setREXMK(rnrExportMitKuendigungType);

        return gf;
    }

    private RnrExportMitKuendigungTermineType termin(Geschaeftsfall geschaeftsfall) {
        RnrExportMitKuendigungTermineType termin = new RnrExportMitKuendigungTermineType();
        termin.setKundenwunschtermin(createKundenwunschtermin(geschaeftsfall));
        return termin;
    }

    private Collection<? extends AuftragspositionRnrExportMitKuendigungType> auftragsPositionen(
            Geschaeftsfall geschaeftsfall) {
        Auftragsposition input = geschaeftsfall.getAuftragsPosition();
        AuftragspositionRnrExportMitKuendigungType auftragsPosition = new AuftragspositionRnrExportMitKuendigungType();

        auftragsPosition.setProdukt(produkt(input));

        AuftragspositionRnrExportMitKuendigungType.GeschaeftsfallProdukt geschaeftsfallProdukt = OBJECT_FACTORY.createAuftragspositionRnrExportMitKuendigungTypeGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setTAL(geschaeftsfallProdukt(input));
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        return Collections.singletonList(auftragsPosition);
    }

    private TALRnrExportMitKuendigungType geschaeftsfallProdukt(Auftragsposition auftragsposition) {
        if (auftragsposition.getProdukt() != Produkt.TAL) {
            throw new RuntimeException("Unknown value for field geschaeftsfallProdukt or not supported yet");
        }
        TALRnrExportMitKuendigungType result = new TALRnrExportMitKuendigungType();

        GeschaeftsfallProdukt input = auftragsposition.getGeschaeftsfallProdukt();
        result.setStandortA(standortKundeOhneLage(input));
        BestandsSuche inputBestandSuche = input.getBestandsSuche();
        if (inputBestandSuche != null) {
            ErweiterteBestandssucheType bestandSuche = new ErweiterteBestandssucheType();

            if (StringUtils.isNotEmpty(inputBestandSuche.getOnkz())) {
                OnkzRufNrType einzelAnschluss = new OnkzRufNrType();
                einzelAnschluss.setONKZ(inputBestandSuche.getOnkz());
                einzelAnschluss.setRufnummer(inputBestandSuche.getRufnummer());
                bestandSuche.setEinzelanschluss(einzelAnschluss);
            }
            else {
                OnkzDurchwahlAbfragestelleType anlagenAnschluss = new OnkzDurchwahlAbfragestelleType();
                anlagenAnschluss.setONKZ(inputBestandSuche.getAnlagenOnkz());
                anlagenAnschluss.setDurchwahlnummer(inputBestandSuche.getAnlagenDurchwahl());
                anlagenAnschluss.setAbfragestelle(inputBestandSuche.getAnlagenAbfrageStelle());
                bestandSuche.setAnlagenanschluss(anlagenAnschluss);
            }
            result.setBestandssuche(bestandSuche);
        }

        result.setRufnummernPortierung(portierung(input));
        return result;
    }

    private RnrExportMitKuendigungAnlagenType anlagen(Geschaeftsfall input) {
        boolean set = false;
        RnrExportMitKuendigungAnlagenType anlagenType = new RnrExportMitKuendigungAnlagenType();
        for (Anlage inputAnlage : input.getAnlagen()) {
            anlagenType.getSonstige().add(sonstigeAnlage(inputAnlage));
            set = true;
        }
        return (set) ? anlagenType : null;
    }

}

