/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2010
 */
package de.mnet.hurrican.reporting;

import javax.jms.*;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.mail.SimpleMailMessage;

import de.augustakom.hurrican.service.reporting.ReportService;
import de.mnet.hurrican.reporting.service.OpenOfficeService;
import de.mnet.hurrican.tools.mail.ErrorHandler;

public class ReportRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(ReportRequestMessageListener.class);

    private JmsTemplate jmsTemplateToSendResponses;
    private OpenOfficeService openOfficeService;
    private SimpleMailMessage mailMessage;
    private ReportService reportService;
    private ErrorHandler errorHandler;

    public ReportRequestMessageListener(JmsTemplate jmsTemplateToSendResponses, OpenOfficeService openOfficeService,
            ReportService reportService, ErrorHandler errorHandler, SimpleMailMessage mailMessage) {
        this.jmsTemplateToSendResponses = jmsTemplateToSendResponses;
        this.openOfficeService = openOfficeService;
        this.reportService = reportService;
        this.errorHandler = errorHandler;
        this.mailMessage = mailMessage;
    }

    @Override
    public void onMessage(Message receivedMessage) {
        Long requestId = Long.valueOf(0);
        Long sessionId = Long.valueOf(0);
        try {
            if (receivedMessage instanceof MapMessage) {
                MapMessage message = (MapMessage) receivedMessage;
                requestId = message.getLong(ReportService.MSG_KEY_REQUEST_ID);
                sessionId = message.getLong(ReportService.MSG_KEY_SESSION_ID);
                if (requestId != 0) {
                    reportService.generateReport(requestId, openOfficeService.getConnection());
                    sendResponse(requestId, ReportService.MSG_STATUS_OK, sessionId, null);
                }
            }
        }
        catch (Exception ex) {
            String errorMsg = ex.getMessage();
            LOGGER.error(errorMsg, ex);
            if (requestId != 0) {
                sendResponse(requestId, ReportService.MSG_STATUS_ERROR, sessionId, errorMsg);
                errorMsg = "ReportRequest Nr. " + requestId + ": " + errorMsg;
            }
            // create copy of mailMessage because handleError will modify mailMessage argument
            // -> this may lead to concurrency problems
            errorHandler.handleError(new SimpleMailMessage(mailMessage), ex, errorMsg);
        }
        finally {
            try {
                receivedMessage.acknowledge();
            }
            catch (Exception e) {
                LOGGER.warn("onMessage() - acknowledge message failed with exception", e);
            }
        }
    }

    private void sendResponse(final Long requestId, final Integer msgStatus, final Long sessionId, final String errorMsg) {
        jmsTemplateToSendResponses.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setInt(ReportService.MSG_KEY_STATUS, msgStatus);
                message.setLong(ReportService.MSG_KEY_REQUEST_ID, requestId);
                message.setString(ReportService.MSG_KEY_ERROR_TEXT, errorMsg);
                message.setLong(ReportService.MSG_KEY_RECEIVER, sessionId);
                return message;
            }
        });
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setOpenOfficeService(OpenOfficeService openOfficeService) {
        this.openOfficeService = openOfficeService;
    }

}
