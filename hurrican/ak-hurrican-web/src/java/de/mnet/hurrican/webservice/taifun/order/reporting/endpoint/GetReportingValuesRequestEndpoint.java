/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2009 11:49:08
 */
package de.mnet.hurrican.webservice.taifun.order.reporting.endpoint;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.cc.ReportingService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesType;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesType.ReportingKey.ReportingKey2;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesTypeAcknowledgement;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesTypeAcknowledgement.ReportingValues;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesTypeAcknowledgement.ReportingValues.ReportingKey;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesTypeAcknowledgementDocument;
import de.mnet.hurricanweb.order.reporting.types.GetReportingValuesTypeDocument;


/**
 * Endpoint-Implementierung, um technische Auftragsdaten zu ermitteln und dem Caller zur Verfuegung zu stellen.
 *
 *
 */
public class GetReportingValuesRequestEndpoint extends MnetAbstractMarshallingPayloadEndpoint {
    private static final Logger LOGGER = Logger.getLogger(GetReportingValuesRequestEndpoint.class);

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        ReportingService reportingService;
        try {
            reportingService = getCCService(ReportingService.class);
        }
        catch (Exception e) {
            LOGGER.error("reportingService not found", e);
            throw e;
        }

        LOGGER.debug("--------- GetReportingValuesRequestEndpoint called");

        GetReportingValuesTypeDocument inputDocument = (GetReportingValuesTypeDocument) requestObject;
        GetReportingValuesType input = inputDocument.getGetReportingValuesType();
        Long orderNo = Long.valueOf(input.getOrderNo());

        GetReportingValuesTypeAcknowledgementDocument repTypeAckDoc =
                GetReportingValuesTypeAcknowledgementDocument.Factory.newInstance();
        GetReportingValuesTypeAcknowledgement repTypeAck = repTypeAckDoc.addNewGetReportingValuesTypeAcknowledgement();
        repTypeAck.setOrderNo(orderNo);
        ReportingValues values = repTypeAck.addNewReportingValues();

        List<ReportingKey2> keyList = input.getReportingKey().getReportingKeyList();
        for (ReportingKey2 reportingKey : keyList) {
            String reportingKeyName = reportingKey.getReportingKeyName();
            String value = reportingService.getValue(orderNo, reportingKeyName);

            // Wurde so vom Auftragsmanagement (Norman Seyfarth) gewuenscht -> Statt eines leeren Strings
            // wird ein Leerzeichen zurueckgegeben, damit Taifun das Feld nicht als 'Kein Wert gefunden'
            // markiert.
            if ((value == null) || "".equals(value)) {
                value = " ";
            }

            ReportingKey key = values.addNewReportingKey();
            key.setReportingKeyName(reportingKeyName);
            key.setReportingKeyValues(value);
        }

        return repTypeAck;
    }

}
