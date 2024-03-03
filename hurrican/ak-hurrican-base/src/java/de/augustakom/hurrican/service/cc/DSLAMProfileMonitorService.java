/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 10:46:50
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitor;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service zur Ueberwachung von Auftraegen bei denen eine Stoerung auf Grund zu hoher DSLAM-Profile auftritt
 */
public interface DSLAMProfileMonitorService extends ICCService {

    /**
     * Erstellt fuer die angegebene Auftragsnummer ein {@link DSLAMProfileMonitor} und sichert dieses fuer die spaetere
     * Verarbeitung.
     *
     * @param auftragId Auftragsnummer des zu ueberwachenden Auftrags.
     * @throws StoreException
     */
    void createDSLAMProfileMonitor(Long auftragId) throws StoreException;

    /**
     * @param ccAuftragId Hurrican Auftrags-ID
     * @return true wenn der Auftrag ueberwacht werden muss, andernfalls false
     * @throws FindException
     */
    boolean needsMonitoring(Long ccAuftragId) throws FindException;

    /**
     * Ermittelt alle Auftragsnummern, die aktuell in Ueberwachung sind.
     *
     * @return
     * @throws FindException
     */
    Collection<Long> findCurrentlyMonitoredAuftragIds() throws FindException;


    /**
     * Ermittelt via CPS fuer eine gegebene techn. Auftrags-ID die momentan gehaltene attainable downstream und upstream
     * Bitrate
     *
     * @param ccAuftragId techn. Auftrags-ID (darf nicht null sein)
     * @param sessionId   darf nicht null sein
     * @return Pair&lt;downstream Bitrate, upstream Bitrate&gt; wenn Daten ermittelt werden konnten, andernfalls null
     */
    Pair<Integer, Integer> cpsQueryAttainableBitrate(Long ccAuftragId, Long sessionId) throws FindException;

    /**
     * Deaktiviert die Ueberwachung des Auftrags fuer die angegebene Auftragsnummer.
     *
     * @param auftragId
     * @throws FindException
     */
    void deactivateMonitoring(Long auftragId) throws FindException;
}
