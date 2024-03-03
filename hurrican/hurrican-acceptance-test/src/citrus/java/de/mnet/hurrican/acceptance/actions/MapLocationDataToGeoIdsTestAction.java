/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2015
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;

public class MapLocationDataToGeoIdsTestAction extends AbstractTestAction {

    private AvailabilityService availabilityService;
    private String street;
    private String houseNum;
    private String houseNumExt;
    private String zipCode;
    private String city;
    private String district;

    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    public MapLocationDataToGeoIdsTestAction(AvailabilityService availabilityService,
            String street,
            String houseNum,
            String houseNumExt,
            String zipCode,
            String city,
            String district) {
        this.availabilityService = availabilityService;
        this.street = street;
        this.houseNum = houseNum;
        this.houseNumExt = houseNumExt;
        this.zipCode = zipCode;
        this.city = city;
        this.district = district;
    }

    @Override
    public void doExecute(TestContext testContext) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    availabilityService.mapLocationDataToGeoIds(street, houseNum, houseNumExt, zipCode, city, district);
                }
                catch (StoreException e) {
                    throw new CitrusRuntimeException("Error on AvailabilityService#mapLocationDataToGeoIds: "+e.getMessage(), e);
                }
            }
        });
    }

}
