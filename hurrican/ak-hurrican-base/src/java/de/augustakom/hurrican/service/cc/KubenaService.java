/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 08:25:37
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.model.cc.kubena.KubenaHVT;
import de.augustakom.hurrican.model.cc.kubena.KubenaProdukt;
import de.augustakom.hurrican.model.cc.kubena.KubenaVbz;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer Kubena (=Kundenbenachrichtigung).
 *
 *
 */
public interface KubenaService extends ICCService {

    /**
     * Sucht nach allen bisherigen Kubena's. (Die Sortierung erfolgt absteigend nach dem Erstellungsdatum.)
     *
     * @return Liste mit Objekten vom Typ <code>Kubena</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Kubena> findKubenas() throws FindException;

    /**
     * Speichert die angegebene Kubena ab.
     *
     * @param toStore zu speicherndes Objekt.
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn das zu speichernde Objekt ungueltige Daten enthaelt.
     */
    public void saveKubena(Kubena toStore) throws StoreException, ValidationException;

    /**
     * Ermittelt die HVTs, die einer best. Kubena zugeordnet sind.
     *
     * @param kubenaId ID der Kubena.
     * @return Liste mit Objekten des Typs <code>KubenaHVT</code>.
     * @throws FindException
     */
    public List<KubenaHVT> findKubenaHVTs(Long kubenaId) throws FindException;

    /**
     * Speichert die angegebenen Objekte.
     *
     * @param toStore
     * @throws StoreException
     */
    public void saveKubenaHVTs(List<KubenaHVT> toStore) throws StoreException;

    /**
     * Loescht die KubenaHVTs der Kubena <code>kubenaId</code>, die die HVT-Standort-IDs <code>hvtStdIds</code>
     * besitzen.
     *
     * @param kubenaId  ID der Kubena
     * @param hvtStdIds IDs der zu loeschenden HVT-Standorte.
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     */
    public void deleteKubenaHVTs(Long kubenaId, List<Long> hvtStdIds) throws DeleteException;

    /**
     * Ermittelt die Produkte, die einer best. Kubena zugeordnet sind.
     *
     * @param kubenaId ID der Kubena.
     * @return Liste mit Objekten des Typs <code>KubenaProdukt</code>.
     * @throws FindException
     */
    public List<KubenaProdukt> findKubenaProdukt(Long kubenaId) throws FindException;

    /**
     * Speichert die angegebenen Objekte.
     *
     * @param toStore
     * @throws StoreException
     */
    public void saveKubenaProdukte(List<KubenaProdukt> toStore) throws StoreException;

    /**
     * Loescht die KubenaProdukte der Kubena <code>kubenaId</code>, die die Produkt-IDs <code>prodIds</code> besitzen.
     *
     * @param kubenaId ID der Kubena
     * @param prodIds  IDs der zu loeschenden Produkte.
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     */
    public void deleteKubenaProdukte(Long kubenaId, List<Long> prodIds) throws DeleteException;

    /**
     * Ermittelt alle VBZs, die einer best. Kubena zugeordnet sind.
     *
     * @param kubenaId ID der Kubena.
     * @return Liste mit Objekten des Typs <code>KubenaVbz</code>.
     * @throws FindException
     */
    public List<KubenaVbz> findKubenaVbz(Long kubenaId) throws FindException;

    /**
     * Speichert das angegebene KubenaVbz-Objekt.
     *
     * @param toStore zu speicherndes Objekt.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveKubenaVbz(KubenaVbz toStore) throws StoreException;

    /**
     * Fuegt eine VerbindungsBezeichnung einer Kubena hinzu
     *
     * @param kubenaId  ID der Kubena
     * @param vbz       Verbindungsbezeichnung, die der Kubena hinzugefuegt werden soll.
     * @param inputType Art der Anlage (manuell/auto).
     * @return die erzeugte(n) KubenaVbz
     * @throws StoreException wenn beim Anlegen ein Fehler auftritt.
     */
    public List<KubenaVbz> addVbz2Kubena(Long kubenaId, String vbz, String inputType) throws StoreException;

    /**
     * Erstellt ein Excel-File mit den Kunden- und Auftrags-Daten der betroffenen Kunden.
     *
     * @param kubenaId ID der zu erstellenden Kubena
     * @return das erstellte Excel-File
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     */
    public File createKubena(Long kubenaId) throws FindException;


}


