/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:26:46
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.ProduktDtag;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.ESAATalOrderService;
import de.augustakom.hurrican.service.cc.RangierungsService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B009 VERTRAGSDATEN
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataB009Command extends AbstractTALDataCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDataB009Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {

            String schaltUevt1 = null;
            String schaltUevt2 = null;

            CBVorgang cbVorgang = getCBVorgang();
            Carrierbestellung cb = getCarrierbestellung();

            Endstelle endstelle = getEndstelle(cbVorgang.getAuftragId(), cb.getCb2EsId());
            TALSegment segment = new TALSegment();
            List<TALSegment> result = new ArrayList<TALSegment>();

            if (endstelle != null) {
                if (endstelle.getRangierId() != null) {

                    RangierungsService rangierungS = getCCService(RangierungsService.class);
                    Rangierung rangierung = rangierungS.findRangierung(endstelle.getRangierId());
                    if (rangierung != null) {
                        Equipment equipment = rangierungS.findEquipment(rangierung.getEqOutId());
                        if (equipment != null) {
                            schaltUevt1 = equipment.getEinbau1DTAG();

                            ProduktDtag pDtag = new ProduktDtag();
                            pDtag.setRangSsType(equipment.getRangSSType());

                            ESAATalOrderService esaaTalOrderService = getCCService(ESAATalOrderService.class);
                            List<ProduktDtag> pDtagListe = esaaTalOrderService.getProduktDtag(pDtag);
                            if ((pDtagListe != null) && (pDtagListe.size() == 1)) {
                                pDtag = pDtagListe.get(0);
                            }
                            else {
                                throw new HurricanServiceCommandException("Es konnte kein DTAG-Produkt ermittelt werden(Segment B009)");
                            }

                            if (equipment.getRangLeiste2() != null) {
                                schaltUevt2 = equipment.getEinbau2DTAG();
                            }
                            segment.setSegmentName(TALSegment.SEGMENT_NAME_B009);

                            String vtrNr = getVerifiedVtrNr(cb.getVtrNr());
                            verifyMandatory(vtrNr, segment, "B009_2: Vertragsnummer ist nicht gesetzt");
                            verifyMandatory(cb.getLbz(), segment, "B009_3: Leitungsbezeichnung ist nicht gesetzt");
                            verifyMandatory(pDtag.getProduktDtag(), segment, "B009_4: Produkt (DTAG) ist nicht gesetzt");
                            verifyMandatory(schaltUevt1, segment, "B009_5: Schaltungsangaben sind nicht gesetzt");
                            segment.addValue((schaltUevt2 == null) ? null : schaltUevt2);
                            segment.addValue(null);
                            segment.addValue(null);
                            segment.addValue(null);
                            segment.addValue(null);
                            verifyMandatory("N", segment, "B009_11");
                            segment.addValue((cb.getLl() == null) ? null : cb.getLl());
                            segment.addValue((cb.getAqs() == null) ? null : cb.getAqs());
                            result.add(segment);
                        }
                        else {
                            throw new HurricanServiceCommandException("Es konnte kein Equipment ermittelt werden(Segment B009)");
                        }
                    }
                    else {
                        throw new HurricanServiceCommandException("Es konnte keine Rangierung zur RangierId gefunden werden(Segment B009)");
                    }
                }
                else {
                    throw new HurricanServiceCommandException("Es konnte keine Rangierung ermittelt werden(Segment B009)");
                }
            }
            else {
                throw new HurricanServiceCommandException("Es konnte keine Endstelle ermittelt werden(Segment B009)");
            }
            if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B009)) {
                return result;
            }
            else {
                throw new HurricanServiceCommandException("Fehler beim ertsellen des Segment B009" +
                        "Die geforderte Anzahl stimmt nicht überein");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim erstellen des Segmentes B009: " + e.getMessage());
        }
    }

}


