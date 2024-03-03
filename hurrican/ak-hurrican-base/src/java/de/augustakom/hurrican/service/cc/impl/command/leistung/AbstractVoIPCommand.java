/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2007 13:37:09
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.VoIPService;


/**
 * Abstrakte Klasse fuer VOIP-Command-Klassen
 *
 *, HaagSi
 */
public abstract class AbstractVoIPCommand extends AbstractLeistungCommand {

    /* Generiert den VoIP- und VoIPDN-Zusatz fuer den Auftrag. */
    protected void createAuftragVoIPDN() throws Exception {
        Long auftId = getAuftragId();

        VoIPService voipService = getCCService(VoIPService.class);
        voipService.createVoIP4Auftrag(auftId, getSessionId());

        CCAuftragService serviceAuft = getCCService(CCAuftragService.class);

        AuftragDaten ad = serviceAuft.findAuftragDatenByAuftragIdTx(auftId);
        Long auftragNoOrig = ad.getAuftragNoOrig();

        if (auftragNoOrig == null) {
            throw new HurricanServiceCommandException(
                    "VoIP-Zusatz fuer den Auftrag konnte nicht generiert werden!");
        }
        else {
            // Liste der Rufnummern ermitteln:
            RufnummerService serviceRn = getBillingService(RufnummerService.class);
            List<Rufnummer> dns = serviceRn.findRNs4Auftrag(auftragNoOrig);

            if (CollectionTools.isEmpty(dns)) {
                throw new HurricanServiceCommandException(
                        "Keine Rufnummern gefunden für Auftrag: " + auftId + "  AuftragNoOrig: " + auftragNoOrig + "!");
            }

            // für jede Rufnummer einen Eintrag in der Rufnummern-Passworte-Tabelle erstellen:
            for (Rufnummer dn : dns) {
                voipService.createVoIPDN4Auftrag(auftId, dn.getDnNoOrig());
            }
        }
    }

    /* Kuendigt die VoIP-Daten aus dem Auftrag. */
    protected void cancelVoIP() throws Exception {
        VoIPService voipSer = getCCService(VoIPService.class);
        AuftragVoIP voip = voipSer.findVoIP4Auftrag(getAuftragId());
        if ((voip != null) && BooleanTools.nullToFalse(voip.getIsActive())) {
            voip.setIsActive(Boolean.FALSE);
            voipSer.saveAuftragVoIP(voip, true, getSessionId());
        }
    }
}
