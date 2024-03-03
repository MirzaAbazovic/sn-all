/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.14
 */
package de.mnet.wita.route.processor;

import java.sql.*;
import java.time.*;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.HurricanProcessor;
import de.mnet.common.service.HistoryService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.wbci.model.IOType;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.IoArchive;

/**
 *
 */
@Component
public abstract class WitaIoArchiveProcessor implements HurricanProcessor {

    @Autowired
    private HistoryService historyService;

    @Override
    public void process(Exchange exchange) throws Exception {
        WitaMessage witaMessage = getOriginalMessage(exchange);
        // convert wita message and cdm values to io archive entry
        IoArchive archive = createIoArchive(exchange, witaMessage);
        historyService.saveIoArchive(archive);
    }

    protected IoArchive createIoArchive(Exchange exchange, WitaMessage witaMessage) {
        IoArchive archive = new IoArchive();
        LocalDateTime versandzeitstempel = null;

        if (witaMessage instanceof MnetWitaRequest) {
            MnetWitaRequest request = (MnetWitaRequest) witaMessage;
            versandzeitstempel = request.getSentAt() != null ? DateConverterUtils.asLocalDateTime(request.getSentAt()) : LocalDateTime.now();
            archive.setWitaExtOrderNo(request.getExterneAuftragsnummer());
            archive.setRequestGeschaeftsfall(request.getGeschaeftsfall().getGeschaeftsfallTyp().name());
            archive.setRequestTimestamp(request.getMwfCreationDate());
            archive.setRequestMeldungstyp(request.getMeldungstypForIoArchive());
            archive.setWitaVertragsnummer(request.getGeschaeftsfall().getVertragsNummer());
        }
        else if (witaMessage instanceof Meldung) {
            Meldung meldung = (Meldung) witaMessage;
            versandzeitstempel = meldung.getVersandZeitstempel() != null ? DateConverterUtils.asLocalDateTime(meldung.getVersandZeitstempel()) : LocalDateTime.now();
            archive.setWitaExtOrderNo(meldung.getExterneAuftragsnummer());
            archive.setRequestGeschaeftsfall(meldung.getGeschaeftsfallTyp().name());
            archive.setRequestTimestamp(Date.from(versandzeitstempel.atZone(ZoneId.systemDefault()).toInstant()));
            archive.setRequestMeldungstyp(meldung.getMeldungsTyp().getValue());
            archive.setRequestMeldungscode(meldung.getMeldungsCodes());
            archive.setRequestMeldungstext(meldung.getMeldungsTexte());
            archive.setWitaVertragsnummer(meldung.getVertragsNummer());
        }
        archive.setTimestampSent(Date.from(versandzeitstempel.atZone(ZoneId.systemDefault()).toInstant()));
        archive.setIoSource(IOArchiveProperties.IOSource.WITA);
        archive.setIoType(getIOType());
        archive.setRequestXml(XmlPrettyFormatter.prettyFormat(getRequestXml(exchange)));
        return archive;
    }

    /**
     * @return the current valid {@link IOType}.
     */
    protected abstract IOArchiveProperties.IOType getIOType();

    /**
     * Gets the initial request XML from exchange. This depends on route direction in/out and has to be implemented by
     * subclasses.
     *
     * @param exchange
     * @return
     */
    protected abstract String getRequestXml(Exchange exchange);
}
