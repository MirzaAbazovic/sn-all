/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 11.06.2014 
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.hurrican.model.cc.Feature.*;
import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;
import static de.mnet.wbci.model.AutomationTask.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Throwables;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.elektra.ElektraCancelOrderResponseDto;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.augustakom.hurrican.service.elektra.builder.ElektraResponseDtoBuilder;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.elektra.services.DialingNumberType;
import de.mnet.elektra.services.NumberPortierungType;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.DialingNumberTypeHelper;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;
import de.mnet.wbci.model.RufnummernportierungVO;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

@CcTxRequired
public class WbciElektraServiceImpl implements WbciElektraService {

    private static final Logger LOGGER = Logger.getLogger(WbciElektraServiceImpl.class);

    @Autowired
    private ElektraFacadeService elektraFacadeService;

    @Autowired
    private WbciCommonService wbciCommonService;

    @Autowired
    private FeatureService featureService;

    @Autowired
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    @Override
      public ElektraResponseDto processRuemVa(String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        if (isFeatureOnline(WBCI_RUEMVA_AUTO_PROCESSING)) {
            return changeOrderDialNumber(gf, user, TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN);
        }
        else {
            //message parameter have to be null, to ensure that the automation task can executed multiple times
            return handleFeatureNotOnline(gf, null, WBCI_RUEMVA_AUTO_PROCESSING, TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN, user);
        }
    }

    @Override
    @CcTxRequiresNew
    public ElektraResponseDto processRuemVaNewTx(String vorabstimmungsId, AKUser user) {
        return processRuemVa(vorabstimmungsId, user);
    }

    @Override
    public ElektraResponseDto processRrnp(String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        if (isFeatureOnline(WBCI_RRNP_AUTO_PROCESSING)) {
            if (GeschaeftsfallTyp.VA_RRNP.equals(gf.getTyp())) {
                WbciGeschaeftsfallRrnp rrnp = (WbciGeschaeftsfallRrnp) gf;
                VorabstimmungsAnfrage va = wbciCommonService.findWbciRequestByType(vorabstimmungsId,
                        VorabstimmungsAnfrage.class).get(0);

                LocalDate realDate = rrnp.getWechseltermin();
                LocalDate vaReceivedAt = DateConverterUtils.asLocalDate(va.getProcessedAt());

                try {
                    final ElektraResponseDto response = elektraFacadeService.portCancelledDialNumber(
                            rrnp.getRufnummernportierung().getPortierungszeitfenster().timeFrom(realDate),
                            rrnp.getRufnummernportierung().getPortierungszeitfenster().timeTo(realDate),
                            DialingNumberTypeHelper.lookupDialNumbers(rrnp.getRufnummernportierung()),
                            vaReceivedAt,
                            rrnp.getRufnummernportierung().getPortierungskennungPKIauf());
                    wbciGeschaeftsfallService.createOrUpdateAutomationTask(gf, TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN,
                            response, user);
                    return response;
                }
                catch (Exception e) {
                    //message parameter have to be null, to ensure that the automation task can executed multiple times
                    return handleCaughtException("portCancelledDialNumber", gf, null, e, TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN, user);
                }
            }
            else {
                String msg = String.format("Der Service in Elektra 'portCancelledDialNumber' kann nicht für die VA " +
                        "'%s' aufgerufen werden, da dies keine reine Rufnummernportierung ist", vorabstimmungsId);
                LOGGER.error(msg);
                throw new WbciServiceException(msg);
            }
        }
        else {
            return handleFeatureNotOnline(gf, null, WBCI_RRNP_AUTO_PROCESSING, TaskName.TAIFUN_NACH_RRNP_AKTUALISIEREN, user);
        }
    }

    @Override
    public ElektraResponseDto addDialNumber(String vorabstimmungsId, RufnummernportierungVO toAdd, AKUser user) {
        WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
            RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(vorabstimmungsId,
                    RueckmeldungVorabstimmung.class);
        
        if (isFeatureOnline(WBCI_DIAL_NUMBER_UPDATE)) {
            LocalDate realDate = (ruemVa != null) ? ruemVa.getWechseltermin() : null;
            if (realDate == null) {
                throw new WbciServiceException(
                        String.format(
                                "Wechseltermin konnte nicht ermittelt werden. Wahrscheinlich ist keine RUEM-VA zur Vorabstimmung %s vorhanden.",
                                vorabstimmungsId));
            }

            try {
                String pkiAuf = lookupPortKennungTnbAuf(gf.getAuftragId());

                final ElektraResponseDto responseDto = elektraFacadeService.addDialNumber(
                        gf.getBillingOrderNoOrig(),
                        DialingNumberTypeHelper.buildFrom(toAdd),
                        pkiAuf,
                        toAdd.getPkiAbg(),
                        realDate);
                wbciGeschaeftsfallService.createOrUpdateAutomationTask(gf, TaskName.RUFNUMMER_IN_TAIFUN_ANLEGEN, responseDto, user);
                return responseDto;
            }
            catch (Exception e) {
                return handleCaughtException("addDialNumbers", gf, ruemVa, e, TaskName.RUFNUMMER_IN_TAIFUN_ANLEGEN, user);
            }
        }
        else {
            return handleFeatureNotOnline(gf, ruemVa, WBCI_DIAL_NUMBER_UPDATE, TaskName.RUFNUMMER_IN_TAIFUN_ANLEGEN, user);
        }
    }

    @Override
    public ElektraResponseDto deleteDialNumber(String vorabstimmungsId, RufnummernportierungVO toDelete, AKUser user) {
        WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        if (isFeatureOnline(WBCI_DIAL_NUMBER_UPDATE)) {
            try {
                final ElektraResponseDto responseDto = elektraFacadeService.deleteDialNumber(
                        gf.getBillingOrderNoOrig(), DialingNumberTypeHelper.buildFrom(toDelete));
                wbciGeschaeftsfallService.createOrUpdateAutomationTask(gf, TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN, responseDto, user);
                return responseDto;
            }
            catch (Exception e) {
                //message parameter have to be null, to ensure that the automation task can executed multiple times
                return handleCaughtException("deleteDialNumbers", gf, null, e, TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN, user);
            }
        }
        else {
            //message parameter have to be null, to ensure that the automation task can executed multiple times
            return handleFeatureNotOnline(gf, null, WBCI_DIAL_NUMBER_UPDATE, TaskName.RUFNUMMER_IN_TAIFUN_ENTFERNEN, user);
        }
    }

    @Override
    public ElektraCancelOrderResponseDto cancelBillingOrder(Long orderNoOrig, RueckmeldungVorabstimmung ruemVa) {
        try {
            List<DialingNumberType> dialNumbersToPort = null;
            if (ruemVa.getRufnummernportierung() != null) {
                dialNumbersToPort = DialingNumberTypeHelper.lookupDialNumbers(ruemVa.getRufnummernportierung());
            }

            final WbciGeschaeftsfall wbciOriginalGeschaeftsfall = wbciCommonService.findOriginalWbciGeschaeftsfall(ruemVa.getVorabstimmungsId());
            final List<VorabstimmungsAnfrage> originalWbciRequests = wbciCommonService.findWbciRequestByType(
                    wbciOriginalGeschaeftsfall.getVorabstimmungsId(), VorabstimmungsAnfrage.class);
            // the list contains only one element, it's a must
            if ( originalWbciRequests.size() != 1 ) {
                throw new WbciServiceException(String.format("Service wbciCommonService.findWbciRequestByType() with parameters [%s] [%s]"
                        + " must return exactly one value, but it returned [%d] values", wbciOriginalGeschaeftsfall.getVorabstimmungsId(),
                        VorabstimmungsAnfrage.class.getName(),  originalWbciRequests.size()));
            }
            final LocalDateTime firstWbciRequestCreationDate = Instant.ofEpochMilli(originalWbciRequests.iterator().next().getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();

            final ElektraCancelOrderResponseDto responseDto = elektraFacadeService.cancelOrder(
                    orderNoOrig, ruemVa.getWechseltermin(), LocalDate.from(firstWbciRequestCreationDate), ruemVa.getEKPPartner(), dialNumbersToPort);

            String reportType = (responseDto.isReclaimPositions())
                    ? ElektraFacadeService.REPORT_TYPE_KB_MIT_RUECKFORDERUNG
                    : ElektraFacadeService.REPORT_TYPE_KB_OHNE_RUECKFORDERUNG;
            elektraFacadeService.generateAndPrintReportByType(orderNoOrig, reportType);
            
            return responseDto;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WbciServiceException(
                    String.format("Bei der Kündigung des Taifun Auftrags %s ist ein Fehler aufgetreten: %s", 
                            orderNoOrig, e.getMessage()), e);
        }
    }


    @Override
    public ElektraCancelOrderResponseDto undoCancellation(@Nonnull final Long billingOrderNo) {
        try {
            final ElektraCancelOrderResponseDto responseDto =  elektraFacadeService.undoCancellation(billingOrderNo);
            elektraFacadeService.generateAndPrintReportByType(billingOrderNo, ElektraFacadeService.REPORT_TYPE_KB_STORNO);

            return responseDto;
        }
        catch (Exception e) {
            throw new WbciServiceException(
                    String.format("die Stornierung des Taifun-Auftrags %s konnte nicht rükgänig gemacht werden", billingOrderNo), e);
        }
    }
    

    @Override
    public ElektraResponseDto processTvErlm(String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
            final ErledigtmeldungTerminverschiebung lastForVaId =
                    wbciCommonService.findLastForVaId(vorabstimmungsId, ErledigtmeldungTerminverschiebung.class);
        if (isFeatureOnline(WBCI_TVS_VA_AUTO_PROCESSING)) {
            if (lastForVaId == null) {
                throw new WbciServiceException(
                        String.format("Es konnte keine Erledigtmeldung zu einer Terminverschiebung fuer die " +
                                "Vorabstimmung '%s' gefunden werden. Deswegen kann die Aktualisierung des " +
                                "Taifun-Auftrags ueber Elektra nicht durchgefuehrt werden!", vorabstimmungsId));
            }

            if (CarrierRole.ABGEBEND.equals(CarrierRole.lookupMNetCarrierRole(lastForVaId.getWbciGeschaeftsfall()))) {
                return changeOrderCancellationDate(gf, user);
            }
            else {
                return changeOrderDialNumber(gf, user, TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN);
            }
        }
        else {
            return handleFeatureNotOnline(gf, lastForVaId, WBCI_TVS_VA_AUTO_PROCESSING, TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN, user);
        }
    }

    @Override
    public ElektraResponseDto processAkmTr(String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        if (isFeatureOnline(WBCI_AKMTR_AUTO_PROCESSING)) {
            return updatePortKennungTnb(wbciGeschaeftsfall, user);
        }
        else {
            return handleFeatureNotOnline(wbciGeschaeftsfall, null, WBCI_AKMTR_AUTO_PROCESSING, TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, user);
        }
    }

    @CcTxRequiresNew
    @Override
    public ElektraResponseDto updatePortKennungTnbTx(String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        return updatePortKennungTnb(wbciGeschaeftsfall, user);
    }

    private ElektraResponseDto updatePortKennungTnb(WbciGeschaeftsfall wbciGeschaeftsfall, AKUser user) {
        final String vorabstimmungsId = wbciGeschaeftsfall.getVorabstimmungsId();
        String pkiKennungAbg = null;

        if (GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfall.getTyp())
                || GeschaeftsfallTyp.VA_KUE_MRN.equals(wbciGeschaeftsfall.getTyp())) {
            RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(vorabstimmungsId,
                    RueckmeldungVorabstimmung.class);

            if (ruemVa != null) {
                pkiKennungAbg = lookupPortKennungTnbAbg(ruemVa.getRufnummernportierung());
            }
            else {
                throw new WbciServiceException(String.format(
                        "Es liegt keine Rueckmeldung zur Vorabstimmung %s vor", vorabstimmungsId));
            }
        }

        if (!StringUtils.hasText(pkiKennungAbg)) {
            pkiKennungAbg = lookupPortKennungTnbAuf(wbciGeschaeftsfall.getAuftragId());
        }

        UebernahmeRessourceMeldung akmTrMeldung = wbciCommonService.findLastForVaId(vorabstimmungsId,
                UebernahmeRessourceMeldung.class);
        if (akmTrMeldung == null) {
            throw new WbciServiceException(String.format(
                    "Es liegt keine UebernahmeRessourceMeldung zur Vorabstimmung %s vor", vorabstimmungsId));
        }

        if (!StringUtils.hasText(akmTrMeldung.getPortierungskennungPKIauf())) {
            throw new WbciServiceException(String.format(
                    "Es konnte keine Portierungskennung PKIauf für die Vorabstimmung %s ermittelt werden.",
                    vorabstimmungsId));
        }

        try {
            final ElektraResponseDto responseDto = elektraFacadeService.updatePortKennungTnb(new ArrayList<>(wbciGeschaeftsfall.getOrderNoOrigs()),
                    akmTrMeldung.getPortierungskennungPKIauf(), pkiKennungAbg);
            wbciGeschaeftsfallService.createOrUpdateAutomationTask(wbciGeschaeftsfall,
                    TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, responseDto, user);
            return responseDto;
        }
        catch (Exception e) {
            return handleCaughtException("updatePortKennungTnb", wbciGeschaeftsfall, akmTrMeldung, e, TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN, user);
        }
    }

    protected ElektraResponseDto changeOrderDialNumber(WbciGeschaeftsfall wbciGeschaeftsfall, AKUser user, TaskName taskName) {
        VorabstimmungsAnfrage va = wbciCommonService.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(),
                VorabstimmungsAnfrage.class).get(0);
        RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(),
                RueckmeldungVorabstimmung.class);

        LocalDate realDate = wbciGeschaeftsfall.getWechseltermin();
        LocalDate ruemVaReceivedAt = DateConverterUtils.asLocalDate(ruemVa.getProcessedAt());
        LocalDate vaOrderSentAt = DateConverterUtils.asLocalDate(va.getProcessedAt());

        NumberPortierungType numberPortierung = null;
        if (GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfall.getTyp()) || GeschaeftsfallTyp.VA_KUE_MRN.equals(wbciGeschaeftsfall.getTyp())) {
            numberPortierung = new NumberPortierungType();
            numberPortierung.setPortKennungTnbAbg(lookupPortKennungTnbAbg(ruemVa.getRufnummernportierung()));

            if (GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfall.getTyp())) {
                numberPortierung.setPortKennungTnbAuf(((WbciGeschaeftsfallRrnp) wbciGeschaeftsfall).getRufnummernportierung()
                        .getPortierungskennungPKIauf());
            }
            else {
                numberPortierung.setPortKennungTnbAuf(lookupPortKennungTnbAuf(wbciGeschaeftsfall.getAuftragId()));
            }

            numberPortierung.getDialNumbers()
                    .addAll(DialingNumberTypeHelper.lookupDialNumbers(ruemVa.getRufnummernportierung()));
        }

        try {
            final ElektraResponseDto responseDto = elektraFacadeService.changeOrderDialNumber(realDate, ruemVaReceivedAt, vaOrderSentAt,
                    new ArrayList<>(wbciGeschaeftsfall.getOrderNoOrigs()), numberPortierung);
            wbciGeschaeftsfallService.createOrUpdateAutomationTask(wbciGeschaeftsfall, taskName, responseDto, user);
            return responseDto;
        }
        catch (Exception e) {
            return handleCaughtException("changeOrderDialNumber", wbciGeschaeftsfall, ruemVa, e, taskName, user);
        }
    }

    private ElektraResponseDto changeOrderCancellationDate(WbciGeschaeftsfall wbciGeschaeftsfall, AKUser user) {
        try {
            final ElektraResponseDto responseDto = elektraFacadeService.changeOrderCancellationDate(wbciGeschaeftsfall.getOrderNoOrigs(),
                    wbciGeschaeftsfall.getWechseltermin());
            wbciGeschaeftsfallService.createOrUpdateAutomationTask(wbciGeschaeftsfall,
                    TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN, responseDto, user);
            return responseDto;
        }
        catch (Exception e) {
            //message parameter have to be null, to ensure that the automation task can executed multiple times
            return handleCaughtException("changeOrderCancellationDate", wbciGeschaeftsfall, null, e, TaskName.TAIFUN_NACH_TVS_ERLM_AKTUALISIEREN, user);
        }
    }

    private String lookupPortKennungTnbAuf(Long auftragId) {
        String pkiKennungAuf = wbciCommonService.getTnbKennung(auftragId);
        if (!StringUtils.hasText(pkiKennungAuf)) {
            throw new WbciServiceException(String.format("Es konnte keine TnbKennung zum Auftrag %s ermittelt werden",
                    auftragId));
        }
        return pkiKennungAuf;
    }

    private String lookupPortKennungTnbAbg(Rufnummernportierung rufnummernportierung) {
        // since all numbers have the same Portierungskennung, the first number (block or individual) is retrieved
        // and its Portierungskennung is returned
        if (RufnummernportierungTyp.ANLAGE.equals(rufnummernportierung.getTyp())) {
            return ((RufnummernportierungAnlage) rufnummernportierung).getRufnummernbloecke().get(0)
                    .getPortierungskennungPKIabg();
        }
        else {
            return ((RufnummernportierungEinzeln) rufnummernportierung).getRufnummernOnkz().get(0)
                    .getPortierungskennungPKIabg();
        }
    }

    protected boolean isFeatureOnline(FeatureName featureName) {
        if (featureService.isFeatureOnline(featureName)) {
            return true;
        }

        LOGGER.debug(String.format("Skipping elektra service call as feature '%s' is disabled", featureName));
        return false;
    }

    protected ElektraResponseDto handleCaughtException(String elektraService, WbciGeschaeftsfall gf, Meldung meldung,
            Exception wrappedException, TaskName taskName, AKUser user) {
        String msg = String.format("Waehrend des Aufrufs des '%s' Services in Elektra für die VA '%s' ist ein " +
                "unerwarteter Fehler aufgetreten.%n%s", elektraService, gf.getVorabstimmungsId(),
                Throwables.getStackTraceAsString(wrappedException));
        wbciGeschaeftsfallService.createOrUpdateAutomationTask(gf, meldung, taskName, AutomationStatus.ERROR, msg, user);
        LOGGER.error(msg, wrappedException);
        return new ElektraResponseDtoBuilder()
                .withStatus(ElektraResponseDto.ResponseStatus.ERROR)
                .withModifications(msg)
                .build();
    }

    protected ElektraResponseDto handleFeatureNotOnline(WbciGeschaeftsfall gf, Meldung meldung, FeatureName featureName, TaskName taskName, AKUser user) {
        final String msg = String.format("Die Aktion kann nicht durchgefuehrt werden, " +
                "weil das Feature %s nicht aktiv ist.", featureName.name());
        wbciGeschaeftsfallService.createOrUpdateAutomationTask(gf, meldung, taskName, AutomationStatus.FEATURE_IS_NOT_ENABLED, msg, user);
        return new ElektraResponseDtoBuilder()
                .withStatus(ElektraResponseDto.ResponseStatus.ERROR)
                .withModifications(msg)
                .build();
    }

}
