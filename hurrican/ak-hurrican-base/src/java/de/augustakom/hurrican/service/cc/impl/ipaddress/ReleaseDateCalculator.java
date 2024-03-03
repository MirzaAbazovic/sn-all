/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2011 12:49:40
 */
package de.augustakom.hurrican.service.cc.impl.ipaddress;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.time.DateUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RegistryService;

/**
 * Errechnet das Datum zu welchem eine IP-Adresse freigegeben werden kann. Das hat etwas mit der Gueltigkeit der
 * IP-Adressen zu tun. Basis der Berechnung ist eine Karenzzeit (in Tagen), die in der Datenbank konfiguriert ist und
 * über den {@link RegistryService} abgefragt werden kann.
 */
public class ReleaseDateCalculator {

    @Resource(name = "de.augustakom.hurrican.service.cc.RegistryService")
    private RegistryService registryService;

    /**
     * @return Returns the registryService.
     */
    protected RegistryService getRegistryService() {
        return registryService;
    }

    protected int getDaysConfigured() throws FindException {
        int days = registryService.getIntValue(RegistryService.REGID_IP_FREIGABE_KARENZZEIT);
        return days;
    }

    /**
     * Liefert ein Datum = Heute abzueglich der konfigurierten Karenzzeit.
     *
     * @throws FindException
     */
    public Date get() throws FindException {
        int days = getDaysConfigured();
        Date releaseDate = DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, days * -1);
        return DateUtils.truncate(releaseDate, Calendar.DAY_OF_MONTH);
    }

} // end
