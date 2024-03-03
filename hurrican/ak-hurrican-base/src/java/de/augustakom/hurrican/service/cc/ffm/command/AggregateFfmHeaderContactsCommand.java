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

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.CommunicationBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.ContactPersonBuilder;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'Header' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert: <br/>
 * <ul>
 *   <li>Ansprechpartner aus Hurrican inkl. Kommunikations-Daten (z.B. Telefon, Mail etc)</li>
 * </ul>
 * <p/>
 * Der {@link Ansprechpartner.Typ#ENDSTELLE_B} wird dabei fix auf den Wert
 * {@link AggregateFfmHeaderContactsCommand#ENDKUNDE_MONTAGELEISTUNG} umgesetzt.
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderContactsCommand")
@Scope("prototype")
public class AggregateFfmHeaderContactsCommand extends AbstractFfmCommand {

    /**
     * Wert fuer FFM Contact-Role aus EMail von Stefan Schoenauer vom 05.09.2014
     */
    static final String ENDKUNDE_MONTAGELEISTUNG = "Endkunde Montageleistung";
    /**
     * Wert fuer FFM Contact-Role (definiert von Timo Botzenhardt)
     */
    static final String KUNDE = "Kunde";
    /**
     * Wert fuer FFM Contact-Role
     */
    public static final String HAUPTPROJEKTVERANTWORTLICHE = "Hauptprojektverantwortliche";
    /**
     * Wert fuer FFM Contact-Role
     */
    public static final String PROJEKTLEITER = "Projektleiter";

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderContactsCommand.class);
    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    private AnsprechpartnerService ansprechpartnerService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService ccKundenService;
    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    private AKUserService userService;

    @Override
    public Object execute() throws Exception {
        try {
            List<Ansprechpartner> ansprechpartners = ansprechpartnerService.findAnsprechpartner(
                    null, getAuftragDaten().getAuftragId());

            if (CollectionUtils.isNotEmpty(ansprechpartners)) {
                for (Ansprechpartner ansprechpartner : ansprechpartners) {
                    // Hurrican Ansprechpartner kennt die Anrede nicht
                    ContactPersonBuilder contactBuilder = new ContactPersonBuilder()
                            .withRole(getRole(Ansprechpartner.Typ.forRefId(ansprechpartner.getTypeRefId())));

                    if (ansprechpartner.getAddress() != null) {
                        contactBuilder
                                .withCommunication(createCommunication(ansprechpartner.getAddress()))
                                .withFirstName(ansprechpartner.getAddress().getVorname())
                                .withLastName(ansprechpartner.getAddress().getName());
                    }

                    getWorkforceOrder().getContactPerson().add(contactBuilder.build());
                }
            }

            // zusaetzlich noch Adresse von Endstelle B ermitteln und als Typ 'Kunde' an FFM uebergeben
            Endstelle endstelleB = getEndstelleB(false);
            if (endstelleB != null && endstelleB.getAddressId() != null) {
                CCAddress address = ccKundenService.findCCAddress(endstelleB.getAddressId());
                ContactPersonBuilder contactBuilder = new ContactPersonBuilder()
                        .withRole(KUNDE)
                        .withCommunication(createCommunication(address))
                        .withFirstName(address.getVorname())
                        .withLastName(address.getName());

                getWorkforceOrder().getContactPerson().add(contactBuilder.build());
            }

            // "Projektleiter" und "Hauptprojektverantwortliche"
            final AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(getAuftragId());
            if (auftragTechnik != null && auftragTechnik.getProjectResponsibleUserId() != null) {
                final AKUser projectResponsibleUser = userService.findById(auftragTechnik.getProjectResponsibleUserId());
                if (projectResponsibleUser != null) {
                    final ContactPersonBuilder contactBuilder = new ContactPersonBuilder()
                            .withRole(HAUPTPROJEKTVERANTWORTLICHE)
                            .withCommunication(createCommunication(projectResponsibleUser))
                            .withFirstName(projectResponsibleUser.getFirstName())
                            .withLastName(projectResponsibleUser.getName());
                    getWorkforceOrder().getContactPerson().add(contactBuilder.build());
                }
            }
            if (auftragTechnik != null && auftragTechnik.getProjectLeadUserId() != null) {
                final AKUser projectLeadUser = userService.findById(auftragTechnik.getProjectLeadUserId());
                if (projectLeadUser != null) {
                    final ContactPersonBuilder contactBuilder = new ContactPersonBuilder()
                            .withRole(PROJEKTLEITER)
                            .withCommunication(createCommunication(projectLeadUser))
                            .withFirstName(projectLeadUser.getFirstName())
                            .withLastName(projectLeadUser.getName());
                    getWorkforceOrder().getContactPerson().add(contactBuilder.build());
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM Header Contact Data: " + e.getMessage(), this.getClass());
        }
    }

    private ContactPerson.Communication createCommunication(CCAddress address) {
        if (address != null) {
            CommunicationBuilder communicationBuilder = new CommunicationBuilder();

            if (StringUtils.isNotBlank(address.getTelefon())) {
                communicationBuilder.addPhone(address.getTelefon());
            }

            if (StringUtils.isNotBlank(address.getHandy())) {
                communicationBuilder.addMobile(address.getHandy());
            }

            if (StringUtils.isNotBlank(address.getFax())) {
                communicationBuilder.addFax(address.getFax());
            }

            if (StringUtils.isNotBlank(address.getEmail())) {
                communicationBuilder.addEmail(address.getEmail());
            }
            return communicationBuilder.build();
        }
        return null;
    }

    private ContactPerson.Communication createCommunication(AKUser user) {
        if (user != null) {
            final CommunicationBuilder communicationBuilder = new CommunicationBuilder();
            if (StringUtils.isNotBlank(user.getPhone())) {
                communicationBuilder.addPhone(user.getPhone());
            }
            if (StringUtils.isNotBlank(user.getFax())) {
                communicationBuilder.addFax(user.getFax());
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                communicationBuilder.addEmail(user.getEmail());
            }
            return communicationBuilder.build();
        }
        return null;
    }

    /**
     * Transformiert die Ansprechpartner-Rolle in einen definierten Namen fuer FFM.
     *
     * @param ansprechpartnerTyp
     * @return
     */
    private String getRole(Ansprechpartner.Typ ansprechpartnerTyp) {
        if (Ansprechpartner.Typ.ENDSTELLE_B.equals(ansprechpartnerTyp)) {
            return ENDKUNDE_MONTAGELEISTUNG;
        }
        return ansprechpartnerTyp.name();
    }

}
