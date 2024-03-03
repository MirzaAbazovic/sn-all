/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2015
 */
package de.augustakom.hurrican.service.cc.ffm.impl;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataFault;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataRequest;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataResponse;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataService;

/**
 *
 */
@Component("de.augustakom.hurrican.service.cc.ffm.impl.WorkforceDataService")
public class WorkforceDataServiceImpl implements WorkforceDataService {

    private static final Logger LOGGER = Logger.getLogger(WorkforceDataServiceImpl.class);

    @Autowired
    private FFMService ffmService;

    @Resource(name = "de.augustakom.hurrican.service.cc.BAConfigService")
    private BAConfigService baConfigService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Override
    public WorkforceDataResponse getWorkforceData(WorkforceDataRequest in) throws WorkforceDataFault {
        BAVerlaufAnlass verlaufAnlass = null;
        try {
            verlaufAnlass = baConfigService.findBAVerlaufAnlass(in.getIncidentReason());
        }
        catch (FindException e) {
            LOGGER.error("konnte VerlaufAnlass nicht lesen", e);
        }
        if (verlaufAnlass == null) {
            throw new WorkforceDataFault(String.format("Störungsgrund '%s' nicht gefunden", in.getIncidentReason()));
        }

        Auftrag auftrag = null;
        try {
            auftrag = ccAuftragService.findAuftragById(in.getTechnicalOrderId());
        }
        catch (FindException e) {
            LOGGER.error("konnte Auftrag nicht lesen", e);
        }
        if (auftrag == null) {
            throw new WorkforceDataFault(String.format("Auftrag '%d' nicht gefunden", in.getTechnicalOrderId()));
        }

        try {
            WorkforceOrder order = ffmService.createOrder(in.getTechnicalOrderId(), verlaufAnlass.getId());
            WorkforceDataResponse response = new WorkforceDataResponse();
            response.setActivityType(order.getActivityType());
            response.setPlannedDuration(order.getPlannedDuration());
            response.getQualification().addAll(order.getQualification());
            response.setTechParams(order.getDescription().getTechParams());
            return response;
        }
        catch (Exception e) {
            LOGGER.error("Fehler beim Lesen der FFM Daten", e);
            throw new WorkforceDataFault("Fehler beim Lesen der FFM Daten");
        }
    }
}