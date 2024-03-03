/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:32:55
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von Cross-Connections.
 *
 *
 */
public interface EQCrossConnectionService extends ICCService {

    /**
     * Speichert das angegebene Cross-Connection Objekt ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveEQCrossConnection(EQCrossConnection toSave) throws StoreException;

    /**
     * Speichert die angegebenen Cross Connections ab.
     *
     * @param toSave zu speichernede Cross Connections
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn die Validierung der CrossConnections einen Fehler ergab.
     */
    void saveEQCrossConnections(List<EQCrossConnection> toSave) throws StoreException, ValidationException;

    /**
     * Ermittelt alle Cross-Connections zu einem Equipment, die zum angegebenen Datum gueltig sind.
     *
     * @param equipmentId ID des Equipments (=Ports), dessen Cross-Connections ermittelt werden sollen
     * @param validDate   Datum, zu dem die Cross-Connections ermittelt werden sollen
     * @return Liste mit den Cross-Connections zum angegebenen Equipment u. Datum
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<EQCrossConnection> findEQCrossConnections(Long equipmentId, Date validDate) throws FindException;

    /**
     * Ermittelt alle Cross-Connections zu einem Equipment
     *
     * @param equipmentId ID des Equipments (=Ports), dessen Cross-Connections ermittelt werden sollen
     * @return Liste mit den Cross-Connections zum angegebenen Equipment
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<EQCrossConnection> findEQCrossConnections(Long equipmentId) throws FindException;

    /**
     * Berechnet die Standard-CrossConnection-Werte auf dem gegebenen Port und liefert sie als Liste zurueck. Die
     * CrossConnection-Berechnung setzt voraus, dass der angegebene Port einem Sub-Rack zugeordnet ist!
     *
     * @param port           Der Port, fuer den die CrossConnections berechnet werden sollen
     * @param userW          Der User, der als UserW in die CrossConnections geschrieben wird
     * @param activationDate Das Datum, zu dem die neu berechneten CrossConnections gueltig sein sollen
     * @param auftragId      Der Auftrag wird genutzt, um festzustellen, ob eine VoIP-CC berechnet werden soll
     * @return Eine Liste an CrossConnections
     * @throws FindException Falls ein Fehler aufgetreten ist (Beispiel: Es konnte kein zugehoeriger Auftrag ermittelt
     *                       werden, um den VoIP-Status zu ermitteln)
     */
    List<EQCrossConnection> calculateDefaultCcs(Equipment port, String userW, @Nonnull Date activationDate,
            Long auftragId)
            throws FindException;

    /**
     * Berechnet die Standard-CrossConnection-Werte auf dem gegebenen Port und liefert sie als Liste zurueck. Die
     * CrossConnection-Berechnung setzt voraus, dass der angegebene Port einem Sub-Rack zugeordnet ist!
     *
     * @param port    Der Port, fuer den die CrossConnections berechnet werden sollen
     * @param userW   Der User, der als UserW in die CrossConnections geschrieben wird
     * @param hasVoip VoIP-Status: {@code true}: eine VoIP-CrossConnection soll angelegt werden; {@code false}: keine
     *                VoIP-CC
     * @param hasIpV6 {@code true}, falls IpV6 im Dualstack oder DualStackLight (nur IPv6) aktivieren
     * @return Eine Liste an CrossConnections
     * @throws FindException Falls ein Fehler aufgetreten ist (Beispiel: Es konnte kein zugehoeriger Auftrag ermittelt
     *                       werden, um den VoIP-Status zu ermitteln)
     */
    List<EQCrossConnection> calculateDefaultCcs(Equipment port, String userW, @Nonnull Date activationDate,
            boolean hasVoip, boolean hasBusinessCPE, boolean hasIpV6) throws FindException;

    /**
     * Prüft ob Crossconnections für das Equipment berechnet werden können. Es wird das
     * FeatureName.CROSSCONNECTION_BY_HVT_TECHNIK geprüft, bzw. wenn das Feature noch nicht eingeschaltet ist die "alte"
     * Prüfung ob der Port in einem Subrack ist.
     *
     * @param port
     * @return true wenn CrossConnections berechnet werden können
     * @throws FindException
     */
    boolean isCrossConnectionEnabled(Equipment port) throws FindException;

    /**
     * Liefert einen jetzt und in Zukunft noch freien Virtual Channel eines Pools zurueck
     *
     * @param brasPool Der zu nutzende Pool
     * @return Freie VC, oder {@code null}, falls der Pool leer ist
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    Integer getVcFromPool(BrasPool brasPool) throws FindException;

    /**
     * Liefert einen zwischen den angegebenen Daten freien Virtual Channel eines Pools zurueck
     *
     * @param brasPool Der zu nutzende Pool
     * @param from     Datum, ab dem der VC genutzt werden soll. Falls {@code null} wird das heutige Datum angenommen.
     * @param till     Datum, bis zu dem der VC genutzt werden soll. Falls {@code null} wird das Hurrican-End-Date
     *                 angenommen (siehe {@link DateTools}).
     * @return Freie VC, oder {@code null}, falls der Pool leer ist
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    Integer getVcFromPool(BrasPool brasPool, Date from, Date till) throws FindException;

    /**
     * Liefert einen zwischen den angegebenen Daten freien Virtual Channel eines Pools zurueck
     *
     * @param brasPool Der zu nutzende Pool
     * @param from     Datum, ab dem der VC genutzt werden soll. Falls {@code null} wird das heutige Datum angenommen.
     * @param till     Datum, bis zu dem der VC genutzt werden soll. Falls {@code null} wird das Hurrican-End-Date
     *                 angenommen (siehe {@link DateTools}).
     * @param skip     VCs, die nicht genutzt werden sollen, auch wenn sie im Pool noch nicht belegt sind
     * @return Freie VC, oder {@code null}, falls der Pool leer ist
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    Integer getVcFromPool(BrasPool brasPool, Date from, Date till, Set<Integer> skip) throws FindException;

    /**
     * Liefert eine Liste aller BRAS Pools zurueck
     */
    List<BrasPool> getAllBrasPools() throws FindException;

    /**
     * Liefert den BRAS Pool mit der gegebenen Id
     */
    BrasPool findBrasPoolById(Long brasPoolId) throws FindException;

    /**
     * Liefert eine Liste der BRAS Pools mit dem gegebenen Namenspraefix
     *
     * @return Liste (eventuell leer), nie {@code null}
     */
    List<BrasPool> findBrasPoolByNamePrefix(String poolPrefix) throws FindException;

    /**
     * Loescht alle Cross Connections des uebergenenen Ports.
     *
     * @param equipmentId id des Ports dessen Cross Connections geloescht werden sollen
     */
    void deleteEQCrossConnectionsOfEquipment(Long equipmentId) throws DeleteException;

    /**
     * Ermittelt die aktuellen CrossConnections auf dem angegebenen Equipment. Falls die CrossConnections abweichend vom
     * Default sind, werden die alten CCs deaktiviert und neue Defaults berechnet.
     */
    void defineDefaultCrossConnections4Port(Equipment equipment, Long auftragId, Date changeDate, Boolean vierDrahtProdukt, Long sessionId)
            throws StoreException;

    void defineDefaultCrossConnections4Port(Equipment equipment, Long auftragId,
            Date changeDate, Boolean vierDrahtProdukt, Long sessionId, List<EQCrossConnection> crossConnectionsToSet,
            List<EQCrossConnection> crossConnectionsToDeactivate) throws StoreException;

    /**
     * Beendet alle Cross Connections der Endstelle.
     */
    Pair<Rangierung, Equipment> deactivateCrossConnections4Endstelle(Endstelle endstelle, Date changeDate)
            throws FindException, StoreException;

    /**
     * Prueft, ob die Bedingungen erfuellt sind, um CCs zu berechnen. Sind alle Bedingungen erfuellt, ist der
     * Rueckgabewert null. Andernfalls enthaellt der String die Fehlermeldung.
     */
    String checkCcsAllowed(Endstelle endstelle) throws FindException;
}
