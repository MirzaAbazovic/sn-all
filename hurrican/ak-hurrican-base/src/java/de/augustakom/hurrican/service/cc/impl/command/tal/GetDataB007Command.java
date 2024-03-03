/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2007 11:04:06
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B007 "Lage TAL".
 *
 */
public class GetDataB007Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB007Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            CBVorgang cbVorgang = getCBVorgang();

            CarrierService cService = getCCService(CarrierService.class);
            Carrierbestellung cBestellung = cService.findCB(cbVorgang.getCbId());
            Endstelle endstelle = getEndstelle(cbVorgang.getAuftragId(), cBestellung.getCb2EsId());
            if (endstelle == null) {
                throw new HurricanServiceCommandException("Endstelle konnte nicht ermittelt werden!");
            }

            EndstellenService esSrv = getCCService(EndstellenService.class);
            EndstelleAnsprechpartner esAnsp = esSrv.findESAnsp4ES(endstelle.getId());

            List<TALSegment> result = new ArrayList<TALSegment>();
            TALSegment segment = new TALSegment();
            String lage = null;

            AddressModel adresseStandort = getAPAddress(endstelle, cbVorgang.getAuftragId());
            if (adresseStandort != null) {
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                adresseStandort = availabilityService.getDtagAddressForCb(endstelle.getGeoId(), adresseStandort);
            }

            if (NumberTools.equal(cbVorgang.getUsecaseId(), CBUsecase.CBUSECASE_ID_TALM)) {
                // einziger USECASE mit Type TALM in dem das Segment B007 vorkommt. Text so von Dtag gefordert.
                // In der ITEX-Sepzifikation falsch mit TALN angegeben
                lage = "Wie bisher";
            }
            else {
                if (StringUtils.isNotBlank(adresseStandort.getStrasseAdd())) {
                    lage = adresseStandort.getStrasseAdd();
                }
                else {
                    if (adresseStandort instanceof Adresse) {
                        lage = adresseStandort.getCombinedStreetData();
                    }
                    else {
                        lage = endstelle.getEndstelle();
                    }
                }

                if ((esAnsp != null) && StringUtils.isNotBlank(esAnsp.getAnsprechpartner())) {
                    lage += " - Rückrufnummer Endkunde: " + esAnsp.getAnsprechpartner();
                }
            }
            segment.setSegmentName(TALSegment.SEGMENT_NAME_B007);
            verifyMandatory(StringTools.replaceChars(lage, invalidITEXChars), segment, "B007_2: Lage der TAE ist nicht gesetzt");

            result.add(segment);
            if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B007)) {
                return result;
            }
            else {
                throw new HurricanServiceCommandException(
                        "Anzahl des Segmentes B007 stimmt nicht mit der Vorgabe überein!");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim ermitteln von Segment B007: " + e.getMessage(), e);
        }
    }

}


