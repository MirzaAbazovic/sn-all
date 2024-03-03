/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 13:53:14
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Die Command-Klasse setzt die Auftragsart des Ziel-Auftrags abhaengig von der Physikaenderungs-Strategie.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.ChangeAuftragsartCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChangeAuftragsartCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(ChangeAuftragsartCommand.class);

    @Override
    public Object executeAfterFlush() throws Exception {
        Long auftragsart = null;
        Long strategy = getStrategy();
        if (NumberTools.equal(strategy, PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME)) {
            auftragsart = BAVerlaufAnlass.ANSCHLUSSUEBERNAHME;
        }
        else if (NumberTools.equal(strategy, PhysikaenderungsTyp.STRATEGY_BANDBREITENAENDERUNG)) {
            auftragsart = BAVerlaufAnlass.BANDBREITENAENDERUNG;
        }

        changeAuftragsart(getAuftragTechnikTx(getAuftragIdDest()), auftragsart);
        return null;
    }

    /* Aendert die Auftragsart der aktuellen Auftrag-Technik. */
    private void changeAuftragsart(AuftragTechnik atDest, Long auftragsart) throws StoreException {
        if (auftragsart == null) {
            addWarning(this, "Es wurde nicht angegeben, auf welche Auftragsart der Ziel-Auftrag gesetzt werden soll.");
            return;
        }

        try {
            atDest.setAuftragsart(auftragsart);
            CCAuftragService as = getCCService(CCAuftragService.class);
            as.saveAuftragTechnik(atDest, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Dem Auftrag konnte die Änderung nicht übergeben werden.", e);
        }
    }

}


