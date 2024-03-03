/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 15:06:31
 */
package de.augustakom.hurrican.service.exmodules.archive;

import java.util.*;

import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.IExModulesService;

/**
 * Definition fuer den Archiv-Service, ueber den der Zugriff auf das M-net weite Archiv (ScanView) moeglich ist.
 */
public interface ArchiveService extends IExModulesService {

    /**
     * Ermittelt aus dem M-net Archiv alle Dokumente, die den Parametern {@code vertragsnummer} und {@code docType}
     * entsprechen. Es werden nur die Metadaten zu den Dokumenten geliefert, aber nicht der Inhalt.<br>
     *
     * @param vertragsnummer   Angabe der Vertragsnummer (SAP) des Auftrags, zu dem die Dokumente gesucht werden
     * @param documentType     Art des Dokuments, das gesucht wird
     * @param archiveLoginName Login-Name fuer das Archiv
     * @return Liste mit den gefundenen Dokumenten
     * @throws FindException wenn bei der Ermittlung der Dokumente ein Fehler auftritt
     */
    List<ArchiveDocumentDto> retrieveDocuments(String vertragsnummer, ArchiveDocumentType documentType,
            String archiveLoginName) throws FindException;

    /**
     * Ermittelt aus dem M-net Archiv ein Dokument ueber einen eindeutigen Key.
     *
     * @param vertragsnummer   Angabe der Vertragsnummer (SAP) des Auftrags, zu dem die Dokumente gesucht werden
     * @param documentType     Art des Dokuments, das gesucht wird
     * @param key              eindeutiger Key des zu ermittelnden Dokuments (nicht UUID)
     * @param archiveLoginName Login-Name fuer das Archiv
     * @return Dokument, das zu dem Key gefunden wurde
     * @throws FindException wenn bei der Ermittlung des Dokuments ein Fehler auftritt
     */
    ArchiveDocumentDto retrieveDocument(String vertragsnummer, ArchiveDocumentType documentType, String key,
            String archiveLoginName) throws FindException;

    /**
     * Ermittelt aus dem M-net Archiv ein Dokument ueber die UUID - zur Verwendung ohne Suche.
     *
     * @param uuid UUID des zu ermittelnden Dokuments (nicht "eindeutiger Key")
     * @return Dokument, das zu dem Key gefunden wurde
     * @throws FindException wenn bei der Ermittlung des Dokuments ein Fehler auftritt
     */
    ArchiveDocumentDto retrieveDocumentByUuid(String uuid, String archiveLoginName) throws FindException;

    /**
     * Archiviert ein Dokument im M-net Archiv und liefert die UUID des archivierten Dokuments zurueck.
     *
     * @param document Dokument, das zu dem Key gefunden wurde
     * @param metaData Metadaten, die fuer das Dokument ins Archiv eingetragen werden muessen
     * @return UUID fuer das archivierte Dokument
     */
    String archiveDocument(byte[] document, ArchiveMetaData metaData);
}
