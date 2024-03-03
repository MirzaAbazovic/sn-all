/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 11:30:51
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDluView;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.model.cc.view.HWBaugruppeView;
import de.augustakom.hurrican.model.exceptions.ModelCalculationException;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationSearchCriteria;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Interface zur Definition eines Services, der fuer Hardware-Komponenten zustaendig ist. <br>
 *
 *
 */
public interface HWService extends ICCService {

    /**
     * Sucht nach allen Racks, die einem best. HVT zugeordnet sind. <br> (Es werden nur Racks ermittelt, die ein
     * Gueltig-Bis Datum '01.01.2200' besitzen.)
     *
     * @param hvtIdStandort Standort ID des HVT-Standorts
     * @return Liste mit HWRack-Objekten oder <code>null</code>
     * @throws FindException wenn bei der Suche ein Fehler auftritt.
     */
    List<HWRack> findRacks(Long hvtIdStandort) throws FindException;

    boolean standortContainsGFastTech(Long hvtIDStandort) throws FindException;

    /**
     * Speichert ein HWRack-Objekt
     *
     * @param toSave zu speicherndes Objekt
     * @return
     * @throws StoreException falls ein Fehler auftrat.
     */
    <T extends HWRack> T saveHWRack(T toSave) throws StoreException, ValidationException;

    /**
     * Ausfuehrung in neuer Transaktion!
     * 
     * @see HWService#saveHWRack(HWRack) 
     * @param toSave
     * @param <T>
     * @return
     * @throws StoreException
     * @throws ValidationException
     */
    <T extends HWRack> T saveHWRackNewTx(T toSave) throws StoreException, ValidationException;

    /**
     * Sucht nach Hardware-Definitionen der Typen (z.B. HWDlu, HWDslam, HWLtg, etc.)
     *
     * @param hvtIdStandort Id des HVT-Standorts
     * @param typ           Klasse des Hardware-Typs
     * @param onlyActive    Falls nur aktive Komponenten gesucht werden
     * @return Liste mit Objekten des gesuchten Typs, nie {@code null}
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    <T extends HWRack> List<T> findRacks(Long hvtIdStandort, Class<T> typ,
            Boolean onlyActive) throws FindException;

    /**
     * Sucht alle OLTs und GSLAMs zu einem Betriebsraum. Ein Betriebsraum kann FTTX_BR, aber auch ein FTTC_KVZ etc.
     * sein.
     */
    List<HWRack> findAllRacksForFtth(final Long betriebsraumId) throws FindException;

    /**
     * Liefert alle Baugruppen fuer einen best. HVT-Standort.
     *
     * @param hvtIdStandort Id des HVT-Standorts
     * @param onlyActive    Falls nur aktive Baugruppen, -racks gesucht werden
     * @return Liste mit Baugruppen-Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWBaugruppe> findBaugruppen(Long hvtIdStandort, Boolean onlyActive) throws FindException;

    /**
     * Ermittelt HW-Baugruppen ueber ein Example-Objekt.
     *
     * @param example Example-Objekt mit den Query-Parametern
     * @return Liste mit Objekten des Typs <code>HWBaugruppe</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<HWBaugruppe> findBaugruppen(HWBaugruppe example) throws FindException;

    /**
     * Ermittelt eine bestimmte Baugruppe ueber deren ID.
     *
     * @param bgId ID der gesuchten Baugruppe
     * @return Instanz von <code>HWBaugruppe</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     *
     */
    HWBaugruppe findBaugruppe(Long bgId) throws FindException;

    /**
     * Ermittelt alle Baugruppen des angegebenen Standorts als View-Objekt.
     *
     * @param hvtIdStandort
     * @return Liste mit Objekten des Typs {@link HWBaugruppeView}
     * @throws FindException
     */
    List<HWBaugruppeView> findHWBaugruppenViews(Long hvtIdStandort) throws FindException;

    /**
     * Speichert ein Objekt vom Typ <code>HWBaugruppe</code>
     *
     * @param baugruppe zu speicherndes Objekt.
     * @return
     * @throws StoreException
     * @throws ValidationException
     *
     */
    HWBaugruppe saveHWBaugruppe(HWBaugruppe baugruppe) throws StoreException, ValidationException;

    /**
     * Sucht alle Auftraege passend zum engegebenden Suchkriterium und erstellt Switch Migration View Objekte.
     * @param searchCriteria
     * @return
     */
    List<SwitchMigrationView> createSwitchMigrationViews(SwitchMigrationSearchCriteria searchCriteria);

    /**
     * Speichert ein Objekt vom Typ <code>HWBaugruppenTyp</code>
     *
     * @param hwBaugruppenTyp
     * @return
     * @throws StoreException
     */
     HWBaugruppenTyp saveHWBaugruppenTyp(HWBaugruppenTyp hwBaugruppenTyp) throws StoreException;

    /**
     * Ermittelt das Rack zu einer bestimmten Baugruppe (definiert ueber die angegebene ID). Je nach Typ des gesuchten
     * Racks wird die jeweilige Auspraegung (z.B. HWDslam) zurueck geliefert.
     *
     * @param hwBaugruppeId ID der Baugruppe deren Rack-Definition ermittelt werden soll
     * @return Objekt vom Typ HWRack (z.B. HWDslam oder HWDlu etc.)
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    HWRack findRackForBaugruppe(Long hwBaugruppeId) throws FindException;

    /**
     * Ermittelt einen DSLAM ueber dessen IP-Adresse.
     *
     * @param ipAddress IP-Adresse des gesuchten DSLAMs.
     * @return Objekt vom Typ <code>HWDslam</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    HWDslam findDslamByIP(String ipAddress) throws FindException;

    /**
     * Funktion liefert ein HWRack-Objekt anhand der Id
     *
     * @param rackId Id des gesuchten Objekts
     * @return Gesuchtes Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    HWRack findRackById(Long rackId) throws FindException;

    /**
     * Ausfuehrung in neuer(!) Transaktion!
     * @see HWService#findRackById(Long)
     * @param rackId
     * @return
     * @throws FindException
     */
    HWRack findRackByIdNewTx(Long rackId) throws FindException;

    /**
     * @param rackId    Id des zugewiesenen Racks
     * @param subrackId Id des zugewiesenen Subracks
     * @param modNumber modNumber der Baugruppe
     * @return die passende Baugruppe oder {@code null}, falls keine entsprechende gefunden wird
     * @throws FindException
     */
    HWBaugruppe findBaugruppe(Long rackId, Long subrackId, String modNumber) throws FindException;

    /**
     * Speichert ein HWSubrack-Objekt
     *
     * @param hwSubrack zu speicherndes Objekt
     * @throws ValidationException
     * @throws StoreException      falls ein Fehler auftrat.
     */
    void saveHWSubrack(HWSubrack hwSubrack) throws ValidationException, StoreException;

    /**
     * Findet ein bestimmtes Subrack
     *
     * @param hwSubrackId Die ID des Subracks
     * @return Das Subrack, oder {@code null}
     * @throws FindException
     *
     */
    HWSubrack findSubrackById(Long hwSubrackId) throws FindException;

    /**
     * Funktion liefert ein HWSubrack-Objekte anhand der HWRack-Id
     *
     * @param hwRackId Id des HWRacks, in dem das Subrack eingebaut ist
     * @return Liste von HWRacks, nie {@code null}
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWSubrack> findSubracksForRack(Long hwRackId) throws FindException;

    /**
     * Findet ein HwSubrack anhander des zuegoerigen HwRacks und seiner modNumber
     *
     * @param hwRackId
     * @param modNumber
     * @return den gesuchten Subrack oder {@code null}, falls kein Ergebnis gefunden wird
     * @throws FindException im Fall eines unerwarteten Fehlers oder falls mehr als 1 Ergebnis gefunden wird
     */
    HWSubrack findSubrackByHwRackAndModNumber(Long hwRackId, String modNumber) throws FindException;

    /**
     * Liefert alle Subracks fuer einen best. HVT-Standort.
     *
     * @param hvtIdStandort Id des HVT-Standorts
     * @return Liste mit Baugruppen-Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWSubrack> findSubracksForStandort(Long hvtIdStandort) throws FindException;

    /**
     * Funktion liefert alle Subrack-Typen.
     *
     * @return Liste mit Subrack-Typen, nie {@code null}
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWSubrackTyp> findAllSubrackTypes() throws FindException;

    /**
     * Funktion liefert alle Subrack-Typen fuer einen bestimmten Rack-Typen (siehe HWRack fuer Rack-Typen).
     *
     * @param rackType Der Typ des Racks
     * @return Liste mit Subrack-Typen, nie {@code null}
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWSubrackTyp> findAllSubrackTypes(String rackType) throws FindException;

    /**
     * Funktion liefert alle Baugruppen-Typ Objekte
     *
     * @return Liste mit allen Objekten vom Typ HWBaugruppenTyp
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWBaugruppenTyp> findAllBaugruppenTypen() throws FindException;

    /**
     * Ermittelt alle Baugruppen-Typen mit einem bestimmten Prefix-Namen (Wildcard wird angehaengt).
     *
     * @param prefix     zu verwendender Prefix fuer den BaugruppenTyp-Namen
     * @param onlyActive Flag, ob nur aktive Baugruppen-Typen ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>HWBaugruppenTyp</code>
     * @throws FindException wenn bei der Abfrage einer Fehler auftritt.
     *
     */
    List<HWBaugruppenTyp> findBaugruppenTypen(String prefix, boolean onlyActive) throws FindException;

    /**
     * Ermittelt eine Liste mit den EWSD-Baugruppen eines bestimmten HVTs. <br>
     *
     * @param hvtIdStandort ID des HVT-Standorts.
     * @param onlyFree      Flag, ob nur freie (true) oder alle (false) Baugruppen ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>HWDluView</code>, die je eine Baugruppe darstellen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<HWDluView> findEWSDBaugruppen(Long hvtIdStandort, boolean onlyFree) throws FindException;

    /**
     * Funktion liefert ein HW-Rack Objekt anhand der Geraetebezeichnung
     *
     * @param bezeichnung Geraetebezeichnung des Racks
     * @return gesuchtes HW-Rack Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    HWRack findRackByBezeichnung(String bezeichnung) throws FindException;

    /**
     * Funktion liefert ein HW-DSLAM Objekt anhand der alternativen GSLAM Bezeichnung (altGslamBez ist das in Hurrican
     * hinterlegte Mapping fuer die in Command abweichenden GSLAM Bezeichnungen)
     *
     * @param altGslamBez alternative GSLAM Bezeichnung
     * @return gesuchter GSLAM (HW-DSLAM) Objekt
     */
    @Nullable
    HWDslam findGslamByAltBez(@Nullable String altGslamBez) throws FindException;

    /**
     * Funktion liefert ein aktives HW-Rack Objekt anhand der Geraetebezeichnung
     *
     * @param bezeichnung Geraetebezeichnung des Racks
     * @return gesuchtes HW-Rack Objekt
     * @throws FindException Falls ein Fehler auftrat
     */
    HWRack findActiveRackByBezeichnung(String bezeichnung) throws FindException;

    /**
     * Liefert alle Baugruppen zu einem bestimmten Rack
     *
     * @param rackId Id des Rack
     * @return Liste mit gesuchten Baugruppen, nie {@code null}
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWBaugruppe> findBaugruppen4Rack(Long rackId) throws FindException;

    HWBaugruppe findBaugruppe4RackWithModName(String modName) throws FindException;

    /**
     * Emittelt HW-Racks ueber die Management-Bezeichnung der Baugruppe. (Dies ist der Name der Baugruppe, wie er in den
     * verschiedenen Management-Systemen der Netztechnik hinterlegt ist.)
     *
     * @param managementBez Management-Bezeichnung einer Baugruppe
     * @return Liste von <code>HWRack</code> Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HWRack> findRacksByManagementBez(String managementBez) throws FindException;

    /**
     * Liefert den HW-Baugruppen-Typ mit dem gegebenen Namen
     *
     * @param hwBaugruppenTypName Name des HW-Baugruppen-Typs
     * @return gesuchte <code>HWBaugruppenTyp</code> Object
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    HWBaugruppenTyp findBaugruppenTypByName(String hwBaugruppenTypName) throws FindException;

    /**
     * Ermittelt alle Racks zu einem bestimmten Typ.
     *
     * @param type
     * @return Liste von Racks, nie {@code null}
     * @throws FindException
     */
    <T extends HWRack> List<T> findRacksByType(Class<T> type) throws FindException;

    /**
     * Funktion setzt das Freigabe-Datum einer MDU und gibt alle Rangierungen bzw. Ports frei
     *
     * @param rackId ID des Hw-Racks
     * @param date   Freigabedatum
     * @throws StoreException
     */
    void freigabeMDU(Long rackId, Date date) throws StoreException;

    /**
     * Funktion setzt das Freigabe-Datum einer DPU und gibt alle Rangierungen bzw. Ports frei
     *
     * Analog zu MDU
     *
     * @param rackId ID des Hw-Racks
     * @param date   Freigabedatum
     * @throws StoreException
     */
    void freigabeDPU(Long rackId, Date date) throws StoreException;

    /**
     * Ermittelt die {@link HWOltChild}-Objekte die an der angegebenen OLT haengen
     *
     * @param oltId
     * @return
     * @throws FindException
     */
    <T extends HWOltChild> List<T> findHWOltChildByOlt(Long oltId, Class<T> clazz) throws FindException;

    /**
     * Ermittelt {@link HWOltChild}-Objekte zur angegebenen Seriennummer
     *
     * @param SerialNo
     * @return Liste mit Objekten des Typs <code>HWOnt</code>
     * @throws FindException
     */
    <T extends HWOltChild> List<T> findHWOltChildBySerialNo(String SerialNo, Class<T> clazz) throws FindException;

    /**
     * Generiert die zugehoerige IP-Adresse für Geräte die an einem OLT Port angeschlossen sind (aktuell MDUs und ONT).
     *
     * @param olt      zugehoerige OLT
     * @param oltChild der OLT child fuer den die IP generiert werden soll
     * @return die generierte IP Nummer oder <code>null</code> die OLT keinen IP Bereich definiert
     * @throws de.augustakom.hurrican.model.exceptions.ModelCalculationException
     */
    String generateOltChildIp(HWOlt olt, HWOltChild oltChild) throws ModelCalculationException;

}
