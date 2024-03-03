/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2007 10:00:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.reporting.ReportData;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Command-Klasse, um Text-Bausteine zu einem best. ReportRequest zu ermittlen und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetTxtBausteineCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetTxtBausteineCommand.class);

    private Long requestId = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            read();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    public String getPrefix() {
        return TXT_BAUSTEINE;
    }

    /* Ermittelt die Text-Bausteine und schreibt diese in die HashMap. */
    protected void read() throws HurricanServiceCommandException {
        try {
            ReportService rs = (ReportService) getReportService(ReportService.class);
            ReportRequest request = rs.findReportRequest(requestId);
            if (request != null) {
                List<TxtBausteinGruppe> list = rs.findAllTxtBausteinGruppen4Report(request.getRepId());
                if (CollectionTools.isNotEmpty(list)) {
                    for (TxtBausteinGruppe gruppe : list) {
                        if (gruppe != null) {
                            List<String> value = rs.findReportDataByKey(requestId, gruppe.getName());
                            map.put(getPropName(gruppe.getName()), CollectionTools.isNotEmpty(value) ? value.get(0) : "");
                        }
                    }
                }
                else {
                    List<ReportData> data = rs.findAllReportDataByRequestId(requestId);
                    if (CollectionTools.isNotEmpty(data)) {
                        for (ReportData rd : data) {
                            map.put(getPropName(rd.getKeyName()), (rd.getKeyValue() != null) ? rd.getKeyValue() : "");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(REQUEST_ID);
        requestId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (requestId == null) {
            throw new HurricanServiceCommandException("Request-ID wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetTxtBausteineCommand.properties";
    }
}


