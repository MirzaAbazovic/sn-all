/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2007 10:09:15
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALBestellung;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.model.exmodules.tal.TALVorfall;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.exmodules.tal.TALService;
import de.augustakom.hurrican.service.exmodules.tal.utils.TALServiceFinder;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt den Datensatz fuer Geschaeftsfall B001 AUFTRAGSDATEN
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataB001Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB001Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            Long usecaseId = getPreparedValue(KEY_CBUSECASE_ID,
                    Long.class, false, "Die ID des Geschaeftsfalles wurde nicht gesetzt!");
            CBVorgang cbVorgang = getCBVorgang();

            CarrierElTALService caService = getCCService(CarrierElTALService.class);
            CBUsecase cbUsecase = caService.findCBUsecase(usecaseId);

            TALService talService = (TALService) TALServiceFinder.instance().getTALService(TALService.class);
            TALVorfall vorfall = talService.findById(cbUsecase.getExmTbvId(), TALVorfall.class);

            if (vorfall == null) {
                throw new HurricanServiceCommandException("Es wurde kein passender Vorgang gefunden ");
            }
            CarrierService cService = getCCService(CarrierService.class);
            Carrier carrier = cService.findCarrier(cbVorgang.getCarrierId());

            String meldungstyp = vorfall.getMeldungstyp(); // "ANT", "AUF" usw. Herkunft noch unklar

            List<TALSegment> result = new ArrayList<TALSegment>();
            TALSegment segment = new TALSegment();
            segment.setSegmentName(TALSegment.SEGMENT_NAME_B001);
            verifyMandatory(meldungstyp, segment, "B001_2: Meldungstyp ist nicht gesetzt");
            verifyMandatory(TALBestellung.SENDER_ID_MNET, segment, "B001_3: Netzbetreiber ist nicht gesetzt");
            // Auftragsnummer wird beim Speichern ueber die ID der TAL-Bestellung generiert
            segment.addValue(null);
            segment.addValue((cbVorgang.getBezeichnungMnet() == null) ? null : cbVorgang.getBezeichnungMnet());
            verifyMandatory(vorfall.getTyp(), segment, "B001_6: Geschäftsvorfall ist nicht gesetzt");
            verifyMandatory(carrier.getElTalEmpfId(), segment, "B001_7: Auftragsempfänger ist nicht gesetzt");
            result.add(segment);

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim ertsellen von Segment B001: " + e.getMessage(), e);
        }
    }
}

