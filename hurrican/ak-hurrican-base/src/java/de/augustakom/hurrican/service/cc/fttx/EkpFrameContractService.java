/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 15:57:41
 */
package de.augustakom.hurrican.service.cc.fttx;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Service zum Verwalten von Modell-Objekten des Typs EkpFrameContract
 */
@ObjectsAreNonnullByDefault
public interface EkpFrameContractService extends ICCService {

    /**
     * speichert das angegebene {@link A10NspPort} Objekt.
     *
     * @param a10nspPort
     * @return
     */
    A10NspPort saveA10NspPort(A10NspPort a10nspPort);

    /**
     * findet eine {@link A10NspPort} Objekt anhand seiner Id
     *
     * @param id
     * @return
     */
    A10NspPort findA10NspPortById(Long id);

    /**
     * speichert das angegebene {@link EkpFrameContract} Objekt.
     *
     * @param ekpFrameContract
     * @return
     */
    public EkpFrameContract saveEkpFrameContract(EkpFrameContract ekpFrameContract);

    /**
     * speichert das angegebene {@link Auftrag2EkpFrameContract} Objekt
     *
     * @param auftrag2EkpFrameContract
     * @return
     */
    public Auftrag2EkpFrameContract saveAuftrag2EkpFrameContract(Auftrag2EkpFrameContract auftrag2EkpFrameContract);

    /**
     * Ermittelt das {@link EkpFrameContract} Objekt zu der angegebenen {@code ekpId} und {@code contractId}
     *
     * @param ekpId
     * @param contractId
     * @return
     */
    @Nullable
    public EkpFrameContract findEkpFrameContract(String ekpId, String contractId);

    /**
     * Liefert alle EkpFrameContract.
     *
     * @return
     */
    public List<EkpFrameContract> findAllEkpFrameContract();

    /**
     * Löschen einen EKP (cascade).
     *
     * @param ekpFrameContract
     */
    void deleteEkpFrameContract(EkpFrameContract ekpFrameContract);

    /**
     * Ordnet den angegebenen {@link EkpFrameContract} dem {@link Auftrag} zu. <br> Ueber das Datum {@code validAt} wird
     * angegeben, ab wann der Ekp-Rahmenvertrag dem Auftrag zugeordnet sein soll. <br> <br> Die Methode prueft, ob dem
     * Auftrag zum angegebenen Datum ({@code validAt}) bereits ein EKP-Rahmenvertrag zugeordnet ist. Sollte dies der
     * Fall sein und der bisher zugeordnete EKP-Rahmenvertrag vom neuen ({@code toAssign} ) abweichend sein, so wird die
     * alte Zuordnung zum Datum {@code validAt} beendet und der neue EKP-Rahmenvertrag ( {@code toAssign}) zu diesem
     * Datum zugeordnet.
     *
     * @param toAssign
     * @param auftrag
     * @param validAt
     * @param auftragAktion (optionale) Angabe der {@link AuftragAktion}, durch die die EKP-Zuordnung geaendert wurde
     */
    public Auftrag2EkpFrameContract assignEkpFrameContract2Auftrag(EkpFrameContract toAssign, Auftrag auftrag, LocalDate validAt,
            @Nullable AuftragAktion auftragAktion);

    /**
     * Ermittelt das Zuordnungsobjekt {@link Auftrag2EkpFrameContract} zu einem bestimmten Auftrag und zu einem
     * bestimmten Zeitpunkt. <br> Das Datum wird dabei mit assignedFrom<=validAt und assignedTo>validAt geprueft.
     *
     * @param auftragId ID des Hurrican Auftrags
     * @param validAt   Datum, zu dem der EkpFrame-Contract ermittelt werden soll
     * @return
     * @throws IncorrectResultSizeDataAccessException wenn zum angegebenen Zeitpunkt mehr(!) als ein {@link
     *                                                EkpFrameContract} dem Auftrag zugeordnet ist.
     */
    @CheckForNull
    Auftrag2EkpFrameContract findAuftrag2EkpFrameContract(Long auftragId, LocalDate validAt);


    /**
     * Ermittelt das Zuordnungsobject {@link Auftrag2EkpFrameContract} zu einem Auftrag; Abhaengig von {@code
     * useDefaultEkp} wird der Default-EKP (M-net) geladen, falls dem Auftrag kein EKP zugeordnet ist.
     *
     * @param auftragId
     * @param when
     * @param useDefaultEkp
     * @return
     * @throws FindException
     */
    @CheckForNull
    EkpFrameContract findEkp4AuftragOrDefaultMnet(final Long auftragId, final LocalDate when, boolean useDefaultEkp) throws FindException;

    /**
     * Ermittelt die Zuordnungsobjekte {@link Auftrag2EkpFrameContract} zu einem Auftrag anhand der als Parameter
     * angegebenen Properties; Es werden keine Zeitraeume betrachtet, sondern lediglich Uebereinstimmungen von {@code
     * assignedFrom} und {@code assignedTo}
     *
     * @param auftragId
     * @param assignedFrom
     * @param assignedTo
     * @return
     */
    List<Auftrag2EkpFrameContract> findAuftrag2EkpFrameContract(Long auftragId, LocalDate assignedFrom,
            LocalDate assignedTo);

    /**
     * Deaktiviert (loescht) die {@link Auftrag2EkpFrameContract} Zuordnung, die durch die angegebene {@link
     * AuftragAktion} angelegt wurde; die durch die {@link AuftragAktion} deaktivierte {@link Auftrag2EkpFrameContract}
     * Zuordnung wird dagegen wieder aktiviert.
     *
     * @param auftragId
     * @param auftragAktion {@link AuftragAktion}, die rueckgaengig gemacht werden soll
     * @return reaktivierte {@link Auftrag2EkpFrameContract} Zuordnung
     * @throws StoreException
     * @throws FindException
     */
    Auftrag2EkpFrameContract cancelEkpFrameContractAssignment(Long auftragId, AuftragAktion auftragAktion);

    /**
     * Prüft ob dem gegebenen {@link EkpFrameContract} aktive Aufträge zugeordnet sind.
     *
     * @param ekpFrameContract
     * @return true falls Aufträge zugeordnet sind
     */
    boolean hasAuftrag2EkpFrameContract(EkpFrameContract ekpFrameContract);

    /**
     * Prüft ob dem gegebenen {@link A10NspPort} (über dessen Zuordnung zu {@link EkpFrameContract}) aktive Aufträge
     * zugeordnet sind.
     *
     * @param a10NspPort
     * @return
     */
    boolean hasAuftrag2EkpFrameContract(A10NspPort a10NspPort);

    /**
     * Prüft ob dem gegebenen {@link A10Nsp} (über die Zuordnung aller seiner {@link A10NspPort} zu {@link
     * EkpFrameContract}) aktive Aufträge zugeordnet sind.
     *
     * @param a10Nsp
     * @return
     */
    boolean hasAuftrag2EkpFrameContract(A10Nsp a10Nsp);

    /**
     * Findet für den uebergegenen EKP-Contract für die OLT den A10-NSP Port.
     *
     * @param contract EKP-Contract
     * @param oltId    OLD
     * @return gefundener Port oder default-Port falls kein Port für die OLT definiert oder null, falls kein
     * default-Port definiert ist
     */
    @CheckForNull
    A10NspPort findA10NspPort(EkpFrameContract contract, Long oltId);

    /**
     * Speichert eine A10-NSP.
     *
     * @param toStore
     * @return
     */
    A10Nsp saveA10Nsp(A10Nsp a10Nsp);

    /**
     * Generiert einen neuen {@link A10NspPort} und gibt diesen zurueck. Dem {@link A10NspPort} wird die angegebene
     * {@link A10Nsp} zugeordnet.
     *
     * @param a10Nsp
     * @return
     * @throws StoreException
     */
    A10NspPort createA10NspPort(A10Nsp a10Nsp) throws StoreException;

    /**
     * Loescht ein Objekt vom Typ {@link Auftrag2EkpFrameContract}
     *
     * @param id der zu loeschenden Entitaet
     */
    void deleteAuftrag2EkpFrameContract(Long id);

    /**
     * Liefert den MNET MNET-001 contract als Default-EkpFrameContract zurück.
     *
     * @return
     */
    EkpFrameContract getDefaultEkpFrameContract();

    /**
     * Liefert alle A10-NSPs.
     *
     * @return
     */
    public List<A10Nsp> findAllA10Nsp();

    /**
     * Liefert alle A10-NSP Ports fuer eine A10-NSP.
     *
     * @param a10Nsp
     * @return
     * @throws FindException
     */
    public List<A10NspPort> findA10NspPorts(A10Nsp a10Nsp) throws FindException;

    /**
     * Loescht den uebergebenen A10-NSP-Port.
     *
     * @param selectedA10NspPort
     */
    public void deleteA10NspPort(A10NspPort selectedA10NspPort);

    /**
     * Ermittelt die {@link EkpFrameContract}s die dem angegebenen A10NspPort zugeordnet sind
     *
     * @param a10NspPort
     * @return
     */
    List<EkpFrameContract> findEkpFrameContractsByA10NspPort(A10NspPort a10NspPort);

    /**
     * Prueft ob es pro {@link EkpFrameContract} und HWOlt bereits einen {@link A10NspPort} gibt
     *
     * @param ekpFrameContract
     * @param olt
     * @return
     */
    boolean isHwOltAssignedToEkpFrameContract(EkpFrameContract ekpFrameContract, HWOlt olt);

    /**
     * Loescht den uebergebenen A10-NSP.
     *
     * @param a10Nsp
     * @throws FindException
     */
    void deleteA10Nsp(A10Nsp a10Nsp) throws FindException;


    /**
     * Prueft ob der {@link A10NspPort} dem {@link EkpFrameContract} zugeordnet werden kann, indem verglichen wird, ob
     * eine {@link HWOlt} eines bereits zugewiesenen {@link A10NspPort} auch dem angegebenen {@link A10NspPort}
     * zugewiesen ist.
     *
     * @param ekpFrameContract
     * @param a10NspPort
     * @return wenn die Zuordnung als valide geprueft wurde ein leeres {@link AKWarnings}-Objekt, andernfalls enthaelt
     * es die entsprechenden Warnungsmeldungen
     */
    AKWarnings checkA10NspPortAssignableToEkp(EkpFrameContract ekpFrameContract, A10NspPort a10NspPort);

    /**
     * Filter die uebergene Liste an {@link HWOlt}s. Es werden die {@link HWOlt}-Objekte aus der Liste entfernt, die den
     * {@link EkpFrameContract}s, denen der {@link A10NspPort} zugeordnet ist, bereits durch einen anderen {@link
     * A10NspPort} zugeordnet sind.
     *
     * @param a10NspPort
     * @param oltsToFilter
     * @return ein {@link AKWarnings}-Objekt, dass eine Meldung pro entfernter {@link HWOlt} enthaelt, oder ein leeres
     * {@link AKWarnings}-Objekt, falls nichts gefiltert wurde.
     */
    AKWarnings filterNotAssignableOlts(A10NspPort a10NspPort, List<HWOlt> oltsToFilter);

}
