package de.mnet.wbci.service;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Service manages WBCI meldung instances. Performs operations such as storing incoming meldung objects to the database
 * or creating and sending WBCI meldung instances to the outside world.
 *
 *
 */
public interface WbciMeldungService extends WbciService, WbciConstants {

    /**
     * Saves a new incoming meldung to the database and updates the according geschaeftsfall status.
     *
     * @param metadata    the results of the processing are stored here
     * @param wbciMeldung any type of an INCOMING {@link Meldung}
     */
    <M extends Meldung<?>> void processIncomingMeldung(MessageProcessingMetadata metadata, M wbciMeldung);

    /**
     * Updates the status of the correlating request in case of the in- or outgoing Meldung.
     *
     * @param wbciMeldung any type of {@link Meldung}
     */
    <M extends Meldung<?>> void updateCorrelatingRequestForMeldung(M wbciMeldung);

    /**
     * Creates, validate and sends immediately a new meldung. <br> The given {@code wbciMeldung} object is enriched with
     * parameters from the corresponding WBCI requests. The parameters which are automatically enriched: <br> <ul>
     * <li>{@link Meldung#ioType} - will be set to {@link de.mnet.wbci.model.IOType#OUT}</li> <li>{@link
     * Meldung#absender}</li> <li>{@link Meldung#wbciGeschaeftsfall}</li> </ul>
     * <p/>
     * Throws a {@link de.mnet.wbci.exception.WbciValidationException}, if an error occurs at the validation of a
     * meldung. If any other error occurs, a {@link de.mnet.wbci.exception.WbciServiceException} will be thrown.
     *
     * @param wbciMeldung
     * @param vorabstimmungsId
     */
    <M extends Meldung<?>> void createAndSendWbciMeldung(M wbciMeldung, String vorabstimmungsId);

    /**
     * Executes the autoprocessing features like "check if LineID/Witavertrags-Nr. are equal to RUEMVA" for the {@link
     * de.mnet.wbci.model.UebernahmeRessourceMeldung} messages.
     *
     * @param akmTr an incomming {@link de.mnet.wbci.model.UebernahmeRessourceMeldung}.
     */
    void postProcessIncomingAkmTr(UebernahmeRessourceMeldung akmTr);

    /**
     * Creates and sends a response meldung to an invalid incoming request or meldung. This is used when M-Net receives
     * an invalid Message from the other EKP and has to send an ABBM response to the received message. <br /> The
     * supplied {@link MessageProcessingMetadata} are used to influence the normal processing behavior (like skip
     * post-processing or io archive entry) when sending out the response.
     *
     * @param metadata    message processing details
     * @param wbciMeldung the meldung to be sent
     */
    <M extends Meldung<?>> void sendErrorResponse(MessageProcessingMetadata metadata, M wbciMeldung);

    /**
     * Sends an AkmTr to the partner carrier.<br/> This method will be always executed within a new Transaction and is
     * used by the AutomationService!
     *
     * @param vorabstimmungsId
     * @param user             the current user who triggers this method (e.g. the scheduler)
     */
    void sendAutomatedAkmTr(String vorabstimmungsId, AKUser user);

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
     *     <li>{@link WbciGeschaeftsfall#mnetTechnologie} is included in {@link Technologie#getWitaOrderRelevantTechnologies()}</li>
     * </ul>
     * @return Collection of {@link UebernahmeRessourceMeldung}.
     */
    //@formatter:on
    @NotNull
    Collection<UebernahmeRessourceMeldung> findAutomatableAkmTRsForWitaProcesing();


    //@formatter:off
    /**
     * Returns all latest incoming(!) {@link UebernahmeRessourceMeldung}en which are automatable for further WITA processing and
     * fit to the following criteria:
     * <ul>
     *     <li>{@link WbciGeschaeftsfall#STATUS} is {@link WbciGeschaeftsfallStatus#ACTIVE} or {@link WbciGeschaeftsfallStatus#PASSIVE} </li>
     *     <li>{@link VorabstimmungsAnfrage#requestStatus} is equal to {@link WbciRequestStatus#AKM_TR_EMPFANGEN}</li>
     *     <li>{@link WbciGeschaeftsfall#klaerfall} is false</li>
     *     <li>{@link WbciGeschaeftsfall#automatable} is true and no  entries in {@link WbciGeschaeftsfall#automationTasks}</li>
     *     <li>no active {@link TerminverschiebungsAnfrage} or {@link StornoAnfrage}</li>
     *     <li>no active {@link WitaCBVorgang}</li>
     *     <li>{@link WbciGeschaeftsfall#mnetTechnologie} is included in {@link Technologie#getWitaOrderRelevantTechnologies()}</li>
     * </ul>
     * @return
     */
    //@formatter:on
    @NotNull
    Collection<UebernahmeRessourceMeldung> findAutomatableIncomingAkmTRsProcesing();

    
    //@formatter:off
    /**
     * Returns all Preagreemet IDs that satisfy the following criteria:
     * <ul>
     *     <li>the VA request ist TV_ERLM_EMPFANGEN</li>
     *     <li>M-net is the receiving carrier for the preagreement</li>
     *     <li>state of last WbciRequest for the VA is 'TV_ERLM_EMPFANGEN'</li>
     *     <li>VA has no active storno request</li>
     *     <li>VA has no automation error entry</li>
     * </ul>
     * @return
     */
    //@formatter:on
    @NotNull
    Collection<ErledigtmeldungTerminverschiebung> findAutomatableTvErlmsForWitaProcessing();
    
    
    //@formatter:off
    /**
     * Returns all Preagreemet IDs that satisfy the following criteria:
     * <ul>
     *     <li>the request type is STR_AUFH_AUF</li>
     *     <li>M-net is the receiving carrier for the preagreement</li>
     *     <li>state of last WbciRequest for the VA is 'STORNO_ERLM_EMPFANGEN'</li>
     *     <li>VA has no automation error entry</li>
     *     <li>VA has at least one WITA request</li>
     *     <li>VA has no automation log of type 'WITA_SEND_STORNO'</li>
     * </ul>
     * @return
     */
    //@formatter:on
    @NotNull
    Collection<ErledigtmeldungStornoAuf> findAutomatableStrAufhErlmsForWitaProcessing();


    //@formatter:off
    /**
     * Returns all Preagreemet IDs that satisfy the following criteria:
     * <ul>
     *     <li>M-net is the donating carrier for the preagreement</li>
     *     <li>the request type is STR_AUFH_AUF or STR_AUFH_ABG</li>
     *     <li>state of last WbciRequest for the VA is 'STORNO_ERLM_EMPFANGEN' or 'STORNO_ERLM_VERSENDET'</li>
     *     <li>VA has no automation error entry</li>
     *     <li>VA has no automation log of type 'UNDO_AUFTRAG_KUENDIGUNG'</li>
     * </ul>
     * @return
     */
    //@formatter:on
    @NotNull
    Collection<ErledigtmeldungStornoAuf> findAutomatableStrAufhErlmsDonatingProcessing();

}
