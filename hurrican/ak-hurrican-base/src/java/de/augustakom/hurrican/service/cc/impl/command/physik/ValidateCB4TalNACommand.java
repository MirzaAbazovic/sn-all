/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2005 07:40:48
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Diese Command-Klasse prueft, ob die (letzte) Carrierbestellung des Ziel-Auftrags fuer eine TAL-Nutzungsaenderung
 * gueltig ist. <br> Die CB ist dann fuer eine TAL-Nutzungsaenderung gueltig, wenn folgende Punkte erfuellt sind: <ul>
 * <li>gueltige LBZ und VtrNr <li>Bereitstellungsdatum gefuellt <li>Kuendigungsdatum darf nicht gesetzt sein <li>es ist
 * eine Auftrags-ID fuer den Ursprungs-Auftrag definiert </ul>
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.ValidateCB4TalNACommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ValidateCB4TalNACommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(ValidateCB4TalNACommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        validateCB4TalNA();
        return null;
    }

    /* Laedt die letzte CB des Ziel-Auftrags und prueft diese auf Gueltigkeit. */
    private void validateCB4TalNA() throws FindException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle esBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);
            if (esBDest == null) {
                throw new FindException("Endstelle B des Ziel-Auftrags konnte nicht ermittelt werden.");
            }

            CarrierService cs = getCCService(CarrierService.class);
            List<Carrierbestellung> cbs = cs.findCBsTx(esBDest.getCb2EsId());
            if (cbs == null || cbs.isEmpty() || cbs.get(0).getAuftragId4TalNA() == null) {
                throw new FindException("Es konnte keine Carrierbestellung fuer die Endstelle B ermittelt werden!");
            }

            Carrierbestellung cb = cbs.get(0);
            if (cb.getAuftragId4TalNA() == null) {
                throw new FindException("In der CB ist kein Ursprungs-Auftrag vermerkt.");
            }

            if (cb.getBereitstellungAm() == null || cb.getKuendigungAnCarrier() != null) {
                throw new FindException("Die CB ist entweder nicht positiv bestaetigt oder bereits gekuendigt.");
            }

            try {
                cs.validateLbz(cb.getCarrier(), cb.getLbz());
            }
            catch (ValidationException e) {
                throw new FindException("Die LBZ der Carrierbestellung ist nicht gueltig.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Es konnte nicht ueberprueft werden, ob die letzte Carrierbestellung " +
                    "gueltig ist.\nGrund: " + e.getMessage(), e);
        }
    }

}


