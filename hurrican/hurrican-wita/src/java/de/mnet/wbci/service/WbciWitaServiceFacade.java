/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.14
 */
package de.mnet.wbci.service;

import static de.mnet.wbci.service.impl.WbciAutomationServiceImpl.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.OverdueWitaOrderVO;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciWitaOrderDataVO;
import de.mnet.wbci.service.impl.WbciWitaServiceFacadeImpl;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * ServiceFacade, die der Entkopplung von Wita und Wbci Services dienen soll. Die Kommunikation zwischen Wbci und Wita
 * auf Service-Ebene soll ueber diese Fassade abgewickelt werden. Für die andere Richtung Wita -> Wbci soll die {@link
 * de.mnet.wita.service.WitaWbciServiceFacade} verwendet werden.
 *
 *
 */
public interface WbciWitaServiceFacade extends ICCService {

    /**
     * Retrieves all {@link de.mnet.wita.model.WitaCBVorgang} associated with the provided auftrag Id.
     *
     * @param auftragId
     * @return
     */
    List<WitaCBVorgang> findWitaCbVorgaengeByAuftrag(Long auftragId);

    /**
     * Retrieves all {@link de.mnet.wita.model.WitaCBVorgang} associated with the provided vorabstimmungsId.
     *
     * @param vorabstimmungsId
     * @return
     */
    List<WitaCBVorgang> findWitaCbVorgaenge(String vorabstimmungsId);

    /**
     * Retrieves a {@link WitaCBVorgang} associated with the provided {@code vorabstimmungsId} which has an active
     * workflow instance. <br/> If there are more than one active WITA orders, {@code null} will be returned!
     *
     * @param vorabstimmungsId
     * @return
     */
    WitaCBVorgang findSingleActiveWitaCbVorgang(String vorabstimmungsId);

    /**
     * Ermittelt analog zu {@link WbciWitaServiceFacade#findSingleActiveWitaCbVorgang(String)} einen aktiven
     * WITA Vorgang zu der angegebenen Vorabstimmungs-Id. Zusaetzlich muss der Vorgang aber auch mit dem
     * angegebenen Auftrag verlinkt sein.
     * 
     * @param auftragId
     * @param vorabstimmungsId
     * @return
     */
    WitaCBVorgang findSingleActiveWitaCbVorgang(Long auftragId, String vorabstimmungsId);

    /**
     * Updates the {@link de.mnet.wita.model.VorabstimmungAbgebend} of the underlining hurrican order if: <ul> <li>a
     * hurrican oder is assigned</li> <li>the request is in one of following states:</li> <ul> <li>{@link
     * de.mnet.wbci.model.WbciRequestStatus#RUEM_VA_VERSENDET}</li> <li>{@link de.mnet.wbci.model.WbciRequestStatus#ABBM_VERSENDET}</li>
     * <li>{@link de.mnet.wbci.model.WbciRequestStatus#TV_ERLM_VERSENDET}</li> <li>{@link
     * de.mnet.wbci.model.WbciRequestStatus#STORNO_ERLM_EMPFANGEN}</li> <li>{@link de.mnet.wbci.model.WbciRequestStatus#STORNO_ERLM_VERSENDET}</li>
     * </ul> </ul>
     *
     * @param wbciRequest modified {@link de.mnet.wbci.model.WbciRequest}
     * @return {@code null} or the updated/created {@link de.mnet.wita.model.VorabstimmungAbgebend};
     */
    @Nullable
    VorabstimmungAbgebend updateOrCreateWitaVorabstimmungAbgebend(final WbciRequest wbciRequest);

    /**
     * Aktualisiert den Carrier auf Basis von den in der AKM-TR angebebenen PKIAuf wenn RessourcenÜbernahme=ja. <br/>
     * Grund: der aufnehmende Carrier kann von dem Partner-Carrier, mit dem der WBCI Vorgang durchgefuehrt wird
     * abweichend sein. Dies ist z.B. dann der Fall, wenn der Partner-EKP nur als Reseller auftritt und den (Kunden-)
     * Auftrag gar nicht selbst realisiert; dies ist bspw. bei 1&1 der Fall.
     *
     * @param uebernahmeRessourceMeldung die AKM-TR
     * @return {@code null} oder die aktualisierte {@link de.mnet.wita.model.VorabstimmungAbgebend};
     */
    VorabstimmungAbgebend updateWitaVorabstimmungAbgebend(final UebernahmeRessourceMeldung uebernahmeRessourceMeldung);


    //@formatter:off
    /**
     * Generates a List of {@link OverdueWitaOrderVO}s. Each VO represents a current {@link VorabstimmungsAnfrage} with
     * a missing {@link WitaCBVorgang} in the state {@link WbciWitaServiceFacadeImpl#getOpenWitaStatuses()}.
     * <br/>
     * Following preconditions must be fulfilled by the {@link VorabstimmungsAnfrage}:
     * <ul>
     *     <li>An {@link UebernahmeRessourceMeldung} is created</li>
     *     <li>The {@link WbciGeschaeftsfall#mnetTechnologie} is one of these {@link Technologie#getWitaOrderRelevantTechnologies()}</li>
     *     <li>The {@link WbciGeschaeftsfall#wechseltermin} is in {@link WbciWitaServiceFacadeImpl#OVERDUE_DAYS_FOR_MISSING_WITA_ORDERS}
     *         days</li>
     * </ul>
     *
     * @return
     */
    //@formatter:on
    List<OverdueWitaOrderVO> getOverduePreagreementsWithMissingWitaOrder();

    //@formatter:off
    /**
     * Creates a new WITA-Vorgang in consideration of the assigned vorabstimmungsId.
     *
     * @param akmTrAutomationTask typ of WITA-Vorgang, currently supported {@link CBVorgang#TYP_ANBIETERWECHSEL} and
     *                            {@link CBVorgang#TYP_NEU}
     * @param vorabstimmungsId    {@link WbciGeschaeftsfall#vorabstimmungsId}
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return {@link WitaCBVorgang} of the created WITA-Vorgang or NULL
     */
    //@formatter:on
    WitaCBVorgang createWitaVorgang(AkmTrAutomationTask akmTrAutomationTask, String vorabstimmungsId, AKUser user);


    //@formatter:off
    /**
     * Erstellt fuer die angegebenen WITA VtrNrs WITA Kuendigungen und linkt diese zu der VA-Id aus der AKM-TR.
     *
     * @param akmTr
     * @param witaVtrNrs
     * @param user
     * @return
     */
    //@formatter:on
    List<WitaCBVorgang> createWitaCancellations(UebernahmeRessourceMeldung akmTr, SortedSet<String> witaVtrNrs, AKUser user);


    /**
     * Generates all needed data for creating an {@link WitaCBVorgang} after a successful {@link
     * VorabstimmungsAnfrage}.
     *
     * @param vorabstimmungsId {@link WbciGeschaeftsfall#vorabstimmungsId}
     * @return {@link WbciWitaOrderDataVO}
     */
    WbciWitaOrderDataVO generateWitaDataForPreAggreement(String vorabstimmungsId);

    /**
     * Tries to determines a possible WITA-{@link GeschaeftsfallTyp} for the assigned data. Currently not supported ist
     * the CB-Typ {@link WitaCBVorgang#TYP_PORTWECHSEL}.
     *
     * @param vo           {@link WbciWitaOrderDataVO}
     * @param cbVorgangTyp {@link WitaCBVorgang#typ}
     * @return a valid {@link WbciGeschaeftsfall} or throw an {@link WbciServiceException}
     */
    GeschaeftsfallTyp determineWitaGeschaeftsfall(WbciWitaOrderDataVO vo, Long cbVorgangTyp);

    /**
     * Calls the {@link WitaTalOrderService#doTerminverschiebung(Long, LocalDate, AKUser, boolean, String)} method
     * and fills it with the data of the latest {@link ErledigtmeldungTerminverschiebung}.
     *
 * @param vorabstimmungsID
     * @param cbVorgangId
     * @param user               the current user
     * @return the created (or modified) {@link WitaCBVorgang}
     */
    WitaCBVorgang doWitaTerminverschiebung(String vorabstimmungsID, Long cbVorgangId,  AKUser user, TamUserTask.TamBearbeitungsStatus tamBearbeitungsStatus);


    /**
     * Calls the {@link WitaTalOrderService#doStorno(Long, AKUser)} method with the given data.
     *
     * @param erlmStrAuf
     * @param cbVorgangId
     * @param user
     * @return
     */
    WitaCBVorgang doWitaStorno(ErledigtmeldungStornoAuf erlmStrAuf, Long cbVorgangId, AKUser user);

}
