/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2006 08:32:35
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Command, um zu pruefen, ob der Ursprungs-Auftrag ein Buendel-Auftrag ist bzw. einen Buendel-Auftrag besitzt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AssertSrcIsBuendelCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AssertSrcIsBuendelCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(AssertSrcIsBuendelCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    protected void loadRequiredData() throws FindException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten adSrc = getAuftragDatenTx(getAuftragIdSrc());
            if (adSrc.getBuendelNr() == null) {
                throw new FindException("Ursprungs-Auftrag besitzt kein Buendel. " +
                        "Physikaenderung kann nicht durchgefuehrt werden!");
            }
            else {
                List<AuftragDaten> buendelADs =
                        as.findAuftragDaten4BuendelTx(adSrc.getBuendelNr(), AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT);
                if (buendelADs == null || buendelADs.size() < 2) {
                    throw new FindException("Der zugehoerige Buendel-Auftrag vom Ursprungs-Auftrag " +
                            "konnte nicht ermittelt werden!");
                }
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ueberpruefung, ob ein Buendel-Auftrag existiert.", e);
        }
    }

}


