/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.service.impl;

import static de.mnet.hurrican.webservice.ngn.model.PortierungStatusEnum.*;
import static org.apache.commons.collections.CollectionUtils.*;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.hurrican.webservice.ngn.model.PortierungRequest;
import de.mnet.hurrican.webservice.ngn.model.PortierungResponse;
import de.mnet.hurrican.webservice.ngn.model.PortierungResult;
import de.mnet.hurrican.webservice.ngn.model.PortierungStatusEnum;
import de.mnet.hurrican.webservice.ngn.model.PortierungWarning;
import de.mnet.hurrican.webservice.ngn.model.ValidationRequest;
import de.mnet.hurrican.webservice.ngn.model.ValidationResponse;
import de.mnet.hurrican.webservice.ngn.model.ValidationStatus;
import de.mnet.hurrican.webservice.ngn.model.ValidationStatusEnum;
import de.mnet.hurrican.webservice.ngn.service.PortierungService;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;
import de.mnet.hurrican.webservice.ngn.service.impl.dn.DNLeistungMigration;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.model.WitaCBVorgang;

/**
 */
@Service
public class PortierungServiceImpl implements PortierungService {
    private static final Logger LOGGER = Logger.getLogger(PortierungServiceImpl.class);

    protected FeatureService featureService;
    protected PortierungHelperService portierungHelperService;
    private PortierungskennungMigrationService portierungskennungMigrationService;
    protected DNLeistungMigration dnLeistungMigration;

    public PortierungServiceImpl() {
    }

    @Autowired
    public PortierungServiceImpl(FeatureService featureService, PortierungHelperService portierungHelperService,
            PortierungskennungMigrationService portierungskennungMigrationService, DNLeistungMigration dnLeistungMigration) {
        this.featureService = featureService;
        this.portierungHelperService = portierungHelperService;
        this.portierungskennungMigrationService = portierungskennungMigrationService;
        this.dnLeistungMigration = dnLeistungMigration;
    }

    @Override
    public boolean isFeatureFlagActive() {
        return featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERING_WEB_SERVICE);
    }

    @Override
    public PortierungResponse migratePortierungskennung(PortierungRequest portierungRequest) throws PortierungskennungMigrationException {
        final PortierungResult portierungResult = new PortierungResult();

        try {
            for (Long orderNoOrig : portierungRequest.getBillingOrderNumbers()) {
                final List<String> errorMessages = new ArrayList<>();
                final List<String> warnings = new ArrayList<>();
                checkForInactiveOrders(orderNoOrig, errorMessages, warnings);
                if (isNotEmpty(errorMessages)) {
                    // has errors
                    portierungResult.putStatus(orderNoOrig, ERROR, getErrorMessagesToString(errorMessages));
                } else {
                    // does not have any error yet
                    final Set<String> portKennungenBilling = portierungHelperService.findPortierungsKennungBilling(orderNoOrig);
                    int numOfPortKennungenBilling = portKennungenBilling.size();
                    if (numOfPortKennungenBilling != 1) {
                        String allPortKennungen = portKennungenBilling.stream().collect(Collectors.joining(","));
                        String message = "Bei den Rufnummern ist keine eindeutige Portierungkennung hinterlegt. "
                                + "Anzahl der Portierungskennungen = " + numOfPortKennungenBilling
                                + ". PortierungKennungen = " + allPortKennungen;
                        errorMessages.add(message);
                        portierungResult.putStatus(orderNoOrig, ERROR, getErrorMessagesToString(errorMessages));
                        LOGGER.error(message);
                    }
                    else {
                        checkWbciGeschaeftsfaelleWarnings(orderNoOrig, portKennungenBilling, warnings);

                        if (isNotEmpty(warnings)) {
                            // has warnings
                            final PortierungWarning hint = new PortierungWarning(getErrorMessagesToString(warnings));
                            portierungResult.addWarning(orderNoOrig, hint);
                        }

                        performOrderMigration(orderNoOrig, portierungResult);
                    }
                }

            }
            return new PortierungResponse(portierungResult);
        }
        catch (Exception e) {
            throw new PortierungskennungMigrationException(e.getMessage(), e);
        }
    }

    private void checkWbciGeschaeftsfaelleWarnings(Long orderNoOrig, Set<String> portKennungenBilling, List<String> warnings) {
        final List<WbciGeschaeftsfall> wbciGeschaeftsfaelle =
                portierungHelperService.findCompleteWbciGeschaeftsfaelle(orderNoOrig);
        if (!wbciGeschaeftsfaelle.isEmpty()) {
            List<String> vorabstimmungId = new ArrayList<>();
            wbciGeschaeftsfaelle.stream()
                    .filter(wbciGeschaeftsfall -> portierungHelperService.findWitaCbVorgaenge(wbciGeschaeftsfall).isEmpty())
                    .forEach(wbciGeschaeftsfall -> {
                        if (!portKennungenBilling.isEmpty()
                                && !portKennungenBilling.contains(portierungHelperService.findPortierungsKennungVa(wbciGeschaeftsfall))) {
                            vorabstimmungId.add(wbciGeschaeftsfall.getVorabstimmungsId());
                        }
                    });
            final String msg = String.format("zur abgeschlossenen VorabstimmungId: %s,"
                            + " fehlt die WITA TAL Bestellung und PK weicht zur VA ab -> Neue Vorabstimmung nötig",
                    vorabstimmungId.stream().collect(Collectors.toList()));
            LOGGER.info(msg);
            warnings.add(msg);
        }
    }

    private void checkForInactiveOrders(Long orderNoOrig, List<String> errorMessages, List<String> warnings) {
        final List<AuftragDaten> auftragDaten = portierungHelperService.findAuftragDaten(orderNoOrig);
        if (CollectionUtils.isEmpty(auftragDaten)) {
            errorMessages.add(String.format("Es wurde kein aktiver technischer Auftrag zu Billing-Auftrag %s gefunden.", orderNoOrig));
        }
        else if (portierungHelperService.hasInactiveAuftraege(auftragDaten)) {
            String auftragDatenString = auftragDaten.stream()
                    .map(a -> a.getAuftragId().toString() + "-" + a.getAuftragNoOrig().toString())
                    .collect(Collectors.joining(", "));
            final String msg = String.format("Inaktive technische Aufträge vorhanden: [%s]", auftragDatenString);
            LOGGER.info(msg);
            warnings.add(msg);
        }
    }

    private void performOrderMigration(Long orderNoOrig, PortierungResult res) {
        try {
            final List<AuftragDaten> auftraegeForMigration = portierungskennungMigrationService.findAuftraegeForMigration(orderNoOrig);
            portierungskennungMigrationService.executeWitaGfMigration(auftraegeForMigration);
            // add cps here upon needed: warnings = portierungskennungMigrationService#executeCpsProvisonierung(List)

//            if (isNotEmpty(warnings))
//            {
//                final PortierungWarning hint = new PortierungWarning(getErrorMessagesToString(warnings));
//                res.addWarning(orderNoOrig, hint);
//            }
            res.putStatus(orderNoOrig, PortierungStatusEnum.SUCCESSFUL, null);
        }
        catch (PortierungskennungMigrationException e) {
            final String msg = String.format(
                    "Der Billing-Auftrag [%s] wurde nicht migriert aufgrund folgender Fehlermeldung [%s]",
                    orderNoOrig, e.getMessage());
            LOGGER.warn(msg);
            res.putStatus(orderNoOrig, ERROR, msg);
        }
    }

    @Override
    public PortierungResult performPhoneNumbersMigration(List<Long> dnNumbers) {
        final PortierungResult res = new PortierungResult();
        if (CollectionUtils.isNotEmpty(dnNumbers)) {
            final List<Rufnummer> processedRufnummers = new ArrayList<>();
            // perform phone numbers migration
            for (Long newDnNr : dnNumbers) {
                try {
                    final Rufnummer newDn = dnLeistungMigration.getRufnummerByDnNr(newDnNr);
                    if (newDn != null) {
                        final Optional<String> warningOpt = dnLeistungMigration.migrateRufnummerLeistungen(newDn);
                        if (warningOpt.isPresent()) {
                            res.addWarning(newDnNr, new PortierungWarning(warningOpt.get()));
                        }
                        processedRufnummers.add(newDn);
                    } else {
                        final String message = String.format("Keine Rufnummer gefunden fuer DN Nr. [%d] gefunden", newDnNr);
                        LOGGER.warn(message);
                        res.putStatus(newDnNr, ERROR, message);
                    }
                }
                catch (PortierungskennungMigrationException e) {
                    final String msg = String.format(
                            "Der Rufnummer [%d] wurde nicht migriert aufgrund folgender Fehlermeldung [%s]", newDnNr, e.getMessage());
                    LOGGER.warn(msg);
                    res.putStatus(newDnNr, ERROR, msg);
                }
            }
            // cps provisonierung
            final Set<Long> cpsProcessedOrders = new HashSet<>();
            for (Rufnummer dn : processedRufnummers) {
                final Long orderNoOrig = dn.getAuftragNoOrig();
                if (!cpsProcessedOrders.contains(orderNoOrig)) {
                    try {
                        final List<AuftragDaten> auftraegeForMigration = portierungskennungMigrationService.findAuftraegeForMigration(orderNoOrig);
                        if (CollectionUtils.isEmpty(auftraegeForMigration)) {
                            final String msg = String.format("Es wurde kein aktiver technischer Auftrag zu "
                                    + "Billing-Auftrag [%d] und Rufnummer [%d] gefunden. Es erfolgt keine CPS Provisionierung.", orderNoOrig, dn.getDnNo());
                            LOGGER.warn(msg);
                            res.addWarning(dn.getDnNo(), new PortierungWarning(msg));
                        } else {
                            final List<String> cpsWarnings = portierungskennungMigrationService.executeCpsProvisonierung(auftraegeForMigration);
                            if (CollectionUtils.isNotEmpty(cpsWarnings)) {
                                cpsWarnings.stream().forEach(w -> res.addWarning(dn.getDnNo(), new PortierungWarning(w)));
                            }
                            cpsProcessedOrders.add(orderNoOrig);
                        }
                    }
                    catch (PortierungskennungMigrationException e) {
                        final String msg = String.format("Der Rufnummer [%d] wurde nicht migriert aufgrund folgender "
                                + "Fehlermeldung [%s] bei CPS Provisonierung von Auftrag [%d] ",
                                dn.getDnNo(), e.getMessage(), orderNoOrig);
                        LOGGER.warn(msg);
                        res.putStatus(dn.getDnNo(), ERROR, msg);
                    }
                }
                // auftrag provisoniert, rufnummer migriert
                res.putStatus(dn.getDnNo(), PortierungStatusEnum.SUCCESSFUL, null);
            }
        }
        return res;
    }

    @Override
    public ValidationResponse validatePortierungskennung(ValidationRequest validationRequest) throws PortierungskennungMigrationException {
        final Long orderNoOrig = validationRequest.getBillingOrderNumber();
        final List<String> errorMessages = new ArrayList<>();

        try {

            final List<AuftragDaten> auftragDaten = portierungHelperService.findAuftragDaten(orderNoOrig);
            if (!portierungHelperService.hasActiveAuftraege(auftragDaten)) {
                final String msg = String.format("Es wurde kein aktiver technischer Auftrag zu Billing-Auftrag %s gefunden.", orderNoOrig);
                LOGGER.info(msg);
                errorMessages.add(msg);
            }

            if (errorMessages.isEmpty()) {  // does not have previous error
                final Set<String> portKennungenBilling = portierungHelperService.findPortierungsKennungBilling(orderNoOrig);
                if (portKennungenBilling.isEmpty() || portKennungenBilling.size() > 1) {
                    String pkError = portKennungenBilling.isEmpty() ? "keine" : "keine eindeutige";
                    final String msg = String.format("Zu Billing-Auftrag %s ist bei den Rufnummern %s"
                            + " Portierungskennung hinterlegt. Bitte korrigieren.", orderNoOrig, pkError);
                    LOGGER.info(msg);
                    errorMessages.add(msg);
                }

                if (errorMessages.isEmpty()) {  // does not have previous error
                    final List<WbciGeschaeftsfall> activeWbciGfs = portierungHelperService.findActiveWbciGeschaeftsfaelle(orderNoOrig);
                    if (!activeWbciGfs.isEmpty()) {
                        final String ids = activeWbciGfs.stream().map(gf -> gf.getId().toString()).collect(Collectors.joining(", "));
                        final String msg = String.format("Zu Billing-Auftrag %s wurden noch aktive WBCI-Geschäftsfälle gefunden: %s", orderNoOrig, ids);
                        LOGGER.info(msg);
                        errorMessages.add(msg);
                    }

                    final List<WitaCBVorgang> activeWitaVorgaenge = portierungHelperService.findNonClosedWitaCbVorgaenge(auftragDaten);
                    if (!activeWitaVorgaenge.isEmpty()) {
                        final String ids = activeWitaVorgaenge.stream().map(vorgang -> vorgang.getId().toString()).collect(Collectors.joining(", "));
                        final String msg = String.format("Zu Billing-Auftrag %s wurden noch aktive WITA-Vorgänge gefunden: %s", orderNoOrig, ids);
                        LOGGER.info(msg);
                        errorMessages.add(msg);
                    }
                }
            }

        }
        catch (Exception ex) {
            throw new PortierungskennungMigrationException(ex.getMessage(), ex);
        }

        ValidationStatus status;
        if (errorMessages.isEmpty()) {
            status = validationStatus(ValidationStatusEnum.MIGRATION_POSSIBLE, "");
        }
        else {
            status = validationStatus(ValidationStatusEnum.MIGRATION_NOT_POSSIBLE, getErrorMessagesToString(errorMessages));
        }

        return new ValidationResponse(orderNoOrig, status);

    }

    private String getErrorMessagesToString(List<String> errorMessages) {
        return errorMessages.stream().map(msg -> "- " + msg + System.lineSeparator()).collect(Collectors.joining(""));
    }

    private ValidationStatus validationStatus(ValidationStatusEnum statusEnum, String msg) {
        ValidationStatusEnum validationStatusEnum = ValidationStatusEnum.fromValue(statusEnum.value());
        return new ValidationStatus(validationStatusEnum, msg);
    }

}

