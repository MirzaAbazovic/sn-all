/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 09:09:04
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.Device;


/**
 * DAO-Interface fuer die Ermittlung von Devices (Endgeraeten).
 *
 *
 */
public interface DeviceDAO extends FindDAO {

    /**
     * Ermittelt ein spezifisches Endgeraet ueber dessen ID.
     *
     * @param devNo ID des zu ermittelnden Endgeraets
     * @return Objekt vom Typ <code>Endgeraet</code> oder <code>null</code>
     *
     */
    public Device findDevice(Long devNo);

    /**
     * Ermittelt alle Endgeraete zu einem Auftrag, die von einem bestimmten Typ sind. (Typen als Konstante im Modell
     * <code>Device</code> hinterlegt.
     *
     * @param auftragNoOrig      (original) Auftragsnummer
     * @param provisioningSystem (optional) zu beruecksichtigendes Provisionierungs-System des Geraetetyps.
     * @param deviceClass        (optional) Angabe der zu beruecksichtigenden Geraete-Klasse
     * @return Liste mit Objekten des Typs <code>Device</code>
     *
     */
    public List<Device> findDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass);

    /**
     * Ermittelt alle Endgeraetebestellungen zu einem Auftrag, die von einem bestimmten Typ sind. (Typen als Konstante
     * im Modell <code>Device</code> hinterlegt.
     *
     * @param auftragNoOrig      (original) Auftragsnummer
     * @param provisioningSystem (optional) zu beruecksichtigendes Provisionierungs-System des Geraetetyps.
     * @param deviceClass        (optional) Angabe der zu beruecksichtigenden Geraete-Klasse
     * @return Liste mit Objekten des Typs <code>Device</code>
     */
    public List<Device> findOrderedDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass);

}


