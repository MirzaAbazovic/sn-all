/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:10:24
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Command-Klasse prueft, ob die Portierungsdaten (abgehend) in den Rufnummern des Auftrags gesetzt sind. <br>
 * Portierungsdaten muessen in folgenden Faellen gesetzt sein: <ul> <li>Rufnummer ist dem Auftrag nicht mehr zugeordnet
 * (ORDER__NO der Rufnummer ist leer) <li>BLOCK__NO der Rufnummer ist leer oder kein Block von 'AKOM' </ul> <br><br>
 * Portierung abgehend darf in folgenden Faellen nicht(!) gesetzt sein: <br> <ul> <li>Rufnummerntyp ist '68'
 * (Appartement) <li>Rufnummer ist PreSelect (wenn Port-Mode ist 'null' und Last-Carrier ist 'null') <li>Rufnummer mit
 * HistLast=1 ist weiterhin einem Auftrag zugeordnet (= ORDER__NO gefuellt) </ul> <br><br> Die Portierung abgehend ist
 * dann korrekt eingetragen, wenn in der Rufnummer der Port-Mode auf 'PORTIERUNG_A' und das Real-Date nicht 'null' ist.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDNPortierungAbCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDNPortierungAbCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckDNPortierungAbCommand.class);

    private static final String MSG_RESOURCE = "de.augustakom.hurrican.service.cc.resources.BAVerlauf";

    @Override
    public Object execute() throws Exception {
        try {
            if (NumberTools.notEqual(getBillingAuftrag().getAstatus(), BAuftrag.STATUS_STORNIERT)) {
                RufnummerService rs = getBillingService(RufnummerService.class);
                //List<Rufnummer> rufnummern = rs.findRNs4Auftrag(getBillingAuftrag().getAuftragNoOrig());
                List<Rufnummer> rufnummern = rs.findByParam(
                        Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { getBillingAuftrag().getAuftragNoOrig(), Boolean.FALSE });

                if (BooleanTools.nullToFalse(getProdukt().needsDn()) && CollectionTools.isEmpty(rufnummern)) {
                    throw new FindException("Es konnten keine Rufnummern zu dem Auftrag ermittelt werden!");
                }

                List<Rufnummer> portDataMissing = new ArrayList<>();
                List<Rufnummer> portingNotAllowed = new ArrayList<>();
                for (Rufnummer rufnummer : rufnummern) {
                    // letzten/aktuellen Datensatz der Rufnummer laden
                    Rufnummer lastRN = rs.findLastRN(rufnummer.getDnNoOrig());
                    if (lastRN == null) {
                        throw new FindException("Aktuellster Datensatz der Rufnummer " + rufnummer.getRufnummer() +
                                " konnte nicht ermittelt werden!");
                    }

                    if (rufnummer.isAppartement() || rufnummer.isPreSelect()) {
                        // Rufnummer darf nicht portiert werden
                        if (lastRN.isPortierungAbgehend()) {
                            portingNotAllowed.add(lastRN);
                        }
                    }
                    else {
                        // pruefen, ob Rufnummer KEINEM AKom-Block angehoert
                        if (!rs.isMnetBlock(rufnummer.getBlockNoOrig())) {
                            if (lastRN.getAuftragNoOrig() == null) {
                                // Rufnummer muss portiert werden
                                if (!lastRN.isPortierungAbgehend() || (lastRN.getRealDate() == null)) {
                                    portDataMissing.add(lastRN);
                                }
                            }
                            else {
                                // Rufnummer darf nicht(!) portiert werden
                                if (lastRN.isPortierungAbgehend()) {
                                    portingNotAllowed.add(lastRN);
                                }
                            }
                        }
                    }
                }

                // Exception, falls Portierungsdaten (abgehend) fehlen oder ungueltig gesetzt
                if ((!portDataMissing.isEmpty()) || (!portingNotAllowed.isEmpty())) {
                    StringBuilder dns = new StringBuilder();
                    addRufnummern(portDataMissing, dns);
                    addRufnummern(portingNotAllowed, dns);

                    ResourceReader rr = new ResourceReader(MSG_RESOURCE);
                    String msg = rr.getValue("port.data.missing", new Object[] { dns.toString() });
                    throw new FindException(msg);
                }
            }
        }
        catch (FindException e) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der Portierungsdaten (abgehend) ist ein Fehler aufgetreten: " +
                            e.getMessage(), getClass()
            );
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /* Fuegt die Rufnummern der Liste dem StringBuilder hinzu. */
    private void addRufnummern(List<Rufnummer> rufnummern, StringBuilder sb) {
        if ((rufnummern != null) && (sb != null)) {
            for (Rufnummer r : rufnummern) {
                sb.append(r.getRufnummer());
                sb.append(SystemUtils.LINE_SEPARATOR);
            }
        }
    }

}


