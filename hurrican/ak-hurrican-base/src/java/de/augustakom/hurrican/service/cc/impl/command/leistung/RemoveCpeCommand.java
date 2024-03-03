/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.14
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;

// @formatter:off
/**
 * Die Command-Klasse fuehrt notwendige Aktionen aus, wenn eine FritzBox(=CPE) von einem Auftrag (speziell Telefon Flat)
 * entfernt wird. <br/>
 * Die durchgefuehrten Aktionen: <br/>
 * <ul>
 *     <li>in Zukunft gueltige DSLAM-Profile werden deaktiviert</li>
 *     <li>SIP-Domain wird auf Default SIP-Domain zurueck gesetzt</li>
 *     <ul>
 *         <li>Achtung: ist nicht historisiert! Daher ab entfernen der FritzBox neue SIP-Domain! (nach Absprache mit
 *                      Andreas Gilg)</li>
 *     </ul>
 * </ul>
 */
// @formatter:on
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.RemoveCpeCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RemoveCpeCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(RemoveCpeCommand.class);

    @Autowired
    private DSLAMService dslamService;
    @Autowired
    private VoIPService voipService;
    @Autowired
    private SIPDomainService sipDomainService;

    @Override
    public Object execute() throws Exception {
        try {
            deactivateDslamProfile();
            resetSipDomainToDefault();

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    String.format("Fehler beim Entfernen des CPEs: %s", e.getMessage()), e);
        }
    }

    /**
     * Deaktiviert das DSLAM-Profil zu dem Datum, ab dem die CPE-Leistung nicht mehr gueltig ist.
     */
    void deactivateDslamProfile() throws HurricanServiceCommandException {
        try {
            List<Auftrag2DSLAMProfile> assignedProfiles = dslamService.findAuftrag2DSLAMProfiles(getAuftragId());
            if (CollectionUtils.isNotEmpty(assignedProfiles)) {
                for (Auftrag2DSLAMProfile auftrag2DSLAMProfile : assignedProfiles) {
                    if (DateTools.isDateAfter(auftrag2DSLAMProfile.getGueltigBis(), getAktivBis())) {
                        auftrag2DSLAMProfile.setGueltigBis(getAktivBis());
                        dslamService.saveAuftrag2DSLAMProfile(auftrag2DSLAMProfile);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(
                    String.format("Fehler bei der Deaktivierung des DSLAM-Profils: %s", e.getMessage()), e);
        }
    }

    /**
     * Setzt die SIP-Domaene des Auftrags auf den Produkt-Default zurueck.
     */
    void resetSipDomainToDefault() throws FindException, StoreException, HurricanServiceCommandException {
        try {
            List<AuftragVoipDNView> auftragVoipDns = voipService.findVoIPDNView(getAuftragId());
            if (CollectionUtils.isNotEmpty(auftragVoipDns)) {
                Reference defaultDomain = sipDomainService.findDefaultSIPDomain4Auftrag(getAuftragId());
                if (defaultDomain == null) {
                    throw new HurricanServiceCommandException("Die Default SIP-Domain fuer den Auftrag konnte nicht ermitteln!");
                }

                for (AuftragVoipDNView auftragVoipDNView : auftragVoipDns) {
                    auftragVoipDNView.setSipDomain(defaultDomain);
                }

                voipService.saveSipDomainOnVoIPDNs(auftragVoipDns, getAuftragId());
            }
            else {
                throw new HurricanServiceCommandException("Es konnten keine VOIP Daten zu dem Auftrag ermittelt werden!");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(
                    String.format("Fehler bei der Zuordnung der Default SIP-Domain zu dem Auftrag: %s", e.getMessage()), e);
        }
    }


}
