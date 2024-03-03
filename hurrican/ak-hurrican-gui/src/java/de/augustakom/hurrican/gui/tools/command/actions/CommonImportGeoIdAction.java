/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2009 10:02:43
 */
package de.augustakom.hurrican.gui.tools.command.actions;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.file.FileFilterExtension;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.service.cc.AvailabilityService;

/**
 * Abstrakte Action, für den Import von GeoIDs
 */
public class CommonImportGeoIdAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(CommonImportGeoIdAction.class);
    private static final long serialVersionUID = 3618188707696050063L;

    // werden in den konkreten Implementierungen im Konstruktor gesetzt
    protected String CONFIRM_DIALOG_MESSAGE;
    protected String CONFIRM_DIALOG_TITLE;
    protected String FILE_CHOOSER_DIALOG_TITLE;

    protected GeoIdImportAction currentAction;

    @Override
    public void actionPerformed(ActionEvent e) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(new FileFilterExtension(FileFilterExtension.FILE_EXTENSION_EXCEL));
        fileChooser.setAcceptAllFileFilterUsed(Boolean.FALSE);
        fileChooser.setDialogTitle(FILE_CHOOSER_DIALOG_TITLE);

        if (fileChooser.showOpenDialog(HurricanSystemRegistry.instance().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File importFile = fileChooser.getSelectedFile();

            if (importFile != null && showConfirmDialog()) {
                try {
                    Map<String, Object> result = currentAction.execute(importFile.getAbsolutePath());
                    MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(), buildMessage(result).toString());
                }
                catch (Exception se) {
                    LOGGER.error(se.getMessage(), se);
                    MessageHelper.showErrorDialog(getMainFrame(), se);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    protected StringBuilder buildMessage(Map<String, Object> returnValue) {
        StringBuilder messageBuffer = new StringBuilder();
        List<Exception> exceptionList = (List<Exception>) getList(returnValue, AvailabilityService.EXCEPTION);
        List<String> successList = (List<String>) getList(returnValue, AvailabilityService.SUCCESS);
        List<String> warningList = (List<String>) getList(returnValue, AvailabilityService.WARNING);

        if (successList != null) {
            messageBuffer.append(String.format("Es wurden %s Datensätze erfolgreich importiert.%n%n", successList.size()));
        }

        if ((exceptionList != null) && (!exceptionList.isEmpty())) {
            messageBuffer.append(String.format("Es wurden %s Datensätze nicht importiert:%n%n", exceptionList.size()));
            for (Exception exception : exceptionList) {
                messageBuffer.append("- ");
                messageBuffer.append(exception.getMessage());
                messageBuffer.append("\n");
            }
            messageBuffer.append("\n");
        }

        if ((warningList != null) && (!warningList.isEmpty())) {
            messageBuffer.append(String.format("%s Warnungen sind während des Imports aufgetreten:%n%n", warningList.size()));
            for (String warning : warningList) {
                messageBuffer.append("- ");
                messageBuffer.append(warning);
                messageBuffer.append("\n");
            }
            messageBuffer.append("\n");
        }
        return messageBuffer;
    }

    /**
     * @param returnValue
     * @param type
     * @return
     */
    private List<?> getList(Map<String, Object> returnValue, String type) {
        if ((returnValue != null) && (type != null) && (returnValue.get(type) != null) && (returnValue.get(type) instanceof List<?>)) {
            return (List<?>) returnValue.get(type);
        }
        return null;
    }

    /**
     * Zeigt den Dialog mit der "Sicherheitsabfrage" an
     *
     * @return
     */
    protected boolean showConfirmDialog() {
        return MessageHelper.showConfirmDialog(
                HurricanSystemRegistry.instance().getMainFrame(),
                CONFIRM_DIALOG_MESSAGE,
                CONFIRM_DIALOG_TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

}
