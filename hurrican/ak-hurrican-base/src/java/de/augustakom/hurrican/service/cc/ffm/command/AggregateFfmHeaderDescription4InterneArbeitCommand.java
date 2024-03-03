/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.ReferenceService;

// @formatter:off
/**
 * @see {@link AggregateFfmHeaderDescriptionCommand} <br/>
 * Zusaetzlich wird noch die Bemerkung aus der 'internen Arbeit' ermittelt und gesetzt.
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderDescription4InterneArbeitCommand")
@Scope("prototype")
public class AggregateFfmHeaderDescription4InterneArbeitCommand extends AggregateFfmHeaderDescriptionCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderDescription4InterneArbeitCommand.class);

    /** Default value for Project-Responsible, Project-Lead and so on */
    private static final String UNBEKANNT = "Unbekannt";

    @Resource(name = "de.augustakom.hurrican.service.cc.AuftragInternService")
    private AuftragInternService auftragInternService;

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    private AKUserService userService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    protected String loadBemerkungen(AuftragDaten auftragDaten) throws FindException {
        StringBuilder details = new StringBuilder();
        try {
            AuftragIntern auftragIntern = auftragInternService.findByAuftragId(auftragDaten.getAuftragId());
            if (auftragIntern != null) {
                if (StringUtils.isNotBlank(auftragIntern.getDescription())) {
                    details.append(auftragIntern.getDescription());
                }

                if (details.length() > 0) {
                    details.append(System.lineSeparator());
                    details.append(LINE);
                    details.append(System.lineSeparator());
                }

                details.append("Arbeit: ").append(getWorkType(auftragIntern));
                details.append(System.lineSeparator());

                String projectResponsibleName = UNBEKANNT;
                String projectLeadName = UNBEKANNT;
                AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(auftragDaten.getAuftragId());
                if (auftragTechnik != null) {
                    projectResponsibleName = getProjectResponsible(auftragTechnik);
                    projectLeadName = getProjectLead(auftragTechnik);
                }

                details.append("Projektleiter: ").append(projectResponsibleName);
                details.append(System.lineSeparator());
                details.append("Hauptprojektverantwortlicher: ").append(projectLeadName);
            }
        }
        catch (FindException e) {
            LOGGER.error(String.format(
                    "Error loading 'AuftragIntern' record for AuftragId %s: %s",
                    auftragDaten.getAuftragId(), e.getMessage()), e);
        }

        String defaultDetails = super.loadBemerkungen(auftragDaten);
        if (details.length() > 0 && StringUtils.isNotBlank(defaultDetails)) {
            details.append(System.lineSeparator());
            details.append(LINE);
            details.append(System.lineSeparator());
        }

        details.append(defaultDetails);
        return details.toString();
    }

    private String getProjectResponsible(AuftragTechnik auftragTechnik) {
        try {
            return userService.findById(auftragTechnik.getProjectResponsibleUserId()).getFirstNameAndName();
        }
        catch (AKAuthenticationException e) {
            LOGGER.error(String.format("Error loading user with id %s", auftragTechnik.getProjectResponsibleUserId()), e);
        }
        return UNBEKANNT;
    }

    private String getProjectLead(AuftragTechnik auftragTechnik) {
        if (auftragTechnik.getProjectLeadUserId() != null) {
            try {
                return userService.findById(auftragTechnik.getProjectLeadUserId()).getFirstNameAndName();
            }
            catch (AKAuthenticationException e) {
                LOGGER.error(String.format("Error loading user with id %s", auftragTechnik.getProjectResponsibleUserId()), e);
            }
        }
        return UNBEKANNT;
    }

    private String getWorkType(AuftragIntern auftragIntern) {
        if (auftragIntern.getWorkingTypeRefId() != null) {
            try {
                Reference workTypeReference = referenceService.findReference(auftragIntern.getWorkingTypeRefId());
                if (workTypeReference != null) {
                    return workTypeReference.getStrValue();
                }
            }
            catch (FindException e) {
                LOGGER.error(String.format("Error loading work type reference with id %s", auftragIntern.getWorkingTypeRefId()), e);
            }
        }
        return UNBEKANNT;
    }

}
