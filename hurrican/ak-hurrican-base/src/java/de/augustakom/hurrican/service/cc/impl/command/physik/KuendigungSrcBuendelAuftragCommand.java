/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2006 10:48:21
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
 * Command, um den Buendel-Auftrag (=Child-Auftrag) des Ursprungsauftrags auf 'gekuendigt' (Status=9800) zu setzen. <br>
 * Wird fuer Anschlussuebernahmen von alter Produkt-Struktur (ADSL + phone getrennt) auf die neue Struktur
 * (Kombi-Auftrag) benoetigt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.KuendigungSrcBuendelAuftragCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class KuendigungSrcBuendelAuftragCommand extends KuendigungSrcAuftragCommand {

    private static final Logger LOGGER = Logger.getLogger(KuendigungSrcBuendelAuftragCommand.class);

    private AuftragDaten adBuendelSrc = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        super.executeAfterFlush();
        if (kuendPossible) {
            loadRequiredData();
            changeStatusOfOldAuftrag(adBuendelSrc);
            createVerlaufKuendigung(adBuendelSrc);
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
            addWarning(this, "Einer der Ursprungsauftraege konnte nicht automatisch gekuendigt werden! " +
                    "Bitte Kuendigungsbauauftraege selbst erstellen.");
        }
    }

}


