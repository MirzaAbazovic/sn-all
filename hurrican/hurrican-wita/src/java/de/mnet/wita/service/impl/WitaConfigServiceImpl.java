/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 10:18:46
 */
package de.mnet.wita.service.impl;

import static de.mnet.wita.message.auftrag.Kundenwunschtermin.*;

import java.time.*;
import java.util.*;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.exmodules.tal.IBoxedWitaConfigService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.dao.WitaConfigDao;
import de.mnet.wita.exceptions.WitaConfigException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.SendAllowed;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaConfig;
import de.mnet.wita.model.WitaSendCount;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.WitaConfigService;

@CcTxRequired
public class WitaConfigServiceImpl implements WitaConfigService, IBoxedWitaConfigService {

    private static final Logger LOGGER = Logger.getLogger(WitaConfigServiceImpl.class);

    @Autowired
    private WitaConfigDao witaConfigDao;

    @Autowired
    private HVTService hvtService;

    @Override
    public WitaSendLimit findWitaSendLimit(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode) {
        return witaConfigDao.findWitaSendLimit(geschaeftsfallTyp, kollokationsTyp, ituCarrierCode);
    }

    @Override
    public WitaSendLimit findWitaSendLimit(GeschaeftsfallTyp geschaeftsfallTyp, Long hvtStandortId) {
        try {
            final HVTStandort standort = hvtService.findHVTStandort(hvtStandortId);
            KollokationsTyp kollokationsTyp =
                    (standort != null && standort.getStandortTypRefId().equals(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ))
                            ? KollokationsTyp.FTTC_KVZ
                            : KollokationsTyp.HVT;

            return findWitaSendLimit(geschaeftsfallTyp.name(), kollokationsTyp, null);
        }
        catch (Exception e) {
            throw new WitaConfigException("error during determination of wita send limit", e);
        }
    }

    @Override
    public boolean fttcKvzBereitstellungAllowed() {
        WitaSendLimit kvzWitaSendLimit = findWitaSendLimit(GeschaeftsfallTyp.BEREITSTELLUNG.name(), KollokationsTyp.FTTC_KVZ, null);
        return (kvzWitaSendLimit != null) ? kvzWitaSendLimit.getAllowed() : false;
    }

    @Override
    public void saveWitaSendLimit(WitaSendLimit toSave) {
        witaConfigDao.store(toSave);
    }

    @Override
    public Long getWitaSentCount(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode) {
        return witaConfigDao.getWitaSentCount(geschaeftsfallTyp, kollokationsTyp, ituCarrierCode);
    }

    @Override
    public boolean isSendAllowed(MnetWitaRequest request) {
        return checkSendAllowed(request) == SendAllowed.OK;
    }

    @Override
    public SendAllowed checkSendAllowed(MnetWitaRequest request) {
        GeschaeftsfallTyp geschaeftsfallTyp = request.getGeschaeftsfall().getGeschaeftsfallTyp();
        Kundenwunschtermin kwt = request.getGeschaeftsfall().getKundenwunschtermin();

        if (!request.isStorno() && !request.isTv() && !checkSendLimit(request)) {
            LOGGER.debug(String.format("Send not allowed. Reason: %s. Request: %s", SendAllowed.SENDE_LIMIT, request));
            return SendAllowed.SENDE_LIMIT;
        }
        else if (!request.isStorno() && !request.isTv() && !checkRequestDelay(geschaeftsfallTyp, kwt)) {
            LOGGER.debug(String
                    .format("Send not allowed. Reason: %s. Request: %s", SendAllowed.KWT_IN_ZUKUNFT, request));
            return SendAllowed.KWT_IN_ZUKUNFT;
        }
        else if (!checkRequestOnHold(request)) {
            LOGGER.debug(String.format("Send not allowed. Reason: %s. Request: %s", SendAllowed.REQUEST_VORGEHALTEN,
                    request));
            return SendAllowed.REQUEST_VORGEHALTEN;
        }
        else if (!checkEarliestSendDate(request)) {
            LOGGER.debug(String.format("Send not allowed. Reason: %s. Request: %s", SendAllowed.EARLIEST_SEND_DATE_IN_FUTURE,
                    request));
            return SendAllowed.EARLIEST_SEND_DATE_IN_FUTURE;
        }
        LOGGER.debug(String.format("Send allowed. Request: %s", request));
        return SendAllowed.OK;
    }

    /**
     * Checks that it is not <b>too early</b> to send request.
     *
     * @param request
     * @return true if it is not too early to send.
     */
    private boolean checkEarliestSendDate(MnetWitaRequest request) {
        LocalDateTime earliestSendDate = request.getEarliestSendDate() != null ? Instant.ofEpochMilli(request.getEarliestSendDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        return earliestSendDate == null || earliestSendDate.isBefore(LocalDateTime.now());
    }

    protected boolean checkRequestOnHold(MnetWitaRequest request) {
        boolean isSendAllowed = true;
        String temp = witaConfigDao.getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD);
        int requestsOnHoldInMinutes = Integer.parseInt(temp);
        if (requestsOnHoldInMinutes > 0) {
            isSendAllowed = Instant.ofEpochMilli(request.getMwfCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(requestsOnHoldInMinutes).isBefore(LocalDateTime.now());
        }
        return isSendAllowed;
    }

    public boolean checkRequestDelay(GeschaeftsfallTyp geschaeftsfallTyp, Kundenwunschtermin kwt) {
        int daysBeforeRequestWillBeSent = getCountOfDaysBeforeSent(geschaeftsfallTyp);
        LocalDate today = LocalDate.now();
        return kwt == null || today.isAfter(kwt.getDatum().minusDays(daysBeforeRequestWillBeSent));
    }

    @Override
    public int getCountOfDaysBeforeSent(GeschaeftsfallTyp geschaeftsfallTyp) {
        String daysBeforeSentForGfKey = WitaConfig.daysBeforeSentFor(geschaeftsfallTyp);
        Integer result = getCountOfDaysBeforeSentInt(daysBeforeSentForGfKey);
        if (result == null) {
            result = getCountOfDaysBeforeSentInt(WitaConfig.DAYS_BEFORE_SENT);
        }

        if (result == null) {
            LOGGER.warn(String.format("Could not find/parse wita config %s and %s. Returning Default: %s",
                    daysBeforeSentForGfKey, WitaConfig.DAYS_BEFORE_SENT,
                    WitaConfigService.DEFAULT_COUNT_DAYS_BEFORE_SENT));
            result = DEFAULT_COUNT_DAYS_BEFORE_SENT;
        }
        return result;
    }

    private Integer getCountOfDaysBeforeSentInt(String daysBeforeSentKey) {
        try {
            String daysBeforeSentString = witaConfigDao.getWitaConfigValue(daysBeforeSentKey);
            if (NumberUtils.isNumber(daysBeforeSentString)) {
                int daysBeforeSent = Integer.parseInt(daysBeforeSentString);
                if (daysBeforeSent > 0) {
                    return daysBeforeSent;
                }
            }
        }
        catch (WitaConfigException e) {
            // non existing wita config entry - do not raise error as entry is optional
            LOGGER.info("WitaConfig Eintrag konnte nicht gefunden werden. " + e.getMessage());
        }

        return null;
    }

    boolean checkSendLimit(MnetWitaRequest request) {
        boolean sendLimitIsOk = true;
        WitaSendLimit sendLimit = findWitaSendLimit(request.getGeschaeftsfall().getGeschaeftsfallTyp().name(),
                KollokationsTyp.getKollokationsTypForRequest(request), null);
        if ((sendLimit != null) && !sendLimit.isLimitInfinite()) {
            // sendLimit gg. protokolliertem Count pruefen!
            Long existingCount = getWitaSentCount(sendLimit.getGeschaeftsfallTyp(), sendLimit.getKollokationsTyp(), sendLimit.getItuCarrierCode());
            sendLimitIsOk = (existingCount == null) || NumberTools.isLess(existingCount, sendLimit.getWitaSendLimit());
        }
        return sendLimitIsOk;
    }

    @Override
    public void createSendLog(MnetWitaRequest request, LocalDateTime sentAt) {
        try {
            GeschaeftsfallTyp geschaeftsfallTyp = request.getGeschaeftsfall().getGeschaeftsfallTyp();
            KollokationsTyp kollokationsTyp = KollokationsTyp.getKollokationsTypForRequest(request);
            WitaSendLimit sendLimit = findWitaSendLimit(geschaeftsfallTyp.name(), kollokationsTyp, null);
            if (isSendLogNecessary(sendLimit)) {
                WitaSendCount count = new WitaSendCount(geschaeftsfallTyp.name(), kollokationsTyp, sentAt.toLocalDate());
                witaConfigDao.store(count);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void createSendLog(WbciRequest request) {
        try {
            final WbciGeschaeftsfall wbciGeschaeftsfall = request.getWbciGeschaeftsfall();
            WitaSendCount count = new WitaSendCount(wbciGeschaeftsfall.getTyp().name(),
                    wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode(), DateConverterUtils.asLocalDate(request.getProcessedAt()));
            witaConfigDao.store(count);
        }
        catch (Exception e) {
            LOGGER.error(String.format("Error occurred during creating a SendLog entry for wbci request '%s'", request), e);
        }
    }

    /**
     * Prueft, ob ein Send-Log auf Grund der aktuellen Konfiguration notwendig ist. Dies ist dann der Fall, wenn ein
     * {@link WitaSendLimit} mit Angabe einer Einschraenkung (also Limit <> WitaSendLimit#INFINITE_LIMIT) konfiguriert
     * ist.
     */
    boolean isSendLogNecessary(WitaSendLimit sendLimit) {
        return ((sendLimit != null) && !sendLimit.isLimitInfinite());
    }

    @Override
    public void switchWitaVersion(WitaCdmVersion witaCdmVersionToUse) {
        WitaConfig witaConfig = witaConfigDao.getWitaDefaultVersion();
        witaConfig.setValue(witaCdmVersionToUse.getVersion());
        witaConfigDao.store(witaConfig);
    }

    @Override
    public String getConfigValue(String key) {
        return witaConfigDao.getWitaConfigValue(key);
    }

    @Override
    public void setConfigValue(String key, String value) {
        WitaConfig witaConfig = witaConfigDao.findWitaConfig(key);
        witaConfig.setValue(value);
        witaConfigDao.store(witaConfig);
    }

    @Override
    public int getWbciRuemVaPortingDateDifference() {
        return getConfigValueAsInt(WitaConfig.WBCI_RUEMVA_PORTING_DATE_DIFFERENCE);
    }

    @Override
    public int getWbciTvFristEingehend() {
        return getConfigValueAsInt(WitaConfig.WBCI_TV_FRIST_EINGEHEND);
    }

    @Override
    public int getWbciStornoFristEingehend() {
        return getConfigValueAsInt(WitaConfig.WBCI_STORNO_FRIST_EINGEHEND);
    }

    @Override
    public int getWbciWitaKuendigungsOffset() {
        return getConfigValueAsInt(WitaConfig.WBCI_WITA_KUENDIGUNG_OFFSET);
    }

    @Override
    public int getWbciMinutesWhileRequestIsOnHold() {
        return getConfigValueAsInt(WitaConfig.WBCI_MINUTES_WHILE_REQUESTS_ON_HOLD);
    }

    private int getConfigValueAsInt(String key) {
        final String configValue = getConfigValue(key);
        try {
            return Integer.parseInt(configValue);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalStateException(String.format("Der Wert '%s' fuer den Konfigurationsparameter '%s' muss" +
                    "nummerisch sein!", configValue, key), nfe);
        }
    }

    @Override
    public String getMinutesWhileRequestIsOnHold() {
        return witaConfigDao.getWitaConfigValue(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD);
    }

    @Override
    public void saveMinutesWhileRequestIsOnHold(String minutes) {
        WitaConfig witaConfig = witaConfigDao.findWitaConfig(WitaConfig.MINUTES_WHILE_REQUESTS_ON_HOLD);
        witaConfig.setValue(minutes);
        witaConfigDao.store(witaConfig);
    }

    @Override
    public WitaCdmVersion getDefaultWitaVersion() {
        return WitaCdmVersion.getCdmVersion(lookupInterfaceVersion(WitaConfig.DEFAULT_WITA_VERSION));
    }

    @Override
    public WbciCdmVersion getWbciCdmVersion(CarrierCode carrierCode) {
        if (carrierCode == null) {
            throw new WitaConfigException("To determine the CDM version the carrier has to be not null.");
        }
        String interfaceKey = String.format(WitaConfig.DEFAULT_WBCI_CDM_VERSION, carrierCode.name());
        return WbciCdmVersion.getCdmVersion(lookupInterfaceVersion(interfaceKey));
    }

    String lookupInterfaceVersion(String interfaceKey) {
        String version = witaConfigDao.getWitaConfigValue(interfaceKey);
        if (StringUtils.isEmpty(version)) {
            WitaConfigException witaConfigException = new WitaConfigException(
                    "Die WITA/WBCI Version ist nicht konfiguriert. Die Version muss in der Config-Tabelle mit "
                            + interfaceKey + " als key eingetragen werden."
            );
            LOGGER.error(witaConfigException.getMessage(), witaConfigException);
            throw witaConfigException;
        }
        return version;
    }


    @Override
    public void resetWitaSentCount(GeschaeftsfallTyp geschaeftsfallTyp, KollokationsTyp kollokationsTyp) {
        witaConfigDao.resetWitaSentCount(geschaeftsfallTyp, kollokationsTyp);
    }

    @Override
    public List<Zeitfenster> getPossibleZeitfenster(Long cbVorgangTyp, Long vorabstimmungCarrier) {
        List<Zeitfenster> result = new ArrayList<>();
        GeschaeftsfallTyp[] gfTypen = WitaCBVorgang.transformCbVorgangToGeschaeftsfallTyp(cbVorgangTyp, vorabstimmungCarrier);
        for (Zeitfenster zf : Zeitfenster.values()) {
            if (zf.isPossibleForAtLeastOneOf(gfTypen)) {
                result.add(zf);
            }
        }

        return result;
    }

    @Override
    public int getWbciRequestedAndConfirmedDateOffset() {
        return getConfigValueAsInt(WitaConfig.WBCI_REQUESTED_AND_CONFIRMED_DATE_OFFSET);
    }
}
