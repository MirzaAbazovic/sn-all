/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DescriptionBuilder;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'Header' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert: <br/>
 * <ul>
 *   <li>Description-Daten wie z.B. Hurrican Produkt, Verlaufs-Bemerkungen etc.</li>
 * </ul>
 * <p/>
 * Die technischen Parameter werden NICHT gesetzt.
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderDescriptionCommand")
@Scope("prototype")
public class AggregateFfmHeaderDescriptionCommand extends AbstractFfmCommand {

    protected static final String LINE = "-----------";
    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderDescriptionCommand.class);
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    @Override
    public Object execute() throws Exception {
        try {
            AuftragDaten auftragDaten = getAuftragDaten();

            DescriptionBuilder descriptionBuilder = new DescriptionBuilder()
                    .withSummary(produktService.findProdukt4Auftrag(auftragDaten.getAuftragId()).getAnschlussart());

            String details = loadBemerkungen(auftragDaten);
            if (StringUtils.isNotBlank(details)) {
                descriptionBuilder.withDetails(details);
            }

            getWorkforceOrder().setDescription(descriptionBuilder.build());

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM Header Timeslot Data: " + e.getMessage(), this.getClass());
        }
    }

    protected String loadBemerkungen(AuftragDaten auftragDaten) throws FindException {
        StringBuilder details = new StringBuilder();
        if (StringUtils.isNotBlank(auftragDaten.getBemerkungen())) {
            details.append(auftragDaten.getBemerkungen());
        }

        if (getBauauftrag().isPresent()) {
            List<VerlaufAbteilung> verlaufAbteilungen =
                    baService.findVerlaufAbteilungen(getBauauftrag().get().getId(),
                            Abteilung.AM, Abteilung.DISPO, Abteilung.NP);

            if (CollectionUtils.isNotEmpty(verlaufAbteilungen)) {
                for (VerlaufAbteilung va : verlaufAbteilungen) {
                    if (StringUtils.isNotBlank(va.getBemerkung())) {
                        if (details.length() > 0) {
                            details.append(System.lineSeparator());
                            details.append(LINE);
                            details.append(System.lineSeparator());
                        }

                        details.append(va.getBemerkung());
                    }
                }
            }
        }
        return details.toString();
    }

}
