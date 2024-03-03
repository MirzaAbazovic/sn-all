/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2007 16:20:38
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCKundenService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt den Datensatz fuer Geschaeftsfall B004 AUFBAUORT
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataB004Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB004Command.class);

    private static final String ADDRESS_TYPE_ACCESSPOINT = "LA";
    private static final String ADDRESS_TYPE_CONNECTION_OWNER = "CA";

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

            AddressModel adresseStandort = getAPAddress(endstelle, cbVorgang.getAuftragId());
            if (adresseStandort == null) {
                throw new HurricanServiceCommandException("Standortadresse konnte nicht ermittelt werden!");
            }

            if (endstelle != null) {
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                adresseStandort = availabilityService.getDtagAddressForCb(endstelle.getGeoId(), adresseStandort);
            }

            if (NumberTools.isIn(cbVorgang.getUsecaseId(),
                    new Number[] { CBUsecase.CBUSECASE_ID_NTAL, CBUsecase.CBUSECASE_ID_TALN })) {
                // bei TALN + NTAL darf B004 nur einmal uebermittelt werden.
                // In diesem Fall wird ein evtl. 'Name2' in den Vornamen mit eingefuegt.
                TALSegment seg = createSegment(adresseStandort, ADDRESS_TYPE_ACCESSPOINT, 1);
                if (StringUtils.isNotBlank(adresseStandort.getName2()) || StringUtils.isNotBlank(adresseStandort.getVorname2())) {
                    String name2 = StringTools.join(
                            new String[] { adresseStandort.getName2(), adresseStandort.getVorname2() }, " ", true);

                    String vorname4Adr =
                            StringTools.join(new String[] { adresseStandort.getVorname(), name2 }, " und ", true);
                    seg.changeValue(1, vorname4Adr);
                }
                result.add(seg);
            }
            else {
                result.add(createSegment(adresseStandort, ADDRESS_TYPE_ACCESSPOINT, 1));
                if (StringUtils.isNotBlank(adresseStandort.getName2()) || StringUtils.isNotBlank(adresseStandort.getVorname2())) {
                    result.add(createSegment(adresseStandort, ADDRESS_TYPE_ACCESSPOINT, 2));
                }

                // Anschlussinhaberadresse (falls definiert) uebermitteln (als Typ 'CA')
                if (cb.getAiAddressId() != null) {
                    CCKundenService ccKundenService = getCCService(CCKundenService.class);
                    CCAddress adresseAi = ccKundenService.findCCAddress(cb.getAiAddressId());
                    if (adresseAi == null) {
                        throw new HurricanServiceCommandException("Anschlussinhaberadresse konnte nicht ermittelt werden!");
                    }

                    result.add(createSegment(adresseAi, ADDRESS_TYPE_CONNECTION_OWNER, 1));
                    if (StringUtils.isNotBlank(adresseAi.getName2()) || StringUtils.isNotBlank(adresseAi.getVorname2())) {
                        result.add(createSegment(adresseAi, ADDRESS_TYPE_CONNECTION_OWNER, 2));
                    }
                }
            }

            if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B004)) {
                return result;
            }
            else {
                throw new HurricanServiceCommandException(
                        "Anzahl des Segmentes B004 stimmt nicht mit der Vorgabe überein!");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Segmentermittlung B004 Adressdaten: " + e.getMessage(), e);
        }
    }

    /**
     * Erstelle die Einzelsegmente fuer das Segment B004
     *
     * @param endstelle
     * @param adresstyp Angabe, ob es sich um die 1. oder 2. Adresse handelt (falls Name2/Vorname2 angegeben).
     * @return
     * @throws HurricanServiceCommandException
     *
     */
    private TALSegment createSegment(AddressModel address, String addressTyp, int addressNr) throws HurricanServiceCommandException {
        if (address == null) {
            throw new HurricanServiceCommandException("Adress-Objekt ist nicht angegeben.");
        }

        TALSegment segment = new TALSegment();
        segment.setSegmentName(TALSegment.SEGMENT_NAME_B004);
        if (addressNr == 1) {
            verifyMandatory(address.getName(), segment, "B004_2: Name des Kunden/AI ist nicht gesetzt");
            segment.addValue(address.getVorname());
        }
        else {
            verifyMandatory(address.getName2(), segment, "B004_2: Name 2 des Kunden/AI ist nicht gesetzt");
            segment.addValue(address.getVorname2());
        }
        verifyMandatory(address.getStrasse(), segment, "B004_4: Strasse des Kunden/AI ist nicht gesetzt");

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(address.getNummer())) {
            sb.append(address.getNummer());
        }
        if (StringUtils.isNotBlank(address.getHausnummerZusatz())) {
            sb.append(" ");
            sb.append(address.getHausnummerZusatz());
        }
        String hnTmp = sb.toString();
        segment.addValue((StringUtils.isBlank(hnTmp)) ? null : hnTmp);
        verifyMandatory(address.getPlz(), segment, "B004_6: PLZ des Kunden/AI ist nicht gesetzt");
        verifyMandatory(address.getOrt(), segment, "B004_7: Ort des Kunden/AI ist nicht gesetzt");
        verifyMandatory(addressTyp, segment, "B004_8: Adresstyp ist nicht gesetzt");
        segment.addValue("DE");

        return segment;
    }
}


