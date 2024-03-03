/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.service;

import java.time.*;
import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wita.service.WitaConfigService;

/**
 * WBCI Service for encapsulating common behavior associated with the WBCI Geschaeftsfall.
 */
public interface WbciGeschaeftsfallService extends WbciService {

    static final String REASON_WITA_CB_VORGANG_PRESENT = "Zu der Vorabstimmung '%s' existieren bereits " +
            "ein oder mehrere WITA-Vorgänge mit folgenden Nummern: %s.";

    /**
     * Closes the Geschaeftsfall, setting the status to closed for the supplied {@code id}. <br/> No further checks /
     * validations are done!
     *
     * @param geschaeftsfallId the primary key of the Geschaeftsfall
     */
    void closeGeschaeftsfall(Long geschaeftsfallId);

    /**
     * Sets the linked geschaeftsfall status from {@link WbciGeschaeftsfallStatus#NEW_VA} to {@link
     * WbciGeschaeftsfallStatus#COMPLETE}. The linked geschaeftsfall is retrieved by following the path:  request ->
     * geschaeftsfall.strAenVorabstimmungsId -> linked geschaeftsfall .
     *
     * @param request
     */
    void closeLinkedStrAenGeschaeftsfall(WbciRequest request);

    /**
     * Ordnet die angegebene taifunAuftragsId der Vorabsitummungsanfrage mit der angegebenen VorabstimmungsId zu und
     * lehnt gleich die Vorabstimmung mit einer ABBM mit MeldungsCode {@link de.mnet.wbci.model.MeldungsCode#VAE}).
     *
     * @param vorabstimmungsId die VorabstimmungsId der eingehende Vorabstimmungsanfrage
     * @param taifunOrderId    die TaifunAuftragsId, die der Vorabstimmungsanfrage zugeordnet werden muss.
     */
    void assignTaifunOrderAndRejectVA(@NotNull String vorabstimmungsId, @NotNull Long taifunOrderId) throws FindException;

    /**
     * Checks whether the {@code WbciGeschaeftsfall} with the specified {@code vorabstimmungsId} is already assigned to
     * the taifun order with the provided {@code taifunOrderId}.
     */
    boolean isGfAssignedToTaifunOrder(@NotNull String vorabstimmungsId, @NotNull Long taifunOrderId);

    /**
     * Checks if incoming Vorabstimmungsanfrage is linked to Geschaeftsfall with Status {@link
     * WbciGeschaeftsfallStatus#NEW_VA}. Valid link is only given when same EKPauf Carrier is represented in VA and GF
     * candidates.
     *
     * @param vorabstimmungsId die VorabstimmungsId der eingehende Vorabstimmungsanfrage.
     * @param geschaeftsfallId GF id im Status {@link WbciGeschaeftsfallStatus#NEW_VA}
     */
    boolean isLinkedToStrAenGeschaeftsfall(@NotNull String vorabstimmungsId, @NotNull Long geschaeftsfallId);

    /**
     * Ordnet die angegebene taifunAuftragsId der Vorabstimmungsanfrage mit der angegebenen VorabstimmungsId zu und
     * schließt den GF im Status {@link WbciGeschaeftsfallStatus#NEW_VA}. Verbindet zusätzlich die Vorabstimmung zum GF
     * im Status {@link WbciGeschaeftsfallStatus#NEW_VA} mit der eingehenden Vorabstimmunganfrage.
     *
     * @param vorabstimmungsId         die VorabstimmungsId der eingehende Vorabstimmungsanfrage
     * @param taifunOrderId            die TaifunAuftragsId, die der Vorabstimmungsanfrage zugeordnet werden muss.
     * @param strAenGeschaeftsfallId   GF im Status {@link WbciGeschaeftsfallStatus#NEW_VA} der geschlossen werden
     *                                 soll.
     * @param addCustomerCommunication definiert, ob ein sog. Protokolleintrag fuer die Auftragszuordnung erstellt
     *                                 werden soll. Dies ist nur bei manueller Auftragszuordnung (also aus der GUI
     *                                 heraus) notwendig; bei automatischer Auftragszuordnung erfolgt der
     *                                 Protokolleintrag aus der Route heraus.
     */
    void assignTaifunOrderAndCloseStrAenGeschaeftsfall(@NotNull String vorabstimmungsId, @NotNull Long taifunOrderId, 
            @NotNull Long strAenGeschaeftsfallId, boolean addCustomerCommunication) throws FindException;

    /**
     * Prueft, ob es sich bei dem Geschaefstfall {@code originalGf} um einen GF mit Typ RRNP und bei {@code actualGf} um
     * einen GF mit Typ KUE-MRN oder KUE-ORN handelt.
     *
     * @param originalGf
     * @param actualGf
     * @return {@code true} wenn der GF-Typ von {@code originalGf}=RRNP und {@code actualGF}=KUE-MRN oder KUE-ORN ist
     */
    boolean isGeschaeftsfallWechselRrnpToMrnOrn(@NotNull WbciGeschaeftsfall originalGf, @NotNull WbciGeschaeftsfall actualGf);
    
    /**
     * Marks the WbciGeschaeftsfall with the provided id as 'Klaerfall'.
     *
     * @param geschaeftsfallId the primary key of the Geschaeftsfall
     * @param reason           the reason why the WbciGeschaeftsfall is marked for clarification
     * @param user             the user who has marked the WbciGeschaeftsfall as 'Klaerfall'
     */
    void markGfForClarification(Long geschaeftsfallId, String reason, AKUser user);

    /**
     * Auf dem WBCI Geschaeftsfall wird das {@link WbciGeschaeftsfall#automatable} Flag auf den Wert von {@code
     * automatable} gesetzt.
     *
     * @param geschaeftsfallId
     * @param automatable
     */
    void markGfAsAutomatable(Long geschaeftsfallId, boolean automatable);

    /**
     * Marks the issue as being resolved for the WbciGeschaeftsfall matching the provided {@code geschaeftsfallId}. This
     * service will set the clarification flag in the WbciGeschaeftsfall to false amd will record the provided {@code
     * comment} along with the user in the WbciGeschaeftsfall. If the WbciGeschaeftsfall is not marked for clarification
     * then no changes are recorded in the geschaeftsfall.
     *
     * @param geschaeftsfallId the primary key of the Geschaeftsfall. A valid ID must be provided and the GF must be
     *                         marked for clarification
     * @param user             the user who has clarified the issue. A valid user must be provided
     * @param comment          any comments relating to how the issue was resolved. <em>Null</em> or
     *                         <em>empty-string</em> are not permitted.
     */
    void issueClarified(Long geschaeftsfallId, AKUser user, String comment);

    /**
     * Locates the VorabstimmungsAnfrage matching the supplied {@code vorabstimmungsId} and updates the field
     * 'Wechseltermin'.
     *
     * @param vorabstimmungsId of the Vorabstimmung
     * @param wechseltermin    as DateTime;
     */
    void updateVorabstimmungsAnfrageWechseltermin(String vorabstimmungsId, LocalDate wechseltermin);

    /**
     * Updates the internal status of the provided WbciGeschaeftsfall
     *
     * @param geschaeftsfallId
     * @param internalStatus
     */
    void updateInternalStatus(Long geschaeftsfallId, String internalStatus);

    /**
     * Removes wbci geschaeftsfall and associated vorabstimmungs data. This can only be done for geschaeftsfaelle that
     * have requests which have not yet been transmitted ({@link WbciRequestStatus#VA_VORGEHALTEN}).
     *
     * @param geschaeftsfallId
     */
    void deleteGeschaeftsfall(Long geschaeftsfallId);

    /**
     * Removes the given WBCI request. Only requests that have not already been transmitted can be deleted. The
     * associated vorabstimmungs data is NOT removed!
     *
     * @param wbciRequestId
     */
    void deleteStornoOrTvRequest(Long wbciRequestId);

    /**
     * Completes the provided automation task and sets the specified user as the one who completed the task.
     *
     * @param automationTask Automation task to be completed
     * @param user
     * @return the updated automation task
     */
    AutomationTask completeAutomationTask(AutomationTask automationTask, AKUser user);

    /**
     * Searchs through all active Geschaeftsfaelle and automatically sets the status to {@link
     * WbciGeschaeftsfallStatus#COMPLETE} when the Geschaeftsfall is eligible for completion.
     *
     * @return the number of geschaeftsfaelle that were completed.
     */
    int autoCompleteEligiblePreagreements();

    /**
     * Searchs through all Geschaeftsfaelle in state {@link WbciGeschaeftsfallStatus#NEW_VA} and automatically updates
     * the status to {@link WbciGeschaeftsfallStatus#NEW_VA_EXPIRED} if the deadline for sending or receiving the new VA
     * has not been met by the receiving carrier.
     *
     * @return the number of geschaeftsfaelle that were updated.
     */
    int updateExpiredPreagreements();

    /**
     * Checks whether there are any {@link de.mnet.wita.model.WitaCBVorgang}s assigned to the provided VorabstimmungsId,
     * which is set in the wbciGeschaeftsfall. If found the {@link de.mnet.wbci.model.WbciGeschaeftsfall} is marked for
     * clarification.
     *
     * @param wbciGeschaeftsfall
     */
    void checkCbVorgangAndMarkForClarification(WbciGeschaeftsfall wbciGeschaeftsfall);

    /**
     * Weist dem Endkunden des angegebenen Geschaeftsfalls den angegebenen Kundentyp (Geschaefts- oder Privatkunde) zu.
     *
     * @param geschaeftsfallId
     * @param kundenTyp
     */
    void assignCustomerTypeToEndCustomer(Long geschaeftsfallId, KundenTyp kundenTyp);

    /**
     * Adds a new or updates the existing automation task for the provided WbciGeschaeftsfall and TaskName.
     *
     * @param wbciGeschaeftsfall
     * @param taskName
     * @param elektraResponse
     * @param user
     */
    void createOrUpdateAutomationTask(WbciGeschaeftsfall wbciGeschaeftsfall, AutomationTask.TaskName taskName,
            ElektraResponseDto elektraResponse, AKUser user);

    /**
     * @param wbciGeschaeftsfall
     * @param taskName
     * @param automationStatus
     * @param modifications
     * @param user
     * @see WbciGeschaeftsfallService#createOrUpdateAutomationTask(WbciGeschaeftsfall, Meldung, AutomationTask.TaskName,
     * AutomationTask.AutomationStatus, String, AKUser)
     */
    void createOrUpdateAutomationTask(WbciGeschaeftsfall wbciGeschaeftsfall, AutomationTask.TaskName taskName,
            AutomationTask.AutomationStatus automationStatus, String modifications, AKUser user);

    /**
     * Adds a new or updates the existing automation task for the provided WbciGeschaeftsfall and TaskName.<br/> An
     * existing automation task will be updated in following cases:<br/> <ul> <li>the task is automatable, can be
     * multiple processed and it exists already an entry for this task within the AutomationTask-Table</li> <li>the task
     * is automatable, cannot be multiple processed and it exists already a faulty entry for this task within the
     * AutomationTask-Table</li> </ul> <br/>In all other cases a new AutomationTask entry will be created.
     *
     * @param wbciGeschaeftsfall
     * @param meldung
     * @param taskName
     * @param automationStatus
     * @param modifications
     * @param user
     */
    void createOrUpdateAutomationTask(WbciGeschaeftsfall wbciGeschaeftsfall,
            Meldung meldung,
            AutomationTask.TaskName taskName,
            AutomationTask.AutomationStatus automationStatus,
            String modifications, AKUser user);

    /**
     * Returns all Preagreement IDs that satisfy the following criteria: <ul> <li>the VA request status is
     * RUEM_VA_EMPFANGEN</li> <li>the geschaeftsfall status is ACTIVE or PASSIV</li> <li>the RUEM-VA has only ZWA or NAT
     * code</li> <li>the geschaeftsfall has no active TVs or STORNOs</li> <li>the geschaeftsfall has no automation
     * errors</li> <li>the confirmed date is not before the requested date</li> <li>the discrepancy, if any, between the
     * confirmed and requested date does not exceed {@link WitaConfigService#getWbciRequestedAndConfirmedDateOffset()}</li>
     * </ul>
     *
     * @return the preagreement IDs that match the criteria.
     */
    Collection<String> findPreagreementsWithAutomatableRuemVa();

    /**
     * Adds a new or updates the existing automation task for the provided VorabstimmungsId and TaskName within a new
     * Transaction.<br/> An existing automation task will be updated in following cases:<br/> <ul> <li>the task is
     * automatable, can be multiple processed and it exists already an entry for this task within the
     * AutomationTask-Table</li> <li>the task is automatable, cannot be multiple processed and it exists already a
     * faulty entry for this task within the AutomationTask-Table</li> </ul> <br/>In all other cases a new
     * AutomationTask entry will be created.
     *
     * @param vorabstimmungsId
     * @param meldung
     * @param taskName
     * @param automationStatus
     * @param modifications
     * @param user
     */
    void createOrUpdateAutomationTaskNewTx(String vorabstimmungsId, Meldung meldung, AutomationTask.TaskName taskName,
            AutomationTask.AutomationStatus automationStatus, String modifications, AKUser user);

    // @formatter:off
    /**
     * Returns all GFs with the following criteria:
     * <ul>
     *     <li>{@link WbciGeschaeftsfall#status} is {@link WbciGeschaeftsfallStatus#ACTIVE} or {@link WbciGeschaeftsfallStatus#PASSIVE}</li>
     *     <li>WbciGeschaeftsfall#typ is GeschaeftsfallTyp#VA_KUE_MRN, GeschaeftsfallTyp#VA_KUE_ORN</li>
     *     <li>Meldung is of type MeldungTyp#RUEM_VA (RueckmeldungVorabstimmung)</li>
     *     <li>no AutomationTask in state ERROR</li>
     *     <li>no AutomationTask of type TaskName#AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN</li>
     *     <li>Request in status {@link WbciRequestStatus#RUEM_VA_VERSENDET}</li>
     *     <li>change date is in future</li>
     * </ul>
     * @return
     */
     // @formatter:on
    public Collection<WbciGeschaeftsfall> findAutomateableOutgoingRuemVaForKuendigung();

}
