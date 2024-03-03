/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.06.2006 13:36:51
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Check-Command um zu pruefen, ob die aus der tech. Leistung des Auftrags hervorgehende Downstream-Bandbreite mit der
 * dem Auftrag zugeordneten Physik/Hardware moeglich ist. Command bezieht sich auf die in der DB hinterlegten technische
 * Leistungen (LS_CHECK_ZUGANG).
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.CheckZugangBandwidthCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckZugangBandwidthCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckZugangBandwidthCommand.class);

    // dem Auftrag zugeordnete Rangierung (Endstelle B)
    protected Rangierung rangierung = null;

    private RangierungsService rangierungsService = null;
    private EndstellenService endstellenService = null;

    @Override
    public ServiceCommandResult execute() throws Exception {
        try {
            rangierungsService = getCCService(RangierungsService.class);
            endstellenService = getCCService(EndstellenService.class);
            TechLeistung techLeistung = getTechLeistung();
            Bandwidth requiredBandwidth = Bandwidth.create(techLeistung.getLongValue().intValue());
            loadRequiredData();

            if (getRangierung() != null
                    && !rangierungsService.isBandwidthPossible4Rangierung(getRangierung(), requiredBandwidth)) {

                return ServiceCommandResult
                        .createCmdResult(
                                ServiceCommandResult.CHECK_STATUS_INVALID,
                                String.format(
                                        "Die benötigte Bandbreite %d kann mit der aktuellen Physik/Hardware nicht realisiert werden!",
                                        techLeistung.getLongValue()), this.getClass()
                        );
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format("Fehler in der Bandbreitenprüfung:%n%s",
                    e.getMessage()), e);
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
    }

    @Override
    protected void loadRequiredData() throws FindException {
        try {
            Endstelle esB = endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if ((esB != null) && (esB.getRangierId() != null)) {
                setRangierung(rangierungsService.findRangierung(esB.getRangierId()));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Rangierung.%nPrüfung der benötigten "
                    + "Bandbreite für Auftrag nicht möglich!", e);
        }
    }

    public Rangierung getRangierung() {
        return rangierung;
    }

    private void setRangierung(Rangierung rangierung) {
        this.rangierung = rangierung;
    }

}
