/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 11:29:05
 */
package de.augustakom.hurrican.gui.hvt.actions;

import java.awt.*;
import java.io.*;
import javax.annotation.*;
import javax.swing.*;
import com.google.common.base.Function;
import com.google.common.io.Files;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.file.FileFilterExtension;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;

/**
 * Abstrakte Klasse fuer den Import von HVT-Standorten, Baugruppen, UEVT Stiften via Excellisten
 */
public abstract class AbstractImportAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(AbstractImportAction.class);

    public File chooseFile(String propertyKey, int fileSelectionMode, String title) {
        final JFileChooser fileChooser = new JFileChooser(System.getProperty(propertyKey));
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(fileSelectionMode);
        if (fileSelectionMode == JFileChooser.FILES_ONLY) {
            fileChooser.addChoosableFileFilter(new FileFilterExtension(FileFilterExtension.FILE_EXTENSION_EXCEL_ALL));
            fileChooser.setAcceptAllFileFilterUsed(Boolean.FALSE);
        }

        if (fileChooser.showOpenDialog(HurricanSystemRegistry.instance().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile() != null) {
                System.setProperty(propertyKey, fileChooser.getSelectedFile().getAbsolutePath());
            }
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public byte[] readData(File importFile) {
        try {
            return FileTools.convertToByteArray(importFile);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }

        return null;
    }

    /**
     * @param operation
     * @param propertyKey
     * @param postProcess          Function um das Anzeigen des Result-Dialoges zu uebersteuern
     * @param outputFileNameAppend falls (nicht null) wird die die modifierte Exceldatei mit dem angehaengten Suffix
     *                             <em>outputFileNameAppend</em> geschrieben
     */
    public void executeOperation(Function<byte[], XlsImportResultView[]> operation, String propertyKey,
            @Nullable Function<XlsImportResultView[], Void> postProcess, @Nullable String outputFileNameAppend) {
        File importFile = chooseFile(propertyKey, JFileChooser.FILES_ONLY, "Bitte Datei zum Import auswählen");
        if (importFile != null) {
            int option = MessageHelper.showYesNoQuestion(
                    getMainFrame(),
                    String.format("Sollen die Daten jetzt importiert werden? Importpfad: %s",
                            importFile.getAbsolutePath()), "Import durchführen?"
            );

            if (option == JOptionPane.YES_OPTION) {
                try {
                    getMainFrame().showProgressInLastField("Importiere Daten...");
                    getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    byte[] xlsData = readData(importFile);
                    if ((xlsData == null) || (xlsData.length <= 0)) {
                        MessageHelper.showInfoDialog(GUISystemRegistry.instance().getMainFrame(),
                                String.format(
                                        "Die selektierte Datei \"%s\" enthält keine Daten, Verarbeitung abgebrochen!",
                                        importFile.getName())
                        );
                        return;
                    }
                    XlsImportResultView resultView[] = operation.apply(xlsData);
                    if (resultView == null) {
                        MessageHelper.showInfoDialog(GUISystemRegistry.instance().getMainFrame(),
                                String.format("Verarbeitung der Datei \"%s\" abgebrochen!", importFile.getName()));
                        return;
                    }

                    getMainFrame().stopProgressInLastField();

                    if (postProcess == null) {
                        showResultDialog(resultView);
                    }
                    else {
                        postProcess.apply(resultView);
                    }

                    if (outputFileNameAppend != null && resultView.length > 0) {
                        String fileName = importFile.getName();
                        int dot = fileName.lastIndexOf('.');
                        String outputFileName = fileName.substring(0, dot).concat(outputFileNameAppend)
                                .concat(fileName.substring(dot));
                        byte[] xlsDataOut = resultView[0].getXlsDataOut();
                        if (xlsDataOut != null) {
                            try {
                                final File file = FileTools.createWriteFile(importFile.getParent(), outputFileName);
                                Files.write(xlsDataOut, file);
                            }
                            catch (IOException e3) {
                                MessageHelper.showInfoDialog(
                                        GUISystemRegistry.instance().getMainFrame(),
                                        String.format("Kann Datei \"%s\" nicht schreiben: %s", outputFileName,
                                                e3.toString())
                                );
                            }
                        }
                    }
                }
                finally {
                    getMainFrame().stopProgressInLastField();
                    getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }

    protected void showResultDialog(XlsImportResultView[] resultView) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < resultView.length; i++) {
            str.append(resultView[i]);
            str.append("\n\n");
        }
        MessageHelper.showInfoDialog(getMainFrame(), new Dimension(750, 300), str.toString(),
                "Ergebnis des Imports", null, true);
    }
}
