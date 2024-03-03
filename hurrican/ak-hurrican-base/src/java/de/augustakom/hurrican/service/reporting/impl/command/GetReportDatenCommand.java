/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 09:30:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Command-Klasse, um Daten zum aktuellen Report zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetReportDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetReportDatenCommand.class);

    // Konstanten für Properties
    public static final String DATUM = "Erstellungsdatum";
    public static final String DATETIME = "Datetime";

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
            readReportDaten();
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
        return REPORT_DATEN;
    }

    /* Ermittelt Daten zum aktuellen Report und schreibt diese in die HashMap. */
    protected void readReportDaten() throws HurricanServiceCommandException {
        try {
            ReportService rs = getReportService(ReportService.class);
            ReportRequest request = rs.findReportRequest(requestId);

            String datum = null;
            String zeit = null;
            String datetime = null;
            if (request != null) {
                datum = DateTools.formatDate(request.getRequestAt(), DateTools.PATTERN_DAY_MONTH_YEAR);
                zeit = DateTools.formatDate(request.getRequestAt(), DateTools.PATTERN_TIME);
                datetime = datum + " " + zeit;
            }

            // Erstellungsdatum
            map.put(getPropName(DATUM), StringUtils.trimToEmpty(datum));
            map.put(getPropName(DATETIME), StringUtils.trimToEmpty(datetime));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmp = getPreparedValue(REQUEST_ID);
        requestId = (tmp instanceof Long) ? (Long) tmp : null;
        if (requestId == null) {
            throw new HurricanServiceCommandException("RequestId wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetReportDatenCommand.properties";
    }
}


