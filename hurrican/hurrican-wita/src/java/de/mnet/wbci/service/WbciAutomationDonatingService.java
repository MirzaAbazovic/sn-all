/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.14
 */
package de.mnet.wbci.service;

import java.util.*;

import de.augustakom.authentication.model.AKUser;

/**
 * Service-Interface fuer WBCI Automatisierungen, bei denen M-net die <b>abgebende</b> Rolle einnimmt.
 */
public interface WbciAutomationDonatingService extends WbciService {

    // @formatter:off
    /**
     * Does an automatic processing of all automatable outgoing RUEM-VAs.<br/> (M-net is the donating carrier)
     * For every automatable RUEM-VA following actions will be executed:<br/>
     * <ol>
     *     <li>cancellation of Taifun order(s)</li>
     *     <ul>
     *         <li>generation and printing cancellation confirmation (if necessary)</li>
     *     </ul>
     *     <li>cancellation of Hurrican order(s) with</li>
     *     <ul>
     *         <li>cancellation of ports</li>
     *         <li>creation of 'Bauauftrag'</li>
     *     </ul>
     * </ol>
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return the VorabstimmungIDs of RUEM-VAs that were automatically processed
     */
    // @formatter:on
    Collection<String> processAutomatableOutgoingRuemVas(AKUser user, Long sessionId);


    //@formatter:off
    /**
     * Fuehrt eine automatische Verarbeitung von ein-/ausgehenden ERLMs auf eine STR-AUF durch, bei denen
     * M-net der abgebende Provider ist. <br/>
     * <p/>
     * Der Service ermittelt alle STR-ERLMs zu STR-AUFH (mit M-net = abgebender Provider) und versucht, zu diesen
     * VAs die Auftragskuendigung rueckgaengig zu machen sowie evtl. vorhandene WITA Kuendigungen zu stornieren.
     * <p/>
     * Dabei werden folgende Aktionen/Checks durchgefuehrt: <br/>
     * <ul>
     *     <li>the request type is STR_AUFH_AUF</li>
     *     <li>M-net is the donating carrier for the preagreement</li>
     *     <li>state of last WbciRequest for the VA is 'STORNO_ERLM_EMPFANGEN' or 'STORNO_ERLM_VERSENDET'</li>
     *     <li>VA has no automation error entry</li>
     * </ul>
     *
     * @param user the current user who triggers this method (e.g. the scheduler)
     * @return VA-IDs, zu denen eine WITA Storno ausgeloest wurde
     */
    //@formatter:on
    Collection<String> processAutomatableStrAufhErlmsDonating(AKUser user, Long sessionId);

}
