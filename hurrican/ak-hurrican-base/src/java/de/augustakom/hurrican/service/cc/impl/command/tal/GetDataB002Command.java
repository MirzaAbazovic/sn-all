/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 13:52:47
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Gibt eine Liste mit den Elmenten des Segments B002 zurueck - Netzbetreiberdaten
 *
 */
public class GetDataB002Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB002Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            CBVorgang cbVorgang = getCBVorgang();

            String segmentname = TALSegment.SEGMENT_NAME_B002;
            String netzbetreiber = cbVorgang.getCarrierKennungAbs();
            String datumKundeAuftrag = DateTools.formatDate(new Date(), DATE_FORMAT_DTAG); // Erstellungsdatum
            String gewAusfTermin = DateTools.formatDate(cbVorgang.getVorgabeMnet(), DATE_FORMAT_DTAG);
            String uebWeGeb = "N";   //Wird aktuell nicht mehr übernommen

            TALSegment segment = new TALSegment();
            segment.setSegmentName(segmentname);
            verifyMandatory(netzbetreiber, segment, "B002_2: Netzbetreiberkennung ist nicht gesetzt");
            verifyMandatory(datumKundeAuftrag, segment, "B002_3: Auftragsdatum (Kunde) ist nicht gesetzt");
            verifyMandatory(gewAusfTermin, segment, "B002_4: Gewünschter Ausführungstermin ist nicht gesetzt");
            segment.addValue(uebWeGeb);

            List<TALSegment> result = new ArrayList<TALSegment>();
            result.add(segment);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Segmentermittlung der Carrierbestellung(B002): " + e.getMessage(), e);
        }
    }
}


