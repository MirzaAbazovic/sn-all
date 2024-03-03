/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 30.11.2016

 */

package de.augustakom.hurrican.service.cc.ffm.command;


import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.PseBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Fuegt dem Request eine ggf. PSE hinzu.
 * PSE wird immer im GFAST-Fall verbaut.
 * PSE Typ wird Stand 12/2016 nicht in Hurrican gepflegt, da nur das selbe Modell bei m-net verbaut wird.
 */
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalPseCommand")
@Scope("prototype")
public class AggregateFfmTechnicalPseCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalPseCommand.class);

    private static final OrderTechnicalParams.PSE defaultPse = new PseBuilder()
            .withManufacturer("Huawei")
            .withModel("PSE-235")
            .withVersion("1.0")
            .build();

    private static final ServiceCommandResult okResult = ServiceCommandResult.createCmdResult(
            ServiceCommandResult.CHECK_STATUS_OK, null, AggregateFfmTechnicalPseCommand.class);

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();
            final Optional<HWRack> hwRack = findRack();
            if (hwRack.isPresent() && needsPse(hwRack)) {
                addDefaultPseToTechParams();
            }

            return okResult;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams PSE Data: " + e.getMessage(), this.getClass());
        }
    }

    private Boolean needsPse(Optional<HWRack> hwRack) {
        return hwRack.map(this::hasReversePower).orElse(Boolean.FALSE);
    }

    private boolean hasReversePower(HWRack r) {
        return r instanceof HWDpu && BooleanTools.nullToFalse(((HWDpu) r).getReversePower());
    }

    private void addDefaultPseToTechParams() {
        getWorkforceOrder()
                .getDescription()
                .getTechParams()
                .setPSE(defaultPse);
    }

    private Optional<HWRack> findRack() throws Exception {
        final RangierungsService rangierungsService = getCCService(RangierungsService.class);
        final HWService hwService = getCCService(HWService.class);

        final Endstelle endstelleB = getEndstelleB(false);
        if (endstelleB == null) {
            return Optional.empty();
        }

        final Rangierung rangierung = rangierungsService.findRangierungTx(endstelleB.getRangierId());
        if (rangierung == null) {
            return Optional.empty();
        }

        final Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
        if (eqIn == null){
            return Optional.empty();
        }

        final HWBaugruppe baugruppe = hwService.findBaugruppe(eqIn.getHwBaugruppenId());
        if(baugruppe == null){
            return Optional.empty();
        }
        
        final HWRack hwRack = hwService.findRackById(baugruppe.getRackId());
        if(hwRack == null){
            return Optional.empty();
        }

        return Optional.of(hwRack);
    }
}
