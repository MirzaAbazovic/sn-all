/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 13:51:32
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Multimap;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCard;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortDetailView;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Interface definiert die Funktionen fuer Baugruppen-Wechsel.
 *
 *
 */
public interface HWBaugruppenChangeService extends ICCService {

    /**
     * Speichert das angegebene HWBaugruppenChange Objekt.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveHWBaugruppenChange(HWBaugruppenChange toSave) throws StoreException, ValidationException;

    /**
     * Loescht den angegebenen Datensatz.
     *
     * @param toDelete zu loeschender Datensatz.
     * @throws DeleteException
     */
    void deleteHWBaugruppenChangeBgType(HWBaugruppenChangeBgType toDelete) throws DeleteException;

    /**
     * Ermittelt alle betroffenen Ports u. somit auch Auftraege fuer die angegebene Planung (vom Typ 'einfacher
     * Kartenwechsel').
     *
     * @param hwBgChange
     * @return Liste mit den generierten {@link HWBaugruppenChangePort2Port} Objekten.
     * @throws StoreException
     */
    List<HWBaugruppenChangePort2Port> createPort2Port4ChangeCard(HWBaugruppenChange hwBgChange) throws StoreException, FindException;

    @Nonnull
    Set<HWBaugruppenChangePort2Port> createPort2Port4PortConcentration(@Nonnull HWBaugruppenChange hwBgChange)
            throws StoreException, FindException, DeleteException, ValidationException;

    List<HWBaugruppe> sortBaugruppenByGerateBezAndModNr(final Iterable<HWBaugruppe> baugruppen);

    /**
     * Ermittelt alle betroffenen Ports (und somit auch Auftraege), die durch die Baugruppentyp-Aenderung der
     * angegebenen Planung betroffen sind.
     *
     * @param hwBgChange
     * @return Liste mit den generierten {@link HWBaugruppenChangePort2Port} Objekten.
     * @throws StoreException
     */
    List<HWBaugruppenChangePort2Port> createPort2Port4ChangeBgType(HWBaugruppenChange hwBgChange) throws StoreException, FindException;

    /**
     * Loescht die Port-2-Port Mappings der angegebenen Planung.
     *
     * @param hwBgChange Planung, von der die bestehenden Port-2-Port Mappings geloescht werden sollen
     * @throws DeleteException
     */
    void deletePort2Ports(HWBaugruppenChange hwBgChange) throws DeleteException;

    /**
     * Bereitet die angegebene Planung fuer die Ausfuehrung vor. Folgende Aktion(en) werden hierbei durchgefuehrt: <ul>
     * <li>CrossConnections pruefen ({@see HWBaugruppenChangeService#checkCrossConnectionDefinitions(Set)})
     * <li>betroffene Ports / Rangierungen werden ermittelt u. gesperrt </ul> (Um eine korrekte CPS-Initialisierung der
     * betroffenen Auftraege muss sich der User selbst kuemmern!)
     *
     * @param toPrepare
     * @return Liste mit den Port-Mappings, bei denen die CrossConnection Definitionen nicht i.O. sind.
     * @throws StoreException
     */
    List<HWBaugruppenChangePort2Port> prepareHWBaugruppenChange(HWBaugruppenChange toPrepare) throws StoreException;

    /**
     * Prueft, ob die Ausfuehrung des Schwenks erlaubt ist.
     * @param toExecute
     * @throws StoreException
     */
    void isExecutionAllowed(HWBaugruppenChange toExecute) throws StoreException;

    /**
     * Fuehrt den Baugruppen-Schwenk - je nach Typ - durch.
     *
     * @param toExecute auszufuehrender Baugruppen-Schwenk
     * @param dslamProfileMapping nur fuer ChangeType.PORT_CONCENTRATION. Mapping von DSLAMProfile (alt - neu)
     * @param sessionId ID der aktuellen User-Session
     * @return Objekt mit evtl. aufgetretenen/protokollierten Warnmeldungen.
     * @throws StoreException wenn bei der Ausfuehrung ein Fehler auftritt.
     */
    AKWarnings executeHWBaugruppenChange(HWBaugruppenChange toExecute,
            Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping, Long sessionId) throws StoreException;

    /**
     * Storniert die angegebene Planung. Dies ist nur dann moeglich, wenn sie noch nicht ausgefuehrt wurde. Falls die
     * Planung schon "vorbereitet" wurde (also, z.B. die Ports gesperrt sind), wird diese Vorbereitung wieder
     * rueckgaengig gemacht.
     *
     * @param toCancel
     * @param sessionId
     * @throws StoreException
     */
    void cancelHWBaugruppenChange(HWBaugruppenChange toCancel, Long sessionId) throws StoreException;

    /**
     * Schliesst die angegebene Planung. Dies ist nur dann moeglich, wenn die Planung auch ausgefuehrt wurde.
     *
     * @param toClose
     * @param sessionId
     * @throws StoreException
     */
    void closeHWBaugruppenChange(HWBaugruppenChange toClose, Long sessionId) throws StoreException;

    /**
     * Ueberprueft, ob die CrossConnections der Port-Mappings korrekt definiert sind. In folgendem Fall wird eine
     * Warning generiert: - alter Port hat manuell definierte CrossConnection und der neue Port hat keine bzw. eine
     * Default-CrossConnection
     *
     * @param portMappings Die Port-Mappings, auf denen die CrossConnections geprueft werden sollen
     * @return {@link HWBaugruppenChangePort2Port} mit den Port-2-Port Mappings, bei denen die CrossConnection nicht
     * korrekt definiert ist.
     * @throws FindException
     */
    Set<HWBaugruppenChangePort2Port> checkCrossConnectionDefinitions(Set<HWBaugruppenChangePort2Port> portMappings) throws FindException;

    /**
     * Ermittelt alle HWBaugruppenChange Eintraege, die noch offen sind. Das bedeutet, dass der Vorgang noch nicht
     * erledigt bzw. storniert ist.
     *
     * @return Liste mit den offenen Baugruppen-Wechseln. Objekte vom Typ {@link HWBaugruppenChange}
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt
     */
    List<HWBaugruppenChange> findOpenHWBaugruppenChanges() throws FindException;

    /**
     * Ermittelt die Port-2-Port Mappings fuer eine bestimmte Planung.
     *
     * @param hwBgChange Planung, von der die Port-2-Port Mappings ermittelt werden sollen
     * @return Liste mit Objekten des Typs {@link HWBaugruppenChangePort2PortView}
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt
     */
    List<HWBaugruppenChangePort2PortView> findPort2PortViews(HWBaugruppenChange hwBgChange) throws FindException;

    /**
     * Ermittelt alle Port-2-Port Mappings mit Detail-Angaben fuer eine bestimmte Planung.
     *
     * @param hwBgChange Planung, von der die Port-2-Port Mappings ermittelt werden sollen
     * @return Liste mit Objekten des Typs {@link HWBaugruppenChangePort2PortDetailView}
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt
     */
    List<HWBaugruppenChangePort2PortDetailView> findPort2PortDetailViews(HWBaugruppenChange hwBgChange) throws FindException;

    /**
     * Ermittelt das Port-2-Port Detail Mapping mit der angegebenen ID.
     *
     * @param port2PortId ID des gesuchten Port-2-Port Mappings
     * @return
     * @throws FindException
     */
    HWBaugruppenChangePort2Port findPort2Port(Long port2PortId) throws FindException;

    /**
     * Ermittelt die aktiven betroffenen Auftraege des angegebenen Baugruppen-Schwenks. Fuer diese Auftraege wird
     * anschliessend versucht, eine CPS-Tx zu erzeugen. Abhaengig von dem Parameter {@code cpsInit} wird eine
     * CPS-Initialisierung (bei 'true') bzw. ein modifySubscriber (bei 'false') erstellt.
     *
     * @param hwBgChange
     * @return String mit Warnings/Errors, die bei der CPS-Erstellung aufgetreten sind.
     * @throws StoreException
     */
    String createCpsTransactions(HWBaugruppenChange hwBgChange, boolean cpsInit, Long sessionId)
            throws StoreException;

    /**
     * Ermittelt die aktiven betroffenen Auftraege des angegebenen Baugruppen-Schwenks. Fuer diese Auftraege wird
     * anschliessend versucht, eine erfolgreiche CPS-Tx auf Basis des Taifun Auftrages zu finden. Ist keine erfolgreiche
     * CPS-Tx verfügbar, so wird ein modifySubscriber mit InitialLoad=1 erstellt, sofern der zugehörige Taifun Auftrag
     * noch keinen CPS-Tx Eintrag besitzt.
     *
     * @param hwBgChange
     * @return String mit Warnings/Errors, die bei der CPS-Erstellung aufgetreten sind.
     * @throws StoreException, ValidationException
     */
    String createCPSReInitTransactions(HWBaugruppenChange hwBgChange, Long sessionId) throws StoreException;

    /**
     * Ermittelt alle HW_EQN/V5-Port Mappings zu dem DLU-Wechsel mit der angegebenen ID.
     *
     * @param hwBgChangeDluId ID des {@link HWBaugruppenChangeDlu} Modells, zu dem die Mappings ermittelt werden
     *                        sollen.
     * @return Liste mit den zugehoerigen HW_EQN/V5-Port Mappings
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    List<HWBaugruppenChangeDluV5> findDluV5Mappings(Long hwBgChangeDluId) throws FindException;

    /**
     * Importiert zu dem angegebenen {@link HWBaugruppenChangeDlu} Modell die HW_EQN/V5-Port Mappings.
     * <p/>
     * Als File wird ein Excel-File mit folgender Struktur erwartet: <br> 1. Zeile: Ueberschrift  <br> 1. Spalte: HW_EQN
     * <br> 2. Spalte: V5-Port      <br>
     * <p/>
     * Existieren zu dem angegebenen DLU-Wechsel bereits V5-Port Mappings, werden diese zuvor geloescht.
     *
     * @param hwBgChange   DLU-Wechsel, zu dem die Mappings importiert werden sollen
     * @param fileToImport zu importierendes Excel-File (als InputStream)
     * @param sessionId    ID der aktuellen User-Session
     * @throws StoreException wenn beim Import ein Fehler auftritt.
     */
    void importDluV5Mappings(HWBaugruppenChange hwBgChange, InputStream fileToImport, Long sessionId)
            throws StoreException;

    /**
     * Speichert das angegebene Modell.
     *
     * @param toSave
     * @throws StoreException
     */
    void saveHWBaugruppenChangeDluV5(HWBaugruppenChangeDluV5 toSave) throws StoreException;

    /**
     * Loescht alle HW_EQN/V5-Port Mappings zu dem angegebenen DLU-Schwenk. Die Ausfuehrung erfolgt in einer eigenen /
     * neuen Transaction.
     *
     * @param hwBgChangeDluId
     * @throws DeleteException
     */
    void deleteDluV5MappingsNewTx(Long hwBgChangeDluId) throws DeleteException;

    /**
     * @param hwBgChangeDluId
     * @throws DeleteException
     * @see HWBaugruppenChangeService#deleteDluV5MappingsNewTx(Long) Ausfuehrung erfolgt in aktueller Transaction.
     */
    void deleteDluV5MappingsInTx(Long hwBgChangeDluId) throws DeleteException;

    void checkAndAddHwBaugruppe4Source(HWBaugruppenChangeCard hwBgChangeCard, HWBaugruppe hwBaugruppe) throws StoreException;

    void checkAndAddHwBaugruppe4New(HWBaugruppenChangeCard hwBgChangeCard, HWBaugruppe hwBaugruppe) throws StoreException;

}


