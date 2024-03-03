/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2012 11:48:28
 */
package de.mnet.hurrican.scheduler.job.validation;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobWarningsHandler;

/**
 * Laeuft ueber die ueberwachten DSLAM-Profile und prueft diese nochmal durch:
 * <p/>
 * <ol> <li>CPS Query ausfuehren und damit die möglichen Bitraten in Up- und Downstream erhalten</li> <li>Falls die
 * Bitraten des ermittelten Profils ungleich dem aktuellen Profil sind, muss das passendste Profil geladen werden.</li>
 * <li>Das passendste Profil dem Auftrag zuordnen und an CPS ein "modifySub" versenden</li> <li>Falls Operationen
 * erfolgreich den Eintrag aus der Ueberwachung herausnehmen.</li> </ol>
 *
 *
 * @since Release 11
 */
public class CheckMonitoredDSLAMProfileJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(CheckMonitoredDSLAMProfileJob.class);

    private DSLAMProfileMonitorService dslamProfileMonitorService = null;
    private CCLeistungsService leistungsService = null;
    private DSLAMService dslamService = null;
    private CPSService cpsService = null;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Collection<Long> monitoredAuftragIds = Collections.emptyList();
        try {
            monitoredAuftragIds = findCurrentlyMonitoredAuftragIds();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }

        AKWarnings exceptionMessages = new AKWarnings();
        for (Long auftragId : monitoredAuftragIds) {
            try {
                Pair<Integer, Integer> bitrateDownAndUp = findAttainableBitrate(auftragId);
                if (bitrateDownAndUp != null) {
                    DSLAMProfile bestFittedProfile = findBestFittedProfile(auftragId, bitrateDownAndUp);
                    DSLAMProfile profile = findActiveProfile(auftragId);
                    if (hasDSLAMProfileChanged(profile, bestFittedProfile) && checkBestFittedProfileAllowed(profile, bestFittedProfile)) {
                            changeDSLAMProfile(auftragId, bestFittedProfile);
                            sendCPSModifySub(auftragId);
                    }
                    deactivateMonitoring(auftragId);
                }
                else {
                    exceptionMessages.addAKWarning(this, String.format(
                            "Das DSLAM Profil Monitoring für den Auftrag %d ist fehlgeschlagen." +
                                    " Grund: Attainable Bitrates konnten nicht ermittelt werden!", auftragId
                    ));
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                exceptionMessages.addAKWarning(this, String.format(
                        "Das DSLAM Profil Monitoring für den Auftrag %d ist fehlgeschlagen." +
                                " Grund: %s", auftragId, e.getMessage()
                ));
                new LogDBJobErrorHandler().handleError(context, e, null);
            }
        }
        if (exceptionMessages.isNotEmpty()) {
            new SendMailJobWarningsHandler().handleWarnings(context, exceptionMessages);
        }
    }

    /**
     * Prueft, ob das neue Profil die bestehende Down-/Upstream Bandbreite nicht uebersteigt
     *
     * @return false wenn das Profil nicht erlaubt ist, true wenn das Profil zum Wechseln erlaubt ist
     */
    protected boolean checkBestFittedProfileAllowed(DSLAMProfile profile, DSLAMProfile bestFittedProfile)
            throws FindException, ServiceNotFoundException {
        if (NumberTools.isGreater(bestFittedProfile.getBandwidth().getDownstream(), profile.getBandwidth().getDownstream())
                || NumberTools.isGreater(bestFittedProfile.getBandwidth().getUpstream(), profile.getBandwidth().getUpstream())) {
            return false;
        }
        return true;
    }

    /**
     * Modify Subscriber erstellen und an CPS schicken
     */
    protected void sendCPSModifySub(Long auftragId) throws ServiceNotFoundException, StoreException {
        Long sessionId = HurricanScheduler.getSessionId();
        CPSTransactionResult cpsTxResult = getCPSService().createCPSTransaction(new CreateCPSTransactionParameter(auftragId,
                null, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT, new Date(), null, null, null, null, Boolean.FALSE,
                Boolean.FALSE, sessionId));

        if ((cpsTxResult.getCpsTransactions() != null) && (cpsTxResult.getCpsTransactions().size() == 1)) {
            getCPSService().sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), sessionId);
        }

        String warnings = (cpsTxResult.getWarnings() != null) ? cpsTxResult.getWarnings().getWarningsAsText() : null;
        if (StringUtils.isNotBlank(warnings)) {
            throw new StoreException(warnings);
        }
    }

    /**
     * Ermittelt das DSLAM Profile welches am besten zum Attainable Bitrate passt
     */
    protected DSLAMProfile findBestFittedProfile(Long auftragId, Pair<Integer, Integer> bitrateDownAndUp)
            throws FindException, ServiceNotFoundException {
        return getDSLAMService().findNextHigherDSLAMProfile4DSL18000Auftrag(auftragId, bitrateDownAndUp.getSecond(),
                bitrateDownAndUp.getFirst());
    }

    /**
     * Ermittelt das aktuell aktive Profil
     */
    protected DSLAMProfile findActiveProfile(Long auftragId) throws FindException, ServiceNotFoundException {
        DSLAMProfile profile = getDSLAMService().findDSLAMProfile4Auftrag(auftragId, new Date(), false);
        return profile;
    }

    /**
     * Prüft ob das aktive DSLAM Profil geändert werden muss
     */
    protected boolean hasDSLAMProfileChanged(DSLAMProfile profile, DSLAMProfile bestFittedProfile)
            throws FindException, ServiceNotFoundException {
        if (((profile == null) && (bestFittedProfile == null))
                || ((profile != null) && (bestFittedProfile == null))
                || ((profile != null) && (bestFittedProfile != null) && NumberTools.equal(profile.getId(), bestFittedProfile.getId()))) {
            return false;
        }
        return true;
    }

    /**
     * Schreibt das uebergebene DSLAM Profile auf dem Auftrag um
     *
     * @throws FindException
     */
    protected Auftrag2DSLAMProfile changeDSLAMProfile(Long auftragId, DSLAMProfile bestFittedProfile)
            throws StoreException, ServiceNotFoundException, AKAuthenticationException {
        final String userName = getCurrentUser(HurricanScheduler.getSessionId()).getLoginName();
        return getDSLAMService().changeDSLAMProfile(auftragId, bestFittedProfile.getId(), new Date(), userName,
                DSLAMProfileChangeReason.CHANGE_REASON_ID_AUTOMATIC_SYNC, null);
    }

    /**
     * Ermittelt ueber den CPS die Attainable Bitraten
     */
    protected Pair<Integer, Integer> findAttainableBitrate(Long auftragId) throws FindException,
            ServiceNotFoundException {
        Pair<Integer, Integer> bitrateDownAndUp = getMonitorService().cpsQueryAttainableBitrate(auftragId,
                HurricanScheduler.getSessionId());
        return bitrateDownAndUp;
    }

    /**
     * Ermittelt alle derzeit ueberwachten Auftraege
     */
    protected Collection<Long> findCurrentlyMonitoredAuftragIds() throws FindException, ServiceNotFoundException {
        return getMonitorService().findCurrentlyMonitoredAuftragIds();
    }

    /**
     * Deaktiviert die Ueberwachung fuer den uebergebenen Auftrag
     */
    protected void deactivateMonitoring(Long auftragId) throws FindException, ServiceNotFoundException {
        getMonitorService().deactivateMonitoring(auftragId);
    }

    protected CCLeistungsService getLeistungsService() throws ServiceNotFoundException {
        if (leistungsService == null) {
            leistungsService = getCCService(CCLeistungsService.class);
        }
        return leistungsService;
    }

    protected CPSService getCPSService() throws ServiceNotFoundException {
        if (cpsService == null) {
            cpsService = getCCService(CPSService.class);
        }
        return cpsService;
    }

    protected DSLAMProfileMonitorService getMonitorService() throws ServiceNotFoundException {
        if (dslamProfileMonitorService == null) {
            dslamProfileMonitorService = getCCService(DSLAMProfileMonitorService.class);
        }
        return dslamProfileMonitorService;
    }

    protected DSLAMService getDSLAMService() throws ServiceNotFoundException {
        if (dslamService == null) {
            dslamService = getCCService(DSLAMService.class);
        }
        return dslamService;
    }

}
