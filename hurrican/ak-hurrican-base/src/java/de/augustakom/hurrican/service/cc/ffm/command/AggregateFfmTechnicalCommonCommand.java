/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams.Common.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.CommonBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.PortingBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>common</li>
 *     <ul>
 *         <li>lineId</li>
 *         <li>contractId</li>
 *         <li>porting</li>
 *         <li>...</li>
 *     </ul>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalCommonCommand")
@Scope("prototype")
public class AggregateFfmTechnicalCommonCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalCommonCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();
            final AuftragDaten auftragDaten = getAuftragDaten();

            final CommonBuilder commonBuilder = new CommonBuilder()
                    .withContractId(String.format("%s", auftragDaten.getAuftragId()))
                    .withPorting(loadPortingInformation());

            if (auftragDaten.getAuftragNoOrig() != null) {
                commonBuilder.withAdditionalContractInfo(String.format("%s", auftragDaten.getAuftragNoOrig()));
            }

            final Auftrag auftrag = ccAuftragService.findAuftragById(getAuftragId());
            if (auftrag != null && auftrag.getKundeNo() != null) {
                commonBuilder.withCustomerNumber(String.format("%s", auftrag.getKundeNo()));
            }

            VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
            if (vbz != null) {
                commonBuilder.withLineId(vbz.getVbz());
            }

            getWorkforceOrder().getDescription().getTechParams().setCommon(commonBuilder.build());

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams Common Data: " + e.getMessage(), this.getClass());
        }
    }

    // @formatter:off
    /**
     * Ermittelt das Portierungsdatum fuer den Auftrag, sofern dieser eine Taifun-Referenz mit zu
     * portierenden Rufnummern besitzt. <br/>
     * Bei den Rufnummern werden nur diejenigen beruecksichtigt, die folgende Bedingungen erfuellen:
     * <ul>
     *     <li>Rufnummer ist zum Bauauftragstermin gueltig</li>
     *     <li>Rufnummer ist im Status PORTMODE_KOMMEND</li>
     *     <li>Rufnummer besitzt ein RealDate (=Portierungsdatum)</li>
     * </ul>
     * Sollten mehrere Rufnummern diese Bedingungen erfuellen, so wird nur die erste Rufnummer verwendet.
     * @return
     */
    // @formatter:on
    Porting loadPortingInformation() {
        try {
            AuftragDaten auftragDaten = getAuftragDaten();
            if (auftragDaten.getAuftragNoOrig() == null) {
                return null;
            }

            Collection<Rufnummer> rufnummern = rufnummerService.findRNs4Auftrag(
                    auftragDaten.getAuftragNoOrig(), DateConverterUtils.asDate(getReferenceDate()));
            rufnummern = Collections2.filter(rufnummern, Rufnummer.PORTMODE_KOMMEND);
            rufnummern = Collections2.filter(rufnummern, Rufnummer.REAL_DATE_NOT_NULL);

            if (CollectionUtils.isNotEmpty(rufnummern)) {
                Rufnummer rufnummer = rufnummern.iterator().next();
                return new PortingBuilder()
                        .withPortingDate(DateTools.formatDate(rufnummer.getRealDate(), DateTools.PATTERN_DAY_MONTH_YEAR))
                        .withLastCarrier(rufnummer.getLastCarrier())
                        .build();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}
