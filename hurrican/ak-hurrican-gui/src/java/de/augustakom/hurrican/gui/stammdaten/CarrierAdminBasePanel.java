/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2009 15:33:32
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.util.*;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;

/**
 * Basisklasse für die Administration der Carrier-Daten.
 */
public abstract class CarrierAdminBasePanel extends AbstractAdminPanel implements AKDataLoaderComponent {

    private static final long serialVersionUID = 924460245138191700L;

    public CarrierAdminBasePanel() {
        super(null);
        createGUI();
    }

    /**
     * Laedt alle Carrier und gibt diese in einer Map (Key=ID, Value=Carrier-Objekt) zurueck.
     */
    protected Map<Long, Carrier> getCarrierMap() throws ServiceNotFoundException, FindException {
        CarrierService cs = getCCService(CarrierService.class);
        List<Carrier> carrier = cs.findCarrier();
        Map<Long, Carrier> carrierMap = new HashMap<Long, Carrier>();
        CollectionMapConverter.convert2Map(carrier, carrierMap, "getId", null);
        return carrierMap;
    }

}
