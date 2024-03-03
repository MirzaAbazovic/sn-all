/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:16:28
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;

import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt den Datensatz fuer Geschaeftsfall B008 ANSCHLUSSKUENDIGUNG
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataB008Command extends AbstractTALDataCommand {

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        TALSegment segment = new TALSegment();
        List<TALSegment> result = new ArrayList<TALSegment>();

        segment.setSegmentName(TALSegment.SEGMENT_NAME_B008);
        verifyMandatory("J", segment, "B008_2");
        segment.addValue(null);// NTBA-Uebernahme - in Version 3.00 nicht realisiert - in Tab-Struktur MNETCALL aber vorgesehen
        segment.addValue(null);// CLS-Übernahem - in Version 3.00 nicht realisiert - in TAB-Struktur MNETCALL aber vorgesehen
        result.add(segment);
        if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B008)) {
            return result;
        }
        else {
            throw new HurricanServiceCommandException(
                    "Anzahl des Segmentes B008 stimmt nicht mit der Vorgabe überein!");
        }
    }
}


