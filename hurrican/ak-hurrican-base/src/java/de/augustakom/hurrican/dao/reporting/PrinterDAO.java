/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2008 09:07:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.Printer;

public interface PrinterDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Liefert ein Printer-Objekt anhand des Druckernames (case-insensitiv)
     *
     * @param name Name des gesuchten Druckers
     * @return Liste mit Printer-Objekten
     *
     */
    public List<Printer> findPrintersByName(String name);
}
