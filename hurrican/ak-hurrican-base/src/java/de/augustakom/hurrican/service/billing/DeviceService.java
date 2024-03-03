/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 08:40:41
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Definition fuer die Ermittlung von Endgeraeten.
 *
 *
 */
public interface DeviceService extends IBillingService {

    /**
     * Ermittelt ein Objekt der Zusatztabelle DeviceFritzBox ueber dessen ID.
     *
     * @param devNo ID des zu ermittelnden Endgeraets
     * @return Objekt vom Typ <code>DeviceFritzBox</code> oder <code>null</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public DeviceFritzBox findDeviceFritzBox(Long devNo) throws FindException;

    /**
     * Ermittelt alle Endgeraete zu einem Auftrag, die von einem bestimmten Typ sind. (Typen als Konstante im Modell
     * <code>Device</code> hinterlegt.
     *
     * @param auftragNoOrig      (original) Auftragsnummer
     * @param provisioningSystem (optional) Angabe des zu beruecksichtigenden Provisionierungs-Systems fuer den
     *                           Geraetetyp.
     * @param deviceClass        (optional) Angabe der zu beruecksichtigenden Geraete-Klasse.
     * @return Liste mit Objekten des Typs <code>Device</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Device> findDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass)
            throws FindException;

    /**
     * Ermittelt alle Endgeraetebestellungen zu einem Auftrag, die von einem bestimmten Typ sind. (Typen als Konstante
     * im Modell <code>Device</code> hinterlegt.
     *
     * @param auftragNoOrig      (original) Auftragsnummer
     * @param provisioningSystem (optional) Angabe des zu beruecksichtigenden Provisionierungs-Systems fuer den
     *                           Geraetetyp.
     * @param deviceClass        (optional) Angabe der zu beruecksichtigenden Geraete-Klasse.
     * @return Liste mit Objekten des Typs <code>Device</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Device> findOrderedDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass)
            throws FindException;

}


