/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2009 18:36:45
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command-Klasse prueft, ob der auf EQ_IN verbundene Port der Rangierung (Endstellen A+B) eine uebersteuerte
 * CrossConnection Konfiguration besitzt.
 * <p/>
 * Ist dies NICHT der Fall, wird ermittelt, ob die zum Port hinterlegte CrossConnection abweichend vom Standard ist. In
 * diesem Fall, wird fuer den Port die Standard CrossConnection mit ValidFrom=Realisierungstermin gesetzt. <br> <br>
 * Aktuell werden nur fuer DSLAM-Ports CrossConnections gesetzt, die einem SubRack zugeordnet sind. Somit ist eine
 * Unterscheidung MUC / AGB moeglich. (Fuer AGB ist die SubRack Struktur noch nicht aufgebaut; die CrossConnections sind
 * fuer AGB vorerst noch nicht notwendig.)
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndDefineCrossConnectionsCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckAndDefineCrossConnectionsCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckAndDefineCrossConnectionsCommand.class);

    private Endstelle endstelleA = null;
    private Endstelle endstelleB = null;
    private Boolean vierDrahtProdukt;

    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private EQCrossConnectionService eqCrossConnectionService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    private HWService hwService;
    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private ProduktService produktService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            loadRequiredData();
            checkAndDefineDefaultCrossConnections();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der CrossConnections ist ein Fehler aufgetreten: " + e.getMessage(),
                    getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * Prueft, ob fuer den Port eine Uebersteuerung der CrossConnections vorhanden ist und setzt diese gegebenenfalls.
     */
    void checkAndDefineDefaultCrossConnections() throws ServiceCommandException {
        try {
            Equipment eqInEndstelleA = getDSLAMPort(endstelleA);
            if ((eqInEndstelleA != null) && !Boolean.TRUE.equals(eqInEndstelleA.getManualConfiguration())) {
                defineDefaultCrossConnection4Port(eqInEndstelleA);
            }

            Equipment eqInEndstelleB = getDSLAMPort(endstelleB);
            if ((eqInEndstelleB != null) && !Boolean.TRUE.equals(eqInEndstelleB.getManualConfiguration())) {
                defineDefaultCrossConnection4Port(eqInEndstelleB);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Prüfung/Definition der Default CrossConnections: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt den DSLAM-Port zu einer Endstelle.
     *
     * @return der ermittelte Port, oder {@code null}
     */
    Equipment getDSLAMPort(Endstelle endstelle) throws FindException {
        if (endstelle.getRangierId() != null) {
            Rangierung rangierung = rangierungsService.findRangierung(endstelle.getRangierId());
            if ((rangierung != null) && (rangierung.getEqInId() != null)) {
                Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
                if (rangierungsService.isPortInADslam(eqIn)) {
                    return eqIn;
                }
            }
        }

        return null;
    }

    /**
     * Definiert auf dem angegebenen Port die Default CrossConnections.
     */
    void defineDefaultCrossConnection4Port(Equipment equipment) throws ServiceCommandException {
        final Date when = (getRealDate() != null) ? getRealDate() : new Date();
        try {
            eqCrossConnectionService.defineDefaultCrossConnections4Port(
                    equipment,
                    getAuftragId(),
                    when,
                    vierDrahtProdukt,
                    getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error validating or defining the cross connections: " + e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            endstelleA = endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_A);
            endstelleB = endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(getAuftragId());
            vierDrahtProdukt = produktService.isVierDrahtProdukt(auftragDaten.getProdId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Daten zur HVT-Ueberpruefung: " + e.getMessage(), e);
        }
    }

}
