package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnEnum;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceException;

/**
 *  EVN (Einwahlkennung DialIn Einzelverbindungsnachweise) service
 *
 */
public interface EvnService extends ICCService {

    EvnEnum getEvnData(AuftragDaten auftragDaten, IntAccount account);

    List<IntAccount> getIntAccounts4Auftrag(Long auftragId);

    void activateEvn(String radiusAccountId, Long sessionId) throws EvnServiceException;

    void deactivateEvn(String radiusAccountId, Long sessionId) throws EvnServiceException;

    /**
     * CPS provisonierung callback method, is triggered when asynchronous cps provisonierung is completed
     *
     * @param auftragId
     */
    void completeEvnChange(Long auftragId);

    boolean hasActiveCpsTransaction(AuftragDaten auftragDaten);

    boolean verifyCpsTransactionsForProvisionierung(AuftragDaten auftragDaten);

    void doCpsProvisionierung(AuftragDaten auftragDaten, Long sessionId, boolean isSync) throws EvnServiceException;

    AuftragDaten getAuftragByRadiusAccount(String radiusAccount) throws EvnServiceException;
}