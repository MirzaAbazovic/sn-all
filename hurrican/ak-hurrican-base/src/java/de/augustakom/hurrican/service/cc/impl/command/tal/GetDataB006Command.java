/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.07.2007 11:14:05
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt den Datensatz fuer Geschaeftsfall B006 ZUSATZDATEN PORTIERUNG
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataB006Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB006Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            CBVorgang cbVorgang = getCBVorgang();

            CCAuftragService ccAuftragService = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId());

            RufnummerService rnService = getBillingService(RufnummerService.class);
            List<Rufnummer> rufnummern = rnService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig());

            String segmentname = TALSegment.SEGMENT_NAME_B006;
            Integer portVonInt = null;
            Integer portBisInt = null;
            String portVon = null;
            String portBis = null;
            String portierungsprojekt = "N";
            List<TALSegment> result = new ArrayList<TALSegment>();

            for (Rufnummer rufnummer : rufnummern) {
                if ((rufnummer.getPortierungVon() != null) && (rufnummer.getPortierungBis() != null)) {
                    Integer tmpPortVonInt = Integer.valueOf(
                            DateTools.formatDate(rufnummer.getPortierungVon(), TIME_FORMAT_DTAG));
                    Integer tmpPortBisInt = Integer.valueOf(
                            DateTools.formatDate(rufnummer.getPortierungBis(), TIME_FORMAT_DTAG));

                    if ((portVonInt == null) || NumberTools.isLess(tmpPortVonInt, portVonInt)) {
                        portVon = DateTools.formatDate(rufnummer.getPortierungVon(), TIME_FORMAT_DTAG);
                        portVonInt = tmpPortVonInt;
                    }

                    if ((portBisInt == null) || NumberTools.isLess(tmpPortBisInt, portBisInt)) {
                        portBis = DateTools.formatDate(rufnummer.getPortierungBis(), TIME_FORMAT_DTAG);
                        portBisInt = tmpPortBisInt;
                    }
                }
            }

            TALSegment segment = new TALSegment();
            segment.setSegmentName(segmentname);
            verifyMandatory(portVon + portBis, segment, "B006_2: Portierungfenster ist nicht gesetzt");
            segment.addValue(portierungsprojekt);
            result.add(segment);

            if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B006)) {
                return result;
            }
            else {
                throw new HurricanServiceCommandException(
                        "Fehler bei der Segmentermittlung der Portierungszusatzdaten.\n" +
                                "Die Anzahl des Segmentes B006 stimmt nicht."
                );
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Ein Fehler ist beim ermitteln des" +
                    "Segment B006 Portierungsfenster aufgetreten: " + e.getMessage(), e);
        }
    }
}


