/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2014
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import java.util.stream.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Setzt die SIP-Domaene Reference.REF_ID_SIP_DOMAIN_MAXI_M_CALL fuer alle VoipRN des Auftrages, wenn die SIP-Domaene
 * beim Produkt des Auftrages als moegliche Domaene konfiguriert ist. Erstellt die VOIP Daten fuer den Auftrag wenn noch
 * nicht vorhanden (#createAuftragVoIPDN())
 * <p/>
 * Sollte als "LS_ZUGANG" fuer die CPE Leistung konfiguriert werden.
 * <p/>
 * Created by guiber on 24.04.2014.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.SetMaxiSipDomainCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetMaxiSipDomainCommand extends AbstractVoIPCommand {
    private static final Logger LOGGER = Logger.getLogger(SetMaxiSipDomainCommand.class);

    @Override
    public ServiceCommandResult execute() throws Exception {
        try {
            Reference maxiSipDomain = checkAndGetMaxiSipDomain();
            if (maxiSipDomain != null) {
                // anlegen wenn noch nicht vorhanden ... (da ansonsten abhaengig von der Reihenfolge der ServiceCommands)
                createAuftragVoIPDN();

                VoIPService voipService = getCCService(VoIPService.class);
                List<AuftragVoipDNView> auftragVoipDns = voipService.findVoIPDNView(getAuftragId());
                for (AuftragVoipDNView view : auftragVoipDns) {
                    view.setSipDomain(maxiSipDomain);
                }
                if (!auftragVoipDns.isEmpty()) {
                    voipService.saveSipDomainOnVoIPDNs(auftragVoipDns, getAuftragId());
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler beim Zuordnen der MAXI SIP-Domain!\n" +
                            "Fehlermeldung:\n" + e.getMessage(), e
            );
        }
    }

    private Reference checkAndGetMaxiSipDomain() throws Exception {
        AuftragDaten auftragDaten = getAuftragDatenTx(getAuftragId());
        AuftragTechnik auftragTechnik = getAuftragTechnikTx(getAuftragId());
        if (auftragTechnik.getHwSwitch() == null) {
            throw new FindException(String.format("Ermittlung Maxi SIP-Domäne für Auftrag %s fehlgeschlagen, da "
                    + "Switchkenner nicht verfügbar!", auftragDaten.getAuftragId()));
        }
        List<Produkt2SIPDomain> sipDomains = getCCService(SIPDomainService.class)
                .findProdukt2SIPDomains(auftragDaten.getProdId(), auftragTechnik.getHwSwitch(), null);
        if (sipDomains != null) {
            List<Reference> sipDomainRefs = sipDomains.stream()
                    .map(Produkt2SIPDomain::getSipDomainRef)
                    .filter(sd -> (sd.getId().equals(Reference.REF_ID_SIP_DOMAIN_MAXI_M_CALL)
                            || sd.getId().equals(Reference.REF_ID_SIP_DOMAIN_MAXI_M_CALL_MUC06)
                            || sd.getId().equals(Reference.REF_ID_SIP_DOMAIN_MAXI_M_CALL_MUC07)))
                    .collect(Collectors.toList());
            if (sipDomainRefs.size() > 1) {
                throw new FindException(String.format("Ermittlung Maxi SIP-Domäne für Auftrag %s fehlgeschlagen, da "
                        + "mehr als eine Domäne konfiguriert ist!", auftragDaten.getAuftragId()));
            }
            else if (sipDomainRefs.size() == 1) {
                return sipDomainRefs.get(0);
            }
        }
        return null;
    }
}
