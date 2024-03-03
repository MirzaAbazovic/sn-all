/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 16:01:28
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.VoIPService;


/**
 * Command-Klasse, um VoIP-Daten zu ueberpruefen. <br> Diese Klasse prueft sowohl Daten fuer die neue Realisierung von
 * VoIP, als auch die Kuendigung einer solchen Option.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckVoIPCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckVoIPCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckVoIPCommand.class);

    @Autowired
    private VoIPService voipService = null;
    @Autowired
    private CCLeistungsService ccLeistungsService = null;

    @Override
    public Object execute() throws Exception {
        try {
            AuftragVoIP avoip = voipService.findVoIP4Auftrag(getAuftragId());

            if (hasVoIP()) {
                // pruefen, ob aktive VoIP-Daten vorhanden
                if ((avoip == null) || !BooleanTools.nullToFalse(avoip.getIsActive())) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "Der Auftrag besitzt keine aktiven VoIP-Daten!", getClass());
                }

                // Pruefe Rufnummern-Passwoerter
                checkDN();

                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
            }
            else {
                // VoIP kuendigen
                if ((avoip != null) && BooleanTools.nullToFalse(avoip.getIsActive())) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "Der Auftrag besitzt noch aktive VoIP-Daten, aber keine VoIP-Leistung!", getClass());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der VoIP-Daten ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /* Prueft, ob der Auftrag eine aktive VoIP-Leistung besitzt. */
    private boolean hasVoIP() throws FindException {
        try {
            List<TechLeistung> ls = ccLeistungsService.findTechLeistungen4Auftrag(getAuftragId(),
                    TechLeistung.TYP_VOIP, true);

            return CollectionTools.isNotEmpty(ls);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * Prueft, dass allen Voip-Rufnummern <ul> <li>ein Passwort zugeordnet ist <li>die default SIP Domäne zugeordnet
     * ist, falls vorhanden </ul>
     */
    void checkDN() throws StoreException, FindException {
        try {
            //Pruefe alle Rufnummern des Auftrags
            List<Rufnummer> rns = getRufnummern();
            if (CollectionTools.isEmpty(rns)) {
                return;
            }

            for (Rufnummer rn : rns) {
                if (rn != null) {
                    // Falls kein AuftragVoipDn-Objekt existiert, muss dieses
                    // angelegt werden, falls ein AuftragVoipDn-Objekt bereits
                    // existiert, eventuell die Default SIP Domaene ermitteln
                    // und zuordnen
                    AuftragVoIPDN auftragVoipDn = voipService.createVoIPDN4Auftrag(getAuftragId(),
                            rn.getDnNoOrig());
                    // Check Objekt erstellt und SIP-Passwort vorhanden
                    if (auftragVoipDn == null) {
                        throw new FindException(String.format(
                                "Dem Auftrag %d konnte keine Rufnummer %s%s zugeordnet werden!", getAuftragId(),
                                rn.getOnKz(), rn.getDnBase()));
                    }
                    else if (StringUtils.isEmpty(auftragVoipDn.getSipPassword())) {
                        throw new FindException(String.format("Der Rufnummer %s%s ist kein Passwort zugeordnet!",
                                rn.getOnKz(), rn.getDnBase()));
                    }
                }
            }
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Anlegen der Auftrag zu Rufnummer Zuordnung: " + e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Die Zuordnung/Prüfung der Rufnummer ist fehlgeschlagen!", e);
        }
    }

}


