/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import java.util.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypQEBType;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;

@Component
public class QebUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypQEBType> {

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
