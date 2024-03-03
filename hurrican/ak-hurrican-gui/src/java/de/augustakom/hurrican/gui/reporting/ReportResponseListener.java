/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2007 13:40:47
 */
package de.augustakom.hurrican.gui.reporting;

import java.lang.reflect.*;
import javax.jms.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.service.base.exceptions.ReportException;
import de.augustakom.hurrican.service.reporting.ReportService;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;

/**
 * MessageListener für die Verbindung zum JMS-Server um Rueckmeldungen vom Report-Server empfangen zu können.
 */
public class ReportResponseListener implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(ReportResponseListener.class);

    public void onMessage(final Message message) {
        if (!(message instanceof MapMessage)) {
            return;
        }
        final MapMessage mapMessage = (MapMessage) message;
        try {
            if (NumberTools.notEqual(mapMessage.getLong(ReportService.MSG_KEY_RECEIVER), HurricanSystemRegistry.instance().getSessionId())) {
                // from another user/session generated -> do not process this message
                return;
            }
            processMessageInGuiThread(mapMessage);
        }
        catch (JMSException e1) {
            LOGGER.error(e1.getMessage());
        }
    }

    private void processMessageInGuiThread(final MapMessage mapMessage) {
        try {
            // Die Dialoge sollen im GUI-Thread gestartet werden.
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    try {
                        long requestId = mapMessage.getLong(ReportService.MSG_KEY_REQUEST_ID);

                        // Im Fehlerfall zeige einen Info-Dialog
                        if (mapMessage.getInt(ReportService.MSG_KEY_STATUS) == ReportService.MSG_STATUS_ERROR) {
                            showInfoDialogForReportError(mapMessage, requestId);
                        }
                        else {
                            if (requestId != 0) {
                                ReportService rs = ReportServiceFinder.instance().getReportService(ReportService.class);
                                ReportRequest request = rs.findReportRequest(requestId);

                                // Unterscheide Report und Serienbrief
                                if (request.getBuendelNo() == null) {
                                    handleReport(request, requestId);
                                }
                                else if (rs.testSerienbriefReady(request.getBuendelNo()) && (request.getReportDownloadedAt() == null)) {
                                    handleSerienbrief(request, requestId);
                                }
                            }
                            else {
                                MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(), "Report konnte nicht angezeigt werden.", null, true);
                            }
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(),
                                new ReportException("Fehler beim Empfang einer Rückmeldung des Report-Servers!", e));
                    }
                }
            });
        }
        catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            try {
                mapMessage.acknowledge();
            }
            catch (JMSException e) {
                LOGGER.error("onMessage() - acknowledge was not successful " + e.getMessage(), e);
            }
        }
    }

    private void showInfoDialogForReportError(MapMessage mapMessage, long requestId) throws JMSException {
        String errorText = "Beim Erzeugen des Reports ";
        if (requestId != 0) {
            errorText += "für die Anfrage " + requestId + " ";
        }
        errorText += "ist ein Fehler aufgetreten.\n";
        if (StringUtils.isNotBlank(mapMessage.getString(ReportService.MSG_KEY_ERROR_TEXT))) {
            errorText += mapMessage.getString(ReportService.MSG_KEY_ERROR_TEXT);
        }
        MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(), errorText, null, true);
    }

    private void handleSerienbrief(ReportRequest request, long requestId) {
        // Frage, ob Serienbrief angezeigt werden soll
        int result = MessageHelper.showYesNoQuestion(HurricanSystemRegistry.instance().getMainFrame(),
                "Serienbrief wurde fertig gestellt.\nMöchten Sie den " +
                        "Serienbrief anzeigen?", "Report-Server"
        );

        // Falls Nutzer Serienbrief angezeigt haben möchte, starte Viewer
        if (result == JOptionPane.YES_OPTION) {
            ShowReportFrame.openFrame(null, request.getBuendelNo(), null);
        }
    }

    private void handleReport(ReportRequest request, long requestId) {
        // Frage, ob Report angezeigt werden soll
        int result = MessageHelper.showYesNoQuestion(HurricanSystemRegistry.instance().getMainFrame(),
                "Report-Anfrage " + requestId + " ist fertig gestellt.\nMöchten Sie den " +
                        "Report anzeigen?", "Report-Server"
        );

        // Falls Nutzer Report angezeigt haben möchte, starte Viewer
        if (result == JOptionPane.YES_OPTION) {
            ShowReportFrame.openFrame(request, null, null);
        }
    }

}
