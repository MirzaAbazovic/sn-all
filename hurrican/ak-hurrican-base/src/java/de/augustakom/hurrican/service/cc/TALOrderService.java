/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 12:00:51
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Allgemeines Interface fuer TAL-Bestellungen (z.B. ueber ESAA oder WITA).
 */
public interface TALOrderService extends ICCService {

    /**
     * Erzeugt zu der angegebenen Carrierbestellung ein Vorgangs-Objekt, ueber dass eine elektronische bzw. interne
     * TAL-Bestellung erfolgt.
     *
     * @param cbId              ID der Carrierbestellung
     * @param auftragId         ID des CC-Auftrags, zu dem die Bestellung ausgeloest wird.
     * @param subOrders4Klammer (optional) SubOrder Objekte, die die zu klammernden Auftraege beinhalten
     * @param carrierId         ID des Carriers, der die Bestellung erhalten soll
     * @param vorgabe           Datum, zu dem die TAL bereitgestellt werden soll
     * @param typ               Typ der Bestellung (z.B. 'NEU', 'KUENDIGUNG)
     * @param usecaseId         (optional) Usecase fuer weitere Detaillierungen des CBVorgang-Typs
     * @param vierDraht         Ob eine Vierdraht-Bestellung durchgefuehrt werden soll (dann muss genau eine SubOrder
     *                          vorhanden sein)
     * @param montagehinweis    (optional) Bemerkung fuer die Bestellung
     * @param sessionId         Session-ID des Users.
     * @return das erstellte CBVorgang-Objekt.
     * @throws StoreException wenn bei der Generierung ein Fehler auftritt.
     */
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            Long sessionId) throws StoreException;

    /**
     * Erzeugt zu der angegebenen Carrierbestellung ein Vorgangs-Objekt, ueber dass eine elektronische bzw. interne
     * TAL-Bestellung erfolgt. Hier kann der Benutzer direkt angegeben werden
     *
     * @param cbId              ID der Carrierbestellung
     * @param auftragId         ID des CC-Auftrags, zu dem die Bestellung ausgeloest wird.
     * @param subOrders4Klammer (optional) SubOrder Objekte, die die zu klammernden Auftraege beinhalten
     * @param carrierId         ID des Carriers, der die Bestellung erhalten soll
     * @param vorgabe           Datum, zu dem die TAL bereitgestellt werden soll
     * @param typ               Typ der Bestellung (z.B. 'NEU', 'KUENDIGUNG)
     * @param usecaseId         (optional) Usecase fuer weitere Detaillierungen des CBVorgang-Typs
     * @param vierDraht         Ob eine Vierdraht-Bestellung durchgefuehrt werden soll (dann muss genau eine SubOrder
     *                          vorhanden sein)
     * @param montagehinweis    (optional) Bemerkung fuer die Bestellung
     * @param user              der User.
     * @return das erstellte CBVorgang-Objekt.
     * @throws StoreException wenn bei der Generierung ein Fehler auftritt.
     */
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            AKUser user) throws StoreException;

    /**
     * Vereinfachte Erstellung eines Vorgangs, unter folgenden Voraussetzungen: <ul> <li>Default
     * Realisierungszeitfenster (bei WITA SLOT_2)</li> <li>Keine Auftragsklammerung</li> <li>Kein Vierdraht</li>
     * <li>Keine Rufnummerportierung</li> <li>Keine Attachments</li> <li>Kein Projektkenner</li> <li>Kein
     * Kopplungskenner</li> </ul>
     *
     * @param cbId           ID der Carrierbestellung
     * @param auftragId      ID des CC-Auftrags, zu dem die Bestellung ausgeloest wird.
     * @param carrierId      ID des Carriers, der die Bestellung erhalten soll
     * @param vorgabe        Datum, zu dem die TAL bereitgestellt werden soll
     * @param typ            Typ der Bestellung (typischerweise 'NEU')
     * @param montagehinweis (optional) Bemerkung fuer die Bestellung
     * @param user           der User
     * @return das erstellte CBVorgang-Objekt
     * @throws StoreException wenn bei der Generierung ein Fehler auftritt
     */
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Long carrierId, Date vorgabe, Long typ,
            String montagehinweis, AKUser user) throws StoreException;

    /**
     * Schliesst den angegebenen CBVorgang. Je nach Geschaeftsfall, Status und Rueckmeldung (pos/neg) etc. werden die
     * korrekten Daten auf die Carrierbestellung uebertragen
     *
     * @param id ID des abzuschliessenden CB-Vorgangs.
     * @return das abgeschlossene CBVorgang-Objekt.
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn ungueltige Daten angegeben sind.
     */
    public CBVorgang closeCBVorgang(Long id, Long sessionId) throws StoreException, ValidationException;

    /**
     * Storniert eine Carrierbestellung. <br> Abhaengig vom Status der Bestellung wird hierbei der Vorgang einfach
     * beendet oder auch zusaetzlich ein neuer Vorgang fuer den Storno ausgeloest. <br> <br> Ablauf: <br> - Status
     * 'submitted'                        --> auf 'storniert' setzen <br> - Status >= 'transferred' && < 'answered' -->
     * auf 'storniert' setzen und Storno-Vorgang erstellen <br> - Status = 'answered'                       -->
     * Exception 'Storno nicht moeglich' <br> <br>
     *
     * @param id   ID des zu stornierenden CBVorgang-Objekts.
     * @param user angemeldeter Hurrican-User.
     * @return CBVorgang-Objekt. Wurde durch den Storno-Vorgang ein weiterer Vorgang an den Carrier ausgeloest, wird das
     * neue Objekt uebergeben, sonst das durch die ID referenzierte CBVorgang-Objekt.
     * @throws StoreException      wenn beim Stornieren ein Fehler auftritt.
     * @throws ValidationException wenn der CBVorgang auf Grund seines Status nicht mehr storniert werden kann.
     */
    public CBVorgang doStorno(Long id, AKUser user) throws StoreException, ValidationException;

    /**
     * Ermittelt die moeglichen TAL-Bestellungs-Geschaeftsfaelle für den angegebenen Carrier.
     *
     * @param carrierId ID des Carriers, zu dem die moeglichen Geschaeftsfaelle ermittelt werden sollen
     * @param forKvz    Flag definiert, ob lediglich die Geschaeftsfaelle fuer KVZ-Standort ermittelt werden sollen
     */
    List<Reference> findPossibleGeschaeftsfaelle(Long carrierId, boolean forKvz) throws FindException;

}
