/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 11:11:51
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service zur Verwaltung der Ansprechpartner.
 * <p/>
 * Ein Ansprechpartner hat einen Typen und einer Adresse und gehoert zu einem Auftrag.
 *
 *
 */
public interface AnsprechpartnerService extends ICCService {

    /**
     * Speichert das angegebene Ansprechpartner-Objekt ab.
     *
     * @param ansprechpartner zu speicherndes Objekt
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn Werte falsch gesetzt sind.
     */
    void saveAnsprechpartner(Ansprechpartner ansprechpartner) throws StoreException, ValidationException;

    /**
     * Loescht den angegebenen Ansprechpartner.
     *
     * @param ansprechpartner zu loeschender Ansprechpartner
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt
     */
    void deleteAnsprechpartner(Ansprechpartner ansprechpartner) throws DeleteException;

    /**
     * Liefert eine Liste an Ansprechpartnern des angefragten Typs fuer den gegebenen Auftrag zurueck.
     *
     * @param type      Falls {@code null}, werden alle Ansprechpartner zurueckgegeben
     * @param auftragId Darf nicht {@code null} sein
     * @return Liste von Ansprechpartnern, kann leer sein
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    List<Ansprechpartner> findAnsprechpartner(Ansprechpartner.Typ type, Long auftragId) throws FindException;

    /**
     * Liefert den bevorzugten Ansprechpartner des angefragten Typs fuer den gegebenen Auftrag zurueck.
     *
     * @param type      Darf nicht {@code null} sein
     * @param auftragId Darf nicht {@code null} sein
     * @return Der bevorzugte Ansprechpartner, oder {@code null}, falls er nicht existiert
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    Ansprechpartner findPreferredAnsprechpartner(Ansprechpartner.Typ type, Long auftragId) throws FindException;

    /**
     * Ermittelt von einem Auftrag einen Ansprechpartner eines best. Typs (PREFERRED=TRUE) und kopiert diesen auf den
     * angegebenen Ziel-Auftrag. Die Kopie wird nur durchgefuehrt, wenn auf dem Ziel-Auftrag noch kein Ansprechpartner
     * des angegebenen Typs vorhanden ist!
     *
     * @param type          Typ des zu kopierenden Ansprechpartners
     * @param auftragIdSrc  Ursprungs-AuftragsID
     * @param auftragIdDest Ziel-AuftragsID
     * @throws StoreException wenn beim Kopieren ein Fehler auftritt.
     */
    void copyAnsprechpartner(Ansprechpartner.Typ type, Long auftragIdSrc, Long auftragIdDest) throws StoreException;

    /**
     * Kopiert einen Ansprechpartner. Der neue Ansprechpartner wird persistiert.
     *
     * @param ansprechpartner Ansprechpartner, der kopiert werden soll
     * @param type            Ansprechpartnertyp des neuen Ansprechpartners
     * @param addressType     Adresstyp des neuen Ansprechpartners
     * @param preferred       ob der neue Ansprechpartner ein bevorzugter Ansprechpartner sein soll (null -> preffered
     *                        Flag von Original uebernehmen, falls moeglich)
     * @return den neu erstellten Ansprechpartner
     * @throws StoreException wenn beim Kopieren ein Fehler auftritt
     */
    Ansprechpartner copyAnsprechpartner(Ansprechpartner ansprechpartner, Ansprechpartner.Typ ansprechpartnerType, Long addressType, Boolean preferred) throws StoreException;

    /**
     * Kopiert einen Ansprechpartner. Der neue Ansprechpartner wird persistiert.
     *
     * @param ansprechpartnerId ID des Ansprechpartner, der kopiert werden soll
     * @param auftragId         Auftrags-ID, die dem neuen Ansprechpartner zugeordnet werden soll
     * @return den neu erstellten Ansprechpartner
     * @throws StoreException wenn beim Kopieren ein Fehler auftritt
     */
    Ansprechpartner copyAnsprechpartner(Long ansprechpartnerId, Long auftragId) throws StoreException;

    /**
     * Findet einen Ansprechpartner anhand dessen ID
     *
     * @param id
     * @return Der Ansprechpartner, oder {@code null}, falls er nicht gefunden wurde.
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    Ansprechpartner findAnsprechpartner(Long id) throws FindException;

    /**
     * erstellt den bevorzugten Ansprechpartner für Endstelle B, der auf die gegebene Adresse und den gegebenen Auftrag
     *
     * @param ccAddress Darf nicht {@code null} sein
     * @param auftragId Darf nicht {@code null} sein
     * @return Der bevorzugte Ansprechpartner
     * @throws FindException Falls ein Fehler aufgetreten ist
     */
    Ansprechpartner createPreferredAnsprechpartner(CCAddress ccAddress, Long auftragId) throws FindException;

    /**
     * Findet alle Ansprechpartner zu einer Kundennummer und Buendelnummer.
     *
     * @param kundeNo die Kundennummer (darf nicht {@code null} sein).
     * @param buendelNr die Buendelnummer.
     * @return Liste mit Ansprechpartnern.
     * @throws FindException Falls ein Fehler auftritt.
     */
    List<CCAnsprechpartnerView> findAnsprechpartnerByKundeNoAndBuendelNo(Long kundeNo, Integer buendelNr) throws FindException;

}
