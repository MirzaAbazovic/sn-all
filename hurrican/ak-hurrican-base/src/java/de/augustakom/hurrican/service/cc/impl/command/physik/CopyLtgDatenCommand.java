/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 16:09:46
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Command-Klasse, um die Leitungs-Daten vom Ursprungs- auf den Ziel-Auftrag zu kopieren.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.CopyLtgDatenCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CopyLtgDatenCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(CopyLtgDatenCommand.class);

    private Endstelle endstelleADest = null;
    private Endstelle endstelleBDest = null;
    private Endstelle endstelleASrc = null;
    private Endstelle endstelleBSrc = null;
    private Produkt produktDest = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        moveLtgDaten();
        return null;
    }

    /* Kopiert die Leitungsdaten der Endstellen. */
    private void moveLtgDaten() throws StoreException {
        moveLtgDaten(endstelleASrc, endstelleADest);
        moveLtgDaten(endstelleBSrc, endstelleBDest);
    }

    /*
     * Kopiert die Leitungsdaten der Endstelle <code>oldES</code> auf die ES <code>newES</code>.
     * Wichtig: Exceptions werden unterdrueckt!!!
     */
    private void moveLtgDaten(Endstelle esSrc, Endstelle esDest) throws StoreException {
        try {
            EndstellenService esSrv = (EndstellenService) getCCService(EndstellenService.class);
            EndstelleLtgDaten oldLtg = esSrv.findESLtgDaten4ESTx(esSrc.getId());

            if (oldLtg != null) {
                EndstelleLtgDaten newLtg = esSrv.findESLtgDaten4ESTx(esDest.getId());
                if (newLtg == null) {
                    newLtg = new EndstelleLtgDaten();
                    newLtg.setEndstelleId(esDest.getId());
                }

                if (!BooleanTools.nullToFalse(produktDest.getIsCombiProdukt())) {
                    newLtg.setLeitungsartId(oldLtg.getLeitungsartId());
                }
                newLtg.setSchnittstelleId(oldLtg.getSchnittstelleId());
                esSrv.saveESLtgDaten(newLtg, false);
            }
        }
        catch (StoreException e) {
            throw new StoreException("Die Leitungsdaten konnten nicht übernommen werden!", e);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            addWarning(this, "Die Leitungsdaten wurde aus unbekanntem Grund nicht uebernommen.");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    protected void loadRequiredData() throws FindException {
        try {
            EndstellenService esSrv = (EndstellenService) getCCService(EndstellenService.class);
            endstelleASrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_A);
            endstelleBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            endstelleADest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_A);
            endstelleBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);

            ProduktService prodService = (ProduktService) getCCService(ProduktService.class);
            produktDest = prodService.findProdukt4Auftrag(getAuftragIdDest());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}


