/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.14
 */
package de.mnet.wita.service;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.wbci.exception.InvalidRufnummerPortierungException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * ServiceFacade, die der Entkopplung von Wita und Wbci Services dienen soll. Die Kommunikation zwischen Wita und Wbci
 * auf Service-Ebene soll ueber diese Fassade abgewickelt werden. Für die andere Richtung Wbci -> Wita soll die {@link
 * de.mnet.wbci.service.WbciWitaServiceFacade} verwendet werden.
 *
 *
 */
public interface WitaWbciServiceFacade extends ICCService {

    public static final String AKM_PV_TERMIN_ABWEICHEND =
            "Der angefragte Wechseltermin ist nicht identisch mit dem vorabgestimmten Termin!";
    public static final String RUFNUMMERN_NOT_MATCH =
            "Die angegebene Taifun-Rufnummer '%s' stimmt nicht mit der in der WBCI-Vorabstimmung angegebenen Rufnummer überein!";
    public static final String NO_VALID_VORABSTIMMUNG_FOUND =
            "Zu der Vorabstimmungs-ID '%s' konnte keine gültige WBCI-Vorabstimmung gefunden werden!";
    public static final String NO_RUEM_VA =
            "Zu der Vorabstimmungs-ID '%s' konnte keine gültige Rückmeldung zur Vorabstimmung (RUEM-VA) gefunden werden!";
    public static final String NO_AKM_TR =
            "Zu der Vorabstimmungs-ID '%s' ist keine positive Meldung zur Übernahme der Ressource (AKM-TR) vorhanden!";
    public static final String NO_LEITUNGEN_IN_AKMTR =
            "Zu der Vorabstimmungs-ID '%s' sind keine übernommenen Leitungen in der AKM-TR vorhanden, obwohl dies erwartet wurde!";
    public static final String UEBERNAHME_RESSOURCE_REQUIRED =
            "Zu der Vorabstimmungs-ID '%s' wurde keine Ressourcenübernahme in der AKM-TR ausgewählt, obwohl dies für den WITA-Geschäftsfall '%s' erwartet wurde!";
    public static final String COUNT_OF_LEITUNGEN_IN_AKMTR =
            "In der AKM-TR zu der Vorabstimmungs-ID '%s' stimmt die Anzahl der übernommenen Leitungen (%s) nicht mit der Anzahl der ausgelösten WITA-Vorgänge (%s) überein!";
    public static final String NO_TAIFUN_NO =
            "Für die Vorabstimmungs-ID '%s' ist kein Taifun-Auftrag zugeordnet!";
    public static final String ACTIVE_TV_OR_STORNO = "Die Vorabstimmung '%s' ist nicht gültig für eine " +
            "Referenzierung in einem WITA-Vorgang. Ein WBCI-Vorabstimmung muss bereits mit einer AKM-TR " +
            "bestätigt sein und darf keine aktiven Terminverschiebungens- oder Storno-Anfragen haben.";
    public static final String DATE_IS_NOT_MATCHING =
            "Der angegebene Termin '%s' stimmt nicht mit dem vorabgestimmten Termin '%s' der WBCI-Vorabstimmung '%s' überein!";
    public static final String DATE_IS_NOT_EQUAL_OR_AFTER =
            "Der angegebene Termin '%s' ist nicht identisch mit bzw. liegt nicht nach dem vorabgestimmten Termin '%s' der WBCI-Vorabstimmung '%s'!";
    public static final String NO_WECHSELTERMIN = "Zur Vorabstimmung '%s' liegt kein Wechseltermin vor.";

    /**
     * Ermittelt den {@link de.mnet.wbci.model.WbciGeschaeftsfall} zu der angegebenen Vorabstimmungs-Id
     *
     * @param vorabstimmungsId Vorabstimmungs-Id
     * @return der zur Vorabstimmung gehoerende WBCI-Geschaeftsfall
     */
    WbciGeschaeftsfall getWbciGeschaeftsfall(String vorabstimmungsId);

    /**
     * Ermittelt die TaskVariablen, die zum Erzeugen von dem RuemPV als Antwort auf die angegebene AkmPV notwendig sind.
     * Falls eine automatische Antwort nicht möglich wäre, wird eine leere Map zurückgegeben.
     *
     * @param akmPv Die AnkuendigunsmeldungProviderwechsel, die automatisch beantwortet werden soll
     * @return Map mit TaskVariablen, auf deren Basis die RuemPV erzeugt wird.
     */
    Map<WitaTaskVariables, Object> getAutomaticAnswerForAkmPv(AnkuendigungsMeldungPv akmPv);

    /**
     * Liefert alle im System vorhandenen IDs der Vorabstimmungen zurück, die: <ul> <li>nicht abgeschlossenen sind, d.h.
     * die den Status {@link WbciGeschaeftsfallStatus#ACTIVE} or {@link WbciGeschaeftsfallStatus#PASSIVE} haben,</li>
     * <li>die angegebenen Taifunauftrags-Id zugeordnet wurde,</li> <li>und die angegebenen {@link WbciRequestStatus}
     * haben.</li>
     * <p/>
     * </ul>
     * <p/>
     * Ist das Flag 'considerActiveTVsOrStornos' auf TRUE gesetzt, wird zusätzlich geprüft, ob aktive {@link
     * TerminverschiebungsAnfrage}n oder {@link StornoAnfrage}n bestehen. Bestehen diese wird diese Vorabstimmungs nicht
     * berücksichtigt.
     *
     * @param vaStatus                   Status der Vorabstimmung
     * @param taifunOrderId              Taifunauftrag-Id.
     * @param considerActiveTVsOrStornos sind aktive/offenen TVs und Stornos zu berücksichtigen
     * @return
     */
    Set<String> findNonCompletedVorabstimmungen(WbciRequestStatus vaStatus, Long taifunOrderId,
            boolean considerActiveTVsOrStornos);

    /**
     * @return den derzeit bestätigten Wechseltermin der zugehörigen Vorabstimmung
     */
    LocalDateTime getWechselterminForVaId(String wbciVorabstimmungsId);

    /**
     * Updatet eine bestehende Selektion an Rufnummern unter der Berücksichtigung der angegebene Vorabstimmung. Zur
     * Auswahl der Rufnummer wird die letze RUEM-VA herangezogen. Können nicht alle in der RUEM-VA vorhanden Rufnummern
     * selektiert werden, wird eine {@link InvalidRufnummerPortierungException} geworfen.
     *
     * @param rufnummerPortierungSelections vorbefüllte Collection von {@link RufnummerPortierungSelection}
     * @param wbciVorabstimmungsId          Vorabstimmungs-ID für die Rufnumernauswahl.
     * @return eine aktualisierte Collection an {@link RufnummerPortierungSelection}
     */
    Collection<RufnummerPortierungSelection> updateRufnummernSelectionForVaId(
            Collection<RufnummerPortierungSelection> rufnummerPortierungSelections, String wbciVorabstimmungsId);

    /**
     * Überpüft ob zu der angegebenen Vorabstimmung derzeit keine aktiven {@link TerminverschiebungsAnfrage}n oder
     * {@link StornoAnfrage} bestehen.
     *
     * @param witaGfTyp {@link GeschaeftsfallTyp} des WITA-Vorgangs
     * @param wbciVaId  ID der Vorabstimmung
     * @throws WbciValidationException when check is unsuccessful.
     */
    void checkVorabstimmungValidForWitaVorgang(GeschaeftsfallTyp witaGfTyp, String wbciVaId);

    /**
     * Ermittelt aus der letzen AKM-TR die als nächste zu verwendende WITA-Vertragsnummer für den angegeben
     * WITA-CB-Vorgang. Handelt es sich um einen geklammerten Aufrag, wird anhand der Auftragsklammer die zugehörige
     * VertragsNr. ermitellt. Es wird dabei überprüft ob die angegebene Anzahl der WITA-Vorgänge mit der Anzahl der zu
     * übernehmenden Wita-Vertragsnummern der {@link UebernahmeRessourceMeldung} übereinstimmt und eine Übernahme der
     * Ressource erfolgt. Die Prüfung und Ermittlung der WITA-Vertrags-Nr wird lediglich für die WITA-Geschäftsfäll
     * {@link GeschaeftsfallTyp#VERBUNDLEISTUNG} und {@link GeschaeftsfallTyp#PROVIDERWECHSEL} durchgeführt.
     * <p/>
     * SONDERFALL WITA-7: falls der abgebende Provider in der Vorabstimmung die DTAG ist, darf die Vertragsnummer nicht
     * uebermittelt werden! (d.h. es wird null zurückgeliefert) siehe hierzu auch WITA
     * AuftragsMeldestruktur_Order-SST-V700.xls (Anmerkung und Kommentarfunktion zu 'vertragsnummer' und
     * 'bestandssuche').
     *
     * WITA-10: Vertragsnummer muss uebermitelt werden, da die Bestandsuche bei VBL und PV entfaellt
     *
     * @param witaGfTyp            {@link GeschaeftsfallTyp} des WITA-Vorgangs
     * @param wbciVorabstimmungsId ID der WBCI-Vorabstimmung ({@link WbciGeschaeftsfall#vorabstimmungsId}
     * @param cbVogangId           die {@link WitaCBVorgang#cbId}.
     * @param auftragsKlammerId    die {@link WitaCBVorgang#auftragsKlammer}
     * @return vorabgestimmte WITA-Vertragsnummer
     */
    String checkAndReturnNextWitaVertragsnummern(GeschaeftsfallTyp witaGfTyp, String wbciVorabstimmungsId,
            Long cbVogangId, Long auftragsKlammerId) throws WbciValidationException;

    /**
     * Validiert den angegebene Termin auf Gültigkeit mit der in der Vorabstimmung vereinbarten Wechseltermin.
     *
     * @param date     der zu überprüfende Termin
     * @param wbciVaId ID der Vorabstimmung
     * @throws WbciValidationException, wenn die Termine nicht übereinstimmen
     */
    void checkDateForMatchingWithVorabstimmung(Date date, String wbciVaId) throws WbciValidationException;

    /**
     * Validiert den angegebene Termin auf Gültigkeit mit der in der Vorabstimmung vereinbarten Wechseltermin. <br/>
     * Dabei wird geprueft, ob das angegebene Datum {@code date} >= dem in der WBCI VA hinterlegten Wechseltermin ist.
     *
     * @param date     der zu überprüfende Termin
     * @param wbciVaId ID der Vorabstimmung
     * @throws WbciValidationException, wenn die {@code date} NICHT >= dem Wechseltermin aus der WBCI VA ist
     */
    void checkDateIsEqualOrAfterWbciVa(Date date, String wbciVaId) throws WbciValidationException;

    /**
     * Validiert alle angegbenen Rufnummern auf ihre Übereinstimmung mit den vorabgestimmten Rufnummern der
     * Vorabstimmung.
     *
     * @param rufnummern alle zu validierenden Rufnummern
     * @param wbciVaId   ID der Vorabstimmung
     */
    void checkRufnummernForMatchingWithVorabstimmung(Collection<Rufnummer> rufnummern, String wbciVaId);


    /**
     * Ermittelt die letzte AKM-TR zu der angegebenen Vorabstimmungs-Id
     *
     * @param wbciVorabstimmungsId WBCI Vorabstimmungs-Id zu der die AKM-TR ermittelt werden soll
     * @return die letzte AKM-TR zu der angegebenen Vorabstimmungs-Id oder {@code null} wenn nicht vorhanden
     */
    UebernahmeRessourceMeldung getLastAkmTr(String wbciVorabstimmungsId);

    /**
     * Ermittelt die Rueckmeldung Vorabstimmung zu der angegebenen Vorabstimmungs-Id
     *
     * @param wbciVorabstimmungsId WBCI Vorabstimmungs-Id zu der die RUEMVA ermittelt werden soll
     * @return die RUEMVA zu der angegebenen Vorabstimmungs-Id oder {@code null} wenn nicht vorhanden
     */
    RueckmeldungVorabstimmung getRuemVa(String wbciVorabstimmungsId);

}
