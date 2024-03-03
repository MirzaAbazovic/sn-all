/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 15:02:16
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Interface zur Definition der DAO-Methoden, um {@link HWSwitch}-Objekte zu verwalten.
 */
public interface HWSwitchDAO extends FindDAO {

    /**
     * liefert ein {@link HWSwitch} anhand seiner {@link Reference} auf seinen Namen.
     *
     * @param name
     * @return
     */
    HWSwitch findSwitchByName(String name);

    /**
     * liefert alle bekannten {@link HWSwitch}es.
     *
     * @return
     */
    List<HWSwitch> findAllSwitches();

    /**
     * liefert alle {@link HWSwitch}es fuer die angegebenen Switch-Typen.
     *
     * @param types
     * @return
     */
    List<HWSwitch> findSwitchesByType(HWSwitchType... types);

}
