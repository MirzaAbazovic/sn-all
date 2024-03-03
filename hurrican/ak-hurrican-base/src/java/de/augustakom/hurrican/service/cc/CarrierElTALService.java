/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2007 10:35:38
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.tal.CBUsecase;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.view.CBVorgangView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer Funktionen der elektronischen TAL-Bestellung (sowohl Richtung externer Carrier als auch fuer
 * interne TAL-Bestellungen).
 *
 *
 */
public interface CarrierElTALService extends ICCService {

    /**
     * Ermittelt die notwendigen Check-Commands fuer die vorgesehene TAL-Bestellung und fuehrt diese aus.
     */
    void executeCheckCommands4CBV(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long typ, Long usecaseId, CarrierKennung ck) throws FindException, ServiceCommandException,
            StoreException;

    /**
     * Speichert das angegebene CBVorgangs-Objekt.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    CBVorgang saveCBVorgang(CBVorgang toSave) throws StoreException;

    /**
     * Ermittelt einen CB-Vorgang ueber die ID.
     *
     * @param id ID des gesuchten CB-Vorgangs.
     * @return Objekt vom Typ <code>CBVorgang</code> oder <code>null</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    CBVorgang findCBVorgang(Long id) throws FindException;

    /**
     * Ermittelt alle CBVorgangs-Objekte, die zu einer bestimmten Carrierbestellung erfasst sind. <br> Die Sortierung
     * erfolgt in absteigender(!) Reihenfolge der Vorgangs-ID (aktuellster Vorgang an Index 0).
     *
     * @param cbIDs ID der Carrierbestellung
     * @return Liste mit Objekten des Typs <code>CBVorgang</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<CBVorgang> findCBVorgaenge4CB(Long... cbIDs) throws FindException;

    /**
     * Ermittelt alle noch offenen(!) CBVorgaenge (anhand vom Status) samt der bearbeitenden Niederlassung, <br> die das
     * Wiedervorlage_AM Datum in der Vergangenheit haben. Die Sortierung erfolgt nach dem Vorgabedatum M-net.
     *
     * @return Liste mit Objekten des Typs <code>CBVorgangNiederlassung</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<CBVorgangNiederlassung> findOpenCBVorgaengeNiederlassungWithWiedervorlage() throws FindException;

    /**
     * Ermittelt alle noch nicht bearbeiteten CBVorgaenge fuer die interne TAL-Bestellung (zustaendiger Carrier 'AKOM').
     * <br> Beruecksichtige Stati sind >= SUBMITTED und < ANSWERED <br> Die Sortierung erfolgt nach dem Vorgabedatum
     * M-net.
     *
     * @return Liste mit Objekten des Typs <code>CBVorgangView</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<CBVorgangView> findOpenCBVorgaenge4Intern() throws FindException;

    /**
     * Ermittelt einen bestimmten CBUsecase ueber eine ID.
     *
     * @param usecaseId ID des gesuchten Usecases
     * @return Objekt vom Typ <code>CBUsecase</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    CBUsecase findCBUsecase(Long usecaseId) throws FindException;

    /**
     * Ermittelt alle {@link CBVorgang} Objekte mit den angegebenen Example-Werten.
     */
    <T extends CBVorgang> List<T> findCBVorgaengeByExample(T example) throws FindException;

    /**
     * Ermittelt {@link CBVorgang} anhand der uebergebenen {@code carrierRefNr}.
     *
     * @throws NoSuchElementException   falls kein {@link CBVorgang} gefunden
     * @throws IllegalArgumentException falls mehrere {@link CBVorgang}e gefunden
     */
    CBVorgang findCBVorgangByCarrierRefNr(String carrierRefNr);
}
