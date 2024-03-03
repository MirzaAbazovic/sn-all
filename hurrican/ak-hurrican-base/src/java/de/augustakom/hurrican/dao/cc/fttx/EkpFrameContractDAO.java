/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 16:30:21
 */
package de.augustakom.hurrican.dao.cc.fttx;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * DAO-Interface fuer die Verwaltung von {@link EkpFrameContract} Objekten.
 */
@ObjectsAreNonnullByDefault
public interface EkpFrameContractDAO extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Ermittelt das {@link EkpFrameContract} Objekt zu der angegebenen {@code contractId}
     *
     * @param ekpId
     * @param frameContractId
     * @return
     */
    @CheckForNull
    EkpFrameContract findEkpFrameContract(String ekpId, String frameContractId);

    /**
     * Ermittelt das Zuordnungsobjekt {@link Auftrag2EkpFrameContract} zu einem bestimmten Auftrag und zu einem
     * bestimmten Zeitpunkt. <br> Das Datum wird dabei mit assignedFrom<=validAt und assignedTo>validAt geprueft.
     *
     * @param auftragId ID des Hurrican Auftrags
     * @param validAt   Datum, zu dem der EkpFrame-Contract ermittelt werden soll
     * @return
     */
    @CheckForNull
    Auftrag2EkpFrameContract findAuftrag2EkpFrameContract(Long auftragId, LocalDate validAt);

    /**
     * Loescht das Objekt vom Typ {@link Auftrag2EkpFrameContract} mit der angegebenen Id
     *
     * @param id
     */
    void deleteAuftrag2EkpFrameContract(Long id);

    /**
     * @param ekpFrameContract
     */
    void delete(EkpFrameContract ekpFrameContract);

    /**
     * Prüft ob für die gegebenen FrameContracts mindestens ein {@link Auftrag2EkpFrameContract} existiert welches zum
     * aktuellen Zeitpunkt oder in der Zukunft gültig ist.
     *
     * @param ekpFrameContracts
     * @return
     */
    boolean hasAuftrag2EkpFrameContract(List<EkpFrameContract> ekpFrameContracts);

    /**
     * @return alle EkpFrameContract
     */
    List<EkpFrameContract> findAll();
}


