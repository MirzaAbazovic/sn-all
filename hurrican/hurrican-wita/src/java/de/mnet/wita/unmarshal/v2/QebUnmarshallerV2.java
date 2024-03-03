/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.util.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypQEBType;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;

@SuppressWarnings("Duplicates")
@Component
public class QebUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypQEBType> {

    @Override
    public QualifizierteEingangsBestaetigung unmarshal(MeldungstypQEBType in) {
        QualifizierteEingangsBestaetigung out = new QualifizierteEingangsBestaetigung();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypQEBType in, QualifizierteEingangsBestaetigung out) {
        MeldungsattributeQEBType inMeldungsattribute = in.getMeldungsattribute();

        out.setExterneAuftragsnummer(inMeldungsattribute.getExterneAuftragsnummer());
        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setKundennummerBesteller(inMeldungsattribute.getKundennummerBesteller());
    }

    private void mapMeldungPositions(MeldungstypQEBType in, QualifizierteEingangsBestaetigung out) {
        List<MeldungstypQEBType.Meldungspositionen.Position> inPositions = in.getMeldungspositionen().getPosition();

        for (MeldungstypQEBType.Meldungspositionen.Position inPosition : inPositions) {
            MeldungsPositionWithAnsprechpartner outPosition = new MeldungsPositionWithAnsprechpartner();
            mapMeldungPosition(inPosition, outPosition);
            out.getMeldungsPositionen().add(outPosition);
        }
    }

    private void mapMeldungPosition(MeldungstypQEBType.Meldungspositionen.Position in, MeldungsPositionWithAnsprechpartner out) {
        super.mapMeldungPosition(in, out);

        MeldungspositionsattributeQEBType inPositionAttribute = in.getPositionsattribute();
        if (inPositionAttribute != null) {
            out.setAnsprechpartnerTelekom(unmarshallAnsprechpartnerTelekom(inPositionAttribute.getAnsprechpartnerTelekom()));
        }
    }
}
