/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 10:47:58
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Bearbeitung von IPSec-Daten.
 *
 *
 */
public interface IPSecService extends ICCService {

    /**
     * Ermittelt die IPSec Site-to-Site Daten fuer einen Auftrag.
     *
     * @param auftragId ID des Auftrags, zu dem die Site-to-Site Daten ermittelt werden sollen
     * @return das {@link IPSecSite2Site} Objekt zu dem Auftrag oder <code>null</code>, falls zu dem Auftrag keine
     * Site-to-Site Daten erfasst sind.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    IPSecSite2Site findIPSecSiteToSite(Long auftragId) throws FindException;

    /**
     * Speichert das angegebene IPSec Site-to-Site Objekt ab.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn ungueltige Daten angegeben wurden (z.B. eine nicht korrekt formatierte
     *                             IP-Adresse)
     */
    void saveIPSecSiteToSite(IPSecSite2Site toSave) throws StoreException, ValidationException;

    /**
     * Speichert das angegebene IPSecClient2Site-Token.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveClient2SiteToken(IPSecClient2SiteToken toSave) throws StoreException;

    /**
     * Speichert eine Liste von IPSecClient2Site-Tokens
     *
     * @param tokens
     */
    void saveClient2SiteTokens(List<IPSecClient2SiteToken> tokens) throws StoreException;

    /**
     * Sucht nacht IPSecClient2Site-Tokens fuer einen Auftrag.
     *
     * @param auftragId Id des Auftrags fuer den IPSecClient2Site-Tokens ermittelt werden sollen.
     * @return Liste mit allen gefundenen IPSecClient2Site-Tokens.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<IPSecClient2SiteToken> findClient2SiteTokens(Long auftragId) throws FindException;

    /**
     * Ermittelt alle IPSecClient2Site-Tokens.
     *
     * @return Liste mit allen gefundenen IPSecClient2Site-Tokens.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<IPSecClient2SiteToken> findAllClient2SiteTokens() throws FindException;

    /**
     * Ermittelt alle freien Client2SiteTokens
     *
     * @return Liste mit allen gefundenen IPSecClient2Site-Tokens.
     */
    List<IPSecClient2SiteToken> findFreeClient2SiteTokens() throws FindException;

    /**
     * Ermittelt alle freien Client2SiteTokens deren Seriennummer aehnlich zu dem Argument ist.
     *
     * @param serialNumber mit Wildcards
     * @return Liste mit allen gefundenen IPSecClient2Site-Tokens.
     */
    List<IPSecClient2SiteToken> findFreeClient2SiteTokens(String serialNumber) throws FindException;

    /**
     * @param text
     * @return
     */
    List<IPSecClient2SiteToken> findAllClient2SiteTokens(String serialNumber) throws FindException;

    /**
     * Importiert die Tokens, die in einer CSV-Datei angegeben sind. Die CSV Datei muss folgenden Header besitzen: <br>
     * serialNumber;sapOrderId;lieferdatum;laufzeitInMonaten;batterieEnde;batch <br><br> Datumswerte sind im Format
     * dd.MM.yyyy anzugeben (Bsp.: 20.11.2009)
     *
     * @param reader FileReader fuer das zu importierende File
     * @return Die importieren Client2Site Tokens
     * @throws StoreException wenn beim Import ein Fehler auftritt
     */
    List<IPSecClient2SiteToken> importClient2SiteTokens(Reader reader) throws StoreException;

    /**
     * Loescht einen IPSecClient2SiteToken
     *
     * @param token
     */
    void deleteClient2SiteToken(IPSecClient2SiteToken token) throws StoreException;

}


