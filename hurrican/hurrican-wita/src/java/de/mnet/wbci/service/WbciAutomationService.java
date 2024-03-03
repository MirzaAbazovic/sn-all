/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2014
 */
package de.mnet.wbci.service;

import java.time.*;
import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Service-Interface fuer WBCI Automatisierungen, bei denen M-net die aufnehmende Rolle einnimmt.
 *
 *
 */
public interface WbciAutomationService extends WbciService {

    // @formatter:off
    /**
     * Does an automatic processing of all automatable incoming RUEM-VAs.<br/> For every automatable RUEM-VA following
     * actions will be executed:<br/> 
     * <ol> 
     *     <li>Dialing numbers within Taifun will be updated.</li> 
     *     <li>An AKM-TR will be sent out.</li> 
     * </ol>
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return the VorabstimmungIDs of RUEM-VAs that were automatically processed
     */
    // @formatter:on
    Collection<String> processAutomatableRuemVas(AKUser user);

    //@formatter:off
    /**
     * Does an automatic processing of all automatable AKM-TRs where M-Net is the receiving carrier.<br/>
     * For every automatable AKM-TR following actions will be executed:<br/>
     * <p/>
     * Try to create and send out an WITA-Order if {@link UebernahmeRessourceMeldung#uebernahme} and
     * {@link UebernahmeRessourceMeldung#leitungen} fit to the criteria:
     * <ul>
     *    <li>Ressourcenübernahme true, 1 Leitung    =>      {@link CBVorgang#TYP_ANBIETERWECHSEL} </li>
     *    <li>Ressourenübernahme true, 1+n Leitungen =>      manual processing, automation error in {@link WbciGeschaeftsfall#automationTasks}/li>
     *    <li>Ressourenübernahme true, 0 Leitungen   =>
     *    <ul>
     *        <li>1 Leitung RUEM-VA => {@link CBVorgang#TYP_ANBIETERWECHSEL}</li>
     *        <li>!= 1 Leitung in RUEM-VA => manual processing, automation error in {@link WbciGeschaeftsfall#automationTasks}</li>
     *    </ul>
     *    <li>Ressourcenübernahme false, 1 hurrican order    =>   {@link CBVorgang#TYP_NEU}</li>
     *    <li>Ressourcenübernahme false, != 1 hurrican order =>   manual processing, automation error in {@link WbciGeschaeftsfall#automationTasks}</li>
     * </ul>
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return all {@link WitaCBVorgang#carrierRefNr} of the created WITA orders.
     */
    //@formatter:on
    Collection<String> processAutomatableAkmTrs(AKUser user);


    //@formatter:off
    /**
     * Does an automatic processing of all automatable incoming AKM-TRs where M-Net is the donating carrier.<br/>
     * For every automatable AKM-TR following actions will be executed:<br/>
     * <p/>
     * Try to create and send out an WITA cancel order if {@link UebernahmeRessourceMeldung#uebernahme} and
     * {@link UebernahmeRessourceMeldung#leitungen} fit to the criteria:
     * <ul>
     *    <li>Ressourcenübernahme false                 =>  no WITA cancel order but AutomationTask=success </li>
     *    <li>Ressourenübernahme true, 0 Leitung(en)    =>  no WITA Cancel order but AutomationTask=success </li>
     *    <li>Ressourenübernahme true, >= 1 Leitung(en) =>
     *      <ul>
     *         <li>RUEM_VA has more 'Leitung(en)' than the AKM-TR  =>  not requested TALs are cancelled </li>
     *      </ul>
     *    </li>
     * </ul>
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return all {@link WitaCBVorgang#carrierRefNr} of the created WITA orders.
     */
    //@formatter:on
    Collection<String> processAutomatableIncomingAkmTrs(AKUser user);


    //@formatter:off
    /**
     * Fuehrt eine automatische Verarbeitung von eingegangenen WBCI ERLM-TVs durch. <br/>
     * <p/>
     * Der Service ermittelt alle eingegangenen ERLM-TVs (M-net = aufnehmender Provider) zu automatisierbaren WBCI
     * Vorgaengen und versucht, zu diesen VAs eine WITA Terminverschiebung mit entsprechendem Termin einzustellen.
     * <br/>
     * <p/>
     * Dabei werden folgende Aktionen/Checks durchgefuehrt: <br/>
     * <p/>
     * <ul>
     *     <li>WITA Vorgang besitzt identischen Wunschtermin wie WBCI ERLM  ==>  keine Aktion  </li>
     *     <li>WITA Vorgang  zur VA vorhanden aber nicht mehr aktiv (keine Workflow Instanz)  ==>  AutomationError </li>
     *     <li>WITA Vorgang ausgeloest / versendet und KWT weicht von WBCI TV Termin ab  ==>  WITA TV erstellen </li>
     *     <li>WITA Vorgang ausgeloest aber vorgehalten   ==>  WITA KWT wird ueberschrieben </li>
     *     <li>WITA Vorgang versendet und TV vorgehalten  ==>  WITA TV KWT wird ueberschrieben </li>
     * </ul>
     * <p/>
     * zu den Checks: siehe dazu auch {@link
     * WitaTalOrderService#doTerminverschiebung(Long, LocalDate, AKUser, boolean, String)} <br/>
     * <p/>
     * Tritt bei der Terminverschiebung ein Fehler auf, wird ein AutomationError generiert!
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return VA-IDs, zu denen eine WITA TV ausgeloest wurde bzw. der WITA KWT angepasst wurde
     */
    //@formatter:on
    Collection<String> processAutomatableErlmTvs(AKUser user);


    //@formatter:off
    /**
     * Fuehrt eine automatische Verarbeitung von eingegangenen ERLMs auf eine STR-AUF durch. <br/>
     * <p/>
     * Der Service ermittelt alle STR-ERLMs zu STR-AUFH (mit M-net = aufnehmender Provider) und versucht, zu diesen
     * VAs einen WITA Storno zu erstellen.
     * <p/>
     * Dabei werden folgende Aktionen/Checks durchgefuehrt: <br/>
     * <ul>
     *     <li>the request type is STR_AUFH_AUF</li>
     *     <li>M-net is the receiving carrier for the preagreement</li>
     *     <li>state of last WbciRequest for the VA is 'STORNO_ERLM_EMPFANGEN'</li>
     *     <li>VA has no automation error entry</li>
     *     <li>VA has at least one WITA request</li>
     * </ul>
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return VA-IDs, zu denen eine WITA Storno ausgeloest wurde
     */
    //@formatter:on
    Collection<String> processAutomatableStrAufhErlms(AKUser user);

    //@formatter:off
    /**
     * Checks whether a WITA-Order can be processed automatically after sending an AKM-TR to the partner carrier.
     * In this case M-Net is the receiving carrier.<br/>
     * <p/>
     * WITA-Order automation would be possible if {@link UebernahmeRessourceMeldung#uebernahme} and
     * {@link UebernahmeRessourceMeldung#leitungen} fit to the criteria:
     * <ul>
     *    <li>WBCI Geschaeftsfall is automatable </li>
     *    <li>Ressourcenübernahme true, 1 Leitung    =>      {@link CBVorgang#TYP_ANBIETERWECHSEL} </li>
     *    <li>Ressourenübernahme true, 1+n Leitungen =>      manual processing, automation error in {@link WbciGeschaeftsfall#automationTasks}/li>
     *    <li>Ressourenübernahme true, 0 Leitungen   =>
     *    <ul>
     *        <li>1 Leitung RUEM-VA => {@link CBVorgang#TYP_ANBIETERWECHSEL}</li>
     *        <li>!= 1 Leitung in RUEM-VA => manual processing, automation error in {@link WbciGeschaeftsfall#automationTasks}</li>
     *    </ul>
     *    <li>Ressourcenübernahme false, 1 hurrican order    =>   {@link CBVorgang#TYP_NEU}</li>
     *    <li>Ressourcenübernahme false, != 1 hurrican order =>   manual processing, automation error in {@link WbciGeschaeftsfall#automationTasks}</li>
     * </ul>
     *
     * @return <code>true</code> if WITA-Order can be processed automatically, otherwise <code>false</code>
     */
    //@formatter:on
    boolean canWitaOrderBeProcessedAutomatically(String vorabstimmungsId);

}
