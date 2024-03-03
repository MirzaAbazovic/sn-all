/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.exceptions.ExpiredServicesException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.ProcessExpiredServicesService;
import de.mnet.common.tools.DateConverterUtils;

@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.ProcessExpiredServicesService")
public class ProcessExpiredServicesServiceImpl extends DefaultCCService implements ProcessExpiredServicesService {

    private static final Logger LOGGER = Logger.getLogger(ProcessExpiredServicesServiceImpl.class);

    private static final int DAYS_TO_GO_BACK = 7;

    @Autowired
    CCLeistungsService ccLeistungsService;
    @Autowired
    BillingAuftragService billingAuftragService;
    @Autowired
    CCAuftragService ccAuftragService;
    @Autowired
    BAService baService;

    @Override
    public AKWarnings processExpiredServices(Long sessionId) {
        try {
            AKWarnings warnings = new AKWarnings();

            // AUTO_EXPIRE Leistungen ermitteln
            Set<Long> extLeistungNos = getExtLeistungNosForExpireServices();
            if (CollectionUtils.isEmpty(extLeistungNos)) {
                throw new ExpiredServicesException("Es wurden keine 'AUTO_EXPIRE' Leistungen gefunden!");
            }

            // Taifun Auftraege ermitteln
            Set<Long> auftragNoOrigs = billingAuftragService.findAuftragNoOrigsWithExtLeistungNos(
                    extLeistungNos, java.time.LocalDate.now(), DAYS_TO_GO_BACK);

            // vglDatum zusätzlich + 1 / -1 , da in Billing dao.findAuftragPos4Auftrag mit between gesucht wird ('>='  && '>=')
            // und LocalDate isAfter / isBefore nur '>' && '<' vergleicht
            final LocalDate vglDatumBis = LocalDate.now().plusDays(1);
            final LocalDate vglDatumVon = vglDatumBis.minusDays(DAYS_TO_GO_BACK + 1);

            for (Long auftragNoOrig : auftragNoOrigs) {
                try {
                    List<AuftragDaten> auftragDaten = ccAuftragService.findAuftragDaten4OrderNoOrig(auftragNoOrig);
                    List<AuftragDaten> auftragDatenInBetrieb = auftragDaten.stream()
                            .filter(ad -> (ad.isInBetrieb() || ad.isAenderung()))
                            .collect(Collectors.toList());
                    boolean hasActiveBA = auftragDaten.stream()
                            .filter(AuftragDaten::isAenderungImUmlauf)
                            .findFirst().isPresent();

                    if (auftragDatenInBetrieb.isEmpty() && !hasActiveBA) {
                        BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragNoOrig);
                        if (isBillingAuftragNotCanceled(bAuftrag, vglDatumVon, vglDatumBis)
                                && isTechnicalAuftragNotCanceled(auftragDaten, vglDatumVon, vglDatumBis)) {
                            throw new FindException(String.format(
                                    "Zum Taifun Auftrag %s konnte kein Hurrican Auftrag im Status 'in Betrieb'/'Aenderung' gefunden werden!",
                                    auftragNoOrig));
                        }
                    }
                    else if (auftragDatenInBetrieb.size() > 1 && !hasActiveBA) {
                        throw new FindException(String.format(
                                "Zum Taifun Auftrag %s konnte kein eindeutiger Hurrican Auftrag im Status 'in Betrieb'/'Aenderung' gefunden werden!",
                                auftragNoOrig));
                    }
                    else {
                        AuftragDaten toChange = auftragDatenInBetrieb.get(0);
                        if (hasLeistungsDiff(toChange)) {
                            toChange.setStatusId(AuftragStatus.AENDERUNG);
                            ccAuftragService.saveAuftragDatenNoTx(toChange);

                            // Bauauftrag zum naechsten Werktag erstellen
                            baService.createVerlaufNewTx(new CreateVerlaufParameter(
                                    toChange.getAuftragId(),
                                    Date.from(DateCalculationHelper.asWorkingDay(LocalDate.now().plusDays(1)).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                    BAVerlaufAnlass.AUTO_DOWNGRADE,
                                    null,
                                    false,
                                    sessionId,
                                    null));
                        }
                    }
                }
                catch (Exception ex) {
                    warnings.addAKWarning(this, String.format(
                            "Taifun Auftrag %s: Fehler bei der Bauauftragserstellung fuer automatischen Leistungsabgleich aufgetreten:%n%s",
                            auftragNoOrig, ExceptionUtils.getMessage(ex)));
                }
            }
            return warnings;
        }
        catch (ExpiredServicesException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExpiredServicesException(e);
        }
    }

    boolean isBillingAuftragNotCanceled(BAuftrag bAuftrag, LocalDate vglDatumVon, LocalDate vglDatumBis) {
        final LocalDate bAuftragGueltigBis = DateConverterUtils.asLocalDate(bAuftrag.getGueltigBis());
        return !(bAuftrag.isKuendigung() && bAuftragGueltigBis.isAfter(vglDatumVon) && bAuftragGueltigBis.isBefore(vglDatumBis));
    }

    boolean isTechnicalAuftragNotCanceled(List<AuftragDaten> auftragDaten, LocalDate vglDatumVon, LocalDate vglDatumBis) {
        List<AuftragDaten> auftragDatenInKuendigung = auftragDaten.stream()
                .filter(ad -> {
                    LocalDate auftragGueltigBis = DateConverterUtils.asLocalDate(ad.getGueltigBis());
                    return ((ad.isInKuendigungEx() || ad.isAuftragClosed())
                            && auftragGueltigBis.isAfter(vglDatumVon) && auftragGueltigBis.isBefore(vglDatumBis));
                })
                .collect(Collectors.toList());
        return auftragDatenInKuendigung.isEmpty();
    }

    /**
     * Ermittelt alle EXT_LEISTUNG__NOs von techn. Leistungen, die als 'AUTO_EXPIRE' markiert sind.
     * @return Set mit den EXT_LEISTUNG__NOs von AUTO_EXPIRE Leistungen
     */
    @NotNull Set<Long> getExtLeistungNosForExpireServices() throws FindException {
        List<TechLeistung> techLeistungen = ccLeistungsService.findTechLeistungen(true);
        return techLeistungen.stream()
                .filter(tl -> tl.getAutoExpire())
                .filter(tl -> tl.getExternLeistungNo() != null)
                .map(tl -> tl.getExternLeistungNo())
                .collect(Collectors.toSet());
    }


    /**
     * Ueberprueft, ob es auf dem Auftrag ueberhaupt eine Leistungs-Differenz Taifun/Hurrican gibt.
     * @param auftragDaten zu pruefender Auftrag
     * @return {@code true} wenn eine Leistungs-Differenz zwischen Taifun und Hurrican vorhanden ist
     */
    boolean hasLeistungsDiff(AuftragDaten auftragDaten) throws FindException {
        List<LeistungsDiffView> leistungsDiff = ccLeistungsService.findLeistungsDiffs(auftragDaten.getAuftragId(),
                auftragDaten.getAuftragNoOrig(), auftragDaten.getProdId());
        return CollectionUtils.isNotEmpty(leistungsDiff);
    }

}
