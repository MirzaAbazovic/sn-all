/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.12.14
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;

import java.util.*;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.service.WbciAutomationTxHelperService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * @see WbciAutomationTxHelperService
 */
public class WbciAutomationTxHelperServiceImpl implements WbciAutomationTxHelperService {

    @Autowired
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    private WbciElektraService wbciElektraService;
    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Autowired
    private CarrierService carrierService;

    
    @Override
    @CcTxRequiresNew
    public void undoCancellation(ErledigtmeldungStornoAuf strAufErlm, Long orderNoOrig, AKUser user, Long sessionId) {
        // wg. TX-Klammer zuerst Kuendigung in Hurrican rueckgaengig machen und erst wenn dort alles ok,
        // auch die Kuendigung in Taifun (ueber Elektra) zurueck nehmen
        undoCancellationOfHurricanOrders(orderNoOrig, strAufErlm, user, sessionId);

        wbciElektraService.undoCancellation(orderNoOrig);

        wbciGeschaeftsfallService
                .createOrUpdateAutomationTaskNewTx(strAufErlm.getVorabstimmungsId(), strAufErlm,
                        AutomationTask.TaskName.UNDO_AUFTRAG_KUENDIGUNG, COMPLETED, null, user);
    }


    void undoCancellationOfHurricanOrders(Long orderNoOrig, ErledigtmeldungStornoAuf strAufErlm,
            AKUser user, Long sessionId) {

        try {
            List<AuftragDaten> auftragDaten = auftragService.findAuftragDaten4OrderNoOrig(orderNoOrig);
            Collection<AuftragDaten> inKuendigung = Collections2.filter(auftragDaten, AuftragDaten::isInKuendigungEx);
            if (CollectionUtils.isNotEmpty(inKuendigung)) {
                for (AuftragDaten toActivate : inKuendigung) {
                    RevokeTerminationModel revokeTerminationModel = new RevokeTerminationModel();
                    revokeTerminationModel.setAuftragId(toActivate.getAuftragId());
                    revokeTerminationModel.setIsAuftragStatus(Boolean.TRUE);
                    revokeTerminationModel.setIsRangierung(Boolean.TRUE);
                    revokeTerminationModel.setIsTechLeistungen(Boolean.TRUE);
                    revokeTerminationModel.setAuftragsArtId(BAVerlaufAnlass.NEUSCHALTUNG);
                    revokeTerminationModel.setSessionId(sessionId);

                    auftragService.revokeTermination(revokeTerminationModel);

                    WitaCBVorgang witaCBVorgang = doWitaStorno(strAufErlm, toActivate.getAuftragId(), user);

                    // HUR-23891: M-Net ist abgebender Carrier, automatisch gesetzte KuendigungAnCarrier muss jetzt
                    // natuerlich wieder geloescht werden.
                    if (witaCBVorgang != null && witaCBVorgang.getCbId() != null) {
                        Carrierbestellung carrierbestellung = carrierService.findCB(witaCBVorgang.getCbId());
                        if (carrierbestellung != null) {
                            carrierbestellung.setKuendigungAnCarrier(null);
                            carrierService.saveCB(carrierbestellung);
                        }
                    }
                }
            }
        }
        catch (WbciServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format(
                    "Error during undo cancellation of hurrican order(s) for billing order %s", orderNoOrig), e);
        }
    }


    WitaCBVorgang doWitaStorno(ErledigtmeldungStornoAuf strAufErlm, Long auftragId, AKUser user) {
        WitaCBVorgang witaCBVorgang = 
                wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(auftragId, strAufErlm.getVorabstimmungsId());
        
        // WITA Vorgang muss nicht unbedingt existieren, da STR-AUFH auch auf Auftraegen mit M-net Technologie
        // automatisch prozessiert werden soll
        if (witaCBVorgang != null && witaCBVorgang.isKuendigung() && !witaCBVorgang.isStorno()) {
            return wbciWitaServiceFacade.doWitaStorno(strAufErlm, witaCBVorgang.getId(), user);
        }
        return witaCBVorgang;
    }
    
}
