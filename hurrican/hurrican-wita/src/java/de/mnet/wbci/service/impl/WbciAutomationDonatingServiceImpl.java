/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.14
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;

import java.time.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciAutomationDonatingService;
import de.mnet.wbci.service.WbciAutomationTxHelperService;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;

/**
 * Service automatisiert Aufgaben eines abgebenden EKPs
 */
@CcTxRequired
public class WbciAutomationDonatingServiceImpl extends AbstractWbciCommonAutomationService
        implements WbciAutomationDonatingService {

    private static final Logger LOGGER = Logger.getLogger(WbciAutomationDonatingServiceImpl.class);

    public static final String AUFTRAG_WURDE_GEKUENDIGT = "Auftrag wurde gekündigt.";

    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    private WbciMeldungService wbciMeldungService;
    @Autowired
    private WbciElektraService wbciElektraService;
    @Autowired
    private CCAuftragStatusService auftragStatusService;
    @Autowired
    private WbciAutomationTxHelperService wbciAutomationTxHelperService;

    @Override
    public Collection<String> processAutomatableOutgoingRuemVas(AKUser user, Long sessionId) {
        LOGGER.info("Process automatable 'RUEM-VA' (M-net = donating carrier)");
        final Collection<WbciGeschaeftsfall> gfsForKuendigung = wbciGeschaeftsfallService
                .findAutomateableOutgoingRuemVaForKuendigung();

        final List<String> processedVaIds = new ArrayList<>();
        for (WbciGeschaeftsfall wbciGeschaeftsfall : gfsForKuendigung) {
            processedVaIds.add(wbciGeschaeftsfall.getVorabstimmungsId());

            if (wbciGeschaeftsfall.getWechseltermin() == null ||
                    wbciGeschaeftsfall.getWechseltermin().isBefore(LocalDate.now().plusDays(2))) {
                handleAutomationException(wbciGeschaeftsfall, AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN,
                        new RuntimeException(
                                String.format("RUEM-VA zur VA-Id %s konnte nicht verarbeitet werden, da das neue Kuendigungsdatum zu nahe liegt!",
                                        wbciGeschaeftsfall.getVorabstimmungsId())),
                        user);
            }
            else {
                RueckmeldungVorabstimmung ruemVa;
                try {
                    ruemVa = wbciCommonService
                            .findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);

                }
                catch (WbciServiceException e) {
                    ruemVa = null;
                }
                if (ruemVa != null) {
                    try {
                        Set<Long> orderNoOrigs = wbciGeschaeftsfall.getOrderNoOrigs();
                        for (Long orderNoOrig : orderNoOrigs) {
                            // Billing-Auftrag kuendigen
                            wbciElektraService.cancelBillingOrder(orderNoOrig, ruemVa);

                            // Hurrican-Auftraege ermitteln und kuendigen
                            AKWarnings warnings = auftragStatusService.cancelHurricanOrdersAndCreateBA(orderNoOrig,
                                    Date.from(wbciGeschaeftsfall.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()), user, sessionId);

                            if (warnings.isNotEmpty()) {
                                handleAutomationException(wbciGeschaeftsfall, AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN,
                                        new RuntimeException(warnings.getWarningsAsText()), user);
                            }
                        }

                        wbciGeschaeftsfallService
                                .createOrUpdateAutomationTaskNewTx(wbciGeschaeftsfall.getVorabstimmungsId(), ruemVa,
                                        AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN, COMPLETED,
                                        AUFTRAG_WURDE_GEKUENDIGT, user);
                    }
                    catch (Exception e) {
                        handleAutomationException(ruemVa, AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN, e,
                                user);
                    }
                }
                else {
                    handleAutomationException(wbciGeschaeftsfall, AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN,
                            new RuntimeException(
                                    String.format("Zur VA-Id %s konnte keine RUEM-VA ermittelt werden!",
                                            wbciGeschaeftsfall.getVorabstimmungsId())),
                            user);
                }
            }
        }
        LOGGER.info(String.format("Automatically processed %s outgoing RUEM-VAs", processedVaIds.size()));
        return processedVaIds;
    }


    @Override
    public Collection<String> processAutomatableStrAufhErlmsDonating(AKUser user, Long sessionId) {
        LOGGER.info("Process automatable 'STR-AUFH ERLM' (M-net = donating carrier)");
        final List<String> processedVaIds = new ArrayList<>();

        final Collection<ErledigtmeldungStornoAuf> strAufErlms = wbciMeldungService
                .findAutomatableStrAufhErlmsDonatingProcessing();

        for (ErledigtmeldungStornoAuf strAufErlm : strAufErlms) {
            processedVaIds.add(strAufErlm.getVorabstimmungsId());
            
            Set<Long> orderNoOrigs = strAufErlm.getWbciGeschaeftsfall().getOrderNoOrigs();
            for (Long orderNoOrig : orderNoOrigs) {
                try {
                    wbciAutomationTxHelperService.undoCancellation(strAufErlm, orderNoOrig, user, sessionId);
                }
                catch (Exception e) {
                    handleAutomationException(strAufErlm, AutomationTask.TaskName.UNDO_AUFTRAG_KUENDIGUNG, e, user);
                }
            }
        }

        LOGGER.info(String.format("Automatically processed %s STR-AUFH ERLMs (M-net = donating carrier)",
                processedVaIds.size()));
        return processedVaIds;
    }


}
