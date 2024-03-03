/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.billing.BAuftragBNFC;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Command-Klasse, um Daten von Servicerufnummern zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetSRNDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetSRNDatenCommand.class);

    public static final String SRN = "Servicerufnummer";
    public static final String PREFIX = "Prefix";
    public static final String ZIELRN = "ZielRN";
    public static final String BASE = "Base";

    private Long orderNoOrig = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            readRufnummerDaten();
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
        return SRN_DATEN;
    }

    /* Ermittelt die SRN eines Auftrags und schreibt diese in die HashMap. */
    protected void readRufnummerDaten() throws HurricanServiceCommandException {
        try {
            RufnummerService rs = (RufnummerService) getBillingService(RufnummerService.class);
            BAuftragBNFC bn = rs.findBusinessNumber(orderNoOrig);
            map.put(getPropName(PREFIX), (bn != null) ? bn.getPrefix() : null);
            map.put(getPropName(BASE), (bn != null) ? bn.getBusinessNr() : null);
            map.put(getPropName(ZIELRN), (bn != null) ? bn.getDestination() : null);
            map.put(getPropName(SRN), (bn != null && bn.getPrefix() != null
                    && bn.getBusinessNr() != null) ? bn.getPrefix() + bn.getBusinessNr() : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(ORDER_NO_ORIG);
        orderNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (orderNoOrig == null) {
            throw new HurricanServiceCommandException("Order__No wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetSRNDatenCommand.properties";
    }
}


