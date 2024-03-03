/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.HurricanProcessor;
import de.mnet.common.service.HistoryService;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.wbci.exception.WbciProcessorException;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.model.IoArchive;

/**
 * Abstrakter Apache Camel Processor, um aus dem an Atlas CDM Daten ein IOArchive Objekt zu erstellen.
 */
@Component
public abstract class WbciIoArchiveProcessor implements HurricanProcessor, WbciCamelConstants {

    @Autowired
    private HistoryService historyService;

    @Override
    public void process(Exchange exchange) throws Exception {
        WbciMessage wbciMessage = getOriginalMessage(exchange);
        // convert wbci message and cdm values to io archive entry
        IoArchive archive = createIoArchive(exchange, wbciMessage);
        historyService.saveIoArchive(archive);
    }

    protected IoArchive createIoArchive(Exchange exchange, WbciMessage wbciMessage) {
        IoArchive archive = new IoArchive();
        archive.setIoSource(IOArchiveProperties.IOSource.WBCI);
        switch (wbciMessage.getIoType()) {
            case IN:
                archive.setIoType(IOArchiveProperties.IOType.IN);
                break;
            case OUT:
                archive.setIoType(IOArchiveProperties.IOType.OUT);
                break;
            default:
                archive.setIoType(IOArchiveProperties.IOType.UNDEFINED);
                break;
        }

        if (wbciMessage.getWbciGeschaeftsfall() != null) {
            archive.setWitaExtOrderNo(wbciMessage.getWbciGeschaeftsfall().getVorabstimmungsId());
            archive.setRequestGeschaeftsfall(wbciMessage.getWbciGeschaeftsfall().getTyp().name());
        }
        else {
            throw new WbciProcessorException("To process the wbci message the wbciGeschaeftsfall have to be not NULL");
        }

        archive.setTimestampSent(wbciMessage.getProcessedAt());
        archive.setRequestTimestamp(wbciMessage.getProcessedAt());
        if (wbciMessage instanceof Meldung) {
            archive.setRequestMeldungscode(((Meldung) wbciMessage).getMeldungsCodes());
            archive.setRequestMeldungstext(((Meldung) wbciMessage).getMeldungsTexte());
        }

        archive.setRequestMeldungstyp(wbciMessage.getTyp().toString());

        archive.setRequestXml(XmlPrettyFormatter.prettyFormat(getRequestXml(exchange)));

        return archive;
    }

    /**
     * Gets the initial request XML from exchange. This depends on route direction in/out and has to be implemented by
     * subclasses.
     *
     * @param exchange
     * @return
     */
    protected abstract String getRequestXml(Exchange exchange);

}
