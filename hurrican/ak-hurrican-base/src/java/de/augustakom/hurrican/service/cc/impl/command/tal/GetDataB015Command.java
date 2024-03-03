/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2007 16:25:25
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B015 (Ansprechpartner) fuer die el TAL-Schnittstelle
 *
 */
public class GetDataB015Command extends AbstractTALDataCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDataB015Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            CBVorgang cbVorgang = getCBVorgang();

            AKUserService userService = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            AKUser user = userService.findById(cbVorgang.getUserId());
            CarrierService cService = getCCService(CarrierService.class);
            String plz = null;
            String ort = null;
            String kundennummer = null;

            Long hvtStdId = cService.findHvtStdId4Cb(cbVorgang.getCbId());
            CarrierKennung ck = cService.findCarrierKennung4Hvt(hvtStdId);
            if (ck == null) {
                throw new HurricanServiceCommandException("Die CarrierKennung konnte nicht ermittelt werden!");
            }
            plz = ck.getPlz();
            ort = ck.getOrt();
            kundennummer = ck.getKundenNr();

            TALSegment segment = new TALSegment();
            List<TALSegment> result = new ArrayList<TALSegment>();

            if (user != null) {
                segment.setSegmentName(TALSegment.SEGMENT_NAME_B015);
                verifyMandatory(user.getName(), segment, "B015_2: Name des Bearbeiters ist nicht gesetzt");
                verifyMandatory(user.getFirstName(), segment, "B015_3: Vorname des Bearbeiters ist nicht gesetzt");
                verifyMandatory(user.getPhone(), segment, "B015_4: Rufnummer des Bearbeiters ist nicht gesetzt");
                segment.addValue((user.getFax() == null) ? null : user.getFax());
                segment.addValue((user.getEmail() == null) ? null : user.getEmail());
                verifyMandatory(kundennummer, segment, "B015_7: Kundennummer DTAG ist nicht gesetzt");
                segment.addValue((plz == null) ? null : plz);
                segment.addValue((ort == null) ? null : ort);

                result.add(segment);
            }

            if (checkSegmentAnzahl(result.size(), TALSegment.SEGMENT_NAME_B015)) {
                return result;
            }
            else {
                throw new HurricanServiceCommandException("Fehler beim ermitteln von Segment B015.");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim ermitteln von Segment B015: " + e.getMessage(), e);
        }
    }
}


