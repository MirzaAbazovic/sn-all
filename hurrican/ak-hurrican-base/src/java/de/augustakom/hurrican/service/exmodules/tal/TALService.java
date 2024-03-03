/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2007 14:29:57
 */
package de.augustakom.hurrican.service.exmodules.tal;

import java.lang.reflect.*;
import java.util.*;

import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;


/**
 * Interface fuer die Durchfuehrung der elektronischen TAL-Bestellung. (Anbindung der Schnittstellen-Funktion aus
 * MNETCALL)
 *
 *
 */
public interface TALService extends ITALService, ISimpleFindService {

    /**
     * Ermittelt alle Segmente zu einer TAL-Bestellung
     *
     * @return
     */
    public SortedMap<Number, SortedMap<String, List<TALSegment>>> findAllSegmentsForTBSFirstId(Number orderNumber) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

}
