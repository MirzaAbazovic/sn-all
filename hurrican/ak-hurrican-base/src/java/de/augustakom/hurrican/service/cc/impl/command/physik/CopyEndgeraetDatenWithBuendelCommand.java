/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 13:06:39
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 *
 * @see CopyEndgeraetDatenCommand In dieser Command-Klasse werden auch die Endgeraete des Ursprungs-Buendel-Auftrags auf
 * den Ziel-Auftrag kopiert.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.CopyEndgeraetDatenWithBuendelCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CopyEndgeraetDatenWithBuendelCommand extends CopyEndgeraetDatenCommand {

    private static final Logger LOGGER = Logger.getLogger(CopyEndgeraetDatenWithBuendelCommand.class);

    private AuftragDaten adBuendelSrc = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        copyEG2Auftrag(getAuftragIdSrc(), getSessionId());
        if (adBuendelSrc != null) {
            copyEG2Auftrag(adBuendelSrc.getAuftragId(), getSessionId());
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        // Buendel-Auftrag (Child-Auftrag) laden
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten adSrc = getAuftragDatenTx(getAuftragIdSrc());
            List<AuftragDaten> buendelADs =
                    as.findAuftragDaten4BuendelTx(adSrc.getBuendelNr(), AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT);
            if (CollectionTools.isEmpty(buendelADs)) {
                throw new FindException("Keine Buendel-Auftraege gefunden!");
            }

            for (AuftragDaten ad : buendelADs) {
                if (NumberTools.notEqual(getAuftragIdSrc(), ad.getAuftragId())
                        && NumberTools.isLess(ad.getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT)) {
                    adBuendelSrc = ad;
                    break;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Von einem der Ursprungsauftraege konnten keine Endgeraete uebernommen werden! " +
                    "Grund: Ursprungs-Buendel Auftrag wurde nicht gefunden!");
        }
    }


}


