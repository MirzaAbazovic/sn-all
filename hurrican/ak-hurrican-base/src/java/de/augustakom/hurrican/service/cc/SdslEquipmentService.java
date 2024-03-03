/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.14
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.service.base.exceptions.FindException;

public interface SdslEquipmentService extends ICCService {

    /**
     * Liefert eine sortierte Liste von freien SDSL Equipments am HVT Standort mit der ID hvtStandortId. Die Equipments
     * sind dabei nach ihrem HW-EQN sortiert.
     */
    @Nonnull
    public List<Equipment> findFreeEquipmentsByStandortSorted(Long hvtStandortId, List<Long> physikTypIds)
            throws FindException;

    /**
     * Gruppiert die gegebenen Equipments anhand ihres Schicht2Protokoll. Die Reihenfolge der Equipments mit dem selben
     * Schicht2Protokoll bleibt dabei unverändert. Für Equipments die kein Schicht2Protokoll explizit gesetzt haben,
     * wird ATM verwendet.
     */
    @Nonnull
    public Map<Schicht2Protokoll, List<Equipment>> groupBySchicht2Protokoll(List<Equipment> equipments);

    /**
     * Ordnet einem SDSL-Auftrag und den dazugehoerigen SDSL-N-Draht-Option Auftraegen Rangierungen gemaess der
     * Konfiguration des Produktes zu.
     *
     * @param esId  endstelle der Rangierung des SDSL-Auftrags (nicht der eines N-Draht-Option Auftrags!)
     * @param today das heutige Datum
     * @return ein leeres AKWarning-Objekt, wenn keine (fachlichen) Fehler aufgetreten sind, ansonsten enthaelt es
     * entsprechende Fehlermeldungen. Es wird niemals null zurueckgegeben.
     */
    @Nonnull
    AKWarnings assignSdslNdraht(long esId, final Date today) throws ValidationException;

    /**
     * Ermittelt fuer den angegebenen Port den zugehoerigen 4er-Block, in dem er sich befindet
     *
     * @param equipmentId
     * @return eine Liste mit genau 4 Ports eines 4er Blocks oder eine leere Liste wenn - der Port kein S(H)DSL-Port ist
     * - es sich nicht um eine Physik vom Typ Huawei oder Alcatel-IP handelt
     * @throws RuntimeException wenn irgendetwas schief geht
     */
    List<Equipment> findViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslam(long equipmentId);
}
