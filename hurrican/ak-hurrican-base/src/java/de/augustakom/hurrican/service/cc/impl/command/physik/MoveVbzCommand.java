/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 12:09:10
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Die Command-Klasse ordnet dem Ziel-Auftrag die VerbindungsBezeichnung des Ursprungs-Auftrags zu.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.MoveVbzCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MoveVbzCommand extends AbstractPhysikCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        moveVbz();
        return null;
    }

    /* Uebernimmt die VerbindungsBezeichnung des 'alten' auf den 'neuen' Auftrag. */
    private void moveVbz() throws StoreException {
        try {
            // VerbindungsBezeichnung umsetzen (VerbindungsBezeichnung des alten Auftrags wird auf den neuen Auftrag geschrieben)
            PhysikService ps = getCCService(PhysikService.class);
            VerbindungsBezeichnung oldVbz = ps.findVerbindungsBezeichnungByAuftragIdTx(getAuftragIdSrc());
            if (oldVbz != null) {
                AuftragTechnik at = getAuftragTechnikTx(getAuftragIdDest());
                if (at == null) {
                    throw new FindException("Die Auftrags-Daten des Ziel-Auftrags konnten nicht ermittelt werden!");
                }

                at.setVbzId(oldVbz.getId());
                CCAuftragService as = getCCService(CCAuftragService.class);
                as.saveAuftragTechnik(at, false);
            }
        }
        catch (Exception e) {
            throw new StoreException("Leitungsnummer konnte nicht auf neuen Auftrag uebernommen werden!", e);
        }
    }
}


