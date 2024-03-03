/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.ElectricMeterBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.HousingBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.TransponderBuilder;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingParcel;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.service.cc.HousingService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>housing</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalHousingCommand")
@Scope("prototype")
public class AggregateFfmTechnicalHousingCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalHousingCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HousingService")
    private HousingService housingService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            AuftragHousing auftragHousing = housingService.findAuftragHousing(getAuftragId());
            if (auftragHousing != null) {
                HousingBuilding building = housingService.findHousingBuilding4Auftrag(getAuftragId());
                HousingFloor floor = housingService.findHousingFloorById(auftragHousing.getFloorId());
                HousingParcel parcel = housingService.findHousingParcelById(auftragHousing.getParcelId());
                HousingRoom room = housingService.findHousingRoomById(auftragHousing.getRoomId());
                List<AuftragHousingKeyView> transponders = housingService.findHousingKeys(getAuftragId());

                OrderTechnicalParams.Housing housing = new HousingBuilder()
                        .withBuilding(building.getBuilding())
                        .withStreet(building.getAddress().getCombinedStreetData())
                        .withZipCode(building.getAddress().getCombinedPlzOrtData())
                        .withFloor((floor != null) ? floor.getFloor() : null)
                        .withRoom((room != null) ? room.getRoom() : null)
                        .withPlot((parcel != null) ? parcel.getParcel() : null)
                        .withRack(auftragHousing.getRack())
                        .withRackUnits((auftragHousing.getRackUnits() != null)
                                ? String.format("%s", auftragHousing.getRackUnits()) : null)
                        .withWattage((auftragHousing.getElectricCircuitCount() != null)
                                ? String.format("%s", auftragHousing.getElectricCircuitCount()) : null)
                        .withCircuitCount((auftragHousing.getElectricCircuitCapacity() != null)
                                ? String.format("%s", auftragHousing.getElectricCircuitCapacity()) : null)
                        .withFuse((auftragHousing.getElectricSafeguard() != null)
                                ? String.format("%s", auftragHousing.getElectricSafeguard()) : null)
                        .addElectricMeter(new ElectricMeterBuilder()
                                .withEmId(auftragHousing.getElectricCounterNumber())
                                .withProvisioning(auftragHousing.getElectricCounterStart())
                                .withTermination(auftragHousing.getElectricCounterEnd())
                                .build())
                        .addElectricMeter(new ElectricMeterBuilder()
                                .withEmId(auftragHousing.getElectricCounterNumber2())
                                .withProvisioning(auftragHousing.getElectricCounterStart2())
                                .withTermination(auftragHousing.getElectricCounterEnd2())
                                .build())
                        .addElectricMeter(new ElectricMeterBuilder()
                                .withEmId(auftragHousing.getElectricCounterNumber3())
                                .withProvisioning(auftragHousing.getElectricCounterStart3())
                                .withTermination(auftragHousing.getElectricCounterEnd3())
                                .build())
                        .addElectricMeter(new ElectricMeterBuilder()
                                .withEmId(auftragHousing.getElectricCounterNumber4())
                                .withProvisioning(auftragHousing.getElectricCounterStart4())
                                .withTermination(auftragHousing.getElectricCounterEnd4())
                                .build())
                        .withTransponder(convert(transponders))
                        .build();

                getWorkforceOrder().getDescription().getTechParams().setHousing(housing);
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams Housing Data: " + e.getMessage(), this.getClass());
        }
    }


    List<OrderTechnicalParams.Housing.Transponder> convert(List<AuftragHousingKeyView> housingKeys) {
        if (CollectionUtils.isEmpty(housingKeys)) {
            return null;
        }

        return housingKeys
                .stream()
                .map(
                        housingKey -> new TransponderBuilder()
                                .withId(housingKey.getTransponderId())
                                .withGroup(housingKey.getTransponderGroupDescription())
                                .withFirstName(housingKey.getCustomerFirstName())
                                .withLastName(housingKey.getCustomerLastName())
                                .build()
                )
                .collect(Collectors.toList());
    }

}
