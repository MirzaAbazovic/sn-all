/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 14:58:10
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class ErledigtMeldungKundeBuilder extends
        MessageBuilder<ErledigtMeldungKunde, ErledigtMeldungKundeBuilder, MeldungsPosition> {

    public LocalDate erledigungstermin = LocalDate.now();

    @Override
    public ErledigtMeldungKunde build() {
        ErledigtMeldungKunde result = new ErledigtMeldungKunde(vertragsnummer, externeAuftragsnummer, kundennummer,
                kundennummerBesteller, geschaeftsfallTyp, aenderungsKennzeichen);
        addCommonFields(result);
        return result;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("0015",
                "Endkunde hat die Bereitstellung der Leistung bestätigt. Anschluss störungsfrei in Betrieb");
    }

}
