/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.2016
 */
package de.mnet.hurrican.webservice.ngn.service.impl.dn;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.startup.HurricanWebUserSession;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;

public class DNLeistungMigration {
    private static final Logger LOGGER = Logger.getLogger(DNLeistungMigration.class);

    private RufnummerService rufnummerService;
    private CCRufnummernService ccRufnummernService;

    public DNLeistungMigration(RufnummerService rufnummerService, CCRufnummernService ccRufnummernService) {
        this.rufnummerService = rufnummerService;
        this.ccRufnummernService = ccRufnummernService;
    }

    public Rufnummer getRufnummerByDnNr(Long newDnNr) throws PortierungskennungMigrationException {
        try {
            return rufnummerService.findDN(newDnNr);
        }
        catch (FindException e) {
            final String message = String.format("Fehler bei der Erfassung von Rufnummer [%d] ", newDnNr);
            LOGGER.error(message);
            throw new PortierungskennungMigrationException(message, e);
        }
    }

    public Optional<String> migrateRufnummerLeistungen(Rufnummer activeDn) throws PortierungskennungMigrationException {
        try {
            if (!BillingConstants.HIST_STATUS_AKT.equals(activeDn.getHistStatus())
                    && !BillingConstants.HIST_STATUS_NEU.equals(activeDn.getHistStatus())) {
                final String message = String.format("Die Rufnummer mit DN Nr. [%d] hat Hist. Status [%s], "
                        + "aber Hist. Status muss entweder AKT oder NEU sein", activeDn.getDnNo(), activeDn.getHistStatus());
                LOGGER.warn(message);
                return Optional.of(message);
            } else if (ccRufnummernService.hasLeistung(activeDn)) {
                String message = String.format("Die Rufnummer [%s] mit der DN[%s] fuer den Billing-Auftrag [%s] "
                                + "hat bereits Leistungen zugeordnet. Es erfolgt keine automatisierte Zuordnung",
                        activeDn.toString(), activeDn.getDnNo(), activeDn.getAuftragNoOrig());
                LOGGER.warn(message);
                return Optional.of(message);
            }

            final List<Rufnummer> dNsByDnNoOrig = rufnummerService.findDNsByDnNoOrig(activeDn.getDnNoOrig(), null);
            final Optional<Rufnummer> previousDnOpt = findDNBeforeActiveDN(dNsByDnNoOrig, activeDn);
            if (previousDnOpt.isPresent()) {
                copyLeistungenForOneDn(activeDn, previousDnOpt.get());
            } else {
                String message = String.format("Es konnte keine alte Rufnummer fuer die neue Ruffnummer DN Nr. [%d] und DN Nr. Orig. [%d] "
                        + "gefunden werden", activeDn.getDnNo(), activeDn.getOeNoOrig());
                LOGGER.warn(message);
                return Optional.of(message);
            }
            return Optional.empty();
        } catch (FindException | StoreException e) {
            final String message = String.format("Fehler beim Kopieren der Leistungen fuer Rufnummer [%d] ", activeDn.getDnNo());
            LOGGER.error(message);
            throw new PortierungskennungMigrationException(message, e);
        }
    }

    private Optional<Rufnummer> findDNBeforeActiveDN(List<Rufnummer> dns, Rufnummer activeDn) {
        final Date gueltigVonDate = activeDn.getGueltigVon();
        final LocalDate gueltigVonLocalDate = DateConverterUtils.asLocalDate(gueltigVonDate);
        final LocalDate previousDay = gueltigVonLocalDate.minusDays(1);
        return dns.stream()
                .filter(dn -> DateConverterUtils.asLocalDate(dn.getGueltigBis()).isEqual(previousDay)).findAny();
    }

    private void copyLeistungenForOneDn(Rufnummer activeDn, Rufnummer previousDn) throws FindException, StoreException {
        try {
            ccRufnummernService.copyDnLeistung(previousDn, activeDn, getSessionID());
        }
        catch (FindException | StoreException e) {
            String message =
                    String.format("Die Leistungen fuer Billing-Auftrag [%s] und DN [%s] konnte nicht gespeichert werden: Fehler '%s'",
                            activeDn.getAuftragNoOrig(), activeDn.getDnNo(), e.getMessage());
            LOGGER.error(message);
            throw e;
        }
    }

    public static Map<Long, List<Rufnummer>> groupDnNoOrig(final List<Rufnummer> rufnummers) {
        return rufnummers.stream().collect(
                Collectors.groupingBy(Rufnummer::getDnNoOrig));
    }

    public static Optional<Rufnummer> findActiveDn(final List<Rufnummer> dns) {
        return dns.stream()
                .filter(r -> BillingConstants.HIST_STATUS_AKT.equals(r.getHistStatus())
                        || BillingConstants.HIST_STATUS_NEU.equals(r.getHistStatus()))
                .findAny();
    }

    private Long getSessionID() {
        return HurricanWebUserSession.getSessionId();
    }

}

