/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 13:31:12
 */
package de.mnet.wita.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamVorgang;

public interface TaskDao extends StoreDAO {

    /**
     * Sucht nach offenen TAM-Usertasks.
     */
    List<TamVorgang> findOpenTamTasks();

    /**
     * Sucht nach offenen TAM-Usertasks nach dem Wiedervorlage-Datum.
     */
    List<TamVorgang> findOpenTamTasksWithWiedervorlage();

    /**
     * Laedt einen KueDtUserTask anhand der externenAuftragsnummer oder {@code null}.
     */
    KueDtUserTask findKueDtTask(String externeAuftragsnummer);

    /**
     * Laedt den letzten AkmPvUserTask anhand der externenAuftragsnummer oder {@code null}.
     */
    AkmPvUserTask findAkmPvUserTask(String externeAuftragsnummer);

    /**
     * Laedt den letzten AkmPvUserTask anhand der Vertragsnummer.
     */
    AkmPvUserTask findAkmPvUserTaskByContractId(String vertragsnummer);

    /**
     * Laedt einen offenen AkmPvUserTask anhand der VertragsNr.
     */
    AkmPvUserTask findOpenAkmPvUserTaskByVertragNr(String vertragsNr);

    List<AbgebendeLeitungenUserTask> findAbgebendeLeitungenUserTask(String externeAuftragsnummer);

    /**
     * Sucht nach offenen AbgebendenLeitungen-Usertasks nach dem Wiedervorlage-Datum.
     */
    List<AbgebendeLeitungenVorgang> findOpenAbgebendeLeitungenTasksWithWiedervorlage();

}
