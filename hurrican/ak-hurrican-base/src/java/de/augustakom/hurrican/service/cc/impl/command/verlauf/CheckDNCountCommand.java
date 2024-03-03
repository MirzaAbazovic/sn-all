/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:05:09
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Command-Klasse, um die Rufnummern zu einem Auftrag zu ueberpruefen. <br> Folgende Checks werden durchgefuehrt: <br>
 * <ul> <li>genau eine Rufnummer als Hauptrufnummer markiert <li>Anzahl Rufnummern uebersteigt nicht die maximal
 * zulaessige Anzahl </ul>
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDNCountCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDNCountCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckDNCountCommand.class);

    private static final String MSG_RESOURCE = "de.augustakom.hurrican.service.cc.resources.BAVerlauf";

    @Override
    public Object execute() throws Exception {
        try {
            if (getProdukt() == null) {
                throw new FindException("Produkt-Konfiguration ist nicht vorhanden!");
            }

            List<Rufnummer> rufnummern = getRufnummern();

            // Anzahl Rufnummern pruefen (ausser Routing etc.)
            checkDnCount(rufnummern);

            int countHauptRN = 0;
            for (Rufnummer rn : rufnummern) {
                if (rn.isMainNumber()) {
                    countHauptRN++;
                }
            }

            // Haupt-Rufnummer pruefen
            if (BooleanTools.nullToFalse(getProdukt().needsDn()) || CollectionTools.isNotEmpty(rufnummern)) {
                if (countHauptRN == 0) {
                    throw new FindException("Dem Auftrag ist keine Hauptrufnummer zugeordnet. " +
                            "Bitte in Taifun nachholen.");
                }
                else if (countHauptRN > 1) {
                    throw new FindException("Diesem Auftrag sind mehrere Hauptrufnummern zugeordnet. " +
                            "Bitte in Taifun korrigieren.");
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
                    "Bei der Ueberpruefung der Rufnummernanzahl ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * Ueberprueft, ob dem aktuellen Auftrag nicht mehr Rufnummern zugeordnet sind, als durch die Konfiguration
     * eigentlich zulaessig sind (-> Exception). Falls zu wendige Rufnummer zugeordnet sind, wird eine Warning erstellt,
     * die nur als Hinweis für den Benutzer dienen soll. Wichtig: es werden nur physikalische Rufnummern berucksichtigt
     * - keine Routings etc. <br> Ablauf: <ul> <li>Rufnummernliste nach den konfigurierten Rufnummern-Typen filtern
     * <li>Anzahl mit maximaler Anzahl aus Produkt-Konfiguration pruefen </ul>
     *
     * @param rufnummern alle Rufnummern, die dem aktuellen Auftrag zugeordnet sind
     * @throws FindException falls zu viele Rufnummern zugeordnet sind
     */
    private void checkDnCount(List<Rufnummer> rufnummern) throws FindException {
        try {
            // Rufnummern-Liste nach OE__NO (=Rufnummerntyp) filtern
            @SuppressWarnings("unchecked")
            Collection<Rufnummer> filteredList = CollectionUtils.select(rufnummern, new Predicate() {
                public boolean evaluate(Object obj) {
                    return NumberTools.equal(getProdukt().getDnTyp(), ((Rufnummer) obj).getOeNoOrig());
                }
            });

            // Anzahl pruefen
            int dnCount = 0;
            if (BooleanTools.nullToFalse(getProdukt().getDnBlock())) {
                // JG / 29.03.2006
                // in Absprache mit EWSD/SCV und ITS wird bei Produkten, die einen
                // Rufnummernblock besitzen wie folgt geprueft:
                //   - alle Rufnummern mit gleicher DN-Base werden als eine Rufnummer angesehen
                Set<String> diffDNBase = new HashSet<String>();
                for (Rufnummer rufnummer : filteredList) {
                    diffDNBase.add(rufnummer.getDnBase());
                }
                dnCount = diffDNBase.size();
            }
            else {
                dnCount = (filteredList != null) ? filteredList.size() : 0;
            }

            if (NumberTools.isGreater(dnCount, getProdukt().getMaxDnCount())) {
                ResourceReader rr = new ResourceReader(MSG_RESOURCE);
                throw new FindException(rr.getValue("max.dn.count.invalid", new Object[] { getProdukt().getDnTyp() }));
            }
            else if (NumberTools.isLess(dnCount, getProdukt().getMinDnCount())) {
                ResourceReader rr = new ResourceReader(MSG_RESOURCE);
                addWarning(this, rr.getValue("min.dn.count.invalid", new Object[] { getProdukt().getDnTyp() }));
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ueberpruefung der min. bzw. max. Rufnummern-Anzahl!", e);
        }
    }
}
