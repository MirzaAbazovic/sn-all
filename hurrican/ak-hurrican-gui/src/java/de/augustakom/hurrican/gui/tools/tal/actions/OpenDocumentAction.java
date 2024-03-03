/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2012 16:42:08
 */
package de.augustakom.hurrican.gui.tools.tal.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;

/*
 * Action, um ein ausgewaehltes Dokument zu laden und zu oeffnen.
 */
public class OpenDocumentAction extends AKAbstractAction {

    private static final Logger LOGGER = Logger.getLogger(OpenDocumentAction.class);

    private final OpenDocumentPanel panel;
    private static final long serialVersionUID = -3630688978781665522L;

    public OpenDocumentAction(OpenDocumentPanel panel) {
        super();

        this.panel = panel;
        setName("Dokument öffnen");
        setTooltip("Lädt das Dokument in eine temp. Datei und öffnet diese");
        setActionCommand("open.document.action");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ArchiveDocumentDto archiveDoc = panel.getSelectedDocument();
            if (archiveDoc == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst ein Dokument aus.");
            }

            ArchiveDocumentDto downloadableArchiveDoc = null;
            if (archiveDoc.getStream() != null) {
                downloadableArchiveDoc = archiveDoc;
            }
            else {
                downloadableArchiveDoc = panel.getArchiveService().retrieveDocument(
                        archiveDoc.getVertragsNr(),
                        archiveDoc.getDocumentType(), archiveDoc.getKey(), HurricanSystemRegistry.instance()
                                .getCurrentLoginName()
                );
            }

            if (downloadableArchiveDoc == null) {
                throw new HurricanGUIException("Das File konnte aus dem Archiv nicht geladen werden!");
            }

            String fileName = String.format("archive_%s.%s", System.currentTimeMillis(),
                    downloadableArchiveDoc.getFileExtension());
            File downloadedFile = downloadableArchiveDoc.convertToFile(System.getProperty("java.io.tmpdir"), fileName, true);
            Desktop.getDesktop().open(downloadedFile);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }
}

