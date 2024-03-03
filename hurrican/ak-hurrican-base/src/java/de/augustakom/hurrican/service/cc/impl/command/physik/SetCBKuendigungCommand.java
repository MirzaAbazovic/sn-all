/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2005 08:30:15
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Command-Klasse, um die letzte Carrierbestellung des Ursprungs-Auftrags zu kuendigen (die Kuendigung erfolgt nur in
 * der DB!). <br> Als Kuendigungstermin wird entweder das Bereitstellungsdatum der letzten Carrierbestellung des
 * Ziel-Auftrags verwendet. Sollte dieses Datum nicht ermittelt werden koennen, wird das heutige Datum verwendet.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.SetCBKuendigungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetCBKuendigungCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(SetCBKuendigungCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        cbKuendigen();
        return null;
    }

    /*
     * Kuendigt die letzte/aktuellste Carrierbestellung des Ursprungs-Auftrags.
     */
    private void cbKuendigen() throws StoreException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle esBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            Endstelle esBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);
            if (esBSrc == null || esBDest == null) {
                throw new FindException("Endstelle B des Ursprungs- oder Ziel-Auftrags nicht gefunden!");
            }

            CarrierService cs = getCCService(CarrierService.class);
            List<Carrierbestellung> cbs = cs.findCBs4EndstelleTx(esBSrc.getId());
            if (cbs != null && !cbs.isEmpty()) {
                Carrierbestellung cb = cbs.get(0);

                // Kuendigung in urspruengliche Carrierbestellung eintragen.
                // Kuendigungsdatum fuer die urspr. CB ist das Bereitstellungsdatum der neuen CB.
                List<Carrierbestellung> cbsAct = cs.findCBs4EndstelleTx(esBDest.getId());
                Date kuendigung = new Date();
                if (cbsAct != null && !cbsAct.isEmpty() && cbsAct.get(0).getBereitstellungAm() != null) {
                    kuendigung = cbsAct.get(0).getBereitstellungAm();
                }

                cb.setKuendBestaetigungCarrier(kuendigung);
                cb.setKuendigungAnCarrier(kuendigung);
                cs.saveCB(cb);
            }
            else {
                addWarning(this, "Es konnte keine Carrierbestellung fuer den Ursprungs-Auftrag ermittelt werden.\n" +
                        "Bitte erfassen Sie die Kuendigung dieser Carrierbestellung selbst.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Die Kuendigungs-Daten der urspruenglichen Carrierbestellung konnten nicht " +
                    "erfasst werden!\nGrund: " + e.getMessage());
        }
    }
}


