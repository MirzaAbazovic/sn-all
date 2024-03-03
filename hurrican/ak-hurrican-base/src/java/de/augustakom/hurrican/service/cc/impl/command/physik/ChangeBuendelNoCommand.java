/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 08:35:30
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Command-Klasse, um die Buendel-No des 'neuen' Auftrags den Child-Auftraegen des 'alten' Auftrags zu uebergeben.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.ChangeBuendelNoCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChangeBuendelNoCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(ChangeBuendelNoCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        changeBuendelNr(getAuftragDatenTx(getAuftragIdSrc()), getAuftragDatenTx(getAuftragIdDest()));
        return null;
    }

    /*
     * Aktualisiert die Buendel-Nr der zum 'alten' Auftrag gehoerenden Buendel-Auftraege.
     * (Bei Bandbreitenaenderung wird die neue Buendel-Nr den S0-Auftraegen uebergeben.)
     */
    private void changeBuendelNr(AuftragDaten adSrc, AuftragDaten adDest) throws StoreException {
        try {
            if (adSrc.getBuendelNr() != null && adSrc.getBuendelNr().intValue() > 0) {
                CCAuftragService as = (CCAuftragService) getCCService(CCAuftragService.class);
                List<AuftragDaten> ads = as.findAuftragDaten4BuendelTx(
                        adSrc.getBuendelNr(), adSrc.getBuendelNrHerkunft());
                if (ads != null) {
                    for (AuftragDaten ad : ads) {
                        if (!NumberTools.equal(adSrc.getAuftragId(), ad.getAuftragId())) {
                            ad.setBuendelNr(adDest.getBuendelNr());
                            ad.setBuendelNrHerkunft(adDest.getBuendelNrHerkunft());
                            as.saveAuftragDaten(ad, true);
                        }
                    }
                }
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

}


