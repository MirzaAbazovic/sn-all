/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2007 13:43:04
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
 * Erstellt den Datensatz fuer Geschaeftsfall B003 RUFNUMMERNDATEN
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataB003Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB003Command.class);

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
            List<Rufnummer> rufnummern = rnService.findByParam(
                    Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                    new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE, Boolean.TRUE });

            String segmentname = TALSegment.SEGMENT_NAME_B003;
            String onkz = null;
            Integer rufnummer = null;
            String abfragestelle = null;
            String blockVon = null;
            String blockBis = null;
            List<Rufnummer> dnFilter = new ArrayList<Rufnummer>();
            for (Rufnummer o : rufnummern) {
                String lastCarrier = StringUtils.trimToEmpty(o.getLastCarrier());
                String futureCarrier = StringUtils.trimToEmpty(o.getFutureCarrier());
                if (StringUtils.equalsIgnoreCase(lastCarrier, Rufnummer.LAST_CARRIER_DTAG) ||
                        StringUtils.equalsIgnoreCase(futureCarrier, Rufnummer.FUTURE_CARRIER_DTAG)) {
                    dnFilter.add(o);
                }
            }

            if (checkSegmentAnzahl(dnFilter.size(), TALSegment.SEGMENT_NAME_B003)) {
                List<TALSegment> result = new ArrayList<TALSegment>();
                for (Rufnummer objRufnummer : dnFilter) {
                    onkz = objRufnummer.getOnKz(); // mit führender 0
                    rufnummer = Integer.valueOf(objRufnummer.getDnBase());
                    abfragestelle = objRufnummer.getDirectDial();
                    blockVon = objRufnummer.getRangeFrom();
                    blockBis = objRufnummer.getRangeTo();

                    TALSegment segment = new TALSegment();
                    segment.setSegmentName(segmentname);
                    verifyMandatory(onkz, segment, "B003_2: ONKZ ist nicht gesetzt");
                    verifyMandatory(rufnummer, segment, "B003_3: Rufnummer ist nicht gesetzt");
                    segment.addValue((abfragestelle == null) ? null : StringUtils.trimToNull(abfragestelle));
                    segment.addValue((blockVon == null) ? null : StringUtils.trimToNull(blockVon));
                    segment.addValue((blockBis == null) ? null : StringUtils.trimToNull(blockBis));
                    result.add(segment);
                }
                return result;
            }
            else {
                throw new HurricanServiceCommandException(
                        "Fehler bei der Segmentermittlung B003 Rufnummerndaten. Die Anzahl des Segmentes B003 stimmt nicht.");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Segmentermittlung B003 Rufnumemrndaten: " + e.getMessage(), e);
        }
    }
}


