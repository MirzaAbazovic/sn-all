/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 11:40:28
 */
package de.mnet.wita.ticketing;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wita.message.MessageWithSentToBsiFlag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.service.WitaService;

/**
 * WitaBsiProtokollService ist zustaendig fuer die Protokollierung von ein- und ausgehenden Nachrichten nach BSI
 */
public interface WitaBsiProtokollService extends WitaService {

    /**
     * Protokollierung einer Wita-Nachricht
     *
     * @param nachricht zu protokollierende Nachricht
     * @throws StoreException fliegt wenn auf Datenbank nicht geschrieben werden kann oder der BSI-Webservice nicht
     *                        erreichbar ist
     */
    <T extends MwfEntity & MessageWithSentToBsiFlag> void protokolliereNachricht(T nachricht) throws StoreException;

    /**
     * Erstellt einen BSI-Protokolleintrag fuer einen vorgehaltenen WITA-Request. <br> (Der Client der Methode hat
     * sicherzustellen, dass es sich bei dem Request um eine vorgehaltene Nachricht handelt!)
     *
     * @param witaRequest WITA-Request, fuer den ein BSI-Protokolleintrag erstellt werden soll
     * @throws StoreException
     */
    <T extends MnetWitaRequest & MessageWithSentToBsiFlag> void protokolliereDelay(T witaRequest) throws StoreException;

    /**
     * Status von PV-Meldungen auf DONT_SENT_TO_BSI umsetzen, wenn nach mehrmalingen versuchen immer noch kein
     * eindeutiger Hurrican-Auftrag zugeordnet werden konnte
     * <p/>
     * Dazu werden die nach BSI gesendeten PV-Meldungsworkflows (gruppieren anhand der ext. Auftragsnummer) gesucht
     * Letzte Meldung zum Workflow holen, wenn entweder ABBM-PV oder ERLM-PV dann alle Meldungen dieses Workflows auf
     * DONT_SEND setzen
     */
    void dontSentPvMeldungenIfAuftragIsNotSet();
}
