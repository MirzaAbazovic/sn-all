package de.mnet.wbci.dao;

import java.time.*;
import java.util.*;
import org.hibernate.LockMode;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.KuendigungCheck;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wita.model.WitaCBVorgang;

public interface WbciDao extends FindDAO, StoreDAO, ByExampleDAO, FindAllDAO {

    /**
     * Gets the next sequence value for the declared request type. The request typ is needed for generation of a correct
     * VorabstimmungsId.
     */
    Long getNextSequenceValue(RequestTyp requestTyp);

    /**
     * Finds all {@link WbciRequest}s matching the Vorabstimmungs-Id and request-type
     *
     * @param vorabstimmungsId
     * @param requestTyp
     * @return
     */
    <T extends WbciRequest> List<T> findWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp);

    /**
     * Finds all {@link WbciRequest}s matching the Vorabstimmungs-Id and request-type, setting the supplied lockMode
     *
     * @param vorabstimmungsId
     * @param requestTyp
     * @param lockMode
     * @return
     */
    <T extends WbciRequest> List<T> findWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp, LockMode lockMode);

    /**
     * Ermittelt den letzten {@link WbciRequest} mit der angegebenen {@code vorabstimmungsId} und {@code requestTyp} und
     * gibt ihn zurueck.
     *
     * @param vorabstimmungsId
     * @param requestTyp
     * @return
     */
    <T extends WbciRequest> T findLastWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp);

    /**
     * Finds all WbciRequests matching the supplied {@code vorabstimmungsId} and {@code changeId}
     *
     * @param vorabstimmungsId
     * @param changeId
     * @return
     */
    List<WbciRequest> findWbciRequestByChangeId(String vorabstimmungsId, String changeId);

    /**
     * Finds the T matching to the supplied {@code vorabstimmungsId} and {@code changeId}
     *
     * @param vorabstimmungsId orginal ID of the VA
     * @param changeId         ID of the TV or Storno
     * @param requestTyp       type of T which should be searched
     * @return T
     */
    <T extends WbciRequest> T findWbciRequestByChangeId(String vorabstimmungsId, String changeId, Class<T> requestTyp);

    /**
     * Find the {@link de.mnet.wbci.model.WbciGeschaeftsfall} matching to the Vorabstimmungs-Id
     *
     * @param vorabstimmungsId
     * @return
     */
    WbciGeschaeftsfall findWbciGeschaeftsfall(String vorabstimmungsId);

    /**
     * Finds all {@code Meldung}en matching to the Vorabstimmungs-Id.
     *
     * @param vorabstimmungsId
     * @return a list of {@link Meldung}
     */
    List<Meldung> findMeldungenForVaId(String vorabstimmungsId);

    /**
     * Finds all {@code Meldung}en matching to the Vorabstimmungs-Id and the Meldungstyp.
     *
     * @param vorabstimmungsId
     * @param classDef         the class definition of a {@link Meldung}
     * @return a list of {@link Meldung}
     */
    <M extends Meldung> List<M> findMeldungenForVaIdAndMeldungClass(String vorabstimmungsId, Class<M> classDef);

    /**
     * Liefert die Antwortfrist fuer den angegebenen RequestStatus und Typ.
     *
     * @param typ               request typ
     * @param wbciRequestStatus request status
     * @return die Antwortfrist
     */
    Antwortfrist findAntwortfrist(RequestTyp typ, WbciRequestStatus wbciRequestStatus);

    /**
     * Returns a list of {@link WbciGeschaeftsfall} which have received a positive answered AkmTr for the provided
     * taifun order id.
     *
     * @param criteria the criteria to use when searching. The criteria cannot be null and a billingOrderNo must be
     *                 specified. All other criteria is optional.
     * @return
     */
    List<String> findVorabstimmungIdsByBillingOrderNoOrig(VorabstimmungIdsByBillingOrderNoCriteria criteria);

    /**
     * Finds all scheduled {@link WbciRequest}s (requests which have sendAfter parameter in the past). The WbciRequest
     * IDs returned are sorted from oldest to newest 'sendAfter'.
     *
     * @return List of {@link WbciRequest} IDs
     */
    List<Long> findScheduledWbciRequestIds();

    /**
     * Liefert alle im System vorhandenen Vorabstimmungen. Vorabstimmungen mit dem Status {@link
     * WbciGeschaeftsfallStatus#COMPLETE} werden automatisch <b>ausgefiltert</b>. Je nach {@link CarrierRole} von M-Net
     * werden entweder die aufnehmende ( {@link CarrierRole#AUFNEHMEND}) oder die abgebende ( {@link
     * CarrierRole#ABGEBEND}) Vorabstimmungen zurueck geliefert.
     *
     * @param mnetCarrierRole {@link CarrierRole} aus Sicht von M-Net
     * @return Eine Liste von {@link PreAgreementVO}-Objekten
     */
    List<PreAgreementVO> findMostRecentPreagreements(CarrierRole mnetCarrierRole);

    /**
     * @see de.mnet.wbci.dao.WbciDao#findMostRecentPreagreements(de.mnet.wbci.model.CarrierRole)
     * Anstatt alle VAs in der angegebenen Rolle zu laden wird jedoch nur die VA mit der angegebenen
     * {@code singlePreAgreementIdToLoad} ermittelt.
     * @param mnetCarrierRole {@link CarrierRole} aus Sicht von M-Net
     * @param singlePreAgreementIdToLoad PreAgreementId der zu ladenden Vorabstimmung
     * @returnreturn Eine Liste von {@link PreAgreementVO}-Objekten, die den Such-Parametern entsprechen
     */
    List<PreAgreementVO> findMostRecentPreagreements(CarrierRole mnetCarrierRole, String singlePreAgreementIdToLoad);

    /**
     * Returns all preagreements that have a wechseltermin before the supplied {@code withWechselTerminBefore} date and
     * do <b>NOT</b> have a corresponding Wita ABM-PV (or at least a Wita AKM-PV) Meldung. <br/> Only preagreements are
     * considered where MNET is the donating carrier ({@link CarrierRole#ABGEBEND}), the preagreement is still active
     * ({@link WbciGeschaeftsfallStatus#ACTIVE} or {@link WbciGeschaeftsfallStatus#PASSIVE}), resource uebernahme = true
     * ({@link UebernahmeRessourceMeldung#isUebernahme()}) and the vertragsnummer ({@link
     * TechnischeRessource#getVertragsnummer()} is set. <br /> Note: The vertragsnummer from the RUEM-VA is used to
     * correlate the WBCI preagreement with the WITA ABM-PV.
     *
     * @param withWechselTerminBefore checks preagreements with a wechseltermin before this date
     * @return the overdue preagreements
     */
    List<OverdueAbmPvVO> findPreagreementsWithOverdueAbmPv(LocalDate withWechselTerminBefore);

    /**
     * Liefert alle im System vorhandenen aktiven {@link WbciGeschaeftsfall}, die dem Taifunauftrag mit der angegebenen
     * Id zugeordnet sind. <br/> Es werden hier nur die direkt zugeordneten GFs ermittelt; ist der Auftrag ueber die
     * 'nonBillingRelevantOrderNo' Collection dem GF zugeordnet, wird dieser nicht beruecksichtigt!
     *
     * @param billingOrderNoOrig           Taifunauftrag-Id.
     * @param interpretNewVaStatusAsActive wenn true, wird {@link WbciGeschaeftsfallStatus#NEW_VA} als aktiv
     *                                     interpretiert.
     * @return Ein Liste von {@link WbciGeschaeftsfall}-Objekten
     */
    List<WbciGeschaeftsfall> findActiveGfByOrderNoOrig(Long billingOrderNoOrig, boolean interpretNewVaStatusAsActive);

    /**
     * Liefert alle im System vorhandenen geschlossenen {@link WbciGeschaeftsfall}, die dem Taifunauftrag mit der angegebenen
     * Id zugeordnet sind. <br/> Es werden hier nur die direkt zugeordneten GFs ermittelt; ist der Auftrag ueber die
     * 'nonBillingRelevantOrderNo' Collection dem GF zugeordnet, wird dieser nicht beruecksichtigt!
     *
     * @param billingOrderNoOrig           Taifunauftrag-Id.
     * @return Ein Liste von {@link WbciGeschaeftsfall}-Objekten
     */
    List<WbciGeschaeftsfall> findCompleteGfByOrderNoOrig(Long billingOrderNoOrig);

    /**
     * Ermittelt alle {@link WbciGeschaeftsfall} Objekte, die direkt oder ueber die 'nonBillingRelevantOrderNos' dem
     * Taifun Auftrag zugeordnet sind.
     *
     * @param billingOrderNoOrig
     * @return
     */
    List<WbciGeschaeftsfall> findGfByOrderNoOrig(Long billingOrderNoOrig);

    /**
     * Fetches the entity by id, setting the supplied LockMode
     *
     * @param id       the id of the entity
     * @param lockMode the lock mode to use for the query
     * @param clazz    the id of the entity
     * @return the {@link WbciEntity} matching the id or null
     */
    <T extends WbciEntity> T byIdWithLockMode(Long id, LockMode lockMode, Class<T> clazz);

    /**
     * Deletes wbci entity.
     *
     * @param entity
     * @param <T>
     */
    <T extends WbciEntity> void delete(T entity);

    KuendigungCheck findKuendigungCheckForOeNoOrig(Long oeNoOrig);

    /**
     * Returns all Preagreements that satisfy the following criteria: <ul> <li>the {@link
     * WbciGeschaeftsfall#getStatus()} is either {@link WbciGeschaeftsfallStatus#ACTIVE} or {@link
     * WbciGeschaeftsfallStatus#PASSIVE}.</li> <li>the {@link WbciGeschaeftsfall#getWechseltermin()} date &lt;= today -
     * 3 days.</li> <li>the geschaeftsfall has <b>NO</b> {@link AutomationTask} with error state (if the task is
     * automatable).</li> <li>the geschaeftsfall has <b>NOT</b> been marked as a klaerfall.</li> </ul>
     *
     * @param maxResults limits the number of preagreements returned. To return all preagreements set this value to 0
     * @return the preagreements that match the criteria.
     */
    List<WbciGeschaeftsfall> findElapsedPreagreements(int maxResults);

    /**
     * Returns <code>true</code> if all automation tasks of the WbciGeschaeftsfall with the provided vorabstimmungId
     * have been successfully completed. If there is at least one faulty automation task <code>false</code> will be
     * returned.
     *
     * @param vorabstimmungsId
     * @return
     */
    boolean hasFaultyAutomationTasks(String vorabstimmungsId);

    //@formatter:off
    /**
     * Returns all latest {@link UebernahmeRessourceMeldung}en which are automatable for further WITA processing and
     * fit to the following criteria:
     * <ul>
     *     <li>{@link WbciGeschaeftsfall#STATUS} is {@link WbciGeschaeftsfallStatus#ACTIVE} or {@link WbciGeschaeftsfallStatus#PASSIVE} </li>
     *     <li>{@link VorabstimmungsAnfrage#requestStatus} is equal to {@link WbciRequestStatus#AKM_TR_VERSENDET}</li>
     *     <li>{@link WbciGeschaeftsfall#klaerfall} is false</li>
     *     <li>{@link WbciGeschaeftsfall#automatable} is true and no  entries in {@link WbciGeschaeftsfall#automationTasks}</li>
     *     <li>no active {@link TerminverschiebungsAnfrage} or {@link StornoAnfrage}</li>
     *     <li>no active {@link WitaCBVorgang}</li>
     * </ul>
     * @param mnetTechnologies  matching one of the {@link Technologie}s in this list. To ignore the technologie pass
     *                          an empty list.
     * @return List of {@link UebernahmeRessourceMeldung}.
     */
    //@formatter:on
    List<UebernahmeRessourceMeldung> findAutomatableAkmTRsForWitaProcesing(List<Technologie> mnetTechnologies);
    
    
    //@formatter:off
    /**
     * Returns all latest incoming(!) {@link UebernahmeRessourceMeldung}en which are automatable for further WITA 
     * processing (WITA type: cancel) and fit to the following criteria:
     * <ul>
     *     <li>{@link WbciGeschaeftsfall#STATUS} is {@link WbciGeschaeftsfallStatus#ACTIVE} or {@link WbciGeschaeftsfallStatus#PASSIVE} </li>
     *     <li>{@link VorabstimmungsAnfrage#requestStatus} is equal to {@link WbciRequestStatus#AKM_TR_EMPFANGEN}</li>
     *     <li>{@link WbciGeschaeftsfall#klaerfall} is false</li>
     *     <li>{@link WbciGeschaeftsfall#automatable} is true and no  entries in {@link WbciGeschaeftsfall#automationTasks}</li>
     *     <li>no active {@link TerminverschiebungsAnfrage} or {@link StornoAnfrage}</li>
     *     <li>no active {@link WitaCBVorgang}</li>
     * </ul>
     * @return
     */
    //@formatter:on
    List<UebernahmeRessourceMeldung> findAutomatableIncomingAkmTRsForWitaProcesing();

    
    //@formatter:off
    /**
     * Returns all latest {@link ErledigtmeldungTerminverschiebung}en which are automatable for further WITA processing
     * (sending WITA TV) and fit to the following criteria:
     * <ul>
     *     <li>the VA request type is TV</li>
     *     <li>M-net is the receiving carrier for the preagreement</li>
     *     <li>state of last WbciRequest for the VA is 'TV_ERLM_EMPFANGEN'</li>
     *     <li>VA has no active storno request</li>
     *     <li>VA has no automation error entry</li>
     *     <li>VA has at least one WITA request</li>
     * </ul>
     * 
     * @param mnetTechnologies matching one of the {@link Technologie}s in this list. To ignore the technologie pass
     *                          an empty list.
     * @return List of {@link ErledigtmeldungTerminverschiebung}.
     */
    //@formatter:on
    List<ErledigtmeldungTerminverschiebung> findAutomateableTvErlmsForWitaProcessing(List<Technologie> mnetTechnologies);


    //@formatter:off
    /**
     * Returns all latest {@link ErledigtmeldungStornoAuf}en which are automatable for further WITA processing
     * (sending WITA Storno) and fit to the following criteria:
     * <ul>
     *     <li>M-net is the receiving carrier for the preagreement</li>
     *     <li>the request type is STR_AUFH_AUF</li>
     *     <li>state of last WbciRequest for the VA is 'STORNO_ERLM_EMPFANGEN'</li>
     *     <li>VA has no automation error entry</li>
     *     <li>VA has at least one WITA request</li>
     *     <li>VA has no automation log of type 'WITA_SEND_STORNO'</li>
     * </ul>
     *
     * @param mnetTechnologies matching one of the {@link Technologie}s in this list. To ignore the technologie pass
     *                          an empty list.
     * @return List of {@link ErledigtmeldungTerminverschiebung}.
     */
    //@formatter:on
    List<ErledigtmeldungStornoAuf> findAutomateableStrAufhErlmsForWitaProcessing(List<Technologie> mnetTechnologies);


    //@formatter:off
    /**
     * Returns all latest {@link ErledigtmeldungStornoAuf}en which are automatable for further processing
     * and fit to the following criteria:
     * <ul>
     *     <li>M-net is the donating carrier for the preagreement</li>
     *     <li>the request type is STR_AUFH_AUF or STR_AUFH_ABG</li>
     *     <li>state of last WbciRequest for the VA is 'STORNO_ERLM_EMPFANGEN' or 'STORNO_ERLM_VERSENDET'</li>
     *     <li>VA has no automation error entry</li>
     *     <li>VA has no automation log of type 'UNDO_AUFTRAG_KUENDIGUNG'</li>
     * </ul>
     *
     * @return List of {@link ErledigtmeldungTerminverschiebung}.
     */
    //@formatter:on
    List<ErledigtmeldungStornoAuf> findAutomateableStrAufhErlmsDonatingProcessing();


    //@formatter:off
    /**
     * Returns all latest {@link UebernahmeRessourceMeldung}en with the following criteria:
     * <ul>
     *     <li>{@link WbciGeschaeftsfall#STATUS} is not {@link WbciGeschaeftsfallStatus#COMPLETE} and not {@link WbciGeschaeftsfallStatus#NEW_VA} </li>
     *     <li>{@link WbciGeschaeftsfall#WECHSELTERMIN} is between today and (today - workingDaysBeforeWechseltermin)</li>
     *     <li>{@link WbciGeschaeftsfall#AUFNEHMENDER_EKP} is equal to aufnehmenderEKP</li>
     *     <li>{@link VorabstimmungsAnfrage#requestStatus} is equal to {@link WbciRequestStatus#AKM_TR_VERSENDET} or {@link WbciRequestStatus#AKM_TR_EMPFANGEN}</li>
     * </ul>
     * @param daysBeforeWechseltermin working days before the {@link WbciGeschaeftsfall#WECHSELTERMIN} is reached
     * @param aufnehmenderEKP  specifies the receiving carrier in form of his {@link CarrierCode}.
     * @param mnetTechnologies  matching one of the {@link Technologie}s in this list. To ignore the technologie pass
     *                          an empty list.
     * @return List of {@link UebernahmeRessourceMeldung}.
     */
    //@formatter:on
    List<UebernahmeRessourceMeldung> findOverdueAkmTrsNearToWechseltermin(int daysBeforeWechseltermin, CarrierCode aufnehmenderEKP, List<Technologie> mnetTechnologies);

    /**
     * Returns all Preagreements that satisfy the following criteria: <ul> <li>the {@link
     * WbciGeschaeftsfall#getStatus()} matches the supplied {@code status}</li> <li>the {@link
     * WbciGeschaeftsfall#getWechseltermin()} date &lt; the supplied {@code wechselTerminBefore} date</li> </ul>
     *
     * @return the preagreements that match the criteria.
     */
    List<WbciGeschaeftsfall> findPreagreementsWithStatusAndWechselTerminBefore(WbciGeschaeftsfallStatus status, LocalDate wechselTerminBefore);

    /**
     * Returns all Preagreement IDs that satisfy the following criteria: <ul> <li>the VA request status is
     * RUEM_VA_EMPFANGEN</li> <li>the geschaeftsfall status is ACTIVE or PASSIV</li> <li>the RUEM-VA has only ZWA or NAT
     * code</li> <li>the geschaeftsfall has no active TVs or STORNOs</li> <li>the geschaeftsfall has no automation
     * errors</li> </ul>
     *
     * @return the preagreement IDs that match the criteria.
     */
    List<String> findPreagreementsWithAutomatableRuemVa();

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
     * </ul>
     * @return
     */
     // @formatter:on
    List<WbciGeschaeftsfall> findAutomateableOutgoingRuemVaForKuendigung();
}
