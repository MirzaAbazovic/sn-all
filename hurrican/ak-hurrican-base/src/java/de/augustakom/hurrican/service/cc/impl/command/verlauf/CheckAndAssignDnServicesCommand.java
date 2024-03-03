/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 21.06.2010 11:43:53
  */

package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndAssignDnServicesCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckAndAssignDnServicesCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckAndAssignDnServicesCommand.class);

    protected Produkt produkt;
    private AuftragDaten auftragDaten;
    private Long auftragId;
    protected Date realTermin;
    private boolean isKuendigung;

    protected List<Rufnummer> rufnummern;

    private RufnummerService rufnummerService;
    private CCRufnummernService ccRufnummernService;

    @Override
    public Object execute() throws Exception {
        try {
            init();
            if (!isKuendigung) {
                attachDNDefaults();
                verifyDNPresence();
            }
        }
        catch (FindException ex) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    ex.getMessage(), getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der Rufnummerleistungen ist ein Fehler aufgetreten: "
                            + ExceptionUtils.getFullStackTrace(e), getClass()
            );
        }
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    protected void verifyDNPresence() throws FindException, StoreException {
        if (produkt.isDnAllowed()) {
            List<Leistung4Dn> missing = ccRufnummernService.findMissingDnLeistungen(auftragId);
            if (!missing.isEmpty()) {
                List<Leistung4Dn> withParameter = ccRufnummernService.findDNLeistungenWithParameter(missing);
                List<Leistung4Dn> withoutParameter = ccRufnummernService.findDNLeistungenWithoutParameter(missing);

                Leistungsbuendel leistungsbuendel = ccRufnummernService.findLeistungsbuendel4Auftrag(auftragId);
                if (leistungsbuendel == null) {
                    throw new StoreException("DN-Leistungsbuendel konnte nicht ermittelt werden!");
                }

                for (Leistung4Dn leistung4Dn : withoutParameter) {
                    // Add the Leistungen without Parameter to the Hurrican
                    for (Rufnummer rufnummer : this.rufnummern) {
                        ccRufnummernService.saveLeistung2DN(rufnummer, leistung4Dn.getId(), null, false,
                                leistungsbuendel.getId(), this.realTermin, null, getSessionId());
                    }
                }

                if (!withParameter.isEmpty()) {
                    StringBuilder msg = new StringBuilder(
                            "Folgende Rufnummernleistungen wurden in Taifun aber nicht in Hurrican konfiguriert und benötigen einen Parameter: ");
                    for (Leistung4Dn leistung4Dn : withParameter) {
                        msg.append("\n").append(leistung4Dn.getLeistung());
                    }
                    throw new FindException(msg.toString());
                }
            }
        }
    }

    /**
     * @param auftragNoOrig
     * @return
     * @throws FindException
     */

    private void init() throws FindException, ServiceNotFoundException {
        produkt = getProdukt();
        auftragId = getAuftragId();
        auftragDaten = getAuftragDatenTx(auftragId);
        isKuendigung = NumberTools.equal(getAnlassId(), BAVerlaufAnlass.KUENDIGUNG);
        realTermin = getRealDate();
        if (produkt.isDnAllowed()) {
            rufnummerService = getBillingService(RufnummerService.class);
            ccRufnummernService = getCCService(CCRufnummernService.class);
            rufnummern = rufnummerService.findRNs4Auftrag(auftragDaten.getAuftragNoOrig());
        }
    }

    /**
     * Ordnet allen Rufnummern des aktuellen Auftrags die Default-Leistungen hinzu, sofern zu der Rufnummer noch keine
     * Leistung eingetragen ist.
     *
     * @throws StoreException
     */
    private void attachDNDefaults() throws StoreException {
        try {
            if (produkt.isDnAllowed()) {
                if (CollectionTools.isNotEmpty(rufnummern)) {
                    for (Rufnummer rn : rufnummern) {
                        ccRufnummernService.attachDefaultLeistungen2DN(rn, auftragId, realTermin, getSessionId());
                    }
                }
                else {
                    if (BooleanTools.nullToFalse(produkt.needsDn())) {
                        throw new FindException("Rufnummern konnten nicht ermittelt werden!");
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Zuordnung der Rufnummern-Leistungen!", e);
        }
    }
}
