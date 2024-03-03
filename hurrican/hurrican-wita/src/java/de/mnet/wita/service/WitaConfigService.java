/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 10:18:04
 */
package de.mnet.wita.service;

import static de.mnet.wita.message.auftrag.Kundenwunschtermin.*;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.SendAllowed;
import de.mnet.wita.model.WitaSendLimit;

/**
 * Service fuer WITA-Konfigurationen.
 */
public interface WitaConfigService extends WitaService {

    /**
     * Der Wert kommt von der DTAG und entspricht der maximalen Vorlaufzeit mit der ein Auftrag bei der DTAG eingestellt
     * werden kann. Aktuell: 2 Jahre = 730 Tage
     */
    public static final int DEFAULT_COUNT_DAYS_BEFORE_SENT = 730;

    /**
     * Ermittelt das konfigurierte Send-Limit fuer einen bestimmten Geschaeftsfall.
     *
     * @param geschaeftsfallTyp Geschaeftsfalltyp, fuer den die Konfiguration ermittelt werden solll
     * @param kollokationsTyp   gibt an, fuer welchen Kollokations-Typ (HVT, FTTC_KVZ) das Sende-Limit ermittelt werden
     *                          soll. Fuer Wbci-Sendelimits muss das Feld null sein.
     * @param ituCarrierCode    gibt an, fuer welchen Carrier das Sende-Limit ermittelt werden soll. Fuer
     *                          Wita-Sendelimits muss das Feld null sein.
     * @return das zugehoerige {@link WitaSendLimit} Objekt
     */
    WitaSendLimit findWitaSendLimit(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode);

    /**
     * Ermittelt das konfigurierte Send-Limit fuer einen bestimmten Geschaeftsfall.
     *
     * @param geschaeftsfallTyp Geschaeftsfalltyp, fuer den die Konfiguration ermittelt werden solll
     * @param hvtStandortId     ID des HVT-Standorts
     * @return das zugehoerige {@link WitaSendLimit} Objekt
     */

    WitaSendLimit findWitaSendLimit(GeschaeftsfallTyp geschaeftsfallTyp, Long hvtStandortId);

    /**
     * Speichert das angegebene Objekt.
     *
     * @param toSave zu speicherndes Objekt
     */
    void saveWitaSendLimit(WitaSendLimit toSave);

    /**
     * Ermittelt die Anzahl der protokollierten WITA Vorgaenge fuer den angegebenen {@link GeschaeftsfallTyp}
     *
     * @param geschaeftsfallTyp Geschaeftsfalltyp, fuer den die Anzahl der gesendeten Vorgaenge ermittelt werden soll.
     * @param kollokationsTyp   {@link KollokationsTyp}, fuer den die Anzahl der gesendeten Vorgaenge ermittelt werden
     *                          soll. Fuer Wbci-Sendelimits muss das Feld null sein.
     * @param ituCarrierCode    gibt an, fuer welchen Carrier das Sende-Limit ermittelt werden soll. Fuer
     *                          Wita-Sendelimits muss das Feld null sein.
     * @return Anzahl der protokollierten Eintraege fuer den {@link GeschaeftsfallTyp}
     */
    Long getWitaSentCount(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode);

    /**
     * See {@link #checkSendAllowed(MnetWitaRequest)}
     *
     * @return {@code true} wenn das Senden erlaubt ist.
     */
    boolean isSendAllowed(MnetWitaRequest request);

    /**
     * Loescht alle Eintraege zum {@link GeschaeftsfallTyp} und {@link KollokationsTyp} aus der WitaSendCount Tabelle
     */
    void resetWitaSentCount(GeschaeftsfallTyp geschaeftsfallTyp, KollokationsTyp kollokationsTyp);

    /**
     * Ueberprueft, ob fuer den angegebenen {@link MnetWitaRequest} das Senden erlaubt ist. <br> Dies ist dann der Fall,
     * wenn das konfigurierte {@link WitaSendLimit} noch nicht erreicht ist, bzw. wenn zu dem {@link GeschaeftsfallTyp}
     * und {@link KollokationsTyp} KEIN {@link WitaSendLimit} hinterlegt ist. Ausserdem wird ueberprueft, ob die zu
     * sendende Anfrage noch vorgehalten werden soll, da Nachrichten erst X Tage (z.B. 100) vor dem {@link
     * Kundenwunschtermin} an die DTAG gesendet werden sollen.
     *
     * @return {@link SendAllowed#OK} oder den Grund, warum das senden nicht erlaubt ist
     */
    SendAllowed checkSendAllowed(MnetWitaRequest request);

    /**
     * Erstellt einen Protokoll-Eintrag fuer den angegebenen {@link MnetWitaRequest}, dass dieser zum Zeitpunkt {@code
     * sentAt} ausgefuehrt wurde. <br> Der Log-Eintrag wird allerdings nur dann geschrieben, wenn fuer den
     * GeschaeftsfallTyp/Kollokationstyp ein {@link WitaSendLimit} mit Einschraenkungen konfiguriert ist.
     *
     * @param request
     * @param sentAt
     */
    void createSendLog(MnetWitaRequest request, LocalDateTime sentAt);

    /**
     * Erstellt einen Protokoll-Eintrag fuer den angegebenen {@link WbciRequest}, dass dieser versendet wurde. <br>
     *
     * @param request
     */
    void createSendLog(WbciRequest request);

    /**
     * Definiert die zu verwendende WITA-Version. <br> ACHTUNG: diese Methode ist nur fuer automatische Tests gedacht,
     * um die Default WITA Version zu veraendern.
     *
     * @param witaCdmVersionToUse
     */
    void switchWitaVersion(WitaCdmVersion witaCdmVersionToUse);

    /**
     * Ermittelt die Anzahl der Tage, die der {@link Kundenwunschtermin} fuer den angegebenen {@link GeschaeftsfallTyp}
     * maximal in der Zukunft liegen darf, bevor vorgehaltene Nachrichten rausgeschickt werden. Anfragen mit
     * Schaltungsterminen, die mehr als X (momentan 730) Tage in der Zukunft liegen, werden von DTAG abgewiesen.
     * Weiterhin werden unnoetige Stornos vermieden.
     * <p/>
     * Wenn der Key fuer den gewuenschten {@link GeschaeftsfallTyp} nicht in der Datenbank gefunden werden kann oder
     * nicht in eine Zahl verwandelt werden kann, so wird der Default-Wert (ohne Geschaeftsfallpraefix) versucht zu
     * ermitteln. Wenn auch dieser nicht in der Datenbank gefunden werden kann oder nicht in eine Zahl verwandelt werden
     * kann, so wird {@link #DEFAULT_COUNT_DAYS_BEFORE_SENT} zurueckgeliefert.
     */
    int getCountOfDaysBeforeSent(GeschaeftsfallTyp geschaeftsfallTyp);

    /**
     * Ermittelt Anzahl der Minuten, die ein Request vorgehalten wird, bis er dann rausgeschickt wird
     */
    String getMinutesWhileRequestIsOnHold();

    /**
     * speichert die Anzahl der Minuten, die ein Request vorgehalten wird, bis er dann rausgeschickt wird
     */
    void saveMinutesWhileRequestIsOnHold(String minutes);

    /**
     * Ermittelt die Version der WITA-Schnittstelle mit der neue Aufträge rausgeschickt werden.
     */
    WitaCdmVersion getDefaultWitaVersion();

    /**
     * Ermittelt die Version der WBCI-Schnittstelle abhaengig vom CarrierCode.
     */
    WbciCdmVersion getWbciCdmVersion(CarrierCode carrierCode);

    /**
     * Retrieves the value for the provided configuration key. If the key doesn't exist within the DB an
     * IllegalStateException will be thrown.
     */
    String getConfigValue(String key);

    /**
     * Sets the provided value for the specified configuration key. If the key doesn't exist within the DB an
     * IllegalStateException will be thrown.
     */
    void setConfigValue(String key, String value);

    /**
     * Ermittelt Anzahl der Minuten, die ein WbciRequest vorgehalten wird, bis er dann rausgeschickt wird.
     */
    int getWbciMinutesWhileRequestIsOnHold();

    /**
     * Gibt eine Liste mit allen gueltigen Zeitfenstern zurueck. <br/> Die gueltigen Zeitfenster haengen von folgenden
     * Faktoren ab:
     * <p/>
     * <ul> <li>ist ein Zeitfenster fuer den angegebenen Vorgangs-Typ erlaubt</li> <li>bei Vorgangs-Typ
     * 'Anbieterwechsel' wird auch noch die Carrier-Id ueberprueft (wg. Sonderfall VBL bzw. PV abhaengig vom Carrier,
     * mit dem der Wechsel stattfindet)</li> </ul>
     *
     * @param cbVorgangTyp         Typ des zu beruecksichtigenden CB-Vorgangs (Konstante aus
     *                             de.augustakom.hurrican.model.cc.tal.CBVorgang
     * @param vorabstimmungCarrier Angabe des Carriers, mit dem ein evtl. Anbieterwechsel durchgefuehrt werden soll
     * @return
     */
    public List<Zeitfenster> getPossibleZeitfenster(Long cbVorgangTyp, Long vorabstimmungCarrier);

    /**
     * Ermittelt die zulässige Abweichung für den Wechseltermin aus der neuen RuemVa. Die Abweichung wird nur dann
     * benötigt, wenn es eine vorangegangene VA existiert. Diese erfolgreich mit ERLM auf STR-AEN storniert wurde und
     * anschließend eine RuemVa zu einer neuen VA empfangen wird.
     *
     * @return
     */
    int getWbciRuemVaPortingDateDifference();

    /**
     * Ermittelt die Frist in Tagen, die fuer eine eingehende WBCI TV mindestens eingehalten werden muss.
     *
     * @return
     */
    int getWbciTvFristEingehend();

    /**
     * Ermittelt die Frist in Tagen, die fuer eine eingehende WBCI Stornos mindestens eingehalten werden muss.
     *
     * @return
     */
    int getWbciStornoFristEingehend();

    /**
     * Ermittelt den Offset fuer den WITA-Kuendigungstermin der verwendet werden soll, wenn die Kuendigung mit einer
     * VA-Id erstellt wird.
     *
     * @return
     */
    int getWbciWitaKuendigungsOffset();

    /**
     * Retrieves the permitted number of working days between the requested date (from the VA request) and the confirmed
     * date (in the RUEM-VA). If the date offset is outside this range then the RUEM-VA request requires manual
     * processing.
     *
     * @return
     */
    int getWbciRequestedAndConfirmedDateOffset();

}
