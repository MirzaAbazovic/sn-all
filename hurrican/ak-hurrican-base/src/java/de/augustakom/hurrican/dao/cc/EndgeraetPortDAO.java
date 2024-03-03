/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 11:55:08
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.EndgeraetPort;

/**
 * DAO-Interface für die Verwaltung von Entities des Typs EndgeraetPort. Da die zugehörige Tabelle mit Werten vorbelegt
 * ist, finden sich hier keine Methoden zur Datenmanipulation.
 */
public interface EndgeraetPortDAO {

    /**
     * @param numbers Die Portnummern der zu findenen Ports
     * @return alle Ports die zu den angegebenen Portnummern gefunden wurden.
     */
    List<EndgeraetPort> findByNumbers(Collection<Integer> numbers);

}
