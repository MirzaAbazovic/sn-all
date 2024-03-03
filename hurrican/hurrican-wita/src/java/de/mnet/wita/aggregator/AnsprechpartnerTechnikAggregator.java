/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 14:22:32
 */
package de.mnet.wita.aggregator;

import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.lang.TelefonnummerUtils;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.Ansprechpartner;
import de.mnet.wita.message.auftrag.AnsprechpartnerRolle;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um den technischen Ansprechpartner zu einem Auftrag zu laden. <br> Als techn. Ansprechpartner wird
 * der Hauptprojektverantwortliche des Auftrags ermittelt. Falls kein Hauptprojektverantwortlicher definiert ist, wird
 * kein {@link Ansprechpartner} ermittelt.
 */
public class AnsprechpartnerTechnikAggregator extends AbstractWitaDataAggregator<Ansprechpartner> {

    private static final Logger LOG = Logger.getLogger(AnsprechpartnerTechnikAggregator.class);

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    AKUserService userService;

    @Override
    public Ansprechpartner aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragIdTx(cbVorgang.getAuftragId());
            // @formatter:off
            if (auftragTechnik == null) {
                throw new WitaDataAggregationException("Auftrag konnte nicht ermittelt werden!");
            }
            // @formatter:on

            if (auftragTechnik.getProjectLeadUserId() != null) {
                AKUser user = loadUser(auftragTechnik.getProjectLeadUserId());
                if (user == null) {
                    throw new WitaDataAggregationException(
                            "Der Ansprechpartner (Technik) konnte nicht ermittelt werden!");
                }

                Ansprechpartner ansprechpartner = new Ansprechpartner();
                ansprechpartner.setRolle(AnsprechpartnerRolle.TECHNIK);
                ansprechpartner.setAnrede(Anrede.UNBEKANNT);
                ansprechpartner.setNachname(user.getName());
                ansprechpartner.setVorname(user.getFirstName());
                ansprechpartner.setTelefonnummer(TelefonnummerUtils.convertTelefonnummer(user.getPhone()));
                ansprechpartner.setEmail(user.getEmail());
                return ansprechpartner;
            }
        }
        catch (WitaDataAggregationException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Fehler bei der Ermittlung des Ansprechpartners (Technik) zum Auftrag: " + e.getMessage(), e);
        }
        return null;
    }

    private AKUser loadUser(Long userId) {
        try {
            return userService.findById(userId);
        }
        catch (AKAuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Fehler bei der Ermittlung des Ansprechpartners (Technik) zum Auftrag: " + e.getMessage(), e);
        }
    }
}
