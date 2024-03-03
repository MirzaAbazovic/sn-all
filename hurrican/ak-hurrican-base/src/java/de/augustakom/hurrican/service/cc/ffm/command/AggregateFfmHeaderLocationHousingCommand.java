/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

// @formatter:off
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.AddressBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.service.cc.HousingService;

/**
 * Command-Klasse, die fuer die 'Header' Daten einer FFM {@link de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder}
 * folgende Daten aggregiert: <br/>
 * <ul>
 *   <li>Standort-Information (= Housing Adresse aus Hurrican)</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderLocationHousingCommand")
@Scope("prototype")
public class AggregateFfmHeaderLocationHousingCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderLocationHousingCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HousingService")
    private HousingService housingService;

    @Override
    public Object execute() throws Exception {
        try {
            HousingBuilding building = housingService.findHousingBuilding4Auftrag(getAuftragId());
            if (building == null || building.getAddress() == null) {
                throw new FFMServiceException(
                        "Housing-Auftrag hat kein Gebäude oder ein Gebäude ohne Adresse hinterlegt!");
            }

            getWorkforceOrder().setLocation(new AddressBuilder().withAddressModel(building.getAddress()).build());

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM Header Location (Housing) Data: " + e.getMessage(), this.getClass());
        }
    }

}
