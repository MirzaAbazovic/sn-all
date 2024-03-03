/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 10:04:31
 */
package de.mnet.wita.aggregator;

import static de.augustakom.common.tools.lang.TelefonnummerUtils.*;

import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.Auftragsmanagement;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Ansprechpartner-Daten des AM-Mitarbeiters zu laden.
 */
public class AnsprechpartnerAmAggregator extends AbstractWitaDataAggregator<Auftragsmanagement> {

    private static final Logger LOG = Logger.getLogger(AnsprechpartnerAmAggregator.class);

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    AKUserService userService;

    @Override
    public Auftragsmanagement aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            AKUser user = loadUser(cbVorgang);
            if (user == null) {
                throw new WitaDataAggregationException(
                        "Kontaktdaten vom AM-Mitarbeiter konnten nicht ermittelt werden!");
            }

            Auftragsmanagement auftragsmanagement = new Auftragsmanagement();
            auftragsmanagement.setNachname(user.getName());
            auftragsmanagement.setVorname(user.getFirstName());
            auftragsmanagement.setTelefonnummer(convertTelefonnummer(user.getPhone()));
            auftragsmanagement.setEmail(user.getEmail());
            auftragsmanagement.setAnrede(Anrede.UNBEKANNT);
            return auftragsmanagement;
        }
        catch (WitaDataAggregationException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Fehler bei der Ermittlung des Ansprechpartners (Auftragsmanagement) zum Auftrag: "
                            + e.getMessage(), e
            );
        }
    }

    AKUser loadUser(CBVorgang cbVorgang) {
        if ((cbVorgang == null) || (cbVorgang.getUserId() == null)) {
            throw new WitaDataAggregationException("User-ID vom AM-Mitarbeiter ist nicht angegeben!");
        }
        try {
            return userService.findById(cbVorgang.getUserId());
        }
        catch (AKAuthenticationException e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Fehler bei der Ermittlung der User-Informationen: "
                    + e.getMessage(), e);
        }
    }

}
