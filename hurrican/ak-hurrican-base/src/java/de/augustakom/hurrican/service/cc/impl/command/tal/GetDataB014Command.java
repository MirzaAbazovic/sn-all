/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2007 13:23:00
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.HVTService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B014 UEVT-Standort fuer die el TAL-Schnittstelle
 *
 */
public class GetDataB014Command extends AbstractTALDataCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDataB014Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {

            CBVorgang cbVorgang = getCBVorgang();
            Carrierbestellung cb = getCarrierbestellung();
            Endstelle endstelle = getEndstelle(cbVorgang.getAuftragId(), cb.getCb2EsId());
            List<TALSegment> result = new ArrayList<TALSegment>();

            if (endstelle.getHvtIdStandort() != null) {
                HVTService hvtService = getCCService(HVTService.class);
                HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
                if (hvtStandort != null) {
                    if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_KVZ)
                            || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                        throw new HurricanServiceCommandException(
                                "Elektronische TAL-Bestellung fuer KVZ ist nicht moeglich!");
                    }

                    HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(endstelle.getHvtIdStandort());
                    TALSegment segment = new TALSegment();

                    segment.setSegmentName(TALSegment.SEGMENT_NAME_B014);
                    verifyMandatory(hvtGruppe.getOnkz(), segment, "B014_2: ONKZ ist nicht gesetzt");
                    verifyMandatory(hvtStandort.getAsb(), segment, "B014_3: ASB ist nicht gesetzt");
                    verifyMandatory(hvtGruppe.getPlz(), segment, "B014_4: PLZ des HVT ist nicht gesetzt");
                    verifyMandatory(hvtGruppe.getOrt(), segment, "B014_5: Ort des HVT ist nicht gesetzt");
                    verifyMandatory(hvtGruppe.getStrasse(), segment, "B014_6: Strasse des HVT ist nicht gesetzt");
                    verifyMandatory(hvtGruppe.getHausNr(), segment, "B014_7: Hausnummer des HVT ist nicht gesetzt");

                    result.add(segment);
                }
            }
            else {
                throw new HurricanServiceCommandException("Es konnte kein HVT-Standort zur Endstelle ermittelt werden(Segment B014)");
            }

            if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B014)) {
                return result;
            }
            else {
                throw new HurricanServiceCommandException("Fehler beim erstellen von Segment B014.\n" +
                        "Die geforderte Anzahl stimmt nicht überein");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler beim erstellen des Segments B014 (UEVT-Standort): " + e.getMessage(), e);
        }
    }
}


