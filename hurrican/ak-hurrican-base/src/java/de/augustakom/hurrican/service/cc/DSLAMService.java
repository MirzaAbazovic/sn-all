/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 16:49:20
 */
package de.augustakom.hurrican.service.cc;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Multimap;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileMapping;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von DSLAMs und der zugehoerigen DSLAM-Profile.
 *
 *
 */
public interface DSLAMService extends ICCService {

    /**
     * Ermittelt alle verfuegbaren DSLAM-Profile.
     *
     * @return Liste mit Objekten des Typs <code>DSLAMProfile</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<DSLAMProfile> findDSLAMProfiles() throws FindException;

    /**
     * Ermittelt DSLAM-Profile ueber den Namen.
     *
     * @param name Name des gesuchten DSLAM-Profils
     * @return Liste mit <code>DSLAMProfile</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<DSLAMProfile> findDSLAMProfiles(String name) throws FindException;

    /**
     * Ermittelt alle DSLAM-Profile, die fuer ein best. Produkt moeglich sind.
     *
     * @param prodId Produkt-ID
     * @return Liste mit den DSLAM-Profilen, die dem Produkt zugeordnet sind.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<DSLAMProfile> findDSLAMProfiles4Produkt(Long prodId) throws FindException;

    /**
     * Ermittelt alle DSLAM-Profile, die dem Auftrag zugeordnet sind (Historie).
     *
     * @param auftragId
     * @return
     */
    List<DSLAMProfile> findDSLAMProfiles4Auftrag(Long auftragId);

    /**
     * Ermittelt das Standard DSLAM-Profil fuer den angegebenen Auftrag.
     *
     * @param auftragId ID des Auftrags.
     * @return ermitteltes DSLAM-Profil
     * @throws FindException wenn kein DSLAM-Profil ermittelt werden konnte.
     */
    DSLAMProfile calculateDefaultDSLAMProfile(Long auftragId) throws FindException;

    /**
     * Ermittelt das zum angegebenen Zeitpunkt fuer den Auftrag gueltige Profil, oder das Default-Profil falls dem
     * Auftrag noch keines zugewiesen wurde
     *
     * @param auftragId
     * @param when      Zeitpunkt
     * @return
     * @throws FindException
     */
    DSLAMProfile findDslamProfile4AuftragOrCalculateDefault(Long auftragId, Date when)
            throws FindException;

    /**
     * Ermittelt das fuer den Auftrag passende DSLAM-Profil und fuegt es diesem hinzu. <br>
     *
     * @param auftragId     Auftrags-ID
     * @param gueltigVon    Datum, ab dem das Profil zugeordnet werden soll.
     * @param auftragAktion (optionale) Angabe der {@link AuftragAktion}, durch die das DSLAM-Profil dem Auftrag
     *                      zugeordnet wird.
     * @param sessionId     Session-ID des Users
     * @return das ermittelte und zugeordnete DSLAM-Profil
     * @throws FindException  wenn kein DSLAM-Profil gefunden werden konnte
     * @throws StoreException wenn das DSLAM-Profil nicht zugeordnet werden konnte
     *
     */
    DSLAMProfile assignDSLAMProfile(Long auftragId, Date gueltigVon, @Nullable AuftragAktion auftragAktion, Long sessionId) throws FindException, StoreException;

    /**
     * Ermittelt alle {@link Auftrag2DSLAMProfile} Datensaetze zu dem Auftrag mit der Id {@code auftragId} und filtert
     * die Datensaetze heraus, die durch die {@link AuftragAktion} ausgeloest wurden. Die Datensaetze, die durch die
     * {@link AuftragAktion} angelegt wurden, werden geloescht; die durch die {@link AuftragAktion} deaktivierten
     * DSLAM-Profile werden wieder aktiviert.
     *
     * @param auftragId
     * @param auftragAktion die {@link AuftragAktion}, deren Aenderungen wieder rueckgaengig gemacht werden sollen
     * @throws StoreException
     */
    void cancelAuftrag2DslamProfile(Long auftragId, AuftragAktion auftragAktion) throws StoreException;

    /**
     * Aendert das DSLAM-Profil eines Auftrags. <br> Das vorhergende Profil wird historisiert zum Stichtag beendet. <br>
     * Ist das aktuelle Profil (mit GueltigBis='2200-01-01') identisch mit dem neuen Profil, wird kein neuer Datensatz
     * erstellt. Als Return-Value wird dann das letzte Profil zurueck gegeben.
     *
     * @param auftragId      Auftrags-ID
     * @param newProfileId   ID des DSLAM-Profils, das zugeordnet werden soll
     * @param gueltigVon     Datum, ab dem das Profil gueltig sein soll
     * @param user           Name des Users, der die Aenderung vornimmt.
     * @param changeReasonId (optional) ID gibt den Aenderungsgrund an
     * @param changeReason   (optional) Grund der Aenderung, Bemerkung
     * @return das erstellte Mapping-Objekt zwischen Auftrag und DSLAM-Profil
     * @throws StoreException wenn bei der Zuordnung des Profils zum Auftrag ein Fehler auftritt.
     *
     */
    Auftrag2DSLAMProfile changeDSLAMProfile(Long auftragId, Long newProfileId,
            Date gueltigVon, String user, Long changeReasonId, String changeReason) throws StoreException;

    /**
     * Ermittelt alle DSLAM-Profile des Auftrags und verschiebt die gueltigVon/gueltigBis Werte. Sofern eine {@link
     * AuftragAktion} angegeben ist, werden nur die DSLAM-Profile modifiziert, die mit dieser {@link AuftragAktion}
     * verbunden sind; ist keine {@link AuftragAktion} angegeben, so werden alle gueltigVon/gueltigBis Werte verschoben,
     * bei denen das {@code originalDate} uebereinstimmt.
     *
     * @param auftragId     Auftrags-ID
     * @param originalDate  urspruengliches Datum, das angepasst werden soll
     * @param modifiedDate  Datum, auf das die Gueltigkeit der DSLAM-Profile verschoben werden soll
     * @param auftragAktion (optional) {@link AuftragAktion}, deren Datum verschoben wird
     * @return Liste mit den angepassten {@link Auftrag2DSLAMProfile} Objekten
     * @throws StoreException wenn bei der Datumsanpssung ein Fehler auftritt
     */
    @Nonnull
    List<Auftrag2DSLAMProfile> modifyDslamProfiles4Auftrag(Long auftragId, LocalDate originalDate, LocalDate modifiedDate,
            @Nullable AuftragAktion auftragAktion) throws StoreException;

    /**
     * Ermittelt alle DSLAM-Profile, die dem Auftrag zugeordnet sind. <br> Die Ermittlung der Profile erfolgt in der
     * Reihenfolge ihrer Gueltigkeit.
     *
     * @param auftragId Auftrags-ID
     * @return Liste mit den Zuordnungen zwischen Auftrag und DSLAM-Profil
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    @Nonnull
    List<Auftrag2DSLAMProfile> findAuftrag2DSLAMProfiles(Long auftragId) throws FindException;

    /**
     * Ermittelt das DSLAM-Profil fuer einen Auftrag, dass zu dem Datum <code>validDate</code> gueltig ist.
     *
     * @param auftragId     ID des Auftrags, dessen DSLAM-Profil gesucht wird
     * @param validDate     Datum, zu dem das DSLAM-Profil gueltig sein soll
     * @param getLastIfNull ueber das Flag kann gesteuert werden, ob als Result das zuletzt zugeordnete DSLAM-Profile
     *                      zurueck gegeben werden soll, wenn zu dem Datum <code>validDate</code> kein Profil gefunden
     *                      wurde.
     * @return Objekt vom Typ <code>DSLAMProfile</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    DSLAMProfile findDSLAMProfile4Auftrag(Long auftragId, Date validDate, boolean getLastIfNull)
            throws FindException;

    /**
     * Ermittelt das DSLAM-Profil fuer einen Auftrag, dass zu dem Datum <code>validDate</code> gueltig ist. Analog zu
     * {@link DSLAMService#findDSLAMProfile4Auftrag}, wirft aber keine Exception, falls dem Auftrag kein DSLAM-Profil
     * zugeordnet ist oder kein aktives Profil gefunden werden konnte.
     */
    DSLAMProfile findDSLAMProfile4AuftragNoEx(Long auftragId, Date validDate, boolean getLastIfNull)
            throws FindException;

    /**
     * Speichert das angegebene Auftrag2DSLAMProfile. <b>ACHTUNG</b><br> Diese Methode soll nur dann verwendet werden,
     * wenn das angegebene wirklich modifiziert werden soll. Fuer eine Aenderung des DSLAM-Profils zu einem Auftrag
     * stehen eigentlich die Methoden {@link DSLAMService#assignDSLAMProfile(Integer, Date, Integer)} bzw. {@link
     * DSLAMService#modifyDslamProfiles4Auftrag(Integer, LocalDate, LocalDate)} zur Verfuegung.
     *
     * @param toStore
     * @throws StoreException
     */
    void saveAuftrag2DSLAMProfile(Auftrag2DSLAMProfile toStore) throws StoreException;

    /**
     * Sucht DSLAM-Profile die einem Auftrag zugeordnet werden können.
     *
     * @param auftragId ID des Auftrags, für den DSLAM-Profil gesucht werden
     * @return Liste von möglichen DSLAM-Profilen
     * @throws FindException
     */
    List<DSLAMProfile> findValidDSLAMProfiles4Auftrag(Long auftragId) throws FindException;

    /**
     * @param fromParams
     * @return
     */
    List<DSLAMProfile> findByExample(DSLAMProfile fromParams);

    /**
     * @param fromParams
     * @return
     */
    List<DSLAMProfile> findWithParams(DSLAMProfile fromParams);

    /**
     * Ermittelt das naechst hoehere DSLAM-Profil fuer einen Auftrag anhand der uebergebenen max. attainable bitrate.
     *
     * @param ccAuftragId - darf nicht null sein!
     * @param bitrateUp   max. attainable bitrate Upstream (ermittlung z.B. ueber CPSService#queryAttainableBitrate) -
     *                    darf nicht null sein!
     * @param bitrateDown max. attainable bitrate Downstream (ermittlung z.B. ueber CPSService#queryAttainableBitrate) -
     *                    darf nicht null sein!
     * @return das naechst hoehere DSLAMProfil, falls kein hoeheres gefunden wurde das bisherige
     * @throws FindException falls ein unerwarteter Fehler auftritt oder es sich nicht um einen DSL18000- Auftrag
     *                       handelt
     */
    DSLAMProfile findNextHigherDSLAMProfile4DSL18000Auftrag(Long ccAuftragId, Integer bitrateUp, Integer bitrateDown) throws FindException;

    /**
     * Ermittelt alle zu den angegebenen Parametern passenden DSLAM-Profile. <br>Achtung</b> <br> Es werden nur
     * DSLAM-Profile ermittelt, deren Flag {@link DSLAMProfile#enabledForAutochange} auf {@code true} gesetzt sind!
     *
     * @param baugruppenTyp (optional) es werden DSLAM-Profile zum angegebenen Baugruppentyp ermittelt, bzw. Profile,
     *                      die keinem(!) Baugruppentyp zugeordnet sind!
     * @param fastpath
     * @param uetvsAllowed
     * @return die entsprechenden DSLAMProfile-Objekte, die auf die angegebenen Parameter matchen
     * @throws FindException wenn ein unerwarteter Fehler auftritt
     */
    List<DSLAMProfile> findDSLAMProfiles(Long baugruppenTyp, final Boolean fastpath, Collection<String> uetvsAllowed) throws FindException;

    /**
     * Loescht das Auftrag2DSLAMProfil-Objekt mit der angegebenen Id
     *
     * @param id
     * @throws DeleteException
     */
    void deleteAuftrag2DslamProfile(Long id) throws DeleteException;

    /**
     * Speichert das angegebene DSLAM-Profil.
     *
     * @param dslamProfile
     * @return
     */
    DSLAMProfile saveDSLAMProfile(DSLAMProfile dslamProfile);

    /**
     * Ermittelt alle verfuegbaren DSLAM-Profile Mappings.
     *
     * @param dslamProfileName
     * @return Liste mit Objekten des Typs <code>DSLAMProfileMapping</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<DSLAMProfileMapping> findDSLAMProfileMappings(String dslamProfileName) throws FindException;

    /**
     * Ermittelt Multimap mit Mapping "ID altes Profil", UETV - "neue moeglichen DSLAM Profile". Wenn für die Bezeichnung des
     * neuen Profils kein DSLAM Profil gefunden wurde, enthaelt die Map leere Liste als Value. Die Multimap
     * haelt alle zu dem Namen gefundenden Profile, die zuordnung der neuen Zielprofile erfolgt ohne beruecksichtigung
     * auf Baugruppentypen. D.h. es muss entsprechen dem Ziel-BG-Typ das entsprechende Profil der Liste entnommen
     * werden.
     *
     * @param oldDslamProfileId
     * @return
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> findDSLAMProfileMappingCandidates(Long oldDslamProfileId) throws FindException;

    /**
     * Findet best match für neue DSLAM Profil anhand der Mapping Konfigurationen verfügbar für das alte DSLAM Profil.
     *
     * @param hwBaugruppenTypId
     * @param oldDslamProfileId
     * @param uetv
     * @return
     * @throws FindException
     */
    DSLAMProfile findNewDSLAMProfileMatch(Long hwBaugruppenTypId, Long oldDslamProfileId, Uebertragungsverfahren uetv)  throws FindException;
}


