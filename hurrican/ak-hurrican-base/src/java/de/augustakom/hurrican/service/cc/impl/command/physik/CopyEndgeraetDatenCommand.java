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
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 * Diese Command-Klasse kopiert die Endgeraet-Daten vom Ursprungs- auf den Ziel-Auftrag. <br> Neben der EG-Zuordnung
 * wird auch eine evtl. vorhandene EG-Konfiguration kopiert. Dabei wird allerdings nur die aktuelle Konfiguration
 * beruecksichtigt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.CopyEndgeraetDatenCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CopyEndgeraetDatenCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(CopyEndgeraetDatenCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        copyEG2Auftrag(getAuftragIdSrc(), getSessionId());
        return null;
    }

    /**
     * Kopiert die Endgeraete des 'alten' Auftrags auf den neuen Auftrag.
     */
    protected void copyEG2Auftrag(Long auftragId, Long sessionId) throws StoreException {
        try {
            EndgeraeteService egs = getCCService(EndgeraeteService.class);
            List<EG2Auftrag> egs2Auftrag = egs.findEGs4Auftrag(auftragId);
            if (CollectionTools.isNotEmpty(egs2Auftrag)) {
                for (EG2Auftrag eg2a : egs2Auftrag) {
                    // Zuordnung fuer neuen Auftrag erzeugen
                    EG2Auftrag newEG2A = EG2Auftrag.createCopy(eg2a, getAuftragIdDest());
                    egs.saveEG2Auftrag(newEG2A, sessionId);

                    // Konfiguration kopieren
                    copyEGConfig(egs, eg2a.getId(), newEG2A.getId());
                }
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Endgeraete-Daten konnten nicht uebernommen werden. Grund:\n" + e.getMessage());
        }
    }

    /*
     * Kopiert die aktuelle EG-Konfiguration des angegebenen Endgeraets.
     * @param eg2AuftragIdSrc ID der (alten) Endgeraet-2-Auftragszuordnung
     * @param eg2AuftragIdDest ID der (neuen) Endgeraet-2-Auftragszuordnung
     * @throws StoreException
     */
    private void copyEGConfig(EndgeraeteService service, Long eg2AuftragIdSrc, Long eg2AuftragIdDest)
            throws StoreException {
        try {
            EGConfig config = service.findEGConfig(eg2AuftragIdSrc);
            if (config != null) {
                EGConfig newConfig = EGConfig.createCopy(config, eg2AuftragIdDest);
                service.saveEGConfig(newConfig, getSessionId());
            }
        }
        catch (Exception e) {
            throw new StoreException("EG-Konfiguration konnte nicht kopiert werden!", e);
        }
    }

}


