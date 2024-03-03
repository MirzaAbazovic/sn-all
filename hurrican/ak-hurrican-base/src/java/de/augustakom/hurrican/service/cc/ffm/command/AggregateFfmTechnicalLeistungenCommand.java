/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.TechnicalServiceBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>TechnischeLeistung</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalLeistungenCommand")
@Scope("prototype")
public class AggregateFfmTechnicalLeistungenCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalCpeCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            List<TechLeistung> assignedTechLeistungen =
                    leistungsService.findTechLeistungen4Auftrag(getAuftragId(), getReferenceDate().toLocalDate());
            Map<Long, TechLeistung> techLeistungenById = assignedTechLeistungen.stream()
                    .collect(Collectors.toMap(TechLeistung::getId, Function.identity(), (v1, v2) -> v1));

            if (getBauauftrag().isPresent()) {
                // zusaetzlich noch alle techn. Leistungen ermitteln, die durch den Bauauftrag hinzukommen
                // (dadurch werden auch einmalige Leistungen mit Datum von=bis ermittelt)
                List<TechLeistung> techLeistungen4Verlauf =
                        leistungsService.findTechLeistungen4Verlauf(getBauauftrag().get().getId(), true);
                techLeistungen4Verlauf.stream()
                        .forEach(tl -> techLeistungenById.put(tl.getId(), tl));
            }

            getWorkforceOrder().getDescription().getTechParams()
                    .getTechnicalService().addAll(convert(techLeistungenById.values()));

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams Leistungen Data: " + e.getMessage(), this.getClass());
        }
    }

    List<OrderTechnicalParams.TechnicalService> convert(Collection<TechLeistung> techLeistungen) {
        if (CollectionUtils.isEmpty(techLeistungen)) {
            return Collections.emptyList();
        }

        return techLeistungen
                .stream()
                .map(
                        techLs ->
                                new TechnicalServiceBuilder()
                                        .withType(techLs.getTyp())
                                        .withName(techLs.getName())
                                        .build()
                )
                .collect(Collectors.toList());
    }

}
