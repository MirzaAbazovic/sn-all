/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2012 16:44:14
 */
package de.augustakom.hurrican.gui.tools.tal.actions;

import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;


/**
 * Panel that provides a document to be opened.
 *
 *
 */
public interface OpenDocumentPanel {

    /**
     * Gibt das zu oeffnende Dokument zurueck.
     *
     * @throws HurricanGUIException
     */
    ArchiveDocumentDto getSelectedDocument() throws HurricanGUIException;

    /**
     * Gibt den ArchiveService zurueck.
     */
    ArchiveService getArchiveService();

}


