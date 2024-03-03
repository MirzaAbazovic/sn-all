/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2005 14:34:26
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * ServiceCommand, um zu ueberpruefen, ob das Uebertragungsverfahren der Out-Stifte des alten und neuen Auftrags
 * identisch sind. <br> Sollte dies nicht der Fall sein, wird von der Klasse eine FindException mit entsprechender
 * Meldung erzeugt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AssertEqualUetvCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AssertEqualUetvCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(AssertEqualUetvCommand.class);

    private Equipment eqOutSrc = null;
    private Equipment eqOutDest = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        if (eqOutDest.getUetv() != eqOutSrc.getUetv()) {
            throw new FindException("Die Uebertragungsverfahren der EQ-Out Stifte sind " +
                    "unterschiedlich.\nDie Physikaenderung kann deshalb nicht durchgefuehrt werden!");
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle esBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);
            Endstelle esBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            if ((esBDest == null) || (esBSrc == null)) {
                throw new FindException("Die fuer die Ueberpruefung der Uebertragungsverfahren " +
                        "notwendigen Daten konnten nicht ermittelt werden!");
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            Rangierung rangierungEsBDest = rs.findRangierungTx(esBDest.getRangierId());
            Rangierung rangierungEsBSrc = rs.findRangierungTx(esBSrc.getRangierId());

            eqOutDest = (rangierungEsBDest != null) ? rs.findEquipment(rangierungEsBDest.getEqOutId()) : null;
            eqOutSrc = (rangierungEsBSrc != null) ? rs.findEquipment(rangierungEsBSrc.getEqOutId()) : null;

            if ((eqOutDest == null) || (eqOutSrc == null)) {
                throw new FindException("Die EQ-Out Stifte der beteiligten Auftraege konnten nicht " +
                        "ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(
                    "Bei der Ueberpruefung des Uebertragungsverfahrens ist ein Fehler aufgetreten: " +
                            e.getMessage(), e
            );
        }
    }

}


