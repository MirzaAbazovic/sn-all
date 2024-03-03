/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2011 15:28:04
 */
package de.mnet.hurrican.archive;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.antivirus.scan.AntivirusScanService;
import de.mnet.hurrican.reporting.documentconvertor.service.DocumentConvertorService;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.service.WitaService;


/**
 * Service orchestriert mehrere Funktionalitaeten, um ein Dokument im Scan-View zu archivieren. Dazu wird zunaecht das
 * Dokument auf Viren ueberprueft {@link AntivirusScanService}. Ist der Stream frei von Viren wird das Dokument ggf.
 * konvertiert {@link DocumentConvertorService}. Zuletzt wird das Dokument vom {@link ArchiveService} im
 * Scan-View-Archiv gespeichert.
 */
public interface ArchiveDumperService extends WitaService {

    /**
     * Dokument wird archiviert und gibt UUID zurück, mit der im Scanview das Dokument eindeutig identifiziert werden
     * kann.
     * <p/>
     * <p/>
     * <b>Achtung: </b> Archiviert nur Dokumente die eindeutig einem Auftrag zugeordnet werden konnten. Sonst wird die
     * Anlage einfach ignoriert. Dies kann eintreten, wenn die AKM-PV nicht eindeutig einem Hurrican-Auftrag zugeordnet
     * werden kann.
     *
     * @throws FindException wenn bei einer der Suchen ein Fehler auftritt.
     */
    public void archiveDocument(Anlage anlage) throws FindException;

}


