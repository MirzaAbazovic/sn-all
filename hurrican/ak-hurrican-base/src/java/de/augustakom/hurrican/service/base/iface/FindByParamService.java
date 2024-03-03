/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 10:25:29
 */
package de.augustakom.hurrican.service.base.iface;

import java.util.*;

import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface definiert Methoden fuer Service-Implementierungen, die Objekte ueber die Angabe von Parametern finden
 * koennen. <br> <br> Dieses Interface sollte nur fuer Services verwendet werden, die relativ wenig unterschiedliche
 * Suchmoeglichkeiten besitzen. Fuer Services mit mehreren Suchmoeglichkeiten sollten eigene Interfaces definiert und
 * verwendet werden. Dadurch wird der Client-Code (und auch die Service-Implementierung) uebersichtlicher.
 *
 *
 */
public interface FindByParamService<T> extends IHurricanService {

    /**
     * Sucht nach Objekten, die best. Eigenschaften erfuellen. Die Eigenschaften werden ueber die <code>params</code>
     * definiert. <br> Um unterschiedliche Such-Strategien zu unterstuetzen (z.B. Suche nach 'name' bzw. Suche nach
     * 'name' und 'vorname'), kann mit dem Parameter <code>strategy</code> der Suchtyp unterschieden werden. <br> Der
     * Wert von strategy kann sowohl als Konstante in dem <strong>Business-Objekt</strong> definiert sein, das geladen
     * werden soll. <br> Alternativ dazu kann die Konstante aber auch im Service-Interface definiert werden. Welche der
     * beiden Definitions-Arten verwendet wird muss von Fall zu Fall entschieden werden. <br>
     *
     * @param type     Typ der Objekte, die geladen werden sollen.
     * @param strategy Angabe der Such-Strategie.
     * @param params   Angabe der Such-Parameter.
     * @return Liste mit den gefundenen Objekten.
     * @throws FindException wenn bei der Suche nach den Objekten ein Fehler aufgetreten ist.
     */
    public List<T> findByParam(short strategy, Object[] params)
            throws FindException;

}



