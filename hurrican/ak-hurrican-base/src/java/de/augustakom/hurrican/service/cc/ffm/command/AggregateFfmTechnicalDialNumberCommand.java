/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.OptionalTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DialNumberBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DialNumberPlanBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.NumberRangeBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.VoipLoginDataBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>dialNumber</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalDialNumberCommand")
@Scope("prototype")
public class AggregateFfmTechnicalDialNumberCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalDialNumberCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;

    @Resource(name = "de.augustakom.hurrican.service.cc.VoIPService")
    private VoIPService voipService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            final AuftragDaten auftragDaten = getAuftragDaten();
            if (auftragDaten.getAuftragNoOrig() != null) {
                Collection<Rufnummer> rufnummern = rufnummerService.findRNs4Auftrag(
                        auftragDaten.getAuftragNoOrig(), DateConverterUtils.asDate(getReferenceDate()));

                if (CollectionUtils.isNotEmpty(rufnummern)) {
                    for (Rufnummer rufnummer : rufnummern) {
                        DialNumberBuilder dnBuilder = new DialNumberBuilder()
                                .withAreaDialingCode(rufnummer.getOnKz())
                                .withNumber(rufnummer.getDnBase())
                                .withValidFrom(rufnummer.getGueltigVon() != null
                                        ? DateConverterUtils.asLocalDate(rufnummer.getGueltigVon())
                                        : LocalDate.now())
                                .withValidTo(rufnummer.getGueltigBis() != null
                                        ? DateConverterUtils.asLocalDate(rufnummer.getGueltigBis())
                                        : LocalDate.now())
                                .withMain(rufnummer.isMainNumber())
                                .withPortMode(rufnummer.getPortMode())
                                .withVoIPLogin(getVoipLogin(auftragDaten, rufnummer).orElse(null))
                                .addRufnummerplans(getRufnummernplans(auftragDaten, rufnummer));

                        if (rufnummer.isBlock()) {
                            dnBuilder.withNumberRange(new NumberRangeBuilder()
                                            .withCentral(rufnummer.getDirectDial())
                                            .withFrom(rufnummer.getRangeFrom())
                                            .withTo(rufnummer.getRangeTo())
                                            .build()
                            );
                        }

                        getWorkforceOrder().getDescription().getTechParams().getDialNumber().add(dnBuilder.build());
                    }
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams DialNumber Data: " + e.getMessage(), this.getClass());
        }
    }

    List<OrderTechnicalParams.DialNumber.DialNumberPlan> getRufnummernplans(AuftragDaten auftragDaten, Rufnummer rufnummer) throws FindException  {
        final Optional<Date> realDate = getBauauftrag().map(Verlauf::getRealisierungstermin);
        if(realDate.isPresent())    {
            final Stream<AuftragVoipDNView> voipDnViews = getGueltigeAuftragVoipDnViews(auftragDaten, realDate.get());
            final Optional<AuftragVoipDNView> viewForDn = getAuftragVoipDNViewForRufnummer(voipDnViews, rufnummer);
            final Optional<VoipDnPlanView> actualRufnummernplan = getActualRufnummernplan(realDate.get(), viewForDn);
            final Stream<VoipDn2DnBlockView> voipDnBlocks = extractVoipDnBlockViews(actualRufnummernplan);
            return convertToRufnummernplans(voipDnBlocks);
        }
        else {
            return Collections.emptyList();
        }
    }

    private Stream<AuftragVoipDNView> getGueltigeAuftragVoipDnViews(AuftragDaten auftragDaten, Date realDate) throws FindException {
        return voipService.findVoIPDNView(auftragDaten.getAuftragId())
                .stream()
                .filter(v -> DateTools.isDateBetween(realDate, v.getGueltigVon(), v.getGueltigBis()));
    }

    private List<OrderTechnicalParams.DialNumber.DialNumberPlan> convertToRufnummernplans(Stream<VoipDn2DnBlockView> voipDnBlocks) {
        return voipDnBlocks.map((VoipDn2DnBlockView block) -> new DialNumberPlanBuilder()
                        .withDnBase(block.getDnBase())
                        .withStart(block.getAnfang())
                        .withEnd(block.getEnde())
                        .withOnkz(block.getOnkz())
                        .withZentrale(block.getZentrale())
                .build())
                .collect(Collectors.toList());
    }

    private Stream<VoipDn2DnBlockView> extractVoipDnBlockViews(Optional<VoipDnPlanView> actualRufnummernplan) {
        return OptionalTools.streamOf(actualRufnummernplan)
                .flatMap((VoipDnPlanView view) -> view.getSortedVoipDn2DnBlockViews().stream());
    }

    private Optional<VoipDnPlanView> getActualRufnummernplan(Date today, Optional<AuftragVoipDNView> viewForDn) {
        return viewForDn.flatMap(v -> v.getActiveVoipDnPlanView(today));
    }

    private Optional<AuftragVoipDNView> getAuftragVoipDNViewForRufnummer(Stream<AuftragVoipDNView> voipDnViews, Rufnummer rufnummer) {
        return voipDnViews
                .filter(v -> v.getDnNoOrig().equals(rufnummer.getDnNoOrig()))
                .findFirst();
    }

    Optional<OrderTechnicalParams.DialNumber.VoIPLogin> getVoipLogin(AuftragDaten auftragDaten, Rufnummer rufnummer) throws FindException {
        final Optional<AuftragVoIPDN> auftragVoipDn =
                Optional.ofNullable(voipService.findByAuftragIDDN(auftragDaten.getAuftragId(), rufnummer.getDnNoOrig()));
        return auftragVoipDn.map(av -> {
                VoipLoginDataBuilder builder = new VoipLoginDataBuilder()
                        .withSipPassword(av.getSipPassword());

                VoipDnPlan plan = av.getActiveRufnummernplan(new Date());
                if (plan != null) {
                    builder.withSipMainNumber(plan.getSipHauptrufnummer());
                    builder.withSipLogin(plan.getSipLogin());
                }

                 return builder.build();
            }
        );
    }

}
