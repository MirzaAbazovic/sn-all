/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2009 09:58:07
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.SperreInfo;
import de.augustakom.hurrican.model.cc.SperreVerteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 *
 */
public interface SperreVerteilungService extends ICCService {

    /**
     * Erstellt fuer ein best. Produkt Sperre-Verteilungen.
     *
     * @param prodId       ID des Produkts, fuer das die Sperre-Verteilungen erstellt werden sollen.
     * @param abteilungIds IDs der Abteilungen fuer die die Sperre-Verteilungen gelten sollen.
     * @throws StoreException wenn bei der Erstellung ein Fehler auftritt
     */
    public void createSperreVerteilungen(Long prodId, List<Long> abteilungIds)
            throws StoreException;

    /**
     * Sucht nach allen Sperr-Verteilungen, die einem best. Produkt zugeordnet sind.
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten des Typs <code>SperreVerteilung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<SperreVerteilung> findSperreVerteilungen4Produkt(Long produktId)
            throws FindException;

    /**
     * Sucht nach allen SperreInfo-Eintraegen. <br> Die SperreInfo-Datensaetze definieren, an welche EMail eine
     * Information ueber eine Sperre gehen soll.
     *
     * @param onlyActive   ueber dieses Flag kann eingeschraenkt werden, ob nur die aktiven Datensaetze gelesen werden
     *                     sollen. <br> Bsp.: onlyActive=null --> alle Datensaetze <br> onlyActive=true --> nur 'aktive'
     *                     Datensaetze <br> onlyActive=false --> nur 'inaktive' Datensaetze <br>
     * @param abteilungsId ueber diesen Parameter kann das Suchergebnis weiter eingeschraenkt werden. Wird ein Wert
     *                     ungleich <code>null</code> angegeben, werden nur die Sperre-Infos gesucht, die mit der
     *                     Abteilungs-ID uebereinstimmen.
     * @return Liste mit Objekten des Typs <code>SperreInfo</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<SperreInfo> findSperreInfos(Boolean onlyActive, Long abteilungsId) throws FindException;

    /**
     * Speichert das SperreInfo-Objekt ab.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveSperreInfo(SperreInfo toSave) throws StoreException, ValidationException;

}
