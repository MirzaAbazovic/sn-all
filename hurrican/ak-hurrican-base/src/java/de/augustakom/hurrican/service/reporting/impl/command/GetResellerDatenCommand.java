/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Command-Klasse, um Reseller-Daten anhand der KundenNoOrig zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetResellerDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetResellerDatenCommand.class);

    private Long kundeNoOrig = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            readResellerDaten();

            return (!map.isEmpty()) ? map : null;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    public String getPrefix() {
        return RESELLER_DATEN;
    }

    /* Ermittelt die Resellerdaten zu einer KundenNoOrig und schreibt diese in die HashMap. */
    protected void readResellerDaten() throws HurricanServiceCommandException {
        try {
            // Lade Properties-File
            Properties props = PropertyUtil.loadPropertyHierarchy(Arrays.asList("ReportDatenReseller"), "properties", true);

            if (!props.isEmpty()) {
                // Ermittle Reseller
                // Suche Kunde anhand der KundeNoOrig, geliefert wird der Kundendatensatz mit hist_status=akt
                KundenService ks = (KundenService) getBillingService(KundenService.class);
                Kunde kunde = ks.findKunde(kundeNoOrig);

                if (kunde != null && kunde.getResellerKundeNo() != null) {
                    Iterator iter = props.keySet().iterator();

                    // Ermittle alle Properties für den Reseller des Kunden
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        if (StringUtils.equals(StringUtils.substringBefore(key, "."), Long.toString(kunde.getResellerKundeNo()))) {
                            String propKey = StringUtils.substringAfter(key, ".");
                            String propValue = props.getProperty(key);
                            map.put(getPropName(propKey), StringUtils.trimToEmpty(propValue));
                        }
                    }
                }
                else {
                    throw new FindException("Reseller-Daten zu KundeNoOrig " +
                            kundeNoOrig + " konnten nicht ermittelt werden!");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }


    /**
     * Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde.
     */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(KUNDE_NO_ORIG);
        kundeNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (kundeNoOrig == null) {
            throw new HurricanServiceCommandException("Kunde__No wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetResellerDatenCommand.properties";
    }
}


