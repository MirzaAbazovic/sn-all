/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.time.*;
import java.util.*;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMappingBuilder;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

public abstract class AbstractAggregateFfmCommandTest extends BaseTest {

    protected WorkforceOrder workforceOrder;
    protected FfmProductMapping ffmProductMapping;
    protected HVTStandort hvtStandort;
    protected AuftragDaten auftragDaten;

    protected void prepareFfmCommand(AbstractFfmCommand cmd) {
        prepareFfmCommand(cmd, false);
    }

    protected void prepareFfmCommand(AbstractFfmCommand cmd, boolean addTechnicalParamsToWorkforceOrder) {
        workforceOrder = new WorkforceOrder();

        if (addTechnicalParamsToWorkforceOrder) {
            WorkforceOrder.Description description = new WorkforceOrder.Description();
            description.setTechParams(new OrderTechnicalParams());
            workforceOrder.setDescription(description);
        }

        ffmProductMapping = new FfmProductMappingBuilder()
                .setPersist(false).build();

        hvtStandort = new HVTStandortBuilder().setPersist(false).build();

        auftragDaten = new AuftragDatenBuilder().setPersist(false).build();

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId().setPersist(false);
        cmd.prepare(AbstractFfmCommand.KEY_WORKFORCE_ORDER, workforceOrder);
        cmd.prepare(AbstractFfmCommand.KEY_AUFTRAG_ID, auftragBuilder.build().getAuftragId());
        cmd.prepare(AbstractFfmCommand.KEY_FFM_PRODUCT_MAPPING, ffmProductMapping);
        cmd.prepare(AbstractFfmCommand.KEY_HVT_STANDORT, hvtStandort);
        cmd.prepare(AbstractFfmCommand.KEY_AUFTRAG_DATEN, auftragDaten);
        cmd.prepare(AbstractFfmCommand.KEY_TECH_LEISTUNGEN, Collections.emptyList());
        cmd.prepare(AbstractFfmCommand.KEY_REFERENCE_DATE, LocalDateTime.now().plusDays(10));
        cmd.prepare(AbstractFfmCommand.KEY_VERLAUF,
                new VerlaufBuilder()
                        .withRealisierungstermin(DateConverterUtils.asDate(LocalDateTime.now().plusDays(10)))
                        .withAuftragBuilder(auftragBuilder)
                        .setPersist(false).build());
    }

}
