/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Command-Klasse, um Rufnummern-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetRufnummernCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetRufnummernCommand.class);

    public static final String RN_PORTIERUNG_LIST = "RNPortierungList";
    public static final String RUFNUMMERN = "rufnummern";
    public static final String PORTIERUNG = "portierung";
    public static final String RN_LIST = "RNList";
    public static final String ONKZ = "onkz";
    public static final String BASE = "base";
    public static final String RN = "rn";

    private Long orderNoOrig = null;
    private Map<String, Object> map = null;

    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<>();

            readRufnummerDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    @Override
    public String getPrefix() {
        return RUFNUMMERN_DATEN;
    }

    /**
     * Ermittelt die Rufnummern eines Auftrags und schreibt diese in die HashMap.
     */
    protected void readRufnummerDaten() throws HurricanServiceCommandException {
        try {
            RufnummerService rs = getBillingService(RufnummerService.class);
            List<Rufnummer> rufnummern = rs.findRNs4Auftrag(orderNoOrig);
            List<Map<String, Object>> dnMaps = new ArrayList<>();
            List<Rufnummer> rufnummerPortierungen = new ArrayList<>();
            Boolean portierung = Boolean.FALSE;
            String onkz = null;

            if (rufnummern != null) {
                for (int i = 0; i < rufnummern.size(); i++) {
                    Rufnummer rn = rufnummern.get(i);
                    if (rn != null) {
                        Map<String, Object> dn = new HashMap<>();
                        dn.put(ONKZ, rn.getOnKz());
                        dn.put(BASE, rn.getDnBase());
                        dn.put(RN, rn.getRufnummer());
                        dnMaps.add(dn);
                        if (BooleanTools.nullToFalse(getFlagPortierung(rn))) {
                            portierung = Boolean.TRUE;
                            if (onkz == null) {
                                onkz = rn.getOnKz();
                            }
                            rufnummerPortierungen.add(rn);
                        }
                    }
                }
                map.put(getPropName(RUFNUMMERN), dnMaps);

                map.put(getPropName(RN_LIST), getRufnummernAsString(rufnummern));

                map.put(getPropName(ONKZ), onkz);

                map.put(getPropName(PORTIERUNG), portierung);
                map.put(getPropName(RN_PORTIERUNG_LIST), getRufnummerPortierungenAsString(rufnummerPortierungen));
            }
            else {
                throw new FindException("Rufnummern-Daten zu Order__No " +
                        orderNoOrig + " konnten nicht ermittelt werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * Ermittelt, ob die Rufnummer portiert werden muss
     */
    private Boolean getFlagPortierung(Rufnummer rn) {
        if (rn == null) {return Boolean.FALSE;}
        if (StringUtils.equals(Rufnummer.PORT_MODE_KOMMEND, rn.getPortMode())
                && StringUtils.equals(Rufnummer.LAST_CARRIER_DTAG, rn.getLastCarrier())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Funktion erzeugt aus Liste der zu portierenden Rufnummern einen String, der auf Anschreiben direkt angedruckt
     * werden kann.
     */
    private String getRufnummerPortierungenAsString(List<Rufnummer> rufnummerPortierungen) {
        if ((rufnummerPortierungen == null) || (rufnummerPortierungen.isEmpty())) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Rufnummer rn : rufnummerPortierungen) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(rn.getDnBaseAndDirect());
        }
        return sb.toString();
    }

    /**
     * Funktion erzeugt aus den Rufnummern einen String, der auf Anschreiben direkt angedruckt werden kann.
     */
    private String getRufnummernAsString(List<Rufnummer> rufnummern) {
        if ((rufnummern == null) || (rufnummern.isEmpty())) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Rufnummer rn : rufnummern) {
            sb.append(rn.getRufnummer()).append(", ");
        }
        // Entferne letzte zwei Zeichen
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde.
     */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(ORDER_NO_ORIG);
        orderNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (orderNoOrig == null) {
            throw new HurricanServiceCommandException("Order__No wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetRufnummerDatenCommand.properties";
    }
}


