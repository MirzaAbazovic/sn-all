/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2004 10:27:09
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition, um eine Counter-Tabelle zu verwalten.
 *
 *
 */
public interface CounterService extends ICCService {

    /**
     * Counter-Name kennzeichnet den Hurrican Buendel-Counter.
     */
    public static final String COUNTER_BUENDEL = "hurrican.buendel";

    /**
     * Counter-Name kennzeichnet den Counter fuer die IP-Endgeraet Konfigurationen.
     */
    public static final String COUNTER_IP_ENDGERAET_CONFIG = "ip.endgeraete.config";

    /**
     * Counter-Name kennzeichnet den Counter fuer ASB-Bezeichnungen.
     */
    public static final String COUNTER_ASB = "deluxe.asb";

    /**
     * Erzeugt einen neuen Wert fuer den Counter mit Namen <code>counterName</code> und gibt diesen zurueck. <br> In der
     * Counter-Tabelle steht immer der zuletzt vergebene Wert! <br><br> Die Namen fuer die moeglichen Counter sind als
     * Konstante in diesem Interface definiert! <br>
     *
     * @param counterName Name des Counter, von dem ein neuer Wert ermittelt werden soll.
     * @return neuer Wert fuer den Counter
     * @throws StoreException wenn der Counter nicht gefunden oder nicht erhoeht werden konnte.
     */
    public Integer getNewIntValue(String counterName) throws StoreException;

}


